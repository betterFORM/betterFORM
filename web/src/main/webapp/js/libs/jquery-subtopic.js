/**
 * jQuery Pub/Sub plugin for Loosely Coupled logic.
 *
 * Based on Hiroshi Kuwabara's jQuery version of Peter Higgins' port 
 * from Dojo to JQuery.
 *
 * Hiroshi Kuwabara's JQuery version
 * https://github.com/kuwabarahiroshi/bloody-jquery-plugins/blob/master/pubsub.js
 *
 * Peter Higgins' port from Dojo to jQuery
 * https://github.com/phiggins42/bloody-jquery-plugins/blob/master/pubsub.js
 *
 * Perfomance enhanced version based on Luís Couto
 * https://github.com/phiggins42/bloody-jquery-plugins/blob/55e41df9bf08f42378bb08b93efcb28555b61aeb/pubsub.js
 */
(function($) {
    "use strict";
    var cache = {},
    /**
     *    Events.publish
     *    e.g.: Events.publish("/Article/added", [article], this);
     *
     *    @class Events
     *    @method publish
     *    @param topic {String}
     *    @param args    {Array}
     *    @param scope {Object} Optional
     */
    publish = function(topic, args, scope) {
        var topics = topic.split('/');
        while (topics[0]) {
            if (cache[topic]) {
                var thisTopic = cache[topic],
                    i = thisTopic.length - 1;

                for (i; i >= 0; i -= 1) {
                    thisTopic[i].apply(scope || this, args || []);
                }
            }
            topics.pop();
            topic = topics.join('/');
        }
    },
    /**
     *    Events.subscribe
     *    e.g.: Events.subscribe("/Article/added", Articles.validate)
     *
     *    @class Events
     *    @method subscribe
     *    @param topic {String}
     *    @param callback {Function}
     *    @return Event handler {Array}
     */
    subscribe = function(topic, callback) {
        if (!cache[topic]) {
            cache[topic] = [];
        }
        cache[topic].push(callback);
        return [topic, callback];
    },
    /**
     *    Events.unsubscribe
     *    e.g.: var handle = Events.subscribe("/Article/added", Articles.validate);
     *        Events.unsubscribe(handle);
     *
     *    @class Events
     *    @method unsubscribe
     *    @param handle {Array}
     *    @param completly {Boolean}
     *    @return {type description }
     */
    unsubscribe = function(handle, completly) {
        var t = handle[0],
            i = cache[t].length - 1;

        if (cache[t]) {
            for (i; i >= 0; i -= 1) {
                if (cache[t][i] === handle[1]) {
                    cache[t].splice(cache[t][i], 1);
                    if (completly) {
                        delete cache[t];
                    }
                }
            }
        }
    };

    $.publish = publish;
    $.subscribe = subscribe;
    $.unsubscribe = unsubscribe;

}(jQuery));