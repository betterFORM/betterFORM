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
    <xsl:param name="contextroot" select="'/Users/joern/dev/betterFORM/tools/component-incubator/target/'"/>

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


    <xsl:param name="realPath" select="''"/>
    <xsl:param name="componentPath" select="'webcomponents/'"/>

    <xsl:param name="form-name" select="//title"/>

    <xsl:variable name="CR">
        <xsl:text>&#xa;</xsl:text>
    </xsl:variable>

    <!-- ####################################################################################################### -->
    <!-- ##################################### TEMPLATES ####################################################### -->
    <!-- ####################################################################################################### -->

    <xsl:output method="xhtml" omit-xml-declaration="yes"/>


    <xsl:template match="/html">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>

        <xsl:copy copy-namespaces="no">
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="head">
        <xsl:copy copy-namespaces="no">

            <xsl:comment>*** power   ed by betterFORM, &amp;copy; 2012 ***</xsl:comment>

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
            <link href="{$contextroot}/{$componentPath}styles.css" rel="stylesheet"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            copy user-defined stylesheets and inline styles
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="getLinkAndStyle"/>
            <xsl:call-template name="copyStyles"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            include needed javascript files
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="copyInlineScript"/>
            <!--
            *******************************************************************************
            Experimental:
            model id is used as variable name for js model. Names are split at '-' to
            avoid JS name convention problems - you must be aware of that when authoring
            pages.
            *******************************************************************************
            -->
            <!--
                        <script type="text/javascript">


                            var <xsl:value-of select="substring-after(//xf:model[1]/@id,'-')"/> = {
                            <xsl:for-each select="//bf:data">
                                <xsl:message>data:<xsl:value-of select="."/>
                                </xsl:message>
                                    <xsl:value-of select="../@id"/>:{
                                        id:"<xsl:value-of select="../@id"/>",
                                        readonly:<xsl:value-of select="@readonly"/>,
                                        required:<xsl:value-of select="@required"/>,
                                        relevant:<xsl:value-of select="@enabled"/>,
                                        valid:<xsl:value-of select="@valid"/>,
                                        type:"<xsl:value-of select="@type"/>",
                                        value:"<xsl:value-of select="./text()"/>"
                                    }<xsl:if test="position() != last()">,
                            </xsl:if>
                            </xsl:for-each>

                            };
                        </script>
            -->

            <xsl:comment>##### global web components #####</xsl:comment>
            <xsl:value-of select="$CR"/>


        </xsl:copy>

    </xsl:template>


    <xsl:template match="body">
        <xsl:element name="body">
            <xsl:copy-of select="@*" copy-namespaces="no"/>

<!--
            <fore-model>
                <xsl:for-each select="//bf:data">
                    <xsl:element name="xf-state">
                        <xsl:attribute name="for">
                            <xsl:value-of select="../@id"/>
                        </xsl:attribute>
                        <xsl:copy-of select="text()" copy-namespaces="no"/>
                    </xsl:element>
                </xsl:for-each>
            </fore-model>
-->
            <better-atmosphere></better-atmosphere>
            <xsl:apply-templates/>

<!--
            <script type="text/javascript">

                var data = {
                    <xsl:for-each select="//bf:data">
                        <xsl:value-of select="../@id"/>:{
                            value:<xsl:value-of select="."/>,
                            readonly:<xsl:value-of select="@readonly"/>
                        }
                    </xsl:for-each>
                };
            </script>
-->

            <xsl:value-of select="$CR"/>




            <script src="{$scriptPath}/x-tag-core/dist/x-tag-core.js" type="text/javascript"/>

            <script src="/betterform/webcomponents/bower_components/jquery/dist/jquery.js" type="text/javascript"></script>
            <xsl:value-of select="$CR"/>
            <script src="/betterform/webcomponents/bower_components/jquery-atmosphere/jquery.atmosphere.js" type="text/javascript"></script>
            <xsl:value-of select="$CR"/>

            <script src="/betterform/webcomponents/better-atmosphere.js" type="text/javascript"></script>

            <!--<xsl:variable name="model" select="unparsed-text(concat($realPath,$componentPath,'fore-model.html'))"/>-->
            <!--<xsl:variable name="raw" select="encode-for-uri($model/text())"/>-->




