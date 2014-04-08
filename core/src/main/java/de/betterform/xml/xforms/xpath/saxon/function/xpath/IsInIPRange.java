package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Item;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.util.SubnetUtils;

/**
 * Created with IntelliJ IDEA.
 * User: zwobit
 * Date: 28.09.12
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
public class IsInIPRange extends XFormsFunction {
    private static final Log LOGGER = LogFactory.getLog(IsInIPRange.class);

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    public Item evaluateItem(XPathContext xpathContext) throws XPathException {
        if (argument.length !=  3) {
            throw new XPathException("There must be 3 arguments (subnetid, subnetmask, ipaddress) for this function");
        }

        final Expression subnetIDExpression = argument[0];
        final String subnetID = subnetIDExpression.evaluateAsString(xpathContext).toString();

        final Expression subnetMaskExpression = argument[1];
        final String subnetMask = subnetMaskExpression.evaluateAsString(xpathContext).toString();

        final Expression ipAddressExpression = argument[2];
        final String ipAddress = ipAddressExpression.evaluateAsString(xpathContext).toString();

        if ("".equals(subnetID.trim()) || "".equals(subnetMask.trim()) || "".equals(ipAddress.trim())) {
            return BooleanValue.FALSE;
        }
        SubnetUtils subnetUtils = new SubnetUtils(subnetID, subnetMask);
        try {
        if (subnetUtils.getInfo().isInRange(ipAddress)) {
            return BooleanValue.TRUE;
        }
        } catch (IllegalArgumentException iae) {
            LOGGER.debug("IsInIPRange Exception:", iae);
        }
        return BooleanValue.FALSE;
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