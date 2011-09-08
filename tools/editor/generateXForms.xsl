<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
<!--
    <xsl:output method="xhtml" indent="yes" name="xhtml" doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
        doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />
-->
    <xsl:output method="xhtml" indent="yes" name="xhtml" exclude-result-prefixes="xf"/>
    <!-- author: Joern Turner -->
    <!-- author: Lars Windauer -->

    <xsl:param name="webxml.path" select="''"/>
    <xsl:variable name="inputDoc" select="/"/>

    <xsl:variable name="lang" select="'en'" as="xs:string"/>

    <xsl:template match="/xsd:schema">
        <div>
            <xsl:variable name="unique-list" select="//xsd:element[@name][not(@name = preceding::xsd:element/@name)]" />
            <xsl:for-each select="$unique-list">

                <xsl:variable name="current" select="."/>
                    <!--<xsl:message>create document <xsl:value-of select="@name"/></xsl:message>-->
                    <xsl:result-document href="./target/xforms/{@name}.xhtml" format="xhtml" >
                        <html xmlns:xf="http://www.w3.org/2002/xforms">
                            <head>
                                <title><xsl:value-of select="@name"/></title>
                        </head>
                        <body>
                            <div id="xforms">
                                <xsl:choose>
                                    <xsl:when test="@type = 'xforms:ValueTemplate'">
                                        <xsl:if test="@type = 'xforms:ValueTemplate'">
                                            <xsl:message>Type is 'xforms:ValueTemplate' <xsl:value-of select="@name"/> </xsl:message>
                                        </xsl:if>
                                        <div style="display:none;">
                                            <!-- put model(s) here -->
                                            <xf:model id="formdef">
                                                <xf:instance xmlns="">
                                                   <data>
                                                      <xfElement type="{@name}" value="">
                                                         <textcontent/>
                                                      </xfElement>
                                                   </data>
                                                </xf:instance>
                                                <xf:bind nodeset="@value" type="xforms:XPathExpression"/>
                                                <xf:bind nodeset="textcontent" type=""/>
                                             </xf:model>
                                          </div>
                                          <xf:group xmlns:xf="http://www.w3.org/2002/xforms" ref="xfElement" id="properties"
                                                    appearance="bf:verticalTable">
                                             <xf:input ref="@value">
                                                <xf:label>XPath Value</xf:label>
                                             </xf:input>
                                             <xf:input ref="@textcontent">
                                                <xf:label>Text Value</xf:label>
                                             </xf:input>
                                          </xf:group>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <div style="display:none;">
                                            <!-- put model(s) here -->
                                            <xf:model id="formdef">
                                                <xf:instance xmlns="">
                                                    <data>
                                                        <xfElement>
                                                            <xsl:attribute name="type"><xsl:value-of select="@name"/></xsl:attribute>
                                                            <xsl:apply-templates select="$current//xsd:attributeGroup" mode="instance"/>
                                                            <xsl:apply-templates select="$current//xsd:attribute" mode="instance"/>
                                                            <xsl:if test="exists(.//xsd:attributeGroup[@ref='xforms:XML.Events'])">
                                                                <xml-event>
                                                                    <xsl:apply-templates select="$current//xsd:attributeGroup" mode="event-instance"/>
                                                                </xml-event>
                                                            </xsl:if>
                                                            <xsl:apply-templates select="$current//xsd:complexType[@mixed='true']" mode="instance"/>
                                                        </xfElement>
                                                    </data>
                                                </xf:instance>
                                                <xsl:apply-templates select="$current//xsd:attributeGroup" mode="bind"/>
                                                <xsl:apply-templates select="$current//xsd:attribute" mode="bind"/>
                                                <xsl:apply-templates select="$current//xsd:complexType[@mixed='true']" mode="bind"/>
                                                <xsl:if test="exists(.//xsd:attributeGroup[@ref='xforms:XML.Events'])">
                                                    <xf:bind nodeset="xml-event">
                                                        <xsl:apply-templates select="$current//xsd:attributeGroup" mode="event-bind"/>
                                                    </xf:bind>
                                                </xsl:if>
                                            </xf:model>
                                            <xf:group ref="xfElement" id="properties" appearance="bf:verticalTable">
                                                <xsl:apply-templates select="$current//xsd:attributeGroup" mode="ui"/>
                                                <xsl:apply-templates select="$current//xsd:attribute" mode="ui"/>
                                                <xsl:apply-templates select="$current//xsd:complexType[@mixed='true']" mode="ui"/>
                                                <xsl:if test="exists(.//xsd:attributeGroup[@ref='xforms:XML.Events'])">
                                                    <xf:group id="event-properties" appearance="bf:verticalTable" ref="xfElement/xml-events">
                                                        <xsl:apply-templates select="$current//xsd:attributeGroup" mode="event-ui">
                                                            <xsl:with-param name="current" select="$current"/>
                                                        </xsl:apply-templates>
                                                    </xf:group>
                                                </xsl:if>
                                            </xf:group>
                                        </div>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </div>
                        </body>
                    </html>
                </xsl:result-document>
            </xsl:for-each>
        </div>
    </xsl:template>

    <!--################################### Mode instance ################################### -->
    <!--################################### Mode instance ################################### -->
    <!--################################### Mode instance ################################### -->
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

    <xsl:template match="xsd:complexType[@mixed='true']" mode="instance">
        <textcontent/>

    </xsl:template>

    <xsl:template match="*|@*|node()" mode="instance"/>


    <!--################################### Mode event-instance ################################### -->
    <!--################################### Mode event-instance ################################### -->
    <!--################################### Mode event-instance ################################### -->

    <xsl:template match="xsd:attributeGroup[@ref='xforms:XML.Events']" mode="event-instance" priority="10">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetGroup" mode="event-instance"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name='XML.Events']" mode="event-instance" priority="10">
        <xsl:apply-templates select="xsd:attribute" mode="event-instance"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[substring-before(@ref,':') = 'ev']" mode="event-instance" priority="10">
        <xsl:variable name="attrName"><xsl:value-of select="substring-after(@ref,':')"/></xsl:variable>

        <!--<xsl:message><xsl:value-of select="@ref"/></xsl:message>-->
        <xsl:variable name="eventXSD" select="document('xml-events-attribs-1.xsd')/xsd:schema"/>
        <xsl:variable name="defaultValue"><xsl:value-of select="$eventXSD//*[@name=$attrName]/@default"/></xsl:variable>
        <xsl:attribute name="{$attrName}"><xsl:value-of select="$defaultValue"/></xsl:attribute>

    </xsl:template>

    <!--################################### Mode event-bind ################################### -->
    <!--################################### Mode event-bind ################################### -->
    <!--################################### Mode event-bind ################################### -->
    <xsl:template match="xsd:attributeGroup[@ref='xforms:XML.Events']" mode="event-bind" priority="10">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
            <xsl:apply-templates select="$targetGroup" mode="event-bind"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name='XML.Events']" mode="event-bind" priority="10">
        <xsl:apply-templates select="xsd:attribute" mode="event-bind"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[substring-before(@ref,':') = 'ev']" mode="event-bind" priority="10">
        <xsl:variable name="attrName"><xsl:value-of select="substring-after(@ref,':')"/></xsl:variable>

        <!--<xsl:message><xsl:value-of select="@ref"/></xsl:message>-->
        <xsl:variable name="eventXSD" select="document('xml-events-attribs-1.xsd')/xsd:schema"/>
        <xsl:variable name="typeAttr"><xsl:value-of select="if(string-length($eventXSD//*[@name=$attrName]/@type) != 0) then $eventXSD//*[@name=$attrName]/@type else 'string'"/></xsl:variable>
        <xf:bind nodeset="@{$attrName}" type="{$typeAttr}"/>

    </xsl:template>

    <!--################################### Mode event-ui ################################### -->
    <!--################################### Mode event-ui ################################### -->
    <!--################################### Mode event-ui ################################### -->
    <xsl:template match="xsd:attributeGroup[@ref='xforms:XML.Events']" mode="event-ui" priority="10">
        <xsl:param name="current"/>

        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
            <xsl:apply-templates select="$targetGroup" mode="event-ui">
                <xsl:with-param name="current" select="$current"/>
            </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name='XML.Events']" mode="event-ui" priority="10">
        <xsl:param name="current"/>
        <xsl:apply-templates select="xsd:attribute" mode="event-ui">
            <xsl:with-param name="current" select="$current"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="xsd:attribute[@ref = 'ev:event']" mode="event-ui" priority="20">
        <xsl:param name="current"/>
        <xsl:variable name="attrName"><xsl:value-of select="substring-after(@ref,':')"/></xsl:variable>

        <!--<xsl:message><xsl:value-of select="@ref"/></xsl:message>-->
        <xsl:variable name="eventXSD" select="document('xml-events-attribs-1.xsd')/xsd:schema"/>
        <xsl:variable name="typeAttr"><xsl:value-of select="if(string-length($eventXSD//*[@name=$attrName]/@type) != 0) then $eventXSD//*[@name=$attrName]/@type else 'string'"/></xsl:variable>
        <xf:select1 ref="@event" appearance="minimal">
            <xf:label>Event</xf:label>
            <xf:hint>The type of event to listen for</xf:hint>
            <!-- build item list from external eventTargets.xml file. -->
            <xsl:variable name="eventTargets" select="document('resources/eventTargets.xml')"/>
            <xsl:for-each select="$eventTargets/data/target[contains(@match,$current/@name)]/event">
                <xf:item>
                    <xf:label><xsl:value-of select="./@name"/></xf:label>
                    <xf:value><xsl:value-of select="./@name"/></xf:value>
                </xf:item>
            </xsl:for-each>
        </xf:select1>

    </xsl:template>

    <xsl:template match="xsd:attribute[substring-before(@ref ,':') = 'ev']" mode="event-ui" priority="10">
        <!--<xsl:message>other:<xsl:value-of select="./@ref"/></xsl:message>-->
        <xsl:variable name="eventXSD" select="document('xml-events-attribs-1.xsd')/xsd:schema"/>

        <!--<xsl:message><xsl:value-of select="$eventXSD//xsd:attribute[@name = substring-after(@ref,'ev:')]/@name"/></xsl:message>-->
        <!--<xsl:message><xsl:value-of select="substring-after(@ref,'ev:')"/></xsl:message>-->
        <xsl:variable name="targetAttribute" select="$eventXSD//xsd:attribute[@name = substring-after(@ref,'ev:')]"/>
        <!--<xsl:message>targetAttribute: <xsl:value-of select="$targetAttribute"/></xsl:message>-->

        <!--<xsl:apply-templates select="$eventXSD/xs:schema/xs:attribute[@name = substring-after(@ref,'ev:')]" mode="event-ui"/>-->
        <xf:input ref="@{@ref}">
            <xf:label><xsl:value-of select="./@ref"/></xf:label>
        </xf:input>

    </xsl:template>

    <!--################################### Mode bind ################################### -->
    <!--################################### Mode bind ################################### -->
    <!--################################### Mode bind ################################### -->

    <xsl:template match="xsd:attributeGroup[@ref]" mode="bind">
        <xsl:variable name="ref" select="@ref"/>
        <xsl:variable name="targetGroup" select="//*[@name = substring-after($ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetGroup" mode="bind"/>
    </xsl:template>

    <xsl:template match="xsd:attributeGroup[@name]" mode="bind">
        <xsl:apply-templates select="xsd:attribute" mode="bind"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@name]" mode="bind">
        <xf:bind nodeset="@{@name}" type="{@type}"/>
    </xsl:template>

    <xsl:template match="xsd:attribute[@ref]" mode="bind">
        <xsl:variable name="targetAttribute" select="//xsd:attribute[@name = substring-after(@ref,'xforms:')]"/>
        <xsl:apply-templates select="$targetAttribute" mode="bind"/>
    </xsl:template>

    <xsl:template match="xsd:complexType[@mixed='true']" mode="bind">
        <xf:bind nodeset="textcontent" type=""/>
<!--
        <xf:input ref="@textcontent">
            <xf:label>textcontent</xf:label>
        </xf:input>
-->
    </xsl:template>

    <xsl:template match="*|@*|node()" mode="bind"/>


    <!--################################### Mode ui ################################### -->
    <!--################################### Mode ui ################################### -->
    <!--################################### Mode ui ################################### -->

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

    <xsl:template match="xsd:complexType[@mixed='true']" mode="ui">
        <xf:input ref="@textcontent">
            <xf:label>textcontent</xf:label>
        </xf:input>
    </xsl:template>
</xsl:stylesheet>
