<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->
<!-- TODO:
        - create empty instance see Input

-->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xpath-default-namespace="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="#all">

    <xsl:param name="webContext" select="'../..'"/>

    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" media-type="text/xml" indent="yes"/>

    <!--<xsl:strip-space elements="*"/>-->

    <xsl:preserve-space elements="code"/>
    <xsl:variable name="root" select="."/>
    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="head">
        <head>
            <xsl:apply-templates mode="head"/>
            <meta http-equiv="Content-Type" content="text/xml; charset=UTF-8"/>
            <link rel="stylesheet" type="text/css" href="../../bfResources/scripts/dojox/highlight/resources/highlight.css"/>
            <link rel="stylesheet" type="text/css" href="reference.css"/>
        </head>
    </xsl:template>

    <xsl:template match="*|@*|text()" mode="head">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="head"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="*|@*|text()" mode="head">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="head"/>
        </xsl:copy>
    </xsl:template>
    <xsl:template match="comment()" mode="head"><xsl:text>
        </xsl:text>
        <xsl:copy/>
    </xsl:template>




    <xsl:template match="title">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="body">
        <body class="{if(exists(@class)) then @class else 'soria'}" style="margin:30px">
            <div id="xforms">
                <!-- the xforms model here -->
                <xsl:apply-templates select="div[@class='sample']//xf:model" mode="xforms"/>

                <xsl:apply-templates select="div[@class='CSS']" mode="css"/>

                <div class="pageintro">
                    <h1><xsl:value-of select="//title"/></h1>
                    <xsl:apply-templates select="*[@class='description']" mode="description"/>

                    <xsl:call-template name="referenceTable">
                        <xsl:with-param name="specRef" select="div[@class='references']/a[1]/@href"/>
                        <xsl:with-param name="quickRef" select="div[@class='references']/a[2]/@href"/>
                    </xsl:call-template>
                </div>

                <xsl:apply-templates select="div[@class='sample']" mode="sample"/>
                <xsl:apply-templates select="//*[@class='MIPS']" mode="mips"/>
                <xsl:call-template name="xformsMarkup"/>
                <xsl:apply-templates select="//*[@class='notes']"/>
                <xsl:apply-templates select="//*[@class='limitations']"/>
                <xsl:for-each select="div[@class='other']">
                    <h2><xsl:value-of select="@title"/></h2>
                    <xsl:copy-of select="."/>
                </xsl:for-each>

            </div>
            <xsl:call-template name="javascript"/>
        </body>
    </xsl:template>





    <xsl:template name="javascript">
        <xsl:variable name="inlineScript">
            <script type="text/javascript">
                function showClass(cssClass) {
                    require(["dojo/query", "dojo/dom-class"], function (query, domClass) {
                        query('.sample .' + cssClass).forEach(function (item) {
                            domClass.add(item, 'showClass');
                        });
                    });
                }

                function hideClass(cssClass) {
                    require(["dojo/query", "dojo/dom-class"], function (query, domClass) {
                        query('.sample .' + cssClass).forEach(function (item) {
                            domClass.remove(item, 'showClass');
                        });
                    });
                }

                require(["dojo/parser", "dojo/_base/connect", "dijit/Tooltip", "dojo/dom", "dojox/highlight", "dojox/highlight/languages/_all", "dojox/highlight/widget/Code", "dojo/domReady!"],
                        function (parser, connect, Tooltip, dom) {
                            var xforms = dom.byId("xforms");
                            // console.debug("parse ",xforms);
                            parser.parse(xforms);
                            connect.subscribe("/xf/ready", function () {
                                fluxProcessor.skipshutdown = true;
                            });
                        }
                );
            </script>
        </xsl:variable>
        <xsl:copy-of select="$inlineScript"/>
    </xsl:template>


    <!-- here the highlighted code for the example will be generated -->
    <!-- here the highlighted code for the example will be generated -->
    <!-- here the highlighted code for the example will be generated -->
    <xsl:template name="xformsMarkup">

        <h2>XForms Markup</h2>
        <xsl:element name="div">
            <xsl:attribute name="class">Section markup</xsl:attribute>
            <xsl:element name="pre">
                <xsl:element name="code">
                    <xsl:attribute name="class">xml</xsl:attribute>
                    <xsl:attribute name="data-dojo-type">dojox.highlight.Code</xsl:attribute>
                            <xsl:text>
