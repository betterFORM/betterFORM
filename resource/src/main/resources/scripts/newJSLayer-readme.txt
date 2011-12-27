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