/* Copyright 2008 - Joern Turner, Lars Windauer */


package de.betterform.agent.web.servlet.compositecontrols;

/**
 * Represents the value of a composite control of the type xs:date
 * <p/>
 * xs:date looks like 2006-09-19 or YYYY-MM-DD
 * <p/>
 * YYYY = the year, string index 0 to 3
 * MM = the month, string index 5 to 6
 * DD = the day, string index 8 to 9
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>
 */
public class DateValue implements CompositeControlValue {
    public final static String prefix = CompositeControlValue.prefix + "dt_";

    private String year = new String();
    private String month = new String();
    private String day = new String();


    public void setPart(String name, String value) {
        String partName = name.substring(prefix.length());
        partName = partName.substring(0, partName.lastIndexOf('_'));

        if (partName.equals("year")) {
            setYear(value);
        } else if (partName.equals("month")) {
            setMonth(value);
        } else if (partName.equals("day")) {
            setDay(value);
        }
    }

    protected void setYear(String year) {
        if (year.length() == 2) {
            this.year = "20" + year;
        }
        if (year.length() == 4) {
            this.year = year;
        }
    }

    protected void setMonth(String month) {
        if (month.length() == 1) {
            this.month = '0' + month;
        }
        if (month.length() == 2) {
            this.month = month;
        }
    }

    protected void setDay(String day) {
        if (day.length() == 1) {
            this.day = '0' + day;
        }
        if (day.length() == 2) {
            this.day = day;
        }
    }

    public boolean isComplete() {
        return (year.length() == 4 && month.length() == 2 && day.length() == 2);
    }

    public String toString() {
        return new String(year + "-" + month + "-" + day);
    }
}