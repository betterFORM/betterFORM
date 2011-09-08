<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    exclude-result-prefixes="bf xsl">

    <!-- Copyright 2010 - Joern Turner, Lars Windauer, Tobi Krebs, Fabian Otto
         Licensed under the terms of BSD and Apache 2 Licenses -->

    <xsl:param name="rootDir" select="'../../../../../../src/main/xforms'"/>
    <xsl:param name="filename">bogus</xsl:param>
    <xsl:param name="filedir">.</xsl:param>
    <xsl:param name="timeout">30</xsl:param>
    <!--
        generates .java files in junit-style from selenium html tests.
    -->
    <xsl:strip-space elements="*"/>
    <xsl:output name="javaText" method="text" />
    <xsl:variable name="defaulttimeout" select="'30000'"/>

    <xsl:variable name="testname" select="replace(//html:head/html:title/text(),'\.','_')"/>
    <xsl:variable name="classname" select="concat('XF_',$filedir,'_',$testname,'_Test')"/>
    <xsl:template match="/">
        <output>
            using:<xsl:value-of select="$testname"/>
            filedir:<xsl:value-of select="$filedir"/>
        <xsl:result-document format="javaText" href="{concat($classname,'.java')}">
package de.betterform.conformance.xf11.<xsl:value-of select="$filedir"/>;

import de.betterform.conformance.WebTestBase;
import com.thoughtworks.selenium.*;
import java.util.regex.Pattern;

public class <xsl:value-of select="$classname"/> extends WebTestBase {
    public void setUp() throws Exception {
        this.formURL = "<xsl:value-of select="concat($filedir,'/',$filename)"/>";
        super.setUp();
    }

    <!--public void test3.1.a() throws Exception {-->
	public void test<xsl:value-of select="$testname"/>() throws Exception {
        <xsl:apply-templates/>
    }
}
        </xsl:result-document>
        </output>
    </xsl:template>

<!--
    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>
-->

    <xsl:template match="html:tbody/*/html:td[1]">
        <xsl:message>WARNING WARNING WARNING WARNING WARNING</xsl:message>
        <xsl:message terminate="yes">Matcher for command '<xsl:value-of select="."/>' does not yet exist - Your testcase won't work.</xsl:message>
    </xsl:template>


    <xsl:variable name="quot">"</xsl:variable>
    <xsl:variable name="quote">\\"</xsl:variable>
    <xsl:template match="html:td[text()='assertAlert']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        assertEquals("<xsl:value-of select="$param1"/>", selenium.getAlert());
    </xsl:template>

    <xsl:template match="html:td[text()='assertAlertNotPresent']" priority="10">
        assertFalse(selenium.isAlertPresent());
    </xsl:template>

    <xsl:template match="html:td[text()='assertAttribute']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        assertEquals("<xsl:value-of select="$param2"/>", selenium.getAttribute("<xsl:value-of select="$param1"/>"));
    </xsl:template>

   <xsl:template match="html:td[text()='assertAlertPresent']" priority="10">
        assertTrue(selenium.isAlertPresent());
    </xsl:template>

    <xsl:template match="html:td[text()='addSelection']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        selenium.addSelection("<xsl:value-of select="$param1"/>", "<xsl:value-of select="$param2"/>");
    </xsl:template>


    <xsl:template match="html:td[text()='assertText']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>

