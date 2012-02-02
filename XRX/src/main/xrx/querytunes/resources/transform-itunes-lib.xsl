<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
    <xsl:output method="xml" version="1.0" encoding="UTF-8" indent="yes"/>
    <xsl:preserve-space elements="*"/>
    <xsl:template match="/">
        <xsl:variable name="iTunesLibTransformationStep1">
            <tracks>
                <xsl:for-each select="/*/*/dict[1]/dict">
                    <xsl:choose>
                        <xsl:when test="child::*[preceding-sibling::* = 'Kind'][1] = 'MPEG audio file' and child::*[preceding-sibling::* = 'Genre'][1] != 'Podcast'">
                            <xsl:call-template name="track"/>
                        </xsl:when>
                        <xsl:otherwise/>
                    </xsl:choose>
                </xsl:for-each>
            </tracks>
        </xsl:variable>
        <library>
            <xsl:for-each-group select="$iTunesLibTransformationStep1//track" group-by="album">
                <album>
                    <xsl:attribute name="id" select="generate-id()"/>
                    <xsl:element name="artist">
                        <xsl:value-of select="normalize-space(current-group()[1]//artist[1]/text())"/>
                    </xsl:element>
                    <xsl:element name="name">
                        <xsl:value-of select="normalize-space(current-grouping-key())"/>
                    </xsl:element>
                    <xsl:element name="genre">
                        <xsl:value-of select="normalize-space(current-group()[1]//genre[1]/text())"/>
                    </xsl:element>
                    <xsl:for-each select="current-group()">
                        <track>
                            <xsl:copy-of select="@*"/>
                            <xsl:value-of select="name"/>
                        </track>
                    </xsl:for-each>
                </album>
            </xsl:for-each-group>
        </library>
    </xsl:template>
    <xsl:template name="track">
        <xsl:element name="track">
            <xsl:variable name="album" select="child::*[preceding-sibling::* = 'Album'][1]"/>
            <xsl:variable name="albumName" select="concat(upper-case(substring($album,1,1)),substring($album,2, string-length($album)-1))"/>


            <xsl:attribute name="id" select="child::*[preceding-sibling::* = 'Track ID'][1]"/>
            <xsl:attribute name="time" select="child::*[preceding-sibling::* = 'Total Time'][1]"/>
            <xsl:attribute name="size" select="child::*[preceding-sibling::* = 'Size'][1]"/>
            <xsl:attribute name="trackNumber" select="child::*[preceding-sibling::* = 'Track Number'][1]"/>
            <xsl:attribute name="trackCount" select="child::*[preceding-sibling::* = 'Track Count'][1]"/>
            <xsl:attribute name="discNumber" select="child::*[preceding-sibling::* = 'Disc Number'][1]"/>
            <xsl:attribute name="discCount" select="child::*[preceding-sibling::* = 'Disc Count'][1]"/>
            <xsl:attribute name="year" select="child::*[preceding-sibling::* = 'Year'][1]"/>
            <xsl:attribute name="dateModified" select="child::*[preceding-sibling::* = 'Date Modified'][1]"/>
            <xsl:attribute name="dateAdded" select="child::*[preceding-sibling::* = 'Date Added'][1]"/>
            <xsl:attribute name="bitRate" select="child::*[preceding-sibling::* = 'Bit Rate'][1]"/>
            <xsl:attribute name="sampleRate" select="child::*[preceding-sibling::* = 'Sample Rate'][1]"/>
            <xsl:attribute name="playCount" select="child::*[preceding-sibling::* = 'Play Count'][1]"/>
            <xsl:attribute name="playDate" select="child::*[preceding-sibling::* = 'Play Date'][1]"/>
            <xsl:attribute name="kind" select="child::*[preceding-sibling::* = 'Kind'][1]"/>

            <!--
            <xsl:value-of select="child::*[preceding-sibling::* = 'Normalization']" />
            <xsl:value-of select="child::*[preceding-sibling::* = 'File Type']" />
            <xsl:value-of select="child::*[preceding-sibling::* = 'File Creator']" />
            <xsl:value-of select="child::*[preceding-sibling::* = 'File Folder Count']" />
            <xsl:value-of select="child::*[preceding-sibling::* = 'Library Folder Count']" />
            -->
            <name>
                <xsl:value-of select="child::*[preceding-sibling::* = 'Name'][1]"/>
            </name>
            <artist>
                <xsl:value-of select="child::*[preceding-sibling::* = 'Artist'][1]"/>
            </artist>

            <album>
                <xsl:value-of select="$albumName"/>
                <xsl:message select="$albumName"/>
            </album>
            <genre>
                <xsl:value-of select="child::*[preceding-sibling::* = 'Genre'][1]"/>
            </genre>
            <kind>
                <xsl:value-of select="child::*[preceding-sibling::* = 'Kind'][1]"/>
            </kind>
        </xsl:element>
    </xsl:template>
</xsl:stylesheet>