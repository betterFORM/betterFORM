define(["../../../lib/dojo-release-1.6.1-src/dojo/behavior","dojo/_base/connect","dojo/dom-attr","dijit/registry","bf/XFControl","bf/util"],
    function(behavior,connect,domAttr,registry,XFControl) {
/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */


/*
todo: dependencies must be imported for foreign (non-dojo) components
<script type="text/javascript src="..."/>
<style type="text/css" src="..."/>
*/

        /*

        /xfInput/xsdString/-/-
        /xfInput/xsdBoolean/-/-

        /xfInput/xsdDate/-/-
        /xfInput/xsdDateTime/-/-
        /xfOutput/-/mediatypeText/-
        /xfOutput/-/mediatypeImage/-
        /xfOutput/-/mediatypeHtml/-
        /xfOutputU/xsdAnyURI/-/-
        ...

        ... somewhere ...

         connect.subscribe("/xfInput/xsdString/-/-", function(n){

            require(["dijit/registry"], function(connect){
                 var xfId = bf.util.getXfId(n);
                 var xfControlDijit = registry.byId(xfId);
                 xfControlDijit.setValue = function(value) {
                     domAttr.set(node, "value", value);
                 };
                 connect.connect(n,"onkeyup",function(evt){
                 // console.debug("onkeypress",n);
                 xfControlDijit.sendValue(n.value,evt);
                 });

                 connect.connect(n,"onblur",function(evt){
                 // console.debug("onblur",n);
                 xfControlDijit.sendValue(n.value, evt);
                 });
             };

         });

        var mappings = {
            ['.xfInput.xsdString.aDefault.mediatype', 'Input.createString'],
            ['.xfInput.xsdString.aDefault.mediatype', 'Input.createDate'],
            ['.xfInput.xsdString.aDefault.mediatype', '/asdasdas],
        }

        dojo.forEach(mappings, function(mapping){
                mapping : function(n) {
                    dojo.publish(
                }
        }
         */


/*
    Component Definition File which is the central for mapping XForms controls to client-side controls.
    todo:see newJSLayer-readme.txt
*/

    return {
        /*
         ################################ GENERIC XFCONTROL #########################################################
         matching all elements with .xfControl and instanciate a XFControl instance for each of them.
         Order is important here - all XFControl should be instanciated before their respective widget childs are
         created. Thus this behavior must be loaded before any other control behavior file.
         */
        '.xfControl':function(n) {
            console.debug("ControlBehaviour: XFControl found: ",n);
            var controlId = domAttr.get(n,"id");
            console.debug("ControlBehaviour: controlId:",controlId);
            var xfControl = new XFControl({
                id:controlId,
                controlType:"generic"
            }, n);
            console.debug("created new XFControl: ",xfControl);
        },

        /*
         ################################## GROUP #################################################################
         ################################## GROUP #################################################################
         ################################## GROUP #################################################################
         */

        //TODO

        /*
         ################################## SWITCH #################################################################
         ################################## SWITCH #################################################################
         ################################## SWITCH #################################################################
         */


        /*
         ################################## REPEAT #################################################################
         ################################## REPEAT #################################################################
         ################################## REPEAT #################################################################
         matching all elements with .xfRepeat and instanciate a Repeat Object for each of them.
         */
        '.xfRepeat':function(n) {
            // console.debug("\n\nRepeatBehaviour: found Repeat: ",n, " \n\n");
            new Repeat({repeatId:domAttr.get(n,"repeatId")}, n);
        },

        '.xfRepeat .xfRepeatItem':function(n) {
            // console.debug("\n\nRepeatBehaviour:  found xfRepeatItem: ",n, " \n\n");
        },

        // ############################## INPUT MAPPINGS ############################################################
        // ############################## INPUT MAPPINGS ############################################################
        // ############################## INPUT MAPPINGS ############################################################
        '.xfInput.xsdString .xfValue, .xfInput.xsdDefault .xfValue': function(n) {
            console.debug("FOUND: string input field: ",n);
            connect.publish("/xfInput/xsdString/-/-",n);
        },
        '.xfInput.xsdBoolean .xfValue': function(n) {
            console.debug("FOUND: boolean input field: ",n);
        },
        '.uaMobile .xfInput.xsdDate .xfValue, .uaTablet .xfInput.xsdDate .xfValue': function(n) {
        },

        // ############################## DATE INPUT DESKTOP ##############################
        /*  rendering dijit.formDateTextBox (DropdownDatePicker) for desktop browser */
        /* WARNING: xfValue must not be matched here due to templated widgets, this would end in an
        *  infinite loop */
        '.uaDesktop .xfInput.xsdDate .widgetContainer':function (node) {
        },

        // ############################## DATETIME INPUT ##############################
        '.xfInput.xsdDateTime .xfValue': function(n) {
            //todo: implement
        },

        // ############################## OUTPUT MAPPINGS ############################################################
        // ############################## OUTPUT MAPPINGS ############################################################
        // ############################## OUTPUT MAPPINGS ############################################################
        '.xfOutput.mediatypeText .xfValue': function(n) {
            console.debug("FOUND .xfOutput.mediatypeText .xfValuelue ",n);
        },

        '.xfOutput.mediatypeImage .xfValue': function(n) {
            // console.debug("FOUND: output mediatype image: ",n);
        },

        '.xfOutput.xsdAnyURI .xfValue': function(n) {
            // console.debug("FOUND: .xfOutput.xsdAnyURI .xfValue",n);

        },
        '.xfOutput.mediatypeHtml .xfValue': function(n) {
            // console.debug("FOUND: output mediatype HTML: ",n);

            registry.byId(bf.util.getXfId(n)).setValue = function(value) {
                n.innerHTML = value;
            };
        },

        // ############################## RANGE MAPPINGS ############################################################
        // ############################## RANGE MAPPINGS ############################################################
        // ############################## RANGE MAPPINGS ############################################################
        '.xfRange.xsdInteger .xfValue':function(n){
            // console.debug("Found xf:range: node:",n);
        },

        // ############################## SECRET MAPPINGS ############################################################
        // ############################## SECRET MAPPINGS ############################################################
        // ############################## SECRET MAPPINGS ############################################################
        '.xfSecret .xfValue':function(n){
        },

        // ############################## SELECT1 MAPPINGS ############################################################
        // ############################## SELECT1 MAPPINGS ############################################################
        // ############################## SELECT1 MAPPINGS ############################################################

        '.xfSelect1.aMinimal .xfValue, .xfSelect1.aDefault .xfValue': function(n) {
        },
        '.xfSelect1.aCompact .xfValue': function(n) {
        },

        '.xfSelect1.aFull .xfValue': function(n) {
        },

        // ############################## SELECT MAPPINGS ############################################################
        // ############################## SELECT MAPPINGS ############################################################
        // ############################## SELECT MAPPINGS ############################################################
        '.xfSelect.aMinimal .xfValue, .xfSelect.aDefault .xfValue, .xfSelect.aCompact .xfValue': function(n) {
        },
        '.xfSelect.aFull .xfValue': function(n) {
        },

        // ############################## TEXTAREA MAPPINGS #########################################################
        // ############################## TEXTAREA MAPPINGS #########################################################
        // ############################## TEXTAREA MAPPINGS #########################################################
        '.xfTextarea.mediatypeHtml .xfValue' : function (n) {
        },

        // ############################## TRIGGER MAPPINGS #########################################################
        // ############################## TRIGGER MAPPINGS #########################################################
        // ############################## TRIGGER MAPPINGS #########################################################
        '.xfTrigger .xfValue': function(n) {
            var parentId = n.id.substring(0,n.id.lastIndexOf("-"));
            connect.connect(n, "onclick", function(){
                fluxProcessor.dispatchEvent(parentId);
            });
        }


        // ############################## UPLOAD MAPPINGS #########################################################
        // ############################## UPLOAD MAPPINGS #########################################################
        // ############################## UPLOAD MAPPINGS #########################################################

        //TODO

        // ############################## ALERT BEHAVIORS #########################################################
        // ############################## ALERT BEHAVIORS #########################################################
        // ############################## ALERT BEHAVIORS #########################################################
/*
        'body.ToolTipAlert': function(n) {
            console.debug("AlertBehaviour.found: ToolTipAlert");

            require(["bf/AlertToolTip","dojo/domReady!"],
                function(AlertToolTip) {
                    var alertToolTip = new AlertToolTip({});
                    connect.subscribe(alertToolTip);
                    // console.debug("created ToolTipAlert:",alertToolTip);

                });

        },

        'body.InlineAlert': function(n) {
            console.debug("AlertBehaviour.found: InlineAlert");

            require(["bf/AlertInline","dojo/domReady!"],
                function(AlertInline) {
                    var alertInline= new AlertInline({});
                    connect.subscribe(alertInline);
                    // console.debug("created InlineAlert:",alertInline);

                });
        }
*/





    }
});

