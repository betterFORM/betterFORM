/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

package de.betterform.xml.xforms.ui.state;

import de.betterform.xml.config.Config;
import de.betterform.xml.dom.DOMComparator;
import de.betterform.xml.dom.DOMUtil;
import de.betterform.xml.events.BetterFormEventNames;
import de.betterform.xml.events.XFormsEventNames;
import de.betterform.xml.ns.NamespaceConstants;
import de.betterform.xml.ns.NamespaceResolver;
import de.betterform.xml.xforms.Container;
import de.betterform.xml.xforms.XFormsProcessorImpl;
import de.betterform.xml.xforms.exception.XFormsException;
import de.betterform.xml.xforms.model.ModelItem;
import de.betterform.xml.xforms.model.bind.RefreshView;
import de.betterform.xml.xforms.ui.BindingElement;
import de.betterform.xml.xforms.ui.Item;
import de.betterform.xml.xforms.ui.UIElementState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.events.EventTarget;

import java.text.*;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * State keeper utility methods.
 *
 * @author Ulrich Nicolas Liss&eacute;
 * @version $Id: UIElementStateUtil.java 3498 2008-08-28 12:00:39Z lars $
 */
public class UIElementStateUtil {

    public static final short VALID = 0;
    public static final short READONLY = 1;
    public static final short REQUIRED = 2;
    public static final short ENABLED = 3;
    private static final Log LOGGER = LogFactory.getLog(UIElementStateUtil.class);

    /**
     * Creates the state element.
     *
     * @param parent the parent element to use.
     * @return the state element.
     */
    public static Element createStateElement(Element parent) {
        Element state = DOMUtil.findFirstChildNS(parent, NamespaceConstants.BETTERFORM_NS, UIElementState.STATE_ELEMENT);
        if (state != null) {
            return state;
        }

        Document document = parent.getOwnerDocument();
        state = document.createElementNS(NamespaceConstants.BETTERFORM_NS, NamespaceConstants.BETTERFORM_PREFIX + ":" + UIElementState.STATE_ELEMENT);
        parent.appendChild(state);

        return state;
    }

    /**
     * Creates or updates the specified state attribute. If the value is
     * <code>null</code>, the attribute will be removed.
     *
     * @param element the element.
     * @param name    the attribute name.
     * @param value   the attribute value.
     */
    public static void setStateAttribute(Element element, String name, String value) {
        if (value != null) {
            element.setAttributeNS(NamespaceConstants.BETTERFORM_NS, NamespaceConstants.BETTERFORM_PREFIX + ":" + name, value);
        } else {
            element.removeAttributeNS(NamespaceConstants.BETTERFORM_NS, name);
        }
    }

    public static boolean hasPropertyChanged(boolean[] currentProperties, boolean[] newProperties, short index) {
        return (currentProperties == null && newProperties != null) ||
                (currentProperties != null && newProperties == null) ||
                (currentProperties != null && currentProperties[index] != newProperties[index]);
    }

    public static boolean hasValueChanged(Object currentValue, Object newValue) {
        if(currentValue instanceof Element && newValue instanceof Element){
            DOMComparator comparator = new DOMComparator();
            comparator.setIgnoreNamespaceDeclarations(true);
            return !comparator.compare(((Element)currentValue), ((Element)newValue));
        }else if(newValue instanceof Element || currentValue instanceof Element) {
            return false;
        }else {
            return (currentValue == null && newValue != null) ||
                    (currentValue != null && newValue == null) ||
                    (currentValue != null && !currentValue.equals(newValue));
        }
    }

    public static boolean hasTypeChanged(String currentType, String newType) {
        return hasValueChanged(currentType, newType);
    }

    public static ModelItem getModelItem(BindingElement owner) throws XFormsException {
        if (owner.hasBindingExpression()) {
            return owner.getModel()
                    .getInstance(owner.getInstanceId())
                    .getModelItem(owner.getInstanceNode());
        }
        return null;
    }

