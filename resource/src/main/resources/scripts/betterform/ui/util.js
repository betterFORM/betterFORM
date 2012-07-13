/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dojo.provide("betterform.ui.util");

betterform.ui.util.showAtWidget = function(widgetId) {
    //    var widget = dojo.byId(widgetId + '-value');
    var widget = dojo.query("*[widgetId='" + widgetId + "-value']");
    // console.debug("showWidget:", widget);
    //var widget = dojo.byId(widgetId + '-value');
    

    /* Wipe In Version */
/*
    var position = dojo.coords(widget[0], true);
    console.debug("pos:", dojo.coords(widget[0], false));
    console.debug("abs: ", dojo._abs(widget[0],true));
    console.debug("abs: ", dojo._abs(widget[0],false));
    var help = dojo.byId(widgetId + "-helptext");
    //help.style.left = position.x + 'px';
    var height = position.y + (2 * position.h);
    console.debug("Height: " + height);
    help.style.top = height;
    dojo.fx.wipeIn({node:help,duration:300}).play();
*/
};


/**
 * replaces a CSS class within the css attribute of given element. 'current' class will be replaced by 'update'.
 */
/*
 betterform.ui.util.replaceClass = function (element, current, update) {
     if (!element || !element.className) {
         return false;
     }
     if(dojo.hasClass(element,current)){
        dojo.removeClass(element,current);
     } else {
         console.warn("Element ", element , " does not have class ", current);
         return false;
         }

     if(dojo.hasClass(element,update)){
         console.warn("Element ", element , " class ", update, " allready present");
         return false;
     } else {
         dojo.addClass(element,update);
     }
     return true;
 };
 */

betterform.ui.util.getContainerByClass = function(/* node */ node, /*String*/ cssClass ){
    var body = dojo.body();
    while(node && node != body && !dojo.hasClass(node, cssClass)) {
        node = node.parentNode;
    }
    if(dojo.hasClass(node, cssClass)){
        return node;
    }
    return null;
};


/* Former replace Class*/
betterform.ui.util.replaceClass = function (element, current, update) {
    //console.debug("betterform.ui.util.replaceClass " + current + " with " +  update + " for Control ", element);
    if (!element || !element.className) {
        return false;
    }

    var oldClassName = element.className;

    // surround all strings with spaces to guarantee non-ambigous lookups
    var classList = " " + oldClassName + " ";
    var classCurrent = " " + current + " ";
    var classUpdate = " " + update + " ";

    if (classList.indexOf(classUpdate) == -1) {
        var newClassName = classList.replace(new RegExp(classCurrent), classUpdate);
        if (newClassName.indexOf(classUpdate) == -1) {
            // ensure the new class name, even if no replacement happened
            newClassName = classList + update + " ";
        }

        // remove leading and trailing spaces and update the element's class name
        newClassName = newClassName.slice(1, newClassName.length - 1);
        element.className = newClassName;

        return true;
    }

    return false;
};
betterform.ui.util.setDefaultClasses = function (element) {
    if(!dojo.hasClass(element,"xfEnabled") && !dojo.hasClass(element,"xfDisabled")){
        dojo.addClass(element,"xfEnabled");
    }
    if(!dojo.hasClass(element,"xfOptional") && !dojo.hasClass(element,"xfRequired")){
        dojo.addClass(element,"xfOptional");
    }
    if(!dojo.hasClass(element,"xfReadWrite") && !dojo.hasClass(element,"xfReadOnly")){
        dojo.addClass(element,"xfReadWrite");
    }
    if(!dojo.hasClass(element,"xfValid") && !dojo.hasClass(element,"xfInvalid")){
        dojo.addClass(element,"xfValid");
    }
};

betterform.ui.util.setRelevance = function(element){
    if(!dojo.hasClass(element,"xfEnabled") && !dojo.hasClass(element,"xfDisabled")){
        dojo.addClass(element,"xfEnabled");
    }
}


betterform.ui.util.removeStyle = function(element,styleToRemove) {
    if(element == undefined || styleToRemove == undefined) {
        return false;
    }
    var style = dojo.attr(element, "style");
    // console.debug("deleting style: " + styleToRemove + " for stlye: "+ style);
    if(style != undefined && style.indexOf(styleToRemove)!= -1){
        // console.debug("deleting style: " + styleToRemove + " for stlye: "+ style);
        style.replace(styleToRemove,"");
        dojo.attr(element, "style", style);
        return true;
    }
    return false; 
};

betterform.ui.util.removeNamespace = function(value){
        var nonNamespacedValue = value;
        if(nonNamespacedValue != undefined && nonNamespacedValue != "" && nonNamespacedValue.indexOf(":") != -1) {
            nonNamespacedValue = nonNamespacedValue.substring(nonNamespacedValue.indexOf(":")+1,nonNamespacedValue.length);
        }
        return nonNamespacedValue;
};

betterform.ui.util.closeSelect1 = function(htmlCase) {
    /*
    console.debug("betterform.ui.util.closeSelect1 argument: ",arguments);
    var selectList = dojo.query(".dijitComboBox",htmlCase);
    console.debug("betterform.ui.util.closeSelect1 select: ", selectList);
    var id = dojo.attr(selectList[0],"widgetid");
    console.debug("betterform.ui.util.closeSelect1 id: ", id);
    var dijitValue = dijit.byId(id);
    console.debug("betterform.ui.util.closeSelect1 dijit: ", dijitValue);
    dijitValue._hideResultList();
    */
    dojo.query(".xfSelect1 .dijitComboBox",htmlCase).forEach("dijit.byId(dojo.attr(item, 'widgetid'))._hideResultList()");
};