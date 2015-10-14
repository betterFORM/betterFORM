/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.xforms.xpath.saxon.function.extensions.BFSort;
import de.betterform.xml.xforms.xpath.saxon.function.xpath.*;
import net.sf.saxon.expr.StaticProperty;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.Type;

import static net.sf.saxon.functions.StandardFunction.CORE;
import static net.sf.saxon.functions.StandardFunction.ONE;
import static net.sf.saxon.functions.StandardFunction.OPT;
import static net.sf.saxon.functions.StandardFunction.STAR;

/**
 * This class contains static data tables defining the properties of XForms functions. "XForms functions" here means the
 * XForms 1.0 functions.
 */
public class BetterFormFunctionLibrary extends XPathFunctionLibrary {

    private static String functionNamespace = NamespaceConstants.BETTERFORM_NS;

    @Override
    protected String getFunctionNamespace() {
        return functionNamespace;
    }

    static {

        // locationPath function returns canonical XPath locationPath of context element
        register("{" + NamespaceConstants.BETTERFORM_NS + "}locationPath", LocationPath.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR, null);

        // instanceOfModel is analogous to instance function but returns instance of foreign model        
        register("{" + NamespaceConstants.BETTERFORM_NS + "}instanceOfModel", InstanceOfModel.class, 0, 2, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}calcIPRange", CalcIPRange.class, 0, 2, 2, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null)
            .arg(1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}isInIPRange", IsInIPRange.class, 0, 3, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null)
            .arg(1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null)
            .arg(2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}sort", BFSort.class, 0, 2, 2, Type.ITEM_TYPE, STAR, CORE, 0)
            .arg(0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY)
            .arg(1, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}appContext", AppContext.class, 0, 1, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}config", Config.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}fileSize", FileSize.class, 0, 1, 1, BuiltInAtomicType.FLOAT, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}fileDate", FileDate.class, 0, 1, 2, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, STAR, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}match", Match.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, STAR, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}props2xml", Props2XML.class, 0, 1, 1, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}xml2props", XML2Props.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}createAttributeNode", CreateAttributeNode.class, 0, 2, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}schemareader", SchemaReader.class, 0, 1, 2, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}schemachilds", SchemaChilds.class, 0, 1, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}schemaelement", SchemaElement.class, 0, 2, 3, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null);

        register("{" + NamespaceConstants.BETTERFORM_NS + "}schemaenumeration", SchemaEnumeration.class, 0, 2, 2, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);
    }
}
