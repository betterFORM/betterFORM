<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xpath-default-namespace="">
    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" media-type="text/xml"/>

    <xsl:strip-space elements="*"/>

    <xsl:variable name="samples" select="//samples"/>
    <xsl:variable name="models" select="//models"/>

    <xsl:template match="/">
        <html>
            <xsl:apply-templates/>
        </html>
    </xsl:template>
    
    <xsl:template match="title">
            <head>
                <xsl:apply-templates select="." mode="title"/>
            </head>    
    </xsl:template>

    <xsl:template match="title" mode="title">
        <title><xsl:value-of select="."/></title>
        <style type="text/css">
            .xfGroupLabel {
            font-size: 12pt;
            font-weight: bold;
            padding-bottom: 10px;
            }

            #mips .xfTrigger .dijitButtonContents {
            width: 200px;
            }
        </style>


        <link rel="stylesheet" type="text/css"
              href="../../bfResources/scripts/release/dojo/dojox/highlight/resources/highlight.css"/>
        <link rel="stylesheet" type="text/css" href="../../reference/reference.css"/>

        <script type="text/javascript">
            dojo.require("dojox.highlight");
            dojo.require("dojox.highlight.languages.xml");
            dojo.addOnLoad(function() {
            dojo.query("code").forEach(dojox.highlight.init);
            });

            dojo.require("dijit.form.Button");
            dojo.require("dijit.TitlePane");
            dojo.require('dijit.layout.ContentPane');
        </script>
    </xsl:template>

    <xsl:template match="page">
        <body class="soria" style="margin:30px;">
            <div id="xforms">
                <div style="display:none;">
                    <xsl:apply-templates mode="model"/>
                </div>
            </div>
            <xsl:apply-templates mode="ui"/>
        </body>
    </xsl:template>

    <xsl:template match="content" mode="model">
        <xsl:choose>
            <xsl:when test="string($models)">
                <!-- <xsl:copy-of select="./models/*"/> -->
                <xsl:apply-templates select="$models" mode="modifyModel"/>

            </xsl:when>
            <xsl:otherwise>
                <xf:model>
                    <xf:instance>
                        <data xmlns="">
                               <xsl:apply-templates select="$samples" mode="instance"/>
                        </data>
                    </xf:instance>
                    <xsl:apply-templates select="$samples" mode="bind"/>
                </xf:model>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>



    <!--
    ######################################################################################################
                Mode instance
    ######################################################################################################
    -->
    <xsl:template match="xf:*" mode="instance" priority="20">
        <xsl:variable name="name" select="./@ref"/>
        <xsl:variable name="value" select="./@value"/>

        <xsl:element name="{$name}" namespace="">
            <xsl:attribute name="constraint">true</xsl:attribute>
            <xsl:attribute name="readonly">true</xsl:attribute>
            <xsl:attribute name="required">true</xsl:attribute>
            <xsl:attribute name="relevant">true</xsl:attribute>
            <xsl:element name="value" namespace=""><xsl:value-of select="$value"/></xsl:element>                        
        </xsl:element>
    </xsl:template>

    <xsl:template match="text()" mode="instance"/>

    <!--
    ######################################################################################################
                Mode bind
    ######################################################################################################
    -->
    <xsl:template match="xf:*" mode="bind" priority="10">
        <xsl:variable name="datatype">
            <xsl:choose>
                <xsl:when test="exists(./@datatype)">
                    <xsl:value-of select="./@datatype"/>
                </xsl:when>
                <xsl:otherwise>string</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xf:bind nodeset="{./@ref}">
            <xf:bind nodeset="value"
                     constraint="boolean-from-string(../@constraint)"
                     readonly="boolean-from-string(../@readonly)"
                     required="boolean-from-string(../@required)"
                     relevant="boolean-from-string(../@relevant)"
                     type="{$datatype}"/>

            <xf:bind nodeset="@constraint" type="boolean"/>
            <xf:bind nodeset="@readonly" type="boolean"/>
            <xf:bind nodeset="@required" type="boolean"/>
            <xf:bind nodeset="@relevant" type="boolean"/>
        </xf:bind>
    </xsl:template>

    <xsl:template match="xf:model" mode="modifyModel" priority="10">
        <xf:model id="{./@id}">
            <xsl:apply-templates mode="modifyInstance"/>
            <xsl:apply-templates select="$samples" mode="bind"/>
        </xf:model>
    </xsl:template>


    <xsl:template match="xf:instance" mode="modifyInstance" priority="10">
        <xf:instance id="{./@id}" xmlns="">
            <xsl:apply-templates mode="modifyData"/>
        </xf:instance>
    </xsl:template>

    <xsl:template match="*" mode="modifyData" priority="10">
        <xsl:variable name="name" select="name(.)"/>
        <xsl:variable name="constraint">
            <xsl:choose>
                <xsl:when test="exists(./@constraint)">
                    <xsl:value-of select="./@constraint"/>
                </xsl:when>
                <xsl:otherwise>true</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="readonly">
            <xsl:choose>
                <xsl:when test="exists(./@readonly)">
                    <xsl:value-of select="./@readonly"/>
                </xsl:when>
                <xsl:otherwise>true</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="required">
            <xsl:choose>
                <xsl:when test="exists(./@required)">
                    <xsl:value-of select="./@required"/>
                </xsl:when>
                <xsl:otherwise>true</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="relevant">
            <xsl:choose>
                <xsl:when test="exists(./@relevant)">
                    <xsl:value-of select="./@relevant"/>
                </xsl:when>
                <xsl:otherwise>true</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>


        <xsl:element name="{$name}" namespace="">
            <xsl:attribute name="constraint"><xsl:value-of select="$constraint"/></xsl:attribute>
            <xsl:attribute name="readonly"><xsl:value-of select="$readonly"/></xsl:attribute>
            <xsl:attribute name="required"><xsl:value-of select="$required"/></xsl:attribute>
            <xsl:attribute name="relevant"><xsl:value-of select="$relevant"/></xsl:attribute>
            <xsl:apply-templates mode="modifyData"/>
        </xsl:element>
    </xsl:template>
    <!--
    ######################################################################################################
                Mode ui
    ######################################################################################################
    -->
    <xsl:template match="title" mode="ui">
        <div class="pagetitle"><xsl:value-of select="."/></div>
    </xsl:template>

    <xsl:template match="description" mode="ui">
        <div class="Section">
            <div class="PageDescription">Description</div>
            <xsl:value-of select="."/>
        </div>
    </xsl:template>

    <xsl:template match="specification" mode="ui">
        <xsl:variable name="link" select="./link"/>
        <xsl:variable name="description" select="./description"/>
        <div class="Section">
            <div class="PageDescription"><a href="{$link}"><xsl:value-of select="$description"/></a>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="section" mode="ui">
            <div class="Section">
               <xsl:apply-templates select="." mode="section"/>
            </div>
    </xsl:template>

    <xsl:template match="section" mode="section">
        <div class="Headline"><xsl:value-of select="./title"/></div>


    <p class="Description"><xsl:value-of select="./description"/></p>
    <div>
        <div class="Subheadline">XForms Markup</div>
        <div class="Subsection">
