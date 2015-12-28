package fadwsclient;

import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Date: Dec 22, 2008
 *
 * @author Christian Hvid
 */

public class FadResponse {
    private Object data;

    public String toString() {
        return "FadResponse - data: " + renderData(data);
    }

    private String renderData(Object data) {
        if (data instanceof List) {
            String result = "[";

            for (Object o : (List) data) result += renderData(o) + ", ";

            if (result.length() > 1) result = result.substring(0, result.length() - 2);
            result += "]";

            return result;
        } else if (data instanceof Map) {
            String result = "{";
            Map<String, Object> map = (Map<String, Object>) data;

            for (String key : map.keySet()) result += key + ": " + renderData(map.get(key)) + ", ";

            if (result.length() > 1) result = result.substring(0, result.length() - 2);
            result += "}";

            return result;
        } else if (data instanceof String)
            return "\"" + data + "\"";
        else
            return "" + data;
    }

    public FadResponse(Object data) {
        this.data = data;
    }

    public Object subhierarchy(String position) {
        String tokens[] = position.split("\\.");

        Object result = data;

        for (String token : tokens) {
            if (result instanceof List) {
                List<Object> newResult = new ArrayList<Object>();
                for (Object subhierarchy : (List<Object>) result) {
                    if (token.endsWith("*"))
                        subhierarchy = ((Map<String, Object>) subhierarchy).get(token.substring(0, token.length() - 1));
                    else {
                        subhierarchy = ((Map<String, Object>) subhierarchy).get(token);

                        if ((subhierarchy instanceof List) && ((List) subhierarchy).size() == 1)
                            subhierarchy = ((List) subhierarchy).get(0);

                    }
                    newResult.add(subhierarchy);
                }
                result = newResult;
            } else {
                if (token.endsWith("*"))
                    result = ((Map<String, Object>) result).get(token.substring(0, token.length() - 1));
                else {
                    result = ((Map<String, Object>) result).get(token);

                    if ((result instanceof List) && ((List) result).size() == 1)
                        result = ((List) result).get(0);

                }
            }
        }

        return result;
    }
}
