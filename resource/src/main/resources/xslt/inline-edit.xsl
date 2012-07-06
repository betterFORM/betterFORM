<?xml version="1.0" encoding="UTF-8"?>

<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                exclude-result-prefixes="xf bf xhtml ev"
                xpath-default-namespace= "http://www.w3.org/1999/xhtml">

    <xsl:import href="dojo.xsl"/>
    <xsl:include href="dojo-ui.xsl"/>
    <xsl:include href="dojo-controls.xsl"/>


    <!-- ####################################################################################################### -->
    <!-- This stylesheet transcodes a XTHML2/XForms input document to HTML 4.0.                                  -->
    <!-- It serves as a reference for customized stylesheets which may import it to overwrite specific templates -->
    <!-- or completely replace it.                                                                               -->
    <!-- This is the most basic transformator for HTML browser clients and assumes support for HTML 4 tagset     -->
    <!-- but does NOT rely on javascript.                                                                        -->
    <!-- author: joern turner                                                                                    -->
    <!-- author: lars windauer                                                                                   -->
    <!-- ####################################################################################################### -->

    <!-- ############################################ PARAMS ################################################### -->


    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="no"
                doctype-system="/resources/xsd/xhtml1-transitional.dtd"/>
    <!-- ### transcodes the XHMTL namespaced elements to HTML ### -->
    <!--<xsl:namespace-alias stylesheet-prefix="xhtml" result-prefix="#default"/>-->



    <!-- ####################################################################################################### -->
    <!-- ##################################### TEMPLATES ####################################################### -->
    <!-- ####################################################################################################### -->

    <!-- ######################################################################################################## -->
    <!-- #####################################  CONTROLS ######################################################## -->
    <!-- ######################################################################################################## -->

    <!-- ##### TRIGGER / SUBMIT ##### -->
    <!-- ##### TRIGGER / SUBMIT ##### -->
    <!-- ##### TRIGGER / SUBMIT ##### -->
    <xsl:template match="xf:trigger|xf:submit">
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <!--<xsl:with-param name="appearance" select="@appearance"/>-->
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="trigger">
            <xsl:with-param name="classes" select="$control-classes"/>
        </xsl:call-template>
    </xsl:template>

    <!-- ####################################################################################################### -->
    <!-- #####################################  HELPER TEMPLATES '############################################## -->
    <!-- ####################################################################################################### -->

    <xsl:template name="buildControl">
        <xsl:variable name="id" select="@id"/>

        <xsl:variable name="lname" select="local-name()"/>
        <xsl:variable name="name" select="concat($data-prefix,@id)"/>
       <!-- TODO: DateTime -->

        <xsl:choose>
            <xsl:when test="$lname='input' or
                            $lname='output' or
                            $lname='secret' or
                            $lname='submit' or
                            $lname='textarea' or
                            $lname='upload'">

                <span id="{concat($id,'-value')}"
                     class="xfValue"
                     data-bf-type="{$lname}"
                     appearance="{@appearance}"><xsl:value-of select="bf:data"/></span>
            </xsl:when>


            <xsl:when test="local-name()='trigger'">
                <!--xsl:variable name="value" select="bf:data/text()"/-->
                <xsl:variable name="value">
                    <xsl:call-template name="create-label">
                        <xsl:with-param name="label-elements" select="xf:label"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="appearance" select="@appearance"/>

                <button
                     id="{concat(@id,'-value')}"
                     class="xfValue"
                     controlType="trigger"
                     appearance="{$appearance}"
                     name="{$name}"
                     value="{$value}">
                </button>
                <xsl:apply-templates select="xf:hint"/>
            </xsl:when>

            <xsl:when test="local-name()='range'">
                <xsl:call-template name="range"/>
            </xsl:when>
            <xsl:when test="local-name()='select'">
                <xsl:call-template name="select"/>
            </xsl:when>
            <xsl:when test="local-name()='select1'">
                <xsl:call-template name="select1"/>
            </xsl:when>
            <xsl:when test="local-name()='repeat'">
                <xsl:apply-templates select="."/>
            </xsl:when>
            <xsl:when test="local-name()='group'">
                <xsl:apply-templates select="."/>
            </xsl:when>
            <xsl:when test="local-name()='switch'">
                <xsl:apply-templates select="."/>
            </xsl:when>
        </xsl:choose>

    </xsl:template>

</xsl:stylesheet>
