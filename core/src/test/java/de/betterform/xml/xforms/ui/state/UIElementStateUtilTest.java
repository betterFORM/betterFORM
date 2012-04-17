package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import org.apache.xerces.dom.DOMInputImpl;
import org.apache.xerces.xs.XSImplementation;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.LSInput;

import java.io.File;
import java.io.FileInputStream;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 13.04.12
 * Time: 16:52
 */
public class UIElementStateUtilTest extends BetterFormTestCase {
    String schemaFile = "myschema.xsd";
    XSModel schema;

    protected void setUp() throws Exception {
        super.setUp();

        LSInput input = new DOMInputImpl();
        FileInputStream schemaStream = new FileInputStream(new File(getClass().getResource(schemaFile).getPath()));
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        XSImplementation implementation = (XSImplementation) registry.getDOMImplementation("XS-Loader");
        XSLoader loader = implementation.createXSLoader(null);
        input.setByteStream(schemaStream);
        schema = loader.load(input);
        schemaStream.close();
    }


    protected void tearDown() throws Exception {
        schema = null;
        super.tearDown();
    }


    public void testGetBaseTypeWithoutForm() {
        assertEquals("string", UIElementStateUtil.getBaseType(schema, "login", "http://example.org/myschema/1.0"));
    }

    public void testGetBaseTypeWithForm() throws Exception {
        assertEquals("string", UIElementStateUtil.getBaseType(getDefaultModel().getSchemas(), "XPathExpression", "http://www.w3.org/2002/xforms"));
        assertEquals("string", UIElementStateUtil.getBaseType(getDefaultModel().getSchemas(), "card-number", "http://www.w3.org/2002/xforms"));
        assertEquals("string", UIElementStateUtil.getBaseType(getDefaultModel().getSchemas(), "string", "http://www.w3.org/2002/xforms"));
        assertEquals("", UIElementStateUtil.getBaseType(getDefaultModel().getSchemas(), "blabla", "http://www.w3.org/2002/xforms"));
    }

    protected String getTestCaseURI() {
        return "UIElementStateUtilTest.xhtml";
    }

    @Override
    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
