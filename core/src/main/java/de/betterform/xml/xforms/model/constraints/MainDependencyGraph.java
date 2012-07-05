/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.constraints;

import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.model.bind.Bind;
import de.betterform.xml.xpath.impl.saxon.BetterFormXPathContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Node;

import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

/**
 * Implementation of XForms recalculation.
 *
 * Todo: The Dependency Engine desperately needs refactoring, then tuning.
 *
 * @author This code is based on the ideas of Mikko Honkala from the X-Smiles project.
 *         Although it has been heavily refactored and rewritten to meet our needs.
 * @author unl, joernt
 * @version $Id: MainDependencyGraph.java 3253 2008-07-08 09:26:40Z lasse $
 */
public class MainDependencyGraph extends DependencyGraph {
    /**
     * Logger
     */
    private static Log LOGGER = LogFactory.getLog(MainDependencyGraph.class);

    public MainDependencyGraph() {
        super();
    }

    /**
     * returns all Vertices
     *
     * @return all Vertices
     */
    public Vector getVertices() {
        return this.vertices;
    }

    /**
     * Adds a single bind's ref node to the Main Graph
     * called by MainDependencyGraph.buildBindGraph()
     */
    private void addReferredNodesToGraph(BetterFormXPathContext relativeContext, Node instanceNode,
                                         String expression, short property, Set references) throws XFormsException {
    	addReferredNodesToGraph(relativeContext, instanceNode, expression, property, references, null);
    }
    
    /**
     * Adds a single bind's ref node to the Main Graph
     * called by MainDependencyGraph.buildBindGraph()
     */
    private void addReferredNodesToGraph(BetterFormXPathContext relativeContext, Node instanceNode,
                                         String expression, short property, Set references, String customMIP) throws XFormsException {
        //creates a new vertex for this Node or return it, in case it already existed
    	//RKU
        Vertex vertex = this.addVertex(relativeContext, instanceNode, expression, property, customMIP);
        boolean hadVertex = vertex.wasAlreadyInGraph;
        vertex.wasAlreadyInGraph = false;

        // Analyze the Xpath Expression 'calculate'. Read nodeset RefNS
        // (the nodes this XPAth references)
        String xpath = vertex.getXPathExpression();

        if ((xpath == null) || (xpath.length() == 0)) {
            // bind without xpath, remove vertex
            if (!hadVertex) {
                this.removeVertex(vertex);
            }

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("addReferredNodesToGraph: ignoring vertex " + vertex + " without xpath");
            }

            return;
        }

        //Analyse xpath-expression to determine the Referenced dataitems
        Vector refns = this.getXPathRefNodes(relativeContext, xpath, references);

        if (refns == null) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("addReferredNodesToGraph: ignoring vertex " + vertex + " without references");
            }

            return;
        }

        if (refns.size() == 0) {
            // this is a calculated value, that is not depending on anything, let's calculate it now
            vertex.compute();
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("addReferredNodesToGraph: processing vertex " + vertex + " with " + refns.size() + " references");
        }

        Enumeration enumeration = refns.elements();

        while (enumeration.hasMoreElements()) {
            Node referencedNode = (Node) enumeration.nextElement();

            // pre-build vertex
            Vertex refVertex = this.addVertex(null, referencedNode, null, Vertex.CALCULATE_VERTEX, null);
            this.addEdge(refVertex, vertex);
        }
    }

    /**
     * builds the dependency graph for a single Bind. Processes all instancenodes that are associated with
     * the bind and creates one Vertex-object for every Modelitem Property found. That means that if there are
     * two instancenodes in the evaluated nodeset, two Vertices for every property (readonly, required, relevant,
     * constraint, calculate) will be created.
     * <p/>
     * Note: only dynamic Modelitem Properties will be processed.
     */
    public void buildBindGraph(Bind bind, Model model) throws XFormsException {
        Instance instance = model.getInstance(bind.getInstanceId());

        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("buildBindGraph: building " + bind);
        }

        
        final List nodeset = bind.getNodeset();
        for (int i = 0; i < nodeset.size(); i++) {
            BetterFormXPathContext relativeContext = new BetterFormXPathContext(nodeset, i + 1, bind.getPrefixMapping(), bind.getXPathFunctionContext());
            Node node = (Node) XPathUtil.getAsNode(nodeset, i + 1);
            ModelItem modelItem = instance.getModelItem(node);
            

            String property = bind.getCalculate();
            if (property != null) {
                modelItem.getDeclarationView().setCalculate(property);
                this.addReferredNodesToGraph(relativeContext, node, property, Vertex.CALCULATE_VERTEX, bind.getCalculateReferences());
            }

            property = bind.getRelevant();
            if (property != null) {
                modelItem.getDeclarationView().setRelevant(property);
                this.addReferredNodesToGraph(relativeContext, node, property, Vertex.RELEVANT_VERTEX, bind.getRelevantReferences());
            }

            property = bind.getReadonly();
            if (property != null) {
                modelItem.getDeclarationView().setReadonly(property);
                this.addReferredNodesToGraph(relativeContext, node, property, Vertex.READONLY_VERTEX, bind.getReadonlyReferences());
            }

            property = bind.getRequired();
            if (property != null) {
                modelItem.getDeclarationView().setRequired(property);
                this.addReferredNodesToGraph(relativeContext, node, property, Vertex.REQUIRED_VERTEX, bind.getRequiredReferences());
            }

            property = bind.getConstraint();
            if (property != null) {
                modelItem.getDeclarationView().setConstraint(property);
                this.addReferredNodesToGraph(relativeContext, node, property, Vertex.CONSTRAINT_VERTEX, bind.getConstraintReferences());
            }
            
            //RKU
            //property = bind.getCustomMIPs();
            Map<String, String> customMIPs = bind.getCustomMIPs();
            if (!customMIPs.isEmpty()) {
            	modelItem.getDeclarationView().setCustomMIPs(customMIPs);
            	for (String key : customMIPs.keySet()) {
                    
                    this.addReferredNodesToGraph(relativeContext, node, customMIPs.get(key), Vertex.CUSTOM_VERTEX, bind.getCustomMIPReferences(key), key);
				}
            }

            property = bind.getDatatype();
            if (property != null) {
                modelItem.getDeclarationView().setDatatype(property);
            }

            property = bind.getP3PType();
            if (property != null) {
                modelItem.getDeclarationView().setP3PType(property);
            }
        }
    }

    /**
     * Extends DependencyGraph.recalculate(). Restores the vertices vector after recalc
     */
/*
    public void recalculate() {
        this.printGraph();

        Vector tempvertices = (Vector) this.vertices.clone();
        super.recalculate();
        this.vertices = tempvertices;

        //		calculated=true;
    }
*/
}
