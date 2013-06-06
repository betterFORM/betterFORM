/**
 * MappingProcessor generates dojo.behaviors of the CSS matchers defined in Mapping.js. Further it calls the the Factory.create
 * function of a given factory and passes in the matched node and the 3rd param of the mapping (a string telling the
 * factory which Widget to create)
 */
define(["dojo/_base/declare"],
    function(declare) {
        return declare(null, {
            factories:{},

            constructor:function() {
                var widgetFactories = this.factories;

                require(["dojo/behavior","dojo/dom-attr","dojo/_base/array","bf/XFControl","bf/Mapping"],
                    function(behavior,domAttr,array){
                        // read all mappings and iterate them
                        var mappings = bf.Mapping.data;
                        array.forEach(mappings, function(mapping){
                            var widgetBehavior = {};
                            var behaviorString = mapping[0];
                            // create dojo.behavior programmatically
                            widgetBehavior[behaviorString] = {
                                found: function(n){
                                    // the 2nd entry of each mapping array specifies which factory to call or which object to
                                    // create
                                    var JS_CLASS_NAME = mapping[1];
                                    // a string telling the factory which widget to create
                                    var param= mapping[2];
                                    // console.debug("FOUND: ", n);
                                    // console.debug("MappingProcessor: map to: ", JS_CLASS_NAME, " param: ", param);
                                    require([JS_CLASS_NAME],
                                        function(JS_CLASS_NAME){
                                            // if a factory is used, a 'param' must be given, otherwise the behavior will
                                            // create for any appearance of the match string a new object (see else)
                                            if(param){
                                                // check if an instance of the factory is already present
                                                var factory = undefined;
                                                if(widgetFactories[mapping[1]]){
                                                    // console.debug("MappingProcessor: use existing factory: ",mapping[1]);
                                                    factory = widgetFactories[mapping[1]];
                                                }else {
                                                    // console.debug("create new factory: ",mapping[1]);
                                                    try {
                                                        factory = new JS_CLASS_NAME({},n);
                                                        widgetFactories[mapping[1]] = factory;
                                                    }catch(err) {
                                                        console.error("MappingProcessor: Could not create factory ",JS_CLASS_NAME, " Mapping: ",mapping);
                                                    }
                                                }
                                                // call the create function of the factory
                                                // console.debug("MappingProcessor: factory.create: ",factory);
                                                factory.create(param,n);
                                            }else {
                                                try {
                                                    new JS_CLASS_NAME({},n);
                                                }catch(err) {
                                                    console.error("Could not create dijit ",JS_CLASS_NAME, " Mapping: ",mapping);
                                                }
                                            }
                                        }
                                    );
                                }
                            };
                            // console.debug("add new behavior: ",widgetBehavior);
                            // add the created behavior to dojo.behavior
                            behavior.add(widgetBehavior);
                        }
                    );
                });
            }
        });
    }
);

