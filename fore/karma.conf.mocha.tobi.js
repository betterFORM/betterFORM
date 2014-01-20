// Karma configuration
// Generated on Fri Jan 17 2014 10:52:25 GMT+0100 (CET)

module.exports = function(config) {
  config.set({

    // base path, that will be used to resolve files and exclude
    basePath: '.',


    // frameworks to use
    frameworks: ['mocha'],
    
    files: [
        'test/karma/tools/mocha-htmltest.js',
        'test/karma/conf/mocha.conf.js',
	'node_modules/chai/chai.js',
	'app/bower_components/platform/platform.js',
	'test/karma/tools/karma-include-polymer.js',
	'test/karma/js/*.js',
	{pattern: 'test/karma/**/*.css', included: false},
	{pattern: 'test/karma/**/*.html', included: false},
	{pattern: 'test/karma/**/*.js', included: false},
	{pattern: 'app/elements/*.html', included: false},
	{pattern: 'app/bower_components/polymer/*.html', included: false},
	{pattern: 'app/bower_components/polymer/polymer.js', included: false},
	{pattern: 'app/bower_components/polymer/polymer.js.map', included: false},
	{pattern: 'app/bower_components/platform/platform.js.map', included: false},
	{pattern: 'app/bower_components/jquery/jquery.js', included: false},
	{pattern: 'app/bower_components/jquery-ui/ui/jquery-ui.js', included: false},
	{pattern: 'test/karma/tools/*.js', included: false}
	],


    // list of files to exclude
    exclude: [
      
    ],


    // test results reporter to use
    // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
    reporters: ['progress'],


    // web server port
    port: 9876,


    // enable / disable colors in the output (reporters and logs)
    colors: true,


    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_DEBUG,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,


    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera (has to be installed with `npm install karma-opera-launcher`)
    // - Safari (only Mac; has to be installed with `npm install karma-safari-launcher`)
    // - PhantomJS
    // - IE (only Windows; has to be installed with `npm install karma-ie-launcher`)
    browsers: ['Chrome'],


    // If browser does not capture in given timeout [ms], kill it
    captureTimeout: 60000,


    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false,
    
    plugins: [
      'karma-mocha',
      'karma-firefox-launcher',
      'karma-chrome-launcher'
    ]
  });
};
