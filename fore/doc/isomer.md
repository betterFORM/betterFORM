# Isomer - extending HTML5 forms
### Green Paper 26. Nov 2013

Version: 1.0

Editors:

* Joern Turner


# Preface - what's a 'Green Paper'?

A 'Green Paper' according to the definition of the European Commission is a document: 
> (...) to stimulate discussion on given topics(...). They invite the relevant parties (bodies or individuals) to participate in a consultation process and debate on the basis of the proposals they put forward. Green Papers may give rise to (...) developments that are then outlined in White Papers.

So keep in mind - this is a proposal AND subject to change.

# Abstract

This paper proposes a framework enhancing the form processing capabilities of HTML5 subsumed as 'Isomer'. Isomer defines a set of attributes, web components, events and a JavaScript API to selectively and incrementally enhance simple HTML forms into model-driven, secure and highly dynamic web applications. 

# Introduction

Forms are a cornerstone of most web applications but building browser-based form applications is still a cumbersome process. Existing standards like [XForms](http://www.w3.org/TR/2009/REC-xforms-20091020/) have failed to succeed in the wild and native support for advanced form functionality is lacking in the current HTML standard as well as in browser implementations. 

The mobile revolution brings new challenges that developers need to address: offline-capability, responsiveness, notifications etc. These require the orchestration of many standards, tools and libraries to deliver state-of-the-art solutions.

Isomer picks up the good parts of XForms and other standards and integrates them into the HTML world. It builds on the common ground of existing standards and best practices to give easy access to the advanced functionality required by todays' applications.

Most web applications today are developed with an iterative or agile process. Forms, with are a vital part of many of them, often start simple but quickly grow complex when validations, calculations or dependencies between data items enter the picture. But the price of using powerful form frameworks right from the start often seems too high. This becomes a problem once when developers realize that the amount of script code quickly grows and becomes messy. The hurdles to switch to a more powerful tool is often painful as they force re-implementation of already running functionality which - under pressing timelines - often is no option. Isomer will allow to start with standard HTML forms and progressively enhance them into full-blown model-driven form applications.

Isomer is a community effort. All interested parties are invited to participate in the effort.

## Relations to other work

Many ideas of Isomer will be borrowed from work already done in other places:

* W3C [XForms 1.1](http://www.w3.org/TR/2009/REC-xforms-20091020/) has set a standard for XML-based forms that offers a huge, powerful toolkit to address even to most complex scenarios.
* [XForms for HTML](http://www.w3.org/TR/XForms-for-HTML) was an effort that tried to re-integrate the ideas of XForms into the HTML world. Unfortunately the effort has been discontinued after a first draft document.
* [Web Components](http://www.w3.org/TR/components-intro/) are a new standard that will allow well-encapsulated functionality within a browser and addresses the needs of web application developers. Isomer builds upon this upcoming standard to implement its functionality.


## Approach

This paper is not a specification in the strict sense. It just tries to outline the functionality to a reasonable extend to give users a clear impression of what to expect from an implementation. 

## Model-driven forms

Isomer takes a model-driven approach to forms to make them more maintainable and extensible.

Both XML and JSON models will be supported for the following reasons:

* JSON is the de-facto standard on the client-side today. It provides a lightweight data representation and is efficient to process in JavaScript and to transfer via the wire.
* XML on the other hand is the standard for data exchange and representation in countless industries and domains. Namespaces, a rich set of datatypes, validation  and i18n support are just a few arguments that distinguishes it from the simple JSON structures.

Models can be either implicit (auto-generated) or explicit. Details are given in the following sections.

### Model lifecycle

Models are constructed/inited right after page load by dispatching a model-construct event to each `form`element found in a document.

During `model-construct`following steps are performed by each respective model:

if a form does NOT use an explicit model (see section "Implicit model"):

* a data instance is generated from the occurrences of the `name` attribute within the given form.
* 'bind objects' will be created for each element having a `name`attribute by inspecting the other attributes on the given element. It's open to an implementation how 'bind objects' are represented. 
* each bind object will be bound to its corresponding instance data node holding the states for that node.

if a form USES an explicit model:

* each instance element found in a model will be processed in turn
* if it has a `src` attribute this will be resolved and the response will be treated as data instance
* if it has no `src`attribute but provides an inline data structure this is used for the data instance.
* if neither is given ...
* each `bind`element found in a model will be processed in turn...
* a bind object will be created linking the states defined by the bind to a instance data node referenced by the `ref`attribute

Once all elements are processed a 'model-construct-done' event is dispatched to the repective model element.
 


### Implicit model

** Editorial Note:** ISSUE? - is implied (implicit) mode useless? Under a security view the implied mode seems questionable as a whole - generally the policy of secure processing is to never trust the client. That said it seems necessary to control what a client is allowed to imply. E.g. a given form authored without explicit model would imply a certain model in xforms. So far so good. But only if the server has access to the original file that was loaded by the client it is able to validate the implied policy by converting it in the way the client does. 

So for each serious application the server needs access to a valid model that exists outside/aside of the data submitted by a client (browser) for re-validation on the server. Does that also mean that a secure app MUST always load the form document from a server (not from local filesystem that means)?


** Editorial Note:** element names that contain a dash ('-') character signify an HTML5 extension element (web component). These are used by Isomer to extend the HTML functionality with the functionality described in this paper.  


If a form does not use an explicit model (see following sections), then an implicit 'xf-model' element is generated during model construction and prepended before the first child element of the form element. The generated `<xf-model>` element is required as it is used as target of model events.

Example:

    <form>
        <label for="title">Title</label>
        <select id="title" name="title">
            <option value="dr">Dr</option>
            <option value="prof">Prof</option>
            <option value="dipling">Dipl.-Ing.</option>
         </select>

         <label for="firstName">First Name</label>
         <input id="firstName" type="text" name="firstname">

    </form>

In XML mode the above input form will create the following implicit model at runtime:

    <form>
        <xf-model>
            <xf-instance>
                <data>
                    <title></title>
                    <firstname></firstname>
                </data>
            </xf-instance>
        </xf-model>
        
        <label for="title">Title</label>
        <select id="title" name="title">
            <option value="dr">Dr</option>
            <option value="prof">Prof</option>
            <option value="dipling">Dipl.-Ing.</option>
         </select>

         <label for="firstName">First Name</label>
         <input id="firstName" type="text" name="firstname">

    </form>
    
** Editorial Note:**     

    

### Using an inline model

To use an inline XForms model the containing document must be XHTML and declare the appropriate namespaces. The model can be referenced by a fragment identifier:


    <xf:model id="myModel">
    
        ...
    </xf:model>
            
    <form model-src="#myModel">
    ...
    </form>
    


[full example](examples/inline-model.html)


### Importing a model

`<form model-src="myXFormsModel.xml">`

myXFormsModel.xml must contain fully XForms 1.1 conformant markup and will be resolved relative to `document.location.href` 

If the URL contains a fragment identifier (#) it will be resolved as idref in context of the current document.

By using linked models the host document no longer needs to be well-formed and namespaced XHTML. 
 
[full example](examples/linked-model.html)




## Data instances

XForms uses XML and the XPath data model for representing instance data while JSON has established as a de-facto standard in the web development world. Isomer will support both data models.

Instances can either be explicitly given as part of an `xf:model` or generated based upon the element structure of the UI controls. The latter are called **generated data instances**.

A model might use an arbitrary amount of instances being associated with a single form.

The default instance is indicated by the `instance` attribute of element `form `. The value of the `instance` attribute must match the `id` attribute of an existing instance. 

** Editorial Note:** fix this: no mention yet on how to use JSON or an JS object as instance.

If the `instance` attribute is absent, a default instance will be generated based upon the  `name` attributes that appear on elements enclosed by the given `form`. The generated instance will be inserted as first child of the associated `model`. Generated instances will be constructed before actual model initialization takes place during 'xforms-model-construct` event.

### Instance data generation

The structure of generated instances is determined by the structure of elements bearing `name` attributes that are enclosed by a `form` element.

### XML instance generation
Given this markup:
    
    <form model-src="#address" name="address">

        <label for="firstName">First Name</label>
        <input id="firstName" type="text" name="firstname">

        <label for="lastName">Last Name</label>
        <input id="lastName" type="text" name="lastname">

        <div name="postaladdress">
            <label for="street">Street</label>
            <input id="street" name="street"/>
        </div>
    </form>
    
the following instance is created:

    <address>
        <firstname></firstname>
        <lastname></lastname>
        <postaladdress>
            <street></street>
        </postaladdress>
    </address>            

If a `name` attribute on element `form` is not present the value will default to "data".

###  JSON instance generation

If JSON is used the same example above would result in this JSON structure:

    
    <form>
        <xf-model>
            <script type="text/javascript">
                var address = {
                    title:null,
                    firstname:null
                };
            </script>
        </xf-model>
    </form>
    
 
 
### Using HTML attributes in instance generation

HTML attributes are used to provide default values during instance generation. The following table gives an overview of the attributes used:

Attribute    | Content Model  | Description
------------ | -------------  | ------------
instance     | IDREF or empty | May exclusively be used on `form` elements to identify the default instance of a form. If present must point to an instance with given ID which will be used as the default instance for that form. If not present an instance will be generated for the form.
name         | xsd:QName      | The HTML `name` attribute is used to bind a control to a data node that matches this attributes value. E.g. when `name="foo"` the control will bind to a data node named "foo".
value        | xsd:string     | If attribute `value` is present on a form control it will set the default value of the bound data node given by the `name`attribute.
checked      | `true`, `checked` or `false` | If attribute `checked`is present on a given form control it will be interpreted as a value of `true`. If not present or `false` the value of the bound instance node will be `false`.
type | 
 
### Precedence of HTML attributes

tbd: define order of precedence when HTML attributes are used with an explicit xf:model



 
## Binding data

tbd

**Editorial Note**: in later versions of this Spec it is planned to allow complex path expressions that contain steps and filters. 

### Declarative validation

tbd.

### Declarative calculation

tbd.

### handling of the `readonly` attribute

tbd.

### Evaluation of expressions

tbd.

## Events

tbd.


## Submissions

tbd

## Repeated data sets

**Editorial Note**: this section needs review!

Repeated data nodes are handled according to the upcoming HTML template specification. 

E.g.

    <xf-instance>
        <data>
            <items>
                <item>Item one</item>
                <item>Item two</item>
                <item>Item three</item>
            </items>
        </data>
    <xf-instance>
    ...
    
    <form>
        <template id="repeatedItems" repeat="items">
            <input name="item" type="text">
        </template>
    </form>
    
will create the following DOM tree at runtime:

    <form>
        <input name="item" type="text" value="Item one">
        <input name="item" type="text" value="Item two">
        <input name="item" type="text" value="Item three">
    </form>
    
 


## JavaScript API

tbd

## Overview of HTML enhancements

This section describes the additional attributes provided by Isomer to enhance native HTML behavior.

Two controls that share the same name (specifiy the same value for the `name`attribute) they are bound to the same data node. If the bound data node changes all bound controls will be updated to reflect the new value. 


### General enhancements

Attribute    | Content Model  | may appear on | Description
------------ | -------------  | ------------
name    | xsd:QName            | any HTML element in the `body` **Editorial Note:** we should be more specific here. | A parameter name binding to an instance node



### Enhancements to the `form`element

Attribute    | Content Model  | Description
------------ | -------------  | ------------
xfModel      | URL            | URL pointing to XForms model
instance     | IDREF | indicates the default instance of a form 

### Enhancements to HTML controls


Attribute    | Content Model  | Description
------------ | -------------  | ------------
constraint   | XPath or JavaScript expression | Expression that must evaluate to boolean indicating whether or not data bound the control are valid. The expression of this attribute is automatically re-evaluated whenever any of the data values is depends upon are changing. 
required     | XPath or JavaScript expression or `true`, `false` (case-insensitive) | Expression that must evaluate to boolean indicating whether or not the bound data value MUST provide a value. In HTML the presence of this attribute indicates a boolean `true`value while the non-existence indicates a boolean `false`value.
type | xsd:string | String denoting an HTML datatype (e.g. 'number') or an XSD datatype

### Enhancements to the `label`element

Isomer maps `xf:label`, `xf:alert`, `xf:hint` and `xf:help`to the HTML `label`element to attach their specific behavior.


Attribute    | Content Model  | Description
------------ | -------------  | ------------
for | IDREF pointing to control | same meaning as in HTML - associates the label with a control.
is | One of 'xf-label', 'xf-alert', 'xf-hint' or 'xf-help'| Identifies Web Component to be used and is resolved by HTML imports module
class | One of 'xfLabel', 'xfAlert', 'xfHint' or 'xf:help' | Alternative way of marking the respective semantics for rendering purposes. Does NOT create the a web component but acts as a mere convention for CSS matching.




### Mapping of datatypes

HTML defines a set of values for the `type`attribute of a control. Some of these can be mapped to XML Schema datatypes or XForms datatypes for ease of authoring and validation.


HTML type value | XSD datatype | Remarks
--------------- | ------------ | --------
button | no match | -
checkbox | xsd:boolean | As checkbox is used to input boolean values it can be mapped to xsd:boolean
color | no match | There's no direct correspondence to a XML schema datatype. However it would be possible to define an extension from xsd:string that validates correct color values
date | xsd:date | **Editorial Note**: exact representation and the role of language-specific formats must be clarified
datetime | xsd:dateTime | **Editorial Note**: same issues as above apply
datetime-local | no-match | **Editorial Note**: there's no direct match but automatic conversion might be possible when language is defined in the context of the document or form
email |  xf:email | XForms defines an email datatype which can be used for mapping.
file | xsd:base64Binary or xsd:hexBinary or xsd:anyURI | **Editorial Note**: as multiple mappings would be possible it will probably be necessary to define an additional mechanism to choose from these options. Most of the time users will expect a binary fileupload that ends up in some server location.
hidden | no match | **Editorial Note**: there's no sensible mapping for hidden controls
image | no match | Is used for presentational purposes in HTML. Does not imply any datatype mapping.
month | xsd:gMonth | -
number | xsd:integer or xsd:decimal or xsd:double or xsd:float | tbd.
password | no match | Used for presentational purposes in HTML.
radio | no match | **Editorial Note**: in implied mode it might be possible to generate an enumeration type to validate that one of the options is used. 
range | no match | **Editorial Note**: in XForms the `xf:range`control can bind to different datatypes.
reset | no match | -
search | no match | Default to xf:string
submit | no match | -
tel | no match | **Editorial Note**: a regex pattern might be added to validate these values of the server side
text | xf:string | -
time | xf:time | -
url | xf:anyURI | - 
week | no match | **Editorial Note**: a custom XML Schema datatype might be created.
 




**Editorial Note:**

Might we want to support these elements in addition as attributes?




# Appendices

### A Glossary of Terms

### B Links

* [XForms 1.1](http://www.w3.org/TR/2009/REC-xforms-20091020/)
* [XForms for HTML](http://www.w3.org/TR/XForms-for-HTML)
* [Web Components](http://www.w3.org/TR/components-intro/)
* [Polymer](http://polymer-project.org)
* [JSONPath - JavaScript implementation](http://goessner.net/articles/JsonPath/)
* [json-path - Java implementation](https://code.google.com/p/json-path/)
* [HTML templates](https://dvcs.w3.org/hg/webcomponents/raw-file/tip/spec/templates/index.html)


### C Acknowledgements

------
[^1]: This opinion is build on top of many years of experience as implementors and users of XForms and applying it in many different problem domains. 
[^2]: It's beyond the scope of this document to discuss all reasons for this lack of acceptence in detail. There are political, technical and sociological aspects but in general these have to do with XForms markup 'not feeling right at home' within HTML documents.
[^3]: People that bring their knowledge of HTML writing, CSS and JavaScript.
[^4]: The author(s) have no further information about the reasons.