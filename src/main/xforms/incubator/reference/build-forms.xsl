 <?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" media-type="text/xml"/>
    
    <xsl:template match="pageintro">
        <xsl:message terminate="no">PAGEINTRO</xsl:message>
        <html xmlns="http://www.w3.org/1999/xhtml"
              xmlns:xf="http://www.w3.org/2002/xforms"
              xmlns:ev="http://www.w3.org/2001/xml-events"
              xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
              xmlns:xsd="http://www.w3.org/2001/XMLSchema"
              lang="de">
              <head>
                  <xsl:apply-templates select="." mode="pageintro"/>
              </head>
        </html>
    </xsl:template>

    <xsl:template match="page">
        <xsl:message terminate="no">PAGE</xsl:message>
    </xsl:template>

    <xsl:template match="title" mode="pageintro">
          <xsl:element name="title">
                <xsl:copy-of select="@*"/>
          </xsl:element>
    </xsl:template>
</xsl:stylesheet>
