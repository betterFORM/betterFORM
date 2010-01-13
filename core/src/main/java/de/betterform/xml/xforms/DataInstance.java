/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms;

import org.w3c.dom.Element;

/**
 * Interface for 'Data Instance Module'
 * (http://www.w3.org/MarkUp/Forms/specs/XForms1.2/modules/instance/dataIsland/Data-Island-Module.html)
 *
 * @author Joern Turner
 */
public interface DataInstance {

    static final String DATA_INSTANCE_LOAD = "data-instance-load";
    static final String DATA_INSTANCE_READY = "data-instance-ready";
    static final String DATA_LINK_ERROR = "data-link-error";

    /**
     * returns the DOM Document of the XForms Instance identified by id. Implementation of 'Data Instance Module' IDL
     *
     * @param id identifier for Instance
     * @return the DOM Document of the XForms Instance identified by id
     * @throws org.w3c.dom.DOMException in case the processor was not intialized or the wanted Instance does not exist
     */
    Element getInstanceDocument(String id);

    /**
     * This method signals the processor to access any specified external resources and initialize the instance
     * document, or to refresh instance data from inline markup. This method takes no parameters
     * and raises no exceptions. 
     */
    void load();
}