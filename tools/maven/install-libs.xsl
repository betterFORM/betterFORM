<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
        exclude-result-prefixes="html ev xsl xf">

    <!-- Copyright 2008 Lars Windauer, Joern Turner -->

    <xsl:param name="buildprops"/>
    <xsl:param name="core.dir" />
    <xsl:param name="web.dir" />
    <xsl:param name="convex.dir"/>
    <xsl:param name="server.dir"/>
    <xsl:param name="module"/>

    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="/">
            <project name="Maven dependency generator" default="install-dependencies" basedir=".">
                    <condition property="maven.executable" value="mvn.bat" else="mvn">
                        <os family="windows"/>
                </condition>

                <property>
                    <xsl:attribute name="name">mvn-cmd</xsl:attribute>
                    <xsl:attribute name="value">${maven.executable}</xsl:attribute>
                </property>

                <property name="core.dir" value="{$core.dir}"/>
                <xsl:if test="string-length($web.dir)"><property name="web.dir" value="{$web.dir}"/></xsl:if>
                <xsl:if test="string-length($convex.dir)"><property name="convex.dir" value="{$convex.dir}"/></xsl:if>
                <xsl:if test="string-length($server.dir)"><property name="server.dir" value="{$server.dir}"/></xsl:if>

                <xsl:if test="$module=document($buildprops)/root/convex/app/name">
                    <property file="build.properties"/>
                </xsl:if>

                <target name="mvn">
                    <echo><xsl:attribute name="message">${goal}</xsl:attribute></echo>
                    <exec failonerror="true">
                        <xsl:attribute name="dir">${basedir}</xsl:attribute>
                        <xsl:attribute name="executable">${mvn-cmd}</xsl:attribute>
                        <arg>
                            <xsl:attribute name="line">${goal}</xsl:attribute>
                        </arg>
                    </exec>
                </target>

                <target name="install-dependencies" description="init the build process">
                    <xsl:choose>
                        <xsl:when test="$module=document($buildprops)/root/core/app/name">
                            <xsl:for-each select="document($buildprops)/root/core//pathelement[@artifactId]">
                                <xsl:variable name="install-cmd">install:install-file -DgroupId=<xsl:value-of select="@groupid"/> -DartifactId=<xsl:value-of select="@artifactId"/> -Dversion=<xsl:value-of select="@version"/> -Dpackaging=jar -Dfile=<xsl:value-of select="@location"/></xsl:variable>
                                <antcall target="mvn">
                                    <xsl:element name="param">
                                        <xsl:attribute name="name">goal</xsl:attribute>
                                        <xsl:attribute name="value"><xsl:value-of select="$install-cmd"/></xsl:attribute>
                                    </xsl:element>
                                </antcall>
                            </xsl:for-each>                            
                        </xsl:when>

                        <xsl:when test="$module=document($buildprops)/root/web/app/name">
                            <xsl:for-each select="document($buildprops)/root/web//pathelement[@artifactId]">
                                <xsl:variable name="install-cmd">install:install-file -DgroupId=<xsl:value-of select="@groupid"/> -DartifactId=<xsl:value-of select="@artifactId"/> -Dversion=<xsl:value-of select="@version"/> -Dpackaging=jar -Dfile=<xsl:value-of select="@location"/></xsl:variable>
                                <antcall target="mvn">
                                    <xsl:element name="param">
                                        <xsl:attribute name="name">goal</xsl:attribute>
                                        <xsl:attribute name="value"><xsl:value-of select="$install-cmd"/></xsl:attribute>
                                    </xsl:element>
                                </antcall>
                            </xsl:for-each>
                            <antcall target="mvn">
                                <xsl:element name="param">
                                    <xsl:attribute name="name">goal</xsl:attribute>
                                    <xsl:attribute name="value">install:install-file -DgroupId=de.betterform -DartifactId=<xsl:value-of select="document($buildprops)/root/core/app/name"/> -Dversion=<xsl:value-of select="document($buildprops)/root/core/app/version"/> -Dpackaging=jar -Dfile=${core.dir}/target/<xsl:value-of select="document($buildprops)/root/core/app/name"/>-<xsl:value-of select="document($buildprops)/root/core/app/version"/>.jar</xsl:attribute>
                                </xsl:element>
                            </antcall>
                        </xsl:when>
                        <xsl:when test="$module=document($buildprops)/root/convex/app/name">
                            <xsl:for-each select="document($buildprops)/root/convex//pathelement[@artifactId]">
                                <xsl:variable name="install-cmd">install:install-file -DgroupId=<xsl:value-of select="@groupid"/> -DartifactId=<xsl:value-of select="@artifactId"/> -Dversion=<xsl:value-of select="@version"/> -Dpackaging=jar -Dfile=<xsl:value-of select="@location"/></xsl:variable>
                                <antcall target="mvn">
                                    <xsl:element name="param">
                                        <xsl:attribute name="name">goal</xsl:attribute>
                                        <xsl:attribute name="value"><xsl:value-of select="$install-cmd"/></xsl:attribute>
                                    </xsl:element>
                                </antcall>
                            </xsl:for-each>
                            <antcall target="mvn">
                                <xsl:element name="param">
                                    <xsl:attribute name="name">goal</xsl:attribute>
                                    <xsl:attribute name="value">install:install-file -DgroupId=de.betterform -DartifactId=<xsl:value-of select="document($buildprops)/root/core/app/name"/> -Dversion=<xsl:value-of select="document($buildprops)/root/core/app/version"/> -Dpackaging=jar -Dfile=${core.dir}/target/<xsl:value-of select="document($buildprops)/root/core/app/name"/>-<xsl:value-of select="document($buildprops)/root/core/app/version"/>.jar</xsl:attribute>
                                </xsl:element>
                            </antcall>
                        </xsl:when>
                        <xsl:when test="$module=document($buildprops)/root/server/app/name">
                            <xsl:for-each select="document($buildprops)/root/server//pathelement[@artifactId]">
                                <xsl:variable name="install-cmd">install:install-file -DgroupId=<xsl:value-of select="@groupid"/> -DartifactId=<xsl:value-of select="@artifactId"/> -Dversion=<xsl:value-of select="@version"/> -Dpackaging=jar -Dfile=<xsl:value-of select="@location"/></xsl:variable>
                                <antcall target="mvn">
                                    <xsl:element name="param">
                                        <xsl:attribute name="name">goal</xsl:attribute>
                                        <xsl:attribute name="value"><xsl:value-of select="$install-cmd"/></xsl:attribute>
                                    </xsl:element>
                                </antcall>
                            </xsl:for-each>
                            <antcall target="mvn">
                                <xsl:element name="param">
                                    <xsl:attribute name="name">goal</xsl:attribute>
                                    <xsl:attribute name="value">install:install-file -DgroupId=de.betterform -DartifactId=<xsl:value-of select="document($buildprops)/root/core/app/name"/> -Dversion=<xsl:value-of select="document($buildprops)/root/core/app/version"/> -Dpackaging=jar -Dfile=${core.dir}/target/<xsl:value-of select="document($buildprops)/root/core/app/name"/>-<xsl:value-of select="document($buildprops)/root/core/app/version"/>.jar</xsl:attribute>
                                </xsl:element>
                            </antcall>
                        </xsl:when>
                    </xsl:choose>
                </target>
            </project>
    </xsl:template>

</xsl:stylesheet>
