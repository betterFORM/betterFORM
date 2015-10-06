package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.expr.parser.ExpressionVisitor;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.om.SequenceTool;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.tree.iter.ListIterator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.xs.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 * betterFORM Project
 * User: Tobi Krebs
 * Date: 15.03.12
 * Time: 13:33
 */

/* Unneeded for now

    private XSModel loadSchema(File schema) throws Exception {
        LSInput input = new DOMInputImpl();
        FileInputStream schemaStream = new FileInputStream(schema);
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        XSImplementation implementation = (XSImplementation) registry.getDOMImplementation("XS-Loader");
        XSLoader loader = implementation.createXSLoader(null);
        input.setByteStream(schemaStream);
        XSModel xsModel = loader.load(input);
        schemaStream.close();
        return xsModel;
    }
    
    private XSModel getSchema(File schema) throws XPathException {
        XSModel xsModel = (XSModel) CacheManager.getElementFromFileCache(schema); 
        if ( xsModel == null) {
            try {
             xsModel = loadSchema(schema);
            } catch (Exception e) {
                throw new XPathException("SchemaReader.getSchema:", e);
            }
            CacheManager.putIntoFileCache(schema, xsModel);
        }
        
        return xsModel;
    }

*/
public class SchemaEnumeration extends XFormsFunction {
    private static final Log LOGGER = LogFactory.getLog(SchemaEnumeration.class);
    /*
     * Schemahandling: TODO: move to seperate Class so we can create more XPathFunctions
     */
    private XSTypeDefinition getTypeDefinition(XSModel schemaModel, String name, String namespace) {
        XSNamedMap map = schemaModel.getComponents(XSConstants.TYPE_DEFINITION);

        for (int j=0; j<map.getLength(); j++) {
            XSTypeDefinition xsTypeDefinition = (XSTypeDefinition) map.item(j);

            if (xsTypeDefinition.getName().equals(name)) {
                //No namespace specified return first "name" matching typedefinition
                if (namespace == null) {
                    return xsTypeDefinition;
                    //namespace specified return first "name" and "namespace" matching typedefinition
                } else  if (xsTypeDefinition.getNamespace().equals(namespace)) {
                    return xsTypeDefinition;
                }
            }
        }

        return null;
    }

    private Document getContextNode(String namespace, String elementName, XSModel xsModel) {
        try {
            Iterator<XSNamespaceItem> xsNamespaceItemList = xsModel.getNamespaceItems().listIterator();

            //Search schema with correct namespace
            while (xsNamespaceItemList.hasNext()) {
                String schemaNamespace = xsNamespaceItemList.next().getSchemaNamespace();
                if (namespace.equals(schemaNamespace)) {
                    XSTypeDefinition xsTypeDefinition =  xsModel.getTypeDefinition(elementName, namespace);
                    if (xsTypeDefinition != null) {
                        LOGGER.debug("Do something.");
                        if (xsTypeDefinition instanceof XSSimpleTypeDecl) {
                            XSSimpleTypeDecl xsSimpleTypeDecl = (XSSimpleTypeDecl) xsTypeDefinition;
                            StringList lexicalEnumeration = xsSimpleTypeDecl.getLexicalEnumeration();
                            if (lexicalEnumeration.getLength() > 0) {
                                DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                                factory.setNamespaceAware(true);
                                factory.setValidating(false);
                                Document document = factory.newDocumentBuilder().newDocument();
                                document.appendChild(document.createElementNS(namespace, elementName));
                                Element rootElement = document.getDocumentElement();

                                for (int i = 0; i < lexicalEnumeration.getLength(); i++) {
                                    Element childElement = document.createElementNS(namespace, lexicalEnumeration.item(i));
                                    childElement.setTextContent(lexicalEnumeration.item(i));
                                    rootElement.appendChild(childElement);
                                }

                                return document;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /*
    * XPath function
    */
    @Override
    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    @Override
    public SequenceIterator iterate(final XPathContext xpathContext) throws XPathException {
        //Get name of searched Element
        final String namespace = argument[0].evaluateAsString(
            xpathContext).toString();
        final String elementName = argument[1].evaluateAsString(
            xpathContext).toString();

        return schemaEnumeration(xpathContext, namespace, elementName);
    }

    public Sequence call(final XPathContext context,
                         final Sequence[] arguments) throws XPathException {
        final String namespace = arguments[0].head().getStringValue();
        final String elementName = arguments[1].head().getStringValue();

        return SequenceTool.toLazySequence(schemaEnumeration(context, namespace, elementName));
    }

    private SequenceIterator schemaEnumeration(final XPathContext context, final String namespace, final String elementName) throws XPathException {
        final Container container = getContainer(context);

        if (container != null) {
            try {
                //Get all to model know schemas
                final List<XSModel> schemas = container.getDefaultModel().getSchemas();

                //Iterate over all schemas till we find a first match.
                final Iterator<XSModel> it = schemas.iterator();
                while (it.hasNext()) {
                    final XSModel xsModel = it.next();
                    final Document elementDocument = getContextNode(namespace, elementName, xsModel);

                    if (LOGGER.isDebugEnabled() && elementDocument != null) {
                        DOMUtil.prettyPrintDOM(elementDocument.getDocumentElement());
                    }

                    if (elementDocument != null) {
                        String baseURI = container.getProcessor().getBaseURI();
                        return new ListIterator(de.betterform.xml.xpath.impl.saxon.XPathUtil.getRootContext(elementDocument,baseURI));
                    }
                }


                //XSModel xsModel = getSchema(new File(schemaFile));
            } catch (Exception e) {
                throw new XPathException(e);
            }
        }

        return new ListIterator(Collections.EMPTY_LIST);
    }



}
