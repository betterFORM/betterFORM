/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.model.constraints.Validator;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

/**
 * Validator test cases.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: ValidatorTest.java 3264 2008-07-16 12:26:54Z joern $
 */
public class ValidatorTest extends BetterFormTestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    /**
     * Tests loaded datatypes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsKnown() throws Exception {
        Model model = (Model) this.processor.getXFormsModel("model-1");
        Validator validator = model.getValidator();

        // assert schema primitive datatypes
        assertEquals(true, validator.isKnown("string"));
        assertEquals(true, validator.isKnown("boolean"));
        assertEquals(true, validator.isKnown("decimal"));
        assertEquals(true, validator.isKnown("float"));
        assertEquals(true, validator.isKnown("double"));
        assertEquals(false, validator.isKnown("duration"));
        assertEquals(true, validator.isKnown("dateTime"));
        assertEquals(true, validator.isKnown("time"));
        assertEquals(true, validator.isKnown("date"));
        assertEquals(true, validator.isKnown("gYearMonth"));
        assertEquals(true, validator.isKnown("gYear"));
        assertEquals(true, validator.isKnown("gMonthDay"));
        assertEquals(true, validator.isKnown("gDay"));
        assertEquals(true, validator.isKnown("gMonth"));
        assertEquals(true, validator.isKnown("base64Binary"));
        assertEquals(true, validator.isKnown("hexBinary"));
        assertEquals(true, validator.isKnown("anyURI"));
        assertEquals(true, validator.isKnown("QName"));
        assertEquals(false, validator.isKnown("NOTATION"));
        assertEquals(true, validator.isKnown("xs:string"));
        assertEquals(true, validator.isKnown("xs:boolean"));
        assertEquals(true, validator.isKnown("xs:decimal"));
        assertEquals(true, validator.isKnown("xs:float"));
        assertEquals(true, validator.isKnown("xs:double"));
        assertEquals(false, validator.isKnown("xs:duration"));
        assertEquals(true, validator.isKnown("xs:dateTime"));
        assertEquals(true, validator.isKnown("xs:time"));
        assertEquals(true, validator.isKnown("xs:date"));
        assertEquals(true, validator.isKnown("xs:gYearMonth"));
        assertEquals(true, validator.isKnown("xs:gYear"));
        assertEquals(true, validator.isKnown("xs:gMonthDay"));
        assertEquals(true, validator.isKnown("xs:gDay"));
        assertEquals(true, validator.isKnown("xs:gMonth"));
        assertEquals(true, validator.isKnown("xs:base64Binary"));
        assertEquals(true, validator.isKnown("xs:hexBinary"));
        assertEquals(true, validator.isKnown("xs:anyURI"));
        assertEquals(true, validator.isKnown("xs:QName"));
        assertEquals(false, validator.isKnown("xs:NOTATION"));

        // assert schema derived datatypes
        assertEquals(true, validator.isKnown("normalizedString"));
        assertEquals(true, validator.isKnown("token"));
        assertEquals(true, validator.isKnown("language"));
        assertEquals(true, validator.isKnown("NMTOKEN"));
        assertEquals(true, validator.isKnown("NMTOKENS"));
        assertEquals(true, validator.isKnown("Name"));
        assertEquals(true, validator.isKnown("NCName"));
        assertEquals(true, validator.isKnown("ID"));
        assertEquals(true, validator.isKnown("IDREF"));
        assertEquals(true, validator.isKnown("IDREFS"));
        assertEquals(false, validator.isKnown("ENTITY"));
        assertEquals(false, validator.isKnown("ENTITIES"));
        assertEquals(true, validator.isKnown("integer"));
        assertEquals(true, validator.isKnown("nonPositiveInteger"));
        assertEquals(true, validator.isKnown("negativeInteger"));
        assertEquals(true, validator.isKnown("long"));
        assertEquals(true, validator.isKnown("int"));
        assertEquals(true, validator.isKnown("short"));
        assertEquals(true, validator.isKnown("byte"));
        assertEquals(true, validator.isKnown("nonNegativeInteger"));
        assertEquals(true, validator.isKnown("unsignedLong"));
        assertEquals(true, validator.isKnown("unsignedInt"));
        assertEquals(true, validator.isKnown("unsignedShort"));
        assertEquals(true, validator.isKnown("unsignedByte"));
        assertEquals(true, validator.isKnown("positiveInteger"));
        assertEquals(true, validator.isKnown("xs:normalizedString"));
        assertEquals(true, validator.isKnown("xs:token"));
        assertEquals(true, validator.isKnown("xs:language"));
        assertEquals(true, validator.isKnown("xs:NMTOKEN"));
        assertEquals(true, validator.isKnown("xs:NMTOKENS"));
        assertEquals(true, validator.isKnown("xs:Name"));
        assertEquals(true, validator.isKnown("xs:NCName"));
        assertEquals(true, validator.isKnown("xs:ID"));
        assertEquals(true, validator.isKnown("xs:IDREF"));
        assertEquals(true, validator.isKnown("xs:IDREFS"));
        assertEquals(false, validator.isKnown("xs:ENTITY"));
        assertEquals(false, validator.isKnown("xs:ENTITIES"));
        assertEquals(true, validator.isKnown("xs:integer"));
        assertEquals(true, validator.isKnown("xs:nonPositiveInteger"));
        assertEquals(true, validator.isKnown("xs:negativeInteger"));
        assertEquals(true, validator.isKnown("xs:long"));
        assertEquals(true, validator.isKnown("xs:int"));
        assertEquals(true, validator.isKnown("xs:short"));
        assertEquals(true, validator.isKnown("xs:byte"));
        assertEquals(true, validator.isKnown("xs:nonNegativeInteger"));
        assertEquals(true, validator.isKnown("xs:unsignedLong"));
        assertEquals(true, validator.isKnown("xs:unsignedInt"));
        assertEquals(true, validator.isKnown("xs:unsignedShort"));
        assertEquals(true, validator.isKnown("xs:unsignedByte"));
        assertEquals(true, validator.isKnown("xs:positiveInteger"));

        // assert xforms derived datatypes
        assertEquals(true, validator.isKnown("listItem"));
        assertEquals(true, validator.isKnown("listItems"));
        assertEquals(true, validator.isKnown("dayTimeDuration"));
        assertEquals(true, validator.isKnown("yearMonthDuration"));
        assertEquals(true, validator.isKnown("xf:listItem"));
        assertEquals(true, validator.isKnown("xf:listItems"));
        assertEquals(true, validator.isKnown("xf:dayTimeDuration"));
        assertEquals(true, validator.isKnown("xf:yearMonthDuration"));
    }

    /**
     * Tests supported datatypes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsSupported() throws Exception {
        Model model = (Model) this.processor.getXFormsModel("model-1");
        Validator validator = model.getValidator();

        // assert schema primitive datatypes
        assertEquals(true, validator.isSupported("string"));
        assertEquals(true, validator.isSupported("boolean"));
        assertEquals(true, validator.isSupported("decimal"));
        assertEquals(true, validator.isSupported("float"));
        assertEquals(true, validator.isSupported("double"));
        assertEquals(false, validator.isSupported("duration"));
        assertEquals(true, validator.isSupported("dateTime"));
        assertEquals(true, validator.isSupported("time"));
        assertEquals(true, validator.isSupported("date"));
        assertEquals(true, validator.isSupported("gYearMonth"));
        assertEquals(true, validator.isSupported("gYear"));
        assertEquals(true, validator.isSupported("gMonthDay"));
        assertEquals(true, validator.isSupported("gDay"));
        assertEquals(true, validator.isSupported("gMonth"));
        assertEquals(true, validator.isSupported("base64Binary"));
        assertEquals(true, validator.isSupported("hexBinary"));
        assertEquals(true, validator.isSupported("anyURI"));
        assertEquals(true, validator.isSupported("QName"));
        assertEquals(false, validator.isSupported("NOTATION"));
        assertEquals(true, validator.isSupported("xs:string"));
        assertEquals(true, validator.isSupported("xs:boolean"));
        assertEquals(true, validator.isSupported("xs:decimal"));
        assertEquals(true, validator.isSupported("xs:float"));
        assertEquals(true, validator.isSupported("xs:double"));
        assertEquals(false, validator.isSupported("xs:duration"));
        assertEquals(true, validator.isSupported("xs:dateTime"));
        assertEquals(true, validator.isSupported("xs:time"));
        assertEquals(true, validator.isSupported("xs:date"));
        assertEquals(true, validator.isSupported("xs:gYearMonth"));
        assertEquals(true, validator.isSupported("xs:gYear"));
        assertEquals(true, validator.isSupported("xs:gMonthDay"));
        assertEquals(true, validator.isSupported("xs:gDay"));
        assertEquals(true, validator.isSupported("xs:gMonth"));
        assertEquals(true, validator.isSupported("xs:base64Binary"));
        assertEquals(true, validator.isSupported("xs:hexBinary"));
        assertEquals(true, validator.isSupported("xs:anyURI"));
        assertEquals(true, validator.isSupported("xs:QName"));
        assertEquals(false, validator.isSupported("xs:NOTATION"));

        // assert schema derived datatypes
        assertEquals(true, validator.isSupported("normalizedString"));
        assertEquals(true, validator.isSupported("token"));
        assertEquals(true, validator.isSupported("language"));
        assertEquals(true, validator.isSupported("NMTOKEN"));
        assertEquals(true, validator.isSupported("NMTOKENS"));
        assertEquals(true, validator.isSupported("Name"));
        assertEquals(true, validator.isSupported("NCName"));
        assertEquals(true, validator.isSupported("ID"));
        assertEquals(true, validator.isSupported("IDREF"));
        assertEquals(true, validator.isSupported("IDREFS"));
        assertEquals(false, validator.isSupported("ENTITY"));
        assertEquals(false, validator.isSupported("ENTITIES"));
        assertEquals(true, validator.isSupported("integer"));
        assertEquals(true, validator.isSupported("nonPositiveInteger"));
        assertEquals(true, validator.isSupported("negativeInteger"));
        assertEquals(true, validator.isSupported("long"));
        assertEquals(true, validator.isSupported("int"));
        assertEquals(true, validator.isSupported("short"));
        assertEquals(true, validator.isSupported("byte"));
        assertEquals(true, validator.isSupported("nonNegativeInteger"));
        assertEquals(true, validator.isSupported("unsignedLong"));
        assertEquals(true, validator.isSupported("unsignedInt"));
        assertEquals(true, validator.isSupported("unsignedShort"));
        assertEquals(true, validator.isSupported("unsignedByte"));
        assertEquals(true, validator.isSupported("positiveInteger"));
        assertEquals(true, validator.isSupported("xs:normalizedString"));
        assertEquals(true, validator.isSupported("xs:token"));
        assertEquals(true, validator.isSupported("xs:language"));
        assertEquals(true, validator.isSupported("xs:NMTOKEN"));
        assertEquals(true, validator.isSupported("xs:NMTOKENS"));
        assertEquals(true, validator.isSupported("xs:Name"));
        assertEquals(true, validator.isSupported("xs:NCName"));
        assertEquals(true, validator.isSupported("xs:ID"));
        assertEquals(true, validator.isSupported("xs:IDREF"));
        assertEquals(true, validator.isSupported("xs:IDREFS"));
        assertEquals(false, validator.isSupported("xs:ENTITY"));
        assertEquals(false, validator.isSupported("xs:ENTITIES"));
        assertEquals(true, validator.isSupported("xs:integer"));
        assertEquals(true, validator.isSupported("xs:nonPositiveInteger"));
        assertEquals(true, validator.isSupported("xs:negativeInteger"));
        assertEquals(true, validator.isSupported("xs:long"));
        assertEquals(true, validator.isSupported("xs:int"));
        assertEquals(true, validator.isSupported("xs:short"));
        assertEquals(true, validator.isSupported("xs:byte"));
        assertEquals(true, validator.isSupported("xs:nonNegativeInteger"));
        assertEquals(true, validator.isSupported("xs:unsignedLong"));
        assertEquals(true, validator.isSupported("xs:unsignedInt"));
        assertEquals(true, validator.isSupported("xs:unsignedShort"));
        assertEquals(true, validator.isSupported("xs:unsignedByte"));
        assertEquals(true, validator.isSupported("xs:positiveInteger"));

        // assert xforms derived datatypes
        assertEquals(true, validator.isSupported("listItem"));
        assertEquals(true, validator.isSupported("listItems"));
        assertEquals(true, validator.isSupported("dayTimeDuration"));
        assertEquals(true, validator.isSupported("yearMonthDuration"));
        assertEquals(true, validator.isSupported("xf:listItem"));
        assertEquals(true, validator.isSupported("xf:listItems"));
        assertEquals(true, validator.isSupported("xf:dayTimeDuration"));
        assertEquals(true, validator.isSupported("xf:yearMonthDuration"));
    }

    /**
     * Tests restricted datatypes.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testIsRestricted() throws Exception {
        Model model = (Model) this.processor.getXFormsModel("model-1");
        Validator validator = model.getValidator();

        // assert built-in restriction
        assertEquals(true, validator.isRestricted("decimal", "integer"));
        assertEquals(true, validator.isRestricted("decimal", "xs:integer"));
        assertEquals(true, validator.isRestricted("xs:decimal", "integer"));
        assertEquals(true, validator.isRestricted("xs:decimal", "xs:integer"));

        // assert self restriction

        assertEquals(true, validator.isRestricted("decimal", "decimal"));

        assertEquals(true, validator.isRestricted("decimal", "xs:decimal"));
        assertEquals(true, validator.isRestricted("xs:decimal", "decimal"));
        assertEquals(true, validator.isRestricted("xs:decimal", "xs:decimal"));
        assertEquals(true, validator.isRestricted("xs:decimal", "xf:decimal"));
        assertEquals(false, validator.isRestricted("xf:dateTime", "xs:dateTime"));
        assertEquals(true, validator.isRestricted("xs:dateTime", "xf:dateTime"));
        assertEquals(true, validator.isRestricted("xs:string", "xf:dateTime"));
        assertEquals(false, validator.isRestricted("xs:decimal", "xf:dateTime"));

        // assert user restriction
        assertEquals(true, validator.isRestricted("decimal", "test:restricted"));
        assertEquals(true, validator.isRestricted("xs:decimal", "test:restricted"));
    }

    /**
     * Test model item validation with no associated type.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidateNoType() throws Exception {
        ModelItem modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/item[1]"));
        Validator validator = getDefaultModel().getValidator();
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("-23");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4711");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4712");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("foobar");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());
    }

    /**
     * Test model item validation with an associated xforms type property.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidateXFormsType() throws Exception {
        ModelItem modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/item[2]"));
        Validator validator = getDefaultModel().getValidator();
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("-23");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4711");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4712");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("foobar");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());
    }

    /**
     * Test model item validation with an associated schema type.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidateSchemaType() throws Exception {
        ModelItem modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/item[3]"));
        Validator validator = getDefaultModel().getValidator();
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("-23");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4711");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4712");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("foobar");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("4711xx");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("xx4711");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("xx4711xx");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid()); 
    }

    /**
     * Test model item validation with both an associated xforms type property
     * and an associated schema type.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidateXFormsAndSchemaType() throws Exception {
        ModelItem modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/item[4]"));
        Validator validator = getDefaultModel().getValidator();
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("-23");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("4711");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4712");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("foobar");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());
    }

    /**
     * Test model item validation for a nillable type.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testValidateSchemaNillable() throws Exception {
        ModelItem modelItem = getDefaultModel().getDefaultInstance().getModelItem(evaluateInDefaultContextAsNode("/data/item[5]"));
        Validator validator = getDefaultModel().getValidator();
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("-23");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4711");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());

        modelItem.setValue("4712");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("foobar");
        validator.validate(modelItem);
        assertEquals(false, modelItem.isValid());

        modelItem.setValue("");
        validator.validate(modelItem);
        assertEquals(true, modelItem.isValid());
    }
    
    protected String getTestCaseURI() {
        return "ValidatorTest.xhtml";  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;
    }

}
