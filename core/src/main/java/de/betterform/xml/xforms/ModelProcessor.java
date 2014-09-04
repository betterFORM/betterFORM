package de.betterform.xml.xforms;

import de.betterform.xml.config.Config;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import org.w3c.dom.events.Event;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * processes standalong XForms models.
 */
public class ModelProcessor extends AbstractProcessorDecorator {

    private boolean isSuccess=true;
    private List errors;

    public ModelProcessor() {
        super();
        this.errors = new ArrayList();
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
                        ModelItem modelItem = (ModelItem) iterator.next();
                        if(!modelItem.getLocalUpdateView().isDatatypeValid()){
                            this.errors.add(new ErrorInfo(ErrorInfo.DATATYPE_INVALID,modelItem.toString()));
                        }
                        if(modelItem.getRefreshView().isInvalidMarked()){
                            this.errors.add(new ErrorInfo(ErrorInfo.CONSTRAINT_INVALID,modelItem.toString()));
                            this.isSuccess=false;
                        }
                        if(modelItem.getRefreshView().isRequiredMarked()){
                            this.errors.add(new ErrorInfo(ErrorInfo.REQUIRED_INVALID,modelItem.toString()));
                        }

                    }
                }
            }
        } catch (Exception e) {
            handleEventException(e);
        }
    }



    /**
     * triggers standard submission.
     *
     * @return if form was submitted successfully returns true, false otherwise
     * @throws XFormsException
     */
    public boolean isSuccess() throws XFormsException {
        return this.isSuccess;
    }

    class ErrorInfo{
        public static final short DATATYPE_INVALID=0;
        public static final short CONSTRAINT_INVALID=1;
        public static final short REQUIRED_INVALID=2;

        private short errorType;
        private String path;


        ErrorInfo(short type, String path){
            this.errorType = type;
            this.path = path;
        }

        public String getPath(){
            return this.path;
        }
        public short getErrorType(){
            return this.errorType;
        }

    }
}
