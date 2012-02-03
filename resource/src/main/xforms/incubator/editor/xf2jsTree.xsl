<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms" version="2.0"
                exclude-result-prefixes="html" xml:base="/exist/rest/db/betterform/apps/editor/">
    <xsl:variable name="bfEditorPath" select="'/rest/db/betterform/apps/editor/'"/>
    <!-- author: Joern Turner -->
    <!-- author: Tobias Krebs -->
    <!-- author: Lars Windauer -->

    <!-- eXist version -->
    <!--
        xml:base="/betterform/rest/db/betterform/apps/editor/">
        <xsl:variable name="bfEditorPath" select="'/rest/db/betterform/apps/editor/'"/>
    -->
    <!-- Standalone -->
    <!--
                xml:base="/betterform/forms/incubator/editor/">
    <xsl:variable name="bfEditorPath" select="'/forms/incubator/editor/'"/>
    -->
    <xsl:output method="xml" indent="yes"/>
    <xsl:param name="bfContext" select="''"/>
    <xsl:param name="filename" select="''"/>
    <!-- <xsl:variable name="bfContext" select="'/exist'"/> -->
    <xsl:strip-space elements="*"/>
    <xsl:template match="/">
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
                <div style="display:none">
                    <xf:model id="model-1">
                        <!--todo: we need to use the baseURI of the editor here instead of the one we load -->
                        <!--  i-default hold the editor DOM transformed to XForms -->
                        <xf:instance xmlns="" id="i-default" src="{{$contextroot}}{$bfEditorPath}cdata-instance.xml"/>
                        
                        <!-- Submission transforms the editor DOM to XForms-->
                        <xf:submission id="s-dom2xforms" method="get"
                                       resource="xslt:{$bfContext}{$bfEditorPath}dom2xf.xsl?parseString=true" replace="instance">
                            <xf:action ev:event="xforms-submit-done">
                                <!--<xf:message level="ephemeral">Data transformed to xforms</xf:message>-->
                                <xf:send submission="s-replaceContent"/>
                            </xf:action>
                            <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                        </xf:submission>

                          <!-- merge original XForms host document with Editor XForms markup -->
                        <xf:submission id="s-replaceContent" method="get"
                                       action="xslt:{$bfContext}{$bfEditorPath}updateOriginal.xsl?originDoc={$bfContext}/rest{$filename}"
                                       replace="instance" validate="false">
                            <!-- MODE: SAVE AS -->
                            <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'save-as'">
                                <!--<xf:message level="ephemeral">Data mounted back to original document</xf:message>-->
                                <script type="text/javascript">
                                    dijit.byId("saveDialog").hide();
                                </script>
                                <xf:send submission="s-save-as"/>
                            </xf:action>
                            <!-- MODE: SAVE -->
                            <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'save'">
                                <!--<xf:message level="ephemeral">Data mounted back to original document</xf:message>-->
                                <xf:send submission="s-save"/>
                            </xf:action>
                            <!-- MODE: PREVIEW -->
                            <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'preview'">
                                <xf:message level="ephemeral">Data ready to be saved at tmp directory and to be loaded</xf:message>
                                <xf:send submission="s-preview"/>
                            </xf:action>

                            <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                        </xf:submission>

                        <!-- Overwrites the current form loaded within the editor  -->
                        <xf:submission id="s-save" method="put" replace="none">
                            <xf:resource value="IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) )"/>
                            <xf:header>
                                <xf:name>username</xf:name>
                                <xf:value value="instance('i-login')/username"/>
                            </xf:header>
                            <xf:header>
                                <xf:name>password</xf:name>
                                <xf:value value="instance('i-login')/password"/>
                            </xf:header>
                            <xf:action ev:event="xforms-submit-done">
                                <xf:setvalue ref="instance('i-controller')/save-msg" value="concat('Data stored to ',IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) ) )"/>
                                <xf:message level="ephemeral" ref="instance('i-controller')/save-msg"/>
                            </xf:action>
                            <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                        </xf:submission>
                        
                        <!-- Saves the form to a location specified by the user with  a custom name (given by the user too) -->
                        <xf:submission id="s-save-as" method="put" replace="none">
                            <xf:resource value="concat(bf:appContext('sl-filePath') ,'/', bf:appContext('sl-filename'))"/>
                            <!-- Set username and password for submission -->
                            <xf:header>
                                <xf:name>username</xf:name>
                                <xf:value value="instance('i-login')/username"/>
                            </xf:header>
                            <xf:header>
                                <xf:name>password</xf:name>
                                <xf:value value="instance('i-login')/password"/>
                            </xf:header>
                            <xf:action ev:event="xforms-submit-done">
                                <xf:setvalue ref="instance('i-controller')/save-msg" value="concat('Data stored to ', bf:appContext('sl-filePath') ,'/', bf:appContext('sl-filename'))"/>
                                <xf:message level="ephemeral" ref="instance('i-controller')/save-msg"/>
                            </xf:action>
                            <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                        </xf:submission>

                        <!-- saves form to preview and opens it -->
                        <xf:submission id="s-preview" method="put" replace="none">
                            <xf:resource value="concat(substring-before( IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) ),'.xhtml'),'-prev.xhtml')"/>
                            <xf:header>
                                <xf:name>username</xf:name>
                                <xf:value value="instance('i-login')/username"/>
                            </xf:header>
                            <xf:header>
                                <xf:name>password</xf:name>
                                <xf:value value="instance('i-login')/password"/>
                            </xf:header>
                            <xf:load show="new" ev:event="xforms-submit-done">
                                <xf:resource value="concat(substring-before( IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) ),'.xhtml'),'-prev.xhtml')"/>
                            </xf:load>
                            <xf:message ev:event="xforms-submit-error">Preview failed</xf:message>
                        </xf:submission>
                        
                        <!-- internal controller instance -->
                        <xf:instance id="i-controller">
                            <data xmlns="">
                                <originalDoc/>
                                <save-msg/>
                                <preview-msp>Error previewing file</preview-msp>
                                <preview-path/>
                                <mode/>
                                <filename><xsl:value-of select="$filename"/></filename>
                            </data>
                        </xf:instance>
                        
                        <!-- Instance holding the login data of the user -->
                        <xf:instance id="i-login" xmlns="" >
                            <data/>
                        </xf:instance>
                        
                        <xf:submission  id="s-get-login-credentials" 
                                        method="get" 
                                        replace="instance" 
                                        instance="i-login">
                            <xf:resource value=" concat(bf:appContext('contextroot'), '/rest/db/betterform/utils/Login.xql')"/>
                            
                            <xf:action ev:event="xforms-submit-done">
                                <xf:toggle case="c-login" if="not(boolean-from-string(instance('i-login')/login))"/>
                                <xf:toggle case="c-editor" if="boolean-from-string(instance('i-login')/login)"/>
                            </xf:action>
                        </xf:submission>

                        <xf:submission id="s-login"
                                        method="get"
                                        replace="instance"
                                        instance="i-login"
                                        serialization="none"
                                        validate="false"
                                        relevant="false">
                            
                            <xf:resource value="concat(bf:appContext('contextroot'), '/rest/db/betterform/utils/Login.xql?username=', instance('i-login')/username, '&amp;password=', instance('i-login')/password)"/>
                            
                            <xf:action ev:event="xforms-submit-done" if="boolean-from-string(instance('i-login')/login)">
                                <xf:toggle case="c-editor"/>
                            </xf:action>                    
                            
                            <xf:message ev:event="xforms-submit-done" level="modal" if="not(boolean-from-string(instance('i-login')/login))">Login failed.</xf:message>
                            <xf:message ev:event="xforms-submit-error" level="modal">Login failed.</xf:message>
                        </xf:submission>
                                               
                        <xf:action ev:event="xforms-ready">
                            <xf:send submission="s-get-login-credentials"/>
                        </xf:action>
                    </xf:model>

                    
