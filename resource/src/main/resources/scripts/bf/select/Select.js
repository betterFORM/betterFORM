define(["dojo/_base/declare", "dijit/_Widget","dojo/query"],
    function(declare, _Widget,query){
        return declare(_Widget, {
            control:undefined,

            _onBlur:function() {
                console.debug("bf.SelectFull._onBlur arguments:",arguments, " control:",this.xfControl);
                var evt=new Object();
                evt.type = "blur";

                var selectedValue = "";
                query(".xfCheckBoxValue",this.domNode).forEach(function(item){
                    if(item.checked){
                        if(selectedValue  == ""){
                            selectedValue = item.value;
                        }else {
                            selectedValue += " " + item.value;
                        }
                    }
                });
                this.xfControl.sendValue(selectedValue,evt);
            }
    });
});

