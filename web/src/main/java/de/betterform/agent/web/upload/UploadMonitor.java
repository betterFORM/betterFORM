/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.upload;


import uk.ltd.getahead.dwr.WebContextFactory;

import javax.servlet.http.HttpServletRequest;

/**
 * Class by Pierre-Alexandre Losson -- http://www.telio.be/blog
 *
 * @author Original : plosson on 05-janv.-2006 10:46:33 - Last modified  by $Author: joernt $ on $Date: 2007-03-28 12:59:18 +0200 (Wed, 28 Mar 2007) $
 * @version $id: $
 */
public class UploadMonitor {
    public UploadInfo getUploadInfo() {
        HttpServletRequest req = WebContextFactory.get().getHttpServletRequest();

        if (req.getSession().getAttribute("uploadInfo") != null)
            return (UploadInfo) req.getSession().getAttribute("uploadInfo");
        else
            return new UploadInfo();
    }
}
