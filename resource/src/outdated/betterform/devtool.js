/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.devtool");
dojo.require("dojox.fx");
dojo.require("dojo.dnd.Moveable");

var inprogress=false;

function toggleLog(){
    var dnd = new dojo.dnd.Moveable(dojo.byId("evtLogContainer"));
    var evtContainer = dojo.byId("evtLogContainer");
    var logStyle = dojo.attr(evtContainer,"style");
    if(logStyle.length != 0 ){
        dojo.attr(evtContainer,"style","");
    }else{
        dojo.attr(evtContainer,"style","width:26px;height:26px;overflow:hidden;");

    }
}
function clearLog(){
    dojo.query("#eventLog li").forEach(function(node){
        dojo.destroy(node);
    });
}
function reveal(node){

    var id = node.innerHTML;
    var tNode = dojo.byId(id);
    if(tNode !=undefined && inprogress==false){
        var currPadding = dojo.style(tNode,"padding");
        console.debug("padding >>> ", currPadding);
        dojox.fx.highlight({
            node:tNode,
            color:'#0066FF',
            duration:1000,
            onBegin:function(){
                dojo.style(tNode,"padding","10px");
                inprogress=true;
            },
            onEnd:function(){
                dojo.style(tNode,"padding",currPadding);
                inprogress=false;
            }
        }).play();
    }
}
function toggleEntry(node){
    var entry = dojo.query(".eventLogTable",node.parentNode)[0];
    var entryStyle = dojo.attr(entry,"style");
    if(entryStyle == undefined || entryStyle.length == 0 ){
        dojo.style(entry,"display","none");
    }else{
        dojo.style(entry,"display","");
    }
}

