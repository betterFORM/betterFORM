/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.xml.xpath;

import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.SaxonReferenceFinderImpl;
import junit.framework.TestCase;

import java.util.Collections;
import java.util.Set;

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
        assertReferences(new String[]{"child::data"},
                this.referenceFinder.getReferences("data", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::observation/child::data", "child::observation"},
                this.referenceFinder.getReferences("observation/data", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::observation", "child::observation/child::data"},
                this.referenceFinder.getReferences("observation[data != '']", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::observation", "child::observation/child::data"},
                this.referenceFinder.getReferences("count(observation[data != '']) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{".."},
                this.referenceFinder.getReferences("..", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"..", "../child::data"},
                this.referenceFinder.getReferences("../data", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::f", "child::f/child::g"},
                this.referenceFinder.getReferences("f[g != '']", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::observation[child::data!=\"\"]/child::code", "child::observation", "child::observation/child::data"},
                this.referenceFinder.getReferences("observation[data != '']/code", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::observation[child::data!=\"\"]/child::code", "child::observation", "child::observation/child::data"},
                this.referenceFinder.getReferences("count(observation[data != '']/code) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::observation", "/child::data", "child::observation[/child::data!=\"\"]/child::code"},
                this.referenceFinder.getReferences("count(observation[/data != '']/code) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"/child::observation[/child::data!=\"\"]/child::code", "/child::observation", "/child::data"},
                this.referenceFinder.getReferences("count(/observation[/data != '']/code) > 0", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{},
                this.referenceFinder.getReferences("current()", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"child::a", "child::b"},
                this.referenceFinder.getReferences("a * b", Collections.EMPTY_MAP, fDummyContainer));
        assertReferences(new String[]{"../child::item", ".."},
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
        assertReferences(new String[]{"instance(\"test\")/child::observation", "instance(\"test\")/child::observation/child::data", "instance(\"test\")"},
                this.referenceFinder.getReferences("count(instance(\"test\")/observation[data != '']) > 0", Collections.EMPTY_MAP, fDummyContainer));
        /*
            This test failed as reference finder returns xs:anySimpleType instead of xs:anyAtomicType as assumed by the assertion.

            Now it seems that the assertion here was likely incorrect and/or the behavior of Saxon has changed in version 9. When you see definitions for the above types a xs:anyAtomicType is the superclass for all atomic values (meaning any value that is
            atomic but not list types for instance). For the simple expression of an attribute as in this test below the test assumes to get back an xs:anyAtomicType but gets a xs:anySimpleType. But the latter seems
            to be more correct as attributes surely can hold list types as values e.g. thinking of the HTML class attribute that can hold a list of atomic values.

            As a consequence i adapt the test assertion to expect a xs:anySimpleType now and it runs green. Actually for the processing in betterFORM the type of the attribute value should be of no importance
            at all so the test is just too restrictive here. Anyway i leave the original assertion here for reference and investigation for the unlikely case that problems should come up later on.
         */
        // review if test is valid and can be fixed
//        assertReferences(new String[]{"instance(\"test\")/child::observation", "instance(\"list\")/attribute::attribute(data, xs:anyAtomicType)", "instance(\"list\")", "instance(\"test\")/child::observation/child::data", "instance(\"test\")"},
//                this.referenceFinder.getReferences("count(instance(\"test\")/observation[data != instance('list')/@data]) > 0", Collections.EMPTY_MAP, fDummyContainer));

        assertReferences(new String[]{"instance(\"test\")/child::observation", "instance(\"list\")/attribute::data", "instance(\"list\")", "instance(\"test\")/child::observation/child::data", "instance(\"test\")"},
                this.referenceFinder.getReferences("count(instance(\"test\")/observation[data != instance('list')/@data]) > 0", Collections.EMPTY_MAP, fDummyContainer));


        assertReferences(new String[]{"instance(\"editModes\")", "child::consumerId", "instance(\"version-data\")", "instance(\"editModes\")/child::editable", "instance(\"version-data\")/child::selectedVersion"},
                this.referenceFinder.getReferences("concat(concat('sampleConsentQuestionnaireWorkflow?consumerId=', consumerId), IF(instance('version-data')/selectedVersion != '', concat('&versionNumber=', instance('version-data')/selectedVersion), ''), '&editable=', instance('editModes')/editable)", Collections.EMPTY_MAP, fDummyContainer));
        // review if test is valid and can be fixed
//        assertReferences(new String[]{"instance(\"y\")/child::node", "instance(\"z\")", "child::element", "../child::no", "child::data", "..", "instance(\"x\")", "instance(\"y\")", "child::element/attribute::attribute(id, xs:anyAtomicType)", "(../child::no)/child::data"},
//                this.referenceFinder.getReferences("concat(instance('x'), data or ../no/data, instance('y')/node, element[@id = instance('z')])", Collections.EMPTY_MAP, fDummyContainer));

        assertReferences(new String[]{"instance(\"y\")/child::node", "instance(\"z\")", "child::element", "../child::no", "child::data", "..", "instance(\"x\")", "instance(\"y\")", "child::element/attribute::id", "../child::no/child::data"},
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
