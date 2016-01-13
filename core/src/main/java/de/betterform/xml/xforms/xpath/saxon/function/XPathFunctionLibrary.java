/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.*;
import net.sf.saxon.functions.FunctionLibrary;
import net.sf.saxon.functions.StandardFunction;
import net.sf.saxon.functions.SystemFunctionCall;
import net.sf.saxon.functions.SystemFunctionLibrary;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.trans.SymbolicName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceType;

import java.util.HashMap;
import java.util.Map;

/**
 * This class heavily borrows from {@link net.sf.saxon.functions.SystemFunctionLibrary}
 * in particular special attention needs to be paid to making
 * {@link XPathFunctionLibrary#bind} and {@link net.sf.saxon.functions.SystemFunctionLibrary#bind}
 * as similar as possible when updating to newer Saxon versions
 */
public abstract class XPathFunctionLibrary implements FunctionLibrary {
    protected static Sequence EMPTY = EmptySequence.getInstance();
    protected static Map<String, StandardFunction.Entry> functionTable = new HashMap<String, StandardFunction.Entry>(200);
    private static final long serialVersionUID = -6673788638743556161L;

    protected abstract String getFunctionNamespace();


    /**
     * Adapted from {@link net.sf.saxon.functions.SystemFunctionLibrary#bind(SymbolicName, Expression[], StaticContext, Container)}
     * so that we can operate over several namespaces by calling `getFunctionNamespace`
     */
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

    /**
     * Adapted from {@link net.sf.saxon.functions.SystemFunctionLibrary#isAvailable(SymbolicName)}
     * so that we can operate over several namespaces by calling `getFunctionNamespace`
     */
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
     * Duplicated from {@link net.sf.saxon.functions.SystemFunctionLibrary#checkArgumentCount(int, int, int, String)}
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
     * Duplicated from {@link net.sf.saxon.functions.SystemFunctionLibrary#pluralArguments(int)}
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
     * Duplicated from {@link net.sf.saxon.functions.SystemFunctionLibrary#copy()}
     */
    public FunctionLibrary copy() {
        return this;
    }

    /**
     * Duplicated from {@link net.sf.saxon.functions.StandardFunction#getFunction(java.lang.String, int)}
     */
    public static StandardFunction.Entry getFunction(String name, int arity) {
        if (arity == -1) {
            for (int i = 0; i < 10; i++) {
                StandardFunction.Entry e = getFunction(name, i);
                if (e != null) {
                    return e;
                }
            }
            return null;
        }
        // try first for an entry of the form name#arity
        StandardFunction.Entry e = functionTable.get(name + '#' + arity);
        if (e != null) {
            return e;
        }
        // try for a generic entry
        return functionTable.get(name);
    }
}
