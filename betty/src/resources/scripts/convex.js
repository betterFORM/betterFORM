/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
// TODO: modularize, introduce oo-like structures
var DEBUG = true;
var DATE_DISPLAY_FORMAT = "%d.%m.%Y";
var DATETIME_DISPLAY_FORMAT = "%d.%m.%Y %H:%M";
var BETTERFORM_PSEUDO_ITEM = "betterform-pseudo-item";


function handleStateChange(id, value, valid, readonly, required, relevant, type){
    console.debug("args:", arguments);

    //1. get target
    var target = dojo.byId(id);
    var targetValue = dojo.byId(id+"-value");

    if(target != ""){
        //2. update value                                                                                                                                                  
        if(value != ""){
            targetValue.value = value;
        }

        //3. update valid MIP
        if(valid != ""){
            if(valid == "true"){
                dojo.removeClass(target,"invalid");
                dojo.addClass(target,"valid");
            }else{
                dojo.removeClass(target,"valid");
                dojo.addClass(target,"invalid");
            }
        }

        //4. update readonly MIP
        if(readonly != ""){
            if(readonly == "true"){
                dojo.removeClass(target,"readwrite");
                dojo.addClass(target,"readonly");
            }else{
                dojo.removeClass(target,"readonly");
                dojo.addClass(target,"readwrite");
            }
        }

        //5. update required MIP
        if(required != ""){
            if(required == "true"){
                dojo.removeClass(target,"optionel");
                dojo.addClass(target,"required");
            }else{
                dojo.removeClass(target,"required");
                dojo.addClass(target,"optionel");
            }
        }

        //6. update relevant MIP
        if(relevant != ""){
            if(relevant == "true"){
                dojo.removeClass(target,"disabled");
                dojo.addClass(target,"enabled");
            }else{
                dojo.removeClass(target,"enabled");
                dojo.addClass(target,"disabled");
            }
        }
        if(type != "") {
            //todo: check for existing type classes - do we still have the XS prefixes on our type names? check
            
        }
    }

//    dojo.query("#"+id).addClass("")
    
    return;
}

// Init / setup.

function setDebug (debug) {
    DEBUG = eval(debug);
}

function dojoEval(id, value) {         
    _debug("Id: " + id);
    _debug("Value: " + value[0]);
    dojo.byId(id+"-value").value = value;
}
function setDateDisplayFormat (format) {
    DATE_DISPLAY_FORMAT = format;
}
function setDateTimeDisplayFormat (format) {
    DATETIME_DISPLAY_FORMAT = format;
}
function setBetterFormPseudoItem (item) {
    BETTERFORM_PSEUDO_ITEM = item;
}
function setView(html){
    var xformsui = document.getElementById("xformsui");
    xformsui.innerHTML = html;
    xformsui.className = "enabled";
    return true;
}

// User interaction.

function submit() {
    // todo: really needed ?
}

/**
 * Performs activate for the current event target.
 */
function activate(target) {
    var id = dojo.byId(target).id;
    _debug("activate: " + id);

    document.Convex.dispatch(id, "DOMActivate");
}

/**
 * Performs upload for the current event target.
 */
function upload(unused) {
    var id = _getTargetId();
    var value = _getTargetValue();
    _debug("upload: " + id);

    // lookup destination
    var destination = "";
    var element = document.getElementById(id + "-destination");
    if (element != null) {
        destination = element.value;
    }

    // todo: mediatype detection
    document.Convex.setValue(id, value, "", destination);
}

/**
 * Performs value update for the current event target.
 */
function setXFormsValue(unused) {
    var id = _getTargetId();
    var value = _getTargetValue();
    var target = _getTargetObject();

    switch (target.type){
        case "radio":
            // get target id from parent control, since the id passed in is the item's id
            while(! _hasClass(target, "select1")) {
                target = target.parentNode;
            }
            id = target.id;
            break;
        case "checkbox":
            // keep name
            var name = target.name;

            // get target id from parent control, since the id passed in is the item's id
            while(! _hasClass(target, "select")) {
                target = target.parentNode;
            }
            id = target.id;

            // assemble value from selected checkboxes
            var elements = eval("document.betterform.elements");
            var checkboxes = new Array();
            for (i = 0; i < elements.length; i++) {
                if (elements[i].name == name && elements[i].type != "hidden" && elements[i].checked) {
                    checkboxes.push(elements[i].value);
                }
            }
            value = checkboxes.join(" ");
            break;
        case "select-multiple":
            // assemble value from selected options
            var options = target.options;
            var multiple = new Array();
            for (i = 0 ; i < options.length; i++){
                if (options[i].selected){
                    multiple.push(options[i].value);
                }
            }
            value = multiple.join(" ");
            break;
        default:
            break;
    }

    _debug("set value: " + id + "='" + value + "'");
    document.Convex.setValue(id, value);
}

