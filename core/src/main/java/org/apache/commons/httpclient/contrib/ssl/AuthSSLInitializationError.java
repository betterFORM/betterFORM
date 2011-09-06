/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package org.apache.commons.httpclient.contrib.ssl;

/**
 * <p>
 * Signals fatal error in initialization of {@link AuthSSLProtocolSocketFactory}.
 * </p>
 * 
 * @author <a href="mailto:oleg@ural.ru">Oleg Kalnichevski</a>
 * 
 * <p>
 * DISCLAIMER: HttpClient developers DO NOT actively support this component.
 * The component is provided as a reference material, which may be inappropriate
 * for use without additional customization.
 * </p>
 */

public class AuthSSLInitializationError extends Error {

    /**
     * Creates a new AuthSSLInitializationError.
     */
    public AuthSSLInitializationError() {
        super();
    }

    /**
     * Creates a new AuthSSLInitializationError with the specified message.
     *
     * @param message error message
     */
    public AuthSSLInitializationError(String message) {
        super(message);
    }
}
