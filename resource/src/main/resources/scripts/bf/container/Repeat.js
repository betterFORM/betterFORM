define(["dojo/_base/declare","bf/XFBinding","dojo/query","dojo/dom", "dojo/dom-style","dojo/dom-attr","dojo/dom-class","dojo/dom-construct","dojo/_base/window","dojo/behavior","dojo/_base/connect","dojo/on","dojo/_base/lang","bf/util"],
    function(declare, XFBinding,query, dom, domStyle, domAttr, domClass, domConstruct, win, behavior,connect,on,lang){
        return declare(XFBinding, {

            repeatId:null,

            constructor:function(properties, node){
                // console.debug("Repeat.postCreate created new instace node:", node);
                this.inherited(arguments);

                this.repeatId = domAttr.get(this.srcNodeRef,"repeatId");
                // console.debug("\n\nthis.repeatId:",this.repeatId);
                if(dom.byId(this.repeatId)==undefined){
                    domAttr.set(this.srcNodeRef,"id", this.repeatId);
                }
                // console.debug("Repeat.postCreate this.repeatId:", this.repeatId);

                this.appearance = undefined;
                if (domClass.contains(this.srcNodeRef, "aCompact")) {
                    this.appearance = "compact";
                } else {
                    this.appearance = "full";
                }
                // console.debug("Repeat.postCreate appearance:",this.appearance);

                connect.subscribe("betterform-insert-repeatitem-"+ this.repeatId, this, "handleInsert");
                connect.subscribe("betterform-item-deleted-"+ this.repeatId,      this, "handleDelete");
                connect.subscribe("betterform-index-changed-"+ this.repeatId,     this, "handleSetRepeatIndex");
                var self = this;
                query(".xfRepeatItem",this.srcNodeRef).forEach(function(repeatItem){
                    on(repeatItem, "click", lang.hitch(self, self._onClickRepeatItem));
                    connect.subscribe("bf-state-change-"+ domAttr.get(repeatItem,"repeatItemId"), self.handleStateChanged);
                });

            },
            handleStateChanged:function(contextInfo) {
                // console.debug("Repeat.handleStateChanged: contextInfo:",contextInfo);
                var repeatItem = query(".xfRepeatItem[repeatItemId='" + contextInfo.targetId + "']")[0];
                if(contextInfo["readonly"] == "true"){
                    domClass.replace(repeatItem, "xfReadOnly","xfReadWrite");
                }else {
                    domClass.replace(repeatItem, "xfReadWrite","xfReadOnly");
                }
            },

            _onClickRepeatItem:function(evt){
                if(!evt || (!evt.currentTarget && !evt.srcElement)){
                    return;
                }
                var repeatItem = (evt.currentTarget) ? evt.currentTarget : evt.srcElement;
                // console.debug("onClickRepeatItem: repeatItem:",repeatItem);
                if(domClass.contains(repeatItem,"xfReadOnly")){
                    console.warn("repeatItem ", repeatItem, " is readonly");
                    return;
                }
                if(domClass.contains(repeatItem, "xfRepeatIndex") || domClass.contains(repeatItem, "xfRepeatIndexPre")){
                    return;
                }
                var repeatNode = dom.byId(this.repeatId);
                var repeatItems = query(".xfRepeatItem", repeatNode);
                // console.debug("repeatItems:",repeatItems);
                var position = repeatItems.indexOf(repeatItem) + 1;
                // console.debug("position is:",position);

                query(".xfRepeatIndex", repeatNode).forEach(function(node){
                    domClass.remove(node, "xfRepeatIndex")
                });

                query(".xfRepeatIndexPre", repeatNode).forEach(function(node){
                    domClass.remove(node, "xfRepeatIndexPre")
                });

                // add xfRepeatIndexPre CSS Class to selected RepeatItem and send setRepeatIndex to the server
                domClass.add(repeatItem, "xfRepeatIndexPre");
                fluxProcessor.setRepeatIndex(this.repeatId, position);
            },

            handleSetRepeatIndex:function(/*Map*/ contextInfo) {
                // console.debug("Repeat.handleSetRepeatIndex: contextInfo'",contextInfo, " for Repeat id: ", this.id);
                var intIndex = parseInt(contextInfo.index,"10");

                var repeatNode = dom.byId(this.repeatId);
                this._removeRepeatIndexClasses();

                var repeatIndexNodeList;
                if (this.appearance == "compact") {
                    repeatIndexNodeList = query("> tbody > .xfRepeatItem", repeatNode);
                } else {
                    repeatIndexNodeList = query("> .xfRepeatItem", repeatNode);
                }

                // console.debug("handleSetRepeatIndex for repeatIndexNodeList",repeatIndexNodeList);
                var repeatIndexNode = repeatIndexNodeList[intIndex - 1];
                if (repeatIndexNode != undefined) {
                    domClass.add(repeatIndexNode, "xfRepeatIndex");
                }

            },

            handleInsert:function(/*Map*/ contextInfo) {
                // console.debug("handleInsert contextInfo: ",contextInfo);
                // search for former repeat-index and remove it
                this._removeRepeatIndexClasses();
                // console.debug("handleInsert replace prototype ids");

                // clone prototype and manipulate classes of repeat item
                var prototype = dom.byId(contextInfo.originalId + "-prototype");
                var insertedNode = prototype.cloneNode(true);
                this._replaceRepeatItemClasses(insertedNode);

                // replace prototype ids with generated ones
                var generatedIds = "";
                if (contextInfo.prototypeId != undefined) {
                    generatedIds = contextInfo.generatedIds;
                    domAttr.set(insertedNode, "id", generatedIds[contextInfo.prototypeId]);

                } else if (contextInfo.repeatedSelects) {
                    generatedIds = contextInfo.repeatedSelects[0].generatedIds;
                    // console.debug("Generated Ids: ", generatedIds);
                    var clonedNodeId = contextInfo.repeatedSelects[0].generatedIds[0];
                    domAttr.set(insertedNode, "id", clonedNodeId);
                }
                // replace prototype ids with generated ones
                this._replacePrototypeIds(insertedNode, generatedIds);
                // create dijit and place it within repeat
                var position = parseInt(contextInfo.position,"10");

                // console.debug("InsertedNode: " + insertedNode.id );
                var repeatItemExists = query("*[repeatItemId='" + insertedNode.id + "']");
                var repeatItemNode = undefined;
                if (repeatItemExists[0] != null ) {
                    // console.warn("Skipping already present repeatItem: ", repeatItemExists);
                    repeatItemNode = dom.byId(domAttr.get(repeatItemExists[0], "id"));
                }else {
                    repeatItemNode = this._createRepeatItem(insertedNode, position);
                }
                // console.debug("repeatItemNode",repeatItemNode);
                // console.debug("handleInsert fix inserted controls repeatItemNode:",repeatItemNode);
                behavior.apply();
                if(domStyle.get(repeatItemNode,"display") == "none"){
                    repeatItemNode.removeAttribute("style");
                }
                domClass.add(repeatItemNode,"xfRepeatIndex");
                on(repeatItemNode, "click", lang.hitch(this,this._onClickRepeatItem));
                // console.debug("Inserted new Repeat Item", repeatItemNode);
            },

            handleDelete:function(/*Map*/ contextInfo) {
                // console.debug("Repeat.handleDelete contextInfo:",contextInfo);
                var position = parseInt(contextInfo.position,"10");
                var itemToRemove;
                var repeatNode = dom.byId(this.repeatId);
                if (this.appearance == "compact") {
                    itemToRemove = dojo.query("> tbody > .xfRepeatItem", repeatNode)[position - 1];
                    dojo.query("> tbody", repeatNode)[0].removeChild(itemToRemove);
                } else {
                    itemToRemove = dojo.query("> .xfRepeatItem", repeatNode)[position - 1];
                    repeatNode.removeChild(itemToRemove);
                }
            },

            _removeRepeatIndexClasses:function() {
                var repeatNode = dom.byId(this.repeatId);
                // console.debug("Repeat._removeRepeatIndexClasses: repeatNode:",repeatNode, " this:",this);
                if (this.appearance == "compact") {
                    query("> tbody > .xfRepeatIndexPre", repeatNode).forEach(
                        function(repeatIndexItem) {
                            domClass.remove(repeatIndexItem, "xfRepeatIndexPre");
                        }
                    );
                    query("> tbody > .xfRepeatIndex", repeatNode).forEach(
                        function(repeatIndexItem) {
                            domClass.remove(repeatIndexItem, "xfRepeatIndex");
                        }
                    );
                } else {
                    query("> .xfRepeatIndexPre", repeatNode).forEach(
                        function(repeatIndexItem) {
                            domClass.remove(repeatIndexItem, "xfRepeatIndexPre");
                        }
                    );
                    query("> .xfRepeatIndex", repeatNode).forEach(
                        function(repeatIndexItem) {
                            domClass.remove(repeatIndexItem, "xfRepeatIndex");
                        }
                    );
                }
            },
            _replaceRepeatItemClasses:function(/* Node */ node) {
                // console.debug("Repeat._replaceRepeatItemClasses node:",node );
                // console.dirxml(node);
                domClass.remove(node, "xfRepeatPrototype");
                domClass.remove(node, "xfDisabled");
                domClass.add(node, "xfRepeatItem");
                domClass.add(node, "xfEnabled");
            },

            _replacePrototypeIds:function(node, generatedIds) {
                var compactRepeat = false;
                if(domClass.contains(dom.byId(this.repeatId),"aCompact")) {
                    compactRepeat = true;
                }

                query("*", node).forEach(
                    function(xfNode) {
                        var idAtt = domAttr.get(xfNode, "id");
                        var repeatId = domAttr.get(xfNode, "repeatid");
                        if (repeatId != undefined && generatedIds[repeatId] != undefined) {
                            domAttr.set(xfNode, "repeatid", generatedIds[idAtt]);
                        }

                        if (idAtt != undefined && generatedIds[idAtt] != undefined) {
                            domAttr.set(xfNode, "id", generatedIds[idAtt]);
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
                            domAttr.set(xfNode, "id", generatedId);
                        }
                    }
                );

            },
            _createRepeatItem:function(/*Dijit*/node, /* int */position) {
                // console.debug("RepeatItem._createRepeatItem node:",node, " at position " + position);
                var repeatItemCount = this._getSize();
                // repeatItemDijit.hideRepeatItem();
                domStyle.set(node, "display","none");

                var targetNode = null;
                var repeatNode = dom.byId(this.repeatId)
                if (position == 1 && repeatItemCount > 0) {
                    if (this.appearance == "compact") {
                        targetNode = query("> tbody > .xfRepeatItem", repeatNode)[0];
                    } else {
                        targetNode = query("> .xfRepeatItem", repeatNode)[0];
                    }
                    domConstruct.place(node, targetNode, "before");

                } else if (position == 1 && repeatItemCount == 0) {

                    if (this.appearance == "compact") {
                        // console.debug("RepeatItem._createRepeatItem for CompactRepeat domNode: ", repeatNode);
                        var tbodyNode = query("tbody", repeatNode)[0];
                        if (tbodyNode == undefined) {
                            tbodyNode = win.doc.createElement("tbody");
                            domConstruct.place(tbodyNode, repeatNode);
                        }
                        domConstruct.place(node, tbodyNode);
                    } else {
                        domConstruct.place(node.domNode, repeatNode);
                    }


                } else {
                    //  position - 2 causes
                    //  1. XForms Position 1 = JavaScript Array Position 1 and
                    //  2. Default Insert happens after the targetNode

                    if (this.appearance == "compact") {
                        targetNode = query("> tbody > .xfRepeatItem", repeatNode)[position - 2];
                    } else {
                        targetNode = query("> .xfRepeatItem", repeatNode)[position - 2];
                    }
                    // console.debug("RepeatItem._createRepeatItem targetNode: ", targetNode , " repeatItem: ", repeatItemDijit);
                    domConstruct.place(node, targetNode, "after");
                }
                // console.debug("RepeatItem._createRepeatItem Insert at Position "+ position + " of  :"+(repeatItemCount+1));
                return node;
            },

            _getSize:function() {
                var size;
                var repeatNode = dom.byId(this.repeatId)
                if (this.appearance == "compact") {
                    size = query("> tbody > .xfRepeatItem", repeatNode).length;
                } else {
                    size = query("> .xfRepeatItem", repeatNode).length;
                }
                return size;
            }

    });
});


