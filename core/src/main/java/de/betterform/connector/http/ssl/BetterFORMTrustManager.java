/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.http.ssl;

import org.apache.commons.httpclient.contrib.ssl.AuthSSLX509TrustManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.Iterator;
import java.util.Vector;

public class BetterFORMTrustManager {
        private static Log LOGGER = LogFactory.getLog(BetterFORMKeyStoreManager.class);

        private Vector<TrustManager[]> customX509TrustManagers;
        private TrustManager[] javaDefaultTrustManagers;

        public BetterFORMTrustManager() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            initBetterFORMKeyStoreManager();
        }

        private void initBetterFORMKeyStoreManager() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            this.customX509TrustManagers = new Vector<TrustManager[]>();
            this.javaDefaultTrustManagers = getJavaDefaultTrustManagers();
        }

    public void addCustomX509TrustManager(final URL url, final String password) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        TrustManager[] customX509TrustManager;
        customX509TrustManager = getCustomX509TrustManagers(url, password);
        if (customX509TrustManager != null) {
            this.customX509TrustManagers.add(customX509TrustManager);
        } else {
            LOGGER.warn("BetterFORMTrustManager.addCustomX509KeyManager: Keystore: " + url.getFile()+ " not usable!");
        }
    }

    private TrustManager[] getCustomX509TrustManagers(final URL url, final String password) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());

        if (url == null) {
            throw new IllegalArgumentException("BetterFORMTrustManager: Keystore url may not be null");
        }

        LOGGER.debug("BetterFORMTrustManager: initializing custom key store");
        KeyStore customKeystore  = KeyStore.getInstance(KeyStore.getDefaultType());
        InputStream is = null;
        try {
            is = url.openStream();
            customKeystore.load(is, password != null ? password.toCharArray(): null);
        } finally {
            if (is != null) is.close();
        }

        trustManagerFactory.init(customKeystore);

        TrustManager[] customX509TrustManagers = trustManagerFactory.getTrustManagers();
        for (int i = 0; i < customX509TrustManagers.length; i++) {
            if (customX509TrustManagers[i] instanceof X509TrustManager) {
                customX509TrustManagers[i] = new AuthSSLX509TrustManager(
                    (X509TrustManager)customX509TrustManagers[i]);
            }
        }
        return customX509TrustManagers;
    }

    private TrustManager[] getJavaDefaultTrustManagers() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init((KeyStore)null);

        return trustManagerFactory.getTrustManagers();
    }

    public TrustManager[] getTrustManagers() {
        TrustManager[] customClientAliases = null;
        Iterator<TrustManager[]> iterator = customX509TrustManagers.iterator();
        while (iterator.hasNext()) {
            customClientAliases = (TrustManager[]) ArrayUtils.addAll(customClientAliases, iterator.next());
        }
        return (TrustManager[]) ArrayUtils.addAll(customClientAliases, javaDefaultTrustManagers);
    }
}