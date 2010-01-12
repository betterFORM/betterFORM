dojo.provide("betterform.ui.select.OptGroup");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.ControlValue");
dojo.require("dojox.data.dom");
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
       console.debug("OptGroup.handleInsert this:", this,
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
        if(contextInfo.targetName == "label"){
            dojo.byId(contextInfo.parentId).innerHTML = contextInfo.value;
        }else if(contextInfo.targetName == "value"){
            dojo.attr(dojo.byId(contextInfo.parentId),"value",contextInfo.value);
            // make sure that the associated select displays the correct value
            if(dojo.hasClass(this.domNode.parentNode.localName == "select")){
                var selectDijit = dijit.byId(dojo.attr(this.domNode.parentNode,"id"));
                if(selectDijit.currentValue == contextInfo.value) {
                    selectDijit._handleSetControlValue(contextInfo.value);
                }
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
        //console.debug("generatedIds: ",generatedIds, " prototype:",prototype, " newId: ",generatedIds[contextInfo.prototypeId]);
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
            itemNode.innerHTML = contextInfo.label;
        }
        if(contextInfo.value != undefined) {
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


