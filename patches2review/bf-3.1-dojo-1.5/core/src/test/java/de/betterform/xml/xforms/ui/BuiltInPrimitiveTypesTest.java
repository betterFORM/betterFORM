/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.xforms.ui;

import de.betterform.xml.events.DOMEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.Document;

public class BuiltInPrimitiveTypesTest extends BetterFormTestCase {

    protected void setUp() throws Exception {
         super.setUp();
    }

    public void testSetValid() throws Exception{
        // dump(this.processor.getXForms());
        assertTrue(evaluateInDefaultContextAsString("/types/my_dateTime").equals(""));
        this.processor.dispatch("t-validate",DOMEventNames.ACTIVATE);

        assertEquals("1999-05-31T13:20:00-05:00", evaluateInDefaultContextAsString("/types/my_dateTime"));
        assertEquals("13:20:00-05:00", evaluateInDefaultContextAsString("/types/my_time"));
        assertEquals("1999-05-31", evaluateInDefaultContextAsString("/types/my_date"));
        assertEquals("1999-05", evaluateInDefaultContextAsString("/types/my_gYearMonth"));
        assertEquals("1999", evaluateInDefaultContextAsString("/types/my_gYear"));        
        assertEquals("-09-14", evaluateInDefaultContextAsString("/types/my_gMonthDay"));
        assertEquals("-15", evaluateInDefaultContextAsString("/types/my_gDay"));
        assertEquals("-11", evaluateInDefaultContextAsString("/types/my_gMonth"));
        assertEquals("Strings, strings, everywhere.", evaluateInDefaultContextAsString("/types/my_string"));
        assertEquals("true", evaluateInDefaultContextAsString("/types/my_boolean"));
        assertEquals("WEZvcm1zIFJ1bGVz", evaluateInDefaultContextAsString("/types/my_base64Binary"));
        assertEquals("DEADBEEF", evaluateInDefaultContextAsString("/types/my_hexBinary"));
        assertEquals("1e-6", evaluateInDefaultContextAsString("/types/my_float"));
        assertEquals("12678967.543233", evaluateInDefaultContextAsString("/types/my_decimal"));
        assertEquals("INF", evaluateInDefaultContextAsString("/types/my_double"));
        assertEquals("http://example.com/data/potato", evaluateInDefaultContextAsString("/types/my_anyURI"));
        assertEquals("my:myelement", evaluateInDefaultContextAsString("/types/my_QName"));
        Document events = ((XFormsProcessorImpl)this.processor).getContainer().getModel("event_model").getDefaultInstance().getInstanceDocument();
        // dump(events);

    }

    public void testSetInValid() throws Exception{
        // dump(this.processor.getXForms());

        assertTrue(evaluateInDefaultContextAsString("/types/my_dateTime").equals(""));
        this.processor.dispatch("t-invalidate",DOMEventNames.ACTIVATE);

        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_dateTime"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_dateTime"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_time"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_date"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_gYearMonth"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_gYear"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_gMonthDay"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_gDay"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_gMonth"));
        assertEquals("", evaluateInDefaultContextAsString("/types/my_string"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_boolean"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_base64Binary"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_hexBinary"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_float"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_decimal"));
        assertEquals("bogus Data", evaluateInDefaultContextAsString("/types/my_double"));
        assertEquals("% 6 7", evaluateInDefaultContextAsString("/types/my_anyURI"));
        assertEquals("Bogus Data", evaluateInDefaultContextAsString("/types/my_QName"));
        Document events = ((XFormsProcessorImpl)this.processor).getContainer().getModel("event_model").getDefaultInstance().getInstanceDocument();
        // dump(events);

    }


    protected String getTestCaseURI() {
        return "5.1.a.mod.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }
}
