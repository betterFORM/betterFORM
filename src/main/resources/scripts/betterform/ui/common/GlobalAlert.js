dojo.provide("betterform.ui.common.GlobalAlert");

dojo.declare("betterform.ui.common.GlobalAlert",
        null,
{
    handleValid:function(id,action){
        console.warn("COMING SOON GLOBAL ALERTS - betterform.ui.common.GlobalAlert.valid [id:" + id , " action: " + action + "]");
    },

    handleInvalid:function(id,action){
        console.warn("COMING SOON GLOBAL ALERTS - betterform.ui.common.GlobalAlert.invalid [id:" + id , " action: " + action + "]");
    }

});
