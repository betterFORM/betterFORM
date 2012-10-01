/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;


import de.betterform.connector.ConnectorFactory;
import de.betterform.connector.URIResolver;
import de.betterform.xml.config.Config;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.submission.AttributeOrValueChild;
import de.betterform.xml.xforms.ui.RepeatItem;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import net.sf.saxon.dom.NodeWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Implements the action as defined in <code>10.1.8 The load Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;, Nick van den Bleeken, Joern Turner, Lars Windauer
 * @version $Id: LoadAction.java 3477 2008-08-19 09:26:47Z joern $
 */
public class LoadAction extends AbstractBoundAction {
    private static final Log LOGGER = LogFactory.getLog(LoadAction.class);
    private AttributeOrValueChild resource;
    private String showAttribute = null;
    private String targetAttribute = null;

    /**
     * Creates a load action implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public LoadAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();

        this.resource = new AttributeOrValueChild(this.element, this.model, RESOURCE_ATTRIBUTE);
        this.resource.init();

        this.showAttribute = getXFormsAttribute(SHOW_ATTRIBUTE);
        if (this.showAttribute == null) {
            // default
            this.showAttribute = "replace";
        }

        this.targetAttribute = getXFormsAttribute(TARGETID_ATTRIBUTE);

    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>load</code> action.
     *
     * @throws XFormsException if an error occurred during <code>load</code>
     *                         processing.
     */
    public void perform() throws XFormsException {
        String bindAttribute = getXFormsAttribute(BIND_ATTRIBUTE);
        String refAttribute = getXFormsAttribute(REF_ATTRIBUTE);
        boolean includeCSS = true;
        boolean includeScript = true;

        Element extension = DOMUtil.findFirstChildNS(this.element, NamespaceConstants.XFORMS_NS, EXTENSION);
        if (extension != null) {
            includeCSS = ("true".equalsIgnoreCase(getXFormsAttribute(extension, "includeCSS")))? true: false;
            includeScript = ("true".equalsIgnoreCase(getXFormsAttribute(extension, "includeScript")))? true: false;
        }

        if (this.resource.isAvailable() && (bindAttribute != null || refAttribute != null)) {
            // issue warning
            getLogger().warn(this + " perform: since both binding and linking attributes are present this action has no effect");
            return;
        }

        // obtain relative URI
        String relativeURI;
        String absoluteURI = null;
        try {
            //todo: needs refactoring
            updateXPathContext();
            if ("none".equals(this.showAttribute)) {
                //todo: dirty dirty dirty
                String evaluatedTarget = evalAttributeValueTemplates(this.targetAttribute, this.element);
//                Element targetElem = this.container.getElementById(evaluatedTarget);
                Element targetElem = getTargetElement(evaluatedTarget);
                destroyembeddedModels(targetElem);
                DOMUtil.removeAllChildren(targetElem);
                HashMap map = new HashMap();


                map.put("show", this.showAttribute);
                if (isRepeated()) {
                    map.put("nameTarget", evaluatedTarget);
                    String idForNamedElement = getXFormsAttribute(getTargetElement(evaluatedTarget), "id");
                    map.put("xlinkTarget", idForNamedElement);

                } else {
                    map.put("xlinkTarget", evaluatedTarget);
                }

                this.container.dispatch(this.target, BetterFormEventNames.LOAD_URI, map);
                return;
            }

            if (this.resource.isAvailable()) {
                relativeURI = this.resource.getValue();
            } else {

                if (this.nodeset == null || this.nodeset.size() == 0) {
                    getLogger().warn(this + " perform: nodeset '" + getLocationPath() + "' is empty");
                    return;
                }

                final Instance instance = this.model.getInstance(getInstanceId());
                relativeURI = instance.getNodeValue(XPathUtil.getAsNode(this.nodeset, 1));
            }

            // resolve uri
            absoluteURI = this.container.getConnectorFactory().getAbsoluteURI(relativeURI, this.element).toString();

            HashMap map = new HashMap();
            handleEmbedding(absoluteURI, map, includeCSS, includeScript);

        } catch (XFormsException e) {
            LOGGER.error("Error performing xf:load action at: " + DOMUtil.getCanonicalPath(this.getElement()));
            throw new XFormsException("Error performing xf:load action at: " + DOMUtil.getCanonicalPath(this.getElement()), e);
        }

        // backwards compat
//        storeInContext(absoluteURI, this.showAttribute);
    }

