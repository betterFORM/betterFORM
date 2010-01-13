/* Copyright 2008 - Joern Turner, Lars Windauer */

package de.betterform.xml.xforms.xpath.saxon.function.xpath;

import net.sf.saxon.Configuration;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.ExpressionVisitor;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.om.ArrayIterator;
import net.sf.saxon.om.EmptyIterator;
import net.sf.saxon.om.Item;
import net.sf.saxon.om.SequenceIterator;
import net.sf.saxon.trans.XPathException;
import de.betterform.xml.xforms.XFormsElement;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.bind.Binding;
import de.betterform.xml.xforms.model.bind.BindingResolver;
import de.betterform.xml.xforms.xpath.saxon.function.XFormsFunction;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;

import java.util.*;

/**
 * Implementation of 7.10.3 The id() Function
 * <p/>
 * node-set id(object, node-set?)
 * 
 * The object parameter provides one or more IDREFs. This may be in the form of
 * a string containing a space-separated list of IDREFs or a node-set, each node
 * of which contains an IDREF. The node-set parameter provides nodes in one or
 * more instance data documents to be searched. If the node-set parameter is not
 * given or is empty, then the instance data document to be searched is the one
 * containing the context node of the function call. For each node in the
 * node-set parameter (or its default), the set of element nodes are collected
 * with IDs that match the IDREFs from the object parameter. The result of this
 * function is a node-set containing the union of the collected element nodes
 * from each string. An element node can be assigned an ID by means of an xml:id
 * attribute or an attribute that is assigned the type ID by a DTD or xsd:ID or
 * any type derived from xsd:ID by an XML schema, or the type model item
 * property.
 * 
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class Id2 extends XFormsFunction {
	/**
     * 
     */
	private static final long serialVersionUID = -4093826587796615212L;

	/**
	 * Get the static properties of this expression (other than its type). The
	 * result is bit-signficant. These properties are used for optimizations. In
	 * general, if property bit is set, it is true, but if it is unset, the
	 * value is unknown.
	 */

	public int computeSpecialProperties() {
		return StaticProperty.NON_CREATIVE;
	}

	/**
	 * Pre-evaluate a function at compile time. Functions that do not allow
	 * pre-evaluation, or that need access to context information, can override
	 * this method.
	 * 
	 * @param visitor
	 *            an expression visitor
	 * @return the result of the early evaluation, or the original expression,
	 *         or potentially a simplified expression
	 */

	public Expression preEvaluate(ExpressionVisitor visitor)
			throws XPathException {
		return this;
	}

	/**
	 * Iterate over the contents of the collection
	 * 
	 * @param context
	 *            the dynamic context
	 * @return an iterator, whose items will always be nodes (typically but not
	 *         necessarily document nodes)
	 * @throws XPathException
	 */

	@SuppressWarnings("unchecked")
	public SequenceIterator iterate(final XPathContext xpathContext)
			throws XPathException {
		final String ids = argument[0].evaluateAsString(xpathContext)
				.toString();
		final SequenceIterator nodeIterator = argument.length > 1 ? argument[1]
				.iterate(xpathContext) : null;

		final XPathFunctionContext functionContext = getFunctionContext(xpathContext);
		final XFormsElement xformsElement = functionContext.getXFormsElement();

		ArrayList<Item> resultList = new ArrayList<Item>();

		Item item = (nodeIterator != null)?nodeIterator.next():null;
		if (argument.length == 1 || item == null) {
			final Binding binding;
            if (xformsElement instanceof Binding) {
                binding = (Binding) xformsElement;
            }
            else {
                binding = xformsElement.getEnclosingBinding(xformsElement, false);
            }
            try {
				getElementsByIDs(ids, xformsElement.getModel().getInstance(binding.getInstanceId()).getInstanceDocument().getDocumentElement()
						, resultList, xformsElement.getContainerObject().getConfiguration());
			} catch (XFormsException e) {
				throw new XPathException(e);
			}
		} else {
			do {
				getElementsByIDs(ids, XPathUtil.getAsNode(Collections
						.singletonList(item), 1), resultList, xformsElement
						.getContainerObject().getConfiguration());
			} while ((item = nodeIterator.next()) != null);
		}

		if (resultList.isEmpty()) {
			return EmptyIterator.getInstance();
		}

		final Item[] result = new Item[resultList.size()];
		return new ArrayIterator(resultList.toArray(result));
	}

	private void getElementsByIDs(String ids, Node node, ArrayList<Item> result, Configuration configuration) {
		// Naive non-performant implementation
		
		final Set<String> idSet = new HashSet<String>(Arrays.asList(ids.split(" ")));
		
		final DocumentTraversal traversable = (DocumentTraversal) node.getOwnerDocument();
	    final NodeIterator iterator = traversable.createNodeIterator(node, NodeFilter.SHOW_ELEMENT, null, true);
	    
	    Node it;
	    while ((it = iterator.nextNode()) != null) {
    		final Element el = (Element)it;
			if ((el.hasAttributeNS("http://www.w3.org/XML/1998/namespace", "id") && idSet.contains(el.getAttribute("xml:id"))) 
					|| (el.hasAttributeNS("http://www.w3.org/XMLSchema-instance", "type") && "xsd:ID".equals(el.getAttributeNS("http://www.w3.org/XMLSchema-instance", "type")) && idSet.contains(el.getTextContent()))) {
				result.add(new DocumentWrapper(el.getOwnerDocument(), "dummy", configuration).wrap(el));
    		}
	    }
	}
}
