/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.util.HashMap;

/**
 * handles JavaScript inside of action blocks.
 *
 * @author Joern Turner
 * @version $Id: ScriptAction.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class ScriptAction extends AbstractBoundAction {
    private static Log LOGGER = LogFactory.getLog(ScriptAction.class);
    private String script = null;

    /**
     * Creates a script action implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public ScriptAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();
        this.script = DOMUtil.getTextNode(this.element).getNodeValue();

        if (getLogger().isTraceEnabled()) {
            getLogger().trace("Script content: " + script);
        }
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>script</code> action.
     *
     * @throws XFormsException if an error occurred during <code>script</code>
     *                         processing.
     */
    public void perform() throws XFormsException {
        // dispatch internal betterform event
        HashMap map = new HashMap();
        map.put("script", script);
        this.container.dispatch(this.target, BetterFormEventNames.SCRIPT_ACTION, map);
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
