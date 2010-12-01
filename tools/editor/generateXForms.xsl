<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
    <xsl:output method="xhtml" indent="yes" name="xhtml" exclude-result-prefixes="xf"/>
    <!-- author: Joern Turner -->

    <xsl:param name="webxml.path" select="''"/>

    <xsl:variable name="inputDoc" select="/"/>
    <xsl:template match="/xsd:schema">
        <div>
            <xsl:for-each select="xsd:element[@name]">
                <xsl:variable name="current" select="."/>
                <xsl:result-document href="./target/{@name}.xhtml" format="xhtml" >
                    <html xmlns:xf="http://www.w3.org/2002/xforms">
                        <head>
                            <title></title>
                        </head>
                        <body>
                            <div id="xforms">
                                <div style="display:none;">
                                    <!-- put model(s) here -->
                                    <xf:model id="formdef">
                                        <xf:instance xmlns="">
                                            <data>
                                                <xfElement>
                                                    <xsl:attribute name="type"><xsl:value-of select="@name"/></xsl:attribute>
                                                    <xsl:apply-templates select="$current//xsd:attributeGroup" mode="instance"/>
                                                    <xsl:apply-templates select="$current//xsd:attribute" mode="instance"/>
                                                </xfElement>
                                            </data>
                                        </xf:instance>
                                        <xsl:apply-templates select="$current//xsd:attributeGroup" mode="bind"/>
                                        <xsl:apply-templates select="$current//xsd:attribute" mode="bind"/>

                                    </xf:model>
                                </div>
                                <xf:group ref="xfElement" id="properties" appearance="bf:verticalTable">
                                    <xsl:apply-templates select="$current//xsd:attributeGroup" mode="ui"/>
                                    <xsl:apply-templates select="$current//xsd:attribute" mode="ui"/>
                                </xf:group>
                            </div>
                        </body>
                    </html>
                </xsl:result-document>
            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@ref]" mode="instance">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetGroup" mode="instance"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name]" mode="instance">
        <xsl:apply-templates select="xsd:attribute" mode="instance"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@name]" mode="instance">
        <xsl:attribute name="{@name}">
            <xsl:if test="@default">
                <xsl:value-of select="@default"/>    
            </xsl:if>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="xsd:attribute[@ref]" mode="instance">
        <xsl:variable name="targetAttribute" select="//xsd:attribute[@name = substring-after(@ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetAttribute" mode="instance"/>
    </xsl:template>

    <xsl:template match="*|@*|node()" mode="instance"/>

    <xsl:template match="xsd:attributeGroup[@ref]" mode="bind">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetGroup" mode="bind"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name]" mode="bind">
        <xsl:apply-templates select="xsd:attribute" mode="bind"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@name]" mode="bind">
        <xf:bind nodeset="{@name}" type="{@type}"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@ref]" mode="bind">
        <xsl:variable name="targetAttribute" select="//xsd:attribute[@name = substring-after(@ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetAttribute" mode="bind"/>
    </xsl:template>

    <xsl:template match="*|@*|node()" mode="bind"/>


    <xsl:template match="xsd:attributeGroup[@ref]" mode="ui">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetGroup" mode="ui"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name]" mode="ui">
        <xsl:apply-templates select="xsd:attribute" mode="ui"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@name]" mode="ui">
        <xf:input ref="@{@name}">
            <xf:label><xsl:value-of select="./@name"/></xf:label>
        </xf:input>
    </xsl:template>

    <xsl:template match="xsd:attribute[@ref]" mode="ui">
        <xsl:variable name="targetAttribute" select="//xsd:attribute[@name = substring-after(@ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetAttribute"/>
    </xsl:template>





</xsl:stylesheet>
