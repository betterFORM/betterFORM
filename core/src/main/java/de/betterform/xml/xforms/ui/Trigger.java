/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.state.BoundElementState;
import org.w3c.dom.Element;

/**
 * Implementation of <b>8.1.8 The trigger Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Trigger.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Trigger extends AbstractFormControl {
    private static final Log LOGGER = LogFactory.getLog(Trigger.class);

    /**
     * Creates a new trigger element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Trigger(Element element, Model model) {
        super(element, model);
    }

    // form control methods

    /**
     * Sets the value of this form control.
     * <p/>
     * If this method is called a warning is issued since the value of a
     * <code>trigger</code> control cannot be set.
     *
     * @param value the value to be set.
     */
    public void setValue(String value) throws XFormsException {
        getLogger().warn(this + " set value: the value of a trigger control cannot be set");
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
        return isBound() ? new BoundElementState(false, false) : null;
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
