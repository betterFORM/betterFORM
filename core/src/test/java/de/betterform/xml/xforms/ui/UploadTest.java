/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */
package de.betterform.xml.xforms.ui;

import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.action.EventCountListener;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.TestEventListener;
import de.betterform.xml.xforms.ui.Upload;

import java.io.BufferedInputStream;

/**
 * Tests the upload control.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: UploadTest.java 3251 2008-07-08 09:26:03Z lasse $
 */
public class UploadTest extends TestCase {
//    static {
//        org.apache.log4j.BasicConfigurator.configure();
//    }

    private XFormsProcessorImpl xformsProcesssorImpl;
    private TestEventListener valueChangedListener;
    private EventCountListener bindingListener;

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUploadBase64() throws Exception {
        Upload upload = (Upload) this.xformsProcesssorImpl.getContainer().lookup("upload-base64");
        upload.getTarget().addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        String filename = "UploadTest.xhtml";
        String mediatype = "application/xhtml+xml";

        BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream(filename));
        byte[] data = new byte[bis.available()];
        bis.read(data);
        upload.setValue(data, filename, mediatype);
        upload.getTarget().removeEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        assertEquals(new String(data), new String(Base64.decodeBase64(((String)upload.getValue()).getBytes())));
        assertEquals(filename, upload.getFilename().getValue());
        assertEquals(mediatype, upload.getMediatype().getValue());

        ModelItem modelItem = upload.getModel().getInstance(upload.getInstanceId()).getModelItem(upload.getInstanceNode());
        assertEquals(filename, modelItem.getFilename());
        assertEquals(mediatype, modelItem.getMediatype());

