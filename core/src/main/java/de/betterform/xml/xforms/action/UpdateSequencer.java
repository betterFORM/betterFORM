/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.xforms.model.Model;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;

/**
 * The update sequencer provides a means to defer any further update while an
 * update is running. This is needed to perform nested updates in a sequential
 * manner, e.g. during <code>xforms-refresh</code> when any actions like
 * <code>xf:setvalue</code> happen which in turn cause another
 * <code>xforms-refresh</code>.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: UpdateSequencer.java 2873 2007-09-28 09:08:48Z lars $
 */
public class UpdateSequencer {
    private static Log LOGGER = LogFactory.getLog(UpdateSequencer.class);

    private Model model;
    private boolean updateRunning;
    private LinkedList updateSequence;

    /**
     * Creates an update sequencer for the specified model.
     *
     * @param model the model to be updated.
     */
    public UpdateSequencer(Model model) {
        this.model = model;
        this.updateRunning = false;
        this.updateSequence = new LinkedList();
    }

    /**
     * Checks wether the specified update method needs to be sequenced.
     * <p/>
     * If no update is running yet, this method returns <code>true</code>. The
     * handler now assumes that an update is running.
     * <p/>
     * If an update is running already, this method returns <code>false</code>.
     * The handler supposes the model not to perform the update. The update
     * method name is added to the sequence of updates to be deferred further.
     *
     * @param method the update method.
     * @return <code>true</code> if the method has to be performed by the model
     *         immediately, otherwise <code>false</code>.
     */
    public boolean sequence(String method) {
        if (this.updateRunning) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("sequencing " + method);
            }

            this.updateSequence.addLast(method);
            return true;
        }

        this.updateRunning = true;
        return false;
    }

    /**
     * Performs the next deferred update if any.
     * <p/>
     * The update method name is removed from the sequence of updates to be
     * deferred further.
     *
     * @return <code>true</code> if an deferred update has been performed,
     *         otherwise <code>false</code>.
     * @throws Exception if any error occurred during the update.
     */
    public boolean perform() throws Exception {
        this.updateRunning = false;

        if (this.updateSequence.size() > 0) {
            String name = this.updateSequence.removeFirst().toString();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("performing sequenced " + name + " level: " + this.updateSequence.size());
                for(int i = 0; i<this.updateSequence.size(); i++){
                    LOGGER.debug("sequence " + i + " level: " + this.updateSequence.get(i));
                }
            }

            try {
                this.model.getClass().getDeclaredMethod(name, (Class[])null).invoke(this.model, (Object[])null);
            }
            catch (InvocationTargetException e) {
                throw new Exception(e.getTargetException());
            }

            return true;
        }

        return false;
    }

    /**
     * Resets the update sequencer for reuse.
     */
    public void reset() {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("reset");
        }

        this.updateRunning = false;
        this.updateSequence.clear();
    }

}
