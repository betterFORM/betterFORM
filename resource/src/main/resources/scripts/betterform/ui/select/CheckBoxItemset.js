/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select.CheckBoxItemset");

dojo.require("dijit._Widget");

dojo.require("dojox.xml.parser");
dojo.require("betterform.ui.select.CheckBox");
/**
	All Rights Reserved.

	@author Joern Turner
	@author Lars Windauer
    @see betterform.ui.select.CheckBoxGroup
    @date 2008.11.06
    
    CheckBoxGroup "Value Diit" represents a Select with appearance="full"
        Controller class for checkboxes (betterform.util.select.CheckBox) within Select Control

**/

dojo.declare(
        "betterform.ui.select.CheckBoxItemset",
         [dijit._Widget],
{
    values:"",
    widgetsInTemplate:true,

    /**
     * handle betterform-state-changed event
     * @param contextInfo
     */
    handleStateChanged:function(contextInfo) {
        // console.debug("CheckBoxItemSet.handleStateChanged contextInfo", contextInfo);
        if(contextInfo.targetName == "label"){
            dojo.byId(contextInfo.parentId+"-label").innerHTML = contextInfo.value;
        }else if(contextInfo.targetName == "value"){
            dojo.attr(dojo.byId(contextInfo.parentId+"-value"),"value",contextInfo.value);
            dijit.byId(contextInfo.parentId+"-value").currentValue =  contextInfo.value;
            // make sure that the associated select displays the correct value
            if(dojo.hasClass(this.domNode.parentNode, "CheckBoxGroup")){
                var selectDijit = dijit.byId(dojo.attr(this.domNode.parentNode,"id"));
                console.debug("CheckBoxItemset SelectDijit: ", selectDijit, " selectDijit.currentValue:",selectDijit.currentValue , " contextInfo.value: ",contextInfo.value);
                if(selectDijit.currentValue == contextInfo.value) {
                    selectDijit._handleSetControlValue(contextInfo.value);
                }
            }

        }else {
           console.warn("CheckBoxItemset.handleStateChanged: no action taken for contextInfo: ",contextInfo);
        }

    },

    /**
     * Create new entry for Select
     * @param contextInfo
     */
    handleInsert:function(contextInfo) {
        console.debug("CheckBoxItemset.handleInsert [id: ",this.id, " / contextInfo:",contextInfo,"]");
        var itemNode = document.createElement("span");
        var generatedIds= contextInfo.generatedIds;
        var itemId = generatedIds[contextInfo.prototypeId];
        dojo.attr(itemNode,"id",itemId);
        dojo.addClass(itemNode, "xfSelectorItem");
        // find CheckBoxGroup widget
        var myParentNode = this.domNode.parentNode;
        while(!dojo.hasClass(myParentNode,"bfCheckBoxGroup")){
            myParentNode = myParentNode.parentNode;
        }
        console.debug("CheckBoxItemset.handleInsert [id: ",this.id, " / parentNode:",myParentNode,"]");
        var valueNode = new betterform.ui.select.CheckBox({
                id:itemId+"-value",
                "class":"xfCheckBoxValue",
                type:"checkbox",
                value:contextInfo.value,
                selectWidgetId:myParentNode.id,
                dojoType:"betterform.ui.select.CheckBox"
            }
        );

         console.debug("CheckBoxItemset.handleInsert [id: ",this.id, " / checkBoxDijit:",valueNode,"]");
        dojo.place(valueNode.domNode,itemNode);
        // create Label
        var labelNode = document.createElement("span");
        dojo.addClass(labelNode, "xfCheckBoxLabel");
        dojo.attr(labelNode,"id",itemId + "-label");
        labelNode.innerHTML = contextInfo.label;


        dojo.place(labelNode,itemNode);
        dojo.place(itemNode,this.domNode,  contextInfo.position);

    },
    /**
     * Delete entry from Select
     * @param contextInfo
     */
    handleDelete:function(contextInfo) {
        console.debug("CheckBoxItemset.deleteItem: ",contextInfo);
        var itemToRemove =dojo.query(".xfSelectorItem", this.domNode)[contextInfo.position-1];
        this.domNode.removeChild(itemToRemove);
    }

});


