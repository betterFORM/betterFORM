<%--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  --%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
    "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en" lang="en">
    <head>
		<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
		<meta name="Author" content="Joern Turner">
		<meta name="Author" content="Lars Windauer">
		<meta name="publisher" content="betterFORM Project">
		<meta name="description" content="betterFORM lighSteelBlue Demo, Reference and XForms 1.1 conformance testsuite browser. Explore the world of XForms"/>
		<meta name="keywords" content="betterFORM, xforms, forms, form, xhtml, xpath, xslt, xslt2.0, dojo, dojotoolkit, java, javascript, xml, schema, web, web2.0, web2, web3.0, web3, semantic, semantic web, joern turner, turner, lars windauer, ajax, xforms processor, processor">
		<meta name="copyright" content="betterForm Project">
		<meta name="content-language" content="en">
		<meta name="robots" content="all">
		<meta http-equiv="expires" content="Wed, 9 Feb 2011 12:21:57 GMT">
		<meta name="revisit-after" content="5 days">

        <title>betterFORM lightSteelBlue Forms Browser</title>

        <link REV="made" HREF="mailto:info@betterform.de"/>
        <link rel="SHORTCUT ICON" href="images/betterform.ico"/>

        <link rel="ICON" href="../images/betterform.ico" type="image/x-icon"/>
        <link rel="stylesheet" type="text/css" href="../styles/bf.css"/>


        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/styles/betterform-styles.css"/>

    </head>
    <%@ page import="org.apache.commons.logging.Log"%>
    <%@ page import="org.apache.commons.logging.LogFactory"%>
    <%@ page import="java.io.File"%>
    <%@ page import="java.io.InputStreamReader" %>
    <%@ page import="java.io.Reader" %>
    <%@ page import="java.text.DateFormat" %>
    <%@ page import="java.util.Date" %>
    <%@ page import="java.util.Enumeration" %>
    <%@ page import="de.betterform.agent.web.utils.SortingWalker" %>
    <%@ page import="java.util.Collections" %>
    <%@ page import="java.util.List" %>
    <%@ page session="true"%>

    <body id="formsbrowser">
        <div class="page">

            <div id="header">
                <div class="pageMarginBox">
                    <div class="languages">
                        <img src="../images/en.png" class="langSelector" alt="english"/>
                        <img src="../images/de.png" class="langSelector" alt="deutsch"/>
                    </div>
                    <div id="logoBar">
                        <a href="../index.html" id="linkLogo" class="link"><img id="logo" src="../images/logo.png" alt="betterFORM project"/></a>

                        <div id="topnav">
                            <a href="../index.html">home</a><span class="menuDevider"> | </span>
                            <a href="../demo.xhtml">demo</a><span class="menuDevider"> | </span>
                            <a href="../download.html">download</a><span class="menuDevider"> | </span>
                            <a href="../product.html">product</a><span class="menuDevider"> | </span>
                            <a href="../support.html">support</a><span class="menuDevider"> | </span>
                            <a href="../whoweare.html">who we are</a>
                        </div>
                    </div>
                </div>
            </div>

            <div id="content" class="contact">
                <img id="shadowTop" src="../images/shad_top.jpg" alt=""/>

                <!-- ######################### Content here ################################## -->
                <!-- ######################### Content here ################################## -->
                <!-- ######################### Content here ################################## -->
                <!-- ######################### Content here ################################## -->
                <!-- ######################### Content here ################################## -->
                <div class="pageMarginBox">
                    <div class="contentBody">
                        <h1>Forms Browser</h1>
<%--
                        <table width="100%" height="85%" cellpadding="10" cellspacing="0" border="0">
                        <tr>

                        <td valign="top">
