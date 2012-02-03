<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                version="2.0"
                exclude-result-prefixes="html"
                xml:base="http://localhost:8080/betterform/forms/incubator/editor/">
    <!-- author: Joern Turner -->
    <!-- author: Tobias Krebs -->
    <!-- author: Lars Windauer -->

    <!--
    todo: find a better approach than setting the url above in xml:base
    -->
    <!--
        FOUNDATION STYLESHEET FOR XFORMS EDITOR HOSTED ON NORMAL WEBAPP.
        THIS TRANSFORM MAY BE OVERWRITTEN FOR CERTAIN ENVIRONMENTS LIKE EXIST (see bfExistEditor.xsl)
    -->

    <!--
        xml:base="/betterform/forms/incubator/editor/">
        <xsl:variable name="EDITOR_HOME" select="'/forms/incubator/editor/'"/>
    -->
    <xsl:import href="bfEditorPanel.xsl"/>
    <xsl:import href="bfEditorModel.xsl"/>

    <xsl:output method="xml" indent="yes"/>

    <xsl:param name="APP_CONTEXT" select="''"/>
    <xsl:param name="filename" select="''"/>

    <xsl:param name="EDITOR_HOME" select="'/betterform/forms/incubator/editor/'" />
<!--
    <xsl:variable name="EDITOR_HOME" select="'/betterform/forms/incubator/editor/'" />
