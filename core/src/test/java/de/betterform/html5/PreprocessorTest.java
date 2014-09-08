/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.html5;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import de.betterform.xml.xslt.impl.FileResourceResolver;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.io.StringWriter;
import java.net.URI;

/**
 * @author joern turner
 * @version $Id: XMLBaseResolverTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class PreprocessorTest extends TestCase {
//    static {
//        BasicConfigurator.configure();
//    }

    /**
     * __UNDOCUMENTED__
     *
     * @throws Exception __UNDOCUMENTED__
     */
    public void testhtml2XForms() throws Exception {
        //reading file as string
        InputStream input = getClass().getResourceAsStream("htmlparsetest.html");
        StringWriter writer = new StringWriter();
        IOUtils.copy(input,writer);
        String inputString = writer.toString();

        assert(inputString.length()!=0);

        CachingTransformerService transformerService = new CachingTransformerService();
        transformerService.setNoCache(true);
        transformerService.addResourceResolver(new FileResourceResolver());
        URI uri = new URI("file://" + getClass().getResource("html2xforms.xsl").getPath());
        transformerService.getTransformer(uri);
        Node result = Preprocessor.html2Xforms(inputString, transformerService);
        assertNotNull(result);
        DOMUtil.prettyPrintDOM(result);
    }



    /**
     *
     */
    protected void setUp() throws Exception {
    }

    /**
     * __UNDOCUMENTED__
     */
    protected void tearDown() {
    }


}
