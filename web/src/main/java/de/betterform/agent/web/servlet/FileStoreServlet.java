/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebFactory;
import de.betterform.agent.web.utils.SortingWalker;
import de.betterform.xml.config.XFormsConfigException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 04.11.11
 * Time: 14:22
 */
public class FileStoreServlet extends FormsServlet {
    protected boolean showFiles = true;
    protected boolean recursive = false;
    protected boolean filterReadOnly = false;

    protected int total = 0;
    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String path = request.getParameter("path");
        String query = request.getParameter("query");

        if (path == null) {
            path = "forms";
        }

        if (query != null) {
            String[] queryOptions = query.substring(query.indexOf('{')+1, query.indexOf('}')).split(",");

            for (int i = 0; i < queryOptions.length; i++) {
                if (queryOptions[i].contains("deep")) {
                    recursive = Boolean.parseBoolean(queryOptions[i].substring(queryOptions[i].indexOf(':')+1));
                } else if (queryOptions[i].contains("files")) {
                    showFiles = Boolean.parseBoolean(queryOptions[i].substring(queryOptions[i].indexOf(':')+1));
                } else if (queryOptions[i].contains("filterReadOnly")) {
                    filterReadOnly = Boolean.parseBoolean(queryOptions[i].substring(queryOptions[i].indexOf(':')+1));
                }
            }
        }

        String listing = handleFileListing(path);
        String tmp = "{\n\"total\":"+ total + ",\n\"items\":[" + listing + "\n]\n}";
        response.getOutputStream().write(tmp.getBytes());

        response.flushBuffer();
        total = 0;
    }

    private String handleFileListing(String path) throws IOException, XFormsConfigException {
        String readDir = null;
        String root = null;
        String rootDir = null;
        StringBuffer listing = new StringBuffer();

        root = WebFactory.getRealPath(".", getServletConfig().getServletContext());
        if(root != null && root.endsWith("/")){
            rootDir = root + "/";
        }else {
            rootDir = "/";
        }
        readDir = rootDir + path;

        File filesroot = new File(readDir);
        if (filesroot.exists()) {
            List<File> files = SortingWalker.sortDirsAndFiles(filesroot);
            File f = null;

            if (files != null) {
                //process dirs/collections first
                for (File aFile : files) {
                    if (!ignores.contains(aFile.getName())) {
                        f = new File(readDir + "/" + aFile.getName());
                        // file is directory and does not start with '.'
                        if (f.isDirectory() && !aFile.getName().startsWith(".")) {
                            listing.append(listDirectory(aFile));
                        }
                    }
                }
                if(showFiles) {
                    for (File aFile : files) {
                        if (!ignores.contains(aFile.getName())) {
                            f = new File(readDir + "/" + aFile.getName());
                            // file is not a directory and does not start with '.'
                            if (f.isFile() && !aFile.getName().startsWith(".")) {
                                listing.append(listFile(aFile));
                            }
                        }
                    }
                }

            }
        }

        return listing.toString();
    }


    protected String listDirectory(File directory) throws IOException {

        if (directory.canWrite() || !filterReadOnly) {
            total++;
            if (recursive) {
                return "{\"name\": \"" + directory.getName()+ "\", \"parentDir\": \""+ directory.getParent() + "\", \"directory\": true, \"path\": \"" +directory.getPath() + "\", \"children\": [" + listChildren(directory) + "]},";
            } else {
                return "{\"name\": \"" + directory.getName()+ "\", \"parentDir\": \""+ directory.getParent() + "\", \"directory\": true, \"path\": \"" +directory.getPath() + "\"},";
            }
        }
        return "";
    }

    protected String listFile(File file) {
        if (file.canWrite() || !filterReadOnly) {
            total++;
            return "{\"name\": \"" + file.getName()+ "\", \"parentDir\": \""+ file.getParent() + "\", \"directory\": false, \"path\": \"" + file.getPath() + "\"},";
        }
        return "";
    }

    protected String listChildren(File directory) throws IOException {
        List<File> files = SortingWalker.sortDirsAndFiles(directory);
        StringBuffer listing = new StringBuffer();

        if (files != null) {
                //process dirs/collections first
                for (File file : files) {
                    if (!ignores.contains(file.getName())) {
                        // file is directory and does not start with '.'
                        if (file.isDirectory() && !file.getName().startsWith(".")) {
                            listing.append(listDirectory(file));
                        }
                    }
                }
//                if (showFiles) {
                    for (File file : files) {
                        if (!ignores.contains(file.getName())) {
                            // file is directory and does not start with '.'
                            if (file.isFile() && !file.getName().startsWith(".")) {
                                listing.append(listFile(file));
                            }
                        }
                    }
//                }
        }

        return listing.toString();
    }
}