/**
 * Performs index update for the current event target.
 */
function setRepeatIndex (unused) {
    // stop event propagation (for IE)
    window.event.cancelBubble = true;
    var target = _getTargetObject();

    // lookup repeat item
    while (! _hasClass(target, "repeat-item")) {
        target = target.parentNode;
    }
    target.setAttribute("selected", "true");

    var repeatItems = target.parentNode.childNodes;
    var currentPosition = 0;
    var targetPosition = 0;

    // lookup target to compute logical position
    for (index = 0; index < repeatItems.length; index++) {
        if (repeatItems[index].nodeType == 1) {
            if (_hasClass(repeatItems[index], "repeat-item")) {
                currentPosition++;
            }
            if (repeatItems[index].getAttribute("selected") == "true") {
                repeatItems[index].removeAttribute("selected");
                targetPosition = currentPosition;
                break;
            }
        }
    }

    // lookup repeat id
    while (! _hasClass(target, "repeat")) {
        target = target.parentNode;
    }
    var repeatId = target.id;

    _debug("setRepeatIndex: " + repeatId + "='" + targetPosition + "'");
    document.Convex.setRepeatIndex(repeatId, targetPosition);
}

// Events.

function _getTargetObject() {
    // IE
    if (window.event.srcElement) {
        return window.event.srcElement;
    }

    return null;
}

function _getTargetId() {
    // IE
    if (window.event.srcElement) {
        var target = window.event.srcElement;
        if (target.id) {
            var id = target.id;
            if (id.substring(id.length - 6, id.length) == "-value") {
                // cut off "-value"
                id = id.substring(0, id.length - 6);
            }

            return id;
        }
        else {
            alert("event target " + target + " has no id");
        }
    }

    return null;
}

function _getTargetValue() {
    // IE
    var value = null;
    if (window.event.srcElement) {
        var target = window.event.srcElement;
        if (target.value) {
            value = target.value;
        }
        else {
            value = "";
        }
    }

    return value;
}

// Utilities.

function _getElementById (element, id) {
    if (element.getAttribute("id") == id) {
        return element;
    }

    var hit;
    var children = element.childNodes;
    for (var index = 0; index < children.length; index++) {
        if (children[index].nodeType == 1) {
            hit = _getElementById(children[index], id);
            if (hit) {
                return hit;
            }
        }
    }

    return null;
}

function _hasClass (element, clazz) {
    if (!element.className) {
        return false;
    }
    if ((" " + element.className + " ").indexOf(" " + clazz + " ") == -1) {
        return false;
    }
    return true;
}

function _replaceClass (element, current, update) {
    if(element){
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
            _debug("replaced classes '" + oldClassName + "' with '" + newClassName + "'");
        }
    }
}

function _debug (msg) {
        console.debug(msg);
        document.Convex.debug("" + msg);

}

// Calendar.

/**
 * Initializes the calendar component according to the underlying datatype.
 */
function calendarSetup (id, value, type) {
    // initialize hidden calendar
    // todo: jsCalendar has problems with time part
    var dateTime = type == 'dateTime';
    Calendar.setup({
        date: value && value.length > 0 ? value : null, // use null for empty value
        firstDay: 1, // use monday as first day of week
        showsTime: dateTime, // configure time display
        inputField: id + '-value', // hidden input field
        displayArea: id + '-' + type + '-display', // formatted display area
        button: id + '-' + type + '-button', // date control image button
        ifFormat: dateTime ? '%Y-%m-%dT%H:%M:%S' : '%Y-%m-%d', // ISO date/dateTime format
        daFormat: dateTime ? DATETIME_DISPLAY_FORMAT : DATE_DISPLAY_FORMAT, // configurable display format
        onClose: calendarOnClose, // callback for updating the processor
        onSelect: calendarOnSelect, // callback for updating the processor
        electric: false // matches xf:incremental, should be a parameter
    });

    // jsCalendar updates the display area only on user interaction, not on
    // init/setup nor on internal value updates.
    calendarUpdate(id, value, type);
}

/**
 * Updates the calendar component, namely the display area.
 */
function calendarUpdate (id, value, type) {
    var element = document.getElementById(id + '-' + type + '-display');
    if (element) {
        var date = _parseISODate(value);
        var format = type == 'dateTime' ? DATETIME_DISPLAY_FORMAT : DATE_DISPLAY_FORMAT;

        if (element.innerHTML) {
            // update <span/>, <div/> et al.
            element.innerHTML = date ? date.print(format) : '&nbsp;';
        }
        else {
            // update <input/>
            element.value = date ? date.print(format) : '';
        }

        return true;
    }

    return false;
}

