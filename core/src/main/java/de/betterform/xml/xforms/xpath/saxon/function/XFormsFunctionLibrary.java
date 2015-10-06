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
import net.sf.saxon.om.StandardNames;
import net.sf.saxon.pattern.AnyNodeTest;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.type.AnyItemType;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.DoubleValue;
import net.sf.saxon.value.Int64Value;
import net.sf.saxon.value.StringValue;

import static net.sf.saxon.functions.StandardFunction.ABS;
import static net.sf.saxon.functions.StandardFunction.AS_ARG0;
import static net.sf.saxon.functions.StandardFunction.AS_PRIM_ARG0;
import static net.sf.saxon.functions.StandardFunction.BASE;
import static net.sf.saxon.functions.StandardFunction.CORE;
import static net.sf.saxon.functions.StandardFunction.DCOLL;
import static net.sf.saxon.functions.StandardFunction.FOCUS;
import static net.sf.saxon.functions.StandardFunction.INS;
import static net.sf.saxon.functions.StandardFunction.IMP_CX_D;
import static net.sf.saxon.functions.StandardFunction.IMP_CX_I;
import static net.sf.saxon.functions.StandardFunction.NAV;
import static net.sf.saxon.functions.StandardFunction.NS;
import static net.sf.saxon.functions.StandardFunction.ONE;
import static net.sf.saxon.functions.StandardFunction.OPT;
import static net.sf.saxon.functions.StandardFunction.PLUS;
import static net.sf.saxon.functions.StandardFunction.STAR;
import static net.sf.saxon.functions.StandardFunction.TRA;
import static net.sf.saxon.functions.StandardFunction.USE_WHEN;
import static net.sf.saxon.functions.StandardFunction.XPATH30;
import static net.sf.saxon.functions.StandardFunction.XSLT;

/**
 * This class contains static data tables defining the properties of XForms functions. "XForms functions" here means the
 * XForms 1.0 functions.
 */
public class XFormsFunctionLibrary extends XPathFunctionLibrary {

    private static String functionNamespace = NamespaceConstants.XFORMS_NS;

    @Override
    protected String getFunctionNamespace() {
        return functionNamespace;
    }

    private static String xf(final String localArity) {
        return "{" + NamespaceConstants.XFORMS_NS + "}" + localArity;
    }

    static {

        register(xf("boolean-from-string"), BooleanFromString.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);


        register(xf("is-card-number"), IsCardNumber.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("count-non-empty"), CountNonEmpty.class, 0, 1, 1, BuiltInAtomicType.INTEGER, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, Int64Value.ZERO);


        register(xf("current"), Current.class, 0, 0, 0, Type.ITEM_TYPE, ONE, CORE, 0);

        register(xf("IF"), If.class, 0, 3, 3, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.BOOLEAN, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);


        register(xf("instance"), Instance.class, 0, 0, 1, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("index"), Index.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);


        register(xf("power"), Power.class, 0, 2, 2, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.NUMERIC, ONE, null)
            .arg(1, BuiltInAtomicType.NUMERIC, ONE, null);


        register(xf("random"), Random.class, 0, 0, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.BOOLEAN, OPT, null);


        register(xf("compare#2"), Compare.class, 0, 2, 2, BuiltInAtomicType.NUMERIC, OPT, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.STRING, ONE, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, ONE, EMPTY);

        register(xf("compare#3"), Compare.class, 0, 2, 3, BuiltInAtomicType.INTEGER, OPT, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, OPT, EMPTY)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("property"), Property.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("digest"), Digest.class, 0, 2, 3, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null);

        register(xf("hmac"), Hmac.class, 0, 3, 4, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null)
            .arg(3, BuiltInAtomicType.STRING, OPT, null);

        register(xf("local-date"), LocalDate.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, 0);

        register(xf("local-dateTime"), LocalDateTime.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, 0);

        register(xf("now"), Now.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, 0);

        register(xf("days-from-date"), DaysFromDate.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("days-to-date"), DaysToDate.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE, null);

        register(xf("seconds-from-dateTime"), SecondsFromDateTime.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("seconds-to-dateTime"), SecondsToDateTime.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.NUMERIC, ONE, null);

        register(xf("adjust-dateTime-to-timezone"), AdjustDateTimeToTimezone.class, 0, 0, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("seconds"), Seconds.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("months"), Months.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("choose"), Choose.class, 0, 3, 3, Type.ITEM_TYPE, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.BOOLEAN, ONE, null)
            .arg(1, Type.ITEM_TYPE, STAR, null)
            .arg(2, Type.ITEM_TYPE, STAR, null);

        register(xf("context"), Context.class, 0, 0, 0, Type.ITEM_TYPE, ONE, CORE, 0);

