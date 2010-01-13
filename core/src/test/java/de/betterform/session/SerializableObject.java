/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.session;

import java.io.Serializable;

public class SerializableObject  implements Serializable {
    private String value = "initial" ;

    public SerializableObject(String value) {
        this.value = value;
    }
    public void setValue(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
