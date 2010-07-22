dojo.provide("betterform.ui.common.BowlAlert");

dojo.declare("betterform.ui.common.BowlAlert",
        null,
{
    handleValid:function(id,action){
        console.warn("COMING SOON GROWL STYLE ALERTS - betterform.ui.common.BowlAlert.valid [id:" + id , " action: " + action + "]");
    },

    handleInvalid:function(id,action){
        console.warn("COMING SOON GROWL STYLE ALERTS - betterform.ui.common.BowlAlert.invalid [id:" + id , " action: " + action + "]");
    }

});
