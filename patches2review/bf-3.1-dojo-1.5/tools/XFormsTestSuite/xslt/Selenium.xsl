<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="2.0"

        xmlns:xhtml="http://www.w3.org/1999/xhtml"
        xmlns:bf="http://betterform.sourceforge.net/xforms"
        xmlns:ev="http://www.w3.org/2001/xml-events"
        xmlns:xf="http://www.w3.org/2002/xforms"
        xmlns:xs="http://www.w3.org/2001/XMLSchema"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
        xmlns:hdlg="http://www.hdlg.info/XML/filesystem"
        exclude-result-prefixes="bf xsl ev">
    <xsl:param name="targetURL" select="''"/>
    <xsl:param name="server" select="''"/>
    <xsl:param name="uriParams" select="''"/>


    <xsl:output method="text"/>
    <xsl:output method="html" indent="yes" name="html"/>

    <xsl:template match="hdlg:file">
            <xsl:variable name="testCasePath" select="substring-after(@url,'/target/xhtml/')"/>
            <xsl:variable name="relativePath" select="substring-before($testCasePath,'.xhtml')"/>
            <xsl:variable name="filename" select="concat('webtest/',$relativePath,'.html')"/>
            <xsl:message>Relative: <xsl:value-of select="$relativePath"/> FileName <xsl:value-of select="$filename"/> </xsl:message>
            <xsl:call-template name="create-testcase">
                <xsl:with-param name="resultFile" select="$filename"/>
                <xsl:with-param name="testCasePath" select="$testCasePath"/>
            </xsl:call-template>
    </xsl:template>

<!--
    <xsl:template match="hdlg:folder">
            <xsl:variable name="testSuitePath" select="substring-after(@url,'/target/xhtml/')"/>
            <xsl:variable name="filename" select="concat('webtest/',substring($testSuitePath,1,string-length($testSuitePath)),'/',@name,'.html')"/>
            <xsl:message>testSuitePath: <xsl:value-of select="$testSuitePath"/> FileName <xsl:value-of select="$filename"/> </xsl:message>

            <xsl:if test="string-length($testSuitePath) &gt; 1">
                <xsl:call-template name="create-test-suite">
                    <xsl:with-param name="resultFile" select="$filename"/>
                    <xsl:with-param name="testCasePath" select="$testSuitePath"/>
                    <xsl:with-param name="testSuite" select="."/>
                </xsl:call-template>
            </xsl:if>
    </xsl:template>
-->


    <xsl:template name="create-testcase">
        <xsl:param name="resultFile" select="''"/>
        <xsl:param name="testCasePath" select="''"/>
        <xsl:variable name="conformanceTest" select="document(concat('../target/xhtml/',$testCasePath))/xhtml:html"/>
        <xsl:message>Title: <xsl:value-of select="$conformanceTest//xhtml:title"/></xsl:message>

        <xsl:result-document href="{$resultFile}" format="html">
                <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
                <head profile="http://selenium-ide.openqa.org/profiles/test-case">
                    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                    <link rel="selenium.base" href="{$server}/"/>
                    <title>
                        <xsl:value-of select="$conformanceTest//xhtml:title"/>
                    </title>
                </head>
                <body>
                    <table cellpadding="1" cellspacing="1" border="1">
                        <thead>
                            <tr>
                                <td rowspan="1" colspan="3">
                                    <xsl:value-of select="$conformanceTest//xhtml:title"/>
                                </td>
                            </tr>
                        </thead>
                        <tbody>
                            <tr>
                                <td>open</td>
                                <td><xsl:value-of select="concat($targetURL,'/',$testCasePath,$uriParams)"/></td>
                                <td></td>
                            </tr>
                        </tbody>
                    </table>
                </body>
            </html>

        </xsl:result-document>

    </xsl:template>

    <xsl:template name="create-test-suite">
        <xsl:param name="resultFile" select="''"/>
        <xsl:param name="testCasePath" select="''"/>
        <xsl:param name="testSuite" select="''"/>
        
        <xsl:result-document href="{$resultFile}" format="html">
            <html>
                <head>
                    <title>Test Suite <xsl:value-of select="$testSuite/@name"/></title>
                </head>
                <body>
                    <h3>Selenium Webtest Suite</h3>
                    <div>
                        Usage:
                        <ul>
                            <li>download the Selenium IDE Firefox Plugin (http://seleniumhq.org/download/)</li>
                            <li>in Firefox click: Tools > Selenium IDE</li>
                            <li>in Selenium IDE click: File > OpenTestSuite and select this file</li>
                            <li>Press "Play entire test suite" or execute each test on its own with "Play current test case"</li>
                        </ul>
                    </div>
                    <br />
                    <table>
                        <xsl:for-each select="*">
                            <tr>
                                <td><a href="@name"><xsl:value-of select="substring-before(@name,'.')" /></a></td>
                            </tr>

                        </xsl:for-each>
                    </table>
                </body>
            </html>
        </xsl:result-document>

    </xsl:template>
</xsl:stylesheet>