        register(xf("event"), Event.class, 0, 1, 1, Type.ITEM_TYPE, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        register(xf("id"), Id2.class, 0, 1, 2, NodeKindTest.ELEMENT, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, STAR, EMPTY)
            .arg(1, Type.NODE_TYPE, STAR, null);



        // Adapted xpath 2.0 functions


        // can't say "same as first argument" because the avg of a set of
        // integers is decimal
        register(xf("avg"), Aggregate2.class, Aggregate2.AVG, 1, 1, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);

        register(xf("max#1"), Minimax2.class, Minimax2.MAX, 1, 1, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);

        register(xf("max#2"), Minimax2.class, Minimax2.MAX, 2, 2, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, BASE)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("min#1"), Minimax2.class, Minimax2.MIN, 1, 1, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);

        register(xf("min#2"), Minimax2.class, Minimax2.MIN, 2, 2, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, BASE)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        // Standard XPath functions
        register(xf("abs"), Abs.class, 0, 1, 1, SAME_AS_FIRST_ARGUMENT, OPT, CORE, AS_PRIM_ARG0)
            .arg(0, BuiltInAtomicType.NUMERIC, OPT, EMPTY);

        register(xf("adjust-date-to-timezone"), Adjust.class, 0, 1, 2, BuiltInAtomicType.DATE, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.DAY_TIME_DURATION, OPT, null);

//                        e = register(xf("adjust-dateTime-to-timezone"), Adjust.class, 0, 1, 2, BuiltInAtomicType.DATE_TIME,
//                            StaticProperty.ALLOWS_ZERO_OR_ONE);
//                        arg(e, 0, BuiltInAtomicType.DATE_TIME, StaticProperty.ALLOWS_ZERO_OR_ONE, EMPTY);
//                        arg(e, 1, BuiltInAtomicType.DAY_TIME_DURATION, StaticProperty.ALLOWS_ZERO_OR_ONE, null);

        register(xf("adjust-time-to-timezone"), Adjust.class, 0, 1, 2, BuiltInAtomicType.TIME, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.TIME, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.DAY_TIME_DURATION, OPT, null);

//                        e = register(xf("avg"), Aggregate.class, Aggregate.AVG, 1, 1, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_ONE, CORE, 0);
//                        // can't say "same as first argument" because the avg of a set of
//                        // integers is decimal
//                        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, StaticProperty.ALLOWS_ZERO_OR_MORE, EMPTY);


        register(xf("base-uri"),
            BaseURI.class, 0, 0, 1, BuiltInAtomicType.ANY_URI, OPT, CORE, BASE | IMP_CX_I)
            .arg(0, Type.NODE_TYPE, OPT, EMPTY);

        register(xf("boolean"), BooleanFn.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR, null);

        register(xf("ceiling"), Ceiling.class, 0, 1, 1, SAME_AS_FIRST_ARGUMENT, OPT, CORE, AS_PRIM_ARG0)
            .arg(0, BuiltInAtomicType.NUMERIC, OPT, EMPTY);

        register(xf("codepoint-equal"), CodepointEqual.class, 0, 2, 2, BuiltInAtomicType.BOOLEAN, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, OPT, EMPTY);

        register(xf("codepoints-to-string"), CodepointsToString.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.INTEGER, STAR, null);

        register(xf("collection"), Collection.class, 0, 0, 1, Type.NODE_TYPE, STAR, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

//        register(xf("compare#2"), Compare.class, 0, 2, 2, BuiltInAtomicType.INTEGER, OPT, CORE, DCOLL)
//                .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY)
//                .arg(1, BuiltInAtomicType.STRING, OPT, EMPTY);
//
//        register(xf("compare#3"), Compare.class, 0, 3, 3, BuiltInAtomicType.INTEGER, OPT, CORE, BASE)
//                .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY)
//                .arg(1, BuiltInAtomicType.STRING, OPT, EMPTY)
//                .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("concat"), Concat.class, 0, 2, Integer.MAX_VALUE, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, OPT, null);
        // Note, this has a variable number of arguments so it is treated
        // specially

        register(xf("contains#2"), Contains.class, 0, 2, 2, BuiltInAtomicType.BOOLEAN, ONE, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, BooleanValue.TRUE);

        register(xf("contains#3"), Contains.class, 0, 3, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, BooleanValue.TRUE)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("count"), Aggregate2.class, Aggregate2.COUNT, 1, 1, BuiltInAtomicType.INTEGER, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR | INS, Int64Value.ZERO);

        // XForms has special current version
        // register(xf("current"), Current.class, 0, 0, 0, Type.ITEM_TYPE, StaticProperty.EXACTLY_ONE);
        register(xf("current-date"), CurrentDateTime.class, 0, 0, 0, BuiltInAtomicType.DATE, ONE, CORE, 0);

