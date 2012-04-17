define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/query","bf/util"],
    function(declare,connect,registry,query) {
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
                                var value = self.getSelectMinimalValue(n);
                                xfControlDijit.sendValue(value,evt);
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
                                    selectFull.selectFullSendValue(xfControlDijit, n,evt);
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
                            if(selectedValue  == ""){
                                selectedValue = item.value;
                            }else {
                                selectedValue += " " + item.value;
                            }
                        }
                    });
                    return selectedValue;
                }
            }
        );
    }
);

