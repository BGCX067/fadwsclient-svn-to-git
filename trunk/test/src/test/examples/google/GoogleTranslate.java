package test.examples.google;

import fadwsclient.FadService;

/**
 * Date: Dec 22, 2008
 *
 * @author Christian Hvid
 */

public interface GoogleTranslate {
    @FadService(
            requestUrl = "http://ajax.googleapis.com/ajax/services/language/translate?v=1.0&langpair={0}&q={1}",
            responseEncoding = FadService.ResponseEncoding.JSON,
            responseSubhierarchy = "responseData.translatedText"
    )
    public String translate(String languagePair, String text);
}