        register(xf("current-dateTime"), CurrentDateTime.class, 0, 0, 0, BuiltInAtomicType.DATE_TIME, ONE, CORE, 0);

        register(xf("current-time"), CurrentDateTime.class, 0, 0, 0, BuiltInAtomicType.TIME, ONE, CORE, 0);

        register(xf("current-group"), CurrentGroup.class, 0, 0, 0, Type.ITEM_TYPE, STAR, XSLT, 0);

        register(xf("current-grouping-key"), CurrentGroupingKey.class, 0, 0, 0, BuiltInAtomicType.ANY_ATOMIC, OPT, XSLT, 0);

        register(xf("data#0"), Data.class, 0, 0, 0, BuiltInAtomicType.ANY_ATOMIC, STAR, XPATH30, FOCUS);

        register(xf("data#1"), Data.class, 0,
            1, 1, BuiltInAtomicType.ANY_ATOMIC, STAR, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR | ABS, EMPTY);

        register(xf("dateTime"), DateTimeConstructor.class, 0, 2, 2, BuiltInAtomicType.DATE_TIME, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.TIME, OPT, EMPTY);

        register(xf("day-from-date"), AccessorFn.class, (AccessorFn.DAY << 16) + StandardNames.XS_DATE, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE, OPT, EMPTY);

