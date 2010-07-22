<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
        <xsl:output method="xml" indent="yes" />

    <!-- author: Joern Turner / Lars Windauer -->

    <xsl:template match="property[@name='filter.ignoreResponseBody']">
        <property name="filter.ignoreResponseBody" value="true"
                  description="if 'true' XFormsFilter will not check reponse body for XForms markup"/>

    </xsl:template>


    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>


</xsl:stylesheet>
