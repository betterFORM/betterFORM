<?xml version="1.0" encoding="UTF-8"?>
<!--
    Copyright (c) 2010. betterForm Project - http://www.betterform.de
    Licensed under the terms of BSD License */
-->

<xsl:stylesheet version="2.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    exclude-result-prefixes="xforms bf xhtml"
    xpath-default-namespace="http://www.w3.org/1999/xhtml">


    <xsl:import href="dojo-dev.xsl"/>

    <!-- ********************************* PARAMETERS ********************************   -->
    <xsl:param name="action-url" select="'javascript:submit()'"/>


    <!-- ### signals the phase of processing (init|submit) ### -->
    <xsl:param name="phase" select="'false'"/>

    <xsl:param name="form-id" select="'betterform'"/>
    <xsl:param name="form-name" select="'betterForm XForms Processor'"/>
    <xsl:param name="debug-enabled" select="'true'"/>

    <!-- ### contains the full user-agent string as received from the servlet ### -->
    <xsl:param name="user-agent" select="'convex'"/>
    <xsl:param name="base-url" select="'betterform-@betterform-version@'"/>

    <!-- ********************************* GLOBAL VARIABLES ********************************   -->
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
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <!--<xsl:template match="head"/>-->
    <xsl:template match="head" priority="10">
        <xsl:message>match head</xsl:message>

        <head>
            <!-- copy all meta tags except 'contenttype' -->
            <xsl:call-template name="getMeta" />

            <title>
                jheejlkajlfkdsjalkfjdlksjkl
            </title>

            <!-- copy base if present -->
            <xsl:if test="base">
                <base>
                    <xsl:attribute name="href">
                        <xsl:value-of select="base/@href"/>
                    </xsl:attribute>
                </base>
            </xsl:if>


	        <style type="text/css">
                <xsl:if test="//xforms:repeat">
                @import "<xsl:value-of select="$contextroot"/>/resources/scripts/dojox/grid/_grid/tundraGrid.css";
                </xsl:if>
	            @import "<xsl:value-of select="$contextroot"/>/resources/scripts/dijit/themes/tundra/tundra.css";
	            @import "<xsl:value-of select="$contextroot"/>/resources/scripts/dojo/resources/dojo.css";
                #fluxProcessor {
                    width:100%;
                    height:100%;
                    margin:0;
                    padding:0;
                    background:#fff
                    url('<xsl:value-of select="$contextroot"/>/resources/images/betterform-logo_klein2.gif')
                    no-repeat center center;
                    position:absolute;
                    z-index:999;
                }
                .buttons{background:silver;}
                #switchmsg{font-size:14pt;padding:10px;background:#A42322;color:white;text-align:center;}
                #grid {
                    border:1px solid black;
                    width:35em;
                    height:30em;
                }

	        </style>
	        <xsl:text>
</xsl:text>

            <!-- copy user-defined stylesheets and inline styles -->
            <!--<xsl:call-template name="getLinkAndStyle"/>-->


            <!-- include needed javascript files -->
            <!-- dojo init -->
            <script type="text/javascript" src="{concat($contextroot,$scriptPath,'dojo/dojo.js')}">
                var djConfig = {
                    debugAtAllCosts:  <xsl:value-of select="$debug-enabled"/>,
                    isDebug: <xsl:value-of select="$debug-enabled"/>,
                    popup:true
                };
            </script>

            <!--
                <script type="text/javascript">
                    var djConfig = {
                    debugAtAllCost:  <xsl:value-of select="$debug-enabled"/>,
                    isDebug: <xsl:value-of select="$debug-enabled"/> };
                </script>
-->
                <xsl:text>
