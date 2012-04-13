define(["../../../lib/dojo-release-1.6.1-src/dojo/behavior","dojo/dom-attr","dojo/_base/connect","dojo/dom-class","dojo/_base/array","dojo/query","bf/container/Repeat"],
    function(behavior,domAttr,connect,domClass,array,query, Repeat) {
        return {

        /*
         ###########################################################################################
         matching all elements with .xfRepeat and instanciate a Repeat Object for each of them.
         */
        '.xfRepeat':function(n) {
            // console.debug("\n\nRepeatBehaviour: found Repeat: ",n, " \n\n");
            new Repeat({repeatId:domAttr.get(n,"repeatId")}, n);
        },

        '.xfRepeat .xfRepeatItem':function(n) {
            // console.debug("\n\nRepeatBehaviour:  found xfRepeatItem: ",n, " \n\n");



        }
    }
});
