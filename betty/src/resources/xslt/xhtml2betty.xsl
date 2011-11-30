<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:xf="http://www.w3.org/2002/xforms"
                exclude-result-prefixes="xf bf xsl"
                xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="no"/>

    <xsl:param name="filedir"/>

    <xsl:strip-space elements="xf:model"/>
    <!-- ********************************* TEMPLATES ********************************   -->
    <xsl:template match="/html">
        <xsl:element name="html">
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:element>
    </xsl:template>


    <xsl:template match="head">
        <xsl:variable name="head" select="."/>
        <xsl:copy>
            <!--<xsl:copy-of select="title"/>-->
            <title></title>
            <xsl:text>
</xsl:text>
            <link rel="stylesheet" type="text/css" href="resources/scripts/dojo/dojo.css"/><xsl:text>
</xsl:text>
            <link rel="stylesheet" type="text/css" href="resources/scripts/dijit/themes/tundra/tundra.css"/><xsl:text>
</xsl:text>
            <link rel="stylesheet" type="text/css" href="resources/styles/xforms.css"/><xsl:text>
</xsl:text>
            <link rel="stylesheet" type="text/css" href="resources/styles/betterform.css"/><xsl:text>
</xsl:text>

            <style type="text/css">
                .hide {
                    display: none;
                }

                #bettyProcessor {
                    position: fixed;
                    bottom: 6px;
                    right: 10px;
                }

                #splash {
                    width: 300px;
                    position: absolute;
                    bottom: 0;
                    right: 0;
                    z-index: 999;
                    opacity: 0.8;
                    height: 30px;
                }

                .splash-screen {
                    -moz-border-radius: 10px;
                    border: thin solid steelBlue;
                    height: 12px;
                    padding: 8px;
                    background: #b0c4de;
                }

                .splash-message {
                    float: left;
                    margin: 18px;
                }

                /*
                                .splash-image {
                                    background-image: url(../resources/images/betterform_icon49x49.png);
                                    background-repeat: no-repeat;
                                    background-position: left center;
                                    width: 49px;
                                    height: 49px;
                                    float: left;
                                }
                */
            </style>

            <xsl:call-template name="getLinkAndStyle"/>

            <script type="text/javascript">
                var djConfig = {
                    debugAtAllCosts:false,
                    locale:'en',
                    isDebug:true,
                    parseOnLoad:false
                };
            </script><xsl:text>
</xsl:text>

            <script type="text/javascript" src="resources/scripts/dojo/dojo.js"></script><xsl:text>
