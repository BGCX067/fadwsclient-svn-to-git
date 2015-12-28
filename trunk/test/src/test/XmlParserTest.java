package test;

import fadwsclient.FadParser;
import fadwsclient.parsers.FadXmlParser;

/**
 * Date: Dec 26, 2008
 *
 * @author Christian Hvid
 */

public class XmlParserTest {
    public static void main(String[] args) {
        System.out.println("Test cases for FadXmlParser:");

        FadParser parser = new FadXmlParser();

        String test1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<skoler xmlns=\"http://itst.dk/schemas/danmarkservice\">\n" +
                "  <skole ref=\"http://oiorest.dk/danmark/skoler/825001\">\n" +
                "    <institutionsnr>825001</institutionsnr>\n" +
                "    <navn>L¾s¿ Skole</navn>\n" +
                "    <type>Folkeskole</type>\n" +
                "    <kommune ref=\"http://oiorest.dk/danmark/kommuner/825\" />\n" +
                "    <skoledistrikt ref=\"http://oiorest.dk/danmark/kommuner/825/skoledistrikter/0\" />\n" +
                "    <adresse ref=\"http://oiorest.dk/danmark/adresser/1422304\">\n" +
                "      <vej>\n" +
                "        <navn> Byrum Hovedgade </navn>\n" +
                "      </vej>\n" +
                "      <husnr>58</husnr>\n" +
                "      <postdistrikt ref=\"http://oiorest.dk/danmark/postdistrikter/9940\">\n" +
                "        <nr>9940</nr>\n" +
                "        <navn>L¾s¿</navn>\n" +
                "      </postdistrikt>\n" +
                "    </adresse>\n" +
                "  </skole>\n" +
                "</skoler>";

        System.out.println(test1 + " parses to: " + parser.parse(test1));

        System.out.println("subhierarchy test: " + parser.parse(test1).subhierarchy("skoler.skole.navn"));

        String test2 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<skoler xmlns=\"http://itst.dk/schemas/danmarkservice\">\n" +
                "  <skole ref=\"http://oiorest.dk/danmark/skoler/825001\">\n" +
                "    <institutionsnr>825001</institutionsnr>\n" +
                "    <navn>L¾s¿ Skole</navn>\n" +
                "    <type>Folkeskole</type>\n" +
                "    <kommune ref=\"http://oiorest.dk/danmark/kommuner/825\" />\n" +
                "    <skoledistrikt ref=\"http://oiorest.dk/danmark/kommuner/825/skoledistrikter/0\" />\n" +
                "    <adresse ref=\"http://oiorest.dk/danmark/adresser/1422304\">\n" +
                "      <vej>\n" +
                "        <navn> Byrum Hovedgade </navn>\n" +
                "      </vej>\n" +
                "      <husnr>58</husnr>\n" +
                "      <postdistrikt ref=\"http://oiorest.dk/danmark/postdistrikter/9940\">\n" +
                "        <nr>9940</nr>\n" +
                "        <navn>L¾s¿</navn>\n" +
                "      </postdistrikt>\n" +
                "    </adresse>\n" +
                "  </skole>\n" +
                "  <skole ref=\"http://oiorest.dk/danmark/skoler/825210\">\n" +
                "    <institutionsnr>825210</institutionsnr>\n" +
                "    <navn>L¾s¿ Ungdomsskole</navn>\n" +
                "    <type>Kommunale ungdomsskoler og ungdomskostskoler</type>\n" +
                "    <kommune ref=\"http://oiorest.dk/danmark/kommuner/825\" />\n" +
                "    <skoledistrikt ref=\"http://oiorest.dk/danmark/kommuner/825/skoledistrikter/0\" />\n" +
                "    <adresse ref=\"http://oiorest.dk/danmark/adresser/1422304\">\n" +
                "      <vej>\n" +
                "        <navn> Byrum Hovedgade </navn>\n" +
                "      </vej>\n" +
                "      <husnr>58</husnr>\n" +
                "      <postdistrikt ref=\"http://oiorest.dk/danmark/postdistrikter/9940\">\n" +
                "        <nr>9940</nr>\n" +
                "        <navn>L¾s¿</navn>\n" +
                "      </postdistrikt>\n" +
                "    </adresse>\n" +
                "  </skole>\n" +
                "</skoler>";

        System.out.println(test2 + " parses to: " + parser.parse(test2));

        System.out.println("subhierarchy test: " + parser.parse(test2).subhierarchy("skoler*.skole.navn"));

    }
}
