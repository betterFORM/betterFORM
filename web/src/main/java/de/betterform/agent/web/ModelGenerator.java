package de.betterform.agent.web;

import de.betterform.thirdparty.DOMBuilder;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;


/**
 * takes HTML5 input, parses it and turns it into an XML representation that will be transformed to
 * XForms syntax.
 *
 * @author Joern Turner
 */
public class ModelGenerator {
    private static final Log LOG = LogFactory.getLog(ModelGenerator.class);

    private String htmlFilePath;
    private HttpServletRequest request;
    private CachingTransformerService transformerService;
    private WebFactory webFactory;
    private String referer;

    public ModelGenerator(){
    }

    public String getHtmlFilePath() {
        return htmlFilePath;
    }

    //todo: can probably be refactored to use less arguments

    /**
     * resolve the pathes for input HTML and generated model and generates XForms model (if needed).
     *
     * @param request a http request with form data
     * @param cachingTransformerService used to cache XSLT stylesheets and create transformers
     * @param webFactory
     * @return the XForms model for the HTML input
     * @throws URISyntaxException
     * @throws TransformerException
     * @throws XFormsConfigException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public Node generateModel(HttpServletRequest request,
                              CachingTransformerService
                                      cachingTransformerService,
                              WebFactory webFactory)
            throws URISyntaxException, TransformerException, XFormsConfigException, IOException, ParserConfigurationException, SAXException {
        this.request = request;
        this.transformerService = cachingTransformerService;
        this.webFactory = webFactory;
        // get referer document
        /*
        This is a weak point: the referer header is not reliable in all cases or might get filtered by proxies. For this to work
        the referer must return the URL of the host document of the form.
        */
        this.referer = request.getHeader("Referer");
//        String fullPath = getAbsoluteHTMLFilePath(request, webFactory, referer);
        htmlFilePath = getAbsoluteHTMLFilePath(referer);
        File xformModelFile = getModelFile();


        String data = getRequestParams();


        if(!xformModelFile.exists()){
            //Parse original HTML and sanitize to XHTML
            org.w3c.dom.Document domDoc = getSanitizedHtml();
            String reqUri = request.getRequestURI();
            DOMResult domResult = generateModel(reqUri, data, domDoc);
            //store it
            String xm = DOMUtil.serializeToString((org.w3c.dom.Document) domResult.getNode());
            FileUtils.writeStringToFile(xformModelFile, xm);
            return domResult.getNode();
        }else{
            return DOMUtil.parseXmlFile(xformModelFile.getAbsoluteFile(),true,false);
        }
    }

    private org.w3c.dom.Document getSanitizedHtml() throws IOException {
        URL uri = new URL(referer);
        Document doc = Jsoup.parse(uri, 1000);
        return DOMBuilder.jsoup2DOM(doc);
    }

    private File getModelFile() {
        File htmlFile = new File(htmlFilePath);
        File parentFile = htmlFile.getParentFile();
        String fileName = htmlFile.getName();
        String baseName = fileName.substring(0,fileName.indexOf("."));
        String xfmFileName = baseName + ".xfm";
        return new File(parentFile,xfmFileName);
    }

    private String getAbsoluteHTMLFilePath(String referer) {
        String contextname = request.getContextPath();
        int pos = referer.indexOf(contextname);
        String relPath = referer.substring(pos+contextname.length());
        String absPath=null;
        try {
            absPath = WebFactory.getRealPath(relPath,webFactory.getServletContext());
        } catch (XFormsConfigException e) {
            e.printStackTrace();
        }
        return absPath;
    }

    private DOMResult generateModel(String reqUri, String data, org.w3c.dom.Document domDoc) throws XFormsConfigException, TransformerException {
        //generate XForms Model for incoming HTML via XSLT
        String styles = Config.getInstance().getProperty("preprocessor-transform");
        Transformer transformer = this.transformerService.getTransformerByName(styles);
        transformer.setParameter("data", data);
        transformer.setParameter("submission",reqUri);
        DOMResult domResult = new DOMResult();
        transformer.transform(new DOMSource(domDoc), domResult);
        return domResult;
    }

    public ByteArrayOutputStream mixinErrors(String errorXML) throws XFormsConfigException, TransformerException, IOException {
        String styles = Config.getInstance().getProperty("error-transform");
        Transformer transformer = this.transformerService.getTransformerByName(styles);
        org.w3c.dom.Document htmlDoc = getSanitizedHtml();
        transformer.setParameter("errors", new StreamSource(new StringReader(errorXML)));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();


//        StreamResult result = new StreamResult(outputStream);
        DOMResult result = new DOMResult();
        transformer.transform(new DOMSource(htmlDoc), result);
        DOMUtil.prettyPrintDOM(result.getNode());
        return outputStream;
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

    private String getRequestParams() {
        Enumeration<String> params = this.request.getParameterNames();
        StringBuffer formData = new StringBuffer();
        while(params.hasMoreElements()){
            String name = params.nextElement();
            String value = request.getParameter(name);
            formData.append(name);
            formData.append(":");
            if(value != null){
                formData.append(value);
            }else{
                formData.append("");
            }
            formData.append(";");
        }
        if(LOG.isDebugEnabled()){
            LOG.debug("data send by form: " + formData.toString());
        }
        return formData.toString();
    }

}
