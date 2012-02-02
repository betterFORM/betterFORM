/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
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
public class XFormsFunctionLibrary extends XPathFunctionLibrary {

    private static String functionNamespace = NamespaceConstants.XFORMS_NS;

    @Override
    protected String getFunctionNamespace() {
        return XFormsFunctionLibrary.functionNamespace;
    }

    static {
        Entry e;

        e = register("{" + NamespaceConstants.XFORMS_NS + "}boolean-from-string", BooleanFromString.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}is-card-number", IsCardNumber.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}count-non-empty", CountNonEmpty.class, 0, 1, 1, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}current", Current.class, 0, 0, 0, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}IF", If.class, 0, 3, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}instance", Instance.class, 0, 0, 1, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}index", Index.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}power", Power.class, 0, 2, 2, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}random", Random.class, 0, 0, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}compare", Compare.class, 0, 2, 2, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}property", Property.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}digest", Digest.class, 0, 2, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}hmac", Hmac.class, 0, 3, 4, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}local-date", LocalDate.class, 0, 0, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}local-dateTime", LocalDateTime.class, 0, 0, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}now", Now.class, 0, 0, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}days-from-date", DaysFromDate.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}days-to-date", DaysToDate.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}seconds-from-dateTime", SecondsFromDateTime.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}seconds-to-dateTime", SecondsToDateTime.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}adjust-dateTime-to-timezone", AdjustDateTimeToTimezone.class, 0, 0, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}seconds", Seconds.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}months", Months.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}choose", Choose.class, 0, 3, 3, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 1, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 2, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}context", Context.class, 0, 0, 0, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}event", Event.class, 0, 1, 1, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}id", Id2.class, 0, 1, 2, NodeKindTest.ELEMENT, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 1, Type.NODE_TYPE, StaticProperty.ALLOWS_ONE_OR_MORE, null);

        //Adapted xpath 2.0 functions
        e = register("{" + NamespaceConstants.XFORMS_NS + "}avg", Aggregate2.class, Aggregate2.AVG, 1, 1, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE);
        // can't say "same as first argument" because the avg of a set of
        // integers is decimal
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}max", Minimax2.class, Minimax2.MAX, 1, 2, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}min", Minimax2.class, Minimax2.MIN, 1, 2, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        // Standard XPath functions
        e = register("{" + NamespaceConstants.XFORMS_NS + "}abs", Rounding.class, Rounding.ABS, 1, 1, SAME_AS_FIRST_ARGUMENT, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}adjust-date-to-timezone", Adjust.class, 0, 1, 2, BuiltInAtomicType.DATE, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 1, BuiltInAtomicType.DAY_TIME_DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

//                        e = register("{" + NamespaceConstants.XFORMS_NS + "}adjust-dateTime-to-timezone", Adjust.class, 0, 1, 2, BuiltInAtomicType.DATE_TIME,
//                            StaticProperty.ALLOWS_ZERO_OR_ONE);
//                        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
//                        arg(e, 1, BuiltInAtomicType.DAY_TIME_DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}adjust-time-to-timezone", Adjust.class, 0, 1, 2, BuiltInAtomicType.TIME, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 1, BuiltInAtomicType.DAY_TIME_DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

