package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;
import org.apache.commons.net.util.SubnetUtils;

/**
 * Created with IntelliJ IDEA.
 * User: zwobit
 * Date: 28.09.12
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public class CalcIPRange extends XFormsFunction {
    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    @Override
    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
        if (argument.length !=  2) {
            throw new XPathException("There must be 2 arguments (ip address, subnetmask) for this function");
        }

        final Expression subnetIDExpression = argument[0];
        final String subnetID = subnetIDExpression.evaluateAsString(xpathContext).toString();

        final Expression subnetMaskExpression = argument[1];
        final String subnetMask = subnetMaskExpression.evaluateAsString(xpathContext).toString();

        return ipRange(subnetID, subnetMask);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        final String subnetID = arguments[0].head().getStringValue();
        final String subnetMask = arguments[1].head().getStringValue();

        return ipRange(subnetID, subnetMask);
    }

    private StringValue ipRange(final String subnetID, final String subnetMask) {
        if ("".equals(subnetID.trim()) || "".equals(subnetMask.trim())) {
            return new StringValue("unknown");
        }

        final SubnetUtils subnetUtils = new SubnetUtils(subnetID, subnetMask);
        return  new StringValue(subnetUtils.getInfo().getLowAddress() + " - " + subnetUtils.getInfo().getHighAddress());
    }
}