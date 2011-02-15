dojo.provide("betterform.BfRequiredTimeTracker");

dojo.declare("betterform.BfRequiredTimeTracker", null,
{
    constructor:function() {
        dojo.require("betterform.BfRequiredFull");

        var fullDependencies = new betterform.BfRequiredFull();
        dojo.require("dijit.Declaration");
        dojo.require("dijit.Toolbar");
        dojo.require("dijit.ToolbarSeparator");
    }
});