/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("bf.BowlAlert");

dojo.require("bf.Alert");

dojo.declare("bf.BowlAlert",
        bf.Alert,
{
    handleValid:function(id,action){
        console.warn("COMING SOON GROWL STYLE ALERTS - bf.BowlAlert.valid [id:" + id , " action: " + action + "]");
    },

    handleInvalid:function(id,action){
        console.warn("COMING SOON GROWL STYLE ALERTS - bf.BowlAlert.invalid [id:" + id , " action: " + action + "]");
    }

});
