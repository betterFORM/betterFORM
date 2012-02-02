<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
        xmlns:html="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xf="http://www.w3.org/2002/xforms"
        xmlns:ev="http://www.w3.org/2001/xml-events"
        exclude-result-prefixes="html xsl xf ev">

    <!-- Copyright 2009 Lars Windauer, Joern Turner -->

    <xsl:output method="xml" version="1.0" indent="yes"/>

    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="html:html">
        <xsl:variable name="root" select="."/>
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="html:head"/>
            <xsl:apply-templates mode="model" select="$root/*"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:head">
        <xsl:copy-of select="."/>
    </xsl:template>


    <xsl:template match="*|@*|text()" mode="modelbinds">
        <xsl:apply-templates select="*|@*|text()" mode="modelbinds"/>
    </xsl:template>

    <xsl:template match="*|@*|text()" mode="model">
        <xsl:apply-templates select="*|@*|text()" mode="model"/>
    </xsl:template>

    <xsl:template match="html:body" mode="model">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:element name="div" namespace="http://www.w3.org/1999/xhtml">
                <xsl:attribute name="class">caForm</xsl:attribute>
                <xsl:apply-templates select="html:form" mode="model"/>
                <xsl:apply-templates  mode="ui" />
            </xsl:element>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:form" mode="model">
        <xsl:apply-templates mode="model"/>
    </xsl:template>

    <xsl:template match="html:form" mode="ui">
        <xsl:apply-templates mode="ui"/>
    </xsl:template>

    <xsl:template match="xf:model" mode="model">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <!-- copy model actions -->
            <xsl:for-each select="xf:action[@ev:event]">
                <xsl:copy-of select="."/>
            </xsl:for-each>

            <!--<xsl:call-template name="buildDefaultInstance"/>-->

            <xsl:for-each select="xf:instance"><xsl:copy-of select="."/></xsl:for-each>

            <xsl:for-each select="xf:submission">
                <xsl:copy-of select="."/>
            </xsl:for-each>

            <xsl:apply-templates select="../xf:*[not(xf:model)]" mode="modelbinds"/>
            <xsl:apply-templates mode="model"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:form[not(xf:model)]" mode="model">
        <xf:model>
            <xf:instance>
                <data xmlns="">
                    <xsl:for-each select="*[@name]">
                        <xsl:apply-templates select="." mode="generateDataInstance"/>
                    </xsl:for-each>
                </data>
            </xf:instance>
            <xsl:for-each select="//*[./@name]">
                <xsl:apply-templates select="." mode="modelbinds"/>
            </xsl:for-each>
        </xf:model>
    </xsl:template>

    <xsl:template match="*[@name]" mode="generateDataInstance">
        <xsl:element name="{@name}">
            <xsl:if test="@value"><xsl:value-of select="@value"/></xsl:if>
            <xsl:apply-templates select="*[@name]" mode="generateDataInstance"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xf:bind" mode="modelbinds">
        <xsl:message>WARNING: existing bind overwrites any xforms attributes on controls</xsl:message>
        <xsl:copy-of select="."/>
    </xsl:template>

    <!-- nothing working here yet - to be continued... -->
    <xsl:template name="buildDefaultInstance">
        <xsl:variable name="id" select="@id"/>
        <xf:instance id="default">
            <!--<xsl:for-each select="//*[starts-with(@ref,$instancePath) or starts-with(@nodeset,$instancePath)]">-->
            <data>
                <xsl:for-each select="//*[@ref or @nodeset]">
                    <xsl:variable name="xpath-binding">
                        <xsl:choose>
                            <xsl:when test="@nodeset"><xsl:value-of select="@nodeset"/></xsl:when>
                            <xsl:otherwise><xsl:value-of select="@ref"/></xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>


<xsl:if test="not(starts-with($xpath-binding,'instance('))">
                    <xsl:choose>
                        <xsl:when test="starts-with($xpath-binding,'@')">
                            <xsl:attribute name="{substring($xpath-binding,2)}"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:element name="{$xpath-binding}"/>
                        </xsl:otherwise>
                    </xsl:choose>

