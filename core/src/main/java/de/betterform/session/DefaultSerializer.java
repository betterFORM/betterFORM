/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.session;

import de.betterform.generator.XSLTGenerator;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import de.betterform.xml.xslt.impl.CachingTransformerService;
import de.betterform.xml.xslt.impl.FileResourceResolver;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.DOMNodeWrapper;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.IndependentContext;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;

/**
 * SessionSerializer allows to persist a running XForms session as XML. It uses a special
 * XML structure as container to hold all data and states.
 *
 * todo: serialize the context map !!!
 * todo: CURRENT VERSION DOES NOT SUPPORT THE USAGE OF ATTRIBUTE VALUE TEMPLATES 
 *
 * @author Joern Turner
 */
public class DefaultSerializer {
    /**
     * The logger.
     */
    protected static Log LOGGER = LogFactory.getLog(DefaultSerializer.class);

    private XFormsProcessorImpl processor;


    public DefaultSerializer(XFormsProcessorImpl processor) {
        this.processor = processor;
    }

    /**
     * writes the XML session container
     *
     * @throws java.io.IOException
     */
    public Document serialize() throws IOException {
        Document result;
        try {
            result = serializeHostDocument();
        } catch (XFormsException e) {
            throw new IOException("Error while serializing host document: " + e.getMessage());
        } catch (TransformerException e) {
            throw new IOException("Error while transforming host document: " + e.getMessage());
        } catch (URISyntaxException e) {
            throw new IOException("Invalid URI: " + e.getMessage());
        }
        return result;
    }


    private Document serializeHostDocument() throws XFormsException, TransformerException, URISyntaxException {
        Document in = this.processor.getXForms();
        Document out = DOMUtil.newDocument(true,false);

        //resetting internal DOM to original state
        resetForm(in, out);
        inlineInstances(out);

//        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("##### resetted XForms #####");
            //DOMUtil.prettyPrintDOM(out);
//        }
        return out;
    }

    /**
     * inlines all instances from the processor into the output document. Eventually existent @src Attributes
     * have already been removed during the 'reset' transformation.
     *
     * @param out the output document for serialization
     */
    public void inlineInstances(Document out) throws XFormsException {
        //imlining instances
        NodeInfo context = getDocumentElementContext(out);
        //iterate all models to get all instances
        List models = this.processor.getContainer().getModels();
        for (int i = 0; i < models.size(); i++) {
            Model model = (Model) models.get(i);

            List instances = model.getInstances();
            for (int j = 0; j < instances.size(); j++) {
                Instance instance = (Instance) instances.get(j);
                String id = instance.getId();

                //get node from out
                String search = "//*[@id='" + id + "']";
                Node outInstance = XPathUtil.getAsNode(XPathCache.getInstance().evaluate(context, search, Collections.EMPTY_MAP, null), 1);
                Node imported = out.adoptNode(instance.getInstanceDocument().getDocumentElement());
                if(imported == null){
                    throw new XFormsException("Root Element for Instance '" + instance.getId() + "' not found");
                }

                Node firstChild = DOMUtil.getFirstChildElement(outInstance);

                if(firstChild != null){
                    outInstance.removeChild(firstChild);
                }

                outInstance.appendChild(imported);
//                    outInstance.replaceChild(imported,DOMUtil.getFirstChildElement(outInstance));

            }
        }
    }

    /**
     * resets the form to its original state right after parsing has been done and before XForms initialization
     * has taken place. This means that repeats will not be unrolled and all bf:data elements have been removed.
     *
     * Besides resetting the form the stylesheet also preserves the states of repeat indexes and selected case elements.
     *
     * @param in the internal betterForm DOM
     * @param out the output document for serialization
     * @throws TransformerException in case something with Transformation goes wrong
     * @throws URISyntaxException in case the stylesheet URI is invalid or cannot be loaded
     * @throws XFormsException
     */
    private void resetForm(Document in, Document out) throws TransformerException, URISyntaxException, XFormsException {
        CachingTransformerService transformerService = new CachingTransformerService(new FileResourceResolver());
        String path = getClass().getResource("reset.xsl").getPath();
        String xslFilePath = "file:" + path;
        transformerService.getTransformer(new URI(xslFilePath));

        XSLTGenerator generator = new XSLTGenerator();
        generator.setTransformerService(transformerService);
        generator.setStylesheetURI(new URI(xslFilePath));
        generator.setInput(in);
        generator.setOutput(out);
        generator.generate();
    }

    /**
     * @param document
     * @return
     */
     private static NodeWrapper getDocumentElementContext(Document document) {
	return new DocumentWrapper(document, "configuration.xml", new IndependentContext().getConfiguration()).wrap(document.getDocumentElement());
     }
}
