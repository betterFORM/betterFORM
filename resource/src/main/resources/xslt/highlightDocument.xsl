<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<!-- $Id: sort-instance.xsl,v 1.4 2006/03/21 19:24:57 uli Exp $ -->
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        >

    <xsl:output method="xhtml" encoding="UTF-8" indent="no"/>
    <xsl:param name="params"/>

    <xsl:preserve-space elements="*"/>
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<title><xsl:value-of select="$params/URI"/></title>
                <script type="text/javascript" src="{$params/context}/bfResources/scripts/syntaxhighlighter_3.0.83/scripts/shCore.js"></script>
                <script type="text/javascript" src="{$params/context}/bfResources/scripts/syntaxhighlighter_3.0.83/scripts/shBrushXml.js"></script>
                <link type="text/css" rel="stylesheet" href="{$params/context}/bfResources/scripts/syntaxhighlighter_3.0.83/styles/shCore.css"/>
                <link type="text/css" rel="stylesheet" href="{$params/context}/bfResources/scripts/syntaxhighlighter_3.0.83/styles/shCoreDefault.css"/>
				<script type="text/javascript">SyntaxHighlighter.all();</script>
                <style type="text/css">
                    body{
                        font-family:Tahoma;
                        font-size:14pt;
                        background:url('<xsl:value-of select="$params/context"/>/bfResources/images/bgOne.gif') repeat-x scroll;
                        font-family: san-serif;
                        color:#4682b4;
                    }
                    .syntaxhighlighter{
                        font-size: 10pt !important;
                    }
                </style>
			</head>
			
			<body>
				
				<h2>URI: <xsl:value-of select="$params/URI"/></h2>
                <xsl:if test="exists($params/model)">Model: '<xsl:value-of select="$params/model"/>' - Instance: '<xsl:value-of select="$params/instance"/>'</xsl:if>
				<pre class="brush: xml">
                    <xsl:copy>
                        <xsl:apply-templates />
                    </xsl:copy>
				</pre>
			</body>
		</html>
	</xsl:template>

	<xsl:variable name="nl"><xsl:text>
</xsl:text>
	</xsl:variable>

	<xsl:variable name="indent-increment" select="'    '"/>
	
	<xsl:template name="write-starttag">
		<xsl:text>&lt;</xsl:text>
		<xsl:value-of select="name()"/>
		<xsl:for-each select="@*">
			<xsl:call-template name="write-attribute"/>
		</xsl:for-each>
		<xsl:if test="not(*|text()|comment()|processing-instruction())">/</xsl:if>
		<xsl:text>&gt;</xsl:text>
	</xsl:template>
	
	<xsl:template name="write-endtag">
		<xsl:text>&lt;/</xsl:text>
		<xsl:value-of select="name()"/>
		<xsl:text>&gt;</xsl:text>
	</xsl:template>
	
	<xsl:template name="write-attribute">
		<xsl:text> </xsl:text>
		<xsl:value-of select="name()"/>
		<xsl:text>="</xsl:text>
		<xsl:value-of select="."/>
		<xsl:text>"</xsl:text>
	</xsl:template>
	
	<xsl:template match="*">
		<xsl:param name="indent-string" select="$indent-increment"/>
		<xsl:value-of select="$indent-string"/>
		<xsl:call-template name="write-starttag"/>
		<xsl:if test="*">
			<xsl:value-of select="$nl"/>
		</xsl:if>
		<xsl:apply-templates>
			<xsl:with-param name="indent-string" select="concat($indent-string, $indent-increment)"/>
		</xsl:apply-templates>
		<xsl:if test="*">
			<xsl:value-of select="$indent-string"/>
		</xsl:if>
		<xsl:if test="*|text()|comment()|processing-instruction()">
			<xsl:call-template name="write-endtag"/>
		</xsl:if>
        <xsl:value-of select="$nl"/>
	</xsl:template>
	
	<xsl:template match="text()">
        <xsl:copy-of select="."/>
	</xsl:template>

	<xsl:template match="@*">
        <xsl:copy/>
    </xsl:template>

</xsl:stylesheet>
