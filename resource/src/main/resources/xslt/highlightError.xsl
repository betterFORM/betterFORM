<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<!-- $Id: sort-instance.xsl,v 1.4 2006/03/21 19:24:57 uli Exp $ -->
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bf="http://betterform.sourceforge.net/xforms">

    <xsl:output method="xhtml" encoding="UTF-8" indent="yes" version="1.0" doctype-system="/resources/xsd/xhtml1-transitional.dtd"/>
	<xsl:strip-space elements="bf:data"/>
    <xsl:param name="params" />

	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			<head>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
				<title>Error</title>
				<script type="text/javascript" src="{$params/context}/bfResources/scripts/syntaxhighlighter/scripts/shCore.js"></script>
				<script type="text/javascript" src="{$params/context}/bfResources/scripts/syntaxhighlighter/scripts/shBrushXml.js"></script>
				<link type="text/css" rel="stylesheet" href="{$params/context}/bfResources/scripts/syntaxhighlighter/styles/shCore.css"/>
				<link type="text/css" rel="stylesheet" href="{$params/context}/bfResources/scripts/syntaxhighlighter/styles/shCoreDefault.css"/>
				<script type="text/javascript">SyntaxHighlighter.all();</script><xsl:text>
</xsl:text>

                <style type="text/css">
                    body{
                        font-family:Tahoma;
                        font-size:14pt;
                        background:url('<xsl:value-of select="$params/context"/>/bfResources/images/bgOne.gif') repeat-x scroll;
                        font-family: san-serif;
                        color:#4682b4;
                    }
                    table{
                        margin:10px 0;
                        width: 750px;
                    }
                    td{
                        padding: 5px;
                        background: whitesmoke;
                    }
                    #errorBody{
                        width: 750px;
                        margin-left: auto;
                        margin-right: auto;
                        margin-top: 50px;
                    }
                    #errorIcon{
                        float: left;
                        margin-right: 10px;
                    }
                    .message1{
                        display:block;
                        color:steelblue;
                        font-weight:bold;
                    }
                    .message2{
                        display:block;
                        color:darkred;
                        font-size:16pt;
                        padding-top:30px;
                        font-weight:bold;
                    }

                    .syntaxhighlighter{
                        border:thin solid #999999;
                        margin: 20px 50px;
                        font-size: 10pt !important;
                    }
                </style>
			</head>

			<body>
                <div id="errorBody">
                    <img id="errorIcon" src="{$params/context}/bfResources/images/error.png" width="24" height="24" alt="Error"/>
                    <div class="message1">Oops, an error occured...<br/></div>
                    <div class="message2"><xsl:value-of select="$params/message"/></div>
                    <table>
                        <tr>
                            <td>URL</td>
                            <td><xsl:value-of select="$params/url"/></td>
                        </tr>
                        <tr>
                            <td>XPath</td>
                            <td><xsl:value-of select="$params/xpath"/></td>
                        </tr>
                    </table>
                    <pre class="brush: xml; highlight:[{$params//lineNumber}]">
                        <xsl:copy>
                            <xsl:apply-templates />
                        </xsl:copy>
                    </pre>
                    <div style="float:right;text-align:right;font-size:8pt;font-family: san-serif;" id="copyright">
                        <a href="http://www.betterform.de">
                            <img style="vertical-align:text-bottom; margin-right:5px;"
                                 src="{$params/context}/bfResources/images/betterform_icon16x16.png" alt="betterFORM project"/>
                        </a>
                        <span>&#xA9; 2012 betterFORM</span>
                    </div>
                </div>
			</body>
		</html>
	</xsl:template>

	<xsl:variable name="nl">
		<xsl:text></xsl:text>
	</xsl:variable>
	<xsl:variable name="indent-increment" select="''"/>

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

	<xsl:template match="comment()"/>
	<xsl:template match="@*|text()">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()"/>
        </xsl:copy>
    </xsl:template>


	<!--<xsl:template match="bf:data" priority="10"/>-->
	<!--<xsl:template match="xf:group[@appearance='repeated']"/>-->
	<!--<xsl:template match="@src"/>-->

<!--
	<xsl:template match="xf:repeat" priority="5">
		<xsl:copy>
			<xsl:copy-of select="@*"/>
			<xsl:apply-templates select="bf:data/xf:group/*"/>
		</xsl:copy>
	</xsl:template>
-->


</xsl:stylesheet>
