dojo.provide("betterform.BfRequiredXFormsFeatureExplorer");

dojo.declare("betterform.BfRequiredXFormsFeatureExplorer", null,
{
    // Class to dojo.require all other classes

    constructor:function() {
        dojo.require("betterform.BfRequiredFull");

        var fullDependencies = new betterform.BfRequiredFull();

        dojo.require("dijit.MenuBar");
        dojo.require("dijit.PopupMenuBarItem");
        dojo.require("dijit.Tooltip");
    }
});