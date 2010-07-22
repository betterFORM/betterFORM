/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.config;

import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.dom.NodeWrapper;
import net.sf.saxon.om.NodeInfo;
import net.sf.saxon.sxpath.IndependentContext;
import de.betterform.connector.InstanceSerializer;
import de.betterform.connector.InstanceSerializerMap;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Load the configuration in the default XML format from an InputStream.
 * 
 * @author Flavio Costa <flaviocosta@users.sourceforge.net>
 * @author Terence Jacyno
 */
public class DefaultConfig extends Config {

	/**
	 * Creates and loads a new configuration.
	 * 
	 * @param stream
	 *            InputStream to read XML data in the default format.
	 */
	public DefaultConfig(InputStream stream) throws XFormsConfigException {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			factory.setNamespaceAware(false);
			factory.setValidating(false);

			Document document = factory.newDocumentBuilder().parse(stream);
			
			NodeInfo context = getDocumentElementContext(document);
			
			this.properties = load(context, "properties/property", "name", "value");
			this.useragents = load(context, "useragents/useragent", "name", "value");
			this.uriResolvers = load(context, "connectors/uri-resolver", "scheme", "class");
			this.submissionHandlers = load(context, "connectors/submission-handler", "scheme", "class");
			this.errorMessages = load(context, "error-messages/message", "id", "value");

			//this.actions = load (context,"actions/action", "name", "class");
			//this.generators = load(context, "generators/generator", "name",
			// "class");
			this.extensionFunctions = loadExtensionFunctions(context,
					"extension-functions/function");
			this.customElements = loadCustomElements(context,
					"custom-elements/element");

			this.connectorFactory = load(context, "connectors", "factoryClass");

			this.instanceSerializerMap = loadSerializer(context,
					"register-serializer/instance-serializer", "scheme",
					"method", "mediatype", "class");

		} catch (Exception e) {
			throw new XFormsConfigException(e);
		}
	}

	/**
	 * @param document
	 * @return
	 */
	private NodeWrapper getDocumentElementContext(Document document) {
	    return new DocumentWrapper(document, "configuration.xml", new IndependentContext().getConfiguration()).wrap(document.getDocumentElement());
	}

	/**
	 * Returns the specified configuration section in a hash map.
	 * 
	 * @param configContext
	 *            the context holding the configuration.
	 * @param sectionPath
	 *            the context relative path to the section.
	 * @param nameAttribute
	 *            the name of the attribute to be used as a hash map key.
	 * @param valueAttribute
	 *            the name of the attribute to be used as a hash map value.
	 * @return the specified configuration section in a hash map.
	 * @throws Exception
	 *             if any error occured during configuration loading.
	 */
	private HashMap load(NodeInfo configContext, String sectionPath,
			String nameAttribute, String valueAttribute) throws Exception {
		HashMap map = new HashMap();
		List nodeset = XPathCache.getInstance().evaluate(configContext, sectionPath, Collections.EMPTY_MAP, null);

		for(int i = 0; i < nodeset.size(); ++i) {
			Element element = (Element) XPathUtil.getAsNode(nodeset, i + 1);

			map.put(element.getAttribute(nameAttribute), element
					.getAttribute(valueAttribute));
		}

		return map;
	}

	/**
	 * Read custom instance serializer that are used by AbstractConnector.
	 * 
	 * @param configContext
	 *            the context holding the configuration.
	 * @param sectionPath
	 *            the context relative path to the section.
	 * @param scheme
	 *            the name of the attribute holding the scheme.
	 * @param method
	 *            the name of the attribute holding the method.
	 * @param mediatype
	 *            the name of the attribute holding the mediatype.
	 * @param serializerClass
	 *            the name of the attribute holding the InstanceSerializer
	 *            implementation.
	 * @return the specified configuration section in a hash map.
	 * @throws Exception
	 *             if any error occured during configuration loading.
	 */
	private InstanceSerializerMap loadSerializer(NodeInfo configContext,
			String sectionPath, String scheme, String method, String mediatype,
			String serializerClass) throws Exception {
		InstanceSerializerMap map = new InstanceSerializerMap();
		List nodeset = XPathCache.getInstance().evaluate(configContext, sectionPath, Collections.EMPTY_MAP, null);

		for(int i = 0; i < nodeset.size(); ++i) {
			Element element = (Element) XPathUtil.getAsNode(nodeset, i + 1);

			try {

				String schemeVal = element.getAttribute(scheme);
				schemeVal = ("".equals(schemeVal)) ? "*" : schemeVal;

				String methodVal = element.getAttribute(method);
				methodVal = ("".equals(methodVal)) ? "*" : methodVal;

				String mediatypeVal = element.getAttribute(mediatype);
				mediatypeVal = ("".equals(mediatypeVal)) ? "*" : mediatypeVal;

				String classVal = element.getAttribute(serializerClass);
				if (classVal == null) {
					continue;
				}

				InstanceSerializer serializer = (InstanceSerializer) Class
						.forName(classVal).newInstance();

				map.registerSerializer(schemeVal, methodVal, mediatypeVal,
						serializer);

			} catch (Exception e) {
				// silently ignore invalid references ...
				LOGGER.error("registerSerializer(\"" + scheme + "\",\""
						+ method + "\"," + mediatype + "\",\""
						+ serializerClass + "\") failed: " + e.getMessage(), e);
			}
		}

		return map;
	}

	// ==================== Added by Terence Jacyno (start): 7.12 - extension
	// functions
	private HashMap loadExtensionFunctions(NodeInfo configContext,
			String sectionPath) throws XFormsException {
		HashMap map = new HashMap();
		List nodeset = XPathCache.getInstance().evaluate(configContext, sectionPath, Collections.EMPTY_MAP, null);

		for(int i = 0; i < nodeset.size(); ++i) {
			Element element = (Element) XPathUtil.getAsNode(nodeset, i + 1);

			String namespace = element.getAttribute("namespace");
			namespace = ("".equals(namespace)) ? null : namespace;

			//String prefix = element.getXFormsAttribute("prefix");
			//prefix = ("".equals(prefix)) ? null : prefix;

			String function = element.getAttribute("name");
			function = ("".equals(function)) ? null : function;

			String functionClass = element.getAttribute("class");
			functionClass = ("".equals(functionClass)) ? null : functionClass;

			String key = (namespace == null) ? function : namespace
					+ ((function == null) ? "" : " " + function);
			//String prefixKey = (prefix == null) ? function : prefix +
			// ((function == null) ? "" : " " + function);

			if ((function != null) && (functionClass != null)) {
				String javaFunction = element.getAttribute("java-name");
				javaFunction = ((javaFunction == null) || ""
						.equalsIgnoreCase(javaFunction))
						? function
						: javaFunction;
				String[] classFunction = new String[]{functionClass,
						javaFunction};

				if (key != null) {
					map.put(key, classFunction);
				}
				//if (prefixKey != null) {
				//    map.put(prefixKey, classFunction);
				//}
			}
		}

		return map;
	}

	// ==================== Added by Terence Jacyno (end): 7.12 - extension
	// functions

	private HashMap loadCustomElements(NodeInfo configContext,
			String sectionPath) throws XFormsException {
		HashMap map = new HashMap();
		List nodeset = XPathCache.getInstance().evaluate(configContext, sectionPath, Collections.EMPTY_MAP, null);

		for(int i = 0; i < nodeset.size(); ++i) {
			Element element = (Element) XPathUtil.getAsNode(nodeset, i + 1);

			String namespace = element.getAttribute("namespace");
			namespace = ("".equals(namespace)) ? null : namespace;

			String elementName = element.getAttribute("name");
			elementName = ("".equals(elementName)) ? null : elementName;

			String elementClass = element.getAttribute("class");
			elementClass = ("".equals(elementClass)) ? null : elementClass;

			String key = (namespace == null) ? elementName : namespace
					+ ((elementName == null) ? "" : ":" + elementName);

			if ((elementName != null) && (elementClass != null)) {
				if (key != null) {
					map.put(key, elementClass);
				}
			}
		}

		return map;
	}

	/**
	 * Returns the specified configuration value in a String.
	 * 
	 * @param configContext
	 *            the context holding the configuration.
	 * @param path
	 *            the context relative path to the section.
	 * @param nameAttribute
	 *            the name of the attribute to load from
	 * @return the specified configuration value in a String
	 * @throws Exception
	 *             if any error occured during configuration loading.
	 */
	private String load(NodeInfo configContext, String path,
			String nameAttribute) throws Exception {
		String value;


		Element element = (Element) XPathUtil.getAsNode(XPathCache.getInstance().evaluate(configContext, path, Collections.EMPTY_MAP, null), 1);
		value = element.getAttribute(nameAttribute);

		return value;
	}
	
}
