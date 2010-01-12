/*
 *
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a Package may be copied, such that
 *    the Copyright Holder maintains some semblance of artistic control over the development of the
 *    package, while giving the users of the package the right to use and distribute the Package in a
 *    more-or-less customary fashion, plus the right to make reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the Copyright Holder, and derivatives
 *    of that collection of files created through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been modified, or has been modified
 *    in accordance with the wishes of the Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of media cost, duplication
 *    charges, time of people involved, and so on. (You will not be required to justify it to the
 *    Copyright Holder, but only to the computing community at large as a market that must bear the
 *    fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself, though there may be fees
 *    involved in handling the item. It also means that recipients of the item may redistribute it under
 *    the same conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the Standard Version of this
 *    Package without restriction, provided that you duplicate all of the original copyright notices and
 *    associated disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications derived from the Public Domain
 *    or from the Copyright Holder. A Package modified in such a way shall still be considered the
 *    Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way, provided that you insert a
 *    prominent notice in each changed file stating how and when you changed that file, and provided that
 *    you do at least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make them Freely
 *        Available, such as by posting said modifications to Usenet or an equivalent medium, or
 *        placing the modifications on a major archive site such as ftp.uu.net, or by allowing the
 *        Copyright Holder to include your modifications in the Standard Version of the Package.
 *
 *        b) use the modified Package only within your corporation or organization.
 *
 *        c) rename any non-standard executables so the names do not conflict with standard
 *        executables, which must also be provided, and provide a separate manual page for each
 *        non-standard executable that clearly documents how it differs from the Standard
 *        Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or executable form, provided that
 *    you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library files, together with
 *        instructions (in the manual page or equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of the Package with
 *        your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding Standard Version
 *        executables, giving the non-standard executables non-standard names, and clearly
 *        documenting the differences in manual pages (or equivalent), together with instructions
 *        on where to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this Package. You may charge
 *    any fee you choose for support of this Package. You may not charge a fee for this Package itself.
 *    However, you may distribute this Package in aggregate with other (possibly commercial) programs as
 *    part of a larger (possibly commercial) software distribution provided that you do not advertise this
 *    Package as a product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as output from the programs of this
 *    Package do not automatically fall under the copyright of this Package, but belong to whomever
 *    generated them, and may be sold commercially, and may be aggregated with this Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package shall not be considered part of
 *    this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or promote products derived from
 *    this software without specific prior written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
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
