/* Copyright 2008 - Joern Turner, Lars Windauer */


package de.betterform.agent.web.servlet.compositecontrols;

/**
 * Factory to create an appropriate composite control based on the
 * name of an XForms control
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>
 */
public class CompositeControlFactory {
    public final static CompositeControlValue createCompositeControl(String name) {
        if (name.startsWith(CompositeControlValue.prefix)) {
            if (name.startsWith(DateValue.prefix)) {
                return new DateValue();
            } else if (name.startsWith(TimeValue.prefix)) {
                return new TimeValue();
            } else if (name.startsWith(DateTimeValue.prefix)) {
                return new DateTimeValue();
            } else if (name.startsWith(DayTimeDurationValue.prefix)) {
                return new DayTimeDurationValue();
            }
        }

        return null;
    }
}
