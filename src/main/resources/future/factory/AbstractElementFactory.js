dojo.provide("betterform.ui.AbstractUIElementFactory");

dojo.require("betterform.ui.input.Date");
// creates all betterForm Widgets for xf:* UI Controls dependent on datatype and controltype.
dojo.declare(
        "betterform.ui.AbstractUIElementFactory",
        null,
{

    controlType:"",
    dataType:"String",

    createWidget:function(sourceNode, controlId) {
        this.controlType = dojo.attr(sourceNode, "controlType");
        this.dataType = dojo.attr(sourceNode, "dataType");
        if (this.dataType != undefined && this.dataType.indexOf(":") != -1) {
            this.dataType = this.dataType.substring(this.dataType.indexOf(":") + 1, this.dataType.length);
        }
        var classValue = "xfValue";
        if (dojo.attr(sourceNode, "class")) {
            var controlClasses = dojo.attr(sourceNode, "class");
            if (controlClasses.indexOf("xfValue") == -1) {
                classValue = classValue + " " + controlClasses;
            } else {
                classValue = controlClasses;
            }
        }
        var newWidget = null;
        if (this.controlType == undefined) {
            console.warn("UIElementFactory.createWidget: undefined controlType, Node: ", sourceNode);
            return sourceNode;
        }
        return createWidgetForDatatype();
    },

    createWidgetForDatatype:function(){
        var type = this.dataType.substr(0, 1).toUpperCase() + this.dataType.substr(1);
        var widget = eval("create"+ type + "Widget()");

        if(widget != null){
            return widget;
        }else{
            return createDefaultWidget();
        }
    },

    createDefaultWidget:function(){console.error("could not create widget for :" + this.controlType);},

    createDateTimeWidget:function(){return null;},
    createTimeWidget:function(){return null;},
    createDateWidget:function(){return null;},
    createGYearMonthWidget:function(){return null;},
    createGYearWidget:function(){return null;},
    createGYearMonthDayWidget:function(){return null;},
    createGDayWidget:function(){return null;},
    createGMonthWidget:function(){return null;},
    createStringWidget:function(){return null;},
    createBooleanWidget:function(){return null;},
    createBase64BinaryWidget:function(){return null;},
    createHexBinaryWidget:function(){return null;},
    createFloatWidget:function(){return null;},
    createDecimalWidget:function(){return null;},
    createDoubleWidget:function(){return null;},
    createAnyURIWidget:function(){return null;},
    createQNameWidget:function(){return null;},
    createNormalizedStringWidget:function(){return null;},
    createTokenWidget:function(){return null;},
    createLanguageWidget:function(){return null;},
    createNameWidget:function(){return null;},
    createNCNameWidget:function(){return null;},
    createIDWidget:function(){return null;},
    createIDREFWidget:function(){return null;},
    createNMTOKENWidget:function(){return null;},
    createNMTOKENSWidget:function(){return null;},
    createIntegerWidget:function(){return null;},
    createNonPositiveIntegerWidget:function(){return null;},
    createNegativeIntegerWidget:function(){return null;},
    createLongWidget:function(){return null;},
    createIntWidget:function(){return null;},
    createShortWidget:function(){return null;},
    createByteWidget:function(){return null;},
    createNonNegativeIntegerWidget:function(){return null;},
    createUnsignedLongWidget:function(){return null;},
    createUnsignedIntWidget:function(){return null;},
    createUnsignedShortWidget:function(){return null;},
    createUnsignedByteWidget:function(){return null;},
    createPositiveIntegerWidget:function(){return null;}

});


