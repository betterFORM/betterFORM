# Using betterForm in a Cross Context Environment

## Introduction

This Documentation describes how to use betterForm from another servlet container context. 
It allows to store XForms in your application context and let betterForm process the form
in its own context.

### Advantages
  
  * Easy and independently updating of each software stacks.
  * Usage of the same software library in different versions.
    Since both software stacks are loaded from different class loaders
    the do not interfere each other.
  * Creating XForms from within your web application and the created form 
    is processed in the context of betterform.

### Disadvantages

  * configuring the setup is not simple.
  * (the setup can be slower)

## Setup

In order to operate with two interacting contexts one must obey some requirements.

### Prerequisite
 
  * The setup can only be used in one servlet container.
    Both contexts have to be deployed on the same servlet container. 
    Distributed systems are not supported.
  * The servlet container muss allow to access the context of betterForm.
    Most servlet container allow this by default. Apache needs to be configured
    separately. The current distributions of betterForm already contain an context.xml 
    file which allows other context to access betterForms context.
  * The distributed filter class muss be installed in the context which
    accesses betterForm. An Jar file is (not yet) distributed.
  * The filter must me configured correctly. The filter is configured
    in the deployment descriptor of the web application.

### Configuration

Most of the configuration is done in the deployment descriptor.
betterForm itself is already configured distributed. In the deployment descriptor
one has to specify:

  * the URL when the filter is used
  * the context name of betterForm
  * and URL which points back to the applications context

The filter is used if an url pattern matches. It should be a simple 
path. This path can hold forms or servlets which generate forms.

The name of the betterForm context is configured through the filter parameter
'xforms.engine.webcontext'. It should contain the name of the context.

In the filter parameter 'xforms.engine.resource' one configures an URL 
loop. This allows the filter to handle and forward requests which are resources 
within the betterForm context. This applies to css, scripts and images distributed
with betterForm.

Additionally one can configure the servlet which processes forms in betterForm.
It is not recommended to change this variable. It is intended for development.

## Examples for Apache Tomcat

    Beispiele ab Tomacat Version 5.x

### Applications WEB.xml

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

  * Experimental Status
  * Authentication funktioniert
  * Weiterleitungs URL ist komplex.
  * Debugging ist nicht funktionstüchtig
  * nur relative URLs

### Used Technology

   * Benutzung von `Dispatcher.forward()
   * Alle von betterForm genutzten und generierten URLs erhalten ein Präfix.
   * Filter in der Anwendung leitet die Anfragen dieser URLs an betterForm weiter.
   * Request.parameter 
