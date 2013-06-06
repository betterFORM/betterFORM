/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import de.betterform.xml.config.Config;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xslt.TransformerService;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import de.betterform.xml.xslt.impl.ClasspathResourceResolver;
import de.betterform.xml.xslt.impl.FileResourceResolver;
import de.betterform.xml.xslt.impl.HttpResourceResolver;

import java.io.File;
import java.net.URI;

/**
 * Test cases for xf:load action with show=embedded including cross model submissions
 *
 * @author Joern Turner
 * @author Lars Windauer
 * @version $Id: 
 */
public class LoadActionEmbedTestWithBfInclude extends LoadActionEmbedTest {
    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();
        Config.getInstance(getClass().getResource("include.xml").getPath());


        CachingTransformerService transformerService =  new CachingTransformerService();
        transformerService.addResourceResolver(new FileResourceResolver());
        transformerService.addResourceResolver(new ClasspathResourceResolver( getClass().getResource(".").getPath() ));
        transformerService.addResourceResolver(new HttpResourceResolver());

        URI includeTransformer = new File(getClass().getResource(".").getPath() + "include.xsl").toURI();
        transformerService.getTransformer(includeTransformer);

        this.processor.setContextParam(TransformerService.TRANSFORMER_SERVICE, transformerService);
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    protected String getTestCaseURI() {
        return "LoadActionEmbedTestWithBfInclude.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
