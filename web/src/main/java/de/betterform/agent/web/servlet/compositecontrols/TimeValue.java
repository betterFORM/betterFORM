/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet.compositecontrols;

/**
 * Represents the value of a composite control of the type xs:time
 * <p/>
 * xs:time looks like 10:56:00.000+01:00 or HH:mm:ss.mss+tz
 * <p/>
 * HH = the hour, string index 11 to 12
 * mm = the minute, string index 14 to 15
 * ss = the second, string index 17 to 18
 * mss = the milliseconds
 * tz = the timezone if any
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>
 */
public class TimeValue implements CompositeControlValue {
    public final static String prefix = CompositeControlValue.prefix + "tm_";

    private String hour = new String();
    private String minute = new String();
    private String second = new String();
    private String millisecond = "000";
    private String timezone = new String();


    public void setPart(String name, String value) {
        String partName = name.substring(prefix.length());
        partName = partName.substring(0, partName.lastIndexOf('_'));

        if (partName.equals("hour")) {
            setHour(value);
        } else if (partName.equals("minute")) {
            setMinute(value);
        } else if (partName.equals("second")) {
            setSecond(value);
        } else if (partName.equals("timezone")) {
            setTimeZone(value);
        }
    }

    protected void setHour(String hour) {
        if (hour.length() == 1) {
            this.hour = '0' + hour;
        }
        if (hour.length() == 2) {
            this.hour = hour;
        }
    }

    protected void setMinute(String minute) {
        if (minute.length() == 1) {
            this.minute = '0' + minute;
        }
        if (minute.length() == 2) {
            this.minute = minute;
        }
    }

    protected void setSecond(String second) {
        if (second.length() == 1) {
            this.second = '0' + second;
        }
        if (second.length() == 2) {
            this.second = second;
        }
    }

    protected void setTimeZone(String timezone) {
        if (timezone.length() == 5) {
            this.timezone = timezone.substring(0, 1) + '0' + timezone.substring(1);
        }
        if (timezone.length() == 6) {
            this.timezone = timezone;
        }
    }

    public boolean isComplete() {
        return (hour.length() == 2 && minute.length() == 2 && second.length() == 2 && millisecond.length() == 3 && timezone.length() == 6);
    }

    public String toString() {
        return new String(hour + ":" + minute + ":" + second + "." + millisecond + timezone);
    }
}