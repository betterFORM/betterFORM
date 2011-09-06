<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
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
        <xsl:for-each select="data/ul">
            function <xsl:value-of select="@id"/>Menu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                <xsl:if test="exists(li[@class='common'])">
                    var pCommonMenu = new dijit.Menu();
                    <xsl:for-each select="li[@class='common']">
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "<xsl:value-of select="text()"/>" ,
                            onClick:function() {
                                addElement('<xsl:value-of select="text()"/>');
                            }
                        }));
                    </xsl:for-each>
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                </xsl:if>
                <xsl:if test="exists(li[not(exists(@class))])">
                    <xsl:for-each select="li[not(exists(@class))]">
                        pMenu.addChild(new dijit.MenuItem({
                            label: "<xsl:value-of select="text()"/>",
                            onClick:function() {
                                addElement('<xsl:value-of select="text()"/>');
                            }
                        }));
                    </xsl:for-each>
                </xsl:if>
                <xsl:if test="exists(li[@class='control'])">
                    var pControlsMenu = new dijit.Menu();
                    <xsl:for-each select="li[@class='control']">
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "<xsl:value-of select="text()"/>",
                            onClick:function() {
                                addElement('<xsl:value-of select="text()"/>');
                            }
                        }));
                    </xsl:for-each>
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                </xsl:if>
                <xsl:if test="exists(li[@class='action'])">
                    var pActionMenu = new dijit.Menu();
                    <xsl:for-each select="li[@class='action']">
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "<xsl:value-of select="text()"/>",
                            onClick:function() {
                                addElement('<xsl:value-of select="text()"/>');
                            }
                        }));
                    </xsl:for-each>
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                </xsl:if>
                <xsl:if test="exists(li[@class='container'])">
                    var pContainerMenu = new dijit.Menu();
                    <xsl:for-each select="li[@class='container']">
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "<xsl:value-of select="text()"/>",
                            onClick:function() {
                                addElement('<xsl:value-of select="text()"/>');
                            }
                        }));
                    </xsl:for-each>
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "container",
                        popup: pContainerMenu
                    }));

                </xsl:if>
                pMenu.startup();
            <xsl:text>}</xsl:text>

        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