--%>
                    <%!
                        static Log cat = LogFactory.getLog("de.betterform.agent.web.jsp");

                        String root = null;
                        String rootDir = null;

                        public void jspInit() {

                            // +++ read general parameters from web.xml
                            root = getServletConfig().getServletContext().getRealPath("");
                            if (root == null) {
                                root = getServletConfig().getServletContext().getRealPath(".");
                            }
                            rootDir = root + "/";
                        }

                    %>

                    <%
                        String uri=request.getQueryString();
                        String readDir=null;


                        if (uri == null || uri.contains("..") || ! uri.contains("forms") ) {
                            uri = "forms";
                        }
                        readDir = rootDir + uri;
                        cat.debug("URI: " + uri);
                        cat.debug("Read dir: " + readDir);
                    %>

                    <table>
                        <tr>
                            <td class="formBrowserHeader" colspan="3">
                                <span id="path">/<%=uri%>
                            </td>
                        </tr>

                        <%

                        //list files from documents directory

                        File root=new File(readDir);
                        if (root.exists()) {
                            java.util.List<File> results = SortingWalker.sortDirsAndFiles(root);
                            File[] files = results.toArray(new File[results.size()]);
                            cat.debug("files: " + files.length);
                            File f = null;
                            String up = null;
                            if (files != null) {
                                if (uri.indexOf("/") != -1) {
                                    up = uri.substring(0, uri.lastIndexOf("/"));
                        %>
                        <tr>
                            <td class="directory" colspan="3">
                                <a href="forms.jsp?<%=up%>"><img
                                        src="<%=request.getContextPath()%>/resources/images/folder.gif" border="0"></a>
                                <a class="textLink" href="forms.jsp?<%=up%>">..</a>
                            </td>
                        </tr>
                        <%
                            }

                            for (int i = 0; i < files.length; i++) {
                                File aFile = files[i];
                                f = new File(readDir + "/" + aFile.getName());

                                if (f.isDirectory()) {
                        %>

                        <tr class="directory">
                            <td colspan="3">
                                <a href="<%=request.getContextPath()%>/jsp/forms.jsp?<%=uri%>/<%=aFile.getName()%>"><img
                                        src="<%=request.getContextPath()%>/resources/images/folder.gif" border="0"></a>
                                <a class="textLink"
                                   href="<%=request.getContextPath()%>/jsp/forms.jsp?<%=uri%>/<%=aFile.getName()%>"><%=aFile.getName()%>
                                </a>
                            </td>
                        </tr>
                        <%
                                    }
                                }
                            }
                            root = new File(readDir);
                            results = SortingWalker.sortDirsAndFiles(root);
                            files = results.toArray(new File[results.size()]);
                            cat.debug("files: " + files.length);

                            if (files != null) {
                                for (int i = 0; i < files.length; i++) {
                                    File aFile = files[i];
                                    f = new File(readDir + "/" + aFile.getName());


                                    if (!(f.isDirectory())) {

                        %>
                        <tr class="file">
                            <td width="25%">
                                <a href="<%=request.getContextPath()%>/<%=uri%>/<%=aFile.getName()%>?"
                                   onclick="this.href=this.href.substring(0,this.href.indexOf('?'));" target="_blank">
                                    <%=aFile.getName()%>
                                </a>
                            </td>
                            <td width="25%">
                                <a href="<%=request.getContextPath()%>/<%=uri%>/<%=aFile.getName()%>?source=true"
                                   target="_blank">source</a>
                            </td>

                            <td><%= "" + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(f.lastModified())) %>
                            </td>

                        </tr>
                        <%
                                        }
                                    }
                                }
                            }
                        %>

                    </table>
                    <%--

                    For Uploading Forms:

                    <a href="upload">Upload some Data into this directory!</a>

                    --%>
                    <%--</td></tr></table>--%>
                    </div>
                </div>
            </div>
			<div id="footer">
				<img id="shadowBottom" src="../images/shad_bottom.jpg" alt=""/>

				<div class="pageMarginBox">
				<span id="bottomMenu">
					&#169; 2010 betterFORM<span class="menuDevider"> | </span>
					<a href="../index.html">home</a><span class="menuDevider"> | </span>
					<a href="../demo.xhtml">demo</a><span class="menuDevider"> | </span>
					<a href="../download.html">download</a><span class="menuDevider"> | </span>
					<a href="../product.html">product</a><span class="menuDevider"> | </span>
					<a href="../support.html">support</a><span class="menuDevider"> | </span>
					<a href="../whoweare.html">who we are</a><span class="menuDevider"> | </span>
					<a href="../contact.html">contact / impressum</a>
				</span>
				</div>
			</div>
        </div>

        <script type="text/javascript">
            var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
            document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
        </script>
        <script type="text/javascript">
            try {
            var pageTracker = _gat._getTracker("UA-15044944-1");
            pageTracker._trackPageview();
            } catch(err) {}
        </script>
    </body>
</html>