/**
 * Calendar callback for select events.
 *
 * Allows jsCalendar to use form controls as display area too.
 */
function calendarOnSelect (calendar) {
    // copied from calendar-setup.js
    var p = calendar.params;
    var update = (calendar.dateClicked || p.electric);
    if (update && p.flat) {
        if (typeof p.flatCallback == "function")
            p.flatCallback(calendar);
        else
            alert("No flatCallback given -- doing nothing.");
        return false;
    }
    if (update && p.inputField) {
        p.inputField.value = calendar.date.print(p.ifFormat);
        if (typeof p.inputField.onchange == "function")
            p.inputField.onchange();
    }
    if (update && p.displayArea)
        // start patch by unl
        // check for 'innerHTML' property, otherwise try 'value' property
        if (p.displayArea.innerHTML) {
            p.displayArea.innerHTML = calendar.date.print(p.daFormat);
        }
        else {
            p.displayArea.value = calendar.date.print(p.daFormat);
        }
        // end patch by unl
    if (update && p.singleClick && calendar.dateClicked)
        calendar.callCloseHandler();
    if (update && typeof p.onUpdate == "function")
        p.onUpdate(calendar);
}

/**
 * Calendar callback for close events.
 *
 * Hides the calendar and updates the processor.
 */
function calendarOnClose (calendar) {
    calendar.hide();

    var id = calendar.params.inputField.id;
    var value = calendar.params.inputField.value;

    // cut off '-value'
    id = id.substring(0, id.length - 6);
    document.Convex.setValue(id, value);
}

/**
 * Parses an ISO date/datetime string. Timezones not supported yet.
 */
function _parseISODate (iso) {
    if (!iso || iso.length == 0) {
        return null;
    }

    var separator = iso.indexOf('T');
    if (separator == -1) {
        iso = iso + 'T00:00:00';
    }
    var parts = iso.split('T');
    var date = parts[0].split('-');
    var time = parts[1].split(':');

    return new Date(date[0], date[1] - 1, date[2], time[0], time[1], time[2]);
}

// Processor response handling

/**
 * Default constructor.
 */
Responder = function() {
};

/**
 * Prototype clones stack.
 */
Responder.PROTOTYPE_CLONES = new Array();

/**
 * Generated ids stack.
 */
Responder.GENERATED_IDS = new Array();

/**
 * Renders an ephemeral message.
 *
 * @param target the message target.
 * @param value the message content.
 */
Responder.renderEphemeralMessage = function(target,value) {
    _debug("Responder.renderEphemeralMessage: " + target + "='" + value + "'");

    alert("TODO: setControlHelp: " + target + "='" + value + "'");
};

/**
 * Renders a modeless message.
 *
 * @param target the message target.
 * @param value the message content.
 */
Responder.renderModelessMessage = function(target,value) {
    _debug("Responder.renderModelessMessage: " + target + "='" + value + "'");

    alert("TODO: renderModelessMessage: " + target + "='" + value + "'");
};

/**
 * Renders a modal message.
 *
 * @param target the message target.
 * @param value the message content.
 */
Responder.renderModalMessage = function(target,value) {
    _debug("Responder.renderModalMessage: " + target + "='" + value + "'");

    alert(value);
};

/**
 * Selects a case.
 *
 * @param target the message target.
 */
Responder.selectCase = function(target) {
    _debug("Responder.selectCase: " + target);

    document.getElementById(target).className = "selected-case";
};

/**
 * Deselects a case.
 *
 * @param target the message target.
 */
Responder.deselectCase = function(target) {
    _debug("Responder.deselectCase: " + target);

    document.getElementById(target).className = "deselected-case";
};

/**
 * Sets a bound element valid.
 *
 * @param target the message target.
 */
Responder.setElementValid = function(target) {
    _debug("Responder.setElementValid: " + target);

    var control = document.getElementById(target);
    _replaceClass(control, "invalid", "valid");
};

/**
 * Sets a bound element invalid.
 *
 * @param target the message target.
 */
Responder.setElementInvalid = function(target) {
    _debug("Responder.setElementInvalid: " + target);

    var control = document.getElementById(target);
    _replaceClass(control, "valid", "invalid");
};

/**
 * Sets a bound element readonly.
 *
 * @param target the message target.
 */
