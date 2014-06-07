package de.betterform.agent.web.atmosphere;

/**
 * Created by joern on 05.06.14.
 */
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

import java.util.Date;

public class Message {

//    private String message;
//    private String author;
    private long time;


    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    private String targetId;
    private String eventType;
    private String value;

    public Message() {
        this("", "", "");
    }

    public Message(String targetId, String eventType,String value) {
        this.targetId=targetId;
        this.eventType=eventType;
        this.value=value;
        this.time = new Date().getTime();
    }

/*
    public Message(String author, String message) {
        this.author = author;
        this.message = message;
        this.time = new Date().getTime();
    }
*/

/*
    public String getMessage() {
        return message;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setMessage(String message) {
        this.message = message;
    }
*/

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

}
