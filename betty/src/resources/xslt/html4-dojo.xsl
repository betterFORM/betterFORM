<?xml version="1.0" encoding="UTF-8"?>
<!--
    Copyright (c) 2010. betterForm Project - http://www.betterform.de
    Licensed under the terms of BSD License */
-->
<xsl:stylesheet version="2.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:xlink="http://www.w3.org/1999/xlink"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    exclude-result-prefixes="xhtml xforms bf xlink">
    <!-- Copyright 2010 betterForm Project-->

    <xsl:include href="dojo-ui.xsl"/>
    <xsl:include href="dojo-controls.xsl"/>


    <!-- ####################################################################################################### -->
    <!-- This stylesheet transcodes a XTHML2/XForms input document to HTML 4.0.                                  -->
    <!-- It serves as a reference for customized stylesheets which may import it to overwrite specific templates -->
    <!-- or completely replace it.                                                                               -->
    <!-- This is the most basic transformator for HTML browser clients and assumes support for HTML 4 tagset     -->
    <!-- but does NOT rely on javascript.                                                                        -->
    <!-- author: joern turner                                                                                    -->
    <!-- ####################################################################################################### -->

    <!-- ############################################ PARAMS ################################################### -->
    <xsl:param name="contextroot" select="''"/>

    <xsl:param name="sessionKey" select="''"/>

    <!-- ### this url will be used to build the form action attribute ### -->
    <xsl:param name="action-url" select="'http://localhost:8080/betterform-1.0.0/XFormsServlet'"/>


    <xsl:param name="form-id" select="'betterform'"/>
    <xsl:param name="form-name" select="//xhtml:title"/>
    <xsl:param name="debug-enabled" select="'true'"/>

    <!-- ### specifies the parameter prefix for repeat selectors ### -->
    <xsl:param name="selector-prefix" select="'s_'"/>

    <!-- ### contains the full user-agent string as received from the servlet ### -->
    <xsl:param name="user-agent" select="'default'"/>

    <xsl:param name="CSS-managed-alerts" select="'true'"/>

    <!--- path to javascript files -->
    <xsl:param name="scriptPath" select="''"/>

    <!-- path to core CSS file -->
    <xsl:param name="CSSPath" select="''"/>

    <xsl:param name="keepalive-pulse" select="'0'"/>

     <!-- ############################################ VARIABLES ################################################ -->
    <!-- ### checks, whether this form uses uploads. Used to set form enctype attribute ### -->
    <xsl:variable name="uses-upload" select="boolean(//*/xforms:upload)"/>

    <!-- ### checks, whether this form makes use of date types and needs datecontrol support ### -->
    <!-- this is only an interims solution until Schema type and base type handling has been clarified -->
    <xsl:variable name="uses-dates">
        <xsl:choose>
            <xsl:when test="boolean(//bf:data/bf:type='date')">true()</xsl:when>
            <xsl:when test="boolean(//bf:data/bf:type='dateTime')">true()</xsl:when>
            <xsl:when test="boolean(substring-after(//bf:data/bf:type,':') ='date')">true()</xsl:when>
            <xsl:when test="boolean(substring-after(//bf:data/bf:type,':') ='dateTime')">true()</xsl:when>
            <xsl:otherwise>false()</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

	<!-- ### checks, whether this form makes use of <textarea xforms:mediatype='text/html'/> ### -->
	<xsl:variable name="uses-html-textarea" select="boolean(//xforms:textarea[@mediatype='text/html'])"/>

    <!-- ### the CSS stylesheet to use ### -->
    <xsl:variable name="default-css" select="concat($contextroot,$CSSPath,'xforms.css')"/>
    <xsl:variable name="betterform-css"  select="concat($contextroot,$CSSPath,'betterform-theme.css')"/>

    <xsl:variable name="default-hint-appearance" select="'bubble'"/>

    <xsl:output method="html" version="4.01" encoding="UTF-8" indent="yes"
                doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"
                doctype-system="http://www.w3.org/TR/html4/loose.dtd"/>
    <!-- ### transcodes the XHMTL namespaced elements to HTML ### -->
    <!--<xsl:namespace-alias stylesheet-prefix="xhtml" result-prefix="#default"/>-->

    <xsl:preserve-space elements="*"/>
    <xsl:strip-space elements="xforms:action"/>

    <!-- ####################################################################################################### -->
    <!-- ##################################### TEMPLATES ####################################################### -->
    <!-- ####################################################################################################### -->

    <xsl:template match="/">
        <xsl:message>running dojo transform</xsl:message>
        <xsl:apply-templates/>
    </xsl:template>

                            
    <xsl:template name="getMeta">
        <xsl:variable name="uc">ABCDEFGHIJKLMNOPQRSTUVWXYZ</xsl:variable>
        <xsl:variable name="lc">abcdefghijklmnopqrstuvwxyz</xsl:variable>
        <xsl:for-each select="xhtml:meta">
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

    <xsl:template name="getLinkAndStyle">
        <xsl:for-each select="xhtml:link|xhtml:style">
            <xsl:element name="{local-name()}">
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>

    <!-- copy unmatched mixed markup, comments, whitespace, and text -->
    <!-- ### copy elements from the xhtml2 namespace to html (without any namespace) by re-creating the     ### -->
    <!-- ### elements. Other Elements are just copied with their original namespaces.                       ### -->
    <xsl:template match="*|@*|text()" name="handle-foreign-elements">
        <xsl:choose>
            <xsl:when test="namespace-uri(.)='http://www.w3.org/1999/xhtml'">
                <xsl:element name="{local-name(.)}" namespace="">
                    <xsl:apply-templates select="*|@*|text()"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="*|@*|text()"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*|@*|text()" mode="inline">
        <xsl:choose>
            <xsl:when test="namespace-uri(.)='http://www.w3.org/1999/xhtml'">
                <xsl:element name="{local-name(.)}" namespace="">
                    <xsl:apply-templates select="*|@*|text()" mode="inline"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="*|@*|text()" mode="inline"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="xhtml:html">
        <html>
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <xsl:template match="xhtml:link">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="xhtml:body" priority="10">
        <body class="tundra">
            <xsl:copy-of select="@*"/>
            <div id="loading">
                <img src="{concat($contextroot,'/resources/images/indicator.gif')}" class="disabled" id="indicator" alt="loading" />
            </div>

            <xsl:variable name="outermostNodeset"
                select=".//xforms:*[not(xforms:model)][not(ancestor::xforms:*)]"/>

            <!-- detect how many outermost XForms elements we have in the body -->
            <xsl:choose>
                <xsl:when test="count($outermostNodeset) = 1">
                    <!-- match any body content and defer creation of form tag for XForms processing.
                     This option allows to mix HTML forms with XForms markup. -->
					<!-- todo: issue to revisit: this obviously does not work in case there's only one xforms control in the document. In that case the necessary form tag is not written. -->
					<!-- hack solution: add an output that you style invisible to the form to make it work again. -->
					<xsl:apply-templates select="//*[@id='xformsui']" mode="inline"/>
                </xsl:when>
                <xsl:otherwise>
                    <!-- in case there are multiple outermost xforms elements we are forced to create
                     the form tag for the XForms processing.-->
                    <xsl:call-template name="createForm"/>
                </xsl:otherwise>
            </xsl:choose>

            <div id="betterform-logo">
                <a href="resources/jsp/forms.jsp">
                    <img src="{concat($contextroot,'/resources/images/poweredby_sw.gif')}" style="border:none;" alt="powered by betterForm"/>
                </a>
            </div>
            <div id="copyright">
                <xsl:text disable-output-escaping="yes">&amp;copy; 2008 betterForm Project</xsl:text>
            </div>
            <xsl:if test="$debug-enabled='true'">
                <script type="text/javascript">
                    dojo.require("dojo.debug.console");
                </script>
            </xsl:if>
            <div id="messagePane"/>
        </body>
    </xsl:template>

    <!--
    match outermost group of XForms markup. An outermost group is necessary to allow standard HTML forms
    to coexist with XForms markup and still produce non-nested form tags in the output.
    -->
    <xsl:template match="xforms:group[not(ancestor::xforms:*)][1] | xforms:repeat[not(ancestor::xforms:*)][1] | xforms:switch[not(ancestor::xforms:*)][1]" mode="inline">
        <xsl:element name="form">
            <xsl:attribute name="name">
                <xsl:value-of select="$form-id"/>
            </xsl:attribute>

            <xsl:attribute name="action">
                <xsl:choose>
                    <xsl:when test="not($uses-upload)">javascript:submitFunction();</xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($action-url,'?sessionKey=',$sessionKey)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="onSubmit">return submitFunction();</xsl:attribute>

            <xsl:attribute name="method">POST</xsl:attribute>
            <xsl:attribute name="enctype">application/x-www-form-urlencoded</xsl:attribute>
            <xsl:if test="$uses-upload">
                <xsl:attribute name="enctype">multipart/form-data</xsl:attribute>
            </xsl:if>
            <input type="hidden" id="bfSessionKey" name="sessionKey" value="{$sessionKey}"/>

            <xsl:apply-templates select="."/>
        </xsl:element>
    </xsl:template>

    <!-- this template is called when there's no single outermost XForms element meaning there are
     several blocks of XForms markup scattered in the body of the host document. -->
    <xsl:template name="createForm">
        <xsl:element name="form">
            <xsl:attribute name="name">
                <xsl:value-of select="$form-id"/>
            </xsl:attribute>

            <xsl:attribute name="action">
                <xsl:choose>
                    <xsl:when test="not($uses-upload)">javascript:submitFunction();</xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($action-url,'?sessionKey=',$sessionKey)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:attribute name="onSubmit">return submitFunction();</xsl:attribute>
            <xsl:attribute name="method">POST</xsl:attribute>
            <xsl:attribute name="enctype">application/x-www-form-urlencoded</xsl:attribute>
            <xsl:if test="$uses-upload">
                <xsl:attribute name="enctype">multipart/form-data</xsl:attribute>
                <xsl:if test="$uses-upload = true">
                    <iframe id="UploadTarget" name="UploadTarget" src="" style="width:0px;height:0px;border:0"></iframe>
                </xsl:if>
            </xsl:if>
            <input type="hidden" id="bfSessionKey" name="sessionKey" value="{$sessionKey}"/>

            
                <xsl:apply-templates select="//*[@id='xformsui']/*"/>
            
        </xsl:element>
    </xsl:template>

    <xsl:template match="xhtml:span">
        <span>
            <xsl:if test="@class">
                <xsl:attribute name="class">
                    <xsl:value-of select="@class"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@id">
                <xsl:attribute name="id">
                    <xsl:value-of select="@id"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@style">
                <xsl:attribute name="style">
                    <xsl:value-of select="@style"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
        </span>
    </xsl:template>

    <!-- ### skip bf:data elements ### -->
    <xsl:template match="bf:data"/>

    <!-- ### skip model section ### -->
    <xsl:template match="xforms:model"/>

    <xsl:template match="xforms:model" mode="inline"/>

    <!-- ######################################################################################################## -->
    <!-- #####################################  CONTROLS ######################################################## -->
    <!-- ######################################################################################################## -->