Responder.setElementReadonly = function(target) {
    _debug("Responder.setElementReadonly: " + target);

    var control = document.getElementById(target);
    _replaceClass(control, "readwrite", "readonly");

    if (document.getElementById(target + "-date-display")) {
        // special treatment for calendar
        Responder._updateCalendar(target, "date", true);
        return;
    }
    if (document.getElementById(target + "-dateTime-display")) {
        // special treatment for calendar
        Responder._updateCalendar(target, "dateTime", true);
        return;
    }

    var value = document.getElementById(target + "-value");
    if (value != null) {
        if (value.nodeName.toLowerCase() == "input" && value.type.toLowerCase() == "hidden") {
            // special treatment for radiobuttons/checkboxes
            Responder._updateSelectors(value, true);
            return;
        }

        value.setAttribute("disabled", "disabled");
    }
};

/**
 * Sets a bound element readwrite.
 *
 * @param target the message target.
 */
Responder.setElementReadwrite = function(target) {
    _debug("Responder.setElementReadwrite: " + target);

    var control = document.getElementById(target);
    _replaceClass(control, "readonly", "readwrite");

    if (document.getElementById(target + "-date-display")) {
        // special treatment for calendar
        Responder._updateCalendar(target, "date", false);
        return;
    }
    if (document.getElementById(target + "-dateTime-display")) {
        // special treatment for calendar
        Responder._updateCalendar(target, "dateTime", false);
        return;
    }

    var value = document.getElementById(target + "-value");
    if (value != null) {
        if (value.nodeName.toLowerCase() == "input" && value.type.toLowerCase() == "hidden") {
            // special treatment for radiobuttons/checkboxes
            Responder._updateSelectors(value, false);
            return;
        }

        value.removeAttribute("disabled");
    }
};

/**
 * Sets a bound element required.
 *
 * @param target the message target.
 */
Responder.setElementRequired = function(target) {
    _debug("Responder.setElementRequired: " + target);

    var control = document.getElementById(target);
    _replaceClass(control, "optional", "required");

    var required = document.getElementById(target + "-required");
    if (required != null) {
        required.innerText="*";
    }
};

/**
 * Sets a bound element optional.
 *
 * @param target the message target.
 */
Responder.setElementOptional = function(target) {
    _debug("Responder.setElementOptional: " + target);

    var control = document.getElementById(target);
    _replaceClass(control, "required", "optional");

    var required = document.getElementById(target + "-required");
    if (required != null) {
        required.innerText="";
    }
};

/**
 * Sets a bound element enabled.
 *
 * @param target the message target.
 */
Responder.setElementEnabled = function(target) {
    _debug("Responder.setElementEnabled: " + target);

    var control = document.getElementById(target);
    _replaceClass(control, "disabled", "enabled");

    var label = document.getElementById(target + "-label");
    if (label != null) {
        _replaceClass(label, "disabled", "enabled");
    }
};

/**
 * Sets a bound element disabled.
 *
 * @param target the message target.
 */
Responder.setElementDisabled = function(target) {
    _debug("Responder.setElementDisabled: " + target);

    var control = document.getElementById(target);
    _replaceClass(control, "enabled", "disabled");

    var label = document.getElementById(target + "-label");
    if (label != null) {
        _replaceClass(label, "enabled", "disabled");
    }
};

/**
 * Sets the value of a form control.
 *
 * @param target the message target.
 * @param value the message content.
 */
Responder.setControlValue = function(target,value) {
    _debug("Responder.setControlValue: " + target + "='" + value + "'");

    var control = document.getElementById(target + "-value");
    if (control == null) {
        alert("value for '" + target + "' not found");
        return;
    }

    var listValue = " " + value + " ";
    switch (control.nodeName.toLowerCase()) {
        case "a":
            // <xf:output appearance="anchor"/>
            control.href = value;
            control.innerText = value;
            break;
        case "img":
            // <xf:output appearance="image"/>
            control.src = value;
            break;
        case "input":
            if (control.type.toLowerCase() == "hidden"){
                // check for date control
                if (document.getElementById(target + "-date-display") || document.getElementById(target + "-date-button")) {
                    control.value = value;
                    calendarUpdate(target, value, "date");
                    break;
                }

                // check for dateTime control
                if (document.getElementById(target + "-dateTime-display") || document.getElementById(target + "-dateTime-button")) {
                    control.value = value;
                    calendarUpdate(target, value, "dateTime");
                    break;
                }

                // special treatment for radiobuttons/checkboxes
                var elements = eval("document.betterform.elements");
                var box;
                var boxValue;
                for (i = 0; i < elements.length; i++) {
                    if (elements[i].name == control.name && elements[i].type != "hidden") {
                        box = elements[i];
                        boxValue = " " + box.value + " ";
                        if (listValue.indexOf(boxValue) > -1) {
                            box.checked = true;
                        }
                        else {
                            box.checked = false;
                        }
                    }
                }
                break;
            }

            if (control.type.toLowerCase() == "button"){
                // ignore
                break;
            }

            if (control.type.toLowerCase() == "file"){
                // ignore
                break;
            }

            control.value = value;
            break;
        case "option":
            control.value = value;
            break;
        case "span":
            control.innerText = value;
            break;
        case "select":
            // special treatment for options
            var options = control.options.length;
            var option;
            var optionValue;
            for (i = 0 ; i < options; i++){
                option = control.options[i];
                optionValue = " " + option.value + " ";
                if (listValue.indexOf(optionValue) > -1) {
                    option.selected = true;
                }
                else {
                    option.selected = false;
                }
            }
            break;
        case "textarea":
            control.value = value;
            break;
        default:
            alert("unknown control '" + control.nodeName + "'");
    }
};

