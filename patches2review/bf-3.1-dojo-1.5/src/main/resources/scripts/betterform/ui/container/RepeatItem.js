/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.container.RepeatItem");

dojo.require("betterform.ui.container.Container");
dojo.require("dijit._Templated");
dojo.require("dijit.Dialog")
dojo.require("dojo.NodeList-fx");

dojo.declare(
        "betterform.ui.container.RepeatItem",
         [betterform.ui.container.Container,dijit._Templated],
    {
        repeatId:"",
        protoype:"",
        position:"",
        animIn:null,
        animOut:null,
        dialogId:null, 
        appearance:"",

        buildRendering: function() {
            this.domNode = this.srcNodeRef;
            // console.debug("betterform.ui.container.RepeatItem.buildRendering: appearance:",dojo.attr(this.srcNodeRef, "appearance"), " srcNodeRef: ",this.srcNodeRef);
            if(dojo.attr(this.srcNodeRef, "appearance")!= undefined) {
                this.appearance =dojo.attr(this.srcNodeRef, "appearance"); 
            }
            //console.debug("RepeatItem.buildRendering domNode", this.domNode);
            // TODO: verify if this._attachTemplateNodes(this.domNode) can really be removed, remove it in all other controls
            // this._attachTemplateNodes(this.domNode);
        },
/*
        postCreate:function() {
            // console.debug("betterform.ui.container.RepeatItem.postCreate: appearance: ",this.appearance);
            dojo.connect(this.domNode, "onmouseover", this, "_onMouseOver");
            dojo.connect(this.domNode, "onmouseout", this, "_onMouseOut");
            dojo.connect(this.domNode, "onkeydown", this, "_onKeyDown");
        },
        _onMouseOver:function(){
            if(!dojo.hasClass(this.domNode,"xfRepeatIndexMouseOver" )){
                dojo.addClass(this.domNode, "xfRepeatIndexMouseOver");
            }
        },

        _onMouseOut:function(){
            if(dojo.hasClass(this.domNode,"xfRepeatIndexMouseOver" )){
                dojo.removeClass(this.domNode, "xfRepeatIndexMouseOver");
            }
        },

        _onKeyDown:function( event) {
            var keycode;
            if (window.event) {
                keycode = window.event.keyCode;
            }
            else if (event) {
                keycode = event.which;
            }

            if (keycode == 38) {
                this.previousItem();
            }
            else if (keycode == 40) {
                this.nextItem();
            }
        },

*/

        /* extension point to overwrite handleStateChanged for containers */
        handleStateChanged:function(contextInfo) {
            this.inherited(arguments);

        },
        _onBlur:function() {           
            //dojo.query(".caRepeatActionDock",this.domNode).style("display","none");
            this.inherited(arguments);
        },
        _onFocus:function(){
           // dojo.query(".caRepeatActionDock",this.domNode).style("display","block");
            if(dojo.hasClass(this.domNode,"xfRepeatIndexMouseOver" )){
                dojo.removeClass(this.domNode, "xfRepeatIndexMouseOver");
            }
            
            // console.debug("\n\nRepeatItem " + this.id + " focused");
            this.forceIndex();
            this.inherited(arguments);
        },


        forceIndex:function() {
            // console.debug("RepeatItem.forceIndex id: " + this.id);

            if(dojo.hasClass(this.domNode, "xfRepeatIndex")){
                // console.debug("repeat item " + this.id + " allready selected");
                return;
            }
            var repeatItems = this.domNode.parentNode.childNodes;
            // lookup target to compute logical position
/*
            dojo.query(".xfRepeatIndex", repeatItems).removeClass("xfRepeatIndex");
            dojo.query(".xfRepeatIndexPre", repeatItems).removeClass("xfRepeatIndexPre");
*/
            dojo.forEach(repeatItems,
                  function(entry) {
                      if (dojo.hasClass(entry, "xfRepeatIndex")) { dojo.removeClass(entry, "xfRepeatIndex");}
                      if (dojo.hasClass(entry, "xfRepeatIndexPre")) { dojo.removeClass(entry, "xfRepeatIndexPre");}
                  }
            );
            dojo.addClass(this.domNode, "xfRepeatIndexPre");

            var position = this._getXFormsPosition();
            // lookup repeat id
            var repeat = this._getRepeat();
          // console.debug("ForceIndex: Repeat", repeat, " DOMNode: ",repeat.domNode);
          // console.debug("forceIndex: set repeat index for repeatitem " +dojo.attr(repeat.domNode,"repeatId")+ " to position " + position);
            fluxProcessor.setRepeatIndex(dojo.attr(repeat.domNode,"repeatId"), position);
         },


        _getRepeat:function() {
            var repeat = this.domNode.parentNode;
            while (dojo.attr(repeat, "repeatId") == undefined && repeat != undefined) {
                repeat = repeat.parentNode;
            }
            return dijit.byId(dojo.attr(repeat,"id"));
        },

        _getXFormsPosition:function() {
           dojo.attr(this.domNode, "selected", "true");

            var repeatItems = this._getRepeat()._getRepeatItems();
          // console.debug("RepeatItem._getXFormsPosition repeatItems: ", repeatItems);

            // lookup target to compute logical position
            var position = 0;
            dojo.forEach(repeatItems,
                  function(entry, index) {
                        if(dojo.attr(entry, "selected") == "true"){
                            entry.removeAttribute("selected");
                            position = index + 1;

                        }
                    }
            );
            // console.debug("Position = "+ position);
            return position;
        },

        nextItem:function() {
            var next = this._getXFormsPosition() +1;
            var repeatDijit = this._getRepeat();
            if(next > repeatDijit._getSize()){
                next = repeatDijit._getSize();
            }
            // console.debug("NextItem: set repeat index for repeatitem " +dojo.attr(repeatDijit.domNode,"repeatId")+ " to position " + next);
            fluxProcessor.setRepeatIndex(dojo.attr(repeatDijit.domNode,"repeatId"), next);
         },

        previousItem:function() {
            var previous = this._getXFormsPosition() -1;
            // lookup repeat id
            var repeatDijit = this._getRepeat();
            // console.debug("PreviousItem: set repeat index for repeatitem " +dojo.attr(repeatDijit.domNode,"repeatId")+ " to position " + previous);
            if(previous < 1 ) {
                previous = 1;
            }
            fluxProcessor.setRepeatIndex(dojo.attr(repeatDijit.domNode,"repeatId"), previous);
         },

        
        showRepeatItem:function() {
            dojo.style(this.domNode, "opacity","1");

//            if(this.animOut != undefined && this.animOut.status == "playing") {
//                this.animOut.stop();
//            }
//            var animFadeIn = null;
//
//            // FULL REPEAT INSERT EFFECTS
//            if(this.appearance=="full"){
//                animFadeIn = dojo.fadeIn({
//                                    node: this.domNode,
//                                    duration: 300
//                });
//
//            }
//            // COMPACT REPEAT INSERT EFFECTS
//            else{
//                var animFadeInBegin = dojo.fadeIn({
//                                    node: this.domNode,
//                                    duration: 300
//                });
//
//                var animSizeNormal= dojo.animateProperty({
//                                node: this.domNode,
//                                properties:{
//                                    height:{start:40.0, end:20.0,unit:"px"}
//                                },
//                                duration:300
//                            });
//
//                animFadeIn = dojo.fx.combine([animFadeInBegin,animSizeNormal]);
//
//
//
//            }
//            var animChildsIn = dojo.query(".xfControl",this.domNode).animateProperty({
//                duration:300,
//                properties: {
//                       opacity: {start:0,end:1 },
//                       color: {start:"red",end:"black" }
//                }
//            });
//            this.animIn = dojo.fx.combine([animFadeIn, animChildsIn]);
//
//            this.animIn.play();
           // this.animIn.play();

        },
        /* hide RepeatItem while child controls are created
           do not misstake hideRepeatItem with delete RepeatItem (this happens in Repeat.handleDelete();
        */
        hideRepeatItem:function() {
            dojo.style(this.domNode, "opacity","0");

//            if(this.appearance !="full") {
//                 this.animOut = dojo.animateProperty({
//                         node: this.domNode,
//                         properties: {
//                             height:{end:40.0,unit:"px"}
//
//                         },
//                         duration:10
//                 });
//                this.animOut.play();
//            }
//
//            setTimeout("",300);

        }
    }
);


