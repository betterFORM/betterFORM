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
                <script type="text/javascript">
                    dojo.require("dijit.layout.ContentPane");

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
                        margin-top:50px;
                    }
                    #xfDoc{
                        overflow:auto;
                    }
                    #topPane{
                        height: 50px;
                        background: #E6E6E7;
                        width: 100%;
                        position:fixed;
                        z-index:100;
                    }
                    #topPane img{
                        width:32px;
                        height:32px;
                        float:right;
                        margin:9px;
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
                        margin-top:50px;
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
                        line-height: 24px;
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
                </style>
            </head>
            <body>
                <div style="display:none">
                    <xf:model id="model-1">
                        <xf:instance id="i-default">
                            <data xmlns="">
                            </data>
                        </xf:instance>
                    </xf:model>
                </div>

                <div id="mainWindow" style="width:100%;">

                    <div id="topPane">
                        <img src="/betterform/bfResources/images/betterform_icon16x16.png"/>
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
                                 href="/betterform/forms/incubator/editor/initial.html"
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
                        console.log(new XMLSerializer().serializeToString( document.getElementById("xfDoc") ));
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
                                    "core" : { "initially_open" : [ "root" ] }
                                    // it makes sense to configure a plugin only if overriding the defaults
                                })
                                .bind("select_node.jstree", function (event, data) {
                                    // `data.rslt.obj` is the jquery extended node that was clicked
                                    console.log(data);
                                    // alert( data.rslt.obj.attr("data-xf-type"));
                                    var xfType = data.rslt.obj.attr("data-xf-type");
                                    var id=data.rslt.obj.attr("id");
                                    dojo.attr(dojo.byId("xfMount"),"xfId", id);

                                    dijit.byId("xfMount").set("href", "/betterform/forms/incubator/editor/" + xfType + ".html");
                                    // alert('hallo');
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


        <li id="{$id}" data-xf-type="{local-name()}" data-xf-attrs="{$props}">
<!--
            <xsl:for-each select="@*">
                <xsl:attribute name="{local-name()}"><xsl:value-of select="."/></xsl:attribute>
            </xsl:for-each>
-->
            <a href="#"><xsl:value-of select="local-name()"/>:<xsl:value-of select="@id"/></a>
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
