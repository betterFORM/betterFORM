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

import org.atmosphere.config.service.AtmosphereHandlerService;
import org.atmosphere.config.service.Get;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import org.atmosphere.cpr.AtmosphereResponse;
import org.atmosphere.interceptor.AtmosphereResourceLifecycleInterceptor;
import org.atmosphere.interceptor.BroadcastOnPostAtmosphereInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.Date;

/**
 * AtmosphereHandler that provides the adapter between network layer and betterFORM XForms processor.
 *
 * @author Joern Turner
 */
@AtmosphereHandlerService(path = "/msg",
        interceptors= {AtmosphereResourceLifecycleInterceptor.class,
                       BroadcastOnPostAtmosphereInterceptor.class})
public class BetterformAtmosphereHandler extends MyHandler<String> {
    private final Logger logger = LoggerFactory.getLogger(BetterformAtmosphereHandler.class);

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
    public void onMessage(AtmosphereResponse response, String message) throws IOException {
        // Message looks like { "author" : "foo", "message" : "bar" }

        String author = message.substring(message.indexOf(":") + 2, message.indexOf(",") - 1);
        String chat = message.substring(message.lastIndexOf(":") + 2, message.length() - 2);
        logger.debug("atmosphere: " + message);

        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(response.getOutputStream(), "UTF-8"));
        response.setCharacterEncoding("UTF-8");
        //response.getWriter().write(new Data(author, chat).toString());
        bw.write(new Data(author, chat).toString());
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

        private final String text;
        private final String author;

        public Data(String author, String text) {
            this.author = author;
            this.text = text;
        }

        public String toString() {
            return "{ \"text\" : \"" + text + "\", \"author\" : \"" + author + "\" , \"time\" : " + new Date().getTime() + "}";
        }
    }
}
