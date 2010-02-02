<%@ page import="de.betterform.xml.config.Config" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page contentType="text/html; ISO-8859-1" session="true" isErrorPage="true" %>
<%--
  ~ Copyright (c) 2010. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  --%>

<html>
<head>
	<title>Error Page</title>
	<style>
	body{
        font-family:Tahoma;
        font-size:14pt;
        background:url('<%=request.getContextPath()%>/resources/images/bgOne.gif') repeat-x scroll;
    }
	pre { font-size:8pt; }
    .errorContent{
        margin-top:50px;
        width:600px;
        border:thin solid steelblue;
        margin-left:auto;
        margin-right:auto;
        padding:20px;
    }
    .message1{
        display:block;
        color:steelblue;
        font-weight:bold;
    }
    .message2{
        display:block;
        color:darkred;
        font-size:12pt;
        padding-top:30px;
        font-weight:bold;
    }
    .message3{
        display:block;
        font-size:10pt;
        color:steelblue;
        margin-top:10px;
    }
    input{
        margin-top:20px;
        margin-left:0;
        margin-bottom:0;
    }
	</style>
</head>
<body>
<%-- Exception Handler --%>
<%
    Exception e = null;
    if (session.getAttribute("betterform.exception") != null) {
        e = (Exception) session.getAttribute("betterform.exception");
    } else if (request.getAttribute("betterform.exception") != null)
    {
        e = (Exception) request.getAttribute("betterform.exception");
    }

//    response.setContentLength(3000);
%>
<div class="errorContent">
    <img src="<%=request.getContextPath()%>/resources/images/error.png" width="24" height="24" alt="Error" style="float:left;padding-right:5px;"/>
    <div class="message1">
        Oops, an error occured...<br/>

    </div>
    <%
        String msg = e.getMessage();
        int start = e.getMessage().indexOf("/");
        String xpath ="unknown";
        String cause="";
        if(msg != null && start > 3){
            xpath = e.getMessage().substring(start-1);
            msg=msg.substring(0,start-3);
        }
        if(exception.getCause() != null && exception.getCause().getMessage() != null){
            cause = exception.getCause().getMessage();
        }

    %>

    <div class="message2"><%= msg %></div>
    <div class="message3"><strong>URL:</strong><br/> <%=session.getAttribute("betterform.referer")%></div>
    <div class="message3"><strong>Element causing Exception:</strong><br/><%=xpath%></div>
    <div class="message3"><strong>Caused by:</strong><br/><%=cause%></div>
    <form>
        <input type="button" value="Back" onClick="history.back()">
    </form>
    <div class="message3">
    <a href="mailto:<%=Config.getInstance().getProperty("admin.mail") %>?subject=XForms%20Problem%20at%20<%=session.getAttribute("betterform.referer")%>&Body=Message:%0D<%= msg %>%0D%0DElement%20causing%20Exception:%0D<%= xpath %>%0D%0DCaused%20by:%0D<%= URLEncoder.encode(cause,"UTF-8") %>">Report this problem...</a>
    </div>
</div>

</body>
</html>
