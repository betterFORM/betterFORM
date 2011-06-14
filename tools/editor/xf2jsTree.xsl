<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes"/>
    <!-- author: Joern Turner -->
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <xsl:variable name="elements" select="//xf:model[not(ancestor::xf:*)]|//xf:group[not(ancestor::xf:*)]"/>
        <!--<xsl:variable name="uiElements" select="//*[name()='xf:group']"/>-->
        <div id="xfDoc" class="xfDoc" style="height:100px;">
            <ul>
                <xsl:for-each select="$elements">
                    <xsl:apply-templates select="." />
                </xsl:for-each>
            </ul>
        </div>
    </xsl:template>

    <xsl:template match="xf:*">
        <xsl:variable name="id" select="current()/@id"/>


        <xsl:variable name="props">[<xsl:for-each select="@*">
                <xsl:value-of select="local-name()"/>:'<xsl:value-of select="."/>'<xsl:if test="position()!=last()">,</xsl:if>
            </xsl:for-each>]</xsl:variable>
        <li id="{$id}" data-xf-type="{local-name()}" class="{local-name()}">
            <xsl:attribute name="data-xf-props"><xsl:value-of select="$props"/></xsl:attribute>
            <a href="#"><xsl:value-of select="local-name()"/>:<xsl:value-of select="@id"/></a>

            <xsl:if test="count(xf:*) != 0">
                <ul>
                    <xsl:for-each select="*">
                        <xsl:apply-templates select="." />
                    </xsl:for-each>
                </ul>
            </xsl:if>
        </li>
     </xsl:template>


<!--
    <ul>
        <li id="phtml_1">
            <a href="#">Root node 1</a>
            <ul>
                <li id="phtml_2">
                    <a href="#">Child node 1</a>
                </li>
                <li id="phtml_3">
                    <a href="#">Child node 2</a>
                </li>
            </ul>
        </li>
        <li id="phtml_4">
            <a href="#">Root node 2</a>
        </li>
    </ul>
-->

    <xsl:template match="xf:*/text()"/>
</xsl:stylesheet>
