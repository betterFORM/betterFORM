/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.ui;




import net.sf.saxon.trans.XPathException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.AttributeOrValueChild;
import de.betterform.xml.xforms.ui.state.OutputElementState;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.ui.state.UIElementStateUtil;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import de.betterform.xml.events.XFormsEventNames;
import org.w3c.dom.Element;

/**
 * Implementation of <b>8.1.5 The output Element</b>.
 * <p/>
 * Note: In case this control is not bound but has a <code>value</code>
 * attribute, re-evaluation of this attribute occurs during
 * <code>xforms-refresh</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @author Nick Van den Bleeken
 * @author Lars Windauer
 * @version $Id: Output.java 3253 2008-07-08 09:26:40Z lasse $
 */
//TODO: AttributeOrValueChild -> DispatchAction
public class Output extends AbstractFormControl {
    private static final Log LOGGER = LogFactory.getLog(Output.class);
    //private Mediatype mediatypeHelper;
    private String mediatype = null;
    private AttributeOrValueChild mediatypeHelper = null;

    /**
     * Creates a new output element handler.
     *
     * @param element the host document element.
     * @param model the context model.
     */
    public Output(Element element, Model model) {
        super(element, model);
    }

    /**
     * Performs element init.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if any error occurred during init.
     */
    @Override
    public void init() throws XFormsException {
        super.init();
        this.mediatypeHelper= new AttributeOrValueChild(this.element, this.model, XFormsConstants.MEDIATYPE_ATTRIBUTE);
        this.mediatypeHelper.init();

        if (this.mediatypeHelper != null) {
             this.mediatype = this.mediatypeHelper.getValue();
        }
        
        if(this.mediatype != null && !(Mediatype.isMediaTypeValid(this.mediatype))){
            this.model.getContainer().dispatch(this.target, XFormsEventNames.OUTPUT_ERROR, null);
        }

        if (isBound()) {
           Instance instance = this.model.getInstance(getInstanceId());
           ModelItem item = instance.getModelItem(getInstanceNode());

           if (this.mediatype != null && Mediatype.isMediaTypeValid(this.mediatype) && item!= null && !item.isReadonly()) {
               item.setMediatype(this.mediatype);
            }
        }

        if (this.mediatype != null) {
            getUIElementState().setProperty( XFormsConstants.MEDIATYPE_ATTRIBUTE, this.mediatype);
        }
        
    }

    // form control methods

    /**
     * Sets the value of this form control.
     * <p/>
     * If this method is called a warning is issued since the value of an
     * <code>output</code> control cannot be set.
     *
     * @param value the value to be set.
     */
    public void setValue(String value) {
        getLogger().warn(this + " set value: the value of an output control cannot be set");
    }

    // output specific methods
    
    /**
     * Checks wether this output has a <code>value</code> attribute.
     *
     * @return <code>true</code> if this output has a <code>value</code>
     *         attribute, otherwise <code>false</code>.
     */
    public boolean hasValueAttribute() {
        return getXFormsAttribute(VALUE_ATTRIBUTE) != null;
    }

    /**
     * Returns the contents of the <code>value</code> attribute if any.
     *
     * @return the contents of the <code>value</code> attribute or
     *         <code>null</code> if there is no such attribute.
     */
    public String getValueAttribute() {
        return getXFormsAttribute(VALUE_ATTRIBUTE);
    }

    /**
     * Evaluates the expression found in the <code>value</code> attribute.
     *
     * @return the evaluated result of the expression found in the
     *         <code>value</code> attribute.
     * @throws XFormsException if the expression could not be evaluated.
     */
    public Object computeValueAttribute() throws XFormsException {
        
        try
		{
            if (isBound()) {
                return XPathUtil.getAsString(getNodeset(), getPosition());
            }
            else {
                return XPathCache.getInstance().evaluateAsString(getNodeset(), getPosition(), getValueAttribute(), getPrefixMapping(), xpathFunctionContext);
            }
		}
		catch (XFormsException e)
		{
			if (e.getCause() instanceof XPathException && "XPDY0002".equals(((XPathException)e.getCause()).getErrorCodeLocalPart()))
			{
				return null;
			}
			if (e instanceof XFormsComputeException) {
				throw e;
			}
			throw new XFormsComputeException(e.getMessage(), e, getTarget(), this);
		}        
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
        if (isBound()) {
            return super.createElementState();
        }
        if (hasValueAttribute()) {
            return new OutputElementState();
        }
        return null;
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
