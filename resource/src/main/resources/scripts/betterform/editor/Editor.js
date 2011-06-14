dojo.provide("betterform.editor.Editor");


dojo.require("dijit.layout.ContentPane");
dojo.require("dojox.json.ref") ;

dojo.provide("betterform.Editor");
betterform.Editor.editProperties =  function(targetId){
    console.log("betterform.Editor.editProperties: id of property sheet: ",targetId);
    var dataXfAttrs = dojo.attr(dojo.byId(targetId), "data-xf-attrs");
    var dataXfType = dojo.attr(dojo.byId(targetId), "data-xf-type");

    console.log("dataXfAttrs: ",dataXfAttrs, " dataXfType" ,dataXfType);
/*
                        if (jQuery) {
                            console.log("jquery loaded");
                        } else {
                            console.log("jquery not(!) loaded");
                        }
*/

    var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
    console.log("xfAttrObj:",xfAttrObj);
    if(dataXfAttrs != undefined) {
        var xfAppearance = xfAttrObj.appearance;
        var xfAppearancePropertyNode = dojo.byId("appearance");
        console.log("xfAppearance:",xfAppearance, " xfAppearancePropertyNode :",xfAppearancePropertyNode );
        if(xfAppearancePropertyNode  != undefined) {
            if(!xfAppearance)xfAppearance="";
            dojo.attr(xfAppearancePropertyNode,"value", xfAppearance);
         }

        var nodeset = xfAttrObj.nodeset;
        var nodesetPropertyNode = dojo.byId("nodeset");
        console.log("nodeset:",nodeset, " nodesetPropertyNode:",nodesetPropertyNode);
        if(nodesetPropertyNode!= undefined) {
            dojo.attr(nodesetPropertyNode,"value", nodeset);
         }
        var xfRef = xfAttrObj.ref;
        var xfRefPropertyNode = dojo.byId("ref");
        console.log("xfRef:",xfRef, " refPropertyNode:",xfRefPropertyNode);
        if(xfRefPropertyNode!= undefined) {
            if(!xfRef)xfRef="";
            dojo.attr(xfRefPropertyNode,"value", xfRef);
         }
        var xfBind = xfAttrObj.bind;
        var xfBindPropertyNode = dojo.byId("bind");
        console.log("xfBind:",xfBind , " refPropertyNode:",xfBindPropertyNode);
        if(xfBindPropertyNode  != undefined) {
            if(!xfBind)xfBind="";
            dojo.attr(xfBindPropertyNode ,"value", xfBind);
         }

        var constraint= xfAttrObj.constraint;
        var constraintPropertyNode = dojo.byId("constraint");
        console.log("constraint:",constraint, " constraintPropertyNode:",constraintPropertyNode);
        if(constraintPropertyNode!= undefined) {
            dojo.attr(constraintPropertyNode,"value", constraint);
         }

        var required= xfAttrObj.required;
        var requiredPropertyNode = dojo.byId("required");
        console.log("required:",required, " requiredPropertyNode:",requiredPropertyNode);
        if(requiredPropertyNode!= undefined) {
            dojo.attr(requiredPropertyNode,"value", required);
         }

        var relevant= xfAttrObj.relevant;
        var relevantPropertyNode = dojo.byId("relevant");
        console.log("relevant:",relevant, " relevantPropertyNode:",relevantPropertyNode);
        if(relevantPropertyNode!= undefined) {
            dojo.attr(relevantPropertyNode,"value", relevant);
         }

        var readonly= xfAttrObj.readonly;
        var readonlyPropertyNode = dojo.byId("readonly");
        console.log("readonly:",readonly, " readonlyPropertyNode:",readonlyPropertyNode);
        if(readonlyPropertyNode!= undefined) {
            dojo.attr(readonlyPropertyNode,"value", readonly);
         }

        var xfId= xfAttrObj.id;
        var xfIdPropertyNode = dojo.byId("id");
        console.log("xfId:",readonly, " xfIdPropertyNode:",xfIdPropertyNode);
        if(xfIdPropertyNode!= undefined) {
            if(xfId == undefined) xfId = "";
            dojo.attr(xfIdPropertyNode,"value", xfId);
         }

        var xfReplace= xfAttrObj.replace;
        var xfReplacePropertyNode = dojo.byId("replace");
        console.log("xfReplace:",readonly, " xfReplacePropertyNode:",xfReplacePropertyNode);
        if(xfReplacePropertyNode!= undefined) {
            dojo.attr(xfReplacePropertyNode,"value", xfReplace);
         }
        var xfModel= xfAttrObj.model;
        var xfModelPropertyNode = dojo.byId("model");
        console.log("xfModel:",readonly, " xfModelPropertyNode:",xfModelPropertyNode);
        if(xfModelPropertyNode!= undefined) {
            if(!xfModel)xfModel="";
            dojo.attr(xfModelPropertyNode,"value", xfModel);
         }

        var xfResource= xfAttrObj.resource;
        var xfResourcePropertyNode = dojo.byId("resource");
        console.log("xfResource:",readonly, " xfResourcePropertyNode:",xfResourcePropertyNode);
        if(xfResourcePropertyNode!= undefined) {
            if(!xfResource)xfResource="";
            dojo.attr(xfResourcePropertyNode,"value", xfResource);
         }


        var xfMethod= xfAttrObj.method;
        var xfMethodPropertyNode = dojo.byId("method");
        console.log("xfMethod:",readonly, " xfMethodPropertyNode:",xfMethodPropertyNode);
        if(xfMethodPropertyNode!= undefined) {
            dojo.attr(xfMethodPropertyNode,"value", xfMethod);
         }
    }

};


betterform.Editor.saveProperties =  function(targetId){
    console.log("betterform.Editor.saveProperties: id",targetId);
    var xfAttrObj = new Object();

    var nodeId = dojo.attr(dojo.byId("id"),"value");
    console.log("id for current target: ", nodeId);
    if(nodeId != undefined) {
        xfAttrObj.id = nodeId;
    }
    var xfModel = dojo.attr(dojo.byId("model"),"value");
    if(xfModel  != undefined) {
        xfAttrObj.model= xfModel;
    }

    var xfRef= dojo.attr(dojo.byId("ref"),"value");
    if(xfRef!= undefined) {
        xfAttrObj.ref= xfRef;
    }

    var xfBind= dojo.attr(dojo.byId("bind"),"value");
    if(xfBind != undefined) {
        xfAttrObj.bind= xfBind;
    }

    var xfAppearance= dojo.attr(dojo.byId("appearance"),"value");
    if(xfAppearance!= undefined) {
        xfAttrObj.appearance= xfAppearance;
    }

    var xfAttrString = dojox.json.ref.toJson(xfAttrObj);
    console.debug("xfAttrString :",xfAttrString);
    dojo.attr(dojo.byId(targetId), "data-xf-attrs",xfAttrString);


//    var dataXfAttrs = dojo.attr(dojo.byId(targetId), "data-xf-attrs");
//    var dataXfType = dojo.attr(dojo.byId(targetId), "data-xf-type");
//      console.log("dataXfAttrs: ",dataXfAttrs, " dataXfType" ,dataXfType);

/*
                        if (jQuery) {
                            console.log("jquery loaded");
                        } else {
                            console.log("jquery not(!) loaded");
                        }
*/



};
