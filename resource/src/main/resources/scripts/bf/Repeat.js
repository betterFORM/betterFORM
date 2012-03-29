dojo.provide("bf.Repeat");

dojo.require("bf.Container");

dojo.declare(
    "bf.Repeat",bf.Container,
    {
        handleSetRepeatIndex:function(/*Map*/ contextInfo) {
            // console.debug("Repeat.handleSetRepeatIndex: contextInfo'",contextInfo, " for Repeat id: ", this.id);
            if(contextInfo != undefined && contextInfo.index != undefined ){
                this._handleSetRepeatIndex(contextInfo.index);
            }
        },

        handleInsert:function(/*Map*/ contextInfo) {
            // console.debug("handleInsert contextInfo: ",contextInfo);
            // search for former repeat-index and remove it
            this._removeRepeatIndexClasses();

            // clone prototype and manipulate classes of repeat item
            var prototype = dom.byId(contextInfo.originalId + "-prototype");
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
            var repeatItemExists = query("*[repeatItemId='" + insertedNode.id + "']");
            var repeatItemNode = undefined;
            if (repeatItemExists[0] != null ) {
                // console.warn("Skipping already present repeatItem: ", repeatItemExists);
                // console.debug("repeatItemExists.id: " , dojo.attr(repeatItemExists[0], "id"));
                repeatItemNode = dom.byId(dojo.attr(repeatItemExists[0], "id"));
            }else {
                repeatItemNode = this._createRepeatItem(insertedNode, position);
            }
            // console.debug("repeatItemNode",repeatItemNode);

            query(".repeated", repeatItemNode).forEach(
                function(item) {
                    // console.debug("Create UIControl for unbound item", item, " id:",item.id);
                    if(!dojo.hasClass(item,"xfControl")){
                        dojo.addClass(item,"xfControl");
                    }
                }
            );
            dojo.behavior.apply();

            dojo.style(repeatItemNode,"display", "block");
            // console.debug("Inserted new Repeat Item", repeatItemNode);
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
                repeatIndexNode = query("> tbody > .xfRepeatItem", this.domNode)[intIndex - 1];
            } else {
                repeatIndexNode = query("> .xfRepeatItem", this.domNode)[intIndex - 1];
            }
            // console.debug("handleSetRepeatIndex for repeatIndexNode",repeatIndexNode);
            if (repeatIndexNode != undefined) {
                dojo.addClass(repeatIndexNode, "xfRepeatIndex");
                //this.setFocusOnChild(repeatIndexNode);

            }
        },

        _removeRepeatIndexClasses:function() {
            if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
                query("> tbody > .xfRepeatIndexPre", this.domNode).forEach(
                    function(repeatIndexItem) {
                        dojo.removeClass(repeatIndexItem, "xfRepeatIndexPre");
                    }
                );
                query("> tbody > .xfRepeatIndex", this.domNode).forEach(
                    function(repeatIndexItem) {
                        dojo.removeClass(repeatIndexItem, "xfRepeatIndex");
                    }
                );
            } else {
                query("> .xfRepeatIndexPre", this.domNode).forEach(
                    function(repeatIndexItem) {
                        dojo.removeClass(repeatIndexItem, "xfRepeatIndexPre");
                    }
                );
                query("> .xfRepeatIndex", this.domNode).forEach(
                    function(repeatIndexItem) {
                        dojo.removeClass(repeatIndexItem, "xfRepeatIndex");
                    }
                );
            }
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

            query("*", node).forEach(
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
        _createRepeatItem:function(/*Dijit*/node, /* int */position) {
            // console.debug("RepeatItem._createRepeatItem node:",node, " at position " + position);
            var repeatItemCount = this._getSize();
            var appearance;
            if (dojo.hasClass(this.domNode, "xfFullRepeat")) {
                appearance = "full";
            } else {
                appearance = "compact";
            }
            // var repeatItemDijit = new bf.ui.container.RepeatItem({repeatId:this.id,appearance:appearance}, node);
            // repeatItemDijit.hideRepeatItem();
            dojo.style(node, "display","none");

            var targetNode = null;
            if (position == 1 && repeatItemCount > 0) {
                if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
                    targetNode = query("> tbody > .xfRepeatItem", this.domNode)[0];
                } else {
                    targetNode = query("> .xfRepeatItem", this.domNode)[0];
                }
                dojo.place(node, targetNode, "before");

            } else if (position == 1 && repeatItemCount == 0) {

                if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
                    // console.debug("RepeatItem._createRepeatItem for CompactRepeat domNode: ", this.domNode);
                    var tbodyNode = query("tbody", this.domNode)[0];
                    if (tbodyNode == undefined) {
                        tbodyNode = dojo.doc.createElement("tbody");
                        dojo.place(tbodyNode, this.domNode);
                    }
                    dojo.place(node, tbodyNode);
                } else {
                    dojo.place(node.domNode, this.domNode);
                }


            } else {
                //  position - 2 causes
                //  1. XForms Position 1 = JavaScript Array Position 1 and
                //  2. Default Insert happens after the targetNode

                if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
                    targetNode = query("> tbody > .xfRepeatItem", this.domNode)[position - 2];
                } else {
                    targetNode = query("> .xfRepeatItem", this.domNode)[position - 2];
                }
                // console.debug("RepeatItem._createRepeatItem targetNode: ", targetNode , " repeatItem: ", repeatItemDijit);
                dojo.place(node, targetNode, "after");
            }
            // console.debug("RepeatItem._createRepeatItem Insert at Position "+ position + " of  :"+(repeatItemCount+1));
            return node;
        },

        _getSize:function() {
            var size;
            if (dojo.hasClass(this.domNode, "xfCompactRepeat")) {
                size = query("> tbody > .xfRepeatItem", this.domNode).length;
            } else {
                size = query("> .xfRepeatItem", this.domNode).length;
            }
            return size;
        }

    }
);


