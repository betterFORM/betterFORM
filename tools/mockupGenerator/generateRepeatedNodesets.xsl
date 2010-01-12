<?xml version="1.0" encoding="UTF-8"?>
        
<xsl:stylesheet version="2.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:saxon="http://icl.com/saxon"
    exclude-result-prefixes="xhtml xforms saxon">

    <xsl:template match="/">
        <textfield>
            <xsl:call-template name="loop">
                <xsl:with-param name="count">100</xsl:with-param>
            </xsl:call-template>

        </textfield>
    </xsl:template>



    <xsl:template name="loop">
        <xsl:param name="count"/>
        <xsl:param name="iteration">1</xsl:param>
        <entry a="a{$iteration}" b="b{$iteration}" c="c{$iteration}" d="d{$iteration}" e="e{$iteration}"/>
        

        <xsl:if test="$iteration &lt; $count">
            <xsl:call-template name="loop">
                <xsl:with-param name="count" select="$count"/>
                <xsl:with-param name="iteration" select="$iteration +1"/>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>