package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

/**
 * represents a single constraint of a bind
 */
public class ConstraintElement extends XFormsElement implements Constraint {
    private static final Log LOGGER = LogFactory.getLog(ConstraintElement.class);

    private boolean valid=true;

    public ConstraintElement(Element element, Model model){
        super(element, model);
    }

    @Override
    public void init() throws XFormsException {
        Initializer.initializeUIElements(this.element);
    }

    @Override
    public void dispose() throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Log getLogger() {
        return LOGGER;
    }

    public void setInvalid(){
        this.valid=false;
        this.element.setAttributeNS(null,"initial","true");
    }

    public boolean isValid(){
        return this.valid;
    }

    public String getXPathExpr(){
        return getBFAttribute("value");
    }

    public String getAlert(){
        Element e = (Element) DOMUtil.getFirstChildByTagNameNS(this.element, NamespaceConstants.XFORMS_NS, "alert");
        if(e != null){
            return DOMUtil.getElementValue(e);
        }
        return null;
    }
}

