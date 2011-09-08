<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:saxon="http://icl.com/saxon"
    exclude-result-prefixes="xf saxon">

    <xsl:template match="/">
        <map version="0.8.1">
            <attribute_registry SHOW_ATTRIBUTES="selected"/>

            <node text="Form">
                <xsl:for-each select="*">
                    <xsl:apply-templates select="."/>
                </xsl:for-each>
            </node>
        </map>
    </xsl:template>

    <xsl:template match="xf:*" name="copyNode">
        <xsl:param name="position" />
        <xsl:param name="folded" select="'true'"/>
        <xsl:variable name="id">
            <xsl:choose>
                <xsl:when test="string-length(@id) != 0"><xsl:value-of select="@id"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="generate-id()"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <node ID="{$id}" TEXT="{concat(name(.),':',$id)}">
            <xsl:if test="string-length($position) != 0"><xsl:attribute name="position" select="$position"/></xsl:if>
            <xsl:if test="string-length($folded) != 0"><xsl:attribute name="folded" select="$folded"/></xsl:if>
            <xsl:for-each select="@*">
                <xsl:call-template name="copyAttr"/>
            </xsl:for-each>
            <xsl:for-each select="*">
                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </node>
    </xsl:template>

    <xsl:template match="xf:model">
        <xsl:call-template name="copyNode">
            <xsl:with-param name="position" select="'left'"/>
            <xsl:with-param name="folded" select="'true'"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="xf:instance//*">
        <xsl:call-template name="copyNode"/>
    </xsl:template>

    <xsl:template match="text()"/>

    <xsl:template match="@*" name="copyAttr">
        <attribute NAME="{name()}" VALUE="{.}"/>
    </xsl:template>
</xsl:stylesheet>
