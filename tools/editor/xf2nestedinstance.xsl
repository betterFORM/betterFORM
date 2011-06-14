<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes"/>
    <!-- author: Joern Turner -->

    <xsl:template match="/*">
        <data>
            <!-- <xsl:for-each select="//*[namespace-uri()='http://www.w3.org/2002/xforms']"> -->
            <xsl:for-each select="//xf:*">
                <xsl:apply-templates select="." />
            </xsl:for-each>
        </data>
    </xsl:template>

    <xsl:template match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:trigger|xf:submit|xf:upload">
        <xfElement id="{@id}" name="{local-name()}">
            <xsl:variable name="level">
                <xsl:value-of select="count(ancestor::xf:*) + 1" />
            </xsl:variable>
            <xsl:attribute name="level"><xsl:value-of select="$level"/></xsl:attribute>
             <xsl:for-each select="@*">
                 <xsl:copy-of select="."/>
             </xsl:for-each>
            <xsl:apply-templates/>
         </xfElement>
     </xsl:template>

    <xsl:template match="xf:*/text()"/>
</xsl:stylesheet>
