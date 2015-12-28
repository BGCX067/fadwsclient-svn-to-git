package fadwsclient;

import fadwsclient.parsers.FadJsonParser;
import fadwsclient.parsers.FadNullParser;
import fadwsclient.parsers.FadXmlParser;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.*;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.net.URLEncoder;
import java.net.URLConnection;
import java.net.URL;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStream;

/**
 * Date: Dec 22, 2008
 *
 * @author Christian Hvid
 */

public class FadClient {
    private static Logger logger = Logger.getLogger(FadClient.class.getName());

    private static int BUFFER_SIZE = 1024 * 64;

    private static String fetchDataByUrl(String urlAsString, String postData, FadService.RequestMethod method) throws IOException {
        URL url = new URL(urlAsString);

        URLConnection urlConnection = url.openConnection();

        if (!method.equals(FadService.RequestMethod.UNDEFINED)) {
            ((HttpURLConnection) urlConnection).setRequestMethod(method.toString());
        }

        if ((postData != null) && (!postData.equals(""))) {
            urlConnection.setDoOutput(true);
            urlConnection.getOutputStream().write(postData.getBytes());
            urlConnection.getOutputStream().flush();
        }

        InputStream is = urlConnection.getInputStream();

        byte data[] = new byte[0];

        try {
            int bytesRead;

            byte buffer[] = new byte[BUFFER_SIZE];

            do {
                bytesRead = is.read(buffer, 0, BUFFER_SIZE);

                if (bytesRead > 0) {
                    byte newData[] = new byte[data.length + bytesRead];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    System.arraycopy(buffer, 0, newData, data.length, bytesRead);
                    data = newData;
                }

            } while (bytesRead > 0);
        } finally {
            is.close();
        }

        return new String(data);
    }

    private static boolean canClassBeMappedTo(Class klass1, Class klass2) {
        if (klass2.isAssignableFrom(klass1)) return true;

        if (klass1.equals(String.class)) {
            return klass2.equals(Integer.class) || klass2.equals(int.class) ||
                    klass2.equals(Double.class) || klass2.equals(double.class) ||
                    klass2.equals(Float.class) || klass2.equals(float.class);
        }

        return false;
    }

    private static Object mapObjectToClass(Object object, Class klass2) {
        Class klass1 = object.getClass();

        if (klass2.isAssignableFrom(klass1)) return object;

        if (klass1.equals(String.class)) {
            if (klass2.equals(Integer.class) || klass2.equals(int.class)) return Integer.parseInt((String) object);
            if (klass2.equals(Double.class) || klass2.equals(double.class)) return Double.parseDouble((String) object);
            if (klass2.equals(Float.class) || klass2.equals(float.class)) return Float.parseFloat((String) object);
        }

        return null;
    }

    private static Map<FadService.ResponseEncoding, FadParser> defaultParsers() {
        Map<FadService.ResponseEncoding, FadParser> parsers = new HashMap<FadService.ResponseEncoding, FadParser>();

        parsers.put(FadService.ResponseEncoding.JSON, new FadJsonParser());
        parsers.put(FadService.ResponseEncoding.XML, new FadXmlParser());
        parsers.put(FadService.ResponseEncoding.NONE, new FadNullParser());

        return parsers;
    }

