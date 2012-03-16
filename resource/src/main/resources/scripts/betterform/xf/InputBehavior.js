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
        // console.debug("string input field: ",n);

        /*
         ###########################################################################################
         xfId is the id of the parent element (the XFControl) that matches the id of the original XForms
         element in the document.
        */
        var xfId = getXfId(n);
        var xfControl = dijit.byId(xfId);

        /*
        ###########################################################################################
        Event handler binding XForms and widget layer.

        There always need to be at least 2 event listener:

        - one listening tor handleStateChanged which are events coming from the processor representing
        changes of the value or state of a node that need to be applied to the widget. The following event handler
        will be triggered by the execution of 'handleStateChanged' in XFControl. This means that all updates on
        XFControl and their representing CSS classes are done. This handler just needs to propagate the change
        to the widget for updating the value.

        - one for listening to the respective change events fired by the widget (usually onBlur or onChange)
        to pass the changed value to the processor
        */

        /*
        if incremental support is needed this eventhandler has to be added for the widget
         */
        dojo.connect(n,"onkeyup",function(evt){
            // console.debug("onkeypress",n);
            xfControl.sendValue(n.value,evt);
        });

        dojo.connect(n,"onblur",function(evt){
            // console.debug("onblur",n);
            xfControl.sendValue(n.value, evt);
        });

        //todo: Dijits will need to create themselves later here...
    },

    // ############################## BOOLEAN INPUT ##############################
    // ############################## BOOLEAN INPUT ##############################
    // ############################## BOOLEAN INPUT ##############################
    '.xfInput.xsdBoolean .xfValue': function(n) {
        // console.debug("boolean input field: ",n);
        var xfId = n.id.substring(0,n.id.lastIndexOf("-"));

        dojo.connect(n,"onblur",function(evt){
//            console.debug("onblur",n);
//            console.debug("xfId",xfId);
//            console.debug("checked",n.checked);
            var xfControl = dijit.byId(xfId);
            if(n.checked != undefined){
                xfControl.sendValue(n.checked,evt);
            }
        });
        dojo.connect(n,"onclick",function(evt){
//            console.debug("onclick",n);
//            console.debug("xfId",xfId);
//            console.debug("value",n.value);
//            console.debug("checked",n.checked);
            var xfControl = dijit.byId(xfId);
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
            // create dijit for datePicker as not widly available yet in browsers
/*
            dojo.require("betterform.xf.input.Date");

            var dateWidget=new betterform.xf.input.Date({
                xfControl  : dijit.byId(getXfId(n)),
                value      : new Date(n.value)
            },n);
*/
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

