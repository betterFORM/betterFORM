<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->
<xsl:stylesheet version="2.0"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    exclude-result-prefixes="fo">

    <xsl:output method="xml" version="1.0" omit-xml-declaration="no" indent="yes"/>

    <xsl:template match="/">
        <fo:root>
            <fo:layout-master-set>
                <fo:simple-page-master
                    master-name="simpleA4"
                    page-height="29.7cm"
                    page-width="21cm"
                    margin-top="2cm"
                    margin-bottom="2cm"
                    margin-left="2cm"
                    margin-right="2cm">
                    <fo:region-body/>
<!--
                    <fo:region-before extent="1.0cm"/>
                    -->
                    <fo:region-after extent="1.5cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <fo:page-sequence master-reference="simpleA4">

<!--
                <fo:static-content flow-name="xsl-region-before">
                    <fo:block-container left="5cm" top="5cm" position="absolute">
                        <fo:block>
                            <fo:external-graphic content-height="1em" content-width="1em" src="url(html/test/images/betterform50.jpg)"/>
                        </fo:block>
                    </fo:block-container>
                    <fo:block text-align="center">
                        <fo:leader leader-pattern="rule" rule-thickness="0.5pt" leader-length="18cm"/>
                    </fo:block>
                </fo:static-content>
-->

                <fo:static-content flow-name="xsl-region-after">
                    <fo:block text-align="center" space-before.optimum="0pt" space-after.optimum="0pt">
                        <fo:leader leader-pattern="rule" rule-thickness="0.5pt" leader-length="18cm"/>
                    </fo:block>
                    <fo:block text-align="right" font-size="10pt">
                            betterForm 2010
                    </fo:block>
                </fo:static-content>

                <fo:flow flow-name="xsl-region-body">
                    <fo:block>
                        <fo:external-graphic src="url(file://d:/projects/blindow/build/images/betterform50.jpg)"/>
                    </fo:block>
                    <fo:block text-align="center" space-before.optimum="0pt" space-after.optimum="0pt">
                        <fo:leader leader-pattern="rule" rule-thickness="0.5pt" leader-length="18cm"/>
                    </fo:block>
                    <fo:block font-size="10pt">
                        <xsl:message>ROOT</xsl:message>
                        <xsl:apply-templates select="//html:body"/>
                    </fo:block>
                </fo:flow>
            </fo:page-sequence>

        </fo:root>
    </xsl:template>

    <xsl:template match="html:body">
        <xsl:message>body</xsl:message>
        <xsl:apply-templates select="*"/>
    </xsl:template>

    <xsl:template match="xf:group">
        <fo:block font-size="12pt">
            <xsl:value-of select="xf:label"/>
        </fo:block>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="xf:repeat">

    </xsl:template>

    <xsl:template match="xf:switch">

    </xsl:template>

    <xsl:template match="xf:case">

    </xsl:template>

    <!-- ********** Controls ********** -->

    <xsl:template match="xf:input">
        <fo:block font-size="10pt">
            <xsl:value-of select="xf:label"/> =
            <xsl:value-of select="bf:data"/>
        </fo:block>
    </xsl:template>

    <xsl:template match="xf:output">
        <fo:block font-size="10pt">
            <xsl:value-of select="xf:label"/> =
            <xsl:value-of select="bf:data"/>
        </fo:block>
    </xsl:template>

    <xsl:template match="xf:range">
        <fo:block font-size="10pt">
            <xsl:value-of select="xf:label"/> =
            <xsl:value-of select="bf:data"/>
        </fo:block>
    </xsl:template>

    <xsl:template match="xf:secret">
        <fo:block font-size="10pt">
            <xsl:value-of select="xf:label"/> =
            <xsl:value-of select="bf:data"/>
        </fo:block>
    </xsl:template>


    <xsl:template match="xf:select1">
        <fo:block font-size="10pt">
            <xsl:value-of select="xf:label"/> =
            <xsl:value-of select="bf:data"/>
        </fo:block>
    </xsl:template>

    <xsl:template match="xf:select">
        <fo:block font-size="10pt">
            <xsl:value-of select="xf:label"/> =
            <xsl:value-of select="bf:data"/>
        </fo:block>
    </xsl:template>

    <xsl:template match="xf:textarea">
        <fo:block font-size="10pt">
            <xsl:value-of select="xf:label"/> =
            <xsl:value-of select="bf:data"/>
        </fo:block>
    </xsl:template>

    <xsl:template match="xf:submit"></xsl:template>

    <xsl:template match="xf:trigger"></xsl:template>

    <xsl:template match="xf:upload"></xsl:template>

    <xsl:template match="*"/>
</xsl:stylesheet>