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
                    var xfControlDijit = registry.byId(bf.util.getXfId(node));
                    switch(type){
                        case "htmltextarea":
                            xfControlDijit.setCurrentValue(domAttr.get(node,"value"));

                            xfControlDijit.setValue = function (value) {
                                node.innerHTML = value;
                            };

                            connect.connect(node,"onkeyup",function(evt){
                                //console.debug("onkeypress",node);
                                if(xfControlDijit.isIncremental()){
                                    xfControlDijit.sendValue(node.value,false);
                                }
                            });

                            connect.connect(node,"onblur",function(evt){
                                //console.debug("onblur",node);
                                if(!xfControlDijit.isIncremental()){
                                    xfControlDijit.sendValue(node.value,true);
                                }
                            });

                            connect.connect(node,"onfocus",function(evt){
                                xfControlDijit.handleOnFocus();
                            });
                            break;
                        case "htmleditor":
                            //todo: support incremental behavior - this shouldn't be simple keyup updating but interval-based updating
                            var ckPath = dojo.config.baseUrl + "ckeditor/ckeditor.js";
                            console.debug("ckPath",ckPath);

                            var ckInstance = "CKEDITOR.instances." + node.id;
                            console.debug("CKEditor instance: ", ckInstance);

                            require([ckPath], function(baz) {
                                console.debug("ckeditor loaded for node: ",node.id);
                                CKEDITOR.replace( node.id );

                                xfControlDijit.setValue = function (value) {

                                    n.innerHTML = value;
                                };

                                CKEDITOR.instances['textarea-value'].on('blur',function(evt){
                                    console.debug("onblur",n);
                                    xfControlDijit.sendValue(ckInstance.getData(), evt);
                                });


/*
                                connect.connect(node,"onblur",function(evt){
                                    // console.debug("onblur",node);
                                        xfControlDijit.sendValue(ckInstance.getData(), evt);
                                });
*/
                            });


/*

                            connect.connect(node,"onkeyup",function(evt){
                                // console.debug("onkeypress",node);
                                    xfControlDijit.sendValue(node.value,evt);
                            });

*/
                            break;
                        default:
                            console.warn("no mapping found for Node: ", node);

                    }
                }

            }
        );
    }
);