-->
    <xsl:include href="bfEditorMenu.xsl"/>
    <xsl:include href="bfEditorHelp.xsl"/>

    <xsl:strip-space elements="*"/>
    <xsl:template match="/">

        <xsl:message>WebApplication Context: <xsl:value-of select="$APP_CONTEXT"/></xsl:message>
        <xsl:message>Editor home directory: <xsl:value-of select="$EDITOR_HOME"/></xsl:message>
        <html>
            <head>
                <title>betterFORM Editor</title>
                <link rel="stylesheet" type="text/css" href="{$EDITOR_HOME}xforms-editor.css"/>
            </head>
            <body id="editor" jsId="xformsEditor">

                <div id="overlay">
                    <img src="{$EDITOR_HOME}images/loader.gif" style="margin-top:50px;" alt="loading..."/>
                </div>
                <!--################### editor model ################### -->
                <!--################### editor model ################### -->
                <!--################### editor model ################### -->
                <xsl:call-template name="editor-model"/>

                <!-- ################### DOJO Store holding XForms Datatypes ################### -->
                <!-- ################### DOJO Store holding XForms Datatypes ################### -->
                <!-- ################### DOJO Store holding XForms Datatypes ################### -->
                <div dojoType="dojo.data.ItemFileReadStore" data-dojo-id="stateStore" url="{$EDITOR_HOME}xfDatatype.json"/>


                <!-- ################### MAIN UI WITH COMPONENT TREE ################### -->
                <!-- ################### MAIN UI WITH COMPONENT TREE ################### -->
                <!-- ################### MAIN UI WITH COMPONENT TREE ################### -->
                <xsl:call-template name="main-ui" />

                <!-- ###########
                    this menu is created only once and placed by JS at the position of the currently
                    selected item.
                 ######### -->
                <xsl:call-template name="contextMenuBar"/>

                <!-- ################### HELP DIALOG FOR KEYBOARD SHORTCUTS ################### -->
                <!-- ################### HELP DIALOG FOR KEYBOARD SHORTCUTS ################### -->
                <!-- ################### HELP DIALOG FOR KEYBOARD SHORTCUTS ################### -->
                <xsl:call-template name="help-dialog"/>


                <div id="saveDialog" dojotype="dijit.Dialog" title="Save as ..." autofocus="false" style="width:820px;height:540px;overflow:auto;">
                    <div id="embedDialog"/>
                </div>

                <div style="display: none">
                    <xsl:copy-of select="document(concat($EDITOR_HOME, 'templates.html'))/div"/>
                    <xsl:copy-of select="document(concat($EDITOR_HOME, 'display.html'))"/>

                </div>
                <!-- ################### SCRIPTS ################### -->
                <!-- ################### SCRIPTS ################### -->
                <!-- ################### SCRIPTS ################### -->
                <!-- ################### SCRIPTS ################### -->
                <!-- ################### SCRIPTS ################### -->

                <script type="text/javascript"
                        src="{$EDITOR_HOME}/scripts/jstree_pre1.0_stable/_lib/jquery.js"></script>
                <script type="text/javascript"
                        src="{$EDITOR_HOME}/scripts/jstree_pre1.0_stable/_lib/jquery.cookie.js"></script>
                <script type="text/javascript"
                        src="{$EDITOR_HOME}/scripts/jstree_pre1.0_stable/_lib/jquery.hotkeys.js"></script>
                <script type="text/javascript"
                        src="{$EDITOR_HOME}/scripts/jstree_pre1.0_stable/jquery.jstree.js"></script>

                <!-- ##### set a global var that contains the path to the editor ##### -->
                <script type="text/javascript">
                    EDITOR_HOME = "<xsl:value-of select="$EDITOR_HOME"/>";
                </script>

                <!--##### using direct import here instead of dojo.require - see comment below! ##### -->
                <script type="text/javascript" src="{$EDITOR_HOME}/scripts/betterform/editor/Editor.js"></script>
                <script type="text/javascript" src="{$EDITOR_HOME}/scripts/betterform/editor/mainTree.js"></script>
                <script type="text/javascript" src="{$EDITOR_HOME}/scripts/componentTree.js"></script>

                <script type="text/javascript" defer="defer"
                        src="{$APP_CONTEXT}/bfResources/scripts/betterform/betterform-XFormsEditor.js"></script>
                <!-- ##### USING DIRECT IMPORT INSTEAD OF DOJO REQUIRE.
                The source of the editor should be kept separate from the core resources and deployed separately.
                They should NOT be made part of the betterform default distribution but be available as a separate
                package.
                -->
                <script type="text/javascript" defer="defer" src="{$EDITOR_HOME}/scripts/betterform/editor/xfEditorUtil.js"></script>
                <script type="text/javascript" defer="defer" src="{$EDITOR_HOME}/scripts/betterform/editor/bfEditor.js"></script>

                <!--
                <script type="text/javascript" defer="defer">
                    // <![CDATA[
                    dojo.require("dijit.Toolbar");
                    dojo.require("dijit.form.DropDownButton");
                    dojo.require("dijit.form.Button");
                    dojo.require("dijit.TooltipDialog");
                    dojo.require("dojo.data.ItemFileReadStore");
                    dojo.require("dojo.fx");

                    var xfReadySubscribers;
                    var displayPropertyMap = new Array();

                    function showSaveDialog() {
                        dijit.byId("saveDialog").show();
                        var embedDialog = dojo.byId("embedDialog");


                        if (xfReadySubscribers != undefined) {
                            dojo.unsubscribe(xfReadySubscribers);
                            xfReadySubscribers = null;
                        }

                        var xfReadySubscribers = dojo.subscribe("/xf/ready", function(data) {
                            dojo.fadeIn({
                                node: embedDialog,
                                duration:100
                            }).play();
                        });

                        dojo.fadeOut({
                            node: embedDialog,
                            duration:100,
                            onBegin: function() {
                                fluxProcessor.dispatchEvent("t-save-as");
                            }
                        }).play();

                    }


                    xformsEditor = new betterform.editor.Editor({}, "editor");
                    //console.debug("xformsEditor.: ",xformsEditor);

                    function checkKeyboardInput(pEvent) {
                        var activeElem = document.activeElement.localName;
                        // console.debug("activeElem: ", activeElem);
                        if (activeElem == "input") {
                            return;
                        }
                        // console.debug("activeElem: ", activeElem);
                        switch (pEvent.charOrCode) {
                            case '?': //Process the Help key event
                                dijit.byId("bfEditorHelp").show();
                                break;
                            case 't':
                            case 'T':
                                dojo.byId("root").focus();
                                break;

                            default:
                                //no defaults at this time
                                break;

                        }
                    }

                    function generateCSSLocator(start, attribute_xpath) {
                        var reference_locator = "";

                        //TODO: < !!
                        for (var i = 1; i != attribute_xpath.length; i++) {
                            reference_locator += " ." + attribute_xpath[i];
                        }

                        return reference_locator;
                    }

                    function setDisplayProps(node) {
                        //console.debug("Node:", node);
                        //console.debug("data-xf-attrs:", dojo.attr(node, 'data-xf-attrs'));

                        var propertyName = displayPropertyMap[dojo.attr(node, 'data-xf-type')];

                        var xfattrs = dojox.json.ref.fromJson(dojo.attr(node, 'data-xf-attrs'));

                        if (xfattrs[propertyName] != undefined) {
                            var span = dojo.query("a .displayProps", node)[0];
                            span.innerHTML = propertyName + " : " + xfattrs[propertyName];
                        }
                        //check ref, bind, model, idrefs

                        if (xfattrs['ref'] != undefined && xfattrs['ref'] != '' ) {
                            var reference = xfattrs['ref'];
                            var instance_locator = ".instance";
                            var reference_locator = "";

                            if (reference.indexOf('instance(\'') != -1) {
                                instance_locator = "#" + reference.substring(reference.indexOf('instance(\'') + 'instance(\''.length, reference.indexOf('\')'));
                                var reference_xpath = reference.split('/');
                                reference_locator = generateCSSLocator(1, reference_xpath);
                            } else if (reference.indexOf('instance()') != -1) {
                                instance_locator = ".model .instance:first-child"

                                var reference_xpath = reference.split('/');
                                reference_locator = generateCSSLocator(1, reference_xpath);
                            } else {
                                var reference_xpath = reference.split('/');
                                reference_locator = generateCSSLocator(0, reference_xpath);
                            }

                            console.debug(instance_locator + reference_locator);
                            if (dojo.query(instance_locator + reference_locator).length == 0) {
                                dojo.style(span, 'color', 'red');
                            } else {
                               dojo.style(span, 'color', 'green');
                            }
                        }
                    }

                    dojo.addOnLoad(
                            function() {
                                dojo.connect(dojo.body(), "onkeypress", checkKeyboardInput);
                            },

                            function() {
                                // hide the componentTree if a click outside a 'category' happens
                                dojo.connect(dojo.body(), "onclick", function(evt) {
                                    var ancestorClass = evt.target.parentNode.parentNode.className;
                                    if (ancestorClass != "category") {
                                        dojo.style("componentTree", "display", "none");
                                    }
                                });
                            },


                            dojo.behavior.add({
                                '.input, .output, .range, .secret, .select, .select1, .submit, .textarea, .trigger, .upload': function(n) {
                                    displayPropertyMap[dojo.attr(n, 'data-xf-type')] = 'ref';
                                    setDisplayProps(n);

                                },
                                '.bind': function(n) {
                                    displayPropertyMap[dojo.attr(n, 'data-xf-type')] = 'nodeset';
                                    setDisplayProps(n);
                                },
                                '.model, .instance, .group, .switch, .repeat, .submission': function(n) {
                                    displayPropertyMap[dojo.attr(n, 'data-xf-type')] = 'id';
                                    setDisplayProps(n);
                                }
                            }),

                            dojo.behavior.apply()
                    );

                    //]]>
                </script>
                -->

            </body>
        </html>
    </xsl:template>

    <xsl:template name="main-ui">
        <xsl:call-template name="editor-panel"/>
    </xsl:template>


    <!-- convert native XForms markup into a <ul><li> structure for jsTree -->
    <xsl:template match="xf:*">
        <xsl:variable name="this" select="."/>
        <!--
        ####################################################################################################
        create an id if there's none
        ####################################################################################################
        -->
        <xsl:variable name="id">
            <xsl:choose>
                <xsl:when test="exists(current()/@id) and string-length(current()/@id) != 0">
                    <xsl:value-of select="current()/@id"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="generate-id()"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <!--
        ####################################################################################################
        all attributes of the current xforms element are transferred into a 'data-xf-props' attribute
        ####################################################################################################
        -->
        <xsl:variable name="props">{
            <xsl:for-each select="@*">
                "<xsl:value-of select="local-name()"/>":"<xsl:value-of select="."/>"
                <xsl:if test="position()!=last()">,</xsl:if>
            </xsl:for-each>}
        </xsl:variable>

        <li tabindex="0" id="{$id}" data-xf-type="{local-name()}" data-xf-attrs="{ $props }" class="{local-name()} jstree-drop" rel="{local-name()}">
            <!--
            ####################################################################################################
            the outermost xforms elements found get the id of their parent node written to a 'xpath' attribute.
            This can later be used to 're-mount' the edited markup.
            ####################################################################################################
            -->
            <xsl:if test="count(ancestor::xf:*) = number(0)">
                <xsl:attribute name="oid">
                <xsl:value-of select="$this/@id"/>
                </xsl:attribute>
            </xsl:if>
            <a href="#">
                <span class="elementName"><xsl:value-of select="local-name()"/> </span>
                <xsl:if test="text()">
                    <span class="textNode">
                        <xsl:apply-templates select="*|text()"/>
                    </span>
                </xsl:if>

                <span class="displayProps"/>
                <span class="buttonWrapper">
