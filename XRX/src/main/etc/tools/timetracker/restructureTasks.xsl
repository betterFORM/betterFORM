<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"      
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xmlns:bf="http://betterform.sourceforge.net/xforms"    
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    exclude-result-prefixes="xf xsi ev bf xs"
    version="2.0">
    <xsl:output indent="yes" method="xml"/>

    <xsl:variable name="projects" select="document('main/xrx/timetracker/data/project.xml')"/>


    <xsl:template match="data">
        <data>
            <xsl:apply-templates/>
        </data>        
    </xsl:template>
    
    <xsl:template match="task">
        <task>
            <xsl:variable name="currentProject" select="project"/>
            <xsl:variable name="projectId" select="$projects/data/project[.=$currentProject]/@id"/>
            <xsl:attribute name="client" select="$projectId"/>
            <xsl:attribute name="project" select="'1'"/>
            <xsl:attribute name="iteration" select="'1'"/>

            <xsl:message>Project <xsl:value-of select="$currentProject"/> </xsl:message>
            <xsl:message>Project Id: <xsl:value-of select="$projectId"/> </xsl:message>
            <xsl:if test="string-length($projectId) = 0">
                <xsl:message terminate="yes">ERROR: Project Id missing</xsl:message>
            </xsl:if>
            <date><xsl:value-of select="date"/></date>
            <start><xsl:value-of select="start"/></start>
            <end><xsl:value-of select="end"/></end>
            <duration>
                <xsl:attribute name="hours" select="duration/@hours"/>
                <xsl:attribute name="minutes" select="duration/@minutes"/>
                <xsl:value-of select="duration"/>
            </duration>
            <who><xsl:value-of select="who"/></who>
            <what><xsl:value-of select="what"/></what>
            <note><xsl:value-of select="note"/></note>
            <billable><xsl:value-of select="billable"/></billable>
            <billed date="">
                <xsl:attribute name="date"><xsl:value-of select="billed/@date"/></xsl:attribute>
                <xsl:value-of select="billed"/>
            </billed>
            <status><xsl:value-of select="status"/></status>
            <created><xsl:value-of select="created"/></created>
            <tag/>
        </task>
    </xsl:template>
</xsl:stylesheet>