package de.betterform.xml.xforms;

import de.betterform.xml.config.Config;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.events.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * processes standalong XForms models.
 */
public class ModelProcessor extends AbstractProcessorDecorator {

    private boolean isSuccess=true;
    private List<ErrorInfo> errors;
    private List<XMLEvent> events;

    public ModelProcessor() {
        super();
        this.errors = new ArrayList();
        this.events = new ArrayList();
    }

    public List<ErrorInfo> getErrors(){
        return this.errors;
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
        try {
            if (event instanceof XMLEvent) {
                XMLEvent xmlEvent = (XMLEvent) event;
                String type = xmlEvent.getType();

                if(XFormsEventNames.MODEL_CONSTRUCT_DONE.equalsIgnoreCase(type)){
                    String s=type;
                    System.out.println(s);

                    Iterator iterator = this.xformsProcessor.getContainer().getDefaultModel().getDefaultInstance().iterateModelItems();
                    while(iterator.hasNext()){
                        boolean invalid=false;
                        ModelItem modelItem = (ModelItem) iterator.next();
                        String datatype = modelItem.getDeclarationView().getDatatype();

                        ErrorInfo errorInfo = new ErrorInfo();
                        if(!modelItem.getLocalUpdateView().isDatatypeValid()){
                            errorInfo.setDataType(datatype);
                            errorInfo.setErrorType(ErrorInfo.DATATYPE_INVALID);
                            invalid=true;
                        }
                        if(modelItem.getRefreshView().isInvalidMarked()){
                            errorInfo.setErrorType(ErrorInfo.CONSTRAINT_INVALID);
                            invalid=true;
                        }
                        if(modelItem.getRefreshView().isRequiredMarked()){
                            if(modelItem.getValue().length()==0){
                                errorInfo.setErrorType(ErrorInfo.REQUIRED_INVALID);
                                invalid=true;
                            }
                        }
                        if(invalid){
                            errorInfo.setRef(modelItem.toString());
                            this.errors.add(errorInfo);
                        }
                    }
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
    public Document serialize(){

        //create document + root
        Document serialized = DOMUtil.newDocument(false,false);
        Element root = serialized.createElement("errors");
        serialized.appendChild(root);

        for (ErrorInfo error : this.errors) {
//            Element
            //tbd.
        }

        return null;
    }

    /**
     * triggers standard submission.
     *
     * @return if form was submitted successfully returns true, false otherwise
     * @throws XFormsException
     */
    public boolean isSuccess() throws XFormsException {
        return this.errors.size()==0;
    }

    class ErrorInfo{
        public static final String DATATYPE_INVALID="datatype invalid";
        public static final String CONSTRAINT_INVALID="constraint invalid";
        public static final String REQUIRED_INVALID="required but empty";

        private String ref="";
        private String dataType="";
        private String errorType;
        private String path;
        private String alert;


        private final String TYPE_INVALID_MSG = "The value is no valid " + this.dataType;
        private final String REQUIRED_INVALID_MSG = "This value is required. ";
        private final String CONSTRAINT_INVALID_MSG = "The value is not valid ";

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
