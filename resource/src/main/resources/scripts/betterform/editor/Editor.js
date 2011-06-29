dojo.provide("betterform.editor.Editor");


dojo.require("dijit.layout.ContentPane");
dojo.require("dojox.json.ref");

dojo.provide("betterform.Editor");

dojo.declare("betterform.Editor", null,
{
    currentjsTreeData:null,

    constructor:function() {
        dojo.subscribe("nodeSelected", function(args){
            console.log("nodeSelected: arg:", args);

            var selectedNode = args.jsTreeData;
            var selectedNodeId = selectedNode ? selectedNode.rslt.obj.attr("id"):null;
            var currentNodeId = this.currentjsTreeData ? this.currentjsTreeData.rslt.obj.attr("id"):null;

            console.debug("compare currentNodeId: " + currentNodeId + " with selctedNode: ",selectedNodeId);

            if(currentNodeId == selectedNodeId){
                return;
            }
            this.currentjsTreeData = selectedNode;

            var xfType = args.xfType;
            console.debug("Editor.subscription.nodeSelected: xfType:", xfType);
            if(xfType =="document"){
                //jump back to root
                dijit.byId("xfMount").set("href", "/betterform/forms/incubator/editor/document.html");
                //hide addToolbars
                dojo.query("#actionlist li").forEach(
                    function(item,index,array){
                        dojo.attr(item,"style","display:none;");
                    }
                );
                dojo.query("#childList li").forEach(
                    function(item,index,array){
                        dojo.attr(item,"style","display:none;");
                    }
                );

                return;
            }


/*
            var pMenu = new dijit.Menu({
                        targetNodeIds: [args.id+"-addMenu"] ,
                        leftClickToOpen: true
                    });


            var childElements = eval(xfType+"Childs");
            dojo.forEach(childElements, function(itemName){
                console.debug("Add Target: ",itemName);
                pMenu.addChild(new dijit.MenuItem({
                            label: itemName
                        }));

            });
            pMenu.startup();
*/
            console.debug("creating menu ....", xfType + "Menu('" + args.id + "')") ;

            var funcCall = xfType + "Menu('" + args.id + "-addMenu');";
            eval(funcCall);

            dojo.query("#childList li").forEach(
                  function(item, index, array){
                        var currentClass = dojo.attr(item,"class");
                        var cutted = currentClass.substring(0,currentClass.indexOf("-",4))

                        var childArray=eval(xfType+"Childs");
                        if(childArray == undefined){
                            return;
                        }
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
    },


    editProperties : function(targetId) {
        console.log("attrEditor.editProperties: id of property sheet: ", targetId);
        var  currentNode =  dojo.byId(targetId);
        var dataXfAttrs = dojo.attr(currentNode, "data-xf-attrs");
        var dataXfType = dojo.attr(currentNode, "data-xf-type");

        // console.log("editProperties: dataXfAttrs: ", dataXfAttrs, " dataXfType", dataXfType);

        var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
        // console.log("editProperties xfAttrObj:", xfAttrObj);
        if (xfAttrObj) {
            for (attributeName in xfAttrObj) {
                var xfAttrValue = xfAttrObj[attributeName];
                if (!xfAttrValue)xfAttrValue = "";
                var currentDijitNode =  dojo.query("xf" + attributeName)[0];
                console.log("editProperties: currentDijitNode: ", currentDijitNode);
                if (currentDijitNode) {
                    var currentDijit = dijit.byId(dojo.attr(currentDijitNode, "id"));
                    if (currentDijit) {
                        console.log("editProperties: currentDijit: ", currentDijit, " - xfAttrValue:",xfAttrValue);
                        currentDijit.set("value", xfAttrValue);
                    }
                    else {
                        console.log("editProperties: currentNode: ", currentDijitNode, " - xfAttrValue:",xfAttrValue);
                        dojo.attr(currentDijitNode, "value", xfAttrValue);
                    }
                }
                else {
                    console.log("editProperties: currentNode: ", dojo.byId(attributeName), " - xfAttrValue:",xfAttrValue);
                    dojo.attr(dojo.byId(attributeName), "value", xfAttrValue);

                }

            }
        } else {
            console.warn("editProperties: Missing xfAttrObj for Element [id='",targetId,"']");
        }
        var valueNode = dojo.query(".textNode",currentNode)[0];
        console.log("editProperties: valueNode: ", valueNode);
        if(valueNode) {
            var nodeValue =  valueNode.innerHTML;
            nodeValue.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
            //var nodeValue =  valueNode.innerHTML.replace(/</g, "lt;").replace(/>/g, "gt;").replace(/\&/g, "amp;").replace(/\"/g, "quot;");
            console.debug("editProperties: node value: ", nodeValue);
            var mixedContentNode =  dojo.byId("mixedContent");
            var mixedContentDijit = dijit.byId(dojo.attr(mixedContentNode,"id"));
            if (mixedContentDijit) {
                mixedContentDijit.set("value",nodeValue);
            }else {
                dojo.attr(mixedContentNode, "value", nodeValue);
            }

        }



    },

  
    saveProperty:function(targetId, propertyId) {
        console.log("attrEditor.saveProperty: id", targetId, " propertyId:", propertyId);
        // get the former attribute values
        var dataXfAttrs = dojo.attr(dojo.byId(targetId), "data-xf-attrs");
        // console.log("dataXfAttrs orig: ", dataXfAttrs);
        var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
        // console.log("xfAttrObj:", xfAttrObj);

        // get the dijit holding the attribute value to save
        var propertyNode = dojo.byId(propertyId);
        // console.log("propertyNode:", propertyNode);
        var newValue = dojo.attr(propertyNode, "value");
        // console.log("newValue:", newValue);
        if (!newValue)newValue = "";
        xfAttrObj[propertyId] = newValue;
        var result = "{";
        for(attrValue in xfAttrObj){
            result += attrValue + ":'" + xfAttrObj[attrValue] +"',";
        }
        if(result.charAt(2) != undefined) {
            result = result.substring(0,result.lastIndexOf(","));
        }

        result += "}";
        // var xfAttrString = dojox.json.ref.toJson(xfAttrObj);
        console.debug("xfAttr new:", result);
        dojo.attr(dojo.byId(targetId), "data-xf-attrs", result);
    },
    

    moveItemUp : function(event) {
        console.debug("moveItemUp:  currentjsTreeData:",this.currentjsTreeData);
        console.debug("moveItemUp: data.rslt.obj.attr('id'): ",jsTreeObject.currentjsTreeData.rslt.obj.attr("id"));

        console.debug("move item down next:",$("#xfDoc"));
        console.debug("move item down next:",$("#xfDoc").jstree);

        // console.debug("move item down tree index:",$("#xfDoc").jstree("get_next",this.currentjsTreeData.rslt.obj));
        console.debug("move item down tree index:",$.jstree._reference("#xfDoc").get_index());


        // console.debug("moveItemUp: data.inst._get_parent(this).attr('id'): ",this.currentjsTreeData.inst._get_parent(this).attr("id"));
        // console.debug("moveItemUp: data.rslt.obj.attr('rel'): ",this.currentjsTreeData.rslt.obj.attr("rel"));
        //console.debug("move item up :",$.tree_reference('xfDoc').get_prev());
    },

    moveItemDown : function(event) {
        console.debug("move item down event:",event, "currentjsTreeData:",this.currentjsTreeData);
        console.debug("moveItemUp: data.rslt.obj.attr('id'): ",this.currentjsTreeData.rslt.obj.attr("id"));
        console.debug("move item down next:",$("#xfDoc"));
        console.debug("move item down next:",$("#xfDoc").jstree);

        var data = this.currentjsTreeData.rslt.obj;


        // console.debug("move item down tree index:",$("#xfDoc").jstree("get_next",data.ui.selected,false));
        console.debug("move item down tree index:",$.jstree._reference("#xfDoc").get_index());

        //console.debug("move item down next:",$.tree_reference('xfDoc').get_next());
        // console.debug("moveItemUp: data.inst._get_parent(this).attr('id'): ",this.currentjsTreeData.inst._get_parent(this).attr("id"));
        // console.debug("moveItemUp: data.rslt.obj.attr('rel'): ",this.currentjsTreeData.rslt.obj.attr("rel"));

    },

    moveAllowed:function(origin, target) {
        // console.debug("moveAllowed origin:", origin, " target:",target);
        var xfType = origin.attr("data-xf-type");
        // console.log("xfType ",xfType);
        var targetType = target.attr("data-xf-type");
        // console.log("check target:",targetType);

        //check rules look for match in drop target elements list of allowed children
        //if found 'true' 'false' otherwise
        var childArray=eval(targetType+"Childs");
        // console.debug("childArray: ",childArray);
        if(childArray == undefined){
            return false;
        }
        if(dojo.indexOf(childArray,xfType) != -1){
            return true;
        }else{
            return false;
        }
    } ,

    moveNodeUp:function(jsTreeObject){
        var selectedNode = jsTreeObject.data.ui.selected;
        // console.debug("move item up: selectedNode", selectedNode);
        var prevNode = jsTreeObject._get_prev(selectedNode);
        // console.debug("move item up: nextNode", prevNode);

        var parentNode = jsTreeObject._get_parent(prevNode);
        var isMovingAllowed = attrEditor.moveAllowed(selectedNode, parentNode);

        if (isMovingAllowed) {
            jsTreeObject.move_node(selectedNode, prevNode, "before", false, false, isMovingAllowed);
            jsTreeObject.hover_node(selectedNode);
        }
        else {
            console.debug("Moving Node not allowed");
        }
        jsTreeObject.hover_node(selectedNode);
    },

    moveNodeDown:function(jsTreeObject) {
        var selectedNode = jsTreeObject.data.ui.selected;
        //console.debug("move item down: selectedNode",selectedNode);
        var nextNode = jsTreeObject._get_next(selectedNode);
        // console.debug("move item down: nextNode",nextNode);

        var parentNode = jsTreeObject._get_parent(nextNode);
        if (!parentNode) {
            // console.debug("parentNode does not exist: return");
            return;
        }
        var isMovingAllowed = this.moveAllowed(selectedNode, parentNode);
        // console.debug("isMovingAllowed: ",isMovingAllowed);
        if (isMovingAllowed) {
            jsTreeObject.move_node(selectedNode, nextNode, "after", false, false, isMovingAllowed);
            jsTreeObject.hover_node(selectedNode);
        }
        else {
            //  console.debug("Moving Node not allowed");
        }
    },

    showEventListener:function() {
        console.debug("showEventListener arguments:",arguments);
    }

});



