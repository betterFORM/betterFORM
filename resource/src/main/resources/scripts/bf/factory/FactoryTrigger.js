define(["dojo/_base/declare","dojo/_base/connect"],
    function(declare,connect) {
        return declare(null,
            {
                /**
                 * This rule matches plain standard <input type="button", a trigger rendered with image and
                 * a trigger represented as a link.
                 *
                 * @param type
                 * @param node the node to map to a concrete widget
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

