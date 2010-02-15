/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.connector.InstanceSerializerMap;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Selects a concrete Config implementation and initializes it as a singleton.
 * 
 * @author Ulrich Nicolas Liss&eacute;
 * @author Eduardo Millan <emillan AT users.sourceforge.net>
 * @author Flavio Costa <flaviocosta@users.sourceforge.net>
 * @version $Id: Config.java 3463 2008-08-14 11:03:36Z joern $
 */
public abstract class Config {

	/**
	 * The singleton instance.
	 */
	private static Config SINGLETON;

	/**
	 * The default configuration file name.
	 */
	private static final String DEFAULT_XML_FILE = "default.xml";

	/**
	 * The logger.
	 */
    protected static Log LOGGER = LogFactory.getLog(Config.class);

	/**
	 * The properties lookup map.
	 */
	protected HashMap properties;

	/**
	 * The useragent lookup map.
	 */
	protected HashMap useragents;

	/**
	 * The submission handlers lookup map.
	 */
	protected HashMap submissionHandlers;

	/**
	 * The URI resolvers lookup map.
	 */
	protected HashMap uriResolvers;

	/**
	 * The error messages lookup map.
	 */
	protected HashMap errorMessages;

	/**
	 * The actions lookup map.
	 */
	//protected HashMap actions;
	/**
	 * The generators lookup map.
	 */
	//protected HashMap generators;
	/**
	 * The extension functions lookup map.
	 */
	protected HashMap extensionFunctions;

	/**
	 * The custom elements lookup map.
	 */
	protected HashMap customElements;

	/**
	 * Configured connector factory class.
	 */
	protected String connectorFactory;

	/**
	 * Map of InstanceSerializer.
	 */
	protected InstanceSerializerMap instanceSerializerMap;

    /**
	 * Provides a default constructor for subclasses.
	 */
	protected Config() throws XFormsConfigException {
		//do nothing for the moment
	}

	/**
	 * Instantiates and defines the singleton instance.
	 * 
	 * @param stream
	 *            InputStream from where the configuration will be read.
	 * @throws XFormsConfigException
	 *             If the configuration could not be loaded.
	 */
	private static void initSingleton(InputStream stream)
			throws XFormsConfigException {

		//gets the concrete config class name from a system property
		//using DefaultConfig if the property is not set
		String configClassName = System.getProperty(Config.class.getName(),
				DefaultConfig.class.getName());

		try {
			//uses reflection to get the constructor
			//(the constructor must have public visibility)
			Class classRef = Class.forName(configClassName,true, Config.class.getClassLoader()); 

			Constructor construct = classRef
					.getConstructor(new Class[]{InputStream.class});

			//initializes the singleton invonking the constructor
			SINGLETON = (Config) construct.newInstance(new Object[]{stream});

		} catch (Exception e) {
			throw new XFormsConfigException(e);
		}
	}

	/**
	 * Returns the singleton configuration instance. If it is not yet
	 * initialized, it will be created from the default configuration file.
	 * 
	 * @return The configuration singleton.
	 */
	public static synchronized Config getInstance()
			throws XFormsConfigException {
		if (SINGLETON == null) {
			LOGGER.info("loading config from " + DEFAULT_XML_FILE);
			initSingleton(Config.class.getResourceAsStream(DEFAULT_XML_FILE));
		}

		return SINGLETON;
	}

	/**
	 * Initializes and returns the singleton configuration instance. If it is
	 * already initialized, it will be recreated from the given file.
	 *
	 * @param file
	 *            The absolute path name denoting a configuration file.
	 * @return The configuration singleton.
	 */
	public static synchronized Config getInstance(String file)
			throws XFormsConfigException {

		LOGGER.info((SINGLETON == null ? "loading" : "reloading")
				+ " config from " + file);

		try {
			initSingleton(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			//specified file could not be found
			throw new XFormsConfigException(e);
		}

		return SINGLETON;
	}

	/**
	 * Initializes and returns the singleton configuration instance. If it is
	 * already initialized, it will be recreated from the given InputStream.
	 *
	 * @param inputStream
	 *            The InputStream to read a configuration file.
	 * @return The configuration singleton.
	 */
    public static synchronized Config getInstance(InputStream inputStream)
			throws XFormsConfigException {

		LOGGER.info((SINGLETON == null ? "loading" : "reloading")
				+ " config from an InputStream");

		initSingleton(inputStream);

		return SINGLETON;
	}


        /** Set Config to null */
    public static synchronized void unloadConfig() {
        SINGLETON = null;
    }

    /**
	 * Returns the specifed property value.
	 * 
	 * @param key
	 *            the name of the property.
	 * @return the specifed property value.
	 */
	public String getProperty(String key) {
		return getProperty(key, null);
	}

	/**
	 * Returns the specifed property value.
	 * 
	 * @param key
	 *            the name of the property.
	 * @param value
	 *            the default value of the property.
	 * @return the specifed property value.
	 */
	public String getProperty(String key, String value) {
		String s = (String) this.properties.get(key);

		return (s != null) ? s : value;
	}

	/**
	 * Returns the specifed stylesheet value.
	 * 
	 * @param key
	 *            the name of the stylesheet.
	 * @return the specifed stylesheet value.
	 */
	public String getStylesheet(String key) {
		return (String) this.useragents.get(key);
	}

	/**
	 * Returns the specifed submission handler class.
	 * 
	 * @param key
	 *            the scheme of the submission handler.
	 * @return the specifed submission handler class.
	 */
	public String getSubmissionHandler(String key) {
		return (String) this.submissionHandlers.get(key);
	}

	/**
	 * Returns the specifed URI resolver class.
	 * 
	 * @param key
	 *            the scheme of the URI resolver.
	 * @return the specifed URI resolver class.
	 */
	public String getURIResolver(String key) {
		return (String) this.uriResolvers.get(key);
	}

	/**
	 * Gets error messages.
	 * 
	 * @param key
	 *            Message key.
	 * @return Message string.
	 */
	public String getErrorMessage(String key) {
        if(this.errorMessages.containsKey(key)){
            return (String) this.errorMessages.get(key);
        }
		return (String) this.errorMessages.get("default");
	}

	/**
	 * Gets the map of all defined custom elements.
	 * 
	 * @return Map where each key is in the format namespace-uri:element-name
	 *         and the value is the associated class name.
	 */
	public Map getCustomElements() {
		return Collections.unmodifiableMap(customElements);
	}

	/**
	 * Returns the InstanceSerializer map. This method should be called only in
	 * AbstractConnector.
	 * 
	 * @return instance serializer map.
	 */
	public InstanceSerializerMap getInstanceSerializerMap() {
		return instanceSerializerMap;
	}

	/**
	 * Gets the connector factory from the configuration file.
	 * 
	 * @return The connector factory class name.
	 */
	public String getConnectorFactory() {
		return this.connectorFactory;
	}

	//    /**
	//     * Returns the Action classname handling this tag
	//     *
	//     * @param key the tagname of the Action
	//     * @return the Action classname handling this tag
	//     */
	//    public String getAction(String key) {
	//        return (String) this.actions.get(key);
	//    }
	//
	//    public HashMap getActions() {
	//        return this.actions;
	//    }
	//    /**
	//     * Returns the specifed generator class.
	//     *
	//     * @param key the name of the generator.
	//     * @return the specifed generator class.
	//     */
	//    public String getGenerator (String key)
	//    {
	//        return (String) this.generators.get(key);
	//    }
}

// end of class
