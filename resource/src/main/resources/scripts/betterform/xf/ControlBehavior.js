/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.xf.ControlBehavior");
dojo.require("betterform.xf.XFControl");


/*
    Component Definition File which is the central for mapping XForms controls to client-side controls.
    todo:see newJSLayer-readme.txt

    Important: this file must be loaded as first behavior file
*/
var controlBehavior = {

    /*
     ###########################################################################################
     matching all elements with .xfControl and instanciate a XFControl instance for each of them.
     Order is important here - all XFControl should be instanciated before their respective widget childs are
     created. Thus this rule must be the first in a component definition file like this.
    */
    '.xfControl':function(n) {
        // console.debug("ControlBehaviour: XFControl found: ",n);

        var controlId = dojo.attr(n,"id");
        new betterform.xf.XFControl({
            id:controlId,
            controlType:"generic"
        }, n);
    }

};
function getXfId(/*Node*/n){
    var tmp = n.id.substring(0,n.id.lastIndexOf("-"));
    // console.debug("returning xfId: ",tmp);
    return tmp;
}

