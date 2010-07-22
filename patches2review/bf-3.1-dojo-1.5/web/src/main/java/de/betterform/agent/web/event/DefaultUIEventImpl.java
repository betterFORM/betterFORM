/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.event;


/**
 * Data object for holding a change notification created from either the client (the component using betterForm) or the
 * processor itself.
 *
 * @author Joern Turner
 * @version $Id: DefaultUIEventImpl.java 3083 2008-01-21 11:29:21Z joern $
 */
public class DefaultUIEventImpl implements UIEvent {

    protected String eventName;
    protected String id;
    //protected String value;
    protected Object contextInfo;

    /**
     * Creates a new ModelItem object.
     */
    public DefaultUIEventImpl() {
    }


    public void initEvent(String eventName, String id, Object contextInfo) {
        this.eventName = eventName;
        this.id = id;
        this.contextInfo = contextInfo;
    }

    public String getEventName() {
        return this.eventName;
    }

    public String getId() {
        return id;
    }

    public Object getContextInfo() {
        return contextInfo;
    }

    public boolean equals(Object o) {
        UIEvent toCompare = (UIEvent) o;
        if (this.eventName.equals(toCompare.getEventName())
                && this.id.equals(toCompare.getId())) {
            return true;
        }
        return false;
    }
}

// end of class