        register(xf("day-from-dateTime"), AccessorFn.class, (AccessorFn.DAY << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE_TIME, OPT, EMPTY);

        register(xf("days-from-duration"), AccessorFn.class, (AccessorFn.DAY << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DURATION, OPT, EMPTY);

        register(xf("deep-equal#2"), DeepEqual.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, DCOLL)
            .arg(0, Type.ITEM_TYPE, STAR | ABS, null)
            .arg(1, Type.ITEM_TYPE, STAR | ABS, null);

        register(xf("deep-equal#3"), DeepEqual.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, BASE)
            .arg(0, Type.ITEM_TYPE, STAR, null)
            .arg(1, Type.ITEM_TYPE, STAR, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("default-collation"), DefaultCollation.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, DCOLL);

        register(xf("distinct-values#1"), DistinctValues.class, 0, 1, 2, BuiltInAtomicType.ANY_ATOMIC, STAR, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);

        register(xf("distinct-values#2"), DistinctValues.class, 0, 1, 2, BuiltInAtomicType.ANY_ATOMIC, STAR, CORE, BASE)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("doc"), Doc.class, 0, 1, 1, NodeKindTest.DOCUMENT, OPT, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY);

        register(xf("doc-available"), DocAvailable.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, BooleanValue.FALSE);

        register(xf("document"), DocumentFn.class, 0, 1, 2, Type.NODE_TYPE, STAR, XSLT, BASE)
            .arg(0, Type.ITEM_TYPE, STAR, null)
            .arg(1, Type.NODE_TYPE, ONE, null);

        register(xf("document-uri#0"), DocumentUriFn.class, 0, 0, 0, BuiltInAtomicType.ANY_URI, OPT, XPATH30, FOCUS | IMP_CX_I);

        register(xf("document-uri#1"), DocumentUriFn.class, 0, 1, 1, BuiltInAtomicType.ANY_URI, OPT, CORE, 0)
            .arg(0, Type.NODE_TYPE, OPT | INS, EMPTY);

        register(xf("empty"), Empty.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR | INS, BooleanValue.TRUE);

        register(xf("ends-with#2"), EndsWith.class, 0, 2, 2, BuiltInAtomicType.BOOLEAN, ONE, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, BooleanValue.TRUE);

        register(xf("ends-with#3"), EndsWith.class, 0, 3, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, BooleanValue.TRUE)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("element-available"), ElementAvailable.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, XSLT | USE_WHEN, NS)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("encode-for-uri"), EscapeURI.class, EscapeURI.ENCODE_FOR_URI, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING);

        register(xf("escape-html-uri"), EscapeURI.class, EscapeURI.HTML_URI, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING);

        register(xf("error"), Error.class, 0, 0, 3, Type.ITEM_TYPE, OPT, CORE, 0)
            // The return type is chosen so that use of the error() function will never give a static type error,
            // on the basis that item()? overlaps every other type, and it's almost impossible to make any
            // unwarranted inferences from it, except perhaps count(error()) lt 2.
            .arg(0, BuiltInAtomicType.QNAME, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, Type.ITEM_TYPE, STAR, null);

        // e = register(xf("escape-uri"), EscapeURI.class, EscapeURI.ESCAPE, 2,
        // 2, BuiltInAtomicType.STRING, ONE);
        // arg(e, 0, BuiltInAtomicType.STRING,
        // OPT);
        // arg(e, 1, BuiltInAtomicType.BOOLEAN, ONE);

        register(xf("exactly-one"), TreatFn.class, ONE, 1, 1, Type.ITEM_TYPE, ONE, CORE, AS_ARG0)
            .arg(0, Type.ITEM_TYPE, ONE | TRA, null);
        // because we don't do draconian static type checking, we can do the work in the argument type checking code

        register(xf("exists"), Exists.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR | INS, BooleanValue.FALSE);

        register(xf("false"), False.class, 0, 0, 0, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0);

        register(xf("floor"), Floor.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, OPT, CORE, AS_PRIM_ARG0)
            .arg(0, BuiltInAtomicType.NUMERIC, OPT, EMPTY);

        register(xf("format-date"), FormatDate.class, StandardNames.XS_DATE, 2, 5, BuiltInAtomicType.STRING, OPT, XSLT | XPATH30, 0)
            .arg(0, BuiltInAtomicType.DATE, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null)
            .arg(3, BuiltInAtomicType.STRING, OPT, null)
            .arg(4, BuiltInAtomicType.STRING, OPT, null);

        register(xf("format-dateTime"), FormatDate.class, StandardNames.XS_DATE_TIME, 2, 5, BuiltInAtomicType.STRING, OPT, XSLT | XPATH30, 0)
            .arg(0, BuiltInAtomicType.DATE_TIME, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null)
            .arg(3, BuiltInAtomicType.STRING, OPT, null)
            .arg(4, BuiltInAtomicType.STRING, OPT, null);

        register(xf("format-integer"), FormatInteger.class, 0, 2, 3, AnyItemType.getInstance(), ONE, XPATH30, 0)
            .arg(0, BuiltInAtomicType.INTEGER, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null);

        register(xf("format-number#2"), FormatNumber.class, 0, 2, 2, BuiltInAtomicType.STRING, ONE, XSLT | XPATH30, 0)
            .arg(0, BuiltInAtomicType.NUMERIC, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("format-number#3"), FormatNumber.class, 0, 3, 3, BuiltInAtomicType.STRING, ONE, XSLT | XPATH30, NS)
            .arg(0, BuiltInAtomicType.NUMERIC, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null);

        register(xf("format-time"), FormatDate.class, StandardNames.XS_TIME, 2, 5, BuiltInAtomicType.STRING, OPT, XSLT | XPATH30, 0)
            .arg(0, BuiltInAtomicType.TIME, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null)
            .arg(3, BuiltInAtomicType.STRING, OPT, null)
            .arg(4, BuiltInAtomicType.STRING, OPT, null);

        register(xf("function-available"), FunctionAvailable.class, 0, 1, 2, BuiltInAtomicType.BOOLEAN, ONE, XSLT | USE_WHEN, NS)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.INTEGER, ONE, null);

        register(xf("generate-id#0"), GenerateId.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, XSLT | XPATH30, FOCUS | IMP_CX_I);

        register(xf("generate-id#1"), GenerateId.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, XSLT | XPATH30, 0)
            .arg(0, Type.NODE_TYPE, OPT | INS, StringValue.EMPTY_STRING);

        register(xf("hours-from-dateTime"), AccessorFn.class, (AccessorFn.HOURS << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE_TIME, OPT, EMPTY);

        register(xf("hours-from-duration"), AccessorFn.class, (AccessorFn.HOURS << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DURATION, OPT, EMPTY);

        register(xf("hours-from-time"), AccessorFn.class, (AccessorFn.HOURS << 16) + StandardNames.XS_TIME, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.TIME, OPT, EMPTY);

//            e = register(xf("id"), Id.class, 0, 1, 2, NodeKindTest.ELEMENT, STAR, CORE, 0);
//            arg(e, 0, BuiltInAtomicType.STRING, STAR, EMPTY);
//            arg(e, 1, Type.NODE_TYPE, ONE, null);

        register(xf("idref#1"), Idref.class, 0, 1, 1, Type.NODE_TYPE, STAR, CORE, FOCUS | IMP_CX_D)
            .arg(0, BuiltInAtomicType.STRING, STAR, EMPTY);

        register(xf("idref#2"), Idref.class, 0, 2, 2, Type.NODE_TYPE, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, STAR, EMPTY)
            .arg(1, Type.NODE_TYPE, ONE, null);

        register(xf("implicit-timezone"), CurrentDateTime.class, 0, 0, 0, BuiltInAtomicType.DAY_TIME_DURATION, ONE, CORE, 0);

        register(xf("in-scope-prefixes"), InScopePrefixes.class, 0, 1, 1, BuiltInAtomicType.STRING, STAR, CORE, 0)
            .arg(0, NodeKindTest.ELEMENT, ONE | INS, null);

        register(xf("index-of#2"), IndexOf.class, 0, 2, 2, BuiltInAtomicType.INTEGER, STAR, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY)
            .arg(1, BuiltInAtomicType.ANY_ATOMIC, ONE, null);

        register(xf("index-of#3"), IndexOf.class, 0, 3, 3, BuiltInAtomicType.INTEGER, STAR, CORE, BASE)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY)
            .arg(1, BuiltInAtomicType.ANY_ATOMIC, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("innermost"), Innermost.class, 0, 1, 1, AnyNodeTest.getInstance(), STAR, XPATH30, 0)
            .arg(0, AnyNodeTest.getInstance(), STAR | NAV, null);

        register(xf("insert-before"), InsertBefore.class, 0, 3, 3, Type.ITEM_TYPE, STAR, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR | TRA, null)
            .arg(1, BuiltInAtomicType.INTEGER, ONE, null)
            .arg(2, Type.ITEM_TYPE, STAR | TRA, null);

        register(xf("iri-to-uri"), EscapeURI.class, EscapeURI.IRI_TO_URI, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING);

        register(xf("key#2"), KeyFn.class, 0, 2, 2, Type.NODE_TYPE, STAR, XSLT, FOCUS | NS | IMP_CX_D)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);

        register(xf("key#3"), KeyFn.class, 0, 3, 3, Type.NODE_TYPE, STAR, XSLT, NS)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY)
            .arg(2, Type.NODE_TYPE, ONE, null);

        register(xf("lang#1"), Lang.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, FOCUS)
            .arg(0, BuiltInAtomicType.STRING, OPT, null);

        register(xf("lang#2"), Lang.class, 0, 2, 2, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, Type.NODE_TYPE, ONE | INS, null);

        register(xf("last"), Last.class, 0, 0,
            0, BuiltInAtomicType.INTEGER, ONE, CORE, FOCUS);

        register(xf("local-name#0"), LocalNameFn.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, FOCUS | IMP_CX_I);

        register(xf("local-name#1"), LocalNameFn.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, Type.NODE_TYPE, OPT | INS, StringValue.EMPTY_STRING);

        register(xf("local-name-from-QName"), AccessorFn.class, (AccessorFn.LOCALNAME << 16) + StandardNames.XS_QNAME, 1, 1, BuiltInAtomicType.NCNAME, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.QNAME, OPT, EMPTY);

        register(xf("lower-case"), LowerCase.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING);

        register(xf("matches"), Matches.class, 0, 2, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);


