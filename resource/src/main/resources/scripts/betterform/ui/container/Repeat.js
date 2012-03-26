/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.Repeat");

dojo.require("betterform.ui.container.Container");
dojo.require("dojo.NodeList-fx");

dojo.declare(
        "betterform.ui.container.Repeat",
        betterform.ui.container.Container,
{

    handleSetRepeatIndex:function(/*Map*/ contextInfo) {
        // console.debug("Repeat.handleSetRepeatIndex: contextInfo'",contextInfo, " for Repeat id: ", this.id);
        if(contextInfo != undefined && contextInfo.index != undefined ){
        	this._handleSetRepeatIndex(contextInfo.index);
    	}
    },

    _handleSetRepeatIndex:function(index) {
        // console.debug("Repeat._handleSetRepeatIndex: position='", index,"' for Repeat id: ", this.id);
        var intIndex = eval(index)
        if (intIndex == 0) {
            //this.setFocusOnChild(this.domNode);
            return;
        }
        this._removeRepeatIndexClasses();

        var repeatIndexNode;
        if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
            repeatIndexNode = dojo.query("> tbody > .xfRepeatItem", this.domNode)[intIndex - 1];
        } else {
            repeatIndexNode = dojo.query("> .xfRepeatItem", this.domNode)[intIndex - 1];
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
        } else {
            // if no xfValue is available try to get the first trigger and focus it
            var control = dojo.query(".xfTrigger button")[0];
            if (control == undefined) {
                control = dojo.query("input")[0];
                if (control == undefined) {
                    control = window.document.body.firstChild();
                }
            }
            control.focus();
        }

    },

    handleInsert:function(/*Map*/ contextInfo) {
        // console.debug("handleInsert contextInfo: ",contextInfo);
        // search for former repeat-index and remove it
        this._removeRepeatIndexClasses();

        // clone prototype and manipulate classes of repeat item
        var prototype = dojo.byId(contextInfo.originalId + "-prototype");
        var insertedNode = prototype.cloneNode(true);
        this._replaceRepeatItemClasses(insertedNode);

        // replace prototype ids with generated ones
        var generatedIds = "";
        if (contextInfo.prototypeId != undefined) {
            generatedIds = contextInfo.generatedIds;
            dojo.attr(insertedNode, "id", generatedIds[contextInfo.prototypeId]);
            // replace prototype ids with generated ones

        } else if (contextInfo.repeatedSelects) {
            generatedIds = contextInfo.repeatedSelects[0].generatedIds;
            // console.debug("Generated Ids: ", generatedIds);
            var clonedNodeId = contextInfo.repeatedSelects[0].generatedIds[0];
            dojo.attr(insertedNode, "id", clonedNodeId);
        }
        this._replacePrototypeIds(insertedNode, generatedIds);


        // create dijit and place it within repeat
        var position = eval(contextInfo.position);

        // console.debug("InsertedNode: " + insertedNode.id );
        var repeatItemExists = dojo.query("*[repeatItemId='" + insertedNode.id + "']");
        var repeatItemWidget = undefined;
        if ( repeatItemExists[0] != null ) {
            console.warn("Skipping already present repeatItem: ", repeatItemExists);
            console.debug("repeatItemExists.id: " , dojo.attr(repeatItemExists[0], "id"));
            repeatItemWidget = dijit.byId(dojo.attr(repeatItemExists[0], "id"));
        }else {
            if(!this.domNode.hasChildNodes() && dojo.hasClass(this.domNode,"xfCompactRepeat")){
                this._createCompactRepeatHeader(insertedNode.cloneNode(true));
            }
            repeatItemWidget = this._createRepeatItem(insertedNode, position);
        }
        // console.debug("repeatItemWidget",repeatItemWidget.domNode);
        dojo.query("*[unbound='true']", repeatItemWidget.domNode).forEach(
                function(item) {
                    console.debug("Create UIControl for unbound item", item, " id:",item.id);
                    var controlAlreadyExists = dijit.byId(item.id);
                    if(controlAlreadyExists== undefined) {
                        var xfControl = new betterform.ui.Control({}, item);
                    }else {
                        console.debug("UIControl for RepeatItem " + repeatItemWidget, " is allready present: dijit: ",controlAlreadyExists, "  item: " ,item);
                    }
                }
        );
        repeatItemWidget.showRepeatItem();
        // console.debug("Inserted new Repeat Item", repeatItemWidget.domNode);
    },

    _replaceRepeatItemClasses:function(/* Node */ node) {
        // console.debug("Repeat._replaceRepeatItemClasses node:",node );
        // console.dirxml(node);
        dojo.removeClass(node, "xfRepeatPrototype");
        dojo.removeClass(node, "xfDisabled");
        dojo.addClass(node, "xfRepeatItem");
        dojo.addClass(node, "xfEnabled");
    },

    _replacePrototypeIds:function(node, generatedIds) {
		var compactRepeat = false;
        if(dojo.hasClass(this.domNode,"xfCompactRepeat")) {
	    	compactRepeat = true;
        }

        dojo.query("*", node).forEach(
                function(xfNode) {
                    var idAtt = dojo.attr(xfNode, "id");
                    var repeatId = dojo.attr(xfNode, "repeatid");
                    if (repeatId != undefined && generatedIds[repeatId] != undefined) {
                        dojo.attr(xfNode, "repeatid", generatedIds[idAtt]);
                    }

                    if (idAtt != undefined && generatedIds[idAtt] != undefined) {
                        dojo.attr(xfNode, "id", generatedIds[idAtt]);
                	}

                    else if (idAtt != undefined) {
                        var idPrefix;
                        var idAppendix;

                        if (idAtt.indexOf("-value") != -1) {
                            idPrefix = idAtt.substring(0, idAtt.indexOf("-value"));
                            idAppendix = "-value";
                        } else if (idAtt.indexOf("-label") != -1) {
                            idPrefix = idAtt.substring(0, idAtt.indexOf("-label"));
                            idAppendix = "-label";
                        } else if (idAtt.indexOf("-hint") != -1) {
                            idPrefix = idAtt.substring(0, idAtt.indexOf("-hint"));
                            idAppendix = "-hint";
                        } else if (idAtt.indexOf("-help") != -1) {
                            idPrefix = idAtt.substring(0, idAtt.indexOf("-help"));
                            idAppendix = "-help";
                        } else if (idAtt.indexOf("-alert") != -1) {
                            idPrefix = idAtt.substring(0, idAtt.indexOf("-alert"));
                            idAppendix = "-alert";
                        } else {
                            // console.warn("Repeat._replacePrototypeIds Failure replaceing Id! Id to replace: ", idAtt, " generatedIds: ", generatedIds);
                            return;
                        }

                        //console.debug("original Id: " + idAtt + " prefix:: " + idPrefix + " appendix:" +idAppendix);;
                        var generatedId = generatedIds[idPrefix] + idAppendix;
                        // console.debug("original Id: " + idAtt + " generatedId: " + generatedId);
                        dojo.attr(xfNode, "id", generatedId);
                    }
                }
            );

    },
    _createCompactRepeatHeader:function(node) {
        // console.debug("_createCompactRepeatHeader: START show node",node);
        // console.dirxml(node);
        // console.debug("_createCompactRepeatHeader: END show node");

        var tbody = dojo.create("tbody" ,null,this.domNode,"first");
        // console.debug("_createCompactRepeatHeader: created tbody");

        var repeatHeader = dojo.create("tr" ,null,tbody, "first");
        // console.debug("_createCompactRepeatHeader: created tr");
        dojo.addClass(repeatHeader, "xfRepeatHeader");


        dojo.query("> td", node).forEach(function(columnNode){
                // console.debug("Manipulate node as label node: ",columnNode);
                var tdNode = dojo.create("td" ,{},repeatHeader);
                tdNode.className = columnNode.className;

                var control = dojo.query(".xfControl", columnNode)[0];
                var labelId = dojo.attr(control,"id")+"-label";
                var labelNode = dojo.create("label" ,{id:labelId},tdNode,"first");
                dojo.addClass(labelNode, "xfLabel");
                dojo.addClass(labelNode, "xfEnabled");
            }
        );

        // console.debug("_createCompactRepeatHeader: tbody:",tbody, " \n\n");
        // console.dirxml(tbody);
        // console.debug("_createCompactRepeatHeader: END show node");


    },

    _createRepeatItem:function(/*Dijit*/node, /* int */position) {
        // console.debug("RepeatItem._createRepeatItem node:",node, " at position " + position);
        var repeatItemCount = this._getSize();
        var appearance;
        if (dojo.hasClass(this.domNode, "xfFullRepeat")) {
            appearance = "full";
        } else {
            appearance = "compact";
        }
        var repeatItemDijit = new betterform.ui.container.RepeatItem({repeatId:this.id,appearance:appearance}, node);
        repeatItemDijit.hideRepeatItem();


        var targetNode = null;
        if (position == 1 && repeatItemCount > 0) {
            if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
                targetNode = dojo.query("> tbody > .xfRepeatItem", this.domNode)[0];
            } else {
                targetNode = dojo.query("> .xfRepeatItem", this.domNode)[0];
            }
            dojo.place(repeatItemDijit.domNode, targetNode, "before");

        } else if (position == 1 && repeatItemCount == 0) {

            if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
                // console.debug("RepeatItem._createRepeatItem for CompactRepeat domNode: ", this.domNode);
                var tbodyNode = dojo.query("tbody", this.domNode)[0];
                if (tbodyNode == undefined) {
                    tbodyNode = dojo.doc.createElement("tbody");
                    dojo.place(tbodyNode, this.domNode);
                }
                // console.debug("RepeatItem._createRepeatItem tbodyNode: ",tbodyNode);
                dojo.place(repeatItemDijit.domNode, tbodyNode);
            } else {
                // console.debug("RepeatItem._createRepeatItem for FullRepeat RepeatNode: ", this.domNode, " RepeatItem2Insert: ",repeatItemDijit);
                dojo.place(repeatItemDijit.domNode, this.domNode);
            }


        } else {
            //  position - 2 causes
            //  1. XForms Position 1 = JavaScript Array Position 1 and
            //  2. Default Insert happens after the targetNode

            if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
                targetNode = dojo.query("> tbody > .xfRepeatItem", this.domNode)[position - 2];
            } else {
                targetNode = dojo.query("> .xfRepeatItem", this.domNode)[position - 2];
            }
            // console.debug("RepeatItem._createRepeatItem targetNode: ", targetNode , " repeatItem: ", repeatItemDijit);
            dojo.place(repeatItemDijit.domNode, targetNode, "after");
        }
        // console.debug("RepeatItem._createRepeatItem Insert at Position "+ position + " of  :"+(repeatItemCount+1));
        return repeatItemDijit;
    },

    _getSize:function() {
        var size;
        if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
            size = dojo.query("> tbody > .xfRepeatItem", this.domNode).length;
        } else {
            size = dojo.query("> .xfRepeatItem", this.domNode).length;
        }
        return size;
    },
    _getRepeatItems:function() {
        var repeatItems;
        if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
            repeatItems = dojo.query("> tbody > .xfRepeatItem", this.domNode);
        } else {
            repeatItems = dojo.query("> .xfRepeatItem", this.domNode);
        }
        return repeatItems;
    },

    handleDelete:function(/*Map*/ contextInfo) {
        var position = eval(contextInfo.position);
        var itemToRemove;
        if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
            itemToRemove = dojo.query("> tbody > .xfRepeatItem", this.domNode)[position - 1];
            dojo.query("> tbody", this.domNode)[0].removeChild(itemToRemove);
        } else {
            itemToRemove = dojo.query("> .xfRepeatItem", this.domNode)[position - 1];
            this.domNode.removeChild(itemToRemove);
        }
        // console.debug("handleDelete: repeatItemSize:" + repeatItemsSize + " position:" + position, " itemToRemove",itemToRemove);
    },


    handleStateChanged:function(contextInfo) {
        // console.debug("Repeat.handleStateChanged contextInfo:",contextInfo, " enabled: ",contextInfo["enabled"]);
        if(contextInfo["enabled"] != ""){
            var relevant = contextInfo["enabled"] == 'true';
            // console.debug("Repeat.handleStateChanged relevant:",relevant);
            if (relevant) {
                betterform.ui.util.replaceClass(this.domNode, "xfDisabled", "xfEnabled");
            }
            else {
                betterform.ui.util.replaceClass(this.domNode, "xfEnabled", "xfDisabled");
            }
        }
    },

    _removeRepeatIndexClasses:function() {
        if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
            dojo.query("> tbody > .xfRepeatIndexPre", this.domNode).forEach(
				function(repeatIndexItem) {
					dojo.removeClass(repeatIndexItem, "xfRepeatIndexPre");
				}
			);
            dojo.query("> tbody > .xfRepeatIndex", this.domNode).forEach(
				function(repeatIndexItem) {
					dojo.removeClass(repeatIndexItem, "xfRepeatIndex");
				}
			);
		} else {
            dojo.query("> .xfRepeatIndexPre", this.domNode).forEach(
				function(repeatIndexItem) {
					dojo.removeClass(repeatIndexItem, "xfRepeatIndexPre");
				}
			);
			dojo.query("> .xfRepeatIndex", this.domNode).forEach(
				function(repeatIndexItem) {
					dojo.removeClass(repeatIndexItem, "xfRepeatIndex");
				}
			);
        }

    }
});

