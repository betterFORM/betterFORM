define(["dojo/_base/declare", "dijit/_Widget","dojo/dom-attr","dojo/dom-class","dojo/dom-construct","dijit/registry","dojo/query","dojo/_base/connect","dojo/dom"],
    function(declare, _Widget,domAttr,domClass,domConstruct,registry,query,connect,dom){
        return declare(_Widget, {
            controlId:undefined,

            postCreate:function() {
                connect.subscribe("xforms-item-changed-" + this.id , this, "handleStateChanged");
            },

            _onBlur:function() {
                console.debug("bf.Select1Full._onBlur arguments:",arguments);
                var checkedRadioItemValue = undefined;
                query(".xfRadioValue", this.domNode).forEach(function(item) {
                    console.debug("analysing radioitem:",item);
                    if(item.checked){
                        checkedRadioItemValue = item.value;
                        console.debug("selected radioitem:",checkedRadioItemValue);
                    }
                });
                if(checkedRadioItemValue != undefined) {
                    var evt=new Object();
                    evt.type = "blur";
                    registry.byId(this.controlId).sendValue(checkedRadioItemValue,evt);
                }


            },

            handleInsertItem:function(contextInfo) {
                console.debug("bf.Select1Full.handleInsertItem: ", contextInfo);
                var position = contextInfo.position;
                var itemsetId = contextInfo.targetId;
                var generatedItemId =  contextInfo.generatedIds[contextInfo.prototypeId];

                var selectedItemset = dom.byId(itemsetId);
                if(selectedItemset){
                    // memorize checked radio item
                    /*
                     var checkedRadioItem = query(".xfRadioValue[checked]", selectedItemset);
                     console.debug("checkedRadioItem: ",checkedRadioItem);
                     var checkedRadioItemValue = checkedRadioItem[0].value;
                     console.debug("checkedRadioItemValue: ",checkedRadioItemValue);
                     */

                    var xfSelectorItem = domConstruct.create("span", {id:generatedItemId}, selectedItemset, "last");
                    domClass.add(xfSelectorItem, "xfSelectorItem");
                    var xfSelectorItemValue = domConstruct.create("input", {id:generatedItemId+"-value"}, xfSelectorItem, "first");
                    domClass.add(xfSelectorItemValue, "xfRadioValue");
                    domAttr.set(xfSelectorItemValue, "type", "radio");
                    domAttr.set(xfSelectorItemValue, "name", "d_" + this.controlId);
                    domAttr.set(xfSelectorItemValue, "parentid", this.controlId);
                    domAttr.set(xfSelectorItemValue, "tabindex", "0");
                    var xfControlId = this.controlId;
                    xfSelectorItemValue.onclick = function(evt) {
                        registry.byId(xfControlId).sendValue(xfSelectorItemValue.value,evt);
                    };

                    var xfSelectorItemLabel = domConstruct.create("label", {id:generatedItemId+"-label"}, xfSelectorItemValue, "after");
                    domAttr.set(xfSelectorItemLabel, "for", generatedItemId+"-value");
                    domClass.add(xfSelectorItemLabel, "xfRadioLabel");

                    /*
                     var radioItemToCheck= query(".xfRadioValue[value=\""+ checkedRadioItemValue +"\"]", selectedItemset)[0];
                     console.debug("radioItemToCheck: ",radioItemToCheck);
                     domAttr.set(radioItemToCheck,"checked", true);
                     */
                }else {
                    console.warn("itemset '",itemsetId,"' does not exist for Select1 [id:'",this.id ,"']");
                }
            },

            handleDeleteItem:function(contextInfo){
                console.debug("handleDeleteItem for id:",this.id, " contextInfo:",contextInfo);
                var itemsetNode = dom.byId(contextInfo.originalId);
                var selectorItems  = query(".xfSelectorItem", itemsetNode);

                itemsetNode.removeChild(selectorItems[(contextInfo.position -1)]);


            },

            handleStateChanged:function(contextInfo) {
                // console.debug("Select1Radio.handleStateChanged: contextInfo:",contextInfo);
                var targetName = contextInfo.targetName;
                if(targetName == "label"){
                    dom.byId(contextInfo.parentId + "-label").innerHTML = contextInfo.value;
                }else if(targetName == "value"){
                    domAttr.set(dom.byId(contextInfo.parentId+"-value"),"value",contextInfo.value);
                }else {
                    console.warn("Select1Radio.handleStateChanged: no action taken for contextInfo: ",contextInfo);
                }
            }
        });
    }
);