/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.flux;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.directwebremoting.WebContextFactory;
import org.w3c.dom.Element;

import javax.servlet.http.HttpSession;
import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;


// end of class

/**
 * AJAX Facade class to hide the full functionality from the web-client and to avoid overloaded methods which are
 * not recommended by DWR.
 *
 * @author Joern Turner
 */
public class FluxModel {
    private static final Log LOGGER = LogFactory.getLog(FluxModel.class);
    private HttpSession session;


    /**
     * grabs the actual web from the session.
     */
    public FluxModel() {
        session = WebContextFactory.get().getSession(true);

    }


    /**
     * 7.3.1 The getInstanceDocument() Method.
     * <p/>
     * This method returns a DOM Document that corresponds to the instance data
     * associated with the <code>instance</code> element containing an
     * <code>ID</code> matching the <code>instance-id</code> parameter. If there
     * is no matching instance data, a <code>DOMException</code> is thrown.
     *
     * @param modelId the XForms model id.
     * @param instanceId the instance id.
     * @param sessionKey the key identifying the current XForms Session.
     * @return the corresponding DOM document.
     * @throws org.w3c.dom.DOMException if there is no matching instance data.
     */
    public static org.w3c.dom.Element getInstanceDocument(String modelId, String instanceId, String sessionKey){

        try {
            return FluxUtil.getProcessor(sessionKey).getXFormsModel(modelId).getInstanceDocument(instanceId).getDocumentElement();
        } catch (XFormsException e) {
            return DOMUtil.newDocument(false,false).getDocumentElement();
        } catch (FluxException e) {
            return DOMUtil.newDocument(false,false).getDocumentElement();
        }
    }

    public String getInstanceAsString(String modelId, String instanceId, String sessionKey){
        Element element = null;
        try {
            element = FluxUtil.getProcessor(sessionKey).getXFormsModel(modelId).getInstanceDocument(instanceId).getDocumentElement();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            DOMUtil.prettyPrintDOM(element,out);
            LOGGER.debug("xml: " + StringEscapeUtils.escapeXml(out.toString()));
            return StringEscapeUtils.escapeXml(out.toString());
        } catch (XFormsException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (FluxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (TransformerException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return "";
    }


    /**
     * 7.3.2 The rebuild() Method.
     * <p/>
     * This method signals the XForms Processor to rebuild any internal data
     * structures used to track computational dependencies within this XForms
     * Model. This method takes no parameters and raises no exceptions.
     *
     * @param modelId the XForms model id.
     * @param sessionKey the key identifying the current XForms Session.
     */
    public void rebuild(String modelId,String sessionKey) throws FluxException {
        try {
            FluxUtil.getProcessor(sessionKey).getXFormsModel(modelId).rebuild();
        } catch (XFormsException e) {
            throw new FluxException(e);
        }
    }

    /**
     * 7.3.3 The recalculate() Method.
     * <p/>
     * This method signals the XForms Processor to perform a full recalculation
     * of this XForms Model. This method takes no parameters and raises no
     * exceptions.
     *
     * @param modelId the XForms model id.
     * @param sessionKey the key identifying the current XForms Session.
     */
    public void recalculate(String modelId,String sessionKey) throws FluxException {
        try {
            FluxUtil.getProcessor(sessionKey).getXFormsModel(modelId).recalculate();
        } catch (XFormsException e) {
            throw new FluxException(e);
        }
    }

    /**
     * 7.3.4 The revalidate() Method.
     * <p/>
     * This method signals the XForms Processor to perform a full revalidation
     * of this XForms Model. This method takes no parameters and raises no
     * exceptions.
     *
     * @param modelId the XForms model id.
     * @param sessionKey the key identifying the current XForms Session.
     */
    public void revalidate(String modelId,String sessionKey) throws FluxException {
        try {
            FluxUtil.getProcessor(sessionKey).getXFormsModel(modelId).revalidate();
        } catch (XFormsException e) {
            throw new FluxException(e);
        }
    }

    /**
     * 7.3.5 The refresh() Method.
     * <p/>
     * This method signals the XForms Processor to perform a full refresh of
     * form controls bound to instance nodes within this XForms Model. This
     * method takes no parameters and raises no exceptions.
     *
     * @param modelId the XForms model id.
     * @param sessionKey the key identifying the current XForms Session.
     */
    public void refresh(String modelId,String sessionKey) throws FluxException {
        try {
            FluxUtil.getProcessor(sessionKey).getXFormsModel(modelId).refresh();
        } catch (XFormsException e) {
            throw new FluxException(e);
        }
    }
}