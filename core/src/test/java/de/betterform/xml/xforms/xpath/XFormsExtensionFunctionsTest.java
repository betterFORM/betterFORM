/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.xpath;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.ui.AbstractFormControl;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import net.sf.saxon.lib.ConversionRules;
import net.sf.saxon.value.DateTimeValue;
import net.sf.saxon.value.DateValue;
import org.w3c.dom.Node;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Test cases for XForms Extension Functions.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsExtensionFunctionsTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class XFormsExtensionFunctionsTest extends BetterFormTestCase {

    private static final int kLOCAL_TIME_OFFSET_IN_MINUTES = new GregorianCalendar().get(Calendar.ZONE_OFFSET) / 1000 / 60;

    /**
     * Tests the boolean-from-string() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testBooleanFromString() throws Exception {
        assertEquals(false, evaluateInDefaultContextAsBoolean("boolean-from-string('0')"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("boolean-from-string('1')"));
        assertEquals(false, evaluateInDefaultContextAsBoolean("boolean-from-string('false')"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("boolean-from-string('true')"));
        assertEquals(false, evaluateInDefaultContextAsBoolean("boolean-from-string('FALSE')"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("boolean-from-string('TRUE')"));
        assertEquals(false, evaluateInDefaultContextAsBoolean("boolean-from-string('xforms')"));
    }

    /**
     * Tests the if() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIf() throws Exception {
        assertEquals("then", evaluateInDefaultContextAsString("IF(true(), 'then', 'else')"));
        assertEquals("else", evaluateInDefaultContextAsString("IF(false(), 'then', 'else')"));
    }

    /**
     * Tests the choose() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testChoose() throws Exception {
        assertEquals("then", evaluateInDefaultContextAsString("choose(true(), 'then', 'else')"));
        assertEquals("else", evaluateInDefaultContextAsString("choose(false(), 'then', 'else')"));

        assertEquals(1.0, evaluateInDefaultContextAsDouble("choose(true(), 1.0, 'else')"));
        assertEquals("else", evaluateInDefaultContextAsString("choose(false(), 1.0, 'else')"));
    }

    /**
     * Tests the avg() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testAvg() throws Exception {
        assertEquals(2d, evaluateInDefaultContextAsDouble("avg(/data/number)"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("avg(/data/text)"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("avg(/non-existing)"));
    }

    /**
     * Tests the min() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMin() throws Exception {
        assertEquals(1d, evaluateInDefaultContextAsDouble("min(/data/number)"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("min(/data/text)"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("min(/non-existing)"));
    }

    /**
     * Tests the max() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMax() throws Exception {
        assertEquals(3d, evaluateInDefaultContextAsDouble("max(/data/number)"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("max(/data/text)"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("max(/non-existing)"));
    }

    /**
     * Tests the count-non-empty() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testCountNonEmpty() throws Exception {
        assertEquals(3d, evaluateInDefaultContextAsDouble("count-non-empty(/data/number)"));
        assertEquals(2d, evaluateInDefaultContextAsDouble("count-non-empty(/data/text)"));
        assertEquals(0d, evaluateInDefaultContextAsDouble("count-non-empty(/non-existing)"));
    }

    /**
     * Tests the index() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIndex() throws Exception {
        assertEquals(1d, evaluateInDefaultContextAsDouble("index('repeat')"));
        this.processor.setRepeatIndex("repeat", 3);
        assertEquals(3d, evaluateInDefaultContextAsDouble("index('repeat')"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("index('none-existing')"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("index('default-instance')"));
    }

    /**
     * Tests the property() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testProperty() throws Exception {
        assertEquals("1.1", evaluateInDefaultContextAsString("property('version')"));
        assertEquals("full", evaluateInDefaultContextAsString("property('conformance-level')"));
        assertEquals("", evaluateInDefaultContextAsString("property('foobar')"));

    }

    /**
     * Tests the local-date() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLocalData() throws Exception {
        //Throws exception if it fails
        new DateValue(evaluateInDefaultContextAsString("local-date()"));
    }


    /**
     * Tests the local-date() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testLocalDataTime() throws Exception {
        GregorianCalendar before = new GregorianCalendar();
        final DateTimeValue localdateTime = (DateTimeValue) DateTimeValue.makeDateTimeValue(evaluateInDefaultContextAsString("local-dateTime()"), new ConversionRules()).asAtomic();
        GregorianCalendar after = new GregorianCalendar();

        assertTrue(localdateTime.getCalendar().compareTo(before) >= 0);
        assertTrue(localdateTime.getCalendar().compareTo(after) <= 0);
    }

    /**
     * Tests the now() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testNow() throws Exception {
        Date before = new Date();

        Thread.sleep(500);
        String now_str = evaluateInDefaultContextAsString("now()");
        Date now = null;
        try {
            now = ExtensionFunctionsHelper.parseISODate(now_str);
        } catch (java.text.ParseException e) {
            fail("now() returned unparseable date: '" + now_str + "', message was: " + e.getMessage());
        }
        Thread.sleep(500);
        Date after = new Date();

        assertTrue(before.before(now));
        assertTrue(after.after(now));
    }

    /**
     * Tests the days-from-date() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testDaysFromDate() throws Exception {
        assertEquals(new Double(11688), evaluateInDefaultContextAsDouble("days-from-date('2002-01-01')"));
        assertEquals(new Double(11688), evaluateInDefaultContextAsDouble("days-from-date('2002-01-01T14:15:16Z')"));
        assertEquals(new Double(11688), evaluateInDefaultContextAsDouble("days-from-date('2002-01-01T23:15:16+01:00')"));
        assertEquals(new Double(11689), evaluateInDefaultContextAsDouble("days-from-date('2002-01-01T23:15:16-05:00')"));

        assertEquals(new Double(2), evaluateInDefaultContextAsDouble("days-from-date('1970-01-03')"));
        assertEquals(new Double(1), evaluateInDefaultContextAsDouble("days-from-date('1970-01-02')"));
        assertEquals(new Double(0), evaluateInDefaultContextAsDouble("days-from-date('1970-01-01')"));
        assertEquals(new Double(-1), evaluateInDefaultContextAsDouble("days-from-date('1969-12-31')"));
        assertEquals(new Double(-2), evaluateInDefaultContextAsDouble("days-from-date('1969-12-30')"));

        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("days-from-date('2002-13-29')"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("days-from-date('2002-01-32')"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("days-from-date('2002-02-29')"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("days-from-date('2002-02-29T14:15:16Z')"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("days-from-date('2002-02-29:15:16+01:00')"));
    }

    /**
     * Tests the seconds-from-dateTime() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSecondsFromDateTime() throws Exception {
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("seconds-from-dateTime('2002-01-01')"));
        assertEquals(new Double(1.009894516E9), evaluateInDefaultContextAsDouble("seconds-from-dateTime('2002-01-01T14:15:16Z')"));
        assertEquals(new Double(1.009923316E9), evaluateInDefaultContextAsDouble("seconds-from-dateTime('2002-01-01T23:15:16+01:00')"));

        assertEquals(new Double(172800), evaluateInDefaultContextAsDouble("seconds-from-dateTime('1970-01-03T00:00:00Z')"));
        assertEquals(new Double(86400), evaluateInDefaultContextAsDouble("seconds-from-dateTime('1970-01-02T00:00:00Z')"));
        assertEquals(new Double(0), evaluateInDefaultContextAsDouble("seconds-from-dateTime('1970-01-01T00:00:00Z')"));
        assertEquals(new Double(-86400), evaluateInDefaultContextAsDouble("seconds-from-dateTime('1969-12-31T00:00:00Z')"));
        assertEquals(new Double(-172800), evaluateInDefaultContextAsDouble("seconds-from-dateTime('1969-12-30T00:00:00Z')"));
    }

    /**
     * Tests the seconds-from-dateTime() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSecondsToDateTime() throws Exception {
        assertEquals("2002-01-01T14:15:16Z", evaluateInDefaultContextAsString("seconds-to-dateTime(1.009894516E9)"));

        assertEquals("1970-01-03T00:00:00Z", evaluateInDefaultContextAsString("seconds-to-dateTime(172800)"));
        assertEquals("1970-01-02T00:00:00Z", evaluateInDefaultContextAsString("seconds-to-dateTime(86400)"));
        assertEquals("1970-01-01T00:00:00Z", evaluateInDefaultContextAsString("seconds-to-dateTime(0)"));
        assertEquals("1969-12-31T00:00:00Z", evaluateInDefaultContextAsString("seconds-to-dateTime(-86400)"));
    }

    /**
     * Tests the seconds-from-dateTime() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testAdjustToTimeZone() throws Exception {
        assertEquals(convertToLocalTimeZone("2002-01-01T14:15:16Z"), evaluateInDefaultContextAsString("adjust-dateTime-to-timezone('2002-01-01T14:15:16Z')"));
    }

    private String convertToLocalTimeZone(String dateTime) throws Exception {
        DateTimeValue argAsDateTime = (DateTimeValue) DateTimeValue.makeDateTimeValue(dateTime, new ConversionRules()).asAtomic();
        return argAsDateTime.adjustTimezone(kLOCAL_TIME_OFFSET_IN_MINUTES).getStringValue();
    }

    /**
     * Tests the seconds() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSeconds() throws Exception {
        assertEquals(new Double(0), evaluateInDefaultContextAsDouble("seconds('P1Y2M')"));
        assertEquals(new Double(297001.5), evaluateInDefaultContextAsDouble("seconds('P3DT10H30M1.5S')"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("seconds('3')"));
    }

    /**
     * Tests the months() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMonths() throws Exception {
        assertEquals(new Double(14), evaluateInDefaultContextAsDouble("months('P1Y2M')"));
        assertEquals(new Double(-19), evaluateInDefaultContextAsDouble("months('-P19M')"));
    }

    /**
     * Tests the instance() function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testInstance() throws Exception {
        assertEquals(3d, evaluateInDefaultContextAsDouble("count(instance('instance-1')/number)"));
        assertEquals("2", evaluateInDefaultContextAsString("string(instance('instance-1')/number[2])"));
        assertEquals("2", evaluateInDefaultContextAsString("string(instance('instance-1')/number[2])"));
//        assertEquals("/data[1]/number[2]", this.defaultContext.getPointer("instance('instance-1')/number[2]").asPath());
        assertEquals("data", evaluateInDefaultContextAsNode("instance('instance-1')").getNodeName());
        assertEquals(Node.ELEMENT_NODE, evaluateInDefaultContextAsNode("instance('instance-1')").getNodeType());

        assertEquals(1d, evaluateInDefaultContextAsDouble("count(instance('instance-2'))"));
        assertEquals("dummy", evaluateInDefaultContextAsString("string(instance('instance-2'))"));
//        assertEquals("/data[1]", this.defaultContext.getPointer("instance('instance-2')").asPath());
        assertEquals("data", evaluateInDefaultContextAsNode("instance('instance-2')").getNodeName());
        assertEquals(Node.ELEMENT_NODE, evaluateInDefaultContextAsNode("instance('instance-2')").getNodeType());

//         test instance function without parameters
        // DOMUtil.prettyPrintDOM(processor.getContainer().getDocument());
        assertEquals("1", evaluateInDefaultContextAsString("string(instance()/number[1])"));

        AbstractFormControl element = (AbstractFormControl) processor.lookup("second-default");
        assertEquals("second", element.getValue());

        element = (AbstractFormControl) processor.lookup("scoped-output");
        assertEquals("second", element.getValue());

        element = (AbstractFormControl) processor.lookup("second-bind");
        assertEquals("second", element.getValue());
    }

    public void testCurrent() throws Exception {

        assertEquals("8023.451", evaluateInDefaultContextAsString("string(instance('instance-3')/convertedAmount)"));


        assertEquals("Jan", evaluateInDocumentContextAsString("string(//xf:repeat[2]/xf:group[1]//xf:output/bf:data[1])"));
        assertEquals("Feb", evaluateInDocumentContextAsString("string(//xf:repeat[2]/xf:group[2]//xf:output/bf:data[1])"));
        assertEquals("Mar", evaluateInDocumentContextAsString("string(//xf:repeat[2]/xf:group[3]//xf:output/bf:data[1])"));

        assertEquals("Jan", evaluateInDefaultContextAsString("string(instance('i1')/mon[1]/@result)"));
        assertEquals("Feb", evaluateInDefaultContextAsString("string(instance('i1')/mon[2]/@result)"));
        assertEquals("Mar", evaluateInDefaultContextAsString("string(instance('i1')/mon[3]/@result)"));
    }


    public void testPower() throws Exception {
        assertEquals("2", evaluateInDefaultContextAsString("string(instance('instance-1')/number[2])"));
        assertEquals("3", evaluateInDefaultContextAsString("string(instance('instance-1')/number[3])"));
        assertEquals(8.0, evaluateInDefaultContextAsDouble("power(instance('instance-1')/number[2], instance('instance-1')/number[3])"));
        assertEquals(8.0, evaluateInDefaultContextAsDouble("power(2, 3)"));
        assertEquals(Double.NaN, evaluateInDefaultContextAsDouble("power(-1, 0.5)"));
        assertEquals(1.0, evaluateInDefaultContextAsDouble("power(1, 0.5)"));
        assertEquals(Math.sqrt(2), evaluateInDefaultContextAsDouble("power(2, 0.5)"));
        assertEquals(0.0, evaluateInDefaultContextAsDouble("power(0, 1)"));
    }

    public void testRandom() throws Exception {
        assertNotSame(evaluateInDefaultContextAsDouble("random()"), evaluateInDefaultContextAsDouble("random()"));
        assertNotSame(evaluateInDefaultContextAsDouble("random(true())"), evaluateInDefaultContextAsDouble("random(true())"));
        assertNotSame(evaluateInDefaultContextAsDouble("random(false())"), evaluateInDefaultContextAsDouble("random(false())"));
        assertTrue(1.0 > evaluateInDefaultContextAsDouble("random()"));
        assertTrue(1.0 > evaluateInDefaultContextAsDouble("random(false())"));
        assertTrue(1.0 > evaluateInDefaultContextAsDouble("random(true())"));
        assertTrue(evaluateInDefaultContextAsDouble("random()") >= 0);
        assertTrue(evaluateInDefaultContextAsDouble("random(false())") >= 0);
        assertTrue(evaluateInDefaultContextAsDouble("random(true())") >= 0);
    }

    public void testCompare() throws Exception {
        assertEquals(-1.0, evaluateInDefaultContextAsDouble("compare('apple', 'orange')"));
        assertEquals(0.0, evaluateInDefaultContextAsDouble("compare('apple', 'apple')"));
        assertEquals(1.0, evaluateInDefaultContextAsDouble("compare('orange', 'apple')"));
    }


    public void testIsCardNumber() throws XFormsException {
        assertEquals(true, evaluateInDefaultContextAsBoolean("is-card-number('4111111111111111')"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("is-card-number('5431111111111111')"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("is-card-number('341111111111111')"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("is-card-number('6011601160116611')"));
        assertEquals(false, evaluateInDefaultContextAsBoolean("is-card-number('123')"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("is-card-number(instance('luhn')/number[1])"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("is-card-number(instance('luhn')/number[2])"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("is-card-number(instance('luhn')/number[3])"));
        assertEquals(true, evaluateInDefaultContextAsBoolean("is-card-number(instance('luhn')/number[4])"));
        assertEquals(false, evaluateInDefaultContextAsBoolean("is-card-number(instance('luhn')/number[5])"));


    }

    public void testDigest() throws XFormsException {
        assertEquals("qZk+NkcGgWq6PiVxeFDCbJzQ2J0=", evaluateInDefaultContextAsString("digest('abc', 'SHA-1')"));
        assertEquals("kAFQmDzST7DWlj99KOF/cg==", evaluateInDefaultContextAsString("digest('abc', 'MD5')"));
        assertEquals("ungWv48Bz+pBQUDeXa4iI7ADYaOWF3qctBD/YfIAFa0=", evaluateInDefaultContextAsString("digest('abc', 'SHA-256')"));

        assertEquals("qZk+NkcGgWq6PiVxeFDCbJzQ2J0=", evaluateInDefaultContextAsString("digest('abc', 'SHA-1', 'base64')"));
        assertEquals("kAFQmDzST7DWlj99KOF/cg==", evaluateInDefaultContextAsString("digest('abc', 'MD5', 'base64')"));
        assertEquals("ungWv48Bz+pBQUDeXa4iI7ADYaOWF3qctBD/YfIAFa0=", evaluateInDefaultContextAsString("digest('abc', 'SHA-256', 'base64')"));

        assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d", evaluateInDefaultContextAsString("digest('abc', 'SHA-1', 'hex')"));
        assertEquals("900150983cd24fb0d6963f7d28e17f72", evaluateInDefaultContextAsString("digest('abc', 'MD5', 'hex')"));
        assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad", evaluateInDefaultContextAsString("digest('abc', 'SHA-256', 'hex')"));
    }

    public void testHmac() throws XFormsException {
        assertEquals("7/zfauXrL6LSdBbV8YTfnCWafHk=", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'SHA-1')"));
        assertEquals("dQx4PmqwtQPqqG4xCl23OA==", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'MD5')"));
        assertEquals("W9zBRr9gdU5qBCQmCJV1x1oAPwidJzmDnexYuWTsOEM=", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'SHA-256')"));

        assertEquals("7/zfauXrL6LSdBbV8YTfnCWafHk=", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'SHA-1', 'base64')"));
        assertEquals("dQx4PmqwtQPqqG4xCl23OA==", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'MD5', 'base64')"));
        assertEquals("W9zBRr9gdU5qBCQmCJV1x1oAPwidJzmDnexYuWTsOEM=", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'SHA-256', 'base64')"));

        assertEquals("effcdf6ae5eb2fa2d27416d5f184df9c259a7c79", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'SHA-1', 'hex')"));
        assertEquals("750c783e6ab0b503eaa86e310a5db738", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'MD5', 'hex')"));
        assertEquals("5bdcc146bf60754e6a042426089575c75a003f089d2739839dec58b964ec3843", evaluateInDefaultContextAsString("hmac('Jefe', 'what do ya want for nothing?', 'SHA-256', 'hex')"));
    }

    public void testDayToDate() throws Exception {
        assertEquals("2002-01-01", evaluateInDefaultContextAsString("days-to-date(11688)"));
        assertEquals("1969-12-31", evaluateInDefaultContextAsString("days-to-date(-1)"));
        assertEquals("", evaluateInDefaultContextAsString("days-to-date(" + Double.NaN + ")"));
        assertEquals("", evaluateInDefaultContextAsString("days-to-date('hallo')"));
    }

//    public void testChoose() {
///*
//        assertEquals(1.0, this.defaultContext.getValue("choose(true(), 1,2)"));
//        assertEquals(1.0, this.defaultContext.getValue("choose(boolean-from-string('true'), 1,2)"));
//        assertEquals(2.0, this.defaultContext.getValue("choose(false(), 1,2)"));
//
//*/
//        assertEquals("2", this.defaultContext.getValue("instance('instance-1')/number[2]"));
//        assertEquals("3", this.defaultContext.getValue("instance('instance-1')/number[3]"));
//        assertEquals("1", this.defaultContext.getValue("instance('instance-1')/number"));
//        assertEquals(2, this.defaultContext.getValue("choose(count(instance('instance-1')/number[2]) > 0, instance('instance-1')/number, instance('luhn')/number)"));
//    }

    //XXX add test case for choose

    public void testInstanceOfModel() throws Exception {
        assertEquals("second", evaluateInDefaultContextAsString("bf:instanceOfModel('second','default2')/second"));
    }


    protected InputStream getTestCaseDocumentAsStream() {
        return getClass().getResourceAsStream("XFormsExtensionFunctionsTest.xhtml");
    }

    protected String getTestCaseURI() {
        return "XFormsExtensionFunctionsTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return new XPathFunctionContext(this.processor.lookup("default-instance"));
    }

}

// end of class
