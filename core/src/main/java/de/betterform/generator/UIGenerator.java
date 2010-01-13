/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.generator;

import de.betterform.xml.xforms.exception.XFormsException;

/**
 * Interface for UI Generation.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: UIGenerator.java 3083 2008-01-21 11:29:21Z joern $
 */
public interface UIGenerator {

    /**
     * Sets the generator input.
     * <p/>
     * Usually this is BetterForm's internal representation of the XForms container document.
     *
     * @param input the generator output.
     */
    void setInput(Object input);

    /**
     * Sets the generator output.
     * <p/>
     * This might be a stream or any other appropriate output representation.
     *
     * @param output the generator output.
     * @throws RuntimeException if this generator can't handle the specified output.
     */
    void setOutput(Object output);

    /**
     * Returns a generator parameter.
     *
     * @param name the parameter name.
     */
    Object getParameter(String name);

    /**
     * Sets a generator parameter.
     *
     * @param name the parameter name.
     * @param value the parameter value.
     */
    void setParameter(String name, Object value);

    /**
     * Generates a client-specific representation of the XForms container document.
     *
     * @throws XFormsException if an error occurred during the generation process.
     */
    void generate() throws XFormsException;
}
