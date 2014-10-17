<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<!-- $Id: sort-instance.xsl,v 1.4 2006/03/21 19:24:57 uli Exp $ -->
<xsl:stylesheet version="2.0"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="appContext" select="'/betterform'"/>
    <xsl:output method="xhtml" encoding="UTF-8" indent="yes"/>


    <xsl:variable name="errors" select="document('errors.xml')"/>

    <xsl:output method="xhtml" omit-xml-declaration="yes"/>
    <xsl:strip-space elements="*"/>
    <xsl:variable name="CR">
        <xsl:text>&#xa;</xsl:text>
    </xsl:variable>

    <xsl:template match="/html">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>
        <xsl:value-of select="$CR"/>
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*[@name]">


        <xsl:variable name="currName" select="@name"/>
        <xsl:message><xsl:value-of select="$currName"/></xsl:message>
        <xsl:variable name="matching-errors" select="$errors/errors/errorInfo[ref = $currName]"/>
        <xsl:variable name="classes">
            <xsl:if test="@class">
                <xsl:value-of select="@class"/><xsl:text> </xsl:text>
            </xsl:if>
            <xsl:for-each select="$matching-errors">
                <xsl:value-of select="errorType"/>
                <xsl:if test="position()!=last()"><xsl:text> </xsl:text></xsl:if>
            </xsl:for-each>
        </xsl:variable>

        <xsl:message><xsl:value-of select="$classes"/></xsl:message>

        <xsl:copy>
            <xsl:copy-of select="@*[not(name()='class')]"/>
            <xsl:attribute name="class" select="$classes"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|node()|text()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>
