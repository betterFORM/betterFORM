package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.Item;
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
        SubnetUtils subnetUtils = new SubnetUtils(subnetID, subnetMask);
        return  new StringValue(subnetUtils.getInfo().getLowAddress() + " - " + subnetUtils.getInfo().getHighAddress());
    }
}


/*
IPAddress ip = new IPAddress(new byte[] { 192, 168, 0, 1 });
int bits = 25;

uint mask = ~(uint.MaxValue >> bits);

// Convert the IP address to bytes.
byte[] ipBytes = ip.GetAddressBytes();

// BitConverter gives bytes in opposite order to GetAddressBytes().
byte[] maskBytes = BitConverter.GetBytes(mask).Reverse().ToArray();

byte[] startIPBytes = new byte[ipBytes.Length];
byte[] endIPBytes = new byte[ipBytes.Length];

// Calculate the bytes of the start and end IP addresses.
for (int i = 0; i < ipBytes.Length; i++)
{
    startIPBytes[i] = (byte)(ipBytes[i] & maskBytes[i]);
    endIPBytes[i] = (byte)(ipBytes[i] | ~maskBytes[i]);
}

// Convert the bytes to IP addresses.
IPAddress startIP = new IPAddress(startIPBytes);
IPAddress endIP = new IPAddress(endIPBytes);
*/