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
            var <xsl:value-of select="@id"/>Childs = <xsl:text>[</xsl:text>

                    <xsl:if test="*"><xsl:for-each select="li">
                        "<xsl:value-of select="text()"/>"<xsl:if test="position() != last()">,</xsl:if>
                    </xsl:for-each>
                </xsl:if>
            <xsl:text>];</xsl:text>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>
