/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.connector;

import de.betterform.xml.xforms.exception.XFormsException;

/**
 * A URI resolver performs link traversal as postulated for linking attributes.
 * <p/>
 * Implementors of this interface are expected to return a DOM node as the
 * link traversal result. Other object models may be supported in the future.
 *
 * @author Joern Turner
 * @version $Id: URIResolver.java 2090 2006-03-16 09:37:00Z joernt $
 */
public interface URIResolver extends Connector {
    /**
     * Performs link traversal of the connector URI and returns the result
     * as a DOM node.
     *
     * @return a DOM node representing the link traversal result.
     * @throws XFormsException if any error occurred during link traversal.
     */
    Object resolve() throws XFormsException;
}

//end of interface

