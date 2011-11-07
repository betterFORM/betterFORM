/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

/*
  Implementation of main tree that holds the edited markup
 */
$(function () {
    // TO CREATE AN INSTANCE
    // select the tree container using jQuery
    $("#xfDoc")
        // call `.jstree` with the options object
        .jstree({
            // each plugin you have included can have its own config object
                "core" : { "initially_open" : [ "document","root","group","switch","repeat" ] },
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
                        var childArray = eval(targetType + "Childs");
                        if (childArray == undefined) {
                            return false;
                        }
                        if (dojo.indexOf(childArray, xfType) != -1) {
                            return true;
                        } else {
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
                    xformsEditor.moveNodeUp(this)
                },

                "Alt+down" : function (event) {
                    // console.debug("Alt+down: event:",event);
                    xformsEditor.moveNodeDown(this);
                },
                "ctrl+p" : function (event) {
                    alert("preview");
                }
            }
,
            // the `plugins` array allows you to configure the active plugins on this instance
            "plugins" : ["themes","html_data","ui","crrm","hotkeys","dnd"]
        })
        .bind("select_node.jstree", function (event, data) {
            // `data.rslt.obj` is the jquery extended node that was clicked
            // alert( data.rslt.obj.attr("data-xf-type"));
            var tmpId = data.rslt.obj.attr("id");
            var nodeIsLoaded = xformsEditor.nodeIsLoaded(tmpId);
            // console.debug("nodeIsLoaded:", nodeIsLoaded);
//            console.debug("event: " , event);
//            console.debug("data: " , data);

            if (nodeIsLoaded) {
                // console.debug("PREVENTED LOADING OF PROPERTY EDITOR");
                return;
            } else {
                // console.debug(data);
                var xfType = data.rslt.obj.attr("data-xf-type");
                console.debug("xfType:",xfType);

                var mountNode = dojo.byId("xfMount");
                //Save id of current selected Tree-Leaf on root-node of tree
                dojo.attr( dojo.byId("xfDoc"), "data-bf-currentid", tmpId);
                dojo.attr( dojo.byId("xfDoc"), "data-bf-xftype", xfType);

                var nodesToDestroy = dojo.query("*[widgetId]", mountNode);
                // console.debug("nodesToDestroy: ",nodesToDestroy);
                dojo.forEach(nodesToDestroy, function(item) {
                    var tmpDijit = dijit.byId(dojo.attr(item, "widgetId"));
                    tmpDijit.destroy();
                });
                // console.debug("destroyed existing nodes");
                // console.debug("tmpBfPath:",tmpBfPath);
//                dijit.byId("xfMount").set("href", EDITOR_HOME + xfType + ".html");
                fluxProcessor.setControlValue("currentType",xfType);
                /*
                the following event triggers automatic update of embedded propertyform. If that's not performant
                or to expensive the explicit updating via a contextmenu trigger can be used instead. The relevant
                code is commented in place.
                */
                fluxProcessor.dispatchEvent("t-loadProperties");

                dojo.place(dojo.byId("contextBar"), dojo.query("#"+tmpId + " .buttonWrapper")[0], "last");
//                dojo.place(dojo.byId("componentTree"), dojo.query("#"+tmpId + " .buttonWrapper")[0], "last");
//                dojo.byId('componentTree').style("right:300px;top:200px;");

                //console.debug("publish: nodeSelected: data", data);
                dojo.publish("nodeSelected", [
                    {event:event,xfType:xfType,id:tmpId,jsTreeData:data,bfPath:EDITOR_HOME}
                ]);

            }
        })
        // EVENTS
        // each instance triggers its own events - to process those listen on the container
        // all events are in the `.jstree` namespace
        // so listen for `function_name`.`jstree` - you can function names from the docs
        .bind("loaded.jstree", function (event, data) {
            // you get two params - event & data - check the core docs for a detailed description
            var overlay=dojo.byId("overlay");
            dojo.fadeOut({ node: overlay,duration:600 }).play();
            dojo.destroy("overlay");
        });
});

$("#xfDoc").delegate("a", "click", function() {
    $("#xfDoc").jstree("toggle_node", this);
});

function addElement(type, position) {
    //hide component menu
    dojo.style("componentTree","display", "none");
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
    dojo.create("img", { width:"24", height: "24",src: betterFORMContextPath + editorContextPath + "images/list-remove.png" },
        btnDelete);
    if (type.toLowerCase() == "label" || type.toLowerCase() == "alert" || type.toLowerCase() == "hint" || type.toLowerCase() == "help") {
        var textNode = dojo.create("span", textNode, ahref);
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

function serializeTree() {
    var serializedTree = new XMLSerializer().serializeToString(document.getElementById("xfDoc"));
    // console.debug(serializedTree);
    dijit.byId("fluxProcessor").setControlValue("save", serializedTree);
    dijit.byId("fluxProcessor").dispatchEvent("transform2xf");

}