//                        e = register("{" + NamespaceConstants.XFORMS_NS + "}avg", Aggregate.class, Aggregate.AVG, 1, 1, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE);
//                        // can't say "same as first argument" because the avg of a set of
//                        // integers is decimal
//                        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}base-uri", BaseURI.class, 0, 0, 1, BuiltInAtomicType.ANY_URI, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}boolean", BooleanFn.class, BooleanFn.BOOLEAN, 1, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}ceiling", Rounding.class, Rounding.CEILING, 1, 1, SAME_AS_FIRST_ARGUMENT, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}codepoint-equal", CodepointEqual.class, 0, 2, 2, BuiltInAtomicType.BOOLEAN, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}codepoints-to-string", CodepointsToString.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.INTEGER, StaticProperty.ALLOWS_ZERO_OR_MORE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}collection", Collection.class, 0, 0, 1, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}compare", Compare.class, 0, 2, 3, BuiltInAtomicType.INTEGER, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}concat", Concat.class, 0, 2, Integer.MAX_VALUE, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        // Note, this has a variable number of arguments so it is treated
        // specially

        e = register("{" + NamespaceConstants.XFORMS_NS + "}contains", Contains.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, BooleanValue.TRUE);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}count", Aggregate2.class, Aggregate2.COUNT, 1, 1, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, Int64Value.ZERO);

        // XForms has special current version
        // register("{" + NamespaceConstants.XFORMS_NS + "}current", Current.class, 0, 0, 0, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE);
        register("{" + NamespaceConstants.XFORMS_NS + "}current-date", CurrentDateTime.class, 0, 0, 0, BuiltInAtomicType.DATE, StaticProperty.EXACTLY_ONE);
        register("{" + NamespaceConstants.XFORMS_NS + "}current-dateTime", CurrentDateTime.class, 0, 0, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.EXACTLY_ONE);
        register("{" + NamespaceConstants.XFORMS_NS + "}current-time", CurrentDateTime.class, 0, 0, 0, BuiltInAtomicType.TIME, StaticProperty.EXACTLY_ONE);

        register("{" + NamespaceConstants.XFORMS_NS + "}current-group", CurrentGroup.class, CurrentGroup.CURRENT_GROUP, 0, 0, Type.ITEM_TYPE,
                StaticProperty.ALLOWS_ZERO_OR_MORE);
        register("{" + NamespaceConstants.XFORMS_NS + "}current-grouping-key", CurrentGroup.class, CurrentGroup.CURRENT_GROUPING_KEY, 0, 0, BuiltInAtomicType.ANY_ATOMIC,
                StaticProperty.ALLOWS_ZERO_OR_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}data", Data.class, 0, 1, 1, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}dateTime", DateTimeConstructor.class, 0, 2, 2, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 1, BuiltInAtomicType.TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}day-from-date", Component.class, (Component.DAY << 16) + StandardNames.XS_DATE, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}day-from-dateTime", Component.class, (Component.DAY << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}days-from-duration", Component.class, (Component.DAY << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}deep-equal", DeepEqual.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, null);
        arg(e, 1, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        register("{" + NamespaceConstants.XFORMS_NS + "}default-collation", DefaultCollation.class, 0, 0, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}distinct-values", DistinctValues.class, 0, 1, 2, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}doc", Doc.class, 0, 1, 1, NodeKindTest.DOCUMENT, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}doc-available", DocAvailable.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, BooleanValue.FALSE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}document", Document.class, 0, 1, 2, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, null);
        arg(e, 1, Type.NODE_TYPE, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}document-uri", NamePart.class, NamePart.DOCUMENT_URI, 1, 1, BuiltInAtomicType.ANY_URI,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}empty", Existence.class, Existence.EMPTY, 1, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, BooleanValue.TRUE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}ends-with", EndsWith.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, BooleanValue.TRUE);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}element-available", Available.class, Available.ELEMENT_AVAILABLE, 1, 1, BuiltInAtomicType.BOOLEAN,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}encode-for-uri", EscapeURI.class, EscapeURI.ENCODE_FOR_URI, 1, 1, BuiltInAtomicType.STRING,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}escape-html-uri", EscapeURI.class, EscapeURI.HTML_URI, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}error", Error.class, 0, 0, 3, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE);
        // The return type is chosen so that use of the error() function
        // will never give a static type error.
        arg(e, 0, BuiltInAtomicType.QNAME, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, null);

        // e = register("{" + NamespaceConstants.XFORMS_NS + "}escape-uri", EscapeURI.class, EscapeURI.ESCAPE, 2,
        // 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        // arg(e, 0, BuiltInAtomicType.STRING,
        // StaticProperty.ALLOWS_ZERO_OR_ONE);
        // arg(e, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}exactly-one", TreatFn.class, StaticProperty.EXACTLY_ONE, 1, 1, SAME_AS_FIRST_ARGUMENT, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE, null);
        // because we don't do draconian static type checking, we can do the
        // work in the argument type checking code

        e = register("{" + NamespaceConstants.XFORMS_NS + "}exists", Existence.class, Existence.EXISTS, 1, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, BooleanValue.FALSE);

        register("{" + NamespaceConstants.XFORMS_NS + "}false", BooleanFn.class, BooleanFn.FALSE, 0, 0, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}floor", Rounding.class, Rounding.FLOOR, 1, 1, SAME_AS_FIRST_ARGUMENT, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}format-date", FormatDate.class, StandardNames.XS_DATE, 2, 5, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.DATE, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 3, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 4, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}format-dateTime", FormatDate.class, StandardNames.XS_DATE_TIME, 2, 5, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 3, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 4, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}format-number", FormatNumber.class, 0, 2, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}format-time", FormatDate.class, StandardNames.XS_TIME, 2, 5, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 3, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 4, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}function-available", Available.class, Available.FUNCTION_AVAILABLE, 1, 2, BuiltInAtomicType.BOOLEAN,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 1, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}generate-id", NamePart.class, NamePart.GENERATE_ID, 0, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}hours-from-dateTime", Component.class, (Component.HOURS << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}hours-from-duration", Component.class, (Component.HOURS << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}hours-from-time", Component.class, (Component.HOURS << 16) + StandardNames.XS_TIME, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

