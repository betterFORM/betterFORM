/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.events;

import junit.framework.TestCase;
import de.betterform.xml.events.impl.XercesXMLEvent;

import java.util.HashMap;
import java.util.Collection;

/**
 * Unit test for XMLEvent implementations.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEventTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class XMLEventTest extends TestCase {

    private XMLEvent xmlEvent;

    /**
     * Tests plain Event init.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitEvent() throws Exception {
        this.xmlEvent.initEvent("my-event", true, true);

        assertEquals("my-event", this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());

        assertNull(this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
        assertNull(this.xmlEvent.getContextInfo(null));
        assertNull(this.xmlEvent.getContextInfo(""));
        assertNull(this.xmlEvent.getContextInfo("unknown"));

        assertNull(this.xmlEvent.getPropertyNames());
    }

    /**
     * Tests plain XMLEvent init.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXMLEvent() throws Exception {
        this.xmlEvent.initXMLEvent("my-event", true, true, null);

        assertEquals("my-event", this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());

        assertNull(this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
        assertNull(this.xmlEvent.getContextInfo(null));
        assertNull(this.xmlEvent.getContextInfo(""));
        assertNull(this.xmlEvent.getContextInfo("unknown"));

        assertEquals(1, this.xmlEvent.getPropertyNames().size());
        assertTrue(this.xmlEvent.getPropertyNames().contains("defaultinfo"));
    }

    /**
     * Tests XMLEvent init with a context object.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXMLEventWithContextObject() throws Exception {
        this.xmlEvent.initXMLEvent("my-event", true, true, "foo");

        assertEquals("my-event", this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());

        assertEquals("foo", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
        assertNull(this.xmlEvent.getContextInfo(null));
        assertEquals("foo", this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
        assertNull(this.xmlEvent.getContextInfo("unknown"));

        assertNotNull(this.xmlEvent.getPropertyNames());
        assertEquals(1, this.xmlEvent.getPropertyNames().size());
        assertEquals(XMLEvent.DIRTY_DEFAULT_INFO, this.xmlEvent.getPropertyNames().iterator().next());
    }

    /**
     * Tests XMLEvent init with a context map.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testInitXMLEventWithContextMap() throws Exception {
        HashMap context = new HashMap();
        context.put("foo", "bar");

        this.xmlEvent.initXMLEvent("my-event", true, true, context);

        assertEquals("my-event", this.xmlEvent.getType());
        assertEquals(true, this.xmlEvent.getBubbles());
        assertEquals(true, this.xmlEvent.getCancelable());

        assertNull(this.xmlEvent.getContextInfo(XMLEvent.DIRTY_DEFAULT_INFO));
        assertNull(this.xmlEvent.getContextInfo(null));
        assertEquals("bar", this.xmlEvent.getContextInfo("foo"));
        assertNull(this.xmlEvent.getContextInfo(""));
        assertNull(this.xmlEvent.getContextInfo("unknown"));

        assertNotNull(this.xmlEvent.getPropertyNames());
        assertEquals(1, this.xmlEvent.getPropertyNames().size());
        assertEquals("foo", this.xmlEvent.getPropertyNames().iterator().next());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xmlEvent = new XercesXMLEvent();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xmlEvent = null;
    }

}
