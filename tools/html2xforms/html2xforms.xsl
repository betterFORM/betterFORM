<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:ev="http://www.w3.org/2001/xml-events"
        xmlns:xi="http://www.w3.org/2001/XInclude"
        xmlns:bfc="http://betterform.sourceforge.net/xforms/controls"
        xmlns:xf="http://www.w3.org/2002/xforms"
        xmlns:bf="http://betterform.sourceforge.net/xforms"
        exclude-result-prefixes="bf xsl">

    <!-- 'data' will be passed in case we deal with a html form submit and second layer validation -->
    <xsl:param name="data" select="'record:foo;trackedDate:bar;created:heute;project:mine;duration:3;'"/>
    <!--<xsl:param name="data" select="''"/>-->

    <xsl:output method="xhtml" omit-xml-declaration="yes"/>
    <!--
    Transforms sanitized HTML5 documents into into xforms elements.
    -->
    <xsl:strip-space elements="*"/>
    <xsl:template match="/*">
        <xsl:copy>
            <xsl:namespace name="xf" select="'http://www.w3.org/2002/xforms'"/>
            <xsl:copy-of select="@*"/>


            <xsl:apply-templates/>

        </xsl:copy>
    </xsl:template>

    <xsl:template match="body">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:element name="xf:model" namespace="http://www.w3.org/2002/xforms">
                <xsl:element name="xf:instance" namespace="http://www.w3.org/2002/xforms">
                    <xsl:element name="data" namespace="">

                                <xsl:apply-templates select="*" mode="model"/>

                    </xsl:element>


                </xsl:element>
                <xsl:apply-templates select="//*[@type='submit']" mode="submission"/>
                <xsl:apply-templates select="*" mode="bind"/>
            </xsl:element>
            <xsl:if test="string-length($data) = 0">
                <xsl:apply-templates select="*" mode="ui"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>



    <!--
    ###############################################################################################
    mode model
    ###############################################################################################
    -->
    <xsl:template match="*[@name]" mode="model">
        <xsl:element name="{@name}" namespace="">

            <xsl:if test="string-length($data) != 0">
                <xsl:variable name="name" select="@name" />
                <xsl:variable name="formData" select="tokenize($data, ';')" />
                <xsl:variable name="theValue">
                    <xsl:for-each select="$formData">
                        <xsl:if test="starts-with(.,$name)">
                            <xsl:variable name="varname" select="substring-before(.,':')"/>
                            <xsl:if test="$varname = $name">
                                <xsl:variable name="varValue" select="substring-after(.,':')"/>
                                <xsl:value-of select="$varValue"/>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:if test="string-length($theValue) != 0"><xsl:value-of select="$theValue"/></xsl:if>
            </xsl:if>

            <xsl:apply-templates select="*" mode="model"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="text()" mode="model" priority="10"/>
    <xsl:template match="label" mode="model" priority="10"/>
    <xsl:template match="option" mode="model" priority="10"/>


    <!--
    ###############################################################################################
    mode submission
    ###############################################################################################
    -->
    <xsl:template match="*[@type='submit']" mode="submission" priority="10">
        <xsl:choose>
            <xsl:when test="string-length($data) != 0"/>
            <xsl:otherwise>
                <xsl:element name="xf:submision" namespace="http://www.w3.org/2002/xforms">
                    <!-- ### just uses the first forms' action as submission uri -->
                    <!-- todo: support multiple forms -->
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat(@id,'-submit')"/>
                    </xsl:attribute>
                    <xsl:attribute name="resource">
                        <xsl:value-of select="//form/@action"/>
                    </xsl:attribute>
                    <xsl:attribute name="method">
                        <xsl:variable name="method" select="if(exists(//form/@method)) then //form/@method else 'GET'"/>
                        <xsl:value-of select="$method"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!--
    ###############################################################################################
    mode bind
    ###############################################################################################
    -->

    <xsl:template match="text()" mode="bind" priority="10"/>
    <xsl:template match="label" mode="bind" priority="10"/>
    <xsl:template match="option" mode="bind" priority="10"/>

    <xsl:template match="*[@name]" mode="bind">
        <xsl:element name="xf:bind" namespace="http://www.w3.org/2002/xforms">
            <xsl:attribute name="ref">
                <xsl:value-of select="@name"/>
            </xsl:attribute>

            <xsl:call-template name="evalCalculate"/>
            <xsl:call-template name="evalReadonly"/>
            <xsl:call-template name="evalRequired"/>
            <xsl:call-template name="evalRelevant"/>
            <xsl:call-template name="evalConstraint"/>
            <xsl:call-template name="evalType"/>

            <xsl:apply-templates select="*" mode="bind"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="evalCalculate">
        <xsl:message>WARN: evalCalculate is not implemented yet</xsl:message>
    </xsl:template>

    <xsl:template name="evalReadonly">
        <xsl:if test="@readonly">
            <xsl:attribute name="readonly">true()</xsl:attribute>
        </xsl:if>
    </xsl:template>

    <xsl:template name="evalRequired">
        <xsl:if test="@required">
            <xsl:attribute name="required">true()</xsl:attribute>
        </xsl:if>
    </xsl:template>

    <xsl:template name="evalRelevant">
        <xsl:message>WARN: evalRelevant is not implemented yet</xsl:message>
    </xsl:template>

    <xsl:template name="evalType">

        <xsl:variable name="type">
            <!-- todo: adapt XForms-11-Schema.xsd to contain all types listed below as values -->
            <xsl:choose>
                <xsl:when test="exists(@checked)">boolean</xsl:when>

                <xsl:when test="@type='email'">email</xsl:when>
                <xsl:when test="@type='url'">anyURI</xsl:when>
                <!--todo: tel -->
                <xsl:when test="@type='tel'">string</xsl:when>
                <xsl:when test="@type='number'">decimal</xsl:when>
                <xsl:when test="@type='range'">integer</xsl:when>
                <xsl:when test="@type='date'">date</xsl:when>
                <xsl:when test="@type='month'">gMonth</xsl:when>
                <!--todo: week -->
                <xsl:when test="@type='week'">week</xsl:when>
                <xsl:when test="@type='time'">time</xsl:when>
                <!--todo: datetime-local -->
                <xsl:when test="@type='datetime-local'">datetime</xsl:when>
                <!--todo :color type -->
                <xsl:when test="@type='color'">color</xsl:when>
                <xsl:when test="@type='search'">string</xsl:when>
                <xsl:otherwise>string</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:attribute name="type">
            <xsl:value-of select="$type"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="evalConstraint">
        <xsl:message>WARN: evalConstraint is not implemented yet</xsl:message>
    </xsl:template>

    <xsl:template match="@*|node()|text()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!--
    ###############################################################################################
    mode UI
    ###############################################################################################
    -->

    <!-- ignore label elements -> handled when element with @name is matched -->
    <xsl:template match="label" priority="20" mode="ui"/>

    <xsl:template match="@*|node()|text()" mode="ui">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="ui"/>
            <xsl:apply-templates mode="ui"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*[@name]" mode="ui">

        <xsl:element name="{concat('xf:',name())}" namespace="http://www.w3.org/2002/xforms">
            <xsl:apply-templates select="@*" mode="ui"/>
            <xsl:attribute name="ref">
                <xsl:value-of select="@name"/>
            </xsl:attribute>

            <xsl:variable name="id" select="@id"/>
            <xsl:if test="exists(//label[@for=$id])">
                <xsl:element name="xf:label" namespace="http://www.w3.org/2002/xforms">
                    <xsl:value-of select="//label[@for=$id]"/>
                </xsl:element>
            </xsl:if>
            <xsl:apply-templates select="*" mode="ui"/>
        </xsl:element>
    </xsl:template>

    <xsl:template mode="ui" match="button">
        <xsl:element name="xf:trigger" namespace="http://www.w3.org/2002/xforms">
            <xsl:attribute name="id" select="@id"/>
            <xsl:element name="xf:label">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:if test="@type='submit'">
                <xsl:element name="xf:action" namespace="http://www.w3.org/2002/xforms">
                    <xsl:element name="xf:send" namespace="http://www.w3.org/2002/xforms">
                        <xsl:attribute name="submission">
                            <xsl:value-of select="concat(@id,'-submit')"/>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>

    <xsl:template match="option" mode="ui">
        <xsl:element name="xf:item" namespace="http://www.w3.org/2002/xforms">
            <xsl:element name="xf:label">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:element name="xf:value">
                <xsl:value-of select="@value"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
