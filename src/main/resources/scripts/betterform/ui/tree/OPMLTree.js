/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.tree.OPMLTree");

dojo.require("dijit._Widget");
dojo.require("dijit._Templated");
dojo.require("betterform.ui.ControlValue");
dojo.require("dijit.Tree");
dojo.require("dojox.data.OpmlStore");
dojo.require("dojox.data.dom");

/*
    CLASS: OPMLTree represents an XForms OPML Tree Control

     The class is a mash-up of three other Dijits
       this.store: dojox.data.OpmlStore
       this.model: dijit.tree.ForestStoreModel
       this.tree:  dijit.Tree
    betterform.ui.ControlValue,

 */
dojo.declare(
        "betterform.ui.tree.OPMLTree",
        betterform.ui.ControlValue,
{
    // Root Label
    rootLabel:"Root Node",
    templateString: dojo.cache("betterform", "ui/templates/Tree.html"),
    instanceId:null,
    modelId:null,

    store:null,
    model:null,
    tree:null,
    id:null,
    controlValue:null,
    templateString: null,



    buildRendering:function() {
        // console.debug("betterform.ui.tree.OPMLTree.buildRendering srcNode:", this.srcNodeRef);
        this.inherited(arguments);
        // prepare getInstanceDocument() call in postCreate()
        this.instanceId = dojo.attr(this.srcNodeRef, "instanceId");
        this.modelId = dojo.attr(this.srcNodeRef, "modelId"); 
        this.id = dojo.attr(this.srcNodeRef, "id"); 

        // set tree parameters
        // this.rootLabel = dojo.attr(this.srcNodeRef, "label");
    },

    postMixInProperties:function() {
        this.applyProperties(dijit.byId(this.xfControlId), this.srcNodeRef);
    },
    
    postCreate:function() {
        // console.debug("betterform.ui.tree.OPMLTree.postCreate");
        this.inherited(arguments);
        // retrieve tree data from the server and create store, model and tree in callback method _createTree()
        dijit.byId(this.modelId).getInstanceDocument(this.instanceId,dojo.hitch(this,this._initialTreeCreation));
    },

    _initialTreeCreation:function(data) {
        // console.debug("initialize tree for the first time");
        this._createTree(data);
    },

    /*
        FUNKTION:_createTree(): Callback method for getInstanceDocument() in postCreate()
        creates:  - this.store:   Dojo OPMLDataStore holding the tree instance data
                  - this.model:   Dojo ForestStoreModel (needs no root value within the data)
                  - this.tree:    Dojo dijit.Tree Implementation
     */
    _createTree:function(data) {
        // console.debug("betterform.ui.tree.OPMLTree_createTree: data:", data, " \n\tinstanceId:" , this.instanceId ," \n\tmodelId:" , this.modelId );
        // console.dirxml(data);
        this.store = this._createStore(data);
        this.model = this._createModel();

        dojox.data.dom.removeChildren(this.domNode);
        var treeNode = dojo.doc.createElement("div");
        dojo.place(treeNode, this.domNode)

        this.tree = this._createTreeImpl(treeNode);
        this.tree.startup();

        dojo.connect(this.tree,"onClick", this,"_treeClicked");
        // console.debug("betterform.ui.tree.OPMLTree_createTree: created & connected tree: ", this.tree);
        this.treeCreated();

    },

    _createStore:function(data) {
        return new dojox.data.OpmlStore(
                                {
                                    jsid:this.id + "-store",
                                    url:"",
                                    data:data
                                });
    },

    _createModel:function() {
        return new dijit.tree.ForestStoreModel(
                                {
                                        jsid:this.id + "-model",
                                        store: this.store,
                                        query:{},
                                        rootId:this.rootLabel,
                                        rootLabel:this.rootLabel
                                });
    },

    _createTreeImpl:function(treeNode) {
        return new dijit.Tree(
                                {
                                    model: this.model,
                                    jsid:this.id + "-tree"
                                },
                                treeNode
                            );
    },

    _treeClicked:function(item, treeNode) {
        // console.debug("betterform.ui.tree.OPMLTree._treeClicked: clicked Tree: ", item);
        if(!(this.store.isItem(item))) { return; }
        this.controlValue = this.store.getValue(item, "text");
        this.valueChanged(item, treeNode);
    },
    
    _handleSetControlValue:function(value) {
        // console.debug("betterform.ui.tree.OPMLTree._handleSetControlValue() value:",value, " this:",this);

        this.tree.destroy();
        this.model.destroy();       
        this.store = undefined;

        dijit.byId(this.modelId).getInstanceDocument(this.instanceId,dojo.hitch(this,this._createTree));
    },
/*
######################################################################################################################
WARNING!!!!  This is the first method where store, model and tree are available,
Do not(!) rely on this.store in any method before!!
######################################################################################################################
 */


    /*
        Overwrite this to implement application specific logic 
     */
    valueChanged:function(item, treeNode) {
/*
        if(item == undefined) {
            return;
        }
        // console.debug("Tree " +  this.id + " Leaf '", text, "' clicked ", treeNode);
        var type = this.store.getValue(item, "type");
        var population = this.store.getValue(item, "population");
        if(population==undefined){
            population="unknwon";
        }
        var timezone = this.store.getValue(item, "timezone");
        if(timezone==undefined){
            timezone="unknwon";
        }

        var area = this.store.getValue(item, "area");
        if(area==undefined){
            area="unknwon";
        }

        console.debug("selected "  + type +  " " + this.controlValue + " [ population: '" + population + "'; timezone: '" + timezone + "'; area: '"+ area +"'; ]");
*/
    },

    // Dojo Tree Implementation Wrapper Functions
    getControlValue:function() {
        return this.controlValue;
    },

    getStoreValue:function(/* item */ item, /*String */ key) {
        return this.store.getValue(item, key);
    },


    // Dojo Tree Implementation Wrapper Functions

    treeCreated:function() {
        // console.debug("Tree.treeCreated")
    }

});

