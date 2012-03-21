/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.InputBehavior");
dojo.require("betterform.xf.XFControl");
dojo.require("dijit.form.DateTextBox");
dojo.require("betterform.util");


/*
todo: dependencies must be imported for foreign (non-dojo) components
<script type="text/javascript src="..."/>
<style type="text/css" src="..."/>
*/

/*
    Component Definition File which is the central for mapping XForms controls to client-side controls.
    todo:see newJSLayer-readme.txt
*/
var inputBehavior = {


    // ############################## INPUT MAPPINGS ############################################################
    // ############################## INPUT MAPPINGS ############################################################
    // ############################## INPUT MAPPINGS ############################################################

    // a default input control (TextField) bound to a string
    '.xfInput.xsdString .xfValue': function(n) {
        console.debug("FOUND: string input field: ",n);

        /*
         ###########################################################################################
         xfId is the id of the parent element in the DOM that matches the id of the original XForms
         element.
        */
        var xfId = getXfId(n);

        /*
        xfControl is an instance of the XFControl class. This is the generic class that handles all interactions
        with the XForms processor implementation. The concrete native browser or javascript controls are called
        'widgets' in the context of the client side. They are the concrete representations the user interacts with.
         */
        var xfControl = dijit.byId(xfId);

        /*
        ###########################################################################################
        EVENT BINDING
        ###########################################################################################

        Widgets are bound to their XFControl via events. Whenever a user changes the value of a control this change
        must be propagated to its XFControl which will in turn send it to the server whenever appropriate.

        There must be at least one event handler to notify XFControl of value changes. However it is highly
        recommended to add two listeners - one to support incremental updates and one for onblur updates. Please
        be aware that the order of registration can be significant for proper operation.
        */

        dojo.connect(n,"onkeyup",function(evt){
            // console.debug("onkeypress",n);
            xfControl.sendValue(n.value,evt);
        });

        dojo.connect(n,"onblur",function(evt){
            // console.debug("onblur",n);
            xfControl.sendValue(n.value, evt);
        });

    },

    // ############################## BOOLEAN INPUT ##############################
    // ############################## BOOLEAN INPUT ##############################
    // ############################## BOOLEAN INPUT ##############################
    '.xfInput.xsdBoolean .xfValue': function(n) {
        console.debug("FOUND: boolean input field: ",n);
        var xfId = n.id.substring(0,n.id.lastIndexOf("-"));
        var xfControl = dijit.byId(xfId);

        /*
         input type="checkbox" fails to honor readonly attribute and thus is overwritten here. It seems this is
         rather a HTML Spec issue as e.g. comboxes behave the same. You can visibly change the value though
         the control is readonly. As this seems rather contra intuitive for users we have chosen to use 'disabled'
         here instead.
         */
        xfControl.setReadonly = function() {
            console.debug("overwritten checkbox function")
            dojo.attr(n, "disabled","disabled");
        };
        xfControl.setReadwrite = function() {
            n.removeAttribute("disabled");
        }

        dojo.connect(n,"onblur",function(evt){
//            console.debug("onblur",n);
//            console.debug("xfId",xfId);
//            console.debug("checked",n.checked);
            if(n.checked != undefined){
                xfControl.sendValue(n.checked,evt);
            }
        });
        dojo.connect(n,"onclick",function(evt){
//            console.debug("onclick",n);
//            console.debug("xfId",xfId);
//            console.debug("value",n.value);
//            console.debug("checked",n.checked);
            if(n.checked != undefined){
                xfControl.sendValue(n.checked,evt);
            }
        });

    },

    // ############################## DATE INPUT ##############################
    // ############################## DATE INPUT ##############################
    // ############################## DATE INPUT ##############################

    //using detailed behavior syntax
    '.xfInput.xsdDate .xfValue' : {
        found: function(n){
//            console.debug("date input field: ",n);
//            console.debug("date input field: ",n.value);

            var dateWidget = new dijit.form.DateTextBox({
                value      : new Date(n.value)
            },n);

            var xfId = getXfId(n);
            dojo.connect(dijit.byId(xfId), "handleStateChanged", function(contextInfo){
                // ##### setting value by platform/component-specific means #####
                // console.debug("handleStateChanged for:  ",n);
                if(contextInfo){
                    // console.debug("contextInfo",contextInfo);
                }
                var newValue = contextInfo["schemaValue"];
                if(newValue != undefined){
                    // console.debug("newValue: ",newValue);
                    dijit.byId(xfId).setControlValue(newValue);
//                    dojo.attr(dojo.byId(n.id),"value",contextInfo["value"]);
                    dijit.byId(xfId+"-value").set('value', contextInfo["schemaValue"]);
                    dijit.byId(xfId+"-value").set('displayedValue', new Date(contextInfo["value"]));
                }
            });

            dojo.connect(dateWidget,"onChange",function(evt){
                var dateValue =  dateWidget.serialize(dateWidget.get("value")).substring(0,10);
                //console.debug("onchange on widget",dateValue);
                dijit.byId(xfId).setControlValue(dateValue);
            });
//            dojo.connect(dateWidget,"onBlur",function(){
//                console.debug("onblur on widget",this.displayedValue);
////                dojo.attr(dojo.byId(n.id),"value",this.displayedValue);
//                this.set("value",this.displayValue);
//                console.debug("this.value: ",this.value);
//                console.debug("this.value: ",this.get("value"));
//
//                dijit.byId(xfId).setValue(this.displayedValue);
//            });
        }
    },



    // ############################## DATETIME INPUT ##############################
    // ############################## DATETIME INPUT ##############################
    // ############################## DATETIME INPUT ##############################
    '.xfInput.xsdDateTime .xfValue': function(n) {
        //todo: implement
    }
};

