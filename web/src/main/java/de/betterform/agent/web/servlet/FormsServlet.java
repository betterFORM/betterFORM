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
import java.lang.reflect.Array;
import java.text.DateFormat;
import java.util.*;

/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: FormsServlet 23.11.10 tobi $
 */
public class FormsServlet extends HttpServlet {
    private List ignores;

    @Override
    public void init() throws ServletException {
        super.init();
        String ignoreString= getInitParameter("ignores");
        ignores = Arrays.asList(ignoreString.split(" "));
    }

    /**
     *  Example URLs:
     *  complete HTML-Listing with header and Footer:
     *  fragment-Listing: /FormBrowser?path=/forms/demo&fragment=true
     *<br><br>
     * fragment with directory links wrapped in an javascript function
     * /FormBrowser?path=/forms/demo&fragment=true&ajax=load
     * will create a link like <a href="#" onclick="load('theUrl');">
    */

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        boolean fragment = false;
        String ajaxFunction = request.getParameter("ajax");

        String fragmentParameter = request.getParameter("fragment");
        String uri = request.getParameter("path");

        if (fragmentParameter != null && fragmentParameter.equals("true")) {
            fragment = true;
        }

        if (!fragment) {
            response.getOutputStream().write(getHTMLHead(request).getBytes());
        }
        response.getOutputStream().write(getHTMLFilesListing(request, uri,ajaxFunction).getBytes());

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
                        "        <link rel=\"SHORTCUT ICON\" href=\"" + request.getContextPath() + "/images/betterform.ico\"/>\n" +
                        "        <link rel=\"ICON\" href=\"" + request.getContextPath() + "/images/betterform.ico\" type=\"image/x-icon\"/>\n" +
                        "        <link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/styles/bf.css\"/>\n" +
                        "        <link rel=\"stylesheet\" type=\"text/css\" href=\"" + request.getContextPath() + "/resources/styles/betterform-styles.css\"/>\n" +
                        "</head>\n" +
                        "<body id=\"formsbrowser\">\n" +
                        "<div class=\"page\">\n" +
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


    private String getHTMLFilesListing(HttpServletRequest request, String uri,String ajaxFunction) throws IOException {
        StringBuffer html = new StringBuffer();
        //Todo: Allow something like forms/demo/.. ??
        if (uri == null || uri.contains("..") || !uri.contains("forms")) {
            uri = "forms";
        }

        addTableHead(html, uri);
        handleFileListing(html, request, uri,ajaxFunction);
        html.append(
                "    </div>");

        return html.toString();
    }

    private void addTableHead(StringBuffer html, String uri) {
        html.append(
                "<div id=\"bfFormBrowser\">\n" +
                        "        <div class=\"formBrowserHead\">\n" +
                        "            <div class=\"formBrowserHeader\">\n" +
                        "               <span id=\"path\">/" + uri + "\n" +
                        "            </div>\n" +
                        "        </div>\n");
    }

    private void handleFileListing(StringBuffer html, HttpServletRequest request, String uri,String ajaxFunction) throws IOException {
        String readDir = null;
        String root = null;
        String rootDir = null;

        root = getServletConfig().getServletContext().getRealPath("");
        if (root == null) {
            root = getServletConfig().getServletContext().getRealPath(".");
        }
        rootDir = root + "/";
        readDir = rootDir + uri;

        File filesroot = new File(readDir);
        if (filesroot.exists()) {
            List<File> files = SortingWalker.sortDirsAndFiles(filesroot);

            File f = null;
            String up = null;
            if (files != null) {
                if (uri.indexOf("/") != -1) {
                    up = uri.substring(0, uri.lastIndexOf("/"));
                    handleUp(html, request, up,ajaxFunction);
                }
                for (File aFile : files) {
                    if (!ignores.contains(aFile.getName())) {
                        f = new File(readDir + "/" + aFile.getName());

                        if (f.isDirectory()) {
                            handleDirectory(html, request, uri, aFile, ajaxFunction);
                        } else if (f.isFile()) {
                            handleFile(html, request, uri, aFile, f);
                        }
                    }
                }
            }
        }
    }

