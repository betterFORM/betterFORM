require.config({
    baseUrl: '../../../resources/scripts',
    paths: {
        polymer: 'polymer/polymer.min',
        platform: 'polymer/platform.min',
        jQuery: 'jquery-ui/jquery-1.9.1',
	    jQueryUI: 'jquery-ui/ui/jquery-ui',
	    domReady: 'domReady/domReady',
        app: "../../forms/tests/require.js/js/app"
    },
    shim: {
        "jQueryUI": {
            export:"$" ,
            deps: ['jQuery']
        }
    }
});

requirejs(["app/main"]);

