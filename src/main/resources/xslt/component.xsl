<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:saxon="http://saxon.sf.net/"
                exclude-result-prefixes="xf bf"
                xpath-default-namespace= "http://www.w3.org/1999/xhtml">

    <xsl:import href="dojo.xsl"/>
    <xsl:import href="mappings.xsl"/>

    <!-- ####################################################################################################### -->
    <!-- #####################################  HELPER TEMPLATES '############################################## -->
    <!-- ####################################################################################################### -->

    <xsl:template name="buildControl">
        <xsl:variable name="id" select="@id"/>

        <xsl:variable name="datatype">
            <xsl:call-template name="getType"/>
        </xsl:variable>
        <xsl:variable name="lname" select="local-name()"/>
        <xsl:variable name="name" select="concat($data-prefix,@id)"/>

        <xsl:variable name="mediatypeCurrent">
            <xsl:choose>
                <xsl:when test="$lname='output' and exists(bf:data/@bf:mediatype)">
                    <xsl:value-of select="bf:data/@bf:mediatype"/>
                </xsl:when>
                <xsl:when test="exists(@bf:mediatype)">
                    <xsl:value-of select="@mediatype"/>
                </xsl:when>
                <xsl:otherwise>-</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:message>Anfang</xsl:message>

        <xsl:message>datatype: <xsl:value-of select="$datatype"/></xsl:message>

        <xsl:variable name="xfType" select="if($datatype!='') then $datatype else '-'"/>

        <xsl:message>xfType: <xsl:value-of select="$xfType"/></xsl:message>


        <xsl:variable name="xfAppearanceExists" select="if(@appearance!='') then @appearance else '*'"/>
        <xsl:message>xfAppearanceExists: <xsl:value-of select="$xfAppearanceExists"/></xsl:message>

        <xsl:variable name="xfAppearanceSlash" select="translate($xfAppearanceExists, '/','')"/>
        <xsl:message>xfAppearanceSlash: <xsl:value-of select="$xfAppearanceSlash"/></xsl:message>
        <xsl:variable name="xfAppearanceColon" select="translate($xfAppearanceSlash, ':','')"/>
        <xsl:message>xfAppearanceColon: <xsl:value-of select="$xfAppearanceColon"/></xsl:message>
        <xsl:variable name="xfAppearance" select="translate($xfAppearanceColon, '*', '-')"/>
        <xsl:message>xfAppearance: <xsl:value-of select="$xfAppearance"/></xsl:message>

        <xsl:variable name="xfMediatypeSlash" select="translate($mediatypeCurrent, '/','')"/>
        <xsl:variable name="xfMediatypeColon" select="translate($xfMediatypeSlash, ':','')"/>
        <xsl:variable name="xfMediatype" select="translate($xfMediatypeColon, '*', '-')"/>


        <xsl:variable name="callTemplateName" select="concat($lname, $xfType, $xfAppearance,$xfMediatype)"/>
        <xsl:message select="$callTemplateName" />
        

        <xsl:call-template name="{$callTemplateName}" saxon:allow-avt="yes" xmlns:saxon="http://saxon.sf.net/"/>
        <xsl:message>Ende</xsl:message>




