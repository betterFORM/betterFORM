# Using betterForm in a Cross Context Environment

## Introduction

This documentation describes how to use [betterForm][0] from within a different 
servlet container context. It allows to store or generate forms in your 
application context and let [betterForm][0] process the form in its own context.

### Advantages
  
  * Easy and independently updating the software in the separated contexts.
  * Usage of the same software library in different versions.
    Since both software stacks are loaded from different class loaders
    the do not interfere each other.
  * Creating [XForms][1] from within your web application. The created form
    is processed in the context of [betterForm][0].

### Disadvantages

  * the configuring is not simple.

## Setup



### Prerequisite
 
  * The setup can only be used in one servlet container.
    Both contexts have to be deployed on the same servlet container. 
    Distributed systems are not supported.
  * The servlet container muss allow to access the context of [betterForm][0].
    Most servlet container allow this by default. Apache needs to be configured
    separately. The current distributions of [betterForm][0] already contain an context.xml 
    file which allows other context to access [betterForm][0]'s context.
  * The distributed filter class muss be installed in the context which
    accesses [betterForm][0]. An Jar file is (not yet) distributed.
  * The filter must me configured correctly. The filter is configured
    in the deployment descriptor of the web application.

### Configuration

All the configuration is done in the deployment descriptor.
[betterForm][0] itself is already configured for this configuration.
In the deployment descriptor one has to specify:

  * the URL for the filter.
  * the context name of [betterForm][0].
  * and URL which points back to the applications context

The filter is mapped to an URL. If this url is requested the returned
page is send to [betterForm][0] and processed as an [XForm form][1].
Under the URL one can store forms in files or servlets which generate forms.

The name of the [betterForm][0] context is configured through the filter parameter
`xforms.engine.webcontext`. It should contain the name of the context as string.

In the filter parameter `xforms.engine.resource` one must specifies an URL which 
points back to the filter. The url has to be appended with an virtual directory. 
This allows the filter to handle and forward requests which are resources 
within the [betterForm][0] context. This applies to css, scripts and images distributed
with [betterForm][0].

Additionally one can configure the servlet which processes forms in [betterForm][0].
It is not recommended to change this variable. It is intended for development.

## Examples for Apache Tomcat

The following example files should work with Tomcat 5.5.x and Tomcat 6.x.

### Context.xml

This file is only needed when betterForm is used together with [Tomcat][2]. It should be stored 
in the directory `META-INF` under the name `context.xml`. When first deployed Tomcat copies 
it to its own directory structure. Changes after the first deployment are not considered.

    <?xml version="1.0" encoding="UTF-8"?>
    <Context antiJARLocking="true" crossContext="true" path="/betterform"/>

### Applications WEB.xml

This file shows a minimal setup. The setup renders all forms in the `forms`
directory with the help of [betterForm][0] into an html page. The setup specifies
the context in which [betterForm][0] is running. Its important that the path 
of `xforms.engine.resources` is set to the same directory as the patter of the filter.


    <?xml version="1.0" encoding="UTF-8"?>
    <web-app version="2.5" 
             xmlns="http://java.sun.com/xml/ns/javaee" 
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
             http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">
      <description>Sampleapplication for forwarding Responses to betterForm.</description>
      <display-name>Sample Application</display-name>
      <filter>
        <filter-name>CrossContextFilter</filter-name>
        <filter-class>de.betterform.agent.web.filter.CrossContextFilter</filter-class>
        <init-param>
          <param-name>xforms.engine.webcontext</param-name>
          <param-value>betterform</param-value>
        </init-param>
        <init-param>
          <param-name>xforms.engine.servlet</param-name>
          <param-value>/repeater</param-value>
        </init-param>
        <init-param>
          <param-name>xforms.engine.resources</param-name>
          <param-value>/SampleApp/forms/forward</param-value>
        </init-param>
      </filter>
      <filter-mapping>
        <filter-name>CrossContextFilter</filter-name>
        <url-pattern>/forms/*</url-pattern>
      </filter-mapping>
    </web-app>
    
## Status

The current status of this configuration is experimental. We have not yet used this setup
with any of our clients. This is the first public release. So be warned!

In the current version the code should allow authentication. It does not support 
the configuration of [betterForm][0] with absolute URLs. Reporting of errors does
not work correctly too.

The configuration of the filter in the WEB.xml is quite complex and error prone.
I would be much better to generate the path to [betterForms][0] internal resources.

[0]: http://www.betterform.de "betterForm"
[1]: http://www.w3.org/TR/xforms11/ "XForms 1.1 Spec"
[2]: http://tomcat.apache.org/tomcat-6.0-doc/config/context.html "Cross Context Configuration"