//                        e = register(xf("max"), Minimax.class, Minimax.MAX, 1, 2, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, 0);
//                        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);
//                        arg(e, 1, BuiltInAtomicType.STRING, ONE, null);
//                        
//                        e = register(xf("min"), Minimax.class, Minimax.MIN, 1, 2, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, 0);
//                        arg(e, 0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);
//                        arg(e, 1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("minutes-from-dateTime"), AccessorFn.class, (AccessorFn.MINUTES << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE_TIME, OPT, EMPTY);

        register(xf("minutes-from-duration"), AccessorFn.class, (AccessorFn.MINUTES << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DURATION, OPT, EMPTY);

        register(xf("minutes-from-time"), AccessorFn.class, (AccessorFn.MINUTES << 16) + StandardNames.XS_TIME, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.TIME, OPT, EMPTY);

        register(xf("month-from-date"), AccessorFn.class, (AccessorFn.MONTH << 16) + StandardNames.XS_DATE, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE, OPT, EMPTY);

        register(xf("month-from-dateTime"), AccessorFn.class, (AccessorFn.MONTH << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE_TIME, OPT, EMPTY);

        register(xf("months-from-duration"), AccessorFn.class, (AccessorFn.MONTH << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DURATION, OPT, EMPTY);

        register(xf("name#0"), NameFn.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, FOCUS | IMP_CX_I);

        register(xf("name#1"), NameFn.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, Type.NODE_TYPE, OPT | INS, StringValue.EMPTY_STRING);

        register(xf("namespace-uri#0"), NamespaceUriFn.class, 0, 0, 0, BuiltInAtomicType.ANY_URI, ONE, CORE, FOCUS | IMP_CX_I);

        register(xf("namespace-uri#1"), NamespaceUriFn.class, 0, 1, 1, BuiltInAtomicType.ANY_URI, ONE, CORE, 0)
            .arg(0, Type.NODE_TYPE, OPT | INS, StringValue.EMPTY_STRING);

        register(xf("namespace-uri-for-prefix"), NamespaceForPrefix.class, 0, 2, 2, BuiltInAtomicType.ANY_URI, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, NodeKindTest.ELEMENT, ONE | INS, null);

        register(xf("namespace-uri-from-QName"), AccessorFn.class, (AccessorFn.NAMESPACE << 16) + StandardNames.XS_QNAME, 1, 1, BuiltInAtomicType.ANY_URI, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.QNAME, OPT, EMPTY);

        register(xf("nilled#0"), Nilled.class, 0, 0, 0, BuiltInAtomicType.BOOLEAN, OPT, XPATH30, FOCUS | IMP_CX_I);

        register(xf("nilled#1"), Nilled.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, OPT, CORE, 0)
            .arg(0, Type.NODE_TYPE, OPT | INS, EMPTY);

        register(xf("node-name#0"), NodeNameFn.class, 0, 0, 0, BuiltInAtomicType.QNAME, OPT, XPATH30, FOCUS | IMP_CX_I);

        register(xf("node-name#1"), NodeNameFn.class, 0, 1, 1, BuiltInAtomicType.QNAME, OPT, CORE, 0)
            .arg(0, Type.NODE_TYPE, OPT | INS, EMPTY);

        register(xf("not"), NotFn.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR | INS, BooleanValue.TRUE);

        register(xf("normalize-space#0"), NormalizeSpace_0.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, FOCUS);

        register(xf("normalize-space#1"), NormalizeSpace_1.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null);

        register(xf("normalize-unicode"), NormalizeUnicode.class, 0, 1, 2, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("number#0"), NumberFn.class, 0, 0, 0, BuiltInAtomicType.DOUBLE, ONE, CORE, FOCUS | IMP_CX_I);

        register(xf("number#1"), NumberFn.class, 0, 1, 1, BuiltInAtomicType.DOUBLE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, OPT, DoubleValue.NaN);

        register(xf("one-or-more"), TreatFn.class, PLUS, 1, 1, Type.ITEM_TYPE, PLUS, CORE, AS_ARG0)
            .arg(0, Type.ITEM_TYPE, PLUS | TRA, null);
        // because we don't do draconian static type checking, we can do the work in the argument type checking code

        register(xf("outermost"), Outermost.class, 0, 1, 1, AnyNodeTest.getInstance(), STAR, XPATH30, 0)
            .arg(0, AnyNodeTest.getInstance(), STAR | TRA, null);

        register(xf("parse-xml"), ParseXml.class, 0, 1, 1, NodeKindTest.DOCUMENT, ONE, XPATH30, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("parse-xml-fragment"), ParseXmlFragment.class, 0, 1, 1, NodeKindTest.DOCUMENT, ONE, XPATH30, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("path"), Path.class, 0, 0, 1, BuiltInAtomicType.STRING, OPT, XPATH30, IMP_CX_I)
            .arg(0, AnyNodeTest.getInstance(), OPT | StandardFunction.INS, null);

        register(xf("position"), Position.class, 0, 0, 0, BuiltInAtomicType.INTEGER, ONE, CORE, FOCUS);

        register(xf("prefix-from-QName"), AccessorFn.class, (AccessorFn.PREFIX << 16) + StandardNames.XS_QNAME, 1, 1, BuiltInAtomicType.NCNAME, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.QNAME, OPT, EMPTY);

        register(xf("QName"), QNameFn.class, 0, 2, 2, BuiltInAtomicType.QNAME, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("regex-group"), RegexGroup.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, XSLT, 0)
            .arg(0, BuiltInAtomicType.INTEGER, ONE, null);

        register(xf("remove"), Remove.class, 0, 2, 2, Type.ITEM_TYPE, STAR, CORE, AS_ARG0)
            .arg(0, Type.ITEM_TYPE, STAR | TRA, EMPTY)
            .arg(1, BuiltInAtomicType.INTEGER, ONE, null);

        register(xf("replace"), Replace.class, 0, 3, 4, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null)
            .arg(3, BuiltInAtomicType.STRING, ONE, null);

        register(xf("resolve-QName"), ResolveQName.class, 0, 2, 2, BuiltInAtomicType.QNAME, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY)
            .arg(1, NodeKindTest.ELEMENT, ONE | INS, null);

        register(xf("resolve-uri#1"), ResolveURI.class, 0, 1, 1, BuiltInAtomicType.ANY_URI, OPT, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, null);

        register(xf("resolve-uri#2"), ResolveURI.class, 0, 2, 2, BuiltInAtomicType.ANY_URI, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("reverse"), Reverse.class, 0, 1, 1, Type.ITEM_TYPE, STAR, CORE, 0)
            .arg(0, Type.ITEM_TYPE, STAR | NAV, EMPTY);

        register(xf("root#0"), Root.class, 0, 0, 0, Type.NODE_TYPE, OPT, CORE, FOCUS | IMP_CX_I);

        register(xf("root#1"), Root.class, 0, 1, 1, Type.NODE_TYPE, OPT, CORE, 0)
            .arg(0, Type.NODE_TYPE, OPT | NAV, EMPTY);

        register(xf("round#1"), Round.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, OPT, CORE, AS_PRIM_ARG0)
            .arg(0, BuiltInAtomicType.NUMERIC, OPT, EMPTY);

        register(xf("round#2"), Round.class, 0, 2, 2, BuiltInAtomicType.NUMERIC, OPT, XPATH30, AS_PRIM_ARG0)
            .arg(0, BuiltInAtomicType.NUMERIC, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.INTEGER, ONE, null);

        register(xf("round-half-to-even"), RoundHalfToEven.class, 0, 1, 2, BuiltInAtomicType.NUMERIC, OPT, CORE, AS_PRIM_ARG0)
            .arg(0, BuiltInAtomicType.NUMERIC, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.INTEGER, ONE, null);

//                        e = register(xf("seconds-from-dateTime"), AccessorFn.class, (AccessorFn.SECONDS << 16) + Type.DATE_TIME, 1, 1,
//                            BuiltInAtomicType.DECIMAL, OPT);
//                        arg(e, 0, BuiltInAtomicType.DATE_TIME, OPT, EMPTY);

        register(xf("seconds-from-duration"), AccessorFn.class, (AccessorFn.SECONDS << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.DECIMAL, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DURATION, OPT, EMPTY);

        register(xf("seconds-from-time"), AccessorFn.class, (AccessorFn.SECONDS << 16) + StandardNames.XS_TIME, 1, 1, BuiltInAtomicType.DECIMAL, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.TIME, OPT, EMPTY);

        register(xf("serialize"), Serialize.class, 0, 1, 2, BuiltInAtomicType.STRING, ONE, XPATH30, 0)
            .arg(0, AnyItemType.getInstance(), STAR, null)
            .arg(1, NodeKindTest.ELEMENT, STAR, null);

        register(xf("starts-with#2"), StartsWith.class, 0, 2, 2, BuiltInAtomicType.BOOLEAN, ONE, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, BooleanValue.TRUE);

        register(xf("starts-with#3"), StartsWith.class, 0, 3, 3, BuiltInAtomicType.BOOLEAN, ONE, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, BooleanValue.TRUE)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("static-base-uri"), StaticBaseURI.class, 0, 0, 0, BuiltInAtomicType.ANY_URI, OPT, CORE, BASE);

        register(xf("string#0"), StringFn.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, FOCUS | IMP_CX_I);

        register(xf("string#1"), StringFn.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, Type.ITEM_TYPE, OPT | ABS, StringValue.EMPTY_STRING);

        register(xf("string-length#0"), StringLength.class, 0, 0, 0, BuiltInAtomicType.INTEGER, ONE, CORE, FOCUS);

        register(xf("string-length#1"), StringLength.class, 0, 1, 1, BuiltInAtomicType.INTEGER, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null);

        register(xf("string-join#1"), StringJoin.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, XPATH30, 0)
            .arg(0, BuiltInAtomicType.STRING, STAR, StringValue.EMPTY_STRING);

        register(xf("string-join#2"), StringJoin.class, 0, 2, 2, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, STAR, StringValue.EMPTY_STRING)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("string-to-codepoints"), StringToCodepoints.class, 0, 1, 1, BuiltInAtomicType.INTEGER, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY);

        register(xf("subsequence"), Subsequence.class, 0, 2, 3, Type.ITEM_TYPE, STAR, CORE, AS_ARG0)
            .arg(0, Type.ITEM_TYPE, STAR | TRA, EMPTY)
            .arg(1, BuiltInAtomicType.NUMERIC, ONE, null)
            .arg(2, BuiltInAtomicType.NUMERIC, ONE, null);

        register(xf("substring"), Substring.class, 0, 2, 3, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING)
            .arg(1, BuiltInAtomicType.NUMERIC, ONE, null)
            .arg(2, BuiltInAtomicType.NUMERIC, ONE, null);

        register(xf("substring-after#2"), SubstringAfter.class, 0, 2, 2, BuiltInAtomicType.STRING, ONE, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, null);

        register(xf("substring-after#3"), SubstringAfter.class, 0, 3, 3, BuiltInAtomicType.STRING, ONE, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("substring-before#2"), SubstringBefore.class, 0, 2, 2, BuiltInAtomicType.STRING, ONE, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING);

        register(xf("substring-before#3"), SubstringBefore.class, 0, 3, 3, BuiltInAtomicType.STRING, ONE, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("sum"), Sum.class, 0, 1, 2, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, null)
            .arg(1, BuiltInAtomicType.ANY_ATOMIC, OPT, null);

        register(xf("system-property"), SystemProperty.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, XSLT | USE_WHEN, NS)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("tail"), TailFn.class, 0, 1, 1, AnyItemType.getInstance(), STAR, XPATH30, 0)
            .arg(0, AnyItemType.getInstance(), STAR | TRA, null);

        register(xf("timezone-from-date"), AccessorFn.class, (AccessorFn.TIMEZONE << 16) + StandardNames.XS_DATE, 1, 1, BuiltInAtomicType.DAY_TIME_DURATION, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE, OPT, EMPTY);

        register(xf("timezone-from-dateTime"), AccessorFn.class, (AccessorFn.TIMEZONE << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.DAY_TIME_DURATION, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE_TIME, OPT, EMPTY);

        register(xf("timezone-from-time"), AccessorFn.class, (AccessorFn.TIMEZONE << 16) + StandardNames.XS_TIME, 1, 1, BuiltInAtomicType.DAY_TIME_DURATION, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.TIME, OPT, EMPTY);

        register(xf("trace"), Trace.class, 0, 2, 2, Type.ITEM_TYPE, STAR, CORE, AS_ARG0)
            .arg(0, Type.ITEM_TYPE, STAR | TRA, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

//            register("true", BooleanFn.class, BooleanFn.TRUE, 0, 0, BuiltInAtomicType.BOOLEAN,
//                    UNIT, CORE)

        register(xf("translate"), Translate.class, 0, 3, 3, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("true"), True.class, 0, 0,
            0, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0);

        register(xf("tokenize"), Tokenize.class, 0, 2, 3, BuiltInAtomicType.STRING, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        register(xf("type-available"), TypeAvailable.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, XSLT | USE_WHEN, NS)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        register(xf("unordered"), Unordered.class, 0, 1, 1, Type.ITEM_TYPE, STAR, CORE, AS_ARG0)
            .arg(0, Type.ITEM_TYPE, STAR | TRA, EMPTY);

        register(xf("upper-case"), UpperCase.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, StringValue.EMPTY_STRING);

        register(xf("unparsed-entity-uri#1"), UnparsedEntity.class, UnparsedEntity.URI, 1, 1, BuiltInAtomicType.ANY_URI, ONE, XSLT, FOCUS | IMP_CX_D)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        // internal version of unparsed-entity-uri with second argument representing the current document
        register(xf("unparsed-entity-uri#2"), UnparsedEntity.class, UnparsedEntity.URI, 2, 2, BuiltInAtomicType.STRING, ONE, XSLT, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, Type.NODE_TYPE, ONE, null);
        // it must actually be a document node, but there's a non-standard error code

        register(xf("unparsed-entity-public-id#1"), UnparsedEntity.class, UnparsedEntity.PUBLIC_ID, 1, 1, BuiltInAtomicType.STRING, ONE, XSLT, FOCUS | IMP_CX_D)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        // internal version of unparsed-entity-public-id with second argument representing the current document
        register(xf("unparsed-entity-public-id#2"), UnparsedEntity.class, UnparsedEntity.PUBLIC_ID, 2, 2, BuiltInAtomicType.STRING, ONE, XSLT, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, Type.NODE_TYPE, ONE, null);
        // it must actually be a document node, but there's a non-standard error code

        register(xf("unparsed-text"), UnparsedText.class, 0, 1, 2, BuiltInAtomicType.STRING, OPT, XSLT | XPATH30, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("unparsed-text-available"), UnparsedTextAvailable.class, 0, 1, 2, BuiltInAtomicType.BOOLEAN, ONE, XSLT | XPATH30, BASE)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("unparsed-text-lines"), UnparsedTextLines.class, 0, 1, 2, BuiltInAtomicType.STRING, STAR, XPATH30, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null);

        register(xf("uri-collection"), UriCollection.class, 0, 0, 1, BuiltInAtomicType.ANY_URI, STAR, XPATH30, 0)
            .arg(0, BuiltInAtomicType.STRING, OPT, null);

        register(xf("year-from-date"), AccessorFn.class, (AccessorFn.YEAR << 16) + StandardNames.XS_DATE, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE, OPT, EMPTY);

        register(xf("year-from-dateTime"), AccessorFn.class, (AccessorFn.YEAR << 16) + StandardNames.XS_DATE_TIME, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DATE_TIME, OPT, EMPTY);

        register(xf("years-from-duration"), AccessorFn.class, (AccessorFn.YEAR << 16) + StandardNames.XS_DURATION, 1, 1, BuiltInAtomicType.INTEGER, OPT, CORE, 0)
            .arg(0, BuiltInAtomicType.DURATION, OPT, EMPTY);

        register(xf("zero-or-one"), TreatFn.class, OPT, 1, 1, Type.ITEM_TYPE, OPT, CORE, AS_ARG0)
            .arg(0, Type.ITEM_TYPE, OPT | TRA, null);
        // because we don't do draconian static type checking, we can do the work in the argument type checking code

    }

    //	}

}
