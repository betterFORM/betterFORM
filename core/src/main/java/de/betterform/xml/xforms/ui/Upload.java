/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.model.constraints.Validator;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

/**
 * Implementation of <b>8.1.6 The upload Element</b>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: Upload.java 3489 2008-08-26 10:37:34Z joern $
 */
public class Upload extends AbstractFormControl {
    private static final Log LOGGER = LogFactory.getLog(Upload.class);

    public static final String DEFAULT_MEDIATYPE = "application/octet-stream";

    private Filename filenameHelper;
    private Mediatype mediatypeHelper;
    private static final String datatypeErrorMsg = "Datatype not supported by this control: ";

    /**
     * Creates a new upload element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Upload(Element element, Model model) {
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
        initializeUpload();
    }

    /**
     * Initializes this upload.
     *
     * @throws XFormsException if the datatype of the bound is not supported.
     */
    protected final void initializeUpload() throws XFormsException {
        if (hasBindingExpression()) {
            //datatype health check
            Validator validator = this.model.getValidator();
            String datatype = getDatatype();

            // convert binary data according to bound datatype
            if ( !(validator.isRestricted("base64Binary", datatype) || validator.isRestricted("hexBinary", datatype) || validator.isRestricted("anyURI", datatype)) ) {
                this.container.dispatch(this.target,XFormsEventNames.BINDING_EXCEPTION, datatypeErrorMsg + datatype);
                return;
            }

            Instance instance = this.model.getInstance(getInstanceId());
            ModelItem item = instance.getModelItem(getInstanceNode());
            if (this.filenameHelper != null && !item.isReadonly()) {
                item.setFilename(this.filenameHelper.getValue());
            }
            if (this.mediatypeHelper != null && !item.isReadonly()) {
                item.setMediatype(this.mediatypeHelper.getValue());
            }
        }
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
        LOGGER.warn("Update control cannot be set with this method.");
    }

    /**
     * Sets the value of this form control.
     * <p/>
     * The bound instance data is updated and the event sequence for this
     * control is executed. Event sequences are described in Chapter 4.6 of
     * XForms 1.0 Recommendation.
     *
     * @param data the raw data.
     * @param filename the filename of the uploaded data.
     * @param mediatype the mediatype of the uploaded data.
     */
    public void setValue(byte[] data, String filename, String mediatype) throws XFormsException {
        if (!hasBindingExpression()) {
            return;
        }

        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("uploading to file: " + filename + " - mediatype: " + mediatype);
        }

        String value;
        String name = filename;
        String type = mediatype;

        if (data != null && data.length > 0) {
            // get model item datatype
            Validator validator = this.model.getValidator();
            String datatype = getDatatype();

            // convert binary data according to bound datatype
            if (validator.isRestricted("base64Binary", datatype)) {
                value = new String(Base64.encodeBase64(data, true));
            }
            else if (validator.isRestricted("hexBinary", datatype)) {
                value = new String(Hex.encodeHex(data));
            }
            else if (validator.isRestricted("anyURI", datatype)) {
                value = new String(data);
            }
            else {
                this.container.dispatch(this.target,XFormsEventNames.BINDING_EXCEPTION, datatypeErrorMsg + datatype);
                return;
            }

            // check mediatype
            if (mediatype == null || mediatype.length() == 0) {
                type = DEFAULT_MEDIATYPE;
            }
        }
        else {
            value = "";
            name = "";
            type = "";
        }

        // update instance data
        Instance instance = this.model.getInstance(getInstanceId());
        setNodeValue(value);
        ModelItem item = getModelItem();
        if (!item.isReadonly()) {
            item.setFilename(name);
            item.setMediatype(type);
        }

        // update helper elements
        if (this.filenameHelper != null) {
            this.filenameHelper.setValue(name);
        }
        if (this.mediatypeHelper != null) {
            this.mediatypeHelper.setValue(type);
        }

        dispatchValueChangeSequence();
    }

    // upload specific methods

    /**
     * Returns the filename helper.
     *
     * @return the filename helper.
     */
    public Filename getFilename() {
        return this.filenameHelper;
    }

    /**
     * Sets the filename helper.
     *
     * @param filename helper.
     */
    public void setFilename(Filename filename) {
        this.filenameHelper = filename;
    }

    /**
     * Returns the mediatype helper.
     *
     * @return the mediatype helper.
     */
    public Mediatype getMediatype() {
        return this.mediatypeHelper;
    }

    /**
     * Sets the mediatype helper.
     *
     * @param mediatype helper.
     */
    public void setMediatype(Mediatype mediatype) {
        this.mediatypeHelper = mediatype;
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
