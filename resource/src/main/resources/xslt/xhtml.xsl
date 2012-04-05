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
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf bf"
                xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <!--<xsl:import href="common.xsl"/>-->
    <xsl:include href="html-form-controls.xsl"/>
    <xsl:include href="ui.xsl"/>


    <!--!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    todo: compare and merge dojo.controls with html-form-controls
    todo: compare and merge dojo-ui.xsl and ui.xsl
    -->

    <!-- ####################################################################################################### -->
    <!-- This stylesheet transcodes a XTHML/XForms input document to XHTML.                                  -->
    <!-- It serves as a reference for customized stylesheets which may import it to overwrite specific templates -->
    <!-- or completely replace it.                                                                               -->
    <!-- author: joern turner                                                                                    -->
    <!-- author: lars windauer                                                                                   -->
    <!-- ####################################################################################################### -->

    <!-- ############################################ PARAMS ################################################### -->
    <!-- ############################################ PARAMS ################################################### -->
    <!-- ############################################ PARAMS ################################################### -->
    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    contextroot - the name of the webapp context (default: 'betterform'
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="contextroot" select="''"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    sessionKey - the XForms session identifier used by the processor
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="sessionKey" select="''"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    baseURI - the baseURI of the document to be transformed by this stylesheet
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="baseURI" select="''"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    this url will be used to build the form action attribute ###
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="action-url" select="'http://localhost:8080/betterform-1.0.0/XFormsServlet'"/>

    <xsl:param name="form-id" select="'betterform'"/>
    <xsl:param name="form-name" select="//title"/>
    <xsl:param name="debug-enabled" select="'true'"/>

    <!-- ### specifies the parameter prefix for repeat selectors ### -->
    <xsl:param name="selector-prefix" select="'s_'"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    contains the full user-agent string as received from the servlet
    todo: is this really still needed?
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="user-agent" select="'default'"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    resources are served by ResourceServlet. This param passes the mapping path
    of ResourceServlet
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="resourcesPath" select="'/bfResources/'"/>

    <!---
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    relative path to javascript files within resources
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="scriptPath" select="concat($resourcesPath,'scripts/')"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    relative path to CSS files within resources
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="CSSPath" select="concat($resourcesPath,'styles/')"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    locale Parameter for setting current language
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="locale" select="'en'"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    Dojo Default Theme
    todo: should this be passed or injected at runtime by other means?
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="defaultTheme" select="'tundra'"/>

    <!-- ############################################ VARIABLES ################################################ -->
    <!-- ############################################ VARIABLES ################################################ -->
    <!-- ############################################ VARIABLES ################################################ -->
    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    checks, whether this form uses uploads. Used to set form enctype attribute
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:variable name="uses-upload" select="exists(//*/xf:upload)"/>


    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    checks, whether this form uses the DOMFocusIn event. Used for optimizing
    the client-side processor execution
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:variable name="uses-DOMFocusIn" select="exists(//*[@ev:event='DOMFocusIn'])"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    the following 2 vars select the default css files
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:variable name="default-css" select="concat($contextroot,$CSSPath,'xforms.css')"/>
    <xsl:variable name="betterform-css" select="concat($contextroot,$CSSPath,'betterform.css')"/>


    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="no"
                doctype-system="/resources/xsd/xhtml1-transitional.dtd"/>

    <xsl:preserve-space elements="*"/>
    <xsl:strip-space elements="xf:action"/>

    <!--

    -->
    <xsl:variable name="ualc" select="translate($user-agent,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz')"/>
    <xsl:variable name="client-device">
        <xsl:choose>
            <xsl:when test="(contains($ualc,'android') and contains($ualc,'mobile'))
                            or contains($ualc,'blackberry') or contains($ualc,'benq') or contains($ualc,'iemobile') or
                            contains($ualc,'ipod') or contains($ualc,'iphone') or
                            contains($ualc,'j2me') or contains($ualc,'nokia') or
                            contains($ualc,'phone') or contains($ualc,'smartphone')">uaMobile</xsl:when>
            <xsl:when test="(contains($ualc,'android') and contains($ualc, 'tablet')) or
                            contains($ualc,'ipad') or contains($ualc,'table')">uaTablet</xsl:when>
            <xsl:otherwise>uaDesktop</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>


    <!-- ####################################################################################################### -->
    <!-- ##################################### TEMPLATES ####################################################### -->
    <!-- ####################################################################################################### -->

    <!-- ############################## HEAD ############################## -->
    <!-- ############################## HEAD ############################## -->
    <!-- ############################## HEAD ############################## -->
    <xsl:template match="head">

        <xsl:comment> *** powered by betterFORM, &amp;copy; 2012 *** </xsl:comment>

        <head>
            <title>
                <xsl:value-of select="$form-name"/>
            </title>
            <!-- copy all meta tags except 'contenttype' -->
            <xsl:call-template name="getMeta"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            copy base if present
            todo: review - in HTML5 base is deprecated - shall this still be used?
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <!--
            <xsl:if test="$baseURI != ''">
                <base>
                    <xsl:attribute name="href">
                        <xsl:value-of select="$baseURI"/>
                    </xsl:attribute>
                </base>
            </xsl:if>
            -->

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            CSS needed by Dojo is added here
            todo: review - is there a way to load the CSS conditionally so that
            they won't be loaded when in non-scripted mode? Surely would be possible
            with JS but is it worth the effort?
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="addDojoCSS"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            the default betterFORM CSS files are imported here
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="include-xforms-css"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            copy user-defined stylesheets and inline styles
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="getLinkAndStyle"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            include needed javascript files
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="copyInlineScript"/>
        </head>
    </xsl:template>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    include-xforms-css imports the default stylesheets.
    This template can be overwritten when additional files are needed.
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:template name="include-xforms-css">
        <link rel="stylesheet" type="text/css" href="{$default-css}"/>
        <link rel="stylesheet" type="text/css" href="{$betterform-css}"/>
    </xsl:template>



    <xsl:template name="addDojoCSS"><xsl:text>
</xsl:text>
        <xsl:variable name="cssTheme">
                    <xsl:choose>
                        <xsl:when test="contains(//body/@class, 'tundra')">tundra</xsl:when>
                        <xsl:when test="contains(//body/@class, 'soria')">soria</xsl:when>
                        <xsl:when test="contains(//body/@class, 'nihilo')">nihilo</xsl:when>
                        <xsl:when test="contains(//body/@class, 'claro')">claro</xsl:when>
                        <xsl:when test="contains(//body/@class, 'a11y')">a11y</xsl:when>
                        <xsl:otherwise><xsl:value-of select="$defaultTheme"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                <style type="text/css">
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath, 'dijit/themes/', $cssTheme, '/', $cssTheme,'.css')"/>";
<!--
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath, 'dojo/resources/dojo.css')"/>";
                    @import "<xsl:value-of select="concat($contextroot,$scriptPath, 'dojox/widget/Toaster/Toaster.css')"/>";
