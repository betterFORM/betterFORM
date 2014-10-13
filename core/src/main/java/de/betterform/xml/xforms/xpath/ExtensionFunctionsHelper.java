/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Provides static helper for Extension Functions.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ExtensionFunctionsHelper.java 3254 2008-07-08 09:26:53Z lasse $
 * todo:remove after Saxon migration
 */
public class ExtensionFunctionsHelper {

    /**
     * Returns a date formatted according to ISO 8601 rules.
     *
     * @param date the date to format.
     * @return the formmatted date.
     */
    public static String formatISODate(Date date) {
        // always set time zone on formatter
        TimeZone timeZone = TimeZone.getDefault();
        boolean utc = TimeZone.getTimeZone("UTC").equals(timeZone) || TimeZone.getTimeZone("GMT").equals(timeZone);

        String pattern = utc ? "yyyy-MM-dd'T'HH:mm:ss'Z'" : "yyyy-MM-dd'T'HH:mm:ssZ";
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        format.setTimeZone(timeZone);

        StringBuffer buffer = new StringBuffer(format.format(date));
        if (!utc) {
            buffer.insert(buffer.length() - 2, ':');
        }

        return buffer.toString();
    }

    /**
     * Returns a date parsed from a date/dateTime string formatted accorings to
     * ISO 8601 rules.
     *
     * @param date the formatted date/dateTime string.
     * @return the parsed date.
     * @throws ParseException if the date/dateTime string could not be parsed.
     */
    public static Date parseISODate(String date) throws ParseException {
        String pattern;
        StringBuilder buffer = new StringBuilder(date);

        switch (buffer.length()) {
            case 4:
                // Year: yyyy (eg 1997)
                pattern = "yyyy";
                break;
            case 7:
                // Year and month: yyyy-MM (eg 1997-07)
                pattern = "yyyy-MM";
                break;
            case 10:
                // Complete date: yyyy-MM-dd (eg 1997-07-16)
                pattern = "yyyy-MM-dd";
                break;
            default:
                // Complete date plus hours and minutes: yyyy-MM-ddTHH:mmTZD (eg 1997-07-16T19:20+01:00)
                // Complete date plus hours, minutes and seconds: yyyy-MM-ddTHH:mm:ssTZD (eg 1997-07-16T19:20:30+01:00)
                // Complete date plus hours, minutes, seconds and a decimal fraction of a second: yyyy-MM-ddTHH:mm:ss.STZD (eg 1997-07-16T19:20:30.45+01:00)
                pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";

                if (buffer.length() == 16) {
                    // add seconds
                    buffer.append(":00");
                }
                if (buffer.length() > 16 && buffer.charAt(16) != ':') {
                    // insert seconds
                    buffer.insert(16, ":00");
                }
                if (buffer.length() == 19) {
                    // add milliseconds
                    buffer.append(".000");
                }
                if (buffer.length() > 19 && buffer.charAt(19) != '.') {
                    // insert milliseconds
                    buffer.insert(19, ".000");
                }
                for (int i=20; i<23; i++) {
                    // pad fractional seconds with zeroes
                    if (buffer.length() > i && !Character.isDigit(buffer.charAt(i))) {
                        buffer.insert(i, '0');
                    } else if (buffer.length() == i) {
                        buffer.append('0');
                    }
                }
                if (buffer.length() == 23) {
                    if (buffer.charAt(22) == 'Z')
                    {
                	// replace 'Z' with '+0000'
                	buffer.replace(22, 23, "+0000");
                    }
                    else
                    {
                        // append timzeone
                        buffer.append("+0000");
                    }
                }
                if (buffer.length() == 24 && buffer.charAt(23) == 'Z') {
                    // replace 'Z' with '+0000'
                    buffer.replace(23, 24, "+0000");
                }
                if (buffer.length() == 29 && buffer.charAt(26) == ':') {
                    // delete '.' from 'HH:mm'
                    buffer.deleteCharAt(26);
                }
        }

        // always set time zone on formatter
        SimpleDateFormat format = new SimpleDateFormat(pattern);
        final String dateFromBuffer = buffer.toString();
        if (dateFromBuffer.length() > 10)
        {
        	format.setTimeZone(TimeZone.getTimeZone("GMT" + dateFromBuffer.substring(23)));
        }
        if (!format.format(format.parse(dateFromBuffer)).equals(dateFromBuffer))
        {
        	throw new ParseException("Not a valid ISO date: "+ dateFromBuffer, 0);
        }
        format.setTimeZone(TimeZone.getTimeZone("UTC"));

        return format.parse(buffer.toString());
    }

