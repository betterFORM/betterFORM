define(["dojo/behavior"],
    function(behavior) {
        return {

        /*
         ###########################################################################################
         matching all elements with .xfRepeat and instanciate a Repeat Object for each of them.
         */
        '.xfRepeat.xfFullRepeat':function(n) {
            // console.debug("\n\nRepeatBehaviour: XFControl found: ",n, " \n\n");
            new bf.Repeat({id:n.id}, n);
        },

        '.xfRepeat .xfRepeatItem':function(n) {

            dojo.connect(n,"onclick",function(evt){
                // console.debug("clicked on repeat Item ",n);
                if(domClass.contains(n, "xfRepeatIndex")){
                    // console.debug("repeat item " + n.id + " allready selected");
                    return;
                }
                var repeatItems = n.parentNode.childNodes;
                // console.debug("repeatItems: ",repeatItems);
                dojo.forEach(repeatItems,
                    function(entry) {
                        if (domClass.contains(entry, "xfRepeatIndex")) { domClass.remove(entry, "xfRepeatIndex");}
                        if (domClass.contains(entry, "xfRepeatIndexPre")) { domClass.remove(entry, "xfRepeatIndexPre");}
                    }
                );
                domClass.add(n, "xfRepeatIndexPre");
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
    }
});
