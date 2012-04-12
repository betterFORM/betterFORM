define(["dojo/_base/declare", "dijit/_Widget","dojo/dom-attr","dojo/dom-class","dojo/dom-construct","dojo/_base/connect","dojo/query","dojo/dom"],
    function(declare, _Widget,domAttr,domClass,domConstruct,connect,query,dom){
        return declare(_Widget, {

            postCreate:function() {
                connect.subscribe("xforms-item-changed-" + this.id , this, "handleStateChanged");
            },

            handleInsertItem:function(contextInfo) {
                console.debug("bf.Select1Minimal.handleInsertItem: ", contextInfo);
                var position = contextInfo.position;
                var itemsetId = contextInfo.targetId;
                var generatedItemId =  contextInfo.generatedIds[contextInfo.prototypeId];

                var referenzedNode = query('option[data-bf-itemset=\"'+ itemsetId + '\"]',this.id)[0];
                if(referenzedNode){
                    var item = undefined;
                    if(position == 1){
                        item = domConstruct.create("option", {id:generatedItemId}, referenzedNode, "before");
                        domAttr.set(item, "data-bf-itemset", itemsetId);
                        domAttr.remove(referenzedNode, "data-bf-itemset");
                    }
                    else {
                        var option = undefined;
                        if(position == 2){
                            option = referenzedNode;
                        }else {
                            option = this.getNthSiblingOption(position-2, referenzedNode);
                        }
                        item = domConstruct.create("option", {id:generatedItemId}, option, "after");
                    }
                    domClass.add(item, "xfSelectorItem");
                }else {
                    console.warn("itemset '",itemsetId,"' does not exist for Select1 [id:'",this.id ,"']");
                }
            },

            handleDeleteItem:function(contextInfo) {
                console.debug("Select1Minimal.handleDeleteItem:  contextInfo:",contextInfo);
                var position = contextInfo.position;
                var itemsetId = contextInfo.targetId;

                var referenzedNode = query('option[data-bf-itemset=\"'+ itemsetId + '\"]',this.id)[0];
                var option2remove = undefined;
                if(referenzedNode){
                    if(position == 1){
                        option2remove = referenzedNode;
                    } else {
                        option2remove = this.getNthSiblingOption(position-1, referenzedNode);
                    }
                    this.domNode.removeChild(option2remove);
                }else {
                    console.warn("itemset '",itemsetId,"' does not exist for Select1 [id:'",this.id ,"']");
                }
            },

            handleStateChanged:function(contextInfo) {
                // console.debug("Select1Minimal.handleStateChanged contextInfo:",contextInfo);
                var targetName = contextInfo.targetName;
                var option = dom.byId(contextInfo.parentId);
                if(targetName == "label" && option){
                    option.innerHTML = contextInfo.value;
                }else if(targetName == "value" && option){
                    domAttr.set(option,"value",contextInfo.value);
                }else {
                    console.warn("OptGroup.handleStateChanged: no action taken for contextInfo: ",contextInfo);
                }
            },

            getNthSiblingOption:function(position, referenzedNode){
                var siblingNode = referenzedNode;
                var counter = position;
                while(counter != 0) {
                    counter -= 1;
                    siblingNode = siblingNode.nextSibling;
                    console.debug("getNthSiblingOption: siblingNode:",   siblingNode, " position: ",counter);
                }
                return siblingNode;
            }

        });

    }
);