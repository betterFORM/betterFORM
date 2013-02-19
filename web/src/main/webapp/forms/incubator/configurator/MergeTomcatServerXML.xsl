<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns=""
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" />

    <xsl:param name="keystore" select="''"/>
    <xsl:param name="password" select="''"/>

    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="Connector[@port='8080']" priority="10">
        <xsl:text>
    </xsl:text>
        <Connector
           port="8443" maxThreads="200"
           scheme="https" secure="true" SSLEnabled="true"
           clientAuth="false" sslProtocol="TLS">
                <xsl:attribute name="keystoreFile"><xsl:value-of select="$keystore"/></xsl:attribute>
                <xsl:attribute name="keystorePass"><xsl:value-of select="$password"/></xsl:attribute>
        </Connector>
        <xsl:copy-of select="."/>
    </xsl:template>
</xsl:stylesheet>
