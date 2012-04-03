/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


package de.betterform.agent.web.event;

import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.events.XMLEventFactory;
import de.betterform.xml.events.impl.DefaultXMLEventInitializer;
import de.betterform.xml.events.impl.DefaultXMLEventService;
import de.betterform.xml.events.impl.XercesXMLEvent;
import de.betterform.xml.events.impl.XercesXMLEventFactory;
import de.betterform.xml.xforms.XFormsConstants;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Element;

import java.util.*;


/**
 * EventQueue logs all events happening in XForms processor and build a DOM
 * document which represent those events.
 *
 * @author Joern Turner Lars Windauer
 * @version $Id: EventLog.java 2612 2007-07-24 14:51:21Z joern $
 */
public class EventQueue {
    public static List<String> HELPER_ELEMENTS = Arrays.asList(XFormsConstants.LABEL, XFormsConstants.HELP, XFormsConstants.HINT, XFormsConstants.ALERT, XFormsConstants.VALUE);
    private List<XMLEvent> eventList;
    protected static Log LOGGER = LogFactory.getLog(EventQueue.class);
    private List<XMLEvent> loadEmbedEventList;

    public EventQueue() {
        this.eventList = new ArrayList<XMLEvent>();
        this.loadEmbedEventList = new ArrayList<XMLEvent>();
    }

    public List<XMLEvent> getEventList() {
        return aggregateEventList();
    }

