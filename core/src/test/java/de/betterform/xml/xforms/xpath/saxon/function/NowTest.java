package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.xforms.xpath.ExtensionFunctionsHelper;
import junit.framework.TestCase;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.trans.XPathException;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class NowTest extends TestCase {

    Now now = new Now();

    public void testWholeSeconds() throws XPathException {
        XPathContext context = null;
        assertEquals("2001-10-11T14:12:37Z",
                now.evaluateItem(context, create("UTC", 2001, Calendar.OCTOBER, 11, 14, 12, 37, 0)).getStringValue());
    }

    public void testTensOfSeconds() throws XPathException {
        XPathContext context = null;
        assertEquals("2001-10-11T14:12:37.4Z",
                now.evaluateItem(context, create("UTC", 2001, Calendar.OCTOBER, 11, 14, 12, 37, 400)).getStringValue());
    }

    public void testMicroseconds() throws XPathException {
        XPathContext context = null;
        assertEquals("2001-10-11T14:12:37.22Z",
                now.evaluateItem(context, create("UTC", 2001, Calendar.OCTOBER, 11, 14, 12, 37, 220)).getStringValue());
    }

    private GregorianCalendar create(String z, int yyyy, int MM, int dd, int HH, int mm, int ss, int SSS) {
        GregorianCalendar calendar = new GregorianCalendar(TimeZone.getTimeZone(z));
        calendar.clear();
        calendar.set(Calendar.YEAR, yyyy);
        calendar.set(Calendar.MONTH, MM);
        calendar.set(Calendar.DATE, dd);
        calendar.set(Calendar.HOUR, HH);
        calendar.set(Calendar.MINUTE, mm);
        calendar.set(Calendar.SECOND, ss);
        calendar.set(Calendar.MILLISECOND, SSS);
        return calendar;
    }


}