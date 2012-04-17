define(["dojo/_base/declare","dojo/_base/connect","dijit/registry","dojo/dom-attr","bf/util"],
    function(declare,connect,registry,domAttr) {
        return declare(null,
            {
                /**
                 *
                 * @param type
                 * @param node
                 */
                create:function(type, node){
                    var n = node;
                    var xfId = bf.util.getXfId(n);
                    /*
                     xfControl is an instance of the XFControl class. This is the generic class that handles all interactions
                     with the XForms processor implementation. The concrete native browser or javascript controls are called
                     'widgets' in the context of the client side. They are the concrete representations the user interacts with.
                     */
                    var xfControlDijit = registry.byId(xfId);

                    switch(type){
                    /**
                     * INPUT TYPE STRING
                     */
                        case "plain":
                            console.debug("FactoryInput.createInputPlain");
                            /* Overwriten "abstract" API function on XFControl to handle updating of control values */
                            xfControlDijit.setValue = function(value, schemavalue) {
                                domAttr.set(n, "value", value);
                            };

                            /*
                             ###########################################################################################
                             EVENT BINDING
                             ###########################################################################################

                             Widgets are bound to their XFControl via events. Whenever a user changes the value of a control this change
                             must be propagated to its XFControl which will in turn send it to the server whenever appropriate.

                             There must be at least one event handler to notify XFControl of value changes. However it is highly
                             recommended to add two listeners - one to support incremental updates and one for onblur updates. Please
                             be aware that the order of registration can be significant for proper operation.
                             */

                            connect.connect(n,"onkeyup",function(evt){
                                // console.debug("onkeypress",n);
                                xfControlDijit.sendValue(n.value,evt);
                            });

                            connect.connect(n,"onblur",function(evt){
                                // console.debug("onblur",n);
                                xfControlDijit.sendValue(n.value, evt);
                            });
                            break;
                    /**
                     * INPUT TYPE BOOLEAN
                     */
                        case "boolean":
                            console.debug("FactoryInput.createInputBoolean");
                            // console.debug("FOUND: boolean input field: ",n);
                            if(domAttr.get(n,"type") != "checkbox"){
                                domAttr.set(n,"type","checkbox");
                            }
                            /* overwritten "abstract" API function of XFControl */
                            xfControlDijit.setValue = function(value, schemavalue) {
                                domAttr.set(node,"checked",value  == true || value == 'true');
                            };

                            /*
                             input type="checkbox" fails to honor readonly attribute and thus is overwritten here. It seems this is
                             rather a HTML Spec issue as e.g. comboxes behave the same. You can visibly change the value though
                             the control is readonly. As this seems rather contra intuitive for users we have chosen to use 'disabled'
                             here instead.
                             */
                            xfControlDijit.setReadonly = function() {
                                // console.debug("overwritten checkbox function");
                                domAttr.set(node, "disabled","disabled");
                            };
                            xfControlDijit.setReadwrite = function() {
                                node.removeAttribute("disabled");
                            };

                            connect.connect(node,"onblur",function(evt){
                                console.debug("onblur",n);
                                if(node.checked != undefined){
                                    xfControlDijit.sendValue(node.checked,evt);
                                }
                            });
                            connect.connect(node,"onclick",function(evt){
                                console.debug("FactoryInput (boolean) onclick node.checked:",node.checked);
                                if(node.checked != undefined){
                                    xfControlDijit.sendValue(node.checked,evt);
                                }
                            });
                            break;

                        case "date":
                            // console.debug("FactoryInput: found: .uaDesktop .xfInput.xsdDate .widgetContainer",node);
                            var self = this;
                            require(["dojo/query"],function(query){

                                n = query(".xfValue",node)[0];
                                // console.debug("found date value node: n:",n);

                                xfId = bf.util.getXfId(n);
                                xfControlDijit = registry.byId(xfId);
                                var appearance = domAttr.get(n,"appearance");
                                // console.debug("create new date for xfId:",xfId ," with appearance:",appearance);
                                var datePattern;
                                if (appearance && appearance.indexOf("iso8601:") != -1) {
                                    datePattern = appearance.substring(appearance.indexOf("iso8601:")+8);
                                    if(datePattern.indexOf(" ") != -1) {
                                        datePattern = datePattern.substring(0,datePattern.indexOf(" ")).trim();

                                    }
                                }
                                // console.debug("input type=date appearance:",appearance, " datePattern:",datePattern);
                                var xfValue = new Date(domAttr.get(n,"schemavalue"));
                                var dateWidget = undefined;
                                if(appearance && appearance.indexOf("bf:dropdowndate") != -1){
                                    require(["bf/input/DropDownDate"], function(DropDownDate) {
                                        dateWidget = new DropDownDate({
                                            value:domAttr.get(n,"schemavalue"),
                                            appearance:appearance,
                                            constraints:{
                                                selector:'date'
                                            }
                                        },n);
                                        self._connectDateDijit(xfControlDijit, dateWidget);
                                    });
                                } else if (datePattern) {
                                    require(["dijit/form/DateTextBox"], function(DateTextBox) {
                                        dateWidget = new DateTextBox({
                                            value:xfValue,
                                            required:false,
                                            constraints:{
                                                selector:'date',
                                                datePattern:datePattern
                                            } },n);
                                        self._connectDateDijit(xfControlDijit, dateWidget);
                                    });
                                } else {
                                    require(["dijit/form/DateTextBox"], function(DateTextBox) {
                                        dateWidget = new DateTextBox({
                                            value:xfValue,
                                            required:false,
                                            constraints:{
                                                selector:'date'
                                            }}, n);
                                        self._connectDateDijit(xfControlDijit, dateWidget);
                                    });
                                }
                            });
                            break;
                        case "dateTime":
                            console.warn("tbd: input dateTime");
                            break;
                        case "time":
                            console.warn("tbd: input Time");
                            break;
                        case "mobileDate":
                            xfControlDijit.setValue = function(value, schemavalue) {
                                domAttr.set(node, "value", value);
                            };
                            connect.connect(n,"onkeyup",function(evt){
                                xfControlDijit.sendValue(n.value,evt);
                            });

                            connect.connect(n,"onblur",function(evt){
                                xfControlDijit.sendValue(n.value, evt);
                            });

                            break;
                        case "tbd":
                            console.warn("No handler for node", node, " yet");
                        default:
                            console.warn("FactoryInput.default");

                    }
                },


                /**
                 *
                 * @param xfControlDijit
                 * @param dateWidget
                 * @private
                 */
                _connectDateDijit:function(xfControlDijit, dateWidget){
                    var xfControlDijitTmp = xfControlDijit;
                    var dateWidgetTmp = dateWidget;
                    // console.debug("connectDateDijit: xfControlDijitTmp:",xfControlDijitTmp," dateWidgetTmp:",dateWidgetTmp);
                    require(["dojo/dom-class"],function(domClass){
                        domClass.add(dateWidgetTmp.domNode,"xfValue");
                        connect.connect(dateWidgetTmp, "set", function (attrName, value) {
                            if((attrName == "focused" &&  !value) || attrName == "value") {
                                var dateValue;
                                if(dateWidgetTmp.serialize){
                                    dateValue = dateWidgetTmp.serialize(dateWidgetTmp.get("value")).substring(0, 10);
                                }else{
                                    dateValue = dateWidgetTmp.get("value");
                                }
                                var evt = new Object();
                                if(attrName == "focused"){
                                    evt.type ="blur";
                                    xfControlDijitTmp.sendValue(dateValue,evt);
                                }else {
                                    evt.type = "change";
                                    xfControlDijitTmp.sendValue(dateValue,evt);
                                }
                            }
                        });
                        xfControlDijitTmp.setValue = function(value,schemavalue) {
                            dateWidgetTmp.set('value', schemavalue);
                        };
                        xfControlDijitTmp.setReadonly = function() {
                            dateWidgetTmp.set('readOnly', true);
                        };
                        xfControlDijitTmp.setReadwrite = function() {
                            dateWidgetTmp.set('readOnly', false);
                        };
                    });

                }
            }
        );
    }
);

