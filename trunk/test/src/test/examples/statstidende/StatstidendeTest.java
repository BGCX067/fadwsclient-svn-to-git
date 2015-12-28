package test.examples.statstidende;

import fadwsclient.FadClient;
import fadwsclient.FadService;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;

/**
 * Date: Jan 6, 2009
 *
 * @author Christian Hvid
 */

public class StatstidendeTest {
    public static class Tvangsauktion {
        private String ejerlav;
        private String skoedehaver;
        private int ejendomsvaerdi;
        private int grundvaerdi;

        public Tvangsauktion(String ejerlav, int ejendomsvaerdi, int grundvaerdi, String skoedehaver) {
            this.ejerlav = ejerlav;
            this.ejendomsvaerdi = ejendomsvaerdi;
            this.grundvaerdi = grundvaerdi;
            this.skoedehaver = skoedehaver;
        }

        public String getEjerlav() {
            return ejerlav;
        }

        public String getSkoedehaver() {
            return skoedehaver;
        }

        public int getEjendomsvaerdi() {
            return ejendomsvaerdi;
        }

        public int getGrundvaerdi() {
            return grundvaerdi;
        }

        public String toString() {
            return skoedehaver + " " + ejendomsvaerdi + " kr.";
        }
    }

    private static String findSubregion(String data, String left, String interest, String right) {
        Matcher matcher = Pattern.compile(left + interest + right, Pattern.MULTILINE | Pattern.DOTALL).matcher(data);

        if (matcher.find()) {
            String result = matcher.group().replaceFirst(left, "");

            if (right.equals("")) return result;

            Matcher matcherRight = Pattern.compile(right, Pattern.MULTILINE | Pattern.DOTALL).matcher(result);

            if (matcherRight.find()) {
                return result.substring(0, matcherRight.start());
            }
        }

        return null;
    }

    public static void main(String[] args) throws IOException {
        new StatstidendeTest().go();
    }

