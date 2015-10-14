/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.*;
import net.sf.saxon.functions.FunctionLibrary;
import net.sf.saxon.functions.StandardFunction;
import net.sf.saxon.functions.SystemFunctionCall;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.trans.SymbolicName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceType;

import java.util.HashMap;

/**
 * This class heavily borrows from {@link net.sf.saxon.functions.SystemFunctionLibrary}
 * in particular special attention needs to be paid to making
 * {@link XPathFunctionLibrary#bind} and {@link net.sf.saxon.functions.SystemFunctionLibrary#bind}
 * as similar as possible when updating to newer Saxon versions
 */
public abstract class XPathFunctionLibrary implements FunctionLibrary {
    protected static Sequence EMPTY = EmptySequence.getInstance();
    private static HashMap functionTable = new HashMap(20);
    protected static ItemType SAME_AS_FIRST_ARGUMENT = NodeKindTest.NAMESPACE;
    private static final long serialVersionUID = -6673788638743556161L;

    protected abstract String getFunctionNamespace();

    public Expression bind(SymbolicName symbolicName,
            Expression[] staticArgs, StaticContext env,
            Container container) throws XPathException {
        StructuredQName functionName = symbolicName.getComponentName();
        int arity = symbolicName.getArity();
        String uri = functionName.getURI();
        if (uri.equals(getFunctionNamespace())) {
            String local = functionName.getLocalPart();
            StandardFunction.Entry entry = getFunction("{" + uri + "}" + local, arity);
            if (entry == null) {
                if (getFunction("{" + uri + "}" + local, -1) == null) {
                    XPathException err = new XPathException("Unknown function " + local + "()");
                    err.setErrorCode("XPST0017");
                    err.setIsStaticError(true);
                    throw err;
                } else {
                    XPathException err = new XPathException("Function " + local + "() cannot be called with "
                        + pluralArguments(arity));
                    err.setErrorCode("XPST0017");
                    err.setIsStaticError(true);
                    throw err;
                }
            }
            int originalArity = arity;
            if ((entry.properties & StandardFunction.IMP_CX_I) != 0 && arity == 0) {
                // Add the context item as an implicit argument
                staticArgs = new Expression[1];
                staticArgs[0] = new ContextItemExpression();
                arity = 1;
                entry = StandardFunction.getFunction(local, arity);
            }
            if ((entry.properties & StandardFunction.IMP_CX_D) != 0) {
                // Add the context document as an implicit argument
                Expression[] newArgs = new Expression[arity + 1];
                System.arraycopy(staticArgs, 0, newArgs, 0, arity);
                newArgs[arity] = new RootExpression();
                staticArgs = newArgs;
                arity++;
                entry = StandardFunction.getFunction(local, arity);
            }
            final Class functionClass = entry.implementationClass;
            final SystemFunctionCall f;
            try {
                f = (SystemFunctionCall) functionClass.newInstance();
            } catch (Exception err) {
                throw new AssertionError("Failed to load function {" + uri + "}" + local + " - " + err.getMessage());
            }
            f.setOriginalArity(originalArity);
            f.setDetails(entry);
            f.setFunctionName(functionName);
            checkArgumentCount(arity, entry.minArguments, entry.maxArguments, local);
            if (staticArgs != null) {
                f.setArguments(staticArgs);
            }
            f.setContainer(container);
            f.bindStaticContext(env);
            return f;
        } else {
            return null;
        }
    }

    public boolean isAvailable(final SymbolicName functionName) {
        final String uri = functionName.getComponentName().getURI();
        if (uri.equals(getFunctionNamespace())) {
            final String local = functionName.getComponentName().getLocalPart();
            final StandardFunction.Entry entry = getFunction("{" + uri + "}" + local, functionName.getArity());
            return entry != null && (functionName.getArity() == -1 || (entry.minArguments <= functionName.getArity() && entry.maxArguments >= functionName.getArity()));
        } else {
            return false;
        }
    }

    /**
     * Check number of arguments. <BR>
     * A convenience routine for use in subclasses.
     *
     * @param min the minimum number of arguments allowed
     * @param max the maximum number of arguments allowed
     * @return the actual number of arguments
     * @throws net.sf.saxon.trans.XPathException
     *          if the number of arguments is out of range
     */

    private int checkArgumentCount(int numArgs, int min, int max, String local) throws XPathException {
        if (min == max && numArgs != min) {
//            throw new StaticError("Function " + Err.wrap(local, Err.FUNCTION) + " must have " + min + pluralArguments(min));
            throw new XPathException("Function " + local + " must have " + min + pluralArguments(min));
        }
        if (numArgs < min) {
//            throw new StaticError("Function " + Err.wrap(local, Err.FUNCTION) + " must have at least " + min + pluralArguments(min));
            throw new XPathException("Function " + local + " must have at least " + min + pluralArguments(min));

        }
        if (numArgs > max) {
//            throw new StaticError("Function " + Err.wrap(local, Err.FUNCTION) + " must have no more than " + max + pluralArguments(max));
            throw new XPathException("Function " + local + " must have  no more than " + max + pluralArguments(max));
        }
        return numArgs;
    }

