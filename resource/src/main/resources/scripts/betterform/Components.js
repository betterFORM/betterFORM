dojo.provide("betterform.Components");
dojo.require("betterform.ui.XFControl");

var componentBehavior = {

    /*
    matching the all elements with .xfControl and instanciate a XFControl instance for each of them.
    Order is important here - all XFControl should be instanciated before their respective widget childs are
    created.
    */
    '.xfControl':{
        found:function(n) {
            console.debug("Control found: ",n);

            var controlId = dojo.attr(n,"id");
            new betterform.ui.XFControl({
                id:controlId,
                controlType:"generic"
            }, n);
        }
    },

    // a default input control (TextField) bound to a string
    '.xsdString .xfValue': {
        found:function(n) {
            console.debug("node: ",n);
            var parentId = n.id.substring(0,n.id.indexOf("-"));

            //connecting widget to XFControl listening for external value changes (coming from FluxProcessor)
            dojo.connect(dijit.byId(parentId), "isValid", function(){
                // ##### setting value by platform/component-specific means #####


                console.debug("self:  ",n);
                console.debug("self:  ",parentId);
                alert("The button was clicked and 'onClick' was called");



            });
        },
        onblur: function (evt){
            console.debug("onblur evt: ",evt);
        }
    }

};


