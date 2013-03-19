/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.http.ssl;

import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.config.Config;
import de.betterform.xml.config.XFormsConfigException;
import org.apache.commons.httpclient.contrib.ssl.AuthSSLInitializationError;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * @author <a href="mailto:tobias.krebs@betterform.de">tobi</a>
 * @version $Id: KeyStoreSSLContext 03.08.2011 tobi $
 */
public class KeyStoreSSLContext {
    private static String keyStorePath = null;
    private static String keyStorePasswd = null;
    private SSLContext sslcontext = null;

    private static Log LOGGER = LogFactory.getLog(KeyStoreSSLContext.class);



    public KeyStoreSSLContext() {
        try {
            this.keyStorePath = Config.getInstance().getProperty(AbstractHTTPConnector.HTTPCLIENT_SSL_KEYSTORE_PATH, null);
            this.keyStorePasswd = Config.getInstance().getProperty(AbstractHTTPConnector.HTTPCLIENT_SSL_KEYSTORE_PASSWD , null);
        } catch (XFormsConfigException xfce) {
           LOGGER.warn(xfce.getLocalizedMessage(), xfce);
        }
    }

    private URL getKeyStoreURL() throws AuthSSLInitializationError {
        if (KeyStoreSSLContext.keyStorePath != null) {
            File keystore;

            keystore = new File(KeyStoreSSLContext.keyStorePath);
            if (! keystore.isAbsolute()) {
                LOGGER.info("Keystore path is not absoulte adding user.home to it. New keystore path: " + System.getProperty("user.home") + File.separator + KeyStoreSSLContext.keyStorePath);
                keystore = new File(System.getProperty("user.home") + File.separator + KeyStoreSSLContext.keyStorePath);
            }
            try {
                return keystore.toURI().toURL();
            } catch (MalformedURLException murle) {
                LOGGER.error("Wrong Syntax in " + AbstractHTTPConnector.HTTPCLIENT_SSL_KEYSTORE_PATH, murle);
                throw new AuthSSLInitializationError("Wrong Syntax in " + AbstractHTTPConnector.HTTPCLIENT_SSL_KEYSTORE_PATH);
            }
        } else {
            throw new AuthSSLInitializationError("You must configure "+ AbstractHTTPConnector.HTTPCLIENT_SSL_KEYSTORE_PATH + " in betterform-config.xml!");
        }
    }

    private String getKeyStorePasswd() throws AuthSSLInitializationError {
        if (KeyStoreSSLContext.keyStorePasswd != null) {
            //TODO: Support encryption of passwd!
            return KeyStoreSSLContext.keyStorePasswd;
        }

        throw new AuthSSLInitializationError("You must configure "+ AbstractHTTPConnector.HTTPCLIENT_SSL_KEYSTORE_PASSWD + " in betterform-config.xml!");
    }

    private SSLContext createSSLContext() {
        try {
            TrustManager[] trustmanagers = null;
            KeyManager[] keyManagers = null;
            if (getKeyStoreURL() != null) {
                BetterFORMKeyStoreManager bfkm = new BetterFORMKeyStoreManager();
                bfkm.addCustomX509KeyManager(getKeyStoreURL(), getKeyStorePasswd());
                keyManagers = new KeyManager[]{bfkm};
                BetterFORMTrustManager trustManagers = new BetterFORMTrustManager();
                trustManagers.addCustomX509TrustManager(getKeyStoreURL(), getKeyStorePasswd());
                trustmanagers = trustManagers.getTrustManagers();
            }
            SSLContext sslcontext = SSLContext.getInstance("SSL");
            sslcontext.init( keyManagers, trustmanagers, null);
            return sslcontext;
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error(e.getMessage(), e);
            throw new AuthSSLInitializationError("Unsupported algorithm exception: " + e.getMessage());
        } catch (KeyStoreException e) {
            LOGGER.error(e.getMessage(), e);
            throw new AuthSSLInitializationError("Keystore exception: " + e.getMessage());
        } catch (GeneralSecurityException e) {
            LOGGER.error(e.getMessage(), e);
            throw new AuthSSLInitializationError("Key management exception: " + e.getMessage());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            throw new AuthSSLInitializationError("I/O error reading keystore/truststore file: " + e.getMessage());
        }
    }

     public SSLContext getSSLContext() {
        if (this.sslcontext == null) {
            this.sslcontext = createSSLContext();
        }
        return this.sslcontext;
    }
}
