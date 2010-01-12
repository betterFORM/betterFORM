/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */

package de.betterform.agent.web.servlet.compositecontrols;

/**
 * Represents the value of a composite control of the type xdt:dayTimeDuration
 * <p/>
 * xdt:dayTimeDuration looks like P1DT2H30M00S
 * <p/>
 * P = indicates a period
 * D = prefixed by the number of days
 * T = indicates the time portion
 * H = prefixed by the number of hours
 * M = prefixed by the number of minutes
 * S = prefixed by the number of seconds
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>
 */
public class DayTimeDurationValue implements CompositeControlValue {
    public final static String prefix = CompositeControlValue.prefix + "datmd_";

    private Integer days = null;
    private Integer hours = null;
    private Integer minutes = null;
    private Integer seconds = null;


    public void setPart(String name, String value) {
        String partName = name.substring(prefix.length());
        partName = partName.substring(0, partName.lastIndexOf('_'));

        Integer partValue = null;
        try {
            partValue = new Integer(value);
        }
        catch (NumberFormatException e) {
            //if there is a parse exception, then we can assume 0
            partValue = new Integer(0);
        }

        if (partName.equals("days")) {
            days = partValue;
        } else if (partName.equals("hours")) {
            hours = partValue;
        } else if (partName.equals("minutes")) {
            minutes = partValue;
        } else if (partName.equals("seconds")) {
            seconds = partValue;
        }
    }

    public boolean isComplete() {
        return (days != null && hours != null && minutes != null && seconds != null);
    }

    public String toString() {
        //have all composite values been set?
        if (!isComplete())
            return new String();

        String dayTimeDuration = new String("P");

        //days component
        dayTimeDuration += days.toString() + 'D';

        //time part
        dayTimeDuration += 'T';

        //hours component
        dayTimeDuration += hours.toString() + 'H';

        //minutes component
        dayTimeDuration += minutes.toString() + 'M';

        //seconds component
        dayTimeDuration += seconds.toString() + 'S';

        return dayTimeDuration;
    }
}
    
    
    
    
    
    