    public static <T> T create(Class<T> klass) {
        final Map<FadService.ResponseEncoding, FadParser> parsers = defaultParsers();

        return (T) Proxy.newProxyInstance(FadClient.class.getClassLoader(), new Class[]{klass}, new InvocationHandler() {
            public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
                String combinedUrl = null;
                String combinedData = null;
                String responseData = null;
                FadResponse fadResponse = null;

                try {
                    logger.fine("method: " + method.getName());

                    FadService fadService = method.getAnnotation(FadService.class);

                    if (fadService == null)
                        throw new RuntimeException(method + " is not annotated with the FadService annotation.");

                    // Find parser implementation

                    FadParser parser = parsers.get(fadService.responseEncoding());

                    // Encode request

                    combinedUrl = fadService.requestUrl();

                    if ((!fadService.requestUrlFromSystemProperty().equals("")) &&
                            (System.getProperty(fadService.requestUrlFromSystemProperty(), null) != null))
                        combinedUrl = System.getProperty(fadService.requestUrlFromSystemProperty());

                    if (!fadService.requestUrlFromJndi().equals("")) {
                        try {
                            InitialContext context = new InitialContext();

                            combinedUrl = (String) context.lookup(fadService.requestUrlFromJndi());

                        } catch (NamingException e) {
                            logger.log(Level.FINE, "Ignoring exception "+e, e);
                        }
                    }

                    combinedData = fadService.requestData();

                    for (int parameter = 0; parameter < method.getParameterAnnotations().length; parameter++) {
                        combinedUrl = combinedUrl.replaceAll("\\{" + parameter + "\\}", URLEncoder.encode(objects[parameter].toString(), "UTF-8"));
                        combinedData = combinedData.replaceAll("\\{" + parameter + "\\}", URLEncoder.encode(objects[parameter].toString(), "UTF-8"));
                    }

                    logger.fine("Combined URL is: " + combinedUrl);
                    logger.fine("Combined data is: " + combinedData);

                    // Do request

                    responseData = fetchDataByUrl(combinedUrl, combinedData, fadService.requestMethod());

                    // Parse result

                    fadResponse = parser.parse(responseData);

                    // Read subhierarchies from result

                    String splittedResponseHierarchies[] = fadService.responseSubhierarchy().split("\\,");

                    List<Object> subhierarchies = new ArrayList<Object>();

                    if (splittedResponseHierarchies.length == 0) {
                        subhierarchies.add(fadResponse.subhierarchy(""));
                    } else {
                        for (String hierarchyString : splittedResponseHierarchies)
                            subhierarchies.add(fadResponse.subhierarchy(hierarchyString));
                    }

                    // Map result into expected return type and return the mapped result

                    if (method.getReturnType().isAssignableFrom(List.class)) {
                        Class klass = (Class) ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];

                        List<Object> returnValue = new ArrayList<Object>();

                        if (subhierarchies.size() == 1) {
                            for (Object o1 : (List<Object>) subhierarchies.get(0)) {
                                if (canClassBeMappedTo(((List<Object>) subhierarchies.get(0)).get(0).getClass(), klass)) {
                                    returnValue.add(mapObjectToClass(o1, klass));
                                } else {
                                    throw new RuntimeException("Cannot create object of type " + method.getReturnType() + " from " + fadResponse.subhierarchy(fadService.responseSubhierarchy()).getClass());
                                }
                            }
                            return returnValue;
                        }

                        for (Constructor c : klass.getConstructors()) {
                            if (c.getParameterTypes().length == subhierarchies.size()) {
                                boolean matches = true;

                                for (int i = 0; i < subhierarchies.size(); i++)
                                    matches |= canClassBeMappedTo(subhierarchies.get(i).getClass(), (c.getParameterTypes()[i]));

                                if (matches) {
                                    for (int count = 0; count < ((List<Object>) subhierarchies.get(0)).size(); count++) {
                                        List<Object> parameters = new ArrayList<Object>();

                                        for (int j = 0; j < subhierarchies.size(); j++)
                                            parameters.add(mapObjectToClass(((List<Object>) subhierarchies.get(j)).get(count), (c.getParameterTypes()[j])));

                                        returnValue.add(c.newInstance(parameters.toArray()));

                                    }
                                }
                            }
                        }

                        return returnValue;
                    } else {
                        Class klass = method.getReturnType();

                        if (subhierarchies.size() == 1) {
                            if (canClassBeMappedTo(subhierarchies.get(0).getClass(), klass))
                                return mapObjectToClass(subhierarchies.get(0), klass);
                        }

                        for (Constructor c : klass.getConstructors()) {
                            if (c.getParameterTypes().length == subhierarchies.size()) {
                                boolean matches = true;

                                for (int i = 0; i < subhierarchies.size(); i++)
                                    matches |= canClassBeMappedTo(subhierarchies.get(i).getClass(), c.getParameterTypes()[i]);

                                if (matches) {
                                    List<Object> parameters = new ArrayList<Object>();

                                    for (int j = 0; j < subhierarchies.size(); j++)
                                        parameters.add(mapObjectToClass(subhierarchies.get(j), c.getParameterTypes()[j]));

                                    return c.newInstance(parameters.toArray());
                                }
                            }
                        }
                    }

                    throw new RuntimeException("Cannot create object of type " + method.getReturnType() + " from " + fadResponse.subhierarchy(fadService.responseSubhierarchy()).getClass());
                } catch (Throwable t) {
                    throw new FadRuntimeException(t, combinedUrl, combinedData, responseData, fadResponse);
                }
            }
        });
    }
}
