package test.examples.google;

import fadwsclient.FadService;

import java.util.List;

/**
 * Date: Dec 27, 2008
 *
 * @author Christian Hvid
 */

public interface GoogleBooks {
    public class Book {
        private String id;
        private List<String> titles;

        public Book(String id, List<String> titles) {
            this.id = id;
            this.titles = titles;
        }

        public String getId() {
            return id;
        }

        public List<String> getTitles() {
            return titles;
        }

        public String toString() {
            String result = id;
            for (String title : titles) result += " " + title;
            return result;
        }
    }

    @FadService(
            requestUrl = "http://books.google.com/books/feeds/volumes?q={0}",
            responseEncoding = FadService.ResponseEncoding.XML,
            responseSubhierarchy = "feed*.entry.id,feed*.entry.dc:title*"
    )
    public List<Book> listBooks(String query);
}
