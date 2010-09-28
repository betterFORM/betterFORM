<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->
<!-- TODO:
        - create empty instance see Input

-->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xpath-default-namespace="http://www.w3.org/1999/xhtml"
        >
    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" media-type="text/xml"/>

    <!--<xsl:strip-space elements="*"/>-->

    <xsl:preserve-space elements="code"/>

    <xsl:template match="/">
        <html>
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <xsl:template match="head">
        <xsl:copy>
            <xsl:apply-templates/>
            <meta http-equiv="Content-Type" content="text/xml; charset=UTF-8"/>
            <link rel="stylesheet" type="text/css"
                  href="../../resources/scripts/dojox/highlight/resources/highlight.css"/>
            <!--
                        <link rel="stylesheet" type="text/css"
                              href="../../resources/scripts/dojox/highlight/resources/pygments/borland.css"/>
            -->
            <link rel="stylesheet" type="text/css" href="../../resources/styles/reference.css"/>
            <script type="text/javascript">
                dojo.require("dojox.highlight");
                dojo.require("dojox.highlight.languages.xml");
                //                dojo.require("dojox.highlight.languages.pygments.xml");
                dojo.require("dijit.form.Button");
                dojo.require('dijit.layout.ContentPane');
            </script>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="title">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="body">
        <xsl:copy>
            <xsl:attribute name="class">soria InlineAlert</xsl:attribute>
            <xsl:attribute name="style">margin:30px</xsl:attribute>

            <div id="xforms" class="InlineAlert">
                <!-- the xforms model here -->
                <div style="display:none">
                    <xsl:apply-templates select="div[@class='sample']/div[@class='markup']/xf:model" mode="xforms"/>
                </div>

                <div class="pageintro">
                    <h1>
                        <xsl:value-of select="//title"/>
                    </h1>

                    <xsl:copy-of select="div[@class='description']"/>
                    <xsl:call-template name="referenceTable">
                        <xsl:with-param name="specRef" select="div[@class='references']/a[1]/@href"/>
                        <xsl:with-param name="quickRef" select="div[@class='references']/a[2]/@href"/>
                    </xsl:call-template>
                </div>

                <h2>XForms Markup</h2>
                <div class="Section markup">
                    <pre>
                        <code class="xml" dojoType="dojox.highlight.Code">
                            <!--<xsl:text>&#10;</xsl:text>-->
                            <xsl:for-each select=".//code/*">
                                <xsl:apply-templates select="." mode="escape"/>
                                <xsl:text>&#10;</xsl:text>
                                <xsl:if test="position()!=last()">
                                    <xsl:text>&#10;</xsl:text>
                                </xsl:if>
                            </xsl:for-each>
                        </code>
                    </pre>
                </div>

                <h2>Example</h2>
                <div class="Section sample">
                    <xsl:copy-of select=".//code[@class='ui']/*"/>
                </div>

                <xsl:variable name="sampleDiv" select="div[@class='sample']"/>
                <xsl:for-each select="$sampleDiv/following-sibling::*">
                    <xsl:apply-templates select="."/>
                </xsl:for-each>
                <!--
                                <xsl:for-each select="div[contains(./@class,'Section')]">
                                    <xsl:copy-of select="."/>
                                </xsl:for-each>
                -->
                <script type="text/javascript">
                    dojo.query("code").forEach(dojox.highlight.init);
                </script>
            </div>
        </xsl:copy>
    </xsl:template>


    <xsl:template name="referenceTable">
        <xsl:param name="specRef"/>
        <xsl:param name="quickRef"/>

        <table id="references">
            <tr>
                <td rowspan="3">
                    <a href="http://www.w3c.org" class="link" id="linkLogo" style="margin-right:25px;" target="_blank">
                        <img id="logo" class="image" src="../../resources/images/w3c_home_nb.png" alt="W3C"/>
                    </a>
                </td>
                <td style="color:#005A9C; font-size:16px;">XForms 1.1 Links</td>
            </tr>
            <tr>
                <td>
                    <a style="color:#005A9C;"
                       href="http://www.w3.org/MarkUp/Forms/specs/XForms1.1/index-all.html{$specRef}" target="_blank">
                        Recommendation
                    </a>
                </td>
            </tr>
            <tr>
                <td>
                    <a style="color:#005A9C;"
                       href="http://www.w3.org/MarkUp/Forms/2010/xforms11-qr.html{$quickRef}" target="_blank">
                        Quick Reference
                    </a>
                </td>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="*|@*|text()|comment()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*|@*|text()|comment()" mode="xforms">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="xforms"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="code" mode="xforms" priority="10">
        <xsl:apply-templates mode="xforms"/>
    </xsl:template>

    <xsl:template match="*" mode="escape" priority="10">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:apply-templates mode="escape" select="@*"/>
        <xsl:text>&gt;</xsl:text>
        <xsl:apply-templates mode="escape"/>
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>&gt;</xsl:text>
    </xsl:template>

    <xsl:template match="@*" mode="escape" priority="10">
        <xsl:text> </xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>="</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>"</xsl:text>
    </xsl:template>

</xsl:stylesheet>
