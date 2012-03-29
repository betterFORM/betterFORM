/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

dependencies = {
	//Strip all console.* calls except console.warn and console.error.
	// stripConsole: "normal",

    layers: [
        /** excluded Dojo JavaScript Resource */
        {
            // This layer will be discarded, it is just used
            // to specify some modules that should not be included
            // in a later layer, but something that should not be
            // saved as an actual layer output. The important property
            // is the "discard" property. If set to true, then the layer
            // will not be a saved layer in the release directory.
            name: "bf.discard",
            resourceName: "bf.discard",
            discard: true,
            // Path to the copyright file must be relative to
            // the util/buildscripts directory, or an absolute path.
            copyrightFile: "license.txt",
            dependencies: [
                    "dojo.string"
            ]
        },

        /**
         * Dojo / Dijit JavaScript Resource
         */
        {
            // the dojo stock layer. It builds a "roll up" for
            // dijit.dijit which includes most of the infrastructure needed to
            // build widgets in a single file. We explicitly ignore the string
            // stuff via the previous exclude layer and don't add dojo.js!

            // where the output file goes, relative to the dojo dir
            name: "../dijit/dijit.js",
            // what the module's name will be, i.e., what gets generated
            // for dojo.provide(<name here>);
            resourceName: "dijit.dijit",
            // modules not to include code for
            layerDependencies: [
                "string.discard"
            ],
            // modules to use as the "source" for this layer
            dependencies: [
                "dijit.dijit"
            ]
        },

        {
            name: "../bf/betterform-XHTML.js",
            resourceName: "bf.xhtml",
            dependencies: [
                "bf.BfRequiredXHTML"
            ]
        }
    ],

    prefixes: [
        // the system knows where to find the "dojo/" directory, but we
        // need to tell it about everything else. Directories listed here
        // are, at a minimum, copied to the build directory.
        [ "dijit", "../dijit" ],
        [ "dojox", "../dojox" ],
        [ "bf", "../bf", "license.txt" ]
    ]
}

// If you choose to optimize the JS files in a prefix directory (via the
// optimize= build parameter), you can choose to have a custom copyright
// text prepended to the optimized file. To do this, specify the path to a
// file tha contains the copyright info as the third array item in the
// prefixes array. For instance:
    //      prefixes: [
    //              [ "betterform", "/path/to/betterform", "/path/to/betterform/copyright.txt"]
    //      ]
    //
// NOTE:
//    If no copyright is specified in this optimize case, then by default,
//    the Dojo copyright will be used.
