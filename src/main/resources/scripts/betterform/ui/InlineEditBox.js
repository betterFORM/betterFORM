dojo.provide("betterform.ui.InlineEditBox");

dojo.require("dijit.InlineEditBox");
dojo.require("betterform.ui.input.TextField");

dojo.declare("betterform.ui.InlineEditBox",
	dijit.InlineEditBox,
	{
    // editor: String
    //		Class name for Editor widget
    editor: "betterform.ui.input.TextField",

	postMixInProperties: function(){
		this.inherited(arguments);
//        dojo.disconnect(this.displayNode,'ondijitclick','_onClick');
//        this.connect(this.displayNode, "onfocus", "_onClick");

	},
	save: function(){
		console.debug("betterform.ui.InlineEditBox.save this: ",this, " editWidget: ",this.editWidget);
		this.editWidget.editWidget.setTextValue(this.editWidget.getValue());
        this.inherited(arguments);
	},

	edit: function(){
        console.debug("betterform.ui.InlineEditBox.edit this:",this);
		// summary:
		//		Display the editor widget in place of the original (read only) markup.

		if(this.disabled || this.editing){ return; }
		this.editing = true;

		var editValue =
				(this.renderAsHtml ?
				this.value :
				this.value.replace(/\s*\r?\n\s*/g,"").replace(/<br\/?>/gi,"\n").replace(/&gt;/g,">").replace(/&lt;/g,"<").replace(/&amp;/g,"&").replace(/&quot;/g,"\""));

		// Placeholder for edit widget
		// Put place holder (and eventually editWidget) before the display node so that it's positioned correctly
		// when Calendar dropdown appears, which happens automatically on focus.
		var placeholder = dojo.doc.createElement("span");
		dojo.place(placeholder, this.domNode, "before");

		var ew = this.editWidget = new betterform.ui._InlineEditor({
			value: dojo.trim(editValue),
			autoSave: this.autoSave,
			buttonSave: this.buttonSave,
			buttonCancel: this.buttonCancel,
			renderAsHtml: this.renderAsHtml,
			editor: this.editor,
			editorParams: this.editorParams,
			style: dojo.getComputedStyle(this.displayNode),
			save: dojo.hitch(this, "save"),
			cancel: dojo.hitch(this, "cancel"),
			width: this.width,
            betterformId:this.id
		}, placeholder);

		// to avoid screen jitter, we first create the editor with position:absolute, visibility:hidden,
		// and then when it's finished rendering, we switch from display mode to editor
		var ews = ew.domNode.style;
		this.displayNode.style.display="none";
		ews.position = "static";
		ews.visibility = "visible";

		// Replace the display widget with edit widget, leaving them both displayed for a brief time so that
		// focus can be shifted without incident.  (browser may needs some time to render the editor.)
		this.domNode = ew.domNode;
		setTimeout(function(){
			if(ew.editWidget._resetValue === undefined){
				ew.editWidget._resetValue = ew.getValue();
			}
			ew.focus();
		}, 100);
	}
});
dojo.declare(
	"betterform.ui._InlineEditor",
	 [dijit._InlineEditor],
{
   betterformId:null,

	postCreate: function(){
		// Create edit widget in place in the template
		var cls = dojo.getObject(this.editor);

		// Copy the style from the source
		// Don't copy ALL properties though, just the necessary/applicable ones
		var srcStyle = this.style;
		var editStyle = "line-height:" + srcStyle.lineHeight + ";";
		dojo.forEach(["Weight","Family","Size","Style"], function(prop){
			editStyle += "font-"+prop+":"+srcStyle["font"+prop]+";";
		}, this);
		dojo.forEach(["marginTop","marginBottom","marginLeft", "marginRight"], function(prop){
			this.domNode.style[prop] = srcStyle[prop];
		}, this);
		if(this.width=="100%"){
			// block mode
			editStyle += "width:100%;";
			this.domNode.style.display = "block";
		}else{
			// inline-block mode
			editStyle += "width:" + (this.width + (Number(this.width)==this.width ? "px" : "")) + ";";
		}
		this.editorParams.style = editStyle;


		this.editorParams[ "displayedValue" in cls.prototype ? "displayedValue" : "value"]= this.value;
        var betterformValueId = this.betterformId.substring(0,this.betterformId.length-7)+"-value";
		console.debug("betterform.ui._InlineEdit.postCreate this:",this.domNode, " generatedId: ", betterformValueId);
        this.editorParams[ "id"]= betterformValueId;
        var ew = this.editWidget = new cls(this.editorParams, this.editorPlaceholder);

		this.connect(ew, "onChange", "_onChange");

		// Monitor keypress on the edit widget.   Note that edit widgets do a stopEvent() on ESC key (to
		// prevent Dialog from closing when the user just wants to revert the value in the edit widget),
		// so this is the only way we can see the key press event.
		this.connect(ew, "onKeyPress", "_onKeyPress");

		if(this.autoSave){
			this.buttonContainer.style.display="none";
		}
	}
	
});
