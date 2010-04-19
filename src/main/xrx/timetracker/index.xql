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
	<link rel="stylesheet" type="text/css" href="/exist/rest/db/betterform/apps/timetracker/resources/timetracker.css"/>

    <script type="text/javascript">
    	<!--
		dojo.require("dijit.dijit"); // optimize: load dijit layer
		dojo.require("dijit.Declaration");
		dojo.require("dijit.Toolbar");
		dojo.require("dijit.ToolbarSeparator");

		dojo.require("dijit.form.Button");
		dojo.require("dijit.form.DropDownButton");
		dojo.require("dijit.form.ComboButton");
		dojo.require("dijit.form.ToggleButton");
		dojo.require("dijit.ColorPalette");
		dojo.require("dijit.TooltipDialog");
		dojo.require("dijit.form.TextBox");
		dojo.require("dijit.Menu");




		var xfReadySubscribers;
        dojo.require("dojox.highlight");
        dojo.require("dojox.highlight.languages.xml");
        //dojo.require("dojox.highlight.languages._static");
        //dojo.require("dojox.highlight.languages._dynamic");
        //dojo.require("dojox.highlight.languages._www");
        dojo.addOnLoad(function() {
            dojo.query("code").forEach(dojox.highlight.init);
        });

        dojo.require("dijit.form.Button");
        dojo.require("dijit.TitlePane");
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

        function addToDocument(id) {
            var model = dojo.query(".xfModel", dojo.doc)[0];
            dijit.byId(dojo.attr(model, "id")).getInstanceAsString(id,
                    function(data) {
                        dojo.byId("instanceData").innerHTML = data;
                    });
        }

		function embed(targetTrigger){
			var targetMount = dojo.byId("embedTarget");
			if(xfReadySubscribers != undefined) {
				dojo.unsubscribe(xfReadySubscribers);
				xfReadySubscribers = null;
			}

            xfReadySubscribers = dojo.subscribe("/xf/ready", function(data) {
                dojo.style(targetMount, "opacity", 0);
                dojo.fadeIn({
                    node: targetMount,
                    duration:1000
                }).play();
            });
            dojo.fadeOut({
                node: targetMount,
                duration:200,
                onBegin: function() {
                    fluxProcessor.dispatchEvent(targetTrigger);
                }
            }).play();
            dijit.byId("addTaskDialog").show();
		}

        -->
    </script>


