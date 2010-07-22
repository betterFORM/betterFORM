/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.submission;

public class RequestHeader {
    private String name;
    private String value;


    public RequestHeader(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return this.name+":"+this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    public void prependValue(String valueToPrepend) {
    	this.value = valueToPrepend + "," + this.value;
    }
    
    public void appendValue(String valueToAppend) {
    	this.value = this.value + "," + valueToAppend;
    }
}
