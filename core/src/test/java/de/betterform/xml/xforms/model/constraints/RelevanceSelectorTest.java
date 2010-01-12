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
package de.betterform.xml.xforms.model.constraints;

import junit.framework.TestCase;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;

/**
 * Relevance selector test cases.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: RelevanceSelectorTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class RelevanceSelectorTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;

        /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("RelevanceSelectorTest.xhtml"));
        this.xformsProcesssorImpl.init();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;
    }

    /**
     * Test case 1 for relevance selection of submitted instance data.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelectRelevant1() throws Exception {
        Instance instance = this.xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance();
        Document document = RelevanceSelector.selectRelevant(instance);
        document.normalize();


        assertTrue("name(/*[1]) = 'root'", XPathUtil.evaluateAsString(document,"name(/*[1])").equals("root"));
        assertTrue("/root/@xmlns = ''", document.getDocumentElement().getAttribute("xmlns").equals(""));
//        assertTrue("/root/@xmlns:testutil = 'http://testutil.org/ns'", XPathUtil.evaluateAsString(document,"/root/@xmlns:testutil").equals("http://testutil.org/ns"));
//        assertEquals("http://testutil.org/ns",XPathUtil.evaluateAsString(document,"/root/@xmlns:testutil"));
        assertTrue("count(/root/*) = 3", XPathUtil.evaluateAsString(document,"count(/root/*)").equals("3"));

        assertTrue("name(/root/*[1]) = 'name'", XPathUtil.evaluateAsString(document,"name(/root/*[1])").toString().equals("name"));
        assertTrue("/root/name/@id = 'n-1'", XPathUtil.evaluateAsString(document,"/root/name/@id").toString().equals("n-1"));
        assertTrue("count(/root/name/*) = 0", XPathUtil.evaluateAsString(document,"count(/root/name/*)").equals("0"));

        assertTrue("name(/root/*[2]) = 'paragraph'", XPathUtil.evaluateAsString(document,"name(/root/*[2])").toString().equals("paragraph"));
        assertTrue("/root/paragraph[1]/@id = 'p-1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/@id").toString().equals("p-1"));
        assertTrue("count(/root/paragraph[1]/*) = 1", XPathUtil.evaluateAsString(document,"count(/root/paragraph[1]/*)").equals("1"));

        assertTrue("name(/root/paragraph[1]/*[1]) = 'content'", XPathUtil.evaluateAsString(document,"name(/root/paragraph[1]/*[1])").toString().equals("content"));
        assertTrue("/root/paragraph[1]/content/@id = 'c-1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/@id").toString().equals("c-1"));
        assertTrue("/root/paragraph[1]/content/text()[1] = 'some mixed content 1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/text()[1]").replaceAll("[\n\r]", "").trim().equals("some mixed content 1"));
        assertTrue("/root/paragraph[1]/content/text()[2] = 'some more mixed content 1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/text()[2]").replaceAll("[\n\r]", "").trim().equals("some more mixed content 1"));
        assertTrue("count(/root/paragraph[1]/content/*) = 1", XPathUtil.evaluateAsString(document,"count(/root/paragraph[1]/content/*)").equals("1"));

        assertTrue("name(/root/paragraph[1]/content/*[1]) = 'input'", XPathUtil.evaluateAsString(document,"name(/root/paragraph[1]/content/*[1])").toString().equals("input"));
        assertTrue("/root/paragraph[1]/content/input/@id = 'i-1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/input/@id").toString().equals("i-1"));
        assertTrue("/root/paragraph[1]/content/input/text()[1] = 'input 1'", XPathUtil.evaluateAsString(document,"/root/paragraph[1]/content/input/text()[1]").toString().equals("input 1"));
        assertTrue("count(/root/paragraph[1]/content/input/*) = 0", XPathUtil.evaluateAsString(document,"count(/root/paragraph[1]/content/input/*)").equals("0"));

        assertTrue("name(/root/*[3]) = 'paragraph'", XPathUtil.evaluateAsString(document,"name(/root/*[3])").toString().equals("paragraph"));
        assertTrue("/root/paragraph[2]/@id = 'p-2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/@id").toString().equals("p-2"));
        assertTrue("count(/root/paragraph[2]/*) = 1", XPathUtil.evaluateAsString(document,"count(/root/paragraph[2]/*)").equals("1"));

        assertTrue("name(/root/paragraph[2]/*[1]) = 'content'", XPathUtil.evaluateAsString(document,"name(/root/paragraph[2]/*[1])").toString().equals("content"));
        assertTrue("/root/paragraph[2]/content/@id = 'c-2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/@id").toString().equals("c-2"));
        assertTrue("/root/paragraph[2]/content/text()[1] = 'some mixed content 2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/text()[1]").replaceAll("[\n\r]", "").trim().equals("some mixed content 2"));
        assertTrue("/root/paragraph[2]/content/text()[2] = 'some more mixed content 2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/text()[2]").replaceAll("[\n\r]", "").trim().equals("some more mixed content 2"));
        assertTrue("count(/root/paragraph[2]/content/*) = 1", XPathUtil.evaluateAsString(document,"count(/root/paragraph[2]/content/*)").equals("1"));

        assertTrue("name(/root/paragraph[2]/content/*[1]) = 'input'", XPathUtil.evaluateAsString(document,"name(/root/paragraph[2]/content/*[1])").toString().equals("input"));
        assertTrue("/root/paragraph[2]/content/input/@id = 'i-2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/input/@id").toString().equals("i-2"));
        assertTrue("/root/paragraph[2]/content/input/text()[1] = 'input 2'", XPathUtil.evaluateAsString(document,"/root/paragraph[2]/content/input/text()[1]").toString().equals("input 2"));
        assertTrue("count(/root/paragraph[2]/content/input/*) = 0", XPathUtil.evaluateAsString(document,"count(/root/paragraph[2]/content/input/*)").equals("0"));
    }

    /**
     * Test case 2 for relevance selection of submitted instance data.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testSelectRelevant2() throws Exception {
        Instance instance = this.xformsProcesssorImpl.getContainer().getDefaultModel().getDefaultInstance();
        Document document = RelevanceSelector.selectRelevant(instance, "/root/paragraph");
        document.normalize();

        assertTrue("name(/*[1]) = 'paragraph'", XPathUtil.evaluateAsString(document,"name(/*[1])").equals("paragraph"));
        assertTrue("namespace-uri(/paragraph) = ''", XPathUtil.evaluateAsString(document,"namespace-uri(/paragraph)").equals(""));
        assertTrue("/paragraph/@id = 'p-1'", XPathUtil.evaluateAsString(document,"/paragraph/@id").equals("p-1"));
        assertTrue("count(/paragraph/*) = 1", XPathUtil.evaluateAsString(document,"count(/paragraph/*)").equals("1"));

        assertTrue("name(/paragraph/*[1]) = 'content'", XPathUtil.evaluateAsString(document,"name(/paragraph/*[1])").equals("content"));
        assertTrue("/paragraph/content/@id = 'c-1'", XPathUtil.evaluateAsString(document,"/paragraph/content/@id").equals("c-1"));
        assertTrue("/paragraph/content/text()[1] = 'some mixed content 1'", XPathUtil.evaluateAsString(document,"/paragraph/content/text()[1]").replaceAll("[\n\r]", "").trim().equals("some mixed content 1"));
        assertTrue("/paragraph/content/text()[2] = 'some more mixed content 1'", XPathUtil.evaluateAsString(document,"/paragraph/content/text()[2]").replaceAll("[\n\r]", "").trim().equals("some more mixed content 1"));
        assertTrue("count(/paragraph/content/*) = 1", XPathUtil.evaluateAsString(document,"count(/paragraph/content/*)").equals("1"));

        assertTrue("name(/paragraph/content/*[1]) = 'input'", XPathUtil.evaluateAsString(document,"name(/paragraph/content/*[1])").equals("input"));
        assertTrue("/paragraph/content/input/@id = 'i-1'", XPathUtil.evaluateAsString(document,"/paragraph/content/input/@id").equals("i-1"));
        assertTrue("/paragraph/content/input/text()[1] = 'input 1'", XPathUtil.evaluateAsString(document,"/paragraph/content/input/text()[1]").equals("input 1"));
        assertTrue("count(/paragraph/content/input/*) = 0", XPathUtil.evaluateAsString(document,"count(/paragraph/content/input/*)").equals("0"));
    }



}
