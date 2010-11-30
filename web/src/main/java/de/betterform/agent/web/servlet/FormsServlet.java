/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.agent.web.servlet;

import de.betterform.agent.web.utils.SortingWalker;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: FormsServlet 23.11.10 tobi $
 */
public class FormsServlet extends HttpServlet {

    /*
     * Example URLs:
       *  complete HTML-Listing with header and Footer: /forms/formslist
       *  fragment-Listing: /forms/formslist?fragment=true&path=/forms/demo/
       *  fragment-Listing: /forms/formslist#path=/forms/demo?fragment=true
       *  fragment-Listing: /FormBrowser?path=/forms/demo&fragment=true
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean fragment = false;

        String fragmentParameter = request.getParameter("fragment");
        String uri = request.getParameter("path");

        if(fragmentParameter != null && fragmentParameter.equals("true")){
            fragment = true;
        }

        if (!fragment) {
            response.getOutputStream().write(getHTMLHead(request).getBytes());
        }
        response.getOutputStream().write(getHTMLFilesListing(request, uri).getBytes());

        if (!fragment) {
            response.getOutputStream().write(getHTMLFooter(request).getBytes());
        }

        response.flushBuffer();
    }

    private static final String getHTMLHead(HttpServletRequest request) {
        return
            "<html xmlns=\"http://www.w3.org/1999/xhtml\" xmlns:xf=\"http://www.w3.org/2002/xforms\" xmlns:ev=\"http://www.w3.org/2001/xml-events\" xml:lang=\"en\" lang=\"en\">\n" +
            "<head>\n" +
            "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
            "<meta name=\"Author\" content=\"Joern Turner\">\n" +
            "<meta name=\"Author\" content=\"Lars Windauer\">\n" +
            "<meta name=\"publisher\" content=\"betterFORM Project\">\n" +
            "<meta name=\"description\" content=\"betterFORM lighSteelBlue Demo, Reference and XForms 1.1 conformance testsuite browser. Explore the world of XForms\"/>\n" +
            "<meta name=\"keywords\" content=\"betterFORM, xforms, forms, form, xhtml, xpath, xslt, xslt2.0, dojo, dojotoolkit, java, javascript, xml, schema, web, web2.0, web2, web3.0, web3, semantic, semantic web, joern turner, turner, lars windauer, ajax, xforms processor, processor\">\n" +
            "<meta name=\"copyright\" content=\"betterForm Project\">\n" +
            "<meta name=\"content-language\" content=\"en\">\n" +
            "<meta name=\"robots\" content=\"all\">\n" +
            "<meta http-equiv=\"expires\" content=\"Wed, 9 Feb 2011 12:21:57 GMT\">\n" +
            "<meta name=\"revisit-after\" content=\"5 days\">\n" +
            "        <title>betterFORM lightSteelBlue Forms Browser</title>\n" +
            "        <link REV=\"made\" HREF=\"mailto:info@betterform.de\"/>\n" +
            "        <link rel=\"SHORTCUT ICON\" href=\""+ request.getContextPath() + "/images/betterform.ico\"/>\n" +
            "        <link rel=\"ICON\" href=\""+ request.getContextPath() + "/images/betterform.ico\" type=\"image/x-icon\"/>\n" +
            "        <link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/styles/bf.css\"/>\n" +
            "        <link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/resources/styles/betterform-styles.css\"/>\n" +
            "</head>\n" +
            "<body id=\"formsbrowser\">\n" +
            "<div class=\"page\">\n"+
            "    <div id=\"header\">\n" +
            "        <div class=\"pageMarginBox\">\n" +
            "            <div class=\"languages\">\n" +
            "                <img src=\"" + request.getContextPath() + "/images/en.png\" class=\"langSelector\" alt=\"english\"/>\n" +
            "                <img src=\"" + request.getContextPath() + "/images/de.png\" class=\"langSelector\" alt=\"deutsch\"/>\n" +
            "            </div>\n" +
            "            <div id=\"logoBar\">\n" +
            "                <a href=\"" + request.getContextPath() + "/index.html\" id=\"linkLogo\" class=\"link\"><img id=\"logo\" src=\"" + request.getContextPath() + "/images/logo.png\" alt=\"betterFORM project\"/></a>\n" +
            "                <div id=\"topnav\">\n" +
            "                    <a href=\"" + request.getContextPath() + "/index.html\">home</a><span class=\"menuDevider\"> | </span>\n" +
            "                    <a href=\"" + request.getContextPath() + "/demo.xhtml\">demo</a><span class=\"menuDevider\"> | </span>\n" +
            "                    <a href=\"" + request.getContextPath() + "/download.html\">download</a><span class=\"menuDevider\"> | </span>\n" +
            "                    <a href=\"" + request.getContextPath() + "/product.html\">product</a><span class=\"menuDevider\"> | </span>\n" +
            "                    <a href=\"" + request.getContextPath() + "/support.html\">support</a><span class=\"menuDevider\"> | </span>\n" +
            "                    <a href=\"" + request.getContextPath() + "/whoweare.html\">who we are</a>\n" +
            "                </div>\n" +
            "            </div>\n" +
            "        </div>\n" +
            "    </div>\n" +
            "\n" +
            "    <div id=\"content\" class=\"contact\">\n" +
            "        <img id=\"shadowTop\" src=\"" + request.getContextPath() + "/images/shad_top.jpg\" alt=\"\"/>\n" +
            "        <div class=\"pageMarginBox\">\n" +
            "            <div class=\"contentBody\">\n " +
            "                 <h1>Forms Browser</h1>\n";
    }


    private static final String getHTMLFooter(HttpServletRequest request) {
            return
                "            </div>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div id=\"footer\">\n" +
                "        <img id=\"shadowBottom\" src=\"" + request.getContextPath() + "/images/shad_bottom.jpg\" alt=\"\"/>\n" +
                "        <div class=\"pageMarginBox\">\n" +
                "            <span id=\"bottomMenu\">\n" +
                "                &#169; 2010 betterFORM" +
                "                <span class=\"menuDevider\"> | </span>\n" +
                "                <a href=\"" + request.getContextPath() + "/index.html\">home</a><span class=\"menuDevider\"> | </span>\n" +
                "                <a href=\"" + request.getContextPath() + "/demo.xhtml\">demo</a><span class=\"menuDevider\"> | </span>\n" +
                "                <a href=\"" + request.getContextPath() + "/download.html\">download</a><span class=\"menuDevider\"> | </span>\n" +
                "                <a href=\"" + request.getContextPath() + "/product.html\">product</a><span class=\"menuDevider\"> | </span>\n" +
                "                <a href=\"" + request.getContextPath() + "/support.html\">support</a><span class=\"menuDevider\"> | </span>\n" +
                "                <a href=\"" + request.getContextPath() + "/whoweare.html\">who we are</a><span class=\"menuDevider\"> | </span>\n" +
                "                <a href=\"" + request.getContextPath() + "/contact.html\">contact / impressum</a>\n" +
                "            </span>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>\n" +
                "<script type=\"text/javascript\">\n" +
                "    var gaJsHost = ((\"https:\" == document.location.protocol) ? \"https://ssl.\" : \"http://www.\");\n" +
                "    document.write(unescape(\"%3Cscript src='\" + gaJsHost + \"google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E\"));\n" +
                "</script>\n" +
                "<script type=\"text/javascript\">\n" +
                "    try {\n" +
                "        var pageTracker = _gat._getTracker(\"UA-15044944-1\");\n" +
                "        pageTracker._trackPageview();\n" +
                "    } catch(err) {}\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
    }




    private String getHTMLFilesListing(HttpServletRequest request, String uri) throws IOException {
        StringBuffer html = new StringBuffer();
        //Todo: Allow something like forms/demo/.. ??
        if ( uri == null || uri.contains("..") || ! uri.contains("forms") ) {
            uri = "forms";
        }

        addTableHead(html, uri);
        handleFileListing(html, request, uri);
        html.append(
        "    </table>\n" +
        "</div>");

        return html.toString();
    }

    private void addTableHead(StringBuffer html, String uri) {
        html.append(
        "<div id=\"bfFormBrowser\">\n" +
        "    <table>\n" +
        "        <tr>\n" +
        "            <td class=\"formBrowserHeader\" colspan=\"3\">\n" +
        "               <span id=\"path\">/"+uri+"\n" +
        "            </td>\n" +
        "        </tr>\n");
    }

    private void handleFileListing(StringBuffer html, HttpServletRequest request, String uri) throws IOException {
        String readDir=null;
        String root = null;
        String rootDir = null;

        root = getServletConfig().getServletContext().getRealPath("");
        if (root == null) {
            root = getServletConfig().getServletContext().getRealPath(".");
        }
        rootDir = root + "/";
        readDir = rootDir + uri;

        File filesroot=new File(readDir);
        if (filesroot.exists()) {
            List<File> files = SortingWalker.sortDirsAndFiles(filesroot);

            File f = null;
            String up = null;
            if (files != null) {
                if (uri.indexOf("/") != -1) {
                    up = uri.substring(0, uri.lastIndexOf("/"));
                    handleUp(html, request, up);
                }
                for (int i = 0; i < files.size(); i++) {
                    File aFile = files.get(i);
                    f = new File(readDir + "/" + aFile.getName());

                    if (f.isDirectory()) {
                        handleDirectory(html, request, uri, aFile);
                    } else if (f.isFile()) {
                        handleFile(html, request, uri, aFile, f);
                    }
                }
            }
        }
    }
    private void handleUp(StringBuffer html, HttpServletRequest request, String up) {
        html.append(
        "        <tr>\n" +
        "            <td class=\"directory\" colspan=\"3\">\n" +
        "                <a href=\""+ getRequestURI(request, up) +"\"><img src=\"" + request.getContextPath() +"/resources/images/folder.gif\" border=\"0\"></a>\n" +
        "                <a class=\"textLink\" href=\"" + getRequestURI(request, up) + "\">..</a>\n" +
        "            </td>\n" +
        "        </tr>");
    }
    private void handleDirectory(StringBuffer html, HttpServletRequest request, String uri, File aFile) {
        html.append(
        "        <tr class=\"directory\">\n" +
        "            <td colspan=\"3\">\n" +
        "                <a href=\""+ getRequestURI(request, uri) + "/" + aFile.getName() + "\"><img src=\"" + request.getContextPath() + "/resources/images/folder.gif\" border=\"0\"></a>\n" +
        "                <a class=\"textLink\" href=\""+ getRequestURI(request, uri) + "/" + aFile.getName()+ "\">" + aFile.getName() + "</a>\n" +
        "            </td>\n" +
        "        </tr>");
    }

    private void handleFile(StringBuffer html, HttpServletRequest request, String uri, File aFile, File f) {
        html.append(
        "        <tr class=\"file\">\n" +
        "            <td width=\"25%\">\n" +
        "                <a href=\""+ request.getContextPath()+ "/" + uri + "/" + aFile.getName() + "\" target=\"_blank\">" + aFile.getName() + "\n" +
        "                </a>\n" +
        "            </td>\n" +
        "            <td width=\"25%\">\n" +
        "                <a href=\"" + request.getContextPath()+ "/" + uri + "/" + aFile.getName()+ "?source=true\" target=\"_blank\">source</a>\n" +
        "            </td>\n" +
        "            <td>" + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(f.lastModified())) + "</td>\n" +
        "        </tr>");
    }

    private String getRequestURI(HttpServletRequest request,String path) {
        return request.getRequestURI() + "?path=" + path;
    }
}
