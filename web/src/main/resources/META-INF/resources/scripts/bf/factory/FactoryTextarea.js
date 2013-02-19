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
                    var xfControlDijit = registry.byId(bf.util.getXfId(node));
                    switch(type){
                        case "texteditor":
                            console.debug("FactoryTextarea (texteditor)");
                            xfControlDijit.setCurrentValue(node.value);

                            xfControlDijit.setValue = function (value) {
                                // console.debug("textarea xfControlDijit: value:",value);
                                node.value   = value;
                            };

                            connect.connect(node,"onkeyup",function(evt){
                                //console.debug("onkeypress",node);
                                if(xfControlDijit.isIncremental()){
                                    xfControlDijit.sendValue(node.value,false);
                                }
                            });

                            connect.connect(node,"onblur",function(evt){
                                //console.debug("onblur",node);
                                xfControlDijit.sendValue(node.value,true);
                            });

                            connect.connect(node,"onfocus",function(evt){
                                xfControlDijit.handleOnFocus();
                            });
                            break;
                        case "htmleditor":
                            //todo: support incremental behavior - this shouldn't be simple keyup updating but interval-based updating
                            var ckPath = dojo.config.baseUrl + "ckeditor/ckeditor.js";

                            require(["dojo/dom-class",ckPath], function(domClass) {
                                // console.debug("ckPath",ckPath, " CKEDITOR:",CKEDITOR);
                                // CKEDITOR.config.scayt_autoStartup = false;
                                // console.debug("load ckeditor for node: ",node.id);

                                CKEDITOR.replace(node.id);
                                var ckInstance = CKEDITOR.instances[node.id];
                                // console.debug("CKEditor instance: ", ckInstance);

                                ckInstance.on('contentDom', function(){
                                    xfControlDijit.setCurrentValue(ckInstance.getData());


                                    xfControlDijit.setValue = function (value) {
                                        ckInstance.setData(value);
                                    };

                                    ckInstance.on('blur',function(evt){
                                        xfControlDijit.sendValue(ckInstance.getData(), true);
                                    });

                                    ckInstance.on('focus',function(){
                                        xfControlDijit.handleOnFocus();
                                    });


                                    ckInstance.document.on( 'keyup', function(evt){
                                        // console.debug("ckInstance change value:",ckInstance.getData());
                                        // Do not capture CTRL hotkeys.
                                        if ( !evt.data.$.ctrlKey && !evt.data.$.metaKey){
                                            if(xfControlDijit.isIncremental()){
                                                xfControlDijit.sendValue(ckInstance.getData(),false);
                                            }

                                        }
                                    });

                                    xfControlDijit.setReadonly = function(){
                                        ckInstance.setReadOnly(true);
                                        domClass.replace(node,"xfReadOnly","xfReadWrite");
                                    };

                                    xfControlDijit.setReadwrite = function(){
                                        ckInstance.setReadOnly(false);
                                        domClass.replace(node,"xfReadWrite", "xfReadOnly");
                                    };


                                });

                                /*



                                connect.connect(node,"onkeyup",function(evt){
                                // console.debug("onkeypress",node);
                                xfControlDijit.sendValue(node.value,false);
                                });

                                */

                            });


                            break;
                        default:
                            console.warn("no mapping found for Node: ", node);

                    }
                }

            }
        );
    }
);

