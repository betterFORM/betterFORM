package de.betterform.html5;

import de.betterform.thirdparty.DOMBuilder;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Node;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;


/**
 * takes HTML5 input, parses it and turns it into an XML representation that will be transformed to
 * XForms syntax.
 *
 * @author Joern Turner
 */
public class Preprocessor {

    public Preprocessor(){
    }

    /**
     * Parses document at given uri and turns it into wellformed XML-conformant HTML. The HTML is then fed
     * into a transformer that outputs an XForms model document that is semantically equivalent to the incoming HTML.
     *
     * @param url  URL pointing to input html5 document (does not need to be wellformed or a complete HTML doc)
     * @param cachingTransformerService  the transformerservice used to run the transform
     * @param data input data as a simple list
     * @return returns XForms document that matches the semantics of the incoming html form
     * @throws URISyntaxException
     * @throws TransformerException
     * @throws XFormsConfigException
     * @throws IOException
     */
    public static Node submit2Xforms(String url, CachingTransformerService cachingTransformerService, String data)
            throws URISyntaxException, TransformerException, XFormsConfigException, IOException {
        URL uri = new URL(url);
        Document doc = Jsoup.parse(uri, 1000);
        org.w3c.dom.Document domDoc = DOMBuilder.jsoup2DOM(doc);
        String styles = Config.getInstance().getProperty("preprocessor-transform");
        Transformer transformer = cachingTransformerService.getTransformerByName(styles);
        transformer.setParameter("data",data);
        DOMResult domResult = new DOMResult();
        transformer.transform(new DOMSource(domDoc), domResult);
        return domResult.getNode();
    }

    /**
     * Parses a string containing HTML5 and transforms it into a semantically equivalent XForms model document
     * @param html5
     * @param cachingTransformerService
     * @return
     */
    public static Node html2Xforms(String html5, CachingTransformerService cachingTransformerService) {
        Document doc = Jsoup.parse(html5);
        org.w3c.dom.Document domDoc = DOMBuilder.jsoup2DOM(doc);
        try {
            String styles = Config.getInstance().getProperty("preprocessor-transform");
            Transformer transformer = cachingTransformerService.getTransformerByName(styles);
            DOMResult domResult = new DOMResult();
            transformer.transform(new DOMSource(domDoc), domResult);
            return domResult.getNode();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (XFormsConfigException e) {
            e.printStackTrace();
        }
        return null;
    }

}
