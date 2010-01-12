// Copyright 2010 betterForm
/*
 *    Artistic License
 *
 *    Preamble
 *
 *    The intent of this document is to state the conditions under which a
 *    Package may be copied, such that the Copyright Holder maintains some
 *    semblance of artistic control over the development of the package,
 *    while giving the users of the package the right to use and distribute
 *    the Package in a more-or-less customary fashion, plus the right to make
 *    reasonable modifications.
 *
 *    Definitions:
 *
 *    "Package" refers to the collection of files distributed by the
 *    Copyright Holder, and derivatives of that collection of files created
 *    through textual modification.
 *
 *    "Standard Version" refers to such a Package if it has not been
 *    modified, or has been modified in accordance with the wishes of the
 *    Copyright Holder.
 *
 *    "Copyright Holder" is whoever is named in the copyright or copyrights
 *    for the package.
 *
 *    "You" is you, if you're thinking about copying or distributing this Package.
 *
 *    "Reasonable copying fee" is whatever you can justify on the basis of
 *    media cost, duplication charges, time of people involved, and so
 *    on. (You will not be required to justify it to the Copyright Holder,
 *    but only to the computing community at large as a market that must bear
 *    the fee.)
 *
 *    "Freely Available" means that no fee is charged for the item itself,
 *    though there may be fees involved in handling the item. It also means
 *    that recipients of the item may redistribute it under the same
 *    conditions they received it.
 *
 *    1. You may make and give away verbatim copies of the source form of the
 *    Standard Version of this Package without restriction, provided that you
 *    duplicate all of the original copyright notices and associated
 *    disclaimers.
 *
 *    2. You may apply bug fixes, portability fixes and other modifications
 *    derived from the Public Domain or from the Copyright Holder. A Package
 *    modified in such a way shall still be considered the Standard Version.
 *
 *    3. You may otherwise modify your copy of this Package in any way,
 *    provided that you insert a prominent notice in each changed file
 *    stating how and when you changed that file, and provided that you do at
 *    least ONE of the following:
 *
 *        a) place your modifications in the Public Domain or otherwise make
 *        them Freely Available, such as by posting said modifications to
 *        Usenet or an equivalent medium, or placing the modifications on a
 *        major archive site such as ftp.uu.net, or by allowing the Copyright
 *        Holder to include your modifications in the Standard Version of the
 *        Package.
 *
 *        b) use the modified Package only within your corporation or
 *        organization.
 *
 *        c) rename any non-standard executables so the names do not conflict
 *        with standard executables, which must also be provided, and provide
 *        a separate manual page for each non-standard executable that
 *        clearly documents how it differs from the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    4. You may distribute the programs of this Package in object code or
 *    executable form, provided that you do at least ONE of the following:
 *
 *        a) distribute a Standard Version of the executables and library
 *        files, together with instructions (in the manual page or
 *        equivalent) on where to get the Standard Version.
 *
 *        b) accompany the distribution with the machine-readable source of
 *        the Package with your modifications.
 *
 *        c) accompany any non-standard executables with their corresponding
 *        Standard Version executables, giving the non-standard executables
 *        non-standard names, and clearly documenting the differences in
 *        manual pages (or equivalent), together with instructions on where
 *        to get the Standard Version.
 *
 *        d) make other distribution arrangements with the Copyright Holder.
 *
 *    5. You may charge a reasonable copying fee for any distribution of this
 *    Package. You may charge any fee you choose for support of this
 *    Package. You may not charge a fee for this Package itself.  However,
 *    you may distribute this Package in aggregate with other (possibly
 *    commercial) programs as part of a larger (possibly commercial) software
 *    distribution provided that you do not advertise this Package as a
 *    product of your own.
 *
 *    6. The scripts and library files supplied as input to or produced as
 *    output from the programs of this Package do not automatically fall
 *    under the copyright of this Package, but belong to whomever generated
 *    them, and may be sold commercially, and may be aggregated with this
 *    Package.
 *
 *    7. C or perl subroutines supplied by you and linked into this Package
 *    shall not be considered part of this Package.
 *
 *    8. The name of the Copyright Holder may not be used to endorse or
 *    promote products derived from this software without specific prior
 *    written permission.
 *
 *    9. THIS PACKAGE IS PROVIDED "AS IS" AND WITHOUT ANY EXPRESS OR IMPLIED
 *    WARRANTIES, INCLUDING, WITHOUT LIMITATION, THE IMPLIED WARRANTIES OF
 *    MERCHANTIBILITY AND FITNESS FOR A PARTICULAR PURPOSE.
 *
 */
package de.betterform.xml.xpath;

import java.util.Collections;
import java.util.Set;

import junit.framework.TestCase;

import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.SaxonReferenceFinderImpl;

