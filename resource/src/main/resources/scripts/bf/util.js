/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

require(['dojo/_base/declare',"dojo/dom-class","dojo/dom-attr","dijit/registry","dojo/_base/window"],
    function(declare,domClass,domAttr,registry,win){
        declare("bf.util", null, {

        });

        bf.util.showAtWidget = function(widgetId) {
            //    var widget = dom.byId(widgetId + '-value');
            var widget = query("*[widgetId='" + widgetId + "-value']");
            // console.debug("showWidget:", widget);
            //var widget = dom.byId(widgetId + '-value');


            /* Wipe In Version */
        /*
            var position = dojo.coords(widget[0], true);
            console.debug("pos:", dojo.coords(widget[0], false));
            console.debug("abs: ", dojo._abs(widget[0],true));
            console.debug("abs: ", dojo._abs(widget[0],false));
            var help = dom.byId(widgetId + "-helptext");
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
         bf.util.replaceClass = function (element, current, update) {
             if (!element || !element.className) {
                 return false;
             }
             if(domClass.contains(element,current)){
                domClass.remove(element,current);
             } else {
                 console.warn("Element ", element , " does not have class ", current);
                 return false;
                 }

             if(domClass.contains(element,update)){
                 console.warn("Element ", element , " class ", update, " allready present");
                 return false;
             } else {
                 domClass.add(element,update);
             }
             return true;
         };
         */

        bf.util.getContainerByClass = function(/* node */ node, /*String*/ cssClass ){
            var body = win.body();
            while(node && node != body && !domClass.contains(node, cssClass)) {
                node = node.parentNode;
            }
            if(domClass.contains(node, cssClass)){
                return node;
            }
            return null;
        };


        /* Former replace Class*/
        bf.util.replaceClass = function (element, current, update) {
            //console.debug("bf.util.replaceClass " + current + " with " +  update + " for Control ", element);
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
        bf.util.setDefaultClasses = function (element) {
            if(!domClass.contains(element,"xfEnabled") && !domClass.contains(element,"xfDisabled")){
                domClass.add(element,"xfEnabled");
            }
            if(!domClass.contains(element,"xfOptional") && !domClass.contains(element,"xfRequired")){
                domClass.add(element,"xfOptional");
            }
            if(!domClass.contains(element,"xfReadWrite") && !domClass.contains(element,"xfReadOnly")){
                domClass.add(element,"xfReadWrite");
            }
            if(!domClass.contains(element,"xfValid") && !domClass.contains(element,"xfInvalid")){
                domClass.add(element,"xfValid");
            }
        };

        bf.util.removeStyle = function(element,styleToRemove) {
            if(element == undefined || styleToRemove == undefined) {
                return false;
            }
            var style = domAttr.get(element, "style");
            // console.debug("deleting style: " + styleToRemove + " for stlye: "+ style);
            if(style != undefined && style.indexOf(styleToRemove)!= -1){
                // console.debug("deleting style: " + styleToRemove + " for stlye: "+ style);
                style.replace(styleToRemove,"");
                domAttr.set(element, "style", style);
                return true;
            }
            return false;
        };

        bf.util.removeNamespace = function(value){
                var nonNamespacedValue = value;
                if(nonNamespacedValue != undefined && nonNamespacedValue != "" && nonNamespacedValue.indexOf(":") != -1) {
                    nonNamespacedValue = nonNamespacedValue.substring(nonNamespacedValue.indexOf(":")+1,nonNamespacedValue.length);
                }
                return nonNamespacedValue;
        };

        bf.util.closeSelect1 = function(htmlCase) {
            /*
            console.debug("bf.util.closeSelect1 argument: ",arguments);
            var selectList = query(".dijitComboBox",htmlCase);
            console.debug("bf.util.closeSelect1 select: ", selectList);
            var id = domAttr.get(selectList[0],"widgetid");
            console.debug("bf.util.closeSelect1 id: ", id);
            var dijitValue = registry.byId(id);
            console.debug("bf.util.closeSelect1 dijit: ", dijitValue);
            dijitValue._hideResultList();
            */
            query(".xfSelect1 .dijitComboBox",htmlCase).forEach(
                registry.byId(domAttr.get(item, 'widgetid'))._hideResultList()
            );
        };

        bf.util.getXfId = function(/*Node*/n){
                var tmp = n.id.substring(0,n.id.lastIndexOf("-"));
                // console.debug("returning xfId: ",tmp);
                return tmp;
        };
    }
);