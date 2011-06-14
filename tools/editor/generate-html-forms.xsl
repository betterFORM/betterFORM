<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xf ev xsd" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:output method="xml" indent="yes"/>
    <xsl:output method="xhtml" indent="yes" name="xhtml" exclude-result-prefixes="xf"/>
    <!-- author: Joern Turner -->

    <xsl:template match="/">
        <div>
            <div class="propertyTitle">Attributes</div>
            <form method="post" action="#" enctype="application/x-www-form-urlencoded">
            <xsl:for-each select="//xf:bind">
                <ul style="list-style-type:none;">
                    <li>
                        <div class="attrEditor">
                            <h4><label for="{@nodeset}"><xsl:value-of select="@nodeset"/></label></h4>
                            <p>hint text here</p>
                            <input id="{@nodeset}" name="{@nodeset}" type="text" value="" placeholder=""/>
                        </div>
                    </li>
                </ul>
            </xsl:for-each>
            </form>
        </div>
    </xsl:template>


</xsl:stylesheet>
