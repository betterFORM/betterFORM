dojo.provide("betterform.xf.Select");

dojo.declare(
    "betterform.xf.Select",dijit._Widget,
    {
        control:undefined,

        _onBlur:function() {
            console.debug("betterform.xf.SelectFull._onBlur arguments:",arguments, " control:",this.control);
            var evt=new Object();
            evt.type = "blur";
            betterform.xf.SelectBehavior.selectFullSendValue(this.control,this.domNode,evt);
        }
    }
);

