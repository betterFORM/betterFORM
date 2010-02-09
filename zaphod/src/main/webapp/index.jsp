<%-- 
    Document   : index
    Created on : Nov 12, 2009, 11:40:51 AM
    Author     : Fabian Otto
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" %>


<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN"
"http://www.w3.org/TR/html4/loose.dtd">

<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>Welcome to Zaphod</title>
    <style type="text/css">
        @import "/zaphod/forms/forward/resources/scripts/dijit/themes/soria/soria.css";
        @import "/zaphod/forms/forward/resources/styles/xforms.css";
        @import "/zaphod/forms/forward/resources/styles/betterform.css";
        @import "/zaphod/forms/forward/resources/styles/reference.css";
    </style>

    <style type="text/css">
         .sidebar {
             float: right;
             width: 30%;
             background: white;
             border-color: black;
             border-width: 1px;
             border-style: dotted;
             margin-top: 100px;
             padding: 20px;
         }

         .main {
             float: left;
             width: 70%;
         }
    </style>
</head>

<body class="soria" style="margin:30px;overflow:auto;">

<div class="sidebar">

    <h2>Examples</h2>

    The following List of examples show the reference forms of betterForm.s

    <ul>
        <li><a href="forms/reference/Group.xhtml">Group</a></li>
        <li><a href="forms/reference/Input.xhtml">Input</a></li>
        <li><a href="forms/reference/Output.xhtml">Output</a></li>
        <li><a href="forms/reference/Range.xhtml">Range</a></li>
        <li><a href="forms/reference/Repeat.xhtml">Repeat</a></li>
        <li><a href="forms/reference/Secret.xhtml">Secret</a></li>
        <li><a href="forms/reference/Select.xhtml">Select</a></li>
        <li><a href="forms/reference/Select1.xhtml">Select1</a></li>
        <li><a href="forms/reference/Switch.xhtml">Switch</a></li>
        <li><a href="forms/reference/Textarea.xhtml">Textarea</a></li>
        <li><a href="forms/reference/Trigger.xhtml">Trigger</a></li>
        <li><a href="forms/reference/Upload.xhtml">Upload</a></li>
    </ul>
</div>

<div>


    <h1>Zaphod</h1>

    Welcome to Zaphod.

    <h2>Introduction</h2>

    <p>This documentation describes how to use <a href="http://www.betterform.de" title="betterForm">betterForm</a> from
        within a different
        servlet container context. It allows to store or generate forms in your
        application context and let <a href="http://www.betterform.de" title="betterForm">betterForm</a> process the
        form in
        its own context.</p>

    <h3>Advantages</h3>

    <ul>
        <li>Easy and independently updating the software in the separated contexts.</li>
        <li>Usage of the same software library in different versions.
            Since both software stacks are loaded from different class loaders
            the do not interfere each other.
        </li>
        <li>Creating <a href="http://www.w3.org/TR/xforms11/" title="XForms 1.1 Spec">XForms</a> from within your web
            application. The created form
            is processed in the context of <a href="http://www.betterform.de" title="betterForm">betterForm</a>.
        </li>
    </ul>

    <h3>Disadvantages</h3>

    <ul>
        <li>the configuring is complex.</li>
    </ul>


    <h2>Setup</h2>

    <h3>Prerequisite</h3>

    <ul>
        <li>The setup can only be used in one servlet container.
            Both contexts have to be deployed on the same servlet container.
            Distributed systems are not supported.
        </li>
        <li>The servlet container muss allow to access the context of <a href="http://www.betterform.de"
                                                                         title="betterForm">betterForm</a>.
            Most servlet container allow this by default. Apache needs to be configured
            separately. The current distributions of <a href="http://www.betterform.de"
                                                        title="betterForm">betterForm</a>
            already contain an context.xml
            file which allows other context to access <a href="http://www.betterform.de"
                                                         title="betterForm">betterForm</a>'s
            context.
        </li>
        <li>The distributed filter class muss be installed in the context which
            accesses <a href="http://www.betterform.de" title="betterForm">betterForm</a>. An Jar file is (not yet)
            distributed.
        </li>
        <li>The filter must me configured correctly. The filter is configured
            in the deployment descriptor of the web application.
        </li>
    </ul>

    <h3>Configuration</h3>

    <p>All the configuration is done in the deployment descriptor.
        <a href="http://www.betterform.de" title="betterForm">betterForm</a> itself is already configured for this
        configuration.
        In the deployment descriptor one has to specify:</p>

    <ul>
        <li>the URL for the filter.</li>
        <li>the context name of <a href="http://www.betterform.de" title="betterForm">betterForm</a>.</li>
        <li>and URL which points back to the applications context</li>
    </ul>

    <p>The filter is mapped to an URL. If this url is requested the returned
        page is send to <a href="http://www.betterform.de" title="betterForm">betterForm</a> and processed as an <a
                href="http://www.w3.org/TR/xforms11/" title="XForms 1.1 Spec">XForm form</a>.
        Under the URL one can store forms in files or servlets which generate forms.</p>

    <p>The name of the <a href="http://www.betterform.de" title="betterForm">betterForm</a> context is configured
        through
        the filter parameter
        <code>xforms.engine.webcontext</code>. It should contain the name of the context as string.</p>

    <p>In the filter parameter <code>xforms.engine.resource</code> one must specifies an URL which
        points back to the filter. The url has to be appended with an virtual directory.
        This allows the filter to handle and forward requests which are resources
        within the <a href="http://www.betterform.de" title="betterForm">betterForm</a> context. This applies to css,
        scripts and images distributed
        with <a href="http://www.betterform.de" title="betterForm">betterForm</a>.</p>

    <p>Additionally one can configure the servlet which processes forms in <a href="http://www.betterform.de"
                                                                              title="betterForm">betterForm</a>.
        It is not recommended to change this variable. It is intended for development.</p>

    <h2>Examples for Apache Tomcat</h2>

    <p>The following example files should work with Tomcat 5.5.x and Tomcat 6.x.</p>

    <h3>Context.xml</h3>

    <p>This file is only needed when betterForm is used together with <a
            href="http://tomcat.apache.org/tomcat-6.0-doc/config/context.html"
            title="Cross Context Configuration">Tomcat</a>. It should be stored
        in the directory <code>META-INF</code> under the name <code>context.xml</code>. When first deployed Tomcat
        copies
        it to its own directory structure. Changes after the first deployment are not considered.</p>

        <pre><code>&lt;?xml version="1.0" encoding="UTF-8"?&gt;
            &lt;Context antiJARLocking="true" crossContext="true" path="/betterform"/&gt;
        </code></pre>

    <h3>Applications WEB.xml</h3>

    <p>This file shows a minimal setup. The setup renders all forms in the <code>forms</code>
        directory with the help of <a href="http://www.betterform.de" title="betterForm">betterForm</a> into an html
        page.
        The setup specifies
        the context in which <a href="http://www.betterform.de" title="betterForm">betterForm</a> is running. Its
        important
        that the path
        of <code>xforms.engine.resources</code> is set to the same directory as the patter of the filter.</p>

        <pre><code>
