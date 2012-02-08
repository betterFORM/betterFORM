/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

$(function () {
    $("#componentTree").jstree({
        "core" : {
            "initially_open" : [ "document-tmpl","model-tmpl","group-tmpl","repeat-tmpl","switch-tmpl","bind-tmpl","submission-tmpl","input-tmpl","output-tmpl","range-tmpl","secret-tmpl","select-tmpl","select1-tmpl","submit-tmpl","textarea-tmpl","trigger-tmpl","upload-tmpl","label-tmpl", "instance-tmpl", "instance-root-tmpl", "instance-data-tmpl" ]
        },
        "html_data":{
            "ajax":{
                url:EDITOR_HOME + "component-menu.html"
            }
        },
        "themes" : {
            "theme" : "default",
            "dots" : false,
            "icons" : false
        },

        "plugins" : [ "themes", "ui", "html_data","dnd" ]
    });
});

$("#componentTree").delegate("a", "click", function() {
    var currentItem = this.parentNode;

    if (dojo.hasClass(currentItem, "jstree-leaf")) {
        if ($("#componentTree").attr("data-bf-addmode") == "child") {
            addElement(currentItem.getAttribute("data-xf-type"), "last");
        } else {
            //get parent
            var parentLI = currentItem.parentNode.parentNode;
            // console.debug("parent add: ", parentLI);
            addElement(currentItem.getAttribute("data-xf-type"), "after");
        }
    }

    $("#componentTree").jstree("toggle_node", this);
});
