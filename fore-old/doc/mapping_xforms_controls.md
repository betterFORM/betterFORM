# Mapping XForms controls

XForms controls are abstract and must be mapped to real controls in HTML.

Mapped (HTML) controls are organised in a structure that is oriented at the upcoming W3C Web Components standard.

A mapped control (input in this case) is stored in a separate file and might look like this:

    `<element name="xf-input">
        <template>
            <input type="text"></input>
        </template>
        <script>
            ({
                created:function(){
                },
                attached:function(){
                },
                detached:function(){
                },
                valueChanged:function(){
                },
                customFunction:function(){
                }
            });
        </script>
    </element>`
        
The mapping from an XForms control to the concrete HTML element is done by convention. E.g. for an xf:input (or xforms:input) the mapping routine looks for a file called xf-input.html in their controls directory.

input:date:full -> input-date-full.html?
select:minimal
output:html -> output-html.html?


XForms control | datatype | appearance | mediatype | Element file
-------------- | -------- | ---------- | --------- | -------------
input          | text     | undefined  |   n.a.    | input-text.html
input          | boolean  | undefined  | n.a.      | input-boolean.html
input          | date     | undefined  | n.a.      | input-date.html
input          | datetime | undefined  | n.a.      | input-datetime.html
input          | integer, decimal, float | undefined | n.a. | input-number.html
input          | time | undefined | n.a. | input-time.html
input | text | minimal | n.a. | input-text-minimal.html

 

## Mapping datatype, appearance and mediatype

XForms controls change their behavior according to the presence of a datatype, appearance attribute and in some cases the mediatype attribute.




# Mapping custom controls

To extend the core set of mappings provided by the XForms processor you can use the following syntax:

<xf:input is="components/my-custom-element.html" ref="foo"/>

During mapping process this will be looked up and the template of your element will get added to the document replacing the original xf:input control. The `is`attribute must always be used as an attribute to one of the controls defined by the XForms spec. The rationale behind this is that the XForms control will define the behavior of the custom element within the context of the XForms processor e.g. behaving as an input, output or select1.

The structure of the custom element must follow the one defined by the sample above and provide at least the functions for `created`, `attached`, `detached`and `valueChanged`. It may however define additional 'private' functions necessary for your processing.
