dojo.provide("betterform.BfRequiredXFormsFeatureExplorer");

dojo.declare("betterform.BfRequiredXFormsFeatureExplorer", null,
{
    // Class to dojo.require all other classes

    constructor:function() {
        dojo.require("betterform.BfRequiredFull");

        var fullDependencies = new betterform.BfRequiredFull();


    }
});