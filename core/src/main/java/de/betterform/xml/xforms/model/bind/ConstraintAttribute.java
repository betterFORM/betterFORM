package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.xforms.XFormsElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

/**
 *
 */
public class ConstraintAttribute implements Constraint {
    private static final Log LOGGER = LogFactory.getLog(ConstraintAttribute.class);

    private boolean valid=true;
    private Element element;

    public ConstraintAttribute(Element element){
        this.element=element;
    }

    protected Log getLogger() {
        return LOGGER;
    }

    public void setInvalid(){
        this.valid=false;
    }

    public boolean isValid(){
        return this.valid;
    }

    public String getXPathExpr(){
        return XFormsElement.getXFormsAttribute(this.element,"constraint");
    }

    public String getAlert(){
       //not supported yet
       return null;
    }
}

