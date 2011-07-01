<xsl:stylesheet version="2.0"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="html" indent="yes" exclude-result-prefixes="xf"/>
    <xsl:strip-space elements="*"/>
    <!-- author: Joern Turner -->
    <!--todo: prototypes -->
    <xsl:param name="webxml.path" select="''"/>

    <xsl:key name="names" match="xsd:element" use="@name"/>

    <xsl:variable name="inputDoc" select="/"/>

    <xsl:template match="/data">
        <ul>
            <xsl:for-each select="ul">

                <xsl:if test="exists(./li)">

                    <li id="{@id}-tmpl" class="{@id}">
                        <a href="#"><xsl:value-of select="@id"/></a>

                        <xsl:if test="li[@class='element']">
                            <ul class="category">
                                    <xsl:for-each select="li[@class='element']">
                                        <xsl:sort select="."/>
                                        <xsl:variable name="name"><xsl:value-of select="."/></xsl:variable>
                                        <li>
                                            <xsl:copy-of select="@*"/>
                                            <a href="javascript:addElement('{$name}');"><xsl:value-of select="$name"/></a>
                                        </li>
                                    </xsl:for-each>
                            </ul>
                        </xsl:if>
                        <xsl:if test="li[@class='common']">
                            <ul class="category">
                                <li class="{@class}">
                                    <a href="#">Common</a>
                                    <ul>
                                        <!--<li class="common"><a href="javascript:addElement('label');">label</a></li>-->
                                        <xsl:call-template name="makeEntry">
                                            <xsl:with-param name="match" select="'common'"/>
                                        </xsl:call-template>
                                    </ul>
                                </li>
                            </ul>
                        </xsl:if>
                        <xsl:if test="li[@class='controls']">
                            <ul class="category">
                                <li class="{@class}">
                                    <a href="#">Controls</a>
                                    <ul>
                                        <xsl:call-template name="makeEntry">
                                            <xsl:with-param name="match" select="'controls'"/>
                                        </xsl:call-template>
                                    </ul>
                                </li>
                            </ul>
                        </xsl:if>
                        <xsl:if test="li[@class='container']">
                            <ul class="category">
                                <li class="container">
                                    <a href="#">Container</a>
                                    <ul>
                                        <xsl:call-template name="makeEntry">
                                            <xsl:with-param name="match" select="'container'"/>
                                        </xsl:call-template>
                                    </ul>
                                </li>
                            </ul>
                        </xsl:if>
                        <xsl:if test="li[@class='action']">
                            <ul class="category">
                                <li class="action" style="text-align:left;">
                                    <a href="#">Actions</a>
                                    <ul>
                                        <xsl:call-template name="makeEntry">
                                            <xsl:with-param name="match" select="'action'"/>
                                        </xsl:call-template>
                                    </ul>
                                </li>
                            </ul>
                        </xsl:if>
                    </li>


                </xsl:if>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template name="makeEntry">
        <xsl:param name="match"/>
            <xsl:for-each select="li[@class=$match]">
                <xsl:sort select="."/>
                <xsl:variable name="name"><xsl:value-of select="."/></xsl:variable>
                <li>
                    <xsl:copy-of select="@*"/>
                    <a href="javascript:addElement('{$name}');"><xsl:value-of select="$name"/></a>
                </li>
            </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
