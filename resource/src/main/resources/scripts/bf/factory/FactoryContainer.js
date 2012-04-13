define(["dojo/_base/declare","bf/util"],
    function(declare) {
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

                        case "repeat":
                            console.debug("FactoryContainer (repeat)");
                            require(["bf/container/Repeat"], function(Repeat) {
                                // repeatId:domAttr.get(n,"repeatId")
                                new Repeat({}, n);
                            });
                            break;
                        default:
                            console.warn("FactoryContainer unknonw type: ",type);
                    }
                }
            }
        )
    }
);

