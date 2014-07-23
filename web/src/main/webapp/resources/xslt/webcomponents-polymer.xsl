<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xhtml="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xf="http://www.w3.org/2002/xforms"
        xmlns:bf="http://betterform.sourceforge.net/xforms"
        xmlns:ev="http://www.w3.org/2001/xml-events"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        exclude-result-prefixes="xf bf xhtml ev xsd"
        xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <xsl:strip-space elements="*"/>
    <!-- ############################################ PARAMS ################################################### -->
    <!-- ############################################ PARAMS ################################################### -->
    <!-- ############################################ PARAMS ################################################### -->
    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    contextroot - the name of the webapp context (default: 'betterform'
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="contextroot" select="''"/>


    <xsl:param name="sessionKey" select="''"/>
    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    resources are served by ResourceServlet. This param passes the mapping path
    of ResourceServlet
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="resourcesPath" select="'/bfResources'"/>

    <!---
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    relative path to javascript files within resources
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="scriptPath" select="concat($contextroot,'/webcomponents/','bower_components/')"/>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    relative path to CSS files within resources
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="CSSPath" select="concat($resourcesPath,'styles/')"/>

    <xsl:param name="locale" select="'en'"/>

    <xsl:param name="realPath" select="'/Users/joern/dev/betterFORM/tools/component-incubator/target/'"/>
    <xsl:param name="componentPath" select="'webcomponents/'"/>

    <xsl:param name="debug-enabled" select="'true'"/>

    <xsl:param name="form-name" select="//title"/>

    <xsl:variable name="isDebugEnabled" select="$debug-enabled eq 'true'" as="xsd:boolean"/>

    <xsl:variable name="CR">
        <xsl:text>&#xa;</xsl:text>
    </xsl:variable>

    <!-- ####################################################################################################### -->
    <!-- ##################################### TEMPLATES ####################################################### -->
    <!-- ####################################################################################################### -->

    <xsl:output method="xhtml" omit-xml-declaration="yes"/>


    <xsl:template match="/html">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>

        <xsl:message>contextroot:
            <xsl:value-of select="$contextroot"/>
        </xsl:message>
        <xsl:message>resourcesPath:
            <xsl:value-of select="$resourcesPath"/>
        </xsl:message>
        <xsl:message>scriptPath:
            <xsl:value-of select="$scriptPath"/>
        </xsl:message>
        <xsl:message>CSSPath:
            <xsl:value-of select="$CSSPath"/>
        </xsl:message>

        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="head">
        <xsl:copy copy-namespaces="no">

            <xsl:comment>*** power ed by betterFORM, &amp;copy; 2012 ***</xsl:comment>

            <xsl:apply-templates select="title"/>

            <!-- copy all meta tags except 'contenttype' -->
            <xsl:call-template name="getMeta"/>
            <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            CSS needed by Dojo is added here
            todo: review - is there a way to load the CSS conditionally so that
            they won't be loaded when in non-scripted mode? Surely would be possible
            with JS but is it worth the effort?
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="addGlobalCSS"/>
            <link href="{$contextroot}{$resourcesPath}/css/styles.css" rel="stylesheet"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            copy user-defined stylesheets and inline styles
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="getLinkAndStyle"/>
            <xsl:call-template name="copyStyles"/>


            <!--<link rel="import" href="/betterform/webcomponents/bower_components/polymer/polymer.html"></link>-->

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            include needed javascript files
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="copyInlineScript"/>

            <xsl:comment>##### global web components #####</xsl:comment>


            <!--<link rel="import" href="/betterform/webcomponents/bower_components/polymer/polymer.html"></link>-->

            <xsl:value-of select="$CR"/>
            <link rel="import" href="/betterform/webcomponents/polymer-elements/better-jquery-atmosphere/better-jquery-atmosphere.html"></link>
            <link rel="import" href="/betterform/webcomponents/polymer-elements/xf-input.html"></link>
            <link rel="import" href="/betterform/webcomponents/polymer-elements/xf-trigger.html"></link>
            <link rel="import" href="/betterform/webcomponents/polymer-elements/xf-hint.html"></link>

        </xsl:copy>

    </xsl:template>


    <xsl:template match="body">
        <xsl:element name="body">
            <xsl:copy-of select="@*" copy-namespaces="no"/>

            <xsl:apply-templates/>

            <!-- jt: @transport is not wired yet in script - just a hint how to do it later -->
            <better-jquery-atmosphere transport="sse" xfSession="{$sessionKey}"></better-jquery-atmosphere>

            <xsl:value-of select="$CR"/>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            Polymer import - the only js file imported directly
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <script src="/betterform/webcomponents/bower_components/platform/platform.js"></script>
            <xsl:value-of select="$CR"/>
            <script src="/betterform/webcomponents/bower_components/jquery/dist/jquery.js" type="text/javascript"></script>
            <xsl:value-of select="$CR"/>
            <script src="/betterform/webcomponents/bower_components/jquery-atmosphere/jquery.atmosphere.js" type="text/javascript"></script>
            <xsl:value-of select="$CR"/>
            <!--<script src="/betterform/webcomponents/js/better-atmosphere.js" type="text/javascript"></script>-->
            <xsl:value-of select="$CR"/>
            <!--
                        <script type="text/javascript">


                            <xsl:apply-templates select="//*[contains(name(.),'-')]" mode="scripts"/>
                            <xsl:for-each-group select="//xf:*" group-by="local-name()">
                                <xsl:apply-templates select="." mode="scripts"/>
                            </xsl:for-each-group>

                        </script>
                    <xsl:value-of select="$CR"/>
            -->
            <xsl:if test="$isDebugEnabled">
                <footer>
                    <div class="open" context="{concat($contextroot,'/inspector/',$sessionKey,'/')}">
                        <div class="bf-debug-bar">
                            <a href="{concat($contextroot,'/inspector/',$sessionKey,'/','hostDOM')}" target="_blank">Host Document</a>
                        </div>
                    </div>
                </footer>
            </xsl:if>
            <footer class="bf-copyright">
                <div>
                    <a href="http://www.betterform.de">
                        <img style="vertical-align:text-bottom; margin-right:5px;"
                                src="{concat($contextroot,'/bfResources/images/betterform_icon16x16.png')}" alt="betterFORM project"/>
                    </a>
                    <span>&#xA9; 2014 betterFORM</span>
                </div>
            </footer>

        </xsl:element>

    </xsl:template>

    <!-- ##### always hide model on client for security reasons ###### -->
    <xsl:template match="xf:model" priority="20"/>

    <xsl:template match="bf:data">
        <!--
        <xf-state>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="*|text()"/>
        </xf-state>
        -->
    </xsl:template>

    <!--
        <xsl:template match="xf:input[bf:data/@type eq 'email']" priority="10">
            <input type="{bf:data/@type}" is="xf-select1-email" ref="{./@ref}">
                <xf-state>
                    <xsl:copy-of select="bf:data/@*" copy-namespaces="no"/>
                </xf-state>
            </input>
        </xsl:template>
    -->

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    templates for overwriting
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:template name="addGlobalCSS">
        <!--<link type="stylesheet" href="{$resourcesPath}/scripts/x-tag-bundle/x-tag-components.min.css"/>-->
        <link type="stylesheet" href="{$contextroot}{$resourcesPath}/styles/betterform.css"/>
    </xsl:template>


    <xsl:template match="xf:*">
        <xsl:variable name="this" select="."/>
        <!-- ##### $contextroot + $componantPath + $controlType + .html ##### -->
        <xsl:variable name="componentURI">
            <xsl:call-template name="getComponentURI"/>
        </xsl:variable>

        <!--
                <xsl:choose>
                    <xsl:when test="doc-available($componentURI)">
                        <xsl:variable name="main-template" select="document($componentURI)//element/template/*" xpath-default-namespace=""/>
                        <xsl:element name="xf-{local-name()}" namespace="http://www.w3.org/1999/xhtml">
                            <xsl:copy-of select="@*" copy-namespaces="no"/>
                            &lt;!&ndash; ### handling state attributes #### &ndash;&gt;
                            <xsl:attribute name="value"><xsl:value-of select="bf:data/text()"/></xsl:attribute>
                            <xsl:copy-of select="bf:data/@*"/>
                                <xsl:apply-templates/>
                        </xsl:element>

                    </xsl:when>
                    <xsl:otherwise>
                        &lt;!&ndash;<div class="error" title="File not found: {$componentURI}"></div>&ndash;&gt;
                        <xsl:apply-templates/>
                    </xsl:otherwise>
                </xsl:choose>
        -->
        <xsl:element name="xf-{local-name()}" namespace="http://www.w3.org/1999/xhtml">
            <xsl:copy-of select="@*" copy-namespaces="no"/>
            <xsl:attribute name="value">
                <xsl:value-of select="bf:data/text()"/>
            </xsl:attribute>
            <xsl:copy-of select="bf:data/@*"/>
            <xsl:apply-templates/>
        </xsl:element>


    </xsl:template>

    <xsl:template name="getComponentURI">
        <xsl:variable name="this" select="."/>
        <xsl:variable name="controlType">
            <xsl:value-of select="local-name()"/>
            <xsl:if test="string-length($this/bf:data/@type) != 0">-<xsl:value-of select="$this/bf:data/@type"/>
            </xsl:if>
            <xsl:if test="string-length(@appearance) != 0">-<xsl:value-of select="@appearance"/>
            </xsl:if>
            <xsl:if test="string-length(@mediatype) != 0">-<xsl:value-of select="@mediatype"/>
            </xsl:if>
            <xsl:if test="string-length(@selection) != 0">-<xsl:value-of select="@selection"/>
            </xsl:if>
        </xsl:variable>
        <xsl:value-of select="concat($realPath,$componentPath,$controlType,'.html')"/>
        <xsl:message>Mapped path:'<xsl:value-of select="concat($realPath,$componentPath,$controlType,'.html')"/>'
        </xsl:message>
    </xsl:template>

    <!--
        <xsl:template match="xf:label | xf:hint | xf:alert | xf:help" priority="10">
            <label class="{local-name()}" for="{../@id}"><xsl:value-of select="text()"/></label>
        </xsl:template>
    -->

    <xsl:template match="*[@startsize]">
        <xsl:copy copy-namespaces="no">
            <xsl:copy-of select="@*" copy-namespaces="no"/>
            <xsl:attribute name="is">xf-repeat</xsl:attribute>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    utilities
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->

    <xsl:template match="*|@*|text()|comment()" name="handle-foreign-elements">
        <!--<xsl:message>element: <xsl:value-of select="name()"/></xsl:message>-->
        <xsl:copy copy-namespaces="no">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="getMeta">
        <xsl:variable name="uc">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
        <xsl:variable name="lc">abcdefghijklmnopqrstuvwxyz</xsl:variable>
        <xsl:for-each select="meta">
            <xsl:choose>
                <xsl:when test="translate(./@http-equiv, $uc, $lc) = 'content-type'"/>
                <xsl:otherwise>
                    <meta>
                        <xsl:copy-of select="@*" copy-namespaces="no"/>
                    </meta>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="getLinkAndStyle"><xsl:text>
</xsl:text>
        <xsl:for-each select="link[not(@rel='import')]">
            <xsl:element name="{local-name()}">
                <xsl:copy-of select="@*" copy-namespaces="no"/>
            </xsl:element>
        </xsl:for-each><xsl:text>
</xsl:text>

    </xsl:template>

    <!-- copy inline styles-->
    <xsl:template name="copyStyles">
        <xsl:for-each select="style">
            <xsl:variable name="content" select="text()"/>
            <xsl:element name="style">
                <xsl:value-of select="$content" disable-output-escaping="yes"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <!-- copy inline javascript -->
    <xsl:template name="copyInlineScript">
        <xsl:message>## copyInlineScript</xsl:message>
        <xsl:for-each select="script">
            <xsl:message>#### found element
                <xsl:value-of select="position"/>
            </xsl:message>
            <xsl:value-of select="$CR"/>
            <xsl:copy-of select="." copy-namespaces="no"/>
            <xsl:value-of select="$CR"/>
        </xsl:for-each>
    </xsl:template>


</xsl:stylesheet>
