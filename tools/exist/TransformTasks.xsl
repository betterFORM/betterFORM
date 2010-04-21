<xsl:stylesheet version="2.0"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:saxon="http://saxon.sf.net/"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml"
                omit-xml-declaration="yes"
                indent="yes"/>

    <xsl:strip-space elements="*"/>


    <xsl:param name="resultDir" select="''"/>
    <xsl:param name="filename"></xsl:param>
    <xsl:param name="filedir">.</xsl:param>


    <xsl:template match="/">


        <xsl:variable name="creationTime" select="concat(substring(//task/created,1,4),substring(//task/created,6,2),substring(//task/created,9,2),'-',substring(//task/created,12,2),substring(//task/created,15,2),substring(//task/created,18,2))"/>
        <xsl:variable name="fileNameDate" select="substring-before($filename,'.xml')"/>

        <xsl:message>Current file is <xsl:value-of select="$fileNameDate"/> in directory <xsl:value-of select="$filedir"/>.</xsl:message>

        <xsl:variable name="result">
            <xsl:apply-templates>
                <xsl:with-param name="dateFromFilename" select="$fileNameDate"/>
                <xsl:with-param name="dateFromCreated" select="$creationTime "/>
            </xsl:apply-templates>
        </xsl:variable>

        <xsl:message>Creation Time: <xsl:value-of select="$creationTime"/></xsl:message>
        <xsl:variable name="taskFileName">
            <xsl:choose>
                <xsl:when test="$creationTime ='-'"><xsl:value-of select="concat($fileNameDate ,'00.xml')"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="concat($creationTime,'.xml')"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>


        <xsl:result-document href="{$resultDir}/{$taskFileName}" encoding="UTF-8">
               <xsl:copy-of select="$result"/>
        </xsl:result-document>

        <xsl:copy-of select="$result"/>

    </xsl:template>


    <xsl:template match="created">
        <xsl:param name="dateFromFilename" select="''"/>
        <xsl:param name="dateFromCreated" select="''"/>
        <xsl:copy><xsl:value-of select="$dateFromCreated"/></xsl:copy>
    </xsl:template>

    <xsl:template match="task">
        <xsl:param name="dateFromFilename" select="''"/>
        <xsl:param name="dateFromCreated" select="''"/>
        <xsl:copy>
            <xsl:apply-templates>
                <xsl:with-param name="dateFromFilename" select="$dateFromFilename"/>
                <xsl:with-param name="dateFromCreated" select="$dateFromCreated"/>
            </xsl:apply-templates>
            <xsl:if test="not(exists(created))">
                <xsl:element name="created"><xsl:value-of select="concat($dateFromFilename,'00')"/></xsl:element>
            </xsl:if>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="data|project|start|end|duration|who|what|note|billable|billded|status">
        <xsl:param name="dateFromFilename" select="''"/>
        <xsl:param name="dateFromCreated" select="''"/>
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates>
                <xsl:with-param name="dateFromFilename" select="$dateFromFilename"/>
                <xsl:with-param name="dateFromCreated" select="$dateFromCreated"/>
            </xsl:apply-templates>

        </xsl:copy>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:param name="dateFromFilename" select="''"/>
        <xsl:param name="dateFromCreated" select="''"/>
        <xsl:copy>
            <xsl:apply-templates select="node()|@*">
                <xsl:with-param name="dateFromFilename" select="$dateFromFilename"/>
                <xsl:with-param name="dateFromCreated" select="$dateFromCreated"/>
            </xsl:apply-templates>
        </xsl:copy>
    </xsl:template>
    
</xsl:stylesheet>
