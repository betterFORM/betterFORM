define(["dojo/_base/declare","bf/util"],
    function(declare) {
        return declare(null,
            {
                /**
                 * Function to create Container Classes / Controls like Group, Switch, Repeat
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    var n = node;
                    switch(type){
                        case "group":
                            // console.debug("FactoryContainer (group)");
                            require(["dojo/dom","dojo/dom-attr","dojo/_base/connect","bf/XFBinding"], function(dom,domAttr,connect,XFBinding) {
                                // repeatId:domAttr.get(n,"repeatId")
                                var group = new XFBinding({}, n);
//                                connect.subscribe("bf-state-change-"+ group.id, group, "handleStateChanged");
                                group.setLabel = function( value) {
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
                                new Repeat({}, n);
                            });
                            break;

                        case "switch":
                            console.debug("FactoryContainer (switch) n: ",n);
                            require(["dojo/dom-class","dojo/_base/connect"], function(domClass,connect) {
                                connect.subscribe("bf-switch-toggled-"+ n.id, function(contextInfo) {
                                    // console.debug("FactoryContain (switch) bf-switch-toggled contextInfo:",contextInfo);
                                    if(contextInfo.deselected != undefined) {
                                        domClass.replace(contextInfo.deselected, "xfDeselectedCase", "xfSelectedCase");
                                    }
                                    if(contextInfo.selected){
                                        domClass.replace(contextInfo.selected, "xfSelectedCase", "xfDeselectedCase");
                                    }
                                });
                            });
                            break;
                        case "tabswitch":
                            console.debug("FactoryContainer (tabswitch) n: ",n);
                            require(["dijit/layout/ContentPane","dijit/layout/TabContainer","dojo/query","dojo/aspect","dojo/_base/array","dojo/dom","dojo/dom-attr","dojo/_base/connect"], function(ContentPane, TabContainer, query,aspect,array,dom,domAttr,connect) {
                                // connect and overwrite 'handleStateChanged' since it is not supported by switch
                                var xfCases = query(".xfCase",n);
                                var tabContainer = new TabContainer({id:n.id, tabPosition: "top", "class": "bfTabContainer"},n);
                                // add xfCases to TabContainer
                                array.forEach(xfCases, function(xfCase) {
                                    tabContainer.addChild( new ContentPane({title:xfCase.title},xfCase) );
                                });
                                tabContainer.startup();

                                // save and overwrite tabContainer.selectChild function, selectChild is executed in response to
                                // bf-switch-toggled-[this-id];
                                var originalSelectChild = tabContainer["selectChild"];
                                tabContainer["selectChild"] = function(page,animated) {
                                    var btnToActivate = "t-" + domAttr.get(dom.byId(page.id),"caseid");
                                    fluxProcessor.dispatchEvent(btnToActivate);
                                };
                                connect.subscribe("bf-switch-toggled-"+ n.id, function(contextInfo) {
                                    var selectedCase = query("*[caseid='"+ contextInfo.selected + "']",tabContainer.domNode)[0];
                                    originalSelectChild.apply(tabContainer,[selectedCase.id,null]);
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

