define(["dojo/_base/declare","dojo/dom","dojo/dom-attr","dojo/dom-construct", "dojo/dom-style"],
    function(declare, dom, domAttr, domConstruct, domStyle){
        return declare(null, {
            timelineNode:null,
            dataSource: null,
            timeline:null,

            constructor:function() {
                // console.debug("Timeline.constructor: this: ", this);
                this.srcNodeRef = arguments[1];
                this.dataSource = domAttr.get(this.srcNodeRef, "data-bf-url");
                var origId = domAttr.get(this.srcNodeRef, "id");
                this.id = origId.substring(0,origId.length -5) + "tlcontainer";
                // console.debug("Timeline: dataSource:", this.dataSource, " origId: ",origId);

                this.timelineNode = domConstruct.toDom("<div class='xfTimeline timeline-default' id='"+ this.id+"' style='width:1000px; height:350px;border:1px solid #aaa' ></div>");
                domConstruct.place(this.timelineNode, origId, "after");
                domStyle.set(this.srcNodeRef, "display", "none");
                this.initTimeline();
            },

            initTimeline:function() {
                // console.debug("this.initTimeline()");
                var eventSource = new Timeline.DefaultEventSource();
                // console.debug("eventSource:",eventSource );

                var d = Timeline.DateTime.parseGregorianDateTime("1900")

                var bandInfos = [
                    Timeline.createBandInfo({
                        width:          "80%",
                        date:           d,
                        intervalUnit:   Timeline.DateTime.MONTH,
                        intervalPixels: 300,
                        eventSource:    eventSource
                    }),
                    Timeline.createBandInfo({
                        overview:       true,
                        width:          "10%",
                        intervalUnit:   Timeline.DateTime.YEAR,
                        intervalPixels: 100,
                        eventSource:    eventSource
                    }),
                    Timeline.createBandInfo({
                        overview:       true,
                        width:          "10%",
                        intervalUnit:   Timeline.DateTime.DECADE,
                        intervalPixels: 50,
                        eventSource:    eventSource
                    })
                ];


                bandInfos[1].syncWith = 0;
                bandInfos[1].highlight = true;
                bandInfos[2].syncWith = 1;
                bandInfos[2].highlight = true;
                console.debug("bandInfos:",bandInfos);
                // create the Timeline

                this.timeline = Timeline.create(this.timelineNode, bandInfos,Timeline.HORIZONTAL);
                var tl = this.timeline;
                Timeline.loadXML(this.dataSource, function(xml,url) {
                    // console.debug("Load XML: ", url);                   
                    eventSource.loadXML(xml,url);
                    tl.layout();

                });
                // console.debug("this.timeline:",this.timeline);
                this.resize();
            },

            resize:function() {
                // console.debug("Timeline.resize: this.timeline",this.timeline);
                var tl = this.timeline;
                window.setTimeout(function() {
                    // console.debug("layout Timeline: : this.timeline",tl);
                    tl.layout();
                }, 500);
            }

        });
    });