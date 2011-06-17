dojo.provide("betterform.editor.Editor");


dojo.require("dijit.layout.ContentPane");
dojo.require("dojox.json.ref");

dojo.provide("betterform.Editor");


betterform.Editor.editProperty = function(xfAttrObj, attributeName) {
    var xfAttrValue = xfAttrObj[attributeName];

    if (!xfAttrValue)xfAttrValue = "";

    console.log("Editor.editPropertyNode: '", attributeName, "' -  xfAttrValue: '", xfAttrValue, "'");


    currentDijit = dijit.byId("widget_" + attributeName);
/*    if (currentDijit == undefined) {
        if(attributeName == "type") {
            currentDijit= new dijit.form.FilteringSelect({
                        name: attributeName,
                        value: xfAttrValue,
                        store: stateStore,
                        searchAttr: "type"
                    },
                    dojo.byId(attributeName));
        } else {
            currentDijit = new dijit.form.TextBox({
                        name: attributeName,
                        value: xfAttrValue
                    },
                    dojo.byId(attributeName));
        }
    }*/

    if (currentDijit != undefined) {
        console.log("currentDijit: ", currentDijit);
        currentDijit.set("value",xfAttrValue);
    }else {
        console.log("currentNode: ", dojo.byId(attributeName));
        var attrNode = dojo.byId(attributeName);
        dojo.attr(attrNode, "value", xfAttrValue);
    }
};

betterform.Editor.editProperties = function(targetId) {
    console.log("betterform.Editor.editProperties: id of property sheet: ", targetId);
    var dataXfAttrs = dojo.attr(dojo.byId(targetId), "data-xf-attrs");
    var dataXfType = dojo.attr(dojo.byId(targetId), "data-xf-type");

    console.log("dataXfAttrs: ", dataXfAttrs, " dataXfType", dataXfType);

    var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
    console.log("xfAttrObj:", xfAttrObj);
    if (xfAttrObj != undefined) {
        for (value in xfAttrObj) {
            betterform.Editor.editProperty(xfAttrObj, value);
        }
    } else {
        console.warn("")
    }

};
betterform.Editor.saveProperty = function(targetId, propertyId) {
    console.log("betterform.Editor.saveProperty: id", targetId, " propertyId:", propertyId);
    // get the former attribute values
    var dataXfAttrs = dojo.attr(dojo.byId(targetId), "data-xf-attrs");
    // console.log("dataXfAttrs orig: ", dataXfAttrs);
    var xfAttrObj = dojox.json.ref.fromJson(dataXfAttrs);
    // console.log("xfAttrObj:", xfAttrObj);

    // get the dijit holding the attribute value to save
    var propertyNode = dojo.query(".xf" + propertyId)[0];
    console.log("propertyNode:", propertyNode);
    var widgetId= dojo.attr(propertyNode, "id");
    console.log("widgetId: ",widgetId);
    var propertyDijit = dijit.byId(widgetId);
    console.log("propertyDijit:", propertyDijit);
    var newValue = propertyDijit.get("value");
    console.log("newValue:", newValue);
    if (!newValue)newValue = "";
    xfAttrObj[propertyId] = newValue;
    var xfAttrString = dojox.json.ref.toJson(xfAttrObj);
    console.debug("xfAttr new:", xfAttrString);
    dojo.attr(dojo.byId(targetId), "data-xf-attrs", xfAttrString);


};
