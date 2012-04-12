define(["dojo/_base/declare", "dijit/_Widget","dojo/query"],
    function(declare, _Widget,query){
        return declare(_Widget, {
            control:undefined,

            _onBlur:function() {
                console.debug("bf.SelectFull._onBlur arguments:",arguments, " control:",this.xfControl);
                var evt=new Object();
                evt.type = "blur";
                this.selectFullSendValue(this.xfControl,this.domNode,evt);
            },

            selectFullSendValue:function(xfControl,n,evt) {
                var selectedValue = "";
                query(".xfCheckBoxValue",n).forEach(function(item){
                    if(item.checked){
                        if(selectedValue  == ""){
                            selectedValue = item.value;
                        }else {
                            selectedValue += " " + item.value;
                        }
                    }
                });
                xfControl.sendValue(selectedValue, evt);
            }

    });
});

