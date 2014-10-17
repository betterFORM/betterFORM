package de.betterform.xml.xforms;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import de.betterform.xml.config.Config;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.xforms.exception.XFormsBindingException;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.model.submission.Submission;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.Event;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * processes standalong XForms models.
 */
public class ModelProcessor extends AbstractProcessorDecorator {
    private static final Log LOG = LogFactory.getLog(ModelProcessor.class);
    private final Errors errors;

    private boolean isSuccess=true;
    private List<XMLEvent> events;
    private Object responseStream=null;
    private Submission defaultSubmission;

    public ModelProcessor() {
        super();
        this.errors = new Errors() ;
        this.events = new ArrayList();
    }

    public List<ErrorInfo> getErrors(){
        return this.errors.getErrorInfo();
    }

    public Submission getDefaultSubmission() {
        return defaultSubmission;
    }

    @Override
    public void init() throws XFormsException {
        this.configuration = Config.getInstance();
        addEventListeners();

        // init processor
        this.xformsProcessor.init();

    }

    @Override
    public void handleEvent(Event event) {
        Object result;
        try {
            if (event instanceof XMLEvent) {
                XMLEvent xmlEvent = (XMLEvent) event;
                String type = xmlEvent.getType();

                if(XFormsEventNames.MODEL_CONSTRUCT_DONE.equalsIgnoreCase(type)){

                    Iterator iterator = this.xformsProcessor.getContainer().getDefaultModel().getDefaultInstance().iterateModelItems();
                    while(iterator.hasNext()){
                        boolean invalid=false;
                        ModelItem modelItem = (ModelItem) iterator.next();

                        if(!modelItem.getLocalUpdateView().isDatatypeValid()){
                            ErrorInfo errorInfo = new ErrorInfo();
                            errorInfo.setErrorType(ErrorInfo.DATATYPE_INVALID);
                            String datatype = modelItem.getDeclarationView().getDatatype();
                            errorInfo.setDataType(datatype);
                            errorInfo.setRef(((Node)modelItem.getNode()).getLocalName());
                            errorInfo.setPath(modelItem.toString());
                            this.errors.add(errorInfo);
                        }
                        if(modelItem.getRefreshView().isInvalidMarked()){
                            ErrorInfo errorInfo = new ErrorInfo();
                            errorInfo.setErrorType(ErrorInfo.CONSTRAINT_INVALID);
                            errorInfo.setRef(((Node)modelItem.getNode()).getLocalName());
                            errorInfo.setPath(modelItem.toString());
                            this.errors.add(errorInfo);
                        }
                        if(modelItem.getRefreshView().isRequiredMarked()){
                            if(modelItem.getValue().length()==0){
                                ErrorInfo errorInfo = new ErrorInfo();
                                errorInfo.setErrorType(ErrorInfo.REQUIRED_INVALID);
                                errorInfo.setRef(((Node)modelItem.getNode()).getLocalName());
                                errorInfo.setPath(modelItem.toString());
                                this.errors.add(errorInfo);
                            }
                        }
                    }
                }else if(XFormsEventNames.SUBMIT_ERROR.equalsIgnoreCase(type)){
                    LOG.debug("XForms submit error");
                }else if(XFormsEventNames.SUBMIT_DONE.equalsIgnoreCase(type)){
                    LOG.debug("XForms submit done");
                    this.responseStream = xmlEvent.getContextInfo(XFormsProcessor.SUBMISSION_RESPONSE_STREAM);
                }
                this.events.add(xmlEvent);
            }
        } catch (Exception e) {
            handleEventException(e);
        }
    }

    /*
    <errors>
        <error-info ref="street" facet="required|type|constraint">
            <alert></alert>
        </error-info>
    </errors>
    */
    public String serialize() throws JsonProcessingException {

        XmlMapper xmlMapper = new XmlMapper();
        String xml = xmlMapper.writeValueAsString(this.errors);

        if(LOG.isDebugEnabled()){
            LOG.debug("errors as xml string: " + xml);
        }
        //create document + root
/*
        Document serialized = DOMUtil.newDocument(false,false);
        Element root = serialized.createElement("errors");
        serialized.appendChild(root);

        for (ErrorInfo error : this.errors) {
        }
*/
        return xml;

    }

    /**
     * submits the HTML form via XForms
     * @throws XFormsException
     */
    public InputStream submit() {
        // todo:  hard-coded id for now
        String id = "s-default";

        // find submission matching the resource string
        Container container = getXformsProcessor().getContainer();

        Object submissionObject = getXformsProcessor().getContainer().lookup(id);
        if (submissionObject == null || !(submissionObject instanceof Submission)) {
            try {
                throw new XFormsBindingException("invalid submission id " + id,((Submission) submissionObject).getTarget(),null);
            } catch (XFormsBindingException e) {
                LOG.error("a binding exception occurred which shouldn't have happend: " + e.getMessage());
            }
        }
        this.defaultSubmission = (Submission) submissionObject;

        // dispatch xforms-submit to submission
        try {
            container.dispatch(((Submission) submissionObject).getTarget(), XFormsEventNames.SUBMIT, null);
        } catch (XFormsException e) {
            e.printStackTrace();
        }
        return (InputStream) this.responseStream;
    }

    /**
     * triggers standard submission.
     *
     * @return if form was submitted successfully returns true, false otherwise
     * @throws XFormsException
     */
    public boolean isSuccess() throws XFormsException {
        return this.errors.getErrorInfo().size()==0;
    }

    @JacksonXmlRootElement(localName = "errors")
    public class Errors{
        private List<ErrorInfo> errorInfo=new ArrayList();

        void add(ErrorInfo info){
            this.errorInfo.add(info);
        }
        @JacksonXmlElementWrapper(useWrapping = false)
        public List getErrorInfo(){
            return errorInfo;
        }
    }

    class ErrorInfo{
        public static final String DATATYPE_INVALID="datatype-failed";
        public static final String CONSTRAINT_INVALID="constraint-failed";
        public static final String REQUIRED_INVALID="required-failed";

        private String ref="";
        private String dataType="";
        private String errorType;
        private String path;
        private String alert;

        ErrorInfo(){
        }

        public String getRef() {
            return ref;
        }

        public void setRef(String ref) {
            this.ref = ref;
        }

        public String getDatatype() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }

        public String getErrorType() {
            return errorType;
        }

        public void setErrorType(String errorType) {
            this.errorType = errorType;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public String getAlert() {
            return alert;
        }

        public void setAlert(String alert) {
            this.alert = alert;
        }
    }
}
