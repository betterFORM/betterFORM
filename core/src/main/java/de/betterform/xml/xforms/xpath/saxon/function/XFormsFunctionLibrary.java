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
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.type.BuiltInAtomicType;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.type.Type;
import net.sf.saxon.value.Int64Value;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.util.Map;

import static net.sf.saxon.functions.StandardFunction.BASE;
import static net.sf.saxon.functions.StandardFunction.CORE;
import static net.sf.saxon.functions.StandardFunction.DCOLL;
import static net.sf.saxon.functions.StandardFunction.ONE;
import static net.sf.saxon.functions.StandardFunction.OPT;
import static net.sf.saxon.functions.StandardFunction.STAR;

/**
 * This class contains static data tables defining the properties of XForms functions. "XForms functions" here means the
 * XForms 1.0 functions plus XPath functions
 */
public class XFormsFunctionLibrary extends XPathFunctionLibrary {

    private static Log LOGGER = LogFactory.getLog(XFormsFunctionLibrary.class);

    private static String functionNamespace = NamespaceConstants.XFORMS_NS;

    static {

        //XForms spec functions

        registerXf("boolean-from-string", BooleanFromString.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);


        registerXf("is-card-number", IsCardNumber.class, 0, 1, 1, BuiltInAtomicType.BOOLEAN, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerXf("count-non-empty", CountNonEmpty.class, 0, 1, 1, BuiltInAtomicType.INTEGER, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, Int64Value.ZERO);


        registerXf("current", Current.class, 0, 0, 0, Type.ITEM_TYPE, ONE, CORE, 0);

        registerXf("IF", If.class, 0, 3, 3, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.BOOLEAN, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);


        registerXf("instance", Instance.class, 0, 0, 1, Type.ITEM_TYPE, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerXf("index", Index.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);


        registerXf("power", Power.class, 0, 2, 2, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.NUMERIC, ONE, null)
            .arg(1, BuiltInAtomicType.NUMERIC, ONE, null);


        registerXf("random", Random.class, 0, 0, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.BOOLEAN, OPT, null);


        registerXf("compare#2", Compare.class, 0, 2, 2, BuiltInAtomicType.NUMERIC, OPT, CORE, DCOLL)
            .arg(0, BuiltInAtomicType.STRING, ONE, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, ONE, EMPTY);

        registerXf("compare#3", Compare.class, 0, 2, 3, BuiltInAtomicType.INTEGER, OPT, CORE, BASE)
            .arg(0, BuiltInAtomicType.STRING, OPT, EMPTY)
            .arg(1, BuiltInAtomicType.STRING, OPT, EMPTY)
            .arg(2, BuiltInAtomicType.STRING, ONE, null);

        registerXf("property", Property.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerXf("digest", Digest.class, 0, 2, 3, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, OPT, null);

        registerXf("hmac", Hmac.class, 0, 3, 4, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null)
            .arg(1, BuiltInAtomicType.STRING, ONE, null)
            .arg(2, BuiltInAtomicType.STRING, ONE, null)
            .arg(3, BuiltInAtomicType.STRING, OPT, null);

        registerXf("local-date", LocalDate.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, 0);

        registerXf("local-dateTime", LocalDateTime.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, 0);

        registerXf("now", Now.class, 0, 0, 0, BuiltInAtomicType.STRING, ONE, CORE, 0);

        registerXf("days-from-date", DaysFromDate.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerXf("days-to-date", DaysToDate.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.NUMERIC, StaticProperty.EXACTLY_ONE, null);

        registerXf("seconds-from-dateTime", SecondsFromDateTime.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerXf("seconds-to-dateTime", SecondsToDateTime.class, 0, 1, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.NUMERIC, ONE, null);

        registerXf("adjust-dateTime-to-timezone", AdjustDateTimeToTimezone.class, 0, 0, 1, BuiltInAtomicType.STRING, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerXf("seconds", Seconds.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerXf("months", Months.class, 0, 1, 1, BuiltInAtomicType.NUMERIC, ONE, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, ONE, null);

        registerXf("choose", Choose.class, 0, 3, 3, Type.ITEM_TYPE, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.BOOLEAN, ONE, null)
            .arg(1, Type.ITEM_TYPE, STAR, null)
            .arg(2, Type.ITEM_TYPE, STAR, null);

        registerXf("context", Context.class, 0, 0, 0, Type.ITEM_TYPE, ONE, CORE, 0);

        registerXf("event", Event.class, 0, 1, 1, Type.ITEM_TYPE, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, StaticProperty.EXACTLY_ONE, null);

        registerXf("id", Id2.class, 0, 1, 2, NodeKindTest.ELEMENT, STAR, CORE, 0)
            .arg(0, BuiltInAtomicType.STRING, STAR, EMPTY)
            .arg(1, Type.NODE_TYPE, STAR, null);


        //Adapted XPath 2.0 functions
        registerXf("avg", Aggregate2.class, Aggregate2.AVG, 1, 1, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, 0)
                .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);

        registerXf("max#1", Minimax2.class, Minimax2.MAX, 1, 1, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, DCOLL)
                .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);

        registerXf("max#2", Minimax2.class, Minimax2.MAX, 2, 2, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, BASE)
                .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY)
                .arg(1, BuiltInAtomicType.STRING, ONE, null);

        registerXf("min#1", Minimax2.class, Minimax2.MIN, 1, 1, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, DCOLL)
                .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY);

        registerXf("min#2", Minimax2.class, Minimax2.MIN, 2, 2, BuiltInAtomicType.ANY_ATOMIC, OPT, CORE, BASE)
                .arg(0, BuiltInAtomicType.ANY_ATOMIC, STAR, EMPTY)
                .arg(1, BuiltInAtomicType.STRING, ONE, null);


        //copy Saxon's XPath functions into our XForms functions
        /**
         * Depends on the class structure of {@link net.sf.saxon.functions.StandardFunction}
         */
        try {
            final Field fFunctionTable = StandardFunction.class.getDeclaredField("functionTable");
            fFunctionTable.setAccessible(true);
            final Map<String, StandardFunction.Entry> stdFunctionTable = (Map<String, StandardFunction.Entry>) fFunctionTable.get(null);
            for (final Map.Entry<String, StandardFunction.Entry> entry : stdFunctionTable.entrySet()) {
                final String xfKey = xf(entry.getKey());
                //prefer XForms functions with the same name and arity over XPath functions
                if (!functionTable.containsKey(xfKey)) {
                    functionTable.put(xfKey, copyEntryToXf(entry.getValue()));
                }
            }
        } catch(final NoSuchFieldException e) {
            LOGGER.error("Unable to incorporate XPath functions into the XForms function namespace", e);
        } catch(final IllegalAccessException e) {
            LOGGER.error("Unable to incorporate XPath functions into the XForms function namespace", e);
        }
    }

    /**
     * Adapted from {@link net.sf.saxon.functions.StandardFunction#register(String, Class, int, int, int, ItemType, int, int, int)}
     * so that we can place these functions into the XForms specific namespace
     */
    private static StandardFunction.Entry registerXf(String name, Class implementationClass, int opcode, int minArguments, int maxArguments, ItemType itemType, int cardinality, int applicability, int properties) {
        name = xf(name);
        StandardFunction.Entry e = StandardFunction.makeEntry(name, implementationClass, opcode, minArguments, maxArguments,
                itemType, cardinality, applicability, properties);
        functionTable.put(name, e);
        return e;
    }

    /**
     * Copies a StandardFunctionEntry, the copy will be in the XForms namespace
     *
     * Somewhat fragile method, needs to be updates
     * if the fields in {@link net.sf.saxon.functions.StandardFunction.Entry}
     * change in future
     */
    private static StandardFunction.Entry copyEntryToXf(final StandardFunction.Entry entry) {
        final StandardFunction.Entry xfEntry = new StandardFunction.Entry();
        xfEntry.name = xf(entry.name);  //place into the correct namespace
        xfEntry.implementationClass = entry.implementationClass;
        xfEntry.opcode = entry.opcode;
        xfEntry.minArguments = entry.minArguments;
        xfEntry.maxArguments = entry.maxArguments;
        xfEntry.itemType = entry.itemType;
        xfEntry.cardinality = entry.cardinality;
        xfEntry.applicability = entry.applicability;
        xfEntry.usage = entry.usage;
        xfEntry.argumentTypes = entry.argumentTypes;
        xfEntry.resultIfEmpty = entry.resultIfEmpty;
        xfEntry.properties = entry.properties;
        return xfEntry;
    }

    private static String xf(final String localArity) {
        return "{" + NamespaceConstants.XFORMS_NS + "}" + localArity;
    }

    @Override
    protected String getFunctionNamespace() {
        return functionNamespace;
    }
}
