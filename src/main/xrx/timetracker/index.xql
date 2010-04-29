xquery version "1.0";
        declare option exist:serialize "method=xhtml media-type=text/xml";

        request:set-attribute("betterform.filter.parseResponseBody", "true"),
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
    <head>
        <title>betterFORM Demo XForms: Address, Registration, FeatureExplorer</title>
        <link rel="stylesheet" type="text/css" href="/exist/resources/styles/bf.css"/>
        <link rel="stylesheet" type="text/css" href="/exist/resources/styles/demo.css"/>
        <link rel="stylesheet" type="text/css"
              href="/exist/rest/db/betterform/apps/timetracker/resources/timetracker.css"/>
        <link rel="stylesheet" type="text/css"
              href="/exist/rest/db/betterform/apps/timetracker/resources/InlineRoundBordersAlert.css"/>

        <script type="text/javascript">
            <!--
            dojo.require("dijit.dijit"); 
            dojo.require("dijit.Declaration");
            dojo.require("dijit.Toolbar");
            dojo.require("dijit.ToolbarSeparator");
            dojo.require("dijit.Dialog");


            dojo.require("dijit.form.Button");
            dojo.require("dijit.form.DropDownButton");
            dojo.require("dijit.form.ComboButton");
            dojo.require("dijit.form.ToggleButton");
            dojo.require("dijit.ColorPalette");
            dojo.require("dijit.TooltipDialog");
            dojo.require("dijit.form.TextBox");
            dojo.require("dijit.Menu");




            var xfReadySubscribers;

            dojo.require("dijit.form.Button");
            dojo.require('dijit.layout.ContentPane');
            dojo.require("betterform.ui.select.OptGroup");
            dojo.require("betterform.ui.select1.RadioItemset");
            dojo.require("betterform.ui.select.CheckBoxItemset");
            dojo.require("betterform.ui.util");
            dojo.require("betterform.ui.container.Switch");
            dojo.require("betterform.ui.container.Repeat");
            dojo.require("betterform.ui.container.RepeatItem");
            dojo.require("betterform.ui.container.TabSwitch");
            dojo.require("betterform.ui.container.Group");
            dojo.require("dojox.grid.DataGrid");
            dojo.require("dojox.data.XmlStore");

            var layoutTasks = [
					[{
						field: "date",
						name: "Date",
						width: 10,
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "project",
						name: "Project",
						width: 10,
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "hours",
						name: "Hours",
						width: 10,
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "minutes",
						name: "Minutes",
						width: 10,
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "who",
						name: "Who",
						width: 10,
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "what",
						name: "Description",
						width: 10,
						formatter: function(item) {
							return item.toString();
						}
					},
/*
					{
						field: "note",
						name: "Note",
						width: 10,
						formatter: function(item) {
							return item.toString();
						}
					},
*/
					{
						field: "status",
						name: "Status",
						width: 10,
						formatter: function(item) {
							return item.toString();
						}
					}
            ]];

            function openTask(e){
				var item = e.grid.getItem(e.rowIndex);
				alert(taskStore.getValue(item,"created"));
			}

            function addToDocument(id) {
                var model = dojo.query(".xfModel", dojo.doc)[0];
                dijit.byId(dojo.attr(model, "id")).getInstanceAsString(id,
                        function(data) {
                            dojo.byId("instanceData").innerHTML = data;
                        });
            }

            function embed(targetTrigger,targetMount){

                if(targetMount == "embedDialog"){
                    dijit.byId("taskDialog").show();
                }

                var targetMount =  dojo.byId(targetMount);
                if(xfReadySubscribers != undefined) {
                    dojo.unsubscribe(xfReadySubscribers);
                    xfReadySubscribers = null;
                }

                xfReadySubscribers = dojo.subscribe("/xf/ready", function(data) {
                    dojo.style(targetMount, "opacity", 0);
                    dojo.fadeIn({
                        node: targetMount,
                        duration:100
                    }).play();
                });
                dojo.fadeOut({
                    node: targetMount,
                    duration:100,
                    onBegin: function() {
                        fluxProcessor.dispatchEvent(targetTrigger);
                    }
                }).play();
            }

            -->
        </script>


    </head>
    <body id="timetracker" class="tundra InlineRoundBordersAlert">
        <div class="page">

            <!-- ***** hidden triggers ***** -->
            <!-- ***** hidden triggers ***** -->
            <!-- ***** hidden triggers ***** -->
            <div style="display:none;">
                <xf:model id="model-1">
                    <xf:instance>
                        <data xmlns="">
                        </data>
                    </xf:instance>
                </xf:model>

                <xf:trigger id="overviewTrigger">
                    <xf:label>Overview</xf:label>
                    <xf:toggle case="c-embedArea"/>
                    <xf:load show="embed" targetid="embedInline">
                        <xf:resource value="'/exist/rest/db/betterform/apps/timetracker/views/list-items.xhtml#pagecontent'"/>
                    </xf:load>
                </xf:trigger>

                <xf:trigger id="addTask">
                    <xf:label>add Task</xf:label>
                    <xf:load show="embed" targetid="embedDialog">
                        <xf:resource
                                value="'/exist/rest/db/betterform/apps/timetracker/edit/edit-item.xql#xforms?mode=new'"/>
                    </xf:load>
                </xf:trigger>
            </div>


            <!-- ######################### Content here ################################## -->
            <!-- ######################### Content here ################################## -->
            <!-- ######################### Content here ################################## -->
            <!-- ######################### Content here ################################## -->
            <!-- ######################### Content here ################################## -->
            <div id="content">
                <img id="shadowTop" src="/exist/resources/images/shad_top.jpg" alt=""/>

                <div id="toolbar" dojoType="dijit.Toolbar">
                    <div id="overviewBtn" dojoType="dijit.form.Button" showLabel="true"
                         onclick="embed('overviewTrigger','embedInline');">
                        <span>Overview</span>
                    </div>
                    <div id="addBtn" dojoType="dijit.form.Button" showLabel="true"
                         onclick="embed('addTask','embedDialog');">
                        <span>New Task</span>
                    </div>
                    <div id="searchBtn" dojoType="dijit.form.Button" showLabel="true" onclick="alert('todo');">
                        <span>Search</span>
                    </div>
                    <div id="settingsBtn" dojoType="dijit.form.Button" showLabel="true" onclick="alert('todo');">
                        <span>Settings</span>
                    </div>
                </div>

                <div id="taskDialog" dojotype="dijit.Dialog" style="width:610px;height:460px;">
                    <div id="embedDialog"></div>
                </div>

                <xf:switch>
                    <xf:case id="c-overview" selected="true">
                    </xf:case>
                    <xf:case id="c-embedArea">
                        <!-- @@@@@@@@@@@@@@@@@@@@@  MOUNTPOINT @@@@@@@@@@@@@@@@@@ -->
                        <div id="embedInline"></div>
                    </xf:case>
                </xf:switch>


                <!-- ######################### Content end ################################## -->
                <!-- ######################### Content end ################################## -->
                <!-- ######################### Content end ################################## -->
                <!-- ######################### Content end ################################## -->
                <!-- ######################### Content end ################################## -->
            </div>
        </div>
        <!-- ######################### Content end ################################## -->
        <!-- ######################### Content end ################################## -->
        <!-- ######################### Content end ################################## -->
        <!-- ######################### Content end ################################## -->
        <!-- ######################### Content end ################################## -->

    </body>
</html>
