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
                exclude-result-prefixes="xf bf"
                xpath-default-namespace= "http://www.w3.org/1999/xhtml">

    <xsl:import href="dojo.xsl"/>

    <xsl:template match="head">

        <xsl:comment> *** powered by betterFORM, &amp;copy; 2011 *** </xsl:comment>

        <head>
            <title><xsl:value-of select="$form-name"/></title><xsl:text>
</xsl:text>
            <!-- copy all meta tags except 'contenttype' -->
            <xsl:call-template name="getMeta"/>

            <meta name = "viewport" content = "width = device-width">  </meta>
            <meta name="apple-mobile-web-app-capable" content="yes"> </meta>
            <meta name="apple-mobile-web-app-status-bar-style" content="black"> </meta>
            <meta name="format-detection" content="telephone=yes"> </meta>

            <link rel="stylesheet" type="text/css" href="{$default-css}"/>

            <link rel="stylesheet" media="all and (orientation:portrait)">
                <xsl:attribute name="href"><xsl:value-of select="concat($contextroot,$CSSPath, 'mobile_portrait.css')"/></xsl:attribute>
            </link>

            <link rel="stylesheet" media="all and (orientation:landscape)">
                <xsl:attribute name="href"><xsl:value-of select="concat($contextroot,$CSSPath, 'mobile_landscape.css')"/></xsl:attribute>
            </link>


            <xsl:choose>
                <xsl:when test="$useCDN='true'">
                    <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dojo/resources/dojo.css"/>
                    <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dijit/themes/tundra/tundra.css"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="addDojoCSS"/>
                </xsl:otherwise>
            </xsl:choose>

            <!-- include betterForm default stylesheet -->
            <link rel="stylesheet" type="text/css" href="{$default-css}"/>
            <link rel="stylesheet" type="text/css" href="{$betterform-css}"/>

            <!-- copy user-defined stylesheets and inline styles -->
            <xsl:call-template name="getLinkAndStyle"/>

            <!-- include needed javascript files -->
            <xsl:call-template name="addDojoConfig"/>
            <xsl:call-template name="addDojoImport"/>
            <xsl:call-template name="addDWRImports"/>

            <!-- Optional Simile Timeline Javascript Imports -->
            <xsl:if test="exists(//xf:input[@appearance='caSimileTimeline'])">
                <xsl:call-template name="addSimileTimelineImports" />
            </xsl:if>

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

                function loadBetterFORMJs(pathToRelease, developmentJsClass){
                    if (isBetterFORMRelease) {
                        var scriptElement = document.createElement('script');
                        scriptElement.type = 'text/javascript';
                        scriptElement.src = pathToRelease;
                        document.getElementsByTagName('head')[0].appendChild(scriptElement);
                    } else {
                        dojo.require(developmentJsClass);
                    }
                }

                dojo.addOnLoad(function(){
                    dojo.addOnLoad(function(){
                        dojo.parser.parse();
                        Flux._path = dojo.attr(dojo.byId("fluxProcessor"), "contextroot") + "/Flux";
                        Flux.init( dojo.attr(dojo.byId("fluxProcessor"),"sessionkey"), dojo.hitch(fluxProcessor,fluxProcessor.applyChanges));
                    });
                });
            </script><xsl:text>
</xsl:text>
            <xsl:call-template name="copyInlineScript"/>

        </head>
    </xsl:template>

    <xsl:template name="iOS_Orientation">
        <script type="text/javascript">
            var updateLayout = function() {
                if (window.innerWidth != currentWidth) {
                    currentWidth = window.innerWidth;
                    var orient = (currentWidth == 320) ? "profile" : "landscape";
                    document.body.setAttribute("orient", orient);
                    window.scrollTo(0, 1);
                }
            };

            iPhone.DomLoad(updateLayout);
            setInterval(updateLayout, 400);
        </script>
    </xsl:template>

</xsl:stylesheet>
