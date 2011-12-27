dojo.provide("betterform.Components");
dojo.require("betterform.ui.XFControl");


/*
todo: dependencies must be imported for foreign (non-dojo) components
<script type="text/javascript src="..."/>
<style type="text/css" src="..."/>
*/

/*
    Component Definition File which is the central for mapping XForms controls to client-side controls.
    todo:see newJSLayer-readme.txt
*/
var componentBehavior = {

    /*
     ###########################################################################################
     matching all elements with .xfControl and instanciate a XFControl instance for each of them.
     Order is important here - all XFControl should be instanciated before their respective widget childs are
     created. Thus this rule must be the first in a component definition file like this.
    */
    '.xfControl':function(n) {
        console.debug("XFControl found: ",n);

        var controlId = dojo.attr(n,"id");
        new betterform.ui.XFControl({
            id:controlId,
            controlType:"generic"
        }, n);
    },

    // a default input control (TextField) bound to a string
    '.xfInput.xsdString .xfValue': function(n) {
        console.debug("string input field: ",n);

        /*
         ###########################################################################################
         xfId is the id of the parent element (the XFControl) that matches the id of the original XForms
         element in the document.
        */
        var xfId = n.id.substring(0,n.id.lastIndexOf("-"));

        /*
        ###########################################################################################
        Event handler binding XForms and widget layer.

        There always need to be at least 2 event listener:

        - one listening tor handleStateChanged which are events coming from the processor representing
        changes of the value or state of a node that need to be applied to the widget.

        - one for listening to the respective change events fired by the widget (usually onBlur or onChange)
        to pass the changed value to the processor
        */
        dojo.connect(dijit.byId(xfId), "handleStateChanged", function(contextInfo){
            // ##### setting value by platform/component-specific means #####
            console.debug("handleStateChanged for:  ",n);
            if(contextInfo){
                console.debug("contextInfo",contextInfo);
            }
            //apply value to widget - handle required, valid and readonly if necessary

        });

        dojo.connect(n,"onblur",function(){
            console.debug("onblur",n);
            console.debug("xfId",xfId);
            var xfControl = dijit.byId(xfId);

            //update the XFControl which will handle sending of new (if so) value to server
            xfControl.setControlValue(n.value);
//            fluxProcessor.setControlValue(xfId, n.value);
        });

        //todo: Dijits will need to create themselves later here...
    },
    '.xfInput.xsdBoolean .xfValue': function(n) {
        console.debug("boolean input field: ",n);
        var xfId = n.id.substring(0,n.id.lastIndexOf("-"));
        dojo.connect(dijit.byId(xfId), "handleStateChanged", function(contextInfo){
            console.debug("handleStateChanged for:  ",n);
            if(contextInfo){
                console.debug("contextInfo",contextInfo);
            }
            //apply value to widget - handle required, valid and readonly if necessary

        });

        dojo.connect(n,"onblur",function(){
            console.debug("onblur",n);
            console.debug("xfId",xfId);
            console.debug("checked",n.checked);
            var xfControl = dijit.byId(xfId);
            if(n.checked != undefined){
                xfControl.setControlValue(n.checked);
            }
        });
        dojo.connect(n,"onclick",function(){
            console.debug("onclick",n);
            console.debug("xfId",xfId);
            console.debug("value",n.value);
            console.debug("checked",n.checked);
            var xfControl = dijit.byId(xfId);
            if(n.checked != undefined){
                xfControl.setControlValue(n.checked);
            }
        });

    },


    '.xfTrigger .xfValue': function(n) {
        console.debug("node: ",n);
        var parentId = n.id.substring(0,n.id.lastIndexOf("-"));

        //connecting widget to XFControl listening for external value changes (coming from FluxProcessor)
        dojo.connect(n, "onclick", function(){
            // ##### setting value by platform/component-specific means #####
            console.debug("self:  ",n);
            console.debug("self:  ",parentId);
            fluxProcessor.dispatchEvent(parentId);
        });
        //todo: add onblur res. onchange handler
    }



};