    private void handleEmbedding(String absoluteURI, HashMap map, boolean includeCSS, boolean includeScript) throws XFormsException {

//        if(this.targetAttribute == null) return;
        //todo: handle multiple params
        if (absoluteURI.indexOf("?") > 0) {
            String name = absoluteURI.substring(absoluteURI.indexOf("?") + 1);
            String value = name.substring(name.indexOf("=") + 1, name.length());
            name = name.substring(0, name.indexOf("="));
            this.container.getProcessor().setContextParam(name, value);
        }
        if (this.targetAttribute == null) {
            map.put("uri", absoluteURI);
            map.put("show", this.showAttribute);

            // dispatch internal betterform event
            this.container.dispatch(this.target, BetterFormEventNames.LOAD_URI, map);
            return;
        }


        if (!("embed".equals(this.showAttribute))) {
            throw new XFormsException("@show must be 'embed' if @target is given but was: '" + this.showAttribute + "' on Element " + DOMUtil.getCanonicalPath(this.element));
        }
        //todo: dirty dirty dirty
        String evaluatedTarget = evalAttributeValueTemplates(this.targetAttribute, this.element);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("targetid evaluated to: " + evaluatedTarget);
        }
        Node embed = getEmbeddedDocument(absoluteURI);
        // fetch CSS

        String inlineCssRules = "";
        String externalCssRules = "";
        String inlineJavaScript = "";
        String externalJavaScript = "";

        if (includeCSS) {
            inlineCssRules = getInlineCSS(embed);
            externalCssRules = getExternalCSS(embed);
        }
        if (includeScript) {
            inlineJavaScript = getInlineJavaScript(embed);
            externalJavaScript = getExternalJavaScript(embed);
        }

        if (Config.getInstance().getProperty("webprocessor.doIncludes", "false").equals("true")) {
            embed = doIncludes(embed, absoluteURI);
        }

        embed = extractFragment(absoluteURI, embed);



        if(LOGGER.isDebugEnabled()){
            DOMUtil.prettyPrintDOM(embed);
        }

        if (embed == null) {
            //todo: review: context info params containing a '-' fail during access in javascript!
            //todo: the following property should have been 'resource-uri' according to spec
            map.put("resourceUri", absoluteURI);
            this.container.dispatch(this.target, XFormsEventNames.LINK_EXCEPTION, map);
            return;
        }

        Element embeddedNode;
        Element targetElem = getTargetElement(evaluatedTarget);

