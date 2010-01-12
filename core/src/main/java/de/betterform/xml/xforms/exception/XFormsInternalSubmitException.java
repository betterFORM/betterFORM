/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.xml.xforms.exception;

// todo: rename to BetterFormException and move up in the
/**
 * betterForm internal submit exception exceptions.
 *
 * @author Nick Van den Bleeken
 */
public class XFormsInternalSubmitException extends XFormsException {
	private final int statusCode; 
	private final String statusText;
	private final String responseBodyAsString;

    /*
    Error Types: submission-in-progress, no-data, validation-error, parse-error, resource-error, target-error.
    TODO: implement missing error-types
     */
	private final String errorType;

    public XFormsInternalSubmitException(int statusCode, String statusText, String errorType) {
        super(statusCode + ": " + statusText + ": "  + errorType);

        this.statusCode = statusCode;
        this.statusText = statusText;
        this.errorType  = errorType;
        responseBodyAsString = null;
    }

	public XFormsInternalSubmitException(int statusCode, String statusText, String responseBodyAsString, String errorType) {
		super(statusCode + ": " + statusText + ": " + responseBodyAsString + ":" + errorType);

		this.statusCode = statusCode;
		this.statusText = statusText;
		this.responseBodyAsString = responseBodyAsString;
        this.errorType  = errorType;
	}

	/**
	 * @return the statusCode
	 */
	public int getStatusCode() {
		return statusCode;
	}

	/**
	 * @return the statusText
	 */
	public String getStatusText() {
		return statusText;
	}

	/**
	 * @return the responseBodyAsString
	 */
	public String getResponseBodyAsString() {
		return responseBodyAsString;
	}

	/**
	 * @return the responseBodyAsString
	 */
	public String getErrorType() {
		return errorType;
	}

}

// end of class
