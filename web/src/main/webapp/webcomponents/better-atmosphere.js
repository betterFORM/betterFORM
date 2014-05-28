xtag.register('better-atmosphere', {


    lifecycle: {


        created: function () {
            console.log("better-atmosphere created", this);
            this.socket = $.atmosphere;

            this.request = {
                url: '/betterform/msg',
                contentType: "application/json; charset=UTF-8",
                logLevel: 'debug',
                transport: 'sse',
                fallbackTransport: 'long-polling',

                onOpen: function (response) {
                    console.log('Atmosphere connected using ' + response.transport);
                },

                onMessage: function (response) {
                    var message = response.responseBody;
                    console.log("response: ",response);
                    try {
                        var json = jQuery.parseJSON(message);
                    } catch (e) {
                        console.log('This doesn\'t look like a valid JSON: ', message);
                        return;
                    }
                    console.log("json: ", json);
                },
                onClose: function (response) {
                    console.log("onCl ose called");
                }

            };

            this.subSocket = this.socket.subscribe(this.request);
        },
        inserted: function () {
            console.log("better-atmosphere inserted");
        },
        removed: function () {
            console.log("better-atmosphere removed");
        },
        attributeChanged: function () {
            console.log("better-atmosphere attributeChanged  ");
        }
    },
    events: {
    },
    accessors: {

    },
    methods: {
        pushMsg: function (msg) {
            console.log("pushing: ",msg);
            this.subSocket.push("{'author':'me','message':'" + msg + "'}");
        }
    }
});
