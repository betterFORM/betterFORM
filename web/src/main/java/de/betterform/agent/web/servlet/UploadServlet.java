/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebProcessor;
import de.betterform.agent.web.WebUtil;
import de.betterform.generator.UIGenerator;
import de.betterform.xml.xforms.XFormsProcessor;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class UploadServlet extends HttpServlet /* extends AbstractXFormsServlet */ {
    private static final Log LOGGER = LogFactory.getLog(UploadServlet.class);

    /**
     * Returns a short description of the servlet.
     *
     * @return - Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "responsible for showing the views to the user in betterForm XForms applications";
    }

    /**
     * Destroys the servlet.
     */
    public void destroy() {
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List /* FileItem */ items = upload.parseRequest(request);

            Iterator iter = items.iterator();
            while (iter.hasNext()) {
               FileItem item = (FileItem) iter.next();
               if (item.isFormField()) {
                    String fieldName = item.getFieldName();
                    String fileName = item.getName();
                    String contentType = item.getContentType();
                    boolean isInMemory = item.isInMemory();
                    long sizeInBytes = item.getSize();

                } else {
                    //stuff for non file input (text box etc)
                   File localFile = new File("/Users/dev/projects/betterform-git/web/target/upload", item.getName());
                   localFile.getParentFile().mkdirs();
                   item.write(localFile);

                   if(LOGGER.isDebugEnabled()){
                       LOGGER.debug("saving data to path: " + localFile);
                   }

                }
            }
        } catch (FileUploadException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
