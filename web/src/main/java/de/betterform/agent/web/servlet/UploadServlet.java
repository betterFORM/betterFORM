/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.servlet;

import de.betterform.agent.web.WebFactory;
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
        String payload ="";
        try {
            FileItemFactory factory = new DiskFileItemFactory();
            ServletFileUpload upload = new ServletFileUpload(factory);
            List /* FileItem */ items = upload.parseRequest(request);

            Iterator iter = items.iterator();
            FileItem uploadItem = null;
            String collectionPath ="";
            String collectionName ="";
            String relativeUploadPath = "";
            while (iter.hasNext()) {
               FileItem item = (FileItem) iter.next();
               String fieldName = item.getFieldName();
                if (item.isFormField() && "bfUploadPath".equals(fieldName)) {
                    relativeUploadPath = this.getFieldValue(item);
                } else if (item.isFormField() && "bfCollectionPath".equals(fieldName)) {
                    collectionPath = this.getFieldValue(item);
                } else if (item.isFormField() && "bfCollectionName".equals(fieldName)) {
                    collectionName = this.getFieldValue(item);
                } else if(item.getName() != null) {
                    // FileItem of the uploaded file
                    uploadItem = item;
                }
            }

            if(uploadItem != null && !"".equals(relativeUploadPath)) {
                this.uploadFile(request, uploadItem, relativeUploadPath);
            } else if(!"".equals(collectionName) && !"".equals(collectionPath)) {
                this.createColection(request, collectionName, collectionPath);
            } else {
                LOGGER.warn("error uploading file to '" + relativeUploadPath + "'");
            }
        } catch (FileUploadException e) {
            e.printStackTrace();
            payload = e.getMessage();
        } catch (Exception e) {
            e.printStackTrace();
            payload = e.getMessage();
        }
        response.getOutputStream().println("<html><body><textarea>"+payload+"</textarea></body></html>");
    }

    private void createColection(HttpServletRequest request, String collectionName, String collectionPath) {
        String realPath = WebFactory.getBfRealPath(".", request.getSession().getServletContext());
        File path2Collection = new File(realPath, collectionPath);
        File localFile = new File(path2Collection.getAbsolutePath(), collectionName);

        if (localFile.getParentFile().exists()) {
            boolean created = localFile.mkdir();
            if(created){
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("collection '" + collectionName + "' created in directory '" + path2Collection.getAbsolutePath() + "'");
                }
            }else {
                LOGGER.warn("collection'" + collectionName + "' could not be created in in directory '" + path2Collection.getAbsolutePath() + "'");
            }

        } else {
            LOGGER.warn("Could not create collection " + collectionName + " because parent collection " + localFile.getParentFile().getAbsolutePath() + " does not exist");
        }
    }

    private String getFieldValue(FileItem item) throws IOException {
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
            return writer.toString();
        }else {
            return null;
        }
    }

    private void uploadFile(HttpServletRequest request, FileItem uploadItem, String relativeUploadPath) throws Exception {
        String realPath = WebFactory.getBfRealPath(".", request.getSession().getServletContext());
        File uploadDirectory = new File(realPath, relativeUploadPath);
        String fileName = uploadItem.getName();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("uploading file '" + fileName + "' to directory '" + uploadDirectory + "'");
        }

        File localFile = new File(uploadDirectory.getAbsolutePath(), fileName);

        if (!localFile.getParentFile().exists()) {
            localFile.getParentFile().mkdirs();
        }
        uploadItem.write(localFile);

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("file '" + fileName + "' successfully uploaded to directory '" + uploadDirectory + "'");
        }
    }
}
