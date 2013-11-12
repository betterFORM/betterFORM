<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
        xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:xf="http://www.w3.org/2002/xforms"
        xmlns:bf="http://betterform.sourceforge.net/xforms"
        xmlns:ev="http://www.w3.org/2001/xml-events"
        xmlns:xsd="http://www.w3.org/2001/XMLSchema"
        exclude-result-prefixes="xf bf"
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

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    resources are served by ResourceServlet. This param passes the mapping path
    of ResourceServlet
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:param name="resourcesPath" select="'/resources/'"/>

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


    <xsl:param name="form-name" select="//title"/>

    <xsl:variable name="CR"><xsl:text>&#xa;</xsl:text></xsl:variable>

    <!-- ####################################################################################################### -->
    <!-- ##################################### TEMPLATES ####################################################### -->
    <!-- ####################################################################################################### -->

    <xsl:output method="xhtml"  omit-xml-declaration="yes"/>


    <xsl:template match="/html">
        <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html></xsl:text>
        <html>
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <xsl:template match="head">
        <xsl:copy>

            <xsl:comment>*** powered by betterFORM, &amp;copy; 2012 ***</xsl:comment>

            <xsl:apply-templates select="title"/>

            <!-- copy all meta tags except 'contenttype' -->
            <xsl:call-template name="getMeta"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            CSS needed by Dojo is added here
            todo: review - is there a way to load the CSS conditionally so that
            they won't be loaded when in non-scripted mode? Surely would be possible
            with JS but is it worth the effort?
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="addGlobalCSS"/>

            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            copy user-defined stylesheets and inline styles
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:call-template name="getLinkAndStyle"/>
            <xsl:call-template name="copyStyles"/>


            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            Polymer import - the only js file imported directly
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:value-of select="$CR"/>
            <script type="text/javascript" src="{concat($contextroot,$scriptPath,'polymer/polymer.min.js')}"/>
            <xsl:value-of select="$CR"/>

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

            <xsl:comment> ##### global web components #####</xsl:comment>
            <xsl:value-of select="$CR"/>


            <link rel="import" href="bfResources/elements/fore-processor.html"><xsl:text> </xsl:text></link>
            <link rel="import" href="bfResources/elements/xf-state.html"><xsl:text> </xsl:text></link>


            <xsl:call-template name="importWebComponents"/>

        </xsl:copy>

    </xsl:template>


    <xsl:template match="body">
        <xsl:element name="body">
            <xsl:copy-of select="@*"/>

            <fore-processor id="fore">
                <xsl:for-each select="//bf:data">
                    <xf-state>
                        <xsl:attribute name="for"><xsl:value-of select="../@id"/></xsl:attribute>
                        <xsl:copy-of select="@*"/>
                        <xsl:copy-of select="text()"/>
                    </xf-state>
                </xsl:for-each>
            </fore-processor>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>

    <!-- ##### always hide model on client for security reasons ###### -->
    <xsl:template match="xf:model" priority="20"/>

    <xsl:template match="bf:data">
        <xf-state>
            <xsl:copy-of select="@*"/>
        </xf-state>
    </xsl:template>

    <xsl:template match="xf:input[bf:data/@type eq 'email']" priority="10">
        <input type="{bf:data/@type}" is="xf-select1-email" ref="{./@ref}">
            <xf-state>
                <xsl:copy-of select="bf:data/@*"/>
            </xf-state>
        </input>
    </xsl:template>

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    templates for overwriting
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:template name="addGlobalCSS"/>

    <xsl:template match="xf:*">
        <xsl:message>element <xsl:value-of select="local-name()"/> with appearance <xsl:value-of select="./@appearance"/></xsl:message>
        <xsl:variable name="self" select="."/>
        <xsl:variable name="component-name">
            <xsl:choose>
                <xsl:when test="starts-with($self/@appearance,'component:')"><xsl:value-of select="substring-after(@appearance,':')"/></xsl:when>
                <xsl:otherwise>xf-<xsl:value-of select="local-name()"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="appearance">
            <xsl:choose>
                <xsl:when test="starts-with($self/@appearance,'component')"></xsl:when>
                <xsl:when test="exists($self/@appearance)"><xsl:value-of select="$self/@appearance"/></xsl:when>
                <xsl:otherwise></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:element name="{$component-name}">
            <xsl:if test="$appearance != ''">
                <xsl:attribute name="appearance" select="$appearance"/>
            </xsl:if>
            <xsl:copy-of select="@*[name() != 'appearance']"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>


    <!--
    ##############################################################################################################
    imports web components for existing xforms controls.
    ##############################################################################################################
    -->
    <xsl:template name="importWebComponents">
        <xsl:comment> ##### xforms web components #####</xsl:comment>
        <xsl:if test="exists(//xf:input)"><link rel="import" href="bfResources/elements/xf-input.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:output)"><link rel="import" href="bfResources/elements/xf-output.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:range)"><link rel="import" href="bfResources/elements/xf-range.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:secret)"><link rel="import" href="bfResources/elements/xf-secret.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:select1)"><link rel="import" href="bfResources/elements/xf-select1.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:select)"><link rel="import" href="bfResources/elements/xf-select.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:textarea)"><link rel="import" href="bfResources/elements/xf-textarea.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:submit)"><link rel="import" href="bfResources/elements/xf-submit.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:trigger)"><link rel="import" href="bfResources/elements/xf-trigger.html"><xsl:text> </xsl:text></link></xsl:if>
        <xsl:if test="exists(//xf:upload)"><link rel="import" href="bfResources/elements/xf-upload.html"><xsl:text> </xsl:text></link></xsl:if>


        <xsl:comment> ##### User web components #####</xsl:comment>
        <xsl:for-each select="link[@rel eq 'import']">
            <link rel="{@rel}" href="{@href}"><xsl:text> </xsl:text></link>
        </xsl:for-each>
    </xsl:template>
    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    utilities
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->

    <xsl:template match="*|@*|text()|comment()" name="handle-foreign-elements">
        <xsl:copy>
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
                        <xsl:copy-of select="@*"/>
                    </meta>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="getLinkAndStyle"><xsl:text>
</xsl:text>
        <xsl:for-each select="link">
            <xsl:element name="{local-name()}">
                <xsl:copy-of select="@*"/>
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
            <xsl:copy-of select="."/>
            <xsl:value-of select="$CR"/>
        </xsl:for-each>
    </xsl:template>


</xsl:stylesheet>
