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
    <!-- ####################################################################################################### -->

    <!-- ############################################ PARAMS ################################################### -->
    <!-- ############################################ VARIABLES ################################################ -->

    <xsl:import href="common-ui.xsl"/>
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
        <xsl:variable name="repeat-index" select="bf:data/@bf:index"/>

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
<!--
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']//xf:repeat">
                <xsl:call-template name="processRepeatPrototype"/>
            </xsl:for-each>
-->
            <xsl:for-each select="bf:data/xf:group[@appearance='repeated']//xf:itemset">
                <xsl:call-template name="processItemsetPrototype"/>
            </xsl:for-each>
        </xsl:if>


        <div repeatId="{$repeat-id}" class="{$repeat-classes}">
            <!-- loop repeat entries -->
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
                     appearance="appFull full"
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
            match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:upload"
            mode="repeated-full-prototype"
            priority="20">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <!--
                <xsl:variable name="htmlElem">
                    <xsl:choose>
                        <xsl:when test="local-name()='output'">span</xsl:when>
                        <xsl:otherwise>div</xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
        -->

        <xsl:element name="span">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="class" select="concat(substring-after($control-classes,'xfControl'),' xfRepeated bfPrototype')"/>
            <xsl:attribute name="appearance" select="@appearance"/>
            <xsl:attribute name="title" select="normalize-space(xf:hint)"/>

            <xsl:if test="exists(@mediatype)">
                <xsl:attribute name="mediatype" select="@mediatype"/>
            </xsl:if>
            <label class="xfLabel">
                <xsl:call-template name="create-label">
                    <xsl:with-param name="label-elements" select="xf:label"/>
                </xsl:call-template>
            </label>

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
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <div id="{$id}" class="{$control-classes} xfRepeated" appearance="{@appearance}" repeatId="{$id}">
            <xsl:apply-templates select="*" mode="repeated-full-prototype"/>
        </div>
    </xsl:template>

    <xsl:template match="xf:group"
                  mode="repeated-full-prototype"
                  priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="appearance" select="@appearance"/>

        <xsl:variable name="htmlElem">
            <xsl:choose>
                <xsl:when test="$appearance='minimal'">span</xsl:when>
                <xsl:otherwise>div</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="group-classes">
            <xsl:call-template name="assemble-compound-classes">
                <xsl:with-param name="appearance" select="@appearance"/>
            </xsl:call-template>
        </xsl:variable>

        <xsl:element name="{$htmlElem}">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="class" select="concat($group-classes,' xfRepeated dijitContentPane')"/>
            <xsl:attribute name="controlType" select="local-name()"/>
            <xsl:attribute name="appearance" select="$appearance"/>

            <xsl:element name="{$htmlElem}">
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


    <xsl:template match="xf:case[bf:data/@bf:selected='true']"
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
                <div id="{$id}" class="{$control-classes} xfRepeated" unbound="true">
                    <div id="{$id}-value" class="xfValue" appearance="{$appearance}" controlType="trigger"
                         label="{xf:label}" name="d_{$id}" title="" navindex="" accesskey="" source=""></div>
                </div>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>

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
        <xsl:variable name="repeat-index" select="bf:data/@bf:index"/>
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


        <table repeatId="{$repeat-id}"
               jsId="{$repeat-id}"
               class="{$repeat-classes}"
               appearance="appCompact compact"
               border="0"
               cellpadding="0"
               cellspacing="0"
                >
            <!-- build table header -->
            <!-- <xsl:for-each select="bf:data/xf:group[@appearance='repeated'][1]"> -->
            <!-- Don´t use Prototype for RepeatHeader but first Repeatitem -->
            <xsl:for-each select="xf:group[@appearance='repeated'][1]">
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

                <tr repeatItemId="{$id}"
                    class="{$repeat-item-classes}"
                    appearance="appCompact compact">
                    <xsl:call-template name="processCompactChildren"/>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>


    <xsl:template name="processCompactHeader">
        <xsl:for-each select="xf:*|bfc:*">
            <xsl:variable name="col-classes">
                <xsl:choose>
                    <xsl:when test="./bf:data/@bf:enabled='false'">
                        <xsl:value-of select="concat('appBfTableCol-', position(),' bfTableCol-',position(),' ','xfDisabled')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('appBfTableCol-',position(),' bfTableCol-',position())"/>
                    </xsl:otherwise>
                </xsl:choose>
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
                            <xsl:when test="./bf:data/@bf:enabled='false'">
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
            match="xf:input|xf:output|xf:range|xf:secret|xf:select|xf:select1|xf:textarea|xf:upload"
            mode="repeated-compact-prototype" priority="10">
        <xsl:variable name="id" select="@id"/>
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes"/>
        </xsl:variable>

        <xsl:variable name="htmlElem">
            <xsl:choose>
                <xsl:when test="local-name()='output'">span</xsl:when>
                <xsl:otherwise>div</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="incrementaldelay">
            <xsl:value-of select="if (exists(@bf:incremental-delay)) then @bf:incremental-delay else 'undef'"/>
        </xsl:variable>

        <xsl:element name="{$htmlElem}">
            <xsl:attribute name="id" select="$id"/>
            <!--<xsl:attribute name="class" select="concat($control-classes,' xfRepeated')"/>-->
            <xsl:attribute name="class" select="concat(substring-after($control-classes,'xfControl'),' xfRepeated bfPrototype')"/>
            <xsl:attribute name="appearance" select="@appearance"/>
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
            <xsl:call-template name="assemble-control-classes"/>
        </xsl:variable>
        <xsl:variable name="appearance" select="@appearance"/>

        <xsl:variable name="htmlElem">
            <xsl:choose>
                <xsl:when test="$appearance='minimal'">span</xsl:when>
                <xsl:otherwise>div</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>


        <xsl:element name="{$htmlElem}">
            <xsl:attribute name="id" select="$id"/>
            <xsl:attribute name="class" select="concat($control-classes,' xfRepeated')"/>
            <xsl:attribute name="controlType" select="local-name()"/>
            <xsl:attribute name="appearance" select="$appearance"/>
            <xsl:call-template name="copy-style-attribute"/>
            <!-- prevent xf:label for groups within compact repeat-->
            <xsl:apply-templates select="*[not(self::xf:label)]" mode="repeated-compact-prototype"/>
        </xsl:element>

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

    <xsl:template match="xf:case[bf:data/@bf:selected='true']" mode="repeated-compact-prototype" priority="10">
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
        <xsl:variable name="control-classes">
            <xsl:call-template name="assemble-control-classes"/>
        </xsl:variable>
        <table id="{$id}"
               dojoAttachEvent='onfocus:_onFocus' repeatId="{$id}">
            <tr class="xfRepeatHeader">
                <xsl:call-template name="processCompactHeader"/>
            </tr>
        </table>
    </xsl:template>

    <xsl:template match="xf:trigger" mode="repeated-compact-prototype" priority="10">
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
            <xsl:call-template name="assemble-control-classes"/>
        </xsl:variable>
        <xsl:choose>
            <xsl:when test="exists(@ref) or exists(@bind)">
                <div id="{$id}" class="{$control-classes} xfRepeated" controlType="{local-name()}"
                     appearance="{$appearance}">
                    <xsl:call-template name="create-label">
                        <xsl:with-param name="label-elements" select="xf:label"/>
                    </xsl:call-template>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <div id="{$id}" class="{$control-classes} xfRepeated" unbound="true">
                    <div id="{$id}-value" class="xfValue" appearance="{$appearance}" controlType="trigger"
                         label="{xf:label}" name="d_{$id}" title="" navindex="" accesskey="" source=""></div>
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="processCompactChildren">
        <xsl:for-each select="xf:*|bfc:*">
            <xsl:variable name="col-classes">
                <xsl:choose>
                    <xsl:when test="./bf:data/@bf:enabled='false'">
                        <xsl:value-of select="concat('appBfTableCol-', position(), ' bfTableCol-',position(),' ','xfDisabled')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat('appBfTableCol-', position(), ' bfTableCol-',position())"/>
                    </xsl:otherwise>
                </xsl:choose>
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
            <xsl:call-template name="assemble-control-classes"/>
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
    <xsl:template match="*[@xf:repeat-bind]|*[@xf:repeat-nodeset]|@repeat-bind|@repeat-nodeset"
                  name="generic-repeat">
        <xsl:variable name="repeat-id" select="@id"/>
        <xsl:variable name="repeat-index" select="bf:data/@bf:index"/>
        <xsl:variable name="repeat-classes">
            <xsl:call-template name="assemble-compound-classes"/>
        </xsl:variable>

        <xsl:element name="{local-name(.)}" namespace="">
            <xsl:attribute name="repeatId"><xsl:value-of select="$repeat-id"/></xsl:attribute>
            <xsl:attribute name="jsId"><xsl:value-of select="@id"/></xsl:attribute>
            <xsl:attribute name="class"><xsl:value-of select="$repeat-classes"/></xsl:attribute>
            <xsl:copy-of select="@*"/>

            <xsl:if test="not(ancestor::xf:repeat)">
                <!-- generate prototype(s) for scripted environment -->
                <xsl:for-each select="bf:data/xf:group[@appearance='repeated']">
                    <xsl:call-template name="processTableRepeatPrototype">
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

            <xsl:for-each select="xf:group[@appearance='repeated']">
                <xsl:variable name="id" select="@id"/>

                <xsl:variable name="repeat-item-classes">
                    <xsl:call-template name="assemble-repeat-item-classes">
                        <xsl:with-param name="selected" select="$repeat-index=position()"/>
                    </xsl:call-template>
                </xsl:variable>

                <xsl:for-each select="xhtml:tr">

                    <tr repeatItemId="{$id}"
                        class="{$repeat-item-classes}"
                        appearance="appCompact compact">
                        <xsl:apply-templates select="*" mode="compact-repeat"/>
                    </tr>
                </xsl:for-each>
            </xsl:for-each>
        </xsl:element>
    </xsl:template>

    <xsl:template name="processTableRepeatPrototype">
        <xsl:param name="id"/>
        <xsl:variable name="col-classes">
            <xsl:choose>
                <xsl:when test="./bf:data/@bf:enabled='false'">
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

    <!--
        <xsl:template match="xf:repeat[@appearance='caRepeatedTab']">
            <xsl:variable name="repeat-id" select="@id"/>
            <xsl:variable name="repeat-index" select="bf:data/@bf:index"/>
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


            <div id="{$repeat-id}" repeatId="{$repeat-id}" class="{$repeat-classes}" dojoType="betterform.ui.container.RepeatTabContainer" doLayout="false">

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

