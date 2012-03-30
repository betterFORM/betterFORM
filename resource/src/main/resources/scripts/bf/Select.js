define(["dojo/_base/declare", "dijit/_Widget"],
    function(declare, _Widget){
        return declare(_Widget, {
            control:undefined,

            _onBlur:function() {
                console.debug("bf.SelectFull._onBlur arguments:",arguments, " control:",this.control);
                var evt=new Object();
                evt.type = "blur";
                bf.SelectBehavior.selectFullSendValue(this.control,this.domNode,evt);
            }
        });
});

