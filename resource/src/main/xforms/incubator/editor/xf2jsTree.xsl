<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="html"
                xml:base="/betterform/forms/incubator/editor/">
    <xsl:variable name="bfEditorPath" select="'/forms/incubator/editor/'"/>

    <xsl:output method="xml" indent="yes"/>
    <!-- XRX -->
    <!--
        xml:base="/betterform/rest/db/betterform/apps/editor/">
        <xsl:variable name="bfEditorPath" select="'/rest/db/betterform/apps/editor/'"/>
    -->
    <!-- Standalone -->
    <!--
                xml:base="/betterform/forms/incubator/editor/">
    <xsl:variable name="bfEditorPath" select="'/forms/incubator/editor/'"/>
    -->
    <xsl:variable name="bfContext" select="'/betterform'"/>

    <!-- author: Joern Turner -->
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>betterFORM Editor</title>
                <script type="text/javascript" src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.js"></script>
                <script type="text/javascript" src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.cookie.js"></script>
                <script type="text/javascript" src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.hotkeys.js"></script>
                <script type="text/javascript" src="../../../bfResources/scripts/jstree_pre1.0_stable/jquery.jstree.js"></script>
                <script type="text/javascript" src="../../../bfResources/scripts/betterform/xfEditorUtil.js"></script>
                <script type="text/javascript" src="../../../bfResources/scripts/betterform/betterform-XFormsEditor.js"> </script>
                <script type="text/javascript" src="../../../bfResources/scripts/betterform/editor/addMenu.js"></script>
                <script type="text/javascript">
                    dojo.require("dijit.layout.ContentPane");
                    dojo.require("dijit.Menu");
                    dojo.require("dijit.MenuBar");
                    dojo.require("dijit.PopupMenuBarItem");
                    dojo.require("dijit.MenuItem");
                    dojo.require("dijit.PopupMenuItem");

                    dojo.require("betterform.editor.Editor");
                    dojo.require("betterform.Editor");
                    dojo.require("dijit.layout.TabContainer");
                    dojo.require("dijit.form.TextBox");
                    dojo.require("dijit.form.Select");
                    dojo.require("dijit.form.FilteringSelect");
                    dojo.require("dojo.data.ItemFileReadStore");
                    dojo.require("dojox.layout.FloatingPane");
                    dojo.require("dijit.TitlePane");
                    var attrEditor = new betterform.Editor();
                    //console.debug("attrEditor.: ",attrEditor);

                    function checkKeyboardInput(pEvent){
                            var activeElem = document.activeElement.localName;
                            console.debug("activeElem: ",activeElem);
                            if(activeElem=="input") {
                                return;
                            }
                            console.debug("activeElem: ",activeElem);
                           switch(pEvent.charOrCode){
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
                        function(){
                             dojo.connect(dojo.body(),"onkeypress",checkKeyboardInput);
                        }
                    );

                </script>
                <link rel="stylesheet" type="text/css" href="../../../bfResources/styles/xforms-editor.css" />

            </head>
            <body class="bf">

                <div style="display:none">
                    <xf:model id="model-1">
                        <!--todo: we need to use the baseURI of the editor here instead of the one we load -->
                        <xf:instance id="i-default" xmlns="" src="{{$contextroot}}{$bfEditorPath}cdata-instance.xml"/>

                        <xf:submission id="s-dom2xforms"
                                       method="get"
                                       resource="xslt:{$bfContext}{$bfEditorPath}dom2xf.xsl?parseString=true"
                                       replace="instance"
                                >
                            <xf:action ev:event="xforms-submit-done">
                                <!--<xf:message level="ephemeral">Data transformed to xforms</xf:message>-->
                                <xf:send submission="s-replaceContent"/>
                            </xf:action>
                            <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                       </xf:submission>

                        <xf:submission id="s-replaceContent"
                                       method="get"
                                       action="xslt:{$bfContext}{$bfEditorPath}updateOriginal.xsl?originDoc={{$contextroot}}{{$pathInfo}}"
                                       replace="instance">
                            <xf:action ev:event="xforms-submit-done">
                                <!--<xf:message level="ephemeral">Data mounted back to original document</xf:message>-->
                                <xf:send submission="s-save"/>;
                            </xf:action>
                            <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                       </xf:submission>

                        <xf:submission id="s-save"
                                       method="put"
                                       replace="none">
                            <xf:resource value="concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2))"/>
                            <xf:action ev:event="xforms-submit-done">
                                <xf:setvalue ref="instance('i-controller')/save-msg"
                                             value="concat('Data stored to ',bf:appContext('webapp.realpath'),bf:appContext('pathInfo'))"/>
                                <xf:message level="ephemeral" ref="instance('i-controller')/save-msg"/>
                            </xf:action>
                            <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                       </xf:submission>

                        <xf:submission id="s-preview"
                                       method="put"
                                       replace="none">
                            <xf:resource value="concat(bf:appContext('webapp.realpath'),'forms/tmp/', bf:appContext('fileName'))"/>
                            <xf:action ev:event="xforms-submit-done">
                                <xf:setvalue ref="instance('i-controller')/preview-path" value="concat(bf:appContext('contextroot'),'/forms/tmp/', bf:appContext('fileName'))"/>
                                <xf:load ref="instance('i-controller')/preview-path" show="new"/>
                            </xf:action>
                            <xf:action ev:event="xforms-submit-error">
<!--
                                <xf:setvalue ref="instance('i-controller')/preview-msg"
                                             value="concat('preview failed for path: ',bf:appContext('webapp.realpath'),'forms/tmp/', bf:appContext('fileName'))"/>
-->
                                <xf:message ref="instance('i-controller')/preview-msg"/>
                            </xf:action>
                       </xf:submission>

                       <xf:instance id="i-controller">
                           <data xmlns="">
                               <originalDoc/>
                               <save-msg></save-msg>
                               <preview-msp>Error previewing file</preview-msp>
                               <preview-path/>
                           </data>
                       </xf:instance>
                    </xf:model>
                    <xf:input id="save" ref="instance()">
                        <xf:label>this is a hidden control set from JS when saving is executed</xf:label>
                    </xf:input>
                    <xf:trigger id="transform2xf">
                        <xf:label>this is hidden</xf:label>
                        <xf:send submission="s-dom2xforms"/>
                    </xf:trigger>
                    <xf:trigger id="preview">
                        <xf:label>this is hidden</xf:label>
                        <xf:send submission="s-preview"/>
                    </xf:trigger>
                </div>
                <div dojoType="dojo.data.ItemFileReadStore" data-dojo-id="stateStore" url="{$bfContext}{$bfEditorPath}xfDatatype.json" />
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
                                <div dojoType="dijit.MenuItem"
                                     onClick="dijit.byId('fluxProcessor').dispatchEvent('preview');">
                                    Preview Strg+p
                                </div>
                                <div dojoType="dijit.MenuItem"
                                     onClick="serializeTree();">
                                    Save
                                </div>
                                <div dojoType="dijit.MenuItem"
                                     onClick="alert('save as');">
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
                                <div dojoType="dijit.MenuItem"
                                     onClick="alert('cut');">
                                    Cut
                                </div>
                                <div dojoType="dijit.MenuItem"
                                     onClick="alert('copy');">
                                    Copy
                                </div>
                                <div dojoType="dijit.MenuItem"
                                     onClick="alert('paste');">
                                    Paste
                                </div>
                            </div>
                        </div>
                            <div dojoType="dijit.MenuBarItem"
                                 onClick="dijit.byId('bfEditorHelp').show();">
                                Help
                            </div>

<!--
                            <div dojoType="dijit.PopupMenuBarItem" label="Add" id="addMenu">
                            </div>
-->
                        </div>
                        <img src="{$bfContext}/bfResources/images/betterform_icon16x16.png" alt=""/>
                        <div id="addToolbar">
                            <span class="title">Add...</span>
                            <ul id="childList">
                                <li class="instance-lnk"><a href="javascript:addElement('instance');">instance</a></li>
                                <li class="schema-lnk"><a href="javascript:addElement('schema');">schema</a></li>
                                <li class="submission-lnk"><a href="javascript:addElement('submission');">submission</a></li>
                                <li class="bind-lnk"><a href="javascript:addElement('bind');">bind</a></li>

                                <li class="group-lnk"><a href="javascript:addElement('group');">group</a></li>
                                <li class="switch-lnk"><a href="javascript:addElement('switch');">switch</a></li>
                                <li class="case-lnk"><a href="javascript:addElement('case');">case</a></li>
                                <li class="repeat-lnk"><a href="javascript:addElement('repeat');">repeat</a></li>

                                <li class="label-lnk"><a href="javascript:addElement('label');">label</a></li>
                                <li class="hint-lnk"><a href="javascript:addElement('hint');">hint</a></li>
                                <li class="help-lnk"><a href="javascript:addElement('help');">help</a></li>
                                <li class="alert-lnk"><a href="javascript:addElement('alert');">alert</a></li>

                                <li class="input-lnk"><a href="javascript:addElement('input');">input</a></li>
                                <li class="secret-lnk"><a href="javascript:addElement('secret');">secret</a></li>
                                <li class="textarea-lnk"><a href="javascript:addElement('textarea');">textarea</a></li>
                                <li class="output-lnk"><a href="javascript:addElement('output');">output</a></li>
                                <li class="upload-lnk"><a href="javascript:addElement('upload');">upload</a></li>
                                <li class="filename-lnk"><a href="javascript:addElement('filename');">filename</a></li>
                                <li class="mediatype-lnk"><a href="javascript:addElement('mediatype');">mediatype"</a></li>

                                <li class="select1-lnk"><a href="javascript:addElement('select1');">select1</a></li>
                                <li class="select-lnk"><a href="javascript:addElement('select');">select</a></li>


                                <li class="item-lnk"><a href="javascript:addElement('item');">item</a></li>
                                <li class="itemset-lnk"><a href="javascript:addElement('itemset');">itemset</a></li>
                                <li class="choices-lnk"><a href="javascript:addElement('choices');">choices</a></li>
                                <li class="value-lnk"><a href="javascript:addElement('value');">value</a></li>
                                <li class="copy-lnk"><a href="javascript:addElement('copy');">copy</a></li>


                                <li class="range-lnk"><a href="javascript:addElement('range');">range</a></li>
                                <li class="submit-lnk"><a href="javascript:addElement('submit');">submit</a></li>
                                <li class="trigger-lnk"><a href="javascript:addElement('trigger');">trigger</a></li>
                            </ul>
                            <ul id="actionlist" class="actions">
                                 <li><a href="javascript:addElement('action');">action</a></li>
                                <li><a href="javascript:addElement('insert');">insert</a></li>
                                <li><a href="javascript:addElement('delete');">delete</a></li>
                                <li><a href="javascript:addElement('setvalue');">setvalue</a></li>
                                <li><a href="javascript:addElement('send');">send</a></li>
                                <li><a href="javascript:addElement('dispatch');">dispatch</a></li>
                                <li><a href="javascript:addElement('message');">message</a></li>
                                <li><a href="javascript:addElement('load');">load</a></li>
                                <li><a href="javascript:addElement('rebuild');">rebuild</a></li>
                                <li><a href="javascript:addElement('recalculate');">recalculate</a></li>
                                <li><a href="javascript:addElement('revalidate');">revalidate</a></li>
                                <li><a href="javascript:addElement('refresh');">refresh</a></li>
                                <li><a href="javascript:addElement('setfocus');">setfocus</a></li>
                                <li><a href="javascript:addElement('setindex');">setindex</a></li>
                                <li><a href="javascript:addElement('toggle');">toggle</a></li>
                                <li><a href="javascript:addElement('reset');">reset</a></li>
                            </ul>

                        </div>
                    </div>
                <div id="mainWindow" style="width:100%;">

                <div id="docWrapper" tabindex="-1">
                    <xf:output value="bf:appContext('pathInfo')">
                        <xf:label>PathInfo</xf:label>
                    </xf:output>
                    <div id="docPane" tabindex="-1">
                        <xsl:variable name="elements" select="//xf:model[not(ancestor::xf:*)]|//xf:group[not(ancestor::xf:*)]"/>
                        <!--<xsl:variable name="uiElements" select="//*[name()='xf:group']"/>-->

                        <div id="xfDoc" class="xfDoc" tabindex="-1">
                            <ul>
                                <li id="root" data-xf-type="document" tabindex="0">
                                    <a href="#">Document</a>
                                    <ul>
                                        <!--<xsl:for-each select="$elements">-->
                                            <xsl:apply-templates select="$elements" />
                                        <!--</xsl:for-each>-->
                                    </ul>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
                <div id="leftPane" tabindex="-1">
                    <div id="componentTree">
<!--
                        <ul>
                            <li>
                                <a href="#">Common</a>
                            </li>
                            <li>
                                <a href="#">Elements</a>

                            </li>
                            <li>
                                <a href="#">Controls</a>

                            </li>
                            <li>
                                <a href="#">Container</a>

                            </li>
                            <li>
                                <a href="#">Actions</a>

                            </li>
                        </ul>
-->
                   </div>
                </div>
                <div id="rightPane" tabindex="-1">
                    <div id="xfMount" dojotype="dijit.layout.ContentPane"
                         href="/betterform/forms/incubator/editor/document.html"
                         preload="false">
                        <script type="dojo/connect" event="onDownloadEnd">
                            var xfId = dojo.attr(dojo.byId("xfMount"),"xfId");
                            if(xfId == undefined) { return;}
                            console.log("xfid: ",xfId);
                            attrEditor.editProperties(xfId);
                        </script>
                     </div>
                </div>

                </div>

                <script type="text/javascript">
                /* <![CDATA[ */
                    // do not do anything but logging yet but shows the right call. Should work on FF and webkit which
                    // is sufficient for the editor.
                    function serializeTree(){
                        var serializedTree = new XMLSerializer().serializeToString( document.getElementById("xfDoc") );
                        console.log(serializedTree);
                        dijit.byId("fluxProcessor").setControlValue("save",serializedTree);
                        dijit.byId("fluxProcessor").dispatchEvent("transform2xf");

                    }

                    function displayAddMenu(event,targetId,xfType){
                        console.log("displayMenu ",targetId, xfType);
                        console.log("event ",event);

                        event.stopPropagation();
                        event.cancelBubble=true;
                        return false;
                    }
                    /* ]]> */
                </script>
                <xsl:variable name="bfFullPath2"><xsl:text>'</xsl:text><xsl:value-of select="concat($bfContext,$bfEditorPath)"/><xsl:text>'</xsl:text></xsl:variable>

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
                                                console.log("check move:",m);
                                                console.log("check move:",m.o);
                                                var origin = m.o;

                                                //the the xf type
                                                var xfType = origin.attr("data-xf-type");
                                                console.log("xfType ",xfType);

                                                var target = m.r;
                                                var targetType = target.attr("data-xf-type");
                                                console.log("check target:",targetType);


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
                                            console.debug("Alt+up: event:",event);
                                            attrEditor.moveNodeUp(this)
                                        },

                                        "Alt+down" : function (event) {
                                            console.debug("Alt+down: event:",event);
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
                                    console.debug("nodeIsLoaded:", nodeIsLoaded);
                                    if(nodeIsLoaded){
                                        console.debug("PREVENTED LOADING OF PROPERTY EDITOR");
                                        return;
                                    }else {
                                        console.log(data);
                                        var xfType = data.rslt.obj.attr("data-xf-type");
                                        dojo.attr(dojo.byId("xfMount"),"xfId", tmpId);
                                        console.debug("tmpBfPath:",tmpBfPath);
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
                            "html_data":{
                                "ajax":{
                                    url:"/betterform/forms/incubator/editor/components.html"
                                }
                            },
                            "themes" : {
                                "theme" : "default",
                                "dots" : false,
                                "icons" : false
                            },

                            "plugins" : [ "themes", "html_data" ]
                        });
                    });

                    /* ]]> */
                </script>

                <script type="text/javascript">
                    $("#xfDoc ul").delegate("li", "dblclick", function(){
                            $("#xfDoc").jstree("toggle_node", this);
                    });

                    function addElement(type){
                        console.log("addElement type:",type);
                        var elem = $("#xfDoc").jstree("create",null,"last",type,false,true);
                        elem.attr("data-xf-type",type);
                        elem.attr("id",new Date().getTime());
                        elem.attr("data-xf-attrs","");
                        var ahref = dojo.query("a",elem[0])[0];
                        var span = dojo.create("span", null, ahref);
                        dojo.addClass(span,"buttonWrapper");
                        var btnDelete = dojo.create("button", { type:"button", style: "padding: 0pt; margin: 0pt; background: none repeat scroll 0% 0% transparent; border: medium none;", onclick: "if(confirm('Really delete?')) alert('deleting');return false;" },
                                     span);
                        dojo.create("img", { width:"24", height: "24",src: "/betterform/forms/incubator/editor/images/list-remove.png" },
                                     btnDelete);

                        $("#xfDoc").jstree("select_node",elem,true,null);
                        $("#id").focus();
                    }
                </script>
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
                                  <dt class="shortcutDesc"><span class="shortcut">↑</span></dt>
                                  <dd>Go to previous Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                  <dt class="shortcutDesc"><span class="shortcut">↓</span></dt>
                                  <dd>Go to next Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                  <dt class="shortcutDesc"><span class="shortcut">SPACE</span></dt>
                                  <dd>Select Node</dd>
                                </dl>

                                <dl class="keyboard-mapping">
                                  <dt class="shortcutDesc"><span class="shortcut">←</span></dt>
                                  <dd>Open Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                  <dt class="shortcutDesc"><span class="shortcut">→</span></dt>
                                  <dd>Close Node</dd>
                                </dl>
                            </div>
                            <div class="column2">
                                <dl class="keyboard-mapping">
                                  <dt class="shortcutDesc"><span class="shortcut">DEL</span></dt>
                                  <dd>Delete Node</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                  <dt class="shortcutDesc">ALT <em>and</em> <span class="shortcut">↑</span></dt>
                                  <dd>Move Node up</dd>
                                </dl>
                                <dl class="keyboard-mapping">
                                  <dt class="shortcutDesc">ALT <em>and</em> <span class="shortcut">↓</span></dt>
                                  <dd>Move Node down</dd>
                                </dl>

                            </div>

                        </div>
                    </div>
                </div>
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
        <xsl:variable name="props">{<xsl:for-each select="@*">
            <xsl:value-of select="local-name()"/>:'<xsl:value-of select="."/>'<xsl:if test="position()!=last()">,</xsl:if>
        </xsl:for-each>}</xsl:variable>

        <li id="{$id}" data-xf-type="{local-name()}" data-xf-attrs="{$props}" class="{local-name()}" rel="{local-name()}">
            <!--
            ####################################################################################################
            the outermost xforms elements found get the id of their parent node written to a 'xpath' attribute.
            This can later be used to 're-mount' the edited markup.
            ####################################################################################################
            -->
            <xsl:if test="count(ancestor::xf:*) = number(0)">
                <xsl:attribute name="oid"><xsl:value-of select="$this/@id"/></xsl:attribute>
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
                    <img src="{$bfContext}{$bfEditorPath}images/flag-red.png" width="24" height="24" alt="add event"/>
                    <button id="{$id}-addMenu" type="button" style="padding:0;margin:0;background:transparent;border:none;" onclick="displayAddMenu(event,'{generate-id()}','{local-name()}');">
                        <img  src="{$bfContext}{$bfEditorPath}images/list-add.png" width="24" height="24" alt="+"/>
                    </button>
                    <!--<button class="deleteBtn" name="deleteItem" onclick="alert('deleting');">x</button>-->
                    <button type="button" onclick="if(confirm('Really delete?')) alert('deleting');return false;" style="padding:0;margin:0;background:transparent;border:none;">
                        <img src="{$bfContext}{$bfEditorPath}images/list-remove.png" width="24" height="24" alt="x"/>
                    </button>
                </span>
            </a>

            <xsl:if test="count(xf:*) != 0">
                <ul>
                    <xsl:for-each select="*">
                        <xsl:apply-templates select="." />
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
