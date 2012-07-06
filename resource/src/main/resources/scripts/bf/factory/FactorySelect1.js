define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/dom-attr","dojo/dom-class","dojo/query","bf/util"],
    function(declare,connect,registry,domAttr,domClass,query) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    var n = query(".xfValue",node)[0];
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
                            require(["bf/select/Select1ComboBox"], function(Select1ComboBox) {
                                // console.debug("FactorySelect (minimal/compact) id:",xfId);
                                var select1Widget = new Select1ComboBox({id:n.id,value:initialValue}, n);

                                xfControlDijit.setValue = function(value, schemavalue) {
                                    domAttr.set(n, "value", value);
                                };

                                connect.connect(n,"onchange",function(evt){
                                    // console.debug("FactorySelect1 comboBox: onchange",n);
                                    // trigger xforms-select event by sending DOMActivate to the XFormsProcessor
                                    // TODO: Lars: should the factory call the fluxProcessor directly or do we need something else here?
                                    var selectedOption = n.options[n.selectedIndex];
                                    fluxProcessor.dispatchEventType(xfId, "xformsSelect", domAttr.get(selectedOption,"id"));
                                    if(xfControlDijit.isIncremental()){
                                        xfControlDijit.sendValue(n.value,false);
                                    }

                                });

                                connect.connect(n,"onblur",function(evt){
                                    xfControlDijit.sendValue(n.value, true);
                                });
                                connect.connect(n,"onfocus",function(evt){
                                    xfControlDijit.handleOnFocus();
                                });
                            });
                            break;
                        case "radiobuttons":
                            // console.debug("FactorySelect (full) id:",xfId);

                            require(["dojo/query", "bf/select/Select1Radio"], function(query, Select1Radio) {

                                var select1RadioWidget = new Select1Radio({id:n.id,controlId:xfId}, n);

                                query(".xfRadioValue", n).forEach(function(radioValue){
                                    radioValue.onclick = function(evt) {
                                        // console.debug("xfRadioValue.onClick:",radioValue);
                                        var selectedOptionId = bf.util.getXfId(radioValue);
                                        // console.debug("selected option id: ", selectedOptionId);
                                        fluxProcessor.dispatchEventType(xfId, "xformsSelect", selectedOptionId);
                                        if(xfControlDijit.isIncremental()){
                                            xfControlDijit.sendValue(radioValue.value,false );
                                        }
                                    }
                                });

                                xfControlDijit.setValue = function(value) {
                                    query(".xfRadioValue", n).forEach(function(radioValue){
                                        if(radioValue.value == value){
                                            domAttr.set(radioValue,"checked", true);
                                        }
                                    });
                                };
                                connect.connect(select1RadioWidget, "onFocus",function() {
                                    xfControlDijit.handleOnFocus();
                                });
                            });
                            break;
                        case "open":
                            require(["dijit/form/ComboBox"],function(ComboBox){
                                var comboBox = new ComboBox({
                                    id:n.id,
                                    name:n.name,
                                    className:"xfValue",
                                    autocomplete:true,
                                    onChange: function(value){
                                        // console.log("combobox onchange ", value);
                                        var result = this.item ? this.item.value : value;
                                        // console.debug("send result:",result);
                                        xfControlDijit.sendValue(result,false);
                                    },
                                    onBlur:function(){
                                        var items = this.store.query({ name: this.get("value") });
                                        // console.debug("item:",items);
                                        var result = items[0] ? items[0].value : this.get("value");
                                        // console.debug("result:",result);
                                        xfControlDijit.sendValue(result,true);
                                    },
                                    onFocus:function(){
                                        xfControlDijit.handleOnFocus();
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

                                connect.subscribe("xforms-item-changed-" + n.id , function(contextInfo){
                                    console.warn("TBD: FactorySelect1 (open) xforms-item-changed contextInfo:",contextInfo)
                                });
                                connect.subscribe("betterform-insert-item-" + n.id , function(contextInfo){
                                    console.warn("TBD: FactorySelect1 (open) betterform-insert-item contextInfo:",contextInfo)
                                });
                                connect.subscribe("betterform-delte-item-" + n.id , function(contextInfo){
                                    console.warn("TBD: FactorySelect1 (open) betterform-delete-item contextInfo:",contextInfo)
                                });


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

