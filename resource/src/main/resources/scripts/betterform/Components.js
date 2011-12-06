dojo.provide("betterform.components");
dojo.require("dojo.behavior");

dojo.behavior.add({

    //select xforms type
    '.xfControl': function(n) {
        console.debug("Control found: ",n);
        //select subtype (datatype, appearance, mediatype)
            //createXXX
    },
    '.xfInput': function(n) {
        console.debug("input found: ",n);

        //select subtype (datatype, appearance, mediatype)
            //createXXX
        var controlId = dojo.attr(n,"id");

            var xfValue = n.innerHTML;
            // console.debug("UIElementFactory.createWidget: String Value: ", xfValue);
            new betterform.ui.input.TextField({
                name:controlId + "-value",
                value:xfValue,
                "class":"foo",
                title:dojo.attr(n,"title"),
                xfControlId:controlId
            }, n);

    }

}),

dojo.behavior.apply()

