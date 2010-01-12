/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.Repeat;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.dom.DOMUtil;
import org.w3c.dom.Element;

import java.util.List;

/**
 * Implements the action as defined in <code>9.3.7 The setindex Element</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SetIndexAction.java 3457 2008-08-13 15:03:54Z joern $
 */
public class SetIndexAction extends AbstractBoundAction {
    private static Log LOGGER = LogFactory.getLog(SetIndexAction.class);
    private String indexAttribute;
    private String repeatAttribute;

    /**
     * Creates a setindex action implementation.
     *
     * @param element the element.
     * @param model the context model.
     */
    public SetIndexAction(Element element, Model model) {
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

        this.repeatAttribute = getXFormsAttribute(REPEAT_ATTRIBUTE);
        if (this.repeatAttribute == null) {
            throw new XFormsBindingException("missing repeat attribute at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, null);
        }

        this.indexAttribute = getXFormsAttribute(INDEX_ATTRIBUTE);
        if (this.indexAttribute == null) {
            throw new XFormsBindingException("missing index attribute at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, null);
        }
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>setindex</code> action.
     *
     * @throws XFormsException if an error occurred during <code>setindex</code>
     * processing.
     */
    public void perform() throws XFormsException {
        // check repeat idref
        Object repeatObject = this.container.lookup(this.repeatAttribute);
        if (repeatObject == null || !(repeatObject instanceof Repeat)) {
            throw new XFormsBindingException("invalid repeat id at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, this.repeatAttribute);
        }
        Repeat repeat = (Repeat) repeatObject;

        List resultNodeset = evalInScopeContext();
        final String relativeExpr = this.indexAttribute;
        //todo:fix this hack - last() function does not evaluate correctly thus we take the size of the nodeset
        String result;
        if(relativeExpr.equals("last()")){
            result = resultNodeset.size() + "";
        }else{
            result = XPathCache.getInstance().evaluateAsString(resultNodeset, getPosition(), relativeExpr, getPrefixMapping(), xpathFunctionContext);
        }


        double value = Double.NaN;
        if(result != null)
           value = Double.valueOf(result);

        if (Double.isNaN(value)) {
            getLogger().warn(this + " perform: expression '" + this.indexAttribute + "' does not evaluate to an integer");
            return;
        }

        // check boundaries
        long index = Math.round(value);
        if (index < 1) {
            repeat.setIndex(1);
            this.container.dispatch(repeat.getTarget(), XFormsEventNames.SCROLL_FIRST, null);
        }
        else if (index > repeat.getContextSize()) {
            index = repeat.getContextSize();
            repeat.setIndex((int) index);
            this.container.dispatch(repeat.getTarget(), XFormsEventNames.SCROLL_LAST, null);
        }
        else {
            // set repeat index
            repeat.setIndex((int) index);
        }

        // update behaviour
        doRebuild(true);
        doRecalculate(true);
        doRevalidate(true);
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
