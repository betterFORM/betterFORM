define(["dojo/_base/declare","dojo/_base/connect"],
    function(declare,connect) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    switch(type){
                        case "plain":
                            var parentId = node.id.substring(0,node.id.lastIndexOf("-"));
                            console.debug("FactoryTrigger (plain) parentId: ", parentId);
                            connect.connect(node, "onclick", function(){
                                fluxProcessor.dispatchEvent(parentId);
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

