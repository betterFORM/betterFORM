<!--
Copyright (c) 2012. betterFORM Project
http://www.betterform.de
Licensed under the terms of BSD License

@version: 0.1
@date: 2014-01-23

-->

# Getting started developing Fore

## Introduction

Fore is a front-end toolkit based upon [Polymer](http://www.polymer-project.org/) (Web Components) and uses [Node.js](http://nodejs.org/), [Bower](http://bower.io/) and [Grunt](http://gruntjs.com/) as its primary tools.
This document describes the basics of setting up a development environment and the procedures to run, test and deploy
your project.

## Requirements
The betterFORM fore module depends on Node.js, Bower and Grunt as their build infrastructure.

### Node.js (Server Side JS Platform)
Please refer to their [homepage](http://nodejs.org/) for a native installer for your platform and follow the instructions.

On Mac OS X you can use the [Homebrew](http://brew.sh/) package manager: `brew install node`

## Installation

All fore dependencies are installed via npm (node package manager) and bower. Npm downloads the dependent modules into a **local** node_modules directory while bower setups all JavaScript libraries utilized by the fore module. Grunt is a tool which provides automating software build processes and various other task needed during development. 

1. Checkout the betterFORM sources from <https://github.com/betterFORM/betterFORM> into a local workspace.
1. Run `npm install bower grunt -g` to install bower and grunt in the **global** node.js context.
1. To install the dependent node_modules for Fore change into the 'fore' directory and call `npm install`. The modules can afterwards be found in the directory 'node_modules'.
1. Install all fore JavaScript dependencies with `bower install`. They will be placed in 'app/bower_components'.

## Running Fore

`grunt server` - by executing this command the grunt http server and your default browser will be started with index.html.

This target will support live reloading meaning that changes will become visible in your browser right after you hit 'save' in your editor without the need to explicitly reload the page.

## Deployment

To create an optimized version of the App run
`grunt dist`
This will optimize all JavaScript, CSS and Image files and create an optimized app in the created dist directory

## Testing

Fore curently provides two different test setups: 'polymer' tests using Karma and Chai and Webtests via DalekJS.

### HTML Tests
The Fore Web Components are tested with Chai and Karma. All tests are placed beneath the folder fore/test/karma.
All html tests are located in karma/html. Be aware that the execution of the html test is triggered via the

JavaScript function htmlTest in karma/fore-test.js (e.g. htmlTest('html/fore-form.html');

The Karma config file can be found at fore/test/karma/conf/karma.conf.js

#### Execute HTML tests

`grunt test-karma`

#### Web Resources:

http://japhr.blogspot.de/2013/11/getting-started-with-karma-for-polymer.html

### Webtests
All Webtests are written in and executed by the DalekJS Framework. The Fore DalekJS Tests are located at $FORE/test/dalekjs

To execute all tests on the app directory (exploded resources, development) execute

`grunt test-dev`

analogue all test on the dist directory (optimized resources, deployment) can be run by calling

`grunt test-dist`


## Trouble Shooting
If you experience any trouble with node (npm) or bower try the following command to clean up your caches:

`npm cache clean` and
`bower cache clean`

## Update build infrastructure
To update the managed node-modules and library dependencies run

`npm update`

or respectively

`bower update`

## Adding new dependencies to project

### Node.js
To install a new node_module and record it as development-dependency run (in this case handlebar)

`npm install --save-dev handlebar`

### Bower
To install a new managed library and record it as development-dependency run (again handlebar)
`bower -D install handlebar`

For more information on commands (and their syntax) see their corresponding homepages ([Nodejs + NPM](http://nodejs.org/) and [Bower](http://bower.io/)).

## Utilized Build Tools
Tools to build and run 'fore'

### Node.js
Platform for building fast, scalable network applications

Website: <http://nodejs.org/>

### Grunt
The JavaScript Task Runner

Website: <http://gruntjs.com/>

### Bower
Package Manager for the Web (like Maven for Java and EXPath for XQuery)

Website: <http://bower.io/>


## Utilized JS Frameworks
JavaScript Frameworks used to implement 'fore' and which are available to form authors

### Polymer
WebComponents for any browser

Website: <http://www.polymer-project.org/>

### JQuery
JavaScript library to make developers life easier

Website: <http://jquery.com/>


## Utilized Test Tools

### Karma
Karma is a testing environment (which is testing testing framework agnostic). It can execute test written in [Jasmine](http://pivotal.github.io/jasmine/), [Mocha](http://visionmedia.github.io/mocha/), [QUnit](http://qunitjs.com/) or use your own framework (via a simple adapter).

Website: <http://karma-runner.github.io/>


### Mocha
JavaScript Test Framework

Website: <http://visionmedia.github.io/mocha/>

### Chai
Chai is a BDD ((Behaviour Driven Design) / TDD (Test Driven Developtment) assertion library for node and the browser

Website: <http://chaijs.com/>

### DalekJs
UI Testing Tool / Automated Cross Browser Testing

Website: <http://dalekjs.com/>

Sample project including automated screenshots of every test: <https://github.com/kmturley/dalekjs-crossbrowser>