</xsl:text>
            <script type="text/javascript">
                dojo.require("dojo.parser");
                dojo.require("betterform.Betty");
                dojo.require("betterform.ui.Control");
                dojo.require("betterform.ui.ControlValue");
                dojo.require("betterform.ui.util");

                //common
                dojo.require("betterform.ui.common.Alert");
                dojo.require("betterform.ui.common.InlineAlert");
                dojo.require("betterform.ui.common.InlineRoundBordersAlert");
                dojo.require("betterform.ui.common.ToolTipAlert");

                //container
                dojo.require("betterform.ui.container.Group");
                dojo.require("betterform.ui.container.Repeat");
                dojo.require("betterform.ui.container.RepeatItem");
                dojo.require("betterform.ui.container.Switch");
                dojo.require("betterform.ui.container.TabSwitch");

                //input
                dojo.require("betterform.ui.input.Boolean");
                dojo.require("betterform.ui.input.Date");
                dojo.require("betterform.ui.input.DateTime");
                dojo.require("betterform.ui.input.DropDownDate");
                dojo.require("betterform.ui.input.TextField");
                dojo.require("betterform.ui.input.Time");

                //output
                dojo.require("betterform.ui.output.Html");
                dojo.require("betterform.ui.output.Image");
                dojo.require("betterform.ui.output.Link");
                dojo.require("betterform.ui.output.Plain");

                //range
                dojo.require("betterform.ui.range.Slider");

                //secret
                dojo.require("betterform.ui.secret.Secret");

                //select
                dojo.require("betterform.ui.select.CheckBox");

                //select1
                dojo.require("betterform.ui.select1.ComboBox");
                dojo.require("betterform.ui.select1.ComboBoxOpen");
                dojo.require("betterform.ui.select1.Plain");
                dojo.require("betterform.ui.select1.RadioButton");

                //textarea
                dojo.require("betterform.ui.textarea.DojoEditor");
                dojo.require("betterform.ui.textarea.HtmlEditor");
                dojo.require("betterform.ui.textarea.SimpleTextarea");

                //trigger
                dojo.require("betterform.ui.trigger.Button");
                dojo.require("betterform.ui.trigger.ImageButton");
                dojo.require("betterform.ui.trigger.LinkButton");

                //upload
                dojo.require("betterform.ui.upload.Upload");
                dojo.require("betterform.ui.upload.UploadPlain");

                var insertPoint;
                dojo.addOnLoad(function() {
                    console.log("document ready");
                });

                setSplash = function setSplash(html) {
                    var splash = document.getElementById("splash");
                    splash.innerHTML = html;
                    return true;
                }
                setView = function setView(html) {
                    console.log("setView called");
                    var insertPoint = dojo.byId("xformsui");
                    console.debug("insertPoint", insertPoint);
                    insertPoint.innerHTML = html;
                    dojo.style("xformsui", "opacity", "0");
                    var fadeArgsIn = {
                        node: "xformsui"
                    };
                    dojo.fadeIn(fadeArgsIn).play();


                    //                    dojo.addClass(dojo.byId("splash"), "hide");
                    dojo.removeClass(insertPoint, "hide")
                    //                    dojo.parser.parse(insertPoint);
                    dojo.parser.parse();
                    dojo.style("splash", "opacity", ".8");
                    var fadeArgs = {
                        node: "splash",
                        duration:3000
                    };
                    dojo.fadeOut(fadeArgs).play();

                }
                function debug(message) {
                    console.log(message);
                }
            </script><xsl:text>
</xsl:text>

            <xsl:if test=".//xf:model">
                <script type="text/xml">
                    <xsl:for-each select="$head//xf:model">
                        <xsl:copy-of select="."/>
                    </xsl:for-each>
                </script>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template name="getLinkAndStyle"><xsl:text>
</xsl:text>
        <xsl:for-each select="link|style">
            <xsl:element name="{local-name()}">
                <xsl:copy-of select="@*"/>
                <xsl:apply-templates/>
            </xsl:element>
        </xsl:for-each><xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template match="body">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="class">tundra</xsl:attribute>
            <div id="splash"></div>
            <div id="xformsui" class="hide">
                <xsl:apply-templates/>
            </div>

            <applet id="bettyProcessor"
                    name="bettyProcessor"
                    code="de.betterform.agent.betty.Betty"
                    codebase="bin"
                    documentbase="."
                    archive="betty.jar,commons-codec-1.3.jar,commons-fileupload-1.2.1.jar,httpcore-4.1.jar,httpclient-4.1.1.jar,httpmime-4.1.1.jar,commons-io-1.4.jar,commons-lang-2.4.jar,commons-logging-1.1.1.jar,activation-1.1.1.jar,mail-1.4.2.jar,log4j-1.2.15.jar,saxon-9.2.1.5.jar,xercesImpl-2.9.1.jar,xml-apis-1.3.04.jar,xmlrpc-common-3.1.2.jar,xmlrpc-client-3.1.2.jar,xmlrpc-server-3.1.2.jar,ehcache-1.6.2.jar"
                    width="17"
                    height="17"
                    mayscript="true">
            </applet>

        </xsl:copy>
    </xsl:template>

    <xsl:template match="*|@*|text()|comment()" name="handle-foreign-elements">
        <xsl:choose>
            <xsl:when test="namespace-uri(.)='http://www.w3.org/1999/xhtml'">
                <xsl:element name="{local-name(.)}" namespace="">
                    <xsl:apply-templates select="*|@*|text()|comment()"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="*|@*|text()|comment()"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>
