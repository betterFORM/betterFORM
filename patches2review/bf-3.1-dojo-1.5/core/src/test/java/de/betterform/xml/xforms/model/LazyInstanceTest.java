/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
package de.betterform.xml.xforms.model;


import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.events.EventTarget;

// end of class

/**
 * Test cases for the instance implementation.
 *
 * @author Joern Turner
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: InstanceTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class LazyInstanceTest extends BetterFormTestCase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    private Instance instance;
    private EventTarget eventTarget;
    private TestEventListener nodeInsertedListener;
    private TestEventListener nodeDeletedListener;

    /**
     * Tests instance initialization.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInit() throws Exception {
        assertNotNull(this.instance);
        dump(this.instance.getInstanceDocument());
//        assertEquals("instance", this.instance.getId());
        assertNotNull(this.instance.getInstanceDocument());
//        assertEquals("data", this.instance.getInstanceDocument().getDocumentElement().getNodeName());
    }



    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();

        this.instance = getDefaultModel().getDefaultInstance();
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
        return "LazyInstanceTest.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}