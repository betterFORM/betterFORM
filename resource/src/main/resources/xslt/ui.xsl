<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:bfc="http://betterform.sourceforge.net/xforms/controls"
                exclude-result-prefixes="xhtml xf bf"
                xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <xsl:import href="repeat-ui.xsl"/>
    <!-- ####################################################################################################### -->
    <!-- This stylesheet handles the XForms UI constructs 'group', 'repeat' and                                  -->
    <!-- 'switch' and offers some standard interpretations for the appearance attribute.                         -->
    <!-- author: joern turner                                                                                    -->
    <!-- author: lars windauer                                                                                   -->
    <!-- ####################################################################################################### -->

    <xsl:param name="betterform-pseudo-item" select="'betterform-pseudo-item'"/>
    <!-- ############################################ PARAMS ################################################### -->
    <!-- ############################################ VARIABLES ################################################ -->


    <xsl:preserve-space elements="*"/>
    <!-- ####################################################################################################### -->
    <!-- ##################################### TEMPLATES ####################################################### -->
    <!-- ####################################################################################################### -->

    <!-- ####################################################################################################### -->
    <!-- #################################### DIALOG ########################################################### -->
    <!-- ####################################################################################################### -->
    <xsl:template match="bfc:dialog" name="dialog" priority="10">
        <xsl:variable name="dialog-id" select="@id"/>
        <xsl:variable name="dialog-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="dialog-label">
            <xsl:call-template name="create-label">
                <xsl:with-param name="label-elements" select="xf:label"/>
            </xsl:call-template>
        </xsl:variable>

        <span id="{$dialog-id}" class="{$dialog-classes}" title="{$dialog-label}">

            <xsl:call-template name="copy-style-attribute"/>

            <xsl:apply-templates select="*[not(self::xf:label)] | text()"/>

        </span>
    </xsl:template>

    <xsl:template match="bfc:dialog" mode="compact-repeat" priority="10">
        <xsl:variable name="dialog-id" select="@id"/>
        <xsl:variable name="dialog-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="dialog-label">
            <xsl:call-template name="create-label">
                <xsl:with-param name="label-elements" select="xf:label"/>
            </xsl:call-template>
        </xsl:variable>

        <span id="{$dialog-id}" class="{$dialog-classes}" title="{$dialog-label}">
            <xsl:call-template name="copy-style-attribute"/>
            <xsl:apply-templates select="*[not(self::xf:label)] | text()" mode="#current"/>
        </span>
    </xsl:template>

    <xsl:template match="bfc:dialog" mode="repeated-compact-prototype" priority="10">
        <xsl:variable name="dialog-id" select="@id"/>
        <xsl:variable name="dialog-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="dialog-label">
            <xsl:call-template name="create-label">
                <xsl:with-param name="label-elements" select="xf:label"/>
            </xsl:call-template>
        </xsl:variable>

        <button id="{$dialog-id}-button" type="button" iconClass="dijitIconSearch"
                showLabel="false" onCLick="show{$dialog-id}Dialog()">
            <script type="dojo/method" event="onClick">alert("Ronald");</script>

            <xsl:choose>
                <xsl:when test="@button-label">
                    <xsl:value-of select="@button-label"/>
                </xsl:when>
                <xsl:otherwise>Open Dialog</xsl:otherwise>
            </xsl:choose>
        </button>

        <span id="{$dialog-id}" title="{$dialog-label}">
            <xsl:attribute name="class" select="concat($dialog-classes,' xfRepeated')"/>
            <xsl:attribute name="controlType" select="local-name()"/>
            <xsl:attribute name="appearance" select="@appearance"/>

            <xsl:call-template name="copy-style-attribute"/>

            <xsl:apply-templates select="*[not(self::xf:label)] | text()" mode="#current"/>

        </span>
    </xsl:template>

    <!-- ####################################################################################################### -->
    <!-- #################################### GROUPS ########################################################### -->
    <!-- ####################################################################################################### -->

    <xsl:template match="xf:group" name="group" priority="10">
        <xsl:variable name="group-id" select="@id"/>
        <xsl:variable name="group-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="group-body">
            <xsl:with-param name="group-id" select="$group-id"/>
            <xsl:with-param name="group-classes" select="$group-classes"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template name="group-body">
        <xsl:param name="group-id"/>
        <xsl:param name="group-classes"/>
        <xsl:param name="group-label" select="true()"/>


        <span id="{$group-id}" class="{$group-classes}">

            <xsl:call-template name="copy-style-attribute"/>

            <span class="legend">
                <xsl:choose>
                    <xsl:when test="$group-label and xf:label">
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat($group-id, '-label')"/>
                        </xsl:attribute>
                        <xsl:attribute name="class">
                            <xsl:call-template name="assemble-group-label-classes"/>
                        </xsl:attribute>
                        <xsl:call-template name="create-label">
                            <xsl:with-param name="label-elements" select="xf:label"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="style">display:none;</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </span>

            <xsl:apply-templates select="*[not(self::xf:label)] | text()"/>
        </span>
    </xsl:template>

    <xsl:template name="group-body-repeated">
        <xsl:param name="group-id"/>
        <xsl:param name="group-classes"/>
        <xsl:param name="group-label" select="true()"/>

        <div id="{$group-id}" class="{$group-classes}">
            <div class="legend">
                <xsl:choose>
                    <xsl:when test="$group-label and xf:label">
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat($group-id, '-label')"/>
                        </xsl:attribute>
                        <xsl:attribute name="class">
                            <xsl:call-template name="assemble-group-label-classes"/>
                        </xsl:attribute>
                        <xsl:call-template name="create-label">
                            <xsl:with-param name="label-elements" select="xf:label"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="style">display:none;</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </div>

            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-full-prototype"/>
        </div>
    </xsl:template>


    <!-- ######################################################################################################## -->
    <!-- ####################################### custom group with vertical layout ############################## -->
    <!-- ######################################################################################################## -->

    <xsl:template match="xf:group[@appearance='bf:verticalTable']" priority="15">
        <xsl:variable name="group-id" select="@id"/>

        <xsl:variable name="mip-classes">
            <xsl:call-template name="get-mip-classes"/>
        </xsl:variable>

        <table cellspacing="0" cellpadding="0" class="xfContainer appBfVerticalTable bfVerticalTable {$mip-classes}" id="{$group-id}">
            <xsl:if test="exists(xf:label)">
                <caption class="xfGroupLabel">
                    <xsl:apply-templates select="./xf:label"/>
                </caption>
            </xsl:if>
            <tbody>
                <xsl:for-each select="*[not(local-name()='label')]">
                    <xsl:choose>
                        <!-- if we got a group with appearance bf:GroupLabelLeft we put the label
                of the first control into the lefthand column -->
                        <xsl:when test="local-name()='group' and ./@appearance='bf:GroupLabelLeft'">
                            <tr class="appBfGroupLabelLeft bfGroupLabelLeft">
                                <td>
                                    <!-- use the label of the nested group for the left column -->
                                    <xsl:value-of select="xf:label"/>
                                </td>
                                <td>
                                    <xsl:apply-templates select="."/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:when test="local-name()='group' or local-name()='repeat' or local-name()='switch'">
                            <tr>
                                <td colspan="3">
                                    <xsl:apply-templates select="."/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:when test="namespace-uri()='http://www.w3.org/1999/xhtml'">
                            <tr>
                                <td colspan="3">
                                    <xsl:apply-templates select="."/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:if test="exists(node())">
                                <tr>
                                    <td class="bfVerticalTableLabel" valign="top">
                                        <xsl:variable name="label-classes">
                                            <xsl:call-template name="assemble-label-classes"/>
                                        </xsl:variable>
                                        <xsl:if test="local-name(.) != 'trigger'">
                                            <label id="{@id}-label" for="{@id}-value" class="{$label-classes}">
                                                <xsl:apply-templates select="xf:label"/>
                                            </label>
                                        </xsl:if>
                                    </td>
                                    <td class="bfVerticalTableValue" valign="top">
                                        <xsl:apply-templates select="." mode="table"/>
                                    </td>
                                    <td class="bfVerticalTableInfo" valign="top">
                                        <xsl:apply-templates select="xf:alert"/>
                                        <xsl:apply-templates select="xf:hint"/>
                                        <xsl:apply-templates select="xf:help"/>
                                        <span class="info" style="display:none;" id="{concat(@id,'-info')}">ok</span>
                                    </td>
                                </tr>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>


    <xsl:template match="xf:trigger" mode="table">
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <span id="{@id}"
             class="{$control-classes}">
            <xsl:call-template name="trigger">
            <!--<xsl:with-param name="classes" select="$control-classes"/>-->
            </xsl:call-template>
        </span>
    </xsl:template>


    <xsl:template
            match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:upload"
            mode="table">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <span id="{$id}"
             class="{$control-classes}">
            <xsl:call-template name="copy-style-attribute"/>
            <xsl:call-template name="buildControl"/>
            <xsl:copy-of select="xhtml:script"/>
        </span>
    </xsl:template>
    <!--<xsl:template match="bf:data" mode="table" priority="10"/>-->

    <!-- ######################################################################################################## -->
    <!-- ####################################### custom group with horizontal layout ############################## -->
    <!-- ######################################################################################################## -->

    <!-- appearance ColumnLeft allows to be nested into a verticalTable and display its labels in the left
    column of the vertical table. All other controls will be wrapped in a horizontal group and be written to the
    right column. -->
    <xsl:template match="xf:group[@appearance='bf:GroupLabelLeft']" priority="15">
        <xsl:call-template name="copy-style-attribute"/>
        <xsl:call-template name="horizontalTable"/>
    </xsl:template>

    <!-- this template is used for horizontalTable AND for ColumnLeft appearance -->
    <xsl:template match="xf:group[@appearance='bf:horizontalTable']" priority="15" name="horizontalTable">
        
        <xsl:variable name="mip-classes">
            <xsl:call-template name="get-mip-classes"/>
        </xsl:variable>


        <table id="{@id}" class="xfContainer appBfHorizontalTable bfHorizontalTable {$mip-classes}">
            <tr>
                <td colspan="{count(*[position() &gt; 1])}" class="xfGroupLabel">
                    <xsl:if test="exists(xf:label) and @appearance !='bf:GroupLabelLeft'">
                        <xsl:apply-templates select="./xf:label"/>
                    </xsl:if>
                </td>
            </tr>
            <tr>
                <xsl:for-each select="*[position() &gt; 1]">
                    <td class="appBfHorizontalTableLabel bfHorizontalTableLabel  appBfTableCol{position()} bfTableCol{position()}">
                        <xsl:if test="local-name(.) != 'trigger'">
                            <label id="{@id}-label" for="{@id}-value" class="appBfTableLabel bfTableLabel">
                                <xsl:apply-templates select="xf:label"/>
                            </label>
                        </xsl:if>
                    </td>
                </xsl:for-each>
            </tr>
            <tr>
                <xsl:for-each select="*[position() &gt; 1 and *[position() != last()]]">
                    <td class="appBfHorizontalTableValue bfHorizontalTableValue">
                        <xsl:apply-templates select="."/>
                    </td>
                </xsl:for-each>
            </tr>
        </table>
    </xsl:template>

    <!-- ############################## BF:DATA ############################## -->
    <!-- ############################## BF:DATA ############################## -->
    <!-- ############################## BF:DATA ############################## -->
    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    bf:data elements are suppressed in the output
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:template match="bf:data" priority="10"/>

    <!-- ######################################################################################################## -->
    <!-- ####################################### SWITCH ######################################################### -->
    <!-- ######################################################################################################## -->

    <!-- ### FULL SWITCH ### -->
    <!--
        Renders a tabsheet. This template requires that the author sticks to an
        authoring convention: The triggers for toggling the different cases MUST
        all appear in a case with id 'switch-toggles'. This convention makes it
        easier to maintain the switch cause all relevant markup is kept under the
        same root element.
    -->

    <!-- ### DEFAULT SWITCH ### -->
    <xsl:template match="xf:switch">
        <xsl:variable name="switch-id" select="@id"/>
        <xsl:variable name="switch-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>


        <div id="{$switch-id}" class="{$switch-classes}">
            <xsl:call-template name="copy-style-attribute"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <!-- ### SELECTED CASE ### -->
    <xsl:template match="xf:case[bf:data/@bf:selected='true']" name="selected-case">
        <xsl:variable name="case-id" select="@id"/>
        <xsl:variable name="case-classes" select="'xfCase xfSelectedCase'"/>

        <div id="{$case-id}" class="{$case-classes}">
            <xsl:apply-templates select="*[not(self::xf:label)]"/>
        </div>
    </xsl:template>

    <!-- ### DE-SELECTED/NON-SELECTED CASE ### -->
    <xsl:template match="xf:case" name="deselected-case">
        <!-- render only in scripted environment -->
        <xsl:variable name="case-id" select="@id"/>
        <xsl:variable name="case-classes" select="'xfCase xfDeselectedCase'"/>

        <div id="{$case-id}" class="{$case-classes}">
            <xsl:apply-templates select="*[not(self::xf:label)]"/>
        </div>
    </xsl:template>


    <xsl:template match="xf:switch[@appearance='dijit:AccordionContainer']">
        <xsl:variable name="switch-id" select="@id"/>
        <xsl:variable name="switch-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>


        <div id="{$switch-id}" class="{$switch-classes} xfSwitch"
             duration="200"
             style="float: left; margin-right: 30px; width: 400px; height: 300px; overflow: hidden">
            <xsl:for-each select="xf:case[.//xf:label]">
                <xsl:variable name="label">
                    <xsl:call-template name="create-label">
                        <xsl:with-param name="label-elements" select=".//xf:label"/>
                    </xsl:call-template>
                </xsl:variable>
                <div selected="{@selected}" title="{$label}">
                    <xsl:apply-templates select="*[not(self::xf:label)]"/>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template match="xf:switch[@appearance='bf:AccordionContainer']">
        <xsl:variable name="switch-id" select="@id"/>
        <xsl:variable name="switch-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <div style="display:none">
            <xsl:for-each select="xf:case[@name='switch-toggles']/xf:trigger">
                <xsl:call-template name="trigger"/>
            </xsl:for-each>
        </div>

        <div id="{$switch-id}" class="{$switch-classes} xfAccordion" duration="200">
            <!--
                    <div id="{$switch-id}" class="{$switch-classes}" dojoType="betterform.ui.container.TabSwitch"
                            style="width: 900px; height: 400px;">
            -->
            <xsl:for-each select="xf:case[./xf:label]">
                <xsl:variable name="selected">
                    <xsl:choose>
                        <xsl:when test="@selected='true'">true</xsl:when>
                        <xsl:otherwise>false</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:variable name="label">
                    <xsl:call-template name="create-label">
                        <xsl:with-param name="label-elements" select="xf:label"/>
                    </xsl:call-template>
                </xsl:variable>
                <div class="xfCase" caseId="{@id}"
                     selected="{$selected}" title="{$label}">
                    <xsl:apply-templates select="*[not(self::xf:label)]"/>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template match="xf:switch[@appearance='dijit:TabContainer']">
        <xsl:variable name="switch-id" select="@id"/>
