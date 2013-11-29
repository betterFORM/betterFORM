# Fore - XForms for HTML reloaded
### Green Paper 26. Nov 2013

Version: 1.0

Editors:

* Joern Turner

Copyright betterFORM 2013


# Preface - what's a 'Green Paper'?

A 'Green Paper' according to the definition of the European Commission is a document: 
> (...) to stimulate discussion on given topics(...). They invite the relevant parties (bodies or individuals) to participate in a consultation process and debate on the basis of the proposals they put forward. Green Papers may give rise to (...) developments that are then outlined in White Papers.

So keep in mind - this is a proposal AND subject to change.

# Abstract

This paper presents 'Fore' - a successor of the (unfortunately stalled) [XForms for HTML](http://www.w3.org/TR/XForms-for-HTML) Working Draft dated from 19 December 2008. It outlines an opinionated approach[^1] of evolving the orginal approach into todays' HTML5 ecosystem.

Cite from original document:

> XForms for HTML provides a set of attributes and script methods that can be used by the tags or elements of an HTML or XHTML web page to simplify the integration of data-intensive interactive processing capabilities from XForms. The semantics of the attributes are mapped to the rich XForms model-view-controller-connector architecture, thereby allowing web application authors a smoother, selective migration path to the higher-order behaviors available from the full element markup available in modules of XForms.


# Motivation

The W3C XForms 1.1 recommendation has set the standard for the next generation of forms on the web. However with its steep learning curve and the perception of being bound exclusively to the XML world it lacks broad acceptence[^2].

As a reaction XForms for HTML was drafted to more closely meet the skills and expectations of HTML or Web developers[^3] and naturally enhance HTML with (limited) XForms capabilities.

Unfortunately XForms for HTML was discontinued[^4] and HTML5 has not addressed the functional gap. While typing and some declarative enhancements esp. to the ìnput`element have been made there's still a lack for e.g. a repeat model or a data binding mechanism (except in detail areas).

Fore is an attempt to revive the spirit of XForms for HTML while staying as close to (existing and evolving) web standards as possible and finally offer a advanced form processing platform in HTML. 

But why not simply implement XForms for HTML but have another spec? X4H is dated back to 2008 before the mobile revolution has really taken speed. The web platform and associated tooling has evolved quite a bit during the past 5 years requiring appropriate answers for the new challenges.

It is unlikely that native support for XForms for HTML will come from any browser vendor. The solution is to provide a JavaScript-driven implementation that builds upon the extensibility features of HTML5 (namely extension elements).

# Relation to other work

The evolving W3C [Web Components](http://www.w3.org/TR/components-intro/) specification is an ideal candidate for the implementation of Fore. It offers a HTML5-based component model for extending common HTML elements to create new custom elements which enhance the original element.

[Polymer](http://polymer-project.org) is an polyfill by Google implementing Web Components. It implements large parts of the specification in JavaScript polyfilling browsers that do not yet support a feature natively. Google and Netscape mainly drive the initiative now but major companies begin to adapt Web Components so there's a chance of native support for most major browsers.

# Table of Contents

### 1. Introduction
### 2. Approach
### 3. XForms as component
### 4. Models in forms
### 5. Data instances
### 6. Binding data
### 7. Submissions
### 8. JavaScript API
### 9. Declarative enhancement



----

## 1. Introduction

The goals of Fore are fully in sync with the ones given in [XForms for HTML](http://www.w3.org/TR/XForms-for-HTML/#intro-reading). A final implementation should feel as simple, intuitive and consistent with widespread standards as possible and must allow developers to turn existing HTML forms into production-grade, model-driven form applications.

In addition Fore will take the mobile world into account which has evolved dramatically since 2008 and forces to address new requirements such as offline-capability, touch events, push notifications and responsive layout.
 
### 1.1. Conventions

When referring to original XForms elements the prefix 'xf' is used to imply that the respective element is from the XForms namespace.

Extension (or custom) elements in the Web Components spec must use a name with a dash ('-') character in its name. For Fore elements the prefix 'fore-' will be used. 

Paragraphs formatted as blockquotes are (if not marked otherwise) copied from the original [XForms for HTML](http://www.w3.org/TR/XForms-for-HTML) spec.

### 2. Approach

This paper is not a specification in the strict sense. It just tries to outline the functionality to a reasonable extend to give users a clear impression of what to expect from the implementation. 

Some decisions about the implementation have already been taken (e.g. to use Polymer) so it's not specifying things in an technology-agnostic way but will make use of work done elsewhere. For instance the Web Components Specification is still under construction and likely things will change here in detail. Where possible these changes will be shielded away by the implementation. However this might not always be possible and users will probably need to change details of their already running forms to catch up with the ongoing development. So we're on the bleeding edge - you have been warned.

As developers (as opposed to spec writers) we are taking a pragmatic approach to get things running. This means that not each and every edge case might get considered in full detail or at all. But we're addicted to standards and will try to align our decisions with common practices and standards whereever possible. 

As already mentioned Fore is an attempt to revive XForms for HTML and as such we'll work along the existing draft but will rephrase most of the text trying to catch the spirit of it but making it more consumable for the reader.

### 3. XForms as component

HTML5 allows to extend the native DOM through extension elements. The only requirement for extension elements is that the name must contain a dash ('-') character (e.g. my-element).

The Web Components standard provides a set of technologies that build on top of extension elements and allow to specify custom element behavior and rendering in an encapsulated fashion.

Fore will use Web Components to implement the functionality of X4H and give web developers access to the rich processing model of XForms. The syntax is considered an important factor for the acceptance of the solution and must meet the expectations of the developers and re-use existing HTML syntax whereever possible and appropriate.

#### 3.1. Syntax
X4H mainly uses attributes on existing HTML elements to add its functionality. This however is not possible for the XForms model which has no correspondence in HTML. 

##### 3.1.1. Avoid requiring XML namespaces
XML Namespaces are often considered an entry hurdle for Web developers not familiar with XML. To keep the syntax easy Fore will not use XML-namespaced elements within its DOM at runtime. It will be able to consume those though.


### 4. Models in forms

Fore will support these different ways of using an XForms model.

#### 4.1. Importing a model

`<form model-src="myXFormsModel.xml">`

myXFormsModel.xml must contain fully XForms 1.1 conformant markup and will be resolved relative to `document.location.href` 

If the URL contains a fragment identifier (#) it will be resolved as idref in context of the current document.

By using linked models the host document no longer needs to be well-formed and namespaced XHTML. 
 
[full example](examples/linked-model.html)

#### 4.2. Using an inline model

To use an inline XForms model the containing document must be XHTML and declare the appropriate namespaces. The model can be referenced by a fragment identifier:


    <xf:model id="myModel">
    
        ...
    </xf:model>
            
    <form model-src="#myModel">
    ...
    </form>
    


[full example](examples/inline-model.html)


#### 4.3. Implicit model

If a form does not have a `model-src` attribute, the an implicit 'xf-model' element is generated and prepended before the first child element of the form element. The generated `<xf:model>` element is required as it is used by the XForms processor as the target of model events.

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
    

#### 4.4. Enhancement attributes for `<form>` element

Attribute    | Content Model  | Description
------------ | -------------  | ------------
model-src    | URL            | URL pointing to external XForms model
instance     | IDREF          | indicates the default instance of a form


### 5. Data instances

XForms uses XML and the XPath data model for representing instance data while JSON has established as a de-facto standard in the web development world. Fore will support both data models.

Instances can either be explicitly given as part of an `xf:model` or generated based upon the element structure of the UI controls. The latter are called **generated data instances**.

A model might use an arbitrary amount of instances being associated with a single form.

The default instance is indicated by the `instance` attribute of element `form `. The value of the `instance` attribute must match the `id` attribute of an existing instance. 

If the `instance` attribute is absent, a default instance will be generated based upon the  `name` attributes that appear on elements enclosed by the given `form`. The generated instance will be inserted as first child of the associated `model`. Generated instances will be constructed before actual model initialization takes place during 'xforms-model-construct` event.

#### 5.1. Instance data generation

The structure of generated instances is determined by the structure of elements bearing `name` attributes that are enclosed by a `form` element.

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

@@@ todo: describe JSON instance generation


  



#### 5.1.1. XML instances



        <xf-instance id="i-default">
            <data>
                <title></title>
                <firstname></firstname>
            </data>
        </xf-instance>

#### 5.1.2. JSON instances











Alternatively a JSON instance might get created like this:
    
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
    
@@@ discussion needed here @@@
- should there be a first level datamodel?
- which implications does the type of instance have for the use of binding expressions - always use one type of binding expression syntax and map that to the respective data model or make the binding syntax dependent on the instance type used?

 
### 6. Binding data
#### 6.1 using the `name` attribute
#### 6.2 using the `data-fore-bind` attribute
### 7. Submissions
### 8. JavaScript API
### 9. Declarative enhancement
#### 9.1. validation
#### 9.2. calculation
#### 9.3. handling of the `readonly` attribute

# Appendices

### A Glossary of Terms

### B Links

* [XForms 1.1](http://www.w3.org/TR/2009/REC-xforms-20091020/)
* [XForms for HTML](http://www.w3.org/TR/XForms-for-HTML)
* [Web Components](http://www.w3.org/TR/components-intro/)
* [Polymer](http://polymer-project.org)
* [JSONPath - JavaScript implementation](http://goessner.net/articles/JsonPath/)
* [json-path - Java implementation](https://code.google.com/p/json-path/)

### C Acknowledgements

------
[^1]: This opinion is build on top of many years of experience as implementors and users of XForms and applying it in many different problem domains. 
[^2]: It's beyond the scope of this document to discuss all reasons for this lack of acceptence in detail. There are political, technical and sociological aspects but in general these have to do with XForms markup 'not feeling right at home' within HTML documents.
[^3]: People that bring their knowledge of HTML writing, CSS and JavaScript.
[^4]: The author(s) have no further information about the reasons.