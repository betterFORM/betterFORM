/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.AttributeOrValueChild;
import de.betterform.xml.xforms.ui.Case;
import de.betterform.xml.xforms.ui.Switch;
import de.betterform.xml.dom.DOMUtil;
import org.w3c.dom.Element;
import de.betterform.xml.xforms.ui.RepeatItem;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Node;

import java.util.HashMap;

/**
 * Implements the action as defined in <code>9.2.3 The toggle Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ToggleAction.java 3501 2008-08-28 17:48:27Z joern $
 */
public class ToggleAction extends AbstractAction {
    private static final Log LOGGER = LogFactory.getLog(ToggleAction.class);
    private AttributeOrValueChild caseId = null;

    /**
     * Creates a toggle action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public ToggleAction(Element element, Model model) {
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
        caseId = new AttributeOrValueChild(this.element, this.model, CASE_ATTRIBUTE);
        caseId.init();

    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>toggle</code> action.
     *
     * @throws XFormsException if an error occurred during <code>toggle</code>
     * processing.
     */
    public void perform() throws XFormsException {
        String evaluatedCaseId = caseId.getValue();
        Object caseObject = null;
        if(isRepeated()) {
            caseObject = null;
            RepeatItem repeatItem = (RepeatItem) this.container.lookup(getRepeatItemId());
            Node caseNode = de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(XPathUtil.evaluate(repeatItem.getElement(), ".//*[@caseId='"+ caseId.getValue() + "']"), 1);
            if(caseNode != null) {
                caseObject = caseNode.getUserData("");
            }
        }
        if(caseObject == null) {
            caseObject = this.container.lookup(evaluatedCaseId);
        }

        if (caseObject == null || !(caseObject instanceof Case)) {
            throw new XFormsBindingException("invalid case id at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, evaluatedCaseId);
        }

        // obtain case and switch elements
        Case toSelect = (Case) caseObject;
        Switch switchElement = toSelect.getSwitch();
        Case isSelected = switchElement.getSelected();

        // perform selection/deselection
        if(!toSelect.getId().equals(isSelected.getId())){
            isSelected.deselect();
        }

        if(!isSelected.getId().equals(toSelect.getId())) {
            toSelect.select();
        }

        // dispatch xforms-deselect and xforms-select events
        if(!toSelect.getId().equals(isSelected.getId())){
            this.container.dispatch(isSelected.getTarget(), XFormsEventNames.DESELECT, null);
        }
        if(!isSelected.getId().equals(toSelect.getId())) {
            this.container.dispatch(toSelect.getTarget(), XFormsEventNames.SELECT, null);
        }

        // dispatch internal betterform event
        HashMap map = new HashMap();
        map.put("selected", toSelect.getId());
        map.put("deselected", isSelected.getId());
        this.container.dispatch(switchElement.getTarget(), BetterFormEventNames.SWITCH_TOGGLED, map);

        //as we did an optimization to only update selected 'case' elements we need to refresh after a toggle
        doRefresh(true);
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
