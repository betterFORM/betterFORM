/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.editor.Editor");

/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
dojo.require("dijit.layout.ContentPane");
dojo.require("dojo.behavior");
dojo.require("dojox.json.ref");
dojo.require("dojox.data.FileStore");
dojo.require("dojox.form.FilePickerTextBox");
dojo.require("dijit.Tree");
dojo.require("dijit.form.TextBox");
dojo.require("dojo.NodeList-manipulate");

dojo.declare("betterform.editor.Editor", null,
{
    currentjsTreeData:null,
    currentNodeId:null,
    currentXfType:null,
    bfPath:null,

    constructor:function() {
        var self = this;
        dojo.subscribe("nodeSelected", function(args){
            // console.debug("nodeSelected: arg:", args);
            // console.debug("nodeSelected: arg.event:", args.event);

            var selectedNode = args.jsTreeData;
            var selectedNodeId = selectedNode ? selectedNode.rslt.obj.attr("id"):null;
            currentNodeId = this.currentjsTreeData ? this.currentjsTreeData.rslt.obj.attr("id"):null;

            console.debug("compare currentNodeId: " + currentNodeId + " with selectedNode: ",selectedNodeId);

            if(currentNodeId =! null && currentNodeId == selectedNodeId){
                args.event.stopPropagation();
                args.event.cancelBubble=true;
                return;
            }

            this.currentjsTreeData = selectedNode;
            this.currentNodeId = selectedNodeId;
            var xfType = args.xfType;
            this.currentXfType = xfType;

            console.debug("Editor.subscription.nodeSelected: xfType:", xfType);
            if( (xfType == undefined) || (xfType =="document") ){
                //jump back to root
//                console.debug("bfpath: ", args.bfPath);
                //dijit.byId("xfMount").set("href", args.bfPath + "document.html");
                return;
            }
            //self.updateComponentTree(dojo.byId(selectedNodeId));
         });

        dojo.subscribe("/properties/changed", function(args){
            console.debug('subscribe properties callback');
//            console.debug(args);

            var newProps = dijit.byId('properties-output').getControlValue();
            console.debug("newProps: ",newProps);

            var currentid = dojo.attr(dojo.byId("xfDoc"),"data-bf-currentid");
            var currentNode = dojo.byId(currentid);
            dojo.attr(currentNode,"data-xf-attrs", newProps);

            var children = dojo.query('#'+ currentid + ' > a > .textNode');
            //console.debug('children: ' + children.length);
            if (children.length > 0 && children != undefined && children != '') {
                children[0].innerHTML = dijit.byId('textnodecontent').getControlValue();
            }
            //register new controls ....
            dojo.behavior.apply();
            setDisplayProps(currentNode);
        });
    },

    updateComponentTree : function(mode){
        // console.debug("updateComponentTree: event: ",aNode, " id: ",aNode.id);
                   /*
        if(aNode.id == "btnChildMode"){
            dojo.removeClass("btnSiblingMode","selected");
            dojo.addClass("btnChildMode","selected");

        }
        if(aNode.id == "btnSiblingMode"){
            dojo.removeClass("btnChildMode","selected");
            dojo.addClass("btnSiblingMode","selected");
            dojo.attr(dojo.byId('componentTree'),"data-bf-addmode","sibling");
        }        */

//        var currentMode = dojo.attr("componentTree","data-bf-addMode");
        dojo.attr(dojo.byId('componentTree'),"data-bf-addmode",mode);

        //get selected item from xfDoc tree
        var currentItem = dojo.byId(xformsEditor.currentNodeId);
        if(currentItem == undefined) return;

        //switch state of buttons regardless of an existing selection
        if(mode == "child"){
            // console.debug("currentMode == child");
            //get xfType from current item selected in xfDoc tree
            var currXfType = dojo.attr(dojo.byId(currentItem),"data-xf-type");
            // console.debug("current xfType: ", currXfType);

            this._renderComponentTree(currXfType);
        }else{
            //get parent item (not element!) of current item
            var parentUL = currentItem.parentNode;
            if(parentUL == undefined) return;
            // console.debug("parent: ", parentUL);

            var parentLI = parentUL.parentNode;
            if(parentLI == undefined) return;
            // console.debug("parentLI: ", parentLI);

            var parentXfType = dojo.attr(parentLI,"data-xf-type");
            if(parentXfType == undefined) return;
            // console.debug("parentXfType: ", parentXfType);

            this._renderComponentTree(parentXfType);

        }
    },

    _renderComponentTree:function(xfType){
        console.debug("_renderComponentTree: xfType:",xfType);
        //hide previously displayed top-level nodes
        dojo.query("#componentTree > ul > li").forEach(
              function(item, index, array){
                  var displays = dojo.style(item,"display");
                  if(diplays="block") dojo.style(item,"display","none");
              }
         );


        //show the tree node (first level of tree) for the given xfType - we look for xfType + "-tmpl"
        var rootForType = dojo.style(dojo.byId(xfType+'-tmpl'),"display","block");


        dojo.query("#"+xfType+"-tmpl li").forEach(
              function(item, index, array){
                    dojo.style(item,"display","block");
              }
         );
    },

    editProperties : function(targetId) {
        console.debug("attrEditor.editProperties: id of property sheet: ", targetId);
        var  currentNode =  dojo.byId(targetId);
        var dataXfAttrs = dojo.attr(currentNode, "data-xf-attrs");
        var dataXfType = dojo.attr(currentNode, "data-xf-type");

         console.debug("editProperties: dataXfAttrs: ", dataXfAttrs, " dataXfType", dataXfType);

        //Send Tree-Data to "properties"-subform
        fluxProcessor.setControlValue("dataAttributes", dataXfAttrs);
        var parent = dojo.attr(currentNode.parentNode.parentNode, "data-xf-type");
        console.debug("ParentNode type: ", parent);
        fluxProcessor.setControlValue("parentElement", parent);

        var children = dojo.query('.textNode', currentNode);
        if (children.length > 0) {
            fluxProcessor.setControlValue("textnodecontent", children[0].innerHTML);
        }

        /*
        var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
         console.debug("editProperties xfAttrObj:", xfAttrObj);
        if (xfAttrObj) {
            for (attributeName in xfAttrObj) {
                var xfAttrValue = xfAttrObj[attributeName];
                if (!xfAttrValue)xfAttrValue = "";
                var currentDijitNode =  dojo.query("xf" + attributeName)[0];
                 console.debug("editProperties: currentDijitNode: ", currentDijitNode);
                if (currentDijitNode) {
                    var currentDijit = dijit.byId(dojo.attr(currentDijitNode, "id"));
                    if (currentDijit) {
                         console.debug("editProperties: currentDijit: ", currentDijit, " - xfAttrValue:",xfAttrValue);
                        currentDijit.set("value", xfAttrValue);
                    }
                    else {
                        // console.debug("editProperties: currentNode: ", currentDijitNode, " - xfAttrValue:",xfAttrValue);
                        dojo.attr(currentDijitNode, "value", xfAttrValue);
                    }
                }
                else {
                    // console.debug("editProperties: currentNode: ", dojo.byId(attributeName), " - xfAttrValue:",xfAttrValue);
                    dojo.attr(dojo.byId(attributeName), "value", xfAttrValue);

                }

            }
        } else {
            console.warn("editProperties: Missing xfAttrObj for Element [id='",targetId,"']");
        }
        var valueNode = dojo.query(".textNode",currentNode)[0];
        // console.debug("editProperties: valueNode: ", valueNode);
        if(valueNode) {
            var nodeValue =  valueNode.innerHTML;
            nodeValue.replace(/&/g, "&amp;").replace(/</g, "&lt;").replace(/>/g, "&gt;").replace(/"/g, "&quot;");
            //var nodeValue =  valueNode.innerHTML.replace(/</g, "lt;").replace(/>/g, "gt;").replace(/\&/g, "amp;").replace(/\"/g, "quot;");
            // console.debug("editProperties: node value: ", nodeValue);
            var textContentNode =  dojo.byId("textcontent");
            var textContentDijit = dijit.byId(dojo.attr(textContentNode,"id"));
            if (textContentDijit) {
                textContentDijit.set("value",nodeValue);
            }else {
                dojo.attr(textContentNode, "value", nodeValue);
            }
        }
        */
    },

    /*
    saveProperty:function(targetId, propertyId) {
        // console.debug("attrEditor.saveProperty: id", targetId, " propertyId:", propertyId);

        // get the current value to save
        var propertyNode = dojo.byId(propertyId);
        // console.debug("propertyNode:", propertyNode);
        var newValue = dojo.attr(propertyNode, "value");
        if (!newValue)newValue = "";
        // console.debug("saveProperty: newValue: ",newValue);
        if(propertyId == "textcontent"){
            // get the dijit holding the attribute value to save
            var textcontentNode = dojo.query(".textNode",dojo.byId(targetId))[0];
            // console.debug("saveProperty: textcontent: ",textcontentNode);
            dojo.html.set(textcontentNode, newValue);
        }else {
            // get the former attribute values
            var dataXfAttrs = dojo.attr(dojo.byId(targetId), "data-xf-attrs");
            // console.debug("dataXfAttrs orig: ", dataXfAttrs);
            var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
            // console.debug("xfAttrObj:", xfAttrObj);

            xfAttrObj[propertyId] = newValue;
            var xfAttrString = dojox.json.ref.toJson(xfAttrObj);
            // console.debug("xfAttr new:", xfAttrString);
            dojo.attr(dojo.byId(targetId), "data-xf-attrs", xfAttrString);

        }
    },
    */
    

    moveItemUp : function(event) {
        // console.debug("moveItemUp:  currentjsTreeData:",this.currentjsTreeData);
        // console.debug("moveItemUp: data.rslt.obj.attr('id'): ",jsTreeObject.currentjsTreeData.rslt.obj.attr("id"));

        // console.debug("move item down next:",$("#xfDoc"));
        // console.debug("move item down next:",$("#xfDoc").jstree);

        // console.debug("move item down tree index:",$("#xfDoc").jstree("get_next",this.currentjsTreeData.rslt.obj));
        // console.debug("move item down tree index:",$.jstree._reference("#xfDoc").get_index());


        // console.debug("moveItemUp: data.inst._get_parent(this).attr('id'): ",this.currentjsTreeData.inst._get_parent(this).attr("id"));
        // console.debug("moveItemUp: data.rslt.obj.attr('rel'): ",this.currentjsTreeData.rslt.obj.attr("rel"));
        //console.debug("move item up :",$.tree_reference('xfDoc').get_prev());
    },

    moveItemDown : function(event) {
        /*
        console.debug("move item down event:",event, "currentjsTreeData:",this.currentjsTreeData);
        console.debug("moveItemUp: data.rslt.obj.attr('id'): ",this.currentjsTreeData.rslt.obj.attr("id"));
        console.debug("move item down next:",$("#xfDoc"));
        console.debug("move item down next:",$("#xfDoc").jstree);
        */
        // var data = this.currentjsTreeData.rslt.obj;
        // console.debug("move item down tree index:",$("#xfDoc").jstree("get_next",data.ui.selected,false));
        // console.debug("move item down tree index:",$.jstree._reference("#xfDoc").get_index());

        //console.debug("move item down next:",$.tree_reference('xfDoc').get_next());
        // console.debug("moveItemUp: data.inst._get_parent(this).attr('id'): ",this.currentjsTreeData.inst._get_parent(this).attr("id"));
        // console.debug("moveItemUp: data.rslt.obj.attr('rel'): ",this.currentjsTreeData.rslt.obj.attr("rel"));

    },

    moveAllowed:function(origin, target) {
        // console.debug("moveAllowed origin:", origin, " target:",target);
        var xfType = origin.attr("data-xf-type");
        // console.debug("xfType ",xfType);
        var targetType = target.attr("data-xf-type");
        // console.debug("check target:",targetType);

        //check rules look for match in drop target elements list of allowed children
        //if found 'true' 'false' otherwise
        //Fix names.
        if (targetType == 'instance-root') {
            targetType = 'instanceroot';
        } else if (targetType == 'instance-data') {
            targetType = 'instancedata';
        }
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
        var isMovingAllowed = xformsEditor.moveAllowed(selectedNode, parentNode);

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

    removeNode:function(jsTreeObject){
        var selectedNode = jsTreeObject.data.ui.selected;
        jsTreeObject.remove(selectedNode);
    },

    showEventListener:function() {
        console.debug("showEventListener arguments:",arguments);
    },
    nodeIsLoaded:function(xfid) {
         console.debug("Editor.nodeIsLoaded" , xfid);
         console.debug("Editor.nodeIsLoaded: this.currentNodeId:",this.currentNodeId);
        if(this.currentNodeId != undefined && this.currentNodeId == xfid) {
            return true;
        }
        this.currentNodeId = xfid;
        return false;
    },
    hideNodeNameInput:function(tmpId) {
        console.debug("hideNodeNameInput: tmpId: " + tmpId)
        //Get current value
        if (dojo.query('#' + tmpId + ' > a > .elementName > #nodeNameInput').length > 0) {
            var value= dojo.query('#' + tmpId + ' > a > .elementName > #nodeNameInput').val();
            console.debug("hideNodeNameInput: value" + value)
            //Reset it
            dojo.query("#nodeNameInput").val("");
            //Move Input away.
            dojo.place(dojo.byId("nodeNameInput"), dojo.byId("parkNodeInput"), "last");
            //Update listElement
            dojo.query('#' + tmpId + ' > a > .elementName').text(value);
            var node = dojo.byId(tmpId);
            dojo.toggleClass(node, dojo.attr(node, 'nodename'));
            dojo.attr(node, 'nodename', value);
            dojo.toggleClass(node, dojo.attr(node, 'nodename'));
        }
    },
    placeNodeNameInput:function(tmpId) {
        console.debug("placeNodeNameInput: tmpId:" + tmpId);
        //Get current textValue
        var value = dojo.query('#' + tmpId + ' > a > .elementName')[0].textContent;
        console.debug("placeNodeNameInput: value:" + value);
        //Empty current textValue
        dojo.query('#' + tmpId + ' > a > .elementName').text("");

        //Set input-value
        dojo.query("#nodeNameInput").val(value);
        //Place input
        dojo.place(dojo.byId("nodeNameInput"), dojo.query('#' + tmpId + ' > a > .elementName')[0], "last");
        dojo.byId("nodeNameInput").focus();
    }
});