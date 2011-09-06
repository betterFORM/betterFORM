/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.XFormsStore");

dojo.require("dojox.data.XmlStore");

dojo.declare("betterform.ui.XFormsStore",
    dojox.data.XmlStore,  {



        getFeatures: function(){
            return {
                'dojo.data.api.Read': true,
                'dojo.data.api.Identity': true
            };
        },

        // Identity value for top level nodes
        getIdentity: function (item) {
           return this.getValue(item, "name");
        },

        // Array of attribute names making up id
        getIdentityAttributes: function(item) {
           return [ "name" ];
        },

        fetchItemByIdentity: function(keywordArgs) {
            var xfData = dojo.byId("xfData");
/*
            if(true){

                var item = dojo.byId("xfData");
                if(!self.isItem(item)){
                    item = null;
                }
                if(keywordArgs.onItem){
                    var scope =  keywordArgs.scope?keywordArgs.scope:dojo.global;
                    keywordArgs.onItem.call(scope, item);
                }

            }
*/
            console.debug("XFData: ", xfData);
            console.dirxml(xfData);
        }
    }
);