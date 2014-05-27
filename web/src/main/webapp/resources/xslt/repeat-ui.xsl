<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:xhtml="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:bf="http://betterform.sourceforge.net/xforms"
                xmlns:bfc="http://betterform.sourceforge.net/xforms/controls"
                exclude-result-prefixes="xhtml xf bf"
                xpath-default-namespace="http://www.w3.org/1999/xhtml">

    <!-- ####################################################################################################### -->
    <!-- This stylesheet handles the XForms UI constructs 'repeat'                                               -->
    <!-- author: joern turner                                                                                    -->
    <!-- author: lars windauer                                                                                   -->
    <!-- ####################################################################################################### -->

    <!-- ############################################ PARAMS ################################################### -->
    <!-- ############################################ VARIABLES ################################################ -->

    <xsl:preserve-space elements="*"/>

    <!-- ######################################################################################################## -->
    <!-- ####################################### REPEAT ######################################################### -->
    <!-- ######################################################################################################## -->

    <!--
    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
    DEFAULT REPEAT - same as FULL REPEAT
    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    -->
    <xsl:template match="xf:repeat" name="repeat">
        <xsl:variable name="repeat-id" select="@id"/>
        <xsl:variable name="repeat-index" select="bf:data/@index"/>

        <xsl:variable name="repeat-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="'full'"/>
            </xsl:call-template>
        </xsl:variable>

        <!--
        >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
        if we are an outermost repeat save the repeat body as prototype
        <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
        -->
        <xsl:if test="not(ancestor::xf:repeat)">
            <!-- generate prototype(s) for scripted environment -->
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']">
                <xsl:call-template name="processFullPrototype">
                    <xsl:with-param name="id" select="$repeat-id"/>
                </xsl:call-template>
            </xsl:for-each>
            <!-- ***** has become unnecessary as handled by above template ***** -->
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']//xf:repeat">
                <xsl:call-template name="processRepeatPrototype"/>
            </xsl:for-each>
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']//xf:itemset">
                <xsl:call-template name="processItemsetPrototype"/>
            </xsl:for-each>
        </xsl:if>


        <div id="{$repeat-id}" class="{$repeat-classes}">
            <!-- loop repeat entries -->
            <xsl:for-each select="xf:group[@appearance='repeated']">
                <xsl:variable name="repeat-item-id" select="@id"/>
                <xsl:variable name="repeat-item-classes">
                    <xsl:call-template name="assemble-repeat-item-classes">
                        <xsl:with-param name="selected" select="$repeat-index=position()"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:variable name="group-label" select="true()"/>

                <div id="{$repeat-item-id}"
                     class="{$repeat-item-classes}"
                     tabindex="0">
                    <div class="legend">
                        <xsl:choose>
                            <xsl:when test="$group-label and xf:label">
                                <xsl:attribute name="id">
                                    <xsl:value-of select="concat($repeat-item-id, '-label')"/>
                                </xsl:attribute>
                                <xsl:attribute name="class">
                                    <xsl:call-template name="assemble-group-label-classes"/>
                                </xsl:attribute>
                                <xsl:call-template name="create-label">
                                    <xsl:with-param name="label-elements" select="xf:label"/>
                                </xsl:call-template>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:attribute name="style">display:none;</xsl:attribute>
                            </xsl:otherwise>
                        </xsl:choose>
                    </div>

                    <!--
                    >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
                    copy all children but change @id into @name
                    <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    -->
                    <xsl:apply-templates select="*[not(self::xf:label)]" />
                    <!--<xsl:apply-templates select="*[not(self::xf:label)]" mode="repeat"/>-->
                </div>

            </xsl:for-each>
        </div>
    </xsl:template>

    <xsl:template name="processFullPrototype">
        <xsl:param name="id"/>

        <xsl:variable name="group-id" select="concat($id, '-prototype')"/>
        <xsl:variable name="group-classes"
                      select="'xfRepeatPrototype xfDisabled xfReadWrite xfOptional xfValid'"/>
        <xsl:variable name="group-label" select="true()"/>

        <div id="{$group-id}" class="{$group-classes}">
            <div class="legend">
                <xsl:choose>
                    <xsl:when test="$group-label and xf:label">
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat($group-id, '-label')"/>
                        </xsl:attribute>
                        <xsl:attribute name="class">
                            <xsl:call-template name="assemble-group-label-classes"/>
                        </xsl:attribute>
                        <xsl:call-template name="create-label">
                            <xsl:with-param name="label-elements" select="xf:label"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="style">display:none;</xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </div>

            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-full-prototype"/>
        </div>
    </xsl:template>


    <xsl:template match="@id" mode="repeat" priority="100">
        <xsl:message>mode repeat @id ****** <xsl:value-of select="."/></xsl:message>
        <xsl:attribute name="foo" select="."/>
    </xsl:template>

    <xsl:template match="*|@*|text()" mode="repeat">
        <xsl:message>mode repeat ******</xsl:message>
        <xsl:copy>
            <xsl:apply-templates select="@*" mode="repeat"/>
            <xsl:apply-templates select="*|text()" />
        </xsl:copy>
    </xsl:template>


    <xsl:template
            match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:upload|xf:trigger|xf:submit"
            mode="repeated-full-prototype"
            priority="20">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:element name="span">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="data-bf-class" select="$control-classes"/>

            <xsl:if test="exists(@mediatype)">
                <xsl:attribute name="mediatype" select="@mediatype"/>
            </xsl:if>
            <xsl:if test="local-name() != 'trigger' and local-name() != 'submit'">
                <label class="xfLabel">
                    <xsl:call-template name="create-label">
                        <xsl:with-param name="label-elements" select="xf:label"/>
                    </xsl:call-template>
                </label>
            </xsl:if>

            <!--<xsl:apply-templates select="xf:alert"/>-->
            <span class="widgetContainer">
                <xsl:call-template name="buildControl"/>
                <xsl:apply-templates select="xf:alert"/>
                <xsl:apply-templates select="xf:hint"/>
                <xsl:apply-templates select="xf:help"/>
            </span>
        </xsl:element>
    </xsl:template>

    <xsl:template match="xf:repeat"
                  mode="repeated-full-prototype"
                  priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="repeat-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="'full'"/>
            </xsl:call-template>
        </xsl:variable>

        <div id="{$id}" data-bf-class="{$repeat-classes}">
            <!--<xsl:apply-templates select="*" mode="repeated-full-prototype"/>-->
        </div>
    </xsl:template>

    <xsl:template match="xf:group"
                  mode="repeated-full-prototype"
                  priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="appearance" select="@appearance"/>

        <xsl:variable name="group-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:element name="span">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="class" select="concat($group-classes,' xfRepeated dijitContentPane')"/>
            <xsl:attribute name="controlType" select="local-name()"/>


            <xsl:element name="span">
                <xsl:attribute name="class">xfGroupLabel</xsl:attribute>
                <xsl:call-template name="create-label">
                    <xsl:with-param name="label-elements" select="xf:label"/>
                </xsl:call-template>
            </xsl:element>

            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-full-prototype"/>
        </xsl:element>


    </xsl:template>

    <xsl:template match="xf:switch"
                  mode="repeated-full-prototype"
                  priority="10">
        <xsl:variable name="switch-id" select="@id"/>
        <xsl:variable name="switch-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <div id="{$switch-id}" class="{$switch-classes}">
            <xsl:apply-templates mode="repeated-full-prototype"/>
        </div>
    </xsl:template>


    <xsl:template match="xf:case[bf:data/@selected='true']"
                  mode="repeated-full-prototype"
                  priority="10">
        <xsl:variable name="case-id" select="@id"/>
        <xsl:variable name="case-classes" select="'xfCase xfSelectedCase'"/>

        <div id="{$case-id}" class="{$case-classes}">
            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-full-prototype"/>
        </div>
    </xsl:template>

    <!-- ### DE-SELECTED/NON-SELECTED CASE ### -->
    <xsl:template match="xf:case"
                  mode="repeated-full-prototype"
                  priority="10">
        <!-- render only in scripted environment -->
        <xsl:variable name="case-id" select="@id"/>
        <xsl:variable name="case-classes" select="'xfCase xfDeselectedCase'"/>

        <div id="{$case-id}" class="{$case-classes}">
            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-full-prototype"/>
        </div>
    </xsl:template>

