dojo.provide("betterform.Components");
dojo.require("betterform.ui.XFControl");


/*
todo: dependencies must be imported for foreign (non-dojo) components
<script type="text/javascript src="..."/>
<style type="text/css" src="..."/>
*/

/*
    todo:see newJSLayer-readme.txt
*/
var componentBehavior = {

    /*
    matching the all elements with .xfControl and instanciate a XFControl instance for each of them.
    Order is important here - all XFControl should be instanciated before their respective widget childs are
    created.
    */
    '.xfControl':function(n) {
        console.debug("Control found: ",n);

        var controlId = dojo.attr(n,"id");
        new betterform.ui.XFControl({
            id:controlId,
            controlType:"generic"
        }, n);
    },

    // a default input control (TextField) bound to a string
    '.xfInput .xfValue': function(n) {
        console.debug("node: ",n);
        var xfId = n.id.substring(0,n.id.lastIndexOf("-"));

        //connecting widget to XFControl listening for external value changes (coming from FluxProcessor)
        dojo.connect(dijit.byId(xfId), "handleStateChanged", function(){
            // ##### setting value by platform/component-specific means #####
            console.debug("self:  ",n);
            console.debug("self:  ",xfId);
            if(contextInfo){
                console.debug("handleStateChanged",contextInfo);
            }
        });
        //todo: add onblur res. onchange handler
        dojo.connect(n,"onblur",function(){
            console.debug("onblur",n);
            fluxProcessor.setControlValue(xfId, n.value);
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


