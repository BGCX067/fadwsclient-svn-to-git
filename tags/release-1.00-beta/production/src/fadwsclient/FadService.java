package fadwsclient;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Date: Dec 22, 2008
 *
 * @author Christian Hvid
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FadService {
    enum ResponseEncoding { XML, JSON, NONE }
    enum RequestMethod { UNDEFINED, GET, POST, HEAD, OPTIONS, PUT, DELETE, TRACE }

    String requestUrl() default "";
    String requestUrlFromSystemProperty() default "";
    String requestUrlFromJndi() default "";
    String requestData() default "";
    RequestMethod requestMethod() default RequestMethod.UNDEFINED;

    ResponseEncoding responseEncoding() default ResponseEncoding.XML;
    String responseSubhierarchy() default "";
}
