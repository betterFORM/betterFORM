/*
 * Copyright (c) 2011. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector.smtp;


import de.betterform.connector.serializer.SerializerRequestWrapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.connector.AbstractConnector;
import de.betterform.connector.SubmissionHandler;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Node;

import javax.mail.Authenticator;
import javax.mail.Message.RecipientType;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * The SMTP submission driver serializes and submits instance data over SMTP (internet mail).
 * <p/>
 * Currently, the driver only supports the <code>post</code> submission method and the replace mode <code>none</code>.
 * Support for <code>form-data-post</code> and <code>urlencoded-post</code> as suggested in <a
 * href="http://www.w3.org/TR/2002/CR-xforms-20021112#submit-options">11.2 Submission Options</a> is on the way.
 * <p/>
 * The driver requires the additional information about the SMTP server to use, the mail subject, and the sender. This
 * information has to be provided in the query part of the submission's <code>action</code> URI. If you want the driver
 * to authenticate a user with the SMTP server, just provide a <code>username</code> and a <code>password</code>.
 * Support for other mail header fields like <code>cc</code> may be added later.
 * <p/>
 * Be careful when writing the submission's <code>action</code> URI: First, the contents of the query part have to be
 * URL-encoded, then you have to replace all <code>&amp;</code>'s with their corresponding XML entity
 * <code>&amp;amp;</code> in order to keep the XML well-formed.
 * <p/>
 * Here is an illustrating example:
 * <pre>
 * &lt;xforms:submission id='smtp' xforms:action='mailto:nn@nowhere.no?server=smtp.nowhere.no&amp;amp;sender=xforms@nowhere.no&amp;amp;subject=instance%20data'
 * /&gt;
 * </pre>
 * The same example enforcing authentication:
 * <pre>
 * &lt;xforms:submission id='smtp-auth' xforms:action='mailto:nn@nowhere.no?server=smtp.nowhere.no&amp;amp;sender=xforms@nowhere.no&amp;amp;subject=instance%20data&amp;amp;username=xforms&amp;amp;password=shhh'
 * /&gt;
 * </pre>
 * Since mail accounts are personal data, there is no example form demonstrating SMTP submission.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: SMTPSubmissionHandler.java 3479 2008-08-19 10:44:53Z joern $
 */


public class SMTPSubmissionHandler extends AbstractConnector implements SubmissionHandler {
    /**
     * The logger.
     */
    private static final Log LOGGER = LogFactory.getLog(SMTPSubmissionHandler.class);

    /**
     * Serializes and submits the given instance data over the <code>mailto</code> protocol.
     *
     * @param submission the submission issuing the request.
     * @param instance   the instance data to be serialized and submitted.
     * @return <code>null</code>.
     * @throws XFormsException if any error occurred during submission.
     */
    public Map submit(Submission submission, Node instance) throws XFormsException {

        if (!submission.getReplace().equals("none")) {
            throw new XFormsException("submission mode '" + submission.getReplace() + "' at: " + DOMUtil.getCanonicalPath(submission.getElement()) + " not supported");
        }

        try {
            String mediatype = "application/xml";
            if (submission.getMediatype() != null) {
                mediatype = submission.getMediatype();
            }

            String encoding = getDefaultEncoding();
            if (submission.getEncoding() != null) {
                encoding = submission.getEncoding();
            }

            SerializerRequestWrapper wrapper = new SerializerRequestWrapper(new ByteArrayOutputStream());
            serialize(submission, instance, wrapper);
            ByteArrayOutputStream stream = (ByteArrayOutputStream) wrapper.getBodyStream();


            /*
             * Some extension mechanism here could be handy
             */
            String method = submission.getMethod();
            if (method.equals("post")) {
                send(getURI(), stream.toByteArray(), encoding, mediatype);
            } else if (method.equals("multipart-post")) {
                send(getURI(), stream.toByteArray(), encoding, "multipart/related");
            } else if (method.equals("form-data-post")) {
                send(getURI(), stream.toByteArray(), encoding, "multipart/form-data");
            } else if (method.equals("urlencoded-post")) {
                send(getURI(), stream.toByteArray(), encoding, "application/x-www-form-urlencoded");
            } else {
                // Note: user has to provide mediatype in submission element otherwise this will
                // be probably wrong type (application/xml) ...
                send(getURI(), stream.toByteArray(), encoding, mediatype);
            }
        } catch (Exception e) {
            throw new XFormsException(e);
        }

        return null;
    }

    private void send(String uri, byte[] data, String encoding, String mediatype) throws Exception {
        URL url = new URL(uri);
        String recipient = url.getPath();

        String server = null;
        String port = null;
        String sender = null;
        String subject = null;
        String username = null;
        String password = null;

        StringTokenizer headers = new StringTokenizer(url.getQuery(), "&");

        while (headers.hasMoreTokens()) {
            String token = headers.nextToken();

            if (token.startsWith("server=")) {
                server = URLDecoder.decode(token.substring("server=".length()), "UTF-8");

                continue;
            }

            if (token.startsWith("port=")) {
                port = URLDecoder.decode(token.substring("port=".length()), "UTF-8");

                continue;
            }

            if (token.startsWith("sender=")) {
                sender = URLDecoder.decode(token.substring("sender=".length()), "UTF-8");

                continue;
            }

            if (token.startsWith("subject=")) {
                subject = URLDecoder.decode(token.substring("subject=".length()), "UTF-8");

                continue;
            }

            if (token.startsWith("username=")) {
                username = URLDecoder.decode(token.substring("username=".length()), "UTF-8");

                continue;
            }

            if (token.startsWith("password=")) {
                password = URLDecoder.decode(token.substring("password=".length()), "UTF-8");

                continue;
            }
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("smtp server '" + server + "'");

            if (username != null) {
                LOGGER.debug("smtp-auth username '" + username + "'");
            }

            LOGGER.debug("mail sender '" + sender + "'");
            LOGGER.debug("subject line '" + subject + "'");
        }

        Properties properties = System.getProperties();
        properties.put("mail.debug", String.valueOf(LOGGER.isDebugEnabled()));
        properties.put("mail.smtp.from", sender);
        properties.put("mail.smtp.host", server);
        
        if (port != null) {
            properties.put("mail.smtp.port", port);
        }

        if (username != null) {
            properties.put("mail.smtp.auth", String.valueOf(true));
            properties.put("mail.smtp.user", username);
        }

        Session session = Session.getInstance(properties, new SMTPAuthenticator(username, password));

        MimeMessage message = null;
        if (mediatype.startsWith("multipart/")) {
            message = new MimeMessage(session, new ByteArrayInputStream(data));
        } else {
            message = new MimeMessage(session);
            if (mediatype.toLowerCase().indexOf("charset=") == -1) {
                mediatype += "; charset=\"" + encoding + "\"";
            }
            message.setText(new String(data, encoding), encoding);
            message.setHeader("Content-Type", mediatype);
        }

        message.setRecipient(RecipientType.TO, new InternetAddress(recipient));
        message.setSubject(subject);
        message.setSentDate(new Date());

        Transport.send(message);
    }

    private class SMTPAuthenticator extends Authenticator {
        private PasswordAuthentication authentication = null;

        /**
         * Creates a new SMTPAuthenticator object.
         *
         * @param user     user name
         * @param password password
         */
        SMTPAuthenticator(String user, String password) {
            this.authentication = new PasswordAuthentication(user, password);
        }

        /**
         * __UNDOCUMENTED__
         *
         * @return __UNDOCUMENTED__
         */
        protected PasswordAuthentication getPasswordAuthentication() {
            return this.authentication;
        }
    }
}

//end of class
