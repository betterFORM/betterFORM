/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.submission;

import de.betterform.connector.SubmissionHandler;
import de.betterform.connector.http.AbstractHTTPConnector;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.DefaultAction;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.xforms.*;
import de.betterform.xml.xforms.action.UpdateHandler;
import de.betterform.xml.xforms.exception.*;
import de.betterform.xml.xforms.model.Instance;
import de.betterform.xml.xforms.model.Model;
import de.betterform.xml.xforms.model.bind.Bind;
import de.betterform.xml.xforms.model.constraints.RelevanceSelector;
import de.betterform.xml.xforms.model.constraints.SubmissionValidatorMode;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.UIElementState;
import de.betterform.xml.xforms.ui.state.BoundElementState;
import de.betterform.xml.xforms.xpath.saxon.function.XPathFunctionContext;
import de.betterform.xml.xpath.impl.saxon.XPathCache;
import de.betterform.xml.xpath.impl.saxon.XPathUtil;
import net.sf.saxon.dom.DocumentWrapper;
import net.sf.saxon.om.Item;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.events.Event;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;


/**
 * Implementation of XForms Submission Element.
 *
 * @author Ulrich Nicolas Liss&eacute;, Joern Turner, Lars Windauer
 * @version $Id: Submission.java 3510 2008-08-31 14:39:56Z lars $
 */
public class Submission extends BindingElement implements DefaultAction {
    private static Log LOGGER = LogFactory.getLog(Submission.class);

    private String action = null;
    private AttributeOrValueChild method = null;
    private String version = null;
    private Boolean indent = null;
    private String mediatype = null;
    private String encoding = null;
    private Boolean omitxmldeclaration = null;
    private Boolean standalone = null;
    private String cdatasectionelements = null;
    private String separator = null;
    private List<String> includenamespaceprefixes = null;
    private String replace = null;
    private String instance = null;
    private String targetExpr = null;

    private Boolean validate = null;
    private Boolean relevant = null;
    private AttributeOrValueChild resource = null;
    protected ArrayList<Header> submissionHeaders = new ArrayList<Header>();
    private String targetModelId;
    private String serialization;
    private static final String EMBEDNODE = "embedElement";
    private static final String DOCUMENT = "document";

    /**
     * Creates a new Submission object.
     *
     * @param element DOM Element of this submission
     * @param model   the parent Model
     */
    public Submission(Element element, Model model) {
        super(element, model);
    }

    // todo: refactor submission driver to have setters for these (IOC)
    // submission options

    /**
     * Returns the <code>action</code> submission option.
     *
     * @return the <code>action</code> submission option.
     */
    @Deprecated
    public String getAction() {
        return this.action;
    }

    /**
     * Returns the <code>resource</code> submission option.
     *
     * @return the <code>resource</code> submission option.
     * @throws XFormsException 
     */
    public String getResource() throws XFormsException {
        return this.resource.getValue();
    }

    /**
     * Returns the <code>cdata-section-elements</code> submission option.
     *
     * @return the <code>cdata-section-elements</code> submission option.
     */
    public String getCDATASectionElements() {
        return this.cdatasectionelements;
    }

    /**
     * Returns the <code>encoding</code> submission option.
     *
     * @return the <code>encoding</code> submission option.
     */
    public String getEncoding() {
        return this.encoding;
    }

    /**
     * Returns the <code>includenamespaceprefixes</code> submission option.
     *
     * @return the <code>includenamespaceprefixes</code> submission option.
     */
    public List<String> getIncludeNamespacePrefixes() {
        return this.includenamespaceprefixes;
    }

    /**
     * Returns the <code>indent</code> submission option.
     *
     * @return the <code>indent</code> submission option.
     */
    public Boolean getIndent() {
        return this.indent;
    }

    /**
     * Returns the <code>mediatype</code> submission option.
     *
     * @return the <code>mediatype</code> submission option.
     */
    public String getMediatype() {
        return this.mediatype;
    }

    /**
     * Returns the <code>method</code> submission option.
     *
     * @return the <code>method</code> submission option.
     * @throws XFormsException 
     */
    public String getMethod() throws XFormsException {
        return this.method.getValue();
    }

    /**
     * Returns the <code>omit-xml-declaration</code> submission option.
     *
     * @return the <code>omit-xml-declaration</code> submission option.
     */
    public Boolean getOmitXMLDeclaration() {
        return this.omitxmldeclaration;
    }

    /**
     * Returns the <code>separator</code> submission option.
     *
     * @return the <code>separator</code> submission option.
     */
    public String getSeparator() {
        return this.separator;
    }

    /**
     * Returns the <code>standalone</code> submission option.
     *
     * @return the <code>standalone</code> submission option.
     */
    public Boolean getStandalone() {
        return this.standalone;
    }

    /**
     * Returns the <code>version</code> submission option.
     *
     * @return the <code>version</code> submission option.
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Returns the submission <code>replace</code> mode.
     *
     * @return the submission <code>replace</code> mode.
     */
    public String getReplace() {
        return this.replace;
    }

