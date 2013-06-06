/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
dojo.require("dijit.Toolbar");
dojo.require("dijit.form.DropDownButton");
dojo.require("dijit.form.Button");
dojo.require("dijit.TooltipDialog");
dojo.require("dojo.data.ItemFileReadStore");
dojo.require("dojo.fx");

var xfReadySubscribers;
var displayPropertyMap = new Array();

function showSaveDialog() {
    dijit.byId("saveDialog").show();
    var embedDialog = dojo.byId("embedDialog");


    if (xfReadySubscribers != undefined) {
        dojo.unsubscribe(xfReadySubscribers);
        xfReadySubscribers = null;
    }

    var xfReadySubscribers = dojo.subscribe("/xf/ready", function(data) {
        dojo.fadeIn({
            node: embedDialog,
            duration:100
        }).play();
    });

    dojo.fadeOut({
        node: embedDialog,
        duration:100,
        onBegin: function() {
            fluxProcessor.dispatchEvent("t-save-as");
        }
    }).play();

}


xformsEditor = new betterform.editor.Editor({}, "editor");
//console.debug("xformsEditor.: ",xformsEditor);

function checkKeyboardInput(pEvent) {
    var activeElem = document.activeElement.localName;
    // console.debug("activeElem: ", activeElem);
    if (activeElem == "input") {
        return;
    }
    // console.debug("activeElem: ", activeElem);
    switch (pEvent.charOrCode) {
        case '?': //Process the Help key event
            dijit.byId("bfEditorHelp").show();
            break;
        case 't':
        case 'T':
            dojo.byId("root").focus();
            break;

        default:
            //no defaults at this time
            break;

    }
}

function generateCSSLocator(start, attribute_xpath) {
    var reference_locator = "";

    //TODO: < !!
    for (var i = start; i != attribute_xpath.length; i++) {
        reference_locator += " ." + attribute_xpath[i];
    }

    return reference_locator;
}

function setDisplayProps(node) {
    //console.debug("Node:", node);
    //console.debug("data-xf-attrs:", dojo.attr(node, 'data-xf-attrs'));

    var propertyName = displayPropertyMap[dojo.attr(node, 'data-xf-type')];

    var xfattrs = dojox.json.ref.fromJson(dojo.attr(node, 'data-xf-attrs'));

    if (xfattrs[propertyName] != undefined) {
        var span = dojo.query("a .displayProps", node)[0];
        span.innerHTML = propertyName + " : " + xfattrs[propertyName];
    }
    //check ref, bind, model, idrefs

    if (xfattrs['ref'] != undefined && xfattrs['ref'] != '') {
        var reference = xfattrs['ref'];
        var instance_locator = ".instance";
        var reference_locator = "";

        if (reference.indexOf('instance(\'') != -1) {
            instance_locator = "#" + reference.substring(reference.indexOf('instance(\'') + 'instance(\''.length, reference.indexOf('\')'));
            var reference_xpath = reference.split('/');
            reference_locator = generateCSSLocator(1, reference_xpath);
        } else if (reference.indexOf('instance()') != -1) {
            instance_locator = ".model .instance:first-child"

            var reference_xpath = reference.split('/');
            reference_locator = generateCSSLocator(1, reference_xpath);
        } else {
            var reference_xpath = reference.split('/');
            reference_locator = generateCSSLocator(0, reference_xpath);
        }

        //console.debug('Thingy: ' + instance_locator + reference_locator);
        if (dojo.query(instance_locator + reference_locator).length == 0) {
            dojo.style(span, 'color', 'red');
        } else {
            dojo.style(span, 'color', 'green');
        }
    }
}

dojo.addOnLoad(
    function() {
        dojo.connect(dojo.body(), "onkeypress", checkKeyboardInput);
    },

    function() {
        // hide the componentTree if a click outside a 'category' happens
        dojo.connect(dojo.body(), "onclick", function(evt) {
            var ancestorClass = evt.target.parentNode.parentNode.className;
            if (ancestorClass != "category") {
                dojo.style("componentTree", "display", "none");
            }
        });
    },

    dojo.behavior.add({
        '.input, .output, .range, .secret, .select, .select1, .submit, .textarea, .trigger, .upload': function(n) {
            displayPropertyMap[dojo.attr(n, 'data-xf-type')] = 'ref';
            setDisplayProps(n);

        },
        '.bind': function(n) {
            displayPropertyMap[dojo.attr(n, 'data-xf-type')] = 'nodeset';
            setDisplayProps(n);
        },
        '.model, .instance, .group, .switch, .repeat, .submission': function(n) {
            displayPropertyMap[dojo.attr(n, 'data-xf-type')] = 'id';
            setDisplayProps(n);
        }
    }),

    dojo.behavior.apply()
);



