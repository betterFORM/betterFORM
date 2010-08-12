/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.xpath.saxon.function.xpath.Aggregate2;
import de.betterform.xml.xforms.xpath.saxon.function.xpath.Id2;
import de.betterform.xml.xforms.xpath.saxon.function.xpath.Minimax2;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.functions.*;
import net.sf.saxon.functions.Compare;
import net.sf.saxon.functions.Error;
import net.sf.saxon.functions.StandardFunction.Entry;
import net.sf.saxon.om.StandardNames;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.StringValue;

/**
 * This class contains static data tables defining the properties of XForms functions. "XForms functions" here means the
 * XForms 1.0 functions.
 */
public class BetterFormFunctionLibrary extends XPathFunctionLibrary {

    private static String functionNamespace = NamespaceConstants.BETTERFORM_NS;

    @Override
    protected String getFunctionNamespace() {
        return BetterFormFunctionLibrary.functionNamespace;
    }

    static {
        Entry e;
        // locationPath function returns canonical XPath locationPath of context element
        e = register("{" + NamespaceConstants.BETTERFORM_NS + "}locationPath", LocationPath.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);

        // instanceOfModel is analogous to instance function but returns instance of foreign model        
        e = register("{" + NamespaceConstants.XFORMS_NS + "}instanceOfModel", InstanceOfModel.class, 0, 2, 2, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);


    }
}