<pre><code class="xml">!!!TODO!!!
    <!--    
    <xsl:apply-templates mode="codesectioninstance"/>
    <xsl:apply-templates mode="codesectionbind"/>
    <xsl:comment>XForms UI</xsl:comment>
    <xsl:apply-templates mode="codesectionxforms"/>
    -->
</code></pre>

        </div>
    </div>
         <div>
            <div class="Subheadline">Sample</div>
            <div class="Sample">
                <xsl:apply-templates mode="samplesection"/>
            </div>

            <div class="Subheadline">Modelitem properties</div>
            <div class="Sample">
                 <xsl:apply-templates mode="modelitemsection"/>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="xf:*" mode="codesectioninstance" priority="10">
        <xsl:if test="string(./@value)">
            <xsl:comment>XForms Instance</xsl:comment>
            <item><xsl:value-of select="./@value"/></item>
        </xsl:if>
    </xsl:template>

    <xsl:template match="xf:*" mode="codesectionbind" priority="10">
        <xsl:variable name="nodeset" select="./@ref"/>
        <xsl:variable name="datatype" select="./@datatype"/>

        <xsl:if test="string($datatype)">
            <xsl:comment>XForms Bind</xsl:comment>
            <xf:bind nodeset="{$nodeset}" type="{$datatype}"/>
        </xsl:if>
    </xsl:template>

    <xsl:template match="xf:*" mode="codesectionxforms" priority="10">
        <xsl:variable name="ref" select="./@ref"/>
        <xsl:variable name="name" select="name(.)"/>
        <xsl:variable name="text" select="text()"/>

        <xsl:choose>
            <xsl:when test="string($models)">
                <xsl:copy-of select="."/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="{$name}">
                    <xsl:if test="string($ref)">
                        <xsl:attribute name="ref" select="concat($ref, '/value')"/>
                        <xsl:attribute name="incremental" select="'true'"/>
                    </xsl:if>
                    <xsl:if test="string($text)">
                        <xsl:value-of select="$text"/>
                    </xsl:if>
                    <xsl:apply-templates mode="codesectionxforms"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="xf:*" mode="samplesection" priority="10">
        <xsl:variable name="ref" select="./@ref"/>
        <xsl:variable name="name" select="name(.)"/>
        <xsl:variable name="text" select="text()"/>


        <xsl:choose>
            <xsl:when test="string($models)">
                <xsl:copy-of select="."/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:element name="{$name}">
                    <xsl:if test="string($ref)">
                        <xsl:attribute name="ref" select="concat($ref, '/value')"/>
                        <xsl:attribute name="incremental" select="'true'"/>
                    </xsl:if>
                    <xsl:if test="string($text)">
                        <xsl:value-of select="$text"/>
                    </xsl:if>
                    <xsl:apply-templates mode="samplesection"/>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="xf:*[@ref]" mode="modelitemsection" priority="10">
        <xsl:variable name="ref" select="./@ref"/>
        <xf:input navindex="-1" style="width:10px;" ref="{$ref}/@readonly" class="mips"
                  incremental="true">
            <xf:label>readonly</xf:label>
        </xf:input>
        <xf:input navindex="-1" ref="{$ref}/@required" style="width:10px;" class="mips"
                  incremental="true">
            <xf:label>required</xf:label>
        </xf:input>
        <xf:input navindex="-1" ref="{$ref}/@relevant" style="width:10px;" class="mips"
                  incremental="true">
            <xf:label>relevant</xf:label>
        </xf:input>
        <xf:input navindex="-1" ref="{$ref}/@constraint" style="width:10px;" class="mips"
                  incremental="true">
            <xf:label>valid</xf:label>
        </xf:input>
    </xsl:template>

    <xsl:template match="text()" mode="ui"/>
    <xsl:template match="text()" mode="section"/>
    <xsl:template match="text()" mode="codesectioninstance"/>
    <xsl:template match="text()" mode="codesectionbind"/> 
    <xsl:template match="text()" mode="codesectionxforms"/>
    <xsl:template match="text()" mode="samplesection"/>
    <xsl:template match="text()" mode="modelitemsection"/>
    <xsl:template match="text()" mode="bind"/>
    <xsl:template match="text()" mode="modelbind"/>
</xsl:stylesheet>
