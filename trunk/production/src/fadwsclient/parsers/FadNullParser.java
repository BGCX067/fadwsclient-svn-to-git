package fadwsclient.parsers;

import fadwsclient.FadParser;
import fadwsclient.FadResponse;

/**
 * Date: Dec 28, 2008
 *
 * @author Christian Hvid
 */

public class FadNullParser implements FadParser {
    public FadResponse parse(String data) {
        return new FadResponse(data);
    }
}
