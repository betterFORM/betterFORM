var profile = ( function() {
    copyOnly = function(filename, mid){
        var list = {
            "dwr/dwr.profile.js":1,
            "dwr/package.json":1,
            "dwr/dwr.js":1
        };
        return (mid in list) || /(css|png|jpg|jpeg|gif|tiff)$/.test(filename);
    };

    return {
        resourceTags: {
            test: function (filename, mid) {
                return false;
            },

            // Files that should be copied as-is without being modified by the build system.
            copyOnly: function (filename, mid) {
                return copyOnly(filename,mid);
            },

            // Files that are AMD modules.
            // Files that are AMD modules.
            amd: function (filename, mid) {
                return !copyOnly(mid) && /\.js$/.test(filename);
            }
        },

        trees:[
	        [".", ".", /(\/\.)|(~$)/]
	    ]
    };
})();
