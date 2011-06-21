<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="no"/>
    <!-- author: Joern Turner -->

    <xsl:param name="originDoc"/>

    <xsl:template match="/">
        <data>
            <xsl:copy-of select="document($originDoc)"/>
        </data>
    </xsl:template>

</xsl:stylesheet>
