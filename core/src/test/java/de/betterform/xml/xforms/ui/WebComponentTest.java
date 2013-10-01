package de.betterform.xml.xforms.ui;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.BetterFormTestCase;
import de.betterform.xml.xforms.XMLTestBase;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;

/**
 * Created with IntelliJ IDEA.
 * User: joern
 * Date: 01.10.13
 * Time: 11:48
 * To change this template use File | Settings | File Templates.
 */
public class WebComponentTest extends BetterFormTestCase {

    public void testInit() throws Exception{
        DOMUtil.prettyPrintDOM(this.processor.getXForms());
    }

    @Override
    protected String getTestCaseURI() {
        return "xf-component-hello.xhtml";
    }

    @Override
    protected XPathFunctionContext getDefaultFunctionContext() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