//            e = register("{" + NamespaceConstants.XFORMS_NS + "}id", Id.class, 0, 1, 2, NodeKindTest.ELEMENT, StaticProperty.ALLOWS_ZERO_OR_MORE);
//            arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
//            arg(e, 1, Type.NODE_TYPE, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}idref", Idref.class, 0, 1, 2, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 1, Type.NODE_TYPE, StaticProperty.EXACTLY_ONE, null);

        register("{" + NamespaceConstants.XFORMS_NS + "}implicit-timezone", CurrentDateTime.class, 0, 0, 0, BuiltInAtomicType.DAY_TIME_DURATION, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}in-scope-prefixes", InScopePrefixes.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, NodeKindTest.ELEMENT, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}index-of", IndexOf.class, 0, 2, 3, BuiltInAtomicType.INTEGER, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 1, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}insert-before", Insert.class, 0, 3, 3, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, null);
        arg(e, 1, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}iri-to-uri", EscapeURI.class, EscapeURI.IRI_TO_URI, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}key", KeyFn.class, 0, 2, 3, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 1, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 2, Type.NODE_TYPE, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}lang", Lang.class, 0, 1, 2, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, Type.NODE_TYPE, StaticProperty.EXACTLY_ONE, null);

        register("{" + NamespaceConstants.XFORMS_NS + "}last", Last.class, 0, 0, 0, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}local-name", NamePart.class, NamePart.LOCAL_NAME, 0, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}local-name-from-QName", Component.class, (Component.LOCALNAME << 16) + StandardNames.XS_QNAME, 1, 1,
                BuiltInAtomicType.NCNAME, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.QNAME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}lower-case", ForceCase.class, ForceCase.LOWERCASE, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}matches", Matches.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

