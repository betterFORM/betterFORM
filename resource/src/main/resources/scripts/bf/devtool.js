/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
require(['dojo/_base/declare',"dojo/dom-style","dojo/dom-attr","dojo/_base/connect","dojo/dom-class","dojo/dnd/Moveable"],
    function(declare,domStyle,domAttr,connect,domClass,Moveable){
        declare(null, {

    /*
        // TODO substitute dojo.fx and dojo.dnd.Moveable requires with new AMD loading
            dojo.require("dojox.fx");
    */
        });

        bf.devtool.inprogress=false;

        bf.devtool.toggleLog = function(){
            var dnd = new Moveable(dom.byId("evtLogContainer"));
            var evtContainer = dom.byId("evtLogContainer");
            var logStyle = domAttr.get(evtContainer,"style");
            if(logStyle.length != 0 ){
                domAttr.set(evtContainer,"style","");
            }else{
                domAttr.set(evtContainer,"style","width:26px;height:26px;overflow:hidden;");

            }
        };
        bf.devtool.clearLog=function(){
            query("#eventLog li").forEach(function(node){
                dojo.destroy(node);
            });
        };

        bf.devtool.reveal=function(node){

            var id = node.innerHTML;
            var tNode = dom.byId(id);
            if(tNode !=undefined && bf.devtool.inprogress==false){
                var currPadding = domStyle.get(tNode,"padding");
                console.debug("padding >>> ", currPadding);
                dojox.fx.highlight({
                    node:tNode,
                    color:'#0066FF',
                    duration:1000,
                    onBegin:function(){
                        domStyle.set(tNode,"padding","10px");
                        bf.devtool.inprogress=true;
                    },
                    onEnd:function(){
                        domStyle.set(tNode,"padding",currPadding);
                        bf.devtool.inprogress=false;
                    }
                }).play();
            }
        };

        bf.devtool.toggleEntry=function(node){
            var entry = query(".eventLogTable",node.parentNode)[0];
            var entryStyle = domAttr.get(entry,"style");
            if(entryStyle == undefined || entryStyle.length == 0 ){
                domStyle.set(entry,"display","none");
            }else{
                domStyle.set(entry,"display","");
            }
        };

        bf.devtool.toggleDebug = function(){
            var debugpane = dom.byId("debug-pane");
            if(domClass.contains(debugpane,"open")){
                var closeAnim = dojo.animateProperty({
                    node:debugpane,
                    properties: {
                        width:{start:100,end:0,unit:"%"},
                        opacity:{start:1.0, end:0}
                    }
                });
                connect.connect(closeAnim, "onEnd", function(node){
                    domStyle.set(node,"opacity", 0);
                    domStyle.set(node,"display", "none");
                });
                closeAnim.play();
                domClass.remove(debugpane,"open");
                domClass.add(debugpane,"closed");

            }else{
                domStyle.set(debugpane,"display", "block");
                var openAnim = dojo.animateProperty({
                    node:debugpane,
                    properties: {
                        width:{start:0,end:100,units:"%"},
                        opacity:{start:0, end:1.0}
                    }
                });
                connect.connect(openAnim, "onEnd", function(node){
                    domStyle.set(node,"opacity", 1.0);

                });
                openAnim.play();
                domClass.remove(debugpane,"closed");
                domClass.add(debugpane,"open");
            }
        }
});