<!--
        <xsl:choose>
            <xsl:when test="$lname='input' or
                            $lname='output' or
                            $lname='secret' or
                            $lname='submit' or
                            $lname='textarea' or
                            $lname='upload'">

                <span id="{concat($id,'-value')}"
                     class="xfValue"
                     dataType="{$datatype}"
                     controlType="{$lname}"
                     appearance="{@appearance}"
                     name="{$name}"
                     incremental="{$incremental}"
                     delay="{$incrementaldelay}"
                     tabindex="{$navindex}"
                     title="{normalize-space(xf:hint)}">

                    <xsl:if test="$accesskey != ' none'">
                        <xsl:attribute name="accessKey"><xsl:value-of select="$accesskey"/></xsl:attribute>
                    </xsl:if>
                    &lt;!&ndash; TODO: move mediatype to bf:data for all Controls &ndash;&gt;
                    <xsl:if test="$lname='output' and exists(bf:data/@bf:mediatype)">
                        <xsl:attribute name="mediatype">
                            <xsl:value-of select="bf:data/@bf:mediatype"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:if test="not($lname='output') and exists(@mediatype)">
                        <xsl:attribute name="mediatype">
                            <xsl:value-of select="@mediatype"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:if test="@appearance='caOPMLTree' or @appearance='caSimileTimeline'" >
                        <xsl:message select="@ref"/>
                        <xsl:variable name="tmpInstanceId1"><xsl:value-of select="substring(@ref,11,string-length(@ref))"/></xsl:variable>
                        <xsl:variable name="tmpInstanceId2"><xsl:value-of select="substring-before($tmpInstanceId1,')')"/></xsl:variable>
                        <xsl:variable name="instanceId"><xsl:value-of select="substring($tmpInstanceId2,1,string-length($tmpInstanceId2)-1)"/></xsl:variable>
                        <xsl:variable name="modelId"><xsl:value-of select="//xf:model[//xf:instance/@id=$instanceId]/@id"/></xsl:variable>

                        <xsl:attribute name="instanceId"><xsl:value-of select="$instanceId" /></xsl:attribute>
                        <xsl:attribute name="modelId"><xsl:value-of select="$modelId" /></xsl:attribute>
                    </xsl:if>

                    <xsl:choose>
                        <xsl:when test="contains(@mediatype,'text/html')">
                            <xsl:attribute name="tabindex" select="-1"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="tabindex" select="$navindex"/>
                        </xsl:otherwise>
                    </xsl:choose>

                    <xsl:if test="$datatype !='string'">
                        <xsl:attribute name="schemaValue">
                            <xsl:value-of select="bf:data/@bf:schema-value"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:if test="$lname='upload'">
                        <xsl:attribute name="fileId">
                            <xsl:value-of select="xf:filename/@id"/>
                        </xsl:attribute>
                        <xsl:attribute name="fileValue">
                            <xsl:value-of select="xf:filename/bf:data"/>
                        </xsl:attribute>
                    </xsl:if>
                    <xsl:choose>
                        <xsl:when test="contains(@mediatype,'text/html')">
                            <xsl:value-of select="bf:data/text()" disable-output-escaping="yes"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="bf:data/text()"/>
                        </xsl:otherwise>
                    </xsl:choose>

                </span>
                &lt;!&ndash;<div style="display:none;" id="{concat($id,'-hint')}"><xsl:value-of select="xf:hint"/></div>&ndash;&gt;
            </xsl:when>


            <xsl:when test="local-name()='trigger'">
                &lt;!&ndash;xsl:variable name="value" select="bf:data/text()"/&ndash;&gt;
                <xsl:variable name="value">
                    <xsl:call-template name="create-label">
                        <xsl:with-param name="label-elements" select="xf:label"/>
                    </xsl:call-template>
                </xsl:variable>
                <xsl:variable name="appearance" select="@appearance"/>

                <button
                     id="{concat(@id,'-value')}"
                     class="xfValue"
                     dataType="{$datatype}"
                     controlType="{local-name()}"
                     appearance="{$appearance}"
                     name="{$name}"
                     tabindex="{$navindex}"
                     value="{$value}"
                     title="{normalize-space(xf:hint)}"
                     type="button">
                    <xsl:if test="$accesskey != ' none'">
                        <xsl:attribute name="accessKey"><xsl:value-of select="$accesskey"/></xsl:attribute>
                    </xsl:if>
                </button>
                &lt;!&ndash;<div style="display:none;" id="{concat($id,'-hint')}"><xsl:value-of select="xf:hint"/></div>&ndash;&gt;
                <xsl:apply-templates select="xf:hint"/>
            </xsl:when>

            <xsl:when test="local-name()='range'">
                <xsl:variable name="value" select="bf:data/@bf:value"/>
                <xsl:variable name="start" select="bf:data/@bf:start"/>
                <xsl:variable name="end" select="bf:data/@bf:end"/>
                <xsl:variable name="step" select="bf:data/@bf:step"/>
                <xsl:variable name="appearance" select="@appearance"/>

                <div id="{concat(@id,'-value')}"
                     class="xfValue"
                     dataType="{$datatype}"
                     controlType="{local-name()}"
                     appearance="{$appearance}"
                     name="{$name}"
                     incremental="{$incremental}"
                     tabindex="{$navindex}"
                     start="{$start}"
                     end="{$end}"
                     step="{$step}"
                     value="{$value}"
                     title="{normalize-space(xf:hint)}">
                    <xsl:if test="$accesskey != ' none'">
                        <xsl:attribute name="accessKey"><xsl:value-of select="$accesskey"/></xsl:attribute>
                    </xsl:if>

&lt;!&ndash;                  <ol dojoType="dijit.form.HorizontalRuleLabels" container="topDecoration"
                        style="height:1em;font-size:75%;color:gray;">
                        <xsl:if test="$start">
                            <li><xsl:value-of select="$start"/></li>
                            <li> </li>
                        </xsl:if>
                        <xsl:if test="$end">
                            <li><xsl:value-of select="$end"/></li>
                        </xsl:if>
                    </ol>&ndash;&gt;
                </div>
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
-->

    </xsl:template>

</xsl:stylesheet>
