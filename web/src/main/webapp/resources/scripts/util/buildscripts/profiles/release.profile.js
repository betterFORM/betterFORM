var profile = {
    // basePath is relative to the directory containing this profile file; in this case, it is being set to the
    // src/ directory, which is the same place as the baseUrl directory in the loader configuration. (If you change
    // this, you will also need to update run.js).
    basePath: "./",

    // This is the directory within the release directory where built packages will be placed. The release directory
    // itself is defined by build.sh. You really probably should not use this; it is a legacy option from very old
    // versions of Dojo (like, version 0.1). If you do use it, you will need to update build.sh, too.
    // releaseName: '',

    // Builds a new release.
    action: 'release',

    // Strips all comments from CSS files.
    cssOptimize: 'comments',

    // Excludes tests, demos, and original template files from being included in the built version.
    mini: true,

    // Uses Closure Compiler as the JavaScript minifier. This can also be set to "shrinksafe" to use ShrinkSafe.
    // Note that you will probably get some “errors” with CC; these are generally safe to ignore, and will be
    // fixed in a later version of Dojo. This defaults to "" (no compression) if not provided.
    optimize: 'closure',

    // We’re building layers, so we need to set the minifier to use for those, too. This defaults to "shrinksafe" if
    // it is not provided.
    layerOptimize: 'closure',

    // Strips all calls to console functions within the code. You can also set this to "warn" to strip everything
    // but console.error, and any other truthy value to strip everything but console.warn and console.error.
    stripConsole: 'normal',

    // The default selector engine is not included by default in a dojo.js build in order to make mobile builds
    // smaller. We add it back here to avoid that extra HTTP request. There is also a "lite" selector available; if
    // you use that, you’ll need to set selectorEngine in bf/run.js too. (The "lite" engine is only suitable if you
    // are not supporting IE7 and earlier.)
    selectorEngine: 'acme',
    releaseDir: "./release",

    packages:[
        {
            name: "dojo",
            location: "./dojo"
        },
        {
            name: "dijit",
            location: "./dijit"
        },
        {
            name: "dojox",
            location: "./dojox"
        },
        {
            name: "bf",
            location: "./bf"
        }
/*,
        {
            name: "dwr",
            location: "./dwr"
        }
        */
    ],

    layers: {
        'dojo/dojo': {
            // In addition to the loader (dojo/dojo) and the loader configuration file (bf/run), we’re also including
            // the main application (bf/main) and the dojo/i18n and dojo/domReady modules because they are one of the
            // conditional dependencies in bf/main (the other being bf/Dialog) but we don’t want to have to make
            // extra HTTP requests for such tiny files.
            include:[
                'dojo/require',
                'dojo/parser',
                'dojo/dom',
                'dojo/dom-style',
                'dojo/dom-attr',
                'dojo/query',
                'dojo/fx',
                'dojo/fx/easing',
                'dojo/io/iframe',
                'dojo/_base/window',
                'dojo/has',
                'dojo/i18n',
                'dojo/date/locale',
                'dojo/cldr/nls/en/gregorian'
            ],
            boot: true,
            customBase: true
        },
        'bf/core':{
            include:[
                'dojo/_base/declare',
                'dojo/_base/connect',
                'dojo/_base/lang',
                'dojo/_base/array',

                'dojo/dom-class',
                'dojo/dom-construct',
                'dojo/domReady',

                'dijit/registry',

                'bf/common/Alert',
                'bf/common/AlertInline',

                'bf/container/Repeat',
                'bf/factory/FactoryContainer',
                'bf/factory/FactoryInput',
                'bf/factory/FactoryOutput',
                'bf/factory/FactorySecret',
                'bf/factory/FactorySelect',
                'bf/factory/FactorySelect1',
                'bf/factory/FactoryTextarea',
                'bf/factory/FactoryTrigger',

                'bf/select/SelectCheckBox',
                'bf/select/Select1Radio',
                'bf/select/Select1ComboBox',

                'bf/ClientServerEvent',
                'bf/Mapping',
                'bf/MappingProcessor',
                'bf/util',
                'bf/XFBinding',
                'bf/XFControl',
                'bf/XFormsModelElement',
                'bf/XFormsProcessor',
                'bf/XFProcessor'

            ],
            boot: false,
            customBase: true,
            exclude: [
                "dojo/dojo"
            ]

        },
        'bf/slider':{
            include:[
                'dijit/form/HorizontalSlider',
                'dijit/form/HorizontalRuleLabels',
                'dijit/form/HorizontalRule',
                'bf/factory/FactoryRange'
            ],
            boot: false,
            customBase: true,
            exclude: [
                "dojo/dojo",
                "bf/core"
            ]
        },
        'bf/tabcontainer':{
            include:[
                'dijit/layout/ContentPane',
                'dijit/layout/TabContainer'
            ],
            boot: false,
            customBase: true,
            exclude: [
                "dojo/dojo",
                "bf/core",
                "bf/slider"
            ]

        },
        'bf/dates':{
            include:[
                'dijit/form/DateTextBox',
                'dijit/form/TimeTextBox',
                'bf/input/DateTime',
                'bf/input/DropDownDate',
                'bf/input/Time'

            ],
            boot: false,
            customBase: true,
            exclude: [
                "dojo/dojo",
                "bf/core",
                "bf/slider",
                "bf/tabcontainer"
            ]

        },
        'bf/upload':{
            include:[
                'bf/factory/FactoryUpload',
                'bf/upload/Upload'

            ],
            boot: false,
            customBase: true,
            exclude: [
                "dojo/dojo",
                "bf/core",
                "bf/slider",
                "bf/tabcontainer",
                "bf/dates"
            ]

        },
        'bf/tooltipAlert':{
            include:[
                'dojo/NodeList-fx',
                'bf/common/AlertToolTip'
            ],
            boot: false,
            customBase: true,
            exclude: [
                "dojo/dojo",
                "bf/core",
                "bf/slider",
                "bf/tabcontainer",
                "bf/dates",
                "bf/upload"
            ]
        },
        'bf/debug': {
            include:[
                'dojo/_firebug/firebug',
                'bf/devtool'
            ],
            exclude: [
                "dojo/dojo",
                "bf/core",
                "bf/slider",
                "bf/tabcontainer",
                "bf/dates",
                "bf/upload",
                "bf/tooltipAlert"
            ],
            boot: false,
            customBase: true
        },


        'bf/bfRelease': {
            include:[
                "bf/core",
                "bf/slider",
                "bf/tabcontainer",
                "bf/dates",
                "bf/upload",
                "bf/tooltipAlert"
            ],

            boot: false,
            customBase: true,
            exclude: [
                "dojo/dojo",
                "bf/debug"
            ]
        },

        'bf/dashboard':{
            include:[
                'dojo/dojo',
                'dijit/form/Button',
                'dijit/form/TextBox',
                'dijit/form/DropDownButton',
                'dijit/TooltipDialog',
                'dijit/Dialog',
                'dojox/fx'
            ],
            boot: false,
            customBase: true
        },
        'bf/featureExplorer': {
            include: [
                'dijit/Menu',
                'dijit/MenuBar',
                'dijit/MenuItem',
                'dijit/PopupMenuBarItem',
                'dijit/Tooltip',
                "dojox/highlight",
                "dojox/highlight/languages/_all",
                "dojox/highlight/widget/Code"
            ],
            exclude: [
                "dojo/dojo",
                "bf/core",
                "bf/slider",
                "bf/tabcontainer",
                "bf/dates",
                "bf/upload",
                "bf/tooltipAlert",
                "bf/debug",
                "bf/bfRelease"
            ],
            boot: false,
            customBase: true
        }
    },

    resourceTags: {
        amd: function (filename, mid) {
            return /\.js$/.test(filename);
        }
    },

    // Providing hints to the build system allows code to be conditionally removed on a more granular level than
    // simple module dependencies can allow. This is especially useful for creating tiny mobile builds.
    // Keep in mind that dead code removal only happens in minifiers that support it! Currently, ShrinkSafe does not
    // support dead code removal; Closure Compiler and UglifyJS do.
    staticHasFeatures: {
        // The trace & log APIs are used for debugging the loader, so we don’t need them in the build
        'dojo-trace-api':0,
        'dojo-log-api':0,

        // This causes normally private loader data to be exposed for debugging, so we don’t need that either
        'dojo-publish-privates':0,

        // We’re fully async, so get rid of the legacy loader
        'dojo-sync-loader':0,

        // dojo-xhr-factory relies on dojo-sync-loader
        'dojo-xhr-factory':0,

        // We aren’t loading tests in production
        'dojo-test-sniff':0
    }
};
