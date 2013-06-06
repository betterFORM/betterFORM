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

    <xsl:template match="property[@name='httpclient.ssl.keystore.path']/@value" priority="10">
        <xsl:attribute name="value"><xsl:value-of select="$keystore"/></xsl:attribute>
    </xsl:template>

    <xsl:template match="property[@name='httpclient.ssl.keystore.passwd']/@value" priority="10">
        <xsl:attribute name="value"><xsl:value-of select="$password"/></xsl:attribute>
    </xsl:template>

    <xsl:template match="property[1]" priority="10">
        <xsl:text>
    </xsl:text>
        <property name="httpclient.ssl.context" value="de.betterform.connector.http.ssl.KeyStoreSSLContext" description="Full classpath of SSLProtocolSocketFactory which should be used by httpclient."/>
        <xsl:copy-of select="."/>
    </xsl:template>
</xsl:stylesheet>
