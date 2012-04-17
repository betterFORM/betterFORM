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
                    var xfId = bf.util.getXfId(n);
                    var xfControlDijit = registry.byId(xfId);

                    switch(type){

                        case "password":
                            console.debug("FactorySecret: secret input: ",n);
                            connect.connect(n,"onkeyup",function(evt){
                                xfControlDijit.sendValue(n.value,evt);
                            });

                            connect.connect(n,"onblur",function(evt){
                                xfControlDijit.sendValue(n.value, evt);
                            });
                            break;
                        default:
                            console.warn();
                    }
                }
            }
        )
    }
);