</xsl:text>

                <!-- ############################# Debug section ################################ -->

                <script type="text/javascript">
                    <!--
                    dojo.setModulePrefix("betterform","betterform");
                    dojo.registerModulePath("betterform","../betterform");
                    -->

                    function switchToEdit(target){
                        //console.debug("target,"target);
                        new betterform.ui.input.TextField({id:target.id,value:dojo.byId(target.id).innerHTML},target.id)

                    }
                    var hideLoader = function(){
                        dojo.fadeOut({
                            node:"fluxProcessor",
                            duration:400,
                            onEnd: function(){
                                dojo.style("fluxProcessor", "display", "none");
                                dojo.style(dojo.body(),"overflow","auto");
                            }
                        }).play();
                    }
                    dojo.addOnLoad(function(){
                    dojo.require("dojo.parser");
                    dojo.require("dojo.fx");
                    dojo.require("dojo.NodeList-fx");
                    dojo.require("dojo.dnd.Selector");
                    dojo.require("dojo.dnd.Source");
                    dojo.require("dijit._Widget");
                    dojo.require("dijit.Dialog");
                    dojo.require("dijit.TitlePane");
                    dojo.require("dijit.Tooltip");
                    dojo.require("dijit.form.CheckBox");
                    dojo.require("dijit.form.Button");
                    dojo.require("dijit.layout.ContentPane");
                    dojo.require("dijit.layout.TabContainer");
                    dojo.require("dijit.layout.BorderContainer");
                    dojo.require("dijit.layout.AccordionContainer");
                    dojo.require("dojox.widget.FisheyeLite");
                    dojo.require("betterform.FluxProcessor");
                    dojo.require("betterform.ui.Control");
                    dojo.require("betterform.ui.FisheyeLite");
                    dojo.require("betterform.ui.InlineEditBox");
                    dojo.require("betterform.ui.util");
                    dojo.require("betterform.ui.container.AccordionSwitch");
                    dojo.require("betterform.ui.container.AccordionSwitchPane");
                    dojo.require("betterform.ui.container.Container");
                    dojo.require("betterform.ui.container.Group");
                    dojo.require("betterform.ui.container.Repeat");
                    dojo.require("betterform.ui.container.RepeatItem");
                    dojo.require("betterform.ui.container.Switch");
                    dojo.require("betterform.ui.container.TabSwitch");
                    dojo.require("betterform.ui.input.Boolean");
                    dojo.require("betterform.ui.input.Date");
                    dojo.require("betterform.ui.input.TextField");
                    dojo.require("betterform.ui.output.Image");
                    dojo.require("betterform.ui.output.Html");
                    dojo.require("betterform.ui.output.Link");
                    dojo.require("betterform.ui.output.Plain");
                    dojo.require("betterform.ui.range.Slider");
                    dojo.require("betterform.ui.range.Rating");
                    dojo.require("betterform.ui.secret.Secret");
                    dojo.require("betterform.ui.select1.ComboBox");
                    dojo.require("betterform.ui.select1.ComboBoxOpen");
                    dojo.require("betterform.ui.select1.Plain");
                    dojo.require("betterform.ui.select1.RadioButton");
                    dojo.require("betterform.ui.select1.RadioGroup");
                    dojo.require("betterform.ui.select1.RadioItemset");
                    dojo.require("betterform.ui.select.CheckBox");
                    dojo.require("betterform.ui.select.CheckBoxGroup");
                    dojo.require("betterform.ui.select.CheckBoxItemset");
                    dojo.require("betterform.ui.select.MultiSelect");
                    dojo.require("betterform.ui.select.OptGroup");
                    dojo.require("betterform.ui.textarea.DojoEditor");
                    dojo.require("betterform.ui.textarea.HtmlEditor");
                    dojo.require("betterform.ui.textarea.SimpleTextarea");
                    dojo.require("betterform.ui.trigger.Button");
                    dojo.require("betterform.ui.trigger.LinkButton");
                    dojo.require("betterform.ui.upload.Upload");
                    dojo.require("betterform.ui.upload.UploadPlain");
                    dojo.require("betterform.ui.util");


                        dojo.addOnLoad(function(){
                            dojo.parser.parse();
                            hideLoader();
                            //Flux.init(dojo.attr(dojo.byId("fluxProcessor"),"sessionKey"),dijit.byId("fluxProcessor").applyChanges);
                        });
                    });
            </script><xsl:text>
</xsl:text>

            <script type="text/javascript" src="resources/scripts/betterform/convexProcessor.js">&#160;</script><xsl:text>
</xsl:text>
            

<!-- copy inline javascript -->
                <xsl:for-each select="script">
                    <script>
                        <xsl:choose>
                            <xsl:when test="@src">
                                <xsl:attribute name="type">
                                    <xsl:value-of select="@type"/>
                                </xsl:attribute>
                                <xsl:attribute name="src">
                                    <xsl:value-of select="@src"/>
                                </xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:apply-templates/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </script>
                    <xsl:text>
</xsl:text>
                </xsl:for-each>
        </head>
    </xsl:template>

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

                <div dojotype="betterform.ConvexProcessor" jsId="fluxProcessor" id="fluxProcessor"/>
                <!-- provide a first submit which does not map to any xforms:trigger -->
                <input type="image" name="dummy" style="width:0pt;height:0pt;" value="dummy"/>
                <xsl:apply-templates select="div[@id='xformsui']"/>
            </xsl:element>
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
