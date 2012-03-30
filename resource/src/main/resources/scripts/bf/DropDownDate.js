define(["dojo/_base/declare",
        "dijit/_WidgetBase",
        "dijit/_TemplatedMixin",
        "dojo/text!./DropDownDate.html",
        "dojo/i18n!bf/nls/DropDownDate",
        "dijit/form/ComboBox",
        "dojo/dom-class",
        "dojo/dom-attr",
        "dojo/_base/connect",
        "dojo/_base/array"],
    function(declare, WidgetBase, TemplatedMixin, template,resources, ComboBox,domClass, domAttr,connect,array){
        return declare([WidgetBase, TemplatedMixin], {

            templateString: template,
            widgetsInTemplate:true,
            minimumYear: 1900,
            maximumYear: 2100,
            years:'',
            months:'',
            days:'',
            appearance:null,
            monthsArray:null,

            postMixInProperties:function() {
                this.monthsArray = new Array(resources.january, resources.february, resources.march, resources.april,
                                             resources.may, resources.june, resources.july, resources.august,
                                             resources.september, resources.october, resources.november,
                                             resources.december);
                var rangeStart;
                var rangeEnd;
                var now = new Date().getFullYear();
                if (this.appearance.indexOf("=") != -1) {
                    rangeStart = this.appearance.substring(this.appearance.indexOf("=") + 1);

                    if (rangeStart.indexOf(":") != -1) {
                        rangeEnd = rangeStart.substring(rangeStart.indexOf(":") + 1);
                        rangeStart = rangeStart.substring(0, rangeStart.indexOf(":"));
                    }

                    if (rangeStart.indexOf("-") != -1) {
                        rangeStart = now - parseInt(rangeStart.substring(rangeStart.indexOf("-") + 1), "10");
                    } else if (rangeStart.indexOf("+") != -1) {
                        rangeStart = now + parseInt(rangeStart.substring(rangeStart.indexOf("+") + 1), "10");
                    }

                    if (rangeEnd != undefined) {
                        if (rangeEnd.indexOf("+") != -1) {
                            rangeEnd = now + parseInt(rangeEnd.substring(rangeEnd.indexOf("+") + 1), "10");
                        } else if (rangeEnd.indexOf("-") != -1) {
                            rangeEnd = now - parseInt(rangeEnd.substring(rangeEnd.indexOf("-") + 1), "10");
                        }
                    } else {
                        rangeEnd = now;
                    }

                } else {
                    rangeEnd = now;
                    rangeStart = rangeEnd - 10;
                }

                rangeStart = parseInt(rangeStart, "10");
                rangeEnd = parseInt(rangeEnd, "10");

                // console.debug("DropDownDate: Range Start: ", rangeStart, " End: ", rangeEnd);
                this.templateString = "<div class='xfDropDownDateControl xfValue'><input type='hidden' data-dojo-attach-point='bfValue' value=''/><" +
                    "span class='xfDropDownDate'><select size='1'  data-dojo-attach-point='daysFacet' class='xfDropDownDateDays'><option></option><" +
                    "option>01</option><option>02</option><option>03</option><option>04</option><option>05</option><option>06</option><option>07</option><option>08</option><" +
                    "option>09</option><option>10</option><option>11</option><option>12</option><option>13</option><option>14</option><option>15</option><option>16</option><" +
                    "option>17</option><option>18</option><option>19</option><option>20</option><option>21</option><option>22</option><option>23</option><option>24</option><" +
                    "option>25</option><option>26</option><option>27</option><option>28</option><option>29</option><option>30</option><option>31</option></select><" +
                    "select size='1'  data-dojo-attach-point='monthsFacet' class='xfDropDownDateMonths'><option value=''></option><" +
                    "option value='01'>"+resources.january+"</option><option value='02'>"+resources.february+"</option><option value='03'>"+resources.march+"<" +
                    "/option><option value='04'>"+resources.april+"</option><option value='05'>"+resources.may+"</option><option value='06'>"+resources.june+"<" +
                    "/option><option value='07'>"+resources.july+"</option><option value='08'>"+resources.august+"</option><option value='09'>"+resources.september+"<" +
                    "/option><option value='10'>"+resources.october+"</option><option value='11'>"+resources.november+"</option><option value='12'>"+resources.december+"<" +
                    "/option></select><select size='1'  data-dojo-attach-point='yearsFacet' class='xfDropDownDateYears'>";

                var end = "</select></span></div>";


                this.templateString = this.templateString + "<option></option>";
                if (rangeStart > rangeEnd) {
                    for (var i = rangeStart; i >= rangeEnd; i--) {
                        this.templateString = this.templateString + "<option>" + i + "</option>";
                    }
                } else {
                    for (var z = rangeStart; z <= rangeEnd; z++) {
                        this.templateString = this.templateString + "<option>" + z + "</option>";
                    }
                }
                this.templateString = this.templateString + end;

                this.minimumYear = rangeStart;
                this.maximumYear = rangeEnd;

                if (rangeStart >= rangeEnd) {
                    this.minimumYear = rangeEnd;
                    this.maximumYear = rangeStart;
                }
                this.inherited(arguments);
                // console.debug("DropDownData.postMixInProperties after");
                this.incremental = false;
            },

            postCreate:function() {
                // console.debug("DropDownDate.postCreate: before this.inherited");
                this.inherited(arguments);

                this.daysDijit = new ComboBox({},this.daysFacet);
                domClass.add(this.daysDijit.domNode,"xfDropDownDateDays");
                this.monthDijit = new ComboBox({},this.monthsFacet);
                domClass.add(this.monthDijit.domNode,"xfDropDownDateMonths");
                this.yearDijit = new ComboBox({},this.yearsFacet);
                domClass.add(this.yearDijit.domNode,"xfDropDownDateYears");

                // console.debug("DropDownData.postMixInProperties this.daysDijit:",this.daysDijit);

                domAttr.set(this.bfValue, "value", this.value);
                this.applyValues(this.value);

                // console.debug("postCreate: this.daysDijit:",this.daysDijit);
                connect.connect(this.daysDijit, "onChange", this, "onDaysChanged");
                connect.connect(this.monthDijit, "onChange", this, "onMonthsChanged");
                connect.connect(this.yearDijit, "onChange", this, "onYearsChanged");
            },

            applyValues:function(value) {
                // console.debug("DropDownDate.applyValues value:",value);
                if(this.daysDijit == undefined) {
                    // console.debug("DropDownDate.applyValues this.daysDijit==undefined: return");
                    return;
                }

                if (value != undefined) {

                    var splittedValue = value.split("-");
                    if (splittedValue.length != 3) {
                        console.warn("DropDownDate.applyValues: value: ", value ," can't be applied");
                        return;
                    }
                    // console.debug("DropDownDate.applyValues this.timeContainer:", splittedValue);

                    this.years = splittedValue[0];
                    this.months = splittedValue[1];
                    this.days = splittedValue[2];

                    // console.debug("DropDownDate.applyValues this.daysDijit:", this.daysDijit);
                    this.daysDijit.set('value', this.days);
                    this.monthDijit.set('value', this.monthsArray[parseInt(this.months, "10") - 1]);
                    //this.monthDijit.set('displayValue', this.monthsArray[parseInt(months)-1]);
                    this.yearDijit.set('value', this.years);
                }
            },

            onDaysChanged:function(evt) {
                // console.debug("DropDownDate.onDaysChanged.");
                var selectedItem = this.daysDijit.get("item");
                if (selectedItem != undefined && selectedItem.value != "") {
                    this.days = selectedItem.value;
                } else {
                    this.days = this.daysDijit.get("value");
                }
                this.set("value", this.getControlValue());
            },

            onMonthsChanged:function(evt) {
                // console.debug("DropDownDate.onMonthsChanged.");
                var selectedItem = this.monthDijit.get("item");
                var value;
                if (selectedItem != undefined) {
                    // console.debug("DropDownDate.onMonthsChanged() selectedItem defined: |", selectedItem.value, "|");
                    value = parseInt(selectedItem.value, "10");
                } else {
                    var month = this.monthDijit.get("value");
                    if (isNaN(month)) {
                        value = parseInt(array.indexOf(this.monthsArray,month) + 1, "10");
                    } else {
                        value = parseInt(month, "10");
                    }
                }

                // console.debug("DropDownDate.onMonthsChanged() current month value:", value);
                if (value < 10) {
                    // console.debug("DropDownDate.onMonthsChanged() adding leading zero to month.");
                    value = "0" + value;
                    // console.debug("DropDownDate.onMonthsChanged() modified month value:", value);
                }

                this.months = value;
                this.set("value", this.getControlValue());
            },

            onYearsChanged:function(evt) {
                // console.debug("DropDownDate.onYearsChanged oldYear: " , this.years);
                var selectedItem = this.yearDijit.get('item');
                var year;

                if (selectedItem != undefined && selectedItem.value != "") {
                    year = selectedItem.value;
                } else {
                    // console.debug("DropDownDate.onYearsChanged this.yearDijit.get('value'): " , this.yearDijit.get("value"));
                    year = this.yearDijit.get("value");
                }

                // TODO: LW: Implement invalid state for range, problem is that the value is not invalid for the processor
                // TODO: LW: workaround: defined a constraint according to the bf:appearance=XYZ value
        /*
                if (year > this.maximumYear) {
                    this.years = this.maximumYear;
                    this.yearDijit.set("displayedValue",this.years);
                } else if (year < this.minimumYear) {
                    this.years = this.minimumYear;
                    this.yearDijit.set("displayedValue",this.years);
                } else {
                    //In range!
                    this.years = year;
                }
        */
                if (year < this.minimumYear || year > this.maximumYear){
                    console.warn("DropDownDate.onYearsChanged: selected year is invalid, " + year + " is not >= " + this.minimumYear + " and <= " + this.maximumYear);
                }
                this.years = year;
                this.set("value", this.getControlValue());
                // console.debug("DropDownDate.onYearsChanged newYear: " , this.years);
            },

            // TODO: Lars: verify if commented functions can be removed
        /*
            _handleSetControlValue:function(value) {
                console.debug("DropDownDate._handleSetControlValue value",value);
                this.applyValues(value);
            },
        */
            getControlValue:function() {
                this.value = this.years + "-" + this.months + "-" + this.days;
                // console.debug("bf.ui.input.DropDownDate.getControlValue currentDate: ", this.value);
                domAttr.set(this.bfValue, "value", this.value);
                return this.value;
            },


            set:function(attrName, value){
                // console.debug("DropDownDate.set: attrName: "+ attrName+ "  value",value);
                if(attrName == "value"){
                    this.applyValues(value);
                }else if(attrName == "readOnly"){
                    this.daysDijit.set("readOnly", value);
                    this.monthDijit.set("readOnly", value);
                    this.yearDijit.set("readOnly", value);
                }
            },

            get:function(attrName) {
                // console.debug("DropDownDate.get: attrName",attrName);
                if(attrName == "value"){
                    return this.getControlValue();
                }
            }
    });
});