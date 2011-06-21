<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xml:base="/betterform/forms/incubator/editor/">

    <xsl:output method="xml" indent="yes"/>
    <!-- author: Joern Turner -->
    <xsl:strip-space elements="*"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>betterFORM Editor</title>
                <script type="text/javascript" src="/betterform/bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.js"></script>
                <script type="text/javascript" src="/betterform/bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.cookie.js"></script>
                <script type="text/javascript" src="/betterform/bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.hotkeys.js"></script>
                <script type="text/javascript" src="/betterform/bfResources/scripts/jstree_pre1.0_stable/jquery.jstree.js"></script>
                <script type="text/javascript" src="/betterform/bfResources/scripts/betterform/xfEditorUtil.js"></script>
                <script type="text/javascript" src="/betterform/bfResources/scripts/betterform/bfEditor.js"></script>
                <script type="text/javascript">
                    dojo.require("dijit.layout.ContentPane");
                    dojo.require("dijit.MenuBar");
                    dojo.require("dijit.PopupMenuBarItem");
                    dojo.require("dijit.MenuItem");
                    
                    dojo.require("betterform.editor.Editor");
                    dojo.require("betterform.Editor");
                    dojo.require("dijit.layout.TabContainer");
                    dojo.require("dijit.form.TextBox");
                    dojo.require("dijit.form.Select");
                    dojo.require("dijit.form.FilteringSelect");
                    dojo.require("dojo.data.ItemFileReadStore");
                    dojo.require("dojox.layout.FloatingPane");
                    var attrEditor = new betterform.Editor();
                    console.debug("attrEditor.: ",attrEditor);

                    function checkKeyboardInput(pEvent){
                           switch(pEvent.charOrCode){
                             case '?': //Process the Help key event
                                dijit.byId("bfEditorHelp").show();
                                break;
                           case 't':
                           case 'T': //Process the Help key event
                              console.debug("T pressed");
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
                <style type="text/css">
                    html, body,#mainWindow {
                        height: 100%;
                        width: 100%;
                        margin: 0;
                        padding: 0;
                        background: #F3F3F3;
                        overflow:auto;
                    }
                    #mainWindow #docWrapper{
                        width:100%;
                        overflow:auto;
                        position:relative;
                        margin-bottom:30px;
                    }
                    #docPane{
                        background: #ffffff;
                        border-radius: 10px 0 0 10px;
                        width:70%;
                        float:left;
                        margin-top:90px;
                    }
                    #xfDoc{
                        overflow-x:hidden;
                        background:white;
                        border:none;
                    }
                    #xfDoc .jstree-hovered{
                        width:100%;
                        background:green;
                    }
                    #xfDoc .jstree-clicked{
                        width:100%;
                        background:red;
                    }
                    #topPane{
                        height: 90px;
                        background: #E6E6E7;
                        width: 100%;
                        position:fixed;
                        z-index:100;
                    }
                    #topPane img{
                        width:32px;
                        height:32px;
                        margin:9px;
                        position:absolute;
                        top:-9px;
                        right:-9px;
                    }
                    #topPane a:link{
                        color:#cccccc;
                        text-decoration:none;
                    }
                    #topPane a:hover{
                        border:thin solid limegreen;
                        font-size:1.2em;
                        padding:3px;
                        background:white;
                        color:#555555;
                        border-radius:10px 10px 10px 10px
                    }
                    #rightPane {
                        background: #666666;
                        color:#e9e9e9;
                        display: block;
                        float: right;
                        min-height: 200px;
                        overflow: auto;
                        width: 280px;
                        margin-top:90px;
                        border-left:thin solid #cecece;
                        border-bottom:thin solid #cecece;
                        border-bottom-left-radius:10px;
                    }
                    .jstree-default,.jstree-default{
                        margin: 5px;
                        border: thin solid #bbbbbb;
                        padding: 10px;

                    }
                    .jstree > ul > li{
                        font-size: 14pt;
                    }
                    #docPane .jstree li{
                        border: thin solid #999999;
                        border-radius: 10px 0 0 10px;
                        margin-bottom: 5px;
                        margin-right:10px;

                    }
                    .jstree a{
                        font-size:1.2em;
                        height:28px;
                        padding:5px;
                        width:95%;
                    }
                    #xfMount{
                        background:#666666;
                        color:#dedede;
                    }
                    #xfMount .attrEditor p{
                        margin:0;
                        line-height:1;
                        font-style:italic;
                    }
                    #xfMount .attrEditor .dojoInput{
                        width:200px;
                    }
                    #xfMount .propertyTitle{
                        font-size:12pt;
                        padding:15px;
                        font-size:1.2em;
                        color:white;
                    }
                    .tundra .dijitMenu, .tundra .dijitMenuBar{
                        padding:7px;
                    }
                    #addToolbar{
                        padding-top:5px;
                        padding-left:5px;
                        background:#555555;
                        padding:10px;
                        color:#e9e9e9;
                    }
                    #addToolbar ul{
                        display:block;
                        list-style-type:none;
                        padding:0;
                        margin:0;
                        margin-left:60px;
                        height:18px;
                    }
                    #addToolbar .title{
                        display:inline;
                        padding-right:10px;
                        font-size:1.2em;
                        float:left;
                        margin:1px 1px 1px 10px;
                    }
                    #addToolbar ul li{
                        display:none;
                        padding-right:20px;
                    }
                    #docPane #xfDoc .model,
                    #docPane #xfDoc .group{
                        border-radius: 10px 0 0 10px;
                        border: 2px solid #cecece;
                        margin-bottom: 5px;
                        padding:0 10px;
                        margin-right:10px;
                    }

                    .buttonWrapper{
                        display:none;
                    }
                    .bf #mainWindow .jstree a:hover .buttonWrapper{
                        display:inline;
                    }
                    .jstree-default.jstree-focused {
                        background:white;
                    }
                    .bf #mainWindow  .jstree-clicked {
                        padding:10px 5px;
                        width:95%;
                        background:transparent;
                        color:#444444;
                        border-color:limegreen;
                        border:2px solid limegreen;
                        border-radius:10px 0;
                    }
                    .bf #mainWindow .jstree-hovered{
                        width:95%;
                        background:#eeeeee;
                        border-color:limegreen;
                    }
                    .bf #mainWindow .dijitMenu, .bf #mainWindow .dijitMenuBar {
                        background:#444444;
                        border:none;
                        border-top:1px solid limegreen;
                    }
                    .bf #mainWindow .dijitMenuItem{
                        color:#e9e9e9;
                    }
                    .bf #mainWindow .dijitTextBox {
                        color:#333333;
                    }
                    .bf #mainWindow .deleteBtn{
                        display:none;
                    }
                    .bf #mainWindow .jstree-clicked .deleteBtn{
                        display:inline;
                        float:right;
                        margin-top:-18px;
                    }

                    .bf #mainWindow .group,
                    .bf #mainWindow .switch,
                    .bf #mainWindow .repeat
                    {
                        background-color:#aaaaaa;
                    }

                    .bf #mainWindow .input,
                    .bf #mainWindow .ouput,
                    .bf #mainWindow .secret,
                    .bf #mainWindow .range,
                    .bf #mainWindow .select,
                    .bf #mainWindow .select1,
                    .bf #mainWindow .textarea,
                    .bf #mainWindow .trigger,
                    .bf #mainWindow .submit,
                    .bf #mainWindow .upload{
                        background-color:#bbbbbb;
                    }
                    .bf #mainWindow .label,
                    .bf #mainWindow .hint,
                    .bf #mainWindow .help,
                    .bf #mainWindow .alert{
                        background-color:#cccccc;
                    }

                    .bf #mainWindow .model{
                        background-color:#777777;
                        color:#eeeeee;
                    }

                    .bf #mainWindow .instance,
                    .bf #mainWindow .submission,
                    .bf #mainWindow .bind{
                        background-color:#999999;
                        color:#eeeeee;
                    }
                    .bf #mainWindow .action{
                        background-color:orangered;
                    }
                    .bf #mainWindow .insert,
                    .bf #mainWindow .delete,
                    .bf #mainWindow .setvalue,
                    .bf #mainWindow .send,
                    .bf #mainWindow .dispatch,
                    .bf #mainWindow .message,
                    .bf #mainWindow .load,
                    .bf #mainWindow .rebuild,
                    .bf #mainWindow .recalculate,
                    .bf #mainWindow .revalidate,
                    .bf #mainWindow .refresh,
                    .bf #mainWindow .setfocus,
                    .bf #mainWindow .setindex,
                    .bf #mainWindow .toggle,
                    .bf #mainWindow .reset{
                        background-color:orange;
                    }


                    dl.keyboard-mapping {
                        font-size: 12px;
                        margin: 5px 0;
                    }
                    dl.keyboard-mapping dt {
                        -moz-border-radius: 2px 2px 2px 2px;
                        background: none repeat scroll 0 0 #333333;
                        color: #EEEEEE;
                        display: inline-block;
                        font-family: Monaco,"Courier New","DejaVu Sans Mono","Bitstream Vera Sans Mono",monospace;
                        margin: 0;
                        min-width: 10px;
                        padding: 3px 6px;
                        text-align: center;
                        text-shadow: 1px 1px 0 #000000;
                    }

                    dl.keyboard-mapping dt em {
                        color: #999999;
                        font-family: Helvetica, Arial, freesans, sans-serif;
                        font-size: 10px;
                        font-style: normal;
                        font-weight: normal;
                        padding: 0 4px;
                        text-shadow: none;
                    }

                    .shortcut {
                        font-family: sans-serif;
                        font-weight: bold;
                    }

                    dl.keyboard-mapping dd {
                        color: #666666;
                        display: inline;
                        margin: 0 0 0 5px;
                    }

                    .bfEditorHelpTitle {
                        border-bottom: 1px solid #DDDDDD !important;
                        font-size: 16px;
                        margin: 0 0 10px -10px;
                        padding: 0 10px 10px;
                        width: 100%;
                    }
                    #bfEditorHelp .column1 {
                        float:left;
                        display:block;
                        width:250px;
                        position:relative;
                    }

                    .dojoxFloatingPaneCanvas {
                        margin:10px;
                    }
                </style>
            </head>
            <body>
                <div style="display:none">
                    <xf:model id="model-1">
                        <xf:instance id="i-default">
                            <data xmlns="">
                            </data>
                        </xf:instance>
                        <xf:submission id="s-dom2xforms"
                                       method="get"
                                       resource="xslt:/betterform/forms/incubator/editor/dom2xf.xsl?parseString=true"
                                       replace="instance">
                            <xf:action ev:event="xforms-submit-done">
                                <xf:message>Data stored</xf:message>
                            </xf:action>
                            <xf:message ev:event="xform-submit-error">Storing failed</xf:message>
                       </xf:submission>
                    </xf:model>
                    <xf:input id="save" ref="instance()">
                        <xf:label>this is a hidden control set from JS when saving is executed</xf:label>
                    </xf:input>
                    <xf:trigger id="transform2xf">
                        <xf:label>this is hidden</xf:label>
                        <xf:send submission="s-dom2xforms"/>
                    </xf:trigger>

                </div>
                <div id="mainWindow" style="width:100%;">
                    <div dojoType="dojo.data.ItemFileReadStore" data-dojo-id="stateStore" url="/betterform/forms/incubator/editor/xfDatatype.json" />
                    <div id="topPane">
                        <div dojoType="dijit.MenuBar" id="mainMenu">
                            <div dojoType="dijit.PopupMenuBarItem" label="File">
                                <div dojoType="dijit.Menu" id="File">
                                    <div dojoType="dijit.MenuItem"
                                         onClick="alert('new');">
                                        New
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
                            <div dojoType="dijit.MenuBarItem"
                                 onClick="dijit.byId('bfEditorHelp').show();">
                                Help
                            </div>

