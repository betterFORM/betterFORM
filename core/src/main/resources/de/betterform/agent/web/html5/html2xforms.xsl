<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xi="http://www.w3.org/2001/XInclude"
                xmlns:bfc="http://betterform.sourceforge.net/xforms/controls"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                exclude-result-prefixes="bf xsl">


    <!--
    Transforms sanitized HTML5 documents into into xforms elements.
    -->
    <xsl:strip-space elements="*"/>
    <xsl:template match="/*">
        <xsl:copy>
            <xsl:namespace name="xf" select="'http://www.w3.org/2002/xforms'"/>
            <xsl:copy-of select="@*"/>


            <div style="display:none">
                <xf:model>
                    <xf:instance>
                        <data>
                            <xsl:apply-templates select="*" mode="model"/>
                        </data>
                    </xf:instance>
                    <xsl:apply-templates select="*" mode="bind"/>
                </xf:model>
            </div>

            <xsl:apply-templates select="*" mode="ui"/>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="*[@name]" mode="model">
        <xsl:element name="{@name}">
            <xsl:apply-templates select="*" mode="model"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="text()" mode="model" priority="10"/>
    <xsl:template match="label" mode="model" priority="10"/>
    <xsl:template match="option" mode="model" priority="10"/>

    <xsl:template match="text()" mode="bind" priority="10"/>
    <xsl:template match="label" mode="bind" priority="10"/>
    <xsl:template match="option" mode="bind" priority="10"/>

    <xsl:template match="*[@name]" mode="bind">
        <xsl:element name="xf:bind" namespace="http://www.w3.org/2002/xforms">
            <xsl:attribute name="ref"><xsl:value-of select="@name"/></xsl:attribute>
            <xsl:apply-templates select="*" mode="bind"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="@*|node()|text()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|node()|text()" mode="ui">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="ui"/>
            <xsl:apply-templates mode="ui"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*[@name]" mode="ui">
        <xsl:element name="{concat('xf:',name())}" namespace="http://www.w3.org/2002/xforms">
            <xsl:apply-templates select="@*" mode="ui"/>
            <xsl:attribute name="ref" namespace="http://www.w3.org/2002/xforms"><xsl:value-of select="@name"/></xsl:attribute>
            <xsl:apply-templates select="*" mode="ui"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="option" mode="ui">
        <xsl:element name="xf:item" namespace="http://www.w3.org/2002/xforms">
            <xsl:element name="xf:label"><xsl:value-of select="text()"/></xsl:element>
            <xsl:element name="xf:value"><xsl:value-of select="@value"/></xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
