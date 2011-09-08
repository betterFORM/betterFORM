/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implements the action as defined in <code>10.1.1 The action Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ActionAction.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class ActionAction extends AbstractAction {
    private static final Log LOGGER = LogFactory.getLog(ActionAction.class);

    /**
     * Creates an action action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public ActionAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        super.init();

        Initializer.initializeActionElements(this.model, this.element, this.repeatItemId);
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>action</code> action.
     *
     * @throws XFormsException if an error occurred during <code>action</code>
     * processing.
     */
    public void perform() throws XFormsException {
        // [1] check for outermost action handler
        boolean outermost = false;
        UpdateHandler updateHandler = this.model.getUpdateHandler();
        if (updateHandler == null) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " perform: starting outermost action handler for " + this.model);
            }

            // register our own handler with model, so we are outermost
            updateHandler = new UpdateHandler(this.model);
            this.model.setUpdateHandler(updateHandler);
            outermost = true;
        }

        // [2] perform child actions
        NodeList childNodes = this.element.getChildNodes();
        Node node;
        for (int index = 0; index < childNodes.getLength(); index++) {
            node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
            	performConditional((Element) node);
            }
        }

        // [3] perfom deferred updates
        if (outermost) {
            if (getLogger().isDebugEnabled()) {
                getLogger().debug(this + " perform: terminating outermost action handler for " + this.model);
            }

            // first, remove our update handler - then, perform any updates
            // which could need their own update handler
            this.model.setUpdateHandler(null);
            updateHandler.doUpdate();
        }
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
