package test.examples.google;

import fadwsclient.FadClient;

import test.examples.google.GoogleBooks;

/**
 * Date: Dec 27, 2008
 *
 * @author Christian Hvid
 */

public class GoogleBooksTest {
    public static void main(String[] args) {
        GoogleBooks googleBooks = FadClient.create(GoogleBooks.class);

        System.out.println("Listing books by 'Elizabeth Bennet':");

        for (GoogleBooks.Book book : googleBooks.listBooks("Elizabeth Bennet"))
            System.out.println("" + book);

    }
}
