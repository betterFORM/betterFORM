<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:xbl="http://betterform.de/ns/xbl"
                xmlns:gen="betterFORM-mapping-generator"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bfb="http://betterform.de/binding"
                exclude-result-prefixes="xsl">
    <xsl:strip-space elements="*"/>
    <xsl:namespace-alias stylesheet-prefix="gen" result-prefix="xsl"/>

    <!--<xsl:variable name="tickle" select="'"/>-->
    <xsl:output method="xml" name="xml"/>

    <xsl:template match="/xbl:xbl">

        <!--
            writes xslt for early binding: matches all bindings that contain XForms markup and substitutes relevant
            templates
        -->
        <xsl:result-document href="early-binding.xsl" format="xml">
            <gen:stylesheet version="2.0" exclude-result-prefixes="xf bf bfb xbl xhtml">

                <gen:template match="/html">
                    <gen:copy>
                        <gen:copy-of select="@*"/>
                        <gen:apply-templates/>
                    </gen:copy>
                </gen:template>

                <xsl:for-each select="xbl:binding[exists(xbl:template//xf:*)]">
                    <xsl:variable name="matchPattern"><xsl:value-of select="@element"/></xsl:variable>
                    <xsl:variable name="dataType">[bf:data/@datatype='<xsl:value-of select="@type"/>']</xsl:variable>
                    <xsl:variable name="filter">
                        <xsl:for-each select="@appearance[.!='*']|@mediatype[.!='*']">[@<xsl:value-of select="local-name()"/>='<xsl:value-of select="."/>']</xsl:for-each>
                    </xsl:variable>

                    <xsl:variable name="fullPattern">
                        <xsl:value-of select="if(exists(@type) and @type != '*') then concat($matchPattern,$dataType,$filter) else concat($matchPattern,$filter)"/>
                    </xsl:variable>

                    <gen:template match="{$fullPattern}" priority="1000">
                        <!--<xsl:copy-of select="xbl:template/*[1]"/>-->
                        <xsl:apply-templates />
                    </gen:template>

                </xsl:for-each>
            </gen:stylesheet>
        </xsl:result-document>

        <!-- writes xslt for runtime mapping of XForms elements to target language -->
        <xsl:result-document href="xbl.xsl" format="xml">
            <gen:stylesheet version="2.0" exclude-result-prefixes="xf bf bfb xbl xhtml">

                <gen:import href="{bfb:resources/bfb:xslt/@href}"/>

                    <xsl:for-each select="xbl:binding[not(exists(xbl:template//xf:*))]">
                        <xsl:variable name="matchPattern"><xsl:value-of select="@element"/></xsl:variable>
                        <xsl:variable name="dataType">[bf:data/@datatype='<xsl:value-of select="@type"/>']</xsl:variable>
                        <xsl:variable name="filter">
                            <xsl:for-each select="@appearance[.!='*']|@mediatype[.!='*']">[@<xsl:value-of select="local-name()"/>='<xsl:value-of select="."/>']</xsl:for-each>
                        </xsl:variable>

                        <xsl:variable name="fullPattern">
                            <xsl:value-of select="if(exists(@type) and @type != '*') then concat($matchPattern,$dataType,$filter) else concat($matchPattern,$filter)"/>
                        </xsl:variable>
                        <gen:template match="{$fullPattern}" priority="1000">
                            <!--<xsl:copy-of select="xbl:template/*[1]"/>-->
                            <xsl:apply-templates />
                        </gen:template>
                    </xsl:for-each>

            </gen:stylesheet>
        </xsl:result-document>
    </xsl:template>

    <xsl:template match="xbl:template">
        <xsl:apply-templates/>
    </xsl:template>


<!--
    <xsl:template match="xf:*" priority="50">
        <gen:apply-templates/>
    </xsl:template>
-->



    <xsl:template match="xbl:handlers"/>


    <xsl:template match="xbl:content[exists(*)]" priority="20">
        <gen:for-each select="{@includes}">
            <xsl:apply-templates/>
        </gen:for-each>
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
