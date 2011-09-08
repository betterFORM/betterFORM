<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="xsl mvn"
    xmlns:mvn="http://maven.apache.org/POM/4.0.0">
    <!--
        author: Joern Turner
        This stylesheet is used to update the dependencies in the Maven pom.xml file when changed in
        build.properties.xml.

    -->
    <xsl:param name="buildprops"/>

    <xsl:output method="text"/>
    <!-- todo: cut last comma -->
    <xsl:template match="/">runtime.libs=<xsl:for-each select="/root/core/compile//pathelement[@artifactId]">
        <xsl:choose>
            <xsl:when test="position() != last()"><xsl:value-of select="concat(substring-after(@location,'lib/'),',')"/></xsl:when>
            <xsl:otherwise><xsl:value-of select="substring-after(@location,'lib/')"/></xsl:otherwise>
        </xsl:choose>
</xsl:for-each>
    </xsl:template>


</xsl:stylesheet>
