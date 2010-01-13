/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.upload;


import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;

/**
 * Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
 *
 * @author Original : plosson on 05-janv.-2006 10:46:33 - Last modified  by $Author: joernt $ on $Date: 2007-03-28 12:59:18 +0200 (Wed, 28 Mar 2007) $
 * @version $id: $
 */
public class MonitoredDiskFileItemFactory extends DiskFileItemFactory {
    private OutputStreamListener listener = null;

    public MonitoredDiskFileItemFactory(OutputStreamListener listener) {
        super();
        this.listener = listener;
    }

    public MonitoredDiskFileItemFactory(int sizeThreshold, File repository, OutputStreamListener listener) {
        super(sizeThreshold, repository);
        this.listener = listener;
    }

    public FileItem createItem(String fieldName, String contentType, boolean isFormField, String fileName) {
        return new MonitoredDiskFileItem(fieldName, contentType, isFormField, fileName, getSizeThreshold(), getRepository(), listener);
    }
}