</xsl:text>
                    <xsl:for-each select="$root//*[@class='ui' or @class='model' or @class='escape']/*">
                        <xsl:apply-templates select="." mode="escape"/>
                        <xsl:text>&#10;</xsl:text>
                        <xsl:if test="position()!=last()">
                            <xsl:text>&#10;</xsl:text>
                        </xsl:if>
                    </xsl:for-each>

                </xsl:element>
            </xsl:element>
        </xsl:element>

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
                           href="http://www.w3.org/TR/2009/REC-xforms-20091020#ui-input{$specRef}" target="_blank">Recommendation</a>
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



    <xsl:template match="div[@class='sample']" mode="sample" priority="10">
        <h2>Example</h2>
        <div class="Section sample">
            <xsl:apply-templates select="*[@class='markup']" mode="sample"/>
        </div>
    </xsl:template>

    <xsl:template match="xf:model" mode="sample" priority="10"/>
    <xsl:template match="*[@class='escape']" mode="sample" priority="10"/>

    <xsl:template match="*[@class='markup']|*[@class='ui']|*[@class='model']" mode="sample" priority="10">
        <xsl:apply-templates mode="sample"/>
    </xsl:template>

    <xsl:template match="@*|*" mode="sample" priority="1">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="sample"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text()" mode="sample" priority="1">
        <xsl:copy-of select="normalize-space(.)"/>
    </xsl:template>
    <xsl:template match="comment()" mode="sample"><xsl:text>
        </xsl:text>
        <xsl:copy/>
    </xsl:template>

    <!-- MIPS Markup -->
    <!-- MIPS Markup -->
    <!-- MIPS Markup -->
    <xsl:template match="*[@class='mips']|*[@class='MIPS']" mode="mips" priority="10">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="mips"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*|@*" mode="mips">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="mips"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text()" mode="mips">
        <xsl:copy-of select="normalize-space(.)"/>
    </xsl:template>
    <xsl:template match="comment()" mode="mips"><xsl:text>
        </xsl:text>
        <xsl:copy/>
    </xsl:template>


    <!-- XFORMS MARKUP -->
    <!-- XFORMS MARKUP -->
    <!-- XFORMS MARKUP -->
    <xsl:template match="xf:model" mode="xforms">
        <div style="display:none">
            <xf:model>
                <xsl:apply-templates select="@*|*" mode="xforms"/>
            </xf:model>
        </div>
    </xsl:template>

    <xsl:template match="*[@class='model']" mode="xforms" priority="10">
        <xsl:apply-templates mode="xforms"/>
    </xsl:template>

    <xsl:template match="*|@*" mode="xforms">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="xforms"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text()" mode="xforms">
        <xsl:copy-of select="normalize-space(.)"/>
    </xsl:template>

    <xsl:template match="comment()" mode="xforms"><xsl:text>
        </xsl:text>
        <xsl:copy/>
    </xsl:template>

    <!-- CSS HIGHLIGHTING -->
    <!-- CSS HIGHLIGHTING -->
    <!-- CSS HIGHLIGHTING -->
    <xsl:template match="div[@class='CSS']" mode="css">
        <xsl:if test="table">
            <div class="CSS">
                <xsl:apply-templates select="table" mode="css"/>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="table" mode="css">
        <table>
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
                        <div data-dojo-type="dijit.Tooltip" connectId="{$idref}">
                            <xsl:value-of select="td[2]"/>
                        </div>
                    </td>
                </tr>
            </xsl:for-each>
            <tr>
                <td id="xfcontrol" onmouseover="showClass('xfControl');"
                    onmouseout="hideClass('xfControl');">xfControl
                    <div data-dojo-type="dijit.Tooltip" connectId="xfcontrol">
                        matches an Element that represents a XForms control
                    </div>
                </td>
            </tr>
            <tr>
                <td id="xflabel" onmouseover="showClass('xfLabel');"
                    onmouseout="hideClass('xfLabel');">xfLabel
                    <div data-dojo-type="dijit.Tooltip" connectId="xflabel">
                        matches the label part of an XForms control
                    </div>
                </td>
            </tr>
            <tr>
                <td id="xfvalue" onmouseover="showClass('xfValue');"
                    onmouseout="hideClass('xfValue');">xfValue
                    <div data-dojo-type="dijit.Tooltip" connectId="xfvalue">
                        matches the widget part of an XForms control
                    </div>
                </td>
            </tr>
            <xsl:if test="exists(//div[@class='MIPS'])">
                <tr>
                    <td id="xfreadonly" onmouseover="showClass('xfReadOnly');"
                        onmouseout="hideClass('xfReadOnly');">
                        xfReadOnly
                        <div data-dojo-type="dijit.Tooltip" connectId="xfreadonly">
                            matches a xforms control that is currently readonly
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfreadwrite" onmouseover="showClass('xfReadWrite');"
                        onmouseout="hideClass('xfReadWrite');">
                        xfReadWrite
                        <div data-dojo-type="dijit.Tooltip" connectId="xfreadwrite">
                            matches a xforms control that is currently writable
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfrequired" onmouseover="showClass('xfRequired');"
                        onmouseout="hideClass('xfRequired');">
                        xfRequired
                        <div data-dojo-type="dijit.Tooltip" connectId="xfrequired">
                            matches a xforms control that is currently required
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfoptional" onmouseover="showClass('xfOptional');"
                        onmouseout="hideClass('xfOptional');">
                        xfOptional
                        <div data-dojo-type="dijit.Tooltip" connectId="xfoptional">
                            matches a xforms control that is currently optional
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfenabled" onmouseover="showClass('xfEnabled');"
                        onmouseout="hideClass('xfEnabled');">
                        xfEnabled
                        <div data-dojo-type="dijit.Tooltip" connectId="xfenabled">
                            matches a xforms control that is currently relevant (enabled)
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfdisabled" onmouseover="showClass('xfDisabled');"
                        onmouseout="hideClass('xfDisabled');">
                        xfDisabled
                        <div data-dojo-type="dijit.Tooltip" connectId="xfdisabled">
                            matches a xforms control that is currently disabled (can't be visualized here as the control gets hidden)
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfvalid" onmouseover="showClass('xfValid');"
                        onmouseout="hideClass('xfValid');">
                        xfValid
                        <div data-dojo-type="dijit.Tooltip" connectId="xfvalid">
                            matches a xforms control that is currently valid
                        </div>
                    </td>
                </tr>
                <tr>
                    <td id="xfinvalid" onmouseover="showClass('xfInvalid');"
                        onmouseout="hideClass('xfInvalid');">
                        xfInvalid
                        <div data-dojo-type="dijit.Tooltip" connectId="xfinvalid">
                            matches a xforms control that is currently invalid
                        </div>
                    </td>
                </tr>
            </xsl:if>
        </table>
    </xsl:template>

    <xsl:template match="*|@*|text()" mode="css">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="css"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="comment()" mode="css"><xsl:text>
        </xsl:text>
        <xsl:copy/>
    </xsl:template>


    <xsl:template match="div[@class='notes']">
        <h2>Notes</h2>
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="div[@class='limitations']">
        <h2>Limitations</h2>
        <xsl:copy-of select="."/>
    </xsl:template>



    <xsl:template match="*[@class='description']" mode="description">
        <xsl:copy>
            <xsl:apply-templates select="@*|*" mode="description"/>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="*[@class='keyword']|a|strong" mode="description">
        <xsl:text> </xsl:text><xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="text()"/>
        </xsl:copy><xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template match="*|@*" mode="description">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="description"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text()" mode="description">
        <xsl:copy-of select="normalize-space(.)"/>
    </xsl:template>

    <xsl:template match="comment()" mode="description"><xsl:text>
        </xsl:text>
        <xsl:copy/>
    </xsl:template>


    <xsl:template match="*|@*">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="text()">
        <xsl:copy-of select="normalize-space(.)"/>
    </xsl:template>

    <xsl:template match="comment()"><xsl:text>
    </xsl:text>
    <xsl:copy/>
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
