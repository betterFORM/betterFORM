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
    stripConsole: 'all',

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
            include: [
                //Start braucht man glaub ich nicht//
                'dojo/dojo',
                'dojo/i18n',
                'dojo/domReady',
                'dojo/_base/declare',
                'dojo/dom',
                'dojo/query',
                'dojo/dom-class',
                'dojo/_base/window',
                'dojo/dom-style',
                'dojo/dom-attr',
                'dojo/_base/connect',
                'dojo/_base/lang',
                'dojo/dom-construct',
                'dojo/_base/array',
                'dojo/_base/event',
                'dojo/has',
                'dojo/_base/sniff',

                'dijit/registry',
                //Stop braucht man glaub ich nicht//

                'dojo/_firebug/firebug',
                'dijit/form/HorizontalRuleLabels',
                'dijit/form/HorizontalSlider',
                'dijit/form/HorizontalRule',
                'dijit/layout/ContentPane',
                'dijit/layout/TabContainer',

                'bf/common/Alert',
                'bf/common/AlertInline',
                'bf/common/AlertToolTip',

                'bf/container/Container',
                'bf/container/Repeat',

                'bf/factory/FactoryContainer',
                'bf/factory/FactoryInput',
                'bf/factory/FactoryOutput',
                'bf/factory/FactoryRange',
                'bf/factory/FactorySecret',
                'bf/factory/FactorySelect',
                'bf/factory/FactorySelect1',
                'bf/factory/FactoryTextarea',
                'bf/factory/FactoryTrigger',
                'bf/factory/FactoryUpload',

                'bf/input/DateTime',
                'bf/input/DropDownDate',
                'bf/input/Time',


                'bf/select/Select',
                'bf/select/Select1Radio',
                'bf/select/Select1ComboBox',

                'bf/upload/Upload',

                'bf/ClientServerEvent',
                'bf/devtool',
                'bf/XFBinding',
                'bf/Mapping',
                'bf/MappingProcessor',
                'bf/util',
                'bf/XFControl',
                'bf/XFormsModelElement',
                'bf/XFormsProcessor',
                'bf/XFProcessor'
            ],
            // By default, the build system will try to include dojo/main in the built dojo/dojo layer, which adds a
            // bunch of stuff we don’t want or need. We want the initial script load to be as small and quick as
            // possible, so we configure it as a custom, bootable base.
            boot: true,
            customBase: true
        },
        'bf/dashboard': {
            include: [
                'dojo/fx',
                'dojox/fx',
                'dojo/parser',
                'dojo/fx/easing',

                'dijit/form/Form',
                'dijit/form/Button',
                'dojo/io/iframe',
                'dijit/form/TextBox',
                'dijit/form/DropDownButton',
                'dijit/TooltipDialog',
                'dijit/Dialog',
                'dijit/Tooltip',
                'dijit/nls/de/loading',

                'dojo/NodeList-fx',
                'dojox/widget/Toaster',
                'dojox/layout/FloatingPane'

            ]
        },
        'bf/featureExplorer': {
            include: [
                'dijit/MenuBar',
                'dijit/PopupMenuBarItem',
                'dijit/Tooltip'
            ]
        }

        // In the demo application, we conditionally require bf/Dialog on the client-side, so we’re building a
        // separate layer containing just that client-side code. (Practically speaking, you’d probably just want
        // to roll everything into a single layer, but I wanted to make sure to illustrate multi-layer builds.)
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