    /**
     * Returns the <code>instance</code> submission option.
     *
     * @return the <code>instance</code> submission option.
     */
    public String getInstance() {
        return this.instance;
    }

    // XForms 1.1 support

    /**
     * Returns the <code>relevant</code> submission option.
     *
     * @return the <code>relevant</code> submission option.
     */
    public Boolean getRelevant() {
        return this.relevant;
    }

    /**
     * Returns the <code>validate</code> submission option.
     *
     * @return the <code>validate</code> submission option.
     */
    public Boolean getValidate() {
        return this.validate;
    }


    public String getLocationPath() {
        return this.locationPath;
    }

    public String getSerialization(){
        return this.serialization;
    }

    // lifecycle methods

    /**
     * Performs element init.
     *
     * @throws XFormsException if any error occurred during init.
     *                         todo: should call BindingElement.init() but some methods have to be refactored first
     */
    public void init() throws XFormsException {
        if (getLogger().isTraceEnabled()) {
            getLogger().trace(this + " init");
        }

        initializeDefaultAction();
        initializeInstanceNode();
        updateXPathContext();
        initializeSubmission();
        Initializer.initializeActionElements(this.model, this.element, null);
    }

    /**
     * Performs element disposal.
     *
     * @throws XFormsException if any error occurred during disposal.
     */
    public void dispose() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " dispose");
        }

        disposeDefaultAction();
    }

    // lifecycle template methods

    /**
     * Initializes the default action.
     */
    protected void initializeDefaultAction() {
        super.initializeDefaultAction();
        this.container.getXMLEventService().registerDefaultAction(this.target, XFormsEventNames.SUBMIT, this);
    }

    /**
     * Updates all ui children of this element.
     *
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if any error occurred during update.
     */
    protected void updateChildren() throws XFormsException {
        //dirty overwrite - but calls to updateChildren must be refactored above in the hierarchy first
    }

    /**
     * Performs submission attribute defaulting.
     * <p/>
     * Supports XForms 1.1 relevant and validate attributes. However, this
     * support should be externalized in a - say Submission11 - class which
     * simply overwrites this template method.
     */
    protected void initializeSubmission() throws XFormsException {
        // 1. init binding context
        // path expression
        this.locationPath = "/";
        String refAttribute = getXFormsAttribute(REF_ATTRIBUTE);
        if (refAttribute != null) {
            this.locationPath = refAttribute;
        }
        String bindAttribute = getXFormsAttribute(BIND_ATTRIBUTE);
        if (bindAttribute != null) {
            Object bindObject = this.container.lookup(bindAttribute);
            if (bindObject == null || !(bindObject instanceof Bind)) {
                throw new XFormsBindingException("invalid bind id at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, bindAttribute);
            }

            this.locationPath = ((Bind) bindObject).getLocationPath();
        }

        // instance id
        this.instanceId = this.model.computeInstanceId(this.locationPath);

        // 2. submission options
        // get required method attribute
        this.method = new AttributeOrValueChild(this.element, this.model, METHOD_ATTRIBUTE);
        this.method.init();
        if (this.method == null) {
            // complain
            throw new XFormsLinkException("no method specified for submission", this.target, null);
        }

        // get optional version attribute
        this.version = getXFormsAttribute(VERSION_ATTRIBUTE);
        if(this.version == null){
            this.version = "1.0"; // setting default
        }

        // get optional indent attribute
        String indentAttribute = getXFormsAttribute(INDENT_ATTRIBUTE);
        if (indentAttribute != null) {
            this.indent = Boolean.valueOf(indentAttribute);
        } else {
            this.indent = false; // setting default
        }

        // get optional mediatype attribute
        this.mediatype = getXFormsAttribute(MEDIATYPE_ATTRIBUTE);
        if(this.mediatype == null){
            this.mediatype = "application/xml"; //setting default
        }

        // get optional encoding attribute
        this.encoding = getXFormsAttribute(ENCODING_ATTRIBUTE);
        if(this.encoding == null){
            this.encoding = "UTF-8"; // setting default
        }

        // get optional omit-xml-declaration attribute
        String omitxmldeclarationAttribute = getXFormsAttribute(OMIT_XML_DECLARATION_ATTRIBUTE);
        if (omitxmldeclarationAttribute != null) {
            this.omitxmldeclaration = "true".equals(omitxmldeclarationAttribute) || "1".equals(omitxmldeclarationAttribute);
        }else{
            this.omitxmldeclaration = false;
        }

        // get optional standalone attribute
        String standaloneAttribute = getXFormsAttribute(STANDALONE_ATTRIBUTE);
        if (standaloneAttribute != null) {
            this.standalone = Boolean.valueOf(standaloneAttribute);
        }

        // get optional cdata-section-elements attribute
        this.cdatasectionelements = getXFormsAttribute(CDATA_SECTION_ELEMENTS_ATTRIBUTE);

        // get optional action attribute
        this.separator = getXFormsAttribute(SEPARATOR_ATTRIBUTE);
        if (this.separator == null) {
            // default per schema
            this.separator = "&";
        }

        // get optional includenamespaceprefixes attribute
        String includenamespaceprefixesAttribute = getXFormsAttribute(INCLUDENAMESPACEPREFIXES_ATTRIBUTE);
        if (includenamespaceprefixesAttribute != null) {
            StringTokenizer tokenizer = new StringTokenizer(includenamespaceprefixesAttribute);
            this.includenamespaceprefixes = new ArrayList<String>(tokenizer.countTokens());

            while (tokenizer.hasMoreTokens()) {
                this.includenamespaceprefixes.add(tokenizer.nextToken());
            }
        }

        // get optional replace attribute
        this.replace = getXFormsAttribute(REPLACE_ATTRIBUTE);
        if (this.replace == null) {
            // default per schema
            this.replace = "all";
        }

        // get optional instance attribute
        this.instance = getXFormsAttribute(INSTANCE_ATTRIBUTE);

        // get optional target attribute

        this.targetExpr = getXFormsAttribute(TARGETREF_ATTRIBUTE);
        if(targetExpr == null){
            //try deprecated 'target' attrbute
            if(getXFormsAttribute(TARGET_ATTRIBUTE) != null) {
                this.targetExpr = getXFormsAttribute(TARGET_ATTRIBUTE);
                LOGGER.warn("'target' Attribute is deprecated - Please use 'targetref' instead.");                
            }
        }

        // check for cross model submission
        if (this.targetExpr != null && this.targetExpr.startsWith("model('")) {
            targetModelId = this.targetExpr.substring(this.targetExpr.indexOf("'") + 1);
            targetModelId = targetModelId.substring(0, targetModelId.indexOf("'"));

            if (targetModelId != null && !targetModelId.equals("")) {
                this.targetExpr = this.targetExpr.replace("model('" + targetModelId + "')", "");
                if(targetExpr.equals("")){
                    targetExpr = null;
                }
            }

        }


        // 3. XForms 1.1 support

        // XForms 1.0 Version
        if (this.container.getVersion().equals(Container.XFORMS_1_0)) {
            this.action = getXFormsAttribute(ACTION_ATTRIBUTE);
            if (this.action == null) {
                throw new XFormsLinkException("no action or resource specified for submission", this.target, null);
            }
        } else {
            // initialize XForms 1.1 submission children
            initializeSubmissionOptions();
            // XForms Version > 1.0 or not set
            this.resource = new AttributeOrValueChild(this.element, this.model, RESOURCE_ATTRIBUTE);
            this.resource.init();
            if (!resource.isAvailable()) {
                // handle action attribtue
                this.action = getXFormsAttribute(ACTION_ATTRIBUTE);
            }
            // either resource or action must be set
            if (!this.resource.isAvailable() && this.action == null) {
                // complain
                throw new XFormsLinkException("no action or resource specified for submission", this.target, null);
            } else if (this.resource == null) {
                getLogger().warn(toString() + " relying on deprecated action attribute");
            }
        }

        // ##### validation and serailization must be handled together #####
        this.validate = true; //default

        //get serialization attribute. If serialization is 'none' validate defaults to false
        this.serialization = getXFormsAttribute(SERIALIZATION_ATTRIBUTE);
        if(this.serialization != null && this.serialization.equalsIgnoreCase("none")){
            this.validate = false; // setting default when not serialized that might get overwritten by evaluation of validateAttribute below
        }

        // get optional validate attribute
        String validateAttribute = getXFormsAttribute(VALIDATE_ATTRIBUTE);
        if(validateAttribute != null){
            this.validate = Boolean.valueOf(validateAttribute);
        }

        // get optional relevant attribute
        String relevantAttribute = getXFormsAttribute(RELEVANT_ATTRIBUTE);
        this.relevant = relevantAttribute != null ? Boolean.valueOf(relevantAttribute) : Boolean.TRUE;

    }

    /**
     * Disposes the default action.
     */
    protected void disposeDefaultAction() {
        this.container.getXMLEventService().deregisterDefaultAction(this.target, XFormsEventNames.SUBMIT, this);
    }

    /**
     * Factory method for the element state.
     *
     * @return an element state implementation or <code>null</code> if no
     *         state keeping is required.
     * @throws de.betterform.xml.xforms.exception.XFormsException
     *          if an error occurred during creation.
     */
    protected UIElementState createElementState() throws XFormsException {
        return hasBindingExpression() ? new BoundElementState() : null;
    }

    /**
     * Returns the logger object.
     *
     * @return the logger object.
     */
    protected Log getLogger() {
        return LOGGER;
    }

    // implementation of 'de.betterform.xml.events.DefaultAction'

    /**
     * Performs the implementation specific default action for this event.
     *
     * @param event the event.
     */
    public void performDefault(Event event) {
        try {
            if (event.getType().equals(XFormsEventNames.SUBMIT)) {
                 submit();
            }
        }
        catch (Exception e) {
            // handle exception and stop event propagation
            this.container.handleEventException(e);
            event.stopPropagation();
        }
    }

    /**
     * Implements <code>xforms-submit</code> default action.
     */
    protected void submit() throws XFormsException {
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " submit");
        }

        try{
            updateXPathContext();
        }catch (Exception xe){
            LOGGER.warn("Exception occured while updating nodeset bound by submission " + DOMUtil.getCanonicalPath(this.element) + " " + xe.getMessage());
            LOGGER.warn("Exception occured while updating nodeset bound by submission - exception will be ignored. Submission cancelled");
            return;
        }

        // get instance object and location path to submit
        Instance instanceObject = this.model.getInstance(getInstanceId());
