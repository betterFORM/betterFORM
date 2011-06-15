dojo.provide("betterform.editor.Editor");


dojo.require("dijit.layout.ContentPane");
dojo.require("dojox.json.ref") ;

dojo.provide("betterform.Editor");


betterform.Editor.editProperty =  function(xfAttrObj, attributeName){
    var xfAttrValue = xfAttrObj[attributeName];
    var xfAttrPropertyNode = dojo.byId(attributeName);
    console.log("Editor.editProperty", attributeName, ":  xfAttrValue :",xfAttrValue , " xfAttrPropertyNode :",xfAttrPropertyNode );
    if(xfAttrPropertyNode != undefined) {
        if(!xfAttrValue)xfAttrValue ="";
        dojo.attr(xfAttrPropertyNode,"value", xfAttrValue);
     }

};

betterform.Editor.editProperties =  function(targetId){
    console.log("betterform.Editor.editProperties: id of property sheet: ",targetId);
    var dataXfAttrs = dojo.attr(dojo.byId(targetId), "data-xf-attrs");
    var dataXfType = dojo.attr(dojo.byId(targetId), "data-xf-type");

    console.log("dataXfAttrs: ",dataXfAttrs, " dataXfType" ,dataXfType);

    var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
    console.log("xfAttrObj:",xfAttrObj);
    if(xfAttrObj != undefined ) {
        for(value in xfAttrObj) {
            console.debug("value: ",value);
            betterform.Editor.editProperty(xfAttrObj,value);
/*
        var commonAttributes = new Array("id");
        var uiCommonAttributes = new Array("appearance","navindex","accesskey");
        var singleNodeBindingAttributes = new Array("ref","model","bind");
        var nodeSetBindingAttributes = new Array("nodeset","model","bind");
        var modelItemPropertyAttributes = new Array("type","readonly","required","relevant","calculate","constraint");

        var xfDocument = new Object();
        xfDocument.model = new Array("functions","schema", "version");
        xfDocument.instance = new Array(commonAttributes, "src","resource");
        xfDocument.submission = new Array(commonAttributes, "ref","bind","resource","action","mode","method","validate",
                                            "relevant","serialization","version","indent", "mediatype","encoding","omit-xml-declaration",
                                            "standalone","cdata-section-elements","replace","instance","targetref","separator","includenamespaceprefixes");
        xfDocument.bind = new Array(commonAttributes,modelItemPropertyAttributes,"nodeset");


        xfDocument.group = new Array(commonAttributes,uiCommonAttributes, singleNodeBindingAttributes);
        xfDocument.input = new Array(commonAttributes,uiCommonAttributes,singleNodeBindingAttributes,"inputmode","incremental");
        xfDocument.secret = xfDocument.input;
        xfDocument.textarea = xfDocument.input;
        xfDocument.output = new Array(commonAttributes,"appearance","value","mediatype");
        xfDocument.upload = new Array(commonAttributes,uiCommonAttributes, singleNodeBindingAttributes,"mediatype", "incremental");
        xfDocument.range = new Array(commonAttributes,uiCommonAttributes,singleNodeBindingAttributes,"start","end","step","incremental");
        xfDocument.trigger= new Array(commonAttributes,uiCommonAttributes,singleNodeBindingAttributes);
        xfDocument.submit = new Array(commonAttributes,uiCommonAttributes,singleNodeBindingAttributes,"submission");
        xfDocument.select = new Array(commonAttributes,uiCommonAttributes,singleNodeBindingAttributes,"selection","incremental");
        xfDocument.select1 = new Array(commonAttributes,uiCommonAttributes,singleNodeBindingAttributes,"selection","incremental");

        if(dataXfType != undefined && xfDocument[dataXfType] != undefined){
            // console.log("udpate instance properties");
            dojo.forEach(xfDocument[dataXfType], function(value) {
                // console.debug("value: ",value);
                if(value instanceof Array) {
                    // console.log("udpate instance properties: found array");
                    dojo.forEach(value, function(arrayValue) {
                        // console.debug("arrayvalue: ",arrayValue);
                        betterform.Editor.editProperty(xfAttrObj,arrayValue);
                    });
                }else {
                    betterform.Editor.editProperty(xfAttrObj,value);
                }
            });
*/
        }
    }

};
betterform.Editor.saveProperty = function(targetId, propertyId) {
    console.log("betterform.Editor.saveProperty: id",targetId, " propertyId:",propertyId);

    var dataXfAttrs = dojo.attr(dojo.byId(targetId), "data-xf-attrs");
    console.log("dataXfAttrs orig: ",dataXfAttrs);

    var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
    console.log("xfAttrObj:",xfAttrObj);
    var newValue = dojo.attr(dojo.byId(propertyId),"value");
    if(!newValue)newValue="";
    xfAttrObj[propertyId]=newValue;
    var xfAttrString = dojox.json.ref.toJson(xfAttrObj);
    console.debug("xfAttr new:",xfAttrString);
    dojo.attr(dojo.byId(targetId), "data-xf-attrs",xfAttrString);





};

betterform.Editor.saveProperties =  function(targetId){
    console.log("betterform.Editor.saveProperties: id",targetId);
    var xfAttrObj = new Object();

    var nodeId = dojo.attr(dojo.byId("id"),"value");
    if(nodeId != undefined) {
        xfAttrObj.id = nodeId;
    }
    var xfModel = dojo.attr(dojo.byId("model"),"value");
    if(xfModel  != undefined) {
        xfAttrObj.model= xfModel;
    }

    var xfAppearance= dojo.attr(dojo.byId("appearance"),"value");
    if(xfAppearance!= undefined) {
        xfAttrObj.appearance= xfAppearance;
    }

    var xfNodeset= dojo.attr(dojo.byId("nodeset"),"value");
    if(xfAppearance!= undefined) {
        xfAttrObj.appearance= xfAppearance;
    }

    var xfRef= dojo.attr(dojo.byId("ref"),"value");
    if(xfRef!= undefined) {
        xfAttrObj.ref= xfRef;
    }

    var xfBind= dojo.attr(dojo.byId("bind"),"value");
    if(xfBind != undefined) {
        xfAttrObj.bind= xfBind;
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
