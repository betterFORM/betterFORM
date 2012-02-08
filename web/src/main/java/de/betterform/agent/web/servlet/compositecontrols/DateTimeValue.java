/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet.compositecontrols;

/**
 * Represents the value of a composite control of the type xs:dateTime
 * xs:dateTime is composed of an xs:date and an xs:time
 * <p/>
 * Value looks like 2006-09-19T10:56:00.00+1:00 or YYYY-MM-DDTHH:mm:ss.ms+tz
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>
 */
public class DateTimeValue implements CompositeControlValue {
    public final static String prefix = CompositeControlValue.prefix + "dttm_";

    private DateValue dtDate = new DateValue();
    private TimeValue dtTime = new TimeValue();


    public void setPart(String name, String value) {
        String partName = name.substring(prefix.length());
        partName = partName.substring(0, partName.lastIndexOf('_'));

        if (partName.equals("year")) {
            dtDate.setYear(value);
        } else if (partName.equals("month")) {
            dtDate.setMonth(value);
        } else if (partName.equals("day")) {
            dtDate.setDay(value);
        } else if (partName.equals("hour")) {
            dtTime.setHour(value);
        } else if (partName.equals("minute")) {
            dtTime.setMinute(value);
        } else if (partName.equals("second")) {
            dtTime.setSecond(value);
        } else if (partName.equals("timezone")) {
            dtTime.setTimeZone(value);
        }
    }

    public boolean isComplete() {
        return (dtDate.isComplete() && dtTime.isComplete());
    }

    public String toString() {
        return new String(dtDate.toString() + 'T' + dtTime.toString());
    }
}