define(["dojo/_base/declare","bf/util"],
    function(declare) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    var n = node;
                    switch(type){
                        case "group":
                            console.debug("TBD: FactoryContainer (group)");
                            require(["bf/container/Container","dojo/dom","dojo/dom-attr","dojo/_base/connect"], function(Container,dom,domAttr,connect) {
                                // repeatId:domAttr.get(n,"repeatId")
                                var group = new Container({}, n);

                                connect.subscribe("bf-state-change-"+ group.id, group, "handleStateChanged");

                                group._setLabel = function( value) {
                                    // console.debug("FactoryContainer (group) _setLabel: ",this.id);
                                    var targetId = this.id;
                                    var labelNode = dom.byId(targetId + "-label");
                                    // labelledBy is an alertnative way to find the corresponding label.
                                    // Compact repeats only have this at the moment
                                    if (labelNode == undefined && domAttr.get(this.domNode, "labelledBy") != undefined) {
                                        labelNode = dom.byId(domAttr.get(this.domNode, "labelledBy"));
                                    }
                                    if (labelNode != undefined && value != undefined) {
                                        labelNode.innerHTML = value;
                                        labelNode.title = value;
                                    }
                                }
                            });
                            break;

                        case "repeat":
                            console.debug("FactoryContainer (repeat)");
                            require(["bf/container/Repeat"], function(Repeat) {
                                // repeatId:domAttr.get(n,"repeatId")
                                new Repeat({}, n);
                            });
                            break;
                        case "switch":
                            console.debug("FactoryContainer (switch) n: ",n);
                            require(["dojo/dom-class","dojo/_base/connect"], function(domClass,connect) {
                                // connect and overwrite 'handleStateChanged' since it is not supported by switch
                                connect.subscribe("bf-state-change-"+ n.id, function(contextInfo) {
                                    console.debug("FactoryContain (switch) handleStateChanged: nothing to do here");
                                });

                                connect.subscribe("bf-switch-toggled-"+ n.id, function(contextInfo) {
                                    console.debug("FactoryContain (switch) bf-switch-toggled contextInfo:",contextInfo);
                                    if(contextInfo.deselected != undefined) {
                                        domClass.replace(contextInfo.deselected, "xfDeselectedCase", "xfSelectedCase");
                                    }
                                    if(contextInfo.selected){
                                        // console.debug("betterform.ui.container.Switch.toggleCase: Case to select:",caseToSelect);
                                        domClass.replace(contextInfo.selected, "xfSelectedCase", "xfDeselectedCase");
                                    }
                                });
                            });
                            break;
                        default:
                            console.warn("FactoryContainer unknonw type: ",type);
                    }
                }
            }
        )
    }
);

