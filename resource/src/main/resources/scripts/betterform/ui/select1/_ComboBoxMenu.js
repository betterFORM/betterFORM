dojo.provide("betterform.ui.select1._ComboBoxMenu");

// Get dependencies
dojo.require("dijit.form.ComboBox");

// Declare the class
dojo.declare("betterform.ui.select1._ComboBoxMenu", dijit.form._ComboBoxMenu,
    {

        createOptions: function(results, dataObject, labelFunc){
            // summary:
            //		Fills in the items in the drop down list
            // results:
            //		Array of dojo.data items
            // dataObject:
            //		dojo.data store
            // labelFunc:
            //		Function to produce a label in the drop down list from a dojo.data item

            // CUSTOM BETTERFORM PART
            // CUSTOM BETTERFORM PART
            // CUSTOM BETTERFORM PART
/*
            console.debug("\n\nbetterform.ui.select1._ComboBoxMenu: results:", results);
            console.debug("\n\nbetterform.ui.select1._ComboBoxMenu: dataObject:", dataObject);
            console.debug("\n\nbetterform.ui.select1._ComboBoxMenu: labelFunc:", labelFunc);
*/
            dojo.forEach(results, function(item) {
                // console.debug("update item: ", item);
                var itemId = dojo.attr(item, "id");
                if (itemId) {
                    // console.debug("searching for option: ", itemId);
                    var option = dojo.byId(itemId);
                    // console.debug("found option: ", option);
                    dojo.attr(item,"value", dojo.attr(option,"value"));
                    item.innerHTML = option.innerHTML;
                }
            });

            // ORIGINAL DOJO CODE (DOES NOT WORK WITH INHERITANCE)
            // ORIGINAL DOJO CODE (DOES NOT WORK WITH INHERITANCE)
            // ORIGINAL DOJO CODE (DOES NOT WORK WITH INHERITANCE)

            this.previousButton.style.display = (dataObject.start == 0) ? "none" : "";
            dojo.attr(this.previousButton, "id", this.id + "_prev");
            dojo.forEach(results, function(item, i){
                var menuitem = this._createOption(item, labelFunc);
                dojo.attr(menuitem, "id", this.id + i);
                this.domNode.insertBefore(menuitem, this.nextButton);
            }, this);
            // display "Next . . ." button
            var displayMore = false;
            //Try to determine if we should show 'more'...
            if(dataObject._maxOptions && dataObject._maxOptions != -1){
                if((dataObject.start + dataObject.count) < dataObject._maxOptions){
                    displayMore = true;
                }else if((dataObject.start + dataObject.count) > dataObject._maxOptions && dataObject.count == results.length){
                    //Weird return from a datastore, where a start + count > maxOptions
                    // implies maxOptions isn't really valid and we have to go into faking it.
                    //And more or less assume more if count == results.length
                    displayMore = true;
                }
            }else if(dataObject.count == results.length){
                //Don't know the size, so we do the best we can based off count alone.
                //So, if we have an exact match to count, assume more.
                displayMore = true;
            }
            this.nextButton.style.display = displayMore ? "" : "none";
            dojo.attr(this.nextButton,"id", this.id + "_next");
            return this.domNode.childNodes;
        }
});