xquery version "1.0";
declare option exist:serialize "method=xhtml media-type=text/xml";

request:set-attribute("betterform.filter.parseResponseBody", "true"),
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
    <head>
        <title>betterFORM Demo XForms: Address, Registration, FeatureExplorer</title>

        <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.4/dojox/grid/resources/Grid.css"/>
        <link rel="stylesheet" type="text/css" href="http://ajax.googleapis.com/ajax/libs/dojo/1.4/dojox/grid/resources/tundraGrid.css"/>
        <link rel="stylesheet" type="text/css" href="/exist/resources/styles/bf.css"/>
        <link rel="stylesheet" type="text/css" href="/exist/resources/styles/demo.css"/>
        <link rel="stylesheet" type="text/css"
              href="/exist/rest/db/betterform/apps/timetracker/resources/InlineRoundBordersAlert.css"/>
        <link rel="stylesheet" type="text/css"
              href="/exist/rest/db/betterform/apps/timetracker/resources/timetracker.css"/>

       
        <script type="text/javascript">
            <!--
            dojo.require("dojo.parser");
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
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "hours",
						name: "Hours",
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "minutes",
						name: "Minutes",
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "who",
						name: "Who",
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "what",
						name: "Description",
						formatter: function(item) {
							return item.toString();
						}
					},
					{
						field: "status",
						name: "Status",
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
                dojo.parser.parse(targetMount);
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

            // -->
        </script>


    </head>
    <body id="timetracker" class="tundra">

        <div class="page">

            <!-- ***** hidden triggers ***** -->
            <!-- ***** hidden triggers ***** -->
            <!-- ***** hidden triggers ***** -->
            <div style="display:none;">
                <xf:model id="model-1">
                    <xf:instance>
                        <data xmlns="">
                            <project/>
                            <from/>
                            <to/>
                            <howmany>10</howmany>
                        </data>
                    </xf:instance>
                    <xf:instance id="i-query">
                        <data xmlns="">
                            <_query>//task</_query>
                            <_howmany/>
                            <_xsl>/db/betterform/apps/timetracker/views/list-items.xsl</_xsl>
                        </data>
                    </xf:instance>
                    <xf:submission id="s-query-tasks-rest"
                                    resource="/exist/rest/db/betterform/apps/timetracker/data/task"
                                    method="get"
                                    replace="embedHTML"
                                    targetid="embedInline"
                                    ref="instance('i-query')"
                                    validate="false">
                        <xf:action ev:event="xforms-submit-done">
                            <xf:refresh/>
                        </xf:action>
                    </xf:submission>
                    <xf:submission id="s-query-tasks"
                                    resource="/exist/rest/db/betterform/apps/timetracker/views/list-items.xql"
                                    method="get"
                                    replace="embedHTML"
                                    targetid="embedInline"
                                    ref="instance()"
                                    validate="false">
                        <xf:action ev:event="xforms-submit-done">
                            <xf:toggle case="c-embedArea"/>
                        </xf:action>
                    </xf:submission>
                </xf:model>

                <xf:trigger id="addTask">
                    <xf:label>new</xf:label>
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
                <xf:trigger id="overviewTrigger">
                    <xf:label>Overview</xf:label>
                    <xf:send submission="s-query-tasks"/>

<!--                    <xf:toggle case="c-embedArea"/>
                    <xf:load show="embed" targetid="embedInline">
                        <xf:resource value="concat('/exist/rest/db/betterform/apps/timetracker/data/task?_query=//task&amp;_howmany=',instance()/howmany,'&amp;_xsl=/db/betterform/apps/timetracker/views/list-items.xsl')"/>
                    </xf:load> -->
                </xf:trigger>

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
                <img id="shadowTop" src="/exist/rest/db/betterform/apps/timetracker/resources/images/shad_top.jpg" alt=""/>

                <div id="taskDialog" dojotype="dijit.Dialog" style="width:610px;height:460px;">
                    <div id="embedDialog"></div>
                </div>

                <xf:switch>
                    <xf:case id="c-overview">
                    </xf:case>
                    <xf:case id="c-embedArea"  selected="true">
                        <!-- @@@@@@@@@@@@@@@@@@@@@  MOUNTPOINT @@@@@@@@@@@@@@@@@@ -->

                        <div id="embedInline"></div>

                        <xf:input ref="howmany">
                            <xf:label/>
                             <xf:load show="embed" targetid="embedInline" ev:event="xforms-value-changed">
                                 <xf:resource value="concat('/exist/rest/db/betterform/apps/timetracker/data/task?_query=//task&amp;_howmany=',instance()/howmany,'&amp;_xsl=/db/betterform/apps/timetracker/views/list-items.xsl')"/>
                             </xf:load>

                        </xf:input>
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
