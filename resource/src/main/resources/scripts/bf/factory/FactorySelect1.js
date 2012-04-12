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

                    switch(type){
                        case "minimal":
                        case "compact":
                            console.debug("FactorySelect (minimal/compact) id:",xfId);
                            xfControlDijit.setValue = function(value, schemavalue) {
                                domAttr.set(n, "value", value);
                            };

                            /*
                             if incremental support is needed this eventhandler has to be added for the widget
                             */
                            connect.connect(n,"onchange",function(evt){
                                // console.debug("onchange",n);
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
                        case "full":
                            require(["dojo/query", "bf/select/Select1Radio"], function(query, Select1Radio) {
                                query(".xfRadioValue", n).forEach(function(radioValue){
                                    radioValue.onclick = function(evt) {
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
                            console.warn("FactorySelect.default");

                    }
                }

            }
        );
    }
);

