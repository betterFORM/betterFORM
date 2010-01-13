/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.xforms.model.ModelItem;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * State keeper for copy elements.
 *
 * @author Adam Retter <adam.retter@devon.gov.uk>
 * @version $Id: CopyElementState.java 2873 2007-09-28 09:08:48Z lars $
 */
public class CopyElementState extends BoundElementState
{
	public CopyElementState()
	{
		super();
	}
	
	/**
     * stores an XML subtree as child of the bf:data element for controls that work with such
     * a structure instead of a simple value. This is especially interesting for e.g. output/@appearance="dojo:tree"
     * which allows to visualize an abritrary piece of an instance data tree.
     *
     * Based on BoundElementState.storeSubtree but doesnt check for the presence of a child element
     *
     * @param modelItem the modelItem which has a subtree as value
     * @return The appended node or null
     */
    protected final Node storeSubtree(ModelItem modelItem)
    {
        Object o = modelItem.getNode();
        if(o instanceof Element)
        {
            Node n = (Node)modelItem.getNode();
            if(n != null)
            {
                Node imported = state.getOwnerDocument().importNode(n,true);
                return state.appendChild(imported);
            }
        }
        return null;
    }
}
