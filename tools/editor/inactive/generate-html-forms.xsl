<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
    <xsl:output method="xhtml" indent="yes" name="xhtml" exclude-result-prefixes="xf"/>
    <!-- author: Joern Turner -->
    <!-- author: Lars Windauer -->
    <xsl:variable name="dictionary" select="document('resources/dictionary.xml')"/>
    <xsl:variable name="lang" select="'en'"/>


    <xsl:template match="/">
        <div>
            <xsl:variable name="htmlTitle" select="//html:title"/>
            <xsl:variable name="title">
                <xsl:choose>
                    <xsl:when test="exists($dictionary//lang[@id=$lang]/key[@name=$htmlTitle]/label)"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$htmlTitle]/label"/></xsl:when>
                    <xsl:otherwise><xsl:value-of select="$htmlTitle"/></xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="propertyTitle"><xsl:value-of select="concat('Property Sheet ',$title)"/></xsl:variable>
            <!--<xsl:message><xsl:value-of select="$propertyTitle"/></xsl:message>-->
            <div class="propertyTitle"><xsl:value-of select="$propertyTitle"/></div>
            <xsl:if test="exists($dictionary//lang[@id=$lang]/key[@name=$title]/hint)">
                <div class="propertyHint"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$title]/hint"/></div>
            </xsl:if>
            <xsl:if test="exists($dictionary//lang[@id=$lang]/key[@name=$title]/help)">
                <div class="propertyHelp" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$title]/help"/></div>
            </xsl:if>
            <xsl:if test="exists($dictionary//lang[@id=$lang]/key[@name=$title]/alert)">
                <div class="propertyAlert" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$title]/alert"/></div>
            </xsl:if>
            <form method="post" action="#" enctype="application/x-www-form-urlencoded">
                <xsl:apply-templates select="//xf:model/xf:bind"/>
            </form>
        </div>
    </xsl:template>

    <xsl:template match="xf:bind[exists(xf:bind)]" priority="5">
        <div dojotype="dijit.TitlePane" open="false" title="{@id}">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="xf:bind[not(exists(xf:bind)) and not(exists(@nodeset))]" priority="1">
        <xsl:message terminate="yes">found generation error, wrong bind: <xsl:value-of select="@id"/></xsl:message>
    </xsl:template>

    <xsl:template match="xf:bind[exists(@nodeset) and not(exists(xf:bind))]" priority="5">
        <xsl:variable name="attrName">
            <xsl:choose>
                <xsl:when test="starts-with(@nodeset,'@')">
                    <xsl:value-of select="substring-after(@nodeset,'@')"/>
                </xsl:when>
                <xsl:otherwise><xsl:value-of select="@nodeset"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--<xsl:message>attrName is: <xsl:value-of select="$attrName"/> </xsl:message>-->
        <xsl:variable name="dictKey" select="concat($attrName,'Attribute')"/>
        <xsl:variable name="title">
            <xsl:choose>
                <xsl:when test="exists($dictionary//lang[@id=$lang]/key[@name=$dictKey]/label)"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/label"/></xsl:when>
                <xsl:otherwise><xsl:value-of select="$attrName"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <ul style="list-style-type:none;">
            <li>
                <div class="attrEditor">
                    <h4><label for="{$attrName}"><xsl:value-of select="$title"/></label></h4>

                    <xsl:if test="exists($dictionary//lang[@id=$lang]/key[@name=$dictKey]/hint)">
                        <p><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/hint"/></p>
                    </xsl:if>
                    <xsl:if test="exists($dictionary//lang[@id=$lang]/key[@name=$dictKey]/help)">
                        <span class="attrHelp" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/help"/></span>
                    </xsl:if>
                    <xsl:if test="exists($dictionary//lang[@id=$lang]/key[@name=$dictKey]/alert)">
                        <span class="attrAlert" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/alert"/></span>
                    </xsl:if>

                    <xsl:choose>
                         <xsl:when test="$attrName='xml-event'">
                             <!-- do nothing -->
                         </xsl:when>
                         <xsl:when test="$attrName='event'">
                             <select dojoType="dijit.form.FilteringSelect" selection="open" searchAttr="name" name="type" id="{$attrName}" value="" placeholder="" class="xf{$attrName} dojoSelect" >
                                 <xsl:attribute name="onblur">attrEditor.saveProperty(dojo.attr(dojo.byId('xfMount'),'data-bf-currentid'),'<xsl:value-of select="$attrName"/>')</xsl:attribute>
                                 <xsl:for-each select="//xf:select1[@ref='@event']/xf:item">
                                    <option value="{xf:value}"><xsl:value-of select="xf:label"/></option>
                                 </xsl:for-each>
                             </select>
                         </xsl:when>
                        <xsl:when test="$attrName='type'">
                            <input dojoType="dijit.form.FilteringSelect" store="stateStore" selection="open" searchAttr="name" name="type" id="{$attrName}" value="" placeholder="" class="xf{$attrName} dojoSelect">
                                <xsl:attribute name="onblur">attrEditor.saveProperty(dojo.attr(dojo.byId('xfMount'),'data-bf-currentid'),'<xsl:value-of select="$attrName"/>')</xsl:attribute>
                            </input>
                        </xsl:when>
                        <xsl:otherwise>
                            <input dojoType="dijit.form.TextBox" id="{$attrName}" name="{$attrName}" type="text" value="" placeholder="" class="xf{$attrName} dojoInput">
                                <xsl:attribute name="onblur">attrEditor.saveProperty(dojo.attr(dojo.byId('xfMount'),'data-bf-currentid'),'<xsl:value-of select="$attrName"/>')</xsl:attribute>
                            </input>
                        </xsl:otherwise>
                    </xsl:choose>
                </div>
            </li>
        </ul>
    </xsl:template>


</xsl:stylesheet>
