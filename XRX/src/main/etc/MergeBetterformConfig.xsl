<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:webxml="http://java.sun.com/xml/ns/j2ee"
                xmlns="http://java.sun.com/xml/ns/j2ee"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="webxml">
    <xsl:output method="xml" indent="yes" />

    <xsl:param name="context" select="''"/>
    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="property[@name='initLogging']/@value" priority="10">
        <xsl:attribute name="value">false</xsl:attribute>
    </xsl:template>


    <xsl:template match="property[@name='error.page']/@value" priority="10">
        <xsl:choose>
            <xsl:when test="$context = 'betterform'">
                <xsl:attribute name="value">xquery/xferror.xql</xsl:attribute>
            </xsl:when>
            <xsl:when test="$context = 'extension'">
                <xsl:attribute name="value">betterfrom/xquery/xferror.xql</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">No context specified!</xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