/*
        if(instanceObject == null) {
            instanceObject = targetModel.getInstance(getInstanceId());
        }
*/
        String pathExpression = getLocationPath();
        if (!XPathUtil.existsNode(instanceObject.getInstanceNodeset(), 1, locationPath, getPrefixMapping(), this.xpathFunctionContext)) {
            throw new XFormsSubmitError("nodeset is empty at: " + DOMUtil.getCanonicalPath(this.getElement()), this.getTarget(), XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, XFormsConstants.NO_DATA, getResourceURI()));
        }

        //todo: when serialization is 'none' validation and relevance selection should be skipped if not explicitly set as attributes
        // validate instance items
        submitValidate(instanceObject, pathExpression, getPrefixMapping(), this.xpathFunctionContext);

        // select relevant items
        Node instanceNode = submitSelectRelevant(instanceObject, pathExpression);

        Map response;
        try {
            // todo: should be supported by serializers
            if (this.includenamespaceprefixes != null) {
                getLogger().warn(this + " submit: the 'includenamespaceprefixes' attribute is not supported yet");
            }

            processSubmissionOptions();


            // todo: refactor submission options to become a typed object, e.g. SubmissionOptions
            // todo: refactor submission response to become a typed object, e.g. SubmissionResponse
            // todo: refactor serializers to be set excplicitly


            // Look for resource submission option and possibly replace action
            // Implementation of the resource attribute version 1.1 feature
            // http://www.w3.org/TR/2007/CR-xforms11-20071129/#submit
            // chapter 11.1
            if (this.resource != null && this.resource.isAvailable()) {
                // obtain relative URI
//                String relativeURI = null;
//                relativeURI = instanceObject.getNodeValue(this.resource);
//
//                if (relativeURI != null) {
//                    // resolve uri and assign to action
//                    this.action = this.container.getConnectorFactory().getAbsoluteURI(relativeURI, this.element).toString();
//                }
                this.action = getResourceURI();
            }


            //do xforms-submit-serialize handling
    		final Element submissionBodyEl = this.element.getOwnerDocument().createElement("submission-body");
    		final Map<String, Object> info = new HashMap<String, Object>();
    		info.put(XFormsConstants.SUBMISSION_BODY, this.container.getDocumentWrapper(this.element).wrap(submissionBodyEl));

    		this.container.dispatch(this.id, XFormsEventNames.SUBMIT_SERIALIZE, info);
			submissionBodyEl.normalize();

            // serialize and transmit instance items
            SubmissionHandler sh = this.container.getConnectorFactory().createSubmissionHandler(this.action, this.element);
            if (submissionBodyEl.getFirstChild() == null) {
                response = sh.submit(this, instanceNode);
            }
            else {
                response = sh.submit(this, submissionBodyEl.getFirstChild());
            }
        }
        catch (XFormsInternalSubmitException e) {
        	Map<String, Object> info = XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, e.getErrorType(), getResourceURI(), e.getStatusCode(), null, e.getStatusText(), e.getResponseBodyAsString());
            throw new XFormsSubmitError("instance submission failed at: " + DOMUtil.getCanonicalPath(this.getElement()), e, this.getTarget(), info);
        }
        catch (Exception e) {
            String errorType;
            if(e instanceof XFormsInternalSubmitException){
                errorType = ((XFormsInternalSubmitException)e).getErrorType();
            } else {
                errorType = XFormsConstants.RESOURCE_ERROR;
            }            
            Map<String, Object> info = XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, errorType, getResourceURI());

            //todo: hacky - event context info construction must be reviewed - using exception cause as response-reason-phrase for now
            if (e.getCause() != null && e.getCause().getMessage() != null) {
                info.put("response-reason-phrase",e.getCause().getMessage());
            }
            throw new XFormsSubmitError("instance submission failed at: " + DOMUtil.getCanonicalPath(this.getElement()), e, this.getTarget(), info);
            //throw new XFormsSubmitError("instance submission failed", e, this.getTarget(), this.action);
        }

        // handle replace mode
        if (this.replace.equals("all")) {
            submitReplaceAll(response);
            return;
        }
        if (this.replace.equals("instance")) {
            submitReplaceInstance(response);
            return;
        }
        if (this.replace.equals("text")) {
            submitReplaceText(response);
            return;
        }
        if (this.replace.equals("none")) {
            submitReplaceNone(response);
            return;
        }
        if (this.replace.equals("embedHTML")){
            submitReplaceEmbedHTML(response);
            return;
        }
        if(this.replace.equals("new")){
            submitReplaceNew(response);
            return;
        }

        throw new XFormsSubmitError("unknown replace mode " + this.replace, this.getTarget(), XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, XFormsConstants.VALIDATION_ERROR, getResourceURI()));
    }

    /**
	 * @return
	 * @throws XFormsException
	 */
	private String getResourceURI() throws XFormsException {
		final String effectiveResource;
		if (this.resource.isAvailable()) {
			effectiveResource = this.resource.getValue();
		}
		else {
			effectiveResource = this.action;
		}
		return this.container.getConnectorFactory().getAbsoluteURI(effectiveResource, this.element).toString();
	}

    private void initializeSubmissionOptions() throws XFormsException {
        XFormsElementFactory elementFactory = model.getContainer().getElementFactory();
        NodeList childNodes = element.getChildNodes();
        // initialize Submission child elements 'header'
        for (int index = 0; index < childNodes.getLength(); index++) {
            Node node = childNodes.item(index);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element elementImpl = (Element) node;

                if ((XFormsElementFactory.isHeaderElement(elementImpl))) {
                    Header submissionHeader = (Header) elementFactory.createXFormsElement(elementImpl, this.model);
                    submissionHeader.init();
                    if(this.submissionHeaders == null) {
                        this.submissionHeaders = new ArrayList<Header>();
                    }
                    if(submissionHeader.getName() != null && !submissionHeader.getName().equals("")){
                        this.submissionHeaders.add(submissionHeader);    
                    }

                }
            }
        }
    }



    private void processSubmissionOptions() throws XFormsException {
        Map contextMap = this.container.getProcessor().getContext();
        RequestHeaders httpRequestHeader = null;
        if (contextMap.containsKey(AbstractHTTPConnector.HTTP_REQUEST_HEADERS)) {
            httpRequestHeader = (RequestHeaders) contextMap.get(AbstractHTTPConnector.HTTP_REQUEST_HEADERS);
        } else {
            httpRequestHeader = new RequestHeaders();
        }


        for (Header submissionHeader : submissionHeaders) {
            RequestHeaders requestHeaders = submissionHeader.getHeaders();

            for(RequestHeader header : requestHeaders.getAllHeaders()){
                httpRequestHeader.addHeader(header);
            }

        }
        contextMap.put(AbstractHTTPConnector.HTTP_REQUEST_HEADERS, httpRequestHeader);
    }



    // template methods for submit processing

    /**
     * Performs validation according to section 11.1, para 2.
     * <p/>
     * Supports XForms 1.1 validate attribute. However, this support should be
     * externalized in a - say Submission11 - class which simply overwrites this
     * template method.
     */
    protected boolean submitValidate(Instance instance, String path, Map prefixMapping, XPathFunctionContext xpathFunctionContext) throws XFormsException {
        // validate model items in submission mode: non-relevant items are ignored
        // and the first occurrence of an invalid item discontinues validation
        SubmissionValidatorMode mode = new SubmissionValidatorMode();
        this.model.getValidator().validate(instance, instance.getInstanceNodeset(), 1, path, prefixMapping, xpathFunctionContext, mode);

        if (mode.isDiscontinued()) {
            // XForms 1.1 support, section 4.3.1
            // in case of an invalid instance report submit error only if the validate attribute is true
            if (Boolean.TRUE.equals(this.validate)) {
                throw new XFormsSubmitError("instance validation failed: " + mode.getStatusText(), this.target, XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, XFormsConstants.VALIDATION_ERROR, getResourceURI()));
            }
        }

        return !mode.isDiscontinued();
    }

    /**
     * Performs relevance selection to section 11.1, para 1.
     * <p/>
     * Supports XForms 1.1 relevant attribute. However, this support should be
     * externalized in a - say Submission11 - class which simply overwrites this
     * template method.
     */
    protected Node submitSelectRelevant(Instance instanceObject, String path) throws XFormsException {
        try {
            // XForms 1.1 support, section 4.3.2
            // select relevant instance items only if the relevant attribute is true
            return (Node) (Boolean.TRUE.equals(this.relevant)
                    ? RelevanceSelector.selectRelevant(instanceObject, path)
                    : XPathCache.getInstance().evaluateAsSingleNode(instanceObject.getRootContext(), path));
        }
        catch (Exception e) {
            throw new XFormsSubmitError("instance relevance selection failed", e, this.target, XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, XFormsConstants.TARGET_ATTRIBUTE, getResourceURI()));
        }
    }


    /**
     * Performs replace processing according to section 11.1, para 5.
     */
    protected void submitReplaceAll(Map response) throws XFormsException {
        // XForms 1.0, section 11.1, para 5
        // - For a success response including a body, when the value of the
        // replace attribute on element submission is "all", the event
        // xforms-submit-done is dispatched, and submit processing concludes
        // with entire containing document being replaced with the returned
        // body.
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " submit: replacing all");
        }
        
        // todo: refactor submission response
        // split copied response into header and body (keep original response
        // for backwards compat)
        Map header = new HashMap();
        header.putAll(response);
        Object body = header.remove(XFormsProcessor.SUBMISSION_RESPONSE_STREAM);

        HashMap map = new HashMap();
        map.put("header", header);
        map.put("body", body);

        // dispatch xforms-submit-done
        this.container.dispatch(this.target, XFormsEventNames.SUBMIT_DONE, constructEventInfo(response));

        
        // dispatch internal betterform event
        // special case for URI redirection, resubmit as GET
        if (header.containsKey("Location")) {
            map.clear();
            map.put("show", "replace");
            map.put("uri", response.get("Location"));
            this.container.dispatch(this.target, BetterFormEventNames.LOAD_URI, map);
            return;
        } else {
            this.container.dispatch(this.target, BetterFormEventNames.REPLACE_ALL, map);
            this.container.dispatch(this.target, XFormsEventNames.MODEL_DESTRUCT, null);
        }

        // backwards compat
        forward(response);
    }

    /**
     * Performs replace processing according to section 11.1, para 5.
     */
    protected void submitReplaceInstance(Map response) throws XFormsException {
        // XForms 1.0, section 11.1, para 5
        // - For a success response including a body of an XML media type (as
        // defined by the content type specifiers in [RFC 3023]), when the value
        // of the replace attribute on element submission is "instance", the
        // response is parsed as XML. An xforms-link-exception (4.5.2 The
        // xforms-link-exception Event) occurs if the parse fails. If the parse
        // succeeds, then all of the internal instance data of the instance
        // indicated by the instance attribute setting is replaced with the
        // result. Once the XML instance data has been replaced, the rebuild,
        // recalculate, revalidate and refresh operations are performed on the
        // model, without dispatching events to invoke those four operations.
        // Submit processing then concludes after dispatching
        // xforms-submit-done.
        // - For a success response including a body of a non-XML media type
        // (i.e. with a content type not matching any of the specifiers in
        // [RFC 3023]), when the value of the replace attribute on element
        // submission is "instance", nothing in the document is replaced and
        // submit processing concludes after dispatching xforms-submit-error.
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " submit: replacing instance");
        }

        Document responseInstance = getResponseAsDocument(response);

        // replace instance
        if(this.targetModelId != null) {
            List<Model> models = container.getModels();
            Model targetModel = null;
            for (Model tmpmodel : models) {
                if (tmpmodel.getId().equals(this.targetModelId)) {
                    targetModel = tmpmodel;
                }
            }
            updateInstanceAndModel(targetModel,responseInstance);
        } else {
            updateInstanceAndModel(this.model,responseInstance);
        }

        this.container.refresh();
        // deferred update behaviour
        UpdateHandler updateHandler = this.model.getUpdateHandler();
        if (updateHandler != null) {
            updateHandler.doRebuild(false);
            updateHandler.doRecalculate(false);
            updateHandler.doRevalidate(false);
            updateHandler.doRefresh(false);
        }

        // dispatch xforms-submit-done
        this.container.dispatch(this.target, XFormsEventNames.SUBMIT_DONE, constructEventInfo(response));
    }

    private Document getResponseAsDocument(Map response) throws XFormsException {
        Document responseInstance;
        try {

            if(response.containsKey(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT)){
                responseInstance = (Document) response.get(XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT);
            }else{
                InputStream responseStream = (InputStream) response.get(XFormsProcessor.SUBMISSION_RESPONSE_STREAM);
                responseInstance = DOMUtil.parseInputStream(responseStream, true, false);
                responseStream.close();
            }
        }
        catch (Exception e) {
            // todo: check for response media type (needs submission response
            // refactoring) in order to dispatch xforms-link-exception
            throw new XFormsSubmitError("instance parsing failed", e, this.getTarget(), XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, XFormsConstants.PARSE_ERROR, getResourceURI(), 200d, null, "", ""));
        }
        return responseInstance;
    }

    private void submitReplaceEmbedHTML(Map response) throws XFormsException{
        // check for targetid
        String targetid = getXFormsAttribute(TARGETID_ATTRIBUTE);
        String resource = getResource();
        Map eventInfo = new HashMap();
        String error = null;

        if (targetid == null) {
            error = "targetId";
        }else if(resource == null){
            error = "resource";
        }

        if(error != null && error.length() > 0) {
            eventInfo.put(XFormsConstants.ERROR_TYPE, "no " +  error + "defined for submission resource");
            this.container.dispatch(this.target, XFormsEventNames.SUBMIT_ERROR, eventInfo);
            return;
        }

        Document result = getResponseAsDocument(response);
        Node embedElement = result.getDocumentElement();
        if(resource.indexOf("#") != -1){
            // detected a fragment so extract that from our result Document

            String fragmentid = resource.substring(resource.indexOf("#")+1);
            if (fragmentid.indexOf("?") != -1) {
                fragmentid = fragmentid.substring(0, fragmentid.indexOf("?"));
            }
            embedElement = DOMUtil.getById(result,fragmentid);
        }

        // Map eventInfo = constructEventInfo(response);


        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            DOMUtil.prettyPrintDOM(embedElement,outputStream);
        } catch (TransformerException e) {
            throw new XFormsException(e);
        }

        eventInfo.put(EMBEDNODE,outputStream.toString());
        eventInfo.put("embedTarget",targetid);

        // dispatch xforms-submit-done
        this.container.dispatch(this.target, XFormsEventNames.SUBMIT_DONE, eventInfo);

    }

    private void submitReplaceNew(Map response) throws XFormsException {
        Document result = getResponseAsDocument(response);
        Node embedElement = result.getDocumentElement();

        OutputStream outputStream = new ByteArrayOutputStream();
        try {
            DOMUtil.prettyPrintDOM(embedElement,outputStream);
        } catch (TransformerException e) {
            throw new XFormsException(e);
        }
        Map eventInfo = new HashMap();
        eventInfo.put(DOCUMENT,outputStream.toString());

        // dispatch xforms-submit-done
        this.container.dispatch(this.target, XFormsEventNames.SUBMIT_DONE, eventInfo);

    }


    
    private void updateInstanceAndModel(Model referedModel, Document responseInstance) throws XFormsException {
        if (this.targetExpr != null) {
            Node targetNode;
            if (this.instance == null)
                targetNode = XPathUtil.getAsNode(XPathCache.getInstance().evaluate(evalInScopeContext(), 1, this.targetExpr, this.prefixMapping, this.xpathFunctionContext), 1);
            else {
                targetNode = XPathUtil.getAsNode(XPathCache.getInstance().evaluate(referedModel.getInstance(this.instance).getRootContext().getNodeset(), 1, this.targetExpr, this.prefixMapping, this.xpathFunctionContext), 1);
            }
            if (targetNode != null && targetNode.getNodeType() == Node.ELEMENT_NODE) {
                targetNode.getParentNode().replaceChild(targetNode.getOwnerDocument().importNode(responseInstance.getDocumentElement(), true), targetNode);

            }
            else if(targetNode != null && targetNode.getNodeType() == Node.ATTRIBUTE_NODE){
                if(LOGGER.isDebugEnabled()) {
                    DOMUtil.prettyPrintDOM(responseInstance);
                }
                // targetNode.setContent(responseInstance.getTextContent());
                String attrValue= responseInstance.getDocumentElement().getTextContent();
                targetNode.setNodeValue(attrValue);
            }else {
                throw new XFormsSubmitError("Invalid target", this.getTarget(), XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, XFormsConstants.TARGET_ERROR, getResourceURI(), 200d, null, "", ""));
            }
        }
        else if(this.instance != null && referedModel.getInstance(this.instance) == null) {
            this.container.dispatch(referedModel.getId(), XFormsEventNames.BINDING_EXCEPTION);
            // throw new XFormsBindingException("invalid instance id at " + DOMUtil.getCanonicalPath(this.getElement()), this.target, this.instance);
        }
        else if(this.instance != null) {
            referedModel.getInstance(this.instance).setInstanceDocument(responseInstance);
        } else {
            referedModel.getInstance(getInstanceId()).setInstanceDocument(responseInstance);
        }

        // perform rebuild, recalculate, revalidate, and refresh
        referedModel.rebuild();
        referedModel.recalculate();
        referedModel.revalidate();
    }

    /**
     * Performs replace processing according to section 11.1, para 5.
     */
    protected void submitReplaceText(Map response) throws XFormsException {

        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " submit: replacing text");
        }
        Node targetNode;
        if (this.targetExpr != null) {
        	targetNode = XPathUtil.getAsNode(XPathCache.getInstance().evaluate(this.instance == null?evalInScopeContext():this.model.getInstance(this.instance).getRootContext().getNodeset(), 1, this.targetExpr, this.prefixMapping, this.xpathFunctionContext), 1);
        }
        else if (this.instance == null) {
        	targetNode = this.model.getInstance(getInstanceId()).getInstanceDocument().getDocumentElement();
        } else {
        	targetNode = this.model.getInstance(this.instance).getInstanceDocument().getDocumentElement();
        }
        final InputStream responseStream = (InputStream) response.get(XFormsProcessor.SUBMISSION_RESPONSE_STREAM);

        StringBuilder text = new StringBuilder(512);
        try {
            String contentType = (String) response.get("Content-Type");
            String encoding = "UTF-8";

            if (contentType != null) {
                final String[] contTypeEntries = contentType.split(", ?");

                for (int i = 0; i < contTypeEntries.length; i++) {
                    if (contTypeEntries[i].startsWith("charset=")) {
                        encoding = contTypeEntries[i].substring(8);
                    }
                }
            }
            byte[] buffer = new byte[512];
            int bytesRead;
            while( (bytesRead = responseStream.read(buffer)) > 0) {
                text.append(new String(buffer, 0, bytesRead, encoding));
            }

            responseStream.close();
        }
        catch (Exception e) {
            // todo: check for response media type (needs submission response
            // refactoring) in order to dispatch xforms-link-exception
            throw new XFormsSubmitError("instance parsing failed", e, this.getTarget(), XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, XFormsConstants.PARSE_ERROR, getResourceURI(), 200d, null, "", ""));
        }


        if (targetNode == null) {
    		throw new XFormsSubmitError("Invalid target", this.getTarget(), XFormsSubmitError.constructInfoObject(this.element, this.container, locationPath, XFormsConstants.TARGET_ERROR, getResourceURI(), 200d, null, "", ""));
    	}

        else if(targetNode.getNodeType() == Node.ELEMENT_NODE){
            while(targetNode.getFirstChild() != null) {
                targetNode.removeChild(targetNode.getFirstChild());
            }

            targetNode.appendChild(targetNode.getOwnerDocument().createTextNode(text.toString()));
        }
        else if(targetNode.getNodeType() == Node.ATTRIBUTE_NODE){
            targetNode.setNodeValue(text.toString());
        }else {
            LOGGER.warn("Don't know how to handle targetNode '" + targetNode.getLocalName() + "', node is neither an element nor an attribute Node");
        }

        // perform rebuild, recalculate, revalidate, and refresh
        this.model.rebuild();
        this.model.recalculate();
        this.model.revalidate();
        this.container.refresh();

        // deferred update behaviour
        UpdateHandler updateHandler = this.model.getUpdateHandler();
        if (updateHandler != null) {
            updateHandler.doRebuild(false);
            updateHandler.doRecalculate(false);
            updateHandler.doRevalidate(false);
            updateHandler.doRefresh(false);
        }

        // dispatch xforms-submit-done
        this.container.dispatch(this.target, XFormsEventNames.SUBMIT_DONE, constructEventInfo(response));
    }

    /**
     * Performs replace processing according to section 11.1, para 5.
     */
    protected void submitReplaceNone(Map response) throws XFormsException {
        // XForms 1.0, section 11.1, para 5
        // - For a success response including a body, when the value of the
        // replace attribute on element submission is "none", submit processing
        // concludes after dispatching xforms-submit-done.
        if (getLogger().isDebugEnabled()) {
            getLogger().debug(this + " submit: replacing none");
        }

        // dispatch xforms-submit-done
        this.container.dispatch(this.target, XFormsEventNames.SUBMIT_DONE, constructEventInfo(response));
    }

    private Map<String, Object> constructEventInfo(Map response) throws XFormsException {
		Map<String, Object> result = new HashMap<String, Object>();
		
		final Document ownerDocument = this.element.getOwnerDocument();
		final DocumentWrapper wrapper = new DocumentWrapper(ownerDocument, this.container.getProcessor().getBaseURI(), this.container.getConfiguration());
		
		
		List<Item> headerItems = new ArrayList<Item>(response.size());
		for (Iterator<Map.Entry<String, String>> it = response.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, String> entry =  it.next();
			if (!XFormsProcessor.SUBMISSION_RESPONSE_STREAM.equals(entry.getKey()) &&
                !XFormsProcessor.SUBMISSION_RESPONSE_DOCUMENT.equals(entry.getKey()))
            {
				
				Element headerEl = ownerDocument.createElement("header");
				
				Element nameEl = ownerDocument.createElement("name");
				nameEl.appendChild(ownerDocument.createTextNode(entry.getKey()));
				headerEl.appendChild(nameEl);

				Element valueEl = ownerDocument.createElement("value");
				valueEl.appendChild(ownerDocument.createTextNode(entry.getValue()));
				headerEl.appendChild(valueEl);
				
				headerItems.add(wrapper.wrap(headerEl));
			}
		}
		result.put(RESOURCE_URI, getResourceURI());
		result.put(RESPONSE_STATUS_CODE, Double.valueOf(200d)); //TODO get real response code
		result.put(RESPONSE_HEADERS, headerItems);
		result.put(RESPONSE_REASON_PHRASE, ""); //TODO get real response reason phrase
		
		return result;
	}

	// deprecated crap

    /**
     * @deprecated backwards compat
     */
    public Map getSubmissionMap() {
        return (Map) container.getProcessor().getContext().get(XFormsProcessor.SUBMISSION_RESPONSE);
    }

    /**
     * @deprecated backwards compat
     */
    public void forward(Map response) {
        this.container.getProcessor().getContext().put(XFormsProcessor.SUBMISSION_RESPONSE, response);
    }

    /**
     * @deprecated backwards compat
     */
    public void redirect(String uri) {
        this.container.getProcessor().getContext().put(XFormsProcessor.LOAD_URI, uri);
    }

}
