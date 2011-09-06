/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.secret.Secret");


dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.form.TextBox");

dojo.declare(
        "betterform.ui.secret.Secret",
        [betterform.ui.ControlValue,betterform.ui.input.TextField],
{
    type:"password"

});
