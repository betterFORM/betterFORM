<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    exclude-result-prefixes="xhtml xforms bf">

    <xsl:import href="dojo.xsl"/>
    <!-- ### CDN support is not avaibable for this stylesheet -->
    <xsl:variable name="useCDN" select="'false'"/>

    <xsl:template name="addDojoCSS"><xsl:text>
</xsl:text>
                <style type="text/css">
                    <xsl:choose>
                        <xsl:when test="//xhtml:body/@class='soria'">
                    @import "<xsl:value-of select="$contextroot"/>/bfResources/scripts/dijit/themes/soria/soria.css";
                        </xsl:when>
                        <xsl:otherwise>
                    @import "<xsl:value-of select="$contextroot"/>/bfResources/scripts/dijit/themes/tundra/tundra.css";
                        </xsl:otherwise>
                    </xsl:choose>
                    @import "<xsl:value-of select="$contextroot"/>/bfResources/scripts/dojo/resources/dojo.css";
                    @import "<xsl:value-of select="$contextroot"/>/bfResources/scripts/dojox/widget/Toaster/Toaster.css";
                    @import "<xsl:value-of select="$contextroot"/>/bfResources/scripts/dojox/layout/resources/FloatingPane.css";
	                @import "<xsl:value-of select="$contextroot"/>/bfResources/scripts/dojox/layout/resources/ResizeHandle.css";
                </style><xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template name="addDojoConfig">
        <script type="text/javascript">
            var djConfig = {
                debugAtAllCost:<xsl:value-of select="$debug-enabled"/>,
                locale:'en',
                isDebug:<xsl:value-of select="$debug-enabled"/>,
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
                dojo.require("betterform.BfRequiredMinimal");
    </xsl:template>

</xsl:stylesheet>
