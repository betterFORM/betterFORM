require(['dojo/_base/declare'],
    function(declare){
        declare("bf.Mapping",null, { });

        /**
         * This file contains all mappings from XForms controls to concrete widget used in the browser.
         *
         * XForms control types and their properties are represented by CSS classes that are present on the
         * output of the XSLT transform (defaults to xhtml.xsl) that is applied once when initing an XForms session.
         *
         * The mapping is hold in an array and each entry consists of a triple:
         * [1] CSS 3 matcher used to identify a specific control in the rendered DOM. Please note that all CSS 3 matchers
         *     can be used as far as they are supported by Dojo query (ver. 1.7 or above). Though most of the matchers
         *     are supported there are some exceptions. If in doubt please consult the official Dojo documentation at
         *     http://dojotoolkit.org.
         * [2] the module name of a JavaScript class. This can be either a factory class that creates the control by using
         *     the third argument or a class that is used directly to provide the widgets' implementation. In the latter
         *     case the third entry in the array may be omitted.
         * [3] a descriptive string (name) that is unique in the context of the given factory.
         *
         * Note: the order of the entries in this array is significant. That means that rules that come first are also
         * applied first to the rendered DOM.
         */
        bf.Mapping.data = [
            //todo: is there any reason why first entry of triple used single quotes while others use double quotes?
            // CONTAINER
            ['.xfGroup',            "bf/factory/FactoryContainer", "group"],
            ['.xfRepeat',           "bf/factory/FactoryContainer", "repeat"],
            ['.xfSwitch.aDefault',  "bf/factory/FactoryContainer", "switch"],
            ['.xfSwitch.bfTabContainer',"bf/factory/FactoryContainer", "tabswitch"],


            // INPUTS
            ['.xfControl',                                                "bf/XFControl"],
            ['.xfInput.xsdString .xfValue, .xfInput.xsdDefault .xfValue', "bf/factory/FactoryInput", "text"],
            ['.xfInput.xsdBoolean > * >  .xfValue',                       "bf/factory/FactoryInput", "checkbox"],
            /*
            the following rule is special in that it matches for 'widgetContainer' and not 'xfValue'. The reason
            for this is the behavior of Dojo Dijits that replace the DOM Node they are applied to. But this creates
            problems with state handling which relies on the existence of these classes which Dojo does not preserver.
            Therefore here the Dijit is created as a child of 'widgetContainer'.
             */
            //todo: use descriptive names for the different types of controls
            ['.uaDesktop .xfInput.xsdDate .widgetContainer',              "bf/factory/FactoryInput", "date"],
            ['.xfInput.xsdDateTime > * > .xfValue',                       "bf/factory/FactoryInput", "tbd"],
            ['.xfInput.xsdTime > * >  .xfValue',                          "bf/factory/FactoryInput", "time"],
            //DateTime support for mobile might still be a problem and must be solved by a combination of controls
            ['.uaMobile .xfInput.xsdDate > * >  .xfValue, .uaTablet .xfInput.xsdDate > * >  .xfValue', "bf/factory/FactoryInput", "mobileDate"],

            // SECRET
            ['.xfSecret .xfValue', "bf/factory/FactorySecret", "password"],

            // SELECT1
            ['.xfSelect1.aMinimal .xfValue, .xfSelect1.aDefault .xfValue , .xfSelect1.aCompact .xfValue', "bf/factory/FactorySelect1", "combobox"],
            ['.xfSelect1.aFull .xfValue',                                                                 "bf/factory/FactorySelect1", "radiobuttons"],

            // SELECT
            ['.xfSelect.aMinimal .xfValue, .xfSelect.aDefault .xfValue, .xfSelect.aCompact .xfValue',   "bf/factory/FactorySelect", "listcontrol"],
            ['.xfSelect.aFull .xfValue',                                                                "bf/factory/FactorySelect", "checkboxes"],


            // OUTPUT
            ['.xfOutput.mediatypeText .xfValue',    "bf/factory/FactoryOutput", "text"],
            ['.xfOutput.mediatypeImage .xfValue',   "bf/factory/FactoryOutput", "image"],
            ['.xfOutput.xsdAnyURI .xfValue',        "bf/factory/FactoryOutput", "link"],
            ['.xfOutput.mediatypeHtml .xfValue',    "bf/factory/FactoryOutput", "html"],

            // RANGE
            ['.xfRange .xfValue',    "bf/factory/FactoryRange", "slider"],

            // TEXTAREA
            ['.xfTextarea.mediatypeHtml .xfValue',    "bf/factory/FactoryTextarea", "htmleditor"],

            // TRIGGER
            // this matcher handles several types of triggers like standard button, image button and link at once
            ['.xfTrigger.aMinimal .xfValue',   "bf/factory/FactoryTrigger", "link"],
            ['.xfTrigger.aDefault .xfValue,.xfTrigger.aCompact .xfValue,.xfTrigger.aFull .xfValue',   "bf/factory/FactoryTrigger", "button"],

            // UPLOAD
            ['.xfUpload .xfValue',    "bf/factory/FactoryUpload", "fileUpload"],
            ['.xfUpload .xfValue',    "bf/factory/FactoryUpload", "base64encoded"],
            ['.xfUpload .xfValue',    "bf/factory/FactoryUpload", "hexBinary"],

            // COMMON CHILDS
            ['body.ToolTipAlert',     "bf/common/AlertToolTip"],
            ['body.InlineAlert',      "bf/common/AlertInline"]


        ];
    }
);