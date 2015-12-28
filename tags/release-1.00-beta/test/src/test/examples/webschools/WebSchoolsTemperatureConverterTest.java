package test.examples.webschools;

import fadwsclient.FadClient;
import test.examples.webschools.WebSchoolsTemperatureConverter;

/**
 * Date: Dec 27, 2008
 *
 * @author Christian Hvid
 */

public class WebSchoolsTemperatureConverterTest {
    public static void main(String[] args) {
        WebSchoolsTemperatureConverter webSchoolsTemperatureConverter = FadClient.create(WebSchoolsTemperatureConverter.class);

        System.out.println("What is 100 degrees celsius in fahrenheit?");
        System.out.println("It is '"+webSchoolsTemperatureConverter.celsiusToFahrenheit(100)+"'");
    }
}
