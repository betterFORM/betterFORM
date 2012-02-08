<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes"/>
    <!-- author: Joern Turner -->

    <xsl:template match="/*">
        <data>
            <!-- <xsl:for-each select="//*[namespace-uri()='http://www.w3.org/2002/xforms']"> -->
            <xsl:for-each select="//xf:*[not(ancestor::xf:*)]">
                <xsl:apply-templates select="." />
            </xsl:for-each>
        </data>
    </xsl:template>

    <xsl:template match="xf:*">
        <div id="{@id}" name="{local-name()}" class="{local-name()}">
            <xsl:variable name="level">
                <xsl:value-of select="count(ancestor::xf:*) + 1" />
            </xsl:variable>
            <xsl:attribute name="level"><xsl:value-of select="$level"/></xsl:attribute>
             <xsl:for-each select="@*">
                 <xsl:copy-of select="."/>
             </xsl:for-each>
            <xsl:apply-templates  />
         </div>
     </xsl:template>

    <xsl:template match="xf:*/text()"/>
</xsl:stylesheet>
