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
    exclude-result-prefixes="xf bf"
    xpath-default-namespace= "http://www.w3.org/1999/xhtml">

    <xsl:import href="dojo.xsl"/>

    <xsl:variable name="default-css" select="concat($contextroot,$CSSPath,'xforms-editor.css')"/>

    <xsl:template match="body">
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

        <body class="{$theme} bf">
            <xsl:copy-of select="@*[name() != 'class']"/>
                <div style="display:none">

                </div>

                <div id="bfLoading" class="disabled">
                    <img src="{concat($contextroot,$resourcesPath,'images/indicator.gif')}" class="xfDisabled" id="indicator"
                         alt="loading"/>
                </div>
                <!-- Toaster widget for ephemeral messages -->

                <div dojoType="dojox.widget.Toaster"
                     id="betterformMessageToaster"
                     positionDirection="bl-up"
                     duration="8000"
                     separator="&lt;div style='height:1px;border-top:thin dotted;width:100%;'&gt;&lt;/div&gt;"
                     messageTopic="testMessageTopic">
                </div>
                <noscript>
                    <div id="noScript">
                        Sorry, you don't have Javascript enabled in your browser. Click here for a non-scripted version
                        of this form.
                    </div>
                </noscript>
                <div id="formWrapper">
                    <div dojotype="betterform.FluxProcessor" jsId="fluxProcessor" id="fluxProcessor"
                         sessionkey="{$sessionKey}" contextroot="{$contextroot}">

                        <!--
                                                <div id="layoutContainer" dojoType="dijit.layout.BorderContainer" gutters="true" design="headline">
                                                    <div id="UIPane" region="center">
                        -->
                        <xsl:for-each select="//xf:model">
                            <xsl:apply-templates select="."/>
                        </xsl:for-each>
                        <xsl:variable name="outermostNodeset"
                                      select=".//xf:*[not(xf:model)][not(ancestor::xf:*)]"/>

                        <!-- detect how many outermost XForms elements we have in the body -->
                        <xsl:choose>
                            <xsl:when test="count($outermostNodeset) = 1">
                                <!-- match any body content and defer creation of form tag for XForms processing.
                                This option allows to mix HTML forms with XForms markup. -->
                                <!-- todo: issue to revisit: this obviously does not work in case there's only one xforms control in the document. In that case the necessary form tag is not written. -->
                                <!-- hack solution: add an output that you style invisible to the form to make it work again. -->
                                <xsl:apply-templates mode="inline"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <!-- in case there are multiple outermost xforms elements we are forced to create
                          the form tag for the XForms processing.-->
                                <xsl:call-template name="createForm"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </div>
                    <!--<div id="modelPane" region="top" dojoType="dojox.layout.ExpandoPane" maxHeight="600" style="width:100%;">-->
                            <!--</div>-->
                        <!--</div>-->

                    <!--</div>-->
                </div>

                <div id="bfMessageDialog" dojotype="dijit.Dialog" style="text-align:center;display:none;">
                    <div id="messageContent"></div>
                    <button dojotype="dijit.form.Button" type="button" style="margin:10px;">OK
                        <script type="dojo/method" event="onClick" args="evt">
                            dijit.byId("bfMessageDialog").hide();
                        </script>
                    </button>
                </div>

                <xsl:if test="$debug-enabled='true'">
                    <!-- z-index of 1000 so it is also in front of shim for modal dialogs -->
                    <div id="debug-pane" context="{concat($contextroot,'/inspector/',$sessionKey,'/')}">
                        <div style="float:right;margin-right:20px;text-align:right;" id="copyright">
                            <a href="http://www.betterform.de">
                                <img style="vertical-align:text-bottom; margin-right:5px;"
                                     src="{concat($contextroot,'/bfResources/images/betterform_icon16x16.png')}" alt="betterFORM project"/>
                            </a>
                            <span>&#xA9; 2011 betterFORM</span>
                        </div>
                        <span id="debug-pane-links" style="float:left;width:80%;">
                            <a href="{concat($contextroot,'/inspector/',$sessionKey,'/','hostDOM')}" target="_blank">Host Document</a>
                        </span>
                    </div>
                </xsl:if>
        </body>
    </xsl:template>

    <xsl:template match="xf:model">
        <xsl:variable name="props">
            <xsl:call-template name="getProps" />
        </xsl:variable>
        <div class="xfModel" id="{@id}" jsId="{@id}" dojoType="betterform.XFormsModelElement" data-bf-props="{$props}">
            <!--<xsl:apply-templates select="."/>-->
        </div>
    </xsl:template>

    <xsl:template name="include-xforms-css">
        <!-- include betterForm default stylesheet -->
        <link rel="stylesheet" type="text/css" href="{$default-css}"/>
        <link rel="stylesheet" type="text/css" href="{$betterform-css}"/>
        <link rel="stylesheet" type="text/css" href="concat($contextroot,$CSSPath,'bfEditor.css')"/>
    </xsl:template>


    <xsl:template name="addLocalScript">
        <script type="text/javascript" defer="defer">
            <xsl:if test="$debug-enabled">function getXFormsDOM(){
                Flux.getXFormsDOM(document.getElementById("bfSessionKey").value,
                function(data){
                console.dirxml(data);
                }
                );
                }

                function getInstanceDocument(instanceId){
                var model = dojo.query(".xfModel", dojo.doc)[0];
                dijit.byId(dojo.attr(model, "id")).getInstanceDocument(instanceId,
                function(data){
                console.dirxml(data);
                });
                }
            </xsl:if>

            dojo.addOnLoad(function(){
                dojo.addOnLoad(function(){
                    dojo.parser.parse();
                    Flux._path = dojo.attr(dojo.byId("fluxProcessor"), "contextroot") + "/Flux";
                    Flux.init( dojo.attr(dojo.byId("fluxProcessor"),"sessionkey"),
                    dojo.hitch(fluxProcessor,fluxProcessor.applyChanges));

                    dojo.connect(dojo.byId("C1"),'onmouseenter',function(){
                        dojo.attr(dojo.byId("C1"),"style","border:steelblue solid 3px;display:block;");
                    });
                    dojo.connect(dojo.byId("C1"),'onmouseleave',function(){
                        dojo.attr(dojo.byId("C1"),"style","border:none;");
                    });
                    dojo.connect(dojo.byId("C1"),'onclick',function(){
                        alert("yeah!!!!!");
                    });
                    dojo.connect(dojo.byId("C1"),'onkeypress',function(){
                        alert("yeah!!!!!");
                    });
                });
            });
        </script><xsl:text>
</xsl:text>
    </xsl:template>

    <xsl:template name="getProps">
        <xsl:for-each select="@*">
            <xsl:value-of select="name()"/>:<xsl:value-of select="."/>
            <xsl:if test="position()!=last()">,</xsl:if>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
