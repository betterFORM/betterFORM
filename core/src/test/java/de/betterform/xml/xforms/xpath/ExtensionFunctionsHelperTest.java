/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xforms.xpath;

import junit.framework.TestCase;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Test cases for Extension Functions Helper.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ExtensionFunctionsHelperTest.java 2797 2007-08-10 12:45:24Z joern $
 */
public class ExtensionFunctionsHelperTest extends TestCase {

    /**
     * Tests ISO date formatting.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testFormatISODate() throws Exception {
        // keep timezone default
        TimeZone timeZone = TimeZone.getDefault();
        Date date = create("UTC", 2001, Calendar.FEBRUARY, 10, 7, 17, 7, 0);

        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        assertEquals("2001-02-10T07:17:07Z", ExtensionFunctionsHelper.formatISODate(date));

        TimeZone.setDefault(TimeZone.getTimeZone("GMT"));
        assertEquals("2001-02-10T07:17:07Z", ExtensionFunctionsHelper.formatISODate(date));

        TimeZone.setDefault(TimeZone.getTimeZone("CET"));
        assertEquals("2001-02-10T08:17:07+01:00", ExtensionFunctionsHelper.formatISODate(date));

        TimeZone.setDefault(TimeZone.getTimeZone("PST"));
        assertEquals("2001-02-09T23:17:07-08:00", ExtensionFunctionsHelper.formatISODate(date));

        // keep timezone default
        TimeZone.setDefault(timeZone);
    }

    /**
     * Tests ISO date parsing.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testParseISODate() throws Exception {
        // test date formats
        assertEquals(create("UTC", 2001, Calendar.JANUARY, 1, 0, 0, 0, 0),
                ExtensionFunctionsHelper.parseISODate("2001"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 1, 0, 0, 0, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 0, 0, 0, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10"));

        // test dateTime Formats yyyy-MM-ddTHH:mmTZD
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 7, 17, 0, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 7, 17, 0, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17Z"));
        assertEquals(create("GMT", 2001, Calendar.FEBRUARY, 10, 7, 17, 0, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17Z"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 6, 17, 0, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17+01:00"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 8, 17, 0, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17-01:00"));

        // test dateTime Formats yyyy-MM-ddTHH:mm:ssTZD
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 7, 17, 7, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 7, 17, 7, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07Z"));
        assertEquals(create("GMT", 2001, Calendar.FEBRUARY, 10, 7, 17, 7, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07Z"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 6, 17, 7, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07+01:00"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 8, 17, 7, 0),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07-01:00"));

        // test dateTime Formats yyyy-MM-ddTHH:mm:ss.sTZD
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 7, 17, 7, 17),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07.017"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 7, 17, 7, 17),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07.017Z"));
        assertEquals(create("GMT", 2001, Calendar.FEBRUARY, 10, 7, 17, 7, 17),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07.017Z"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 6, 17, 7, 17),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07.017+01:00"));
        assertEquals(create("UTC", 2001, Calendar.FEBRUARY, 10, 8, 17, 7, 17),
                ExtensionFunctionsHelper.parseISODate("2001-02-10T07:17:07.017-01:00"));

        // xs:dateTime may not add trailing zeroes for the millisecond part,
        // so the parsing must accept unpadded milliseconds
        assertEquals(create("UTC", 2001, Calendar.OCTOBER, 11, 12, 13, 14, 0),
                ExtensionFunctionsHelper.parseISODate("2001-10-11T12:13:14Z"));

        assertEquals(create("UTC", 2001, Calendar.OCTOBER, 11, 12, 13, 14, 500),
                ExtensionFunctionsHelper.parseISODate("2001-10-11T12:13:14.5Z"));

        assertEquals(create("UTC", 2001, Calendar.OCTOBER, 11, 12, 13, 14, 220),
                ExtensionFunctionsHelper.parseISODate("2001-10-11T12:13:14.22Z"));

        // the same tests without timezone
        assertEquals(create("UTC", 2001, Calendar.OCTOBER, 11, 12, 13, 14, 0),
                ExtensionFunctionsHelper.parseISODate("2001-10-11T12:13:14"));

        assertEquals(create("UTC", 2001, Calendar.OCTOBER, 11, 12, 13, 14, 500),
                ExtensionFunctionsHelper.parseISODate("2001-10-11T12:13:14.5"));

        assertEquals(create("UTC", 2001, Calendar.OCTOBER, 11, 12, 13, 14, 220),
                ExtensionFunctionsHelper.parseISODate("2001-10-11T12:13:14.22"));
    }

    /**
     * Tests ISO duration parsing.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testParseISODuration() throws Exception {
        // test legal values
        assertEquals(toString(new float[]{1f, 1f, 2f, 0f, 0f, 0f, 0}),
                toString(ExtensionFunctionsHelper.parseISODuration("P1Y2M")));
        assertEquals(toString(new float[]{1f, 0f, 0f, 3f, 10f, 30f, 1.5f}),
                toString(ExtensionFunctionsHelper.parseISODuration("P3DT10H30M1.5S")));
        assertEquals(toString(new float[]{-1f, 0f, 19f, 0f, 0f, 0f, 0f}),
                toString(ExtensionFunctionsHelper.parseISODuration("-P19M")));

        // test missing designators
        try {
            ExtensionFunctionsHelper.parseISODuration("1Y2M");
            fail();
        }
        catch (ParseException e) {
            assertEquals("missing 'P' designator at [0]", e.getMessage());
        }
        try {
            ExtensionFunctionsHelper.parseISODuration("-1Y2M");
            fail();
        }
        catch (ParseException e) {
            assertEquals("missing 'P' designator at [1]", e.getMessage());
        }
        try {
            ExtensionFunctionsHelper.parseISODuration("P1Y2");
            fail();
        }
        catch (ParseException e) {
            assertEquals("missing designator at [4]", e.getMessage());
        }

        // test missing values
        try {
            ExtensionFunctionsHelper.parseISODuration("PY2M");
            fail();
        }
        catch (ParseException e) {
            assertEquals("missing value at [1]", e.getMessage());
        }
        try {
            ExtensionFunctionsHelper.parseISODuration("P1YM");
            fail();
        }
        catch (ParseException e) {
            assertEquals("missing value at [3]", e.getMessage());
        }

        // test value formats
        try {
            ExtensionFunctionsHelper.parseISODuration("P1.5Y");
            fail();
        }
        catch (ParseException e) {
            assertEquals("malformed value at [1]", e.getMessage());
        }
        try {
            ExtensionFunctionsHelper.parseISODuration("P1.5M");
            fail();
        }
        catch (ParseException e) {
            assertEquals("malformed value at [1]", e.getMessage());
        }
        try {
            ExtensionFunctionsHelper.parseISODuration("P1.5D");
            fail();
        }
        catch (ParseException e) {
            assertEquals("malformed value at [1]", e.getMessage());
        }
        try {
            ExtensionFunctionsHelper.parseISODuration("PT1.5H");
            fail();
        }
        catch (ParseException e) {
            assertEquals("malformed value at [2]", e.getMessage());
        }
        try {
            ExtensionFunctionsHelper.parseISODuration("PT1.5M");
            fail();
        }
        catch (ParseException e) {
            assertEquals("malformed value at [2]", e.getMessage());
        }
    }

    private Date create(String z, int yyyy, int MM, int dd, int HH, int mm, int ss, int SSS) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(z));
        calendar.clear();
        calendar.set(Calendar.YEAR, yyyy);
        calendar.set(Calendar.MONTH, MM);
        calendar.set(Calendar.DATE, dd);
        calendar.set(Calendar.HOUR, HH);
        calendar.set(Calendar.MINUTE, mm);
        calendar.set(Calendar.SECOND, ss);
        calendar.set(Calendar.MILLISECOND, SSS);
        return calendar.getTime();
    }

    private static String toString(float[] array) {
        StringBuffer buffer = new StringBuffer().append('[');
        for (int i = 0; i < array.length; i++) {
            if (i > 0) {
                buffer.append(',').append(' ');
            }
            buffer.append(array[i]);
        }
        return buffer.append(']').toString();
    }

}

// end of class
