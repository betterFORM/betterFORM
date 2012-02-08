<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    exclude-result-prefixes="xforms bf xhtml"
    xpath-default-namespace="http://www.w3.org/1999/xhtml">


    <xsl:import href="dojo.xsl"/>

    <!-- ********************************* PARAMETERS ********************************   -->
    <xsl:param name="action-url" select="'javascript:submit()'"/>


    <!-- ### signals the phase of processing (init|submit) ### -->
    <xsl:param name="phase" select="'false'"/>

    <xsl:param name="form-id" select="'betterform'"/>
    <xsl:param name="form-name" select="'betterForm XForms Processor'"/>
    <xsl:param name="debug-enabled" select="'true'"/>

    <!-- ### contains the full user-agent string as received from the servlet ### -->
    <xsl:param name="user-agent" select="'betty'"/>
    <xsl:param name="base-url" select="'betterform-@betterform-version@'"/>

    <!-- ********************************* GLOBAL VARIABLES ********************************   -->
    <xsl:variable name="contextroot" select="'..'"/>
    <xsl:variable name="uses-dates" select="boolean(//*/@bf:type='dateTime') or boolean(//*/@bf:type='date')"/>
    <xsl:variable name="uses-upload" select="boolean(//*/xforms:upload)"/>

    <xsl:output method="html" version="4.0" encoding="UTF-8"/>
    <!--<xsl:namespace-alias stylesheet-prefix="xhtml" result-prefix="#default"/>-->

    <xsl:strip-space elements="*"/>

    <!-- ********************************* TEMPLATES ********************************   -->
<!--
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>
-->

    <xsl:template match="html">
        <xsl:message>betterform-applet.xsl</xsl:message>
        <html>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <!--<xsl:template match="head"/>-->

    <xsl:template match="body">
        <xsl:copy>
            <xsl:element name="form" >
                <xsl:attribute name="name">
                    <xsl:value-of select="$form-id"/>
                </xsl:attribute>
                <xsl:attribute name="action">
                    <xsl:value-of select="$action-url"/>
                </xsl:attribute>
                <xsl:attribute name="method">POST</xsl:attribute>
                <xsl:attribute name="enctype">application/x-www-form-urlencoded</xsl:attribute>
                <xsl:if test="$uses-upload">
                    <xsl:attribute name="enctype">multipart/form-data</xsl:attribute>
                </xsl:if>
                <xsl:attribute name="onsubmit">javascript:submit();</xsl:attribute>

                <div dojotype="betterform.Betty" jsId="fluxProcessor" id="fluxProcessor"/>
                <!-- provide a first submit which does not map to any xforms:trigger -->
                <input type="image" name="dummy" style="width:0pt;height:0pt;" value="dummy"/>
                <xsl:apply-templates select="div[@id='xformsui']"/>
            </xsl:element>
            <div id="bfMessageDialog" dojotype="dijit.Dialog" style="text-align:center;">
                <div id="messageContent"></div>
                <button dojotype="dijit.form.Button" type="button" style="margin:10px;">OK
                    <script type="dojo/method" event="onClick" args="evt">
                        dijit.byId("bfMessageDialog").hide();
                    </script>
                </button>
            </div>
            
        </xsl:copy>
    </xsl:template>

    <xsl:template match="div[@id='xformsui']">
        <xsl:apply-templates/>
    </xsl:template>

    <!-- ### handle selected xforms:case ### -->
    <xsl:template match="xforms:case[@xforms:selected='true']">
        <span class="selected-case" id="{@id}">
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <!-- ### handle unselected xforms:case ### -->
    <xsl:template match="xforms:case">
        <span class="deselected-case" id="{@id}">
            <xsl:apply-templates/>
        </span>
    </xsl:template>

</xsl:stylesheet>
