<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
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
</xsl:stylesheet>
