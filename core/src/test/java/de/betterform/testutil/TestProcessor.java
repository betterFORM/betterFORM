package de.betterform.testutil;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.events.EventTarget;
import org.w3c.xforms.XFormsModelElement;
import org.xml.sax.InputSource;

import java.io.InputStream;
import java.net.URI;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: joernturner
 * Date: Jan 25, 2008
 * Time: 5:30:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class TestProcessor implements XFormsProcessor, EventListener {
    private Document hostDoc;
    private EventTarget root;
    private Event result;
    private XFormsProcessor processorReference;

    public TestProcessor(XFormsProcessor processor) {
        this.processorReference = processor;
    }

    public String getBaseURI() {
        return this.processorReference.getBaseURI();
    }

    public void setXForms(Node node) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setXForms(URI uri) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setXForms(InputStream stream) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setXForms(InputSource source) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setBaseURI(String aURI) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setConfigPath(String path) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setContext(Map contextParams) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setContextParam(String key, Object object) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object getContextParam(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Object removeContextParam(String key) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setLocale(String locale) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Initialize the Adapter and thus the XForms Processor.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if an error occurred during init.
     */
    public void init() throws XFormsException {
        this.processorReference.init();
        this.hostDoc = (Document) this.processorReference.getXForms();
        this.root = (EventTarget) hostDoc.getDocumentElement();
        this.root.addEventListener(BetterFormEventNames.REPLACE_ALL, this, true);
        this.root.addEventListener(BetterFormEventNames.RENDER_MESSAGE, this, true);
    }

    public Node getXForms() throws XFormsException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public XFormsModelElement getXFormsModel(String id) throws XFormsException {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Terminates the XForms processing. Should perform resource cleanup.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if an error occurred during shutdown.
     */
    public void shutdown() throws XFormsException {
        if (this.root != null) {
            this.root.removeEventListener(BetterFormEventNames.REPLACE_ALL, this, true);
            this.root.removeEventListener(BetterFormEventNames.RENDER_MESSAGE, this, true);
        }
        this.processorReference.shutdown();
    }

    /**
     * dispatches an Event to an Element specified by parameter 'id'.
     *
     * @param id    the id identifying the Element to dispatch to
     * @param event the type of Event to dispatch identified by a string
     * @return <code>true</code> if the event has been cancelled during dispatch,
     *         otherwise <code>false</code>.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public boolean dispatch(String id, String event) throws XFormsException {
        return this.processorReference.dispatch(id, event);
    }

    /**
     * dispatches an Event to an Element specified by parameter 'id' and allows to set all events properties and
     * pass a context info
     *
     * @param targetId   the id identifying the Element to dispatch to
     * @param eventType  the type of Event to dispatch identified by a string
     * @param info       an implementation-specific context info object
     * @param bubbles    true if event bubbles
     * @param cancelable true if event is cancelable
     * @return <code>true</code> if the event has been cancelled during dispatch,
     *         otherwise <code>false</code>.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *
     */
    public boolean dispatch(String targetId, String eventType, Object info, boolean bubbles, boolean cancelable) throws XFormsException {
        return this.processorReference.dispatch(targetId, eventType,info,bubbles,cancelable);
    }

    public XFormsElement lookup(String id) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleEventException(Exception e) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setControlValue(String id, String value) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setUploadValue(String id, String mediatype, String filename, byte[] data) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public boolean isFileUpload(String id, String type) throws XFormsException {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void setRepeatIndex(String id, int index) throws XFormsException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void handleEvent(Event event) {
        this.result = event;
    }

    public Event getResult() {
        return this.result;
    }

    public void resetResult(){
        this.result = null;
    }

}
