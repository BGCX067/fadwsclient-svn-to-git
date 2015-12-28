package fadwsclient;

/**
 * Date: Dec 28, 2008
 *
 * @author Christian Hvid
 */

public class FadRuntimeException extends RuntimeException {
    private String requestUrl;
    private String requestData;
    private String responseData;
    private FadResponse fadResponse;

    public FadRuntimeException(Throwable throwable, String requestUrl, String requestData, String responseData, FadResponse fadResponse) {
        super(throwable);
        this.requestUrl = requestUrl;
        this.requestData = requestData;
        this.responseData = responseData;
        this.fadResponse = fadResponse;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public String getRequestData() {
        return requestData;
    }

    public String getResponseData() {
        return responseData;
    }

    public FadResponse getFadResponse() {
        return fadResponse;
    }

    public String toString() {
        return getClass().getName() + " - root cause: " + getCause() + " - request url: " + requestUrl +
                " request data: " + requestData + " response data: " + responseData +
                " interpretated data: " + getFadResponse();
    }
}
