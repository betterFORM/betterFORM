/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.http.ssl;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.X509KeyManager;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URL;
import java.security.*;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

public class BetterFORMKeyStoreManager implements X509KeyManager {
        private static Log LOGGER = LogFactory.getLog(BetterFORMKeyStoreManager.class);

        private Vector<X509KeyManager> customX509KeyManagers;
        private X509KeyManager javaDefaultKeyManager;

        public BetterFORMKeyStoreManager() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            initBetterFORMKeyStoreManager();
        }

        private void initBetterFORMKeyStoreManager() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            this.customX509KeyManagers = new Vector<X509KeyManager>();
            this.javaDefaultKeyManager = getJavaDefaultKeyManager();
        }

    public void addCustomX509KeyManager(final URL url, final String password) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
        X509KeyManager customX509KeyManager;
        customX509KeyManager = getCustomX509KeyManager(url, password);
        if (customX509KeyManager != null) {
            this.customX509KeyManagers.add(customX509KeyManager);
        } else {
            LOGGER.warn("BetterFORMKeyStoreManager.addCustomX509KeyManager: Keystore: " + url.getFile()+ " not usable!");
        }
    }

    private X509KeyManager getCustomX509KeyManager(final URL url, final String password) throws NoSuchAlgorithmException, KeyStoreException, IOException, CertificateException, UnrecoverableKeyException {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            if (url == null) {
                throw new IllegalArgumentException("BetterFORMKeyStoreManager: Keystore url may not be null");
            }

            LOGGER.debug("BetterFORMKeyStoreManager: initializing custom key store");
            KeyStore customKeystore  = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream is = null;
            try {
        	    is = url.openStream();
                customKeystore.load(is, password != null ? password.toCharArray(): null);
            } finally {
        	    if (is != null) is.close();
            }

        if (LOGGER.isTraceEnabled()) {
            Enumeration aliases = customKeystore.aliases();
            while (aliases.hasMoreElements()) {
                String alias = (String) aliases.nextElement();
                LOGGER.trace("Trusted certificate '" + alias + "':");
                Certificate trustedcert =  customKeystore.getCertificate(alias);
                if (trustedcert != null && trustedcert instanceof X509Certificate) {
                    X509Certificate cert = (X509Certificate) trustedcert;
                    LOGGER.trace("  Subject DN: " + cert.getSubjectDN());
                    LOGGER.trace("  Signature Algorithm: " + cert.getSigAlgName());
                    LOGGER.trace("  Valid from: " + cert.getNotBefore());
                    LOGGER.trace("  Valid until: " + cert.getNotAfter());
                    LOGGER.trace("  Issuer: " + cert.getIssuerDN());
                }
            }
        }
            keyManagerFactory.init(customKeystore, password.toCharArray());

            KeyManager[] customX509KeyManagers = keyManagerFactory.getKeyManagers();
            if (customX509KeyManagers != null && customX509KeyManagers.length > 0) {
                for (int i = 0; i < customX509KeyManagers.length; i++) {
                    if (customX509KeyManagers[i] instanceof X509KeyManager) {
                         return (X509KeyManager) customX509KeyManagers[i];
                    }
                }
            }

            return null;
        }



        private X509KeyManager getJavaDefaultKeyManager() throws NoSuchAlgorithmException, KeyStoreException, UnrecoverableKeyException {
            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(null, null);

            KeyManager[] x509KeyManagers = keyManagerFactory.getKeyManagers();

            if (x509KeyManagers != null && x509KeyManagers.length > 0) {
                for (int i = 0; i < x509KeyManagers.length; i++) {
                    if (x509KeyManagers[i] instanceof X509KeyManager) {
                        return (X509KeyManager) x509KeyManagers[i];
                    }
                }
            }

            BetterFORMKeyStoreManager.LOGGER.warn("BetterFORMKeyStoreManager: No key managers available for default algorithm.");
            return null;
        }

         //@Override
        public String chooseClientAlias(String[] keyType, Principal[] principals, Socket socket) {
            Iterator<X509KeyManager> iterator = this.customX509KeyManagers.iterator();
            while (iterator.hasNext()) {
                X509KeyManager x509KeyManager = iterator.next();
                if (x509KeyManager.chooseClientAlias(keyType, principals, socket) != null) {
                    BetterFORMKeyStoreManager.LOGGER.trace("BetterFORMKeyStoreManager.chooseClientAlias: Found client alias in custom keystore: " + x509KeyManager.toString());
                    //Found client alias in a custom keystore return it.
                    return x509KeyManager.chooseClientAlias(keyType, principals, socket);
                }
            }

            //Return client alias from JAVA VM keystor or null;
            return javaDefaultKeyManager.chooseClientAlias(keyType, principals, socket);
        }

        //@Override
        public String chooseServerAlias(String keyType, Principal[] principals, Socket socket) {
            Iterator<X509KeyManager> iterator = this.customX509KeyManagers.iterator();
            while (iterator.hasNext()) {
                X509KeyManager x509KeyManager = iterator.next();
                if (x509KeyManager.chooseServerAlias(keyType, principals, socket) != null) {
                    BetterFORMKeyStoreManager.LOGGER.trace("BetterFORMKeyStoreManager.chooseServerAlias: Found server alias in custom keystore: " + x509KeyManager.toString());
                    //Found server alias in a custom keystore return it.
                    return x509KeyManager.chooseServerAlias(keyType, principals, socket);
                }
            }

            //Return server alias from JAVA VM keystor or null;
            return javaDefaultKeyManager.chooseServerAlias(keyType, principals, socket);
        }

        //@Override
        public X509Certificate[] getCertificateChain(String alias) {
            Iterator<X509KeyManager> iterator = this.customX509KeyManagers.iterator();
            while (iterator.hasNext()) {
                X509KeyManager x509KeyManager = iterator.next();
                X509Certificate[] certificateChain = x509KeyManager.getCertificateChain(alias);
                if (certificateChain != null && certificateChain.length > 0) {
                    BetterFORMKeyStoreManager.LOGGER.trace("BetterFORMKeyStoreManager.getCertificateChain: Certificate chain found for " + alias + " in custom keystore: " + x509KeyManager.toString());
                    //Found server alias in a custom keystore return it.
                    return x509KeyManager.getCertificateChain(alias);
                }
            }

            //Return server alias from JAVA VM keystor or null;
            return javaDefaultKeyManager.getCertificateChain(alias);
        }

        //@Override
        public String[] getClientAliases(String keyType, Principal[] principals) {
            String[] customClientAliases = null;
            Iterator<X509KeyManager> iterator = this.customX509KeyManagers.iterator();
            while (iterator.hasNext()) {
                X509KeyManager x509KeyManager = iterator.next();
                customClientAliases = (String[]) ArrayUtils.addAll(customClientAliases, x509KeyManager.getClientAliases(keyType, principals));
            }
            return (String[]) ArrayUtils.addAll(customClientAliases, javaDefaultKeyManager.getClientAliases(keyType, principals));
        }

        //@Override
        public PrivateKey getPrivateKey(String alias) {
            Iterator<X509KeyManager> iterator = this.customX509KeyManagers.iterator();
            while (iterator.hasNext()) {
                X509KeyManager x509KeyManager = iterator.next();
                if (x509KeyManager.getPrivateKey(alias) != null) {
                    BetterFORMKeyStoreManager.LOGGER.trace("BetterFORMKeyStoreManager.getPrivateKey: Private key found for " + alias + " in custom keystore: " + x509KeyManager.toString());
                    //Found private key in a custom keystore return it.
                    return x509KeyManager.getPrivateKey(alias);
                }
            }

            //Return private key from JAVA VM keystor or null;
            return javaDefaultKeyManager.getPrivateKey(alias);
        }

        //@Override
        public String[] getServerAliases(String keyType, Principal[] principals) {
            String[] customClientAliases = null;
            Iterator<X509KeyManager> iterator = this.customX509KeyManagers.iterator();
            while (iterator.hasNext()) {
                X509KeyManager x509KeyManager = iterator.next();
                customClientAliases = (String[]) ArrayUtils.addAll(customClientAliases, x509KeyManager.getServerAliases(keyType, principals));
            }
            return (String[]) ArrayUtils.addAll(customClientAliases, javaDefaultKeyManager.getServerAliases(keyType, principals));
        }
    }