/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select.OptGroup");

dojo.require("dijit._Widget");

dojo.require("dojox.xml.parser");
/**
	All Rights Reserved.
	@author Joern Turner
	@author Lars Windauer

    class represents itemsets in selects with no appearance, appearance="minimal" or "compact"

**/

dojo.declare(
        "betterform.ui.select.OptGroup",
         dijit._Widget,
{
    values:"",

    handleStateChanged:function(contextInfo) {

/*
       console.debug("OptGroup.handleStateChanged this:", this,
                                             "\n\tcontextInfo: ",contextInfo,
                                             "\n\toriginalId: ",contextInfo.originalId,
                                             "\n\tposition: ",contextInfo.position,
                                             "\n\tprototypeId: ",contextInfo.prototypeId,
                                             "\n\ttargetId: ",contextInfo.targetId,
                                             "\n\ttargetName: ",contextInfo.targetName,
                                             "\n\tlabel: ",contextInfo.label,
                                             "\n\tvalue: ",contextInfo.value
        );
*/
        var parentNode = dojo.byId(contextInfo.parentId);
        // console.debug("OptGroup.handleStateChagend: parentNode: ", parentNode);
        if(contextInfo.targetName == "label" && parentNode != undefined){
            var select = dojo.byId(dojo.attr(this.domNode.parentNode,"id"));
            var i;
            for(i=0; i<select.length; i++){
                if(contextInfo.parentId == dojo.attr(select.options[i], "id")){
                    select.options[i].text = contextInfo.value;
                }
            }
        }else if(contextInfo.targetName == "value" && parentNode != undefined){
            dojo.attr(parentNode,"value",contextInfo.value);

            // console.debug("OptGroup.handleInsert parent: " ,parentNode , " parent value: " + dojo.attr(parent, "value"));
            // make sure that the associated select displays the correct value

            if(this.domNode.parentNode.localName.toLowerCase() == "select"){
                var selectDijit = dijit.byId(dojo.attr(this.domNode.parentNode,"id"));
/*
                console.debug("found selectDijit: " + selectDijit);
                console.debug("Compare values: selectDijit.currentValue: " + selectDijit.currentValue  + "  contextInfo.value:" + contextInfo.value);
*/
                if(selectDijit.currentValue == contextInfo.value) {
                    selectDijit._handleSetControlValue(contextInfo.value);
                }
            }else {
                console.warn("OptGroup.handleInsert parentNode is not select");
            }

        }else {
            console.warn("OptGroup.handleStateChanged: no action taken for contextInfo: ",contextInfo);
        }
    },


    handleInsert:function(contextInfo) {
/*
        console.debug("OptGroup.handleInsert this:", this,
                                             "\n\tcontextInfo: ",contextInfo,
                                             "\n\tgeneratedIds: ",contextInfo.generatedIds,
                                             "\n\toriginalId: ",contextInfo.originalId,
                                             "\n\tposition: ",contextInfo.position,
                                             "\n\tprototypeId: ",contextInfo.prototypeId,
                                             "\n\ttargetId: ",contextInfo.targetId,
                                             "\n\ttargetName: ",contextInfo.targetName,
                                             "\n\tlabel: ",contextInfo.label,
                                             "\n\tvalue: ",contextInfo.value
                );
*/

        var itemNode = document.createElement("option");
        dojo.addClass(itemNode, "xfSelectorItem");

        var generatedIds= contextInfo.generatedIds;
        var prototype = dojo.query(".xfSelectorPrototype",dojo.byId(contextInfo.originalId + "-prototype"))[0];
        // console.debug("generatedIds: ",generatedIds, " prototype:",prototype, " newId: ",generatedIds[contextInfo.prototypeId]);
        if(generatedIds != undefined) {
            dojo.attr(itemNode, "id", generatedIds[contextInfo.prototypeId]);

            if(prototype != undefined){
                var title = generatedIds[dojo.attr(prototype, 'title')];
                if (title != undefined) {
                    dojo.attr(itemNode, "title", title);
                }
                var value = generatedIds[dojo.attr(prototype, 'value')];
                if (value != undefined) {
                    dojo.attr(itemNode, "value", value);
                }
                itemNode.innerHTML = prototype.innerHTML;
            }
        }


        if(contextInfo.label != undefined) {
            dojo.query(".xfSelectorItem", itemNode).addContent(contextInfo.label);
            // console.debug("OptGroup.handleInsert: label:" + contextInfo.label + " for new created option" );

        }
        if(contextInfo.value != undefined) {
            // console.debug("OptGroup.insertNode: value: ", value);
            dojo.attr(itemNode, "value", contextInfo.value);
        }

        dojo.place(itemNode,this.domNode,  contextInfo.position);
    },

    handleDelete:function(contextInfo) {
        // console.debug("OptGroup.deleteItem: ",contextInfo);
        var itemToRemove =dojo.query(".xfSelectorItem", this.domNode)[contextInfo.position-1];
        this.domNode.removeChild(itemToRemove)

    }

});


