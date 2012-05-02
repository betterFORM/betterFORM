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
                    var node = node;
                    var xfId = bf.util.getXfId(node);
                    var xfControlDijit = registry.byId(xfId);
                    var dataObj = bf.util.parseDataAttribute(node,"data-bf-params");
                    var initialValue = dataObj.value;
                    xfControlDijit.setCurrentValue(initialValue);

                    var self = this;
                    var openselection = dataObj.selection == "open";
                    if(type == "listcontrol" && openselection){
                        type = "open";
                    }else if(openselection){
                        console.warn("selection = 'open' not support for xf:select with appearance='full'");
                    }

                    switch(type){
                        case "listcontrol":
                            connect.connect(node,"onchange",function(evt){
                                var value = self._handleOnChangeMinimal(xfId,node);
                                xfControlDijit.sendValue(value, evt);
                            });

                            connect.connect(node,"onblur",function(evt){
                                var value = self.getSelectMinimalValue(node);
                                xfControlDijit.sendValue(value,evt);
                            });

                            xfControlDijit.setValue=function(value) {
                                query(".xfSelectorItem",node).forEach(function(item){
                                    item.selected = value.indexOf(item.value) != -1;
                                });
                            };
                            break;
                        case "checkboxes":
                            require(["bf/select/Select"], function(Select) {
                                var selectFull = new Select({id:node.id,xfControl:xfControlDijit}, node);
                                connect.connect(node,"onchange",function(evt){
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
                                    fluxProcessor.dispatchEventType(xfId, "xformsSelect", ids);
                                    xfControlDijit.sendValue(selectedValue,evt);
                                });


                                xfControlDijit.setValue=function(value) {
                                    query(".xfCheckBoxValue",node).forEach(function(item){
                                        item.checked = value.indexOf(item.value) != -1;
                                    });
                                };

                            });
                            break;
                        case "open":
                            require(["dojo/dom-construct","dojo/dom-class","dijit/form/TextBox","dojo/_base/lang"], function(domConstruct,domClass,TextBox,lang){
                                domClass.add(xfControlDijit.domNode,"xfSelectOpen");
                                var textNode = domConstruct.place("<div>", node, "before");
                                var freeTextDijit = new TextBox({},textNode);
                                var textValue =  self._getOpenSelectValuePartition(initialValue, node);
                                // save textValue as bfValue on dijit for later processing
                                freeTextDijit.set("bfValue", textValue);
                                freeTextDijit.set("value",textValue);

                                dojo.connect(freeTextDijit, "onKeyUp",function(event){
                                    //console.debug("freeTextDijit._onKeyUp: event:", event);
                                    var bfValue = freeTextDijit.get("bfValue");
                                    var value = freeTextDijit.get("value");
                                    freeTextDijit.set("bfValue", value);
                                    var result = lang.trim(self.getSelectMinimalValue(node) + " " + value);
                                    //console.debug("OpenSelect.freeTextDijit._handleOnChange: value:",result, " incremental:" , xfControlDijit.isIncremental());
                                    if(xfControlDijit.isIncremental()){
                                        xfControlDijit.sendValue(result,false);
                                    }
                                });
                                dojo.connect(freeTextDijit, "onBlur" ,function(event){
                                    //console.debug("freeTextDijit._onBlur: event:", event);
                                    var bfValue = freeTextDijit.get("bfValue");
                                    var value = freeTextDijit.get("value");
                                    freeTextDijit.set("bfValue", value);
                                    var result = lang.trim(self.getSelectMinimalValue(node) + " " + value);
                                    //console.debug("OpenSelect.freeTextDijit._onblur: value:",result, " incremental:" , xfControlDijit.isIncremental());
                                    if(!xfControlDijit.isIncremental()){
                                        xfControlDijit.sendValue(result,true);
                                    }
                                });

                                // onChange handler for select part
                                connect.connect(node,"onchange",function(evt){
                                    var selectValue = self._handleOnChangeMinimal(xfId,node);
                                    var result = lang.trim(selectValue + " " + freeTextDijit.get("value"));
                                    console.debug("OpenSelect.onChange: value:",result);
                                    if(xfControlDijit.isIncremental()){
                                        xfControlDijit.sendValue(result,false);
                                    }
                                });

                                connect.connect(node,"onblur",function(evt){
                                    var selectValue = self.getSelectMinimalValue(node);
                                    var result = lang.trim(selectValue + " " + freeTextDijit.get("value"));
                                    console.debug("OpenSelect.onblur: value:",result);
                                    if(!xfControlDijit.isIncremental()){
                                        xfControlDijit.sendValue(result,true);
                                    }
                                });

                                connect.connect(node,"onfocus",function(evt){
                                    xfControlDijit.handleOnFocus();
                                });

                                xfControlDijit.setValue=function(value) {
                                    // console.debug("SelectOpen.setValue:",value);
                                    query(".xfSelectorItem",node).forEach(function(item){
                                        item.selected = value.indexOf(item.value) != -1;
                                    });
                                    var textValue =  self._getOpenSelectValuePartition(value, node);
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

                getSelectMinimalValue:function(node) {
                    var selectedValue = "";
                    query(".xfSelectorItem",node).forEach(function(item){
                        if(item.selected){
                            selectedValue  = (selectedValue  == "") ? item.value : selectedValue + " " + item.value;
                        }
                    });
                    return selectedValue;
                },

                _handleOnChangeMinimal:function(selectId, node) {
                    var selectedOptions = new Array();
                    query(".xfSelectorItem",node).forEach(function(item){
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
                    fluxProcessor.dispatchEventType(selectId, "xformsSelect", ids);
                    return selectedValue;
                },

                getSelectedFullOptions:function(node) {
                    var selectedOptions = new Array();
                    query(".xfCheckBoxValue",node).forEach(function(item){
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

