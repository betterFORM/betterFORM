/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.common.BowlAlert");

dojo.require("betterform.ui.common.Alert");

dojo.declare("betterform.ui.common.BowlAlert",
        betterform.ui.common.Alert,
{
    handleValid:function(id,action){
        console.warn("COMING SOON GROWL STYLE ALERTS - betterform.ui.common.BowlAlert.valid [id:" + id , " action: " + action + "]");
    },

    handleInvalid:function(id,action){
        console.warn("COMING SOON GROWL STYLE ALERTS - betterform.ui.common.BowlAlert.invalid [id:" + id , " action: " + action + "]");
    }

});
