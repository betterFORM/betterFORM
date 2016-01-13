/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.xpath.saxon.function.extensions.BFSort;
import de.betterform.xml.xforms.xpath.saxon.function.xpath.*;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.functions.StandardFunction;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.Type;

import static net.sf.saxon.functions.StandardFunction.CORE;
import static net.sf.saxon.functions.StandardFunction.ONE;
import static net.sf.saxon.functions.StandardFunction.OPT;
import static net.sf.saxon.functions.StandardFunction.STAR;

/**
 * betterForm specific xpath functions for use from XForms
 */
public class BetterFormFunctionLibrary extends XPathFunctionLibrary {

    private static String functionNamespace = NamespaceConstants.BETTERFORM_NS;

    static {

        // locationPath function returns canonical XPath locationPath of context element
        registerBf("locationPath", LocationPath.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR, null);

        // instanceOfModel is analogous to instance function but returns instance of foreign model        
        registerBf("instanceOfModel", InstanceOfModel.class, 0, 2, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        registerBf("calcIPRange", CalcIPRange.class, 0, 2, 2, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null)
            .arg(1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        registerBf("isInIPRange", IsInIPRange.class, 0, 3, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null)
            .arg(1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null)
            .arg(2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        registerBf("sort", BFSort.class, 0, 2, 2, Type.ITEM_TYPE, STAR, CORE, 0)
            .arg(0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY)
            .arg(1, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE, null);

        registerBf("appContext", AppContext.class, 0, 1, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        registerBf("config", Config.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerBf("fileSize", FileSize.class, 0, 1, 1, BuiltInAtomicType.FLOAT, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerBf("fileDate", FileDate.class, 0, 1, 2, BuiltInAtomicType.STRING, ONE, CORE, 0)
                .arg(0, BuiltInAtomicType.STRING, ONE, null)
                .arg(1, BuiltInAtomicType.STRING, STAR, null);

        registerBf("match", Match.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, STAR, null);

        registerBf("props2xml", Props2XML.class, 0, 1, 1, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerBf("xml2props", XML2Props.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, ONE, null);

        registerBf("createAttributeNode", CreateAttributeNode.class, 0, 2, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        registerBf("schemareader", SchemaReader.class, 0, 1, 2, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        registerBf("schemachilds", SchemaChilds.class, 0, 1, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        registerBf("schemaelement", SchemaElement.class, 0, 2, 3, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null);

        registerBf("schemaenumeration", SchemaEnumeration.class, 0, 2, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);
    }

    /**
     * Adapted from {@link net.sf.saxon.functions.StandardFunction#register(String, Class, int, int, int, ItemType, int, int, int)}
     * so that we can place these functions into the betterForms specific namespace
     */
    private static StandardFunction.Entry registerBf(String name, Class implementationClass, int opcode, int minArguments, int maxArguments, ItemType itemType, int cardinality, int applicability, int properties) {
        name = bf(name);
        StandardFunction.Entry e = StandardFunction.makeEntry(name, implementationClass, opcode, minArguments, maxArguments,
                itemType, cardinality, applicability, properties);
        functionTable.put(name, e);
        return e;
    }

    private static String bf(final String localArity) {
        return "{" + NamespaceConstants.BETTERFORM_NS + "}" + localArity;
    }


    @Override
    protected String getFunctionNamespace() {
        return functionNamespace;
    }
}
