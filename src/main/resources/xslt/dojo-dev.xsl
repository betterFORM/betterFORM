<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    exclude-result-prefixes="xforms bf"
    xpath-default-namespace= "http://www.w3.org/1999/xhtml">

    <xsl:import href="dojo.xsl"/>
    <!-- ### CDN support is not avaibable for this stylesheet -->
    <xsl:variable name="useCDN" select="'false'"/>
    <!-- path to core CSS file -->
    <xsl:param name="CSSPath" select="concat($resourcesPath,'styles/')"/>

    <!-- ### the CSS stylesheet to use ### -->
    <xsl:variable name="default-css" select="concat($contextroot,$CSSPath,'xforms.css')"/>
    <xsl:variable name="betterform-css" select="concat($contextroot,$CSSPath,'betterform.css')"/>
    <xsl:variable name="custom-css" select="concat($contextroot,$CSSPath,'custom.css')"/>

    
<xsl:template name="addDojoCSS"><xsl:text>
</xsl:text>
                <xsl:variable name="cssTheme">
                    <xsl:choose>
                        <xsl:when test="contains(//body/@class, 'tundra')">tundra</xsl:when>
                        <xsl:when test="contains(//body/@class, 'soria')">soria</xsl:when>
                        <xsl:when test="contains(//body/@class, 'nihilo')">nihilo</xsl:when>
                        <xsl:when test="contains(//body/@class, 'claro')">claro</xsl:when>
                        <xsl:when test="contains(//body/@class, 'a11y')">a11y</xsl:when>
                        <xsl:otherwise><xsl:value-of select="$defaultTheme"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <style type="text/css">
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath, 'dijit/themes/', $cssTheme, '/', $cssTheme,'.css')"/>";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath, 'dojo/resources/dojo.css')"/>";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath, 'dojox/widget/Toaster/Toaster.css')"/>";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath, 'dojox/layout/resources/FloatingPane.css')"/>";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath, 'dojox/layout/resources/ResizeHandle.css')"/>";
                </style><xsl:text>
</xsl:text>
    </xsl:template>
    

    <xsl:template name="addCSS"><xsl:text>
</xsl:text>
                <style type="text/css">
                    <xsl:call-template name="chooseTheme"/>
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dojo/resources/dojo.css";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dojox/widget/Toaster/Toaster.css";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dojox/layout/resources/FloatingPane.css";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dojox/layout/resources/ResizeHandle.css";
                </style><xsl:text>
</xsl:text>
        <!-- include betterForm default stylesheet -->
        <link rel="stylesheet" type="text/css" href="{$default-css}"/><xsl:text>
</xsl:text>
        <link rel="stylesheet" type="text/css" href="{$betterform-css}"/><xsl:text>
</xsl:text>
        <link rel="stylesheet" type="text/css" href="{$custom-css}"/><xsl:text>
</xsl:text>
        <!-- copy user-defined stylesheets and inline styles -->
        <xsl:call-template name="getLinkAndStyle"/>
<xsl:text>
</xsl:text>

    </xsl:template>

    <xsl:template name="addDojoConfig">
        <script type="text/javascript">
            var djConfig = {
                debugAtAllCosts:false,
                locale:'<xsl:value-of select="$locale"/>',
                isDebug:false,
                parseOnLoad:false
            };
        </script><xsl:text>
</xsl:text>
    </xsl:template>

    <!-- ### the dev stylesheet currently does not support to use a CDN in conjunction with local code but
    that's probably a exotic use case. -->
    <xsl:template name="addDojoImport">
        <script type="text/javascript" src="{concat($contextroot,$scriptPath,'dojo/dojo.js')}"> </script><xsl:text>
</xsl:text>
         <xsl:choose>
            <xsl:when test="exists(//script[@id='betterformJs'])">
                <script type="text/javascript">
                    var isBetterFORMRelease = false;
                </script><xsl:text>
</xsl:text>
            </xsl:when>
            <xsl:otherwise>
                 <script type="text/javascript">
                    var isBetterFORMRelease = false;
                    dojo.require("betterform.BfRequiredFull");
                 </script><xsl:text>
</xsl:text>
             </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="addDojoRequires">
<!--
                dojo.require("betterform.BfRequiredFull");
                var bfRequiredFull = new betterform.BfRequiredFull();
-->

    </xsl:template>

</xsl:stylesheet>
