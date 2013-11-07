# Web Tooling

Purpose of this document is to gather resources for designing our own web tooling infrastructure. The term 'web tooling' here means everything dealing with one of these aspects:

* JavaScript architecture
* JavaScript libraries
* JavaScript unit testing
* Browser testing
* Device testing
* JavaScript checking (linting)
* build process - weaving everything together (->Ant)
* compressing CSS + JS + Web Components
* CSS
* mobile application support 
* Scaffolding

## Introductory remarks

As there are many many tools around each with its specific features and we still need some experience with them we should orientate on recommendations used elsewhere.

The Polymer project uses and proposes some tooling that is made up from Yeoman, Grunt and Bower and as we have committed to use Polymer we should stay in close relation to their tooling. The choice for certain areas is therefore at least pre-indicated.


## JavaScript architecture

* Web Components (Polymer)

## JavaScript libraries

A list of candidates for js libraries to be used in projects and our products.

* jQuery 2.x
* JQueryUI
* backbonejs (candidate)
* jQuery mobile

## JavaScript unit testing

* candidates ???


## Browser testing

* Selenium

## Device testing

Not sure which tools may exist here. The purpose of device testing would be to ensure a good experience on tablets and smartphones.

## JavaScript checking (linting)

* JSLint - think there's a plugin in grunt to run that during build process

## Build process

* grunt is a common choice and used in Polymer


## compressing CSS + JS + Web Components

* Vulcanizer - compresses web components and all related resources


## CSS

* LESS
* bootstrap 3 - as it's so widespread some support (as already exists in eXide) can be considered

## 

## Scaffolding

* Yeoman - helps with generation of projects and artifacts (e.g. a polymer component)

## Dependency management

We should discuss our needs here. 

Proposed tool if one is needed:

* Bower


## Setting up the basic environment

* install NodeJS globally on your machine
* install Grunt command line interface by running: `npm install -g grunt-cli`. It might be necessary to run this with in `sudo` mode. This does not install the Grunt taskrunner itself. It is assumed that many project use and package their own (version of) Grunt.
* 


### Questions

* where to put resources that are probably needed? (package.json, gruntfile.js)
* how to setup the toolchain - can this be automated?
* how do we integrate the web tooling into our existing betterFORM builds? E.g. we'd like to run Java unit tests with JavaScript unit tests in one go.