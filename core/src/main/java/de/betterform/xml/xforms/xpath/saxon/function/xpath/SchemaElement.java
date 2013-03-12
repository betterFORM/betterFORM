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
import org.apache.xerces.impl.xs.XSParticleDecl;
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
public class SchemaElement extends XFormsFunction {
    private static final Log LOGGER = LogFactory.getLog(SchemaElement.class);
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


    private Document getContextNode(String namespace, String elementName, String parent, XSModel xsModel) {
        try {
            Iterator<XSNamespaceItem> xsNamespaceItemList = xsModel.getNamespaceItems().listIterator();

            //Search schema with correct namespace
            while (xsNamespaceItemList.hasNext()) {
                String schemaNamespace = xsNamespaceItemList.next().getSchemaNamespace();
                if (namespace.equals(schemaNamespace)) {
                    if (parent != null) {
                        XSObject xsObject = findChild(namespace, elementName, parent, xsModel);
                        if (xsObject != null) {
                            if (xsObject instanceof XSElementDeclaration) {
                                return handleElement((XSElementDeclaration) xsObject);
                            } else if (xsObject instanceof XSModelGroupDefinition) {
                                return handleModelGroup((XSModelGroupDefinition) xsObject);
                            }
                        }
                    } else {
                        XSElementDeclaration xsElementDeclaration = xsModel.getElementDeclaration(elementName, namespace);
                        if (xsElementDeclaration != null) {
                            return handleElement(xsElementDeclaration);
                        }
                        XSModelGroupDefinition xsModelGroupDefinition = xsModel.getModelGroupDefinition(elementName, namespace);
                        if (xsModelGroupDefinition != null) {
                            return handleModelGroup(xsModelGroupDefinition);
                        }
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    private XSObject findChild(String namespace, String elementName, String parent, XSModel xsModel) {
        XSElementDeclaration xsElementDeclaration = xsModel.getElementDeclaration(parent, namespace);
        
        if (xsElementDeclaration != null) {
            XSTypeDefinition xsTypeDefinition = xsElementDeclaration.getTypeDefinition();
            if (xsTypeDefinition != null) {
                if (xsTypeDefinition instanceof XSComplexTypeDefinition) {
                    XSComplexTypeDefinition xsComplexTypeDefinition = (XSComplexTypeDefinition) xsTypeDefinition;
                    return searchComplextype(xsComplexTypeDefinition, elementName);
                } else if (xsTypeDefinition instanceof XSComplexTypeDecl) {
                    //Do nothing.
                } else if (xsTypeDefinition instanceof XSSimpleTypeDefinition) {
                    //handleSimpleType((XSSimpleTypeDefinition) xsTypeDefinition, "");
                }
            }
        }
        XSModelGroupDefinition xsModelGroupDefinition = xsModel.getModelGroupDefinition(parent, namespace);
        if (xsModelGroupDefinition != null) {
               return searchModelChild(xsModelGroupDefinition, elementName);
        }

        return null;
    }
    
    private XSObject searchComplextype(XSComplexTypeDefinition xsComplexTypeDefinition, String elementName) {

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
                        if (elementName.equals(xsElementDeclaration.getName())) {
                            return xsElementDeclaration;
                        }
                    }  else if (particleTerm instanceof XSModelGroup) {
                        XSModelGroup group = (XSModelGroup) particleTerm;

                        switch (group.getCompositor()) {
                            case XSModelGroup.COMPOSITOR_SEQUENCE :
                                XSObjectList groupParticles = group.getParticles();
                                for (int z = 0; z < groupParticles.getLength(); z++)
                                {
                                    XSParticle groupParticle = (XSParticle) groupParticles.item(z);
                                    XSTerm groupParticleTerm = groupParticle.getTerm();
                                    if (elementName.equals(groupParticleTerm.getName())) {
                                        return groupParticleTerm;
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

        return null;
    }
    
    private XSObject searchModelChild(XSModelGroupDefinition xsModelGroupDefinition, String elementName) {
        for (Object object : xsModelGroupDefinition.getModelGroup().getParticles()) {
            XSObject xsObject = ((XSParticleDecl) object).getTerm();
            if (elementName.equals(xsObject.getName())) {
                return xsObject;
            }
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
                //Do nothing.
            } else if (xsTypeDefinition instanceof XSSimpleTypeDefinition) {
                //handleSimpleType((XSSimpleTypeDefinition) xsTypeDefinition, "");
            }
        }
        return document;
    }

    private void handleComplexType(XSComplexTypeDefinition xsComplexTypeDefinition, Document document, Element rootElement) {
        for (Object object : xsComplexTypeDefinition.getAttributeUses()) {
            XSAttributeUse xsAttributeUse = (XSAttributeUse) object;
            rootElement.setAttributeNS(xsAttributeUse.getAttrDeclaration().getNamespace(), xsAttributeUse.getAttrDeclaration().getName(), "");
        }
    }


    private Document handleModelGroup(XSModelGroupDefinition xsModelGroupDefinition) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setValidating(false);
        Document document = factory.newDocumentBuilder().newDocument();

        document.appendChild(document.createElementNS(xsModelGroupDefinition.getNamespace(), xsModelGroupDefinition.getName()));
        Element rootElement = document.getDocumentElement();

        for (Object object : xsModelGroupDefinition.getModelGroup().getParticles()) {
            XSObject xsObject = (XSObject) object;
            System.out.println(xsObject.getNamespace() + "," + xsObject.getName());
        }
        return document;
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
                String namespace = null;
                if (argument[0] != null) {
                     namespace = argument[0].evaluateAsString(xpathContext).toString();
                }
                String elementName = argument[1].evaluateAsString(xpathContext).toString();

                String parent = null;
                if (argument.length >= 3 && argument[2] != null) {
                    parent = argument[2].evaluateAsString(xpathContext).toString();
                }
                //Get all to model know schemas
                List<XSModel> schemas = container.getDefaultModel().getSchemas();

                //Iterate over all schemas till we find a first match.
                Iterator<XSModel> it = schemas.iterator();
                while (it.hasNext()) {
                    XSModel xsModel = it.next();
                    Document elementDocument = getContextNode(namespace, elementName, parent, xsModel);

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
