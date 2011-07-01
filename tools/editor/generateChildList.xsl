<xsl:stylesheet version="2.0"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes" exclude-result-prefixes="xf"/>
    <xsl:strip-space elements="*"/>
    <!-- author: Joern Turner
    builds a unique list of ul elements. There will be one ul element for each xforms element name.
    Possible children are listed as li elements. This styleheets is used as a base for other transforms and
    should be changed only with caution!
     -->
    <!--todo: prototypes -->
    <xsl:param name="webxml.path" select="''"/>

    <xsl:key name="names" match="xsd:element" use="@name"/>

    <xsl:variable name="inputDoc" select="/"/>
    <xsl:template match="/xsd:schema">
        <data>
            <ul id="document" data-xf-type="document">
                <li data-xf-type="model" class="element">model</li>
                <li data-xf-type="group" class="container">group</li>
                <li data-xf-type="repeat" class="container">repeat</li>
                <li data-xf-type="switch" class="container">switch</li>
            </ul>
            <xsl:for-each select=".//xsd:element[@name][generate-id() = generate-id(key('names',@name)[1])]">
                <xsl:variable name="current" select="."/>
                <ul data-xf-type="{@name}" id="{@name}">
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

    <xsl:template match="xsd:group[@name='Core.Form.Controls']">
        <xsl:for-each select="./*/xsd:element">
            <li data-xf-type="{substring-after(@ref,':')}" class="controls">
                <xsl:value-of select="substring-after(@ref,':')"/>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="xsd:group[@name='Action']">
        <xsl:for-each select="./*/*/xsd:element">
            <li data-xf-type="{substring-after(@ref,':')}" class="action">
                <xsl:value-of select="substring-after(@ref,':')"/>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="xsd:group[@name='UI.Common']">
        <xsl:for-each select="./*/*/xsd:element">
            <li data-xf-type="{substring-after(@ref,':')}" class="common">
                <xsl:value-of select="substring-after(@ref,':')"/>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="xsd:group[@name='Container.Form.Controls']">
        <xsl:for-each select="./*/xsd:element">
            <li data-xf-type="{substring-after(@ref,':')}" class="container">
                <xsl:value-of select="substring-after(@ref,':')"/>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="xsd:element[@name='submission']" priority="20">
        <xsl:for-each select="./*/*/*/xsd:element">
            <li data-xf-type="{@name}" class="submission">
                <xsl:value-of select="@name"/>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="xsd:element[@ref]">
        <li data-xf-type="{substring-after(@ref,':')}" class="element">
            <xsl:value-of select="substring-after(@ref,':')"/>
        </li>
    </xsl:template>

    <xsl:template match="xsd:element[substring-after(@ref,':')='label']" priority="10">
        <li data-xf-type="{substring-after(@ref,':')}" class="common">
            <xsl:value-of select="substring-after(@ref,':')"/>
        </li>
    </xsl:template>

    <xsl:template match="xsd:element[@name='load']">
        <xsl:for-each select="./*/*/xsd:element">
            <li data-xf-type="{@name}">
                <xsl:value-of select="@name"/>
            </li>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="text()"/>




</xsl:stylesheet>
