/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.bind.Bind;
import de.betterform.xml.xforms.model.bind.Binding;
import de.betterform.xml.xforms.model.bind.BindingResolver;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

/**
 * Base class for all bound action implementations.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: AbstractBoundAction.java 3474 2008-08-15 22:29:43Z joern $
 */
public abstract class AbstractBoundAction extends AbstractAction implements Binding {
    private static final Log LOGGER = LogFactory.getLog(AbstractBoundAction.class);

    private String instanceId;
    protected List nodeset;
    protected List evalInscopeContext;

    /**
     * Creates a bound action implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public AbstractBoundAction(Element element, Model model) {
        super(element, model);
    }

    public void init() throws XFormsException {
        super.init();
//        updateXPathContext();
    }

    // implementation of 'de.betterform.xml.xforms.model.bind.Binding'
    public List getNodeset() {
        return this.nodeset;
    }

    public int getPosition() {
        return this.position;
    }

    //todo: remove redundany of this method - was originally in BindingElement
    public String evalAttributeValueTemplates(String inputExpr,Element element) throws XFormsException {
        String toReplace = inputExpr;
        int start;
        int end;
        String valueTemplate;
        String value="";
        StringBuffer substitutedString = new StringBuffer();
        boolean hasTokens = true;
        while (hasTokens) {
            start = toReplace.indexOf("{");
            end = toReplace.indexOf('}');
            if (start == -1 || end == -1) {
                hasTokens = false; //exit
                substitutedString.append(toReplace);
            }
            else {
                substitutedString.append(toReplace.substring(0, start));
                valueTemplate = toReplace.substring(start + 1, end);

                //distinguish normal valueTemplate versus context $var
                if(valueTemplate.startsWith("$")){
                    //read key from context
                    valueTemplate = valueTemplate.substring(1);
                    if (this.model.getContainer().getProcessor().getContext().containsKey(valueTemplate)) {
                        value = this.model.getContainer().getProcessor().getContext().get(valueTemplate).toString();
                    }
                }else{

                     value = XPathCache.getInstance().evaluateAsString(this.getNodeset(), getPosition(), valueTemplate, getPrefixMapping(), xpathFunctionContext);
                }
                if(value.equals("")){
                     LOGGER.warn("valueTemplate could not be evaluated. Replacing '" + valueTemplate + "' with empty string");
                }
                substitutedString.append(value);
                toReplace = toReplace.substring(end + 1);
            }
        }
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("inputExpr: " + inputExpr + " evaluated to: " + substitutedString);
        }
        return substitutedString.toString();
    }

    /**
     * Updates the childEvaluationContext
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          in case an XPathException happens during evaluation
     */
    protected void updateXPathContext() throws XFormsException {
        if (isBound()) {
        	this.evalInscopeContext = evalInScopeContext();
            final String relativeExpr = getBindingExpression();
            try {
                if (relativeExpr != null)
                    this.nodeset = XPathCache.getInstance().evaluate(this.evalInscopeContext, 1, relativeExpr, getPrefixMapping(), xpathFunctionContext);
                else
                    this.nodeset = this.evalInscopeContext;
            }
            catch (XFormsException e) {
                throw new XFormsException(e);
            }
        }
    }

//    public int getPosition() {
//        return position;
//    }

/* COMMENTED OUT FOR NOW AS THE SPEC SAYS THAT THE IN SCOPE EVAL CONTEXT IS ALWAYS USED 
    protected boolean evalCondition(String ifCondition) throws XFormsException {

        List resultNodeset = evalInScopeContext();
        final String relativeExpr = getBindingExpression();
        try {
            if (relativeExpr != null)
                nodeset = XPathCache.getInstance().evaluate(resultNodeset, position, relativeExpr, prefixMapping, xpathFunctionContext);
            else
                nodeset = resultNodeset;

            String result = XPathCache.getInstance().evaluateAsString(nodeset, position, ifCondition, prefixMapping, xpathFunctionContext);
            if (result.equalsIgnoreCase("true")) {
                return true;
            } else {
                return false;
            }
        }
        catch (XFormsException e) {
            throw new XFormsException(e);
        }

    }
*/


    /**
     * Returns the binding expression.
     *
     * @return the binding expression.
     */
    public String getBindingExpression() {
        if (hasModelBinding()) {
            return getModelBinding().getBindingExpression();
        }
        return getXFormsAttribute(REF_ATTRIBUTE);
    }

    /**
     * Returns the id of the binding element.
     *
     * @return the id of the binding element.
     */
    public String getBindingId() {
        String bindAttribute = getXFormsAttribute(BIND_ATTRIBUTE);
        if (bindAttribute != null) {
            return bindAttribute;
        }

        return getId();
    }

    /**
     * Checks wether this element has a model binding.
     * <p/>
     * This element has a model binding if it has a <code>bind</code>
     * attribute.
     *
     * @return <code>true</code> if this element has a model binding, otherwise
     *         <code>false</code>.
     */
    protected boolean hasModelBinding() {
        return getXFormsAttribute(BIND_ATTRIBUTE) != null;
    }

    /**
     * Returns the model binding of this element.
     *
     * @return the model binding of this element.
     */
    protected Bind getModelBinding() {
        String bindAttribute = getXFormsAttribute(BIND_ATTRIBUTE);
        if (bindAttribute != null) {
            return (Bind) this.container.lookup(bindAttribute);
        }

        return null;
    }


    /**
     * Returns the enclosing element.
     *
     * @return the enclosing element.
     */
    public Binding getEnclosingBinding() {
        return getEnclosingBinding(this, true);
    }

    /**
     * Returns the location path.
     *
     * @return the location path.
     */
    public String getLocationPath() {
        return BindingResolver.getExpressionPath(this, this.repeatItemId);
    }

    /**
     * Returns the model id of the binding element.
     *
     * @return the model id of the binding element.
     */
    public String getModelId() {
        return getModel().getId();
    }

    public boolean isBound() {
        if (getBindingExpression() != null || getContextExpression() != null)
            return true;
        else
            return false;
    }

    // convenience

    /**
     * Returns the instance id of the binding element.
     *
     * @return the instance id of the binding element.
     */
    public String getInstanceId() throws XFormsException {
        if (this.instanceId == null) {
            this.instanceId = this.model.computeInstanceId(getLocationPath());
        }

        return this.instanceId;
    }

    /**
     * Sets the value of the bound node to the specified value. Returns true if the value could be set, false otherwise
     *
     * @param value
     * @return Returns true if the value could be set, false otherwise
     * @throws XFormsException
     */
    public boolean setNodeValue(String value) throws XFormsException {
        Node node = de.betterform.xml.xpath.impl.saxon.XPathUtil.getAsNode(getNodeset(), getPosition());
        if (node == null) {
            return false;
        }

        this.model.getInstance(getInstanceId()).setNodeValue(node, value);
        return true;
    }
}

// end of class
