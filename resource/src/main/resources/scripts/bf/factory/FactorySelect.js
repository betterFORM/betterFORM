define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/query","dojo/_base/array", "bf/util"],
    function(declare,connect,registry,query,array) {
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
                    var self = this;
                    switch(type){
                        case "listcontrol":
                            connect.connect(n,"onchange",function(evt){
                                var selectedValues  = self.getSelectedMinimalOptions(n);
                                // console.debug("selectedValues:",selectedValues);
                                var ids = "";
                                var selectedValue = "";
                                array.forEach(selectedValues, function(item) {
                                    // concat ids of selected options
                                    ids =  (ids == "") ? item.id : ids + ";" + item.id;
                                    // concat values of selected options
                                    selectedValue = (selectedValue == "") ? item.value : selectedValue + " " + item.value;
                                });

                                // console.debug("MultiSelect.onChange SelectedItem Ids: ", ids, " value: ", selectedValue);
                                fluxProcessor.dispatchEventType(xfId, "DOMActivate", ids);
                                xfControlDijit.sendValue(selectedValue,evt);
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
                        default:
                            console.warn("FactorySelect.default");

                    }
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

                getSelectedMinimalOptions:function(n) {
                    var selectedOptions = new Array();
                    query(".xfSelectorItem",n).forEach(function(item){
                        if(item.selected){
                            selectedOptions.push(item);
                        }
                    });
                    return selectedOptions;
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

