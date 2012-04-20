define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/query","dojo/_base/array", "dojo/dom-attr", "bf/util"],
    function(declare,connect,registry,query,array,domAttr) {
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
                    var initialValue = domAttr.get(n,"data-bf-value");
                    xfControlDijit.setCurrentValue(initialValue);

                    var self = this;
                    var openselection = domAttr.get(n,"selection") == "open";
                    if(type == "listcontrol" && openselection){
                        type = "open";
                    }else if(openselection){
                        console.warn("selection = 'open' not support for xf:select with appearance='full'");
                    }

                    switch(type){
                        case "listcontrol":
                            connect.connect(n,"onchange",function(evt){
                                var value = self._handleOnChangeMinimal(xfId,n);
                                xfControlDijit.sendValue(value, evt);
                            });

                            connect.connect(n,"onblur",function(evt){
                                var value = self.getSelectMinimalValue(n);
                                xfControlDijit.sendValue(value,evt);
                            });

                            xfControlDijit.setValue=function(value) {
                                query(".xfSelectorItem",n).forEach(function(item){
                                    item.selected = value.indexOf(item.value) != -1;
                                });
                            };
                            break;
                        case "checkboxes":
                            require(["bf/select/Select"], function(Select) {
                                var selectFull = new Select({id:n.id,xfControl:xfControlDijit}, n);
                                connect.connect(n,"onchange",function(evt){
                                    var selectedValues  = self.getSelectedFullOptions();
                                    // console.debug("selectedValues:",selectedValues);
                                    var ids = "";
                                    var selectedValue = "";
                                    array.forEach(selectedValues, function(item) {
                                        // concat ids of selected options
                                        var optionId = bf.util.getXfId(item);
                                        ids =  (ids == "") ? optionId : ids + ";" + optionId;
                                        // concat values of selected options
                                        selectedValue = (selectedValue == "") ? item.value : selectedValue + " " + item.value;
                                    });
                                    // console.debug("MultiSelectFull.onChange SelectedItem Ids: ", ids, " value: ", selectedValue);
                                    fluxProcessor.dispatchEventType(xfId, "DOMActivate", ids);
                                    xfControlDijit.sendValue(selectedValue,evt);
                                });


                                xfControlDijit.setValue=function(value) {
                                    query(".xfCheckBoxValue",n).forEach(function(item){
                                        item.checked = value.indexOf(item.value) != -1;
                                    });
                                };

                            });
                            break;
                        case "open":
                            require(["dojo/dom-construct","dojo/dom-class","dijit/form/TextBox","dojo/_base/lang"], function(domConstruct,domClass,TextBox,lang){
                                domClass.add(xfControlDijit.domNode,"xfSelectOpen");
                                var textNode = domConstruct.place("<div>", n, "before");
                                var freeTextDijit = new TextBox({},textNode);
                                var textValue =  self._getOpenSelectValuePartition(initialValue, n);
                                // save textValue as bfValue on dijit for later processing
                                freeTextDijit.set("bfValue", textValue);
                                freeTextDijit.set("value",textValue);

                                dojo.connect(freeTextDijit, "_handleOnChange",function(value){
                                    var bfValue = freeTextDijit.get("bfValue");
                                    freeTextDijit.set("bfValue", value);
                                    var result = lang.trim(self.getSelectMinimalValue(n) + " " + value);
                                    var evt = {};
                                    evt.type = "change";
                                    xfControlDijit.sendValue(result, evt);
                                });
                                dojo.connect(freeTextDijit, "_onBlur" ,function(e){
                                    // console.debug("freeTextDijit._onBlur: ");
                                    var bfValue = freeTextDijit.get("bfValue");
                                    var value = freeTextDijit.get("value")
                                    freeTextDijit.set("bfValue", value);
                                    var result = lang.trim(self.getSelectMinimalValue(n) + " " + value);
                                    var evt = {};
                                    evt.type = "blur";
                                    xfControlDijit.sendValue(result, evt);
                                });

                                // onChange handler for select part
                                connect.connect(n,"onchange",function(evt){
                                    var selectValue = self._handleOnChangeMinimal(xfId,n);
                                    var result = lang.trim(selectValue + " " + freeTextDijit.get("value"));
                                    // console.debug("OpenSelect.onChange: value:",result);
                                    xfControlDijit.sendValue(result, evt);
                                });

                                connect.connect(n,"onblur",function(evt){
                                    var selectValue = self.getSelectMinimalValue(n);
                                    var result = lang.trim(selectValue + " " + freeTextDijit.get("value"));
                                    // console.debug("OpenSelect.onChange: value:",result);
                                    xfControlDijit.sendValue(result, evt);
                                });

                                xfControlDijit.setValue=function(value) {
                                    // console.debug("SelectOpen.setValue:",value);
                                    query(".xfSelectorItem",n).forEach(function(item){
                                        item.selected = value.indexOf(item.value) != -1;
                                    });
                                    var textValue =  self._getOpenSelectValuePartition(value, n);
                                    freeTextDijit.set("bfValue", textValue);
                                    freeTextDijit.set("value",textValue);
                                };
                                // READONLY HANDLING
                                connect.connect(xfControlDijit,"setReadonly",function(evt){
                                    freeTextDijit.set("disabled",true);
                                });
                                connect.connect(xfControlDijit,"setReadwrite",function(evt){
                                    freeTextDijit.set("disabled",false);
                                });
                            });
                            break;
                        default:
                            console.warn("FactorySelect.default");

                    }
                },
                _getOpenSelectValuePartition:function(value, selectNode) {
                    // console.debug("FactorySelect._getOpenSelectValuePartition: check which values in '", value, "' are not present within given select");
                    if(value && value != "" ) {
                        var selectValues = new Array();
                         dojo.query(".xfSelectorItem",selectNode).forEach(function(item){
                             selectValues.push(domAttr.get(item,"value"));
                        });
                        var freeTextTmpValue = new Array();
                        array.forEach(value.split(" "),function(value2analyse){
                            if(array.indexOf(selectValues,value2analyse) == -1){
                                freeTextTmpValue.push(value2analyse);
                            }
                        });
                        return freeTextTmpValue.join(" ");
                    }
                    return "";
                },

                getSelectMinimalValue:function(n) {
                    var selectedValue = "";
                    query(".xfSelectorItem",n).forEach(function(item){
                        if(item.selected){
                            selectedValue  = (selectedValue  == "") ? item.value : selectedValue + " " + item.value;
                        }
                    });
                    return selectedValue;
                },

                _handleOnChangeMinimal:function(selectId, n) {
                    var selectedOptions = new Array();
                    query(".xfSelectorItem",n).forEach(function(item){
                        if(item.selected){
                            selectedOptions.push(item);
                        }
                    });
                    // console.debug("selectedValues:",selectedValues);
                    var ids = "";
                    var selectedValue = "";
                    array.forEach(selectedOptions, function(item) {
                        // concat ids of selected options
                        ids =  (ids == "") ? item.id : ids + ";" + item.id;
                        // concat values of selected options
                        selectedValue = (selectedValue == "") ? item.value : selectedValue + " " + item.value;
                    });
                    // console.debug("MultiSelect.onChange SelectedItem Ids: ", ids, " value: ", selectedValue);
                    // trigger xforms-select event
                    fluxProcessor.dispatchEventType(selectId, "DOMActivate", ids);
                },

                getSelectedFullOptions:function(n) {
                    var selectedOptions = new Array();
                    query(".xfCheckBoxValue",n).forEach(function(item){
                        if(item.checked){
                            selectedOptions.push(item);
                        }
                    });
                    return selectedOptions;
                }
            }
        );
    }
);

