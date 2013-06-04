/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.model.bind;

import de.betterform.xml.xforms.XFormsConstants;
import de.betterform.xml.xforms.XFormsElement;
import org.w3c.dom.Element;


/**
 * Some binding utility methods.
 */
public class BindingUtil implements XFormsConstants {

    public static boolean hasNodesetBinding(Element element){
        if(XFormsElement.getXFormsAttribute(element,NODESET_ATTRIBUTE) != null){
            return true;
        }
        return false;
    }

    public static boolean hasRef(Element element){
        if(XFormsElement.getXFormsAttribute(element,REF_ATTRIBUTE) != null){
            return true;
        }
        return false;
    }
}

