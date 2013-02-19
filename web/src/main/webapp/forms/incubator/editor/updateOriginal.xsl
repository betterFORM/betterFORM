<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="no"/>
    <!-- author: Joern Turner -->

    <!--
    updates the content of the original document (the XForms document that is edited) with the markup
    changed by the editor (newDoc).

    The transform traverses the original document and checks if there's a @oid in newDoc that matches the @id
    of the original document. If so the element from newDoc replaces the element in the original document.
    -->
    <xsl:param name="originDoc" />
    <xsl:variable name="newDoc" select="/"/>

    <xsl:template match="/">
       <xsl:apply-templates select="document($originDoc)/*" />
    </xsl:template>

    <xsl:template match="*[@id]" priority="10">
        <xsl:variable name="id" select="@id"/>

        <xsl:if test="$originDoc=''">
            <xsl:message terminate="yes">Original document not found: <xsl:value-of select="$originDoc"/></xsl:message>
        </xsl:if>
        
        <xsl:choose>
            <xsl:when test="$id[.=$newDoc//@oid]">
                <xsl:message>yes it did: <xsl:value-of select="$id"/> </xsl:message>
                <xsl:copy-of select="$newDoc//*[@oid=$id]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:copy-of select="@*"/>
                    <xsl:apply-templates />
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*|@*|text()|comment()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