<!--
                    <button tabindex="0" type="button" onclick="showCircleMenu(event);return false;" style="padding:0;margin:0;background:transparent;border:none;">
                        <img class="targetBtn" src="{$APP_CONTEXT}/bfResources/images/target.png" width="24" height="24" alt="x"/>
                    </button>
-->
                </span>
            </a>
            <xsl:choose>
                <xsl:when test="local-name() eq 'instance'">
                    <xsl:choose>
                        <xsl:when test="@src">
                            <xsl:variable name="external-instance">
                                <xsl:value-of select="concat(substring($filename,1,index-of(string-to-codepoints($filename),string-to-codepoints('/'))[last()] -1), '/', @src)"/>
                            </xsl:variable>
                            <ul>
                                <li>
                                    <a href="#" class="external-instance-notifier">External Instance. Not editable</a>
                                    <ul>
                                        <xsl:apply-templates select="document($external-instance)" mode="external-instance"/>
                                    </ul>
                                </li>
                            </ul>
                        </xsl:when>
                        <xsl:otherwise>
                            <ul>
                                <xsl:for-each select="*">
                                    <xsl:apply-templates select="." mode="instance">
                                        <xsl:with-param name="position" select="string(position())"/>
                                    </xsl:apply-templates>
                                </xsl:for-each>
                            </ul>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="count(xf:*) != 0">
                    <ul>
                        <xsl:for-each select="*">
                            <xsl:apply-templates select="."/>
                        </xsl:for-each>
                    </ul>
                </xsl:when>
            </xsl:choose>
        </li>


    </xsl:template>

    <xsl:template match="*|text()" mode="external-instance" priority="50">
        <li>
            <a href="#"><xsl:value-of select="local-name()"/></a>
             <xsl:if test="count(*) != 0">
                <ul>
                    <xsl:for-each select="*">
                        <xsl:apply-templates select="." mode="external-instance"/>
                    </xsl:for-each>
                </ul>
            </xsl:if>
        </li>
    </xsl:template>

     <xsl:template match="*|text()" mode="instance" priority="50">
         <xsl:param name="position"/>
         <xsl:variable name="nodeValue">
                 <xsl:value-of select="."/>
         </xsl:variable>

         <xsl:variable name="type">
             <xsl:choose>
                 <xsl:when test="$position eq '1'">instance-root</xsl:when>
                 <xsl:otherwise>instance-data</xsl:otherwise>
             </xsl:choose>
         </xsl:variable>

         <xsl:variable name="props">{
            <xsl:for-each select="@*">
                "<xsl:value-of select="local-name()"/>":"<xsl:value-of select="."/>"
                <xsl:if test="position()!=last()"> </xsl:if>
            </xsl:for-each>
             <xsl:if test="$type eq 'instance-data'"> "instancenodevalue":"<xsl:value-of select="."/>"</xsl:if>
           }
        </xsl:variable>

         <xsl:variable name="id" select="concat('ins-', local-name() , '-', $position)"></xsl:variable>
         <li tabindex="0" id="{$id}" data-xf-type="{$type}" nodename="{local-name()}" data-xf-attrs="{ $props }" class="{local-name()} jstree-drop" rel="{local-name()}">
             <a href="#">

                <span class="elementName"><xsl:value-of select="local-name()"/> </span>
                <span class="displayProps"/>
                <span class="buttonWrapper"/>
            </a>
            <xsl:if test="count(*) != 0">
                <ul>
                    <xsl:for-each select="*">
                        <xsl:apply-templates select="." mode="instance">
                            <xsl:with-param name="position" select="concat($position, '-', position())"/>
                        </xsl:apply-templates>
                    </xsl:for-each>
                </ul>
            </xsl:if>
        </li>
    </xsl:template>

    <xsl:template match="*|text()">
        <xsl:copy>
            <xsl:copy-of select="@*"/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <!-- <xsl:template match="xf:*/text()"/> -->

</xsl:stylesheet>
