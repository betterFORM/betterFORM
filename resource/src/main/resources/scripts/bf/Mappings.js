require(['dojo/_base/declare'],
    function(declare){
        declare("bf.Mappings",null, { });

        bf.Mappings.data = [
            ['.xfControl',                                                "bf/XFControl"],
            ['.xfInput.xsdString .xfValue, .xfInput.xsdDefault .xfValue', "bf/FactoryInput", "plain"],
            ['.xfInput.xsdBoolean > * >  .xfValue',                       "bf/FactoryInput", "boolean"]
        ];

/*
        bf.Mappings.addEntry = function(entry){
            bf.Mappings.data.add();
        }
        dojo.connect
*/

    }
);