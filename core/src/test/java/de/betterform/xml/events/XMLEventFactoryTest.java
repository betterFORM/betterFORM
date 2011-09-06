/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events;

import junit.framework.TestCase;
import de.betterform.xml.events.impl.XercesXMLEventFactory;
import org.w3c.dom.events.Event;

/**
 * Unit test for XMLEventFactory implementations.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEventFactoryTest.java 2797 2007-08-10 12:45:24Z joern $
 */
public class XMLEventFactoryTest extends TestCase {

    private XMLEventFactory xmlEventFactory;

    /**
     * Tests Event creation.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testCreateEvent() throws Exception {
        Event event = this.xmlEventFactory.createEvent("some-arbitrary-event");

        assertNotNull(event);
        assertEquals("some-arbitrary-event", event.getType());
        assertEquals(true, event instanceof XMLEvent);
    }

    /**
     * Tests Event creation.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testCreateXMLEvent() throws Exception {
        XMLEvent xmlEvent = this.xmlEventFactory.createXMLEvent("some-arbitrary-event");

        assertNotNull(xmlEvent);
        assertEquals("some-arbitrary-event", xmlEvent.getType());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xmlEventFactory = new XercesXMLEventFactory();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xmlEventFactory = null;
    }

}
