/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
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
                        //console.debug("mainTree.js: check_move: m:", m);
                        //console.debug("mainTree.js: check_move: origin", m.o);
                        var origin = m.o;

                        //the the xf type
                        var xfType = origin.attr("data-xf-type");

                        //console.debug("mainTree.js: check_move: xfType ", xfType);

                        var target = m.r;
                        //console.debug("mainTree.js: check_move: target:", target);

                        var targetType = target.attr("data-xf-type");

                        //Fixe names.
                        if (targetType == 'instance-root') {
                            targetType = 'instanceroot';
                        } else if (targetType == 'instance-data') {
                            targetType = 'instancedata';
                        }
                        //console.debug("mainTree.js: check_move: targetType:", targetType);

                        //check rules
                        //look for match in drop target elements list of allowed children
                        //if found 'true' 'false' otherwise
                        var childArray = eval(targetType + "Childs");
                        if (childArray == undefined) {
                            //console.debug("mainTree.js: check_move: not allowed.");
                            return false;
                        }
                        if (dojo.indexOf(childArray, xfType) != -1) {
                            //console.debug("mainTree.js: check_move: allowed.");
                            return true;
                        } else {
                            //console.debug("mainTree.js: check_move: not allowed.");
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
            "types" : {
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
            "ui":{
                selected_parent_close:false,
                selected_parent_open:false
            },
            "hotkeys": {
                "up":function(event) {
                    console.debug("up key pressed");
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
                },
                "del" : function(event) {
                    console.debug('del:', this.data.ui.selected.attr('id'));
                    if (this.data.ui.selected.attr('data-xf-type') == 'instance-data') {
                       dojo.place(dojo.byId("nodeNameInput"), dojo.byId("parkNodeInput"), "last");
                    }
                    dojo.place(dojo.byId("contextBar"), dojo.byId("parkToolbar"), "last");

                    this.remove();
                }
            },

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
            console.debug("data: ", data);
            console.debug("tmpId: ", tmpId);


            if (nodeIsLoaded) {
                // console.debug("PREVENTED LOADING OF PROPERTY EDITOR");
                return;
            } else {
                // console.debug(data);
                var xfType = data.rslt.obj.attr("data-xf-type");
                console.debug("xfType:", xfType);

                var mountNode = dojo.byId("xfMount");
                //Save id of current selected Tree-Leaf on root-node of tree
                var oldXfType = dojo.attr(dojo.byId("xfDoc"), "data-bf-xftype");
                var oldId = dojo.attr(dojo.byId("xfDoc"), "data-bf-currentid");

                dojo.attr(dojo.byId("xfDoc"), "data-bf-currentid", tmpId);
                dojo.attr(dojo.byId("xfDoc"), "data-bf-xftype", xfType);

                var nodesToDestroy = dojo.query("*[widgetId]", mountNode);
                // console.debug("nodesToDestroy: ",nodesToDestroy);
                dojo.forEach(nodesToDestroy, function(item) {
                    var tmpDijit = dijit.byId(dojo.attr(item, "widgetId"));
                    tmpDijit.destroy();
                });
                // console.debug("destroyed existing nodes");
                // console.debug("tmpBfPath:",tmpBfPath);
//                dijit.byId("xfMount").set("href", EDITOR_HOME + xfType + ".html");
                if (xfType != undefined) {
                    fluxProcessor.setControlValue("currentType", xfType);
                    /*
                     the following event triggers automatic update of embedded propertyform. If that's not performant
                     or to expensive the explicit updating via a contextmenu trigger can be used instead. The relevant
                     code is commented in place.
                     */
                    fluxProcessor.dispatchEvent("t-loadProperties");
                    console.debug("mainTree.js: oldXfType " + oldXfType);
                    console.debug("mainTree.js: oldId " + oldId);
                    // && dojo.query('#' + tmpId + ' #nodeNameInput').lenght < 1
                    if (oldXfType == "instance-data") {
                        console.debug("mainTree.js: hide instance-data");
                        xformsEditor.hideNodeNameInput(oldId);
                    }
                    if (xfType == "instance-data") {
                        console.debug("mainTree.js: show instance-data")
                        //$("#xfDoc").jstree("rename", null);
                        xformsEditor.placeNodeNameInput(tmpId);
                    }

                    dojo.place(dojo.byId("contextBar"), dojo.query("#" + tmpId + " .buttonWrapper")[0], "last");
                    //                dojo.place(dojo.byId("componentTree"), dojo.query("#"+tmpId + " .buttonWrapper")[0], "last");
                    //                dojo.byId('componentTree').style("right:300px;top:200px;");

                    //console.debug("publish: nodeSelected: data", data);
                    dojo.publish("nodeSelected", [
                        {event:event,xfType:xfType,id:tmpId,jsTreeData:data,bfPath:EDITOR_HOME}
                    ]);
                }

            }
        })
        // EVENTS
        // each instance triggers its own events - to process those listen on the container
        // all events are in the `.jstree` namespace
        // so listen for `function_name`.`jstree` - you can function names from the docs
        .bind("loaded.jstree", function (event, data) {
            // you get two params - event & data - check the core docs for a detailed description
            var overlay = dojo.byId("overlay");
            dojo.fadeOut({ node: overlay,duration:600 }).play();
            dojo.destroy("overlay");
        });
});

$("#xfDoc").delegate("a", "click", function() {
    $("#xfDoc").jstree("toggle_node", this);
});