        <xsl:choose>
            <xsl:when test="substring($param2,1,5) = 'regex'">
                assertTrue(Pattern.compile("<xsl:value-of select="replace(substring($param2,6), '\\','\\\\')"/>").matcher(selenium.getText("<xsl:value-of select="$param1"/>")).find());
            </xsl:when>
            <xsl:otherwise>
                assertEquals("<xsl:value-of select="$param2"/>", selenium.getText("<xsl:value-of select="$param1"/>"));
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template match="html:td[text()='assertNotText']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        assertNotEquals("<xsl:value-of select="$param2"/>", selenium.getText("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='assertTextNotPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        assertFalse(selenium.isTextPresent("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='assertTextPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        assertTrue(selenium.isTextPresent("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='assertTitle']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        assertEquals("<xsl:value-of select="$param1"/>", selenium.getTitle());
    </xsl:template>

    <xsl:template match="html:td[text()='assertVisible']" priority="10">
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        assertFalse(selenium.isVisible("<xsl:value-of select="$param2"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='click']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        selenium.click("<xsl:value-of select="$param1"/>");
    </xsl:template>

    <xsl:template match="html:td[text()='clickAndWait']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        selenium.click("<xsl:value-of select="$param1"/>");
        <!--<xsl:variable name="timeout" select="if(string-length(following-sibling::*[2])!=0) then following-sibling::*[2] else $defaulttimeout" />-->
        selenium.waitForPageToLoad("30000");
    </xsl:template>

    <xsl:template match="html:td[text()='echo']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        System.out.println("<xsl:value-of select="$param1"/>");
    </xsl:template>

    <xsl:template match="html:td[text()='fail']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        System.out.println("<xsl:value-of select="$param1"/>");
        fail("<xsl:value-of select="$param1"/>");
    </xsl:template>

    <xsl:template match="html:td[text()='mouseDown']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        selenium.mouseDown("<xsl:value-of select="$param1"/>");
    </xsl:template>

    <xsl:template match="html:td[text()='open']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        selenium.open("<xsl:value-of select="$param1"/>");
    </xsl:template>

    <xsl:template match="html:td[text()='pause']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        Thread.sleep(<xsl:value-of select="$param1"/>);        
    </xsl:template>

    <xsl:template match="html:td[text()='select']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        selenium.select("<xsl:value-of select="$param1"/>", "<xsl:value-of select="$param2"/>");
    </xsl:template>


    <xsl:template match="html:td[text()='type']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        selenium.type("<xsl:value-of select="$param1"/>","<xsl:value-of select="$param2"/>");
    </xsl:template>


    <xsl:template match="html:td[text()='verifyAlert']" priority="10">
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        selenium.click("id=popUpDiv");
        verifyEquals("<xsl:value-of select="$param2"/>", selenium.getAlert());
    </xsl:template>

    <xsl:template match="html:td[text()='verifyAlertPresent']" priority="10">
        verifyTrue(selenium.isAlertPresent());
    </xsl:template>

    <xsl:template match="html:td[text()='verifyAlertNotPresent']" priority="10">
        verifyFalse(selenium.isAlertPresent());
    </xsl:template>


    <xsl:template match="html:td[text()='verifyAttribute']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        verifyEquals("<xsl:value-of select="$param2"/>", selenium.getAttribute("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='verifyElementNotPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        verifyFalse(selenium.isElementPresent("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='verifyElementPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        verifyTrue(selenium.isElementPresent("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='assertElementPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        assertTrue(selenium.isElementPresent("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='verifyNotBodyText']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        verifyNotEquals("<xsl:value-of select="$param2"/>", selenium.getBodyText());
    </xsl:template>


    <xsl:template match="html:td[text()='verifySelectedIndexes']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        verifyEquals("<xsl:value-of select="$param2"/>", join(selenium.getSelectedIndexes("<xsl:value-of select="$param1"/>"), ','));
    </xsl:template>

    <xsl:template match="html:td[text()='verifySelectOptions']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        verifyEquals("<xsl:value-of select="$param2"/>", join(selenium.getSelectOptions("<xsl:value-of select="$param1"/>"), ','));
    </xsl:template>

    <xsl:template match="html:td[text()='verifyText']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        verifyEquals("<xsl:value-of select="$param2"/>", selenium.getText("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='verifyNotText']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        verifyNotEquals("<xsl:value-of select="$param2"/>", selenium.getText("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='verifyTextPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        verifyTrue(selenium.isTextPresent("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='verifyTextNotPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        verifyFalse(selenium.isTextPresent("<xsl:value-of select="$param1"/>"));
    </xsl:template>


    <xsl:template match="html:td[text()='verifyTitle']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        verifyEquals("<xsl:value-of select="$param1"/>", selenium.getTitle());
    </xsl:template>


    <xsl:template match="html:td[text()='verifyValue']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        verifyEquals("<xsl:value-of select="$param2"/>", selenium.getValue("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='assertValue']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        assertEquals("<xsl:value-of select="$param2"/>", selenium.getValue("<xsl:value-of select="$param1"/>"));
    </xsl:template>


     <xsl:template match="html:td[text()='wait']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
            Thread.sleep(Long.parseLong("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='waitForAlert']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
            for (int second = 0;; second++) {
                if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");

                try {
                        if (selenium.isAlertPresent()) {
                            String alert = selenium.getAlert().trim();
                            if ("<xsl:value-of select="$param1"/>".equals(alert)) break;
                        }
                } catch (Exception e) {}
                Thread.sleep(1000);
            }
    </xsl:template>

    <xsl:template match="html:td[text()='waitForAlertPresent']" priority="10">
        new Wait("Couldn't find Alert") {
            public boolean until() {
                 return selenium.isAlertPresent();
            }
        };
    </xsl:template>

    <xsl:template match="html:td[text()='waitForAlertNotPresent']" priority="10">
        new Wait("Couldn't find Alert") {
            public boolean until() {
                 return  !(selenium.isAlertPresent());
            }
	    };
    </xsl:template>

    <xsl:template match="html:td[text()='waitForAttribute']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>

        for (int second = 0;; second++) {
        if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");
        try {
        <xsl:choose>
            <xsl:when test="contains($param2,'*')">
                if (selenium.getAttribute("<xsl:value-of select="$param1"/>").matches("^[\\s\\S]<xsl:value-of select="$param2"/>[\\s\\S]*$"))
                break;
            </xsl:when>
            <xsl:otherwise>
                if ("<xsl:value-of select="$param2"/>".equals(selenium.getAttribute("<xsl:value-of select="$param1"/>")))
                break;
            </xsl:otherwise>
        </xsl:choose>

        } catch (Exception e) {}
        Thread.sleep(1000);
        }
    </xsl:template>

    <xsl:template match="html:td[text()='waitForElementPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>

        for (int second = 0;; second++) {
            if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");
            try {
                if (selenium.isElementPresent("<xsl:value-of select="$param1"/>")) break;
            } catch (Exception e) {}
                Thread.sleep(1000);
            }
    </xsl:template>

    <xsl:template match="html:td[text()='waitForEditable']" priority="10">
        for (int second = 0;; second++) {
			if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");
			try { if (selenium.isEditable("C6-value")) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
    </xsl:template>

    <xsl:template match="html:td[text()='waitForPopUp']" priority="10">
        selenium.waitForPopUp("_self", "30000");
    </xsl:template>

    <xsl:template match="html:td[text()='waitForPageToLoad']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        selenium.waitForPageToLoad("<xsl:value-of select="$param1"/>");
    </xsl:template>


    <xsl:template match="html:td[text()='waitForText']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        for (int second = 0;; second++) {
            if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");
            try { if ("<xsl:value-of select="$param2"/>".equals(selenium.getText("<xsl:value-of select="$param1"/>"))) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
    </xsl:template>

    <xsl:template match="html:td[text()='waitForTextPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        for (int second = 0;; second++) {
            if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");
            try { if (selenium.isTextPresent("<xsl:value-of select="$param1"/>")) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
    </xsl:template>

    <xsl:template match="html:td[text()='waitForValue']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
		for (int second = 0;; second++) {
			if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");
			try { if ("<xsl:value-of select="$param2"/>".equals(selenium.getValue("<xsl:value-of select="$param1"/>"))) break; } catch (Exception e) {}
			Thread.sleep(1000);
		}
    </xsl:template>

    <xsl:template match="html:td[text()='waitForTitle']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        for (int second = 0;; second++) {
            if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");
            try { if ("<xsl:value-of select="$param1"/>".equals(selenium.getTitle())) break; } catch (Exception e) {}
            Thread.sleep(1000);
        }
    </xsl:template>

    <xsl:template match="html:td[text()='setCursorPosition']" priority="10">
	<xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
	<xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
		selenium.setCursorPosition("<xsl:value-of select="$param1"/>", "<xsl:value-of select="$param2"/>" );
    </xsl:template>

    <xsl:template match="html:td[text()='keyPress']" priority="10">
	<xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
	<xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
		selenium.keyPress("<xsl:value-of select="$param1"/>", "<xsl:value-of select="$param2"/>" );
    </xsl:template>

    <xsl:template match="html:td[text()='keyPressAndWait']" priority="10">
	<xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
	<xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
		selenium.keyPress("<xsl:value-of select="$param1"/>", "<xsl:value-of select="$param2"/>" );
		selenium.waitForPageToLoad("30000");
    </xsl:template>

    <xsl:template match="html:td[text()='verifyNotEditable']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
         <xsl:message>Please replace verifyNotEditable with assertNotEditable</xsl:message>
            assertFalse(selenium.isEditable("<xsl:value-of select="$param1"/>"));
    </xsl:template>

     <xsl:template match="html:td[text()='verifyEditable']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:message>Please replace verifyEditable with assertEditable</xsl:message>
         assertTrue(selenium.isEditable("<xsl:value-of select="$param1"/>"));
     </xsl:template>

     <xsl:template match="html:td[text()='assertEditable']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
            assertTrue(selenium.isEditable("<xsl:value-of select="$param1"/>"));
    </xsl:template>

     <xsl:template match="html:td[text()='assertNotEditable']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
            assertFalse(selenium.isEditable("<xsl:value-of select="$param1"/>"));
    </xsl:template>

     <xsl:template match="html:td[text()='verifyVisible']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
         <xsl:message>Please replace verifyVisible with assertVisible</xsl:message>
            assertTrue(selenium.isVisible("<xsl:value-of select="$param1"/>"));
    </xsl:template>

     <xsl:template match="html:td[text()='assertVisible']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        assertTrue(selenium.isVisible("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='verifyNotVisible']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        <xsl:message>Please replace verifyNotVisible with assertNotVisible</xsl:message>
            assertFalse(selenium.isVisible("<xsl:value-of select="$param1"/>"));
    </xsl:template>


    <xsl:template match="html:td[text()='assertNotVisible']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        assertFalse(selenium.isVisible("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='setSpeed']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        selenium.setSpeed("<xsl:value-of select="$param1"/>");
    </xsl:template>

    <xsl:template match="html:td[text()='assertSelectOptions']" priority="10">
	    <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
	    <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
		assertEquals(selenium.getSelectOptions("<xsl:value-of select="$param1"/>"), "<xsl:value-of select="$param2"/>" );
    </xsl:template>

    <xsl:template match="html:td[text()='assertSelectedLabel']" priority="10">
	    <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
	    <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
		assertEquals(selenium.getSelectedLabel("<xsl:value-of select="$param1"/>"), "<xsl:value-of select="$param2"/>" );
    </xsl:template>

     <xsl:template match="html:td[text()='waitForElementNotPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>

        for (int second = 0;; second++) {
            if (second >= <xsl:value-of select="$timeout"/>) fail("timeout");
            try {
                if ( ! selenium.isElementPresent("<xsl:value-of select="$param1"/>")) break;
            } catch (Exception e) {}
                Thread.sleep(1000);
            }
    </xsl:template>


    <xsl:template match="html:td[text()='windowFocus']" priority="10">
        selenium.windowFocus();
    </xsl:template>

    <xsl:template match="html:td[text()='assertElementNotPresent']" priority="10">
        <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
        assertFalse(selenium.isElementPresent("<xsl:value-of select="$param1"/>"));
    </xsl:template>

    <xsl:template match="html:td[text()='assertCursorPosition']" priority="10">
         <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
         <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
        assertEquals("" + selenium.getCursorPosition("<xsl:value-of select="$param1"/>"), "<xsl:value-of select="$param2"/>");
    </xsl:template>

     <xsl:template match="html:td[text()='selectWindow']" priority="10">
         <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
         selenium.selectWindow("<xsl:value-of select="$param1"/>");
    </xsl:template>

     <xsl:template match="html:td[text()='assertXpathCount']" priority="10">
         <xsl:variable name="param1" select="replace( normalize-space( following-sibling::*[1]) ,$quot, $quote)"/>
         <xsl:variable name="param2" select="replace( normalize-space( following-sibling::*[2]) ,$quot, $quote)"/>
	 assertEquals("" + selenium.getXpathCount("<xsl:value-of select="$param1"/>"), "<xsl:value-of select="$param2"/>");
    </xsl:template>
    
  <xsl:template match="text()"/>
</xsl:stylesheet>
