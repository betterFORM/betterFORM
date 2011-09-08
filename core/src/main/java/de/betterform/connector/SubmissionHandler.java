/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector;

import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Node;

import java.util.Map;

/**
 * A submission driver performs instance data serialization and submission
 * over a specific transport protocol.
 * <p/>
 * Implementors of this interface are expected to return a map containing
 * at least an input stream providing the submission response. Additional
 * information like headers should be provided in the map, too.
 *
 * @author <a href="mailto:unl@users.sourceforge.net">uli</a>
 * @version $Id: SubmissionHandler.java 2873 2007-09-28 09:08:48Z lars $
 */
public interface SubmissionHandler extends Connector {
    /**
     * Serializes and submits the specified instance data over a specific
     * transport protocol.
     *
     * @param submission the submission issuing the request.
     * @param instance   the instance data to be serialized and submitted.
     * @return a map holding the original protocol specific response.
     * @throws XFormsException if any error occurred during submission.
     */
    Map submit(Submission submission, Node instance) throws XFormsException;
}

//end of interface

