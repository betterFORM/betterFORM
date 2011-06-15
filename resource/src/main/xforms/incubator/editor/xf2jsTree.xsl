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

                    function updateProperties(xfId){
                        console.debug("jsTree: id of property sheet: ",xfId);
                        var dataXfAttrs = dojo.attr(dojo.byId(xfId), "data-xf-attrs");
                        var dataXfType = dojo.attr(dojo.byId(xfId), "data-xf-type");

                        console.debug("dataXfAttrs: ",dataXfAttrs, " dataXfType" ,dataXfType);
                        var xfAppearance = dataXfAttrs['appearance'];
                        var xfRef= dataXfAttrs['ref'];
                        console.debug("xfAppearance:",xfAppearance, " ref:",xfRef);

                        // var testObject = { dataXfAttrs };
                        // console.log('typeof testObject: ' + typeof testObject);
                        // console.log('testObject properties:');
                        // for (var prop in testObject) {
                        //     console.log('  ' + prop + ': ' + testObject[prop]);
                        // }

                        // Put the object into storage
                        // localStorage.setItem('testObject', testObject);

                        // Retrieve the object from storage
                        // var retrievedObject = localStorage.getItem('testObject');

                        // console.log('typeof retrievedObject: ' + typeof retrievedObject);
                        // console.log('Value of retrievedObject: ' + retrievedObject);

                    }

                </script>
                <style type="text/css">
                    html, body,#mainWindow {
                        height: 100%;
                        width: 100%;
                        margin: 0;
                        padding: 0;
                        background: #F3F3F3;
                    }
                    #mainWindow #docWrapper{
                        width:100%;
                        overflow:auto;
                        position:relative;
                    }
                    #docPane{
                        background: #ffffff;
                        -moz-border-radius: 10px;
                        -webkit-border-radius: 10px;
                        border-radius: 10px;
                        width:70%;
                        float:left;
                        margin-top:90px;
                    }
                    #xfDoc{
                        overflow-x:hidden;
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
                        top:-5px;
                        right:-3px;
                    }
                    #topPane a:link{
                        color:steelblue;
                    }
                    #topPane a:hover{
                        border:thin solid limegreen;
                        -moz-border-radius:5px;
                        -webkit-border-radius:5px;
                        font-size:1.2em;
                        padding:3px;
                        background:white;
                    }
                    #rightPane {
                        background: #F3F3F3;
                        border-bottom-left-radius: 10px;
                        border-top-left-radius: 10px;
                        display: block;
                        float: right;
                        min-height: 200px;
                        overflow: auto;
                        width: 30%;
                        margin-top:90px;
                    }
                    .jstree-default,.jstree-default{
                        margin: 5px;
                        border: thin solid #bbbbbb;
                        padding: 10px;

                    }
                    .jstree > ul > li{
                        font-size: 14pt;
                    }
                    .jstree li{
                        line-height: 32px;

                    }
                    .jstree a{
                        font-size:1.2em;
                        height:28px;

                    }
                    #xfMount{
                    }
                    #xfMount .attrEditor p{
                        margin:0;
                        line-height:1;
                        font-style:italic;
                    }
                    #xfMount .attrEditor input{
                        width:200px;
                    }
                    #xfMount .propertyTitle{
                        font-size:12pt;
                        padding:5px;
                    }
                    .tundra .dijitMenu, .tundra .dijitMenuBar{
                        padding:7px;
                    }
                    #addToolbar{
                        padding-top:5px;
                        padding-left:5px;
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
                        margin:1px 1px 1px 7px;
                    }
                    #addToolbar ul li{
                        display:none;
                        padding-right:20px;
                    }
                    #docPane #xfDoc .model{
                    }
                    #docPane #xfDoc .group{
                    }
                    .buttonWrapper{
                        display:none;
                    }
                    .jstree a:hover .buttonWrapper{
                        display:inline;
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
                            <xf:message ev:event="xform-submit-done">Data stored</xf:message>
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
<!--
                            <div dojoType="dijit.PopupMenuBarItem" label="Add" id="addMenu">
                            </div>
-->
                        </div>
                        <img src="/betterform/bfResources/images/betterform_icon16x16.png"/>
                        <div id="addToolbar" tabindex="0">
                            <span class="title">Add...</span>
                            <ul id="childList">
                                <li class="instance-lnk"><a href="javascript:addElement('instance');" tabindex="0">instance</a></li>
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

                    <div id="docWrapper">
                        <div id="docPane">
                            <xsl:variable name="elements" select="//xf:model[not(ancestor::xf:*)]|//xf:group[not(ancestor::xf:*)]"/>
                            <!--<xsl:variable name="uiElements" select="//*[name()='xf:group']"/>-->

                            <div id="xfDoc" class="xfDoc" tabindex="0">
                                <ul>
                                    <li id="root" data-xf-type="document">
                                        <a href="some_value_here">Document</a>
                                        <ul>
                                            <xsl:for-each select="$elements">
                                                <xsl:apply-templates select="." />
                                            </xsl:for-each>
                                        </ul>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div id="rightPane" tabindex="0">
                            <div id="xfMount" dojotype="dijit.layout.ContentPane"
                                 href="/betterform/forms/incubator/editor/document.html"
                                 executeScripts="true"
                                 preload="true"
                                 />
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
                                    // the `plugins` array allows you to configure the active plugins on this instance
                                    "plugins" : ["themes","html_data","ui","crrm","hotkeys"],

                                    // each plugin you have included can have its own config object
                                    "core" : { "initially_open" : [ "root" ] },

                                    // it makes sense to configure a plugin only if overriding the defaults
                                    "ui": {
                                        context : { // Could be a function that should return an object like this one
                                        "add" : {
                                            "id"                : "add-cmd",
                                            "separator_before"	: false,
                                            "separator_after"	: true,
                                            "label"				: "Add",
                                            "action"			: function (obj) { this.create(obj); }
                                            }
                                        }
                                    }
                                })

                                .bind("select_node.jstree", function (event, data) {
                                    // `data.rslt.obj` is the jquery extended node that was clicked
                                    console.log(data);
                                    // alert( data.rslt.obj.attr("data-xf-type"));
                                    var xfType = data.rslt.obj.attr("data-xf-type");
                                    var id=data.rslt.obj.attr("id");
                                    dojo.attr(dojo.byId("xfMount"),"xfId", id);

                                    dijit.byId("xfMount").set("href", "/betterform/forms/incubator/editor/" + xfType + ".html");
                                    dojo.publish("nodeSelected",[{xfType:xfType,id:id}]);
                                })
                            // EVENTS
                            // each instance triggers its own events - to process those listen on the container
                            // all events are in the `.jstree` namespace
                            // so listen for `function_name`.`jstree` - you can function names from the docs
                                .bind("loaded.jstree", function (event, data) {
                                    // you get two params - event & data - check the core docs for a detailed description
                        });
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
                    dojo.subscribe("nodeSelected", function(args){
                        console.log("I got: ", args);
                        console.log("I got: ", args.xfType);
                        var xfType = args.xfType;
                        console.log("prototypes",eval(xfType+"Childs"));

                        dojo.query("#childList li").forEach(
                              function(item, index, array){
                                    var currentClass = dojo.attr(item,"class");
                                    var cutted = currentClass.substring(0,currentClass.indexOf("-",4))
                    console.log("cutted:",cutted);

                                    var childArray=eval(xfType+"Childs");
                                    if(dojo.indexOf(childArray,cutted) != -1){
                                        dojo.attr(item,"style","display:inline;");
                                    }else{
                                        dojo.attr(item,"style","display:none;");
                                    }

                                    //check if actions should be there by looking for 'action' element in the relevant array
                                    var hasActions = dojo.indexOf(childArray,"action") != -1;
                                    dojo.query("#actionlist li").forEach(
                                        function(item,index,array){
                                            if(hasActions){
                                                dojo.attr(item,"style","display:inline;");
                                            }else{
                                                dojo.attr(item,"style","display:none;");
                                            }
                                        }
                                    );

                              }
                         );
                     });
                </script>

            </body>
        </html>
    </xsl:template>

    <xsl:template match="xf:*">
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


        <xsl:variable name="props"><xsl:for-each select="@*">
                <xsl:value-of select="local-name()"/>:'<xsl:value-of select="."/>'<xsl:if test="position()!=last()">,</xsl:if>
            </xsl:for-each></xsl:variable>


        <li id="{$id}" data-xf-type="{local-name()}" data-xf-attrs="{$props}" class="{local-name()}">
