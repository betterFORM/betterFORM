/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.generator;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xslt.TransformerService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;

/**
 * An XSLT-based UI generator implementation.
 *
 * todo: unit testing
 *
 * @author Ulrich Nicolas Liss&eactue;
 * @version $Id: XSLTGenerator.java 3499 2008-08-28 12:06:39Z joern $
 * todo: refactor to become a XFormsProcessor wrapper
 */
public class XSLTGenerator implements UIGenerator {

    private static Log LOGGER = LogFactory.getLog(XSLTGenerator.class);

    private Source source = null;
    private Result result = null;
    private HashMap parameters = null;
    private TransformerService transformerService = null;
    private URI stylesheetURI = null;
    private HashMap properties = null;

    /**
     * Creates a new XSLT-based UI generator.
     */
    public XSLTGenerator() {
    }

    // implementation of 'de.betterform.generator.UIGenerator'

    /**
     * Sets the generator input.
     *
     * @param input the generator output.
     * @throws RuntimeException if the specified input cannot be converted to a
     * JAXP Source object.
     */
    public void setInput(Object input) {
        this.source = createInputSource(input);
//        if(LOGGER.isDebugEnabled() && input instanceof Node){
//            LOGGER.debug("XForms DOM received >>>");
//            DOMUtil.prettyPrintDOM((Node) input);
//        }
    }

    /**
     * Sets the generator output.
     *
     * @param output the generator output.
     * @throws RuntimeException if the specified input cannot be converted to a
     * JAXP Result object.
     */
    public void setOutput(Object output) {
        this.result = createOutputResult(output);
    }

    /**
     * Returns a generator parameter.
     *
     * @param name the parameter name.
     */
    public Object getParameter(String name) {
        if (this.parameters == null) {
            return null;
        }

        return this.parameters.get(name);
    }

    /**
     * Sets a generator parameter.
     *
     * @param name the parameter name.
     * @param value the parameter value.
     */
    public void setParameter(String name, Object value) {
        if (this.parameters == null) {
            this.parameters = new HashMap();
        }

        this.parameters.put(name, value);
    }

