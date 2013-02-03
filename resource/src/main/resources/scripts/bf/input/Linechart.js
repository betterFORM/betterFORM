define(["dojo/_base/declare","dojo/dom","dojo/dom-attr","dojo/dom-construct", "dojo/dom-style","jquery","jplot"],
    function(declare, dom, domAttr, domConstruct, domStyle,$,jplot){
        return declare(null, {
            linechartNode:null,
            timeline:null,

            constructor:function() {
                // console.debug("Linechart.constructor: this: ", this);
                this.srcNodeRef = arguments[1];
                var dataSource = domAttr.get(this.srcNodeRef, "data-bf");
                var origId = domAttr.get(this.srcNodeRef, "id");
                this.id = origId.substring(0,origId.length -5) + "tlcontainer";
                // console.debug("Timeline: dataSource:", this.dataSource, " origId: ",origId);

                this.linechartNode = domConstruct.toDom("<div class='xfLinechart' id='"+ this.id+"'></div>");
                domConstruct.place(this.linechartNode, origId, "after");
                domStyle.set(this.srcNodeRef, "display", "none");

                self = this;
                require(['dojo/_base/xhr'], function(xhr){
                    xhr.get({
                        url:dataSource, handleAs:"json",
                        load: function(data){
                            console.debug("data: ",data);
                            self.initLinechart(data)
                        }
                    });
                });
            },

            initLinechart:function(linechartData) {
                var data = linechartData;
                console.debug("initializing LineChart");
                var targetId = this.id;
                require(["dateAxisRenderer","cursor", "highlighter","dojo/ready"], function(dateAxisRenderer,cursor, highlighter,ready){
                    ready(function(){
                        $.jqplot.config.enablePlugins = true;
                        // console.debug("$.jqplot.config.enablePlugins: ",$.jqplot.config.enablePlugins);
                        // For these examples, don't show the to image button.
                        $.jqplot._noToImageButton = true;
                        // console.debug("$.jqplot._noToImageButton : ",$.jqplot._noToImageButton );

                        // console.debug("targetId: ", targetId);                        
                        var opts = {
                            title: 'German Books of Law / Year',
                            axes: {
                                xaxis: {
                                    renderer: $.jqplot.DateAxisRenderer,
                                    label: 'Years',
                                    min:'1/1/1869',
                                    max:'1/1/2013',
                                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                                    tickOptions: {
                                        labelPosition: 'middle',
                                        angle: -30,
                                        formatString:"%Y"
                                    },
                                    tickInterval: "10 years"
                                },
                                yaxis: {
                                    label: 'Count',
                                    min:0,
                                    labelRenderer: $.jqplot.CanvasAxisLabelRenderer,
                                    tickRenderer: $.jqplot.CanvasAxisTickRenderer,
                                    tickOptions: {
                                        // labelPosition: 'middle',
                                        angle:-30
                                    }
                                }
                            },
                            series:[{lineWidth:4, color:"red", shadowDepth:5, markerOptions:{show:false}}],
                            grid: {
                                drawGridLines: false,
                                gridLineColor: 'lightgray' ,

                                background: 'white',
                                borderColor: 'gray',
                                borderWidth: 2.0,
                                shadow: true,
                                shadowAngle: 45,
                                shadowOffset: 1.5,
                                shadowWidth: 3,
                                shadowDepth: 3,
                                shadowAlpha: 0.07,
                                renderer: $.jqplot.CanvasGridRenderer,
                                rendererOptions: {}
                            },
                            cursor:{
                                zoom:false,
                                looseZoom: false,
                                showTooltip:false,
                                followMouse: true,
                                showTooltipOutsideZoom: true,
                                constrainOutsideZoom: false
                            }
                        };

                        // console.debug("opts: ",opts);
                        var linechart = $.jqplot(targetId, [data], opts);
                        console.debug("created LineChart: linechart",linechart);
                    })
                })

            }
        });
    });