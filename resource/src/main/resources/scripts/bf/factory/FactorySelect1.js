define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/dom-attr","dojo/dom-class","bf/util"],
    function(declare,connect,registry,domAttr,domClass) {
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
                    var dataObj = bf.util.parseDataAttribute(n,"data-bf-params");
                    var initialValue = dataObj.value;

                    xfControlDijit.setCurrentValue(initialValue);

                    var openselection = dataObj.selection == "open";
                    if(type == "combobox" && openselection){
                        type = "open";
                    }else if(openselection){
                        console.warn("selection = 'open' not support for xf:select with appearance='full'");
                    }


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
                                // trigger xforms-select event by sending DOMActivate to the XFormsProcessor
                                // TODO: Lars: should the factory call the fluxProcessor directly or do we need something else here?
                                var selectedOption = n.options[n.selectedIndex];
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
                        case "open":
                            require(["dijit/form/ComboBox"],function(ComboBox){
                                var comboBox = new ComboBox({
                                    id:n.id,
                                    name:n.name,
                                    autocomplete:true,
                                    onChange: function(value){
                                        console.log("combobox onchange ", value);
                                        var result = this.item ? this.item.value : value;
                                        // console.debug("send result:",result);
                                        var evt = {};
                                        evt.type = "change";
                                        xfControlDijit.sendValue(result,evt);
                                    },
                                    onBlur:function(){
                                        var items = this.store.query({ name: this.get("value") });
                                        // console.debug("item:",items);
                                        var result = items[0] ? items[0].value : this.get("value");
                                        // console.debug("result:",result);
                                        var evt = {};
                                        evt.type = "blur";
                                        xfControlDijit.sendValue(result,evt);
                                    }
                                }, n);

                                // handle initial value
                                var initialItems = comboBox.store.query({ value: initialValue });
                                if(initialItems[0]){
                                    comboBox.set("item",initialItems[0]);
                                } else {
                                    comboBox.set("value",initialValue);
                                }

                                // override xfControl.setValue
                                xfControlDijit.setValue = function(value) {
                                    var items = comboBox.store.query({ value: value });
                                    // console.debug("items:",items);
                                    if(items[0]){
                                        comboBox.set("item",items[0]);
                                    }else {
                                        comboBox.set("value",value);
                                    }
                                };

                                // READONLY HANDLING
                                xfControlDijit.setReadonly = function() {
                                    domClass.replace(n,"xfReadWrite","xfReadOnly");
                                    comboBox.set("disabled",true);
                                };

                                xfControlDijit.setReadwrite=function() {
                                    domClass.replace(n,"xfReadWrite", "xfReadOnly");
                                    comboBox.set("disabled",false);
                                };
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

