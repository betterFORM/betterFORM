package de.betterform.xml.xforms.xpath.saxon.function;

import net.sf.saxon.expr.Expression;
import net.sf.saxon.expr.StaticContext;
import net.sf.saxon.functions.FunctionLibrary;
import net.sf.saxon.functions.StandardFunction;
import net.sf.saxon.functions.SystemFunction;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.pattern.NodeKindTest;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.type.ItemType;
import net.sf.saxon.value.EmptySequence;
import net.sf.saxon.value.SequenceType;
import net.sf.saxon.value.Value;

import java.util.HashMap;


public abstract class XPathFunctionLibrary implements FunctionLibrary {
    protected static Value EMPTY = EmptySequence.getInstance();
    private static HashMap functionTable = new HashMap(20);
    protected static ItemType SAME_AS_FIRST_ARGUMENT = NodeKindTest.NAMESPACE;

    protected abstract String getFunctionNamespace();

    /**
     * Test whether a system function with a given name and arity is available. This supports
     * the function-available() function in XSLT. This method may be called either at compile time
     * or at run time.
     *
     * @param functionName the name of the function being tested
     * @param arity        The number of arguments. This is set to -1 in the case of the single-argument
     *                     function-available() function; in this case the method should return true if there is some
     */

    public boolean isAvailable(StructuredQName functionName, int arity) {
        String uri = functionName.getNamespaceURI();
        String local = functionName.getLocalName();
//        if (uri.equals(NamespaceConstants.XFORMS_NS)) {
        if (uri.equals(getFunctionNamespace())) {
//            StandardFunction.Entry entry = XFormsFunction.getFunction("{" + uri + "}" + local, arity);
            StandardFunction.Entry entry = getFunction("{" + uri + "}" + local, arity);
            if (entry == null) {
                return false;
            }
            return (arity == -1 || (arity >= entry.minArguments && arity <= entry.maxArguments));
        } else {
            return false;
        }
    }

    /**
     * Bind an extension function, given the URI and local parts of the function name,
     * and the list of expressions supplied as arguments. This method is called at compile
     * time.
     *
     * @param functionName the name of the function to be bound
     * @param staticArgs   The expressions supplied statically in the function call. The intention is
     *                     that the static type of the arguments (obtainable via getItemType() and getCardinality() may
     *                     be used as part of the binding algorithm.
     * @param env
     * @return An object representing the extension function to be called, if one is found;
     *         null if no extension function was found matching the required name and arity.
     * @throws net.sf.saxon.trans.XPathException
     *          if a function is found with the required name and arity, but
     *          the implementation of the function cannot be loaded or used; or if an error occurs
     *          while searching for the function; or if this function library "owns" the namespace containing
     *          the function call, but no function was found.
     */

    public Expression bind(StructuredQName functionName, Expression[] staticArgs, StaticContext env) throws XPathException {
        String uri = functionName.getNamespaceURI();
        String local = functionName.getLocalName();
//        if (uri.equals(NamespaceConstants.XFORMS_NS)) {
        if (uri.equals(getFunctionNamespace())) {
//            StandardFunction.Entry entry = XFormsFunction.getFunction("{" + uri + "}" + local, staticArgs.length);
            StandardFunction.Entry entry = getFunction("{" + uri + "}" + local, staticArgs.length);
            if (entry == null) {
                return null;
            }
            Class functionClass = entry.implementationClass;
            SystemFunction f;
            try {
                f = (SystemFunction) functionClass.newInstance();
            }
            catch (Exception err) {
                throw new AssertionError("Failed to load system function: " + err.getMessage());
            }
            f.setDetails(entry);
            f.setFunctionName(functionName);

            f.setArguments(staticArgs);
            checkArgumentCount(staticArgs.length, entry.minArguments, entry.maxArguments, local);
            return f;
        } else {
            return null;
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
        if (num == 1)
            return " argument";
        return " arguments";
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
     * @return the entry describing the function. The entry is incomplete, it does not yet contain information about the
     *         function arguments.
     */
    protected static StandardFunction.Entry register(String name, Class implementationClass, int opcode, int minArguments, int maxArguments, ItemType itemType, int cardinality) {
        StandardFunction.Entry e = makeEntry(name, implementationClass, opcode, minArguments, maxArguments, itemType, cardinality);
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
     * @return the entry describing the function. The entry is incomplete, it does not yet contain information about the
     *         function arguments.
     */
    public static StandardFunction.Entry makeEntry(String name, Class implementationClass, int opcode, int minArguments, int maxArguments, ItemType itemType, int cardinality) {
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
        if (maxArguments > 100) {
            // special case for concat()
            e.argumentTypes = new SequenceType[1];
            e.resultIfEmpty = new Value[1];
        } else {
            e.argumentTypes = new SequenceType[maxArguments];
            e.resultIfEmpty = new Value[maxArguments];
        }
        return e;
    }

    /**
     * Add information to a function entry about the argument types of the function
     *
     * @param e           the entry for the function
     * @param a           the position of the argument, counting from zero
     * @param type        the item type of the argument
     * @param cardinality the cardinality of the argument
     */

    public static void arg(StandardFunction.Entry e, int a, ItemType type, int cardinality) {
        try {
            e.argumentTypes[a] = SequenceType.makeSequenceType(type, cardinality);
        }
        catch (ArrayIndexOutOfBoundsException err) {
            System.err.println("Internal Saxon error: Can't set argument " + a + " of " + e.name);
        }
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

    public static void arg(StandardFunction.Entry e, int a, ItemType type, int cardinality, Value resultIfEmpty) {
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
