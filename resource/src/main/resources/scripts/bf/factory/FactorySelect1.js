define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/dom-attr","bf/util"],
    function(declare,connect,registry,domAttr) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    var n = node;
                    var xfId = bf.util.getXfId(n);
                    var xfControlDijit = registry.byId(xfId);
                    // console.debug("FactorySelect1 [type:",type," id:",xfId,"]");

                    switch(type){
                        case "combobox":
                            // console.debug("FactorySelect (minimal/compact) id:",xfId);
                            xfControlDijit.setValue = function(value, schemavalue) {
                                domAttr.set(n, "value", value);
                            };

                            /*
                             if incremental support is needed this eventhandler has to be added for the widget
                             */
                            connect.connect(n,"onchange",function(evt){
                                // console.debug("onchange",n);
                                // get selected option node
                                var selectedOption = n.options[n.selectedIndex];
                                // trigger xforms-select event by sending DOMActivate to the XFormsProcessor
                                // TODO: Lars: should the factory call the fluxProcessor directly or do we need something else here?
                                fluxProcessor.dispatchEventType(xfId, "DOMActivate", domAttr.get(selectedOption,"id"));
                                xfControlDijit.sendValue(n.value,evt);
                            });

                            connect.connect(n,"onblur",function(evt){
                                // console.debug("onblur",n);
                                xfControlDijit.sendValue(n.value, evt);
                            });
                            require(["bf/select/Select1ComboBox"], function(Select1ComboBox) {
                                new Select1ComboBox({id:n.id}, n);
                            });
                            break;
                        case "radiobuttons":
                            // console.debug("FactorySelect (full) id:",xfId);

                            require(["dojo/query", "bf/select/Select1Radio"], function(query, Select1Radio) {
                                query(".xfRadioValue", n).forEach(function(radioValue){
                                    radioValue.onclick = function(evt) {
                                        // console.debug("xfRadioValue.onClick:",radioValue);
                                        var selectedOptionId = bf.util.getXfId(radioValue);
                                        // console.debug("selected option id: ", selectedOptionId);
                                        fluxProcessor.dispatchEventType(xfId, "DOMActivate", selectedOptionId);
                                        xfControlDijit.sendValue(radioValue.value,evt );
                                    }
                                });

                                xfControlDijit.setValue = function(value) {
                                    query(".xfRadioValue", n).forEach(function(radioValue){
                                        if(radioValue.value == value){
                                            domAttr.set(radioValue,"checked", true);
                                        }
                                    });
                                };
                                new Select1Radio({id:n.id,controlId:xfId}, n);
                            });
                            break;
                        default:
                            console.warn("FactorySelect1.default");

                    }
                }

            }
        );
    }
);