//                        e = register("{" + NamespaceConstants.XFORMS_NS + "}max", Minimax.class, Minimax.MAX, 1, 2, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE);
//                        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
//                        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
//                        
//                        e = register("{" + NamespaceConstants.XFORMS_NS + "}min", Minimax.class, Minimax.MIN, 1, 2, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE);
//                        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
//                        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}minutes-from-dateTime", Component.class, (Component.MINUTES << 16) + StandardNames.XS_DATE_TIME, 1, 1,
                BuiltInAtomicType.INTEGER, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}minutes-from-duration", Component.class, (Component.MINUTES << 16) + StandardNames.XS_DURATION, 1, 1,
                BuiltInAtomicType.INTEGER, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}minutes-from-time", Component.class, (Component.MINUTES << 16) + StandardNames.XS_TIME, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}month-from-date", Component.class, (Component.MONTH << 16) + StandardNames.XS_DATE, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}month-from-dateTime", Component.class, (Component.MONTH << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}months-from-duration", Component.class, (Component.MONTH << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}name", NamePart.class, NamePart.NAME, 0, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}namespace-uri", NamePart.class, NamePart.NAMESPACE_URI, 0, 1, BuiltInAtomicType.ANY_URI,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}namespace-uri-for-prefix", NamespaceForPrefix.class, 0, 2, 2, BuiltInAtomicType.ANY_URI,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, NodeKindTest.ELEMENT, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}namespace-uri-from-QName", Component.class, (Component.NAMESPACE << 16) + StandardNames.XS_QNAME, 1, 1,
                BuiltInAtomicType.ANY_URI, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.QNAME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}nilled", Nilled.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}node-name", NamePart.class, NamePart.NODE_NAME, 1, 1, BuiltInAtomicType.QNAME, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}not", BooleanFn.class, BooleanFn.NOT, 1, 1, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, BooleanValue.TRUE);

        register("{" + NamespaceConstants.XFORMS_NS + "}normalize-space", NormalizeSpace.class, 0, 0, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        register("{" + NamespaceConstants.XFORMS_NS + "}normalize-space#0", NormalizeSpace.class, 0, 0, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}normalize-space#1", NormalizeSpace.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}normalize-unicode", NormalizeUnicode.class, 0, 1, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}number", NumberFn.class, 0, 0, 1, BuiltInAtomicType.DOUBLE, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE, DoubleValue.NaN);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}one-or-more", TreatFn.class, StaticProperty.ALLOWS_ONE_OR_MORE, 1, 1, SAME_AS_FIRST_ARGUMENT,
                StaticProperty.ALLOWS_ONE_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ONE_OR_MORE, null);
        // because we don't do draconian static type checking, we can do the
        // work in the argument type checking code

        register("{" + NamespaceConstants.XFORMS_NS + "}position", Position.class, 0, 0, 0, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}prefix-from-QName", Component.class, (Component.PREFIX << 16) + StandardNames.XS_QNAME, 1, 1, BuiltInAtomicType.NCNAME,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.QNAME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}QName", QNameFn.class, 0, 2, 2, BuiltInAtomicType.QNAME, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}regex-group", RegexGroup.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}remove", Remove.class, 0, 2, 2, SAME_AS_FIRST_ARGUMENT, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 1, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}replace", Replace.class, 0, 3, 4, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}resolve-QName", ResolveQName.class, 0, 2, 2, BuiltInAtomicType.QNAME, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 1, NodeKindTest.ELEMENT, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}resolve-uri", ResolveURI.class, 0, 1, 2, BuiltInAtomicType.ANY_URI, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}reverse", Reverse.class, 0, 1, 1, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}root", Root.class, 0, 0, 1, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, Type.NODE_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}round", Rounding.class, Rounding.ROUND, 1, 1, SAME_AS_FIRST_ARGUMENT, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}round-half-to-even", Rounding.class, Rounding.HALF_EVEN, 1, 2, SAME_AS_FIRST_ARGUMENT,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.NUMERIC, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 1, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE, null);

