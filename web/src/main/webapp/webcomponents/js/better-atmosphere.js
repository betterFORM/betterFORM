xtag.register('better-atmosphere', {


    lifecycle: {


        created: function () {
            console.debug("better-atmosphere created", this);
            this.socket = $.atmosphere;
            that = this;

            this.request = {
                url: '/betterform/msg',
                contentType: "application/json; charset=UTF-8",
                logLevel: 'debug',
                transport: 'sse',
                trackMessageLength : true,
                fallbackTransport: 'long-polling',
                callback:that.myCall,

                onOpen: function (response) {
                    console.log('Atmosphere connected using ' + response.transport);
                },

                onMessage: function (response) {
                    var message = response.responseBody;

                    console.log("response: ",JSON.stringify(message));
                    var json = null;
                    try {
                        json = jQuery.parseJSON(message);
                    } catch (e) {
                        console.log('This doesn\'t look like a valid JSON: ', message);
                        return;
                    }

                    if("betterform-state-changed" == json.type){
                        var targetId = json.contextInfo.targetId;
                        console.log("executing betterform-state-changed targetId:", targetId);
                        try {
                            document.querySelector("#" + targetId).updateToRemoteState(json);
                        }
                        catch(e){
                            console.log('Could not execute method: ', targetId);
                        }
                    }

//                    console.log("targetId: ", json.contextInfo.targetId);
//                    console.log("eventType: ", json.type);
//                    console.log("value: ", json.value);
                    $('<div/>',{
                        text:json.type
                    }).appendTo("better-atmosphere");
                },
                onClose: function (response) {
                    console.log("onClose called");
                },

                myCall:function(reponse){
                    console.log("callback response:",response);
                }
            };
            console.log("this.socket: ", this.socket);
            this.subSocket = that.socket.subscribe(this.request);
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
            this.subSocket.push(msg);
        }
    }
});