<!--
    <xsl:template match="xf:trigger" mode="repeated-full-prototype" priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="appearance">
            <xsl:choose>
                <xsl:when test="string-length(@appearance) &gt;0">
                    <xsl:value-of select="@appearance"/>
                </xsl:when>
                <xsl:otherwise>full</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="exists(@ref) or exists(@bind)">
                <div id="{$id}" class="{$control-classes} xfRepeated" controlType="{local-name()}"
                     appearance="{$appearance}">
                    <xsl:value-of select="xf:label"/>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="trigger">
                    <xsl:with-param name="classes" select="$control-classes"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>
-->

    <!-- copys non xform nodes into the full protoype -->
    <xsl:template match="xhtml:*"
                  mode="repeated-full-prototype"
                  priority="1">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates mode="repeated-full-prototype"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="*|@*|text()|comment()" mode="repeated-compact-prototype">
        <xsl:choose>
            <xsl:when test="namespace-uri(.)='http://www.w3.org/1999/xhtml'">
                <xsl:element name="{local-name(.)}" namespace="">
                    <xsl:apply-templates select="*|@*|text()|comment()" mode="repeated-compact-prototype"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="*|@*|text()|comment()" mode="repeated-compact-prototype"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="*|@*|text()|comment()" mode="compact-repeat">
        <xsl:choose>
            <xsl:when test="namespace-uri(.)='http://www.w3.org/1999/xhtml'">
                <xsl:element name="{local-name(.)}" namespace="">
                    <xsl:apply-templates select="*|@*|text()|comment()" mode="compact-repeat"/>
                </xsl:element>
            </xsl:when>
            <xsl:otherwise>
                <xsl:copy>
                    <xsl:apply-templates select="*|@*|text()|comment()" mode="compact-repeat"/>
                </xsl:copy>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- repeat prototype helper -->
    <xsl:template name="processRepeatPrototype">
        <xsl:variable name="id" select="@id"/>

        <xsl:choose>
            <xsl:when test="@appearance='compact'">
                <xsl:call-template name="processCompactPrototype">
                    <xsl:with-param name="id" select="$id"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="processFullPrototype">
                    <xsl:with-param name="id" select="$id"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- itemset prototype helper -->
    <xsl:template name="processItemsetPrototype">
        <xsl:variable name="item-id" select="$betterform-pseudo-item"/>
        <xsl:variable name="itemset-id" select="@id"/>
        <xsl:variable name="name" select="concat($data-prefix,../@id)"/>
        <xsl:variable name="parent" select=".."/>

        <xsl:choose>
            <xsl:when test="local-name($parent)='select1' and $parent/@appearance='full'">
                <xsl:call-template name="build-radiobutton-prototype">
                    <xsl:with-param name="item-id" select="$item-id"/>
                    <xsl:with-param name="itemset-id" select="$itemset-id"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="parent" select="$parent"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="local-name($parent)='select' and $parent/@appearance='full'">
                <xsl:call-template name="build-checkbox-prototype">
                    <xsl:with-param name="item-id" select="$item-id"/>
                    <xsl:with-param name="itemset-id" select="$itemset-id"/>
                    <xsl:with-param name="name" select="$name"/>
                    <xsl:with-param name="parent" select="$parent"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="build-item-prototype">
                    <xsl:with-param name="item-id" select="$item-id"/>
                    <xsl:with-param name="itemset-id" select="$itemset-id"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <!-- ######################################################################################################## -->
    <!-- ####################################### COMPACT######################################################### -->
    <!-- ######################################################################################################## -->

    <xsl:template match="xf:repeat[@appearance='compact']" name="compact-repeat">
        <xsl:variable name="repeat-id" select="@id"/>
        <xsl:variable name="repeat-index" select="bf:data/@index"/>
        <xsl:variable name="repeat-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="'compact'"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:if test="not(ancestor::xf:repeat)">
            <!-- generate prototype(s) for scripted environment -->
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']">
                <xsl:call-template name="processCompactPrototype">
                    <xsl:with-param name="id" select="$repeat-id"/>
                </xsl:call-template>
            </xsl:for-each>
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']//xf:repeat">
                <xsl:call-template name="processRepeatPrototype"/>
            </xsl:for-each>
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']//xf:itemset">
                <xsl:call-template name="processItemsetPrototype"/>
            </xsl:for-each>
        </xsl:if>


        <table id="{$repeat-id}"
               jsId="{$repeat-id}"
               class="{$repeat-classes}"
               border="0"
               cellpadding="0"
               cellspacing="0"
                >
            <!-- build table header -->
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated'][1]">
                <tr class="xfRepeatHeader">
                    <xsl:call-template name="processCompactHeader"/>
                </tr>
            </xsl:for-each>

            <!-- loop repeat entries -->
            <xsl:for-each select="xf:group[@appearance='repeated']">
                <xsl:variable name="id" select="@id"/>
                <xsl:variable name="repeat-item-classes">
                    <xsl:call-template name="assemble-repeat-item-classes">
                        <xsl:with-param name="selected" select="$repeat-index=position()"/>
                    </xsl:call-template>
                </xsl:variable>

                <tr id="{$id}"
                    class="{$repeat-item-classes}">
                    <xsl:call-template name="processCompactChildren"/>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>


    <xsl:template name="processCompactHeader">
        <xsl:for-each select="xf:*|bfc:*">
            <xsl:variable name="col-classes">
                <xsl:value-of select="concat('appBfTableCol-',position(),' bfTableCol-',position())"/>
            </xsl:variable>
            <td class="{$col-classes}">


                <xsl:choose>
                    <xsl:when
                            test="self::xf:*[local-name(.)='trigger' or local-name(.)='submit' or (local-name(.)='output' and @appearance='caLink')][xf:label]">
                        <xsl:variable name="label-classes">
                            <xsl:call-template name="assemble-label-classes"/>
                        </xsl:variable>
                        <label id="{@id}-label-header" class="{$label-classes}">
                            <!-- Needed for IE and Chrome to size the label -->
                            <xsl:call-template name="copy-style-attribute"/>
                            <xsl:value-of select="xf:label/@header"/>
                        </label>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:variable name="label-classes">
                            <xsl:call-template name="assemble-label-classes"/>
                        </xsl:variable>

                        <label id="{@id}-label" class="{$label-classes}">
                            <xsl:attribute name="title">
                                <xsl:call-template name="create-label">
                                    <xsl:with-param name="label-elements" select="xf:label"/>
                                </xsl:call-template>
                            </xsl:attribute>

                            <!-- Needed for IE and Chrome to size the label
                            -->
                            <xsl:call-template name="copy-style-attribute"/>
                            <xsl:call-template name="create-label">
                                <xsl:with-param name="label-elements" select="xf:label"/>
                            </xsl:call-template>
                        </label>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
        </xsl:for-each>
    </xsl:template>

    <xsl:template name="processCompactPrototype">
        <xsl:param name="id"/>

        <table style="display:none;">
            <tr class="xfRepeatHeader">
                <!-- build table header -->
                <!-- <xsl:for-each select="bf:data/xf:group[@appearance='repeated'][1]"> -->
                <!-- Don´t use Prototype for RepeatHeader but first Repeatitem -->
                <xsl:for-each select="xf:group[@appearance='repeated'][1]">
                    <xsl:call-template name="processCompactHeader"/>
                </xsl:for-each>
            </tr>
            <tr id="{$id}-prototype" class="xfRepeatPrototype xfDisabled xfReadWrite xfOptional xfValid">
                <xsl:for-each select="xf:*">
                    <xsl:variable name="col-classes">
                        <xsl:choose>
                            <xsl:when test="./bf:data/@enabled='false'">
                                <xsl:value-of select="concat('appBfTableCol-', position(), ' bfTableCol-',position(),' ','xfDisabled')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('appBfTableCol-',position(),' bfTableCol-',position())"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    <td valign="top" class="{$col-classes}">
                        <xsl:apply-templates select="." mode="repeated-compact-prototype"/>
                    </td>
                </xsl:for-each>
            </tr>
        </table>
    </xsl:template>

    <xsl:template
            match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:upload|xf:trigger|xf:submit"
            mode="repeated-compact-prototype" priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="incrementaldelay">
            <xsl:value-of select="if (exists(@incremental-delay)) then @incremental-delay else 'undef'"/>
        </xsl:variable>

        <xsl:element name="span">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="data-bf-class" select="$control-classes"/>
            <xsl:if test="$incrementaldelay ne 'undef'">
                <xsl:message>
                    <xsl:value-of select="concat(' incremental-delay: ', $incrementaldelay)"/>
                </xsl:message>
                <xsl:attribute name="delay" select="$incrementaldelay"/>
            </xsl:if>

            <xsl:call-template name="copy-style-attribute"/>

            <xsl:if test="exists(@mediatype)">
                <xsl:attribute name="mediatype" select="@mediatype"/>
            </xsl:if>

            <span class="widgetContainer">
                <xsl:call-template name="buildControl"/>
                <xsl:apply-templates select="xf:alert"/>
                <xsl:apply-templates select="xf:hint"/>
                <xsl:apply-templates select="xf:help"/>
            </span>
        </xsl:element>

    </xsl:template>

    <xsl:template match="xf:group" mode="repeated-compact-prototype" priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
                <xsl:with-param name="isContainer" select="true()"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:variable name="appearance" select="@appearance"/>

        <xsl:element name="span">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="class" select="concat($control-classes,' xfRepeated')"/>
            <xsl:attribute name="controlType" select="local-name()"/>
            <!--<xsl:attribute name="appearance" select="$appearance"/>-->
            <xsl:call-template name="copy-style-attribute"/>
            <!-- prevent xf:label for groups within compact repeat-->
            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-compact-prototype"/>
        </xsl:element>

    </xsl:template>

    <xsl:template match="xf:group[@appearance='bf:verticalTable']" priority="15" mode="repeated-compact-prototype">
        <xsl:variable name="group-id" select="@id"/>

        <xsl:variable name="mip-classes">
            <xsl:call-template name="get-mip-classes"/>
        </xsl:variable>

        <table cellspacing="0" cellpadding="0" class="xfContainer xfGroup appBfVerticalTable bfVerticalTable {$mip-classes}" id="{$group-id}">
            <xsl:if test="exists(xf:label)">
                <caption class="xfGroupLabel">
                    <xsl:apply-templates select="./xf:label"/>
                    <xsl:apply-templates select="xf:alert"/>
                </caption>
            </xsl:if>
            <tbody>
                <xsl:for-each select="*[not(local-name()='label')]">
                    <xsl:choose>
                        <!-- if we got a group with appearance bf:GroupLabelLeft we put the label
                of the first control into the lefthand column -->
                        <xsl:when test="local-name()='group' and ./@appearance='bf:GroupLabelLeft'">
                            <tr class="appBfGroupLabelLeft bfGroupLabelLeft">
                                <td>
                                    <!-- use the label of the nested group for the left column -->
                                    <xsl:value-of select="xf:label"/>
                                </td>
                                <td>
                                    <xsl:apply-templates select="." mode="repeated-compact-prototype"/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:when test="local-name()='group' or local-name()='repeat' or local-name()='switch'">
                            <tr>
                                <td colspan="3">
                                    <xsl:apply-templates select="." mode="repeated-compact-prototype"/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:when test="namespace-uri()='http://www.w3.org/1999/xhtml'">
                            <tr>
                                <td colspan="3">
                                    <xsl:apply-templates select="." mode="repeated-compact-prototype"/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:if test="exists(node())">
                                <tr>
                                    <td class="bfVerticalTableLabel" valign="top">
                                        <xsl:variable name="label-classes">
                                            <xsl:call-template name="assemble-label-classes"/>
                                        </xsl:variable>
                                        <xsl:if test="local-name(.) != 'trigger'">
                                            <label id="{@id}-label" for="{@id}-value" class="{$label-classes}">
                                                <xsl:apply-templates select="xf:label"/>
                                            </label>
                                        </xsl:if>
                                    </td>
                                    <td class="bfVerticalTableValue" valign="top">
                                        <xsl:apply-templates select="." mode="table"/>
                                    </td>
                                    <td class="bfVerticalTableInfo" valign="top">
                                        <xsl:apply-templates select="xf:alert"/>
                                        <xsl:apply-templates select="xf:hint"/>
                                        <xsl:apply-templates select="xf:help"/>
                                        <span class="info" style="display:none;" id="{concat(@id,'-info')}">ok</span>
                                    </td>
                                </tr>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>
    
    <xsl:template match="xf:group[@appearance='bf:verticalTable']" priority="15" mode="repeated-full-prototype">
        <xsl:variable name="group-id" select="@id"/>

        <xsl:variable name="mip-classes">
            <xsl:call-template name="get-mip-classes"/>
        </xsl:variable>

        <table cellspacing="0" cellpadding="0" class="xfContainer xfGroup appBfVerticalTable bfVerticalTable {$mip-classes}" id="{$group-id}">
            <xsl:if test="exists(xf:label)">
                <caption class="xfGroupLabel">
                    <xsl:apply-templates select="./xf:label"/>
                    <xsl:apply-templates select="xf:alert"/>
                </caption>
            </xsl:if>
            <tbody>
                <xsl:for-each select="*[not(local-name()='label')]">
                    <xsl:choose>
                        <!-- if we got a group with appearance bf:GroupLabelLeft we put the label
                of the first control into the lefthand column -->
                        <xsl:when test="local-name()='group' and ./@appearance='bf:GroupLabelLeft'">
                            <tr class="appBfGroupLabelLeft bfGroupLabelLeft">
                                <td>
                                    <!-- use the label of the nested group for the left column -->
                                    <xsl:value-of select="xf:label"/>
                                </td>
                                <td>
                                    <xsl:apply-templates select="." mode="repeated-full-prototype"/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:when test="local-name()='group' or local-name()='repeat' or local-name()='switch'">
                            <tr>
                                <td colspan="3">
                                    <xsl:apply-templates select="." mode="repeated-full-prototype"/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:when test="namespace-uri()='http://www.w3.org/1999/xhtml'">
                            <tr>
                                <td colspan="3">
                                    <xsl:apply-templates select="." mode="repeated-full-prototype"/>
                                </td>
                            </tr>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:if test="exists(node())">
                                <tr>
                                    <td class="bfVerticalTableLabel" valign="top">
                                        <xsl:variable name="label-classes">
                                            <xsl:call-template name="assemble-label-classes"/>
                                        </xsl:variable>
                                        <xsl:if test="local-name(.) != 'trigger'">
                                            <label id="{@id}-label" for="{@id}-value" class="{$label-classes}">
                                                <xsl:apply-templates select="xf:label"/>
                                            </label>
                                        </xsl:if>
                                    </td>
                                    <td class="bfVerticalTableValue" valign="top">
                                        <xsl:apply-templates select="." mode="table"/>
                                    </td>
                                    <td class="bfVerticalTableInfo" valign="top">
                                        <xsl:apply-templates select="xf:alert"/>
                                        <xsl:apply-templates select="xf:hint"/>
                                        <xsl:apply-templates select="xf:help"/>
                                        <span class="info" style="display:none;" id="{concat(@id,'-info')}">ok</span>
                                    </td>
                                </tr>
                            </xsl:if>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template match="xf:group[@appearance='bf:horizontalTable']" priority="15" name="horizontalTable" mode="repeated-compact-prototype">
        <xsl:variable name="mip-classes">
            <xsl:call-template name="get-mip-classes"/>
        </xsl:variable>

        <xsl:message>$$$$$$$$$$$$ Count of children: <xsl:value-of select="bf:childCount(.)"/></xsl:message>
        <!--
                <xsl:message>$$$$$$$$$$$$ Count of label childs: <xsl:value-of select="count(child::xf:label)"/> </xsl:message>
                <xsl:message>$$$$$$$$$$$$ Count of text() childs: <xsl:value-of select="count(child::text())"/> </xsl:message>
        -->

        <!-- todo: should we really have appBFHorizontalTable AND bfHorizontalTable ? -->
        <table id="{@id}" class="xfContainer xfGroup appBfHorizontalTable bfHorizontalTable {$mip-classes}">

            <xsl:message>$$$$$$$$ has group label:<xsl:value-of select="bf:hasGroupLabel(.)"/></xsl:message>

            <!-- todo: need a hook to integrate xf:alert for horizontal group -->
            <xsl:if test="bf:hasGroupLabel(.)=true()">
                <tr>
                    <td colspan="{bf:childCount(.)}" class="xfGroupLabel">
                        <xsl:if test="exists(xf:label) and @appearance !='bf:GroupLabelLeft'">
                            <xsl:apply-templates select="./xf:label"/>
                        </xsl:if>
                    </td>
                </tr>
            </xsl:if>


            <tr>
                <!--<xsl:for-each select="*[position() &gt; 1]">-->
                <xsl:for-each select="*">
                    <xsl:if test="bf:isGroupOutput(.)=true()">
                        <td class="appBfHorizontalTableLabel bfHorizontalTableLabel  appBfTableCol{position()} bfTableCol{position()}">
                            <xsl:if test="local-name(.) != 'trigger'">
                                <label id="{@id}-label" for="{@id}-value" class="appBfTableLabel bfTableLabel">
                                    <xsl:apply-templates select="xf:label"/>
                                </label>
                            </xsl:if>
                        </td>
                    </xsl:if>
                </xsl:for-each>
            </tr>
            <tr>
                <xsl:for-each select="*">
                    <xsl:if test="bf:isGroupOutput(.)=true()">
                        <td class="appBfHorizontalTableValue bfHorizontalTableValue">
                            <xsl:apply-templates select="." mode="repeated-compact-prototype"/>
                        </td>
                    </xsl:if>
                </xsl:for-each>
            </tr>
        </table>
    </xsl:template>
    <xsl:template match="xf:group[@appearance='bf:horizontalTable']" priority="15" name="horizontalTable" mode="repeated-full-prototype">
        <xsl:variable name="mip-classes">
            <xsl:call-template name="get-mip-classes"/>
        </xsl:variable>

        <xsl:message>$$$$$$$$$$$$ Count of children: <xsl:value-of select="bf:childCount(.)"/></xsl:message>
        <!--
                <xsl:message>$$$$$$$$$$$$ Count of label childs: <xsl:value-of select="count(child::xf:label)"/> </xsl:message>
                <xsl:message>$$$$$$$$$$$$ Count of text() childs: <xsl:value-of select="count(child::text())"/> </xsl:message>
        -->

        <!-- todo: should we really have appBFHorizontalTable AND bfHorizontalTable ? -->
        <table id="{@id}" class="xfContainer xfGroup appBfHorizontalTable bfHorizontalTable {$mip-classes}">

            <xsl:message>$$$$$$$$ has group label:<xsl:value-of select="bf:hasGroupLabel(.)"/></xsl:message>

            <!-- todo: need a hook to integrate xf:alert for horizontal group -->
            <xsl:if test="bf:hasGroupLabel(.)=true()">
                <tr>
                    <td colspan="{bf:childCount(.)}" class="xfGroupLabel">
                        <xsl:if test="exists(xf:label) and @appearance !='bf:GroupLabelLeft'">
                            <xsl:apply-templates select="./xf:label"/>
                        </xsl:if>
                    </td>
                </tr>
            </xsl:if>


            <tr>
                <!--<xsl:for-each select="*[position() &gt; 1]">-->
                <xsl:for-each select="*">
                    <xsl:if test="bf:isGroupOutput(.)=true()">
                        <td class="appBfHorizontalTableLabel bfHorizontalTableLabel  appBfTableCol{position()} bfTableCol{position()}">
                            <xsl:if test="local-name(.) != 'trigger'">
                                <label id="{@id}-label" for="{@id}-value" class="appBfTableLabel bfTableLabel">
                                    <xsl:apply-templates select="xf:label"/>
                                </label>
                            </xsl:if>
                        </td>
                    </xsl:if>
                </xsl:for-each>
            </tr>
            <tr>
                <xsl:for-each select="*">
                    <xsl:if test="bf:isGroupOutput(.)=true()">
                        <td class="appBfHorizontalTableValue bfHorizontalTableValue">
                            <xsl:apply-templates select="." mode="repeated-full-prototype"/>
                        </td>
                    </xsl:if>
                </xsl:for-each>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="xf:switch" mode="repeated-compact-prototype" priority="10">
        <xsl:variable name="switch-id" select="@id"/>
        <xsl:variable name="switch-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>
        <div id="{$switch-id}" class="{$switch-classes}">
            <xsl:apply-templates mode="repeated-compact-prototype"/>
        </div>
    </xsl:template>

    <xsl:template match="xf:case[bf:data/@selected='true']" mode="repeated-compact-prototype" priority="10">
        <xsl:variable name="case-id" select="@id"/>
        <xsl:variable name="case-classes" select="'xfCase xfSelectedCase'"/>

        <div id="{$case-id}" class="{$case-classes}">
            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-compact-prototype"/>
        </div>
    </xsl:template>

    <xsl:template match="xf:case" mode="repeated-compact-prototype" priority="10">
        <!-- render only in scripted environment -->
        <xsl:variable name="case-id" select="@id"/>
        <xsl:variable name="case-classes" select="'xfCase xfDeselectedCase'"/>

        <div id="{$case-id}" class="{$case-classes}">
            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-compact-prototype"/>
        </div>
    </xsl:template>

    <xsl:template match="xf:repeat" mode="repeated-compact-prototype" priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="repeat-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="'full'"/>
            </xsl:call-template>
        </xsl:variable>

        <table id="{$id}"
               dojoAttachEvent='onfocus:_onFocus' data-bf-class="{$repeat-classes}">
            <tr class="xfRepeatHeader">
                <xsl:call-template name="processCompactHeader"/>
            </tr>
        </table>
    </xsl:template>

    <xsl:template name="processCompactChildren">
        <xsl:for-each select="xf:*|bfc:*">
            <!--
            Seems that setting the 'xfDisabled' class here is wrong as this should be handled on the level
            of the XFControl itself. Further tests are necessary to verify that.

            The purpose of the author seems to have been to allow complete cols to become non-relevant but
            cols have no MIPs of their own so this is problematic as it requires the client side code to handle
            specific cases for 'compact' repeats.
            -->
