define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/dom-attr", "bf/util"],
    function(declare,connect,registry,domAttr) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
//                    var xfControlDijit = registry.byId(xfId);
                    require(["dojo/query"],function(query){
                        var n = query("> .xfValue",node)[0];
                        var xfId = bf.util.getXfId(n);

                        switch(type){

                            case "fileUpload":
                            case "base64binary":
                            case "hexBinary":
                                console.warn("FactoryUpload.[file|base64|hex]");
                                require(["bf/upload/Upload"], function(Upload) {
                                    console.debug("upload created bald: ",xfId);
                                    uploadWidget = new Upload({
                                        xfControlId : xfId,
                                        name:domAttr.get(n,'name')
                                    },n);
                                });
                                break;
                            default:
                                console.warn("FactoryUpload.default");
                        }
                    });
                }

            }
        );
    }
);

