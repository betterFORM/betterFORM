/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.connector.file;

import junit.framework.TestCase;
import de.betterform.connector.ConnectorFactory;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * Test cases for the file submission handler implementation.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: FileSubmissionHandlerTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class FileSubmissionHandlerTest extends TestCase {
//	static {
//		org.apache.log4j.BasicConfigurator.configure();
//	}

    private DocumentBuilder builder;
    private DOMComparator comparator;
    private String baseURI;
    private XFormsProcessorImpl xformsProcesssorImpl;
    private Document instance;
    private SubmissionHandler submissionHandler;
    private String tmpFile;

    /**
     * Tests the GET submission method.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSubmitGet() throws Exception {
        Submission submission = (Submission) this.xformsProcesssorImpl.getContainer().lookup("submission-get");
        URI uri = ConnectorFactory.getFactory().getAbsoluteURI(submission.getAction(), submission.getElement());

        this.submissionHandler.setURI(uri.toString());
        Map map = this.submissionHandler.submit(submission, this.instance);

        assertNotNull(map);
        assertNotNull(map.get(XFormsProcessor.SUBMISSION_RESPONSE_STREAM));

        Document get = this.builder.parse((InputStream) map.get(XFormsProcessor.SUBMISSION_RESPONSE_STREAM));
        assertTrue(this.comparator.compare(this.instance, get));
    }

    /**
     * Tests the PUT submission method.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSubmitPut() throws Exception {
        Submission submission = (Submission) this.xformsProcesssorImpl.getContainer().lookup("submission-put");
        URI uri = ConnectorFactory.getFactory().getAbsoluteURI(submission.getAction(), submission.getElement());

        this.submissionHandler.setURI(uri.toString());
        Map map = this.submissionHandler.submit(submission, this.instance);

        assertNotNull(map);
        assertNull(map.get(XFormsProcessor.SUBMISSION_RESPONSE_STREAM));

        this.tmpFile = submission.getAction();
        Document put = this.builder.parse(getClass().getResourceAsStream(this.tmpFile));
        assertTrue(this.comparator.compare(this.instance, put));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        this.builder = factory.newDocumentBuilder();

        this.comparator = new DOMComparator();
        this.comparator.setIgnoreNamespaceDeclarations(true);
        this.comparator.setIgnoreWhitespace(true);
        this.comparator.setIgnoreComments(true);
        this.comparator.setErrorHandler(new DOMComparator.SystemErrorHandler());

        String path = getClass().getResource("FileSubmissionHandlerTest.xhtml").getPath();
        this.baseURI = "file://" + path.substring(0, path.lastIndexOf("FileSubmissionHandlerTest.xhtml"));

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setBaseURI(this.baseURI);
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("FileSubmissionHandlerTest.xhtml"));
        this.xformsProcesssorImpl.init();

        this.instance = this.xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance().getInstanceDocument();

        this.submissionHandler = new FileSubmissionHandler();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.submissionHandler = null;
        this.instance = null;

        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;

        if (this.tmpFile != null) {
            new File(new URI(this.baseURI + this.tmpFile)).delete();
            this.tmpFile = null;
        }

        this.baseURI = null;
        this.comparator = null;
        this.builder = null;
    }

}

// end of class
