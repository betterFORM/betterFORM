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
                    var xfControl = registry.byId(bf.util.getXfId(n));
                    switch(type){
                        case "htmltextarea":
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
                        case "htmleditor":
                            //todo: support incremental behavior - this shouldn't be simple keyup updating but interval-based updating
                            var ckPath = dojo.config.baseUrl + "ckeditor/ckeditor.js";
                            console.debug("ckPath",ckPath);

                            var ckInstance = "CKEDITOR.instances." + n.id;
                            console.debug("CKEditor instance: ", ckInstance);

                            require([ckPath], function(baz) {
                                console.debug("ckeditor loaded for node: ",n.id);
                                CKEDITOR.replace( n.id );

                                xfControl.setValue = function (value) {

                                    n.innerHTML = value;
                                };

                                CKEDITOR.instances['textarea-value'].on('blur',function(evt){
                                    console.debug("onblur",n);
                                    xfControl.sendValue(ckInstance.getData(), evt);
                                });


/*
                                connect.connect(n,"onblur",function(evt){
                                    // console.debug("onblur",n);
                                    xfControl.sendValue(ckInstance.getData(), evt);
                                });
*/
                            });


/*

                            connect.connect(n,"onkeyup",function(evt){
                                // console.debug("onkeypress",n);
                                xfControl.sendValue(n.value,evt);
                            });

*/
                            break;
                        default:
                            console.warn("no mapping found for Node: ", n);

                    }
                }

            }
        );
    }
);

