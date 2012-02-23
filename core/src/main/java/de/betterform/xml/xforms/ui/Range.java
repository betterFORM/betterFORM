/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.constraints.Validator;
import de.betterform.xml.xforms.ui.state.RangeElementState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private String startValue;
    private static BigDecimal startDecimal;
    private String endValue;
    private static BigDecimal endDecimal;
    private String stepValue;
    private static BigDecimal stepDecimal;
    private BigDecimal valueDecimal;
    private String datatype = null;
    private String nsdatatype = null;
    /**
     * Creates a new range element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Range(Element element, Model model) {
        super(element, model);
    }


    protected UIElementState createElementState() throws XFormsException {
        return hasBindingExpression() ? new RangeElementState() : null;
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
        if (hasBindingExpression()) {
			if(LOGGER.isDebugEnabled()){
                LOGGER.trace("Range.setValue value:" + value + " datatype: " + this.datatype);
			}

			String typedValue = value;

        	setNodeValue(typedValue);
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
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(this + " init");
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

        this.datatype = getDatatype();
        if(this.datatype.contains(":")){
            this.datatype = this.datatype.substring(this.datatype.indexOf(":")+1,this.datatype.length());
        }
        BigDecimal foreignValue = null;
        if("int".equals(this.datatype) || "integer".equals(this.datatype) || "float".equals(this.datatype) || "decimal".equals(this.datatype) || "double".equals(this.datatype)|| "string".equals(this.datatype)){
            if(tmpValue == null || tmpValue.equals("")) tmpValue = "0";
            foreignValue = new BigDecimal(tmpValue);
        }

		if(this.valueDecimal.compareTo(foreignValue) != 0){
			if(endDecimal != null && this.startDecimal != null && (foreignValue.compareTo(this.startDecimal) < 0 || foreignValue.compareTo(endDecimal) > 0)){
				this.model.getContainer().dispatch(this.target, XFormsEventNames.OUT_OF_RANGE, null);
			}else {
				this.model.getContainer().dispatch(this.target, XFormsEventNames.IN_RANGE, null);
			}
			this.valueDecimal = foreignValue;
		}
    }

    /**
     * Initializes this range.
     *
     * @throws XFormsException if the datatype of the bound is not supported.
     */
    protected final void initializeRange() throws XFormsException {
        if (!hasBindingExpression()) {
            LOGGER.warn("Range: No binding attributes found!");
            return;
        }

        // get model item datatype
        Validator validator = this.model.getValidator();


        this.nsdatatype = getDatatype();
        if(this.nsdatatype.contains(":")){
            this.datatype = this.nsdatatype.substring(this.nsdatatype.indexOf(":")+1,this.nsdatatype.length());
        } else {
            this.datatype = nsdatatype;
        }
        if (validator.isRestricted("yearMonthDuration", this.datatype) ||
                validator.isRestricted("dayTimeDuration", this.datatype) ||
                validator.isRestricted("date", this.datatype) ||
                validator.isRestricted("time", this.datatype) ||
                validator.isRestricted("dateTime", this.datatype) ||
                validator.isRestricted("gYear", this.datatype) ||
                validator.isRestricted("gYearMonth", this.datatype) ||
                validator.isRestricted("gMonthDay", this.datatype) ||
                validator.isRestricted("gDay", this.datatype) ||
                validator.isRestricted("gMonth", this.datatype) ||
                validator.isRestricted("int", this.datatype) ||
                validator.isRestricted("integer", this.datatype) ||
                validator.isRestricted("float", this.datatype) ||
                validator.isRestricted("decimal", this.datatype) ||
                validator.isRestricted("double", this.datatype)) {


            if("int".equals(this.datatype) || "integer".equals(this.datatype)  || "float".equals(this.datatype) || "decimal".equals(this.datatype) || "double".equals(this.datatype)){
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
            this.valueDecimal = new BigDecimal(value);
        }
        else {
            // set '0.0' as default value
            this.valueDecimal = new BigDecimal(0d);
        }

        // get step size
        //BigDecimal stepDecimal;
        String stepAttribute = getXFormsAttribute(STEP_ATTRIBUTE);
        if (stepAttribute != null) {
            this.stepDecimal = new BigDecimal(stepAttribute);
        }
        else {
            // set '1.0' as default step
            this.stepDecimal = new BigDecimal(1d);
            this.stepValue = String.valueOf(this.stepDecimal);
//            this.element.setAttributeNS(null, STEP_ATTRIBUTE, stepDecimal.toString());
        }
        getUIElementState().setProperty(STEP_ATTRIBUTE, this.stepDecimal.toString());

        // get range start
        String startAttribute = getXFormsAttribute(START_ATTRIBUTE);
        if (startAttribute != null) {
            this.startDecimal = new BigDecimal(startAttribute);
        }
        else {
            // set 'value - (2 * step)' as default start
            this.startDecimal = valueDecimal.subtract(this.stepDecimal.multiply(new BigDecimal(2d)));
            this.startValue = String.valueOf(this.startDecimal);
//            this.element.setAttributeNS(null, START_ATTRIBUTE, this.startDecimal.toString());
        }
        getUIElementState().setProperty(START_ATTRIBUTE, this.startDecimal.toString());

        // get range end

        String endAttribute = getXFormsAttribute(END_ATTRIBUTE);
        if (endAttribute != null) {
            endDecimal = new BigDecimal(endAttribute);
        }
        else {
            // set 'value + (2 * step)' as default end
            this.endDecimal = this.valueDecimal.add(this.stepDecimal.multiply(new BigDecimal(2d)));
            this.endValue = String.valueOf(this.endDecimal);
//            this.element.setAttributeNS(null, END_ATTRIBUTE, endDecimal.toString());
       }
       getUIElementState().setProperty(END_ATTRIBUTE, endDecimal.toString());

        if (this.endDecimal != null && this.startDecimal != null && (this.valueDecimal.compareTo(this.startDecimal) < 0 || this.valueDecimal.compareTo(this.endDecimal) > 0)) {
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