    /**
     * Generates a client-specific representation of the XForms container
     * document by applying an XSLT transformation to BetterForm's internal DOM.
     *
     * @throws XFormsException if an error occurred during the generation process.
     */
    public void generate() throws XFormsException {
        try {
            // sanity checks
            if (this.transformerService == null) {
                throw new IllegalStateException("transformer service missing");
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("generate: using stylesheet at " + this.stylesheetURI);
            }
            Transformer transformer = this.transformerService.getTransformer(this.stylesheetURI);
            prepareTransformer(transformer);

            long start = 0l;
            if (LOGGER.isDebugEnabled()) {
                start = System.currentTimeMillis();
            }

            transformer.transform(this.source, this.result);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("generate: transformation needed " + (System.currentTimeMillis() - start) + " ms");
            }
        }
        catch (Exception e) {
            throw new XFormsException(e);
        }
    }

    // XSLT generator members

    /**
     * Returns the transformer service to be used for transformations.
     *
     * @return the transformer service to be used for transformations.
     */
    public TransformerService getTransformerService() {
        return this.transformerService;
    }

    /**
     * Sets the transformer service to be used for transformations.
     *
     * @param transformerService the transformer service to be used for transformations.
     */
    public void setTransformerService(TransformerService transformerService) {
        this.transformerService = transformerService;
    }

    /**
     * Returns the stylesheet URI.
     *
     * @return the stylesheet URI.
     */
    public URI getStylesheetURI() {
        return this.stylesheetURI;
    }

    /**
     * Sets the stylesheet URI.
     *
     * @param stylesheetURI the stylesheet URI.
     */
    public void setStylesheetURI(URI stylesheetURI) {
        this.stylesheetURI = stylesheetURI;
    }

    /**
     * Returns the specified XSLT output property.
     *
     * @param name the name of the output property.
     * @return the specified XSLT output property.
     */
    public String getOutputProperty(String name) {
        if (this.properties == null) {
            return null;
        }

        return (String) this.properties.get(name);
    }

    /**
     * Sets the specified XSLT output property.
     *
     * @param name the name of the output property.
     * @param value the value of the output property.
     */
    public void setOutputProperty(String name, String value) {
        if (this.properties == null) {
            this.properties = new HashMap();
        }

        this.properties.put(name, value);
    }

    // template methods for easy subclassing

    /**
     * Prepares the transformer for the transformation process.
     * <p/>
     * All output properties as well as all stylesheet parameters are set.
     *
     * @param transformer the transformer.
     * @return the prepared transformer.
     */
    protected Transformer prepareTransformer(Transformer transformer) {
        if (this.properties != null) {
            String name;
            String value;
            Iterator iterator = this.properties.keySet().iterator();
            while (iterator.hasNext()) {
                name = (String) iterator.next();
                value = (String) this.properties.get(name);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("prepare: setting output property " + name + "=" + value);
                }
                transformer.setOutputProperty(name, value);
            }
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("prepare: no output properties to be set");
            }
        }

        if (this.parameters != null) {
            String name;
            Object value;
            Iterator iterator = this.parameters.keySet().iterator();
            while (iterator.hasNext()) {
                name = (String) iterator.next();
                value = this.parameters.get(name);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("prepare: setting stylesheet parameter " + name + "=" + value);
                }
                transformer.setParameter(name, value);
            }
        }
        else {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("prepare: no stylesheet parameters to be set");
            }
        }

        return transformer;
    }

    /**
     * Creates a JAXP Source from the specified input object.
     * <p/>
     * Follwoing object models are supported:
     * <ul>
     * <li><code>org.w3c.dom.Node</code></li>
     * <li><code>org.xml.sax.InputSource</code></li>
     * <li><code>java.io.InputStream</code></li>
     * <li><code>java.io.Reader</code></li>
     * <li><code>java.io.File</code></li>
     * </ul>
     *
     * @param input the input object.
     * @return a JAXP Source.
     * @throws NullPointerException if the input object is <code>null</code>.
     * @throws IllegalArgumentException if the object model is not supported.
     */
    protected Source createInputSource(Object input) {
        if (input == null) {
            throw new NullPointerException("parameter 'input' must not be null");
        }
        if(input instanceof DOMSource){
            return (Source) input;
        }
        // DOM
        if (input instanceof Node) {
            return new DOMSource((Node) input);
        }

        // SAX
        if (input instanceof InputSource) {
            return new SAXSource((InputSource) input);
        }

        // Stream
        if (input instanceof InputStream) {
            return new StreamSource((InputStream) input);
        }
        if (input instanceof Reader) {
            return new StreamSource((Reader) input);
        }
        if (input instanceof File) {
            return new StreamSource((File) input);
        }

        throw new IllegalArgumentException(input.getClass().getName() + " not supported as input");
    }

    /**
     * Creates a JAXP Result from the specified input object.
     * <p/>
     * Follwoing object models are supported:
     * <ul>
     * <li><code>org.w3c.dom.Node</code></li>
     * <li><code>org.xml.sax.ContentHandler</code></li>
     * <li><code>java.io.OutputStream</code></li>
     * <li><code>java.io.Writer</code></li>
     * <li><code>java.io.File</code></li>
     * </ul>
     *
     * @param output the output object.
     * @return a JAXP Result.
     * @throws NullPointerException if the output object is <code>null</code>.
     * @throws IllegalArgumentException if the object model is not supported.
     */
    protected Result createOutputResult(Object output) {
        if (output == null) {
            throw new NullPointerException("parameter 'output' must not be null");
        }
        if(output instanceof DOMResult){
            return (Result) output;
        }
        // DOM
        if (output instanceof Node) {
            return new DOMResult((Node) output);
        }

        // SAX
        if (output instanceof ContentHandler) {
            return new SAXResult((ContentHandler) output);
        }

        // Stream
        if (output instanceof OutputStream) {
            return new StreamResult((OutputStream) output);
        }
        if (output instanceof Writer) {
            return new StreamResult((Writer) output);
        }
        if (output instanceof File) {
            return new StreamResult((File) output);
        }

        throw new IllegalArgumentException(output.getClass().getName() + " not supported as output");
    }
}