    public static boolean[] getModelItemProperties(ModelItem modelItem) {
        boolean[] properties = {true, false, false, false};
        if (modelItem != null) {
            properties[VALID] = modelItem.isValid();
            properties[READONLY] = modelItem.isReadonly();
            properties[REQUIRED] = modelItem.isRequired();
            properties[ENABLED] = modelItem.isRelevant();
        }

        return properties;
    }

    public static String getDefaultDatatype(Element element) {
        String prefix = NamespaceResolver.getPrefix(element, NamespaceConstants.XMLSCHEMA_NS);
        return prefix != null ? prefix + ":string" : "string";
    }

    public static String getDatatype(ModelItem modelItem, Element element) {
        String datatype = modelItem.getDeclarationView().getDatatype();
        if (datatype == null) {
            datatype = modelItem.getXSIType();
            if (datatype == null) {
                datatype = getDefaultDatatype(element);
            }
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("return datatype: " + datatype + " for " + DOMUtil.getCanonicalPath(element));
        }

        return datatype;
    }

    public static void dispatchXFormsEvents(BindingElement bindingElement, ModelItem modelItem) throws XFormsException {
        if(LOGGER.isDebugEnabled()){
            LOGGER.debug("dispatching refresh events for " + DOMUtil.getCanonicalPath(bindingElement.getElement()));
        }
        if (modelItem != null) {
            boolean[] properties = UIElementStateUtil.getModelItemProperties(modelItem);
            // dispatch events according to 4.3.4 The xforms-refresh Event
            RefreshView refreshView = modelItem.getRefreshView();
            if (refreshView == null) {
                return;
            }
            Container container = bindingElement.getContainerObject();
            EventTarget eventTarget = bindingElement.getTarget();

            boolean somethingChanged = false;
            if (refreshView.isValueChangedMarked() || refreshView.isEnabledMarked()) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(DOMUtil.getCanonicalPath(bindingElement.getElement()) + " bound to " + DOMUtil.getCanonicalPath((Node) modelItem.getNode()) + " has changed value or became relevant. Value='" + modelItem.getValue() + "'");
                }
                container.dispatch(eventTarget, properties[ENABLED] ? XFormsEventNames.ENABLED : XFormsEventNames.DISABLED, null);
                container.dispatch(eventTarget, XFormsEventNames.VALUE_CHANGED, null);
                container.dispatch(eventTarget, properties[VALID] ? XFormsEventNames.VALID : XFormsEventNames.INVALID, null);
                container.dispatch(eventTarget, properties[READONLY] ? XFormsEventNames.READONLY : XFormsEventNames.READWRITE, null);
                container.dispatch(eventTarget, properties[REQUIRED] ? XFormsEventNames.REQUIRED : XFormsEventNames.OPTIONAL, null);
                somethingChanged = true;
            }
            else{

                if (refreshView.isDisabledMarked()) {
                    container.dispatch(eventTarget, XFormsEventNames.DISABLED, null);
                    somethingChanged = true;
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(DOMUtil.getCanonicalPath((Node) modelItem.getNode()) + " is now disabled");
                    }
                }
                if (refreshView.isValidMarked()) {
                    container.dispatch(eventTarget, XFormsEventNames.VALID, null);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(DOMUtil.getCanonicalPath((Node) modelItem.getNode()) + " is now valid");
                    }
                }
                if (refreshView.isInvalidMarked()) {
                    container.dispatch(eventTarget, XFormsEventNames.INVALID, null);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(DOMUtil.getCanonicalPath((Node) modelItem.getNode()) + " is now invalid");
                    }
                }
                if (refreshView.isReadonlyMarked()) {
                    container.dispatch(eventTarget, XFormsEventNames.READONLY, null);
                    somethingChanged = true;
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(DOMUtil.getCanonicalPath((Node) modelItem.getNode()) + " is now readonly");                        
                    }
                }
                if (refreshView.isReadwriteMarked()) {
                    container.dispatch(eventTarget, XFormsEventNames.READWRITE, null);
                    somethingChanged = true;
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(DOMUtil.getCanonicalPath((Node) modelItem.getNode()) + " is now readwrite");
                    }
                }
                if (refreshView.isOptionalMarked()) {
                    container.dispatch(eventTarget, XFormsEventNames.OPTIONAL, null);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(DOMUtil.getCanonicalPath((Node) modelItem.getNode()) + " is now optional");
                    }
                }
                if (refreshView.isRequiredMarked()) {
                    container.dispatch(eventTarget, XFormsEventNames.REQUIRED, null);
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug(DOMUtil.getCanonicalPath((Node) modelItem.getNode()) + " is now required");
                    }
                }
            }

            if (somethingChanged) {
                /*
                if a control inherits a enabled/disabled or readwrite/readonly MIP from an ancestor bound node the
                parent refresh view has to be added to the list of refreshViews in Model. Otherwise the parent node
                (if it does not bind itself directly to a control) would never get resetted resulting in an endless
                loop of refresh events.
                
                 */
                modelItem.getModel().addRefreshItem(refreshView);
            } else {
                refreshView.reset();
            }

        }
    }

    public static void dispatchBetterFormEvents(BindingElement bindingElement, boolean[] currentProperties, boolean[] newProperties) throws XFormsException {
        dispatchBetterFormEvents(bindingElement, currentProperties, null, null, newProperties, null, null);
    }

    public static void dispatchBetterFormEvents(BindingElement bindingElement, String currentValue, String newValue) throws XFormsException {
        dispatchBetterFormEvents(bindingElement, null, currentValue, null, null, newValue, null);
    }

    public static void dispatchBetterFormEvents(BindingElement bindingElement, boolean[] currentProperties, String currentValue, boolean[] newProperties, String newValue) throws XFormsException {
        dispatchBetterFormEvents(bindingElement, currentProperties, currentValue, null, newProperties, newValue, null);
    }

    public static void dispatchBetterFormEvents(BindingElement bindingElement, boolean[] currentProperties, Object currentValue, String currentType, boolean[] newProperties, Object newValue, String newType) throws XFormsException {
        // determine changes
        Map context = new HashMap();
        if (hasPropertyChanged(currentProperties, newProperties, VALID)) {
            context.put(UIElementState.VALID_PROPERTY, String.valueOf(newProperties[VALID]));
        }
        if (hasPropertyChanged(currentProperties, newProperties, READONLY)) {
            context.put(UIElementState.READONLY_PROPERTY, String.valueOf(newProperties[READONLY]));
        }
        if (hasPropertyChanged(currentProperties, newProperties, REQUIRED)) {
            context.put(UIElementState.REQUIRED_PROPERTY, String.valueOf(newProperties[REQUIRED]));
        }
        if (hasPropertyChanged(currentProperties, newProperties, ENABLED)) {
            context.put(UIElementState.ENABLED_PROPERTY, String.valueOf(newProperties[ENABLED]));
        }
        if (hasValueChanged(currentValue, newValue)) {
            context.put(UIElementState.VALUE, newValue);
            String tmpType = newType;
            if (tmpType != null && tmpType.contains(":")) {
                tmpType = newType.substring(newType.indexOf(":") + 1, newType.length());
            }
            if (tmpType != null && (tmpType.equalsIgnoreCase("date") || tmpType.equalsIgnoreCase("dateTime"))) {
                UIElementState state = bindingElement.getUIElementState();
                context.put("schemaValue", state.getSchemaValue());
            }
        }
        if (hasTypeChanged(currentType, newType)) {
            context.put(UIElementState.TYPE_ATTRIBUTE, newType);
        }

        if (!context.isEmpty()) {
            // dispatch internal betterform event to update presentation context
            Container container = bindingElement.getContainerObject();
            EventTarget eventTarget = bindingElement.getTarget();
            if(bindingElement.getParentObject() instanceof Item){
                container.dispatch(eventTarget, BetterFormEventNames.ITEM_CHANGED, context);
            }else {
                container.dispatch(eventTarget, BetterFormEventNames.STATE_CHANGED, context);
            }

        }
    }

    /**
     * localize a string value depending on datatype and locale
     *
     * @param owner the BindingElement which is localized
     * @param type  the datatype of the bound Node
     * @param value the string value to convert
     * @return returns a localized representation of the input string
     */
    public static String localiseValue(BindingElement owner, Element state, String type, String value) throws XFormsException {
        if (value == null || value.equals("")) {
            return value;
        }
        String tmpType = type;
        if (tmpType != null && tmpType.contains(":")) {
            tmpType = tmpType.substring(tmpType.indexOf(":") + 1, tmpType.length());
        }
        if (Config.getInstance().getProperty(XFormsProcessorImpl.BETTERFORM_ENABLE_L10N, "true").equals("true")) {

            Locale locale = (Locale) owner.getModel().getContainer().getProcessor().getContext().get(XFormsProcessorImpl.BETTERFORM_LOCALE);
            if (tmpType == null) {
                tmpType = owner.getModelItem().getDeclarationView().getDatatype();
            }
            if (tmpType == null) {
                LOGGER.debug(DOMUtil.getCanonicalPath(owner.getInstanceNode()) + " has no type, assuming 'string'");
                return value;
            }
            if (tmpType.equalsIgnoreCase("float") ||
                    tmpType.equalsIgnoreCase("decimal") ||
                    tmpType.equalsIgnoreCase("double")) {
                if (value.equals("")) return value;
                if (value.equals("NaN")) return value; //do not localize 'NaN' as it returns strange characters
                try {
                    NumberFormat formatter = NumberFormat.getNumberInstance(locale);
                    if(formatter instanceof DecimalFormat){
                        //get original number format
                        int separatorPos = value.indexOf(".");
                        if(separatorPos == -1){
                            formatter.setMaximumFractionDigits(0);
                        }else{
                            int fractionDigitCount = value.length() - separatorPos - 1;
                            formatter.setMinimumFractionDigits(fractionDigitCount);
                        }

                        Double num = Double.parseDouble(value);
                        return formatter.format(num.doubleValue());
                    }
                } catch (NumberFormatException e) {
                    LOGGER.warn("Value '" + value + "' is no valid " + tmpType);
                    return value;
                }

            } else if (tmpType.equalsIgnoreCase("date")) {
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    Date d = sf.parse(value);
                    return DateFormat.getDateInstance(DateFormat.DEFAULT, locale).format(d);
                } catch (ParseException e) {
                    LOGGER.warn("Value '" + value + "' is no valid " + tmpType);
                    return value;
                }

            } else if (tmpType.equalsIgnoreCase("dateTime")) {

                //hackery due to lacking pattern for ISO 8601 Timezone representation in SimpleDateFormat
                String timezone = "";
                String dateTime = null;
                if (value.contains("GMT")) {
                    timezone = " GMT" + value.substring(value.lastIndexOf(":") - 3, value.length());
                    int devider = value.lastIndexOf(":");
                    dateTime = value.substring(0, devider) + value.substring(devider + 1, value.length());
                } else if (value.contains("Z")) {
                    timezone = "";
                    dateTime = value.substring(0, value.indexOf("Z"));

                }else if (value.contains("+")) {
                    timezone = value.substring(value.indexOf("+"),value.length());
                    dateTime = value.substring(0, value.indexOf("+"));

                }else {
                    dateTime = value;
                }
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

                try {
                    Date d = sf.parse(dateTime);
                    return DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale).format(d) + timezone;
                } catch (ParseException e) {
                    LOGGER.warn("Value '" + value + "' is no valid " + tmpType);
                    return value;
                }

            } else {
                //not logging for type 'string'
                if(LOGGER.isTraceEnabled() && !(tmpType.equals("string"))) {
                    LOGGER.trace("Type " + tmpType + " cannot be localized");
                }
            }
        }
        return value;
    }

    /**
     * returns the value of a state attribute
     *
     * @param state         the betterForm state element to examine
     * @param attributeName the name of the attribute to look up
     * @return the value of a state attribute
     */
    public static Object getStateAttribute(Element state, String attributeName) {
        if (state.hasAttributeNS(NamespaceConstants.BETTERFORM_NS, attributeName)) {
            return state.getAttributeNS(NamespaceConstants.BETTERFORM_NS, attributeName);
        }
        return null;
    }
}

// end of class
