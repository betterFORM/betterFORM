/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.Model;
import org.w3c.dom.Element;

/**
 * Implementation of <b>8.2.1 The choices Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Choices.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Choices extends AbstractUIElement {
    private static final Log LOGGER = LogFactory.getLog(Choices.class);

    /**
     * Creates a new choices element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Choices(Element element, Model model) {
        super(element, model);
    }

    // template methods

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
