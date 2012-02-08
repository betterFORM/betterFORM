<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        version="2.0"
        xmlns:ts="http://www.w3c.org/MarkUp/Forms/XForms/Test/11"
        exclude-result-prefixes="xsl">

    <xsl:param name="basePath"></xsl:param>
    <xsl:variable name="searchPath" select="concat($basePath,'/classes')"/>


    <xsl:variable name="junitResultFileStart" select="'TEST-de.betterform.conformance.xf11.'"/>
    <xsl:variable name="junitResultFileStartChapt" select="concat($junitResultFileStart, 'chapt')"/>

    <xsl:output method="xml" indent="yes"/>

    <!--
        author: Joern Turner
        This stylesheet converts junit report results into w3c result format. It takes the document produced
        by combineW3CResults.xsl as input (a merge of all result xml files in the original Testsuite) as input
        and updates the testCaseStatus and testCaseDate from the results of a Selenium junit run.

        DOES ONLY DO NORMATIVE TEST REPORTING!
    -->

    <xsl:template match="/">
        <testsuites>
            <xsl:for-each select="//ts:testSuite">

                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </testsuites>
    </xsl:template>


    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>


    <xsl:template match="ts:testSuite">
        <xsl:copy><xsl:apply-templates/></xsl:copy>
    </xsl:template>



    <xsl:template match="ts:specChapter">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ts:statusSummary">
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ts:normativeOnly">
        <xsl:copy>true</xsl:copy>
    </xsl:template>

    <!-- IGNORE NON-NORMATIVE TESTS AMD FILTER THEN OUT -->
    <xsl:template match="ts:testCase/ts:testCaseNormative[.='false']"/>

    <xsl:template match="ts:testCase/ts:testCaseNormative[.='true']">
        <xsl:copy><xsl:apply-templates/></xsl:copy>
    </xsl:template>
         
    <!-- TEST-de.betterform.conformance.xf11.appendix.XF_appendix_b_7_a_Test.xml -->
     
    <xsl:template match="ts:testCaseStatus">


        <xsl:variable name="chapterNum">
            <xsl:variable name="num" select="substring-before(../../../ts:specChapter/@chapterName,'.')"/>
			
            <xsl:choose>
                <xsl:when test="string-length($num) = 1"><xsl:value-of select="concat('0',$num)"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$num"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="fullPathToResult">
            <xsl:choose>
                <xsl:when test="starts-with($chapterNum,'0B') or starts-with($chapterNum,'0H') or starts-with($chapterNum,'0G')">
                    <xsl:variable name="testCaseFileTmp" select="replace(../ts:testCaseName,'\.','_')"/>
                    <xsl:variable name="testCaseFile" select="concat(lower-case(substring($testCaseFileTmp,1,1)),substring($testCaseFileTmp,2))"/>
                    <xsl:value-of select="concat($searchPath,'/',$junitResultFileStart,'appendix.XF_appendix_',$testCaseFile,'_Test.xml')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="testCaseFile" select="replace(../ts:testCaseName,'\.','_')"/>
                   <xsl:value-of select="concat($searchPath,'/',$junitResultFileStartChapt,$chapterNum,'.XF_chapt',$chapterNum,'_',$testCaseFile,'_Test.xml')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--<xsl:value-of select="$fullPathToResult"/>-->

		<xsl:message>fullPath: <xsl:value-of select="$fullPathToResult"/></xsl:message>
		<xsl:message>chapterNumber:<xsl:value-of select="$chapterNum"/></xsl:message>
        <xsl:variable name="statusFromJunit">
           <xsl:variable name="errors" select="document($fullPathToResult)/testsuite/@errors"/>
           <xsl:variable name="failures" select="document($fullPathToResult)/testsuite/@failures"/>
            <!--<xsl:variable name="manual" select="document($fullPathToResult)/testsuite/system-out = 'manual test successful'"/>-->
           <xsl:choose>
               <!--<xsl:when test="($errors + $failures) = 0 and $manual = true()">Passed Manual</xsl:when>-->
               <xsl:when test="($errors + $failures) = 0">Passed</xsl:when>
               <xsl:when test="($errors + $failures) &gt; 0">Failed</xsl:when>
               <xsl:otherwise>Unknown</xsl:otherwise>
           </xsl:choose>
        </xsl:variable>

        <xsl:copy>
            <xsl:value-of select="$statusFromJunit"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ts:testCaseNote">
        <xsl:variable name="chapterNum">
            <xsl:variable name="num" select="substring-before(../../../ts:specChapter/@chapterName,'.')"/>

            <xsl:choose>
                <xsl:when test="string-length($num) = 1"><xsl:value-of select="concat('0',$num)"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$num"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="fullPathToResult">
            <xsl:choose>
                <xsl:when test="starts-with($chapterNum,'0B') or starts-with($chapterNum,'0H') or starts-with($chapterNum,'0G')">
                    <xsl:variable name="testCaseFileTmp" select="replace(../ts:testCaseName,'\.','_')"/>
                    <xsl:variable name="testCaseFile" select="concat(lower-case(substring($testCaseFileTmp,1,1)),substring($testCaseFileTmp,2))"/>
                    <xsl:value-of select="concat($searchPath,'/',$junitResultFileStart,'appendix.XF_appendix_',$testCaseFile,'_Test.xml')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:variable name="testCaseFile" select="replace(../ts:testCaseName,'\.','_')"/>
                   <xsl:value-of select="concat($searchPath,'/',$junitResultFileStartChapt,$chapterNum,'.XF_chapt',$chapterNum,'_',$testCaseFile,'_Test.xml')"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:copy>
            <xsl:value-of select="document($fullPathToResult)/testsuite/system-out/text()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ts:testCaseDate">
        <xsl:copy><xsl:value-of select="current-date()"/></xsl:copy>
    </xsl:template>


</xsl:stylesheet>
