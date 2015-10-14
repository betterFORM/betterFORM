/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsComputeException;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.StringValue;
import org.apache.commons.codec.BinaryEncoder;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Implementation of 7.8.4 The hmac() Function
 * 
 * This function accepts a string for a key or shared secret, a string of data,
 * a string indicating a cryptographic hashing algorithm, and an optional string
 * indicating an encoding method. The key and data strings are serialized as
 * UTF-8, and they are subjected to the HMAC algorithm defined in [HMAC] and
 * parameterized by the the hash algorithm indicated by the third parameter. The
 * result is encoded with the method indicated by the fourth parameter, and the
 * result is returned by the function.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Hmac extends XFormsFunction {
    private static final String kBASE64 = "base64";
    private static final HashSet<String> kSUPPORTED_ALG = new HashSet<String>(Arrays.asList(new String[] { "MD5", "SHA-1", "SHA-256", "SHA-384", "SHA-512" }));
    private static final HashSet<String> kSUPPORTED_ENCODINGS = new HashSet<String>(Arrays.asList(new String[]{ kBASE64, "hex" }));

    private static final long serialVersionUID = -8331394395194343808L;

    /**
     * Pre-evaluate a function at compile time. Functions that do not allow
     * pre-evaluation, or that need access to context information, can override this method.
     * @param visitor an expression visitor
     * @return the result of the early evaluation, or the original expression, or potentially
     * a simplified expression
     */
	@Override
    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
		return this;
    }

    /**
     * Evaluate in a general context
     */
	@Override
    public Item evaluateItem(final XPathContext xpathContext) throws XPathException {
		final String key = argument[0].evaluateAsString(
			xpathContext).toString();
		final String data = argument[1].evaluateAsString(
			xpathContext).toString();
		final String originalAlgorithmString = argument[2].evaluateAsString(
			xpathContext).toString();
		final String encoding = argument != null && argument.length >= 4 ? argument[3].evaluateAsString(
			xpathContext).toString() : kBASE64;

		return hmac(xpathContext, key, data, originalAlgorithmString, encoding);
	}

	public Sequence call(final XPathContext context,
						 final Sequence[] arguments) throws XPathException {
		final String key = arguments[0].head().getStringValue();
		final String data = arguments[1].head().getStringValue();
		final String originalAlgorithmString = arguments[2].head().getStringValue();
		final String encoding = arguments.length >= 4 ? arguments[3].head().getStringValue() : kBASE64;

		return hmac(context, key, data, originalAlgorithmString, encoding);
	}

	private StringValue hmac(final XPathContext context, final String key, final String data, final String originalAlgorithmString, final String encoding) throws XPathException {

		if (!kSUPPORTED_ALG.contains(originalAlgorithmString)) {
			final XPathFunctionContext functionContext = getFunctionContext(context);
			final XFormsElement xformsElement = functionContext.getXFormsElement();
			throw new XPathException(new XFormsComputeException("Unsupported algorithm '" + originalAlgorithmString + "'", xformsElement.getTarget(), this));
		}

		if (!kSUPPORTED_ENCODINGS.contains(encoding)) {
			final XPathFunctionContext functionContext = getFunctionContext(context);
			final XFormsElement xformsElement = functionContext.getXFormsElement();
			throw new XPathException(new XFormsComputeException("Unsupported encoding '" + encoding + "'", xformsElement.getTarget(), this));
		}

		final String algorithm = "Hmac" + originalAlgorithmString.replaceAll("-", "");

		try {
			// Generate a key for the HMAC-MD5 keyed-hashing algorithm; see RFC 2104
			// In practice, you would save this key.
			final SecretKey secretKey = new SecretKeySpec(key.getBytes("utf-8"), algorithm);

			// Create a MAC object using HMAC-MD5 and initialize with kesaxoniay
			final Mac mac = Mac.getInstance(secretKey.getAlgorithm());
			mac.init(secretKey);
			mac.update(data.getBytes("utf-8"));

			final byte[] digest = mac.doFinal();

			final BinaryEncoder encoder;
			if ("base64".equals(encoding)) {
				encoder = new Base64(digest.length, "".getBytes(), false);
			} else {
				encoder = new Hex();
			}

			return new StringValue(new String(encoder.encode(digest), "ASCII"));

		} catch (final NoSuchAlgorithmException e) {
			throw new XPathException(e);
		} catch (final UnsupportedEncodingException e) {
			throw new XPathException(e);
		} catch (final EncoderException e) {
			final XPathFunctionContext functionContext = getFunctionContext(context);
			final XFormsElement xformsElement = functionContext.getXFormsElement();
			throw new XPathException(new XFormsComputeException("Encoder exception.", e, xformsElement.getTarget(), this));
		} catch (final InvalidKeyException e) {
			throw new XPathException(e);
		}
    }
}
