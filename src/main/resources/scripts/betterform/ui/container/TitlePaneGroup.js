dojo.provide("betterform.ui.container.TitlePaneGroup");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.container.Container");
dojo.require("dijit.TitlePane");


dojo.declare(
        "betterform.ui.container.TitlePaneGroup",
        [betterform.ui.container.Container,dijit.TitlePane],
{
    // XForms MIPs
    handleStateChange:function() {
        // console.debug("betterform.ui.container.TitlePaneGroup.handleStateChange");
        this.inherited(arguments);
    }


});


