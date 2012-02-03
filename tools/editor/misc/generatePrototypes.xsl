<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
    <xsl:output method="xml" indent="yes" exclude-result-prefixes="xf"/>
    <!-- author: Joern Turner -->
    <!--todo: prototypes -->
    <xsl:param name="webxml.path" select="''"/>

    <xsl:variable name="inputDoc" select="/"/>
    <xsl:template match="/xsd:schema">
        <data>
            <xsl:for-each select=".//xsd:element[@name]">
                <xsl:variable name="current" select="."/>
                <xfElement>
                    <xsl:attribute name="type"><xsl:value-of select="$current/@name"/></xsl:attribute>
                    <xsl:attribute name="level">0</xsl:attribute>
                    <xsl:apply-templates select="$current//xsd:attributeGroup" mode="instance"/>
                    <xsl:apply-templates select="$current//xsd:attribute" mode="instance"/>

                    <!--todo:go deeper here when further automatic handling is wanted!-->
                    <!--<xsl:apply-templates select="xsd:sequence"/>-->
                </xfElement>
            </xsl:for-each>
        </data>
    </xsl:template>

    <xsl:template match="xsd:complexType">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="xsd:sequence">
        <xsl:apply-templates />
    </xsl:template>



    <xsl:template match="xsd:attributeGroup[@ref]" mode="instance">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetGroup" mode="instance"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name]" mode="instance">
        <xsl:apply-templates select="xsd:attribute" mode="instance"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@name]" mode="instance">
        <xsl:attribute name="{@name}">
            <xsl:if test="@default">
                <xsl:value-of select="@default"/>    
            </xsl:if>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xsd:attribute[@ref]" mode="instance">
        <xsl:variable name="targetAttribute" select="//xsd:attribute[@name = substring-after(@ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetAttribute" mode="instance"/>
    </xsl:template>

    <xsl:template match="*|@*|node()" mode="instance"/>


<!--
    <xsl:template match="xsd:attributeGroup[@ref]" mode="bind">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetGroup" mode="bind"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name]" mode="bind">
        <xsl:apply-templates select="xsd:attribute" mode="bind"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@name]" mode="bind">
        <xf:bind nodeset="{@name}" type="{@type}"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@ref]" mode="bind">
        <xsl:variable name="targetAttribute" select="//xsd:attribute[@name = substring-after(@ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetAttribute" mode="bind"/>
    </xsl:template>

    <xsl:template match="*|@*|node()" mode="bind"/>


    <xsl:template match="xsd:attributeGroup[@ref]" mode="ui">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetGroup" mode="ui"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name]" mode="ui">
        <xsl:apply-templates select="xsd:attribute" mode="ui"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@name]" mode="ui">
        <xf:input ref="@{@name}">
            <xf:label><xsl:value-of select="./@name"/></xf:label>
        </xf:input>
    </xsl:template>

    <xsl:template match="xsd:attribute[@ref]" mode="ui">
        <xsl:variable name="targetAttribute" select="//xsd:attribute[@name = substring-after(@ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetAttribute"/>
    </xsl:template>
-->





</xsl:stylesheet>
