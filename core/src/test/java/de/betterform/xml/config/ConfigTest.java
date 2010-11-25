/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 */

package de.betterform.xml.config;

import junit.framework.TestCase;

import java.util.Map;

/**
 * Test cases for configuration.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @author Eduardo Millan <emillan@users.sourceforge.net>
 * @version $Id: ConfigTest.java 3406 2008-08-11 16:25:46Z lasse $
 */
public class ConfigTest extends TestCase {

    public ConfigTest(String name) {
        super(name);
    }

    /**
     * Tests the default configuration.
     *
     * @throws java.lang.Exception if any error occurred durinng testing.
     */
    public void testDefaultConfiguration() throws Exception {
        // create default configuration instance
        Config config = Config.getInstance();
        String value = null;

        // test uri resolvers
        value = config.getURIResolver("file");
        assertNotNull("uri-resolver 'file' is unknown", value);
        assertTrue("uri-resolver 'file' is wrong",
                value.equals("de.betterform.connector.file.FileURIResolver"));

        // test submission handlers
        value = config.getSubmissionHandler("file");
        assertNotNull("submission-handler 'file' is unknown", value);
        assertTrue("submission-handler 'file' is wrong" + ": " + value,
                value.equals("de.betterform.connector.file.FileSubmissionHandler"));

        value = config.getSubmissionHandler("http");
        assertNotNull("submission-handler 'http' is unknown", value);
        assertTrue("submission-handler 'http' is wrong" + ": " + value,
                value.equals("de.betterform.connector.http.HTTPSubmissionHandler"));

        value = config.getSubmissionHandler("mailto");
        assertNotNull("submission-handler 'mailto' is unknown", value);
        assertTrue("submission-handler 'mailto' is wrong" + ": " + value,
                value.equals("de.betterform.connector.smtp.SMTPSubmissionHandler"));
    }

    /**
     * Tests the external configuration.
     *
     * @throws java.lang.Exception if any error occurred durinng testing.
     */
    public void testExternalConfiguration() throws Exception {
        // create external configuration instance
        Config config = Config.getInstance(getClass().getResource("test-default.xml").getPath());
        String value = null;

        Map useragents= Config.getInstance().getUserAgents();
        assertTrue(2 == useragents.size());
        assertEquals("foo.bar",useragents.get("foo"));
        assertEquals("bar.baz",useragents.get("bar"));

        Map generators = Config.getInstance().getGenerators();
        assertTrue(3 == generators.size());
        assertEquals("dojo.xsl",Config.getInstance().getStylesheet("foo"));
        assertEquals("dojodev.xsl",Config.getInstance().getStylesheet("bar"));
        assertEquals("html4.xsl",Config.getInstance().getStylesheet("baz"));


        // test properties
        value = config.getProperty("test.property");
        assertNotNull("property 'test.property' is unknown", value);
        assertTrue("property 'test.property' is wrong", value.equals("test.value"));

        // test connector factory
        value = config.getConnectorFactory();
        assertNotNull("Connector factory not found", value);
        assertTrue("Unknown connector factory", value.equals("test.factory"));

        // test uri resolvers
        value = config.getURIResolver("test.scheme");
        assertNotNull("uri-resolver 'test.scheme' is unknown", value);
        assertTrue("uri-resolver 'test.scheme' is wrong", value.equals("test.class"));

        // test submission handlers
        value = config.getSubmissionHandler("test.scheme");
        assertNotNull("submission-handler 'test.scheme' is unknown", value);
        assertTrue("submission-handler 'test.scheme' is wrong" + ": " + value, value.equals("test.class"));

    }

    protected void tearDown() throws Exception {
        Config.unloadConfig();
    }
}

// end of class
