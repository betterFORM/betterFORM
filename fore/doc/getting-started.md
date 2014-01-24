# Getting started developing Fore

## Introduction

Fore is a front-end toolkit based upon Polymer (Web Components) and uses nodejs, Bower and Gruntjs as its primary tools.
This document describes the basics of setting up a development environment and the procedures to run, test and deploy
your project.


## Requirements + Preparations

1. Checkout the betterFORM sources from <https://github.com/betterFORM/betterFORM> into a local workspace.
1. nodejs must be installed. Please refer to their [homepage](http://nodejs.org/)  for a native installer for your platform and follow the instructions.
1. To install the Fore dependencies cd into 'fore' directory and call `npm install`. This will download the dependent libs into a **local** node_modules directory.
1. Install Bower dependency manager with `bower install`. 
1. Install Polymer by calling `bower install --save Polymer/polymer`


## Install/update dependendies

### Install Fore dependencies 

To update package.json run: 

`npm install --save-dev [grunt-task-name]`

### Update Fore dependendies

Fore uses Bower for managing its dependencies. To update (or install) the dependencies run:

`bower update`

## Running Fore with live reload

`grunt server` 

executes the grunt http server and your default browser will be started with index.html.

Changes will become visible in your browser right after you hit 'save' in your editor without the need to reload the page.


## Deployment
To create an optimized version of the App run:

`grunt dist`

This will optimize all JavaScript, CSS and image files and create an optimized app in the dist directory (will be created if necessary).


## Testing

Fore curently provides two different test setups. 'Polymer' tests using Karma and Chai and Webtests via DalekJS.

### HTML Tests
The Fore Web Components are tested with Chai and Karma. All tests are placed beneath the folder fore/test/karma.

All html tests are located in karma/html. Be aware that the execution of the html test is triggered via the
JavaScript function htmlTest in karma/fore-test.js (e.g. htmlTest('html/fore-form.html');

The Karma config file can be found at fore/test/karma/conf/karma.conf.js

#### Executing HTML tests

`grunt test-karma`


#### Web Resources:

<http://japhr.blogspot.de/2013/11/getting-started-with-karma-for-polymer.html>


### Webtests
All Webtests are written in and executed by the DalekJS framework. The Fore DalekJS tests are placed at fore/test/dalekjs

To execute all tests on the development (non-optimized) version execute:

`grunt test-dev`

To execute all tests on the production version execute:

`grunt test-dist`


## Trouble Shooting

If you experience any problems with node (npm) or bower try the following commands to clean up your caches:

`npm cache clean` 

`bower cache clean`


## Tools and libraries
Tools to build and run 'fore'

### Node.js
Platform for building fast, scalable network applications

Website: <http://nodejs.org/>

### Bower
Package Manager for the Web

Website: <http://bower.io/>

### Polymer
[Web Components](http://www.w3.org/TR/components-intro/) polyfill for any modern browser

* Website: <http://www.polymer-project.org/>

### JQuery (optional)
JavaScript library to make developers life easier

* Website: <http://jquery.com/>


## Test Tools

### Karma
Karma is a testing environment (which is testing framework agnostic). It can execute test written in [Jasmine](http://pivotal.github.io/jasmine/), [Mocha](http://visionmedia.github.io/mocha/), [QUnit](http://qunitjs.com/) or use your own framework (via a simple adapter).

Website: <http://karma-runner.github.io/>


### Mocha
JavaScript Test Framework

Website: <http://visionmedia.github.io/mocha/>

### Chai
Chai is a BDD (Behaviour Driven Design) / TDD (Test Driven Development) assertion library for node and the browser.

Website: <http://chaijs.com/>

### DalekJs
UI Testing Tool / Automated Cross Browser Testing

* Website: <http://dalekjs.com/>
* Sample project including automated screenshots of every test: <https://github.com/kmturley/dalekjs-crossbrowser>

