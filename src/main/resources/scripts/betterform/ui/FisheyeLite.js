dojo.provide("betterform.ui.FisheyeLite");


dojo.require("dijit._Widget");
dojo.require("dojox.widget.FisheyeLite");

dojo.declare("betterform.ui.FisheyeLite",
	dojox.widget.FisheyeLite,
	{

	durationIn: 350,
	
	postCreate: function(){
		this.inherited(arguments);
		this._target = dojo.query(".xfControl", this.srcNodeRef)[0];
		this._makeAnims();
		
		this.connect(this.domNode,"onmouseover","show");
		this.connect(this.domNode,"onmouseout","hide");
		this.connect(this._target,"onclick","onClick");

	}
});
