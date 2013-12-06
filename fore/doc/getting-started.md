# Getting started developing Fore

## Requirements

* nodejs must be installed

## Setup Fore

Run the following commands to setup the project and download all needed libs

1. **npm install**
1. **bower install**


## Running Fore


## Update build infrastructure

### Add node dependencies to project
 to update package.js run **npm install --save-dev [grunt-task-name]**

### Update bower controller js components
1. to update bower run **bower update**


## Deployment
To create an optimized version of the App run
1. grunt dist
This will optimize all JavaScript, CSS and Image files and create an optimized app in the created dist directory


## Testing

## Execute Test Suite

All Fore tests are written as Webdriver tests and executed by DalekJS (http://dalekjs.com/) via Grunt.
The Fore Tests are placed at $FORE/test/dalekjs

To execute all tests on the app directory (exploded resources, development) execute
1. grunt test-dev

analogue all test on the dist directory (optimized resources, deployment) can be run by calling
1. grunt test-dist