</xsl:if>

                </xsl:for-each>
            </data>
        </xf:instance>
    </xsl:template>

    <xsl:template match="xf:group" mode="model">
        <xsl:apply-templates mode="model"/>
    </xsl:template>


    <xsl:template match="*[@ref] | *[@nodeset] | *[@name]" mode="modelbinds">
        <xsl:variable name="parent" select="."/>
        <xsl:message>Constraint...: <xsl:value-of select="."/></xsl:message>
        <xsl:if test="string-length($parent/@constraint) &gt; 0
                     or string-length($parent/@datatype) &gt; 0
                     or string-length($parent/@readonly) &gt; 0
                     or string-length($parent/@relevant) &gt; 0
                     or string-length($parent/@required) &gt; 0
                     or string-length($parent/@calculate) &gt; 0">

            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="currNodeset">
                <xsl:choose>
                    <xsl:when test="@nodeset"><xsl:value-of select="@nodeset"/></xsl:when>
                    <xsl:when test="@ref"><xsl:value-of select="@ref"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
                </xsl:choose>
            </xsl:variable>


            <xsl:variable name="constraint">
                <xsl:choose>
                    <xsl:when test="$parent/@constraint"><xsl:value-of select="$parent/@constraint"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@constraint | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@constraint)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="type">
                <xsl:choose>
                    <xsl:when test="$parent/@datatype"><xsl:value-of select="$parent/@datatype"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select=".//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@datatype | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@datatype"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="readonly">
                <xsl:choose>
                    <xsl:when test="$parent/@readonly"><xsl:value-of select="$parent/@readonly"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@readonly | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@readonly)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="relevant">
                <xsl:choose>
                    <xsl:when test="$parent/@relevant"><xsl:value-of select="$parent/@relevant"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@relevant | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@relevant)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="required">
                <xsl:choose>
                    <xsl:when test="$parent/@required"><xsl:value-of select="$parent/@required"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@required | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@required)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="calculate">
                <xsl:choose>
                    <xsl:when test="$parent/@calculate"><xsl:value-of select="$parent/@calculate"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@calculate | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@calculate)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <!-- search all descendants and following nodes -->
            <xsl:for-each select=".//xf:*[@nodeset=$currNodeset or @ref=$currNodeset] | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]">
                <xsl:variable name="targetId" select="@id"/>
                <xsl:if test="@constraint">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING 'type' type="<xsl:value-of select="@constraint"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@datatype">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING 'type' type="<xsl:value-of select="@datatype"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@readonly">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING readonly="<xsl:value-of select="@readonly"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@relevant">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING relevant="<xsl:value-of select="@relevant"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@required">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING required="<xsl:value-of select="@required"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@calculate">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING calculate="<xsl:value-of select="@calculate"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
            </xsl:for-each>
            <!-- TODO: expression must be adjusted for various kind of actions (setvalue, insert....) -->
            <xsl:message>create bind: <xsl:value-of select="not(ancestor::xf:*[@nodeset=$currNodeset or @ref=$currNodeset] | preceding::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]) or preceding::xf:*[@nodeset=$currNodeset or @ref=$currNodeset][name() = 'xf:setvalue']"/> </xsl:message>
            <xsl:if test="not(ancestor::*[@nodeset=$currNodeset or @ref=$currNodeset] | preceding::*[@nodeset=$currNodeset or @ref=$currNodeset]) or preceding::*[@nodeset=$currNodeset or @ref=$currNodeset][name() = 'xf:setvalue'] ">

                <xsl:element name="xf:bind" namespace="http://www.w3.org/2002/xforms">
                    <!--<xsl:apply-templates select="$parent/@nodeset" mode="model"/>-->
                    <xsl:attribute name="nodeset"><xsl:value-of select="$currNodeset"/></xsl:attribute>
                    <xsl:if test="$constraint != ''">
                        <xsl:attribute name="constraint"><xsl:value-of select="$constraint"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$type !=''">
                        <xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$readonly !=''">
                        <xsl:attribute name="readonly"><xsl:value-of select="$readonly"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$relevant != ''">
                        <xsl:attribute name="relevant"><xsl:value-of select="$relevant"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$required != ''">
                        <xsl:attribute name="required"><xsl:value-of select="$required"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$calculate != ''">
                        <xsl:attribute name="calculate"><xsl:value-of select="$calculate"/></xsl:attribute>
                    </xsl:if>
                </xsl:element>
            </xsl:if>
        </xsl:if>
    </xsl:template>
