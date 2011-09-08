<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    xmlns:xf="http://www.w3.org/2002/xforms"
    exclude-result-prefixes="xf bf xsl html">

    <!-- ### this url will be used to build the form action attribute ### -->
    <xsl:param name="sessionKey" select="''"/>
    <xsl:param name="contextName" select="''"/>

    <xsl:output method="html" version="4.0" encoding="UTF-8" media-type="text/html" indent="yes"/>
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <html>
            <head>
                <title></title>
                <link href="{$contextName}/bfResources/scripts/syntaxhighlighter_3.0.83/styles/shCore.css" rel="stylesheet" type="text/css" /><xsl:text>
</xsl:text>
                <link href="{$contextName}/bfResources/scripts/syntaxhighlighter_3.0.83/styles/shThemeDefault.css"  rel="stylesheet" type="text/css" /><xsl:text>
</xsl:text>


                <script type="text/javascript" src="{$contextName}/bfResources/scripts/syntaxhighlighter_3.0.83/scripts/shCore.js"/><xsl:text>
</xsl:text>
                <script type="text/javascript" src="{$contextName}/bfResources/scripts/syntaxhighlighter_3.0.83/scripts/shBrushXml.js"></script><xsl:text>
</xsl:text>
            </head>
            <body><xsl:text>
</xsl:text>
                <pre class="brush: xml">
                    <xsl:apply-templates mode="escape"/>
                    <!--<xsl:copy-of select="."/>-->

                </pre>
<!--
                <script type="syntaxhighlighter" class="brush: xml"><![CDATA[
                    <xsl:apply-templates/>
                ]]></script>
-->
                <script type="text/javascript">
                     SyntaxHighlighter.all();
                </script>
            </body>
        </html>
    </xsl:template>

    <xsl:template match="*" mode="escape" priority="10">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:apply-templates mode="escape" select="@*"/>
        <xsl:text>&gt;</xsl:text><xsl:text>
        </xsl:text>
            <xsl:apply-templates mode="escape"/><xsl:text>
        </xsl:text>
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>&gt;</xsl:text><xsl:text>
        </xsl:text>
    </xsl:template>

    <xsl:template match="text()" mode="escape">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="@*" mode="escape" priority="10">
        <xsl:text> </xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>="</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>"</xsl:text>
    </xsl:template>

</xsl:stylesheet>
