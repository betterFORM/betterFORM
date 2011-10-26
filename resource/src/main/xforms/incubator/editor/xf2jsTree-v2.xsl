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

    <!-- eXist version -->
    <!--
        xml:base="/betterform/rest/db/betterform/apps/editor/">
        <xsl:variable name="EDITOR_HOME" select="'/rest/db/betterform/apps/editor/'"/>
    -->
    <!-- Standalone -->
    <!--
        xml:base="/betterform/forms/incubator/editor/">
        <xsl:variable name="EDITOR_HOME" select="'/forms/incubator/editor/'"/>
    -->
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="APP_CONTEXT" select="''"/>
    <xsl:param name="filename" select="''"/>

    <xsl:variable name="EDITOR_HOME" select="'/betterform/forms/incubator/editor/'" />

    <xsl:include href="editor-model.xsl"/>
    <xsl:include href="help-dialog.xsl"/>
    <xsl:include href="main-ui.xsl"/>

    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
        <xsl:message>WebApplication Context: <xsl:value-of select="$APP_CONTEXT"/></xsl:message>
        <xsl:message>Editor home directory: <xsl:value-of select="$EDITOR_HOME"/></xsl:message>
        <html>
            <head>
                <title>betterFORM Editor</title>
                <script type="text/javascript"
                        src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.js"></script>
                <script type="text/javascript"
                        src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.cookie.js"></script>
                <script type="text/javascript"
                        src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.hotkeys.js"></script>
                <script type="text/javascript"
                        src="../../../bfResources/scripts/jstree_pre1.0_stable/jquery.jstree.js"></script>

                <link rel="stylesheet" type="text/css" href="../../../bfResources/styles/xforms-editor.css"/>
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



                <script type="text/javascript">
                    /* <![CDATA[ */
                    // do not do anything but logging yet but shows the right call. Should work on FF and webkit which
                    // is sufficient for the editor.
                    var xfReadySubscribers;

                    function serializeTree() {
                        var serializedTree = new XMLSerializer().serializeToString(document.getElementById("xfDoc"));
                        // console.debug(serializedTree);
                        dijit.byId("fluxProcessor").setControlValue("save", serializedTree);
                        dijit.byId("fluxProcessor").dispatchEvent("transform2xf");

                    }

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

                    /* ]]> */
                </script>
                <xsl:variable name="bfFullPath2">
                    <xsl:text>'</xsl:text>
                    <xsl:value-of select="concat($APP_CONTEXT,$EDITOR_HOME)"/>
                    <xsl:text>'</xsl:text>
                </xsl:variable>
                <script type="text/javascript" class="source below">
                    var tmpBfPath = <xsl:value-of select="$bfFullPath2"/>;
                    /* <![CDATA[ */
                    $(function () {
                        // TO CREATE AN INSTANCE
                        // select the tree container using jQuery
                        $("#xfDoc")
                            // call `.jstree` with the options object
                                .jstree({
                                    // each plugin you have included can have its own config object
                                    "core" : { "initially_open" : [ "root",".group",".switch",".repeat" ] },
                                    "crrm" : {
                                        "move" : {
                                            "check_move" : function (m) {
                                                // console.debug("check move:",m);
                                                // console.debug("check move:",m.o);
                                                var origin = m.o;

                                                //the the xf type
                                                var xfType = origin.attr("data-xf-type");
                                                // console.debug("xfType ",xfType);

                                                var target = m.r;
                                                var targetType = target.attr("data-xf-type");
                                                // console.debug("check target:",targetType);


                                                //check rules
                                                //look for match in drop target elements list of allowed children
                                                //if found 'true' 'false' otherwise
                                                var childArray=eval(targetType+"Childs");
                                                if(childArray == undefined){
                                                    return false;
                                                }
                                                if(dojo.indexOf(childArray,xfType) != -1){
                                                    return true;
                                                }else{
                                                    return false;
                                                }



                                            }
                                        }
                                    },
                                    "themes" : {
                                        "theme" : "default",
                                        "dots" : false,
                                        "icons" : false
                                    },
                                    "dnd" : {
                                        "drop_target" : false,
                        	            "drag_target" : false
                                    },
                                    types : {
                                        // the default type
                                        "default":{
                                            "valid_children": "all"
                                        },
                                        "model" : {
                                            "valid_children": ["instance","bind","submission","action","insert","delete","setvalue","send","dispatch","message","load","rebuild","recalculate","revalidate","refresh","setfocus","setindex","toggle","reset"]
                                        },
                                        "bind": {
                                            "valid_children": ["bind"]
                                        }

                                    },
                                    hotkeys: {
                                        "up":function(event) {
                                            // console.debug("up key pressed");
                                            // console.debug("this: ",this);
                                            // console.debug("1");
                                            var o = this.data.ui.hovered || this.data.ui.last_selected || -1;
                                            // console.debug("2 o",o);
				                            this.hover_node(this._get_prev(o));
                                            // console.debug("3");
				                            return false;
                                        },
                                        "Alt+up"   : function (event) {
                                            // console.debug("Alt+up: event:",event);
                                            attrEditor.moveNodeUp(this)
                                        },

                                        "Alt+down" : function (event) {
                                            // console.debug("Alt+down: event:",event);
                                            attrEditor.moveNodeDown(this);
                                        },
                                        "ctrl+p" : function (event) {
                                            alert("preview");
                                        }
                                    },

                                    // the `plugins` array allows you to configure the active plugins on this instance
                                    "plugins" : ["themes","html_data","ui","crrm","hotkeys","dnd"]
                                })
                                .bind("select_node.jstree", function (event, data) {
                                    // `data.rslt.obj` is the jquery extended node that was clicked
                                    // alert( data.rslt.obj.attr("data-xf-type"));
                                    var tmpId=data.rslt.obj.attr("id");
                                    var nodeIsLoaded = attrEditor.nodeIsLoaded(tmpId);
                                    // console.debug("nodeIsLoaded:", nodeIsLoaded);
                                    if(nodeIsLoaded){
                                        // console.debug("PREVENTED LOADING OF PROPERTY EDITOR");
                                        return;
                                    }else {
                                        // console.debug(data);
                                        var xfType = data.rslt.obj.attr("data-xf-type");
                                        var mountNode = dojo.byId("xfMount");
                                        dojo.attr(mountNode ,"xfId", tmpId);
                                        var nodesToDestroy = dojo.query("*[widgetId]",mountNode);
                                        // console.debug("nodesToDestroy: ",nodesToDestroy);
                                        dojo.forEach(nodesToDestroy, function(item) {
                                            var tmpDijit = dijit.byId(dojo.attr(item,"widgetId"));
                                            tmpDijit.destroy();
                                        });
                                        // console.debug("destroyed existing nodes");
                                        // console.debug("tmpBfPath:",tmpBfPath);
                                        dijit.byId("xfMount").set("href", tmpBfPath + xfType + ".html");

                                        //console.debug("publish: nodeSelected: data", data);
                                        dojo.publish("nodeSelected", [
                                            {event:event,xfType:xfType,id:tmpId,jsTreeData:data,bfPath:tmpBfPath}
                                        ]);

                                    }
                                })
                            // EVENTS
                            // each instance triggers its own events - to process those listen on the container
                            // all events are in the `.jstree` namespace
                            // so listen for `function_name`.`jstree` - you can function names from the docs
                            .bind("loaded.jstree", function (event, data) {
                                // you get two params - event & data - check the core docs for a detailed description
                            });
                    });

                    $(function () {
                        $("#componentTree").jstree({
                            "core" : {
                                "initially_open" : [ "document-tmpl","model-tmpl","group-tmpl","repeat-tmpl","switch-tmpl","bind-tmpl","submission-tmpl","input-tmpl","output-tmpl","range-tmpl","secret-tmpl","select-tmpl","select1-tmpl","submit-tmpl","textarea-tmpl","trigger-tmpl","upload-tmpl","label-tmpl" ]
                            },
                            "html_data":{
                                "ajax":{
                                    url:tmpBfPath + "components.html"
                                }
                            },
                            "themes" : {
                                "theme" : "default",
                                "dots" : false,
                                "icons" : false
                            },

                            "plugins" : [ "themes", "hotkeys", "ui", "html_data","dnd" ]
                        });
                    });

                    /* ]]> */
                </script>
                <script type="text/javascript">
                    $("#xfDoc").delegate("a", "click", function() {
                        $("#xfDoc").jstree("toggle_node", this);
                    });


                    $("#componentTree").delegate("a", "click", function() {
                        var currentItem = this.parentNode;

                        if (dojo.hasClass(currentItem, "jstree-leaf")) {
                            if ($("#componentTree").attr("data-bf-addmode") == "child") {
                                addElement(currentItem.getAttribute("data-xf-type"), "last");
                            } else {
                                //get parent
                                var parentLI = currentItem.parentNode.parentNode;
                                // console.debug("parent add: ", parentLI);
                                addElement(currentItem.getAttribute("data-xf-type"), "after");
                            }
                        }

                        $("#componentTree").jstree("toggle_node", this);
                    });

                    function addElement(type, position) {
                        // console.debug("addElement type:", type);
                        var betterFORMContextPath = "{$APP_CONTEXT}";
                        var editorContextPath = "{$EDITOR_HOME}";

                        var elem = $("#xfDoc").jstree("create", null, position, type, false, true);
                        elem.attr("data-xf-type", type);
                        elem.attr("rel", type);
                        elem.attr("id", new Date().getTime());
                        elem.attr("data-xf-attrs", "{ }");
                        var ahref = dojo.query("a", elem[0])[0];
                        var span = dojo.create("span", null, ahref);
                        dojo.addClass(span, "buttonWrapper");
                        var btnDelete = dojo.create("button", { type:"button", style: "padding: 0pt; margin: 0pt; background: none repeat scroll 0% 0% transparent; border: medium none;", onclick: "if(confirm('Really delete?')) deleteNode(this);return false;" },
                                span);
                        dojo.create("img", { width:"24", height: "24",src: betterFORMContextPath +  editorContextPath + "images/list-remove.png" },
                                btnDelete);
                        if(type.toLowerCase()=="label" || type.toLowerCase() == "alert" || type.toLowerCase() == "hint" || type.toLowerCase() == "help"){
                            var textNode = dojo.create("span", textNode ,ahref);
                            dojo.addClass(textNode, "textNode");
                        }
                        $("#xfDoc").jstree("select_node", elem, true, null);
                        elem.focus();
                        elem.hide();
                        $(elem).fadeIn("slow");
                    }

                    function deleteNode(elem) {
                        $("#xfDoc").jstree("remove", null);
                    }
                </script>

                <!--
                                //fetch the currently selected item from xdoc tree
                                var currentItem =  attrEditor.currentNodeId;
                                if(!currentItem) return;
                                console.debug("current: ",currentItem);

                                var currentType = dojo.attr(dojo.byId(currentItem),"data-xf-type");
                                if (!currentType) {
                                    console.error("no xfType defined");
                                    return;
                                 }
                                console.debug("current xfType: ",currentType);

                                //display tree with argument 'xfType' or parent xfType
                -->

                <!-- ################### HELP DIALOG FOR KEYBOARD SHORTCUTS ################### -->
                <!-- ################### HELP DIALOG FOR KEYBOARD SHORTCUTS ################### -->
                <!-- ################### HELP DIALOG FOR KEYBOARD SHORTCUTS ################### -->
                <xsl:call-template name="help-dialog"/>


                <div id="saveDialog" dojotype="dijit.Dialog" title="save as ..." autofocus="false" style="width:820px;height:540px;overflow:auto;">
                    <div id="embedDialog"/>
                </div>

                <script type="text/javascript" defer="defer"
                        src="../../../bfResources/scripts/betterform/betterform-XFormsEditor.js"></script>
                <script type="text/javascript" defer="defer" src="../../../bfResources/scripts/betterform/editor/addMenu.js"></script>
                <script type="text/javascript" defer="defer">
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

    <!-- convert native XForms markup into a <ul><li> structure -->
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

        <li id="{$id}" data-xf-type="{local-name()}" data-xf-attrs="{$props}" class="{local-name()}" rel="{local-name()}">
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
                    <button type="button" onclick="if(confirm('Really delete?')) deleteNode(this);return false;" style="padding:0;margin:0;background:transparent;border:none;">
                        <img src="{$APP_CONTEXT}{$EDITOR_HOME}images/list-remove.png" width="24" height="24" alt="x"/>
                    </button>
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