/**
 * Sets the label of a form control.
 *
 * @param target the message target.
 * @param value the message content.
 */
Responder.setControlLabel = function(target,value) {
    _debug("Responder.setControlLabel: " + target + "='" + value + "'");

    var element = document.getElementById(target + "-label");
    if (element != null) {
        // update label element
        element.innerText = value;
        return;
    }

    // heuristics: look for implicit labels
    var control = document.getElementById(target + "-value");
    switch (control.nodeName.toLowerCase()) {
        case "option":
            control.text = value;
            break;
        case "input":
            if (control.type.toLowerCase() == "button"){
                control.value = value;
                break;
            }
            // fall through
        default:
            // dirty hack for compact repeats: lookup enclosing table
            var td = document.getElementById(target);
            if (td != null && td.nodeName.toLowerCase() == "td") {
                var tr = td.parentNode;
                if (tr != null && tr.nodeName.toLowerCase() == "tr") {
                    var tbody = tr.parentNode;
                    if (tbody != null && tbody.nodeName.toLowerCase() == "tbody") {
                        var table = tbody.parentNode;
                        if (table != null && table.nodeName.toLowerCase() == "table") {
                            if (_hasClass(table, "compact-repeat")) {
                                _debug("ignoring label for '" + target + "' in compact repeat");
                                break;
                            }
                        }
                    }
                }
            }

            // complain, finally
            alert("label for '" + target + "' not found");
    }
};

/**
 * Sets the help of a form control.
 *
 * @param target the message target.
 * @param value the message content.
 */
Responder.setControlHelp = function(target,value) {
    _debug("Responder.setControlHelp: " + target + "='" + value + "'");

    alert("TODO: setControlHelp: " + target + "='" + value + "'");
};

/**
 * Sets the hint of a form control.
 *
 * @param target the message target.
 * @param value the message content.
 */
Responder.setControlHint = function(target,value) {
    _debug("Responder.setControlHint: " + target + "='" + value + "'");

    var element = document.getElementById(target + "-value");
    if (element != null) {
        if (element.nodeName.toLowerCase() == "input" &&
            element.type == "hidden") {
            // special treatment for radiobuttons/checkboxes
            var boxes = eval("document.betterform." + element.name + ".length;");
            var box;
            for (i = 0 ; i < boxes; i++){
                box = eval("document.betterform." + element.name + "[" + i + "]");
                box.title = value;
            }
        }
        else {
            element.title = value;
        }
    }
    else {
        alert("hint for '" + target + "' not found");
    }
};

/**
 * Sets the alert of a form control.
 *
 * @param target the message target.
 * @param value the message content.
 */
Responder.setControlAlert = function(target,value) {
    _debug("Responder.setControlAlert: " + target + "='" + value + "'");

    var element = document.getElementById(target + "-alert");
    if (element != null) {
        element.innerText = value;
    }
    else {
        alert("alert for '" + target + "' not found");
    }
};

/**
 * Clones a repeat prototype.
 *
 * @param target the repeat id.
 * @param value the prototype id (original repeat id).
 */
Responder.cloneRepeatPrototype = function(target,value) {
    _debug("Responder.cloneRepeatPrototype: " + target + "='" + value + "'");

    var clone = document.getElementById(value + "-prototype").cloneNode(true);
    clone.removeAttribute("id");
    clone.removeAttribute("style");
    clone.className = "repeat-item";
    Responder.PROTOTYPE_CLONES.push(clone);

    var ids = new Array();
    ids[target] = value;
    Responder.GENERATED_IDS.push(ids);
};

/**
 * Clones a selector prototype.
 *
 * @param target the selector id.
 * @param value the prototype id (original selector id).
 */
