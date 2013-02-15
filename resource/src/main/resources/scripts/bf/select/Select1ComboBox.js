define(["dojo/_base/declare", "dijit/_Widget","dojo/dom-attr","dojo/dom-class","dojo/dom-construct","dojo/_base/connect","dojo/query","dojo/dom"],
    function(declare, _Widget,domAttr,domClass,domConstruct,connect,query,dom){
        return declare(_Widget, {

            currentValue:null,
            lastItem:null,
            itemSetLength:0,

            postCreate:function() {
                // console.debug("Select1ComboBox postCreate id:",this.id);
                var bfHandleStateChanged = connect.subscribe("xforms-item-changed-" + this.id , this, "handleStateChanged");
                fluxProcessor.addSubscriber(this.id, bfHandleStateChanged);
                var bfHandleInsertItem = connect.subscribe("betterform-insert-item-" + this.id , this, "handleInsertItem");
                fluxProcessor.addSubscriber(this.id, bfHandleInsertItem);
                var bfHandleDeleteItem = connect.subscribe("betterform-delete-item-" + this.id , this, "handleDeleteItem");
                fluxProcessor.addSubscriber(this.id, bfHandleDeleteItem);

                this.currentValue = this.domNode.value;
            },

            handleInsertItem:function(contextInfo) {
                console.debug("Select1ComboBox.handleInsertItem: ", contextInfo, " currentValue: ", this.domNode.value);
                var position = contextInfo.position;
                var itemsetId = contextInfo.targetId;
                var generatedItemId =  contextInfo.generatedIds[contextInfo.prototypeId];
                // console.debug("generatedItemId: ",generatedItemId, " itemsetId: ",itemsetId);;
                var referenzedNode = query('option[data-bf-itemset=\"'+ itemsetId + '\"]',this.id)[0];
                //TODO: Quick Fix this needs to be fixed properly!!!!
                if (referenzedNode == undefined) {
                    referenzedNode = query('option[data-bf-itemset=\"'+ contextInfo.originalId + '\"]',this.id)[0];
                }

                if (referenzedNode == undefined) {
                    // console.info("referenced node is sill undefined");
                    var emptyNode = query('option',this.domNode)[0];
                    // console.debug("emptyNode",emptyNode, " id:generatedItemId ",generatedItemId);
                    var emptyOption = domConstruct.create("option", {id:generatedItemId}, emptyNode, "after");
                    domAttr.set(emptyOption, "data-bf-itemset", itemsetId);
                    // console.debug("emptyOption: ",emptyOption);

                }

                if(referenzedNode){
                    var item = undefined;
                    if(position == 1){
                        item = domConstruct.create("option", {id:generatedItemId}, referenzedNode, "before");
                        domAttr.set(item, "data-bf-itemset", itemsetId);
                        // domAttr.remove(referenzedNode, "data-bf-itemset");
                        this.domNode.removeChild(referenzedNode);

                    }
                    else {
                        var option = undefined;
                        if(position == 2){
                            option = referenzedNode;
                        }else {
			                if (position>this.itemSetLength) { // Append to the end
	                    	    option = this.lastItem;
                            } else {
                                option = this.getNthSiblingOption(position-2, referenzedNode);
			                }
                        }
                        item = domConstruct.create("option", {id:generatedItemId}, option, "after");
                    }
                    if (position>this.itemSetLength) {
                        this.lastItem = item;
                    }
                    this.itemSetLength++;
                    domClass.add(item, "xfSelectorItem");
                }else {
                    console.warn("Select1ComboBox: itemset '",itemsetId,"' does not exist for Select1 [id:'",this.id ,"']");
                }
            },

            handleDeleteItem:function(contextInfo) {
                // console.debug("Select1Minimal.handleDeleteItem:  contextInfo:",contextInfo);
                var position = contextInfo.position;
                var itemsetId = contextInfo.targetId;

                var referenzedNode = query('option[data-bf-itemset=\"'+ itemsetId + '\"]',this.id)[0];
                // console.debug("handleDeleteItem: ",referenzedNode, " position:", position);
                var option2remove = undefined;
                if(referenzedNode){
                    if(position == 1){
                        option2remove = referenzedNode;
                    } else {
                        option2remove = this.getNthSiblingOption(position-1, referenzedNode);
                    }
                    this.domNode.removeChild(option2remove);
                }else {
                    console.warn("Select1ComboBox: itemset '",itemsetId,"' does not exist for Select1 [id:'",this.id ,"']");
                }
                this.itemSetLength--;
                // console.debug("handleDeleteItem: this.domNode",this.domNode);
            },

            handleStateChanged:function(contextInfo) {
                // console.debug("Select1Minimal.handleStateChanged contextInfo:",contextInfo, " this: " , this);
                var targetName = contextInfo.targetName;
                var option = dom.byId(contextInfo.parentId);
                var value = contextInfo.value;
                // label changed
                if(targetName == "label" && option){
                    option.innerHTML = value;
                }
                // value changed
                else if(targetName == "value" && option){
                    domAttr.set(option,"value",value);
                    // verify that value is the same as before the insert
                    // console.debug("Select1Minimal.handleStateChanged this.currentValue:",this.currentValue, " value: ",value);
                    if(this.currentValue == value){
                        domAttr.set(this.domNode,"value", value);
                    }
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
                    // console.debug("getNthSiblingOption: siblingNode:",   siblingNode, " position: ",counter);
                }
                return siblingNode;
            }
        });

    }
);