<!--
            <script type="text/javascript">
                <xsl:value-of select="substring-before(substring-after($model, substring-before($model, 'xtag')), '&#60;/script')" disable-output-escaping="yes"/>
            </script>
-->
            <xsl:value-of select="$CR"/>
            <script type="text/javascript">
/*
                xtag.register('xf-state', {
                    lifecycle:{
                    created: function(){
                    console.log("state created");
                    // fired once at the time a component
                    // is initially created or parsed
                    },
                    inserted: function(){
                    console.log("state inserted");
                    // fired each time a component
                    // is inserted into the DOM
                    },
                    removed: function(){
                    console.log("removed");
                    // fired each time an element
                    // is removed from DOM
                    },
                    attributeChanged: function(){
                    console.log("attributeChanged");
                    // fired when attributes are set
                    }
                    },
                    events: {
                    },
                    accessors: {
                    },
                    methods: {
                    }
                });
*/

                <xsl:apply-templates select="//*[contains(name(.),'-')]" mode="scripts"/>
                <xsl:apply-templates select="//xf:*" mode="scripts"/>
            </script>
            <xsl:value-of select="$CR"/>

        </xsl:element>

        <!--
        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        Polymer import - the only js file imported directly
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        <xsl:value-of select="$CR"/>
        <script type="text/javascript" src="{concat($contextroot,$scriptPath,'platform/platform.js')}"/>
        <xsl:value-of select="$CR"/>
        <xsl:value-of select="$CR"/>
        <script type="text/javascript" src="{concat($contextroot,$scriptPath,'polymer/polymer.js')}"/>
        <xsl:value-of select="$CR"/>
         -->

    </xsl:template>

    <!-- ##### always hide model on client for security reasons ###### -->
    <xsl:template match="xf:model" priority="20"/>
    <xsl:template match="xf:model | xf:instance | xf:bind" priority="20" mode="scripts"/>

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
    </xsl:template>


    <xsl:template match="xf:*">
        <!--<xsl:message>element <xsl:value-of select="local-name()"/></xsl:message>-->

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

        <!--<xsl:message>Mapped control:'<xsl:value-of select="$controlType"/>'</xsl:message>-->

        <!-- ##### $contextroot + $componantPath + $controlType + .html ##### -->
        <xsl:variable name="componentURI"><xsl:value-of select="concat($realPath,$componentPath,$controlType,'.html')"/></xsl:variable>
        <xsl:message>Mapped path:'<xsl:value-of select="$componentURI"/>'</xsl:message>

        <xsl:choose>
            <xsl:when test="doc-available($componentURI)">

                <xsl:variable name="component" select="document($componentURI)//template/*" xpath-default-namespace=""/>
                <xsl:element name="xf-{local-name()}" namespace="http://www.w3.org/1999/xhtml">
                    <xsl:copy-of select="@*" copy-namespaces="no"/>
                    <!-- ### handling state attributes #### -->
                    <xsl:attribute name="value"><xsl:value-of select="bf:data/text()"/></xsl:attribute>
                    <xsl:copy-of select="bf:data/@*"/>

                    <!-- ***** most likely this will be needed again later on with textarea or output ***** -->
                    <!--<xf-value><xsl:value-of select="bf:data/text()"/></xf-value>-->
                    <xsl:choose>
                    <xsl:when test="exists($component)">
                        <!--<xsl:apply-templates select="xf:label"/>-->


                        <xsl:for-each select="$component">
                                <xsl:apply-templates select="." mode="copyTemplate">
                                    <xsl:with-param name="parent" select="$this"/>
                                </xsl:apply-templates>
                            </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates/>
                    </xsl:otherwise>
                </xsl:choose>
                </xsl:element>

            </xsl:when>
            <xsl:otherwise><div class="error" title="File not found: {$componentURI}"></div></xsl:otherwise>
        </xsl:choose>


    </xsl:template>

