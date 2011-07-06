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
            <xsl:message><xsl:value-of select="$propertyTitle"/></xsl:message>
            <div class="propertyTitle"><xsl:value-of select="$propertyTitle"/></div>
            <div class="propertyHint"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$title]/hint"/></div>
            <div class="propertyHelp" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$title]/help"/></div>
            <div class="propertyAlert" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$title]/alert"/></div>

            <form method="post" action="#" enctype="application/x-www-form-urlencoded">
            <xsl:for-each select="//xf:model/xf:bind">
                <xsl:variable name="attrName">
                    <xsl:choose>
                        <xsl:when test="starts-with(@nodeset,'@')">
                            <xsl:value-of select="substring-after(@nodeset,'@')"/>
                        </xsl:when>
                        <xsl:otherwise><xsl:value-of select="@nodeset"/></xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>

                <xsl:message>attrName is: <xsl:value-of select="$attrName"/> </xsl:message>
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
                            <xsl:choose>
                                 <xsl:when test="$attrName='xml-event'"></xsl:when>
                                 <xsl:when test="$attrName='event'">
                                     <h4><label for="{$attrName}"><xsl:value-of select="$title"/></label></h4>
                                     <p><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/hint"/></p>
                                     <span class="attrHelp" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/help"/></span>
                                     <span class="attrAlert" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/alert"/></span>
                                     <select dojoType="dijit.form.FilteringSelect" selection="open" searchAttr="name" name="type" id="{$attrName}" value="" placeholder="" class="xf{$attrName} dojoSelect" >
                                         <xsl:attribute name="onblur">attrEditor.saveProperty(dojo.attr(dojo.byId('xfMount'),'xfId'),'<xsl:value-of select="$attrName"/>')</xsl:attribute>
                                         <xsl:for-each select="//xf:select1[@ref='@event']/xf:item">
                                            <option value="{xf:value}"><xsl:value-of select="xf:label"/></option>
                                         </xsl:for-each>
                                     </select>
                                 </xsl:when>
                                <xsl:when test="$attrName='type'">
                                    <h4><label for="{$attrName}"><xsl:value-of select="$title"/></label></h4>
                                    <p><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/hint"/></p>
                                    <span class="attrHelp" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/help"/></span>
                                    <span class="attrAlert" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/alert"/></span>

                                    <input dojoType="dijit.form.FilteringSelect" store="stateStore" selection="open" searchAttr="name" name="type" id="{$attrName}" value="" placeholder="" class="xf{$attrName} dojoSelect">
                                        <xsl:attribute name="onblur">attrEditor.saveProperty(dojo.attr(dojo.byId('xfMount'),'xfId'),'<xsl:value-of select="$attrName"/>')</xsl:attribute>
                                    </input>
                                </xsl:when>
                                <xsl:otherwise>
                                    <h4><label for="{$attrName}"><xsl:value-of select="$title"/></label></h4>
                                    <p><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/hint"/></p>
                                    <span class="attrHelp" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/help"/></span>
                                    <span class="attrAlert" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/alert"/></span>

                                    <input dojoType="dijit.form.TextBox" id="{$attrName}" name="{$attrName}" type="text" value="" placeholder="" class="xf{$attrName} dojoInput">
                                        <xsl:attribute name="onblur">attrEditor.saveProperty(dojo.attr(dojo.byId('xfMount'),'xfId'),'<xsl:value-of select="$attrName"/>')</xsl:attribute>
                                    </input>
                                </xsl:otherwise>
                            </xsl:choose>
                        </div>
                    </li>
                </ul>
            </xsl:for-each>
            <xsl:if test="exists(//xf:model/xf:bind[@nodeset='xml-event']/*)">
                <div dojotype="dijit.TitlePane" open="false" title="Event Details">
                    <xsl:for-each select="//xf:model/xf:bind[@nodeset='xml-event']/*">
                        <xsl:variable name="attrName">
                            <xsl:value-of select="substring-after(@nodeset,'@')"/>
                        </xsl:variable>
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
                                    <p><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/hint"/></p>
                                    <span class="attrHelp" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/help"/></span>
                                    <span class="attrAlert" style="display:none"><xsl:value-of select="$dictionary//lang[@id=$lang]/key[@name=$dictKey]/alert"/></span>
                                    <xsl:choose>
                                         <xsl:when test="$attrName='event'">
                                             <select dojoType="dijit.form.FilteringSelect" selection="open" searchAttr="name" name="type" id="{$attrName}" value="" placeholder="" class="xf{$attrName} dojoSelect" >
                                                 <xsl:attribute name="onblur">attrEditor.saveProperty(dojo.attr(dojo.byId('xfMount'),'xfId'),'<xsl:value-of select="$attrName"/>')</xsl:attribute>
                                                 <xsl:for-each select="//xf:select1[@ref='@event']/xf:item">
                                                    <option value="{xf:value}"><xsl:value-of select="xf:label"/></option>
                                                 </xsl:for-each>
                                             </select>
                                         </xsl:when>
                                        <xsl:otherwise>
                                            <input dojoType="dijit.form.TextBox" id="{$attrName}" name="{$attrName}" type="text" value="" placeholder="" class="xf{$attrName} dojoInput">
                                                <xsl:attribute name="onblur">attrEditor.saveProperty(dojo.attr(dojo.byId('xfMount'),'xfId'),'<xsl:value-of select="$attrName"/>')</xsl:attribute>
                                            </input>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </div>
                            </li>
                        </ul>
                    </xsl:for-each>
                </div>
            </xsl:if>
            </form>
        </div>
    </xsl:template>


</xsl:stylesheet>
