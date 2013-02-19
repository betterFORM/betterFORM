/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.rest;

/**
 * Used for signalling problems with Flux execution
 *
 * @author Joern Turner
 * @version $Id: FluxException.java 2528 2007-03-28 10:59:18Z joernt $
 */
public class RestException extends Exception {

    public RestException() {
    }

    public RestException(String string) {
        super(string);
    }

    public RestException(Throwable throwable) {
        super(throwable);
    }
}


