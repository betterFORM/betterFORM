package de.betterform.agent.web.servlet;

import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class
        FormResponseServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out;
        String title = "Well House Consultants";

        String param1 = request.getParameter("myname");

        // set content type and other response header fields first
        response.setContentType("text/html");


        // then write the data of the response
        out = response.getWriter();

        out.println("<html><head><title>");
        out.println(title);
        out.println("</title></head><body class='bgcolor:green;'>");
        out.println("<center><H1>Result Window</H1>");
        out.println("<p>This is your response window.<p>");
        out.println("Called from " + HttpUtils.getRequestURL(request).toString());
        out.println("Data: '" + param1 + "'<p>");
        out.println("</center></body></html>");
        out.close();
    }
}