Responder.cloneSelectorPrototype = function(target,value) {
    _debug("Responder.cloneSelectorPrototype: " + target + "='" + value + "'");

    // clone prototype and make it an item
    var clone;
    var prototype = document.getElementById(value + "-prototype");
    var option = prototype.getAttribute("option");

    if (option && eval(option)) {
        // special handling for option prototypes, since their prototype
        // element needs a wrapper element for carrying the prototype id
        var optionIndex;
        for (i = 0; i < prototype.childNodes.length; i++) {
            if (prototype.childNodes[i].nodeType == 1) {
                optionIndex = i;
                break;
            }
        }
        prototype = prototype.childNodes[optionIndex];

        // create an option object rather than cloning it, otherwise IE won't
        // display it as an additional option !!!
        clone = new Option("", "");
        clone.selected = false;
        clone.defaultSelected = false;
        clone.id = prototype.id;
        clone.title = prototype.title;
    }
    else {
        clone = prototype.cloneNode(true);
        clone.removeAttribute("id");
    }

    clone.removeAttribute("style");
    clone.className = "selector-item";
    Responder.PROTOTYPE_CLONES.push(clone);

    var ids = new Array();
    ids[target] = value;
    Responder.GENERATED_IDS.push(ids);
};

/**
 * Sets a generated id on the current prototype clone.
 *
 * @param target the generated id.
 * @param value the original id.
 */
Responder.setGeneratedId = function(target,value) {
    _debug("Responder.setGeneratedId: " + target + "='" + value + "'");

    var array = Responder.GENERATED_IDS[Responder.GENERATED_IDS.length - 1];
    array[value] = target;
    array[value + "-value"] = target + "-value";
    array[value + "-label"] = target + "-label";
    array[value + "-alert"] = target + "-alert";
    array[value + "-required"] = target + "-required";

    if (!array[BETTERFORM_PSEUDO_ITEM]) {
        // we have to add a special 'betterform-pseudo-item' mapping, since for itemsets
        // within repeats there is no item yet at the time the prototype is generated.
        // thus, there is no original id in the prototype !!!
        array[BETTERFORM_PSEUDO_ITEM] = target;
        array[BETTERFORM_PSEUDO_ITEM + "-value"] = target + "-value";
        array[BETTERFORM_PSEUDO_ITEM + "-label"] = target + "-label";
    }
};

/**
 * Inserts the current prototype clone as a repeat item.
 *
 * @param target the repeat id.
 * @param value the insert position.
 */
Responder.insertRepeatItem = function(target,value) {
    _debug("Responder.insertRepeatItem: " + target + "='" + value + "'");

    // apply generated ids to prototype
    var prototypeClone = Responder.PROTOTYPE_CLONES.pop();
    var generatedIds = Responder.GENERATED_IDS.pop();
    Responder._applyGeneratedIds(prototypeClone, generatedIds);

    // setup indices
    var currentPosition = 0;
    var targetPosition = parseInt(value);
    var insertIndex = -1;
    var headerIndex = -1;

    // lookup repeat
    var hasEnclosing = Responder.PROTOTYPE_CLONES.length > 0;
    var originalId = generatedIds[target];
    var targetElement;
    if (hasEnclosing) {
        // nested repeat
        var enclosingPrototype = Responder.PROTOTYPE_CLONES[Responder.PROTOTYPE_CLONES.length - 1];
        targetElement = _getElementById(enclosingPrototype, originalId);
    }
    else {
        // top-level repeat
        targetElement = document.getElementById(target);
    }

    var repeatElement = Responder._getRepeatNode(targetElement);
    var repeatItems = repeatElement.childNodes;

    for (i = 0; i < repeatItems.length; i++) {
        // lookup elements
        if (repeatItems[i].nodeType == 1) {
            // lookup repeat header
            if (_hasClass(repeatItems[i], "repeat-header")) {
                headerIndex = i;
            }
            // lookup repeat item
            if (_hasClass(repeatItems[i], "repeat-item")) {
                currentPosition++;

                // store insert index (position *at* insert item)
                if (currentPosition == targetPosition) {
                    insertIndex = i;
                    break;
                }
            }
        }
    }

    if (targetPosition == 1 && headerIndex == -1 && insertIndex == -1) {
        var headerPrototype = document.getElementById(originalId + "-header");
        if (headerPrototype) {
            _debug("Responder.insertRepeatItem: adding repeat header");

            // clone prototype and make it a header
            var headerClone = headerPrototype.cloneNode(true);
            headerClone.removeAttribute("id");
            headerClone.removeAttribute("style");
            headerClone.className = "repeat-header";

            // apply generated ids
            Responder._applyGeneratedIds(headerClone, generatedIds);

            // insert header before item
            repeatElement.appendChild(headerClone);
        }
    }

    // detect reference node
    var referenceNode = null;
    if (insertIndex > -1) {
        referenceNode = repeatItems[insertIndex];
    }

    // insert prototype clone
    prototypeClone = repeatElement.insertBefore(prototypeClone, referenceNode);

    if (hasEnclosing) {
        // hack: store reverse mapping for setRepeatIndex()
        Responder.GENERATED_IDS[Responder.GENERATED_IDS.length - 1][target] = originalId;
    }
};