<!--
        <xsl:variable name="switch-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
-->

        <div style="display:none">
            <xsl:for-each select="xf:case[@name='switch-toggles']/xf:trigger">
                <xsl:call-template name="trigger"/>
            </xsl:for-each>
        </div>
        <div id="{$switch-id}" class="xfSwitch bfTabContainer">
            <xsl:call-template name="copy-style-attribute"/>
            <xsl:for-each select="xf:case[./xf:label]">
                <xsl:variable name="selected">
                    <xsl:choose>
                        <xsl:when test="@selected='true'">true</xsl:when>
                        <xsl:otherwise>false</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:variable name="label">
                    <xsl:call-template name="create-label">
                        <xsl:with-param name="label-elements" select="xf:label"/>
                    </xsl:call-template>
                </xsl:variable>
                <div style="width:100%;height:100%;" class="xfCase" caseId="{@id}"
                     selected="{$selected}" title="{$label}">
                    <xsl:apply-templates select="*[not(self::xf:label)]"/>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template match="xf:switch[@appearance='dijit:TitlePane']">
        <xsl:variable name="switch-id" select="@id"/>
        <xsl:variable name="switch-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <div id="{$switch-id}" class="{$switch-classes}"
             style="width: 600px; height: 300px;">
            <xsl:for-each select="xf:case[.//xf:label]">
                <div title="{.//xf:label[1]}">
                    <xsl:apply-templates select="*[not(self::xf:label)]"/>
                </div>
            </xsl:for-each>
        </div>
    </xsl:template>

</xsl:stylesheet>

