/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events;

import de.betterform.xml.events.impl.DefaultXMLEventInitializer;
import de.betterform.xml.events.impl.DefaultXMLEventService;
import de.betterform.xml.events.impl.XercesXMLEventFactory;
import junit.framework.TestCase;

/**
 * Unit test for XMLEventService implementations.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XMLEventServiceTest.java 2797 2007-08-10 12:45:24Z joern $
 */
public class XMLEventServiceTest extends TestCase {

    private XMLEventService xmlEventService;
    private MockEventTarget eventTarget;
    private MockEventHandler eventListener;
    private MockEventHandler defaultAction;

    /**
     * Tests event dispatching.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testDispatch() throws Exception {
        this.eventTarget.addEventListener("some-arbitrary-event", this.eventListener, false);
        this.xmlEventService.dispatch(this.eventTarget, "some-arbitrary-event", false, true, null);

        assertEquals("some-arbitrary-event", this.eventListener.getType());
    }

    /**
     * Tests event dispatching.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testDispatchWithDefaultAction() throws Exception {
        this.eventTarget.addEventListener("some-arbitrary-event", this.eventListener, false);
        this.xmlEventService.registerDefaultAction(this.eventTarget, "some-arbitrary-event", this.defaultAction);
        this.xmlEventService.dispatch(this.eventTarget, "some-arbitrary-event", false, true, null);

        assertEquals("some-arbitrary-event", this.eventListener.getType());
        assertEquals("some-arbitrary-event", this.defaultAction.getType());
    }

    /**
     * Tests event dispatching.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testDispatchWithDefaultActionCancelled() throws Exception {
        this.eventListener.preventDefault();
        this.eventTarget.addEventListener("some-arbitrary-event", this.eventListener, false);
        this.xmlEventService.registerDefaultAction(this.eventTarget, "some-arbitrary-event", this.defaultAction);
        this.xmlEventService.dispatch(this.eventTarget, "some-arbitrary-event", false, true, null);

        assertEquals("some-arbitrary-event", this.eventListener.getType());
        assertEquals(null, this.defaultAction.getType());
    }

    /**
     * Tests event dispatching.
     *
     * @throws Exception in any error occurred during setup.
     */
    public void testDispatchWithDefaultActionUncancelable() throws Exception {
        this.eventListener.preventDefault();
        this.eventTarget.addEventListener("some-arbitrary-event", this.eventListener, false);
        this.xmlEventService.registerDefaultAction(this.eventTarget, "some-arbitrary-event", this.defaultAction);
        this.xmlEventService.dispatch(this.eventTarget, "some-arbitrary-event", false, false, null);

        assertEquals("some-arbitrary-event", this.eventListener.getType());
        assertEquals("some-arbitrary-event", this.defaultAction.getType());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        // todo: write event factory / initializer mock-up
        this.xmlEventService = new DefaultXMLEventService();
        this.xmlEventService.setXMLEventFactory(new XercesXMLEventFactory());
        this.xmlEventService.setXMLEventInitializer(new DefaultXMLEventInitializer());

        this.eventTarget = new MockEventTarget();
        this.eventListener = new MockEventHandler();
        this.defaultAction = new MockEventHandler();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xmlEventService = null;
        this.eventTarget = null;
        this.eventListener = null;
        this.defaultAction = null;
    }

}