-->
                </style><xsl:text>
</xsl:text>
    </xsl:template>


    <!-- ############################## BODY ############################## -->
    <!-- ############################## BODY ############################## -->
    <!-- ############################## BODY ############################## -->
    <xsl:template match="body">
        <!-- todo: add 'overflow:hidden' to @style here -->

        <xsl:variable name="theme">
            <xsl:choose>
                <xsl:when test="not(exists(//body/@class)) or string-length(//body/@class) = 0"><xsl:value-of select="$defaultTheme"/></xsl:when>
                <xsl:when test="not(contains(//body/@class, $defaultTheme)) and
                                not(contains(//body/@class, 'tundra')) and
                                not(contains(//body/@class, 'soria'))  and
                                not(contains(//body/@class, 'claro'))  and
                                not(contains(//body/@class, 'nihilo')) and
                                not(contains(//body/@class, 'ally'))">
                    <xsl:value-of select="concat($defaultTheme, ' ', //body/@class)"/>
                </xsl:when>
                <xsl:otherwise><xsl:value-of select="//body/@class"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="alert">
            <xsl:choose>
                <xsl:when test="contains(@class,'ToolTipAlert')">ToolTipAlert</xsl:when>
                <xsl:otherwise>InlineAlert</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <body class="{$theme} bf {$client-device} {$alert}">
            <!-- TODO: Lars: keep original CSS classes on body-->
            <xsl:copy-of select="@*[name() != 'class']"/>
            <xsl:message>Useragent is <xsl:value-of select="$user-agent"/></xsl:message>
            <xsl:message>Client Device: <xsl:value-of select="$client-device"/></xsl:message>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            the 'bfLoading' div is used to display an animated icon during ajax activity
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <div id="bfLoading" class="disabled">
                <img    id="indicator"
                        src="{concat($contextroot,$resourcesPath,'images/indicator.gif')}"
                        class="xfDisabled"
                        alt="loading"/>
            </div>
            <!-- Toaster widget for ephemeral messages -->

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            The Toaster widget is used for displaying ephemeral messages at the bottom
            of the window
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
<!--
            <div data-dojo-type="dojox.widget.Toaster"
                 id="betterformMessageToaster"
                 positionDirection="bl-up"
                 duration="8000"
                 separator="&lt;div style='height:1px;border-top:thin dotted;width:100%;'&gt;&lt;/div&gt;"
                 messageTopic="testMessageTopic">
            </div>
-->

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            A noscript block displayed in case javascript is switched off
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <noscript>
                <div id="noScript">
                    Sorry, this page relies on JavaScript which is not enabled in your browser.
                </div>
            </noscript>


            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            actual content of the form starts here
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            -->
            <div id="formWrapper">
                <!--
                >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                creates the client-side processor
                <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                -->
<!--
                <div    id="fluxProcessor"
                        jsId="fluxProcessor"
                        dojotype="bf.XFProcessor"
                        sessionkey="{$sessionKey}"
                        contextroot="{$contextroot}"
                        usesDOMFocusIN="{$uses-DOMFocusIn}"
                        dataPrefix="{$data-prefix}"
                        logEvents="{$debug-enabled}">
-->


                    <!--
                    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    look for outermost UI elements (the ones having no ancestors in the xforms namespace
                    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    -->
                    <xsl:variable name="outermostNodeset"
                                  select=".//xf:*[not(ancestor::*[namespace-uri()='http://www.w3.org/2002/xforms'])]
                                          [not(namespace-uri()='http://www.w3.org/2002/xforms' and local-name()='model')]"/>

                    <!-- detect how many outermost XForms elements we have in the body -->
                    <xsl:choose>
                        <xsl:when test="count($outermostNodeset) = 1">
                            <!-- match any body content and defer creation of form tag for XForms processing.
                             This option allows to mix HTML forms with XForms markup. -->
                            <!-- todo: issue to revisit: this obviously does not work in case there's only one xforms control in the document. In that case the necessary form tag is not written. -->
                            <!-- hack solution: add an output that you style invisible to the form to make it work again. -->

                            <!--possible solution -->
                            <!--<xsl:when test="count($outermostNodeset)=1 and count($outermostNodeset/xf:*) != 0">-->
                            <xsl:variable name="inlineContent"><xsl:apply-templates mode="inline"/></xsl:variable>
                            <xsl:choose>
                                <xsl:when test="exists($inlineContent//xf:*)">
                                    <xsl:element name="form">
                                        <xsl:call-template name="createFormAttributes"/>
                                        <xsl:apply-templates select="*"/>
                                    </xsl:element>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:copy-of select="$inlineContent"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:otherwise>
                            <!-- in case there are multiple outermost xforms elements we are forced to create
                             the form tag for the XForms processing.-->
                            <xsl:call-template name="createForm"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <div id="helpWindow" style="display:none"/>

<!--
                    <div id="bfCopyright">
                        <xsl:text disable-output-escaping="yes">powered by betterFORM, &amp;copy; 2011</xsl:text>
                    </div>

                </div>
-->
            </div>
            <!--
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            end of form section
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            Section for the debug bar. Is displayed when debugging is switched on
            in betterform-config.xml
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:if test="$debug-enabled='true'">
                <!-- z-index of 1000 so it is also in front of shim for modal dialogs -->
                <div id="evtLogContainer" style="width:26px;height:26px;overflow:hidden;">
                    <div id="logControls">
                        <a id="switchLog" href="javascript:bf.devtool.toggleLog();">&gt;</a>
                        <a id="trashLog" href="javascript:bf.devtool.clearLog();">x</a>
                    </div>
                    <ul id="eventLog">
                    </ul>
                </div>
                <div id="openclose">
                    <a href="javascript:bf.devtool.toggleDebug();" ><img class="debug-icon" src="{concat($contextroot,'/bfResources/images/collapse.png')}" alt=""/></a>
                </div>
                    <div id="debug-pane" class="open" context="{concat($contextroot,'/inspector/',$sessionKey,'/')}">
                        <div style="float:right;margin-right:20px;text-align:right;" id="copyright">
                            <a href="http://www.betterform.de">
                                <img style="vertical-align:text-bottom; margin-right:5px;"
                                     src="{concat($contextroot,'/bfResources/images/betterform_icon16x16.png')}" alt="betterFORM project"/>
                            </a>
                            <span>&#xA9; 2012 betterFORM</span>
                        </div>
                        <div id="debug-pane-links">
                            <a href="{concat($contextroot,'/inspector/',$sessionKey,'/','hostDOM')}" target="_blank">Host Document</a>
                        </div>
                    </div>
            </xsl:if>

            <span id="templates" style="display:none;">
                <!--
                >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                todo todo todo: section for all templates (formely 'prototypes') needed
                the idea is to keep them all in one place and have a mode 'template'
                to render them all in this place.
                >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                -->
            </span>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            start section for script imports
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            -->
            <xsl:call-template name="addDojoImport"/>
            <xsl:call-template name="addDWRImports"/>

            <xsl:call-template name="addLocalScript"/>
            <xsl:call-template name="copyInlineScript"/>
            <!--
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            start section for script imports
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
        </body>
    </xsl:template>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    match outermost group, repeat or switch. An outermost container is necessary to allow standard HTML forms
    to coexist with XForms markup and still produce non-nested form tags in the output.
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:template
            match="xf:group[not(ancestor::xf:*)][1] | xf:repeat[not(ancestor::xf:*)][1] | xf:switch[not(ancestor::xf:*)][1]"
            mode="inline">
        <!-- ##### the XFormsProcessor itself is always reachable via id 'fluxProcessor' ##### -->
        <xsl:element name="form">
            <xsl:call-template name="createFormAttributes"/>
            <xsl:apply-templates select="."/>
        </xsl:element>
    </xsl:template>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    this template is called when there's no single outermost XForms element meaning there are
    several blocks of XForms markup scattered in the body of the host document.
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:template name="createForm">
        <!-- ##### the XFormsProcessor itself is always reachable via id 'fluxProcessor' ##### -->
        <!--<div dojotype="betterform.FluxProcessor" jsId="fluxProcessor" id="fluxProcessor" sessionkey="{$sessionKey}"/>-->
        <xsl:element name="form">
            <xsl:call-template name="createFormAttributes"/>
            <xsl:for-each select="*">
                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template name="createFormAttributes">
        <xsl:attribute name="name">
            <xsl:value-of select="$form-id"/>
        </xsl:attribute>

        <xsl:attribute name="onSubmit">return false;</xsl:attribute>
        <xsl:attribute name="method">POST</xsl:attribute>

        <!--
        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        logic to decide wether form is submitted urlencoded or as multipart (in case upload controls are present
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        -->
        <xsl:choose>
            <xsl:when test="$uses-upload = false()">
                <xsl:attribute name="enctype">application/x-www-form-urlencoded</xsl:attribute>
                <xsl:attribute name="action">javascript:submitFunction();</xsl:attribute>
            </xsl:when>
            <xsl:otherwise>
                <xsl:attribute name="enctype">multipart/form-data</xsl:attribute>
                <xsl:attribute name="action">
                    <xsl:value-of select="concat($action-url,'?sessionKey=',$sessionKey,'&#38;isUpload=true')"/>
                </xsl:attribute>
                <input type="hidden" name="isUpload" value=""/>
                <iframe id="UploadTarget" name="UploadTarget" src="" style="width:0px;height:0px;border:0"></iframe>
            </xsl:otherwise>
        </xsl:choose>
        <input type="hidden" id="bfSessionKey" name="sessionKey" value="{$sessionKey}"/>
    </xsl:template>

    <!-- ######################################################################################################## -->
    <!-- #####################################  CONTROLS ######################################################## -->
    <!-- ######################################################################################################## -->

    <xsl:template match="xf:input|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:upload">
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                    <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="label-classes"><xsl:call-template name="assemble-label-classes"/></xsl:variable>

        <!--
        todo: attributes from xf controls should probably be 'tunneled' to widget e.g. 'placeholder' of html5
        todo: remove dojotype here and add with behavior?
        -->
        <span id="{@id}" class="{$control-classes}">

            <xsl:call-template name="copy-style-attribute"/>
            <xsl:if test="@bf:incremental-delay">
            	<xsl:attribute name="bf:incremental-delay" select="@bf:incremental-delay"/>
            </xsl:if>

            <xsl:if test="exists(@bf:name)">
                <xsl:attribute name="data-bf-name" select="@bf:name"/>
            </xsl:if>

            <label for="{@id}-value" id="{@id}-label" class="{$label-classes}">
                <xsl:call-template name="create-label">
                    <xsl:with-param name="label-elements" select="xf:label"/>
                </xsl:call-template>
            </label>
            <span class="widgetContainer">
                <xsl:call-template name="buildControl"/>
                <xsl:apply-templates select="xf:alert"/>
                <xsl:apply-templates select="xf:hint"/>
                <xsl:apply-templates select="xf:help"/>
            </span>
            <xsl:copy-of select="script"/>
        </span>
    </xsl:template>


    <!-- ############################## OUTPUT ############################## -->
    <!-- ############################## OUTPUT ############################## -->
    <!-- ############################## OUTPUT ############################## -->
    <xsl:template match="xf:output">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="label-classes"><xsl:call-template name="assemble-label-classes"/></xsl:variable>

        <!--
        todo: attributes from xf controls should probably be 'tunneled' to widget e.g. 'placeholder' of html5
        todo: remove dojotype here and add with behavior?
        -->
        <span id="{$id}" class="{$control-classes}">
            <xsl:call-template name="copy-style-attribute"/>
            <xsl:if test="exists(@bf:name)">
                <xsl:attribute name="data-bf-name" select="@bf:name"/>
            </xsl:if>


            <label for="{$id}-value" id="{$id}-label" class="{$label-classes}">
                <xsl:call-template name="create-label">
                    <xsl:with-param name="label-elements" select="xf:label"/>
                </xsl:call-template>
            </label>

            <xsl:call-template name="buildControl"/>
            <xsl:apply-templates select="xf:alert"/>
            <xsl:apply-templates select="xf:hint"/>
            <xsl:apply-templates select="xf:help"/>
            <xsl:copy-of select="script"/>
        </span>
    </xsl:template>

    <!-- todo: review - what was the intend here? is never used within the stylesheets - remove? -->
    <xsl:template match="xf:output" mode="prototype">
       <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:variable name="label-classes">
            <xsl:call-template name="assemble-label-classes"/>
        </xsl:variable>

        <span id="{$id}" class="{$control-classes}">
            <xsl:call-template name="copy-style-attribute"/>
                <xsl:if test="exists(@bf:name)">
                    <xsl:attribute name="data-bf-name" select="@bf:name"/>
                </xsl:if>


                <label for="{$id}-value" id="{$id}-label" class="{$label-classes}">
                <xsl:call-template name="create-label">
                    <xsl:with-param name="label-elements" select="xf:label"/>
                </xsl:call-template>
                </label>
            <xsl:call-template name="buildControl"/>
            <span id="{$id}-alertAttachPoint" style="display:none;" class="alertAttachPoint"/>
            <xsl:apply-templates select="xf:hint"/>
            <!--<xsl:apply-templates select="xf:help"/>-->

            <xsl:copy-of select="script"/>
        </span>
    </xsl:template>

    <!-- ############################## TRIGGER / SUBMIT ############################## -->
    <!-- ############################## TRIGGER / SUBMIT ############################## -->
    <!-- ############################## TRIGGER / SUBMIT ############################## -->
    <xsl:template match="xf:trigger|xf:submit">
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <span id="{@id}" class="{$control-classes}">
            <xsl:if test="exists(@bf:name)">
                <xsl:attribute name="data-bf-name" select="@bf:name"/>
            </xsl:if>

            <xsl:call-template name="trigger"/>
        </span>
    </xsl:template>

    <!-- ######################################################################################################## -->
    <!-- #####################################  CHILD ELEMENTS ################################################## -->
    <!-- ######################################################################################################## -->

    <!-- ######################### LABEL ############################## -->
    <!-- ######################### LABEL ############################## -->
    <!-- ######################### LABEL ############################## -->
    <xsl:template match="xf:label">
        <!-- match all inline markup and content -->
        <xsl:apply-templates/>
    </xsl:template>

    <!-- todo: review - what was the intend here? is never used within the stylesheets - remove? -->
    <xsl:template match="xf:label" mode="prototype">
        <xsl:choose>
            <xsl:when test="exists(*)">
                <xsl:for-each select="*">
                    <xsl:choose>
                        <xsl:when test="local-name(.)='output'">
                            <xsl:apply-templates select="." mode="prototype"/>
                        </xsl:when>
                        <xsl:otherwise>
                          <xsl:copy-of select="."/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy-of select="."/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ############################## HELP ############################## -->
    <!-- ############################## HELP ############################## -->
    <!-- ############################## HELP ############################## -->
    <xsl:template match="xf:help">
        <span id="{../@id}-help-text" class="bfHelpText" style="display:none;">
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <!-- ############################## ALERT ############################## -->
    <!-- ############################## ALERT ############################## -->
    <!-- ############################## ALERT ############################## -->
    <xsl:template match="xf:alert">
        <span id="{../@id}-alert" class="xfAlert" style="display:none;">
            <xsl:apply-templates/>
            <span class="closeAlertIcon"> </span>
        </span>
    </xsl:template>

    <!-- ############################## HINT ############################## -->
    <!-- ############################## HINT ############################## -->
    <!-- ############################## HINT ############################## -->
    <xsl:template match="xf:hint">
        <xsl:variable name="parentId" select="../@id"/>
        <span id="{$parentId}-hint" class="xfHint" style="display:none">
            <xsl:apply-templates/>

            <!-- if help exists we output the linking icon here -->
            <xsl:if test="exists(../xf:help)">
                <a tabindex="-1" onmouseover="dojo.style(dom.byId('{$parentId}'+'-help-text'),'display','inline-block');"
                                 onmouseout="dojo.style(dom.byId('{$parentId}'+'-help-text'),'display','none');"
                   href=""
                   id="{$parentId}-help"
                   class="xfHelp">
                    <img id="{$parentId}-help-HelpIcon" src="{concat($contextroot,$resourcesPath,'images/helpBubble.png')}"
                         alt="Help" width="16" height="16"/>
                </a>
            </xsl:if>
            <xsl:apply-templates select="../xf:help"/>

        </span>
    </xsl:template>

    <!-- ####################################################################################################### -->
    <!-- #####################################  HELPER TEMPLATES '############################################## -->
    <!-- ####################################################################################################### -->

    <xsl:template name="buildControl">
        <xsl:choose>
            <xsl:when test="local-name()='input'">
                <xsl:call-template name="input"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='output'">
                <xsl:call-template name="output"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='range'">
                <xsl:call-template name="range"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='secret'">
                <xsl:call-template name="secret"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='select'">
                <xsl:call-template name="select"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='select1'">
                <xsl:call-template name="select1"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='submit'">
                <xsl:call-template name="submit"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='trigger'">
                <xsl:call-template name="trigger"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='textarea'">
                <xsl:call-template name="textarea"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>
            <xsl:when test="local-name()='upload'">
                <xsl:call-template name="upload"/>
                <!-- xf:hint is handled by widget itself -->
                <!--<xsl:apply-templates select="xf:help"/>-->
                <!--<xsl:apply-templates select="xf:alert"/>-->
            </xsl:when>

            <!--todo: JT: check if these can ever be reached -->
<!--
            <xsl:when test="local-name()='repeat'">
                <xsl:apply-templates select="."/>
            </xsl:when>
            <xsl:when test="local-name()='group'">
                <xsl:apply-templates select="."/>
                <xsl:apply-templates select="xf:hint"/>
                <xsl:apply-templates select="xf:help"/>
                <xsl:apply-templates select="xf:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='switch'">
                <xsl:apply-templates select="."/>
            </xsl:when>
-->
        </xsl:choose>
    </xsl:template>

    <!-- What's this?? -->
    <xsl:template match="pre">
        <span class="abc"/>
        <xsl:copy-of select="."/>
    </xsl:template>

    <!-- ####################################### NAMED HELPER TEMPLATES ########################################## -->
    <!-- ####################################### NAMED HELPER TEMPLATES ########################################## -->
    <!-- ####################################### NAMED HELPER TEMPLATES ########################################## -->

    <xsl:template name="addDojoImport">
        <xsl:variable name="dojoConfig">
            has: {
                "dojo-firebug": <xsl:value-of select="$debug-enabled"/>,
                "dojo-debug-messages": <xsl:value-of select="$debug-enabled"/>
            },
            debugAtAllCosts:<xsl:value-of select="$debug-enabled"/>,
            isDebug:<xsl:value-of select="$debug-enabled"/>,
            locale:'<xsl:value-of select="$locale"/>',
            baseUrl: '<xsl:value-of select="concat($contextroot,$scriptPath)"/>',

            parseOnLoad:false,
            async:true,

            packages: [
                'dojo',
                'dijit',
                'dojox',
                'bf'
            ],

            bf:{
                sessionkey: "<xsl:value-of select="$sessionKey"/>",
                contextroot:"<xsl:value-of select="$contextroot"/>",
                fluxPath:"<xsl:value-of select="concat($contextroot,'/Flux')"/>",
                useDOMFocusIN:<xsl:value-of select="$uses-DOMFocusIn"/>,
                logEvents:<xsl:value-of select="$debug-enabled"/>
            }
        </xsl:variable>

        <script type="text/javascript" src="{concat($contextroot,$scriptPath,'dojo/dojo.js')}">
            <xsl:attribute name="data-dojo-config"><xsl:value-of select="normalize-space($dojoConfig)"/></xsl:attribute>
        </script><xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template name="addLocalScript">
        <script type="text/javascript">
            require(["bf/XFProcessor","bf/XFormsModelElement"],
                function(XFProcessor, XFormsModelElement){
                        console.debug("ready - new Session with key:", dojo.config.bf.sessionkey);
                        <!-- create a XForms Processor for the form -->
                        fluxProcessor = new XFProcessor();
                        <!-- create a XFormsModelElement class for each model in the form -->
                        <xsl:for-each select="//xf:model">
                            <xsl:value-of select="@id"/> = new XFormsModelElement({id:"<xsl:value-of select="@id"/>"});
                        </xsl:for-each>
                }
            );
        </script><xsl:text>
</xsl:text>
    </xsl:template>

</xsl:stylesheet>