//                        e = register("{" + NamespaceConstants.XFORMS_NS + "}seconds-from-dateTime", Component.class, (Component.SECONDS << 16) + Type.DATE_TIME, 1, 1,
//                            BuiltInAtomicType.DECIMAL, StaticProperty.ALLOWS_ZERO_OR_ONE);
//                        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}seconds-from-duration", Component.class, (Component.SECONDS << 16) + StandardNames.XS_DURATION, 1, 1,
                BuiltInAtomicType.DECIMAL, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}seconds-from-time", Component.class, (Component.SECONDS << 16) + StandardNames.XS_TIME, 1, 1, BuiltInAtomicType.DECIMAL,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}starts-with", StartsWith.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, BooleanValue.TRUE);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        register("{" + NamespaceConstants.XFORMS_NS + "}static-base-uri", StaticBaseURI.class, 0, 0, 0, BuiltInAtomicType.ANY_URI, StaticProperty.ALLOWS_ZERO_OR_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}string", StringFn.class, 0, 0, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        register("{" + NamespaceConstants.XFORMS_NS + "}string-length", StringLength.class, 0, 0, 1, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE);
        register("{" + NamespaceConstants.XFORMS_NS + "}string-length#0", StringLength.class, 0, 0, 0, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}string-length#1", StringLength.class, 0, 1, 1, BuiltInAtomicType.INTEGER, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}string-join", StringJoin.class, 0, 2, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_MORE, StringValue.EMPTY_STRING);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}string-to-codepoints", StringToCodepoints.class, 0, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}subsequence", Subsequence.class, 0, 2, 3, SAME_AS_FIRST_ARGUMENT, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);
        arg(e, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}substring", Substring.class, 0, 2, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);
        arg(e, 1, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}substring-after", SubstringAfter.class, 0, 2, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}substring-before", SubstringBefore.class, 0, 2, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}sum", Aggregate2.class, Aggregate2.SUM, 1, 2, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, null);
        arg(e, 1, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}system-property", SystemProperty.class, 0, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}timezone-from-date", Component.class, (Component.TIMEZONE << 16) + StandardNames.XS_DATE, 1, 1,
                BuiltInAtomicType.DAY_TIME_DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}timezone-from-dateTime", Component.class, (Component.TIMEZONE << 16) + StandardNames.XS_DATE_TIME, 1, 1,
                BuiltInAtomicType.DAY_TIME_DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}timezone-from-time", Component.class, (Component.TIMEZONE << 16) + StandardNames.XS_TIME, 1, 1,
                BuiltInAtomicType.DAY_TIME_DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}trace", Trace.class, 0, 2, 2, SAME_AS_FIRST_ARGUMENT, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        register("{" + NamespaceConstants.XFORMS_NS + "}true", BooleanFn.class, BooleanFn.TRUE, 0, 0, BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}translate", Translate.class, 0, 3, 3, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}tokenize", Tokenize.class, 0, 2, 3, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 2, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}type-available", Available.class, Available.TYPE_AVAILABLE, 1, 1, BuiltInAtomicType.BOOLEAN,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}unordered", Unordered.class, 0, 1, 1, SAME_AS_FIRST_ARGUMENT, StaticProperty.ALLOWS_ZERO_OR_MORE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}upper-case", ForceCase.class, ForceCase.UPPERCASE, 1, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, StringValue.EMPTY_STRING);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}unparsed-entity-uri", UnparsedEntity.class, UnparsedEntity.URI, 1, 1, BuiltInAtomicType.ANY_URI,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        // internal version of unparsed-entity-uri with second argument
        // representing the current document
        e = register("{" + NamespaceConstants.XFORMS_NS + "}unparsed-entity-uri_9999_", UnparsedEntity.class, UnparsedEntity.URI, 2, 2, BuiltInAtomicType.STRING,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 1, Type.NODE_TYPE, StaticProperty.EXACTLY_ONE, null);
        // it must actually be a document node, but there's a non-standard
        // error code

        e = register("{" + NamespaceConstants.XFORMS_NS + "}unparsed-entity-public-id", UnparsedEntity.class, UnparsedEntity.PUBLIC_ID, 1, 1, BuiltInAtomicType.STRING,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        // internal version of unparsed-entity-public-id with second
        // argument representing the current document
        e = register("{" + NamespaceConstants.XFORMS_NS + "}unparsed-entity-public-id_9999_", UnparsedEntity.class, UnparsedEntity.PUBLIC_ID, 2, 2, BuiltInAtomicType.STRING,
                StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 1, Type.NODE_TYPE, StaticProperty.EXACTLY_ONE, null);
        // it must actually be a document node, but there's a non-standard
        // error code

        e = register("{" + NamespaceConstants.XFORMS_NS + "}unparsed-text", UnparsedText.class, UnparsedText.UNPARSED_TEXT, 1, 2, BuiltInAtomicType.STRING,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}unparsed-text-available", UnparsedText.class, UnparsedText.UNPARSED_TEXT_AVAILABLE, 1, 2,
                BuiltInAtomicType.BOOLEAN, StaticProperty.EXACTLY_ONE);
        arg(e, 0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);
        arg(e, 1, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}year-from-date", Component.class, (Component.YEAR << 16) + StandardNames.XS_DATE, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}year-from-dateTime", Component.class, (Component.YEAR << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}years-from-duration", Component.class, (Component.YEAR << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, BuiltInAtomicType.DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);

        e = register("{" + NamespaceConstants.XFORMS_NS + "}zero-or-one", TreatFn.class, StaticProperty.ALLOWS_ZERO_OR_ONE, 1, 1, SAME_AS_FIRST_ARGUMENT,
                StaticProperty.ALLOWS_ZERO_OR_ONE);
        arg(e, 0, Type.ITEM_TYPE, StaticProperty.ALLOWS_ZERO_OR_ONE, null);
        // because we don't do draconian static type checking, we can do the
        // work in the argument type checking code

    }

    //	}

}