/**
 * Inserts the current prototype clone as a selector item.
 *
 * @param target the selector id.
 * @param value the insert position.
 */
Responder.insertSelectorItem = function(target,value) {
    _debug("Responder.insertSelectorItem: " + target + "='" + value + "'");

    // apply generated ids to prototype
    var prototypeClone = Responder.PROTOTYPE_CLONES.pop();
    var generatedIds = Responder.GENERATED_IDS.pop();
    Responder._applyGeneratedIds(prototypeClone, generatedIds);

    // setup indices
    var option = prototypeClone.nodeName.toLowerCase() == "option";
    var currentPosition = 0;
    var targetPosition = parseInt(value);
    var insertIndex = -1;

    // lookup repeat
    var hasEnclosing = Responder.PROTOTYPE_CLONES.length > 0;
    var originalId = generatedIds[target];
    var targetElement;
    if (hasEnclosing) {
        // nested repeat
        var enclosingPrototype = Responder.PROTOTYPE_CLONES[Responder.PROTOTYPE_CLONES.length - 1];
        targetElement = _getElementById(enclosingPrototype, originalId);
    }
    else {
        // top-level repeat
        targetElement = document.getElementById(target);
    }

    var itemsetElement = targetElement;
    var itemsetItems = itemsetElement.childNodes;

    for (i = 0; i < itemsetItems.length; i++) {
        // lookup elements
        if (itemsetItems[i].nodeType == 1) {
            // lookup repeat item
            if (_hasClass(itemsetItems[i], "selector-item")) {
                currentPosition++;

                // store insert index (position *at* insert item)
                if (currentPosition == targetPosition) {
                    insertIndex = i;
                    break;
                }
            }
        }
    }

    // insert item
    if (insertIndex > -1) {
        // todo: use itemset.parentNode.options[];
        itemsetElement.insertBefore(prototypeClone, itemsetItems[insertIndex]);
    }
    else {
        if (option && eval(option)) {
            itemsetElement.parentNode.options.add(prototypeClone);
        }
        else {
            itemsetElement.appendChild(prototypeClone);
        }
    }
};

/**
 * Deletes a repeat item.
 *
 * @param target the repeat id.
 * @param value the delete position.
 */
Responder.deleteRepeatItem = function(target,value) {
    _debug("Responder.deleteRepeatItem: " + target + "='" + value + "'");

    var currentPosition = 0;
    var targetPosition = parseInt(value);
    var deleteIndex = -1;
    var headerIndex = -1;
    var nextIndex = -1;

    var targetElement = document.getElementById(target);
    var repeatElement = Responder._getRepeatNode(targetElement);
    var repeatItems = repeatElement.childNodes;
    for (i = 0; i < repeatItems.length; i++) {
        // lookup elements
        if (repeatItems[i].nodeType == 1) {
            // lookup repeat header
            if (_hasClass(repeatItems[i], "repeat-header")) {
                headerIndex = i;
            }
            // lookup repeat item
            if (_hasClass(repeatItems[i], "repeat-item")) {
                currentPosition++;

                // store delete index (position *at* delete item)
                if (currentPosition == targetPosition) {
                    deleteIndex = i;
                }

                // check for next item
                if (currentPosition > targetPosition) {
                    nextIndex = i;
                    break;
                }
            }
        }
    }

    // check for next item to be selected
    var deleteItem = repeatItems[deleteIndex];
    if (_hasClass(deleteItem, "repeat-index") && nextIndex > -1) {
        var nextItem = repeatItems[nextIndex];

        // reset repeat index manually since it won't change when it is set to
        // the item to delete and there is a following item
        deleteItem.className = "repeat-item";
        nextItem.className = "repeat-item repeat-index";
    }

    // delete item
    repeatElement.removeChild(deleteItem);

    // check for header to be removed
    if (targetPosition == 1 && nextIndex == -1 && headerIndex > -1) {
        _debug("Responder.deleteRepeatItem: deleting repeat header");

        // delete header
        repeatElement.removeChild(repeatItems[headerIndex]);
    }
};

/**
 * Deletes a selector item.
 *
 * @param target the selector id.
 * @param value the delete position.
 */
Responder.deleteSelectorItem = function(target,value) {
    _debug("Responder.deleteSelectorItem: " + target + "='" + value + "'");

    var itemset = document.getElementById(target);
    var items = itemset.childNodes;
    var currentPosition = 0;
    var targetPosition = parseInt(value);
    var deleteIndex = -1;

    for (i = 0; i < items.length; i++) {
        // lookup elements
        if (items[i].nodeType == 1) {
            // lookup repeat item
            if (_hasClass(items[i], "selector-item")) {
                currentPosition++;

                // store delete index (position *at* delete item)
                if (currentPosition == targetPosition) {
                    deleteIndex = i;
                    break;
                }
            }
        }
    }

    // delete item
    itemset.removeChild(items[deleteIndex]);
};