    private void go() throws IOException {
        Statstidende statstidende = FadClient.create(Statstidende.class);
        List<Statstidende.Item> list = statstidende.listTvangsauktioner();

        for (Statstidende.Item item : list) {
            // System.out.println(findSubregion(item.getDescription(), "Ejerlav: ", ".*", "( - Lejlighed| - Dato| Matr\\.nr\\.)"));
            // System.out.println(findSubregion(item.getDescription(), "Ejendomsv¾rdi: ", ".*", " - Grundv¾rdi:"));
            // System.out.println(findSubregion(item.getDescription(), "Grundv¾rdi: ", ".*", " - Sk¿dehaver"));
            // System.out.println(findSubregion(item.getDescription(), "Sk¿dehaver if¿lge tingbogsattest: ", ".*", ""));

            System.out.println(item.getLink());

            String data = fetchDataByUrl(item.getLink());

            // System.out.println(data);

            String data2 = findSubregion(
                    data,
                    "<td colspan=\"2\" class=\"content_text padLeft47px\">",
                    ".*",
                    "</td>"
            );

            // System.out.println(data2);

            /*data2 = data2.replaceAll("</div>", "\n");
            data2 = data2.replaceAll("</span>", "\n");
            data2 = data2.replaceAll("<[^<]*>", "");*/

            // System.out.println(data2);

            String ejendom = findSubregion(data2, "<div class=\"AnnouncementHeader\">Ejendom</div>", ".*", "<A href");

            ejendom = ejendom.replaceAll("<br>", "\n");

            ejendom = ejendom.replaceAll("<[^<]*>", "");

            System.out.println("ejendom:\n" + ejendom);

            String ejendomsVaerdiBlock = findSubregion(data2, "<div class=\"AnnouncementHeader\">Ejendomsv¾rdi</div>", ".*", "<div class=\"AnnouncementHeader\">");

            String ejendomsVurderingsDato = findSubregion(ejendomsVaerdiBlock, "Pr. ", ".*", "<span class=\"AnnouncementHighlight\">");
            String ejendomsVaerdi = findSubregion(ejendomsVaerdiBlock, "<span class=\"AnnouncementHighlight\">", ".*", "</span>");
            String grundVaerdi = findSubregion(ejendomsVaerdiBlock, "heraf grundv¾rdi ", ".*", "<br>");

            System.out.println("Ejendomsvurdering: " + ejendomsVaerdi + " - grund: " + grundVaerdi + " - vurderet: " + ejendomsVurderingsDato);

            String skoedehaver = findSubregion(data2, "<div class=\"AnnouncementHeader\">Sk¿dehaver if¿lge tingbogsattest</div>", ".*", "<br>");

            System.out.println("Sk¿dehaver: " + skoedehaver);

            String begaerer = findSubregion(data2, "<div class=\"AnnouncementHeader\">Beg¾reren af auktionen \\(Rekvirenten\\)</div>", ".*", "<A href");

            begaerer = begaerer.replaceAll("<br>", "\n");

            System.out.println("Beg¾rer:\n" + begaerer);

            String tidOgStedBlock = findSubregion(data2, "<div class=\"AnnouncementHeader\">Dato, tid og sted for afholdelse af auktion</div>", ".*", "<A href");

            String tidspunkt = findSubregion(tidOgStedBlock, "", ".*kl.*", "<br>");

            String sted = findSubregion(tidOgStedBlock, "<br>", ".*", "");

            sted = sted.replaceAll("<br>", "\n");

            System.out.println("Tidspunkt: " + tidspunkt);

            System.out.println("Sted:\n" + sted);

            String beskrivelse = findSubregion(data2, "</A>", ".*", "<div class=\"AnnouncementHeader\">Ejendomsv¾rdi</div>");

            beskrivelse = beskrivelse.replaceAll("<br>", "\n");

            beskrivelse = beskrivelse.trim();

            System.out.println("Beskrivelse:\n" + beskrivelse);

            String retskreds = findSubregion(data2, "<div class=\"AnnouncementHeader\">Retskreds</div>", ".*", "<A href");

            System.out.println("Retskreds: " + retskreds);

            String sagsnummer = findSubregion(data2, "<br>Sagsnummer: ", ".*", "<div class=\"AnnouncementHeader\">");

            System.out.println("Sagsnr.: " + sagsnummer);

            String ekstraBemaerkninger = findSubregion(data2, "<div class=\"AnnouncementHeader\">Ekstra bem¾rkninger</div>", ".*", "</span>");

            if (ekstraBemaerkninger != null) {
                ekstraBemaerkninger = ekstraBemaerkninger.replaceAll("<br>", "\n");

                ekstraBemaerkninger = ekstraBemaerkninger.trim();
            }

            System.out.println("Ekstra bem¾rkninger:\n" + ekstraBemaerkninger);

            String henvendelseBesigtelse = findSubregion(
                    data2,
                    "<div class=\"AnnouncementHeader\">Henvendelse vedr\\. besigtigelse \\(hvis henvendelse kan ske flere " +
                            "steder, er yderligere adresser anf¿rt under \"Ekstra bem¾rkninger\"\\)</div>",
                    ".*",
                    "<A href"
            );

            henvendelseBesigtelse = henvendelseBesigtelse.replaceAll("<br>", "\n");

            henvendelseBesigtelse = henvendelseBesigtelse.trim();

            System.out.println("Henvendelse vedr. besigtelse:\n" + henvendelseBesigtelse);

            String sagensDokumenter = findSubregion(
                    data2,
                    "<div class=\"AnnouncementHeader\">Sagens dokumenter ligger til eftersyn hos</div>",
                    ".*",
                    "<div class=\"AnnouncementHeader\">"
            );

            sagensDokumenter = sagensDokumenter.replaceAll("<br>", "\n");

            sagensDokumenter = sagensDokumenter.trim();

            System.out.println("Sagens dokumenter:\n" + sagensDokumenter);

            // <div class="AnnouncementHeader">Auktionsbekendtg¿rer</div>

            String bekendtgoerer = findSubregion(data2, "<div class=\"AnnouncementHeader\">Auktionsbekendtg¿rer</div>", ".*", "<div class=\"AnnouncementHeader\">");

            bekendtgoerer = bekendtgoerer.replaceAll("<br>", "\n");

            bekendtgoerer = bekendtgoerer.trim();

            System.out.println("Bekendtg¿rer:\n" + bekendtgoerer);

            // System.out.println(data2);

            // System.exit(0);

        }
    }

    private static int BUFFER_SIZE = 1024 * 64;

    private static String fetchDataByUrl(String urlAsString) throws IOException {
        URL url = new URL(urlAsString);

        URLConnection urlConnection = url.openConnection();

        InputStream is = urlConnection.getInputStream();

        byte data[] = new byte[0];

        try {
            int bytesRead;

            byte buffer[] = new byte[BUFFER_SIZE];

            do {
                bytesRead = is.read(buffer, 0, BUFFER_SIZE);

                if (bytesRead > 0) {
                    byte newData[] = new byte[data.length + bytesRead];
                    System.arraycopy(data, 0, newData, 0, data.length);
                    System.arraycopy(buffer, 0, newData, data.length, bytesRead);
                    data = newData;
                }

            } while (bytesRead > 0);
        } finally {
            is.close();
        }

        return new String(data, "UTF-8");
    }

}
