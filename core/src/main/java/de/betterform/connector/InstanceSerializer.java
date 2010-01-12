/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.connector;

import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Node;

import java.io.OutputStream;

/**
 * Interface for serialization of instances. Implementations should
 * be registered for specific scheme/method/mediatype using
 * <tt>registerSerializer</tt> method for given connector.
 *
 * @author Peter Mikula <peter.mikula@digital-artefacts.fi>
 */
public interface InstanceSerializer {

    /**
     * Serialize instance into the OuputStream <tt>stream</tt>
     *
     * @param submission      submission information.
     * @param instance        instance to serialize.
     * @param stream          stream to write into.
     * @param defaultEncoding use this encoding in case user did not provide one.
     */
    void serialize(Submission submission, Node instance, OutputStream stream,
                   String defaultEncoding) throws Exception;

}
