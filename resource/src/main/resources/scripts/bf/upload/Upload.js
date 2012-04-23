/*
 * Copyright (c) 2012. betterFORM Project - http://www.betterform.de
 * Licensed under the terms of BSD License
 */

define(["dojo/_base/declare",
    "dijit/_WidgetBase",
    "dijit/_TemplatedMixin",
    "dojo/text!./Upload.html",
    "dojo/on",
    "dojo/query",
    "dojo/dom-attr",
    "dojo/dom-style",
    "dojo/_base/lang",
    "dojo/_base/fx"],
    function(declare, _Widget, TemplatedMixin, template, on, query, domAttr, domStyle, lang, fx){
        return declare([_Widget,TemplatedMixin], {

            // parameters
            value:"",
            templateString:template,
            disabledNodes: new Array(),
            progress: null,
            progressBackground: null,
            fileId:"",
            fileValue:"",
            progressUpdate:null,
            name:'',
            xfControlId:'',

            postMixInProperties:function() {
                this.inherited(arguments);
            },

            postCreate:function() {
                this.inherited(arguments);

                on(this.inputNode,"change", lang.hitch(this, "confirm"));

                /*
                // console.debug("Upload.postMixInProperties: START this", this);
                this.inherited(arguments);
                var uploadName = domAttr(this.srcNodeRef, "name");
                var uploadFileId = domAttr(this.srcNodeRef, "fileId");
                var uploadFileValue = domAttr(this.srcNodeRef, "fileValue");
                if(uploadFileId == undefined || uploadFileId == ""){
                    uploadName = fluxProcessor.dataPrefix + this.xfControlId;
                }
                //console.debug("uploadName: ",uploadName, " uploadFileId:",uploadFileId, " uploadFileValue:",uploadFileValue);
                domAttr(this.inputNode, "name", uploadName);
                domAttr(this.fileName, "id", uploadFileId);
                domAttr(this.fileName, "value", uploadFileValue);
                dojo.removeClass(this.domNode, "xfValue");
                  */
            },

            confirm: function() {
                var action = confirm("Really upload ?");
                if (action) {
                    this._submitFile();
                }
                else {
                    this.inputNode.value = "";
                }
            },

            _submitFile: function() {
                // console.debug("UploadPlain._submitFile");

                // disable all controls contained in repeat prototypes to avoid
                // inconsistent updates.
                var me = this.inputNode;
                query(".xfUpload.xfReadWrite .xfValue").forEach(function(item) {
                    if(item != me){
                        //console.debug("Disable Upload Item: ", item);
                        domAttr.set(item, "disabled", "disabled");
                    }
                });


/* Effect.BlindDown(this.xformsId + "-progress"); */
                domStyle.set(this.progress, 'display', 'block');
                domStyle.set(this.progress, 'opacity', '1');


                var path = this.inputNode.value;
                var filename = path.substring(path.lastIndexOf("/") + 1);
                // console.debug("Upload: npath: ", path, " filename:", filename);
                //polling betterForm for update information and submit the form

                //this.progressHandle = connect.subscribe("upload-progress-event-"+ this.xfControlId, this, "updateProgress");
                this.progressUpdate = setInterval("fluxProcessor.fetchProgress('" + this.xfControlId + "','" + filename + "')", 500);

                document.forms["betterform"].target = "UploadTarget";
                document.forms["betterform"].submit();
                return true;
            },

            updateProgress: function (value) {
                // console.debug("UploadPlain.updateProgress: value",value);
                if (value != 0) {
                    domStyle.set(this.progressBackground,"width",value + "%");
                }
                if (value < 0) {
                    alert("Upload failed");
                }
                if (value == 100) {
                    // stop polling
                    // console.debug("UploadPlain.updateProgress before Clear Interval");
                    clearInterval(this.progressUpdate);
                    // console.debug("stopped polling");
                    domStyle.set(this.progressBackground,"width","100%");


                    fx.fadeOut({
                        node: this.progress,
                        duration:2000,
                        onEnd: dojo.hitch(this,function() {
                            domStyle.set(this.progress, 'display', 'none');

                        })
                    }).play();
                }



                    // reset disabled controls

                    query(".xfUpload.xfReadWrite .xfValue:disabled").forEach(function(item) {
                        domAttr.remove(item, "disabled");
                    });




            }



        });
});



/*
 _onFocus:function() {
 this.inherited(arguments);
 this.handleOnFocus();
 },
 */



/*           onChange: function() {
 // console.debug("UploadPlain.onChange");
 var action = confirm("Really upload ?");
 if (action) {
 //this._submitFile(this.inputNode);
 }
 else {
 this.inputNode.value = "";
 }
 },
 */
/*
 */