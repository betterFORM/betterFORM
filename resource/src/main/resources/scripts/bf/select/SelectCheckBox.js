define(["dojo/_base/declare", "dijit/_Widget","dojo/query","dojo/_base/connect","dojo/dom-attr","dojo/dom-class","dojo/dom-construct","dojo/dom"],
    function(declare, _Widget,query,connect,domAttr,domClass,domConstruct,dom){
        return declare(_Widget, {
            control:null,
            currentValue:null,

            postCreate:function() {
                // console.debug("Select1CheckBox postCreate id:",this.id);
                connect.subscribe("xforms-item-changed-" + this.id , this, "handleStateChanged");
                connect.subscribe("betterform-insert-item-" + this.id , this, "handleInsertItem");
                connect.subscribe("betterform-delete-item-" + this.id , this, "handleDeleteItem");


            },

            _onBlur:function() {
                // console.debug("bf.SelectFull._onBlur arguments:",arguments, " control:",this.xfControl);
                var selectedValue = this._getSelectedValue();
                this.xfControl.sendValue(selectedValue,true);
            },

            handleStateChanged:function(contextInfo){
                // console.debug("SelectCheckBox.handleStateChanged");
                if(contextInfo.targetName == "label"){
                    dom.byId(contextInfo.parentId+"-label").innerHTML = contextInfo.value;
                }else if(contextInfo.targetName == "value"){
                    var checkBox = dom.byId(contextInfo.parentId+"-value");
                    domAttr.set(checkBox,"value",contextInfo.value);
                    if(this.currentValue.indexOf(contextInfo.value) != -1){
                        checkBox.checked = true;
                    }else {
                        checkBox.checked = false;
                    }
                }else {
                    console.warn("SelectCheckBox.handleStateChanged: no action taken for contextInfo: ",contextInfo);
                }

            },
            /**
             * Create new entry for Select
             * @param contextInfo
             */
            handleInsertItem:function(contextInfo) {
                // console.debug("SelectCheckBox.handleInsert [id: ",this.id, " / contextInfo:",contextInfo,"]");
                this.currentValue = this._getSelectedValue();
                var currentItemset = dom.byId(contextInfo.targetId);
                // console.debug("SelectCheckBox.handleInsert itemset: ",currentItemset);
                var generatedIds= contextInfo.generatedIds;
                var itemId = generatedIds[contextInfo.prototypeId];

                var itemNode = domConstruct.create("span", {id:itemId}, currentItemset, contextInfo.position);
                domClass.add(itemNode, "xfSelectorItem");
                var valueNode = domConstruct.create("input", {id:itemId+"-value",type:"checkbox",value:contextInfo.value}, itemNode, "first");
                domClass.add(valueNode,"xfCheckBoxValue");
                var labelNode = domConstruct.create("label", {id:itemId+"-label",value:contextInfo.label}, itemNode, "last");
                domClass.add(labelNode, "xfCheckBoxLabel");
            },

            handleDeleteItem:function(contextInfo){
                // console.debug("SelectCheckBox.handleDeleteItem contextInfo:",contextInfo);
                var currentItemset = dom.byId(contextInfo.targetId);
                var itemToRemove = query(".xfSelectorItem", currentItemset)[contextInfo.position-1];
                currentItemset.removeChild(itemToRemove);

            },


            _getSelectedValue:function(){
                var selectedValue = "";
                query(".xfCheckBoxValue",this.domNode).forEach(function(item){
                    if(item.checked){
                        if(selectedValue  == ""){
                            selectedValue = item.value;
                        }else {
                            selectedValue += " " + item.value;
                        }
                    }
                });
                return selectedValue;
            }
    });
});

