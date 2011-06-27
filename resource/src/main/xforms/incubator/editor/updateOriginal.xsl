<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <!-- author: Joern Turner -->

    <xsl:param name="originDoc" select="''"/>

    <xsl:template match="/">
            <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="*[@id]" priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:message>orig:<xsl:value-of select="$originDoc"/></xsl:message>
        <xsl:if test="$originDoc=''">
            <xsl:message terminate="yes">Original document not found: <xsl:value-of select="$originDoc"/></xsl:message>
        </xsl:if>
        
        <xsl:choose>
            <xsl:when test="document($originDoc)//*[@oid=$id]">
                <xsl:message>yes it did</xsl:message>
                <xsl:copy-of select="document($originDoc)//*[@oid=$id]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*|text()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>