    private void handleUp(StringBuffer html, HttpServletRequest request, String up,String ajaxFunction) {
        if (ajaxFunction != null) {
            String wrapperStart = ajaxFunction + "('";
            String wrapperEnd = "');";
            html.append(
                    "        <div class=\"directory\">\n" +
                            "                <a href=\"#\" onclick=\"" + wrapperStart + getRequestURI(request, up) + "&amp;fragment=true&amp;ajax=" + ajaxFunction + wrapperEnd + "\"><img src=\"" + request.getContextPath() + "/resources/images/bf_logo_square_effect_gray_dark.gif\" border=\"0\"></a>\n" +
                            "                <a class=\"textLink\" href=\"#\" onclick=\"" + wrapperStart + getRequestURI(request, up)  + "&amp;fragment=true&amp;ajax=" + ajaxFunction + wrapperEnd + "\">..</a>\n" +
                            "        </div>");
        } else {
            html.append(
                    "        <div class=\"directory\" >\n" +
                            "                <a href=\"" + getRequestURI(request, up) + "\"><img src=\"" + request.getContextPath() + "/resources/images/bf_logo_square_effect_gray_dark.gif\" border=\"0\"></a>\n" +
                            "                <a class=\"textLink\" href=\"" + getRequestURI(request, up) + "\">..</a>\n" +
                            "            </div>\n");
        }
    }

    private void handleDirectory(StringBuffer html, HttpServletRequest request, String uri, File aFile,String ajaxFunction) {
        if (ajaxFunction != null) {
            String wrapperStart = ajaxFunction + "('";
            String wrapperEnd = "');";
            html.append(
                    "        <div class=\"directory\">\n" +
                            "                <a class=\"bfIconDirectory\" href=\"#\" onclick=\"" + wrapperStart + getRequestURI(request, uri) + "/" + aFile.getName()  + "&amp;fragment=true&amp;ajax=" + ajaxFunction + wrapperEnd + "\">\n" +
                            "                   <img src=\"" + request.getContextPath() + "/resources/images/bf_logo_square_effect_gray_dark.png\" border=\"0\">" +
                            "                </a>\n" +
                            "                <a class=\"textLink\" title=\""+ aFile.getName()+"\" href=\"#\" onclick=\"" + wrapperStart + getRequestURI(request, uri) + "/" + aFile.getName()  + "&amp;fragment=true&amp;ajax=" + ajaxFunction + wrapperEnd + "\">" + getFileName(aFile)+ "</a>\n" +
                            "        </div>");
        } else {
            html.append(
                    "        <div class=\"directory\">\n" +
                            "                <a class=\"bfIconDirectory\" href=\"" + getRequestURI(request, uri) + "/" + aFile.getName() + "\">" +
                            "                   <img src=\"" + request.getContextPath() + "/resources/images/bf_logo_square_effect_gray_dark.png\" border=\"0\">" +
                        "                   </a>\n" +
                            "                <a class=\"textLink\" href=\"" + getRequestURI(request, uri) + "/" + aFile.getName() + "\">" + getFileName(aFile) + "</a>\n" +
                            "        </div>");
        }

    }

    private void handleFile(StringBuffer html, HttpServletRequest request, String uri, File aFile, File f) {
        html.append(
                "        <div class=\"file\">\n" +
                        "                <a class=\"bfIconFile\" href=\"" + request.getContextPath() + "/" + uri + "/" + aFile.getName() + "\" target=\"_blank\">" +
                        "                   <img src=\"" + request.getContextPath() + "/resources/images/bf_logo_square_no_effect_gray.png\" border=\"0\">\n" +
                        "                </a>\n" +
                        "                <a class=\"textLink\" title=\""+ aFile.getName()+"\" href=\"" + request.getContextPath() + "/" + uri + "/" + aFile.getName() + "\" target=\"_blank\">" + getFileName(aFile) + "</a>\n" +
/*
                        "            <div>\n" +
                        "                <a href=\"" + request.getContextPath() + "/" + uri + "/" + aFile.getName() + "?source=true\" target=\"_blank\">source</a>\n" +
                        "            </div>\n" +
                        "            <div>" + DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM).format(new Date(f.lastModified())) + "</div>\n" +
*/
                        "        </div>");
    }

    private String getRequestURI(HttpServletRequest request, String path) {
        return request.getRequestURI() + "?path=" + path;
    }

    private String getFileName(File aFile) {

        String fileName = aFile.getName();
        if(fileName.indexOf(".xhtml") != -1 ){
            fileName = fileName.replace(".xhtml","");
        }
        if(fileName.length() > 15){
            fileName = fileName.substring(0,10) + "..." + fileName.substring(fileName.length()-5);
        }
        return fileName;
    }
}
