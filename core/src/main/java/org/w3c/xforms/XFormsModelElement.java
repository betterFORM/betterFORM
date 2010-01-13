/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package org.w3c.xforms;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;

/**
 * Interface as defined by XForms Spec in
 * <a href="http://www.w3.org/TR/2006/REC-xforms-20060314/slice7.html#expr-instance">7.3
 * Instance Data</a>
 * <p/>
 * <q>For each model element, the XForms Processor maintains the state in an
 * internal structure called instance data that conforms to the XPath Data Model
 * [XPath 1.0]. XForms Processors that implement DOM must provide DOM access to
 * this instance data via the interface defined below.</q>
 * <p/>
 * The Interface corresponds to the following IDL:
 * <pre>
 * #include "dom.idl"
 *
 * pragma prefix "w3c.org"
 *
 * module xforms {
 *     interface XFormsModelElement : dom::Element {
 *         dom::Document getInstanceDocument(in dom::DOMString instanceID) raises(dom::DOMException);
 *         void rebuild();
 *         void recalculate();
 *         void revalidate();
 *         void refresh();
 *     };
 * };
 * </pre>
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: XFormsModelElement.java 2098 2006-03-28 16:00:36Z unl $
 */
public interface XFormsModelElement {

    /**
     * 7.3.1 The getInstanceDocument() Method.
     * <p/>
     * This method returns a DOM Document that corresponds to the instance data
     * associated with the <code>instance</code> element containing an
     * <code>ID</code> matching the <code>instance-id</code> parameter. If there
     * is no matching instance data, a <code>DOMException</code> is thrown.
     *
     * @param instanceID the instance id.
     * @return the corresponding DOM document.
     * @throws DOMException if there is no matching instance data.
     */
    Document getInstanceDocument(String instanceID) throws DOMException;

    /**
     * 7.3.2 The rebuild() Method.
     * <p/>
     * This method signals the XForms Processor to rebuild any internal data
     * structures used to track computational dependencies within this XForms
     * Model. This method takes no parameters and raises no exceptions.
     */
    void rebuild();

    /**
     * 7.3.3 The recalculate() Method.
     * <p/>
     * This method signals the XForms Processor to perform a full recalculation
     * of this XForms Model. This method takes no parameters and raises no
     * exceptions.
     */
    void recalculate();

    /**
     * 7.3.4 The revalidate() Method.
     * <p/>
     * This method signals the XForms Processor to perform a full revalidation
     * of this XForms Model. This method takes no parameters and raises no
     * exceptions.
     */
    void revalidate();

    /**
     * 7.3.5 The refresh() Method.
     * <p/>
     * This method signals the XForms Processor to perform a full refresh of
     * form controls bound to instance nodes within this XForms Model. This
     * method takes no parameters and raises no exceptions.
     */
    void refresh();
}