<!--
    <xsl:template match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:trigger|xf:upload" mode="modelbinds">
        <xsl:variable name="parent" select="."/>

        <xsl:if test="string-length(@constraint) &gt; 0
                     or string-length(@datatype) &gt; 0
                     or string-length(@readonly) &gt; 0
                     or string-length(@relevant) &gt; 0
                     or string-length(@required) &gt; 0
                     or string-length(@calculate) &gt; 0">

            <xsl:variable name="id" select="@id"/>
            <xsl:variable name="currNodeset">
                <xsl:choose>
                    <xsl:when test="@nodeset"><xsl:value-of select="@nodeset"/></xsl:when>
                    <xsl:when test="@ref"><xsl:value-of select="@ref"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="@name"/></xsl:otherwise>
                </xsl:choose>
            </xsl:variable>


            <xsl:variable name="constraint">
                <xsl:choose>
                    <xsl:when test="$parent/@constraint"><xsl:value-of select="$parent/@constraint"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@constraint | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@constraint)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="type">
                <xsl:choose>
                    <xsl:when test="$parent/@datatype"><xsl:value-of select="$parent/@datatype"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select=".//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@datatype | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@datatype"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="readonly">
                <xsl:choose>
                    <xsl:when test="$parent/@readonly"><xsl:value-of select="$parent/@readonly"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@readonly | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@readonly)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="relevant">
                <xsl:choose>
                    <xsl:when test="$parent/@relevant"><xsl:value-of select="$parent/@relevant"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@relevant | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@relevant)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="required">
                <xsl:choose>
                    <xsl:when test="$parent/@required"><xsl:value-of select="$parent/@required"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@required | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@required)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <xsl:variable name="calculate">
                <xsl:choose>
                    <xsl:when test="$parent/@calculate"><xsl:value-of select="$parent/@calculate"/></xsl:when>
                    <xsl:otherwise>
                         <xsl:value-of select="normalize-space(.//xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@calculate | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]/@calculate)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            -->
<!-- search all descendants and following nodes -->
<!--
            <xsl:for-each select=".//xf:*[@nodeset=$currNodeset or @ref=$currNodeset] | following::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]">
                <xsl:variable name="targetId" select="@id"/>
                <xsl:if test="@constraint">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING 'type' type="<xsl:value-of select="@constraint"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@datatype">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING 'type' type="<xsl:value-of select="@datatype"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@readonly">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING readonly="<xsl:value-of select="@readonly"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@relevant">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING relevant="<xsl:value-of select="@relevant"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@required">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING required="<xsl:value-of select="@required"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
                <xsl:if test="@calculate">
                    <xsl:message>id="<xsl:value-of select="$parent/@id"/>" - IGNORING calculate="<xsl:value-of select="@calculate"/>" on id:<xsl:value-of select="$targetId"/></xsl:message>
                </xsl:if>
            </xsl:for-each>
            -->
<!-- TODO: expression must be adjusted for various kind of actions (setvalue, insert....) -->
<!--
            <xsl:if test="not(ancestor::xf:*[@nodeset=$currNodeset or @ref=$currNodeset] | preceding::xf:*[@nodeset=$currNodeset or @ref=$currNodeset]) or preceding::xf:*[@nodeset=$currNodeset or @ref=$currNodeset][name() = 'xf:setvalue'] ">

                <xsl:element name="xf:bind" namespace="http://www.w3.org/2002/xforms">
                    -->
<!--<xsl:apply-templates select="$parent/@nodeset" mode="model"/>-->
<!--
                    <xsl:attribute name="nodeset"><xsl:value-of select="$currNodeset"/></xsl:attribute>
                    <xsl:if test="$constraint != ''">
                        <xsl:attribute name="constraint"><xsl:value-of select="$constraint"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$type !=''">
                        <xsl:attribute name="type"><xsl:value-of select="$type"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$readonly !=''">
                        <xsl:attribute name="readonly"><xsl:value-of select="$readonly"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$relevant != ''">
                        <xsl:attribute name="relevant"><xsl:value-of select="$relevant"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$required != ''">
                        <xsl:attribute name="required"><xsl:value-of select="$required"/></xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$calculate != ''">
                        <xsl:attribute name="calculate"><xsl:value-of select="$calculate"/></xsl:attribute>
                    </xsl:if>
                </xsl:element>
            </xsl:if>
        </xsl:if>
    </xsl:template>