&lt;?xml version="1.0" encoding="UTF-8"?&gt;
&lt;web-app version="2.5"
            xmlns="http://java.sun.com/xml/ns/javaee"
            xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
            xsi:schemaLocation="http://java.sun.com/xml/ns/javaee
            http://java.sun.com/xml/ns/javaee/web-app_2_5.xsd"&gt;
  &lt;description&gt;Sampleapplication for forwarding Responses to betterForm.&lt;/description&gt;
  &lt;display-name&gt;Sample Application&lt;/display-name&gt;
  &lt;filter&gt;
    &lt;filter-name&gt;CrossContextFilter&lt;/filter-name&gt;
    &lt;filter-class&gt;de.betterform.agent.web.filter.CrossContextFilter&lt;/filter-class&gt;
    &lt;init-param&gt;
      &lt;param-name&gt;xforms.engine.webcontext&lt;/param-name&gt;
      &lt;param-value&gt;betterform&lt;/param-value&gt;
    &lt;/init-param&gt;
    &lt;init-param&gt;
      &lt;param-name&gt;xforms.engine.servlet&lt;/param-name&gt;
      &lt;param-value&gt;/repeater&lt;/param-value&gt;
    &lt;/init-param&gt;
    &lt;init-param&gt;
      &lt;param-name&gt;xforms.engine.resources&lt;/param-name&gt;
      &lt;param-value&gt;/SampleApp/forms/forward&lt;/param-value&gt;
    &lt;/init-param&gt;
  &lt;/filter&gt;
  &lt;filter-mapping&gt;
    &lt;filter-name&gt;CrossContextFilter&lt;/filter-name&gt;
    &lt;url-pattern&gt;/forms/*&lt;/url-pattern&gt;
  &lt;/filter-mapping&gt;
&lt;/web-app&gt;
        </code></pre>

    <h2>Status</h2>
    <ul>
        <li>
            <p>The current status of this configuration is experimental. We have not yet used this setup
                with any of our clients. This is the first public release. So be warned!</p>
        </li>

        <li>
            <p>In the current version the code should allow authentication. It does not support
                the configuration of <a href="http://www.betterform.de" title="betterForm">betterForm</a> with absolute
                URLs.
                Reporting of errors does
                not work correctly too.</p>
        </li>
        <li>
            <p>The configuration of the filter in the WEB.xml is quite complex and error prone.
                I would be much better to generate the path to <a href="http://www.betterform.de"
                                                                  title="betterForm">betterForms</a>
                internal resources.</p>
        </li>

    </ul>
</div>

</body>
</html>