<!--
                            <div dojoType="dijit.PopupMenuBarItem" label="Add" id="addMenu">
                            </div>
-->
                        </div>
                        <img src="/betterform/bfResources/images/betterform_icon16x16.png" alt=""/>
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

                    <div id="docWrapper" tabindex="-1">
                        <div id="docPane" tabindex="-1">
                            <xsl:variable name="elements" select="//xf:model[not(ancestor::xf:*)]|//xf:group[not(ancestor::xf:*)]"/>
                            <!--<xsl:variable name="uiElements" select="//*[name()='xf:group']"/>-->

                            <div id="xfDoc" class="xfDoc">
                                <ul>
                                    <li id="root" data-xf-type="document">
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

                </div>

                <script type="text/javascript">
                /* <![CDATA[ */
                    // do not do anything but logging yet but shows the right call. Should work on FF and webkit which
                    // is sufficient for the editor.
                    function serializeTree(){
                        var serializedTree = new XMLSerializer().serializeToString( document.getElementById("xfDoc") );
                        console.log(serializedTree);
                        dijit.byId("fluxProcessor").setControlValue("save",serializedTree);
                        dijit.byId("fluxProcessor").dispatchEvent("transform2xf",serializedTree);

                    }
                    /* ]]> */
                </script>

                <script type="text/javascript" class="source below">
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
                                        "Alt+up"   : function (event) {
                                            attrEditor.moveNodeUp(this)
                                        },

                                        "Alt+down" : function (event) {
                                            attrEditor.moveNodeDown(this);
                                        }
                                    },

                                    // the `plugins` array allows you to configure the active plugins on this instance
                                    "plugins" : ["themes","html_data","ui","crrm","hotkeys","dnd"]
                                })
                                .bind("select_node.jstree", function (event, data) {
                                    // `data.rslt.obj` is the jquery extended node that was clicked
                                    console.log(data);
                                    // alert( data.rslt.obj.attr("data-xf-type"));
                                    var xfType = data.rslt.obj.attr("data-xf-type");
                                    var id=data.rslt.obj.attr("id");
                                    dojo.attr(dojo.byId("xfMount"),"xfId", id);

                                    dijit.byId("xfMount").set("href", "/betterform/forms/incubator/editor/" + xfType + ".html");
                                    //console.debug("publish: nodeSelected: data", data);
                                    dojo.publish("nodeSelected", [
                                        {xfType:xfType,id:id,jsTreeData:data}
                                    ]);
                                })
                            // EVENTS
                            // each instance triggers its own events - to process those listen on the container
                            // all events are in the `.jstree` namespace
                            // so listen for `function_name`.`jstree` - you can function names from the docs
                                .bind("loaded.jstree", function (event, data) {
                                    // you get two params - event & data - check the core docs for a detailed description
                                })
                    });
                    /* ]]> */
                </script>

                <script type="text/javascript">
                    function addElement(type){
                        console.log("addElement type:",type);
                        var elem = $("#xfDoc").jstree("create",null,"last",type,false,true);
                        elem.attr("data-xf-type",type);
                        $("#xfDoc").jstree("select_node",elem,false,null);
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
                <xsl:attribute name="xpath"><xsl:value-of select="$this/@id"/></xsl:attribute>
            </xsl:if>

            <a href="#"><xsl:value-of select="local-name()"/>:<xsl:value-of select="@id"/><button class="deleteBtn" name="deleteItem" onclick="alert('deleting');">x</button></a>

            <xsl:if test="count(xf:*) != 0">
                <ul>
                    <xsl:for-each select="*">
                        <xsl:apply-templates select="." />
                    </xsl:for-each>
                </ul>
            </xsl:if>
        </li>
     </xsl:template>

    <xsl:template match="xf:*/text()"/>
</xsl:stylesheet>