    // todo: formatISODuration ???
    /**
     * Parses the specified xsd:duration string.
     * <p/>
     * Returns an array of length 7:
     * <ul>
     * <li>[0] plus/minus (1 or -1)</li>
     * <li>[1] years</li>
     * <li>[2] months</li>
     * <li>[3] days</li>
     * <li>[4] hours</li>
     * <li>[5] minutes</li>
     * <li>[6] seconds</li>
     * </ul>
     *
     * @param duration the xsd:duration string.
     * @return an array containing the parsed values.
     * @throws ParseException if the duration string could not be parsed.
     */
    public static float[] parseISODuration(String duration) throws ParseException {
        // duration format (-)PnYnMnDTnHnMnS
        float[] values = new float[7];
        int index = 0;

        // check minus sign
        if (duration.charAt(index) == '-') {
            values[0] = -1;
            index++;
        }
        else {
            values[0] = 1;
        }

        // check 'P' designator
        if (duration.charAt(index) == 'P') {
            index++;
        }
        else {
            throw new ParseException("missing 'P' designator at [" + index + "]", index);
        }

        char designator;
        int offset;
        int slot;
        boolean time = false;
        boolean pending = false;
        String value = null;

        while (index < duration.length()) {
            // get designator
            designator = duration.charAt(index);

            // time designator
            if (designator == 'T') {
                time = true;

                index++;
                continue;
            }

            // field designator
            if (isDesignator(designator)) {
                if (!pending) {
                    throw new ParseException("missing value at [" + index + "]", index);
                }

                slot = getDesignatorSlot(designator, time);
                try {
                    values[slot] = getDesignatorValue(designator, value);
                }
                catch (NumberFormatException e) {
                    throw new ParseException("malformed value at [" + (index - value.length()) + "]", (index - value.length()));
                }

                pending = false;
                index++;
                continue;
            }

            // field value
            offset = getNumberOffset(duration, index);
            if (offset == index) {
                throw new ParseException("missing value at [" + index + "]", index);
            }
            if (offset == duration.length()) {
                throw new ParseException("missing designator at [" + offset + "]", offset);
            }

            value = duration.substring(index, offset);
            index = offset;
            pending = true;
        }

        return values;
    }

    private static int getNumberOffset(String string, int offset) {
        int index;
        char current;
        for (index = offset; index < string.length(); index++) {
            current = string.charAt(index);
            if (!Character.isDigit(current) && current != '.') {
                break;
            }
        }

        return index;
    }

    private static boolean isDesignator (char c) {
        return c == 'Y' || c == 'M' || c == 'D' || c == 'H' || c == 'S';
    }

    private static int getDesignatorSlot (char designator, boolean time) {
        switch (designator) {
            case 'Y':
                return 1;
            case 'M':
                return time ? 5 : 2;
            case 'D':
                return 3;
            case 'H':
                return 4;
            case 'S':
                return 6;
            default:
                return -1;
        }
    }

    private static float getDesignatorValue (char designator, String value) throws NumberFormatException {
        if (designator != 'S') {
            Integer.parseInt(value);
        }

        return Float.parseFloat(value);
    }
}

// end of class
