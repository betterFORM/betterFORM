# betterFORM 6 Whitepaper

tbd: intro

## Goals

i. offer HTML5-based syntax for using XForms within webapps
ii. provide an easy to extend component architecture 

## Requirements


1. vanilla XForms syntax must be supported for backward compat
1. must be easy to extend without touching the core betterFORM distribution (rebuilding)
1. stay as close with standards as possible but not to an fundamentalistic extend
1. 

## XForms in HTML

The XForms for HTML Draft (http://www.w3.org/TR/XForms-for-HTML/) which was discontinued by the Working Group contained some interesting ideas we'd like to merge into the new architecture of betterFORM.

Especially interesting are following aspects:

* implied models - XFormsForHTML does not use explicit models. They are implied by the structure of bound elements in the document tree. Bound elements are elements that have a 'name' attribute. The value of the 'name' attribute constitutes a node in the implied instance which by default starts with a rootnode of 'data'. (See spec for examples)

* using standard HTML attributes - like the 'name` attribute other attributes like `readonly` can be used to imply an initial value for an implied `bind` element

* simple repeat model via attributes

## XForms versus HTML syntax

XForms4HTML offers some nice features like declarative calculations but to the expense of using just a single `name` instead of a complex xpath like `items/item`.

While in X4H no explicit XForms model is used we'd like to offer that combination - when the problem gets too complex to tackle with plain HTML syntax and implied models are not sufficient the author wants to introduce an explicit model and should not suffer any restriction from the full XForms functionality.

## Implied models

* needed: pre-processor generating default model

## Deprecate 'appearance'?

The goal of the whole effort to replace xforms with html syntax is to take away the abstraction of the UI and to commit to HTML as host language. The appearance only  makes sense to give hints to the renderer (which in our case now is always HTML) about the details of the 'visualization'.

## Design Discussion Notes and Decisions

### Decision: Implementation libraries

For the implementation of the betterFORM frontend 'fore' we'll use:

* Polymer
* jQuery



### Decision: Web tooling

We use the tools proposed by the Polymer makers:

* Yeoman
* Grunt
* Bower
* LESS

Main reasons:
* we need to learn about the tools anyway and have to start somewhere
* makes it easier to follow tutorials and documentation that uses these tools or to copy from examples



## Roadmap

### Step 1 - Definition of Syntax

#### Story:
we'll develop explorative syntax examples using XForms and HTML-based syntax to work out the details of how it will feel to the developer later on. In this step we'll also setup the barebones of the build system and the development workflow.



#### Restrictions:
We'll keep the changes to the server-side code to the minimum. Esp. the messaging will not touched in this step. 

The main goals for the first development step:

* develop 'natural' HTML syntax
* implement set of explorative samples to work out the syntax details
* extensible component model based upon web components
* barebones build system and development process
* backward compat to 'native' XForms
* no changes

