/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.Repeat");

dojo.require("betterform.ui.container.Container");
dojo.require("dojo.NodeList-fx");

dojo.declare(
        "betterform.ui.container.Repeat",
         betterform.ui.container.Container,
{

    createdRepeatItems:null,
    itemsToRemoveList:[],

    postCreate:function() {
        //console.debug("betterform.ui.container.Repeat.postCreate: ",this);
        this.inherited(arguments);
        this.createdRepeatItems = new Array();
        // console.dirxml(this.srcNodeRef);
    },

    handleSetRepeatIndex:function(/*Map*/ contextInfo){
        // console.debug("handleSetRepeatIndex: position='",contextInfo.index,"' for Repeat id: ", this.id, " this:",this);
        if(eval(contextInfo.index) == 0){
            //this.setFocusOnChild(this.domNode);
            return;
        }
        this._removeRepeatIndexClasses();
        // console.dirxml(this.domNode);
        var repeatIndexNode;
        if(dojo.hasClass(this.domNode,"xfCompactRepeat")) {
            repeatIndexNode = dojo.query("> tbody > .xfRepeatItem", this.domNode)[contextInfo.index-1];
        } else {
            repeatIndexNode = dojo.query("> .xfRepeatItem", this.domNode)[contextInfo.index-1];            
        }
        // console.debug("handleSetRepeatIndex for repeatIndexNode",repeatIndexNode);
        if (repeatIndexNode != undefined) {
            dojo.addClass(repeatIndexNode, "xfRepeatIndex");
            //this.setFocusOnChild(repeatIndexNode);

        }
    },


    setFocusOnChild:function(/*Node*/ node) {
        var firstValue = dojo.query(".xfValue", node)[0];
        if (firstValue != undefined) {
            firstValue.focus();
        }else {
            // if no xfValue is available try to get the first trigger and focus it
            var control = dojo.query(".xfTrigger button")[0];
            if(control == undefined) {
                control = dojo.query("input")[0];
                if(control == undefined){
                    control = window.document.body.firstChild();
                }
            }
            control.focus();
        }

    },

    handleInsert:function(/*Map*/ contextInfo){
        // console.debug("handleInsert contextInfo: ",contextInfo);
        // search for former repeat-index and remove it
        this._removeRepeatIndexClasses();
        // clone prototype and manipulate classes of repeat item
        var prototype = dojo.byId(contextInfo.originalId+"-prototype");
        var insertedNode =prototype.cloneNode(true);
        this._replaceRepeatItemClasses(insertedNode);
        // replace prototype ids with generated ones
        var generatedIds ="";
        if (contextInfo.prototypeId != undefined) {
            generatedIds= contextInfo.generatedIds;
            dojo.attr(insertedNode,"id",generatedIds[contextInfo.prototypeId]);
            // replace prototype ids with generated ones

        } else if (contextInfo.repeatedSelects) {
            generatedIds= contextInfo.repeatedSelects[0].generatedIds;
            // console.debug("Generated Ids: ", generatedIds);
            var clonedNodeId = contextInfo.repeatedSelects[0].generatedIds[0];
            dojo.attr(insertedNode,"id",clonedNodeId);
        }
        this._replacePrototypeIds(insertedNode, generatedIds);

        // create dijit and place it within repeat
        var position = eval(contextInfo.position);
        var repeatItemWidget = this._createRepeatItem(insertedNode,position);
        // console.debug("repeatItemWidget",repeatItemWidget.domNode);
        dojo.query("*[unbound='true']",repeatItemWidget.domNode).forEach(
                function(item) {
                    // console.debug("Create UIControl for unbound item", item);
                    var xfControl = new betterform.ui.Control({},item);
                }
        );
        repeatItemWidget.showRepeatItem();
        // this.createdRepeatItems.push(repeatItemWidget);
        // console.debug("Inserted new Repeat Item", repeatItemWidget.domNode);
    },

    _replaceRepeatItemClasses:function(/* Node */ node) {
      // console.debug("Repeat._replaceRepeatItemClasses node:",node );
      // console.dirxml(node);
        dojo.removeClass(node, "xfRepeatPrototype");
        dojo.removeClass(node, "xfDisabled");
        dojo.addClass(node, "xfRepeatItem");
        dojo.addClass(node, "xfEnabled");
        dojo.addClass(node, "xfRepeatIndexPre");
    },

    _replacePrototypeIds:function(node,generatedIds){
        dojo.query("*", node).forEach(
            function(xfNode) {
                var idAtt = dojo.attr(xfNode,"id");
                var repeatId = dojo.attr(xfNode,"repeatid");
                if(repeatId != undefined && generatedIds[repeatId] != undefined){
                    dojo.attr(xfNode,"repeatid",generatedIds[idAtt]);
                }

                if(idAtt != undefined && generatedIds[idAtt] != undefined){
                    dojo.attr(xfNode,"id",generatedIds[idAtt]);
                }

                else if(idAtt != undefined) {
                    var idPrefix;
                    var idAppendix;

                    if(idAtt.indexOf("-value")!= -1){
                        idPrefix = idAtt.substring(0,idAtt.indexOf("-value"));
                        idAppendix = "-value";
                    }else if(idAtt.indexOf("-label")!= -1){
                        idPrefix = idAtt.substring(0,idAtt.indexOf("-label"));
                        idAppendix = "-label";
                    } else if(idAtt.indexOf("-hint")!= -1){
                        idPrefix = idAtt.substring(0,idAtt.indexOf("-hint"));
                        idAppendix = "-hint";
                    }else if(idAtt.indexOf("-help")!= -1){
                        idPrefix = idAtt.substring(0,idAtt.indexOf("-help"));
                        idAppendix = "-help";
                    } else if(idAtt.indexOf("-alert")!= -1){
                        idPrefix = idAtt.substring(0,idAtt.indexOf("-alert"));
                        idAppendix = "-alert";
                    } else {
                        console.warn("Repeat._replacePrototypeIds Failure replaceing Id! Id to replace: ", idAtt, " generatedIds: ", generatedIds);
                        return;
                    }

                    //console.debug("original Id: " + idAtt + " prefix:: " + idPrefix + " appendix:" +idAppendix);;
                    var generatedId = generatedIds[idPrefix]+  idAppendix;
                    // console.debug("original Id: " + idAtt + " generatedId: " + generatedId);
                    dojo.attr(xfNode, "id", generatedId);
                }
            }
        );

    },
    _createRepeatItem:function(/*Dijit*/node,/* int */position) {
      // console.debug("RepeatItem._createRepeatItem node:",node, " at position " + position);
        var insertPosition = position;
        var repeatItemCount = this._getSize();
        var appearance;
        if(dojo.hasClass(this.domNode,"xfFullRepeat")){
            appearance = "full";
        }else{
            appearance = "compact";
        }
        var repeatItemDijit =   new betterform.ui.container.RepeatItem({repeatId:this.id,appearance:appearance},node);
        repeatItemDijit.hideRepeatItem();



        var targetNode = null;
        if(position == 1 && repeatItemCount > 0) {
            if(dojo.hasClass(this.domNode,"xfCompactRepeat")){
                targetNode = dojo.query("> tbody > .xfRepeatItem",this.domNode)[0];                               
            }else {
                targetNode = dojo.query("> .xfRepeatItem",this.domNode)[0];
            }
            dojo.place(repeatItemDijit.domNode, targetNode,"before");

        }else if(position == 1 && repeatItemCount == 0) {

            if(dojo.hasClass(this.domNode,"xfCompactRepeat")){
              // console.debug("RepeatItem._createRepeatItem for CompactRepeat domNode: ", this.domNode);
                var tbodyNode = dojo.query("tbody",this.domNode)[0];
                if(tbodyNode == undefined) {
                    tbodyNode = dojo.doc.createElement("tbody");
                    dojo.place(tbodyNode,this.domNode);
                }
              // console.debug("RepeatItem._createRepeatItem tbodyNode: ",tbodyNode);
                dojo.place(repeatItemDijit.domNode, tbodyNode);
            }else {
              // console.debug("RepeatItem._createRepeatItem for FullRepeat RepeatNode: ", this.domNode, " RepeatItem2Insert: ",repeatItemDijit);
                dojo.place(repeatItemDijit.domNode, this.domNode);
            }


        }else {
            // insertPosition - 2 causes
            //  1. XForms Position 1 = JavaScript Array Position 1 and
            //  2. Default Insert happens after the targetNode

            if(dojo.hasClass(this.domNode,"xfCompactRepeat")){
              targetNode = dojo.query("> tbody > .xfRepeatItem",this.domNode)[insertPosition-2];
            }else {
              targetNode = dojo.query("> .xfRepeatItem",this.domNode)[insertPosition-2];
            }
          // console.debug("RepeatItem._createRepeatItem targetNode: ", targetNode , " repeatItem: ", repeatItemDijit);
            dojo.place(repeatItemDijit.domNode, targetNode,"after");
        }
      // console.debug("RepeatItem._createRepeatItem Insert at Position "+ insertPosition + " (Orig:" + position + ") + of  :"+(repeatItemCount+1));
        return repeatItemDijit;
    },

    _getSize:function() {
        var size;
        if(dojo.hasClass(this.domNode,"xfCompactRepeat")){
            size = dojo.query("> tbody > .xfRepeatItem", this.domNode).length; 
        } else {
            size = dojo.query("> .xfRepeatItem", this.domNode).length;
        }
        return size;
    },
    _getRepeatItems:function() {
        var repeatItems;
        if(dojo.hasClass(this.domNode,"xfCompactRepeat")){
            repeatItems = dojo.query("> tbody > .xfRepeatItem", this.domNode); 
        } else {
            repeatItems = dojo.query("> .xfRepeatItem", this.domNode);
        }
        return repeatItems;
    },

/*    showRepeatItem:function(){
        // console.debug("Repeat.showRepeatItem", this.createdRepeatItems);
        var repeatItem = null;
        while(this.createdRepeatItems.length > 0){
            repeatItem = this.createdRepeatItems.shift();
            //console.debug("ArrayEntry: " , repeatItem);
            repeatItem.showRepeatItem();
        }
        if(repeatItem != undefined){
            // this.setFocusOnChild(repeatItem.domNode);

        }
    },

*/



    handleDelete:function(/*Map*/ contextInfo){
        var position = eval(contextInfo.position);
        var itemToRemove;
        if(dojo.hasClass(this.domNode, "xfCompactRepeat")){
            itemToRemove = dojo.query("> tbody > .xfRepeatItem", this.domNode)[position-1];
            dojo.query("> tbody", this.domNode)[0].removeChild(itemToRemove);
        }else {
            itemToRemove = dojo.query("> .xfRepeatItem", this.domNode)[position-1];
            this.domNode.removeChild(itemToRemove);
        }
      // console.debug("handleDelete: repeatItemSize:" + repeatItemsSize + " position:" + position, " itemToRemove",itemToRemove);
   
/*
        var repeatItemsSize = itemToRemove.length;
        if(repeatItemsSize == 1){
            console.debug("no setRepeatIndex");
            return;
        }
        else if (repeatItemsSize > position) {
            fluxProcessor.setRepeatIndex(this.id, position);
        } else {
            fluxProcessor.setRepeatIndex(this.id, repeatItemsSize);
        }
*/
    },


    handleStateChanged:function(contextInfo){
        var relevant = eval(contextInfo["enabled"]);
        // console.debug("Repeat.handleStateChanged relevant:",relevant);
        if (relevant) {
            betterform.ui.util.replaceClass(this.domNode, "xfDisabled", "xfEnabled");
        }
        else {
            betterform.ui.util.replaceClass(this.domNode, "xfEnabled", "xfDisabled");            
        }

     },

    _removeRepeatIndexClasses:function() {
        if(dojo.hasClass(this.domNode, "xfCompactRepeat")){
            dojo.query("> tbody > .xfRepeatIndexPre", this.domNode).forEach(
                function(repeatIndexItem) {
                    dojo.removeClass(repeatIndexItem,"xfRepeatIndexPre");
                }
            );
            dojo.query("> tbody > .xfRepeatIndex", this.domNode).forEach(
                function(repeatIndexItem) {
                    dojo.removeClass(repeatIndexItem,"xfRepeatIndex");
                }
            );

        }else {
            dojo.query("> .xfRepeatIndexPre", this.domNode).forEach(
                function(repeatIndexItem) {
                    dojo.removeClass(repeatIndexItem,"xfRepeatIndexPre");
                }
            );
            dojo.query("> .xfRepeatIndex", this.domNode).forEach(
                function(repeatIndexItem) {
                    dojo.removeClass(repeatIndexItem,"xfRepeatIndex");
                }
            );
        }

    }
});


