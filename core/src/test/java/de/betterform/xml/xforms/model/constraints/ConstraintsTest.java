/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
package de.betterform.xml.xforms.model.constraints;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

/**
 * Unit tests for constraints. Currently only covers testing of a constraint
 * with a predicate.
 *
 * @author Adrian Baker
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ConstraintsTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class ConstraintsTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.xml.DOMConfigurator.configure("/Users/uli/Development/IdeaProjects/betterform-sandbox/build/log4j.xml");
//    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /**
     * Tests constraints with a predicate.
     *
     * @throws XFormsException if any error occurred during the test.
     */
    public void testConstraintWithPredicate() throws XFormsException {
        Model model = getDefaultModel();
        assertEquals(false, model.getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/e")).getLocalUpdateView().isConstraintValid());

        model.getDefaultInstance().setNodeValue(evaluateInDefaultContextAsNode("/data/e/f[1]/g"), "test");
        model.recalculate();
        model.revalidate();
        assertEquals(true, model.getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/e")).getLocalUpdateView().isConstraintValid());
    }

    /**
     * Tests constraints with a predicate.
     *
     * @throws XFormsException if any error occurred during the test.
     */
    public void testConstraintWithPredicateAndInstanceFunction() throws XFormsException {
        Model model = getDefaultModel();
        assertEquals(true, model.getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/h[1]")).getLocalUpdateView().isConstraintValid());
        assertEquals(true, model.getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/h[2]")).getLocalUpdateView().isConstraintValid());
        assertEquals(false, model.getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/h[3]")).getLocalUpdateView().isConstraintValid());

        model.getInstance("instance-2").setNodeValue(evaluateInInstanceAsNode("instance-2", "/data/item"), "15");
        model.recalculate();
        model.revalidate();
        assertEquals(false, model.getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/h[1]")).getLocalUpdateView().isConstraintValid());
        assertEquals(false, model.getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/h[2]")).getLocalUpdateView().isConstraintValid());
        assertEquals(true, model.getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/h[3]")).getLocalUpdateView().isConstraintValid());
    }


    protected String getTestCaseURI() {
        return "ConstraintsTest.xhtml";
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }
}
