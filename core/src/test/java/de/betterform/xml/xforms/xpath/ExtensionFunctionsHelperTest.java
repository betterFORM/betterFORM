// Copyright 2010 betterForm
/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a Package may be copied, such that
 *    the Copyright Holder maintains some semblance of artistic control over the development of the
 *    package, while giving the users of the package the right to use and distribute the Package in a
 *    more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the Copyright Holder, and derivatives
 *    of that collection of files created through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been modified, or has been modified
 *    in accordance with the wishes of the Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of media cost, duplication
 *    charges, time of people involved, and so on. (You will not be required to justify it to the
 *    Copyright Holder, but only to the computing community at large as a market that must bear the
 *    fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself, though there may be fees
 *    involved in handling the item. It also means that recipients of the item may redistribute it under
 *    the same conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the Standard Version of this
 *    Package without restriction, provided that you duplicate all of the original copyright notices and
 *    associated disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications derived from the Public Domain
 *    or from the Copyright Holder. A Package modified in such a way shall still be considered the
 *    Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way, provided that you insert a
 *    prominent notice in each changed file stating how and when you changed that file, and provided that
 *    you do at least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make them Freely
 *        Available, such as by posting said modifications to Usenet or an equivalent medium, or
 *        placing the modifications on a major archive site such as ftp.uu.net, or by allowing the
 *        Copyright Holder to include your modifications in the Standard Version of the Package.
 *
 *        b) use the modified Package only within your corporation or organization.
 *
 *        c) rename any non-standard executables so the names do not conflict with standard
 *        executables, which must also be provided, and provide a separate manual page for each
 *        non-standard executable that clearly documents how it differs from the Standard
 *        Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or executable form, provided that
 *    you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library files, together with
 *        instructions (in the manual page or equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of the Package with
 *        your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding Standard Version
 *        executables, giving the non-standard executables non-standard names, and clearly
 *        documenting the differences in manual pages (or equivalent), together with instructions
 *        on where to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this Package. You may charge
 *    any fee you choose for support of this Package. You may not charge a fee for this Package itself.
 *    However, you may distribute this Package in aggregate with other (possibly commercial) programs as
 *    part of a larger (possibly commercial) software distribution provided that you do not advertise this
 *    Package as a product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as output from the programs of this
 *    Package do not automatically fall under the copyright of this Package, but belong to whomever
 *    generated them, and may be sold commercially, and may be aggregated with this Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package shall not be considered part of
 *    this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
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
