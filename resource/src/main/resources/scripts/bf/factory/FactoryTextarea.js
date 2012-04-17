define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","bf/util"],
    function(declare,connect,registry) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    var n = node;
                    switch(type){
                        case "htmleditor":
                            var xfControl = registry.byId(bf.util.getXfId(n));

                            xfControl.setValue = function (value) {
                                n.innerHTML = value;
                            };

                            connect.connect(n,"onkeyup",function(evt){
                                // console.debug("onkeypress",n);
                                xfControl.sendValue(n.value,evt);
                            });

                            connect.connect(n,"onblur",function(evt){
                                // console.debug("onblur",n);
                                xfControl.sendValue(n.value, evt);
                            });
                            break;
                        default:
                            console.warn("FactoryTrigger.default");

                    }
                }

            }
        );
    }
);

