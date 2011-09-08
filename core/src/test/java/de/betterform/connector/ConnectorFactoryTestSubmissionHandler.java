/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector;

import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * Dummy submission handler for the connector factory test.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ConnectorFactoryTestSubmissionHandler.java 2797 2007-08-10 12:45:24Z joern $
 */
public class ConnectorFactoryTestSubmissionHandler extends AbstractConnector implements SubmissionHandler {

    /**
     * Returns <code>null</code>.
     *
     * @param submission the submission issuing the request.
     * @param instance   the instance data to be serialized and submitted.
     * @return <code>null</code>.
     */
    public Map submit(Submission submission, Node instance) {
        return null;
    }
}

// end of class
