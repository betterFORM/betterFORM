/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.UIElementFactory");

dojo.require("betterform.ui.factory.UIElementFactoryImpl");

dojo.declare("betterform.ui.UIElementFactory", null,
{
    factoryImpl: null,
    constructor:function() {
        this.factoryImpl = new betterform.ui.factory.UIElementFactoryImpl();
    },

    createWidget:function(sourceNode, controlId) {
        return this.factoryImpl.createWidget(sourceNode, controlId);
    }

});


