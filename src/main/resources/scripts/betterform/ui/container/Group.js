dojo.provide("betterform.ui.container.Group");

dojo.require("dijit._Widget");
dojo.require("betterform.ui.container.Container");


dojo.declare(
        "betterform.ui.container.Group",
        betterform.ui.container.Container,
{
    // XForms MIPs
    handleStateChange:function() {
        // console.debug("betterform.ui.container.Group.handleStateChange", contextInfo);
        this.inherited(arguments);
    }


});


