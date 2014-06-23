# W3Forms
### Green Paper 26. Nov 2013

Version: 1.0

Editors:

* Joern Turner


# Preface - what's a 'Green Paper'?

A 'Green Paper' according to the definition of the European Commission is a document: 
> (...) to stimulate discussion on given topics(...). They invite the relevant parties (bodies or individuals) to participate in a consultation process and debate on the basis of the proposals they put forward. Green Papers may give rise to (...) developments that are then outlined in White Papers.

So keep in mind - this is a proposal AND subject to change.

# Abstract



This paper presents 'W3Forms' - a successor of the (unfortunately stalled) [XForms for HTML](http://www.w3.org/TR/XForms-for-HTML) Working Draft dated from 19 December 2008. It outlines an opinionated approach[^1] of evolving the orginal approach into todays' HTML5 ecosystem.

Cite from original document:

> XForms for HTML provides a set of attributes and script methods that can be used by the tags or elements of an HTML or XHTML web page to simplify the integration of data-intensive interactive processing capabilities from XForms. The semantics of the attributes are mapped to the rich XForms model-view-controller-connector architecture, thereby allowing web application authors a smoother, selective migration path to the higher-order behaviors available from the full element markup available in modules of XForms.


# Motivation

The W3C XForms 1.1 recommendation has set the standard for the next generation of forms on the web. However with its steep learning curve and the perception of being bound exclusively to the XML world it lacks broad acceptence[^2].

As a reaction XForms for HTML was drafted to more closely meet the skills and expectations of HTML or Web developers[^3] and naturally enhance HTML with (limited) XForms capabilities.

Unfortunately XForms for HTML was discontinued[^4] and HTML5 has not addressed the functional gap. While typing and some declarative enhancements esp. to the ìnput`element have been made there's still a lack for e.g. a repeat model or a data binding mechanism (except in detail areas).

W3Forms is an attempt to revive the spirit of XForms for HTML while staying as close to (existing and evolving) web standards as possible and finally offer a advanced form processing platform in HTML. 

But why not simply implement XForms for HTML but have another spec? X4H is dated back to 2008 before the mobile revolution has really taken speed. The web platform and associated tooling has evolved quite a bit during the past 5 years requiring appropriate answers for the new challenges.

It is unlikely that native support for XForms for HTML will come from any browser vendor. The solution is to provide a JavaScript-driven implementation that builds upon the extensibility features of HTML5 (namely extension elements).

# Relation to other work

The evolving W3C [Web Components](http://www.w3.org/TR/components-intro/) specification is an ideal candidate for the implementation of W3Forms. It offers a HTML5-based component model for extending common HTML elements to create new custom elements which enhance the original element.

[Polymer](http://polymer-project.org) is an polyfill by Google implementing Web Components. It implements large parts of the specification in JavaScript polyfilling browsers that do not yet support a feature natively. Google and Netscape mainly drive the initiative now but major companies begin to adapt Web Components so there's a chance of native support for most major browsers.


## Introduction

The goals of W3Forms are fully in sync with the ones given in [XForms for HTML](http://www.w3.org/TR/XForms-for-HTML/#intro-reading). A final implementation should feel as simple, intuitive and consistent with widespread standards as possible and must allow developers to turn existing HTML forms into production-grade, model-driven form applications.

In addition W3Forms will take the mobile world into account which has evolved dramatically since 2008 and forces to address new requirements such as offline-capability, touch events, push notifications and responsive layout.

Starting with a simple and fully client-side approach the goal is to connect W3Forms to the full XForms capabilities defined by XForms 1.1. W3Forms intends to allow an iterative appraoch to form applications which start with simple HTML forms with implied models and allow developers to switch to an explicit model with advanced capabilities on the go. Further it shall address the requirements of production-grade applications by providing transparent validation on client and server.

 
### Conventions

When referring to original XForms elements the prefix 'xf' is used to imply that the respective element is from the XForms namespace.

Extension (or custom) elements in the Web Components spec must use a name with a dash ('-') character in its name. For W3Forms elements the prefix 'fore-' will be used.

## Approach

This paper is not a specification in the strict sense. It just tries to outline the functionality to a reasonable extend to give users a clear impression of what to expect from the implementation. 

Some decisions about the implementation have already been taken (e.g. to use Polymer) so it's not specifying things in an technology-agnostic way but will make use of work done elsewhere. For instance the Web Components Specification is still under construction and likely things will change here in detail. Where possible these changes will be shielded away by the implementation. However this might not always be possible and users will probably need to change details of their already running forms to catch up with the ongoing development. So we're on the bleeding edge - you have been warned.

As developers (as opposed to spec writers) we are taking a pragmatic approach to get things running. This means that not each and every edge case might get considered in full detail or at all. But we're addicted to standards and will try to align our decisions with common practices and standards whereever possible. 

As already mentioned W3Forms is an attempt to revive and modernize XForms for HTML and as such we'll work along the existing draft but will rephrase most of the text trying to catch the spirit of it but making it more consumable for the reader.

## XForms as component

HTML5 allows to extend the native DOM through extension elements. The only requirement for extension elements is that the name must contain a dash ('-') character (e.g. my-element).

The Web Components standard provides a set of technologies that build on top of extension elements and allow to specify custom element behavior and rendering in an encapsulated fashion.

W3Forms will use Web Components to implement the functionality of X4H and give web developers access to the rich processing model of XForms. The syntax is considered an important factor for the acceptance of the solution and must meet the expectations of the developers and re-use existing HTML syntax whereever possible and appropriate.

### Syntax
X4H mainly uses attributes on existing HTML elements to add its functionality. This however is not possible for the XForms model which has no correspondence in HTML. 

### Avoid requiring XML namespaces
XML Namespaces are often considered an entry hurdle for Web developers not familiar with XML. To keep the syntax easy W3Forms will not use XML-namespaced elements within its DOM at runtime. It will be able to consume those though.


## Models in forms

W3Forms will support these different ways of using an XForms model.

todo: say some more about usage of models

### Importing a model

`<form model-src="myXFormsModel.xml">`

myXFormsModel.xml must contain fully XForms 1.1 conformant markup and will be resolved relative to `document.location.href` 

If the URL contains a fragment identifier (#) it will be resolved as idref in context of the current document.

By using linked models the host document no longer needs to be well-formed and namespaced XHTML. 
 
[full example](examples/linked-model.html)

### Using an inline model

To use an inline XForms model the containing document must be XHTML and declare the appropriate namespaces. The model can be referenced by a fragment identifier:


    <xf:model id="myModel">
    
        ...
    </xf:model>
            
    <form model-src="#myModel">
    ...
    </form>
    


[full example](examples/inline-model.html)


### Implicit model

If a form does not have a `model-src` attribute, the an implicit 'xf-model' element is generated and prepended before the first child element of the form element. The generated `<xf-model>` element is required as it is used as target of model events.

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

The above input form will create the following implicit model at runtime:

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
    




## Data instances

#


XForms uses XML and the XPath data model for representing instance data while JSON has established as a de-facto standard in the web development world. W3Forms will support both data models.

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

This section describes the additional attributes provided by W3Forms to enhance native HTML behavior.

Two controls that share the same name (specifiy the same value for the `name`attribute) they are bound to the same data node. If the bound data node changes all bound controls will be updated to reflect the new value. 


### General enhancements

Attribute    | Content Model  | may appear on | Description
------------ | -------------  | ------------
name    | xsd:QName            | any HTML element in the `body` **Editorial Note:** we should be more specific here. | A parameter name binding to an instance node



### Enhancements to the `form`element

Attribute    | Content Model  | Description
------------ | -------------  | ------------
model-src    | URL            | URL pointing to external XForms model
instance     | IDREF | indicates the default instance of a form 

### Enhancements to HTML controls


Attribute    | Content Model  | Description
------------ | -------------  | ------------
constraint   | XPath or JavaScript expression | Expression that must evaluate to boolean indicating whether or not data bound the control are valid. The expression of this attribute is automatically re-evaluated whenever any of the data values is depends upon are changing. 
required     | XPath or JavaScript expression or `true`, `false` (case-insensitive) | Expression that must evaluate to boolean indicating whether or not the bound data value MUST provide a value. In HTML the presence of this attribute indicates a boolean `true`value while the non-existence indicates a boolean `false`value.
type | xsd:string | String denoting an HTML datatype (e.g. 'number') or an XSD datatype

### Enhancements to the `label`element

W3Forms maps `xf:label`, `xf:alert`, `xf:hint` and `xf:help`to the HTML `label`element to attach their specific behavior.


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