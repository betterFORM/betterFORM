<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="#all">
    <xsl:output method="xml" indent="yes"/>    
    <!-- author: Joern Turner -->
    <!-- author: Lars Windauer -->


    <xsl:variable name="propertyOrder" select="document('resources/propertyOrder.xml')"/>
    
    <xsl:template match="*|@*|text()|comment()" priority="1">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xf:model" priority="10">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:copy-of select="*[not(local-name()='bind')]"/>
            <xsl:apply-templates select="$propertyOrder/order/*" mode="sortProperties">
                <xsl:with-param name="model" select="."/>
            </xsl:apply-templates>

        </xsl:copy>
    </xsl:template>

    <xsl:template match="property[exists(property)]" mode="sortProperties" priority="10">
        <xsl:param name="model"/>
        <xsl:variable name="childOfGroupAvailable">
            <xsl:for-each select="property">
                <xsl:variable name="propertyName" select="concat('@',@name)"/>
                <xsl:if test="exists($model//*[@nodeset=$propertyName])">true</xsl:if>
            </xsl:for-each>
        </xsl:variable>
        <!--<xsl:message>childOfGroupAvailable: <xsl:value-of select="$childOfGroupAvailable"/></xsl:message>-->

        <xsl:if test="starts-with($childOfGroupAvailable,'true')">
            <xf:bind id="{@name}" type="group">
                <xsl:apply-templates mode="sortProperties">
                    <xsl:with-param name="model" select="$model"/>
                </xsl:apply-templates>
            </xf:bind>
        </xsl:if>
    </xsl:template>

    <xsl:template match="property" mode="sortProperties">
        <xsl:param name="model"/>
        <!--<xsl:message>search for properties on model <xsl:value-of select="$model/@id"/></xsl:message>-->
        <xsl:variable name="propertyName" select="concat('@',@name)"/>        
        <!--<xsl:message>search for property <xsl:value-of select="$propertyName"/></xsl:message>-->
        <xsl:choose>
            <xsl:when test="exists($model//*[@nodeset=$propertyName])">
                <xsl:copy-of select="$model//*[@nodeset=$propertyName]"/>
            </xsl:when>
            <xsl:otherwise>
                <!--<xsl:message>could not find elem <xsl:value-of select="$propertyName"/></xsl:message>-->
            </xsl:otherwise>

        </xsl:choose>

    </xsl:template>

    


</xsl:stylesheet>
