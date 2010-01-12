dojo.provide("betterform.FluxProcessor");

dojo.require("betterform.XFormsProcessor");
dojo.require("dojo.NodeList-fx");
dojo.require("betterform.ui.UIElementFactory");
dojo.require("dojox.layout.FloatingPane");

/**
	All Rights Reserved.
	@author Joern Turner
	@author Lars Windauer

    This class represents the interface to the remote XForms processor (aka 'betterForm Web') with DWR. It is the only class
    actually having dependency on DWR to handle the AJAX part of things and calling remote Java methods on
    de.betterform.web.betterform.FluxFacade.
**/
dojo.declare("betterform.FluxProcessor",
        betterform.XFormsProcessor,
{
    sessionKey:"",
    skipshutdown:false,
    isDirty:false,
    factory:null,
    currentControlId:"",
    unloadMsg:"You are about to leave this XForms application",
    webtest:'@WEBTEST@',
    isReady:false,
    contextroot:"",
     
/*
    keepAlive: function() {
        if(dwr.engine){
            dwr.engine.setErrorHandler(this._handleExceptions);
            dwr.engine.setOrdered(true);
            Flux.keepAlive(this.sessionKey);
        }
    },
*/
    constructor:function() {
        var fluxAttribute = function(attribute) {
                return dojo.attr(dojo.byId("fluxProcessor"), attribute);
        };
        this.factory = new betterform.ui.UIElementFactory();
        if(this.webtest != 'true') {
            dojo.connect(window,"onbeforeunload", this, "handleUnload");
            dojo.connect(window,"onunload",this, "close");
        }
        this.skipshutdown = false;
        try {
            console.debug("contextroot + \"/Flux\": " + fluxAttribute("contextroot") + "/Flux" );
            Flux._path = fluxAttribute("contextroot") + "/Flux";
            Flux.init( fluxAttribute("sessionkey"), dojo.hitch(this,this.applyChanges));
        }catch(ex) {
            fluxProcessor._handleExceptions("Failure executing initcall within Flux Constructor ", ex);
        }
    },

    handleUnload:function(evt) {
        // console.debug("FluxProcessor.handleUnload Event: ", evt);
        if(this.isDirty == true && this.skipshutdown == false ) {
            dojo.stopEvent(evt);
            // console.log(this.unloadMsg);
            // For IE
            evt.returnValue = this.unloadMsg;
              // For all others
            return this.unloadMsg;
        }
    },

    close:function() {
        var tmpSkipShutdown = dojo.hitch(this,fluxProcessor.skipShutdown).skipshutdown;
        if (!tmpSkipShutdown) {
            fluxProcessor.closeSession();
        }
    },

    closeSession: function() {
        try {
            dwr.engine.setErrorHandler(this._handleExceptions);
            dwr.engine.setOrdered(true);
            Flux.close(this.sessionKey);
        }
        catch(ex) {
            fluxProcessor._handleExceptions("Failure executing Flux.closeSession ", ex);
        }
    },

    ignoreExceptions: function (msg){
        console.warn("FluxProcessor.ignoreExceptions():");
    },

    //eventually an 'activate' method still makes sense to provide a simple DOMActivate of a trigger Element
    dispatchEvent: function (targetId) {
        // console.debug("FluxProcessor.dispatch(",targetId,") this: ", this);
        this._useLoadingMessage();
        try {
            dwr.engine.setErrorHandler(this._handleExceptions);
            dwr.engine.setOrdered(true);
            Flux.dispatchEvent(targetId, this.sessionKey,this.applyChanges);
        }catch(ex) {
            fluxProcessor._handleExceptions("Failure executing Flux.dispatchEvent", ex);
        }
        return false;
    },

    dispatchEventType:function(targetId, eventType,contextInfo){
        try {
            dwr.engine.setErrorHandler(this._handleExceptions);
            dwr.engine.setOrdered(true);
            if(contextInfo == undefined) {
                Flux.dispatchEventType(targetId, eventType, this.sessionKey, dojo.hitch(this,this.applyChanges));
            }else {
                Flux.dispatchEventTypeWithContext(targetId, eventType, this.sessionKey, contextInfo, dojo.hitch(this,this.applyChanges));
            }
        }
        catch(ex) {
            fluxProcessor._handleExceptions("Failure executing Flux.dispatchEventType", ex);
        }
    },

    setControlValue: function (id, value) {
        // console.debug("FluxProcessor.setControlValue", id, value);
        this.isDirty = true;
        try {
            dwr.engine.setErrorHandler(this._handleExceptions);
            this._useLoadingMessage();
            dwr.engine.setOrdered(true);
            dwr.engine.setErrorHandler(this._handleExceptions);
    //        Flux.setUIControlValue(id, value, this.sessionKey,this.changeManager.applyChanges);
            Flux.setUIControlValue(id, value, this.sessionKey,this.applyChanges);
        }
        catch(ex) {
            fluxProcessor._handleExceptions("Failure executing Flux.setControlValue", ex);
        }
    },

/*
    setRange: function (id, value) {
        dwr.engine.setErrorHandler(this._handleExceptions);
        Flux.setXFormsValue( id, value, this.sessionKey,this.changeManager.applyChanges);
    },


*/

    setRepeatIndex:function(/*String*/repeatId,/*String*/targetPosition){
        // console.debug("FluxProcessor.setRepeatIndex for Repeat "+ repeatId + " to position " + targetPosition);
        this._useLoadingMessage();
        try {
            dwr.engine.setErrorHandler(this._handleExceptions);
            dwr.engine.setOrdered(true);
            Flux.setRepeatIndex(repeatId, targetPosition, this.sessionKey, this.applyChanges);
        } catch(ex) {
            fluxProcessor._handleExceptions("Failure executing Flux.setRepeatIndex", ex);
        }
    },


    _useLoadingMessage:function(){
        // console.debug("FluxProcessor._useLoadingMessage");
        try {
            dwr.engine.setPreHook(function() {
                document.getElementById('indicator').className = 'xfEnabled';
            });
            dwr.engine.setPostHook(function() {
                document.getElementById('indicator').className = 'xfDisabled';
            });
        }
        catch(ex){
            fluxProcessor._handleExceptions("Failure executing Flux._useLoadingMessage", ex);
        }
    },

    _handleExceptions:function(msg, exception) {
        if(msg != undefined && exception != undefined ){
            console.error(msg, ' - Exception: ', exception);
        } else if(msg != undefined) {
            console.error(msg);
            if(this.webtest != 'true') {
               alert(msg);
            }else{
                //only for testing purposes
                this.logTestMessage(xmlEvent.contextInfo.errorinformation);
            }
        } else {
            console.error("Unknown exception occured! arguments: ", arguments);
        }
    },

    applyChanges: function(data) {
        // console.debug("FluxProcessor.applyChanges data:",data);
        console.group("EventLog");
        dojo.forEach(data,
            function(xmlEvent) {
                // *** DO NOT COMMENT THIS OUT !!! ***
                console.debug(xmlEvent.type ," [", xmlEvent.contextInfo, "]");
                switch (xmlEvent.type) {

                    case "betterform-index-changed"      : fluxProcessor._handleBetterFormIndexChanged(xmlEvent); break;
                    case "betterform-insert-itemset"     : fluxProcessor._handleBetterFormInsertItemset(xmlEvent); break;
                    case "betterform-insert-repeatitem"  : fluxProcessor._handleBetterFormInsertRepeatItem(xmlEvent); break;
                    case "betterform-item-deleted"       : fluxProcessor._handleBetterFormItemDeleted(xmlEvent); break;
                    case "betterform-load-uri"           : fluxProcessor._handleBetterFormLoadURI(xmlEvent); break;
                    case "betterform-render-message"     : fluxProcessor._handleBetterFormRenderMessage(xmlEvent); break;
                    case "betterform-replace-all"        : fluxProcessor._handleBetterFormReplaceAll(); break;
                    case "betterform-state-changed"      : fluxProcessor._handleBetterFormStateChanged(xmlEvent); break;
                    case "upload-progress-event"    : fluxProcessor._handleUploadProgressEvent(xmlEvent); break;
                    case "xforms-focus"             : fluxProcessor._handleXFormsFocus(xmlEvent); break;
                    case "xforms-help"              : fluxProcessor._handleShowHelp(xmlEvent); break;
                    case "xforms-hint"              : fluxProcessor._handleXFormsHint(xmlEvent); break;
                    case "xforms-link-exception"    : fluxProcessor._handleLinkException(xmlEvent); break;
                    case "betterform-switch-toggled"     : fluxProcessor._handleSwitchToggled(xmlEvent); break;
                    case "betterform-script-action"      : eval(xmlEvent.contextInfo["script"]); break;
                    case "xforms-value-changed"     : /* console.debug(xmlEvent); */ break;
                    case "xforms-version-exception" : fluxProcessor._handleVersionException(xmlEvent); break;
                    case "xforms-binding-exception" : fluxProcessor._handleBindingException(xmlEvent);break;
                    case "xforms-submit-error"      : fluxProcessor._handleSubmitError(xmlEvent); break;
                    case "DOMFocusIn"               : fluxProcessor._handleDOMFocusIn(xmlEvent); break;
                    case "xforms-out-of-range"      : fluxProcessor._handleOutOfRange(xmlEvent);break;
                    case "xforms-in-range"          :fluxProcessor._handleInRange(xmlEvent);break;

                    /* default handling for known events */
                    case "betterform-id-generated"       :
                    case "DOMActivate"              :
                    case "xforms-select"            :
                    case "xforms-deselect"          :
                    case "DOMFocusOut"              :
                    case "xforms-invalid"           :
                    case "xforms-model-construct"   :
                    case "xforms-model-construct-done":
                    case "xforms-valid"             :  break;
                    case "xforms-ready"             :  this.isReady=true;break; //not perfect - should be on XFormsModelElement
                    case "xforms-submit"            :
                    case "xforms-submit-done"       : break;

                    /* Unknow XMLEvent: */
                    default                         : console.error("Event " + xmlEvent.type + " unknown [Event:",xmlEvent, "]"); break;
                }
            }
        );
        console.groupEnd();
    },

    _handleBindingException:function(xmlEvent){
        if(this.webtest != 'true') {
            console.error("xforms-binding-exception at " + xmlEvent.contextInfo.targetId + " - " + xmlEvent.contextInfo.defaultinfo);
        }else{
            //only for testing purposes
            this.logTestMessage("xforms-binding-exception");
        }
    },

    _handleVersionException:function(xmlEvent){
        if(this.webtest != 'true') {
            console.error(xmlEvent.contextInfo.errorinformation);
        }else{
            //only for testing purposes
            this.logTestMessage(xmlEvent.contextInfo.errorinformation);
        }
    },

    _handleSubmitError:function(xmlEvent){
        console.error("xforms-submit-error at ", xmlEvent.contextInfo);
    },

    _handleBetterFormLoadURI:function(/*XMLEvent*/ xmlEvent){
        // xf:load show=replace
        if(xmlEvent.contextInfo.show == "replace") {
            fluxProcessor.skipshutdown = true;
            window.location.href = xmlEvent.contextInfo.uri;
        }
        // xf:load show=new
        else if(xmlEvent.contextInfo.show == "new"){
            window.open(xmlEvent.contextInfo.uri,'_betterform','menubar=yes,toolbar=yes,location=yes,directories=yes,fullscreen=no,titlebar=yes,hotkeys=yes,status=yes,scrollbars=yes,resizable=yes');

        }
        /* xf:load show=embed
            to embed an existing form into the running form
         */
        else if(xmlEvent.contextInfo.show == "embed"){
            var xlinkTarget = xmlEvent.contextInfo.xlinkTarget;
            // remove existing child of mount point
            this._unloadDOM(xmlEvent.contextInfo.xlinkTarget);
                

            var htmlEntryPoint = dojo.byId(xlinkTarget);
            // HACK to replace mountPoint with the embeded form (because FluxProcessor returns XML to insert as String)
            // TODO: replace hack

            htmlEntryPoint.innerHTML = xmlEvent.contextInfo.targetElement;
            dojo.attr(htmlEntryPoint, "id", xlinkTarget + "Old");
            var domToReplace = dojo.byId(xlinkTarget);
            // console.debug("betterform-load-uri show=embed : inserting DOM into target: " ,domToReplace);
            // console.dirxml(domToReplace);
            dojo.require("dojo.parser");
            dojo.parser.parse(htmlEntryPoint);

            dojo.place(domToReplace, htmlEntryPoint, "before");
            htmlEntryPoint.parentNode.removeChild(htmlEntryPoint);

        }
        /*  xf:load show=none
              to unload (loaded) subforms
         */
        else if(xmlEvent.contextInfo.show == "none") {
            // console.debug("FluxProcessor._handleBetterFormLoadURI: htmlEntryPoint", htmlEntryPoint);
            this._unloadDOM(xmlEvent.contextInfo.xlinkTarget);
        }
        else {
            console.error("betterform-load-uri show='"+xmlEvent.contextInfo.show+"' unknown!");
        }
    },


    _unloadDOM:function(target) {
        var htmlEntryPoint = dojo.byId(target);
        if(htmlEntryPoint == undefined) {
            return;
        }
        dojo.query("*[widgetid]", htmlEntryPoint).forEach(
            function(item) {
				// console.debug("FluxProcessor._unloadDOM: item", item);
                var childDijit = dijit.byId(dojo.attr(item, 'id'));
                if (childDijit != undefined) {
                    // console.debug("FluxProcessor._unloadDOM: destroy ", childDijit);
                    childDijit.destroy();
                }else {
                    // console.debug("FluxProcessor._unloadDOM: ChildDijit is null ");
                    childDijit = dijit.byId(dojo.attr(item, 'widgetid'));
					if(childDijit != undefined){
				 		childDijit.destroy();
                	}
        	    }
			}
        );
        while (htmlEntryPoint.hasChildNodes()){ htmlEntryPoint.removeChild(htmlEntryPoint.firstChild); }
    },


    _handleBetterFormRenderMessage:function(/*XMLEvent*/ xmlEvent){
        var message = xmlEvent.contextInfo.message;
        var level = xmlEvent.contextInfo.level;
        //console.debug("FluxProcessor.handleRenderMessage: message='" + message + "', level='" + level + "'");
        if(this.webtest != 'true') {

            if (level == "ephemeral") {
                dijit.byId("betterformMessageToaster").setContent(message,'message');
                dijit.byId("betterformMessageToaster").show();
            }
            else {
                window.alert(message);
            }
        }else{
            //only for testing purposes
            this.logTestMessage(message);
        }
    },
    _handleOutOfRange:function(xmlEvent){
/*
        var message = "Value for ui control '" + xmlEvent.contextInfo.targetName + "' (id:"+xmlEvent.contextInfo.targetId+") is out of range";
        if(this.webtest != 'true') {
            dijit.byId("betterformMessageToaster").setContent(message,'message');
            dijit.byId("betterformMessageToaster").show();
        }else{
            this.logTestMessage(message);
        }
*/
        var uiControl = dojo.byId(xmlEvent.contextInfo.targetId+"-value");
        if( uiControl != undefined) {
            if(dojo.hasClass(uiControl, "xfInRange")){
                dojo.removeClass(uiControl,"xfInRange");
            }
            dojo.addClass(uiControl, "xfOutOfRange");
        }
    },

    _handleInRange:function(xmlEvent){
        var uiControl = dojo.byId(xmlEvent.contextInfo.targetId+"-value");
        if( uiControl != undefined) {
            if(dojo.hasClass(uiControl, "xfOutOfRange")){
                dojo.removeClass(uiControl,"xfOutOfRange");
            }
            dojo.addClass(uiControl, "xfInRange");
        }
    },

    /*
    * function for testing purpose to avoid usage of JS alerts that can cause problems with Selenium
    */
    logTestMessage:function(message){
        var log = dojo.byId('messageLog');
        if (!log) {
            log = document.createElement('div');
            log.id = 'messageLog';
            document.body.appendChild(log);
        }
        var messageDiv = document.createElement('message');
        messageDiv.id = 'message' + ( this._countMessages(log) + 1);
        var messageText = document.createTextNode(message);
        messageDiv.appendChild(messageText);
        log.appendChild(messageDiv);
    },

    _countMessages:function (log) {
        var logMessagesCount = log.getElementsByTagName('message').length;
        return logMessagesCount;
    },

    _handleBetterFormReplaceAll:function(){
        fluxProcessor.skipshutdown = true;

        // add new parameter (params are located before the anchor sign # in an URI)
        var anchorIndex = window.location.href.lastIndexOf("#");
        var queryIndex = window.location.href.lastIndexOf("?");
        var path = window.location.href;
        if (anchorIndex != -1){
          path =  window.location.href.substring(0, anchorIndex);
        }
        if(queryIndex == -1){
          path += "?";
        }
        path += "&submissionResponse&sessionKey="+fluxProcessor.sessionKey;
        if (anchorIndex != -1){
          path +=  window.location.href.substring(anchorIndex);
        }

        window.open(path, "_self");
    },

    _handleBetterFormStateChanged:function(/*XMLEvent*/ xmlEvent){
        //console.debug("FluxProcessor._handleBetterFormStateChanged: targetId: " + xmlEvent.contextInfo.targetId , " parentId: " , xmlEvent.contextInfo.parentId);

/*
        console.debug("FluxProcessor._handleStateChanged this:", this,
                                                        "\n\txmlEvent: ",xmlEvent,
                                                        "\n\tcontextInfo: ",xmlEvent.contextInfo,
                                                        "\n\tparentId: ",xmlEvent.contextInfo.parentId,
                                                        "\n\ttargetId: ",xmlEvent.contextInfo.targetId,
                                                        "\n\tvalue: ",xmlEvent.contextInfo.value,
                                                        "\n\ttargetName: ",xmlEvent.contextInfo.targetName,
                                                        "\n\tmip:readonly: ",xmlEvent.contextInfo.readonly,
                                                        "\n\tmip:required: ",xmlEvent.contextInfo.required,
                                                        "\n\tmip:valid: ",xmlEvent.contextInfo.valid,
                                                        "\n\tmip:enabled: ",xmlEvent.contextInfo.enabled
                );
*/

        var xfControlId =xmlEvent.contextInfo.targetId;

        /**
         *
         * If:  StateChange Target is a XForms Repeat
         *
         * **/
        if(xmlEvent.contextInfo.targetName != undefined && xmlEvent.contextInfo.targetName == "repeat") {
            // console.debug("FluxProcessor._handleBetterFormStateChanged for Repeat");
            var repeatElement = dojo.query("*[repeatId='" + xfControlId + "']")[0];
            // console.debug("FluxProcessor._handleBetterFormStateChanged for Repeat:",repeatElement);
            if(repeatElement == undefined) {
                console.error("(FluxProcessor._handleBetterFormStateChanged xf:repeat: ", xfControlId, " does not exist");
                return;
            }
            var repeat = dijit.byId(dojo.attr(repeatElement, "id"));
            if(repeat != undefined) {
            repeat.handleStateChanged(xmlEvent.contextInfo);
        }
            else if(repeat == undefined && repeatElement != undefined) {
                repeat = new betterform.ui.container.Repeat({},repeatElement);
                repeat.handleStateChanged(xmlEvent.contextInfo);
            }
        }
        else if (xmlEvent.contextInfo.targetName != undefined  && xmlEvent.contextInfo.targetName == "group") {
                // console.debug("FluxProcessor._handleBetterFormStateChanged for Group");
                var group = dijit.byId(xmlEvent.contextInfo.targetId);

                if(group == undefined && dojo.byId(xmlEvent.contextInfo.targetId) != undefined) {
                    // console.debug("creating new Group: ",dojo.byId(xmlEvent.contextInfo.targetId));
                    dojo.require("betterform.ui.container.Group");
                    group = new betterform.ui.container.Group({},dojo.byId(xmlEvent.contextInfo.targetId));
                }
                /* group markup does not exist in ui, check if targetid references an repeatItem */
                else if(xmlEvent.contextInfo.targetId != undefined){
                    // console.debug("creating new Group (xmlEvent.contextInfo.targetId = undefined) : ",xmlEvent.contextInfo.targetId);
                    var control = dojo.query("*[repeatItemId='" + xmlEvent.contextInfo.targetId + "']")[0];
                    if (control != undefined && dojo.hasClass(control, "xfRepeatItem")) {
                        // console.debug("group get: ",dijit.byId(dojo.attr(control, "id")));
                        group = dijit.byId(dojo.attr(control, "id"));
                    }
                }
                if(group != undefined) {
                    group.handleStateChanged(xmlEvent.contextInfo);
                }else {
                    console.warn("FluxProcessor._handleBetterFormStateChanged: don't know how to handle xmlEvent: ",xmlEvent, " for target: " + xmlEvent.contextInfo.targetId +" [",xmlEvent.contextInfo.targetName , "]");
                }
        }

        else if(xmlEvent.contextInfo.targetName != undefined  && xmlEvent.contextInfo.targetName == "select1" && xmlEvent.contextInfo.copyItem != undefined){
            // console.debug("FluxProcessor._handleBetterFormStateChanged xf:copy handling: xmlEvent: ",xmlEvent, " contextInfo: ", xmlEvent.contextInfo);
            var warningMsg = "FluxProcessor._handleBetterFormStateChanged: Select1 ControlValue " + xmlEvent.contextInfo.targetId + "-value: No item selected"
            var select1 = dojo.byId(xmlEvent.contextInfo.targetId+"-value");
            if(select1 != undefined) {
                var selectedItemId = xmlEvent.contextInfo.selectedItem;
                // console.debug("FluxProcessor._handleBetterFormStateChanged xf:copy: selectedItem: ", selectedItemId);
                if (selectedItemId != undefined && selectedItemId != "") {
                    var selectItems = dojo.query(".xfSelectorItem", select1);
                    var itemSelected = false;
                    for (var i = 0; i < selectItems.length; i++) {
                        if (dojo.attr(selectItems[i], "id") == selectedItemId) {
                            // console.debug("SelectedItem: ", selectItems[i]);
                            select1.selectedIndex = (i);
                            itemSelected = true;
                        }
                    }
                    if(!itemSelected){
                        console.warn(warningMsg);
                    }
                }
                else {
                    console.warn(warningMsg);
                }
            } else { console.warn(warningMsg); }
        }

        /**
         *
         * Else If:  XForms Control Dijit allready exists call handleStateChanged on selected control
         *
         * **/
        else if(dijit.byId(xfControlId) != undefined){
            // console.debug("FluxProcessor.handleStateChanged on existing Dijit [id: " + xfControlId + ", / object:",dijit.byId(xfControlId),+"]");
            dijit.byId(xfControlId).handleStateChanged(xmlEvent.contextInfo);
        }
        /**
         *
         * Else If: XForms Control Dijit does not yet exist but a DOM Prototype Template is allready present
         *          represent state-changed-events directly after betterform-item-inserted event
         *
         * **/

        else if(dojo.byId(xfControlId) != undefined){
            // console.debug("FluxProcessor.handleStateChanged on existing DOM  [id: " + xfControlId + ", / xmlEvent:",xmlEvent,+"]");
            var controlNodeCreated = new betterform.ui.Control({contextInfo:xmlEvent.contextInfo},dojo.byId(xfControlId));
            controlNodeCreated.handleStateChanged(xmlEvent.contextInfo);

        }
        /**
         *
         * Else If: No XForms Control for the given id exist at all, e.q. inserting into repeats / itemsets,
         *          Algorithm relies on parent id of the given XForms Control
         * **/


        else if(xmlEvent.contextInfo.parentId != undefined && xmlEvent.contextInfo.parentId != "" ) {
            var parentDijit = dijit.byId(xmlEvent.contextInfo.parentId);
            // parent dijit does exist and executes handleStateChanged
            if(parentDijit != undefined) {
               // console.debug("FluxProcessor.handleStateChanged(ParentDijit" + parentDijit.id + ") no control found, execute handle state change on parent");
               parentDijit.handleStateChanged(xmlEvent.contextInfo);
            }
            // parent dijit does not(!!) exist yet
            else {
                var parentControlNode = dojo.byId(xmlEvent.contextInfo.parentId);
                if(parentControlNode == undefined) {
                    console.error("FluxProcessor betterform-state-changed  Warning: Neither Target nor its Parent does exist [xmlEvent" , xmlEvent, "]");
                }
                //  special handling for Select controls, check if parent node is selector item
                else if(dojo.hasClass(parentControlNode,"xfSelectorItem")){
                    // console.debug("FluxProcessor.handleStateChanged Target Node does not exist, Parent Control is SelectorItem (ParentSelector" + controlNode + ")");
                    dijit.byId(dojo.attr(parentControlNode.parentNode,"id")).handleStateChanged(xmlEvent.contextInfo);
                }
                else  {
                    console.warn("FluxProcessor betterform-state-changed: No handleStateChanged implementation availabled for contextinfo: " , xmlEvent.contextInfo);
                }
            }
        }
        else if(xmlEvent.contextInfo.targetName != undefined &&xmlEvent.contextInfo.targetName.indexOf("copy") != -1) {
/*
                    console.debug("FluxProcessor._handleBetterFormStateChanged: xforms:copy: value: ", xmlEvent.contextInfo.value);
                    if(xmlEvent.contextInfo.value != undefined) {
                        console.dirxml(xmlEvent.contextInfo.value);
		}
*/
                }
        else  {
            console.error("FluxProcessor betterform-state-changed Error: Processor does not know how to handle betterform-state-changed based on xmlEvent " , xmlEvent.contextInfo.targetId);
        }

    },

    _handleBetterFormInsertRepeatItem:function(xmlEvent) {
        // console.debug("betterform-insert-repeatitem [id: '", xmlEvent.contextInfo.targetId, "'] contextInfo:",xmlEvent.contextInfo);
        var repeatToInsertIntoDOM = dojo.query("*[repeatId='"+xmlEvent.contextInfo.targetId + "']");
        // console.debug("FluxProcessor._handleBetterFormInsertRepeatItem repeatToInsertIntoDOM: ",repeatToInsertIntoDOM);
        var repeatToInsertInto = dijit.byId(dojo.attr(repeatToInsertIntoDOM[0],"id"));
        if(repeatToInsertInto == undefined) {
             // console.debug("FluxProcessor._handleBetterFormInsertRepeatItem ",repeatToInsertIntoDOM);
             // console.dirxml(repeatToInsertIntoDOM[0]);
             dojo.require("betterform.ui.container.Repeat");
             repeatToInsertInto = new betterform.ui.container.Repeat({},repeatToInsertIntoDOM[0]);
        }
        repeatToInsertInto.handleInsert(xmlEvent.contextInfo);

    },

    _handleBetterFormInsertItemset:function(xmlEvent) {
        // console.debug("betterform-insert-itemset [id: '", xmlEvent.contextInfo.targetId, " / contextInfo:",xmlEvent.contextInfo,']' );
        if (dijit.byId(xmlEvent.contextInfo.targetId) != undefined) {
            // console.debug("betterform-insert-itemset handle Insert [id: '", xmlEvent.contextInfo.targetId, " / dijit:",dijit.byId(xmlEvent.contextInfo.targetId),']' );
            dijit.byId(xmlEvent.contextInfo.targetId).handleInsert(xmlEvent.contextInfo);
        } else {
            var itemsetDOM = dojo.byId(xmlEvent.contextInfo.targetId);
            // console.debug("betterform-insert-itemset [id: '", xmlEvent.contextInfo.targetId, " / dom:'",dojo.byId(xmlEvent.contextInfo.targetId),"']");
            var itemsetDijit;
            var itemsetType = dojo.attr(itemsetDOM, "dojoType");

            // console.debug("betterform-insert-itemset [id: '", xmlEvent.contextInfo.targetId, " / dojotype:'",itemsetType,"']");
            if (itemsetType != undefined && itemsetType == "betterform.ui.select.OptGroup") {
                dojo.require("betterform.ui.select.OptGroup");
                itemsetDijit = new betterform.ui.select.OptGroup({contextInfo:xmlEvent.contextInfo}, itemsetDOM);
            }
            else if (itemsetType != undefined && itemsetType == "betterform.ui.select1.RadioItemset") {
                dojo.require("betterform.ui.select1.RadioItemset");
                itemsetDijit = new betterform.ui.select1.RadioItemset({contextInfo:xmlEvent.contextInfo}, itemsetDOM);
            } else {
                console.warn("FluxProcessor apply betterform-insert-itemset: Itemset Type " + itemsetType + " not supported yet");
            }
            if (itemsetDijit != undefined) {
                itemsetDijit.handleInsert(xmlEvent.contextInfo);
            } else {
                console.warn("FluxProcessor apply betterform-insert-itemset: Error during itemset creation: ItemsetId" + xmlEvent.contextInfo.targetId + " itemsetType: " + itemsetType + " not supported yet");
            }
        }

    },
    _handleBetterFormItemDeleted:function(xmlEvent) {
        // console.debug("handle betterform-item-deleted for ", xmlEvent.contextInfo.targetName, " [id: '", xmlEvent.contextInfo.targetId, "'] contextInfo:",xmlEvent.contextInfo);
        if(xmlEvent.contextInfo.targetName == "itemset") {
             dijit.byId(xmlEvent.contextInfo.targetId).handleDelete(xmlEvent.contextInfo);
         }
         else if(xmlEvent.contextInfo.targetName == "repeat") {
            var repeatElement = dojo.query("*[repeatId='"+xmlEvent.contextInfo.targetId + "']");
            dijit.byId(dojo.attr(repeatElement[0],"id")).handleDelete(xmlEvent.contextInfo);
         }

    },
    _handleBetterFormIndexChanged:function(xmlEvent) {
        var repeatElement = dojo.query("*[repeatId='"+xmlEvent.contextInfo.targetId + "']");
        var repeat = dijit.byId(dojo.attr(repeatElement[0],"id"));
        // console.debug("FluxProcessor.betterform-index-changed Repeat: " , repeat, " targetId: ",xmlEvent.contextInfo.targetId);
        repeat.handleSetRepeatIndex(xmlEvent.contextInfo);
    },

    _handleUploadProgressEvent:function(xmlEvent) {
        var xfControlId =xmlEvent.contextInfo.targetid;
         // if XForms Control Dijit allready exists call handleStateChanged on selected control
        if(dijit.byId(xfControlId) != undefined){
            dijit.byId(xfControlId).updateProgress(xmlEvent.contextInfo.progress);
        }else {
            console.error("error during upload-progress-event: targetId " + xmlEvent.contextInfo.targetId + " does not exist");
        }
    },
    _handleXFormsFocus:function(xmlEvent) {
        // console.debug("xforms-focus xmlEvent: ",xmlEvent);
        try{
            var targetName = xmlEvent.contextInfo.targetName;
            if(targetName != "group" && targetName != "repeat" && targetName != "switch" && targetName!="case"){
                xfControlId = xmlEvent.contextInfo.targetId + "-value";
                dojo.byId(xfControlId).focus();
            }
        }
        catch(ex){
            fluxProcessor._handleExceptions("error during xforms-focus: targetId " + xmlEvent.contextInfo.targetId + " does not exist - Exception:", ex);
        }

    },

    _handleDOMFocusIn:function(xmlEvent) {
        xfControlId = xmlEvent.contextInfo.targetId + "-value";
        if(dijit.byId(xfControlId) != undefined) {
            dijit.byId(xfControlId)._handleDOMFocusIn();
        }else {
            dojo.byId(xfControlId).focus();
        }
    },

    _handleXFormsHint:function(xmlEvent) {
        var xfControlId =xmlEvent.contextInfo.targetId;
        var message = dojo.attr(dojo.byId(xfControlId + "-value"),"title");
        dijit.byId("betterformMessageToaster").setContent(message,'message');
        dijit.byId("betterformMessageToaster").show();
        if(this.webtest == 'true') {
            //only for testing purposes
            this.logTestMessage(message);
        }
    },

    _handleShowHelp:function(xmlEvent) {
        fluxProcessor.currentControlId = xmlEvent.contextInfo.targetId;
        fluxProcessor.showHelp();
    },

    _handleLinkException:function(xmlEvent){
        if(this.webtest != 'true'){
            console.error("Fatal error - " + xmlEvent.type + ": Failed to load resource: " + xmlEvent.contextInfo.resourceUri);
        }else{
            //only for testing purposes
            fluxProcessor.logTestMessage("Fatal error - " + xmlEvent.type + ": Failed to load resource: " + xmlEvent.contextInfo.resourceUri);
        }
//        fluxProcessor.closeSession();
    },

    _handleSwitchToggled:function(xmlEvent){
      // console.debug("FluxProcessor._handleSwitchToggled xmlEvent:", xmlEvent);
        var tmpSwitch = dijit.byId(xmlEvent.contextInfo.targetId);
        if(tmpSwitch == undefined && dojo.byId(xmlEvent.contextInfo.targetId)!= undefined){
            // console.debug("create new switch: ", xmlEvent);
            dojo.require("betterform.ui.container.Switch");
            tmpSwitch = new betterform.ui.container.Switch({},dojo.byId(xmlEvent.contextInfo.targetId));
        }
        tmpSwitch.toggleCase(xmlEvent.contextInfo);
    },

    fetchProgress:function(id, fileName){
        try {
        	Flux.fetchProgress(id, fileName, this.sessionKey,this.applyChanges);
        }
        catch(ex){
            fluxProcessor._handleExceptions("Failure executing Flux.fetchProgress", ex);
        }
    },

    /*
        @param: String locale
     */

    setLocale:function(locale){
        // console.debug("FluxProcessor.setLocale: Changed locale to: " + locale);
        try {
            Flux.setLocale(locale,this.sessionKey,this.applyChanges);
        }
        catch(ex){
            fluxProcessor._handleExceptions("Failure executing Flux.setLocale", ex);
        }
    },

    showHelp:function() {
        // console.debug("showng help for:", this.currentControlId);
        if (this.currentControlId == undefined || this.currentControlId == "" || this.currentControlId == '') {
            console.warn("No Control selected to show help for");
            return;
        }
        var helpCtrl = dojo.byId(this.currentControlId + '-help');
        if (helpCtrl == undefined) {
            console.warn("No help available for Control Id: '" + this.currentControlId + "'");
            return;
        }

        var helpWnd = dojo.byId('helpTrigger');
        var newdiv = document.createElement('div');
        dojo.style(newdiv, { "display":"none"});

        helpWnd.appendChild(newdiv);
        newdiv.innerHTML = helpCtrl.innerHTML;
        var helpDijit = new dojox.layout.FloatingPane({
            title: 'Help',
            closeable:true,
            resizable:true,
            dockable: false
        }, newdiv);
        dojo.addClass(helpDijit.domNode, "xfHelpPane");
        helpDijit.startup();
        
    }
});
