<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->
<!-- TODO:
        - create empty instance see Input

-->
<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xpath-default-namespace="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="#all">

    <xsl:param name="webContext" select="'../..'"/>

    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" media-type="text/xml"/>

    <xsl:preserve-space elements="code"/>

    <xsl:template match="/">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="head">
        <xsl:copy>
            <xsl:apply-templates/>
            <meta http-equiv="Content-Type" content="text/xml; charset=UTF-8"/>
            <link rel="stylesheet" type="text/css" href="../reference/reference.css"/>
        </xsl:copy>
    </xsl:template>


    <xsl:template match="title">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="body">
        <xsl:copy>
            <xsl:attribute name="class" select="if(exists(@class)) then @class else 'soria'"/>
            <xsl:attribute name="style">margin:30px</xsl:attribute>

            <div id="xforms">
                <!-- the xforms model here -->
                <xsl:apply-templates select="div[@class='sample']/div[@class='markup']/xf:model"/>
                <xsl:apply-templates select="div[@class='sample']/div[@class='markup']/code/xf:model"/>

                <div class="pageintro">
                    <h1>
                        <xsl:value-of select="//title"/>
                    </h1>

                </div>
                <!--
                here the xforms code gets integrated into the page within a hidden div. Sections within <code class="ui">
                are
                 -->
                <xsl:apply-templates select="div[@class='sample']"/>

            </div>
            <script type="text/javascript">
                dojo.subscribe("/xf/ready", function(){
                    fluxProcessor.skipshutdown=true;
                });
            </script>

        </xsl:copy>
    </xsl:template>


    <xsl:template match="div[@class='sample']">
        <xsl:apply-templates select="div[@class='markup']"/>
        <xsl:apply-templates select="div[@class='markup']/following-sibling::*"/>
    </xsl:template>


    <xsl:template match="div[@class='sample']/div[@class='markup']/xf:model">
        <div style="display:none">
            <xsl:apply-templates select="." mode="xforms"/>
        </div>
    </xsl:template>

    <xsl:template match="div[@class='sample']/div[@class='markup']/code/xf:model">
        <div style="display:none">
            <xsl:apply-templates select="." mode="xforms"/>
        </div>
    </xsl:template>

    <xsl:template match="code[@class='ui']">

        <xsl:variable name="ref" select="./xf:*[1]/@ref[1]"/>
        <xsl:variable name="refparent" select="substring-before($ref, '/')"/>
        <xsl:variable name="refchild" select="substring-after($ref, '/')"/>


        <div class="Section sample">
            <xsl:copy-of select="."/>
        </div>

        <div class="Section sample">
            <xf:group appearance="full">
                <xf:label>Test send and set value</xf:label>
                <xf:trigger>
                    <xf:label>setvalue</xf:label>
                    <xf:setvalue ref="{$ref}" value="'new value'"/>
                </xf:trigger>
                <xf:output ref="{$ref}">
                    <xf:label>verify sent value</xf:label>
                </xf:output>
            </xf:group>
        </div>
        <div class="Section sample">
            <xf:group appearance="full">
                <xf:label>Test creation of controls</xf:label>
                <xf:repeat nodeset="{$refparent}[position() &gt; 1]" appearance="full">
                    <xf:input ref="{$refchild}" incremental="true" >
                        <xf:label>a text</xf:label>
                        <xf:hint>Hint for this input</xf:hint>
                        <xf:help>help for input1</xf:help>
                        <xf:alert>invalid</xf:alert>
                    </xf:input>
                </xf:repeat>

                <xf:trigger>
                    <xf:label>Insert</xf:label>
                    <xf:insert nodeset="{$refparent}" at="last()" position="before"/>
                </xf:trigger>
                <xf:trigger>
                    <xf:label>Delete</xf:label>
                    <xf:message if="count({$refparent}) = 1">nodeset '<xsl:value-of select="$refparent"/>' is empty</xf:message>
                    <xf:delete nodeset="{$refparent}" at="last()" if="count({$refparent}) &gt; 1"/>
                </xf:trigger>
            </xf:group>
        </div>
    </xsl:template>


    <xsl:template match="*|@*|text()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*|@*|text()|comment()" mode="xforms">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="xforms"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="code" mode="xforms" priority="10">
        <xsl:apply-templates mode="xforms"/>
    </xsl:template>

    <xsl:template match="*" mode="escape" priority="10">
        <xsl:text>&lt;</xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:apply-templates mode="escape" select="@*"/>
        <xsl:text>&gt;</xsl:text>
        <xsl:apply-templates mode="escape"/>
        <xsl:text>&lt;/</xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>&gt;</xsl:text>
    </xsl:template>

    <xsl:template match="@*" mode="escape" priority="10">
        <xsl:text> </xsl:text>
        <xsl:value-of select="name()"/>
        <xsl:text>="</xsl:text>
        <xsl:value-of select="."/>
        <xsl:text>"</xsl:text>
    </xsl:template>

</xsl:stylesheet>
