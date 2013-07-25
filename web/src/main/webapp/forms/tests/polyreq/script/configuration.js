require.config({
    baseUrl: '../../../resources/scripts',
    paths: {
        'jquery': 'jquery/jquery-2.0.3',
        'jquery.ui.core': 'jquery-ui/ui/jquery.ui.core',
        'jquery.ui.widget': 'jquery-ui/ui/jquery.ui.widget',
        'jquery.ui.datepicker': 'jquery-ui/ui/jquery.ui.datepicker',
        'jquery.layout': 'jquery.layout-latest',
        'jquery.ui.draggable' : 'jquery-ui/ui/jquery.ui.draggable',
        'jquery.ui.effect' : 'jquery-ui/ui/jquery.ui.effect',
        'jquery.ui.effect-drop' : 'jquery-ui/ui/jquery.ui.effect-drop',
        'jquery.ui.effect-slide' : 'jquery-ui/ui/jquery.ui.effect-slide',
        'jquery.ui.effect-scale' : 'jquery-ui/ui/jquery.ui.effect-scale',
        domReady: 'domReady/domReady',
        main: "requirejs/main"
    },
    shim: {
        'jquery.ui.core': {
            deps: ['jquery']
        },
        'jquery.ui.widget': {
            deps: ['jquery.ui.core']
        },
        'jquery.ui.datepicker': {
            deps: ['jquery.ui.widget']
        },
        'jquery.layout': {
            deps: ['jquery.ui.core']
        }
    }
});

requirejs(["main"]);