/*
 * Copyright 2014 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package de.betterform.agent.web.atmosphere;

import org.atmosphere.cache.UUIDBroadcasterCache;
import org.atmosphere.client.TrackMessageSizeInterceptor;
import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.config.service.Get;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.cpr.Broadcaster;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.atmosphere.interceptor.HeartbeatInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * AtmosphereHandler that provides the adapter between network layer and betterFORM XForms processor.
 *
 * @author Joern Turner
 */
@AtmosphereHandlerService(path = "/msg",
        broadcasterCache = UUIDBroadcasterCache.class,
        interceptors = {AtmosphereResourceLifecycleInterceptor.class,
                        BroadcastOnPostAtmosphereInterceptor.class,
                        TrackMessageSizeInterceptor.class,
                        HeartbeatInterceptor.class})
public class BetterformAtmosphereHandler extends MyHandler<String> {
    private final Logger logger = LoggerFactory.getLogger(BetterformAtmosphereHandler.class);
    private AtmosphereResource resource;

    @Get
    public void init(AtmosphereResource r) {
        try {
            r.getRequest().setCharacterEncoding("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        r.getResponse().setCharacterEncoding("UTF-8");
    }

    @Override
    public void onOpen(AtmosphereResource resource) throws IOException {
        this.resource = resource;
        ServletRequest request = resource.getRequest().getRequest();
        ServletResponse response = resource.getResponse().getResponse();


        if(logger.isDebugEnabled()){
            logger.debug("ServletRequest object: " + request);
            logger.debug("ServletResponse object: " + response);
        }

        super.onOpen(resource);

    }

    @Override
    public void onMessage(AtmosphereResponse response, String message) throws IOException {
        // Message looks like { "targetId" : "foo", "eventType" : "updateValue" }

        logger.debug("atmosphere: " + message);
        Broadcaster broadcaster = resource.getBroadcaster();
        broadcaster.broadcast("hello");


        // cut leading and trailing curly braces and tokenize the string
        Map <String,String> props = new HashMap <String,String>();
        StringTokenizer st = new StringTokenizer(message.substring(1,message.length()-1), ":,");
        while(st.hasMoreTokens()) {
            String key = st.nextToken();
            String val = st.nextToken();
            logger.debug(key + "\t" + val);
            props.put(key.substring(1,key.length()-1),val.substring(1,val.length()-1));
        }

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
        response.setCharacterEncoding("UTF-8");
        String out = new Data(props.get("targetId"), props.get("eventType"), props.get("value")).toString();
        logger.debug("out message:"+out);
        bw.write(out);
        bw.flush();
    }

    @Override
    public void onDisconnect(AtmosphereResponse response) throws IOException {
        AtmosphereResourceEvent event = response.resource().getAtmosphereResourceEvent();
        if (event.isCancelled()) {
            logger.info("Browser {} unexpectedly disconnected", response.resource().uuid());
        } else if (event.isClosedByClient()) {
            logger.info("Browser {} closed the connection", response.resource().uuid());
        }
    }

    private final static class Data {

        private final String targetId;
        private final String eventType;
        private final String val;


        public Data(String targetId, String eventType, String val) {
                this.targetId = targetId;
            this.eventType = eventType;
            this.val = val;
        }

        public String toString() {
            return "{ \"targetId\" : \"" + targetId + "\", \"eventType\" : \"" + eventType + "\" , \"value\" : \"" + val + "\" , \"time\" : " + new Date().getTime() + "}";
        }
    }
}
