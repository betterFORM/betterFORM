package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebUtil;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.util.PositionalXMLReader;
import de.betterform.xml.xforms.exception.XFormsErrorIndication;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * a servlet to generate an error page with xslt
 *
 * author: joern turner
 */
public class ErrorServlet extends HttpServlet {
    private static final Log LOGGER = LogFactory.getLog(ErrorServlet.class);


    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        WebUtil.nonCachingResponse(response);
        //pick up the exception details
        String xpath ="unknown";
        String cause= "";
        String msg = (String) getSessionAttribute(request,"betterform.exception.message");
        if (msg != null) {
            int start = msg.indexOf("::");
            if(start > 3){
                xpath = msg.substring(start+2);
                msg=msg.substring(0,start);
            }
            //todo: don't we need an 'else' here?
        }
        Exception ex = (Exception) getSessionAttribute(request, "betterform.exception");
        if(ex != null && ex.getCause() != null && ex.getCause().getMessage() != null){
            cause = ex.getCause().getMessage();
        }

        //create XML structure for exception details
        Element rootNode = createRootElement();
        appendElement(rootNode,"context",request.getContextPath());
        appendElement(rootNode,"url", request.getSession().getAttribute("betterform.referer").toString());
        appendElement(rootNode,"xpath",xpath);
        appendElement(rootNode,"message",msg);
        appendElement(rootNode,"cause",cause);

        //transform is different depending on exception type
        if(ex instanceof XFormsErrorIndication){
            Object o = ((XFormsErrorIndication)ex).getContextInfo();
            if(o instanceof HashMap){
                HashMap<String,Object> map = (HashMap) ((XFormsErrorIndication) ex).getContextInfo();
                if(map.size() != 0){
                    Element contextinfo = rootNode.getOwnerDocument().createElement("contextInfo");
                    rootNode.appendChild(contextinfo);
                    for(Map.Entry<String,Object> entry : map.entrySet()){
                        appendElement(rootNode, entry.getKey(), entry.getValue().toString());
                    }
                }
            }
            //todo: check -> there are contextInfos containing a simple string but seems to be integrated within above error message already - skip for now
/*
            else{
            }
*/
            Document hostDoc = (Document) getSessionAttribute(request, "betterform.hostDoc");
            String serializedDoc = DOMUtil.serializeToString(hostDoc);
            //reparse
            try {
                byte bytes[] = serializedDoc.getBytes("UTF-8");
                Document newDoc = PositionalXMLReader.readXML(new ByteArrayInputStream(bytes));
                DOMUtil.prettyPrintDOM(newDoc);
                //eval xpath
                Node n = XPathUtil.evaluateAsSingleNode(newDoc,xpath);
                String linenumber = (String) n.getUserData("lineNumber");
                appendElement(rootNode,"lineNumber",linenumber);

                DOMUtil.prettyPrintDOM(rootNode);

                doTransform(response, newDoc, "highlightError.xsl", rootNode);
            } catch (Exception e) {
                e.printStackTrace();  
            }

        } else{
            doTransform(response, rootNode.getOwnerDocument(), "error.xsl",null);
        }
    }

    private void doTransform(HttpServletResponse response, Document input, String stylesheetName,Element params) throws IOException {
        ServletContext context = getServletContext();
        CachingTransformerService transformerService  = (CachingTransformerService) context.getAttribute(TransformerService.TRANSFORMER_SERVICE);
        Source xmlSource =  new DOMSource(input);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            Transformer transformer = transformerService.getTransformerByName(stylesheetName);
            if(params != null){
                transformer.setParameter("errorInfo",params);
            }
            transformer.transform(xmlSource, new StreamResult(outputStream));

        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        response.setContentType(WebUtil.HTML_CONTENT_TYPE);
        response.setContentLength(outputStream.toByteArray().length);
        response.getOutputStream().write(outputStream.toByteArray());
        response.getOutputStream().close();
    }

    private Element createRootElement() {
        Document inputDoc = DOMUtil.newDocument(false, false);
        Element rootNode = inputDoc.createElement("error");
        inputDoc.appendChild(rootNode);
        return rootNode;
    }

    private Object getSessionAttribute(HttpServletRequest request,String sessionVar) {
        Object o = request.getSession().getAttribute(sessionVar);
        request.getSession().removeAttribute(sessionVar);
        return o;
    }

    private void appendElement(Element parent,String elementName, String value) {
        Element e = parent.getOwnerDocument().createElement(elementName);
        DOMUtil.setElementValue(e, value);
        parent.appendChild(e);
    }
}
