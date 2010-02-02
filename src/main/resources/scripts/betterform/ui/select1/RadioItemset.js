/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.select1.RadioItemset");

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
        "betterform.ui.select1.RadioItemset",
         dijit._Widget,
{
    values:"",

    handleStateChanged:function(contextInfo) {
        console.debug("RadioItemset.handleStateChanged: ",contextInfo);
        if(contextInfo.targetName == "label"){
            dojo.byId(contextInfo.parentId+"-label").innerHTML = contextInfo.value;
        }else if(contextInfo.targetName == "value"){
            dijit.byId(contextInfo.parentId+"-value")._handleSetControlValue(contextInfo.value);
        }else {
           console.warn("RadioItemSet.handleStateChanged: no action taken for contextInfo: ",contextInfo);
        }
    },


    handleInsert:function(contextInfo) {
        console.debug("RadioItemset.insertItem: ",contextInfo);

        var itemNode = document.createElement("span");
        dojo.addClass(itemNode, "xfSelectorItem");
        dojo.addClass(itemNode, "xfEnabled");
        dojo.addClass(itemNode, "xfReadWrite");
        dojo.addClass(itemNode, "xfOptional");
        dojo.addClass(itemNode, "xfValid");
        dojo.attr(itemNode, "controltype","radioButtonEntry");

        var generatedIds= contextInfo.generatedIds;
        var itemId = generatedIds[contextInfo.prototypeId];
        dojo.attr(itemNode, "id",itemId );
        var labelNode = document.createElement("label");
        dojo.addClass(labelNode, "xfLabel");
        dojo.attr(labelNode, "id",itemId+"-label" );
        dojo.attr(labelNode, "for",itemId+"-value" );
        labelNode.innerHTML = contextInfo.label;
        var myParentNode = this.domNode.parentNode;
        while(!dojo.hasClass(myParentNode,"xfSelect1")){
            myParentNode = myParentNode.parentNode;
        }
        console.debug("myParentNode",myParentNode);
        console.debug("RadioItemset.insertItemset: created node: ", itemNode);
        console.dirxml(itemNode);

        var valueNode = document.createElement("input");
        dojo.addClass(valueNode, "xfValue");
        dojo.attr(valueNode, "id",itemId+"-value" );
        dojo.attr(valueNode, "selected","false");
        dojo.attr(valueNode, "parentId",myParentNode.id);
        dojo.attr(valueNode, "name",myParentNode.id+"-value");
        dojo.attr(valueNode, "value","");
        dojo.attr(valueNode, "datatype","radio");
        dojo.attr(valueNode, "controltype","radio");

       dojo.place(valueNode,itemNode);
       dojo.place(labelNode,itemNode);


        
        var prototype = dojo.byId(contextInfo.originalId+"-prototype");
        //console.dirxml(prototype);

        var outputControls = dojo.query("[controltype='output-control']", prototype);

        if (outputControls != undefined && outputControls != "") {
            //var index=0;
            // for (index=0; index<=outputControls.length; index++) {
                var item =  outputControls[0].cloneNode(true);
                
                dojo.attr(item,"id",generatedIds[item.id]);
                dojo.attr(item,"parentId", myParentNode.id);
                dojo.attr(item,"parentid", myParentNode.id);
            
                this._replacePrototypeIds(item, generatedIds);
                console.dirxml(item);

                dojo.place(item, labelNode);
          // }
        }

        var controlDijit = new betterform.ui.Control({contextInfo:contextInfo}, itemNode);
        dojo.place(itemNode,this.domNode,  contextInfo.position);
    },

    _replacePrototypeIds:function(node, generatedIds) {
        dojo.query("*", node).forEach(
                function(xfNode) {
                    var idAtt = dojo.attr(xfNode, "id");
                    //console.debug("idAtt: ", idAtt)
                    if (idAtt != undefined && generatedIds[idAtt] != undefined) {
                        //console.debug("idAtt2: ", idAtt)
                        dojo.attr(xfNode, "id", generatedIds[idAtt]);
                    }

                    else if (idAtt != undefined) {
                        var idPrefix;
                        var idAppendix;

                        if (idAtt.indexOf("-value") != -1) {
                            //console.debug("-value: ")
                            idPrefix = idAtt.substring(0, idAtt.indexOf("-value"));
                            idAppendix = "-value";
                        } else if (idAtt.indexOf("-label") != -1) {
                            //console.debug("-label: ")
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

                        // console.debug("original Id: " + idAtt + " prefix:: " + idPrefix + " appendix:" +idAppendix);;
                        var generatedId = generatedIds[idPrefix] + idAppendix;
                        // console.debug("original Id: " + idAtt + " generatedId: " + generatedId);
                        dojo.attr(xfNode, "id", generatedId);
                    }

                    var forAtt = dojo.attr(xfNode, "for");
                    //console.debug("forAtt: ", forAtt)
                    if (forAtt != undefined && generatedIds[forAtt] != undefined) {
                        dojo.attr(xfNode, "for", generatedIds[forAtt]);
                    }
                    else if (forAtt != undefined) {
                        var forPrefix;
                        var forAppendix;

                        if (forAtt.indexOf("-value") != -1) {
                            forPrefix = forAtt.substring(0, forAtt.indexOf("-value"));
                            forAppendix = "-value";
                        } else {
                            // console.warn("Repeat._replacePrototypeIds Failure replaceing Id! Id to replace: ", idAtt, " generatedIds: ", generatedIds);
                            return;
                        }

                        // console.debug("original Id: " + idAtt + " prefix:: " + idPrefix + " appendix:" +idAppendix);;
                        var generatedId = generatedIds[forPrefix] + forAppendix;
                        // console.debug("original Id: " + idAtt + " generatedId: " + generatedId);
                        dojo.attr(xfNode, "for", generatedId);
                    }
                }
                );
    },

    _replaceRepeatItemClasses:function(/* Node */ node) {
      // console.debug("Repeat._replaceRepeatItemClasses node:",node );
      // console.dirxml(node);
        dojo.removeClass(node, "xfRepeatPrototype");
        dojo.removeClass(node, "xfDisabled");
        dojo.addClass(node, "xfRepeatItem");
        dojo.addClass(node, "xfEnabled");
        dojo.addClass(node, "xfRepeatIndexPre");
    },

    handleDelete:function(contextInfo) {
        // console.debug("RadioGroup.deleteItem: ",contextInfo);
        var itemToRemove =dojo.query(".xfSelectorItem", this.domNode)[contextInfo.position-1];
        this.domNode.removeChild(itemToRemove)
    }

});


