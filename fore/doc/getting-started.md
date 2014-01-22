# Getting started developing Fore

## Introduction

Fore is a front-end toolkit based upon Polymer (Web Components) and uses Nodejs, Bower Gruntjs as its primary tools.
This document describes the basics of setting up a development environment and the procedures to run, test and deploy
your project.


## Requirements + Preparations

1. nodejs must be installed. Please refer to their homepage for a native installer for your platform and follow the instructions.
2. To install the Fore dependencies call `npm install`. This will download the dependent libs into a **local** node_modules directory.
3. Install Bower dependency manager with `bower install`. 
4. Install Polymer - please refer to their homepage for latest installation instructions at http://www.polymer-project.org/getting-the-code.html


## Update build infrastructure

### Add node dependencies to project
 to update package.js run `npm install --save-dev [grunt-task-name]`

### Update bower controller js components
1. to update bower run `bower update`

## Running Fore

`grunt server` - by executing this command the grunt http server and your default browser will be started with index.html.

This target will support live reloading meaning that changes will become visible in your browser right after you hit 'save' in your editor without the need to explicitly reload the page.


## Deployment
To create an optimized version of the App run
1. grunt dist
This will optimize all JavaScript, CSS and Image files and create an optimized app in the created dist directory


## Testing

Fore curently provides two different test setups. 'Polymer' Tests utilizing Karma and Chai and Webtests via DalekJS.

### HTML Tests
The Polymer Webcomponents are tested with Chai and Karma. All tests are placed beneath the folder fore/test/karma.
All test html forms are placed in $KARMA/html. Be aware that the execution of the html test is triggered via the
JavaScript function htmlTest in $KARMA/fore-test.js (e.q. htmlTest('html/fore-form.html');

The Karma config file can be found at fore/test/karma/conf/karma.conf.js

#### Execute HTML tests

* `grunt test-karma`


#### Web Resources:

* http://japhr.blogspot.de/2013/11/getting-started-with-karma-for-polymer.html


### Webtests
All Webtests are written in and executed by the DalekJS Framework The Fore DalekJS Tests are placed at $FORE/test/dalekjs

To execute all tests on the app directory (exploded resources, development) execute

* `grunt test-dev`

analogue all test on the dist directory (optimized resources, deployment) can be run by calling

* `grunt test-dist`


## Trouble Shooting
If you experience any trouble with node (npm) or bower try the following command to clean up your caches:

* `npm cache clean` and
* `bower cache clean`


## Utilized Build Tools
Tools to build and run 'fore'

### Node.js
Platform for building fast, scalable network applications

* Website: http://nodejs.org/

### Bower
Package Manager for the Web (like Maven for Java and EXPath for XQuery)

* Website: http://bower.io/


## Utilized JS Frameworks
JavaScript Frameworks used to implement 'fore' and which are available to form authors

### Polymer
WebComponents for any browser

* Website: http://www.polymer-project.org/

### JQuery
JavaScript library to make developers life easier

* Website: http://jquery.com/


## Utilized Test Tools

### Karma
Karma is a testing environment (which is testing testing framework agnostic). It can execute test written in [Jasmine](http://pivotal.github.io/jasmine/), [Mocha](http://visionmedia.github.io/mocha/), [QUnit](http://qunitjs.com/) or use your own framework (via a simple adapter).

* Website: http://karma-runner.github.io/


### Mocha
JavaScript Test Framework

* Website: http://visionmedia.github.io/mocha/

### Chai
Chai is a BDD ((Behaviour Driven Design) / TDD (Test Driven Developtment) assertion library for node and the browser

* Website: http://chaijs.com/

### DalekJs
UI Testing Tool / Automated Cross Browser Testing

* Website: http://dalekjs.com/
* Sample project including automated screenshots of every test: https://github.com/kmturley/dalekjs-crossbrowser

