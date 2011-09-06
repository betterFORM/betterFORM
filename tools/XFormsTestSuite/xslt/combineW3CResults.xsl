<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="2.0"
        exclude-result-prefixes="xsl"
        >
    <xsl:param name="filedir"></xsl:param>
    <xsl:param name="basePath"></xsl:param>

    <xsl:output method="xml" indent="yes"/>

    <xsl:variable name="dirs">
        <dir>
            <!--<file>XF11_01_Results.xml</file>-->
            <file>XF11_02_Results.xml</file>
            <file>XF11_03_Results.xml</file>
            <file>XF11_04_Results.xml</file>
            <file>XF11_05_Results.xml</file>
            <file>XF11_06_Results.xml</file>
            <file>XF11_07_Results.xml</file>
            <file>XF11_08_Results.xml</file>
            <file>XF11_09_Results.xml</file>
            <file>XF11_10_Results.xml</file>
            <file>XF11_11_Results.xml</file>
            <file>XF11_AppendixB_Results.xml</file>
            <file>XF11_AppendixG_Results.xml</file>
            <file>XF11_AppendixH_Results.xml</file>
        </dir>
    </xsl:variable>


    <!--
        author: Joern Turner
        This stylesheet merges the result xml files from original XForms 1.1 Testsuite into one for easier processing.
    -->

    <xsl:template match="/">
        <testsuites>

            <xsl:for-each select="$dirs/dir/file">
                <xsl:variable name="currentFile" select="concat($basePath,'/',.)"/>
                <!--<xsl:value-of select="$currentFile"/>-->
                <xsl:copy-of select="doc($currentFile)/*"/>
            </xsl:for-each>
        </testsuites>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
