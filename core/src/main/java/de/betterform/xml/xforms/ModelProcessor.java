package de.betterform.xml.xforms;

import de.betterform.xml.config.Config;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import org.w3c.dom.events.Event;

import java.util.Iterator;

/**
 * processes standalong XForms models.
 */
public class ModelProcessor extends AbstractProcessorDecorator {

    private boolean isSuccess=true;

    public ModelProcessor() {
        super();
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
                        if(modelItem.getRefreshView().isInvalidMarked()){
                            this.isSuccess=false;
                            break;
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
}
