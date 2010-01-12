<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/1999/xhtml"
    xmlns:xf="http://www.w3.org/2002/xforms"
    xmlns:saxon="http://icl.com/saxon"
    exclude-result-prefixes="saxon">


    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml"
              xmlns:xf="http://www.w3.org/2002/xforms"
              xmlns:ev="http://www.w3.org/2001/xml-events"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xmlns:xsd="http://www.w3.org/2001/XMLSchema"
              lang="de">
            <xsl:apply-templates/>
        </html>
    </xsl:template>
    
    <xsl:template match="html:head">
        <head xmlns="http://www.w3.org/1999/xhtml">
            <title>Repeated <xsl:value-of select="//html:head/html:title"/></title>

            <style type="text/css">
                .xfGroupLabel {
                    font-size: 12pt;
                    font-weight: bold;
                    padding-bottom: 10px;
                    margin-bottom: 4px;
                    background: yellow;
                    border-bottom: black thin solid;
                }

                #mips .xfControl {
                    margin-bottom:2px
                }

                #mips .xfTrigger .dijitButtonContents {
                    width: 200px;
                }

                .crud .xfTrigger {
                    float: left;
                    margin: 4px;
                }

                .xfRepeatHeader {
                    font-weight: bold;
                    background: lightyellow;
                    border-bottom: black thin solid;
                    margin-bottom: 4px;
                }



                .xfRepeatHeader .col-1 {
                    padding: 4px;
                }

                .xfRepeatItem .col-1 .xfControl {
                    margin-bottom: 2px;
                    padding: 4px;
                }


                .xfRepeatHeader .col-2 {
                    padding: 4px;
                }

                .xfRepeatItem .col-2 .xfControl {
                    margin-bottom: 2px;
                    padding: 4px;
                }


                .xfRepeatHeader .col-3 {
                    padding: 4px;
                }

                .xfRepeatItem .col-3 .xfControl {
                    margin-bottom: 2px;
                    padding: 4px;
                }


                .xfRepeatHeader .col-4 {
                    padding: 4px;
                }

                .xfRepeatItem .col-4 .xfControl {
                    margin-bottom: 2px;
                    padding: 4px;
                }

                <xsl:variable name="hostTitle" select="//html:head/html:title"/>
                <xsl:choose>
                    <xsl:when test="$hostTitle = 'Input'">



                        .xfRepeatHeader .col-1 {
                            width: 140px;
                        }

                        .xfRepeatItem .col-1 {
                            width: 140px;
                            padding: 2px;

                        }


                        .xfRepeatItem .col-1 .xfValue {
                        width: 130px;
                        padding: 2px;

                        }

                        .xfRepeatItem .col-2 .xfControl {
                            margin-left:45px;
                        }

                        .xfRepeatHeader .col-2 {
                        width: 100px;

                        }

                        .xfRepeatItem .col-2 {
                        width: 100px;
                        padding: 2px;

                        }


                        .xfRepeatHeader .col-3 {
                        width: 140px;
                        }

                        .xfRepeatItem .col-3 {
                        width: 140px;
                        padding: 2px;

                        }


                        .xfRepeatItem .col-3 .xfValue {
                        width: 130px;
                        padding: 2px;
                        }

                    </xsl:when>
                           <xsl:when test="$hostTitle = 'Trigger'">
                               .xfRepeatHeader {
                                            background: white;
                                               }


                               .xfRepeatHeader .col-1 {
                                   padding: 0px;
                               }

                               .xfRepeatHeader .col-2 {
                                   padding: 0px;
                               }

                               .xfRepeatHeader .col-3 {
                                   padding: 0px;
                               }

                               .xfRepeatHeader .col-4 {
                                   padding: 0px;
                               }

                               </xsl:when>

                    <xsl:when test="$hostTitle = 'Range'">
                        .xfRepeatItem .col-2 .xfControl {
                            margin-left:85px;
                        }

                    </xsl:when>
                    <xsl:when test="$hostTitle = 'Select1'">
                           .xfRepeatHeader .col-1 {
                            width: 155px;
                        }

                        .xfRepeatHeader .col-2 {
                            width: 155px;
                        }
                    </xsl:when>
                    <xsl:when test="$hostTitle = 'Select'">
                           .xfRepeatHeader .col-1 {
                            width: 155px;
                        }

                        .xfRepeatHeader .col-2 {
                            width: 155px;
                        }
                    </xsl:when>
                    <xsl:when test="$hostTitle = 'Upload'">
                           .xfRepeatHeader .col-2 {
                            width: 300px;
                        }

                        .xfRepeatHeader .col-3 {
                            width: 300px;
                        }

                        .xfRepeatItem .col-2 {
                            width: 300px;
                        }

                        .xfRepeatItem .col-3 {
                            width: 300px;
                        }
                    </xsl:when>

                </xsl:choose>
            </style>

        </head>


    </xsl:template>


    <xsl:template match="html:body">
        <body class="tundra" style="margin:30px;" xmlns="http://www.w3.org/1999/xhtml">
            <div id="xforms">
                <div style="display:none;">
                    <xsl:apply-templates select="//xf:model" mode="model"/>
                </div>
                <xsl:apply-templates/>
            </div>
        </body>
    </xsl:template>


    <xsl:template match="xf:model" mode="model">
        <xf:model id="model-1" xmlns:xf="http://www.w3.org/2002/xforms">
            <xf:instance id="instance-1" xmlns="" xmlns:xf="http://www.w3.org/2002/xforms">
                <data constraint="true" readonly="false" required="false" relevant="true" xmlns="">
                        <xsl:copy-of select="//xf:instance//items"/>
                        <xsl:copy-of select="//xf:instance//items"/>
                        <xsl:copy-of select="//xf:instance//items"/>
                        <prototype>
                            <xsl:copy-of select="//xf:instance//items"/>
                        </prototype>

                </data>
            </xf:instance>
            <xsl:apply-templates mode="model"/>
        </xf:model>
    </xsl:template>

    <xsl:template match="xf:bind" mode="model">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="xf:instance[not(@id='instance-1')]" mode="model">
        <xsl:copy-of select="."/>
    </xsl:template>


    <xsl:template match="xf:instance[@id='instance-1']" mode="model"/>

    <xsl:template match="xf:instance" />    
    <xsl:template match="item"/>
    

    <xsl:template match="//xf:group[1]">
        <xf:group appearance="full" class="crud">
            <xf:label>Model item manipulation</xf:label>
            <table>
                <tr>
                    <td>
                        <xf:group>
                            <label>Insert Actions</label>
                            <xf:trigger>
                                <xf:label>after</xf:label>
                                    <xf:insert nodeset="items" at="index('r-control')" position="after" origin="instance('instance-1')/prototype/items"/>
                            </xf:trigger>
                            <xf:trigger>
                                <xf:label>before</xf:label>
                                    <xf:insert nodeset="items" at="index('r-control')" position="before" origin="instance('instance-1')/prototype/items"/>
                            </xf:trigger>
                            <xf:trigger>
                                <xf:label>first</xf:label>
                                    <xf:insert nodeset="items" at="1" position="before" origin="instance('instance-1')/prototype/items"/>
                            </xf:trigger>
                            <xf:trigger>
                                <xf:label>last</xf:label>
                                    <xf:insert nodeset="items" at="last()" position="after" origin="instance('instance-1')/prototype/items"/>
                            </xf:trigger>
                        </xf:group>
                    </td>
                    <td>
                        <xf:group>
                            <label>Delete Actions</label>
                            <xf:trigger>
                                <xf:label>selected</xf:label>
                                    <xf:delete nodeset="items" at="index('r-control')"/>
                            </xf:trigger>
                            <xf:trigger>
                                <xf:label>first</xf:label>
                                    <xf:delete nodeset="items" at="1"/>
                            </xf:trigger>
                            <xf:trigger>
                                <xf:label>last</xf:label>
                                    <xf:delete nodeset="items" at="(last()-1)"/>
                            </xf:trigger>
                        </xf:group>
                    </td>
                </tr>
            </table>
        </xf:group>
        <xf:group appearance="full">
            <xf:label>Repeated <xsl:value-of select="xf:label"/></xf:label>
            <xf:repeat id="r-control" nodeset="items" appearance="compact">
                <xsl:copy-of select="*[not(self::xf:label)]"/>
            </xf:repeat>
        </xf:group>
    </xsl:template>
    

    <xsl:template match="//xf:group[@id='mips']">
        <xf:group id="mips" appearance="full">
            <xsl:copy-of select="*"/>
        </xf:group>
    </xsl:template>
</xsl:stylesheet>
