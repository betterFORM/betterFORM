<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                exclude-result-prefixes="bf xsl">

    <xsl:param name="root" select="'../../../../../../src/main/xforms'"/>

    <!--
    Simple Stylesheet to assemble XForms documents from markup found in other files.

    Syntax for includes:
    <bf:include src="[path]#[id]/>

    where [path] is the relative path to the file to be included (basedir is determined by $rootDir global var)
          [id] is some element in the file identified by [filename] that has a matching id Attribute

    -->
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <!--
                <xsl:message>processing include directives.....</xsl:message>
                <xsl:message>inclusion root: <xsl:value-of select="$root"/></xsl:message>
        -->
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>

    <xsl:template match="bf:include">
        <xsl:variable name="file">
            <xsl:choose>
                <xsl:when test="string-length(substring-before(@src,'#')) &gt; 0"><xsl:value-of select="substring-before(@src,'#')"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="@src"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--
                <xsl:message>######################################</xsl:message>
                <xsl:message>File is <xsl:value-of select="$file"/></xsl:message>
                <xsl:message>File is <xsl:value-of select="$file"/></xsl:message>
                <xsl:message>File is <xsl:value-of select="$file"/></xsl:message>
                <xsl:message>######################################</xsl:message>
        -->

        <xsl:variable name="resource">
            <xsl:choose>
                <xsl:when test="exists(/*[@xml:base])">
                    <xsl:value-of select="concat(/*/@xml:base,$file)"/>
                </xsl:when>
                <xsl:when test="starts-with($file,'http')">
                    <xsl:value-of select="$file"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat($root,$file)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--
                <xsl:message>######################################</xsl:message>
                <xsl:message>Resource is <xsl:value-of select="$resource"/></xsl:message>
                <xsl:message>Resource is <xsl:value-of select="$resource"/></xsl:message>
                <xsl:message>Resource is <xsl:value-of select="$resource"/></xsl:message>
                <xsl:message>######################################</xsl:message>
        -->

        <xsl:variable name="fragmentId" select="substring-after(@src,'#')"/>
        <!--
                <xsl:message>######################################</xsl:message>
                <xsl:message>FragmentId is <xsl:value-of select="$fragmentId"/></xsl:message>
                <xsl:message>FragmentId is <xsl:value-of select="$fragmentId"/></xsl:message>
                <xsl:message>FragmentId is <xsl:value-of select="$fragmentId"/></xsl:message>
                <xsl:message>######################################</xsl:message>
        -->
        <xsl:choose>
            <xsl:when test="string-length($fragmentId) &gt;0">
                <!--
                                <xsl:message>######################################</xsl:message>
                                <xsl:message>Including fragment.</xsl:message>
                                <xsl:message>Including fragment.</xsl:message>
                                <xsl:message>Including fragment.</xsl:message>
                                <xsl:message>######################################</xsl:message>
                -->
                <xsl:variable name="markupToInsert">
                    <xsl:apply-templates select="doc($resource)//*[@id=$fragmentId]"/>
                </xsl:variable>
                <xsl:choose>
                    <xsl:when test="./*[exists(@action)]">
                        <xsl:for-each select="./*[exists(@action)]">
                            <!--<xsl:message>Found betterFORM Action: <xsl:value-of select="./@action"/></xsl:message>-->
                            <xsl:choose>
                                <xsl:when test="@action ='append'">
                                    <!--<xsl:message terminate="no">betterFORM Action: Append original markup to template markup</xsl:message>-->
                                    <xsl:apply-templates mode="append" select="$markupToInsert">
                                        <xsl:with-param name="markupToPreserve" select="."/>
                                    </xsl:apply-templates>
                                </xsl:when>
                                <xsl:when test="@action ='overwrite'">
                                    <!--<xsl:message terminate="no">betterFORM Action: Overwrite markup</xsl:message>-->
                                    <xsl:apply-templates mode="replace" select="$markupToInsert">
                                        <xsl:with-param name="markupToPreserve" select="."/>
                                    </xsl:apply-templates>
                                </xsl:when>
                                <xsl:otherwise><xsl:message terminate="yes">Unknown betterFORM Action: <xsl:value-of select="@action"/> </xsl:message></xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise><xsl:copy-of select="$markupToInsert"/></xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="string-length($resource) &gt;0">
                <xsl:apply-templates select="doc($resource)/*[1]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:message terminate="yes">inclusion failed. Attribute src for bf:include does not point to an existing file</xsl:message>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*[exists(@action='append')]" mode="append">
        <xsl:param name="markupToPreserve"/>
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:apply-templates select="*" />
            <xsl:apply-templates select="$markupToPreserve" mode="copy"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*[exists(@action='replace')]" mode="replace">
        <xsl:param name="markupToPreserve"/>
        <xsl:copy>
            <xsl:apply-templates select="@*" />
            <xsl:apply-templates select="*[not(@id = $markupToPreserve/@id)]" />
            <xsl:apply-templates select="$markupToPreserve" mode="copy"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*[name()='bf:action']" mode="copy" priority="20"/>

    <xsl:template match="@*" mode="copy">
        <xsl:copy/>
    </xsl:template>

    <xsl:template match="node()" mode="copy">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="copy"/>
            <xsl:apply-templates mode="copy"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
