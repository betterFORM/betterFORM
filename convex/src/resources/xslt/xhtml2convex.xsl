<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xf="http://www.w3.org/2002/xforms"
    exclude-result-prefixes="xhtml xf">


    <xsl:output method="html" version="4.0" indent="yes" encoding="UTF-8"/>

    <xsl:strip-space elements="xf:model"/>
    <!-- ********************************* TEMPLATES ********************************   -->
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="*|@*|text()">
        <xsl:apply-templates select="*|@*|text()"/>
    </xsl:template>


    <xsl:template match="xhtml:head">
        <xsl:variable name="head" select="."/>
        <xsl:copy>
            <xsl:copy-of select="xhtml:title"/>

            <link rel="stylesheet" type="text/css" href="../resources/css/convex.css"></link>
            <script type="text/javascript" src="../../../forms/script/convex.js"></script>

            <xsl:if test=".//xf:model">
                <script type="text/xml">
                    <xsl:for-each select="$head//xf:model">
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </script>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xhtml:body">
    </xsl:template>


</xsl:stylesheet>
