<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:gen = "betterFORM-mapping-generator"
    exclude-result-prefixes="bf xsl">
    <xsl:strip-space elements="*"/>
    <xsl:namespace-alias stylesheet-prefix="gen" result-prefix="xsl"/>


    <xsl:template match="/mapping">
<gen:stylesheet version="1.0">

     <xsl:for-each select="*">



         <xsl:variable name="xfTypeNamespace" select="if(contains(@type,':')) then substring-after(@type, ':') else @type"/>
         <xsl:variable name="xfTypeSlash" select="translate($xfTypeNamespace, '/','')"/>
         <xsl:variable name="xfTypeColon" select="translate($xfTypeSlash, ':','')"/>
         <xsl:variable name="xfType" select="translate($xfTypeColon, '*', '-')"/>
          
         <xsl:variable name="xfAppearanceSlash" select="translate(@appearance, '/','')"/>
         <xsl:variable name="xfAppearanceColon" select="translate($xfAppearanceSlash, ':','')"/>
         <xsl:variable name="xfAppearance" select="translate($xfAppearanceColon, '*', '-')"/>

         <xsl:variable name="xfMediatypeSlash" select="translate(@mediatype, '/','')"/>
         <xsl:variable name="xfMediatypeColon" select="translate($xfMediatypeSlash, ':','')"/>
         <xsl:variable name="xfMediatype" select="translate($xfMediatypeColon, '*', '-')"/>


<gen:template name="{concat(local-name(), $xfType,$xfAppearance,$xfMediatype)}">
    <xsl:copy-of select="xhtml:template/*[1]"/>
</gen:template>
     </xsl:for-each>

</gen:stylesheet>
    </xsl:template>


    
</xsl:stylesheet>
