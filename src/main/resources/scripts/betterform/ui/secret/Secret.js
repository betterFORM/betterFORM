dojo.provide("betterform.ui.secret.Secret");


dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.TextBox");
dojo.require("betterform.ui.ControlValue");
dojo.require("betterform.ui.input.TextField");

dojo.declare(
        "betterform.ui.secret.Secret",
        [betterform.ui.ControlValue,betterform.ui.input.TextField],
{
    type:"password"

});
