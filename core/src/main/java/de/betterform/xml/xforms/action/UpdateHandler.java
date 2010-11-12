/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;

/**
 * The update handler implements update deferral according to XForms 1.0.
 * It's intended to be used by actions when an outermost action handler (i.e.
 * an enclosing <code>xf:action</code>) is given.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: UpdateHandler.java 2873 2007-09-28 09:08:48Z lars $
 */
public class UpdateHandler {
    private static Log LOGGER = LogFactory.getLog(UpdateHandler.class);

    private Model model;
    private boolean rebuild;
    private boolean recalculate;
    private boolean revalidate;
    private boolean refresh;

    /**
     * Creates an update handlerfor the specified model.
     *
     * @param model the model to be updated.
     */
    public UpdateHandler(Model model) {
        this.model = model;
        this.rebuild = false;
        this.recalculate = false;
        this.revalidate = false;
        this.refresh = false;
    }

    /**
     * Tells the handler wether to perform a rebuild or not.
     *
     * @param rebuild specifies wether to perform a rebuild or not.
     */
    public void doRebuild(boolean rebuild) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace((rebuild ? "deferring" : "clearing") + " rebuild");
        }
        this.rebuild = rebuild;
    }

    /**
     * Tells the handler wether to perform a recalculate or not.
     *
     * @param recalculate specifies wether to perform a recalculate or not.
     */
    public void doRecalculate(boolean recalculate) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace((recalculate? "deferring" : "clearing") + " recalculate");
        }
        this.recalculate = recalculate;
    }

    /**
     * Tells the handler wether to perform a revalidate or not.
     *
     * @param revalidate specifies wether to perform a revalidate or not.
     */
    public void doRevalidate(boolean revalidate) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace((revalidate ? "deferring" : "clearing") + " revalidate");
        }
        this.revalidate = revalidate;
    }

    /**
     * Tells the handler wether to perform a refresh or not.
     *
     * @param refresh specifies wether to perform a refresh or not.
     */
    public void doRefresh(boolean refresh) {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace((refresh ? "deferring" : "clearing") + " refresh");
        }
        this.refresh = refresh;
    }

    /**
     * Performs all deferred updates.
     *
     * @throws XFormsException if an error occurred during an update.
     */
    public void doUpdate() throws XFormsException {
        try {
            if (this.rebuild) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("performing deferred rebuild");
                }
                this.model.getContainer().dispatch(this.model.getTarget(), XFormsEventNames.REBUILD, null);
            }
            if (this.recalculate) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("performing deferred recalculate");
                }
                this.model.getContainer().dispatch(this.model.getTarget(), XFormsEventNames.RECALCULATE, null);
            }
            if (this.revalidate) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("performing deferred revalidate");
                }
                this.model.getContainer().dispatch(this.model.getTarget(), XFormsEventNames.REVALIDATE, null);
            }
            if (this.refresh) {
                if (LOGGER.isTraceEnabled()) {
                    LOGGER.trace("performing deferred refresh");
                }
                this.model.getContainer().dispatch(this.model.getTarget(), XFormsEventNames.REFRESH, null);
            }
        }
        finally {
            this.rebuild = false;
            this.recalculate = false;
            this.revalidate = false;
            this.refresh = false;
        }
    }

}
