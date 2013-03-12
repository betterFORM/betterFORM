package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.ListIterator;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
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
public class SchemaChilds extends XFormsFunction {
    private static final Log LOGGER = LogFactory.getLog(SchemaChilds.class);
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
                    XSElementDeclaration xsElementDeclaration = xsModel.getElementDeclaration(elementName, namespace);
                    if (xsElementDeclaration != null) {
                        return handleElement(xsElementDeclaration);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    private Document handleElement(XSElementDeclaration xsElementDeclaration) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        Document document = factory.newDocumentBuilder().newDocument();
        document.appendChild(document.createElementNS(xsElementDeclaration.getNamespace(), xsElementDeclaration.getName()));
        Element rootElement = document.getDocumentElement();


        XSTypeDefinition xsTypeDefinition = xsElementDeclaration.getTypeDefinition();
        if (xsTypeDefinition != null) {
            if (xsTypeDefinition instanceof XSComplexTypeDefinition) {
                handleComplexType((XSComplexTypeDefinition) xsTypeDefinition, document, rootElement);
            } else if (xsTypeDefinition instanceof XSComplexTypeDecl) {
                //Do nothing
            } else if (xsTypeDefinition instanceof XSSimpleTypeDefinition) {
                //handleSimpleType((XSSimpleTypeDefinition) xsTypeDefinition, "");
            }
        }
        return document;
    }

    private void handleComplexType(XSComplexTypeDefinition xsComplexTypeDefinition, Document document, Element rootElement) {
        if (xsComplexTypeDefinition.getParticle() != null) {
            XSTerm term = xsComplexTypeDefinition.getParticle().getTerm();
            if (term instanceof XSModelGroup)
            {
                XSObjectList particles = ((XSModelGroup) term).getParticles();
                for (int j = 0; j < particles.getLength(); j++)
                {
                    XSParticle particle = (XSParticle) particles.item(j);
                    XSTerm particleTerm = particle.getTerm();
                    if (particleTerm instanceof XSElementDeclaration)
                    {
                        XSElementDeclaration  xsElementDeclaration = (XSElementDeclaration) particleTerm;
                        Element childElement = document.createElementNS(xsElementDeclaration.getNamespace(), xsElementDeclaration.getName());
                        rootElement.appendChild(childElement);
                    }  else if (particleTerm instanceof XSModelGroup) {
                        XSModelGroup group = (XSModelGroup) particleTerm;

                        switch (group.getCompositor()) {
                            case XSModelGroup.COMPOSITOR_SEQUENCE :
                                XSObjectList groupParticles = group.getParticles();
                                for (int z = 0; z < groupParticles.getLength(); z++)
                                {
                                    XSParticle groupParticle = (XSParticle) groupParticles.item(z);
                                    XSTerm groupParticleTerm = groupParticle.getTerm();
                                    if (groupParticleTerm instanceof XSElementDeclaration)
                                    {
                                        Element childElement = document.createElementNS(groupParticleTerm.getNamespace(), groupParticleTerm.getName());
                                        rootElement.appendChild(childElement);
                                    } else if (groupParticleTerm instanceof XSSimpleTypeDefinition) {
                                        //TODO
                                    } else if (groupParticleTerm instanceof XSComplexTypeDefinition) {
                                        //TODO
                                    } else if (groupParticleTerm instanceof XSModelGroup) {
                                        //TODO
                                    }
                                }
                                break;
                            case XSModelGroup.COMPOSITOR_CHOICE :
                                break;
                            case XSModelGroup.COMPOSITOR_ALL :
                                break;
                            default :
                                LOGGER.warn("SchemaReader: unknown Compositor in schema: " + ((XSModelGroup) term).getCompositor());
                        }
                    }
                }
            }
        }
    }

    /*
    * XPath function
    */

    public Expression preEvaluate(ExpressionVisitor visitor) throws XPathException {
        return this;
    }

    public SequenceIterator iterate(XPathContext xpathContext) throws XPathException
    {
        Container container = getContainer(xpathContext);

        if (container != null) {
            try {
                //Get name of searched Element
                String namespace = argument[0].evaluateAsString(xpathContext).toString();
                String elementName = argument[1].evaluateAsString(xpathContext).toString();
                //Get all to model know schemas
                List<XSModel> schemas = container.getDefaultModel().getSchemas();

                //Iterate over all schemas till we find a first match.
                Iterator<XSModel> it = schemas.iterator();
                while (it.hasNext()) {
                    XSModel xsModel = it.next();
                    Document elementDocument = getContextNode(namespace, elementName, xsModel);

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
