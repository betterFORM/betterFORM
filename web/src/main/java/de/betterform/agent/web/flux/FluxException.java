/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */

package de.betterform.agent.web.flux;

/**
 * Used for signalling problems with Flux execution
 *
 * @author Joern Turner
 * @version $Id: FluxException.java 2528 2007-03-28 10:59:18Z joernt $
 */
public class FluxException extends Exception {

    public FluxException() {
    }

    public FluxException(String string) {
        super(string);
    }

    public FluxException(Throwable throwable) {
        super(throwable);
    }
}


