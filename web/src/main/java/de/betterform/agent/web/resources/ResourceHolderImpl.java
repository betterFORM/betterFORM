/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.agent.web.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceHolderImpl implements ResourceHolder {

        private List<String> resources;
        
        public ResourceHolderImpl() {
                resources = new ArrayList<String>();
        }
        
        public void addResource(String resource) {
                if(!resources.contains(resource))
                        resources.add(resource);
        }

        public Collection<String> getResources() {
                return resources;
        }
}
