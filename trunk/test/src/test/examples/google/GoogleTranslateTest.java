package test.examples.google;

import fadwsclient.FadClient;
import test.examples.google.GoogleTranslate;

/**
 * Date: Dec 26, 2008
 *
 * @author Christian Hvid
 */

public class GoogleTranslateTest {
    public static void main(String[] args) {
        GoogleTranslate googleTranslate = FadClient.create(GoogleTranslate.class);

        System.out.println("What does 'r¿d gr¿d med fl¿de' mean?");
        System.out.println("It means '"+googleTranslate.translate("da|en", "r¿d gr¿d med fl¿de")+"'");

    }
}
