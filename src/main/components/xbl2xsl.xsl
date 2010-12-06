<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:xbl="http://betterform.de/ns/xbl"
                xmlns:gen="betterFORM-mapping-generator"
                xmlns:xf="http://www.w3.org/2002/xforms"
                exclude-result-prefixes="bf xsl">
    <xsl:strip-space elements="*"/>
    <xsl:namespace-alias stylesheet-prefix="gen" result-prefix="xsl"/>


    <xsl:template match="/xbl:xbl">
        <gen:stylesheet version="1.0">

            <xsl:for-each select="xbl:binding">
                <xsl:variable name="matchPattern"><xsl:value-of select="@element"/></xsl:variable>
                <xsl:variable name="filter">
                    <xsl:for-each select="@type[.!='*']|@appearance[.!='*']|@mediatype[.!='*']">[@<xsl:value-of select="local-name()"/>='<xsl:value-of select="."/>']</xsl:for-each>
                </xsl:variable>

                <gen:template match="{concat($matchPattern,$filter)}">
                    <!--<xsl:copy-of select="xbl:template/*[1]"/>-->
                    <xsl:apply-templates />
                </gen:template>
            </xsl:for-each>

        </gen:stylesheet>
    </xsl:template>

    <xsl:template match="xbl:template">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="xbl:content">
        <gen:apply-templates select="{@includes}"/>
    </xsl:template>

    <xsl:template match="*|@*|text()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
</xsl:stylesheet>