    /** Adding XMLEvents to the EventQueue to be processed on the client
     *  EventTarget is nulled to avoid sending it over the wire, targetName and targetId (received from EventTarget)
     *  are stored instead in the ContextInfo Map
     *
     * @param event XMLEvent received from processor
     */
    public void add(XMLEvent event) {
    	try {
			XMLEvent clonedEvent = (XMLEvent) event.clone();
		
	        Element target = (Element) clonedEvent.getTarget();
	        clonedEvent.addProperty("targetId", target.getAttributeNS(null, "id"));
	        String targetName = target.getLocalName();
	        clonedEvent.addProperty("targetName", targetName);
	
	        if ((BetterFormEventNames.ITEM_CHANGED.equals(clonedEvent.getType()) ||
                    BetterFormEventNames.STATE_CHANGED.equals(clonedEvent.getType()) && HELPER_ELEMENTS.contains(targetName)) ||
                    BetterFormEventNames.PROTOTYPE_CLONED.equals(clonedEvent.getType()) ||
                    BetterFormEventNames.ITEM_DELETED.equals(clonedEvent.getType())) {
	            // parent id is needed for updating all helper elements cause they
	            // are identified by '<parentId>-label' etc. rather than their own id
	            String parentId = ((Element) target.getParentNode()).getAttributeNS(null, "id");
	            clonedEvent.addProperty("parentId", parentId);
	        }
	
	        ((XercesXMLEvent) clonedEvent).target=null;
	        ((XercesXMLEvent) clonedEvent).currentTarget=null;
            if(isLoadEmbedEvent(clonedEvent)){
                this.loadEmbedEventList.add(clonedEvent);
            }else {
                this.eventList.add(clonedEvent);    
            }

    	} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    public XMLEvent add(String type, String targetId, String targetName) {
        DefaultXMLEventService xmlEventService = new DefaultXMLEventService();
        xmlEventService.setXMLEventFactory(new XercesXMLEventFactory());
        xmlEventService.setXMLEventInitializer(new DefaultXMLEventInitializer());

        XMLEventFactory xmlEventFactory = xmlEventService.getXMLEventFactory();
        XMLEvent xmlEvent = xmlEventFactory.createXMLEvent(type);
        xmlEvent.initXMLEvent(type,false,false,null);
        xmlEvent.addProperty("targetid",targetId);
        xmlEvent.addProperty("targetName",targetName);
        this.eventList.add(xmlEvent);
        return xmlEvent;
    }

    private boolean isLoadEmbedEvent(XMLEvent xmlEvent) {
        if(xmlEvent.getType() == null || xmlEvent.getContextInfo() == null) {
            return false;
        }
        if(xmlEvent.getType().equals(BetterFormEventNames.LOAD_URI) &&( "embed".equals(xmlEvent.getContextInfo("show")) || "none".equals(xmlEvent.getContextInfo("show")))){
            return true;
        }
        return false;
    }

    /**
     * clean the EventQueue
     */
    
    public void flush() {
        this.eventList.clear();
    }

    public void addProperty(XMLEvent progressEvent, String key, String value) {
        Map contextInfo = progressEvent.getContextInfo();
        if(contextInfo != null) {
            contextInfo.put(key,value);
        }

    }

    public List<XMLEvent> aggregateEventList() {
        // Stack is used to "navigate" through the event list
        LinkedList<XMLEvent> aggregatedFocusList= new LinkedList<XMLEvent>();
        Stack<XMLEvent> aggregatedInsertEventsStack = new Stack();
        Stack<XMLEvent> aggregatedEmbedEventsStack = new Stack();
        ArrayList<XMLEvent> aggregatedEventList = new ArrayList<XMLEvent>(eventList.size());

        for(XMLEvent xmlEvent: this.loadEmbedEventList){
            aggregatedEventList.add(xmlEvent);
        }
        
        this.loadEmbedEventList.clear();

        for(int i =0;i< eventList.size(); i++) {
            XercesXMLEvent xmlEvent = (XercesXMLEvent) eventList.get(i);

            XercesXMLEvent xmlEventToAdd = new XercesXMLEvent();
            // Map PROTOTYPE_CLONED event to betterform-insert-repeatitem or betterform-insert-itemset event
            // and copy event properties to new created XMLEvent
            if(xmlEvent.getType().equals(BetterFormEventNames.PROTOTYPE_CLONED)){
                if(xmlEvent.getContextInfo("targetName").equals(XFormsConstants.ITEMSET)){
                    xmlEventToAdd.initXMLEvent("betterform-insert-itemset", xmlEvent.getBubbles(), xmlEvent.getCancelable(), xmlEvent.getContextInfo());
                }else{
                    xmlEventToAdd.initXMLEvent("betterform-insert-repeatitem", xmlEvent.getBubbles(), xmlEvent.getCancelable(), xmlEvent.getContextInfo());
                }
                xmlEventToAdd.target = xmlEvent.target;
                xmlEvent.addProperty("generatedIds", new HashMap());
                aggregatedEventList.add(xmlEventToAdd);
                // push XMLEvent to Stack for further processing
                aggregatedInsertEventsStack.push(xmlEventToAdd);

            }
            // add all generated ids to surrounding betterform-insert-repeatitem or betterform-insert-itemset event
            else if (xmlEvent.getType().equals(BetterFormEventNames.ID_GENERATED) && aggregatedInsertEventsStack.size() >0) {
                XMLEvent aggregatingInsertEvent =  aggregatedInsertEventsStack.peek();
                ((HashMap)aggregatingInsertEvent.getContextInfo("generatedIds")).put(xmlEvent.getContextInfo("originalId"), xmlEvent.getContextInfo("targetId"));
            }
            // add insert position to surrounding betterform-insert-repeatitem or betterform-insert-itemset event
            else if(xmlEvent.getType().equals(BetterFormEventNames.ITEM_INSERTED)){
                XMLEvent tmpEvent = aggregatedInsertEventsStack.pop();
                tmpEvent.addProperty("position", xmlEvent.getContextInfo("position"));
                tmpEvent.addProperty("label", xmlEvent.getContextInfo("label"));
                tmpEvent.addProperty("value", xmlEvent.getContextInfo("value"));

            }
            else if(xmlEvent.getType().equals(BetterFormEventNames.EMBED)){
                aggregatedEventList.add(xmlEvent);
                aggregatedEmbedEventsStack.push(xmlEvent);
            }
            else if(xmlEvent.getType().equals(BetterFormEventNames.EMBED_DONE)){
                aggregatedEmbedEventsStack.pop().addProperty("targetElement", xmlEvent.getContextInfo("targetElement"));
                aggregatedEventList.add(xmlEvent);
            }
            else if(xmlEvent.getType().equals(XFormsEventNames.FOCUS)){
                aggregatedFocusList.push(xmlEvent);
            }
            // all other events within eventList are simply copied to the new eventlist
            else {
                aggregatedEventList.add(xmlEvent);
            }
        }

        while (!aggregatedFocusList.isEmpty()) {
            aggregatedEventList.add(aggregatedFocusList.pollLast());
        }
        return aggregatedEventList;
    }
}
