dojo.provide("betterform.BfRequiredTimeTracker");

dojo.declare("betterform.BfRequiredTimeTracker", null,
{
    // Class to dojo.require all other classes

    constructor:function() {
        dojo.require("betterform.BfRequiredFull");

        var fullDependencies = new betterform.BfRequiredFull();
            dojo.require("dojo.parser");
            dojo.require("dijit.dijit");
            dojo.require("dijit.Declaration");
            dojo.require("dijit.Toolbar");
            dojo.require("dijit.ToolbarSeparator");
            dojo.require("dijit.Dialog");
            dojo.require("dijit.TitlePane");
            dojo.require("betterform.ui.container.Group");
            dojo.require('dijit.layout.ContentPane');
            dojo.require("dijit.form.Button");
            dojo.require("dijit.form.CheckBox");
    }
});