/*
 * Copyright (c) 2010. betterForm Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.ns;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nick Van den Bleeken
 * @version $Id$
 */
public class BetterFormNamespaceMap {
	public static Map kNAMESPACE_MAP;
    
    static {
    	kNAMESPACE_MAP = new HashMap();
    	kNAMESPACE_MAP.put(NamespaceConstants.BETTERFORM_PREFIX, NamespaceConstants.BETTERFORM_NS);
    	kNAMESPACE_MAP.put(NamespaceConstants.XFORMS_PREFIX, NamespaceConstants.XFORMS_NS);
    	kNAMESPACE_MAP.put(NamespaceConstants.XHTML_PREFIX, NamespaceConstants.XHTML_NS);
    	kNAMESPACE_MAP.put(NamespaceConstants.XMLEVENTS_PREFIX, NamespaceConstants.XMLEVENTS_NS);
    	kNAMESPACE_MAP.put(NamespaceConstants.XMLSCHEMA_NS, NamespaceConstants.XMLSCHEMA_NS);
    }
}
