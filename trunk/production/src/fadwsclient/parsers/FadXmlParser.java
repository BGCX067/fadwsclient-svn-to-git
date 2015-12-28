package fadwsclient.parsers;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import fadwsclient.FadParser;
import fadwsclient.FadResponse;

/**
 * Date: Dec 26, 2008
 *
 * @author Christian Hvid
 */

public class FadXmlParser implements FadParser {
    private static Logger logger = Logger.getLogger(FadXmlParser.class.getName());

    private static List<Element> children(Node node) {
        NodeList nodeList = node.getChildNodes();

        List<Element> result = new ArrayList<Element>();

        for (int i = 0; i < nodeList.getLength(); i++)
            if (nodeList.item(i) instanceof Element)
                result.add((Element) nodeList.item(i));

        return result;
    }

    private Object parseStructure(Element root) {
        List<Element> elements = children(root);

        if (elements.size() == 0) return root.getTextContent();

        Map<String, Object> result = new HashMap<String, Object>();

        for (Element e : elements) {
            if (!result.containsKey(e.getNodeName())) result.put(e.getNodeName(), new ArrayList<Object>());
            ((List<Object>)result.get(e.getNodeName())).add(parseStructure(e));
        }

        return result;
    }

    public FadResponse parse(String data) {
        logger.fine("data is "+data);
        
        try {
            Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
                    new ByteArrayInputStream((data).getBytes("UTF-8"))
            );

            Node root = document.getDocumentElement();

            Map<String, Object> result = new HashMap<String, Object>();

            result.put(root.getNodeName(), parseStructure((Element)root));

            return new FadResponse(result);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }
}
