<%@ page import="java.io.Reader,
                 java.io.InputStreamReader,
                 java.util.Enumeration,
                 java.text.DateFormat,
                 java.util.Date"%>
<%@ page session="true" %>
<%@ page errorPage="error.jsp" %>
<%@ page pageEncoding="UTF-8" contentType="text/html;charset=UTF-8" %>
<html>
    <head>
        <title>Instance Data submitted</title>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/styles/xforms.css"/>
        <link rel="stylesheet" type="text/css" href="<%=request.getContextPath()%>/resources/styles/betterform-theme.css"/>
     </head>
    <body>
        <%
            // create a buffer longer than binary content to have enough space for character conversion
//            StringBuffer buffer = new StringBuffer((int) (request.getContentLength() * 1.5));

            // convert binary content to character data in specified encoding
//            Reader reader = new InputStreamReader(request.getInputStream(), request.getCharacterEncoding());

            StringBuffer buffer = new StringBuffer();
            int tmp = request.getContentLength();
            /*
            testing theorectical case of contentLength being 0 just for completeness. In the real world this
            shouldn't ever happen cause there will be at least some header been sent.
            */
            if(tmp != 0) {
                if(tmp < 0) { tmp = - tmp;}
                buffer.setLength(tmp);
                String encoding = request.getCharacterEncoding();
                Reader reader;
                if (encoding == null ) {
                    reader = new InputStreamReader(request.getInputStream());
                } else {
                    reader = new InputStreamReader(request.getInputStream(), encoding);
                }

                int i = reader.read();
                while (i > -1) {
                    switch (i) {
                        case '<':
                            buffer.append("&lt;");
                            break;
                        default:
                            buffer.append((char) i);
                    }
                    i = reader.read();
                }
            }
        %>
        <fieldset class="xfGroup xfFullGroup">
            <legend class="label">Instance Data</legend>
            <div style="color: darkred;"><pre><%=buffer%></pre></div>
        </fieldset>

        <fieldset class="xfGroup xfFullGroup">
            <legend class="label">Request</legend>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Request Method</label><span class="xfValue"><%= request.getMethod() %></span></div>
            <div class="output xfEnabled xfReadwrite xfxfOptional xfValid"><label class="label">Request URI</label><span class="xfValue"><%= request.getRequestURI() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Request Protocol</label><span class="xfValue"><%= request.getProtocol() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Servlet Path</label><span class="xfValue"><%= request.getServletPath() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Path Info</label><span class="xfValue"><%= request.getPathInfo() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Path Translated</label><span class="xfValue"><%= request.getPathTranslated() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Query String</label><span class="xfValue"><%= request.getQueryString() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Content Length</label><span class="xfValue"><%= request.getContentLength() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Content Type</label><span class="xfValue"><%= request.getContentType() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Server Name</label><span class="xfValue"><%= request.getServerName() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Server Port</label><span class="xfValue"><%= request.getServerPort() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Remote User</label><span class="xfValue"><%= request.getRemoteUser() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Remote Address</label><span class="xfValue"><%= request.getRemoteAddr() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Remote Host</label><span class="xfValue"><%= request.getRemoteHost() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Authorization Scheme</label><span class="xfValue"><%= request.getAuthType() %></span></div>
        </fieldset>

        <fieldset class="xfGroup xfFullGroup">
            <legend class="label">Request Headers</legend>
            <%
                Enumeration headers = request.getHeaderNames();
                while (headers.hasMoreElements()) {
                   String name = headers.nextElement().toString(); %>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label"><%= name %></label><span class="xfValue"><%= request.getHeader(name) %></span></div>
            <%
                }
            %>
        </fieldset>

        <fieldset class="xfGroup xfFullGroup">
            <legend class="label">Session</legend>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Session ID</label><span class="xfValue"><%= session.getId() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Creation time</label><span class="xfValue"><%= DateFormat.getDateTimeInstance().format(new Date(session.getCreationTime())) %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Last accessed</label><span class="xfValue"><%= DateFormat.getDateTimeInstance().format(new Date(session.getLastAccessedTime())) %></span></div>
        </fieldset>

        <fieldset class="xfGroup xfFullGroup">
            <legend class="label">Session Attributes during submit</legend>
            <%
                Enumeration variables = session.getAttributeNames();
                while (variables.hasMoreElements()) {
                    String name = variables.nextElement().toString(); %>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label"><%= name %></label><span class="xfValue"><%= session.getAttribute(name) %></span></div>
            <%
                }
            %>
        </fieldset>

        <fieldset class="xfGroup xfFullGroup">
            <legend class="label">Response</legend>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Content Type</label><span class="xfValue"><%= response.getContentType() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Character Encoding</label><span class="xfValue"><%= response.getCharacterEncoding() %></span></div>
            <div class="output xfEnabled xfReadwrite xfOptional xfValid"><label class="label">Locale</label><span class="xfValue"><%= response.getLocale() %></span></div>

        </fieldset>

        <center>
            <font face="sans-serif"><a href="<%=request.getContextPath()%>/jsp/forms.jsp">Back to Samples</a></font>
        </center>
    </body>
</html>
