<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
-->

<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xftr="http://www.w3c.org/MarkUp/Forms/XForms/Test/Runner">
    <xsl:param name="filename">bogus</xsl:param>
    <xsl:param name="filedir">.</xsl:param>

    <xsl:output name="javaText" method="text"/>

    <xsl:variable name="testname" select="replace(substring-before($filename,'.xml'),'\.','_')"/>
    <xsl:variable name="classname" select="concat('Test_',$testname)"/>

    <xsl:variable name="quot">"</xsl:variable>
    <xsl:variable name="quote">\\"</xsl:variable>

    <xsl:template match="xftr:test-case">
        <output> using:<xsl:value-of select="$testname"/> filedir:<xsl:value-of select="$filedir"/>
                classname:<xsl:value-of select="$classname"/>
            <xsl:result-document format="javaText" href="{concat($classname,'.java')}">
                package de.betterform.conformance.xf11.<xsl:value-of select="$filedir"/>;
                
                import de.betterform.conformance.WebDriverTestFunctions;
                import org.junit.Test;
                import static org.junit.Assert.*;
                import org.openqa.selenium.Alert;
                public class <xsl:value-of select="$classname"/> extends WebDriverTestFunctions {
                
                    @Test
		    public void <xsl:value-of select="$classname"/>() throws Exception {
                        Alert alert;
                        <xsl:apply-templates/>
                    }
                }
            </xsl:result-document>
        </output>
    </xsl:template>

    <xsl:template match="xftr:open">
        webDriver.get(baseUrl + "<xsl:value-of select="@href"/>");
    </xsl:template>

    <xsl:template match="xftr:assert-title">
        waitForTitle("<xsl:value-of select="replace( normalize-space(@title) ,$quot, $quote)"/>");
        assertEquals("<xsl:value-of select="replace( normalize-space(@title) ,$quot, $quote)"/>", webDriver.getTitle());
    </xsl:template>

    <xsl:template match="xftr:assert-selection-options">
            assertTrue(checkSelectionOptions("<xsl:value-of select="concat(@locator, '-value')"/>", "<xsl:value-of select="@options"/>".split(",")));
    </xsl:template>

    <xsl:template match="xftr:assert-control-present">
        assertTrue(isControlPresent("<xsl:value-of select="@locator"/>", "<xsl:value-of select="@type"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-control-not-present">
            assertFalse(isControlPresent("<xsl:value-of select="@locator"/>", "<xsl:value-of select="@type"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-text-present">
        assertTrue(isTextPresent("<xsl:value-of select="replace( normalize-space(@text) ,$quot, $quote)"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-text-not-present">
        assertFalse(isTextPresent("<xsl:value-of select="replace( normalize-space(@text) ,$quot, $quote)" disable-output-escaping="yes"/>"));
    </xsl:template>


    <xsl:template match="xftr:select-option">
        selectOption("<xsl:value-of select="concat(@locator, '-value')"/>", "<xsl:value-of select="replace( normalize-space(@option) ,$quot, $quote)"/>");
    </xsl:template>

    <xsl:template match="xftr:click">
        click("<xsl:value-of select="concat(@locator, '-value')"/>");
    </xsl:template>

    <xsl:template match="xftr:type-input">
        typeInput("<xsl:value-of select="concat(@locator, '-value')"/>", "<xsl:value-of select="replace( normalize-space(@value) ,$quot, $quote)"/>");
    </xsl:template>

    <xsl:template match="xftr:type-secret">
        typeInput("<xsl:value-of select="concat(@locator, '-value')"/>", "<xsl:value-of select="replace( normalize-space(@value) ,$quot, $quote)"/>");
    </xsl:template>

    <xsl:template match="xftr:type-textarea">
        typeInput("<xsl:value-of select="concat(@locator, '-value')"/>", "<xsl:value-of select="replace( normalize-space(@value) ,$quot, $quote)"/>");
    </xsl:template>

    <xsl:template match="xftr:assert-control-label">
        assertEquals(getControlLabel("<xsl:value-of select="concat(@locator, '-label')"/>"), "<xsl:value-of select="replace( normalize-space(@value) ,$quot, $quote)"/>");
    </xsl:template>

    <xsl:template match="xftr:assert-control-value-contains">
            assertTrue(getControlValue("<xsl:value-of select="concat(@locator, '-value')"/>").contains("<xsl:value-of select="replace( normalize-space(@value) ,$quot, $quote)"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-control-value">
        assertEquals(getControlValue("<xsl:value-of select="concat(@locator, '-value')"/>"), "<xsl:value-of select="replace( normalize-space(@value) ,$quot, $quote)"/>");
    </xsl:template>

    <xsl:template match="xftr:assert-control-valid">
        assertTrue(isControlValueValid("<xsl:value-of select="@locator"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-control-readwrite">
            assertTrue(isControlReadWrite("<xsl:value-of select="@locator"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-control-readonly">
        assertTrue(isControlReadOnly("<xsl:value-of select="@locator"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-control-required">
        assertTrue(isControlRequired("<xsl:value-of select="@locator"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-control-invalid">
        assertTrue(isControlValueInvalid("<xsl:value-of select="@locator"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-control-focus">
        assertTrue(isControlFocused("<xsl:value-of select="concat(@locator, '-value')"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-control-value-in-range">
            assertTrue(isControlValueInRange("<xsl:value-of select="concat(@locator, '-value')"/>", "<xsl:value-of select="@start"/>", "<xsl:value-of select="@end"/>"));
    </xsl:template>


    <xsl:template match="xftr:assert-exception">
        hasException();
        assertTrue(getExceptionType().contains("<xsl:value-of select="replace( normalize-space(@type) ,$quot, $quote)"/>"));
    </xsl:template>

    <xsl:template match="xftr:assert-message-or-exception">
        hasException();
        assertTrue(getExceptionType().contains("<xsl:value-of select="replace( normalize-space(@exception) ,$quot, $quote)"/>"));
    </xsl:template>

    <xsl:template match="xftr:wait-for-page-to-load"> //Do nothing. </xsl:template>

    <xsl:template match="xftr:assert-no-messages">
        assertNull(getAlert());
    </xsl:template>

    <xsl:template match="xftr:assert-messages">
        <xsl:for-each select="xftr:message">
            alert = getAlert();
            assertEquals(alert.getText(), "<xsl:value-of select="."/>");
            alert.accept();
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="xftr:assert-message">
	assertTrue(checkAlert("<xsl:value-of select="@message"/>"));
    </xsl:template>

    <xsl:template match="xftr:fail">
        fail("<xsl:value-of select="@msg"/>");
    </xsl:template>

    <xsl:template match="xftr:*">
        <xsl:message terminate="yes">Element <xsl:value-of select="name(.)"/> not implemented!</xsl:message>
    </xsl:template>
</xsl:stylesheet>
