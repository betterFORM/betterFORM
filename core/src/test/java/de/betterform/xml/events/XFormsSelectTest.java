/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.events;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.action.EventCountListener;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.events.EventTarget;


/**

 *
 * @author Joern Turner
 * @author Lars Windauer
 *

 */
public class XFormsSelectTest extends BetterFormTestCase {
    private TestEventListener messageListener;
    private EventCountListener valueChangedCountListener;

    protected String getTestCaseURI() {
        return "XFormsSelectTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        super.setUp();
        this.valueChangedCountListener = new EventCountListener(XFormsEventNames.VALUE_CHANGED);
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedCountListener, true);

    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        EventTarget eventTarget = (EventTarget) this.processor.getXForms();
        eventTarget.removeEventListener(BetterFormEventNames.RENDER_MESSAGE, this.messageListener, true);
        eventTarget = null;
        this.messageListener = null;
    }

    /**
     * Tests a modal message.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelect1ValueChangeListener() throws Exception {
        this.processor.dispatch("t-changeValue", DOMEventNames.ACTIVATE);
        assertEquals("xf:setvalue must be dispatched only once!", 1, this.valueChangedCountListener.getCount());

    }


}
