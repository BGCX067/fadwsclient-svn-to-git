package test.examples.statstidende;

import fadwsclient.FadService;

import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Date: Jan 6, 2009
 *
 * @author Christian Hvid
 */

public interface Statstidende {
    public class Item {
        private String title;
        private String link;
        private String description;

        public Item(String title, String link, String description) {
            this.title = title;
            this.link = link;
            this.description = description;
        }

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getDescription() {
            return description;
        }

        public String toString() {
            return title + ": " + link + ": " + description;
        }
    }

    @FadService(
            requestUrl = "http://www.statstidende.dk/rss.aspx?said=790596&SearchType=2",
            responseEncoding = FadService.ResponseEncoding.XML,
            responseSubhierarchy = "rss.channel.item*.title,rss.channel.item*.link,rss.channel.item*.description"
    )
    public List<Item> listTvangsauktioner();

    @FadService(
            requestUrl = "{0}",
            responseEncoding = FadService.ResponseEncoding.NONE
    )
    public String readLink(String url);

}
