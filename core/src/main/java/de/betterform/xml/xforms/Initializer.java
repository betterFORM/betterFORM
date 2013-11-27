/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms;

import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.action.AbstractAction;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.bind.Bind;
import de.betterform.xml.xforms.model.submission.Header;
import de.betterform.xml.xforms.model.submission.Submission;
import de.betterform.xml.xforms.ui.AVTElement;
import de.betterform.xml.xforms.ui.AbstractUIElement;
import de.betterform.xml.xpath.XPathReferenceFinder;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;


/**
 * Initializer holds some static methods that help with recursive initialization and updating of XFormsElement
 * instances.
 *
 * @author Ulrich Nicolas Liss&eacute;

 * @version $Id: Initializer.java 3477 2008-08-19 09:26:47Z joern $
 */
public class Initializer {
    /**
     * Avoids instantiation.
     */
    private Initializer() {
    }

    /**
     * Initializes all actions listening on xforms-model-construct
     *
     * @param model   the current context model.
     * @param element the element to start with.
     * @throws XFormsException if any error occurred during init.
     */
    public static void initializeModelConstructActionElements(Model model, Element element) throws XFormsException {

        XFormsElementFactory elementFactory = model.getContainer().getElementFactory();
        NodeList childNodes = element.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;
                String eventType = ((Element) node).getAttributeNS(NamespaceConstants.XMLEVENTS_NS, "event");
                if (XFormsEventNames.MODEL_CONSTRUCT.equals(eventType) && XFormsElementFactory.isActionElement(elementImpl)){
                    //pass context model
                    initializeActionElement(model, null, elementFactory, elementImpl);
                }
            }
        }

    }

    /**
     * Initializes all action children of the specified element.
     *
     * @param model   the current context model.
     * @param element the element to start with.
     * @throws XFormsException if any error occurred during init.
     */
    public static void initializeActionElements(Model model, Element element) throws XFormsException {
        Initializer.initializeActionElements(model, element, null);
    }

    /**
     * Initializes all action children of the specified element.
     *
     * @param model        the current context model.
     * @param element      the element to start with.
     * @param repeatItemId the id of the containing repeat item, if any.
     * @throws XFormsException if any error occurred during init.
     */
    public static void initializeActionElements(Model model, Element element, String repeatItemId) throws XFormsException {
        XFormsElementFactory elementFactory = model.getContainer().getElementFactory();
        NodeList childNodes = element.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;
                String eventType = ((Element) node).getAttributeNS(NamespaceConstants.XMLEVENTS_NS, "event");
                if (!(XFormsEventNames.MODEL_CONSTRUCT.equals(eventType)) && XFormsElementFactory.isActionElement(elementImpl)){
                    //pass context model
                    initializeActionElement(model, repeatItemId, elementFactory, elementImpl);
                }
            }
        }
    }

    private static void initializeActionElement(Model model, String repeatItemId, XFormsElementFactory elementFactory, Element elementImpl) throws XFormsException {
        Model contextModel = Initializer.getContextModel(model, elementImpl);
        AbstractAction actionElement = (AbstractAction) elementFactory.createXFormsElement(elementImpl, contextModel);

        if (repeatItemId != null) {
            actionElement.setRepeatItemId(repeatItemId);
            actionElement.setGeneratedId(model.getContainer().generateId());
            actionElement.registerId();
        }
        // 31-07-2010	Ronald van Kuijk
        // Removed since duplicate with setting in the if above. And it is not done for the repeat either.
        //actionElement.setRepeatItemId(repeatItemId);
        actionElement.init();
    }

    /**
     * Initializes all bind children of the specified element.
     *
     * @param model   the current context model.
     * @param element the element to start with.
     * @throws XFormsException if any error occurred during init.
     */
    public static void initializeBindElements(Model model, Element element, XPathReferenceFinder referenceFinder) throws XFormsException {
        XFormsElementFactory elementFactory = model.getContainer().getElementFactory();
        NodeList childNodes = element.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;

                if ((XFormsElementFactory.isBindElement(elementImpl))) {
                    Bind bindElement = (Bind) elementFactory.createXFormsElement(elementImpl, model);
                    bindElement.setReferenceFinder(referenceFinder);
                    bindElement.init();
                }
            }
        }
    }

    /**
     * Initializes all submission children of the specified element.
     *
     * @param model   the current context model.
     * @param element the element to start with.
     * @throws XFormsException if any error occurred during init.
     */
    public static void initializeSubmissionElements(Model model, Element element) throws XFormsException {
        XFormsElementFactory elementFactory = model.getContainer().getElementFactory();
        NodeList childNodes = element.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;

                if ((XFormsElementFactory.isSubmissionElement(elementImpl))) {
                    Submission submissionElement = (Submission) elementFactory.createXFormsElement(elementImpl, model);
                    submissionElement.init();
                }
            }
        }
    }

    /**
     * Initializes all ui children of the specified element.
     *
     * @param element the element to start with.
     * @throws XFormsException if any error occurred during init.
     */
    public static void initializeUIElements(Element element) throws XFormsException
    {
        Element elementImpl = element.getOwnerDocument().getDocumentElement();
        Container container = (Container) elementImpl.getUserData("");

        String evalAVT = elementImpl.getAttributeNS(NamespaceConstants.BETTERFORM_NS,"evalAVTs");
        if(evalAVT != null && evalAVT.length() != 0) {
            Initializer.initializeUIElements(container.getDefaultModel(), element, null,evalAVT);
        }else{
            Initializer.initializeUIElements(container.getDefaultModel(), element, null,null);
        }
    }

    /**
     * Initializes all ui children of the specified element.
     *
     * @param model        the current context model.
     * @param element      the element to start with.
     * @param repeatItemId the id of the containing repeat item, if any.
     * @throws XFormsException if any error occurred during init.
     */
    public static void initializeUIElements(Model model, Element element, String repeatItemId, String evalAVT) throws XFormsException
    {
    	Container container = model.getContainer();
        XFormsElementFactory xformsFactory = container.getElementFactory();
        CustomElementFactory customFactory = container.getCustomElementFactory();
        WebComponentElementFactory webComponentElementFactory = container.getWebComponentElementFactory();

        NodeList childNodes = element.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE ) {
                Element elementImpl = (Element) node;

               if((elementImpl.getParentNode()).getUserData("") instanceof Header) {
                    return;                   
               }
                if ((XFormsElementFactory.isUIElement(elementImpl))) {
                	//initializes a standard XForms element
                    Model contextModel = Initializer.getContextModel(model, elementImpl);
                    AbstractUIElement uiElement =
                        (AbstractUIElement) xformsFactory.createXFormsElement(elementImpl, contextModel);

                    initXFormsObject(model, repeatItemId, uiElement);
                }else if(XFormsElementFactory.isActionElement(elementImpl)){
                    if(hasXFormsParent(elementImpl)) {
                        initializeActionElement(model,repeatItemId,xformsFactory,elementImpl);
                    }
                }else if(webComponentElementFactory.isUIElement(elementImpl)){
                    //todo: continue
                    Model contextModel = Initializer.getContextModel(model, elementImpl);
                    AbstractUIElement component = (AbstractUIElement) webComponentElementFactory.createXFormsElement(elementImpl,contextModel);
                    if(component != null)
                        initXFormsObject(model,repeatItemId,component);

                }else if (customFactory.isCustomElement(elementImpl)) {
                	//initializes a custom element
                    Model contextModel = Initializer.getContextModel(model, elementImpl);

                    // If setters, register and init are in a shared abstract class, this can be reduced
                    // Or add a parameter to the config: UIElement/ActionElement and use that in a isCustomXXX
                    // Something could go wrong if it is an action and has a parent. See above (hasXFormsParent())
                    Object customElement = customFactory.createCustomXFormsElement(elementImpl, contextModel);

                    if (customElement != null && customElement instanceof AbstractUIElement) {
                        initXFormsObject(model, repeatItemId, ((AbstractUIElement)customElement));
                    }

                    if (customElement != null && customElement instanceof AbstractAction) {
                    	if (repeatItemId != null) {
                        	((AbstractAction)customElement).setRepeatItemId(repeatItemId);
                        	((AbstractAction)customElement).setGeneratedId(model.getContainer().generateId());
                        	((AbstractAction)customElement).registerId();
                        }
                        ((AbstractAction)customElement).init();
                    }

                } else if(evalAVT != null && hasAVT(elementImpl,evalAVT)){
                    Model contextModel = Initializer.getContextModel(model, elementImpl);
                    AbstractUIElement uiElement = new AVTElement(elementImpl,contextModel);
                    elementImpl.setUserData("",uiElement,null);

                    initXFormsObject(model, repeatItemId, uiElement);


                } else {
                	//recursive call to process child elements
                    Initializer.initializeUIElements(model, elementImpl, repeatItemId, evalAVT);
                }
            }
        }
    }

    private static void initXFormsObject(Model model, String repeatItemId, AbstractUIElement uiElement) throws XFormsException {
        if (repeatItemId != null) {
            uiElement.setRepeatItemId(repeatItemId);
            uiElement.setGeneratedId(model.getContainer().generateId());
            uiElement.registerId();
        }
        uiElement.init();
    }

    /**
     * Pragmatic hack to avoid forward looking for action elements within not xforms host document elements
     * @param node
     * @return
     */

    private static boolean hasXFormsParent(Node node) {
        Node parent = node.getParentNode();

        if(parent == null || parent.getNodeType() == Node.DOCUMENT_NODE){
            return true;
        }
        Object userdata = parent.getUserData("");
        if(userdata != null && userdata instanceof XFormsElement) {
            return false;
        } else {
            return true;
        }
    }

    private static boolean hasAVT(Element element,String allowedAttributes){
        String[] attrs = allowedAttributes.split(" ");
        for(String attribute : attrs){

            if(element.hasAttribute(attribute) && element.getAttribute(attribute).indexOf("{")!=-1){
                if(element.getAttribute(attribute).indexOf("{{") != -1) return false;//we hit some double moustache
                return true;
            }
        }
        return false;
    }

    /**
     * Updates all ui children of the specified element. This is called during refresh.
     *
     * @param element the element to start with.
     * @param model xforms model the element belongs to.
     * @throws XFormsException if any error occurred during update.
     */
    public static void updateUIElements(Element element, Model model) throws XFormsException {
        NodeList childNodes = element.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;
                Object userData = elementImpl.getUserData("");

                if ((userData != null) && userData instanceof AbstractUIElement) {
                    // only refresh UIElements for the given model
                    if(((AbstractUIElement) userData).getModel().getId().equals(model.getId())){
                        ((AbstractUIElement) userData).refresh();
                    }else{
                        Initializer.updateUIElements(elementImpl,model);
                    }
                } else {
                    Initializer.updateUIElements(elementImpl,model);
                }
            }
        }
    }

    /**
     * Disposes all ui children of the specified element.
     *
     * @param element the element to start with.
     * @throws XFormsException if any error occurred during disposal.
     */
    public static void disposeUIElements(Element element) throws XFormsException {
        NodeList childNodes = element.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;
                Object userData = elementImpl.getUserData("");

                if ((userData != null) && userData instanceof AbstractUIElement) {
                    ((AbstractUIElement) userData).dispose();
                } else {
                    Initializer.disposeUIElements(elementImpl);
                }
            }
        }
    }

    /**
     * Returns the context model of the specified element.
     * <p/>
     * The context model is determined as follows: <ol> <li>If the element has a model binding, the context model of the
     * bind element is returned.</li> <li>If the element has an ui binding including a model attribute, this model is
     * looked up and returned.</li> <li>The current context model is returned.</li> </ol>
     *
     * @param model   the current context model.
     * @param element the element in question.
     * @return the context model of the specified element.
     * @throws XFormsBindingException if the model binding or the ui binding is invalid.
     */
    public static Model getContextModel(Model model, Element element) throws XFormsBindingException {
        // check for model binding
        String bindId = null;
        if (element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.BIND_ATTRIBUTE)) {
            bindId = element.getAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.BIND_ATTRIBUTE);
        }
        else if (element.hasAttributeNS(null, XFormsConstants.BIND_ATTRIBUTE)) {
            bindId = element.getAttributeNS(null, XFormsConstants.BIND_ATTRIBUTE);
        }
        else if (element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.REPEAT_BIND_ATTRIBUTE)) {
            bindId = element.getAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.REPEAT_BIND_ATTRIBUTE);
        }

        if (bindId != null) {
            XFormsElement xFormsElement = model.getContainer().lookup(bindId);
            if (xFormsElement == null) {
                throw new XFormsBindingException("bind Element with id: '" + bindId + "' not found in model", (EventTarget) element, bindId);
            }
            if (!(xFormsElement instanceof Bind)) {
                throw new XFormsBindingException("element '" + bindId + "' is not a bind", (EventTarget) element, bindId);
            }

            return xFormsElement.getModel();
        }

        // check for ui binding
        String modelId = null;
        if (element.hasAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.MODEL_ATTRIBUTE)) {
            modelId = element.getAttributeNS(NamespaceConstants.XFORMS_NS, XFormsConstants.MODEL_ATTRIBUTE);
        }
        else if (element.hasAttributeNS(null, XFormsConstants.MODEL_ATTRIBUTE)) {
            modelId = element.getAttributeNS(null, XFormsConstants.MODEL_ATTRIBUTE);
        }

        if (modelId != null) {
            XFormsElement xFormsElement = model.getContainer().lookup(modelId);
            if (xFormsElement == null) {
                throw new XFormsBindingException("model '" + modelId + "' not found", (EventTarget) element, modelId);
            }
            if (!(xFormsElement instanceof Model)) {
                throw new XFormsBindingException("element '" + modelId + "' is not a model", (EventTarget) element, modelId);
            }

            return (Model) xFormsElement;
        }

        return model;
    }

}

//end of class
