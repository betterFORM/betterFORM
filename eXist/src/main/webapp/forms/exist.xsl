<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->
<!--
    <exist:result xmlns:exist="http://exist.sourceforge.net/NS/exist">
        <exist:collection name="/db/betterform/apps/forms/demo" created="2010-12-06T16:47:21.652+01:00" owner="betterFORM" group="betterFORM" permissions="rwur-ur-u">
            <exist:collection name="leer" created="2010-12-09T15:50:17.726+01:00" owner="admin" group="dba" permissions="rwur-ur-u"/>
            <exist:resource name="atom.xhtml" created="2010-12-06T16:47:22.299+01:00" last-modified="2010-12-06T16:47:55.744+01:00" owner="betterFORM" group="betterFORM" permissions="rwur-ur-u"/>
            <exist:resource name="contacts.xhtml" created="2010-12-06T16:47:22.446+01:00" last-modified="2010-12-06T16:47:55.811+01:00" owner="betterFORM" group="betterFORM" permissions="rwur-ur-u"/>
            <exist:resource name="contacts_subform_1.xml" created="2010-12-06T16:47:22.499+01:00" last-modified="2010-12-06T16:47:55.876+01:00" owner="betterFORM" group="betterFORM" permissions="rwur-ur-u"/>
            <exist:resource name="contacts_subform_2.xml" created="2010-12-06T16:47:22.561+01:00" last-modified="2010-12-06T16:47:55.931+01:00" owner="betterFORM" group="betterFORM" permissions="rwur-ur-u"/>
            <exist:resource name="orderlist.xhtml" created="2010-12-06T16:47:22.656+01:00" last-modified="2010-12-06T16:47:55.978+01:00" owner="betterFORM" group="betterFORM" permissions="rwur-ur-u"/>
            <exist:resource name="registration.xhtml" created="2010-12-06T16:47:22.753+01:00" last-modified="2010-12-06T16:47:56.061+01:00" owner="betterFORM" group="betterFORM" permissions="rwur-ur-u"/>
        </exist:collection>
    </exist:result>

    <listing>
        <parent>/forms<parent>
        <current>demo<current>
        <directory last-modified="" absolute-path="">demo</directory>
        <file last-modified="" absolute-path="">atom.xhtml<file>
    </listing>

-->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:exist="http://exist.sourceforge.net/NS/exist"
                exclude-result-prefixes="exist">

    <xsl:output method="xml" version="1.0" encoding="UTF-8" media-type="text/xml" indent="yes"/>


    <xsl:template match="exist:result">
        <listing>
            <xsl:apply-templates/>
        </listing>
    </xsl:template>

    <xsl:template match="exist:collection">
        <xsl:variable name="path" select="@name"/>

        <xsl:variable name="parent">
            <xsl:call-template name="substring-before-last">
                <xsl:with-param name="string1" select="$path"/>
                <xsl:with-param name="string2" select="'/'"/>
            </xsl:call-template>
        </xsl:variable>


        <parent>
            <xsl:value-of select="$parent"/>
        </parent>
        <current>
            <xsl:value-of select="substring-after($path, $parent)"/>
        </current>

        <xsl:apply-templates mode="children">
            <xsl:with-param name="path" select="$path"/>
        </xsl:apply-templates>

    </xsl:template>

    <xsl:template name="substring-before-last">
        <xsl:param name="string1" select="''"/>
        <xsl:param name="string2" select="''"/>

        <xsl:if test="$string1 != '' and $string2 != ''">
            <xsl:variable name="head" select="substring-before($string1, $string2)"/>
            <xsl:variable name="tail" select="substring-after($string1, $string2)"/>
            <xsl:value-of select="$head"/>
            <xsl:if test="contains($tail, $string2)">
                <xsl:value-of select="$string2"/>
                <xsl:call-template name="substring-before-last">
                    <xsl:with-param name="string1" select="$tail"/>
                    <xsl:with-param name="string2" select="$string2"/>
                </xsl:call-template>
            </xsl:if>
        </xsl:if>
    </xsl:template>


    <xsl:template match="exist:collection" mode="children">
        <xsl:param name="path"/>
        <!-- <exist:collection created=​"2010-12-06T16:​47:​21.652+01:​00" group=​"betterFORM" name=​"/​db/​betterform/​apps/​forms/​demo" owner=​"betterFORM" permissions=​"rwur-ur-u">​ -->
        <!-- <directory created="">/forms/demo</directory> -->
        <xsl:element name="directory">
            <xsl:attribute name="created">
                <xsl:value-of select="substring-before(@created, 'T')"/>
            </xsl:attribute>
            <xsl:attribute name="absolute-path">
                <xsl:value-of select="concat($path, '/', @name)"/>
            </xsl:attribute>
            <xsl:value-of select="@name"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="exist:resource" mode="children">
        <xsl:param name="path"/>
        <!-- <exist:resource created=​"2010-12-06T16:​47:​22.299+01:​00" group=​"betterFORM" last-modified=​"2010-12-06T16:​47:​55.744+01:​00" name=​"atom.xhtml" owner=​"betterFORM" permissions=​"rwur-ur-u">​</exist:resource>​ -->
        <!-- <file last-modified="" absolute-path="">atom.xhtml<file> -->
        <xsl:element name="file">
            <xsl:attribute name="last-modified">
                <xsl:value-of select="concat( substring-before(@last-modified, 'T') , ' ' ,substring-after( substring-before(@last-modified, '.'), 'T') )"/>
            </xsl:attribute>
            <xsl:attribute name="absolute-path">
                <xsl:value-of select="concat($path, '/', @name)"/>
            </xsl:attribute>
            <xsl:value-of select="@name"/>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>