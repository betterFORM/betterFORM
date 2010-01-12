/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */

package de.betterform.agent.web.upload;

import org.apache.commons.fileupload.disk.DiskFileItem;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
 *
 * @author Original : plosson on 05-janv.-2006 10:46:33 - Last modified  by $Author: joernt $ on $Date: 2007-03-28 12:59:18 +0200 (Wed, 28 Mar 2007) $
 * @version $id: $
 */
public class MonitoredDiskFileItem extends DiskFileItem {
    private MonitoredOutputStream mos = null;
    private OutputStreamListener listener;

    public MonitoredDiskFileItem(String fieldName, String contentType, boolean isFormField, String fileName, int sizeThreshold, File repository, OutputStreamListener listener) {
        super(fieldName, contentType, isFormField, fileName, sizeThreshold, repository);
        this.listener = listener;
    }

    public OutputStream getOutputStream() throws IOException {
        if (mos == null) {
            mos = new MonitoredOutputStream(super.getOutputStream(), listener);
        }
        return mos;
    }
}
