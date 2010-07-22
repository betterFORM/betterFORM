/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.upload;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.agent.web.WebProcessor;

import javax.servlet.http.HttpServletRequest;

/**
 * Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
 *
 * @author Original : plosson on 05-janv.-2006 10:46:33 - Last modified  by $Author: lars $ on $Date: 2007-09-28 11:43:30 +0200 (Fri, 28 Sep 2007) $
 * @version $id: $
 */
public class UploadListener implements OutputStreamListener {
    private static final Log LOGGER = LogFactory.getLog(UploadListener.class);

    private HttpServletRequest request;
    private long delay = 0;
    private long startTime = 0;
    private int totalToRead = 0;
    private int totalBytesRead = 0;
    private int totalFiles = -1;
    private String sessionKey;

    public UploadListener(HttpServletRequest request, String sessionKey) {
        this.request = request;
        this.delay = 0; //not used
        totalToRead = request.getContentLength();
        this.startTime = System.currentTimeMillis();
        this.sessionKey = sessionKey;
    }

    public void start() {
        totalFiles++;
        updateUploadInfo("start");
    }

    public void bytesRead(int bytesRead) {
        totalBytesRead = totalBytesRead + bytesRead;
        updateUploadInfo("progress");

        try {
            Thread.sleep(delay);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void error(String message) {
        updateUploadInfo("error");
    }

    public void done() {
        // Delay the upload status change to done until the value is set in the XForms instance and a RRR has been done
        // updateUploadInfo("done");
    }

    private long getDelta() {
        return (System.currentTimeMillis() - startTime) / 1000;
    }

    private void updateUploadInfo(String status) {
        long delta = (System.currentTimeMillis() - startTime) / 1000;
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("status: " + status);
            LOGGER.debug("Bytes read: " + totalBytesRead);
        }
        request.getSession().setAttribute(WebProcessor.ADAPTER_PREFIX + sessionKey + "-uploadInfo", new UploadInfo(totalFiles, totalToRead, totalBytesRead, delta, status));
    }

}
