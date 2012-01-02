package de.betterform.agent.web.servlet;

import de.betterform.xml.config.Config;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * Created by IntelliJ IDEA.
 * User: zwobit
 * Date: 24.10.11
 * Time: 14:37
 * To change this template use File | Settings | File Templates.
 */
public class XFormsErrorServlet extends HttpServlet {
    private static final Log LOGGER = LogFactory.getLog(XFormsErrorServlet.class);
    private static final String HTMLHEAD ="" +
            "<html>" +
            "<head>" +
            "<title>Error Page</title>" +
            "<link rel=\"stylesheet\" type=\"text/css\" href=\"../../bfResources/styles/error.css\"/>" +
            "</head>" +
            "<body>" +
            "<div class=\"errorContent\">" +
            "<img src=\"../../bfResources/images/error.png\" width=\"24\" height=\"24\" alt=\"Error\" style=\"float:left;padding-right:5px;\"/>" +
            "<div class=\"message1\">Oops, an error occured...<br/></div>";
    private static final String HTMLFOOT ="" +
            "</div>" +
            "</body>" +
            "</html>";

    private String getHTML(HttpServletRequest request) {
        StringBuffer html = new StringBuffer();
        String msg = (String) request.getSession().getAttribute("betterform.exception.message");
        String xpath ="unknown";
        String cause= " ";

        if (msg != null) {
            int start = msg.indexOf("::");
            if(start > 3){
                xpath = msg.substring(start+2);
                msg=msg.substring(0,start);
            }
        }

        Exception ex = (Exception) request.getSession().getAttribute("betterform.exception");
        if(ex != null && ex.getCause() != null && ex.getCause().getMessage() != null){
            cause = ex.getCause().getMessage();
        }
        request.getSession().removeAttribute("betterform.exception");
        request.getSession().removeAttribute("betterform.exception.message");

        html.append("<div class=\"message2\" id=\"msg\">");
        html.append(msg);
        html.append("</div>");

        html.append("<div class=\"message3\"><strong>URL:</strong><br/>");
        html.append(request.getSession().getAttribute("betterform.referer"));
        html.append("</div>");

        html.append("<div class=\"message3\"><strong>Element causing Exception:</strong><br/>");
        html.append(xpath);
        html.append("</div>");

        html.append("<div class=\"message3\"><strong>Caused by:</strong><br/>");
        html.append(cause);
        html.append("</div>");

        html.append("<form><input type=\"button\" value=\"Back\" onClick=\"history.back()\"/></form>");


        try {
            String mail = Config.getInstance().getProperty("admin.mail");
            StringBuffer mailbody = new StringBuffer();

            html.append("<div class=\"message3\">");
            html.append("<a href=\"mailto:");
            html.append(mail);

            mailbody.append("?subject='XForms Problem at ");
            mailbody.append(request.getSession().getAttribute("betterform.referer"));
            mailbody.append("'");
            mailbody.append("&Body='Message:\n");
            mailbody.append(msg);
            mailbody.append("\n\nElement causing Exception:\n");
            mailbody.append(xpath);
            mailbody.append("\n\nCaused by:\n");
            mailbody.append(cause);
            mailbody.append("'");

            html.append(URLEncoder.encode(mailbody.toString(), "UTF-8"));
            html.append("\">");
            html.append("Report this problem...</a>");
            html.append("</div>");
        } catch (Exception e) {
            LOGGER.debug(e);
        }

        LOGGER.error(html.toString());
        return html.toString();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        new BufferedWriter(new OutputStreamWriter(response.getOutputStream())).write(getHTML(request));
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }

    @Override
    protected void doGet(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {
        response.reset();
        response.resetBuffer();

        response.setContentType("text/html");
        PrintWriter writer = response.getWriter();
        writer.println(HTMLHEAD);
        writer.println(getHTML(request));
        writer.println(HTMLFOOT);
        writer.flush();
        response.getOutputStream().close();

    }
}
