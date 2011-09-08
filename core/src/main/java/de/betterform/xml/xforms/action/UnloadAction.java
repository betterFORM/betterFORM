/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.action;


import de.betterform.connector.ConnectorFactory;
import de.betterform.connector.URIResolver;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.Initializer;
import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.submission.AttributeOrValueChild;
import de.betterform.xml.xforms.ui.RepeatItem;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import net.sf.saxon.dom.NodeWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.EventTarget;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;


/**
 * allows unloading of embedded forms
 *
 * @author Joern Turner
 */
public class UnloadAction extends AbstractBoundAction {
    private static final Log LOGGER = LogFactory.getLog(UnloadAction.class);
    private AttributeOrValueChild resource;
    private String showAttribute = null;
    private String targetAttribute = null;

    /**
     * Creates a load action implementation.
     *
     * @param element the element.
     * @param model   the context model.
     */
    public UnloadAction(Element element, Model model) {
        super(element, model);
    }

    // lifecycle methods

    /**
     * Performs element init.
     */
    public void init() throws XFormsException {
        super.init();
        this.targetAttribute = getXFormsAttribute(TARGETID_ATTRIBUTE);
    }

    // implementation of 'de.betterform.xml.xforms.action.XFormsAction'

    /**
     * Performs the <code>load</code> action.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException if an error occurred during <code>load</code>
     *                         processing.
     */
    public void perform() throws XFormsException {
        String evaluatedTarget = evalAttributeValueTemplates(this.targetAttribute, this.element);
        Element targetElem = getTargetElement(evaluatedTarget);
        destroyembeddedModels(targetElem);
        DOMUtil.removeAllChildren(targetElem);
        HashMap map = new HashMap();


        map.put("show", this.showAttribute);
        if (isRepeated()) {
            map.put("nameTarget", evaluatedTarget);
            String idForNamedElement = getXFormsAttribute(getTargetElement(evaluatedTarget), "id");
            map.put("xlinkTarget", idForNamedElement);

        } else {
            map.put("xlinkTarget", evaluatedTarget);
        }

        this.container.dispatch(this.target, BetterFormEventNames.LOAD_URI, map);
        return;
    }

    private void destroyembeddedModels(Element targetElem) throws XFormsException {
        NodeList childNodes = targetElem.getChildNodes();

        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;

                String name = elementImpl.getLocalName();
                String uri = elementImpl.getNamespaceURI();

                if (NamespaceConstants.XFORMS_NS.equals(uri) && name.equals(XFormsConstants.MODEL)) {
                    Model model = (Model) elementImpl.getUserData("");
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("dispatch 'model-destruct' event to embedded model: " + model.getId());
                        ;
                    }

                    // do not dispatch model-destruct to avoid problems in lifecycle
                    // TODO: review: this.container.dispatch(model.getTarget(), XFormsEventNames.MODEL_DESTRUCT, null);
                    model.dispose();
                    this.container.removeModel(model);
                    model = null;
                } else {
                    destroyembeddedModels(elementImpl);
                }
            }
        }
    }

    /**
     * fetches the Element that is the target for embedding. This Element will be replaced by the markup
     * to be embedded or all of its contents is erased in case of an 'unload'.
     *
     * @param evaluatedTarget the targetid of a load action can be a AVT expression and be evaluated. The result
     *                        is treated as an idref to an Element.
     * @return
     * @throws XFormsException
     */
    private Element getTargetElement(String evaluatedTarget) throws XFormsException {
        Element targetElem = null;
        if (isRepeated()) {
            String itemId = getRepeatItemId();
            RepeatItem item = (RepeatItem) container.lookup(itemId);
            int pos = item.getPosition();
            targetElem = (Element) XPathUtil.evaluateAsSingleNode(item.getElement().getOwnerDocument(), "//*[@name='" + evaluatedTarget + "']");
        } else {
            // ##### try to interpret the targetAttribute value as an idref ##### -->
            targetElem = this.container.getElementById(evaluatedTarget);
        }
        return targetElem;
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

}

// end of class
