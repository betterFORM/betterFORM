dojo.provide("betterform.ui.common.InlineAlert");

dojo.declare("betterform.ui.common.InlineAlert",
        null,
{
    handleValid:function(id,action){
        console.warn("COMING SOON INLINE ALERTS -  betterform.ui.common.InlineAlert.valid [id:" + id , " action: " + action + "]");
    },

    handleInvalid:function(id,action){
        console.warn("COMING SOON INLINE ALERTS - betterform.ui.common.InlineAlert.invalid [id:" + id , " action: " + action + "]");
    }

});
