/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.upload;


/**
 * Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
 *
 * @author Original : plosson on 05-janv.-2006 10:46:33 - Last modified  by $Author: joernt $ on $Date: 2007-03-28 12:59:18 +0200 (Wed, 28 Mar 2007) $
 * @version $id: $
 */
public class UploadInfo {
    private long totalSize = 0;
    private long bytesRead = 0;
    private long elapsedTime = 0;
    private String status = "done";
    private int fileIndex = 0;

    public UploadInfo() {
    }

    public UploadInfo(int fileIndex, long totalSize, long bytesRead, long elapsedTime, String status) {
        this.fileIndex = fileIndex;
        this.totalSize = totalSize;
        this.bytesRead = bytesRead;
        this.elapsedTime = elapsedTime;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }

    public long getElapsedTime() {
        return elapsedTime;
    }

    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public boolean isInProgress() {
        return "progress".equals(status) || "start".equals(status);
    }

    public int getFileIndex() {
        return fileIndex;
    }

    public void setFileIndex(int fileIndex) {
        this.fileIndex = fileIndex;
    }
}
