// Copyright 2010 betterForm
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
package de.betterform.xml.xforms.xpath;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Tests betterform extension functions.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: BetterFormExtensionFunctionsTest.java 3492 2008-08-27 12:37:01Z joern $
 */
public class BetterFormExtensionFunctionsTest extends BetterFormTestCase {


    public void testContextNew() throws Exception {
//        assertEquals("hello", XPathUtil.evaluateAsString(processor.getXForms(),"bffn:foo()"));
        assertEquals("bar", evaluateInDefaultContextAsString("string(bffn:app-context('foo'))"));
        assertEquals("bar", evaluateInDefaultContextAsString("string(/data/item[1])"));
    }

    /**
     * Tests the bf:context() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContext() throws Exception {
        assertEquals("", evaluateInDefaultContextAsString("string(bffn:app-context('plain-property'))"));
        assertEquals("default", evaluateInDefaultContextAsString("string(bffn:app-context('plain-property', 'default'))"));
        assertEquals("default", evaluateInDefaultContextAsString("string(bffn:app-context('plain-property', /data/default))"));

        String plainProperty = String.valueOf(System.currentTimeMillis());
        this.processor.setContextParam("plain-property", plainProperty);

        assertEquals(plainProperty, evaluateInDefaultContextAsString("string(bffn:app-context('plain-property'))"));
        assertEquals("bar", evaluateInDefaultContextAsString("string(/data/item[1])"));

        getDefaultModel().rebuild();
        getDefaultModel().recalculate();

        assertEquals(plainProperty, evaluateInDefaultContextAsString("string(bffn:app-context('plain-property'))"));
        assertEquals(plainProperty, evaluateInDefaultContextAsString("string(/data/item[2])"));
    }

    /**
     * Tests the bf:context() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextNested() throws Exception {
        assertEquals("", evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property'))"));
        assertEquals("default", evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property', 'default'))"));
        assertEquals("default", evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property', /data/default))"));

        String nestedProperty = String.valueOf(System.currentTimeMillis());
        HashMap map = new HashMap();
        map.put("nested-property", nestedProperty);
        this.processor.setContextParam("map", map);

        assertEquals(nestedProperty, evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property'))"));
        assertEquals("", evaluateInDefaultContextAsString("string(/data/item[3])"));

         getDefaultModel().rebuild();
         getDefaultModel().recalculate();

        assertEquals(nestedProperty, evaluateInDefaultContextAsString("string(bffn:app-context('map/nested-property'))"));
        assertEquals(nestedProperty, evaluateInDefaultContextAsString("string(/data/item[3])"));
    }

    /**
     * Tests the bf:context() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testContextNodeset() throws Exception {
        String xml = "<?xml version='1.0' encoding='UTF-8'?><data><item>test</item></data>";
        InputStream stream = new ByteArrayInputStream(xml.getBytes("UTF-8"));
        Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
        document.getDocumentElement().getFirstChild().getFirstChild().getFirstChild();
        stream.close();
        this.processor.setContextParam("document", document);

        assertEquals("test", evaluateInDefaultContextAsString("string(bffn:app-context('document')/data/item)"));
        assertEquals("", evaluateInDefaultContextAsString("string(/data/item[4])"));

        getDefaultModel().rebuild();
        getDefaultModel().recalculate();

        assertEquals("test", evaluateInDefaultContextAsString("string(bffn:app-context('document')/data/item)"));
        assertEquals("test", evaluateInDefaultContextAsString("string(/data/item[4])"));
    }

    /**
     * Tests the bf:match() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testMatch() throws Exception {
        assertEquals("true", evaluateInDocumentContextAsString("string(//xf:output[@id='i-match1']/bf:data)"));
        assertEquals("false", evaluateInDocumentContextAsString("string(//xf:output[@id='i-match2']/bf:data)"));
        assertEquals("true", evaluateInDocumentContextAsString("string(//xf:output[@id='i-match3']/bf:data)"));
    }


    /**
     * Tests the bf:fileSize() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testFilesize() throws Exception {
        assertEquals(getClass().getResource("BetterFormExtensionFunctionsTest.xhtml").openConnection().getContentLength(), Long.parseLong(evaluateInDocumentContextAsString("string(//xf:output[@id='i-filesize']/bf:data/@bf:schema-value)")));
    }

    /**
     * Tests the bf:fileDate() extension function.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testFileChangedate() throws Exception {
        assertEquals(new Date(getClass().getResource("BetterFormExtensionFunctionsTest.xhtml").openConnection().getLastModified()).toString(), new SimpleDateFormat("dd.MM.yyyy H:m:s").parse(evaluateInDocumentContextAsString("string(//xf:output[@id='i-filedate']/bf:data)")).toString());
    }


    protected void preInit() {
        processor.setContextParam("foo", "bar"); //sample app-context param

        processor.setBaseURI(getClass().getResource("BetterFormExtensionFunctionsTest.xhtml").toString());
    }


    protected XPathFunctionContext getDefaultFunctionContext() {
        try {
            return new XPathFunctionContext(getDefaultModel().getDefaultInstance());
        } catch (XFormsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return null;
    }


    protected String getTestCaseURI() {
        return "BetterFormExtensionFunctionsTest.xhtml";
    }
}

// end of class
