/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.state.HelperElementState;
import org.w3c.dom.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Implementation of <b>8.2.3 The value Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Value.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Value extends BindingElement {
    private static final Log LOGGER = LogFactory.getLog(Value.class);

    /**
     * Creates a new value element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Value(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     */
    public void init() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " init");
        }
        

        initializeDefaultAction();
        updateXPathContext();
        initializeElementState();
    }

    /**
     * Performs element update.
     *
     * @throws XFormsException if any error occurred during update.
     */
    public void refresh() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " update");
        }
        updateXPathContext();        
        updateElementState();
    }

    /**
     * Performs element disposal.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " dispose");
        }

        disposeDefaultAction();
        disposeElementState();
        disposeSelf();
    }

    // template methods

    /**
     * Factory method for the element state.
     *
     * @return an element state implementation or <code>null</code> if no state
     *         keeping is required.
     * @throws XFormsException if an error occurred during creation.
     */
    protected UIElementState createElementState() throws XFormsException {
        return isBound() ? new HelperElementState() : null;
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