        //Test if targetElem exist.
        if (targetElem != null) {
            // destroy existing embedded form within targetNode
            if ( targetElem.hasChildNodes()) {
                destroyembeddedModels(targetElem);
                Initializer.disposeUIElements(targetElem);
            }

            // import referenced embedded form into host document
            embeddedNode = (Element) this.container.getDocument().importNode(embed, true);

            //import namespaces
            NamespaceResolver.applyNamespaces(targetElem.getOwnerDocument().getDocumentElement(), (Element) embeddedNode);

            // keep original targetElem id within hostdoc
            embeddedNode.setAttributeNS(null, "id", targetElem.getAttributeNS(null, "id"));
            //copy all Attributes that might have been on original mountPoint to embedded node
            DOMUtil.copyAttributes(targetElem, embeddedNode, null);
            targetElem.getParentNode().replaceChild(embeddedNode, targetElem);


            map.put("uri", absoluteURI);
            map.put("show", this.showAttribute);
            map.put("targetElement", embeddedNode);
            map.put("nameTarget", evaluatedTarget);
            map.put("xlinkTarget", getXFormsAttribute(targetElem, "id"));
            map.put("inlineCSS", inlineCssRules);
            map.put("externalCSS", externalCssRules);
            map.put("inlineJavascript", inlineJavaScript);
            map.put("externalJavascript", externalJavaScript);

            this.container.dispatch((EventTarget) embeddedNode, BetterFormEventNames.EMBED, map);
            //create model for it
            this.container.createEmbeddedForm((Element) embeddedNode);

            // dispatch internal betterform event
            this.container.dispatch((EventTarget) embeddedNode, BetterFormEventNames.EMBED_DONE, map);


            this.container.dispatch(this.target, BetterFormEventNames.LOAD_URI, map);
    //        storeInContext(absoluteURI, this.showAttribute);
        } else {
            String message = "Element reference for targetid: " + this.targetAttribute + " not found. " + DOMUtil.getCanonicalPath(this.element);
            /*
            Element div = this.element.getOwnerDocument().createElementNS("", "div");
            div.setAttribute("class", "bfException");
            div.setTextContent("Element reference for targetid: " + this.targetAttribute + "not found. " + DOMUtil.getCanonicalPath(this.element));

            Element body = (Element) this.container.getDocument().getElementsByTagName("body").item(0);
            body.insertBefore(div, DOMUtil.getFirstChildElement(body));
            */
            map.put("message", message);
            map.put("exceptionPath", DOMUtil.getCanonicalPath(this.element));
            this.container.dispatch(this.target, BetterFormEventNames.EXCEPTION, map);
            //throw new XFormsException(message);
        }
    }

    private Node doIncludes(Node embed, String absoluteURI) {

        try {
            String uri = absoluteURI.substring(0, absoluteURI.lastIndexOf("/") + 1);

            CachingTransformerService transformerService  = (CachingTransformerService) this.container.getProcessor().getContext().get(TransformerService.TRANSFORMER_SERVICE);
            Transformer transformer = transformerService.getTransformerByName("include.xsl");

            DOMSource source = new DOMSource(embed);
            DOMResult result = new DOMResult();
            transformer.setParameter("root",uri);
            transformer.transform(source, result);

            return result.getNode();
        } catch (TransformerException e) {
            LOGGER.debug("LoadAction.doInclude: TransformerException:", e);
        }
        return embed;
    }

    private String getInlineCSS(Node embed) throws XFormsException {
        //fetch style element(s) from Node to embed
        String cssRules = "";
        if(embed instanceof Document){
            embed = ((Document) embed).getDocumentElement();
        }
        List result = XPathUtil.evaluate((Element) embed, "//*[@type='text/css' and (not(boolean(@href)))]");
        if (result.size() == 0) {
            return null;
        }
        for (int i = 0; i < result.size(); i++) {
            Object item = result.get(i);
            if (result.get(i) instanceof NodeWrapper) {
                NodeWrapper wrapper = (NodeWrapper) item;
                Node n = (Node) wrapper.getUnderlyingNode();
                cssRules += DOMUtil.getTextNodeAsString(n);
            }
        }
        return cssRules;
    }

    private String getExternalCSS(Node embed) throws XFormsException {
        //fetch style element(s) from Node to embed
        String cssRules = "";
        if(embed instanceof Document){
            embed = ((Document) embed).getDocumentElement();
        }
        List result = XPathUtil.evaluate((Element) embed, "//*[@type='text/css' and boolean(@href)]");
        if (result.size() == 0) {
            return null;
        }

        for (int i = 0; i < result.size(); i++) {
            Object item = result.get(i);
            if (result.get(i) instanceof NodeWrapper) {
                NodeWrapper wrapper = (NodeWrapper) item;
                Node n = (Node) wrapper.getUnderlyingNode();
                cssRules += n.getAttributes().getNamedItem("href").getNodeValue() + "#";
            }
        }
        return cssRules;
    }

    private String getInlineJavaScript(Node embed) throws XFormsException {
        //fetch style element(s) from Node to embed
        String javaScriptCode = "";
        if(embed instanceof Document){
            embed = ((Document) embed).getDocumentElement();
        }


        //Get all inline script ignoring script that folllows a xforms:node
        List result = XPathUtil.evaluate((Element) embed, "//*[not( namespace-uri()='http://www.w3.org/2002/xforms' )]/*[(@type='text/javascript') and (not(boolean(@src)))]");
        if (result.size() == 0) {
            return null;
        }
        for (int i = 0; i < result.size(); i++) {
            Object item = result.get(i);
            if (result.get(i) instanceof NodeWrapper) {
                NodeWrapper wrapper = (NodeWrapper) item;
                if (! "action".equals( ((NodeWrapper) item).getParent().getLocalPart()))  {
                    Node n = (Node) wrapper.getUnderlyingNode();
                    javaScriptCode += DOMUtil.getTextNodeAsString(n);
                }
            }
        }

        return javaScriptCode;
    }

    private String getExternalJavaScript(Node embed) throws XFormsException {
        String javaScriptCode = "";
        if(embed instanceof Document){
            embed = ((Document) embed).getDocumentElement();
        }


        List result = XPathUtil.evaluate((Element) embed, "//*[(@type='text/javascript') and (boolean(@src)) ]");
        for (int i = 0; i < result.size(); i++) {
            Object item = result.get(i);
            if (result.get(i) instanceof NodeWrapper) {
                NodeWrapper wrapper = (NodeWrapper) item;
                Node n = (Node) wrapper.getUnderlyingNode();
                javaScriptCode += n.getAttributes().getNamedItem("src").getNodeValue() + "#";
            }
        }

        return javaScriptCode;
    }
    /**
     * fetches the Element that is the target for embedding. This Element will be replaced by the markup
     * to be embedded or all of its contents is erased in case of an 'unload'.
     *
     * @param evaluatedTarget the targetid of a load action can be a AVT expression and be evaluated. The result
     *                        is treated as an idref to an Element.
     * @return
     * @throws XFormsException
     */
    private Element getTargetElement(String evaluatedTarget) throws XFormsException {
        Element targetElem = null;
        if (isRepeated()) {
            String itemId = getRepeatItemId();
            RepeatItem item = (RepeatItem) container.lookup(itemId);
            int pos = item.getPosition();
            targetElem = (Element) XPathUtil.evaluateAsSingleNode(item.getElement().getOwnerDocument(), "//*[@name='" + evaluatedTarget + "']");
        } else {
            // ##### try to interpret the targetAttribute value as an idref ##### -->
            targetElem = this.container.getElementById(evaluatedTarget);
        }
        return targetElem;
    }

    /**
     * fetches a resources with the help of a Connector. For HTML the response will be a string containing markup that
     * needs to be parsed.
     * <br><br>
     * If a fragment is given on the URI only the resulting part of the response is returned.
     *
     * @param absoluteURI
     * @return a Node
     * @throws XFormsException
     */
    private Node getEmbeddedDocument(String absoluteURI) throws XFormsException {
        //load resource from absoluteURI
        ConnectorFactory factory = ConnectorFactory.getFactory();
        factory.setContext(this.container.getProcessor().getContext());
        URIResolver uriResolver = factory.createURIResolver(absoluteURI, this.element);

        Object answer = uriResolver.resolve();
        Node embed = null;
        if (answer instanceof Node) {
            embed = (Node) answer;
        } else if (answer instanceof String) {
            // we got some plain HTML and this is returned as a string
            try {
                embed = DOMUtil.parseString((String) answer, true, false);
            } catch (ParserConfigurationException e) {
                throw new XFormsException(e);
            } catch (IOException e) {
                throw new XFormsException(e);
            } catch (SAXException e) {
                throw new XFormsException(e);
            }

        }
//        embed = extractFragment(absoluteURI, embed);
        return embed;
    }

    private Node extractFragment(String absoluteURI, Node embed) throws XFormsException {
        if (embed instanceof Document && absoluteURI.indexOf("#") != -1) {
            String s = absoluteURI.substring(absoluteURI.indexOf("#") + 1);
            embed = DOMUtil.getById((Document) embed, s);
        }

        if (embed == null) {
            throw new XFormsException("content returned from URI could not be recognized");
        }
        if (embed instanceof Document) {
            embed = ((Document) embed).getDocumentElement();
        }
        return embed;
    }

    private void destroyembeddedModels(Element targetElem) throws XFormsException {
        NodeList childNodes = targetElem.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;

                String name = elementImpl.getLocalName();
                String uri = elementImpl.getNamespaceURI();

                if (NamespaceConstants.XFORMS_NS.equals(uri) && name.equals(XFormsConstants.MODEL)) {
                    Model model = (Model) elementImpl.getUserData("");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("dispatch 'model-destruct' event to embedded model: " + model.getId());
                        ;
                    }
                    String modelId=model.getId();
                    // do not dispatch model-destruct to avoid problems in lifecycle
                    // TODO: review: this.container.dispatch(model.getTarget(), XFormsEventNames.MODEL_DESTRUCT, null);
                    model.dispose();
                    this.container.removeModel(model);
                    model = null;
                    if(Config.getInstance().getProperty("betterform.debug-allowed").equals("true")){
                        Map contextInfo = new HashMap(1);
                        contextInfo.put("modelId", modelId);
                        this.container.dispatch(this.target, BetterFormEventNames.MODEL_REMOVED, contextInfo);
                    }

                } else {
                    destroyembeddedModels(elementImpl);
                }
            }
        }
    }


    public boolean hasBindingExpression() {
        return (getBindingExpression() != null) || resource.isAvailable();
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

    /**
     * @deprecated backwards compat
     */
    private void storeInContext(String uri, String show) {
        // store in context
        this.container.getProcessor().getContext().put(XFormsProcessor.LOAD_URI, uri);
        this.container.getProcessor().getContext().put(XFormsProcessor.LOAD_TARGET, show);
    }
}

// end of class