function addElement(type, position) {
    //hide component menu
    dojo.style("componentTree", "display", "none");
    //hide Input

    console.debug("addElement type:", type);
    var betterFORMContextPath = "{$APP_CONTEXT}";
    var editorContextPath = "{$EDITOR_HOME}";

    var elem = $("#xfDoc").jstree("create", null, position, type, false, true);
    elem.attr("data-xf-type", type);
    elem.attr("rel", type);
    elem.attr("id", new Date().getTime());
    if (type == "instance-data") {
        elem.attr('data-xf-attrs', '{ "instancenodevalue":"" }' );
    } else {
        elem.attr("data-xf-attrs", "{ }");
    }
    elem.addClass(type);

    if (type == "instance-data") {
        elem.attr("nodename", type);
    }
    //$(elem).children('a').add('<span>' + type + '</span>').addClass('elementName');

    /*
     var btnDelete = dojo.create("button", { type:"button", style: "padding: 0pt; margin: 0pt; background: none repeat scroll 0% 0% transparent; border: medium none;", onclick: "if(confirm('Really delete?')) deleteNode(this);return false;" },
     span);
     dojo.create("img", { width:"24", height: "24",src: betterFORMContextPath + editorContextPath + "images/list-remove.png" },
     btnDelete);
     */

    //TODO: ??? wieso denn nicht ???
    if ($('#' + type + '-template').length > 0) {
        var template = $('#' + type + '-template');
        //Ensure we have the right type
        if ($('#' + type + '-template').attr('data-xf-type') == type) {
            //Select parent
            //$('#xfDoc').jstree("select_node", $(elem), true, null);

            //Copy all attributes except 'id'
            $.each($('#' + type + '-template')[0].attributes, function() {
                if (this.name != 'id') {
                    elem.attr(this.name, this.value);
                }
            });

            //Select parent
            //$('#xfDoc').jstree("select_node", $(elem), true, null);

            //Handle children

            //$.each($('#' + type + '-template span'), function()
            //console.debug("NOBODY THINKS OF THE CHILDREN: ", $(template).children('span'));
            $.each($(template).children('span'), function() {
                    //console.debug("this: ", this)
                    buildElementFromTemplate(elem, this)
                }
            );
        }

        //$('#' + type + '-template ul').clone().appendTo('#' + elem.attr('id'));
        //$.each($('#' + elem.attr('id') + ' li'), function() { $(this).attr('id', new Date().getTime());});
        //elementName.text(elem.attr(''+ $("#displayLookup").children('' + type).attr('display')));
    }

    var ahref = dojo.query("a", elem[0])[0];


    var text = ahref.lastChild.textContent
    ahref.textContent = "";

    var elementName = dojo.create("span", {innerHTML:text}, ahref);
    dojo.addClass(elementName, "elementName");
    if (type.toLowerCase() == "label" || type.toLowerCase() == "alert" || type.toLowerCase() == "hint" || type.toLowerCase() == "help") {
        //console.debug("addElement: creating textnode")
        var textNode = dojo.create("span", textNode, ahref);
        dojo.addClass(textNode, "textNode");
    }
    var displayProps = dojo.create("span", null, ahref);
    dojo.addClass(displayProps, "displayProps");
    var buttonWrapper = dojo.create("span", null, ahref);
    dojo.addClass(buttonWrapper, "buttonWrapper");

    $("#xfDoc").jstree("select_node", elem, true, null);
    elem.focus();
    elem.hide();
    $(elem).fadeIn("slow");
}

function buildElementFromTemplate(parent, elementDOM) {
    //console.debug('buildElementFormTemplate element:', elementDOM);
    var type = $(elementDOM).attr('data-xf-type');
    //console.debug('buildElementFormTemplate type:', type);
    var child = $("#xfDoc").jstree("create", parent, "inside", type, false, true);
    $.each($(elementDOM)[0].attributes, function() {
            child.attr(this.name, this.value);
        }
    );

    child.attr("data-xf-type", type);
    child.attr("rel", type);
    child.attr("id", new Date().getTime());
    if (child.attr("data-xf-attrs") == undefined) {
        child.attr("data-xf-attrs", "{ }");
    }
    child.addClass(type);

    var ahref = dojo.query("a", child[0])[0];
    var text = ahref.lastChild.textContent
    ahref.textContent = "";

    var elementName = dojo.create("span", {innerHTML:text}, ahref);
    dojo.addClass(elementName, "elementName");
    if (type.toLowerCase() == "label" || type.toLowerCase() == "alert" || type.toLowerCase() == "hint" || type.toLowerCase() == "help") {
        //console.debug("addElement: creating textnode")
        var textNode = dojo.create("span", textNode, ahref);
        dojo.addClass(textNode, "textNode");
    }
    var displayProps = dojo.create("span", null, ahref);
    dojo.addClass(displayProps, "displayProps");
    var buttonWrapper = dojo.create("span", null, ahref);
    dojo.addClass(buttonWrapper, "buttonWrapper")

    if (type == "instance-data") {
        child.attr("nodename", type);
    }
    //console.debug("NOBODY THINKS OF THE CHILDREN: ", $(elementDOM).children('span'));
    //All the way down to the basement
    if ($(elementDOM).children('span') != undefined) {
        $.each($(elementDOM).children('span'), function() {
            buildElementFromTemplate(child, this)
        });
    }
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




