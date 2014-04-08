<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:bf="http://betterform.sourceforge.net/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events">
    <!--
    ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
    ~ Licensed under the terms of BSD License
    -->

    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
    <xsl:strip-space elements="*"/>

	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="*|@*|text()">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()"/>
        </xsl:copy>
    </xsl:template>


	<xsl:template match="bf:data"/>
	<xsl:template match="xf:group[@appearance='repeated']"/>
	<xsl:template match="@src"/>
    <xsl:template match="*[@ev:event='xforms-model-construct']"/>
    <xsl:template match="*[@ev:event='xforms-model-construct-done']"/>
    <xsl:template match="*[@ev:event='xforms-ready']" priority="10"/>

	<xsl:template match="xf:repeat" priority="5">
		<xsl:variable name="index" select="bf:data/@bf:index"/>
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:attribute name="bf:index"><xsl:value-of select="$index"/></xsl:attribute>
			<xsl:apply-templates select="bf:data/xf:group/*"/>
		</xsl:copy>
	</xsl:template>

	<xsl:template match="xf:case">
		<xsl:copy>
            <xsl:copy-of select="@*"/>
			<xsl:variable name="selected" select="bf:data/@bf:selected"/>
			<xsl:attribute name="selected"><xsl:value-of select="$selected"/></xsl:attribute>
			<xsl:apply-templates/>
		</xsl:copy>
	</xsl:template>


</xsl:stylesheet>
