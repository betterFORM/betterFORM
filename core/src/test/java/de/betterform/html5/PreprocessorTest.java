/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.html5;

import de.betterform.thirdparty.DOMBuilder;
import de.betterform.xml.dom.DOMUtil;
import junit.framework.TestCase;
import org.apache.commons.io.IOUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.io.StringWriter;

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
    public void testParseHtml5() throws Exception {
        //reading file as string
        InputStream input = getClass().getResourceAsStream("htmlparsetest.html");
        StringWriter writer = new StringWriter();
        IOUtils.copy(input,writer);
        String inputString = writer.toString();

        assert(inputString.length()!=0);


        Document doc = Jsoup.parse(inputString);
        org.w3c.dom.Document domDoc = DOMBuilder.jsoup2DOM(doc);
        DOMUtil.prettyPrintDOM(domDoc);
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
