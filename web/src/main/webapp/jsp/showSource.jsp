<%@ page import="java.io.*" %>
<%@ page import="org.apache.commons.lang.StringEscapeUtils" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<html>
  <head>
      <title>Simple jsp page</title>
  </head>
  <body>

  <%
      String relativeFileURI = request.getParameter("sourceFile");
      String escapedFileContent = "";
      try {
          String realPath = request.getRealPath(relativeFileURI);
          StringBuffer fileContent = new StringBuffer(1000);
          BufferedReader fileReader = new BufferedReader(new FileReader(realPath));
          char[] buf = new char[1024];
          int numRead = 0;
          while ((numRead = fileReader.read(buf)) != -1) {
              String readData = String.valueOf(buf, 0, numRead);
              fileContent.append(readData);
              buf = new char[1024];
          }
          fileReader.close();
          escapedFileContent = fileContent.toString();
      } catch (Exception e){

      }
      String result = StringEscapeUtils.escapeXml(escapedFileContent);


  %>
<pre><%=result%></pre>
</html>