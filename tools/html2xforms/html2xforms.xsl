<?xml version="1.0" encoding="UTF-8"?>
<!--
~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
~ Licensed under the terms of BSD License
-->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xi="http://www.w3.org/2001/XInclude"
                xmlns:bfc="http://betterform.sourceforge.net/xforms/controls"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                exclude-result-prefixes="bf xsl">

    <!-- 'data' will be passed in case we deal with a html form submit and second layer validation -->
    <xsl:param name="data" select="'record:foo;trackedDate:bar;created:heute;project:mine;duration:3;'"/>
    <!--<xsl:param name="data" select="''"/>-->

    <xsl:output method="xhtml" omit-xml-declaration="yes"/>
    <!--
    Transforms sanitized HTML5 documents into into xforms elements.
    -->
    <xsl:strip-space elements="*"/>
    <xsl:template match="/*">
        <xsl:copy>
            <xsl:namespace name="xf" select="'http://www.w3.org/2002/xforms'"/>
            <xsl:copy-of select="@*"/>

            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="body">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates select="*" mode="model"/>
            <xsl:if test="string-length($data) = 0">
                <xsl:apply-templates select="*" mode="ui"/>
            </xsl:if>
        </xsl:copy>
    </xsl:template>
    <!--
        ###############################################################################################
        mode model
        ###############################################################################################
    -->
    
    <xsl:template match="form[@id]" mode="model">
        <xsl:variable name="form-id" select="@id"/>
        
        <xsl:element name="xf:model" namespace="http://www.w3.org/2002/xforms">
            <xsl:attribute name="id">m-<xsl:value-of select="$form-id"/></xsl:attribute>
            
            <xsl:element name="xf:instance" namespace="http://www.w3.org/2002/xforms">
                <xsl:attribute name="id">i-default</xsl:attribute>
                <xsl:element name="data" namespace="">
                    <xsl:apply-templates select="*" mode="model"/>
                </xsl:element>
            </xsl:element>

            <xsl:variable name="this" select="."/>
            <xsl:for-each select="//*[@list]">
                <xsl:variable name="instance-id" select="@list"/>
                <xsl:variable name="options" select="$this//datalist[@id eq $instance-id]"/>

                <xsl:element name="xf:instance" namespace="http://www.w3.org/2002/xforms">
                    <xsl:attribute name="id">i-<xsl:value-of select="$instance-id"/></xsl:attribute>
                    <xsl:element name="data" namespace="">
                        <xsl:for-each select="$options/option">
                            <xsl:element name="option" namespace="">
                                <xsl:value-of select="@value"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:element>
            </xsl:for-each>

            <xsl:for-each select="//select[option]">
                <xsl:variable name="instance-id" select="@name"/>

                <xsl:element name="xf:instance" namespace="http://www.w3.org/2002/xforms">
                    <xsl:attribute name="id">i-<xsl:value-of select="$instance-id"/></xsl:attribute>
                    <xsl:element name="data" namespace="">
                        <xsl:for-each select="option">
                            <xsl:element name="option" namespace="">
                                <xsl:value-of select="@value"/>
                            </xsl:element>
                        </xsl:for-each>
                    </xsl:element>
                </xsl:element>
            </xsl:for-each>

            <xsl:apply-templates select="*" mode="bind"/>
            
            <xsl:element name="xf:submision" namespace="http://www.w3.org/2002/xforms">
                <xsl:attribute name="id">s-<xsl:value-of select="@id"/>-default</xsl:attribute>
                <xsl:attribute name="resource">
                    <xsl:value-of select="@action"/>
                </xsl:attribute>
                <xsl:attribute name="method">
                    <xsl:variable name="method" select="if(exists(//form/@method)) then @method else 'GET'"/>
                    <xsl:value-of select="$method"/>
                </xsl:attribute>
                <xsl:attribute name="validate">true()</xsl:attribute>
            </xsl:element>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="*[@name]" mode="model">
        <xsl:element name="{@name}" namespace="">

            <xsl:if test="string-length($data) != 0">
                <xsl:variable name="name" select="@name" />
                <xsl:variable name="formData" select="tokenize($data, ';')" />
                <xsl:variable name="theValue">
                    <xsl:for-each select="$formData">
                        <xsl:if test="starts-with(.,$name)">
                            <xsl:variable name="varname" select="substring-before(.,':')"/>
                            <xsl:if test="$varname = $name">
                                <xsl:variable name="varValue" select="substring-after(.,':')"/>
                                <xsl:value-of select="$varValue"/>
                            </xsl:if>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:if test="string-length($theValue) != 0">
                    <xsl:value-of select="$theValue"/>
                </xsl:if>
            </xsl:if>

            <xsl:apply-templates select="*" mode="model"/>
        </xsl:element>
    </xsl:template>

    <xsl:template match="text()" mode="model" priority="10"/>
    <xsl:template match="label" mode="model" priority="10"/>
    <xsl:template match="option" mode="model" priority="10"/>


    <!--
    ###############################################################################################
    mode submission
    ###############################################################################################
    -->
    
    <xsl:template match="*[@type='submit']" mode="submission" priority="10">
        <xsl:choose>
            <xsl:when test="string-length($data) != 0"/>
            <xsl:otherwise>
                <xsl:element name="xf:submision" namespace="http://www.w3.org/2002/xforms">
                    <!-- ### just uses the first forms' action as submission uri -->
                    <!-- todo: support multiple forms -->
                    <xsl:attribute name="id">
                        <xsl:value-of select="concat(@id,'-submit')"/>
                    </xsl:attribute>
                    <xsl:attribute name="resource">
                        <xsl:value-of select="//form/@action"/>
                    </xsl:attribute>
                    <xsl:attribute name="method">
                        <xsl:variable name="method" select="if(exists(//form/@method)) then //form/@method else 'GET'"/>
                        <xsl:value-of select="$method"/>
                    </xsl:attribute>
                </xsl:element>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!--
    ###############################################################################################
    mode bind
    ###############################################################################################
    -->

    <xsl:template match="text()" mode="bind" priority="10"/>
    <xsl:template match="label" mode="bind" priority="10"/>
    <xsl:template match="option" mode="bind" priority="10"/>

    <xsl:template match="*[@name]" mode="bind">
        <xsl:element name="xf:bind" namespace="http://www.w3.org/2002/xforms">
            <xsl:attribute name="ref">
                <xsl:value-of select="@name"/>
            </xsl:attribute>

            <xsl:call-template name="evalCalculate"/>
            <xsl:call-template name="evalReadonly"/>
            <xsl:call-template name="evalRequired"/>
            <xsl:call-template name="evalRelevant"/>
            <xsl:call-template name="evalConstraint"/>
            <xsl:call-template name="evalType"/>

            <xsl:apply-templates select="*" mode="bind"/>
        </xsl:element>
    </xsl:template>

    <xsl:template name="evalCalculate">
        <xsl:message>WARN: evalCalculate is not implemented yet</xsl:message>
    </xsl:template>

    <xsl:template name="evalReadonly">
        <xsl:if test="@readonly">
            <xsl:attribute name="readonly">true()</xsl:attribute>
        </xsl:if>
    </xsl:template>

    <xsl:template name="evalRequired">
        <xsl:if test="@required">
            <xsl:attribute name="required">true()</xsl:attribute>
        </xsl:if>
    </xsl:template>

    <xsl:template name="evalRelevant">
        <xsl:if test="@disabled">
            <xsl:attribute name="relevant">false()</xsl:attribute>
        </xsl:if>
    </xsl:template>

    <xsl:template name="evalType">
        <xsl:variable name="type">
            <xsl:choose>
                <xsl:when test="@type">
                    <xsl:variable name="lc-type" select="lower-case(@type)"/>
                    <xsl:choose>
                        <xsl:when test="$lc-type eq 'checkbox'">boolean</xsl:when>
                        <xsl:when test="$lc-type eq 'color'">color</xsl:when>
                        <xsl:when test="$lc-type eq 'date'">date</xsl:when>
                        <!-- <xsl:when test="$lc-type eq 'datetime'">datetime</xsl:when> -->
                        <!--todo: datetime-local -->
                        <xsl:when test="$lc-type eq 'datetime-local'">datetime</xsl:when>
                        <xsl:when test="$lc-type eq 'email'">
                            <xsl:choose>
                                <!--todo: multiple -->
                                <xsl:when test="@multiple">email</xsl:when>
                                <xsl:otherwise>email</xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <xsl:when test="$lc-type eq 'file'">
                            <xsl:choose>
                                <!--todo: multiple -->
                                <xsl:when test="@multiple">anyURI</xsl:when>
                                <xsl:otherwise>anyURI</xsl:otherwise>
                            </xsl:choose>
                        </xsl:when>
                        <!--todo: image -->
                        <xsl:when test="$lc-type eq 'image'">string</xsl:when>
                        <xsl:when test="$lc-type eq 'month'">gMonth</xsl:when>
                        <xsl:when test="$lc-type eq 'number'">decimal</xsl:when>
                        <xsl:when test="$lc-type eq 'range'">integer</xsl:when>
                        <!--todo: radio -->
                        <xsl:when test="$lc-type eq 'radio'">string</xsl:when>
                        <xsl:when test="$lc-type eq 'search'">string</xsl:when>
                        <!--todo: tel -->
                        <xsl:when test="$lc-type eq 'tel'">string</xsl:when>
                        <xsl:when test="$lc-type eq 'time'">time</xsl:when>
                        <xsl:when test="$lc-type eq 'url'">anyURI</xsl:when>
                        <!--todo: week -->
                        <xsl:when test="$lc-type eq 'week'">week</xsl:when>
                        <xsl:when test="index-of(('hidden', 'password', 'text'), $lc-type) &gt; 0">string</xsl:when>
                    </xsl:choose>
                </xsl:when>
                <xsl:otherwise>string</xsl:otherwise>
            </xsl:choose> 
        </xsl:variable>
        <xsl:attribute name="type">
            <xsl:value-of select="$type"/>
        </xsl:attribute>
    </xsl:template>

    <xsl:template name="evalConstraint">
        <!-- attributes:
            * accept (?)
            * list
            * max
            * maxLength
            * min
            * pattern
            * step
        -->
        
        <!-- accept:
            Accepts a comma separated list of file types. Valid file types can be any of the following.

            The string audio/* indicates any audio file is allowed.
            The string video/* indicates any video file is allowed.
            The string image/* indicates any image file is allowed.
            A valid [type] with no attributes.
            A file extension starting with a . (period). 

            Duplicates are not allowed (case insensitive).
        -->
        <xsl:variable name="type"><xsl:value-of select="@type"/></xsl:variable>
        <xsl:if test="@accept|@list|@max|@maxLength|@min|@pattern|@step">
            <xsl:variable name="constraints" as="node()*">
                <xsl:for-each select="@accept|@list|@max|@maxLength|@min|@pattern|@step">
                    <xsl:choose>
                        <xsl:when test="name(.) eq 'accept'"></xsl:when>
                        <xsl:when test="name(.) eq 'list'">
                            <xsl:element name="constraint">index-of(instance('i-<xsl:value-of select="."/>')//option, .) &gt; 0)</xsl:element>
                        </xsl:when>
                        <xsl:when test="name(.) eq 'maxLength' and index-of(('', 'text', 'search', 'url', 'email', 'telephone', 'password'), $type) &gt; 0 ">
                            <xsl:element name="constraint">string-lenght(.) &lt;= <xsl:value-of select="."/></xsl:element>
                        </xsl:when>
                        <!-- TODO: check if xpath regex matches js regex: see https://people.mozilla.org/~jorendorff/es5.1-final.html#sec-15.10.1 -->
                        <xsl:when test="name(.) eq 'pattern'">
                            <xsl:element name="constraint">matches(., '<xsl:value-of select="."/>')</xsl:element>
                        </xsl:when>
                        <xsl:when test="name(.) eq 'min'">
                            <xsl:element name="constraint">. &gt;= <xsl:value-of select="."/></xsl:element>
                        </xsl:when>
                        <xsl:when test="name(.) eq 'max'">
                            <xsl:element name="constraint">. &lt;= <xsl:value-of select="."/></xsl:element>
                        </xsl:when>
                        <xsl:when test="name(.) eq 'step'"></xsl:when>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:variable>
            <xsl:attribute name="constraint"><xsl:value-of select="string-join($constraints, ' and ')"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="local-name(.) eq 'select' and count(option) &gt; 0">
            <xsl:attribute name="constraint">index-of(instance('i-<xsl:value-of select="@name"/>')//option, .) &gt; 0)</xsl:attribute>
        </xsl:if>
    </xsl:template>
    
    <xsl:template mode="constraint" match="@*">
        
    </xsl:template>
    <!--
    <xsl:template name="evalConstraint">
        < ! - - attributes:
            * accept (?)
            * list
            * max
            * maxLength
            * min
            * pattern
            * step
        - - >
        <xsl:message>WARN: evalConstraint is not implemented yet</xsl:message>
        
        <xsl:if test="@list">
            <xsl:attribute name="constraint">index-of(instance('i-<xsl:value-of select="@list"/>')//option/text(), .) &gt; 0</xsl:attribute>
        </xsl:if>
    
        <xsl:if test="@maxLength and ( not(@type) or index-of(('text', 'search', 'url', 'email', 'telephone', 'password'), @type) &gt; 0 )">
            <xsl:attribute name="constraint">string-lenght(.) &lt;= <xsl:value-of select="@maxLength"/></xsl:attribute>
        </xsl:if>
        
        < ! - - TODO: check if xpath regex matches js regex: see https://people.mozilla.org/~jorendorff/es5.1-final.html#sec-15.10.1 - - >
        <xsl:if test="@pattern">
            <xsl:attribute name="constraint">matches(., '<xsl:value-of select="@pattern"/>')</xsl:attribute>
        </xsl:if>
          
        < ! - - TODO: step - - >
        <xsl:if test="@step|@max|@min">
            <xsl:attribute name="constraint">
                <xsl:choose>
                    <xsl:when test="@min and not(@step|@max)">. &gt;= <xsl:value-of select="@min"/></xsl:when>
                    <xsl:when test="@max and not(@step|@max)">. &lt;= <xsl:value-of select="@max"/></xsl:when>
                    <xsl:when test="@step and not(@min|@max)">step</xsl:when>
                    <xsl:when test="@min and @max and not(@step)">. &gt;= <xsl:value-of select="@min"/> and . &lt;= <xsl:value-of select="@max"/></xsl:when>
                    <xsl:when test="@min and @step and not(@max)">. &gt;= <xsl:value-of select="@min"/> and step</xsl:when>
                    <xsl:when test="@max and @step and not(@min)">. &lt;= <xsl:value-of select="@max"/> and step</xsl:when>
                    <xsl:otherwise>. &gt;= <xsl:value-of select="@min"/> and . &lt;= <xsl:value-of select="@max"/> and step</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>
    -->
    <xsl:template match="@*|node()|text()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!--
    ###############################################################################################
    mode UI
    ###############################################################################################
    -->

    <!-- ignore label elements -> handled when element with @name is matched -->
    <xsl:template match="label" priority="20" mode="ui"/>

    <xsl:template match="@*|node()|text()" mode="ui">
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="ui"/>
            <xsl:apply-templates mode="ui"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*[@name]" mode="ui">
        <xsl:element name="{concat('xf:',name())}" namespace="http://www.w3.org/2002/xforms">
            <xsl:apply-templates select="@*" mode="ui"/>
            <xsl:attribute name="ref">
                <xsl:value-of select="@name"/>
            </xsl:attribute>

            <xsl:variable name="id" select="@id"/>
            <xsl:if test="exists(//label[@for=$id])">
                <xsl:element name="xf:label" namespace="http://www.w3.org/2002/xforms">
                    <xsl:value-of select="//label[@for=$id]"/>
                </xsl:element>
            </xsl:if>
            <xsl:apply-templates select="*" mode="ui"/>
        </xsl:element>
    </xsl:template>

    <xsl:template mode="ui" match="button">
        <xsl:element name="xf:trigger" namespace="http://www.w3.org/2002/xforms">
            <xsl:attribute name="id" select="@id"/>
            <xsl:element name="xf:label">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:if test="@type='submit'">
                <xsl:element name="xf:action" namespace="http://www.w3.org/2002/xforms">
                    <xsl:element name="xf:send" namespace="http://www.w3.org/2002/xforms">
                        <xsl:attribute name="submission">
                            <xsl:value-of select="concat(@id,'-submit')"/>
                        </xsl:attribute>
                    </xsl:element>
                </xsl:element>
            </xsl:if>
        </xsl:element>
    </xsl:template>

    <xsl:template match="option" mode="ui">
        <xsl:element name="xf:item" namespace="http://www.w3.org/2002/xforms">
            <xsl:element name="xf:label">
                <xsl:value-of select="text()"/>
            </xsl:element>
            <xsl:element name="xf:value">
                <xsl:value-of select="@value"/>
            </xsl:element>
        </xsl:element>
    </xsl:template>

</xsl:stylesheet>
