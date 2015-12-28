package test.examples.webschools;

import fadwsclient.FadService;

/**
 * Date: Dec 27, 2008
 *
 * @author Christian Hvid
 */

public interface WebSchoolsTemperatureConverter {
    @FadService(
            requestUrl = "http://www.w3schools.com/webservices/tempconvert.asmx/CelsiusToFahrenheit",
            requestMethod = FadService.RequestMethod.POST,
            requestData = "Celsius={0}",
            responseEncoding = FadService.ResponseEncoding.XML,
            responseSubhierarchy = "string"
    )
    public double celsiusToFahrenheit(double celsius);
}
