package de.betterform.agent.web.atmosphere;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.betterform.agent.web.WebUtil;
import de.betterform.agent.web.flux.SocketProcessor;
import de.betterform.xml.events.XMLEvent;
import de.betterform.xml.xforms.XFormsProcessor;
import de.betterform.xml.xforms.exception.XFormsException;
import org.atmosphere.config.service.Disconnect;
import org.atmosphere.config.service.ManagedService;
import org.atmosphere.config.service.Ready;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.events.Event;

import java.io.IOException;
import java.util.List;

@ManagedService(path = "/msg")
public class BetterSocket {
    private final Logger logger = LoggerFactory.getLogger(BetterSocket.class);
    private String xformsKey;
    private AtmosphereResourceFactory resourceFactory;
    private String uuid;

    @Ready
    public void onReady(final AtmosphereResource r) {

        logger.info("Browser {} connected." + r.uuid());
        this.resourceFactory = AtmosphereResourceFactory.getDefault();
        this.uuid = r.uuid();
        this.xformsKey = (String) r.getRequest().getSession().getAttribute("xfSessionKey");

        SocketProcessor socketProcessor = (SocketProcessor) WebUtil.getWebProcessor(xformsKey, r.getRequest(), r.getResponse(), r.getRequest().getSession());
        //return events that already executed during xforms model init
        eventListToJSON(r, socketProcessor);

        if (logger.isDebugEnabled()) {
            logger.debug("XForms Session key: " + this.xformsKey);
        }
//        r.getBroadcaster().broadcast("just a string would do?");
    }

    private void eventListToJSON(AtmosphereResource r, SocketProcessor socketProcessor) {
        List<XMLEvent> xmlEvents = socketProcessor.getEventQueue().getEventList();
        for (int i = 0; i < xmlEvents.size(); i++) {
            Event ev = xmlEvents.get(i);
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(MapperFeature.AUTO_DETECT_FIELDS, false);
            String json = "";
            try {
                json = mapper.writeValueAsString(ev);

                if (logger.isDebugEnabled()) {
                    logger.debug("mapped json object: " + json);
                }
                r.getBroadcaster().broadcast(json);
            } catch (JsonProcessingException e) {
                logger.error("mapping xml event to JSON failed"+ e.getMessage());
            }
        }
    }

    @Disconnect
    public void onDisconnect(AtmosphereResourceEvent event) {
        if (event.isCancelled()) {
            logger.info("Browser {} unexpectedly disconnected" + event.getResource().uuid());
        } else if (event.isClosedByClient()) {
            logger.info("Browser {} closed the connection" + event.getResource().uuid());
        }
    }

    @org.atmosphere.config.service.Message(encoders = {JacksonEncoder.class}, decoders = {JacksonDecoder.class})
    public Message onMessage(Message message) throws IOException {
        logger.debug("message: " + message);

        AtmosphereResource resource = resourceFactory.find(this.uuid);
        SocketProcessor xp = (SocketProcessor)WebUtil.getWebProcessor(xformsKey, resource.getRequest(), resource.getResponse(), resource.getRequest().getSession());
        if (logger.isDebugEnabled()) {
            logger.debug("XFormsProcessor: " + xp);
        }
        if(message.getEventType().equalsIgnoreCase("DOMActivate")){
            try {
                xp.dispatchEvent(message.getTargetId());
                eventListToJSON(resource, xp);

            }  catch (XFormsException e) {
                e.printStackTrace();
            }
            message.setValue("hat getan");

        }
        return message;

    }


}
