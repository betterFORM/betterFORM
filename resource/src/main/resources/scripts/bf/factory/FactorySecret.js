define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/dom-attr","bf/util"],
    function(declare,connect,registry, domAttr) {
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
                            // console.debug("FactorySecret: secret input: ",n);
                            xfControlDijit.setCurrentValue(domAttr.get(n,"value"));

                            connect.connect(n,"onkeyup",function(evt){
                                if(xfControlDijit.isIncremental()){
                                    xfControlDijit.sendValue(domAttr.get(n,"value"),false);
                                }
                            });

                            connect.connect(n,"onblur",function(evt){
                                xfControlDijit.sendValue(domAttr.get(n,"value"), true);
                            });

                            connect.connect(node,"onfocus",function(evt){
                                xfControlDijit.handleOnFocus();
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

