/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.ui.AbstractUIElement;
import de.betterform.xml.xforms.action.AbstractAction;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Element;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * CustomElementFactory creates objects for all elements in the input Document
 * that match a namespace/element name retrieved from the configuration.
 * 
 * @author <a href="mailto:flaviocosta@users.sourceforge.net">Flavio Costa </a>
 */
public class CustomElementFactory implements XFormsConstants {

	/**
	 * The logger.
	 */
    private static final Log LOGGER = LogFactory.getLog(Config.class);

	/**
	 * Holds the configuration for custom elements.
	 */
	private static final Map ELEMENTS = getCustomElementsConfig();

	/**
	 * Used to quickly check if there are custom elements or not.
	 */
	private boolean noCustomElements;

	/**
	 * Loads the configuration for custom elements.
	 * 
	 * @return A Map where the keys are in the format namespace-uri:element-name
	 *         and the value is the reference to the class that implements the
	 *         custom element, or an empty map if the configuration could not be
	 *         loaded for some reason.
	 */
	private static Map getCustomElementsConfig() {

		try {
			Map elementClassNames = Config.getInstance().getCustomElements();
			Map elementClassRefs = new HashMap(elementClassNames.size());

			Iterator itr = elementClassNames.entrySet().iterator();

			//converts class names into the class references
			while (itr.hasNext()) {

				Map.Entry entry = (Entry) itr.next();

				String key = (String) entry.getKey();
				String className = (String) entry.getValue();
				Class classRef = Class.forName(className,true, CustomElementFactory.class.getClassLoader());

				elementClassRefs.put(key, classRef);
			}

			//return the configuration
			return elementClassRefs;

		} catch (XFormsConfigException xfce) {
			LOGGER.error("could not load custom-elements config: "
					+ xfce.getMessage());

		} catch (ClassNotFoundException cnfe) {
			LOGGER.error("class configured for custom-element not found: "
					+ cnfe.getMessage());
		}

		//returns an empty map (no custom elements)
		return Collections.EMPTY_MAP;
	}

	/**
	 * Creates a new CustomElementFactory object.
	 */
	public CustomElementFactory() throws XFormsException {
		this.noCustomElements = ELEMENTS.isEmpty();
	}

	/**
	 * Given an element, generate a key used to find objects in ELEMENTS.
	 * 
	 * @param element
	 *            Element whose configuration is to be looked for.
	 * 
	 * @return The key corresponding to the element definition.
	 */
	public String getKeyForElement(Element element) {
		return element.getNamespaceURI() + ":" + element.getLocalName();
	}

	/**
	 * Returns true if the given DOM Element matches a configured custom
	 * element.
	 * 
	 * @param element
	 *            The DOM Element to investigate.
	 * @return Whether the element matches a custom element or not.
	 * 
	 * @throws XFormsException
	 *             In case of MustUnderstand Module failure.
	 */
	public boolean isCustomElement(Element element) throws XFormsException {

		if (this.noCustomElements) {
			//no custom elements configured
			return false;
		}

		//key to search the Map for a custom element
		String key = getKeyForElement(element);

		if (ELEMENTS.containsKey(key)) {
			//a match was found
			return true;
		}

		//if the element is not recognized but it has a
		//mustUnderstand attribute, throw an exception to
		//signal error according to the spec
		if (element.hasAttributeNS(NamespaceConstants.XFORMS_NS,
				MUST_UNDERSTAND_ATTRIBUTE)) {

			String elementId = element.getPrefix() + ":"
					+ element.getLocalName();

			throw new XFormsException(
					"MustUnderstand Module failure at element " + elementId);
		}

		//no matching configuration was found
		return false;
	}

	/**
	 * Factory method for custom element implementations.
	 * 
	 * @param element
	 *            The DOM Element which will be annotated.
	 * @param model
	 *            The owning model.
	 * @return The created object.
	 */
	public Object createCustomXFormsElement(Element element, Model model)
			throws XFormsException {

		//gets the class reference
		String key = getKeyForElement(element);
		Class elementClazz = (Class) ELEMENTS.get(key);

		//this should never happen
		if (elementClazz == null) {
			throw new XFormsException("No class defined for " + key);
		}

		if (elementClazz != null) {
			try {
				//initializes a new object from the class

				Object xformsElement= elementClazz.getConstructor(
						new Class[]{Element.class, Model.class}).newInstance(
						new Object[]{(Element) element, model});

				if (xformsElement instanceof AbstractUIElement) {
					element.setUserData("",(AbstractUIElement)xformsElement,null);
					return (AbstractUIElement)xformsElement;
				} if (xformsElement instanceof AbstractAction) {
					element.setUserData("",(AbstractAction)xformsElement,null);
					return (AbstractAction)xformsElement;
				}

			} catch (Exception e) {
				throw new XFormsException(e);
			}

		}

		LOGGER.warn("Custom class for " + key + " not does not extend AbstractUIElement or AbstractAction");
		return null;
	}
}
