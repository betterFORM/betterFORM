// Karma configuration
// Generated on Fri Jan 17 2014 10:52:25 GMT+0100 (CET)
'use strict';

module.exports = function(config) {
    var selectedBrowsers;
    var os = require('os').type();
    if (os === 'Darwin') {
        // selectedBrowsers = ['Chrome', 'ChromeCanaryExperimental', 'Firefox', 'Safari'];
        selectedBrowsers = ['Chrome', 'Firefox'];
    } else if (os === 'Windows_NT') {
        selectedBrowsers = ['Chrome', 'Firefox', 'IE'];
    } else {
        selectedBrowsers = ['Chrome', 'Firefox'];

    }

  config.set({

    // base path, that will be used to resolve files and exclude
    basePath: '../../../',


    // frameworks to use
    frameworks: ['mocha'],
    
    files: [
        'test/karma/tools/mocha-htmltest.js',
        'test/karma/conf/mocha.conf.js',
        'node_modules/chai/chai.js',
        'app/bower_components/platform/platform.js',
        'test/karma/tools/karma-include-polymer.js',
        'test/karma/**/*.js',
        {pattern: 'test/karma/**/*.html', included: false},
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
    exclude: [],

    // test results reporter to use
    // possible values: 'dots', 'progress', 'junit', 'growl', 'coverage'
    reporters: ['progress'],

    preprocessors: null,
    // web server port
    port: 9876,

    // enable / disable colors in the output (reporters and logs)
    colors: true,

    // level of logging
    // possible values: config.LOG_DISABLE || config.LOG_ERROR || config.LOG_WARN || config.LOG_INFO || config.LOG_DEBUG
    logLevel: config.LOG_WARN,


    // enable / disable watching file and executing tests whenever any file changes
    autoWatch: true,

      customLaunchers: {
          ChromeCanaryExperimental: {
              base: 'ChromeCanary',
              name: 'ChromeCanaryExperimental',
              flags: ['--enable-experimental-web-platform-features']
          },
          bs_iphone5: {
              base: 'BrowserStack',
              device: 'iPhone 5',
              os: 'ios',
              os_version: '6.0'
          }
      },
    // Start these browsers, currently available:
    // - Chrome
    // - ChromeCanary
    // - Firefox
    // - Opera (has to be installed with `npm install karma-opera-launcher`)
    // - Safari (only Mac; has to be installed with `npm install karma-safari-launcher`)
    // - PhantomJS
    // - IE (only Windows; has to be installed with `npm install karma-ie-launcher`)
      browsers: selectedBrowsers,


    // If browser does not capture in given timeout [ms], kill it
    captureTimeout: 50000,


    // Continuous Integration mode
    // if true, it capture browsers, run tests and exit
    singleRun: false,

    // report which specs are slower than 500ms
    // CLI --report-slower-than 500
    reportSlowerThan: 500,

    plugins: [
        'karma-mocha',
        'karma-browserstack-launcher',
        'karma-chrome-launcher',
        'karma-firefox-launcher',
        'karma-ie-launcher',
        'karma-ios-launcher',
        'karma-safari-launcher',
        'karma-script-launcher',
        'karma-crbot-reporter'
    ]
  });
};
