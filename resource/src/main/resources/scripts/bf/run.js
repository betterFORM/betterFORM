    require({
    baseUrl: '',

    packages: [
        'dojo',
        'dijit',
        'dojox',
        'bf'

        // For reference, this is what a more verbose package declaration looks like.
    ],

    // This is a hack. In order to allow app/main and app/run to be built together into a single file, a cache key needs
    // to exist here in order to force the loader to actually process the other modules in the file. Without this hack,
    // the loader will think that code for app/main has not been loaded yet and will try to fetch it again, resulting in
    // a needless extra HTTP request.
    cache: {}
// Require 'app'. This loads the main application file
}, [ 'bf' ]);
