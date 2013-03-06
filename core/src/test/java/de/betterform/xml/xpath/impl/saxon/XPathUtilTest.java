package de.betterform.xml.xpath.impl.saxon;

import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.betterform.xml.xforms.XMLTestBase;

/**
 * 
 *
 */
public class XPathUtilTest extends XMLTestBase {

	public void testGetRootContext() throws Exception {
		
		Document xmlResource = getXmlResource("XpathUtilTest.xml");
		Element documentElement = xmlResource.getDocumentElement();
		List rootContext = XPathUtil.getRootContext(xmlResource, "");
		
	}
}
