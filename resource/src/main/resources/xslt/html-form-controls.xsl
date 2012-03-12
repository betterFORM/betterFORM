<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                exclude-result-prefixes="bf xf xsl"
                xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <xsl:variable name="data-prefix" select="'d_'"/>
    <xsl:variable name="trigger-prefix" select="'t_'"/>
    <xsl:variable name="remove-upload-prefix" select="'ru_'"/>

    <!-- ####################################################################################################### -->
    <!-- This stylesheet serves as a 'library' for HTML form controls. It contains only named templates and may  -->
    <!-- be re-used in different layout-stylesheets to create the naked controls.                                -->
    <!-- author: joern turner                                                                                    -->
    <!-- ####################################################################################################### -->


    <!--todo: rework prototype handling -->

    <!-- ############################## INPUT ############################## -->
    <!-- ############################## INPUT ############################## -->
    <!-- ############################## INPUT ############################## -->
    <xsl:template name="input">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="navindex" select="if (exists(@navindex)) then @navindex else '0'"/>
        <xsl:variable name="type"><xsl:call-template name="getType"/></xsl:variable>

        <!--
        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        For input only the datatypes 'boolean' and 'string' are supported in the basic
        layer. Other datatypes are supported by progressive enhancement through JS.
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        -->
        <xsl:choose>
            <xsl:when test="$type='boolean'">
                <input  id="{$id}-value"
                        name="{$name}"
                        type="checkbox"
                        class="xfValue"
                        tabindex="{$navindex}"
                        title="{xf:hint/text()}">
                    <xsl:if test="bf:data/@bf:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="bf:data/text()='true'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                    <!--
                    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    the hint will be applied as html title attribute and additionally output
                    as a span
                    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    -->
                    <xsl:apply-templates select="xf:hint"/>
                </input>
            </xsl:when>
            <xsl:when test="$type='date'">
                <input  id="{$id}-value"
                        name="{$name}"
                        type="date"
                        class="xfValue"
                        tabindex="{$navindex}"
                        placeholder="{xf:hint/text()}"
                        value="{bf:data/text()}">
                    <xsl:if test="bf:data/@bf:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:if test="bf:data/text()='true'">
                        <xsl:attribute name="checked">true</xsl:attribute>
                    </xsl:if>
                </input>
            </xsl:when>
            <xsl:otherwise>
                <input  id="{$id}-value"
                        name="{$name}"
                        type="text"
                        class="xfValue"
                        tabindex="{$navindex}"
                        value="{bf:data/text()}"
                        placeholder="{xf:hint}">
                    <xsl:if test="bf:data/@bf:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                </input>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ############################## OUTPUT ############################## -->
    <!-- ############################## OUTPUT ############################## -->
    <!-- ############################## OUTPUT ############################## -->
    <xsl:template name="output">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="navindex" select="if (exists(@navindex)) then @navindex else '0'"/>
        <xsl:variable name="type"><xsl:call-template name="getType"/></xsl:variable>

        <xsl:choose>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            IMAGE OUTPUT
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:when test="contains(@mediatype,'image/')">
                <img    id="{$id}-value"
                        name="{$name}"
                        src="{bf:data/text()}"
                        alt="{xf:label}"
                        title="{xf:hint/text()}">
                    <!--
                    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    the hint will be applied as html title attribute and additionally output
                    as a span
                    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    -->
                    <xsl:apply-templates select="xf:hint"/>
                </img>
            </xsl:when>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            HTML OUTPUT
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:when test="contains(@mediatype,'text/html')">
                <span   id="{$id}-value"
                        class="xfValue mediatype-text-html">
                    <xsl:value-of select="bf:data/text()" disable-output-escaping="yes"/>
                </span>
            </xsl:when>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            if we got an URI but not an mediatype we handle it as link
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:when test="$type='anyURI' and (not(@mediatype))">
                <a  id="{$id}-value"
                    href="{bf:data/text()}"
                    class="xfValue"
                    tabindex="{$navindex}"
                    title="{xf:hint/text()}">
                    <xsl:value-of select="bf:data/text()"/>
                </a>
                <!--
                >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                the hint will be applied as html title attribute and additionally output
                as a span
                The hint span will be put outside of the anchor
                <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                -->
                <xsl:apply-templates select="xf:hint"/>
            </xsl:when>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            PLAIN OUTPUT AS SPAN
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:otherwise>
                <span   id="{$id}-value"
                        tabindex="{$navindex}"
                        title="{xf:hint/text()}">
                    <xsl:value-of select="bf:data/text()"/>
                </span>
                <xsl:apply-templates select="xf:hint"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ############################## RANGE ############################## -->
    <!-- ############################## RANGE ############################## -->
    <!-- ############################## RANGE ############################## -->
    <xsl:template name="range">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="navindex" select="if (exists(@navindex)) then @navindex else '0'"/>
        <!--
        todo: review: start and end are optional attributes in XForms but how can we make sense of that?
        -->
        <xsl:variable name="start" select="@start"/>
        <xsl:variable name="end" select="@end"/>
        <xsl:variable name="step" select="@step"/>
        <xsl:variable name="value" select="bf:data/text()"/>

        <span>
            <input  id="{$id}-value"
                    name="{$name}"
                    class="xfValue"
                    type="range"
                    min="{$start}"
                    max="{$end}"
                    value="{$value}"
                    tabindex="{$navindex}"
                    title="{xf:hint/text()}">
                <xsl:if test="bf:data/@bf:readonly='true'">
                    <xsl:attribute name="readonly">readonly</xsl:attribute>
                </xsl:if>
                <xsl:if test="string-length($step) != 0">
                    <xsl:attribute name="step" select="$step"/>
                </xsl:if>
            </input>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            the hint will be applied as html title attribute and additionally output
            as a span
            The hint span will be put outside of the anchor
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:apply-templates select="xf:hint"/>
        </span>
    </xsl:template>

    <!-- ############################## SECRET ############################## -->
    <!-- ############################## SECRET ############################## -->
    <!-- ############################## SECRET ############################## -->
    <xsl:template name="secret">
        <!-- todo: review: what about the maxlength param? -->
        <xsl:param name="maxlength"/>
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="navindex" select="if (exists(@navindex)) then @navindex else '0'"/>
        <xsl:variable name="incremental" select="@incremental"/>

        <input  id="{$id}-value"
                name="{$name}"
                class="xfValue"
                tabindex="{$navindex}"
                type="password"
                value="{bf:data/text()}"
                title="{xf:hint/text()}">
            <xsl:if test="$maxlength">
                <xsl:attribute name="maxlength"><xsl:value-of select="$maxlength"/></xsl:attribute>
            </xsl:if>
            <xsl:if test="bf:data/@bf:readonly='true'">
                <xsl:attribute name="readonly">readonly</xsl:attribute>
            </xsl:if>
        </input>
        <!--
        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        the hint will be applied as html title attribute and additionally output
        as a span
        The hint span will be put outside of the anchor
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        -->
        <xsl:apply-templates select="xf:hint"/>

    </xsl:template>

    <!-- ############################## SELECT1 ############################## -->
    <!-- ############################## SELECT1 ############################## -->
    <!-- ############################## SELECT1 ############################## -->
    <xsl:template name="select1">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="navindex" select="if (exists(@navindex)) then @navindex else '0'"/>

        <xsl:variable name="parent" select="."/>
        <xsl:variable name="size" select="if(exists(@size)) then @size else '5'"/>

        <xsl:choose>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            a compact select1 is rendered as a LIST control
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:when test="@appearance='compact'">
                <select id="{$id}-value"
                        name="{$name}"
                        class="xfValue"
                        tabindex="{$navindex}"
                        size="{$size}"
                        title="{xf:hint/text()}"
                        >
                    <xsl:if test="bf:data/@bf:readonly='true'"><xsl:attribute name="readonly">readonly</xsl:attribute></xsl:if>
                    <xsl:apply-templates select="xf:hint"/>
                    <xsl:call-template name="build-items">
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:call-template>
                </select>
            </xsl:when>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            a compact select1 is rendered as a set of RADIOBUTTONS
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:when test="@appearance='full'">
                <xsl:call-template name="build-radiobuttons">
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="parent" select="$parent"/>
                    <xsl:with-param name="navindex" select="$navindex"/>
                </xsl:call-template>
            </xsl:when>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            a minimal select1 is rendered as a DROPDOWN
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:otherwise>
                <select id="{$id}-value"
                        name="{$name}"
                        class="xfValue"
                        tabindex="{$navindex}"
                        size="1"
                        title="{xf:hint/text()}">
                    <xsl:if test="bf:data/@bf:readonly='true'">
                        <xsl:attribute name="readonly">readonly</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="build-items">
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:call-template>
                </select>
                <!-- todo: review: create hidden parameter for deselection -->
                <input type="hidden" name="{$name}" value=""/>
                <!--
                >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                the hint will be applied as html title attribute and additionally output
                as a span
                The hint span will be put outside of the anchor
                <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                -->
                <xsl:apply-templates select="xf:hint"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- ############################## SELECT ############################## -->
    <!-- ############################## SELECT ############################## -->
    <!-- ############################## SELECT ############################## -->
    <xsl:template name="select">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="navindex" select="if (exists(@navindex)) then @navindex else '0'"/>

        <xsl:variable name="parent" select="."/>
        <xsl:variable name="size" select="if(exists(@size)) then @size else '5'"/>

        <xsl:choose>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            a full select1 is rendered as a set of CHECKBOXES
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:when test="@appearance='full'">
                <xsl:call-template name="build-checkboxes">
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="parent" select="$parent"/>
                    <xsl:with-param name="navindex" select="$navindex"/>
                </xsl:call-template>
                <!-- create hidden parameter for identification and deselection -->
                <input type="hidden" id="{$id}-value" name="{$name}" value=""/>
            </xsl:when>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            all other appearances are rendered as a LIST
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:otherwise>
                <select id="{$id}-value"
                        name="{$name}"
                        class="xfValue"
                        tabindex="{$navindex}"
                        size="{$size}"
                        multiple="multiple"
                        title="{xf:hint/text()}">
                    <xsl:if test="bf:data/@bf:readonly='true'">
                        <xsl:attribute name="disabled">disabled</xsl:attribute>
                    </xsl:if>
                    <xsl:call-template name="build-items">
                        <xsl:with-param name="parent" select="$parent"/>
                    </xsl:call-template>
                </select>
                <!-- todo: ?create hidden parameter for deselection ? -->
                <input type="hidden" name="{$name}" value=""/>
                <!--
                >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                the hint will be applied as html title attribute and additionally output
                as a span
                The hint span will be put outside of the anchor
                <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                -->
                <xsl:apply-templates select="xf:hint"/>
            </xsl:otherwise>

        </xsl:choose>
    </xsl:template>


    <!-- ############################## TEXTAREA ############################## -->
    <!-- ############################## TEXTAREA ############################## -->
    <!-- ############################## TEXTAREA ############################## -->
    <xsl:template name="textarea">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="navindex" select="if (exists(@navindex)) then @navindex else '0'"/>
        <xsl:variable name="rows" select="if (exists(@rows)) then @rows else '5'"/>
        <xsl:variable name="cols" select="if (exists(@cols)) then @cols else '30'"/>


        <textarea   id="{$id}-value"
                    name="{$name}"
                    class="xfValue"
                    tabindex="{$navindex}"
                    title="{xf:hint/text()}"
                    rows="{$rows}"
                    cols="{$cols}">
            <xsl:if test="bf:data/@bf:readonly='true'">
                <xsl:attribute name="readonly">readonly</xsl:attribute>
            </xsl:if>
            <xsl:value-of select="bf:data/text()"/>
        </textarea>
        <!--
        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        the hint will be applied as html title attribute and additionally output
        as a span
        The hint span will be put outside of the anchor
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        -->
        <xsl:apply-templates select="xf:hint"/>

    </xsl:template>


    <!-- ############################## SUBMIT ############################## -->
    <!-- ############################## SUBMIT ############################## -->
    <!-- ############################## SUBMIT ############################## -->
    <xsl:template name="submit">
        <xsl:param name="classes"/>
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($trigger-prefix,$id)"/>
        <xsl:variable name="navindex" select="@navindex" />

        <span id="{$id}" class="{$classes}">
            <input  id="{$id}-value"
                    name="{$name}"
                    class="xfValue"
                    tabindex="{$navindex}"
                    title="{xf:hint/text()}"
                    type="submit"
                    value="{xf:label}">
                <xsl:if test="bf:data/@bf:readonly='true'">
                    <xsl:attribute name="readonly">readonly</xsl:attribute>
                </xsl:if>
            </input>
            <!--
            >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
            the hint will be applied as html title attribute and additionally output
            as a span
            The hint span will be put outside of the anchor
            <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
            -->
            <xsl:apply-templates select="xf:hint"/>

        </span>
    </xsl:template>

    <!-- ############################## TRIGGER ############################## -->
    <!-- ############################## TRIGGER ############################## -->
    <!-- ############################## TRIGGER ############################## -->
    <xsl:template name="trigger">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($trigger-prefix,$id)"/>
        <xsl:variable name="navindex" select="@navindex" />

            <!-- minimal appearance only supported in scripted mode -->
        <input  id="{$id}-value"
                name="{$name}"
                class="xfValue"
                tabindex="{$navindex}"
                title="{xf:hint/text()}"
                type="submit"
                value="{xf:label}"
                >
            <xsl:if test="bf:data/@bf:readonly='true'">
                <xsl:attribute name="readonly">readonly</xsl:attribute>
            </xsl:if>
            <!-- todo: does this still apply? -->
            <xsl:if test="@accesskey">
                <xsl:attribute name="accesskey">
                    <xsl:value-of select="@accesskey"/>
                </xsl:attribute>
                <xsl:attribute name="title">
                    <xsl:value-of select="normalize-space(xf:hint)"/>- KEY: [ALT]+ <xsl:value-of select="@accesskey"/>
                </xsl:attribute>
            </xsl:if>
        </input>
        <!--
        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        the hint will be applied as html title attribute and additionally output
        as a span
        The hint span will be put outside of the anchor
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        -->
        <xsl:apply-templates select="xf:hint"/>
    </xsl:template>

    <!-- ############################## UPLOAD ############################## -->
    <!-- ############################## UPLOAD ############################## -->
    <!-- ############################## UPLOAD ############################## -->
    <xsl:template name="upload">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,$id)"/>
        <xsl:variable name="navindex" select="@navindex" />

        <input  id="{$id}-value"
                name="{$name}"
                class="xfValue"
                tabindex="{$navindex}"
                title="{xf:hint/text()}"
                type="file"
                value=""
                >
            <xsl:if test="bf:data/@bf:readonly='true'">
                <xsl:attribute name="readonly">readonly</xsl:attribute>
            </xsl:if>
        </input>
        <xsl:if test="xf:filename">
            <input type="hidden" id="{xf:filename/@id}" value="{xf:filename/bf:data}"/>
        </xsl:if>
        <xsl:if test="@bf:destination">
            <!-- create hidden parameter for destination -->
            <input type="hidden" id="{$id}-destination" value="{@bf:destination}"/>
        </xsl:if>
        <!--
        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        the hint will be applied as html title attribute and additionally output
        as a span
        The hint span will be put outside of the anchor
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        -->
        <xsl:apply-templates select="xf:hint"/>
    </xsl:template>


    <!-- ######################################################################################################## -->
    <!-- ########################################## HELPER TEMPLATES FOR SELECT, SELECT1 ######################## -->
    <!-- ######################################################################################################## -->

    <xsl:template name="build-items">
        <xsl:param name="parent"/>

		<!-- add an empty item, because otherwise deselection is not possible -->
        <xsl:if test="$parent/bf:data/@bf:required='false'">
            <option value="">
                <xsl:if test="string-length($parent/bf:data/text()) = 0">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
            </option>
        </xsl:if>
        <xsl:for-each select="$parent/xf:itemset|$parent/xf:item|$parent/xf:ces">
			<xsl:call-template name="build-items-list"/>
		</xsl:for-each>
    </xsl:template>
    
    <xsl:template name="build-items-list">
    	<xsl:choose>
    		<xsl:when test="local-name(.) = 'choices'">
    			<xsl:call-template name="build-items-choices"/>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'itemset'">
    			<xsl:call-template name="build-items-itemset"/>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'item'">
    			<xsl:call-template name="build-items-item"/>
    		</xsl:when>
    	</xsl:choose>
    </xsl:template>
    
	<xsl:template name="build-items-choices">
		<xsl:for-each select="xf:itemset|xf:item|xf:choices">
			<xsl:call-template name="build-items-list"/>
		</xsl:for-each>
	</xsl:template>

    <xsl:template name="build-items-itemset">
		<optgroup id="{@id}">
			<xsl:for-each select="xf:item">
				<xsl:call-template name="build-items-item"/>
			</xsl:for-each>
		</optgroup>
	</xsl:template>
	
	<xsl:template name="build-items-item">
		<option id="{@id}-value" value="{xf:value}" title="{xf:hint}" class="selector-item">
			<xsl:if test="@selected='true'">
				<xsl:attribute name="selected">selected</xsl:attribute>
			</xsl:if>
			<xsl:value-of select="xf:label" />
		</option>
	</xsl:template>
	
    <xsl:template name="build-item-prototype">
        <xsl:param name="item-id"/>
        <xsl:param name="itemset-id"/>

        <select id="{$itemset-id}-prototype" class="selector-prototype">
            <option id="{$item-id}-value" class="selector-prototype">
	           	<xsl:choose>
    	       		<xsl:when test="xf:copy">
	    	   			<xsl:attribute name="value" select="xf:copy/@id"/>
	              		<xsl:attribute name="title" select="xf:copy/@id"/>
    	          	</xsl:when>
        	      	<xsl:otherwise>
            	   		<xsl:attribute name="value" select="normalize-space(xf:value)"/>
              			<xsl:attribute name="title" select="xf:hint"/>
                	</xsl:otherwise>
				</xsl:choose>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="selected">selected</xsl:attribute>
                </xsl:if>
                <xsl:value-of select="xf:label"/>
            </option>
        </select>
    </xsl:template>

    <xsl:template name="build-checkboxes">
        <xsl:param name="name"/>
        <xsl:param name="parent"/>
        <xsl:param name="navindex"/> 
        <!-- handle items, choices and itemsets -->
        <xsl:for-each select="$parent/xf:item|$parent/xf:choices|$parent/xf:itemset">
        	<xsl:call-template name="build-checkboxes-list">
        		<xsl:with-param name="name" select="$name"/>
        		<xsl:with-param name="parent" select="$parent"/>
        	</xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="build-checkboxes-list">
    	<xsl:param name="name"/>
        <xsl:param name="parent"/>
    	<xsl:choose>
    		<xsl:when test="local-name(.) = 'choices'">
    			<xsl:call-template name="build-checkboxes-choices">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'itemset'">
    			<xsl:call-template name="build-checkboxes-itemset">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'item'">
    			<xsl:call-template name="build-checkboxes-item">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    	</xsl:choose>
    </xsl:template>

	<xsl:template name="build-checkboxes-choices">
		<xsl:param name="name"/>
        <xsl:param name="parent"/>
		<xsl:for-each select="xf:itemset|xf:item|xf:choices">
			<xsl:call-template name="build-checkboxes-list">
				<xsl:with-param name="name" select="$name"/>
           		<xsl:with-param name="parent" select="$parent"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

    <xsl:template name="build-checkboxes-itemset">
    	<xsl:param name="name"/>
        <xsl:param name="parent"/>
		<span id="{@id}">
			<xsl:for-each select="xf:item">
				<xsl:call-template name="build-checkboxes-item">
	           		<xsl:with-param name="name" select="$name"/>
	           		<xsl:with-param name="parent" select="$parent"/>
				</xsl:call-template>
			</xsl:for-each>
		</span>
	</xsl:template>
	
	<xsl:template name="build-checkboxes-item">
    	<xsl:param name="name"/>
        <xsl:param name="parent"/>
        <xsl:param name="navindex"/>         
        <span id="{@id}" class="selector-item">
            <input id="{@id}-value" class="value" type="checkbox" name="{$name}">
                <xsl:if test="string-length($navindex) != 0">
                    <xsl:attribute name="tabindex">
                        <xsl:value-of select="$navindex"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="$parent/bf:data/@bf:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
                <xsl:choose>
        			<xsl:when test="xf:copy">
           				<xsl:attribute name="value" select="xf:copy/@id"/>
	            	</xsl:when>
    	        	<xsl:otherwise>
	    	    		<xsl:attribute name="value" select="xf:value"/>
    	    		</xsl:otherwise>
        	    </xsl:choose>
                <xsl:choose>
                    <xsl:when test="xf:hint">
                        <xsl:apply-templates select="xf:hint"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="$parent/xf:hint"/>
                    </xsl:otherwise>
                </xsl:choose>
            </input>
            <span id="{@id}-label" class="label">
                <xsl:if test="$parent/bf:data/@bf:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="xf:label"/>
            </span>
        </span>
	</xsl:template>
	
    <xsl:template name="build-checkbox-prototype">
        <xsl:param name="item-id"/>
        <xsl:param name="itemset-id"/>
        <xsl:param name="name"/>
        <xsl:param name="parent"/>

        <span id="{$itemset-id}-prototype" class="selector-prototype">
            <input id="{$item-id}-value" class="value" type="checkbox" name="{$name}">
                <xsl:choose>
	       			<xsl:when test="xf:copy">
		   				<xsl:attribute name="value"><xsl:value-of select="xf:copy/@id"/></xsl:attribute>
	              	</xsl:when>
    	        	<xsl:otherwise>
      	 	    		<xsl:attribute name="value"><xsl:value-of select="xf:value"/></xsl:attribute>
            		</xsl:otherwise>
           	    </xsl:choose>
                <xsl:attribute name="title">
                    <xsl:choose>
                        <xsl:when test="xf:hint">
                            <xsl:value-of select="xf:hint"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$parent/xf:hint"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
                <xsl:if test="$parent/bf:data/@bf:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
            </input>
            <span id="{@item-id}-label" class="label">
                <xsl:if test="$parent/bf:data/@bf:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="xf:label"/>
            </span>
        </span>
    </xsl:template>

    <!-- overwrite/change this template, if you don't like the way labels are rendered for checkboxes -->
    <xsl:template name="build-radiobuttons">
        <xsl:param name="name"/>
        <xsl:param name="parent"/>
        <xsl:param name="id"/>
        <xsl:param name="navindex"/> 
        <!-- handle items, choices and itemsets -->
        <xsl:for-each select="$parent/xf:item|$parent/xf:choices|$parent/xf:itemset">
        	<xsl:call-template name="build-radiobuttons-list">
        		<xsl:with-param name="name" select="$name"/>
        		<xsl:with-param name="parent" select="$parent"/>
        	</xsl:call-template>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="build-radiobuttons-list">
    	<xsl:param name="name"/>
    	<xsl:param name="parent"/>
        
        <xsl:choose>
    		<xsl:when test="local-name(.) = 'choices'">
    			<xsl:call-template name="build-radiobuttons-choices">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'itemset'">
    			<xsl:call-template name="build-radiobuttons-itemset">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    		<xsl:when test="local-name(.) = 'item'">
    			<xsl:call-template name="build-radiobuttons-item">
            		<xsl:with-param name="name" select="$name"/>
            		<xsl:with-param name="parent" select="$parent"/>
            	</xsl:call-template>
    		</xsl:when>
    	</xsl:choose>
    </xsl:template>

	<xsl:template name="build-radiobuttons-choices">
		<xsl:param name="name"/>
		<xsl:param name="parent"/>
		<xsl:for-each select="xf:itemset|xf:item|xf:choices">
			<xsl:call-template name="build-radiobuttons-list">
				<xsl:with-param name="name" select="$name"/>
           		<xsl:with-param name="parent" select="$parent"/>
			</xsl:call-template>
		</xsl:for-each>
	</xsl:template>

    <xsl:template name="build-radiobuttons-itemset">
    	<xsl:param name="name"/>
    	<xsl:param name="parent"/>
		<span id="{@id}">
			<xsl:for-each select="xf:item">
				<xsl:call-template name="build-radiobuttons-item">
	           		<xsl:with-param name="name" select="$name"/>
	           		<xsl:with-param name="parent" select="$parent"/>
				</xsl:call-template>
			</xsl:for-each>
		</span>
	</xsl:template>
	
	<xsl:template name="build-radiobuttons-item">
    	<xsl:param name="name"/>
    	<xsl:param name="parent"/>
        <xsl:param name="navindex"/>         
        <span id="{@id}" class="selector-item">
            <input id="{@id}-value" class="value" type="radio" name="{$name}">
                <xsl:if test="string-length($navindex) != 0">
                    <xsl:attribute name="tabindex">
                        <xsl:value-of select="$navindex"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="$parent/bf:data/@bf:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
                <xsl:choose>
        			<xsl:when test="xf:copy">
           				<xsl:attribute name="value" select="xf:copy/@id"/>
	            	</xsl:when>
    	        	<xsl:otherwise>
	    	    		<xsl:attribute name="value" select="xf:value"/>
    	    		</xsl:otherwise>
        	    </xsl:choose>
                <xsl:choose>
                    <xsl:when test="xf:hint">
                        <xsl:apply-templates select="xf:hint"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="$parent/xf:hint"/>
                    </xsl:otherwise>
                </xsl:choose>
            </input>
            <span id="{@id}-label" class="label">
                <xsl:if test="$parent/bf:data/@bf:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="xf:label"/>
            </span>
        </span>
	</xsl:template>
	
    <xsl:template name="build-radiobutton-prototype">
        <xsl:param name="item-id"/>
        <xsl:param name="itemset-id"/>
        <xsl:param name="name"/>
        <xsl:param name="parent"/>
        <xsl:param name="navindex"/>
        <span id="{$itemset-id}-prototype" class="selector-prototype">
            <input id="{$item-id}-value" class="value" type="radio" name="{$name}">
                <xsl:if test="string-length($navindex) != 0">
                    <xsl:attribute name="tabindex">
                        <xsl:value-of select="$navindex"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="$parent/bf:data/@bf:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:if test="@selected='true'">
                    <xsl:attribute name="checked">checked</xsl:attribute>
                </xsl:if>
                <xsl:choose>
					<xsl:when test="xf:copy">
   						<xsl:attribute name="value" select="xf:copy/@id"/>
	            	</xsl:when>
    	        	<xsl:otherwise>
	    	    		<xsl:attribute name="value" select="xf:value"/>
    	    		</xsl:otherwise>
        	    </xsl:choose>
                <xsl:choose>
                    <xsl:when test="xf:hint">
                        <xsl:apply-templates select="xf:hint"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="$parent/xf:hint"/>
                    </xsl:otherwise>
                </xsl:choose>
            </input>
            <span id="{$item-id}-label" class="label">
                <xsl:if test="$parent/bf:data/@bf:readonly='true'">
                    <xsl:attribute name="disabled">disabled</xsl:attribute>
                </xsl:if>
                <xsl:apply-templates select="xf:label"/>
            </span>
        </span>
    </xsl:template>


</xsl:stylesheet>
