dojo.provide("betterform.xf.Select1");

dojo.declare(
    "betterform.xf.Select1",dijit._Widget,
    {
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
    }
);
dojo.declare(
    "betterform.xf.Select1Minimal",betterform.xf.Select1,
    {
        handleInsertItem:function(contextInfo) {
            console.debug("betterform.xf.Select1Minimal.handleInsertItem: ", contextInfo);
            var position = contextInfo.position;
            var itemsetId = contextInfo.targetId;
            var generatedItemId =  contextInfo.generatedIds[contextInfo.prototypeId];

            var referenzedNode = dojo.query('option[data-bf-itemset=\"'+ itemsetId + '\"]',this.id)[0];
            if(referenzedNode){
                var item = undefined;
                if(position == 1){
                    item = dojo.create("option", {id:generatedItemId}, referenzedNode, "before");
                    dojo.attr(item, "data-bf-itemset", itemsetId);
                    dojo.removeAttr(referenzedNode, "data-bf-itemset");
                }
                else {
                    var option = undefined;
                    if(position == 2){
                        option = referenzedNode;
                    }else {
                        option = this.getNthSiblingOption(position-2, referenzedNode);
                    }
                    item = dojo.create("option", {id:generatedItemId}, option, "after");
                }
                dojo.addClass(item, "xfSelectorItem");
            }else {
                console.warn("itemset '",itemsetId,"' does not exist for Select1 [id:'",this.id ,"']");
            }
        },

        handleDeleteItem:function(contextInfo) {
            console.debug("Select1Minimal.handleDeleteItem:  contextInfo:",contextInfo);
            var position = contextInfo.position;
            var itemsetId = contextInfo.targetId;

            var referenzedNode = dojo.query('option[data-bf-itemset=\"'+ itemsetId + '\"]',this.id)[0];
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
            console.debug("Select1Minimal.handleStateChanged contextInfo:",contextInfo);
            var targetName = contextInfo.targetName;
            var option = dojo.byId(contextInfo.parentId);
            if(targetName == "label" && option){
                option.innerHTML = contextInfo.value;
            }else if(targetName == "value" && option){
                dojo.attr(option,"value",contextInfo.value);
            }else {
                console.warn("OptGroup.handleStateChanged: no action taken for contextInfo: ",contextInfo);
            }
        }
    }
);
dojo.declare(
    "betterform.xf.Select1Compact",betterform.xf.Select1Minimal,
    {

    }
);
dojo.declare(
    "betterform.xf.Select1Full",betterform.xf.Select1,
    {
        controlId:undefined,
        _onBlur:function() {
            console.debug("betterform.xf.Select1Full._onBlur arguments:",arguments);
            var checkedRadioItemValue = undefined;
            dojo.query(".xfRadioValue", this.domNode).forEach(function(item) {
                console.debug("analysing radioitem:",item);
                if(item.checked){
                    checkedRadioItemValue = item.value;
                    console.debug("selected radioitem:",checkedRadioItemValue);
                }
            });
            if(checkedRadioItemValue != undefined) {
                var evt=new Object();
                evt.type = "blur";
                dijit.byId(this.controlId).sendValue(checkedRadioItemValue,evt);
            }


        },

        handleInsertItem:function(contextInfo) {
            console.debug("betterform.xf.Select1Full.handleInsertItem: ", contextInfo);
            var position = contextInfo.position;
            var itemsetId = contextInfo.targetId;
            var generatedItemId =  contextInfo.generatedIds[contextInfo.prototypeId];

            var selectedItemset = dojo.byId(itemsetId);
            if(selectedItemset){
                // memorize checked radio item
/*
                var checkedRadioItem = dojo.query(".xfRadioValue[checked]", selectedItemset);
                console.debug("checkedRadioItem: ",checkedRadioItem);
                var checkedRadioItemValue = checkedRadioItem[0].value;
                console.debug("checkedRadioItemValue: ",checkedRadioItemValue);
*/

                var xfSelectorItem = dojo.create("span", {id:generatedItemId}, selectedItemset, "last");
                dojo.addClass(xfSelectorItem, "xfSelectorItem");
                var xfSelectorItemValue = dojo.create("input", {id:generatedItemId+"-value"}, xfSelectorItem, "first");
                dojo.addClass(xfSelectorItemValue, "xfRadioValue");
                dojo.attr(xfSelectorItemValue, "type", "radio");
                dojo.attr(xfSelectorItemValue, "name", "d_" + this.controlId);
                dojo.attr(xfSelectorItemValue, "parentid", this.controlId);
                dojo.attr(xfSelectorItemValue, "tabindex", "0");
                var xfControlId = this.controlId;
                xfSelectorItemValue.onclick = function(evt) {
                    dijit.byId(xfControlId).sendValue(xfSelectorItemValue.value,evt);
                };

                var xfSelectorItemLabel = dojo.create("label", {id:generatedItemId+"-label"}, xfSelectorItemValue, "after");
                dojo.attr(xfSelectorItemLabel, "for", generatedItemId+"-value");
                dojo.addClass(xfSelectorItemLabel, "xfRadioLabel");

/*
                var radioItemToCheck= dojo.query(".xfRadioValue[value=\""+ checkedRadioItemValue +"\"]", selectedItemset)[0];
                console.debug("radioItemToCheck: ",radioItemToCheck);
                dojo.attr(radioItemToCheck,"checked", true);
*/
            }else {
                console.warn("itemset '",itemsetId,"' does not exist for Select1 [id:'",this.id ,"']");
            }
        },

        handleDeleteItem:function(contextInfo){
            console.debug("handleDeleteItem for id:",this.id, " contextInfo:",contextInfo);
            var itemsetNode = dojo.byId(contextInfo.originalId);
            var selectorItems  = dojo.query(".xfSelectorItem", itemsetNode);

            itemsetNode.removeChild(selectorItems[(contextInfo.position -1)]);


        },

        handleStateChanged:function(contextInfo) {
            var targetName = contextInfo.targetName;
            if(targetName == "label"){
                dojo.byId(contextInfo.parentId+"-label").innerHTML = contextInfo.value;
            }else if(targetName == "value"){
                dojo.attr(dojo.byId(contextInfo.parentId+"-value"),"value",contextInfo.value);
            }else {
                console.warn("OptGroup.handleStateChanged: no action taken for contextInfo: ",contextInfo);
            }
        }
    }
);

