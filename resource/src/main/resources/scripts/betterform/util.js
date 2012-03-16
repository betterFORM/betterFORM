/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.util");



betterform.util.innerHTML = function(node, value){
    console.debug("betterform.util.innerHTML: node:",node, " value:",value);
    if (node != undefined) {
        node.innerHTML = value;
    }
    else {
        console.warn("Failure updating Control '" + node.id + "' with value: " + value);
    }
};


betterform.util.valueAttr = function(node, value){
    console.debug("betterform.util.valueAttr: node:",node, " value:",value);
     if (node != undefined) {
        dojo.attr(node, "value", value);
     }
     else {
        console.error("Failure updating Control '" + node.id + "' with value: " + value);
     }
};