<!--
    <xsl:template match="xforms:input">
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes"/>
        </xsl:variable>
        <div id="{$id}" class="{$control-classes}" dojotype="betterform.ui.input">

        </div>
    </xsl:template>
-->

    <xsl:template match="xforms:input|xforms:range|xforms:secret|xforms:select|xforms:select1|xforms:textarea|xforms:upload">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes"/>
        </xsl:variable>
        <xsl:variable name="label-classes">
            <xsl:call-template name="assemble-label-classes"/>
        </xsl:variable>

        <div id="{$id}" class="{$control-classes}">
			<xsl:if test="@style">
				<xsl:attribute name="style"><xsl:value-of select="@style"/></xsl:attribute>
			</xsl:if>
            <label for="{$id}-value" id="{$id}-label" class="{$label-classes}"><xsl:apply-templates select="xforms:label"/></label>
            <xsl:call-template name="buildControl"/>
        </div>
    </xsl:template>

    <!-- cause outputs can be inline they should not use a block element wrapper -->
    <xsl:template match="xforms:output">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes"/>
        </xsl:variable>
        <xsl:variable name="label-classes">
            <xsl:call-template name="assemble-label-classes"/>
        </xsl:variable>

        <span id="{$id}" class="{$control-classes}">
			<label for="{$id}-value" id="{$id}-label" class="{$label-classes}"><xsl:apply-templates select="xforms:label"/></label>
            <xsl:call-template name="buildControl"/>
        </span>
    </xsl:template>

    <xsl:template match="xforms:trigger|xforms:submit">
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes"/>
        </xsl:variable>

        <xsl:call-template name="trigger">
            <xsl:with-param name="classes" select="$control-classes"/>
        </xsl:call-template>
    </xsl:template>

    <!-- ######################################################################################################## -->
    <!-- #####################################  CHILD ELEMENTS ################################################## -->
    <!-- ######################################################################################################## -->

    <!-- ### handle label ### -->
    <xsl:template match="xforms:label">
        <!-- match all inline markup and content -->
        <xsl:apply-templates/>

        <!-- check for requiredness -->
        <xsl:if test="../bf:data/@bf:required='true'"><span class="required-symbol">*</span></xsl:if>
    </xsl:template>

    <!-- ### handle hint ### -->
    <xsl:template match="xforms:hint">
        <xsl:attribute name="title">
            <xsl:value-of select="normalize-space(.)"/>
        </xsl:attribute>
    </xsl:template>

    <!-- ### handle help ### -->
    <!-- ### only reacts on help elements with a 'src' attribute and interprets it as html href ### -->
    <xsl:template match="xforms:help">

        <!-- help in repeats not supported yet due to a cloning problem with help elements -->
        <xsl:if test="string-length(.) != 0 and not(ancestor::xforms:repeat)">
                <xsl:element name="a">
                    <xsl:attribute name="onclick">document.getElementById('<xsl:value-of select="../@id"/>' + '-helptext').style.display='block';return false;</xsl:attribute>
                    <xsl:attribute name="onblur">document.getElementById('<xsl:value-of select="../@id"/>' + '-helptext').style.display='none';</xsl:attribute>
                    <xsl:attribute name="href">#</xsl:attribute>
                    <xsl:attribute name="style">text-decoration:none;</xsl:attribute>
                    <xsl:attribute name="class">help-icon</xsl:attribute>
                    <img src="{concat($contextroot,'/resources/images/help_icon.gif')}" class="help-symbol" alt="?" border="0"/>
                </xsl:element>
                <!--<span id="{../@id}-helptext" class="help-text" style="position:absolute;display:none;width:250px;border:thin solid gray 1px;background:lightgrey;padding:5px;">-->
                <span id="{../@id}-helptext" class="help-text">
                    <xsl:apply-templates/>
                </span>
        </xsl:if>

    </xsl:template>

    <!-- ### handle explicitely enabled alert ### -->
    <!--    <xsl:template match="xforms:alert[../bf:data/@bf:valid='false']">-->
    <xsl:template match="xforms:alert">
        <xsl:choose>
            <xsl:when test="$CSS-managed-alerts='true'">
                <span id="{../@id}-alert" class="alert">
                    <xsl:value-of select="."/>
                </span>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="../bf:data/@bf:valid='false'">
                    <span id="{../@id}-alert" class="alert">
                        <xsl:value-of select="."/>
                    </span>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ### handle extensions ### -->
    <xsl:template match="xforms:extension">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="bf:selector">
    </xsl:template>


    <!-- ########################## ACTIONS ####################################################### -->
    <!-- these templates serve no real purpose here but are shown for reference what may be over-   -->
    <!-- written by customized stylesheets importing this one. -->
    <!-- ########################## ACTIONS ####################################################### -->

    <xsl:template match="xforms:action"/>
    <xsl:template match="xforms:dispatch"/>
    <xsl:template match="xforms:rebuild"/>
    <xsl:template match="xforms:recalculate"/>
    <xsl:template match="xforms:revalidate"/>
    <xsl:template match="xforms:refresh"/>
    <xsl:template match="xforms:setfocus"/>
    <xsl:template match="xforms:load"/>
    <xsl:template match="xforms:setvalue"/>
    <xsl:template match="xforms:send"/>
    <xsl:template match="xforms:reset"/>
    <xsl:template match="xforms:message"/>
    <xsl:template match="xforms:toggle"/>
    <xsl:template match="xforms:insert"/>
    <xsl:template match="xforms:delete"/>
    <xsl:template match="xforms:setindex"/>


    <!-- ####################################################################################################### -->
    <!-- #####################################  HELPER TEMPLATES '############################################## -->
    <!-- ####################################################################################################### -->

    <xsl:template name="buildControl">
        <xsl:choose>
            <xsl:when test="local-name()='input'">
                <xsl:call-template name="input"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='output'">
                <xsl:call-template name="output"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='range'">
                <xsl:call-template name="range"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='secret'">
                <xsl:call-template name="secret"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='select'">
                <xsl:call-template name="select"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='select1'">
                <xsl:call-template name="select1"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='submit'">
                <xsl:call-template name="submit"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='trigger'">
                <xsl:call-template name="trigger"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='textarea'">
                <xsl:call-template name="textarea"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='upload'">
                <xsl:call-template name="upload"/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='repeat'">
                <xsl:apply-templates select="."/>
            </xsl:when>
            <xsl:when test="local-name()='group'">
                <xsl:apply-templates select="."/>
                <xsl:apply-templates select="xforms:help"/>
                <xsl:apply-templates select="xforms:alert"/>
            </xsl:when>
            <xsl:when test="local-name()='switch'">
                <xsl:apply-templates select="."/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>
