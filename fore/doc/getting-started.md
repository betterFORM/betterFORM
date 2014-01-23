<!--
Copyright (c) 2012. betterFORM Project
http://www.betterform.de
Licensed under the terms of BSD License

@version: 0.1
@date: 2014-01-23

-->

# Getting started developing Fore

## Introduction

Fore is a front-end toolkit based upon Polymer (Web Components) and uses Nodejs, Bower Gruntjs as its primary tools.
This document describes the basics of setting up a development environment and the procedures to run, test and deploy
your project.

## Requirements
The betterFORM fore module depends on NodeJS

1. NodeJS (Server Side JS Platform)
    * Please refer to their [homepage](http://nodejs.org/) for a native installer for your platform and follow the instructions.
    * Mac OS X + Homebrew: `brew install node`

## Installation

All fore dependencies are installed via npm and bower. NodeJS downloads the dependent frameworks into a **local** node_modules directory while bower setups all JS libraries utilized by the fore module

1. `npm install bower grunt -g` // install bower and grunt in a global context
1. `npm install` // install all node modules utilized to build fore in a local context (will be placed in fore/node_modules)
1. `bower install` // install all fore js dependencies (will be placed in fore/app/bower_components)

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

## Update build infrastructure

### Add node dependencies to project
 to update package.js run `npm install --save-dev [grunt-task-name]`

### Update bower controller js components
1. to update bower run `bower update`


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

