/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.servlet;

import de.betterform.agent.web.utils.SortingWalker;

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
public class FileLister extends FormsServlet {
    protected boolean showFiles = true;
    protected boolean filterReadOnly = false;
    private String contextroot;



    @Override
    public void init() throws ServletException {
        super.init();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String readDir = null;
        String root = null;
        String rootDir = null;
        String path = request.getParameter("path");
        this.contextroot = request.getContextPath();

        if (path == null || path.equals("")) {
            path = "forms";
        }
        root = getServletConfig().getServletContext().getRealPath("");
        if (root == null) {
            root = getServletConfig().getServletContext().getRealPath(".");
        }
        rootDir = root + "/";
        readDir = rootDir + path;

        File f = new File(readDir);
        String parentDir = f.getName();
        String parentPath = getRelPath(f);


        String listing = handleFileListing(readDir,path);

        String tmp = "{\n\"parentDir\":\""+ parentDir + "\",\n\"parentPath\":\""+ parentPath + "\",\n\"items\":[" + listing + "\n]\n}";


//        String tmp = "{\n\"parentDir\":"+ parentDir + ",\n" + "\"items\":[" + listing + "\n]}";
        response.setContentType("application/json");
        response.getOutputStream().write(tmp.getBytes());

        response.flushBuffer();
    }

    private String handleFileListing(String readDir,String path) throws IOException {

        StringBuffer listing = new StringBuffer();


        File filesroot = new File(readDir);
        if (filesroot.exists()) {
            List<File> files = SortingWalker.sortDirsAndFiles(filesroot);
            File f = null;

            if (files != null) {
                //process dirs/collections first
                for (int i = 0; i < files.size(); i++) {
                    File aFile = files.get(i);
                    if (!ignores.contains(aFile.getName())) {
                        f = new File(readDir + "/" + aFile.getName());
                        // file is directory and does not start with '.'
                        if (f.isDirectory() && !aFile.getName().startsWith(".")) {
                            listing.append(listDirectory(aFile));
                            if (i < files.size() - 1) {
                                listing.append(",");
                            }
                        }
                    }
                }
                for (int i = 0; i < files.size(); i++) {
                    File aFile = files.get(i);
                    if (!ignores.contains(aFile.getName())) {
                        f = new File(readDir + "/" + aFile.getName());
                        // file is not a directory and does not start with '.'
                        if (f.isFile() && !aFile.getName().startsWith(".")) {
                            listing.append(listFile(aFile));
                            if (i < files.size() - 1) {
                                listing.append(",");
                            }
                        }
                    }
                }

            }
        }

        return listing.toString();
    }


    protected String listDirectory(File directory) throws IOException {
        String s = getRelPath(directory);
        if (directory.canWrite()) {
            return "{\"name\": \"" + directory.getName() + "\", \"directory\": true, \"path\": \"" + s + "\"}";
        }
        return "";
    }

    protected String listFile(File file) {
        String s = getRelPath(file);
        if (file.canWrite() || !filterReadOnly) {
            return "{\"name\": \"" + file.getName() + "\", \"directory\": false, \"path\": \"" + s + "\"}";
        }
        return "";
    }

    private String getRelPath(File f) {
        String fullPath = f.getPath();
        int start = fullPath.indexOf(this.contextroot) + this.contextroot.length();
        return fullPath.substring(start);
    }

}
