<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version='1.0'
                xmlns:xsl='http://www.w3.org/1999/XSL/Transform'
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:ts="http://www.w3c.org/MarkUp/Forms/XForms/Test/11"
                exclude-result-prefixes="ts">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" omit-xml-declaration="no"
                doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd"
                indent="yes" xalan:indent-amount="4" xmlns:xalan="http://xml.apache.org/xslt"/>

    <xsl:template match="/">
        <xsl:for-each select="/ts:testSuite">
            <html>
                <head>
                    <title>
                        <xsl:value-of select="/ts:testSuite/ts:specChapter/@chapterName"/>
                        <xsl:value-of select="/ts:testSuite/ts:specChapter/@chapterTitle"/>
                    </title>
                    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
                    <link rel="stylesheet" href="TestSuite11.css" type="text/css"/>
                </head>
                <body>
                    <h2>XForms Test Suite</h2>
                    <h1>
                        <xsl:value-of select="/ts:testSuite/ts:specChapter/@chapterName"/>
                        <xsl:value-of select="/ts:testSuite/ts:specChapter/@chapterTitle"/>
                    </h1>
                    <table cellpadding="2" cellspacing="1" border="2" width="100%">
                        <tr>
                            <th class="testCaseNameTitle" align="left">Test Case</th>
                            <th class="testCaseNameTitle" align="left">Description</th>
                            <th class="testCaseNameTitle" align="left">Link To Spec</th>
                            <th class="testCaseNameTitle" align="left">Normative For XForms Basic</th>
                            <th class="testCaseNameTitle" align="left">Normative For XForms Full</th>
                        </tr>
                        <xsl:for-each
                                select="/ts:testSuite/ts:specChapter/ts:testCase">
                            <tr>
                                <td>
                                    <a href="{ts:testCaseLink}">
                                        <xsl:value-of select="ts:testCaseName"/>
                                    </a>
                                </td>
                                <td>
                                    <xsl:value-of select="normalize-space(ts:testCaseDescription)"/>
                                </td>
                                <td>
                                    <a href="{ts:testCaseSpecLink}">
                                        <xsl:value-of select="ts:testCaseSpecLinkName"/>
                                    </a>
                                </td>
                                <td>
                                    <xsl:value-of select="ts:testCaseBasic"/>
                                </td>
                                <td>
                                    <xsl:value-of select="ts:testCaseNormative"/>
                                </td>
                            </tr>
                        </xsl:for-each>
                    </table>
                </body>
            </html>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
