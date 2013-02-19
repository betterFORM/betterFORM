define(["dojo/_base/declare","dojo/_base/connect","dojo/query","dijit/registry", "bf/util"],
    function(declare,connect,query,registry) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    var n = query(".xfValue",node)[0];
                    var xfId = n.id.substring(0,n.id.lastIndexOf("-"));
                    var xfControl = registry.byId(xfId);

                    var dataObj = bf.util.parseDataAttribute(n,"data-bf-params");
                    var xfValue = (dataObj.value && dataObj.value != "") ? parseInt(dataObj.value,"10") : 0;
                    var start = 0; var end = 10; var step = 1;


                    var minAttr = dataObj.start;
                    if(minAttr && minAttr != ""){ start = parseInt(minAttr , "10"); }
                    var maxAttr = dataObj.end;
                    if(maxAttr && maxAttr!= ""){
                        end = parseInt(maxAttr , "10");
                    } else if (maxAttr && maxAttr == "" && minAttr && minAttr != "") {
                        end = parseInt(minAttr, "10") + end;
                    }
                    var stepAttr = dataObj.step;
                    if(stepAttr && stepAttr != ""){ step = parseInt(stepAttr, "10"); }
                    if(xfValue > end) {xfValue = end;}
                    if(xfValue < start) {xfValue = start;}
                    var discreteValues = ((end - start) / step) +1;
                    xfControl.setCurrentValue(xfValue);

                    switch(type){
                        case "slider":
                            require(["dojo/dom-attr", "dijit/form/HorizontalSlider","dijit/form/HorizontalRuleLabels","dijit/form/HorizontalRule"],
                                function(domAttr, HorizontalSlider, HorizontalRuleLabels,HorizontalRule){
                                    // console.debug("Found xf:range: node:",n);
                                    // console.debug("createRangeSliderWidget: xfValue:",xfValue);
                                    // create and setup Range Rules
                                    var rulesNode = document.createElement('div');

                                    n.appendChild(rulesNode);
                                    var sliderRules = new HorizontalRule({
                                        count: discreteValues,
                                        container: "topDecoration",
                                        style:"height:4px;"
                                    },rulesNode);
                                    // create and setup Range Labels
                                    var labelNode = document.createElement('div');
                                    n.appendChild(labelNode);

                                    // setup the labels
                                    var sliderLabels = new HorizontalRuleLabels({
                                        count: discreteValues,
                                        style: "height:1.2em;font-size:75%;color:gray;",
                                        labels: [start,end/2,end]
                                    },labelNode);
                                    // Create Slider
                                    var slider = new HorizontalSlider({
                                        value:xfValue,
                                        slideDuration:0,
                                        minimum:start,
                                        maximum:end,
                                        discreteValues:discreteValues,
                                        intermediateChanges:true,
                                        showButtons:true,
                                        style: "width:200px;",
                                        onBlur:function(){
                                            xfControl.sendValue(this.get("value"), true);
                                        },
                                        onChange:function(value){
                                            if(xfControl.isIncremental()){
                                                xfControl.sendValue(value, false);
                                            }
                                        },
                                        onFocus:function(){
                                            xfControl.handleOnFocus();
                                        }

                                    },n);

                                    // and start them both
                                    slider.startup();
                                    sliderRules.startup();
                                    sliderLabels.startup();


                                    xfControl.setValue = function(value){
                                        slider._setValueAttr(value, true);
                                    };

                                    xfControl.setReadonly = function() {
                                        slider.set('readOnly', true);
                                    };
                                    xfControl.setReadwrite = function() {
                                        slider.set('readOnly', false);
                                    };
                                }
                            );




                            break;
                        case "rating":
                            require(["dojox/form/Rating","dojo/dom-attr"],
                                function(Rating){
                                    // console.debug("create Rating node:",n, " value: ",xfValue);
                                    var ratingControl = Rating({
                                        name:n.id,
                                        value:xfValue,
                                        numStars:end
                                    }, n);
                                    xfControl.setValue = function(value){
                                        ratingControl.set("value", value);
                                    };

                                    xfControl.setReadonly = function() {
                                        ratingControl.set('readOnly', true);
                                        ratingControl.set('disabled', true);
                                    };
                                    xfControl.setReadwrite = function() {
                                        ratingControl.set('disabled', false);
                                    };

                                    connect.connect(ratingControl, "set", function(attrName, value) {
                                        // console.debug("ratingControl.set: attrName:",attrName, " value:",value);
                                        if(attrName == "value"){
                                            xfControl.sendValue(value);
                                        }
                                    });

                                }
                            );
                            break;
                        default:
                            console.warn("FactoryTrigger.default");

                    }
                }

            }
        );
    }
);

