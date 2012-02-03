/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.resources;

import java.util.Collection;

public interface ResourceHolder {

        public void addResource(String resource);
       
        public Collection<String> getResources();
}

