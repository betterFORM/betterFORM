<%--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  --%>

<html>
<head>
	<title>Dokumente</title>
    <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/styles/betterform-styles.css"/>
    <style type="text/css">
      a{
          text-decoration:none;
      }
      td{padding:2px;}
      .header{
          background-color:#faeeaa;
      }
      *{
          color:#4060a0;
      }
      #path{
          font-size:12pt;
          padding:10px 0px 10px 2px;
          color:#a406080;
          display:block;
      }
      .directory{
          background-color:#FCF6D3;

      }
      .file{
          background-color:#FCF6E3;
      }
    </style>

</head>
<%@ page import="org.apache.commons.logging.Log"%>
<%@ page import="org.apache.commons.logging.LogFactory"%>
<%@ page import="java.io.File"%>
<%@ page import="java.io.InputStreamReader" %>
<%@ page import="java.io.Reader" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Enumeration" %>
<%@ page session="true"%>

   <body text="black" link="black" vlink="black" alink="orange">

	<table width="100%" height="85%" cellpadding="10" cellspacing="0" border="0">
	<tr>
	
	<td valign="top">
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

	
	if (uri == null) {
		uri = "forms";
    }               
    readDir = rootDir + uri;
	cat.debug("URI: " + uri);
	cat.debug("Read dir: " + readDir);
%>

<div style="font-size:14pt;font-family:sans-serif;padding-top:10px;padding-bottom:10px;">
    Browse Pages
</div>

<table style="border:thin solid orange;" cellspacing="1" cellpadding="2" width="100%">
    <tr>
        <td class="header" colspan="3">
            <span id="path">/<%=uri%>
        </td>
    </tr>
    <%--
	<tr class="header">
		<td align="left" width="25%">
			File
		</td>

		<td align="center" width="5%">
			<span style="color:darkred">non-scripted HTML</span>
		</td>
--%>

<%--
		<td width="5%" align="center">
			scripted HTML
		</td>
--%>

<%--
        <td>
            Source
        </td>
		<td>
            last update
		</td>
	</tr>
--%>
	<%

	//list files from documents directory
	
	File root=new File(readDir);
    if (!root.exists()) {
        root.mkdirs();
    }
	String[] files=root.list();
	cat.debug("files: " + files.length);
	File f=null;
	String up=null;
	if (files!=null)
	{
		if(uri.indexOf("/")!=-1){
			up=uri.substring(0,uri.lastIndexOf("/"));
			%>
			<tr bgcolor="#FCF6D3" style="border:thin solid orange;">
				<td valign="middle" colspan="5">
				<a href="forms.jsp?<%=up%>">
					<img src="<%=request.getContextPath()%>/resources/images/folder.gif" border="0" width="20" height="20" align="left">..
				</a>
				</td>
			</tr>				
			<%
		}

		for(int i=0;i< files.length;i++){
			File aFile=new File( files[i]);
			f=new File(readDir + "/" + aFile.getName());	
			
			if(f.isDirectory()){
			%>
					
				<tr class="directory">
					<td valign="middle" colspan="5">
					<a href="<%=request.getContextPath()%>/jsp/forms.jsp?<%=uri%>/<%=aFile.getName()%>">
						<img src="<%=request.getContextPath()%>/resources/images/folder.gif" border="0" width="20" height="20" align="left"><%=aFile.getName()%>
					</a>
					</td>
			
				</tr>
			<%
			}
		}
	}
	root=new File(readDir);
	files=root.list();
	cat.debug ("files: " + files.length);

	if (files!=null)
	{
		for(int i=0;i< files.length;i++){
			File aFile=new File( files[i]);
			f=new File(readDir + "/" + aFile.getName());	


			if(!(f.isDirectory())){
	
			%>
				<tr class="file">

                    <td width="25%">
                        <%--
                        <%
                            if(aFile.getName().endsWith(".xhtml")){
                        %>
                        <a href="<%=request.getContextPath()%>/XFormsServlet?form=/<%=uri%>/<%=aFile.getName()%>"
                            onclick="this.href=this.href + '&js=enabled'">
                            <%=aFile.getName()%>
                        </a>
--%>
<%--
                        <a href="<%=request.getContextPath()%>/XFormsServlet?form=/<%=uri%>/<%=aFile.getName()%>"
                            onclick="this.href=this.href + '?js=enabled'">
                            <%=aFile.getName()%>
                        </a>
--%>
                        <a href="<%=request.getContextPath()%>/<%=uri%>/<%=aFile.getName()%>?" onclick="this.href=this.href.substring(0,this.href.indexOf('?'));">
                            <%=aFile.getName()%>
                        </a>
<%--
                        <a href="<%=request.getContextPath()%>/<%=uri%>/<%=aFile.getName()%>"
                            onclick="this.href=this.href + '?js=enabled'">
                            <%=aFile.getName()%>
                        </a>
                        <%
                            }else{
                        %>
                            <%=aFile.getName()%>
                        <%
                            }
                        %>
--%>
                    </td>
                    <td width="25%">
                            <a href="<%=request.getContextPath()%>/<%=uri%>/<%=aFile.getName()%>?source=true">source</a>
                    </td>

                    <!-- ################## column showing unscripted link - currently disabled ######### -->
                    <%--<td align="center" valign="middle">
                        <%
                            if(aFile.getName().endsWith(".xhtml")){
                        %>
                        --%><%--<a href="<%=request.getContextPath()%>/XFormsServlet?form=/<%=uri%>/<%=aFile.getName()%>">--%><%--
                        <a href="<%=request.getContextPath()%>/<%=uri%>/<%=aFile.getName()%>">
                            <img src="<%=request.getContextPath()%>/resources/images/text.gif" border="0" width="20" height="20" align="center">
                        </a>
                        <%
                            }else{
                        %>
                            <img src="<%=request.getContextPath()%>/resources/images/text.gif" border="0" width="20" height="20" align="center">
                        <%
                            }
                        %>
					</td>--%>

<%--
                    <td align="center" valign="middle">
                        <a href="<%=request.getContextPath()%>/XFormsServlet?form=/<%=uri%>/<%=aFile.getName()%>"
                            onclick="this.href=this.href + '&js=enabled'">
                            <img src="<%=request.getContextPath()%>/resources/images/text.gif" border="0" width="20" height="20" align="center">
                        </a>
					</td>
--%>

					<td>
                            <%= ""+ DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.MEDIUM).format(new Date(f.lastModified())) %>
					</td>
					
				</tr>
				<%
				}
			}
	}
	%>

</table>
<%--

For Uploading Forms:

<a href="upload">Upload some Data into this directory!</a>

--%>
</td></tr></table>

<p align="right" style="font-size:8pt;">&copy; 2010 betterForm</p>
</body>
</html>
