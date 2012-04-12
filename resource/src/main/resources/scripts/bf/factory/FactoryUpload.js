define(["dojo/_base/declare","dojo/_base/connect","bf/util"],
    function(declare,connect) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    var xfId = bf.util.getXfId(node);
                    var xfControlDijit = registry.byId(xfId);

                    switch(type){

                        case "anyURI":
                            console.warn("TBD: FactoryUpload (anyURI)",node);
                            break;
                        default:
                            console.warn("FactoryInput.default");

                    }
                }

            }
        );
    }
);

