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

## Execute Test Suite

All Fore tests are written as Webdriver tests and executed by DalekJS (http://dalekjs.com/) via Grunt.
The Fore Tests are placed at $FORE/test/dalekjs

To execute all tests on the app directory (exploded resources, development) execute
1. `grunt test-dev`

analogue all test on the dist directory (optimized resources, deployment) can be run by calling
1. `grunt test-dist`





