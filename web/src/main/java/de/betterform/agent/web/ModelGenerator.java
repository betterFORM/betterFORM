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

import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.File;
import java.io.IOException;
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

    public ModelGenerator(){
    }


    //todo: can probably be refactored to use less arguments
    public static Node generateXFormsModel(HttpServletRequest request,
                                           CachingTransformerService
                                           cachingTransformerService,
                                           WebFactory webFactory)
            throws URISyntaxException, TransformerException, XFormsConfigException, IOException {
        // get referer document
        /*
        This is a weak point: the referer header is not reliable in all cases or might get filtered by proxies. For this to work
        the referer must return the URL of the host document of the form.
        */
        String referer = request.getHeader("Referer");
        String reqUri = request.getRequestURI();
        String data = getRequestParams(request);

        //Parse original HTML and sanitize to XHTML
        URL uri = new URL(referer);
        Document doc = Jsoup.parse(uri, 1000);
        org.w3c.dom.Document domDoc = DOMBuilder.jsoup2DOM(doc);
        DOMResult domResult = generateModel(cachingTransformerService, reqUri, data, domDoc);


        // persist created model to preconfigured folder
        String contextname = request.getContextPath();
        int pos = referer.indexOf(contextname);
        String relPath = referer.substring(pos+contextname.length());
        String fullPath=null;
        try {
            fullPath = WebFactory.getRealPath(relPath,webFactory.getServletContext());
        } catch (XFormsConfigException e) {
            e.printStackTrace();
        }


        File htmlFile = new File(fullPath);
        File parentFile = htmlFile.getParentFile();
        String fileName = htmlFile.getName();
        String baseName = fileName.substring(0,fileName.indexOf("."));
        String xfmFileName = baseName + ".xfm";

        File xformModelFile = new File(parentFile,xfmFileName);
        if(!xformModelFile.exists()){
            //create it
            String xm = DOMUtil.serializeToString((org.w3c.dom.Document) domResult);
            FileUtils.writeStringToFile(xformModelFile, xm);
        }
        return domResult.getNode();
    }

    private static DOMResult generateModel(CachingTransformerService cachingTransformerService, String reqUri, String data, org.w3c.dom.Document domDoc) throws XFormsConfigException, TransformerException {
        //generate XForms Model for incoming HTML via XSLT
        String styles = Config.getInstance().getProperty("preprocessor-transform");
        Transformer transformer = cachingTransformerService.getTransformerByName(styles);
        transformer.setParameter("data", data);
        transformer.setParameter("submission",reqUri);
        DOMResult domResult = new DOMResult();
        transformer.transform(new DOMSource(domDoc), domResult);
        return domResult;
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

    private static String getRequestParams(HttpServletRequest request) {
        Enumeration<String> params = request.getParameterNames();
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