<!--
    <xsl:template match="xf:label | xf:hint | xf:alert | xf:help" priority="10">
        <label class="{local-name()}" for="{../@id}"><xsl:value-of select="text()"/></label>
    </xsl:template>
-->

    <xsl:template match="*" mode="scripts">
        <xsl:variable name="controlType">
            <xsl:value-of select="local-name()"/>
            <xsl:if test="string-length(bf:data/@type) != 0">-<xsl:value-of select="bf:data/@type"/>
            </xsl:if>
            <xsl:if test="string-length(@appearance) != 0">-<xsl:value-of select="@appearance"/>
            </xsl:if>
            <xsl:if test="string-length(@mediatype) != 0">-<xsl:value-of select="@mediatype"/>
            </xsl:if>
            <xsl:if test="string-length(@selection) != 0">-<xsl:value-of select="@selection"/>
            </xsl:if>
        </xsl:variable>
        <!-- ##### $contextroot + $componantPath + $controlType + .html ##### -->
        <xsl:variable name="componentURI"><xsl:value-of select="concat($realPath,$componentPath,$controlType,'.html')"/></xsl:variable>
        <xsl:message>Mapped script path:'<xsl:value-of select="$componentURI"/>'</xsl:message>

        <xsl:choose>
            <xsl:when test="doc-available($componentURI)">

                <xsl:variable name="script" select="document($componentURI)//script/text()" xpath-default-namespace=""/>
                <xsl:if test="exists($script)">
                    <xsl:for-each select="$script">
                        <xsl:value-of select="."/>
                    </xsl:for-each>
                </xsl:if>

            </xsl:when>
            <xsl:otherwise>console.log('script for <xsl:value-of select="$componentURI"/> not found');</xsl:otherwise>
        </xsl:choose>

    </xsl:template>

    <xsl:template match="*" mode="copyTemplate">
        <xsl:param name="parent"/>
        <xsl:message>#################### copying template node:
            <xsl:value-of select="name()"/>
        </xsl:message>
        <xsl:element name="{name(.)}" namespace="http://www.w3.org/1999/xhtml">
            <xsl:apply-templates select="@*" mode="copyTemplate">
                <xsl:with-param name="parent" select="$parent"/>
            </xsl:apply-templates>

            <xsl:choose>
                <xsl:when test="starts-with(.,'{')">
                    <xsl:variable name="value">
                        <xsl:call-template name="resolveValue">
                            <xsl:with-param name="parent" select="$parent"/>
                        </xsl:call-template>
                    </xsl:variable>

                    <xsl:value-of select="$value"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates mode="copyTemplate">
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:element>
    </xsl:template>


    <xsl:template match="@*" mode="copyTemplate" priority="10">
        <xsl:param name="parent"/>
<!--
        <xsl:message>#################### copying  template attribute: <xsl:value-of select="name()"/></xsl:message>
        <xsl:message>#################### copying template attribute value: <xsl:value-of select="."/></xsl:message>
        <xsl:message>#################### copying template  parent: <xsl:value-of select="name($parent)"/></xsl:message>

-->

        <xsl:variable name="templ"><xsl:value-of select="substring(.,2, string-length(.) - 2 )"/></xsl:variable>

        <xsl:variable name="value">
            <xsl:call-template name="resolveValue">
                <xsl:with-param name="parent" select="$parent"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:attribute name="{local-name()}"><xsl:value-of select="$value"/></xsl:attribute>
    </xsl:template>

    <xsl:template name="resolveValue">
        <xsl:param name="parent"/>
        <xsl:choose>
            <xsl:when test="starts-with(.,'{@')">
                <xsl:variable name="path" select="substring(.,3, string-length(.) - 3 )"/>
                <xsl:value-of select="$parent/@*[name(.)=$path]"/>
            </xsl:when>
            <xsl:when test="starts-with(.,'{')">
                <xsl:variable name="path" select="substring(.,2, string-length(.) - 2 )"/>
                <xsl:value-of select="$parent/*[name(.)=$path]"/>
            </xsl:when>
            <xsl:otherwise><xsl:value-of select="."/></xsl:otherwise>
        </xsl:choose>
    </xsl:template>

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
