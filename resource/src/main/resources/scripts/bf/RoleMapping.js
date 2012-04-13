require(['dojo/_base/declare'],
    function(declare){
        declare("bf.RoleMapping",null, { });

        bf.RoleMapping.data = [
            // INPUTS
            ['.xfControl',                                                "bf/XFControl"],
            ['.xfInput.xsdString .xfValue, .xfInput.xsdDefault .xfValue', "bf/factory/FactoryInput", "plain"],
            ['.xfInput.xsdBoolean > * >  .xfValue',                       "bf/factory/FactoryInput", "boolean"],
            ['.uaDesktop .xfInput.xsdDate .widgetContainer',              "bf/factory/FactoryInput", "date"],
            ['.xfInput.xsdDateTime > * > .xfValue',                       "bf/factory/FactoryInput", "tbd"],
            ['.uaMobile .xfInput.xsdDate > * >  .xfValue, .uaTablet .xfInput.xsdDate > * >  .xfValue', "bf/factory/FactoryInput", "mobileDate"],

            // SECRET
            ['.xfSecret .xfValue', "bf/factory/FactorySecret", "plain"],

            // SELECT1
            ['.xfSelect1.aMinimal .xfValue, .xfSelect1.aDefault .xfValue', "bf/factory/FactorySelect1", "minimal"],
            ['.xfSelect1.aCompact .xfValue',                               "bf/factory/FactorySelect1", "compact"],
            ['.xfSelect1.aFull .xfValue',                                  "bf/factory/FactorySelect1", "full"],

            // SELECT1
            ['.xfSelect.aMinimal .xfValue, .xfSelect.aDefault .xfValue, .xfSelect.aCompact .xfValue',   "bf/factory/FactorySelect", "minimal"],
            ['.xfSelect.aFull .xfValue',                                                                "bf/factory/FactorySelect", "full"],


            // OUTPUT
            ['.xfOutput.mediatypeText .xfValue',    "bf/factory/FactoryOutput", "text"],
            ['.xfOutput.mediatypeImage .xfValue',   "bf/factory/FactoryOutput", "image"],
            ['.xfOutput.xsdAnyURI .xfValue',        "bf/factory/FactoryOutput", "anyURI"],
            ['.xfOutput.mediatypeHtml .xfValue',    "bf/factory/FactoryOutput", "html"],

            // RANGE
            ['.xfRange .xfValue',    "bf/factory/FactoryRange", "plain"],

            // TEXTAREA
            ['.xfTextarea.mediatypeHtml .xfValue',    "bf/factory/FactoryTextarea", "html"],

            // TRIGGER
            ['.xfTrigger .xfValue',   "bf/factory/FactoryTrigger", "plain"],

            // UPLOAD
            ['.xfUpload .xfValue',    "bf/factory/FactoryUpload", "anyURI"],

            // CONTAINER
            ['.xfGroup',             "bf/factory/FactoryContainer",  "group"],
            ['.xfRepeat',             "bf/factory/FactoryContainer", "repeat"],

            // COMMON CHILDS
            ['body.ToolTipAlert',     "bf/common/AlertToolTip"],
            ['body.InlineAlert',      "bf/common/AlertInline"]


        ];
    }
);