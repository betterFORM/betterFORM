define(["dojo/_base/declare","dojo/_base/connect"],
    function(declare,connect) {
        return declare(null, {
            factories:{},

            constructor:function() {
                var widgetFactories = this.factories;

                require(["dojo/behavior","dojo/dom-attr","dojo/_base/array","bf/XFControl","bf/RoleMapping"],
                    function(behavior,domAttr,array){
                        var mappings = bf.RoleMapping.data;
                        array.forEach(mappings, function(mapping){
                            var widgetBehavior = {};
                            var behaviorString = mapping[0];
                            widgetBehavior[behaviorString] = {
                                found: function(n){
                                    var JS_CLASS_NAME = mapping[1];
                                    var param= mapping[2];
                                    console.debug("FOUND: ", n);
                                    console.debug("map to: ", JS_CLASS_NAME, " param: ", param);
                                    require([JS_CLASS_NAME],
                                        function(JS_CLASS_NAME){
                                            // console.debug('found', n);
                                            if(param){
                                                var factory = undefined;
                                                if(widgetFactories[mapping[1]]){
                                                    console.debug("use existing factory: ",mapping[1]);
                                                    factory = widgetFactories[mapping[1]];
                                                }else {
                                                    console.debug("create new factory: ",mapping[1]);
                                                    factory = new JS_CLASS_NAME({},n);
                                                    widgetFactories[mapping[1]] = factory;
                                                }
                                                factory.create(param,n);
                                            }else {
                                                new JS_CLASS_NAME({},n);
                                            }

                                        }
                                    );
                                }
                            };
                                // all <a class="noclick"></a> nodes:
                            // console.debug("add new behavior: ",widgetBehavior);
                            behavior.add(widgetBehavior);
                        });
                });
            }
        });
    }
);

