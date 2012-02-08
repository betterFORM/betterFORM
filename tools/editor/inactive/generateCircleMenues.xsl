<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="text" indent="yes" exclude-result-prefixes="xf"/>
    <xsl:strip-space elements="*"/>
    <!-- author: Joern Turner -->
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <xsl:call-template name="generateActionMenu" />
        <xsl:call-template name="generateMenues" />
    </xsl:template>

    <!-- Must be handled separately cause the actions form a submenu and are excluded from processing in template below-->
    <xsl:template name="generateActionMenu">
$("#actionMenu").prettypiemenu({
    buttons: [
        <xsl:for-each select="data/ul[@id='action']/li">
            { img: "ui-icon-action", title: "<xsl:value-of select="text()"/>" }<xsl:value-of select="if(position()!=last()) then ',' else ''"/>
        </xsl:for-each>
        ],
        onSelection: function(item) {
            alert (item + ' was really? clickedoo!');
        },
        iconW: 40,
        iconH: 16,
        outerPadding: 40,
        hideIcon:true,
        showTitles: false,
        showStartAnimation: true
});
    </xsl:template>

    <xsl:template name="generateMenues">
        <xsl:for-each select="data/ul[exists(li)]">
$("#<xsl:value-of select="@id"/>Menu").prettypiemenu({
    buttons: [
            <xsl:variable name="count" select="count(li)"/>
            <xsl:for-each select="li[not(@class='action')]">
            { img: "ui-icon-<xsl:value-of select="text()"/>", title: "<xsl:value-of select="text()"/>" }<xsl:value-of select="if(position()!=$count) then ',' else ''"/>
            </xsl:for-each>
            <xsl:if test="li[@class='action']">
            { img: "ui-icon-action", title: "actions" }
            </xsl:if>
    ],
    onSelection: function(item) {
        alert (item + ' was really? clickedoo!');
    },
    iconW: 40,
    iconH: 16,
    outerPadding: 40,
    hideIcon:true,
    showTitles: false,
    showStartAnimation: true
});
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
