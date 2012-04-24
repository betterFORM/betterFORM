var profile = (function(){

    copyOnly = function(filename, mid){
        var list = {
            "bf/bf.profile":1,
            "bf/package.json":1
        };
        return (mid in list) || /(css|png|jpg|jpeg|gif|tiff)$/.test(filename);
    };

    return {
        cssOptimize: 'comments',
        mini: true,
        optimize: 'closure',
        layerOptimize: 'closure',
        stripConsole: 'all',
        selectorEngine: 'acme',
        action: 'release',

        resourceTags:{
            test: function(filename, mid){
                return false;
            },

            copyOnly: function(filename, mid){
                return copyOnly(filename, mid);
            },

            amd: function(filename, mid){
                return !copyOnly(filename, mid) && /\.js$/.test(filename);
            }
        },

        trees:[
            [".", ".", /(\/\.)|(~$)/]
        ]
    };
})();
