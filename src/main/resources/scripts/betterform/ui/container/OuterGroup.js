dojo.provide("betterform.ui.container.OuterGroup");

dojo.require("dijit._Widget");
dojo.require("dijit.layout.ContentPane");


dojo.declare(
        "betterform.ui.container.OuterGroup",
        dijit.layout.ContentPane,
{

    onLoad: function(e) {
        this.inherited(arguments);
        // console.debug("OuterGroup loaded", this);
    }
});


