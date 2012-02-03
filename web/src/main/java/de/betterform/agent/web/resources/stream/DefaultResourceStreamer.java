/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */
package de.betterform.agent.web.resources.stream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;

/**
 * Default streamer for scripts, css, images and all other content
 */
public class DefaultResourceStreamer implements ResourceStreamer {

        public boolean isAppropriateStreamer(String mimeType) {
                return (mimeType != null);
        }

        public void stream(HttpServletRequest request, HttpServletResponse response, InputStream inputStream) throws IOException {
                byte[] buffer = new byte[2048];
               
                 int length;
                 while ((length = (inputStream.read(buffer))) >= 0) {
                         response.getOutputStream().write(buffer, 0, length);
                 }
        }

}

