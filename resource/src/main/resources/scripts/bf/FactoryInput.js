define(["dojo/_base/declare","dojo/_base/connect"],
    function(declare,connect) {
        return declare(null, {
                create:function(type, node){
                    switch(type){
                        case "plain":
                            console.debug("FactoryInput.createInputPlain");
                            var n = node;
                            require(["dijit/registry", "dojo/dom-attr","bf/util"],
                                function(registry,domAttr){
                                    var xfId = bf.util.getXfId(n);
                                    /*
                                     xfControl is an instance of the XFControl class. This is the generic class that handles all interactions
                                     with the XForms processor implementation. The concrete native browser or javascript controls are called
                                     'widgets' in the context of the client side. They are the concrete representations the user interacts with.
                                     */
                                    var xfControlDijit = registry.byId(xfId);
                                    /* Overwriten "abstract" API function on XFControl to handle updating of control values */
                                    xfControlDijit.setValue = function(value, schemavalue) {
                                        domAttr.set(n, "value", value);
                                    };

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

                                    connect.connect(n,"onkeyup",function(evt){
                                        // console.debug("onkeypress",n);
                                        xfControlDijit.sendValue(n.value,evt);
                                    });

                                    connect.connect(n,"onblur",function(evt){
                                        // console.debug("onblur",n);
                                        xfControlDijit.sendValue(n.value, evt);
                                    });
                                });
                                break;
                        case "boolean":
                            console.debug("FactoryInput.createInputBoolean");
                            break;
                        default:
                            console.debug("FactoryInput.default");

                    }

                }
        });
    }
);

