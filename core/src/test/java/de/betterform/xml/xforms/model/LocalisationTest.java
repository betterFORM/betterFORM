/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
package de.betterform.xml.xforms.model;

import junit.framework.TestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.config.Config;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.BetterFormXPathContext;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.DOMEventNames;

import java.text.ParseException;


/**
 * Test cases for the instance implementation.
 *
 * @author Joern Turner
 * @version $Id: LocalisationTest.java 3518 2008-09-08 09:59:53Z lars $
 */
public class LocalisationTest extends TestCase {
    private XFormsProcessorImpl xformsProcesssorImpl;
    private Instance inst;
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}


    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {
            BetterFormXPathContext context = xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI());
            String s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='1']/*:data/text()");
            assertEquals("10.001,1111111", s);

            s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='2']/*:data/text()");
            assertEquals("1,127181727", s);

            s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='3']/*:data/text()");
            assertEquals("1,13799898", s);

            s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='4']/*:data/text()");
            assertEquals("06.01.2004", s);
        }
    }

    public void testUpdateNumber() throws Exception {
        //feed numerical value
        this.xformsProcesssorImpl.setControlValue("1", "20002,2222222");
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {
            assertEquals("20002.2222222", XPathCache.getInstance().evaluateAsSingleNode(inst.getRootContext(), "/data/item[3]/text()").getNodeValue());
        }
    }

    public void testUpdateDate() throws Exception {
        //feed date value
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {
            this.xformsProcesssorImpl.setControlValue("4", "07.11.2005");
            assertEquals("2005-11-07", XPathCache.getInstance().evaluateAsString(inst.getRootContext(), "/data/item[6]/text()"));
        }
    }


    public void testUpdateInvalidDate() throws Exception {
        //feed invalid number value
        try {
            this.xformsProcesssorImpl.setControlValue("4", "x23.21");
        } catch (XFormsException e) {
            assertTrue(e.getCause() instanceof ParseException);
        }
    }


    public void testUpdateDateTime() throws Exception {
        //feed dateTime value
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {
            this.xformsProcesssorImpl.setControlValue("6", "16.01.2008 17:34:03 GMT+02:00");
            assertEquals("2008-01-16T17:34:03+02:00", XPathCache.getInstance().evaluateAsString(inst.getRootContext(), "/data/item[7]"));


            this.xformsProcesssorImpl.setControlValue("7", "11.11.2011 13:00:00");
            assertEquals("2011-11-11T13:00:00", XPathCache.getInstance().evaluateAsString(inst.getRootContext(), "/data/item[8]"));
        }
    }

    public void testSetLocale() throws Exception {
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {

                xformsProcesssorImpl.setLocale("de");
//                processor.dispatch("foo", "xforms-refresh");


                String s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='1']/*:data/text()");
                assertEquals("10.001,1111111", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='2']/*:data/text()");
                assertEquals("1,127181727", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='3']/*:data/text()");
                assertEquals("1,13799898", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='4']/*:data/text()");
                assertEquals("06.01.2004", s);

                xformsProcesssorImpl.setLocale("en");
//                processor.dispatch("foo", "xforms-refresh");

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='1']/*:data/text()");
                assertEquals("10,001.1111111", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='2']/*:data/text()");
                assertEquals("1.127181727", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='3']/*:data/text()");
                assertEquals("1.13799898", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='4']/*:data/text()");
                assertEquals("Jan 6, 2004", s);

            }

    }
    public void testDelocalizeFallback() throws Exception {
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {

                xformsProcesssorImpl.setLocale("de");

                String s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='1']/*:data/text()");
                assertEquals("10.001,1111111", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='2']/*:data/text()");
                assertEquals("1,127181727", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='3']/*:data/text()");
                assertEquals("1,13799898", s);

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='4']/*:data/text()");
                assertEquals("06.01.2004", s);

                xformsProcesssorImpl.setControlValue("4","2006-02-10");
                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='4']/*:data/text()");
                assertEquals("10.02.2006", s);

                // DOMUtil.prettyPrintDOM(xformsProcesssorImpl.getXForms().getDocumentElement());

                s = XPathCache.getInstance().evaluateAsString(xformsProcesssorImpl.getContainer().getHostContext(xformsProcesssorImpl.getBaseURI()), "//*[@id='4']/*:data/@*:schema-value");
                assertEquals("2006-02-10", s);


            }

    }

    public void testLocalizedInsert() throws Exception {
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N).equals("true")) {

                xformsProcesssorImpl.setLocale("de");
                xformsProcesssorImpl.dispatch("insert-item", DOMEventNames.ACTIVATE);               
                assertTrue(true);


            }

    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        xformsProcesssorImpl = new XFormsProcessorImpl();
        xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("LocalisationTest.xhtml"));
        xformsProcesssorImpl.setLocale("de");
        //System.out.println("SSSSSS" + getClass().getResource("Config.xml").getPath());
        xformsProcesssorImpl.setConfigPath(getClass().getResource("Config.xml").getPath());
        xformsProcesssorImpl.init();
        inst = xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance();

    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
    }

}
