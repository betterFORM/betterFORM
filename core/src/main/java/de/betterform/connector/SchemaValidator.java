
/* Copyright 2008 - Joern Turner, Lars Windauer */
/* Licensed under the terms of BSD and Apache 2 Licenses */
package de.betterform.connector;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.exception.XFormsException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.net.URI;
import java.util.StringTokenizer;

/**
 * The SchemaValidator can validate the instance node according to its XMLSchema
 * <p>the schema(s) must be specified in the "schema" attribute of the model.</p>
 *
 * @author <a href="mailto:soframel@users.sourceforge.net">Sophie Ramel</a>
 */

public class SchemaValidator {

    /**
     * XMLSchema and XMLSchema-instance namespaces
     */
    private final static String XMLSCHEMA_INSTANCE_NS = "http://www.w3.org/2001/XMLSchema-instance";
    private final static String XMLSCHEMA_NS = "http://www.w3.org/2001/XMLSchema";

    /**
     * The logger.
     */
    private static Log LOGGER = LogFactory.getLog(SchemaValidator.class);

    public SchemaValidator() {
    }

    /**
     * validate the instance according to the schema specified on the model
     *
     * @return false if the instance is not valid
     */
    public boolean validateSchema(Model model, Node instance) throws XFormsException {
        boolean valid = true;
        String message;
        if (LOGGER.isDebugEnabled())
            LOGGER.debug("SchemaValidator.validateSchema: validating instance");

        //needed if we want to load schemas from Model + set it as "schemaLocation" attribute
        String schemas = model.getElement().getAttributeNS(NamespaceConstants.XFORMS_NS, "schema");
        if (schemas != null && !schemas.equals("")) {
//		    valid=false;

            //add schemas to element
            //shouldn't it be done on a copy of the doc ?
            Element el = null;
            if (instance.getNodeType() == Node.ELEMENT_NODE)
                el = (Element) instance;
            else if (instance.getNodeType() == Node.DOCUMENT_NODE)
                el = ((Document) instance).getDocumentElement();
            else {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("instance node type is: " + instance.getNodeType());
            }

            String prefix = NamespaceResolver.getPrefix(el, XMLSCHEMA_INSTANCE_NS);
            //test if with targetNamespace or not
            //if more than one schema : namespaces are mandatory ! (optional only for 1)
            StringTokenizer tokenizer = new StringTokenizer(schemas, " ", false);
            String schemaLocations = null;
            String noNamespaceSchemaLocation = null;
            while (tokenizer.hasMoreElements()) {
                String token = (String) tokenizer.nextElement();
                //check that it is an URL
                URI uri = null;
                try {
                    uri = new java.net.URI(token);
                } catch (java.net.URISyntaxException ex) {
                    if (LOGGER.isDebugEnabled())
                        LOGGER.debug(token + " is not an URI");
                }

                if (uri != null) {
                    String ns;
                    try {
                        ns = this.getSchemaNamespace(uri);

                        if (ns != null && !ns.equals("")) {
                            if (schemaLocations == null)
                                schemaLocations = ns + " " + token;
                            else
                                schemaLocations = schemaLocations + " " + ns + " " + token;

                            ///add the namespace declaration if it is not on the instance?
                            //TODO: how to know with which prefix ?
                            String nsPrefix = NamespaceResolver.getPrefix(el, ns);
                            if (nsPrefix == null) { //namespace not declared !
                                LOGGER.warn("SchemaValidator: targetNamespace " + ns + " of schema " + token + " is not declared in instance: declaring it as default...");
                                el.setAttributeNS(NamespaceConstants.XMLNS_NS,
                                        NamespaceConstants.XMLNS_PREFIX,
                                        ns);
                            }
                        } else if (noNamespaceSchemaLocation == null)
                            noNamespaceSchemaLocation = token;
                        else { //we have more than one schema without namespace
                            LOGGER.warn("SchemaValidator: There is more than one schema without namespace !");
                        }
                    } catch (Exception ex) {
                        LOGGER.warn("Exception while trying to load schema: " + uri.toString() + ": " + ex.getMessage(), ex);
                        //in case there was an exception: do nothing, do not set the schema
                    }
                }
            }
            //write schemaLocations found
            if (schemaLocations != null && !schemaLocations.equals(""))
                el.setAttributeNS(XMLSCHEMA_INSTANCE_NS, prefix + ":schemaLocation", schemaLocations);
            if (noNamespaceSchemaLocation != null)
                el.setAttributeNS(XMLSCHEMA_INSTANCE_NS, prefix + ":noNamespaceSchemaLocation", noNamespaceSchemaLocation);

            //save and parse the doc
            ValidationErrorHandler handler = null;
            File f;
            try {
                //save document
                f = File.createTempFile("instance", ".xml");
                f.deleteOnExit();
                TransformerFactory trFact = TransformerFactory.newInstance();
                Transformer trans = trFact.newTransformer();
                DOMSource source = new DOMSource(el);
                StreamResult result = new StreamResult(f);
                trans.transform(source, result);
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Validator.validateSchema: file temporarily saved in " + f.getAbsolutePath());

                //parse it with error handler to validate it
                handler = new ValidationErrorHandler();
                SAXParserFactory parserFact = SAXParserFactory.newInstance();
                parserFact.setValidating(true);
                parserFact.setNamespaceAware(true);
                SAXParser parser = parserFact.newSAXParser();
                XMLReader reader = parser.getXMLReader();

                //validation activated
                reader.setFeature("http://xml.org/sax/features/validation", true);
                //schema validation activated
                reader.setFeature("http://apache.org/xml/features/validation/schema", true);
                //used only to validate the schema, not the instance
                //reader.setFeature( "http://apache.org/xml/features/validation/schema-full-checking", true);
                //validate only if there is a grammar
                reader.setFeature("http://apache.org/xml/features/validation/dynamic", true);

                parser.parse(f, handler);
            } catch (Exception ex) {
                LOGGER.warn("Validator.validateSchema: Exception in XMLSchema validation: " + ex.getMessage(), ex);
                //throw new XFormsException("XMLSchema validation failed. "+message);
            }

            //if no exception
            if (handler != null && handler.isValid())
                valid = true;
            else {
                message = handler.getMessage();
                //TODO: find a way to get the error message displayed
                throw new XFormsException("XMLSchema validation failed. " + message);
            }

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("Validator.validateSchema: result=" + valid);

        }

        return valid;
    }