/**
 * Sets the index of a repeat.
 *
 * @param target the repeat id.
 * @param value the repeat index.
 */
Responder.setRepeatIndex = function(target,value) {
    _debug("Responder.setRepeatIndex: " + target + "='" + value + "'");

    var currentPosition = 0;
    var targetPosition = parseInt(value);

    if (targetPosition > 0) {
        var targetElement;
        if (Responder.PROTOTYPE_CLONES.length > 0) {
            // nested repeat
            var enclosingPrototype = Responder.PROTOTYPE_CLONES[Responder.PROTOTYPE_CLONES.length - 1];
            var originalId = Responder.GENERATED_IDS[Responder.GENERATED_IDS.length - 1][target];
            targetElement = _getElementById(enclosingPrototype, originalId);
        }
        else {
            // top-level repeat
            targetElement = document.getElementById(target);
        }

        var repeatElement = Responder._getRepeatNode(targetElement);
        var repeatItems = repeatElement.childNodes;
        for (i = 0; i < repeatItems.length; i++) {
            // lookup elements
            if (repeatItems[i].nodeType == 1) {
                // lookup repeat items
                if (_hasClass(repeatItems[i], "repeat-item")) {
                    currentPosition++;

                    if (currentPosition == targetPosition) {
                        // select item
                        repeatItems[i].className = "repeat-item repeat-index";
                    }
                    else {
                        // deselect item
                        repeatItems[i].className = "repeat-item";
                    }
                }
            }
        }
    }
};

Responder._updateSelectors = function(control, readonly) {
    var id = control.id.substring(0, control.id.indexOf("-value"));
    var count = eval("document.betterform." + control.name + ".length;");
    var selector;
    var label;
    var position
    for (i = 0 ; i < count; i++){
        position = i + 1;
        selector = eval("document.betterform." + control.name + "[" + i + "]");
        label = document.getElementById(id + "-" + position + "-label");
        if (readonly) {
            selector.setAttribute("disabled", "disabled");
            if (label != null) {
                label.setAttribute("disabled", "disabled");
            }
        }
        else {
            selector.removeAttribute("disabled");
            if (label != null) {
                label.removeAttribute("disabled");
            }
        }
    }
};

Responder._updateCalendar = function(id, type, readonly) {
    var display = document.getElementById(id + "-" + type + "-display");
    display.disabled = readonly;
    display.readonly = !readonly;

    var button = document.getElementById(id + "-" + type + "-button");
    if (readonly) {
        _replaceClass(button, "enabled", "disabled");
    }
    else {
        _replaceClass(button, "disabled", "enabled");
    }
};

Responder._getRepeatNode = function(element) {
    var items = element.childNodes;

    // special IE table handling cause IE inserts an virtual 'TBODY' element at runtime. Mozilla does not!
    if (items.length == 1 && items[0].nodeName.toLowerCase() == "tbody") {
        return items[0];
    }

    return element;
};

Responder._applyGeneratedIds = function(element, ids) {
    var id = element.getAttribute("id");
    if (id) {
        var generatedId = ids[id];
        if (generatedId) {
            _debug("applying '" + generatedId + "' to '" + id + "'");
            element.setAttribute("id", generatedId);
        }
    }

    // hack for hidden inputs and multiple inputs with same name. this doesn't
    // work for radiobuttons in IE, since input fields dyamically have to be
    // created as follows:
    //     document.createElement("<INPUT NAME='...''></INPUT");
    // (this is not a joke, but has been found at msdn.microsoft.com/ie/webdev)
    // todo: configurable data prefix, at least ;-(
    var BETTERFORM_DATA_PREFIX = "d_";
    if (element.nodeName.toLowerCase() == "input" && element.name && element.name.substring(0, 2) == BETTERFORM_DATA_PREFIX) {
        id = element.name.substring(2, element.name.length);
        var otherId = ids[id];
        if (otherId) {
            _debug("applying '" + BETTERFORM_DATA_PREFIX + otherId + "' to '" + BETTERFORM_DATA_PREFIX + id + "'");
            element.setAttribute("name", BETTERFORM_DATA_PREFIX + otherId);
        }
    }

    var toApply = element.childNodes;
    for (var index = 0; index < toApply.length; index++) {
        // DON'T EVER USE SOMETHING LIKE var node = toApply[index]; IN THIS LOOP !!!
        if (toApply[index].nodeType == 1) {
            Responder._applyGeneratedIds(toApply[index], ids);
        }
    }
};
