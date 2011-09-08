<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                exclude-result-prefixes="xsl xf ev">

    <!-- Copyright 2009 Lars Windauer, Joern Turner -->

    <xsl:output method="xhtml" version="1.0" indent="yes"/>

    <xsl:strip-space elements="*"/>

    <xsl:template match="html">
        <html>
            <head>
                <title></title>
                <xsl:apply-templates mode="head" select="*"/>
            </head>
            <body>
                <xsl:apply-templates mode="ui" select="*"/>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="*" mode="head"/>
    <xsl:template match="xf:model" mode="head"/>


    <xsl:template match="*" mode="ui">
        <xsl:apply-templates mode="ui"/>
    </xsl:template>

    <xsl:template match="xf:bind" mode="head" priority="10"/>
    <xsl:template match="xf:bind" mode="ui" priority="10"/>

    <xsl:template match="xf:model" mode="ui">
        <!-- ##### using resource of first submission in document as form action target ##### -->
        <xsl:variable name="action">
            <xsl:choose>
                <xsl:when test="exists(//xf:submission[1]/@action)"><xsl:value-of select="//xf:submission[1]/@action"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="//xf:submission[1]/@resource"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <form action="{$action}">
            <div style="display:hidden;">
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:for-each select="xf:instance">
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </xsl:copy>
            </div>
            <!-- #####  UI starts here #####-->
            <xsl:apply-templates mode="ui"/>
        </form>
    </xsl:template>

<!--
    <xsl:template match="*|text()">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
-->

    <xsl:template match="text()" mode="ui">
        <xsl:copy-of select="."/>
    </xsl:template>



    <!--<xsl:template match="style"/>-->
    <!--<xsl:template match="xf:bind"/>-->
    
    <xsl:template match="xf:input[@ref]" mode="ui">
        <xsl:copy-of select="//xf:bind[ends-with(@nodeset,@ref)]"/>
    </xsl:template>

</xsl:stylesheet>