</head>
<body id="demo">
<div class="page">
    <div style="display:block">
        <xf:model id="model-1">
            <xf:instance>
                <data xmlns="">
                </data>
            </xf:instance>


            <xf:instance id="i-task" xmlns="">
				<data/>
            </xf:instance>

			<xf:instance id="i-query-tasks">
				<data xmlns="">
					<projects/>
					<worker/>
					<start/>
					<end/>
					<billable/>
				</data>
			</xf:instance>

			<xf:bind nodeset="instance('i-query-tasks')">
				<xf:bind nodeset="start"     type="xf:date" />
				<xf:bind nodeset="end"       type="xf:date" />
				<xf:bind nodeset="billable " type="xf:boolean" />
			</xf:bind>

			<xf:instance id="i-project"
                     src="/exist/rest/db/betterform/apps/timetracker/data/project.xml" />

        	<xf:instance id="i-worker"
                     src="/exist/rest/db/betterform/apps/timetracker/data/worker.xml" />


        <xf:submission id="s-get-tasks"
                       ref="instance('i-query-tasks')"
                       resource="/exist/rest/db/betterform/apps/timetracker/search/search-org.xql"
                       replace="instance"
                       instance="i-tasks"
                       method="form-data-post"
                       validate="false">
            <xf:action ev:event="xforms-submit-done">
                <xf:message level="ephemeral">Received data from eXist DB</xf:message>
            </xf:action>
            <xf:message ev:event="xforms-submit-error">Failure received tasks froms eXist XML DB</xf:message>
        </xf:submission>

        </xf:model>

		<xf:trigger id="contactsDemoTrigger">
            <xf:label>Overview</xf:label>
            <xf:toggle case="c-demoArea"/>
            <xf:load show="embed" targetid="embedTarget">
                <xf:resource value="'/exist/rest/db/betterform/apps/timetracker/views/list-items.xql#xforms'"/>
            </xf:load>
        </xf:trigger>
        <xf:trigger id="addTask">
            <xf:label>add Task</xf:label>
            <xf:toggle case="c-demoArea"/>
            <xf:load show="embed" targetid="embedTarget">
                <xf:resource value="'/exist/rest/db/betterform/apps/timetracker/edit/edit-item.xql#xforms'"/>
            </xf:load>
        </xf:trigger>
    </div>

    <div id="header">
        <!-- <div class="pageMarginBox" dojoType="dijit.layout.ContentPane">
            <div id="logoBar">
                <a href="index.html" class="link" id="linkLogo"><img id="logo" src="images/logo.png" alt="betterFORM TimeTracker"/></a>
                <div id="topnav">
					<a href="edit/edit-item.xql?mode=new">new task</a><span class="menuDevider"> | </span>
                    <a href="search/search-form.html">search</a>
                </div>
            </div>
        </div>
        -->
    </div>

    <div id="content">
        <img id="shadowTop" src="/exist/resources/images/shad_top.jpg" alt=""/>
        <div class="pageMarginBox">
            <div class="contentBody">
        <div id="toolbar1" dojoType="dijit.Toolbar">

					<div id="addBtn" dojoType="dijit.form.Button" showLabel="true" onclick="embed('addTask');">
						<span>New Task</span>
					</div>
				</div>

                <!-- ######################### Content here ################################## -->
                <!-- ######################### Content here ################################## -->
                <!-- ######################### Content here ################################## -->
                <!-- ######################### Content here ################################## -->
                <!-- ######################### Content here ################################## -->

                <xf:switch>
                    <xf:case id="c-overview" selected="true">
                    </xf:case>
                    <xf:case id="c-demoArea">
						<table>
							<tr>
								<td align="left" style="border-right: 1px solid black;border-bottom: 1px solid black;width:90px">Project</td>
								<td align="left"
									style="border-right: 1px solid black;border-bottom: 1px solid black;width: 70px;padding-left:5px">Worker</td>
								<td align="left"
									style="border-right: 1px solid black;border-bottom: 1px solid black;width: 50px; padding-left: 5px;">Start / End</td>
								<td align="left" style="width:90px;padding-left: 5px;">Billable</td>
							</tr>
							<tr>
								<td style="border-right: 1px solid black;width:90px" valign="top">
									<xf:select id="projectToDisplay" ref="instance('i-query-tasks')/projects" appearance="full">
										<xf:label/>
										<xf:itemset nodeset="instance('i-project')/project">
											<xf:label ref="."/>
											<xf:value ref="."/>
										</xf:itemset>
									</xf:select>
								</td>
								<td style="border-right: 1px solid black; width: 60px;padding-left:5px" valign="top">
									<xf:select id="personToDisplay" ref="instance('i-query-tasks')/worker" appearance="full">
										<xf:label/>
										<xf:itemset nodeset="instance('i-worker')/worker">
											<xf:label ref="."/>
											<xf:value ref="."/>
										</xf:itemset>
									</xf:select>
								</td>
								<td style="border-right: 1px solid black; width: 50px; padding-left: 5px;" valign="top">
									<xf:input ref="instance('i-query-tasks')/start">
										<xf:label/>
									</xf:input>
									<br/>
									<xf:input ref="instance('i-query-tasks')/end">
										<xf:label/>
									</xf:input>
								</td>
								<td align="left" style="width:90px;padding-left: 5px;" valign="top">
									<xf:input ref="instance('i-query-tasks')/billable">
										<xf:label/>
									</xf:input>
								</td>
							</tr>
							<tr>
								<td>
									<xf:trigger>
										<xf:label>Search</xf:label>
										<xf:send submission="s-get-tasks"/>
									</xf:trigger>
								</td>
							</tr>
						</table>
						<div id="demoWrapper">
							<div id="embedTarget">
								<!--<div onclick="runDemo();" class="backgroundImage"></div>-->
							</div>
						</div>
                    </xf:case>
                </xf:switch>




                <!-- ######################### Content end ################################## -->
                <!-- ######################### Content end ################################## -->
                <!-- ######################### Content end ################################## -->
                <!-- ######################### Content end ################################## -->
                <!-- ######################### Content end ################################## -->
            </div>
        </div>
    </div>

<!--
    <div id="footer">
        <img id="shadowBottom" src="images/shad_bottom.jpg" alt=""/>

        <div class="pageMarginBox">
        </div>
    </div>
    -->
</div>
</body>
</html>