-->

    <xsl:template match="xf:submit" mode="model">
        <xsl:variable name="submit" select="."/>
        <xsl:element name="xf:submission" namespace="http://www.w3.org/2002/xforms">
            <xsl:copy-of select="$submit/@*"/>
            <xsl:copy-of select="$submit/xf:action"/>

<!--
            <xsl:for-each select="$submit/xf:*">
                <xsl:apply-templates mode="model"/>
            </xsl:for-each>
-->
        </xsl:element>
    </xsl:template>


 <!--  <xsl:template match="@constraint" mode="model">
        <xsl:attribute name="constraint">
            <xsl:copy-of select="normalize-space(.)"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="@datatype" mode="model">
        <xsl:attribute name="type">
            <xsl:copy-of select="normalize-space(.)"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="@readonly" mode="model">
        <xsl:attribute name="readonly">
            <xsl:copy-of select="normalize-space(.)"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="@relevant" mode="model">
        <xsl:attribute name="relevant">
            <xsl:copy-of select="normalize-space(.)"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="@required" mode="model">
        <xsl:attribute name="required">
            <xsl:copy-of select="normalize-space(.)"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="@calculate" mode="model">
        <xsl:attribute name="calculate">
            <xsl:copy-of select="normalize-space(.)"/>
        </xsl:attribute>
    </xsl:template>

-->
    <!-- second phase UI processing -->
    <xsl:template match="*|@*|text()" mode="ui">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()" mode="ui"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="html:head" mode="ui" />

    <xsl:template match="xf:instance" mode="ui"/>
    <xsl:template match="xf:submission" mode="ui"/>

    <!--<xsl:template match="xf:model/xf:action" mode="ui"/>-->

    <xsl:template match="xf:model/*[@ev:event]" mode="ui"/>

    <xsl:template match="html:body" mode="ui">
        <xsl:apply-templates mode="ui"/>
    </xsl:template>

    <xsl:template match="xf:model" mode="ui"/>

    <xsl:template match="xf:group" mode="ui">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="ui"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xf:label" mode="ui">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="ui"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:trigger|xf:upload" mode="ui">

        <xsl:copy>
            <xsl:apply-templates select="@*" mode="ui"/>
            <xsl:apply-templates select="*" mode="ui"/>
        </xsl:copy>

    </xsl:template>

    <xsl:template match="xf:submit" mode="ui">

         <xsl:variable name="parent" select="."/>
        <xsl:element name="xf:submit" namespace="http://www.w3.org/2002/xforms">
            <xsl:attribute name="submission"><xsl:value-of select="$parent/@id"/></xsl:attribute>
            <xsl:attribute name="id"><xsl:value-of select="concat($parent/@id,'-trigger')"/></xsl:attribute>
            <xsl:apply-templates select="$parent/xf:label" mode="ui"/>
        </xsl:element>

    </xsl:template>

    <xsl:template match="xf:alert" mode="ui">

        <xsl:variable name="alertText" select="."/>
        <xf:alert><xsl:value-of select="$alertText"/></xf:alert>
        <xf:hint><xsl:value-of select="$alertText"/></xf:hint>

    </xsl:template>


    <xsl:template match="@nodeset" mode="ui">
        <xsl:variable name="parent" select=".."/>

        <xsl:choose>
            <xsl:when test="name($parent) ='xf:itemset'
                            or
                            name($parent) ='xf:repeat'
                            or
                            name($parent) = 'xf:insert'
                            or
                            name($parent) = 'xf:delete'">
                <xsl:attribute name="nodeset"><xsl:value-of select="."/></xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="ref"><xsl:value-of select="."/></xsl:attribute>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="@constraint" mode="ui"/>
    <xsl:template match="@datatype" mode="ui"/>
    <xsl:template match="@readonly" mode="ui"/>
    <xsl:template match="@relevant" mode="ui"/>
    <xsl:template match="@required" mode="ui"/>
    <xsl:template match="@calculate" mode="ui"/>
</xsl:stylesheet>


