/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.spade.Container");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("dijit.layout.ContentPane");

dojo.declare(
    "betterform.ui.spade.Container",
    [dijit.layout.ContentPane,dijit._Templated],
    {


        buildRendering: function() {
        //            // we already have the srcNodeRef, so lets just
        //            // keep it and use it as the domNode
        //            this.domNode = this.srcNodeRef;
        //            console.debug("node: ", this.domNode);
        //
        //            // call this._attachTemplateNodes to parse the template,
        //            // which is actually just the srcnode
        //            // this method is provided by the _Templated mixin
        //            var dropDiv = dojo.doc.createElement("div");
        //            dropDiv.className = 'dndContainer';
        //            dropDiv.id = dojo.dnd.getUniqueId();
        //            dojo.attr(dropDiv,"dndType","element");
        //            // now create a target with your parameters
        //            dojo.require("dojo.dnd.Source");
        //            var myTarget = new dojo.dnd.Source(dropDiv, {
        //                autosync:true
        //            });
        //
        //            var itemDiv = dojo.doc.createElement("div");
        //            itemDiv.id = dojo.dnd.getUniqueId();
        //            itemDiv.className = 'dojoDndItem';
        //            itemDiv.backgroundColor = "yellow";
        //            itemDiv.style.width="100%";
        //            itemDiv.style.height="20px";
        //            itemDiv.innerHTML = ' ';
        //
        //
        //            dojo.place(dropDiv,this.domNode);
        //            dojo.place(itemDiv,dropDiv);
        //
        //            this._attachTemplateNodes(this.domNode);
        },

        postCreate:function(){
        //            this.inherited(arguments);
        //            console.debug("do the magic");
        }
    /*    postMixInProperties:function() {
        this.inherited(arguments);
        this.xfControl = dijit.byId(this.getControlId(this.id));
    }*/



    });

