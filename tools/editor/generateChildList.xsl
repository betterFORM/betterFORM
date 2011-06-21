<xsl:stylesheet version="2.0"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" exclude-result-prefixes="xf"/>
    <xsl:strip-space elements="*"/>
    <!-- author: Joern Turner -->
    <!--todo: prototypes -->
    <xsl:param name="webxml.path" select="''"/>

    <xsl:key name="names" match="xsd:element" use="@name"/>

    <xsl:variable name="inputDoc" select="/"/>
    <xsl:template match="/xsd:schema">
        <data>
            <ul id="document">
                <li>model</li>
                <li>group</li>
                <li>repeat</li>
            </ul>
            <xsl:for-each select=".//xsd:element[@name][generate-id() = generate-id(key('names',@name)[1])]">
                <xsl:variable name="current" select="."/>
                <ul id="{@name}">
                    <xsl:apply-templates select="."/>
                </ul>
            </xsl:for-each>
        </data>
    </xsl:template>

    <xsl:template match="xsd:complexType">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="xsd:sequence">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="xsd:choice">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="xsd:group[@ref]">
        <xsl:variable name="grp" select="substring-after(@ref,':')"/>
        <xsl:apply-templates select="//xsd:group[@name=$grp]"/>
    </xsl:template>

    <xsl:template match="xsd:group[@name='Action']">
        <xsl:for-each select="./*/*/xsd:element">
            <li>
                <xsl:value-of select="substring-after(@ref,':')"/>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="xsd:element[@name='submission']">
        <xsl:for-each select="./*/*/*/xsd:element">
            <li>
                <xsl:value-of select="@name"/>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="xsd:element[@ref]">
        <li>
            <xsl:value-of select="substring-after(@ref,':')"/>
        </li>
    </xsl:template>

    <xsl:template match="text()"/>




</xsl:stylesheet>
