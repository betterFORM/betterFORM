/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xpath.impl.saxon.sxpath;

import net.sf.saxon.Configuration;
import net.sf.saxon.Platform;
import net.sf.saxon.event.LocationProvider;
import net.sf.saxon.expr.*;
import net.sf.saxon.functions.*;
import net.sf.saxon.instruct.Executable;
import net.sf.saxon.instruct.LocationMap;
import net.sf.saxon.java.JavaPlatform;
import net.sf.saxon.om.NamePool;
import net.sf.saxon.om.NamespaceConstant;
import net.sf.saxon.sort.StringCollator;
import net.sf.saxon.type.BuiltInAtomicType;

import javax.xml.transform.SourceLocator;

/**
 * An abstract and configurable implementation of the StaticContext interface,
 * which defines the static context of an XPath expression.
 *
 * <p>This class implements those parts of the functionality of a static context
 * that tend to be common to most implementations: simple-valued properties such
 * as base URI and default element namespace; availability of the standard
 * function library; and support for collations.</p>
*/

public abstract class AbstractStaticContext implements StaticContext {

    private String baseURI = null;
    private Configuration config;
    private LocationMap locationMap = new LocationMap();
    private Executable executable;
    private String defaultFunctionNamespace = NamespaceConstant.FN;
    private String defaultElementNamespace = NamespaceConstant.NULL;
    private boolean backwardsCompatible = false;

    /**
     * Set the Configuration. This is protected so it can be used only by subclasses;
     * the configuration will normally be set at construction time
     * @param config the configuration
     */

    protected void setConfiguration(Configuration config) {
        this.config = config;
        executable = new Executable(config);
        executable.setHostLanguage(Configuration.XPATH);
    }

    /**
     * Get the system configuration
     */

    public Configuration getConfiguration() {
        return config;
    }

    /**
     * Initialize the default function library for XPath.
     * This can be overridden using setFunctionLibrary().
     */

    protected final void setDefaultFunctionLibrary() {
        FunctionLibraryList lib = new FunctionLibraryList();
        lib.addFunctionLibrary(SystemFunctionLibrary.getSystemFunctionLibrary(StandardFunction.CORE));
        lib.addFunctionLibrary(getConfiguration().getVendorFunctionLibrary());
        lib.addFunctionLibrary(new ConstructorFunctionLibrary(getConfiguration()));
        //lib.addFunctionLibrary(new JavaExtensionLibrary(getConfiguration()));
        if (config.isAllowExternalFunctions()) {
            Platform platform = Configuration.getPlatform();
            if (platform instanceof JavaPlatform) {
                ((JavaPlatform) platform).addFunctionLibraries(lib, config,Configuration.XPATH);
            }
        }
        setFunctionLibrary(lib);
    }

    /**
     * Add a function library to the list of function libraries
     * @param library the function library to be added
     */

    protected final void addFunctionLibrary(FunctionLibrary library) {
        FunctionLibrary libraryList = executable.getFunctionLibrary();
        if (libraryList instanceof FunctionLibraryList) {
            ((FunctionLibraryList)libraryList).addFunctionLibrary(library);
        } else {
            throw new IllegalStateException("Registered function library cannot be extended");
        }
    }

    /**
     * Get the Executable (representing a complete stylesheet or query)
     * @return the executable
     */

    public Executable getExecutable() {
        return executable;
    }

    /**
     * Get the host language (XSLT, XQuery, XPath) used to implement the code in this container
     *
     * @return the value {@link net.sf.saxon.Configuration#XPATH}
     */

    public int getHostLanguage() {
        return Configuration.XPATH;
    }

    /**
     * Construct a dynamic context for early evaluation of constant subexpressions
     */

    public XPathContext makeEarlyEvaluationContext() {
        return new EarlyEvaluationContext(getConfiguration(), executable.getCollationTable());
    }


    public LocationMap getLocationMap() {
        return locationMap;
    }

    /**
     * Set the location map, which is used for translating location identifiers into URIs and line
     * numbers
     * @param locationMap the location map to be used
     */

    public void setLocationMap(LocationMap locationMap) {
        this.locationMap = locationMap;
    }

    /**
     * Set the base URI in the static context
     * @param baseURI the base URI of the expression
     */

    public void setBaseURI(String baseURI) {
        this.baseURI = baseURI;
    }

    /**
    * Get the Base URI, for resolving any relative URI's used
    * in the expression. Used by the document() function, resolve-uri(), etc.
    * @return "" if no base URI has been set
    */

    public String getBaseURI() {
        return baseURI==null ? "" : baseURI;
    }

    /**
     * Get the function library containing all the in-scope functions available in this static
     * context. This method is called by the XPath parser when binding a function call in the
     * XPath expression to an implementation of the function.
     */