    /**
     * method to get the target namespace of a schema given by an URI:
     * it opens the schema at the given URI, anf looks for the "targetNamespace" attribute
     *
     * @param uri the URI of the schema
     * @return the targetNamespace of the schema, or null of none was found
     */
    private String getSchemaNamespace(URI uri) throws Exception {
        String ns = null;

        //load schema
        File schemaFile = new File(uri);
        Document doc = DOMUtil.parseXmlFile(schemaFile, true, false);
        if (doc != null) {
            Element schema = doc.getDocumentElement();
            ns = schema.getAttributeNS(XMLSCHEMA_NS, "targetNamespace");
            if (ns == null || ns.equals("")) //try without NS !
                ns = schema.getAttribute("targetNamespace");
        } else
            LOGGER.warn("Schema " + uri.toString() + " could not be parsed");

        if (LOGGER.isDebugEnabled())
            LOGGER.debug("SchemaValidator.getSchemaNamespace for schema " + uri.toString() + ": " + ns);

        return ns;
    }

    /**
     * SAX error handler for XMLSchema validation
     * TODO: transform Xerces error messages so that they are more "user-friendly"
     */
    class ValidationErrorHandler extends DefaultHandler {
        private boolean valid;
        private String message;

        public ValidationErrorHandler() {
            valid = true;
            message = null;
        }

        public void error(SAXParseException exception) throws SAXException {
            allErrors(exception);
        }

        public void fatalError(SAXParseException exception) throws SAXException {
            allErrors(exception);
        }

        public void warning(SAXParseException exception) throws SAXException {
            allErrors(exception);
        }

        public void allErrors(SAXParseException exception) throws SAXException {
            valid = false;
            if (message == null || message.equals(""))
                message = exception.getMessage();
            else
                message = message + "\n" + exception.getMessage();
            if (LOGGER.isDebugEnabled())
                LOGGER.debug("validation error: " + exception.getMessage());
        }

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }

}
