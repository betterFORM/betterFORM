<xsl:stylesheet version="2.0"                
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bfFunction="http://www.betterform.de/Functions"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="#all">
    <xsl:output method="xml" indent="yes"/>    
    <!-- author: Joern Turner -->
    <!-- author: Lars Windauer -->


    <xsl:template match="*|@*|text()|comment()" priority="1">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="order" priority="10">
        <dictionary>
            <lang id="en">               
                <xsl:apply-templates/>
            </lang>
        </dictionary>
    </xsl:template>
    
    <!-- gruops are not present in dictionary yet -->
    <xsl:template match="property[exists(property)]" priority="10"><xsl:apply-templates/></xsl:template>


    <xsl:template match="property" priority="5">
        <xsl:variable name="uppercasedName" select="bfFunction:start-uppercase(@name)"/>
        <key name="{concat(@name,'Attribute')}">
            <label><xsl:value-of select="$uppercasedName"/></label>
            <hint><xsl:value-of select="$uppercasedName"/>  hint.</hint>
            <help><xsl:value-of select="$uppercasedName"/>  help</help>
            <alert><xsl:value-of select="$uppercasedName"/> alert</alert>
        </key>

    </xsl:template>

    <xsl:function name="bfFunction:start-uppercase" as="xs:string?">
        <xsl:param name="arg" as="xs:string?"/>
        <xsl:sequence select="concat(upper-case(substring($arg,1,1)),substring($arg,2))"/>
    </xsl:function>


</xsl:stylesheet>
