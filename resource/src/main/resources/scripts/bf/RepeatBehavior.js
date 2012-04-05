define(["dojo/behavior","dojo/dom-attr","dojo/_base/connect","dojo/dom-class","dojo/_base/array","bf/Repeat"],
    function(behavior,domAttr,connect,domClass,array,Repeat) {
        return {

        /*
         ###########################################################################################
         matching all elements with .xfRepeat and instanciate a Repeat Object for each of them.
         */
        '.xfRepeat.aFull':function(n) {
            // console.debug("\n\nRepeatBehaviour: found xfFullRepeat: ",n, " \n\n");
            new Repeat({id:n.id}, n);
        },

        '.xfRepeat .xfRepeatItem':function(n) {
            // console.debug("\n\nRepeatBehaviour:  found xfRepeatItem: ",n, " \n\n");

            connect.connect(n,"onclick",function(evt){
                 // console.debug("clicked on repeat Item ",n);
                if(domClass.contains(n, "xfRepeatIndex")){
                    // console.debug("repeat item " + n.id + " allready selected");
                    return;
                }
                var repeatItems = n.parentNode.childNodes;
                // console.debug("repeatItems: ",repeatItems);
                array.forEach(repeatItems,
                    function(entry) {
                        if (domClass.contains(entry, "xfRepeatIndex")) { domClass.remove(entry, "xfRepeatIndex");}
                        if (domClass.contains(entry, "xfRepeatIndexPre")) { domClass.remove(entry, "xfRepeatIndexPre");}
                    }
                );
                domClass.add(n, "xfRepeatIndexPre");
                domAttr.set(n, "selected", "true");

                var position = 0;
                array.forEach(repeatItems,
                    function(entry, index) {
                        if(domAttr.get(entry, "selected") == "true"){
                            entry.removeAttribute("selected");
                            position = index + 1;

                        }
                    }
                );
                // console.debug("Position is: " + position);
                // console.debug("Repeat Node is: " , n.parentNode);
                var repeatId = domAttr.get(n.parentNode,"repeatid");
                // console.debug("Repeat Id is: " + repeatId);

                fluxProcessor.setRepeatIndex(repeatId, position);

            });

        }
    }
});