    public FunctionLibrary getFunctionLibrary() {
        return executable.getFunctionLibrary();
    }

    /**
     * Set the function library to be used
     * @param lib the function library
     */

    public void setFunctionLibrary(FunctionLibrary lib) {
        executable.setFunctionLibrary(lib);
    }

    /**
    * Declare a named collation
    * @param name The name of the collation (technically, a URI)
    * @param comparator The StringCollator used to implement the collating sequence
    * @param isDefault True if this is to be used as the default collation
    */

    public void declareCollation(String name, StringCollator comparator, boolean isDefault) {
        CollationMap collations = executable.getCollationTable();
        collations.setNamedCollation(name, comparator);
        if (isDefault) {
            collations.setDefaultCollationName(name);
        }
    }


    /**
    * Get a named collation.
    * @return the collation identified by the given name, as set previously using declareCollation.
    * Return null if no collation with this name is found.
    */

    public StringCollator getCollation(String name) {
        return executable.getCollationTable().getNamedCollation(name);
    }

    /**
    * Get the name of the default collation.
    * @return the name of the default collation; or the name of the codepoint collation
    * if no default collation has been defined
    */

    public String getDefaultCollationName() {
        return executable.getCollationTable().getDefaultCollationName();
    }

    /**
    * Get the NamePool used for compiling expressions
    */

    public NamePool getNamePool() {
        return config.getNamePool();
    }

    /**
    * Issue a compile-time warning. This method is used during XPath expression compilation to
    * output warning conditions. The default implementation writes the message to System.err. To
    * change the destination of messages, create a subclass of StandaloneContext that overrides
    * this method.
    */

    public void issueWarning(String s, SourceLocator locator) {
        System.err.println("Warning: " + s);
    }

    /**
    * Get the system ID of the container of the expression. Used to construct error messages.
    * @return "" always
    */

    public String getSystemId() {
        return "";
    }


    /**
    * Get the line number of the expression within that container.
    * Used to construct error messages.
    * @return -1 always
    */

    public int getLineNumber() {
        return -1;
    }


    /**
     * Get the default XPath namespace URI
     * Return NamespaceConstant.NULL for the non-namespace
    */

    public String getDefaultElementNamespace() {
        return defaultElementNamespace;
    }

    /**
     * Set the default namespace for elements and types
     * @param uri the namespace to be used for unprefixed element and type names
     */

    public void setDefaultElementNamespace(String uri) {
        defaultElementNamespace = uri;
    }

    /**
     * Set the default function namespace
     * @param uri the namespace to be used for unprefixed function names
     */

    public void setDefaultFunctionNamespace(String uri) {
        defaultFunctionNamespace = uri;
    }

    /**
     * Get the default function namespace
     */

    public String getDefaultFunctionNamespace() {
        return defaultFunctionNamespace;
    }

    /**
     * Set XPath 1.0 compatibility mode on or off (by default, it is false)
     * @param compatible true if XPath 1.0 compatibility mode is to be set to true, false
     * if it is to be set to false.
     */

    public void setBackwardsCompatibilityMode(boolean compatible) {
        backwardsCompatible = compatible;
    }

    /**
     * Determine whether Backwards Compatible Mode is used
     * @return true if XPath 1.0 backwards compatibility has been selected (by default,
     * it is false)
     */

    public boolean isInBackwardsCompatibleMode() {
        return backwardsCompatible;
    }

    /**
     * Determine whether a built-in type is available in this context. This method caters for differences
     * between host languages as to which set of types are built in.
     *
     * @param type the supposedly built-in type. This will always be a type in the XS namespace.
     * @return true if this type can be used in this static context
     */

    public boolean isAllowedBuiltInType(BuiltInAtomicType type) {
        return true;
    }


    /**
     * Get the LocationProvider allowing location identifiers to be resolved.
     * @return the LocationProvider that translates location identifiers into URIs and line numbers
     */

    public LocationProvider getLocationProvider() {
        return locationMap;
    }



    /**
     * Replace one subexpression by a replacement subexpression
     *
     * @param original    the original subexpression
     * @param replacement the replacement subexpression
     * @return true if the original subexpression was found
     * @throws UnsupportedOperationException (always)
     */

    public boolean replaceSubExpression(Expression original, Expression replacement) {
        throw new UnsupportedOperationException();
    }

    /**
     * Return the public identifier.
     * <p/>
     * <p>The return value is the public identifier of the document
     * entity or of the external parsed entity in which the markup that
     * triggered the event appears.</p>
     *
     * @return null (always).
     * @see #getSystemId
     */
    public String getPublicId() {
        return null;
    }

    /**
     * Return the character position where the current document event ends.
     * @return -1 (no column number is available).
     * @see #getLineNumber
     */
    public int getColumnNumber() {
        return -1;
    }

}

