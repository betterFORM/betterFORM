
            function documentMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "model",
                            onClick:function() {
                                addElement('model');
                            }
                        }));
                    
                    var pContainerMenu = new dijit.Menu();
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "group",
                            onClick:function() {
                                addElement('group');
                            }
                        }));
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "repeat",
                            onClick:function() {
                                addElement('repeat');
                            }
                        }));
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "switch",
                            onClick:function() {
                                addElement('switch');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "container",
                        popup: pContainerMenu
                    }));

                
                pMenu.startup();
            }
            function modelMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "instance",
                            onClick:function() {
                                addElement('instance');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "schema",
                            onClick:function() {
                                addElement('schema');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "submission",
                            onClick:function() {
                                addElement('submission');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "bind",
                            onClick:function() {
                                addElement('bind');
                            }
                        }));
                    
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function instanceMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function bindMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "bind",
                            onClick:function() {
                                addElement('bind');
                            }
                        }));
                    
                pMenu.startup();
            }
            function extensionMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function labelMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                pMenu.startup();
            }
            function hintMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                pMenu.startup();
            }
            function helpMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                pMenu.startup();
            }
            function alertMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                pMenu.startup();
            }
            function itemMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "value",
                            onClick:function() {
                                addElement('value');
                            }
                        }));
                    
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function itemsetMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "value",
                            onClick:function() {
                                addElement('value');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "copy",
                            onClick:function() {
                                addElement('copy');
                            }
                        }));
                    
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function choicesMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "choices",
                            onClick:function() {
                                addElement('choices');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "item",
                            onClick:function() {
                                addElement('item');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "itemset",
                            onClick:function() {
                                addElement('itemset');
                            }
                        }));
                    
                pMenu.startup();
            }
            function valueMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function copyMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function inputMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function secretMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function textareaMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function uploadMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function filenameMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function mediatypeMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function select1Menu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "item",
                            onClick:function() {
                                addElement('item');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "itemset",
                            onClick:function() {
                                addElement('itemset');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "choices",
                            onClick:function() {
                                addElement('choices');
                            }
                        }));
                    
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function selectMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "message" ,
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "item",
                            onClick:function() {
                                addElement('item');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "itemset",
                            onClick:function() {
                                addElement('itemset');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "choices",
                            onClick:function() {
                                addElement('choices');
                            }
                        }));
                    
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function rangeMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function triggerMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function outputMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function submitMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function repeatMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "input",
                            onClick:function() {
                                addElement('input');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "secret",
                            onClick:function() {
                                addElement('secret');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "textarea",
                            onClick:function() {
                                addElement('textarea');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "upload",
                            onClick:function() {
                                addElement('upload');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select1",
                            onClick:function() {
                                addElement('select1');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select",
                            onClick:function() {
                                addElement('select');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "range",
                            onClick:function() {
                                addElement('range');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "submit",
                            onClick:function() {
                                addElement('submit');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select1",
                            onClick:function() {
                                addElement('select1');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                    var pContainerMenu = new dijit.Menu();
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "group",
                            onClick:function() {
                                addElement('group');
                            }
                        }));
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "switch",
                            onClick:function() {
                                addElement('switch');
                            }
                        }));
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "repeat",
                            onClick:function() {
                                addElement('repeat');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "container",
                        popup: pContainerMenu
                    }));

                
                pMenu.startup();
            }
            function groupMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "hint" ,
                            onClick:function() {
                                addElement('hint');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "help" ,
                            onClick:function() {
                                addElement('help');
                            }
                        }));
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "alert" ,
                            onClick:function() {
                                addElement('alert');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "input",
                            onClick:function() {
                                addElement('input');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "secret",
                            onClick:function() {
                                addElement('secret');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "textarea",
                            onClick:function() {
                                addElement('textarea');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "upload",
                            onClick:function() {
                                addElement('upload');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select1",
                            onClick:function() {
                                addElement('select1');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select",
                            onClick:function() {
                                addElement('select');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "range",
                            onClick:function() {
                                addElement('range');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "submit",
                            onClick:function() {
                                addElement('submit');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select1",
                            onClick:function() {
                                addElement('select1');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                    var pContainerMenu = new dijit.Menu();
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "group",
                            onClick:function() {
                                addElement('group');
                            }
                        }));
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "switch",
                            onClick:function() {
                                addElement('switch');
                            }
                        }));
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "repeat",
                            onClick:function() {
                                addElement('repeat');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "container",
                        popup: pContainerMenu
                    }));

                
                pMenu.startup();
            }
            function switchMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "input",
                            onClick:function() {
                                addElement('input');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "secret",
                            onClick:function() {
                                addElement('secret');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "textarea",
                            onClick:function() {
                                addElement('textarea');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "upload",
                            onClick:function() {
                                addElement('upload');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select1",
                            onClick:function() {
                                addElement('select1');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select",
                            onClick:function() {
                                addElement('select');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "range",
                            onClick:function() {
                                addElement('range');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "submit",
                            onClick:function() {
                                addElement('submit');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select1",
                            onClick:function() {
                                addElement('select1');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                    var pContainerMenu = new dijit.Menu();
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "case",
                            onClick:function() {
                                addElement('case');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "container",
                        popup: pContainerMenu
                    }));

                
                pMenu.startup();
            }
            function caseMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pCommonMenu = new dijit.Menu();
                    
                        pCommonMenu.addChild(new dijit.MenuItem({
                            label: "label" ,
                            onClick:function() {
                                addElement('label');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "common",
                        popup: pCommonMenu
                    }));
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "input",
                            onClick:function() {
                                addElement('input');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "secret",
                            onClick:function() {
                                addElement('secret');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "textarea",
                            onClick:function() {
                                addElement('textarea');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "upload",
                            onClick:function() {
                                addElement('upload');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select1",
                            onClick:function() {
                                addElement('select1');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select",
                            onClick:function() {
                                addElement('select');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "range",
                            onClick:function() {
                                addElement('range');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "submit",
                            onClick:function() {
                                addElement('submit');
                            }
                        }));
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "select1",
                            onClick:function() {
                                addElement('select1');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                    var pContainerMenu = new dijit.Menu();
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "group",
                            onClick:function() {
                                addElement('group');
                            }
                        }));
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "switch",
                            onClick:function() {
                                addElement('switch');
                            }
                        }));
                    
                        pContainerMenu.addChild(new dijit.MenuItem({
                            label: "repeat",
                            onClick:function() {
                                addElement('repeat');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "container",
                        popup: pContainerMenu
                    }));

                
                pMenu.startup();
            }
            function actionMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pActionMenu = new dijit.Menu();
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "action",
                            onClick:function() {
                                addElement('action');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setvalue",
                            onClick:function() {
                                addElement('setvalue');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "insert",
                            onClick:function() {
                                addElement('insert');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "delete",
                            onClick:function() {
                                addElement('delete');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "message",
                            onClick:function() {
                                addElement('message');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "send",
                            onClick:function() {
                                addElement('send');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "load",
                            onClick:function() {
                                addElement('load');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "dispatch",
                            onClick:function() {
                                addElement('dispatch');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "toggle",
                            onClick:function() {
                                addElement('toggle');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "setfocus",
                            onClick:function() {
                                addElement('setfocus');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "rebuild",
                            onClick:function() {
                                addElement('rebuild');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "recalculate",
                            onClick:function() {
                                addElement('recalculate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "revalidate",
                            onClick:function() {
                                addElement('revalidate');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "refresh",
                            onClick:function() {
                                addElement('refresh');
                            }
                        }));
                    
                        pActionMenu.addChild(new dijit.MenuItem({
                            label: "reset",
                            onClick:function() {
                                addElement('reset');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "action",
                        popup: pActionMenu
                    }));

                
                pMenu.startup();
            }
            function setvalueMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function insertMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function deleteMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function setindexMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function toggleMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function setfocusMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function controlMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function dispatchMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function nameMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function targetidMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function targetMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function delayMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function rebuildMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function recalculateMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function revalidateMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function refreshMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function resetMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function loadMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function resourceMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function sendMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function messageMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                    var pControlsMenu = new dijit.Menu();
                    
                        pControlsMenu.addChild(new dijit.MenuItem({
                            label: "output",
                            onClick:function() {
                                addElement('output');
                            }
                        }));
                    
                    pMenu.addChild(new dijit.PopupMenuItem({
                        label: "controls",
                        popup: pControlsMenu
                    }));

                
                pMenu.startup();
            }
            function submissionMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                        pMenu.addChild(new dijit.MenuItem({
                            label: "resource",
                            onClick:function() {
                                addElement('resource');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "method",
                            onClick:function() {
                                addElement('method');
                            }
                        }));
                    
                        pMenu.addChild(new dijit.MenuItem({
                            label: "header",
                            onClick:function() {
                                addElement('header');
                            }
                        }));
                    
                pMenu.startup();
            }
            function methodMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }
            function headerMenu(targetId){
                var pMenu = new dijit.Menu({
                        targetNodeIds: [targetId] ,
                        leftClickToOpen: true
                });
                
                pMenu.startup();
            }