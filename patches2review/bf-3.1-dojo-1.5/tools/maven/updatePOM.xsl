<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    exclude-result-prefixes="xsl mvn"
    xmlns:mvn="http://maven.apache.org/POM/4.0.0">
    <!--
        author: Joern Turner
        This stylesheet is used to update the dependencies in the Maven pom.xml file when changed in
        build.properties.xml.

    -->
    <xsl:param name="buildprops"/>
    <xsl:param name="module"/>
                 
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


    <xsl:template match="mvn:project/mvn:artifactId">
                <artifactId><xsl:value-of select="$module"/></artifactId>
    </xsl:template>

    <xsl:template match="mvn:project/mvn:version">
        <version><xsl:value-of select="document($buildprops)/root//app/version[../name=$module]"/></version>
    </xsl:template>

    <xsl:template match="mvn:project/mvn:dependencies">        
        <dependencies>
            <xsl:choose>
                <xsl:when test="$module = document($buildprops)/root/web/app/name">
                    <dependency>
                        <groupId>de.betterform</groupId>
                        <artifactId><xsl:value-of select="document($buildprops)/root/core/app/name"/></artifactId>
                        <version><xsl:value-of select="document($buildprops)/root/core/app/version"/></version>
                    </dependency>
                    <xsl:for-each select="document($buildprops)/root/web//pathelement[@artifactId]">
                        <dependency>
                            <groupId><xsl:value-of select="@groupid"/></groupId>
                            <artifactId><xsl:value-of select="@artifactId"/></artifactId>
                            <version><xsl:value-of select="@version"/></version>
                            <xsl:if test="string-length(@scope)&gt; 0"><scope><xsl:value-of select="@scope"/></scope></xsl:if>
                        </dependency>
                    </xsl:for-each>
                </xsl:when>
                <xsl:when test="$module = document($buildprops)/root/convex/app/name">
                    <dependency>
                        <groupId>de.betterform</groupId>
                        <artifactId><xsl:value-of select="document($buildprops)/root/core/app/name"/></artifactId>
                        <version><xsl:value-of select="document($buildprops)/root/core/app/version"/></version>
                    </dependency>

                    <xsl:for-each select="document($buildprops)/root/convex//pathelement[@artifactId]">
                        <dependency>
                            <groupId><xsl:value-of select="@groupid"/></groupId>
                            <artifactId><xsl:value-of select="@artifactId"/></artifactId>
                            <version><xsl:value-of select="@version"/></version>
                            <xsl:if test="string-length(@scope)&gt; 0"><scope><xsl:value-of select="@scope"/></scope></xsl:if>
                        </dependency>
                    </xsl:for-each>
                </xsl:when>
            </xsl:choose>
            <xsl:for-each select="document($buildprops)/root/core//pathelement[@artifactId]">
                <dependency>
                    <groupId><xsl:value-of select="@groupid"/></groupId>
                    <artifactId><xsl:value-of select="@artifactId"/></artifactId>
                    <version><xsl:value-of select="@version"/></version>
                    <xsl:if test="string-length(@scope)&gt; 0"><scope><xsl:value-of select="@scope"/></scope></xsl:if>
                </dependency>
            </xsl:for-each>
        </dependencies>
    </xsl:template>

</xsl:stylesheet>
