/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet;

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
import java.io.*;
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
            FileItem uploadItem = null;
            String relativeUploadPath = "";
            while (iter.hasNext()) {
               FileItem item = (FileItem) iter.next();
               String fieldName = item.getFieldName();
                if (item.isFormField() && "bfUploadPath".equals(fieldName)) {
                    InputStream is = item.getInputStream();
                    if (is != null) {
                        Writer writer = new StringWriter();
                        try {
                            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                            int n;
                            char[] buffer = new char[1024];
                            while ((n = reader.read(buffer)) != -1) {
                                writer.write(buffer, 0, n);
                            }
                        } finally {
                            is.close();
                        }
                        relativeUploadPath = writer.toString();
                    }
                } else if(item.getName() != null) {
                    // FileItem of the uploaded file
                    uploadItem = item;
                }
            }

            if(uploadItem != null && !"".equals(relativeUploadPath)) {
                String realPath = request.getSession().getServletContext().getRealPath("");
                  if (realPath == null) {
                      realPath = request.getSession().getServletContext().getRealPath(".");
                  }
                File uploadDirectory = new File(realPath, relativeUploadPath);
                String fileName = uploadItem.getName();
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("uploading file '" + fileName + "' to directory '" + uploadDirectory +"'");
                }

                File localFile = new File(uploadDirectory.getAbsolutePath(), fileName);

                if (!localFile.getParentFile().exists()) {
                    localFile.getParentFile().mkdirs();
                }
                uploadItem.write(localFile);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("file '" + fileName + "' successfully uploaded to directory '" + uploadDirectory +"'");
                }
            } else {
                LOGGER.warn("error uploading file to '" + relativeUploadPath + "'");
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
