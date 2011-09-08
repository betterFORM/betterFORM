/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.serializer;

import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.model.submission.Submission;
import junit.framework.TestCase;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;

/**
 * Author: Fabian Otto, Tobi Krebs
 * Date: Apr 8, 2010
 */
abstract public class SerializeTest extends TestCase {
    protected FormDataSerializer serializer;
    private XFormsProcessorImpl xformsProcesssorImpl;
    protected Submission submission;
    protected Node node;

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.setUp("SerializerTest.xhtml");

    }

    /**
     * Sets up the test with an test xforms document in fileName
     * @parm fileName of the document
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp(String fileName) throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream(fileName));
        this.xformsProcesssorImpl.init();
        this.serializer = new FormDataSerializer();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during teardown.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    /**
     * Finds an submission by name and calls the serializer of it. The SerializerRespondObject is returned.
     * @param submissionName in the form
     * @return wrapper object. May contain additional information for header.
     */
    protected SerializerRequestWrapper serialize(String submissionName) throws Exception {
        Submission submission            = (Submission) this.xformsProcesssorImpl.getContainer().lookup(submissionName);
        Document instance                = this.xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();
        SerializerRequestWrapper wrapper = new SerializerRequestWrapper(new ByteArrayOutputStream(500));
        this.serializer.serialize(submission, (Node) instance, wrapper, "UTF-8");
        return wrapper;
    }
}