<!--
            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name()}"><xsl:value-of select="."/></xsl:attribute>
            </xsl:for-each>
-->
            <a href="#"><xsl:value-of select="local-name()"/>:<xsl:value-of select="@id"/>
                <div class="buttonWrapper">
                    <button name="add">add</button>
                </div>
            </a>
<!--
        <xsl:variable name="props">[<xsl:for-each select="@*">
                <xsl:value-of select="local-name()"/>:'<xsl:value-of select="."/>'<xsl:if test="position()!=last()">,</xsl:if>
            </xsl:for-each>]</xsl:variable>

        <li id="{$id}" data-xf-type="{local-name()}" class="{local-name()}">
            <xsl:attribute name="data-xf-props"><xsl:value-of select="$props"/></xsl:attribute>
            <a href="#"><xsl:value-of select="local-name()"/>:<xsl:value-of select="@id"/></a>
-->
            <xsl:if test="count(xf:*) != 0">
                <ul>
                    <xsl:for-each select="*">
                        <xsl:apply-templates select="." />
                    </xsl:for-each>
                </ul>
            </xsl:if>
        </li>
     </xsl:template>


<!--
    <ul>
        <li id="phtml_1">
            <a href="#">Root node 1</a>
            <ul>
                <li id="phtml_2">
                    <a href="#">Child node 1</a>
                </li>
                <li id="phtml_3">
                    <a href="#">Child node 2</a>
                </li>
            </ul>
        </li>
        <li id="phtml_4">
            <a href="#">Root node 2</a>
        </li>
    </ul>
-->

    <xsl:template match="xf:*/text()"/>
</xsl:stylesheet>
