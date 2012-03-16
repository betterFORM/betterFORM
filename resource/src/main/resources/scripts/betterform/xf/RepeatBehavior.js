dojo.provide("betterform.xf.RepeatBehavior");

dojo.require("betterform.xf.Repeat");

betterform.repeatBehavior = {

    /*
     ###########################################################################################
     matching all elements with .xfRepeat and instanciate a Repeat Object for each of them.
     */
    '.xfRepeat.xfFullRepeat':function(n) {
        // console.debug("\n\nRepeatBehaviour: XFControl found: ",n, " \n\n");
        new betterform.xf.Repeat({id:n.id}, n);
    },

    '.xfRepeat .xfRepeatItem':function(n) {

        dojo.connect(n,"onclick",function(evt){
            // console.debug("clicked on repeat Item ",n);
            if(dojo.hasClass(n, "xfRepeatIndex")){
                // console.debug("repeat item " + n.id + " allready selected");
                return;
            }
            var repeatItems = n.parentNode.childNodes;
            // console.debug("repeatItems: ",repeatItems);
            dojo.forEach(repeatItems,
                function(entry) {
                    if (dojo.hasClass(entry, "xfRepeatIndex")) { dojo.removeClass(entry, "xfRepeatIndex");}
                    if (dojo.hasClass(entry, "xfRepeatIndexPre")) { dojo.removeClass(entry, "xfRepeatIndexPre");}
                }
            );
            dojo.addClass(n, "xfRepeatIndexPre");
            dojo.attr(n, "selected", "true");

            var position = 0;
            dojo.forEach(repeatItems,
                function(entry, index) {
                    if(dojo.attr(entry, "selected") == "true"){
                        entry.removeAttribute("selected");
                        position = index + 1;

                    }
                }
            );
            // console.debug("Position is: " + position);
            // console.debug("Repeat Node is: " , n.parentNode);
            var repeatId = dojo.attr(n.parentNode,"repeatid");
            // console.debug("Repeat Id is: " + repeatId);

            fluxProcessor.setRepeatIndex(repeatId, position);

        });

    }
};
