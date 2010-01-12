/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.constraints.Validator;
import org.w3c.dom.Element;

import java.math.BigDecimal;

/**
 * Implementation of <b>8.1.7 The range Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Range.java 3457 2008-08-13 15:03:54Z joern $
 */
public class Range extends AbstractFormControl {
    private static final Log LOGGER = LogFactory.getLog(Range.class);
    private static BigDecimal startDecimal;
    private static BigDecimal endDecimal;
    private BigDecimal valueDecimal;
    /**
     * Creates a new range element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Range(Element element, Model model) {
        super(element, model);
    }

    // form control methods

    /**
     * Sets the value of this form control.
     * <p/>
     * The bound instance data is updated and the event sequence for this
     * control is executed. Event sequences are described in Chapter 4.6 of
     * XForms 1.0 Recommendation.
     *
     * @param value the value to be set.
     */
    public void setValue(String value) throws XFormsException {
        if (isBound()) {
            setNodeValue(value);
            dispatchValueChangeSequence();
        }
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
        initializeInstanceNode();
        updateXPathContext();
        initializeElementState();
        initializeRange();
        initializeChildren();
        initializeActions();
    }

    // overwriten refresh to dispath IN_RANGE or OUT_OF_RANGE event
    public void refresh() throws XFormsException {
        super.refresh();
        String tmpValue = (String) this.getValue();
        if(tmpValue == null || tmpValue.equals("")){
            tmpValue = "0";
        }
        String datatype = getDatatype();
        if(datatype.contains(":")){
            datatype = datatype.substring(datatype.indexOf(":")+1,datatype.length());
        }
        if(datatype.equals("integer") || datatype.equals("float") || datatype.equals("decimal") || datatype.equals("double")){
            BigDecimal foreignValue = new BigDecimal(tmpValue);
            if(this.valueDecimal.compareTo(foreignValue) != 0){
                if(endDecimal != null && this.startDecimal != null && (foreignValue.compareTo(this.startDecimal) < 0 || foreignValue.compareTo(endDecimal) > 0)){
                    this.model.getContainer().dispatch(this.target, XFormsEventNames.OUT_OF_RANGE, null);
                }else {
                    this.model.getContainer().dispatch(this.target, XFormsEventNames.IN_RANGE, null);
                }
                this.valueDecimal = foreignValue;
            }
        }
    }


    /**
     * Initializes this range.
     *
     * @throws XFormsException if the datatype of the bound is not supported.
     */
    protected final void initializeRange() throws XFormsException {
        if (!isBound()) {
            return;
        }

        // get model item datatype
        Validator validator = this.model.getValidator();
        String datatype = getDatatype();
        if(datatype.contains(":")){
            datatype = datatype.substring(datatype.indexOf(":")+1,datatype.length());
        }
        if (validator.isRestricted("yearMonthDuration", datatype) ||
                validator.isRestricted("dayTimeDuration", datatype) ||
                validator.isRestricted("date", datatype) ||
                validator.isRestricted("time", datatype) ||
                validator.isRestricted("dateTime", datatype) ||
                validator.isRestricted("gYear", datatype) ||
                validator.isRestricted("gYearMonth", datatype) ||
                validator.isRestricted("gMonthDay", datatype) ||
                validator.isRestricted("gDay", datatype) ||
                validator.isRestricted("gMonth", datatype) ||
                validator.isRestricted("integer", datatype) ||
                validator.isRestricted("float", datatype) ||
                validator.isRestricted("decimal", datatype) ||
                validator.isRestricted("double", datatype)) {           


            if(datatype.equals("integer")  || datatype.equals("float") || datatype.equals("decimal") || datatype.equals("double")){
                this.createDecimalRange();
            }
        }else {
            throw new XFormsBindingException("datatype not supported by range control: " + datatype + " at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, datatype);    
        }


    }

    private void createDecimalRange() throws XFormsException {
        // get bound value
        String value = getInstanceValue();
        if (value != null && value.length() > 0) {
            valueDecimal = new BigDecimal(value);
        }
        else {
            // set '0.0' as default value
            valueDecimal = new BigDecimal(0d);
        }

        // get step size
        BigDecimal stepDecimal;
        String stepAttribute = getXFormsAttribute(STEP_ATTRIBUTE);
        if (stepAttribute != null) {
            stepDecimal = new BigDecimal(stepAttribute);
        }
        else {
            // set '1.0' as default step
            stepDecimal = new BigDecimal(1d);
            this.element.setAttributeNS(null, STEP_ATTRIBUTE, stepDecimal.toString());
        }

        // get range start
        String startAttribute = getXFormsAttribute(START_ATTRIBUTE);
        if (startAttribute != null) {
            this.startDecimal = new BigDecimal(startAttribute);
        }
        else {
            // set 'value - (2 * step)' as default start
            this.startDecimal = valueDecimal.subtract(stepDecimal.multiply(new BigDecimal(2d)));
            this.element.setAttributeNS(null, START_ATTRIBUTE, this.startDecimal.toString());
        }

        // get range end

        String endAttribute = getXFormsAttribute(END_ATTRIBUTE);
        if (endAttribute != null) {
            endDecimal = new BigDecimal(endAttribute);
        }
        else {
            // set 'value + (2 * step)' as default end
            endDecimal = valueDecimal.add(stepDecimal.multiply(new BigDecimal(2d)));
            this.element.setAttributeNS(null, END_ATTRIBUTE, endDecimal.toString());
       }

        if (endDecimal != null && this.startDecimal != null && (valueDecimal.compareTo(this.startDecimal) < 0 || valueDecimal.compareTo(endDecimal) > 0)) {
            this.model.getContainer().dispatch(this.target, XFormsEventNames.OUT_OF_RANGE, null);
        }else {
            this.model.getContainer().dispatch(this.target, XFormsEventNames.IN_RANGE, null);
        }
        return;

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
