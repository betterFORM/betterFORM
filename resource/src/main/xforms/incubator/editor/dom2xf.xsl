<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="no"/>
    <!-- author: Joern Turner -->


    <xsl:template match="/*">
        <div id="xforms">
            <!-- <xsl:for-each select="//*[namespace-uri()='http://www.w3.org/2002/xforms']"> -->
            <xsl:apply-templates />
        </div>
    </xsl:template>

    <xsl:template match="*[@data-xf-type]"  priority="10">
        <xsl:variable name="curr" select="."/>
        <xsl:variable name="attrs" select="substring(@data-xf-attrs,2,string-length(@data-xf-attrs) - 2)"/>
        <xsl:message>attrs: <xsl:value-of select="$attrs"/></xsl:message>
        <xsl:element name="{concat('xf:',@data-xf-type)}" namespace="http://www.w3.org/2002/xforms">
            <!--<xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute>-->
            <xsl:if test="string-length($attrs) != 0">
                <xsl:for-each select="tokenize($attrs,',')">
                    <xsl:variable name="thisAttr"><xsl:value-of select="."/></xsl:variable>
                    <xsl:message>attr: <xsl:value-of select="$thisAttr"/> </xsl:message>

                    <xsl:variable name="quotedValue" select="substring-after($thisAttr,':')"/>
                    <xsl:variable name="unquoted">
                        <xsl:value-of select="substring($quotedValue,2,string-length($quotedValue)-2)"/>
                    </xsl:variable>
                    <xsl:attribute name="{substring-before($thisAttr,':')}"><xsl:value-of select="$unquoted"/></xsl:attribute>
                </xsl:for-each>
            </xsl:if>
            <xsl:apply-templates  />
        </xsl:element>
    </xsl:template>

    <xsl:template match="*[@data-xf-type='document']" priority="30"/>

    <xsl:template match="*">
        <xsl:apply-templates/>
    </xsl:template>


    <xsl:template match="text()"/>
</xsl:stylesheet>
