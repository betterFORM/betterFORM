
/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

/*
 * Created on Jan 21, 2004
 *
 */

package de.betterform.connector.http;


import de.betterform.connector.ConnectorFactory;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * This variant of an HTTP submission driver extends the normal driver, adding
 * support for following 201 Created, and 3xx Redirect status codes.
 *
 * @author <a href="mailto:rloz@users.sourceforge.net">Robert Leftwich</a>
 * @version $Id: RedirectingHTTPSubmissionHandler.java 2873 2007-09-28 09:08:48Z lars $
 */
public class RedirectingHTTPSubmissionHandler extends HTTPSubmissionHandler {
    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(RedirectingHTTPSubmissionHandler.class);

    private Submission mySubmission = null;

    /**
     * Override the submit method to capture the submission element
     */
    public Map submit(Submission submission, Node instance)
            throws XFormsException {
        this.mySubmission = submission;
        return super.submit(submission, instance);
    }

    /**
     * Override the execute method to redirect if appropriate
     */
    protected void execute(HttpRequestBase httpMethod) throws Exception {
        LOGGER.info("RedirectingHTTPSubmissionDriver.execute");
        HttpParams httpParams = new BasicHttpParams();
        HttpClient client = ConnectorFactory.getFactory().getHttpClient(httpParams);

        HttpResponse response = client.execute(httpMethod);

        if (response.getStatusLine().getStatusCode() >= 400) {
            throw new XFormsException("HTTP status "
                    + response.getStatusLine().getStatusCode()
                    + ": "
                    + response.getStatusLine().getReasonPhrase());
        }

        String locationURL = null;

        // if Created then redirect to the new uri
        if (response.getStatusLine().getStatusCode() == 201) {
            Header locationURLHdr =  response.getFirstHeader("Location");
            if (null == locationURLHdr)
                locationURLHdr = response.getFirstHeader("location");
            if (null == locationURLHdr)
                locationURLHdr = response.getFirstHeader("Content-Location");
            if (null == locationURLHdr)
                locationURLHdr = response.getFirstHeader("content-location");

            if (null != locationURLHdr) {
                locationURL = locationURLHdr.getValue();
            }

        }
        // if Updated with no content then redirect to the existing uri
        // (i.e. not the betterForm post address)
        else if (response.getStatusLine().getStatusCode() == 204) {
            locationURL = httpMethod.getURI().toString();
            // make sure redirects to the container
            // i.e. strip off any filename
            // (NOTE this is probably specific to my situation)
            // need access to the referer from the incoming http request)
            int idx = locationURL.lastIndexOf('/');
            if (idx > -1) {
                locationURL = locationURL.substring(0, idx + 1);
            }
        }

        if (null != locationURL) {
            LOGGER.info("Redirecting to location: " + locationURL);

            this.mySubmission.redirect(locationURL);
        }

        this.handleHttpMethod(response);
    }

}
