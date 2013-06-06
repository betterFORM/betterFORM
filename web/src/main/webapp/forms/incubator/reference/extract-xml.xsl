<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                exclude-result-prefixes="xhtml">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" media-type="text/xml"/>

    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <data xmlns:xf="http://www.w3.org/2002/xforms">
            <xsl:apply-templates/>
        </data>
    </xsl:template>

    <xsl:template match="xhtml:head">
        <xsl:apply-templates mode="head"/>
    </xsl:template>

    <xsl:template match="xhtml:title" mode="head" >
        <title><xsl:value-of select="."/></title>
    </xsl:template>


    <xsl:template match="xhtml:div[@id='xforms']">
        <xsl:message>XForms</xsl:message>
        <page>
            <xsl:apply-templates mode="page"/>
            <content>
                <xsl:apply-templates mode="content"/>
            </content>
        </page>
    </xsl:template>

    <xsl:template match="xhtml:div[@class='pagetitle']" mode="page">
        <xsl:message>Page</xsl:message>
        <title><xsl:value-of select="."/></title>
    </xsl:template>

    <xsl:template match="xhtml:div[@class='Section']" mode="page">
        <xsl:if test="exists(./xhtml:div[@class='Headline'])">
            <xsl:choose>
                <xsl:when test="exists(./xhtml:div[@class='Headline']/xhtml:a)">
                    <specification>
                        <link><xsl:value-of select="./xhtml:div[@class='Headline']/xhtml:a/@href"/></link>
                        <description><xsl:value-of select="./xhtml:div[@class='Headline']/xhtml:a"/></description>
                    </specification>
                </xsl:when>
                <xsl:otherwise>
                    <description>
                        <xsl:value-of select="."/>
                    </description>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:if>
    </xsl:template>



    <xsl:template match="xf:model" mode="content">
        <xsl:message>Model</xsl:message>
        <models>
            <xsl:copy-of select="."/>
        </models>
    </xsl:template>

    <xsl:template match="xhtml:div[@class='Section']" mode="content">
        <xsl:message>Samples</xsl:message>
        <xsl:if test="exists(./xhtml:div[@class='Subsection'])">
            <samples>
                <xsl:apply-templates mode="content"/>
            </samples>
        </xsl:if>
    </xsl:template>

    <xsl:template match="xhtml:div[@class='Subsection']" mode="content">
        <xsl:message>Section</xsl:message>
        <section>
            <title><xsl:value-of select="./@title"/></title>
            <description><xsl:value-of select="./xhtml:div[@class='description']"/></description>
            <xsl:apply-templates mode="content"/>            
        </section>
    </xsl:template>

    <xsl:template match="xhtml:div[@class='Sample']" mode="content">
        <xsl:message>XForms Sample</xsl:message>
        <xsl:copy-of select="./xf:*"/>                
    </xsl:template>

    <!-- Strip unneeded Text -->
    <xsl:template match="text()" mode="head"/>
    <xsl:template match="text()" mode="page"/>
    <xsl:template match="text()" mode="content"/>

</xsl:stylesheet>
