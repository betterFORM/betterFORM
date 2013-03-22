package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.util.SubnetUtils;

/**
 * Created with IntelliJ IDEA.
 * User: zwobit
 * Date: 13.12.12
 * Time: 14:33
 * To change this template use File | Settings | File Templates.
 */
public class GetSubnetID extends XFormsFunction {
    private static final Log LOGGER = LogFactory.getLog(GetSubnetID.class);

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
        if (argument.length !=  2) {
            throw new XPathException("There must be 2 arguments (ip address, subnetmask) for this function");
        }

        final Expression subnetIDExpression = argument[0];
        final String subnetID = subnetIDExpression.evaluateAsString(xpathContext).toString();

        final Expression subnetMaskExpression = argument[1];
        final String subnetMask = subnetMaskExpression.evaluateAsString(xpathContext).toString();
        if ("".equals(subnetID.trim()) || "".equals(subnetMask.trim())) {
            return new StringValue("unknown");
        }
        try {
            SubnetUtils subnetUtils = new SubnetUtils(subnetID, subnetMask);
            return  new StringValue(subnetUtils.getInfo().getLowAddress());
        } catch (IllegalArgumentException iae) {
            LOGGER.debug("GetSubnetID Exception:", iae);
        }

        return new StringValue("unknown");
    }
}
