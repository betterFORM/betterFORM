<%
/*
@author mark dimon mailto:mark@markdimon.com

this code simply returns the supplied xml instance data back to the client
*/
        Reader reader = request.getReader();
        char[] buf = new char[request.getContentLength()];
        reader.read( buf );
        response.setContentType("text/xml");
        response.setHeader("Content-disposition", "attachment; filename=response.xml" );
        out.write( buf );

%>
<%@ page import="java.io.*" %>