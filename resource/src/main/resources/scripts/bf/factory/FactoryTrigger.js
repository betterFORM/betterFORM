define(["dojo/_base/declare","dojo/_base/connect","dijit/registry"],
    function(declare,connect,registry) {
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
                    var parentId = node.id.substring(0,node.id.lastIndexOf("-"));
                    connect.connect(node, "onclick", function(evt){
                        // console.debug("FactoryTrigger: node: ", node, " onclick function. Dispatch Event to: ", parentId, " evt: ", evt);
                        fluxProcessor.dispatchEvent(parentId);
                    });
                    var xfId = bf.util.getXfId(node);
                    var xfControlDijit = registry.byId(xfId);

                    switch(type){
                        case "link":
                            xfControlDijit.setLabel = function(value) {
                                node.innerHTML = value;
                            };
                            break;
                        case "button":
                            xfControlDijit.setLabel = function(value) {
                                node.value = value;
                            };
                            break;
                        default:
                            console.warn("FactoryTrigger unknown type: ",type);

                    }
                }

            }
        );
    }
);

