"use strict";
// this function is strict...

function Betterform() {
    // recipes for good pasta sauces
    this.events= {
        'xforms': ["xforms-value-changed", "xforms-select", "xforms-focus"],
        'betterform': ["applyChanges"]
    };
    this.currentEvents= [];
}
Betterform.prototype.add = function (event) {
    if(this.events.xforms.indexOf(event) !== -1 || this.events.betterform.indexOf(event) !== -1){
        this.currentEvents.push(event);
        return true;
    }
    return false;
};


Betterform.prototype.fireEvents= function () {
    if(this.currentEvents && this.currentEvents.size > 0){
        for (var event in this.currentEvents) {
            this.fire(event);
        }
    }
    return undefined;
};

Betterform.prototype.fire= function (event) {
    console.log("firing event: '", event, "'");
    return false;
};

