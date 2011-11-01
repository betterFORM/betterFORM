<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:ev="http://www.w3.org/2001/xml-events"
        xmlns:bf="http://betterform.sourceforge.net/xforms"
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

    <xsl:variable name="EDITOR_HOME" select="'/betterform/forms/incubator/editor/'" />

    <xsl:include href="bfEditorMenu.xsl"/>
    <xsl:include href="bfEditorHelp.xsl"/>

    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:message>WebApplication Context: <xsl:value-of select="$APP_CONTEXT"/></xsl:message>
        <xsl:message>Editor home directory: <xsl:value-of select="$EDITOR_HOME"/></xsl:message>
        <html>
            <head>
                <title>betterFORM Editor</title>
                <link rel="stylesheet" type="text/css" href="../../../bfResources/styles/xforms-editor.css"/>
                <link rel="stylesheet" media="screen" href="../../../bfResources/scripts/piemenu/css/jquery.ui.ppmenu.css" type="text/css" />
                <link rel="stylesheet" media="screen" href="../../../bfResources/scripts/piemenu/css/jquery-ui-1.8.5.custom.css" type="text/css" />

            </head>
            <body id="editor" class="bf" jsId="attrEditor">

                <!--################### editor model ################### -->
                <!--################### editor model ################### -->
                <!--################### editor model ################### -->
                <xsl:call-template name="editor-model"/>

                <!-- ################### DOJO Store holding XForms Datatypes ################### -->
                <!-- ################### DOJO Store holding XForms Datatypes ################### -->
                <!-- ################### DOJO Store holding XForms Datatypes ################### -->
                <div dojoType="dojo.data.ItemFileReadStore" data-dojo-id="stateStore" url="{$APP_CONTEXT}{$EDITOR_HOME}xfDatatype.json"/>


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


                <div id="saveDialog" dojotype="dijit.Dialog" title="save as ..." autofocus="false" style="width:820px;height:540px;overflow:auto;">
                    <div id="embedDialog"/>
                </div>

                <span id="circleMenu" class="circleMenu"></span>

                <!-- ################### SCRIPTS ################### -->
                <!-- ################### SCRIPTS ################### -->
                <!-- ################### SCRIPTS ################### -->
                <!-- ################### SCRIPTS ################### -->
                <!-- ################### SCRIPTS ################### -->

                <script type="text/javascript"
                        src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.js"></script>
                <script type="text/javascript"
                        src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.cookie.js"></script>
                <script type="text/javascript"
                        src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.hotkeys.js"></script>
                <script type="text/javascript"
                        src="../../../bfResources/scripts/jstree_pre1.0_stable/jquery.jstree.js"></script>

<!--
                <script type="text/javascript" src="../../../bfResources/scripts/piemenu/ppmenu/jquery-ui-1.8.8.custom.min.js"></script>
                <script type="text/javascript" src="../../../bfResources/scripts/piemenu/ppmenu/jquery.ui.prettypiemenu.js"></script>
-->


                <script type="text/javascript" class="source below">
                    EDITOR_HOME = "<xsl:value-of select="$EDITOR_HOME"/>";
                </script>

                <!--<script type="text/javascript" src="../../../bfResources/scripts/betterform/editor/mainTree.js"></script>-->
                <!--<script type="text/javascript" defer="defer" src="../../../bfResources/scripts/betterform/editor/circleMenuCommon.js"></script>-->
                <!--<script type="text/javascript" defer="defer" src="../../../bfResources/scripts/betterform/editor/circleMenus.js"></script>-->
                <script type="text/javascript" src="../../../bfResources/scripts/betterform/editor/mainTree.js"></script>
                <script type="text/javascript" src="../../../bfResources/scripts/betterform/editor/componentTree.js"></script>
                <script type="text/javascript" defer="defer"
                        src="../../../bfResources/scripts/betterform/betterform-XFormsEditor.js"></script>
                <script type="text/javascript" defer="defer" src="../../../bfResources/scripts/betterform/editor/addMenu.js"></script>
                <script type="text/javascript" defer="defer">
                    // do not do anything but logging yet but shows the right call. Should work on FF and webkit which
                    // is sufficient for the editor.
                     dojo.require("dijit.Toolbar");
                    dojo.require("dijit.form.DropDownButton");
                    dojo.require("dijit.form.Button");
                    dojo.require("dijit.TooltipDialog");

                    var xfReadySubscribers;


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


                    attrEditor = new betterform.editor.Editor({}, "editor");
                    //console.debug("attrEditor.: ",attrEditor);

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

                    dojo.addOnLoad(
                            function() {
                                dojo.connect(dojo.body(), "onkeypress", checkKeyboardInput);
                            }
                    );
                </script>
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
                <xsl:value-of select="local-name()"/>:'<xsl:value-of select="."/>'
                <xsl:if test="position()!=last()">,</xsl:if>
            </xsl:for-each>
        </xsl:variable>

        <li tabindex="0" id="{$id}" data-xf-type="{local-name()}" data-xf-attrs="{$props}" class="{local-name()}" rel="{local-name()}">
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
                <xsl:value-of select="local-name()"/>:
                <xsl:if test="text()">
                    <span class="textNode">
                        <xsl:apply-templates select="*|text()"/>
                    </span>
                </xsl:if>
                <xsl:value-of select="@id"/>
                <span class="buttonWrapper">
<!--
                    <button tabindex="0" type="button" onclick="showCircleMenu(event);return false;" style="padding:0;margin:0;background:transparent;border:none;">
                        <img class="targetBtn" src="{$APP_CONTEXT}/bfResources/images/target.png" width="24" height="24" alt="x"/>
                    </button>
-->
                </span>
            </a>
            <xsl:if test="count(xf:*) != 0">
                <ul>
                    <xsl:for-each select="*">
                        <xsl:apply-templates select="."/>
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
