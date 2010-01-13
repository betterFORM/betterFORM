/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.ui.Output;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.HashMap;

/**
 * Implements the action as defined in <code>10.1.12 The message
 * Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: MessageAction.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class MessageAction extends AbstractBoundAction {
    private static final Log LOGGER = LogFactory.getLog(MessageAction.class);
    private String levelAttribute;
    private String textContent;

    /**
     * Creates a message action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public MessageAction(Element element, Model model) {
        super(element, model);
        try {
            Initializer.initializeUIElements(model, this.element,null,null);
        } catch (XFormsException e) {
            e.printStackTrace();
        }
    }


    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();

        this.levelAttribute = getXFormsAttribute(LEVEL_ATTRIBUTE);
        if (this.levelAttribute == null) {
            getLogger().warn(this + " init: required level attribute missing, assuming 'modal'");
            this.levelAttribute = "modal";
        }

        Node child = this.element.getFirstChild();
        this.textContent = evaluateMessageContent();

    }

    private String evaluateMessageContent() throws XFormsException {
        String message ="";
        if(this.element.hasChildNodes()) {
            NodeList childNodes = this.element.getChildNodes();
            for(int i =0;i<childNodes.getLength();i++) {
                Node node = childNodes.item(i);
                switch(node.getNodeType()){
                    case Node.TEXT_NODE:
                        message += node.getTextContent();
                        break;
                    case Node.ELEMENT_NODE:
                        Element element = (Element) node;
                        Object userData = element.getUserData("");
                        if ((userData != null) && userData instanceof Output) {
                            Output output = (Output) userData;
                            message += (String) output.computeValueAttribute();
                        }
                        break;
               }
            }
        }
        return message;

    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>message</code> action.
     *
     * @throws XFormsException if an error occurred during <code>message</code>
     * processing.
     */
    public void perform() throws XFormsException {
        String bindAttribute = getXFormsAttribute(BIND_ATTRIBUTE);
        String refAttribute = getXFormsAttribute(REF_ATTRIBUTE);
        String message = "";

        if (bindAttribute != null || refAttribute != null) {
            // get message from instance data
            updateXPathContext();
            message = this.model.getInstance(getInstanceId()).getNodeValue(de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(this.nodeset, 1));
        }


        else {
            // get message from text child
            message = evaluateMessageContent();
        }

        // dispatch internal betterform event
        HashMap map = new HashMap();
        map.put("message", message);
        map.put("level", this.levelAttribute);
        this.container.dispatch(this.target, BetterFormEventNames.RENDER_MESSAGE, map);
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }
}

// end of class