/**
 * Tests reference detection.
 *
 * @author Adrian Baker
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XPathReferenceFinderTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class XPathReferenceFinderTest extends TestCase {

    /**
     * 
     */
    private static final XPathFunctionContext kDUMMY_XPATH_FUNCTION_CONTEXT = new XPathFunctionContext(null);
    private XPathReferenceFinder referenceFinder;
    private Container fDummyContainer = new Container(null);

    /**
     * Tests reference detection for fairly simple expressions.
     *
     * @throws Exception if an error occurred during the test.
     */
    public void testGetReferences() throws Exception {
        assertReferences(new String[]{"child::element(data, xs:anyType)"},
                this.referenceFinder.getReferences("data", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::element(observation, xs:anyType)/child::element(data, xs:anyType)", "child::element(observation, xs:anyType)"},
                this.referenceFinder.getReferences("observation/data", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::element(observation, xs:anyType)", "child::element(observation, xs:anyType)/child::element(data, xs:anyType)"},
                this.referenceFinder.getReferences("observation[data != '']", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::element(observation, xs:anyType)", "child::element(observation, xs:anyType)/child::element(data, xs:anyType)"},
                this.referenceFinder.getReferences("count(observation[data != '']) > 0", Collections.EMPTY_MAP, fDummyContainer));
	assertReferences(new String[]{".."},
	              this.referenceFinder.getReferences("..", Collections.EMPTY_MAP, fDummyContainer));
	assertReferences(new String[]{"..", "../child::element(data, xs:anyType)"},
              this.referenceFinder.getReferences("../data", Collections.EMPTY_MAP, fDummyContainer));
	assertReferences(new String[]{"child::element(f, xs:anyType)", "child::element(f, xs:anyType)/child::element(g, xs:anyType)"},
	              this.referenceFinder.getReferences("f[g != '']", Collections.EMPTY_MAP, fDummyContainer));
	assertReferences(new String[]{"(child::element(observation, xs:anyType)[(child::element(data, xs:anyType)!=\"\")])/child::element(code, xs:anyType)", "child::element(observation, xs:anyType)", "child::element(observation, xs:anyType)/child::element(data, xs:anyType)"},
                this.referenceFinder.getReferences("observation[data != '']/code", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"(child::element(observation, xs:anyType)[(child::element(data, xs:anyType)!=\"\")])/child::element(code, xs:anyType)", "child::element(observation, xs:anyType)", "child::element(observation, xs:anyType)/child::element(data, xs:anyType)"},
                this.referenceFinder.getReferences("count(observation[data != '']/code) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::element(observation, xs:anyType)", "/child::element(data, xs:anyType)", "(child::element(observation, xs:anyType)[((/child::element(data, xs:anyType))!=\"\")])/child::element(code, xs:anyType)"},
                this.referenceFinder.getReferences("count(observation[/data != '']/code) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"(/(child::element(observation, xs:anyType)[((/child::element(data, xs:anyType))!=\"\")]))/child::element(code, xs:anyType)", "/child::element(observation, xs:anyType)", "/child::element(data, xs:anyType)"},
                this.referenceFinder.getReferences("count(/observation[/data != '']/code) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{},
                this.referenceFinder.getReferences("current()", Collections.EMPTY_MAP, fDummyContainer));
	assertReferences(new String[]{"child::element(a, xs:anyType)", "child::element(b, xs:anyType)"},
		this.referenceFinder.getReferences("a * b", Collections.EMPTY_MAP, fDummyContainer));
	assertReferences(new String[]{"../child::element(item, xs:anyType)", ".."},
		this.referenceFinder.getReferences("IF(index('repeat') > 0, ../item[index('repeat')], '')", Collections.EMPTY_MAP, fDummyContainer));
	
    }

    /**
     * Tests reference detection for expressions with instance() function.
     *
     * @throws Exception if an error occurred during the test.
     */
    public void testGetReferencesWithInstanceFunction() throws Exception {
        assertReferences(new String[]{"instance(\"test\")"},
                this.referenceFinder.getReferences("instance(\"test\")", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"instance(\"test\")"},
                this.referenceFinder.getReferences("count(instance(\"test\"))", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"instance(\"test\")/child::element(observation, xs:anyType)", "instance(\"test\")/child::element(observation, xs:anyType)/child::element(data, xs:anyType)", "instance(\"test\")"},
                this.referenceFinder.getReferences("count(instance(\"test\")/observation[data != '']) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"instance(\"test\")/child::element(observation, xs:anyType)", "instance(\"list\")/attribute::attribute(data, xs:anyAtomicType)", "instance(\"list\")", "instance(\"test\")/child::element(observation, xs:anyType)/child::element(data, xs:anyType)", "instance(\"test\")"},
                this.referenceFinder.getReferences("count(instance(\"test\")/observation[data != instance('list')/@data]) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"instance(\"editModes\")", "child::element(consumerId, xs:anyType)", "instance(\"version-data\")", "instance(\"editModes\")/child::element(editable, xs:anyType)", "instance(\"version-data\")/child::element(selectedVersion, xs:anyType)"},
                this.referenceFinder.getReferences("concat(concat('sampleConsentQuestionnaireWorkflow?consumerId=', consumerId), IF(instance('version-data')/selectedVersion != '', concat('&versionNumber=', instance('version-data')/selectedVersion), ''), '&editable=', instance('editModes')/editable)", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"instance(\"y\")/child::element(node, xs:anyType)", "instance(\"z\")", "child::element(element, xs:anyType)", "../child::element(no, xs:anyType)", "child::element(data, xs:anyType)", "..", "instance(\"x\")", "instance(\"y\")", "child::element(element, xs:anyType)/attribute::attribute(id, xs:anyAtomicType)", "(../child::element(no, xs:anyType))/child::element(data, xs:anyType)"},
                this.referenceFinder.getReferences("concat(instance('x'), data or ../no/data, instance('y')/node, element[@id = instance('z')])", Collections.EMPTY_MAP, fDummyContainer));
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.referenceFinder = new SaxonReferenceFinderImpl();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.referenceFinder = null;
    }

    private void assertReferences(String[] expected, Set actual) throws Exception {
        if (expected == null) {
            assertEquals(null, actual);
            return;
        }

        assertEquals("number of references:", expected.length, actual.size());
        for (int i = 0; i < expected.length; i++) {
            assertEquals("reference to " + expected[i] + ":", true, actual.contains(expected[i]));
        }
    }

}
