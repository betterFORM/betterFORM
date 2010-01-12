<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                version="2.0"
                xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance'
                xmlns:bf="http://betterform.sourceforge.net/xforms">
    
    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>

    <!-- Copyright 2008 - Joern Turner, Lars Windauer
         Licensed under the terms of BSD and Apache 2 Licenses -->
    
    <!-- put the "schemaLocation" attributes on the root node -->
    <xsl:template match="/*">

        <xsl:copy>
            <!-- copy all attributes except schemaLocation et noNamespaceSchemaLocation -->
            <xsl:for-each select="@*">
                <xsl:if test="(name(.)!='schemaLocation') and (name(.)!='noNamespaceSchemaLocation')">
                    <xsl:copy/>
                </xsl:if>
            </xsl:for-each>

            <!-- schemaLocation attributes -->
            <xsl:attribute name="xsi:schemaLocation">http://www.w3.org/2002/xforms xforms.xsd http://betterform.sourceforge.net/xforms betterform.xsd http://www.w3.org/1999/xlink xlink.xsd</xsl:attribute>

            <xsl:apply-templates/>
        </xsl:copy>

    </xsl:template>

    <xsl:template match="*">
        <xsl:copy>
            <xsl:for-each select="@*">
                <!--if xforms:attribute, replace by attribute-->
                <xsl:if test="name(.)=concat('xforms:', local-name(.))">
                    <xsl:variable name="attributeName">
                        <xsl:value-of select="local-name(.)"/>
                    </xsl:variable>
                    <xsl:attribute name="{$attributeName}">
                        <xsl:value-of select="."/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="name(.)!=concat('xforms:', local-name(.))">
                    <xsl:copy/>
                </xsl:if>
            </xsl:for-each>

            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
