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

    <xsl:template name="addDojoCSS"><xsl:text>
</xsl:text>
                <style type="text/css">
                    <xsl:choose>
                        <xsl:when test="contains(//body/@class, 'soria')">
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dijit/themes/soria/soria.css";
                        </xsl:when>
                        <xsl:when test="contains(//body/@class, 'nihilo')">
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dijit/themes/nihilo/nihilo.css";
                        </xsl:when>
                         <xsl:when test="contains(//body/@class, 'claro')">
                    @import "<xsl:value-of select="$contextroot"/>dijit/themes/claro/claro.css";
                        </xsl:when>
                        <xsl:otherwise>
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dijit/themes/tundra/tundra.css";
                        </xsl:otherwise>
                    </xsl:choose>
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dojo/resources/dojo.css";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dojox/widget/Toaster/Toaster.css";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dojox/layout/resources/FloatingPane.css";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath)"/>dojox/layout/resources/ResizeHandle.css";
                </style><xsl:text>
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
    </xsl:template>

    <xsl:template name="addDojoRequires">
                dojo.require("betterform.bfRequiredFull");
                var bfRequiredFull = new betterform.bfRequiredFull();

    </xsl:template>

</xsl:stylesheet>
