<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
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
    
    <xsl:template match="xf:group[@id='properties']" priority="10">
        <xf:group ref="xfElement" id="properties" appearance="bf:verticalTable">
            <xsl:apply-templates select="$propertyOrder/order/*" mode="sortProperties">
                <xsl:with-param name="group" select="."/>
            </xsl:apply-templates>
        </xf:group>        
    </xsl:template>
    
    <xsl:template match="property[exists(property)]" mode="sortProperties" priority="10">        
        <xsl:param name="group"/>
        <xsl:message>found group <xsl:value-of select="@name"/></xsl:message>
        <xf:group id="{@name}">
            <xsl:apply-templates mode="sortProperties">
                <xsl:with-param name="group" select="$group"/>
            </xsl:apply-templates>
        </xf:group>
    </xsl:template>

    <xsl:template match="property" mode="sortProperties">        
        <xsl:param name="group"/>
        <!-- <xsl:message>search for properties on group <xsl:value-of select="$group/@id"/></xsl:message> -->        
        <xsl:variable name="propertyName" select="concat('@',@name)"/>        
        <!-- <xsl:message>search for property <xsl:value-of select="$propertyName"/></xsl:message> -->
        <xsl:if test="exists($group//*[@ref=$propertyName])">            
            <xsl:copy-of select="$group//*[@ref=$propertyName]"/>
        </xsl:if>
            
    </xsl:template>

    


</xsl:stylesheet>
