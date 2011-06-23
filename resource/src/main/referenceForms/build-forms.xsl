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

    <xsl:param name="webContext" select="'/betterform'"/>

    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" media-type="text/xml"/>

    <!--<xsl:strip-space elements="*"/>-->

    <xsl:preserve-space elements="code"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="head">
        <xsl:copy>
            <xsl:apply-templates/>
            <meta http-equiv="Content-Type" content="text/xml; charset=UTF-8"/>
            <link rel="stylesheet" type="text/css"
                  href="{$webContext}/bfResources/scripts/dojox/highlight/resources/highlight.css"/>
            <!--
                        <link rel="stylesheet" type="text/css"
                              href="../../resources/scripts/dojox/highlight/resources/pygments/borland.css"/>
            -->
            <link rel="stylesheet" type="text/css" href="reference.css"/>
            <script type="text/javascript">
                dojo.subscribe("/xf/ready", function(){
                    fluxProcessor.skipshutdown=true;
                });
            </script>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="title">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="body">
        <xsl:copy>
            <xsl:attribute name="class" select="if(exists(@class)) then @class else 'soria InlineAlert'"/>
            <xsl:attribute name="style">margin:30px</xsl:attribute>

            <div id="xforms" class="InlineAlert">
                <!-- the xforms model here -->
                <xsl:apply-templates select="div[@class='sample']/div[@class='markup']/xf:model"/>
                <xsl:apply-templates select="div[@class='sample']/div[@class='markup']/code/xf:model"/>
                <!--
                                <div style="display:none">
                                    <xsl:apply-templates select="div[@class='sample']/div[@class='markup']/xf:model" mode="xforms"/>
                                </div>
                -->
                <xsl:apply-templates select="div[@class='CSS']"/>

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

                <!--
                here the xforms code gets integrated into the page within a hidden div. Sections within <code class="ui">
                are
                 -->
                <xsl:apply-templates select="div[@class='sample']"/>

                <!-- here the highlighted code for the example will be generated -->
                <!-- here the highlighted code for the example will be generated -->
                <!-- here the highlighted code for the example will be generated -->
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

                <xsl:apply-templates select="div[@class='Notes']"/>
                <xsl:apply-templates select="div[@class='Limitations']"/>
                <!--
                                <xsl:variable name="sampleDiv" select="div[@class='sample']"/>
                                <xsl:for-each select="$sampleDiv/following-sibling::*">
                                    <xsl:apply-templates select="."/>
                                </xsl:for-each>
                -->
                <xsl:for-each select="div[@class='Other']">
                    <h2><xsl:value-of select="@title"/></h2>
                    <xsl:copy-of select="."/>
                </xsl:for-each>
                <script type="text/javascript">
                    function showClass(cssClass){
                        dojo.query('.sample .'+ cssClass).forEach(function(item){
                            dojo.addClass(item,'showClass');
                        });
                    }
                    function hideClass(cssClass){
                        dojo.query('.sample .'+ cssClass).forEach(function(item){
                            dojo.removeClass(item,'showClass');
                        });
                    }                    
                </script>
            </div>
        </xsl:copy>
    </xsl:template>


    <xsl:template name="referenceTable">
        <xsl:param name="specRef"/>
        <xsl:param name="quickRef"/>

        <xsl:if test="exists(div[@class='references'])">
            <table id="references">
                <tr>
                    <td rowspan="3">
                        <a href="http://www.w3c.org" class="link" id="linkLogo" style="margin-right:25px;" target="_blank">
                            <img id="logo" class="image" src="images/w3c_home_nb.png" alt="W3C"/>
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
        </xsl:if>
    </xsl:template>

    <xsl:template match="div[@class='sample']">
        <h2>Example</h2>
        <xsl:apply-templates select="div[@class='markup']"/>
        <xsl:apply-templates select="div[@class='markup']/following-sibling::*"/>
    </xsl:template>


    <xsl:template match="div[@class='sample']/div[@class='markup']/xf:model">
        <div style="display:none">
            <xsl:apply-templates select="." mode="xforms"/>
        </div>
    </xsl:template>

    <xsl:template match="div[@class='sample']/div[@class='markup']/code/xf:model">
        <div style="display:none">
            <xsl:apply-templates select="." mode="xforms"/>
        </div>
    </xsl:template>

    <xsl:template match="div[@class='markup']">
        <div class="Section sample">
            <xsl:copy-of select=".//code[@class='ui']/*"/>
            <!--<xsl:apply-templates select=".//code[@class='ui']/*"/>-->
        </div>
    </xsl:template>

    <!--
        <xsl:template match="h2[text()='CSS']">
            <h2>CSS</h2>
            <div style="font-size:0.8em;font-style:italic;">Hover the classes on the left to see the matching element(s) in
                the example
            </div>
        </xsl:template>
    -->

    <xsl:template match="div[@class='CSS']">
        <!--<h2><xsl:value-of select="@class"/></h2>-->
        <xsl:if test="table">
            <div class="CSS">
                <xsl:apply-templates select="table"/>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="div[@class='CSS']/table" priority="20">
        <table>
            <!--<xsl:apply-templates/>-->
            <caption>CSS</caption>
            <tr>
                <th style="font-style:italic;color:#C46B47;">
                    Hover the classes to see matching element(s) in the example
                </th>
            </tr>
            <xsl:for-each select="tr">
                <tr>
                    <xsl:variable name="idref" select="generate-id()"/>
                    <td id="{$idref}" onmouseover="showClass('{td[1]}');"
                        onmouseout="hideClass('{td[1]}');">
                        <xsl:value-of select="td[1]"/>
                        <div dojoType="dijit.Tooltip" connectId="{$idref}">
                            <xsl:value-of select="td[2]"/>
                        </div>
                    </td>
                </tr>
            </xsl:for-each>
            <tr>
                <td id="xfcontrol" onmouseover="showClass('xfControl');"
                    onmouseout="hideClass('xfControl');">xfControl
                    <div dojoType="dijit.Tooltip" connectId="xfcontrol">
                        matches an Element that represents a XForms control
                    </div>
                </td>
            </tr>
            <tr>
                <td id="xflabel" onmouseover="showClass('xfLabel');"
                    onmouseout="hideClass('xfLabel');">xfLabel
                    <div dojoType="dijit.Tooltip" connectId="xflabel">
                        matches the label part of an XForms control
                    </div>
                </td>
            </tr>
            <tr>
                <td id="xfvalue" onmouseover="showClass('xfValue');"
                    onmouseout="hideClass('xfValue');">xfValue
                    <div dojoType="dijit.Tooltip" connectId="xfvalue">
                        matches the widget part of an XForms control
                    </div>
                </td>
            </tr>
            <xsl:if test="exists(//div[@class='MIPS'])">
                <tr>
                    <td id="xfreadonly" onmouseover="showClass('xfReadOnly');"
                        onmouseout="hideClass('xfReadOnly');">
                        xfReadOnly
                        <div dojoType="dijit.Tooltip" connectId="xfreadonly">
                            matches a xforms control that is currently readonly
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfreadwrite" onmouseover="showClass('xfReadWrite');"
                        onmouseout="hideClass('xfReadWrite');">
                        xfReadWrite
                        <div dojoType="dijit.Tooltip" connectId="xfreadwrite">
                            matches a xforms control that is currently writable
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfrequired" onmouseover="showClass('xfRequired');"
                        onmouseout="hideClass('xfRequired');">
                        xfRequired
                        <div dojoType="dijit.Tooltip" connectId="xfrequired">
                            matches a xforms control that is currently required
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfoptional" onmouseover="showClass('xfOptional');"
                        onmouseout="hideClass('xfOptional');">
                        xfOptional
                        <div dojoType="dijit.Tooltip" connectId="xfoptional">
                            matches a xforms control that is currently optional
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfenabled" onmouseover="showClass('xfEnabled');"
                        onmouseout="hideClass('xfEnabled');">
                        xfEnabled
                        <div dojoType="dijit.Tooltip" connectId="xfenabled">
                            matches a xforms control that is currently relevant (enabled)
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfdisabled" onmouseover="showClass('xfDisabled');"
                        onmouseout="hideClass('xfDisabled');">
                        xfDisabled
                        <div dojoType="dijit.Tooltip" connectId="xfdisabled">
                            matches a xforms control that is currently disabled (can't be visualized here as the control gets hidden)
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfvalid" onmouseover="showClass('xfValid');"
                        onmouseout="hideClass('xfValid');">
                        xfValid
                        <div dojoType="dijit.Tooltip" connectId="xfvalid">
                            matches a xforms control that is currently valid
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfinvalid" onmouseover="showClass('xfInvalid');"
                        onmouseout="hideClass('xfInvalid');">
                        xfInvalid
                        <div dojoType="dijit.Tooltip" connectId="xfinvalid">
                            matches a xforms control that is currently invalid
                        </div>
                    </td>
                </tr>
            </xsl:if>
        </table>

    </xsl:template>

    <xsl:template match="div[@class='Notes']">
        <h2>Notes</h2>
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="div[@class='Limitations']">
        <h2>Limitations</h2>
        <xsl:copy-of select="."/>
    </xsl:template>


    <xsl:template match="table[@class='CSS']/tr/td[1]" priority="20">
        <td onmouseover="showClass('{.}');"
            onmouseout="hideClass('{.}');">
            <xsl:value-of select="."/>
        </td>
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