    /**
     * Utility routine used in constructing error messages
     */

    private static String pluralArguments(int num) {
        if (num == 0) {
            return "zero arguments";
        }
        if (num == 1) {
            return "one argument";
        }
        return num + " arguments";
    }

    /**
     * This method creates a copy of a FunctionLibrary: if the original FunctionLibrary allows new functions to be
     * added, then additions to this copy will not affect the original, or vice versa.
     *
     * @return a copy of this function library. This must be an instance of the original class.
     */

    public FunctionLibrary copy() {
        return this;
    }

    /**
     * Register a system function in the table of function details.
     *
     * @param name                the function name
     * @param implementationClass the class used to implement the function
     * @param opcode              identifies the function when a single class implements several functions
     * @param minArguments        the minimum number of arguments required
     * @param maxArguments        the maximum number of arguments allowed
     * @param itemType            the item type of the result of the function
     * @param cardinality         the cardinality of the result of the function
     * @param applicability       the host languages (and versions thereof) in which this function is available
     * @param properties
     * @return the entry describing the function. The entry is incomplete, it does not yet contain information
     *         about the function arguments.
     */

    /*@NotNull*/
    public static StandardFunction.Entry register(String name,
                                 Class implementationClass,
                                 int opcode,
                                 int minArguments,
                                 int maxArguments,
                                 ItemType itemType,
                                 int cardinality,
                                 int applicability,
                                 int properties) {
        StandardFunction.Entry e = makeEntry(name, implementationClass, opcode, minArguments, maxArguments,
            itemType, cardinality, applicability, properties);
        functionTable.put(name, e);
        return e;
    }


    /**
     * Make a table entry describing the signature of a function, with a reference to the implementation class.
     *
     * @param name                the function name
     * @param implementationClass the class used to implement the function
     * @param opcode              identifies the function when a single class implements several functions
     * @param minArguments        the minimum number of arguments required
     * @param maxArguments        the maximum number of arguments allowed
     * @param itemType            the item type of the result of the function
     * @param cardinality         the cardinality of the result of the function
     * @param applicability       the host languages (and versions of) in which this function is available
     * @return the entry describing the function. The entry is incomplete, it does not yet contain information
     *         about the function arguments.
     */
    public static StandardFunction.Entry makeEntry(String name, Class implementationClass, int opcode,
                                  int minArguments, int maxArguments,
                                  ItemType itemType, int cardinality, int applicability, int properties) {
        StandardFunction.Entry e = new StandardFunction.Entry();
        int hash = name.indexOf('#');
        if (hash < 0) {
            e.name = name;
        } else {
            e.name = name.substring(0, hash);
        }
        e.implementationClass = implementationClass;
        e.opcode = opcode;
        e.minArguments = minArguments;
        e.maxArguments = maxArguments;
        e.itemType = itemType;
        e.cardinality = cardinality;
        e.applicability = applicability;
        e.properties = properties;
        if (maxArguments > 100) {
            // special case for concat()
            e.argumentTypes = new SequenceType[1];
            e.resultIfEmpty = new Sequence[1];
            e.usage = new OperandUsage[1];
        } else {
            e.argumentTypes = new SequenceType[maxArguments];
            e.resultIfEmpty = new Sequence[maxArguments];
            e.usage = new OperandUsage[maxArguments];
        }
        return e;
    }

    /**
     * Add information to a function entry about the argument types of the function
     *
     * @param e             the entry for the function
     * @param a             the position of the argument, counting from zero
     * @param type          the item type of the argument
     * @param cardinality   the cardinality of the argument
     * @param resultIfEmpty the value returned by the function if an empty sequence appears as the value,
     *                      when this result is unaffected by any other arguments
     */

    public static void arg(StandardFunction.Entry e, int a, ItemType type, int cardinality, Sequence resultIfEmpty) {
        try {
            e.argumentTypes[a] = SequenceType.makeSequenceType(type, cardinality);
            e.resultIfEmpty[a] = resultIfEmpty;
        } catch (ArrayIndexOutOfBoundsException err) {
            System.err.println("Internal Saxon error: Can't set argument " + a + " of " + e.name);
        }
    }

    /**
     * Get the table entry for the function with a given name
     *
     * @param name the name of the function. This may be an unprefixed
     *             local-name for functions in the system namespace, or may
     *             use the conventional prefix "saxon:" in the case of Saxon
     *             extension functions that are specially recognized
     * @return if the function name is known, an Entry containing
     *         information about the function. Otherwise, null
     */

    public static StandardFunction.Entry getFunction(String name, int arity) {
        // try first for an entry of the form name#arity
        StandardFunction.Entry e = (StandardFunction.Entry) functionTable.get(name + '#' + arity);
        if (e != null) {
            return e;
        }
        // try for a generic entry
        return (StandardFunction.Entry)functionTable.get(name);
		}
}
