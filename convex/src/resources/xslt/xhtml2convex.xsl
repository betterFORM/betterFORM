<?xml version="1.0" encoding="UTF-8"?>
<!--
    Copyright (c) 2010. betterForm Project - http://www.betterform.de
    Licensed under the terms of BSD License */
-->
<xsl:stylesheet version="2.0"
    xmlns="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bf="http://betterform.sourceforge.net/xforms"
    xmlns:xf="http://www.w3.org/2002/xforms"
    exclude-result-prefixes="xf bf xsl"
    xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" indent="no"/>

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
            <xsl:copy-of select="title"/>
            <xsl:text>
</xsl:text>
            <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.3/dojo/resources/dojo.css"/><xsl:text>
</xsl:text>
            <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.3/dijit/themes/tundra/tundra.css"/><xsl:text>
</xsl:text>
            <link rel="stylesheet" type="text/css" href="../resources/styles/xforms.css"/><xsl:text>
</xsl:text>
            <link rel="stylesheet" type="text/css" href="../resources/styles/betterform.css"/><xsl:text>
</xsl:text>

            <script type="text/javascript">
                        var djConfig = {
                            debugAtAllCosts:false,
                            locale:'en',
                            isDebug:true,
                            parseOnLoad:false
                        };
            </script><xsl:text>
</xsl:text>
            
            <script type="text/javascript" src="../resources/scripts/dojo/dojo.js"></script><xsl:text>
</xsl:text>
<!--
            <script type="text/javascript" src="../resources/scripts/betterform/ConvexProcessor.js"></script><xsl:text>
</xsl:text>
-->
<!--
            <script type="text/javascript" src="../resources/scripts/betterform/betterform.js"></script><xsl:text>
</xsl:text>
-->
<!--
            <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.5/dojo/dojo.xd.js"></script><xsl:text>
</xsl:text>

-->
            <script type="text/javascript">
                dojo.require("dojo.parser");
                dojo.require("betterform.ConvexProcessor");
                dojo.require("betterform.ui.Control");
                dojo.require("betterform.ui.container.Group");
                dojo.require("betterform.ui.util");

                var insertPoint;
                dojo.addOnLoad(function(){
                    console.log("document ready");
               });

                setView = function setView(html){
                    console.log("setView called");
                    insertPoint = dojo.byId("xformsui");
                    console.debug("insertPoint",insertPoint);
                    insertPoint.innerHTML=html;
//                    dojo.parser.parse(insertPoint);
                    dojo.parser.parse();
                }
                function debug(message){
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

    <xsl:template match="body">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="class">tundra</xsl:attribute>
            <div id="xformsui">
                <xsl:apply-templates />
            </div>
            <applet name="convex"
                    code="de.betterform.agent.convex.Convex"
                    codebase="../bin"
                    documentbase="."
                    archive="convex-1.0.jar,commons-codec-1.3.jar,commons-fileupload-1.2.1.jar,commons-httpclient-3.1.jar,commons-io-1.4.jar,commons-lang-2.4.jar,commons-logging-1.1.1.jar,activation-1.1.1.jar,mail-1.4.2.jar,log4j-1.2.15.jar,saxon-9.2.1.5.jar,xercesImpl-2.9.1.jar,xml-apis-1.3.04.jar,xmlrpc-common-3.1.2.jar,xmlrpc-client-3.1.2.jar,xmlrpc-server-3.1.2.jar,ehcache-1.6.2.jar"
                    width="100"
                    height="100"
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
