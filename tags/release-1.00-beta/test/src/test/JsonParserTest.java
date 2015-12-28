package test;

import fadwsclient.parsers.FadJsonParser;
import fadwsclient.FadParser;

/**
 * Date: Dec 26, 2008
 *
 * @author Christian Hvid
 */

public class JsonParserTest {
    public static void main(String[] args) {
        System.out.println("Test cases for FadJsonParser:");

        FadParser parser = new FadJsonParser();

        String test1 = "{\"responseData\": {\"translatedText\":\"red porridge with cream\"}, \"" +
                "responseDetails\": null, \"responseStatus\": 200}";

        System.out.println(test1 + " parses to: " + parser.parse(test1));

        String test2 = "{\n" +
                "     \"firstName\": \"John\",\n" +
                "     \"lastName\": \"Smith\",\n" +
                "     \"address\": {\n" +
                "         \"streetAddress\": \"21 2nd Street\",\n" +
                "         \"city\": \"New York\",\n" +
                "         \"state\": \"NY\",\n" +
                "         \"postalCode\": 10021\n" +
                "     },\n" +
                "     \"phoneNumbers\": [\n" +
                "         \"212 555-1234\",\n" +
                "         \"646 555-4567\"\n" +
                "     ]\n" +
                "}";

        System.out.println(test2 + " parses to: " + parser.parse(test2));

        String test3 = "{\n" +
                "     \"floatValue\": 232\n" +
                // "     \"textValue\": \"Hello - there \\\" <- quote\"\n" +
                "}";

        System.out.println(test3 + " parses to: " + parser.parse(test3));
    }
}