<!-- UI MARKUP -->
<!-- UI MARKUP -->
<!-- UI MARKUP -->
                    <!-- HIDDEN CONTROLS -->
                    <xf:input id="save" ref="instance()">
                        <xf:label>this is a hidden control set from JS when saving is executed</xf:label>
                    </xf:input>
                    <xf:trigger id="transform2xf">
                        <xf:label>this is hidden</xf:label>
                        <xf:send submission="s-dom2xforms"/>
                    </xf:trigger>
                    <xf:trigger id="t-save">
                        <xf:label>this is hidden</xf:label>
                        <xf:action>
                            <xf:setvalue ref="instance('i-controller')/mode" value="'save'"/>
                            <script type="text/javascript">
                                serializeTree();
                            </script>
                        </xf:action>
                    </xf:trigger>
                    <xf:trigger id="t-preview">
                        <xf:label>this is hidden</xf:label>
                        <xf:action>
                            <xf:message level="ephemeral">Preview Start</xf:message>
                            <xf:setvalue ref="instance('i-controller')/mode" value="'preview'"/>
                            <script type="text/javascript">
                                serializeTree();
                            </script>
                        </xf:action>
                    </xf:trigger>
                    <xf:trigger id="t-save-as">
                        <xf:label>saveas</xf:label>
                        <xf:action>
                            <xf:setvalue ref="instance('i-controller')/mode" value="'save-as'"/>
                            <xf:load show="embed" targetid="embedDialog">
                                <xf:resource value="concat(bf:appContext('contextroot'), '/rest/db/betterform/utils/SaveListing.xql#xforms')"/>
                                <xf:extension includeCSS="true" includeScript="true"/>
                            </xf:load>
                        </xf:action>
                    </xf:trigger>
                </div>
                <!-- DOJO Store holding XForms Datatypes -->
                <div dojoType="dojo.data.ItemFileReadStore" data-dojo-id="stateStore" url="{$bfContext}{$bfEditorPath}xfDatatype.json"/>
                <xf:switch>
                <!-- intially loaded case to prevent showing not initialized markup to the user -->
                    <xf:case id="c-empty" selected="true"/>
                <!-- login view if user is not logged in yet-->
                    <xf:case id="c-login">
                         <div style="font-style: italic;color: orangered;text-align: center;width: 100%; font-size: 18px; border: #ff4500 solid thick;">
                            Caution: This Editor is only a preview.
                        </div>
                        <xf:group id="g-login" appearance="full" style="padding:20px;">
                            <xf:label>betterFORM XForms Editor</xf:label>
                            <div id="notice">In order to use the XForms Editor please login.</div>
                            <xf:input ref="instance('i-login')/username">
                                <xf:label>Username:</xf:label>
                            </xf:input>
                            <xf:secret ref="instance('i-login')/password">
                                <xf:label>Password:</xf:label>
                            </xf:secret>
                            <xf:trigger>
                                <xf:label>Login</xf:label>
                                <xf:send submission="s-login"/>
                            </xf:trigger>                    
                        </xf:group>
                    </xf:case>
                <!-- betterFORM XForms Editor -->
                    <xf:case id="c-editor">
                        <div id="topPane">
                            <div dojoType="dijit.MenuBar" id="mainMenu">
                                <div dojoType="dijit.PopupMenuBarItem" label="File">
                                    <div dojoType="dijit.Menu" id="File">
                                        <!--
                                                                        <div dojoType="dijit.MenuItem"
                                                                             onClick="this.window.href='bfEditor/forms/incubator/editor/standardTemplate.xhtml');">
                                                                            New
                                                                        </div>
                                        -->
                                        <div dojoType="dijit.MenuItem" onClick="dijit.byId('fluxProcessor').dispatchEvent('t-preview');">
                                            Preview Strg+p
                                        </div>
                                        <div dojoType="dijit.MenuItem" onClick="dijit.byId('fluxProcessor').dispatchEvent('t-save');">
                                            Save
                                        </div>
                                        <div dojoType="dijit.MenuItem" onClick="showSaveDialog();">
                                            Save as...
                                        </div>
                                        <!--
                                        <div dojotype="dijit.PopupMenuItem" label="input">
                                            <div dojotype="dijit.Menu">
                                                <div dojoType="dijit.MenuItem"
                                                     onClick="fluxProcessor.dispatchEvent('inputTextTrigger');hideStatic();"
                                                     label="Text"/>
                                            </div>
                                        </div>
                                        -->
                                    </div>
                                </div>
                                <div dojoType="dijit.PopupMenuBarItem" label="Edit">
                                    <div dojoType="dijit.Menu" id="Edit">
                                        <div dojoType="dijit.MenuItem" onClick="alert('cut');">
                                            Cut
                                        </div>
                                        <div dojoType="dijit.MenuItem" onClick="alert('copy');">
                                            Copy
                                        </div>
                                        <div dojoType="dijit.MenuItem" onClick="alert('paste');">
                                            Paste
                                        </div>
                                    </div>
                                </div>
                                <div dojoType="dijit.MenuBarItem" onClick="dijit.byId('bfEditorHelp').show();">
                                    Help
                                </div>

                                <!--
                                                            <div dojoType="dijit.PopupMenuBarItem" label="Add" id="addMenu">
                                                            </div>
                                -->
                            </div>
                            <img src="{$bfContext}/bfResources/images/betterform_icon16x16.png" alt=""/>
                        </div>
                        <div id="mainWindow" style="width:100%;">
                            <div id="docWrapper" tabindex="-1">
                                <!--
                                                    <xf:output value="bf:appContext('pathInfo')">
                                                        <xf:label>PathInfo: </xf:label>
                                                    </xf:output>
                                -->
                                <div id="docPane" tabindex="-1">
                                    <xsl:variable name="elements" select="//xf:model[not(ancestor::xf:*)]|//xf:group[not(ancestor::xf:*)]"/>
                                    <!--<xsl:variable name="uiElements" select="//*[name()='xf:group']"/>-->
                                    <div id="xfDoc" class="xfDoc" tabindex="-1">
                                        <ul>
                                            <li id="root" data-xf-type="document" tabindex="0">
                                                <a href="#">Document</a>
                                                <ul>
                                                    <!--<xsl:for-each select="$elements">-->
                                                    <xsl:apply-templates select="$elements"/>
                                                    <!--</xsl:for-each>-->
                                                </ul>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                            <div id="leftPane" tabindex="-1">
                                <div id="addLabel">
                                    <div class="caption">Add ...</div>
                                    <div id="addModeDiv">
                                        <button id="btnChildMode" type="button" class="modeSelector selected" onclick="attrEditor.updateComponentTree(this);">Child
                                        </button>
                                        <button id="btnSiblingMode" type="button" class="modeSelector" onclick="attrEditor.updateComponentTree(this);">Sibling
                                        </button>
                                    </div>
                                </div>
                                <!--
                                the 'mode' attribute is used to switch between 'children' and 'siblings' mode which
                                determines the list of possible elements displayed in the component tree.
                                -->
                                <div id="componentTree" data-bf-addmode="child"/>
                            </div>
                            <div id="rightPane" tabindex="-1">
                                <div id="xfMount" dojotype="dijit.layout.ContentPane" href="{$bfContext}{$bfEditorPath}document.html" preload="false">
                                    <script type="dojo/connect" event="onDownloadEnd">
                                        var xfId = dojo.attr(dojo.byId("xfMount"), "xfId");
                                        if (xfId == undefined) {
                                            return;
                                        }
                                        // console.debug("xfid: ", xfId);
                                        attrEditor.editProperties(xfId);
                                    </script>
                                </div>
                            </div>
                        </div>
                    </xf:case>
                </xf:switch>
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
                    <xsl:value-of select="concat($bfContext,$bfEditorPath)"/>
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
                        var betterFORMContextPath = "{$bfContext}";
                        var editorContextPath = "{$bfEditorPath}";

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
                <div id="bfEditorHelp" dojoType="dojox.layout.FloatingPane" title="betterFORM Editor Help" resizable="true" dockable="false" style="position:absolute;margin:10px;top:200px;left:200px;width:600px;height:350px;visibility:hidden;">
                    <!--
                                        <div class="bfEditorHelpTitle">betterFORM Editor</div>
                    -->
                    <div>The editor is fully accessible via the keyboard</div>
                    <div>
                        <h3>Editor Shortcuts</h3>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">?</dt>
                            <dd>Open Help</dd>
                        </dl>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">ESC</dt>
                            <dd>Close Help</dd>
                        </dl>
                        <dl class="keyboard-mapping">
                            <dt class="shortcutDesc">t</dt>
                            <dd>Focus the XForms tree</dd>
                        </dl>
                    </div>
                    <div>
                        <h3>Tree Shortcuts</h3>
                        <div class="table">
                            <div class="column1">
                                <dl class="keyboard-mapping">
                                    <dt class="shortcutDesc">
                                        <span class="shortcut">↑</span>
                                    </dt>
                                    <dd>Go to previous Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                    <dt class="shortcutDesc">
                                        <span class="shortcut">↓</span>
                                    </dt>
                                    <dd>Go to next Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                    <dt class="shortcutDesc">
                                        <span class="shortcut">SPACE</span>
                                    </dt>
                                    <dd>Select Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                    <dt class="shortcutDesc">
                                        <span class="shortcut">←</span>
                                    </dt>
                                    <dd>Open Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                    <dt class="shortcutDesc">
                                        <span class="shortcut">→</span>
                                    </dt>
                                    <dd>Close Node</dd>
                                </dl>
                            </div>
                            <div class="column2">
                                <dl class="keyboard-mapping">
                                    <dt class="shortcutDesc">
                                        <span class="shortcut">DEL</span>
                                    </dt>
                                    <dd>Delete Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                    <dt class="shortcutDesc">ALT
                                        <em>and</em>
                                        <span class="shortcut">↑</span>
                                    </dt>
                                    <dd>Move Node up</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                    <dt class="shortcutDesc">ALT
                                        <em>and</em>
                                        <span class="shortcut">↓</span>
                                    </dt>
                                    <dd>Move Node down</dd>
                                </dl>
                            </div>
                        </div>
                    </div>
                </div>
                <div id="saveDialog" dojotype="dijit.Dialog" title="Save as ..." autofocus="false" style="width:820px;height:540px;overflow:auto;">
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
            }
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
                        <img src="{$bfContext}{$bfEditorPath}images/list-remove.png" width="24" height="24" alt="x"/>
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
