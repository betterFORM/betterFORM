/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.ui;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import org.w3c.dom.Element;

import java.text.ParseException;

/**
 * Implementation of <b>8.1.2 The input Element</b>, <b>8.1.3 The secret
 * Element</b>, and <b>8.1.4 The textarea Element</b>.
 *
 * todo: extract Secret class to support binding restrictions
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Text.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class Text extends AbstractFormControl {
    private static final Log LOGGER = LogFactory.getLog(Text.class);

    /**
     * Creates a new text element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Text(Element element, Model model) {
        super(element, model);
    }

    // form control methods

    /**
     * Performs element init.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if any error occurred during init.
     */
    @Override
    public void init() throws XFormsException {
        super.init();
        //todo - extract as seperate classes
        String datatype = getDatatype();
        if(datatype != null && datatype.contains(":")){
            datatype = datatype.substring(datatype.indexOf(":")+1,datatype.length());
        }
        if(this.element.getLocalName().equals("secret")){
            // TODO: check for datatypes derived from base64- or hexBinary (XF1.1 8.3.1)
            if("base64Binary".equals(datatype) || "hexBinary".equals(datatype) ){
                throw new XFormsException("This control is bound to an incompatible datatype: " + this.getDatatype());
            }
        }else if(this.element.getLocalName().equals("textarea") && !this.model.getValidator().isKnown(datatype)){
            throw new XFormsException("The type '" + datatype + "' is not supported");
        }


    }

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
        try {
            value = delocaliseValue(value);
        } catch (ParseException e) {
            //throw new XFormsException(e);
            LOGGER.error("Could not delocalise value '" + value + "'");
        }

        if (isBound()) {
            setNodeValue(value.replaceAll("(\\r\\n)|(\\r)", "\n"));
            
            dispatchValueChangeSequence();
        }
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
