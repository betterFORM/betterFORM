Thoughts and definitions on rewworked JS layer
--------------------------------------------------

Motivation:
-the JS layer has been growing for quite a while and has several inconsistencies and redundancies which
make it hard to extend.

- when integrating a new component both the XSLT transform and the JS must be adapted. This makes integration of
new components harder than should be.

New Approach
--------------------------------
- refactor transform to output native html controls again which will be progressively enhanced by JS. Allows fallback
to native controls instead of Dojo Dijits (more lightweight)

- use Dojo behavior to enhance the controls at init time. There's one new JS file that will contain all
behavior mappings from XForms controls to native controls.

- All state will be represented as CSS classes to avoid redundancy of information on the rendered DOM and still
allow styling options. Eventually some CSS classes can be converted to 'data-*' attributes when they are not
styling-relevant.

- all state handlings will occur on the level of the control (not controlvalue).


Open issues
--------------------------------
Component mapping with dojo.behavior solves some problems we had with extending controls but is not an
answer to importing JS and CSS resources. Adding these still requires changing the xslt transform. We should
add a declarative way of adding these.

 * Using Output within Labels (e.g. of a trigger) does not work yet
 * Switching a RepeatFull to readonly does not work correctly, handleStateChanged events are only present for
    repeat items but not the repeat itself.
 * if behaviours are bound to datatypes like '.xfOutput.xsdString.mediatypeText .xfValue' we are missing a kind of
   default behaviour or we have to write behaviour matches for all kind of possible datatypes
   (listing of basic schema datatypes: http://www.w3.org/TR/xmlschema-2/#built-in-datatypes)
 * xforms-select events within itemsets do not work
 * validation of DropDownDate (if bf:dropdowndata=xyz is used there must be a corresponding constraint on the bind)



To use it
--------------------------------
- change betterform-config to use xhtml.xsl instead of dojo.xsl
- redeploy

The relevant JS classes to be worked on are:
-XFControl
-Component
-XFProcessor

todo: change name of useragent - 'dojo' is not appropriate any more
todo: check for diffs between XFProcessor and FluxProcessor
