# betterFORM 6 Whitepaper

tbd: intro

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

## Design Discussion Notes
