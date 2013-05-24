/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.connector;

import de.betterform.connector.serializer.SerializerRequestWrapper;
import de.betterform.connector.util.URIUtils;
import de.betterform.xml.config.Config;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.submission.Submission;
import org.w3c.dom.Node;

import java.net.URI;
import java.util.Map;

/**
 * A simple base class for convenient connector interface implementation.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: AbstractConnector.java 3479 2008-08-19 10:44:53Z joern $
 * todo: should be renamed cause it's not an abstract class
 * todo: when serialization is 'none' relevance selection and validation should not take place unless the author specifies these attributes. At the moment both WILL happen.
 */
public class AbstractConnector implements Connector {
    /**
     * The URI the connector is associated to.
     */
    private String uri;

    /**
     * Use this encoding, if client does not indicate otherwise.
     */
    private String defaultEncoding = "UTF-8";

    /**
     * InstanceSerializerMap contains scheme+method+mediatype combination along
     * with proper instance serializer.
     */
    private InstanceSerializerMap instanceSerializerMap;

    /**
     * The context map.
     */
    private Map context;

    /**
     * Sets the URI the connector is associated to.
     *
     * @param uri the URI the connector is associated to.
     */
    public void setURI(String uri) {
        this.uri = uri;
    }

    /**
     * Returns the URI the connector is associated to.
     *
     * @return the URI the connector is associated to.
     */
    public String getURI() {
        return this.uri;
    }

    /**
     * Register new instance serializer for given scheme, method and mediatype.
     * Method supports star convention, the scheme, method or mediatype set to
     * "*" acts as "for all" operator.
     *
     * @param scheme scheme part of the action uri
     * @param method method from xforms:method attribute
     * @param mediatype mediatype from xforms:mediatype attribute
     * @param serializer serializer that should be used for instance
     * serialization.
     */
    public void registerSerializer(String scheme, String method,
                                   String mediatype, InstanceSerializer serializer)
            throws XFormsException {

        if (instanceSerializerMap == null) {
            Config config = Config.getInstance();
            instanceSerializerMap = new InstanceSerializerMap(config.getInstanceSerializerMap());
        }
        instanceSerializerMap.registerSerializer(scheme, method, mediatype, serializer);
    }

    /**
     * Return serializer associated with given scheme, method and mediatype. The
     * lookup proceeds with following steps, returning first successfull
     * match:<br> <ol> <li> scheme, method, mediatype <li> scheme, method, "*"
     * <li> scheme, "*", mediatype <li> scheme, "*", "*" <li> "*", method,
     * mediatype <li> "*", method, "*" <li> "*", "*", mediatype <li> "*", "*",
     * "*" </ol>
     *
     * @param scheme scheme part of the action uri
     * @param method method from xforms:method attribute
     * @param mediatype mediatype from xforms:mediatype attribute
     * @return instance serializer or null
     */
    public InstanceSerializer getSerializer(String scheme, String method, String mediatype) throws XFormsException {
        if (instanceSerializerMap == null) {
            Config config = Config.getInstance();
            instanceSerializerMap = new InstanceSerializerMap(config.getInstanceSerializerMap());
        }
        return instanceSerializerMap.getSerializer(scheme, method, mediatype);
    }

    /**
     * Sets the encoding that will be used for serialization if client does not
     * provide it's own.
     *
     * @param defaultEncoding the encoding.
     */
    public void setDefaultEncoding(String defaultEncoding) {
        this.defaultEncoding = defaultEncoding;
    }

    /**
     * Returns the default encoding for serialization.
     *
     * @return the default encoding as String.
     */
    public String getDefaultEncoding() {
        return this.defaultEncoding;
    }

    /**
     * Sets the context map.
     *
     * @param context the context map.
     */
    public void setContext(Map context) {
        this.context = context;
    }

    /**
     * Returns the context map.
     *
     * @return the context map.
     */
    public Map getContext() {
        return this.context;
    }

    /**
     * strips the fragment part  (the part that starts with '#') from the URI
     *
     * @return URI string with fragment cut off
     */
    protected String getURIWithoutFragment() {
        return URIUtils.getURIWithoutFragment(this.uri);
    }

    protected final void serialize(Submission submission, Node instance, SerializerRequestWrapper wrapper) throws Exception {
        if(submission.getSerialization() != null && submission.getSerialization().equalsIgnoreCase("none")){
            return;
        }
        String method = submission.getMethod();
        if (method == null) {
            // oops ...            
            throw new XFormsException("Submission method not defined at: " + DOMUtil.getCanonicalPath(submission.getElement()));
        }

        URI uri = new URI(getURI());
        String scheme = uri.getScheme();

        String mediatype = submission.getMediatype();
        if (mediatype == null) {
            mediatype = "application/xml";
        }

        InstanceSerializer serializer = getSerializer(scheme, method, mediatype);
        if (serializer == null) {
            // is exception the right way to go ?
            throw new XFormsException("No instance serializer defined for scheme '"
                    + scheme + "', method '" + method + "' and mediatype '"
                    + mediatype + "' at: " + DOMUtil.getCanonicalPath(submission.getElement()));
        }

        serializer.serialize(submission, instance, wrapper, getDefaultEncoding());
    }

    /**
     * validate the instance according to its XMLSchema, if it is specified on
     * the model return true if the instance is valid, or if there is no
     * XMLSchema
     */
    public boolean validateSchema(Submission submission, Node instance) throws XFormsException {
        //launch schema validation
        Model model = submission.getModel();
        SchemaValidator validator = new SchemaValidator();
        return validator.validateSchema(model, instance);
    }

}

// end of class