        assertEquals("upload-base64", this.valueChangedListener.getId());
    }

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUploadHex() throws Exception {
        Upload upload = (Upload) this.xformsProcesssorImpl.getContainer().lookup("upload-hex");
        upload.getTarget().addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        String filename = "UploadTest.xhtml";
        String mediatype = "application/xhtml+xml";

        BufferedInputStream bis = new BufferedInputStream(getClass().getResourceAsStream(filename));
        byte[] data = new byte[bis.available()];
        bis.read(data);
        upload.setValue(data, filename, mediatype);
        upload.getTarget().removeEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        assertEquals(new String(data), new String(Hex.decodeHex(((String)upload.getValue()).toCharArray())));
        assertEquals(filename, upload.getFilename().getValue());
        assertEquals(mediatype, upload.getMediatype().getValue());

        ModelItem modelItem = upload.getModel().getInstance(upload.getInstanceId()).getModelItem(upload.getInstanceNode());
        assertEquals(filename, modelItem.getFilename());
        assertEquals(mediatype, modelItem.getMediatype());

        assertEquals("upload-hex", this.valueChangedListener.getId());
    }

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUploadAnyURI() throws Exception {
        Upload upload = (Upload) this.xformsProcesssorImpl.getContainer().lookup("upload-uri");
        upload.getTarget().addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        String filename = "UploadTest.xhtml";
        String mediatype = "application/xhtml+xml";

        byte[] data = getClass().getResource(filename).toString().getBytes();
        upload.setValue(data, filename, mediatype);
        upload.getTarget().removeEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        assertEquals(new String(data), upload.getValue());
        assertEquals(filename, upload.getFilename().getValue());
        assertEquals(mediatype, upload.getMediatype().getValue());

        ModelItem modelItem = upload.getModel().getInstance(upload.getInstanceId()).getModelItem(upload.getInstanceNode());
        assertEquals(filename, modelItem.getFilename());
        assertEquals(mediatype, modelItem.getMediatype());

        assertEquals("upload-uri", this.valueChangedListener.getId());
    }

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUploadWithoutFilename() throws Exception {
        Upload upload = (Upload) this.xformsProcesssorImpl.getContainer().lookup("upload-without-filename");
        upload.getTarget().addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        String filename = "UploadTest.xhtml";
        String mediatype = "application/xhtml+xml";

        byte[] data = getClass().getResource(filename).toString().getBytes();
        upload.setValue(data, filename, mediatype);
        upload.getTarget().removeEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        assertEquals(new String(data), upload.getValue());
        assertEquals(null, upload.getFilename());
        assertEquals(mediatype, upload.getMediatype().getValue());

        ModelItem modelItem = upload.getModel().getInstance(upload.getInstanceId()).getModelItem(upload.getInstanceNode());
        assertEquals(filename, modelItem.getFilename());
        assertEquals(mediatype, modelItem.getMediatype());

        assertEquals("upload-without-filename", this.valueChangedListener.getId());
    }

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUploadWithoutMediatype() throws Exception {
        Upload upload = (Upload) this.xformsProcesssorImpl.getContainer().lookup("upload-without-mediatype");
        upload.getTarget().addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        String filename = "UploadTest.xhtml";
        String mediatype = "application/xhtml+xml";

        byte[] data = getClass().getResource(filename).toString().getBytes();
        upload.setValue(data, filename, mediatype);
        upload.getTarget().removeEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);

        assertEquals(new String(data), upload.getValue());
        assertEquals(filename, upload.getFilename().getValue());
        assertEquals(null, upload.getMediatype());

        ModelItem modelItem = upload.getModel().getInstance(upload.getInstanceId()).getModelItem(upload.getInstanceNode());
        assertEquals(filename, modelItem.getFilename());
        assertEquals(mediatype, modelItem.getMediatype());

        assertEquals("upload-without-mediatype", this.valueChangedListener.getId());
    }

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUploadWrong() throws Exception {
        Upload upload = (Upload) this.xformsProcesssorImpl.getContainer().lookup("upload-wrong");
        upload.getTarget().addEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);
        upload.getTarget().addEventListener(XFormsEventNames.BINDING_EXCEPTION, this.bindingListener, false);

        String filename = "UploadTest.xhtml";
        String mediatype = "application/xhtml+xml";
        byte[] data = getClass().getResource(filename).toString().getBytes();

        assertEquals(0, this.bindingListener.getCount());
        upload.setValue(data, filename, mediatype);
        assertEquals(1, this.bindingListener.getCount());

        upload.getTarget().removeEventListener(XFormsEventNames.VALUE_CHANGED, this.valueChangedListener, false);
        upload.getTarget().removeEventListener(XFormsEventNames.BINDING_EXCEPTION, this.bindingListener, false);

        assertEquals("", upload.getValue());
        assertEquals("", upload.getFilename().getValue());
        assertEquals("", upload.getMediatype().getValue());

        ModelItem modelItem = upload.getModel().getInstance(upload.getInstanceId()).getModelItem(upload.getInstanceNode());

        assertNull(modelItem.getFilename());
        assertNull(modelItem.getMediatype());

        assertEquals(null, this.valueChangedListener.getId());

    }

    /**
     * Tests ui element state.
     *
     * @throws Exception if any error occurred during the test.
     */
    public void testUploadInit() throws Exception {
        Upload upload = (Upload) this.xformsProcesssorImpl.getContainer().lookup("upload-init");

        String filename = "UploadTest.xhtml";
        String mediatype = "application/xhtml+xml";

        assertEquals("", upload.getValue());
        assertEquals(filename, upload.getFilename().getValue());
        assertEquals(mediatype, upload.getMediatype().getValue());

        ModelItem modelItem = upload.getModel().getInstance(upload.getInstanceId()).getModelItem(upload.getInstanceNode());
        assertEquals(filename, modelItem.getFilename());
        assertEquals(mediatype, modelItem.getMediatype());
    }

    /**
     * Sets up the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void setUp() throws Exception {
        this.valueChangedListener = new TestEventListener();
        this.bindingListener = new EventCountListener(XFormsEventNames.BINDING_EXCEPTION);

        this.xformsProcesssorImpl = new XFormsProcessorImpl();
        this.xformsProcesssorImpl.setXForms(getClass().getResourceAsStream("UploadTest.xhtml"));
        this.xformsProcesssorImpl.init();
    }

    /**
     * Tears down the test.
     *
     * @throws Exception in any error occurred during setup.
     */
    protected void tearDown() throws Exception {
        this.xformsProcesssorImpl.shutdown();
        this.xformsProcesssorImpl = null;

        this.valueChangedListener = null;
    }

}