<!--
            <xsl:variable name="col-classes">
                <xsl:choose>
                    <xsl:when test="./bf:data/@enabled='false'">
                        <xsl:value-of select="concat('appBfTableCol-', position(), ' bfTableCol-',position(),' ','xfDisabled')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('appBfTableCol-', position(), ' bfTableCol-',position())"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
-->
            <xsl:variable name="col-classes">
                <xsl:value-of select="concat('appBfTableCol-', position(), ' bfTableCol-',position())"/>
            </xsl:variable>

            <td valign="top" class="{$col-classes}">
                <xsl:apply-templates select="." mode="compact-repeat"/>
            </td>
        </xsl:for-each>
    </xsl:template>

    <xsl:template
            match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:upload"
            mode="compact-repeat">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <div id="{$id}" class="{$control-classes} xfRepeated">
            <xsl:call-template name="copy-style-attribute"/>
            <label for="{$id}-value" id="{$id}-label" style="display:none">
                <xsl:call-template name="create-label">
                    <xsl:with-param name="label-elements" select="xf:label"/>
                </xsl:call-template>
            </label>
            <span class="widgetContainer">
                <xsl:call-template name="buildControl"/>
                <xsl:apply-templates select="xf:alert"/>
                <xsl:apply-templates select="xf:hint"/>
                <xsl:apply-templates select="xf:help"/>
            </span>

        </div>
    </xsl:template>

    <xsl:template match="xf:group" mode="compact-repeat">
        <xsl:variable name="group-id" select="@id"/>
        <xsl:variable name="group-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:call-template name="group-body">
            <xsl:with-param name="group-id" select="$group-id"/>
            <xsl:with-param name="group-classes" select="$group-classes"/>
            <xsl:with-param name="group-label" select="false()"/>
        </xsl:call-template>
    </xsl:template>

    <xsl:template match="bfc:dialog" mode="compact-repeat">
        <xsl:call-template name="dialog"/>
    </xsl:template>


    <!-- default templates for compact repeat -->
    <xsl:template match="xf:*" mode="compact-repeat">
        <xsl:apply-templates select="."/>
    </xsl:template>

    <!-- ######################################################################################################## -->
    <!-- ####################################### REPEAT ATTRIBUTES ############################################## -->
    <!-- ######################################################################################################## -->
    <!--
        this template handles complex tables with several tr elements in *one* repeat item.
    -->
    <xsl:template match="xhtml:table[exists(xhtml:tbody[@xf:repeat-ref]/xf:group[count(xhtml:tr) &gt; 1])]" priority="10">
        <xsl:call-template name="repeat-attribute"/>
    </xsl:template>

    <xsl:template match="xhtml:table[exists(xhtml:tbody[@xf:repeat-nodeset]/xf:group[count(xhtml:tr) &gt;= 1])]" priority="10" name="repeat-attribute">
        <xsl:variable name="repeat-id" select="xhtml:tbody/@id"/>
        <xsl:variable name="repeat-index" select="xhtml:tbody/bf:data/@index"/>
        <xsl:variable name="repeat-classes">
            <xsl:call-template name="assemble-compound-classes"/>
        </xsl:variable>
        
        <table>
            <xsl:copy-of select="@*"/>
            <xsl:attribute name="id"><xsl:value-of select="$repeat-id"/></xsl:attribute>
            <xsl:attribute name="jsId"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="class">xfRepeat <xsl:value-of select="$repeat-classes"/></xsl:attribute>

            <xsl:if test="exists(xhtml:thead)">
                <xsl:copy-of select="xhtml:thead"/>
            </xsl:if>

            <xsl:for-each select="xhtml:tbody/bf:data/xf:group[@appearance='repeated'][1]">
                <tbody>
                    <xsl:attribute name="id"><xsl:value-of select="$repeat-id"/>-prototype</xsl:attribute>
                    <xsl:attribute name="class">xfRepeatPrototype xfDisabled xfReadWrite xfOptional xfValid</xsl:attribute>
                    <xsl:apply-templates select="*" />
                </tbody>
            </xsl:for-each>
            <!-- todo: review the lines below - can this work? -->
            <xsl:for-each select="xhtml:tbody/bf:data/xf:group[@appearance='repeated']//xf:repeat">
                <xsl:call-template name="processRepeatPrototype"/>
            </xsl:for-each>
            <xsl:for-each select="xhtml:tbody/bf:data/xf:group[@appearance='repeated']//xf:itemset">
                <xsl:call-template name="processItemsetPrototype"/>
            </xsl:for-each>
             
            <xsl:for-each select="xhtml:tbody/xf:group[@appearance='repeated']">
                <xsl:variable name="id" select="@id"/>

                <xsl:variable name="repeat-item-classes">
                    <xsl:call-template name="assemble-repeat-item-classes">
                        <xsl:with-param name="selected" select="$repeat-index=position()"/>
                    </xsl:call-template>
                </xsl:variable>


                <tbody>
                    <xsl:attribute name="class" select="$repeat-item-classes"/>
                    <xsl:for-each select="xhtml:*">

                        <xsl:element name="{name(.)}">
                            <!--<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>-->

                            <xsl:apply-templates select="*" />

                        </xsl:element>
                    </xsl:for-each>
                </tbody>
            </xsl:for-each>

        </table>
    </xsl:template>



    <xsl:template match="*[@xf:repeat-bind|@xf:repeat-ref|@xf:repeat-nodeset]">
        <xsl:variable name="repeat-id" select="@id"/>
        <xsl:variable name="repeat-index" select="bf:data/@index"/>
        <xsl:variable name="repeat-classes">
            <xsl:call-template name="assemble-compound-classes"/>
        </xsl:variable>

        <xsl:element name="{local-name(.)}" namespace="">
            <xsl:attribute name="id"><xsl:value-of select="$repeat-id"/></xsl:attribute>
            <xsl:attribute name="jsId"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="class">xfRepeat <xsl:value-of select="$repeat-classes"/></xsl:attribute>
            <xsl:copy-of select="@*"/>

            <xsl:if test="not(ancestor::xf:repeat)">
                <!-- generate prototype(s) for scripted environment -->
                <xsl:for-each select="bf:data/xf:group[@appearance='repeated'][1]">
                    <xsl:for-each select="*">
                        <xsl:element name="{local-name(.)}" namespace="">
                            <xsl:attribute name="id"><xsl:value-of select="$repeat-id"/>-prototype</xsl:attribute>
                            <xsl:attribute name="class">xfRepeatPrototype xfDisabled xfReadWrite xfOptional xfValid</xsl:attribute>
                            <xsl:apply-templates select="*" />
                        </xsl:element>
                    </xsl:for-each>
                </xsl:for-each>
                <xsl:for-each select="bf:data/xf:group[@appearance='repeated']//xf:repeat">
                    <xsl:call-template name="processRepeatPrototype"/>
                </xsl:for-each>
                <xsl:for-each select="bf:data/xf:group[@appearance='repeated']//xf:itemset">
                    <xsl:call-template name="processItemsetPrototype"/>
                </xsl:for-each>
            </xsl:if>

            <xsl:for-each select="xf:group[@appearance='repeated']">
                <xsl:variable name="id" select="@id"/>

                <xsl:variable name="repeat-item-classes">
                    <xsl:call-template name="assemble-repeat-item-classes">
                        <xsl:with-param name="selected" select="$repeat-index=position()"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:for-each select="*">

                    <xsl:element name="{name(.)}">
                        <!--<xsl:attribute name="id"><xsl:value-of select="generate-id()"/></xsl:attribute>-->
                        <xsl:attribute name="class" select="$repeat-item-classes"/>
                        <xsl:apply-templates select="*" />

                    </xsl:element>
                </xsl:for-each>

            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template name="processTableRepeatPrototype">
        <xsl:param name="id"/>
        <xsl:variable name="col-classes">
            <xsl:choose>
                <xsl:when test="./bf:data/@enabled='false'">
                    <xsl:value-of select="concat('appBfTableCol-', position(), ' bfTableCol-',position(),' ','xfDisabled')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="concat('appBfTableCol-', position(), ' bfTableCol-',position())"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:for-each select="*">
            <xsl:element name="{local-name(.)}" namespace="">
                <xsl:attribute name="id"><xsl:value-of select="$id"/>-prototype</xsl:attribute>
                <xsl:attribute name="class">xfRepeatPrototype xfDisabled xfReadWrite xfOptional xfValid<xsl:value-of select="$col-classes"/></xsl:attribute>
                <xsl:apply-templates select="*" mode="repeated-compact-prototype"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>


    <!--
        <xsl:template match="xf:repeat[@appearance='caRepeatedTab']">
            <xsl:variable name="repeat-id" select="@id"/>
            <xsl:variable name="repeat-index" select="bf:data/@index"/>
            <xsl:variable name="repeat-classes">
                <xsl:call-template name="assemble-compound-classes">
                    <xsl:with-param name="appearance" select="'caObjectContainer'"/>
                </xsl:call-template>
            </xsl:variable>

            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']">
                <xsl:call-template name="processFullPrototype">
                    <xsl:with-param name="id" select="$repeat-id"/>
                </xsl:call-template>
            </xsl:for-each>


            <div id="{$repeat-id}" class="{$repeat-classes}" dojoType="betterform.ui.container.RepeatTabContainer" doLayout="false">

                <xsl:for-each select="xf:group[@appearance='repeated']">
                    <xsl:variable name="repeat-item-id" select="@id"/>
                    <xsl:variable name="repeat-item-classes">
                        <xsl:call-template name="assemble-repeat-item-classes">
                            <xsl:with-param name="selected" select="$repeat-index=position()"/>
                        </xsl:call-template>
                    </xsl:variable>

                        <xsl:variable name="group-label" select="true()"/>

                        <div repeatItemId="{$repeat-item-id}"
                             class="{$repeat-item-classes}"
                             title="{.//xf:output[1]/bf:data}"
                             appearance="appFull full">
                            <div class="legend">
                                <xsl:choose>
                                    <xsl:when test="$group-label and xf:label">
                                        <xsl:attribute name="id">
                                            <xsl:value-of select="concat($repeat-item-id, '-label')"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="class">
                                            <xsl:call-template name="assemble-group-label-classes"/>
                                        </xsl:attribute>
                                        <xsl:apply-templates select="xf:label"/>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <xsl:attribute name="style">display:none;</xsl:attribute>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </div>

                            <xsl:apply-templates select="*[not(self::xf:label)]"/>
                        </div>

                </xsl:for-each>
            </div>
        </xsl:template>
    -->

</xsl:stylesheet>

