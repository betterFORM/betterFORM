<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="#all">
    <xsl:output method="xhtml" indent="yes"/>
    <!-- author: Joern Turner -->

    <xsl:template match="/">
        <xf:copy>
            <head>
                <title>Properties Editor</title>
            </head>
            <body>
                <div id="propertyEditor">
                    <div style="display:none">
                        <xf:model id="m-propertyEditor">

                            <xf:instance id="i-properties">
                                <data xmlns="">
                                </data>
                            </xf:instance>

                            <xf:instance id="i-templates">
                                <data xmlns="">
                                    <!--
                                    process only leaves - the groupings in propertyOrder are ignored
                                    -->
                                    <xsl:for-each select="//property[not(*)]">
                                        <xsl:element name="{./@name}"></xsl:element>
                                    </xsl:for-each>
                                </data>
                            </xf:instance>

                        </xf:model>
                    </div>
                    <xf:group>

                        <!--
                        process only leaves - the groupings in propertyOrder are ignored
                        -->
                        <xsl:for-each select="//property[not(*)]">
                            <xf:input ref="{./@name}">
                                <xf:label><xsl:value-of select="./@name"/></xf:label>
                            </xf:input>
                        </xsl:for-each>

                        <xf:trigger>
                            <xf:label>insert</xf:label>
                            <xf:insert nodeset="two" origin="instance('two')/two"/>
                        </xf:trigger>
                    </xf:group>
                </div>
            </body>
        </xf:copy>
    </xsl:template>


    <xsl:template match="*|@*|text()|comment()" priority="1">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
