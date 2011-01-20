xquery version "1.0";
declare option exist:serialize "method=xhtml media-type=application/xhtml+html";

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
    <head>
        <title>betterFORM Demo XForms: Address, Registration, FeatureExplorer</title>

        <link rel="stylesheet" type="text/css" href="/betterform/resources/styles/bf.css"/>
        <link rel="stylesheet" type="text/css" href="/betterform/resources/styles/demo.css"/>
        <link rel="stylesheet" type="text/css"
              href="/betterform/rest/db/betterform/apps/timetracker/resources/InlineRoundBordersAlert.css"/>
        <link rel="stylesheet" type="text/css"
              href="/betterform/rest/db/betterform/apps/timetracker/resources/timetracker.css"/>

        <script type="text/javascript">
            <!--
            dojo.require("dojo.parser");
            dojo.require("dijit.dijit");
            dojo.require("dijit.Declaration");
            dojo.require("dijit.Toolbar");
            dojo.require("dijit.ToolbarSeparator");
            dojo.require("dijit.Dialog");
            dojo.require("dijit.TitlePane");
            dojo.require("betterform.ui.container.Group");
            dojo.require('dijit.layout.ContentPane');
            dojo.require("dijit.form.Button");
            dojo.require("dijit.form.CheckBox");


            var xfReadySubscribers;

            function embed(targetTrigger,targetMount){
                console.debug("embed",targetTrigger,targetMount);
                if(targetMount == "embedDialog"){
                    dijit.byId("taskDialog").show();
                }

                var targetMount =  dojo.byId(targetMount);

                fluxProcessor.dispatchEvent(targetTrigger);

                if(xfReadySubscribers != undefined) {
                    dojo.unsubscribe(xfReadySubscribers);
                    xfReadySubscribers = null;
                }

                xfReadySubscribers = dojo.subscribe("/xf/ready", function(data) {
                    dojo.fadeIn({
                        node: targetMount,
                        duration:100
                    }).play();
                });
                dojo.fadeOut({
                    nxquery version "1.0";
(: $Id: admin.xql 10304 2009-10-31 21:24:20Z wolfgang_m $ :)
(:
    Main module of the database administration interface.
:)

declare namespace admin = "http://exist-db.org/xquery/admin-interface";

declare namespace request = "http://exist-db.org/xquery/request";
declare namespace session = "http://exist-db.org/xquery/session";
declare namespace util = "http://exist-db.org/xquery/util";
declare namespace xdb = "http://exist-db.org/xquery/xmldb";

import module namespace status = "http://exist-db.org/xquery/admin-interface/status" at "status.xqm";
import module namespace browse = "http://exist-db.org/xquery/admin-interface/browse" at "browse.xqm";
import module namespace users = "http://exist-db.org/xquery/admin-interface/users" at "users.xqm";
import module namespace xqueries = "http://exist-db.org/xquery/admin-interface/xqueries" at "xqueries.xqm";
import module namespace shut = "http://exist-db.org/xquery/admin-interface/shutdown" at "shutdown.xqm";
import module namespace setup = "http://exist-db.org/xquery/admin-interface/setup" at "setup.xqm";
import module namespace rev="http://exist-db.org/xquery/admin-interface/revisions" at "versions.xqm";
import module namespace backup="http://exist-db.org/xquery/admin-interface/backup" at "backup.xqm";
import module namespace prof="http://exist-db.org/xquery/profiling" at "trace.xqm";
import module namespace grammar="http://exist-db.org/xquery/admin-interface/grammar" at "grammar.xqm";
(: Triggers XFormsFilter so disabled for now
    import module namespace install="http://exist-db.org/xquery/install-tools" at "install.xqm";
:)

declare option exist:serialize "method=xhtml media-type=text/html";

(:
    Display the version, SVN revision and user info in the top right corner
:)
declare function admin:info-header() as element()
{
    <div class="info">
        <ul>
            <li>Version: { util:system-property( "product-version" ) }</li>
            <li>SVN Revision: { util:system-property( "svn-revision" ) }</li>
            <li>Build: {util:system-property("product-build")}</li>
            <li>User: { xdb:get-current-user() }</li>
        </ul>
    </div>
};

(:
    Select the page to show. Every page is defined in its own module
:)
declare function admin:panel() as element()
{
    let $panel := request:get-parameter("panel", "status") return
        if($panel eq "browse") then
        (
            browse:main()
        )
        else if($panel eq "users") then
        (
            users:main()
        )
         else if($panel eq "xqueries") then
        (
            xqueries:main()
        )
        else if($panel eq "shutdown") then
        (
            shut:main()
        )
        else if($panel eq "setup") then
        (
            setup:main()
        )
		else if ($panel eq "revisions") then
		(
			rev:main()
		)
		else if ($panel eq "backup") then
		(
		    backup:main()
		)
	    else if ($panel eq "trace") then
	    (
	        prof:main()
        )
        else if ($panel eq "grammar") then
	    (
	        grammar:main()
        )
        else if ($panel eq "install") then
        (
            install:main()
        )
        else
        (
            status:main()
        )
};

(: Triggers XFormsFilter so disabled for now
declare function admin:panel-header() {
    let $panel := request:get-parameter("panel", "status")
    return install:header()
};
:)
(:~
    Display the login form.
:)
declare function admin:display-login-form() as element()
{
    <div class="panel">
        <div class="panel-head">Login</div>
        <p>This is a protected resource. Only registered database users can log
        in. If you have not set up any users, login as "admin" and leave the
        password field empty. Note that the "guest" user is not permitted access.</p>

        <form action="{session:encode-url(request:get-uri())}" method="post">
            <table class="login" cellpadding="5">
                <tr>
                    <th colspan="2" align="left">Please Login</th>
                </tr>
                <tr>
                    <td align="left">Username:</td>
                    <td><input name="user" type="text" size="20"/></td>
                </tr>
                <tr>
                    <td align="left">Password:</td>
                    <td><input name="pass" type="password" size="20"/></td>
                </tr>
                <tr>
                    <td colspan="2" align="left"><input type="submit"/></td>
                </tr>
            </table>
        </form>
    </div>
};

(: main entry point :)
let $isLoggedIn :=  if(xdb:get-current-user() eq "guest")then
    (
        (: is this a login attempt? :)
        if(request:get-parameter("user", ()) and not(empty(request:get-parameter("pass", ()))))then
        (
            if(request:get-parameter("user", ()) eq "guest")then
            (
                (: prevent the guest user from accessing the admin webapp :)
                false()
            )
            else
            (
                (: try and log the user in :)
                xdb:login( "/db", request:get-parameter("user", ()), request:get-parameter("pass", () ), true() )
            )
        )
        else
        (
            (: prevent the guest user from accessing the admin webapp :)
            false()
        )
    )
    else
    (
        (: if we are already logged in, are we logging out - i.e. set permissions back to guest :)
        if(request:get-parameter("logout",()))then
        (
        	let $null  := xdb:login("/db", "guest", "guest")
            let $inval := session:invalidate()

            return false()
        )
        else
        (
             (: we are already logged in and we are not the guest user :)
            true()
        )
    )
return (
    <?css-conversion no?>,
    <html>
        <head>
            <title>eXist Database Administration</title>
            <link type="text/css" href="admin.css" rel="stylesheet"/>
			<link type="text/css" href="styles/prettify.css" rel="stylesheet"/>
			<link type="text/css" href="../scripts/yui/yui-skin.css" rel="stylesheet"/>
            <link rel="shortcut icon" href="../resources/exist_icon_16x16.ico"/>
			<link rel="icon" href="../resources/exist_icon_16x16.png" type="image/png"/>
			<script type="text/javascript" src="scripts/prettify.js"/>
			<script type="text/javascript" src="../scripts/yui/yui-combined2.7.0.js"></script>
			<script type="text/javascript" src="scripts/admin.js"></script>
(: Triggers XFormsFilter so disabled for now
			{ admin:panel-header() }
:)
        </head>
        <body class="yui-skin-sam">
            <div class="header">
                {admin:info-header()}
                <img src="logo.jpg"/>
            </div>

            <div class="content">
                <div class="guide">
                    <div class="guide-title">Select a Page</div>
                    {
                        let $link := session:encode-url(request:get-uri())
                        return
                            <ul>
                                <li><a href="..">Home</a></li>
                                <li><a href="{$link}?panel=status">System Status</a></li>
                                <li><a href="{$link}?panel=browse">Browse Collections</a></li>
                                <li><a href="{$link}?panel=users">User Management</a></li>
                                <li><a href="{$link}?panel=xqueries">View Running Jobs</a></li>
                                <li><a href="{$link}?panel=setup">Examples Setup</a></li>
                                <li><a href="{$link}?panel=install">Install Tools</a></li>
                                <li><a href="{$link}?panel=backup">Backups</a></li>
                                <li><a href="{$link}?panel=trace">Query Profiling</a></li>
                                <li><a href="{$link}?panel=grammar">Grammar cache</a></li>
                                <li><a href="{$link}?panel=shutdown">Shutdown</a></li>
                                <li><a href="{$link}?logout=yes">Logout</a></li>
                            </ul>
                    }
                    <div class="userinfo">
                        Logged in as: {xdb:get-current-user()}
                    </div>
                </div>
                {
                    if($isLoggedIn)then
                    (
                        admin:panel()
                    )
                    else
                    (
                        admin:display-login-form()
                    )
                }
            </div>
        </body>
    </html>
)
ode: targetMount,
                    duration:100,
                    onBegin: function() {
                        fluxProcessor.dispatchEvent(targetTrigger);
                    }
                }).play();

            }

            var editSubcriber = dojo.subscribe("/task/edit", function(data){
                fluxProcessor.setControlValue("currentTask",data);
                embed('editTask','embedDialog');

            });

            var deleteSubscriber = dojo.subscribe("/task/delete", function(data){
                var check = confirm("Really delete this entry?");
                if (check == true){
                    fluxProcessor.setControlValue("currentTask",data);
                    fluxProcessor.dispatchEvent("deleteTask");
                }
            });

            var refreshSubcriber = dojo.subscribe("/task/refresh", function(){
                fluxProcessor.dispatchEvent("overviewTrigger");
            });

            function selectAll(){
                dojo.query("input",dojo.byId("taskTable")).forEach(
                function (node){
                    dijit.byId(node.id).setChecked(true);
                });
            }

            function selectNone(){
                dojo.query("input",dojo.byId("taskTable")).forEach(
                function (node){
                    dijit.byId(node.id).setChecked(false);
                });
            }

            function passValuesToXForms(){
                var result="";
                dojo.query("input",dojo.byId("taskTable")).forEach(
                function (node){
                    if(dijit.byId(node.id).checked && node.value != undefined){
                        result = result + " " + node.value;
                    }
                });
                fluxProcessor.setControlValue("selectedTaskIds",result);
            }

            // -->
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
                            <from>2000-01-01</from>
                            <to>2000-01-02</to>
                            <project/>
                            <billable/>
                            <billed/>
                        </data>
                    </xf:instance>
                    <xf:bind nodeset="from" type="xf:date"/>
                    <xf:bind nodeset="to" type="xf:date" />

                    <xf:submission id="s-query-tasks"
                                    resource="/betterform/rest/db/betterform/apps/timetracker/views/list-items.xql"
                                    method="get"
                                    replace="embedHTML"
                                    targetid="embedInline"
                                    ref="instance()"
                                    validate="false">
                        <xf:action ev:event="xforms-submit-error">
                            <xf:message>Submission failed</xf:message>
                        </xf:action>
                    </xf:submission>

                    <xf:submission id="s-delete-task"
                                    method="delete"
                                    replace="none"
                                    validate="false">
                        <xf:resource value="concat('/betterform/rest/db/betterform/apps/timetracker/data/task/',instance('i-vars')/currentTask,'.xml')"/>
                        <xf:header>
                            <xf:name>username</xf:name>
                            <xf:value>betterFORM</xf:value>
                        </xf:header>
                        <xf:header>
                            <xf:name>password</xf:name>
                            <xf:value>Tha0xeiC8a</xf:value>
                        </xf:header>
                        <xf:header>
                            <xf:name>realm</xf:name>
                            <xf:value>exist</xf:value>
                        </xf:header>
                        
                        <xf:action ev:event="xforms-submit-done">
                            <script type="text/javascript">
                                fluxProcessor.dispatchEvent("overviewTrigger");
                            </script>
                            <xf:message level="ephemeral">Entry has been removed</xf:message>
                        </xf:action>
                    </xf:submission>

                    <xf:instance id="i-project" src="/betterform/rest/db/betterform/apps/timetracker/data/project.xml" />

                    <xf:instance id="i-vars">
                        <data xmlns="">
                            <default-duration>120</default-duration>
                            <currentTask/>
                            <selectedTasks/>
                        </data>
                    </xf:instance>
                    <xf:bind nodeset="instance('i-vars')/default-duration" type="xf:integer"/>

                    <xf:submission  id="s-update-billed"
                                    ref="instance('i-vars')/selectedTasks"
                                    method="post"
                                    replace="new"
                                    resource="/betterform/rest/db/betterform/apps/timetracker/reports/timeAndEffort.xql">
                                    <xf:message ev:event="xforms-submit">here it comes...</xf:message>
                    </xf:submission>


                    
                    <xf:action ev:event="xforms-ready">
                        <xf:setvalue ref="to" value="substring(local-date(), 1, 10)"/>
                        <xf:recalculate/>
                        <xf:setvalue ref="from" value="days-to-date(number(days-from-date(instance()/to) - instance('i-vars')/default-duration))"/>
                    </xf:action>

                     <!-- ***************************
                    Commented out but might still be useful as reference - shows REST-style access

                    <xf:instance id="i-query">
                        <data xmlns="">
                            <_query>//task</_query>
                            <_howmany/>
                            <_xsl>/db/betterform/apps/timetracker/views/list-items.xsl</_xsl>
                        </data>
                    </xf:instance>

                    <xf:submission id="s-query-tasks-rest"
                                    resource="/betterform/rest/db/betterform/apps/timetracker/data/task"
                                    method="get"
                                    replace="embedHTML"
                                    targetid="embedInline"
                                    ref="instance('i-query')"
                                    validate="false">
                        <xf:action ev:event="xforms-submit-done">
                            <xf:refresh/>
                        </xf:action>
                    </xf:submission>
                    ****************************** -->
                </xf:model>

                <xf:trigger id="overviewTrigger">
                    <xf:label>Overview</xf:label>
                    <xf:send submission="s-query-tasks"/>
                </xf:trigger>

                <xf:trigger id="addTask">
                    <xf:label>new</xf:label>
                    <xf:action>
                        <xf:load show="embed" targetid="embedDialog">
                            <xf:resource
                                    value="'/betterform/rest/db/betterform/apps/timetracker/edit/edit-item.xql#xforms'"/>
                        </xf:load>
                    </xf:action>
                </xf:trigger>

                <xf:trigger id="editTask">
                    <xf:label>new</xf:label>
                    <xf:action>
                        <xf:load show="embed" targetid="embedDialog">
                            <xf:resource
                                    value="concat('/betterform/rest/db/betterform/apps/timetracker/edit/edit-item.xql#xforms?timestamp=',instance('i-vars')/currentTask)"/>
                        </xf:load>
                    </xf:action>
                </xf:trigger>

                <xf:trigger id="deleteTask">
                    <xf:label>delete</xf:label>
                    <xf:send submission="s-delete-task"/>
                </xf:trigger>

                <xf:input id="currentTask" ref="instance('i-vars')/currentTask">
                    <xf:label>This is just a dummy used by JS</xf:label>
                </xf:input>

                <xf:input id="selectedTaskIds" ref="instance('i-vars')/selectedTasks">
                    <xf:label>This is another dummy allowing to pass all selected tasks into an instance</xf:label>
                    <xf:send submission="s-update-billed" ev:event="xforms-value-changed"/>
                </xf:input>
            </div>


            <!-- ######################### Content here ################################## -->
            <!-- ######################### Content here ################################## -->
            <!-- ######################### Content here ################################## -->
            <!-- ######################### Content here ################################## -->
            <!-- ######################### Content here ################################## -->
            <div id="content">
                <div id="header">
                    <a href="http://www.betterform.de"><img src="/betterform/rest/db/betterform/apps/timetracker/resources/images/bf_logo_201x81.png" alt="betterFORM"/></a>
                    <div id="appName">Timetracker</div>
                </div>
                <div id="toolbar" dojoType="dijit.Toolbar">
                    <div id="overviewBtn" dojoType="dijit.form.DropDownButton" showLabel="true"
                         onclick="fluxProcessor.dispatchEvent('overviewTrigger');">
                        <span>Filter</span>
                        <div id="filterPopup" dojoType="dijit.TooltipDialog">
                            <table id="searchBar">
                                <tr>
                                    <td>
                                        <xf:input ref="from" incremental="true">
                                            <xf:label>from</xf:label>
                                            <xf:action ev:event="xforms-value-changed">
                                                <xf:dispatch name="DOMActivate" targetid="overviewTrigger"/>
                                            </xf:action>
                                        </xf:input>
                                    </td>
                                    <td>
                                        <xf:input ref="to" incremental="true">
                                            <xf:label>to</xf:label>
                                            <xf:action ev:event="xforms-value-changed">
                                                <xf:dispatch  name="DOMActivate" targetid="overviewTrigger"/>
                                            </xf:action>
                                        </xf:input>
                                    </td>
                                    <td>
                                        <xf:select1 ref="project" appearance="minimal" incremental="true">
                                            <xf:label>Project</xf:label>
                                            <xf:action ev:event="xforms-value-changed">
                                                <xf:dispatch  name="DOMActivate" targetid="overviewTrigger"/>
                                            </xf:action>
                                            <xf:itemset nodeset="instance('i-project')/*">
                                                <xf:label ref="."/>
                                                <xf:value ref="."/>
                                            </xf:itemset>
                                        </xf:select1>
                                    </td>
                                    <td>
                                        <xf:select1 ref="billable" appearance="minimal" incremental="true">
                                            <xf:label>Billable</xf:label>
                                            <xf:action ev:event="xforms-value-changed">
                                                <xf:dispatch  name="DOMActivate" targetid="overviewTrigger"/>
                                            </xf:action>
                                            <xf:item>
                                                <xf:label>yes</xf:label>
                                                <xf:value>true</xf:value>
                                            </xf:item>
                                            <xf:item>
                                                <xf:label>no</xf:label>
                                                <xf:value>false</xf:value>
                                            </xf:item>
                                        </xf:select1>
                                    </td>
                                    <td>
                                        <xf:select1 ref="billed" appearance="minimal" incremental="true">
                                            <xf:label>Billed</xf:label>
                                            <xf:action ev:event="xforms-value-changed">
                                                <xf:dispatch  name="DOMActivate" targetid="overviewTrigger"/>
                                            </xf:action>
                                            <xf:item>
                                                <xf:label>not billed yet</xf:label>
                                                <xf:value>false</xf:value>
                                            </xf:item>
                                            <xf:item>
                                                <xf:label>already billed</xf:label>
                                                <xf:value>true</xf:value>
                                            </xf:item>
                                        </xf:select1>
                                    </td>
                                    <td>
                                        <xf:trigger id="closeFilter">
                                            <xf:label/>
                                            <script type="text/javascript">
                                                dijit.byId("filterPopup").onCancel();
                                                fluxProcessor.dispatchEvent("overviewTrigger");
                                            </script>
                                        </xf:trigger>
                                    </td>
                                </tr>
                            </table>
                        </div>
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
                    <div dojotype="dijit.form.Button" showLabel="true" onclick="dijit.byId('aboutDialog').show();">
                        <span>About</span>
                    </div>
                </div>

                <img id="shadowTop" src="/betterform/rest/db/betterform/apps/timetracker/resources/images/shad_top.jpg" alt=""/>

                <div id="fromTo">
                    <xf:output value="concat(from,' - ',to)" id="durationLabel">
                        <xf:label/>
                    </xf:output>
                </div>

                <div id="taskDialog" dojotype="dijit.Dialog" style="width:610px;height:480px;" title="Task" autofocus="false">
                    <div id="embedDialog"></div>
                </div>

                <div id="embedInline"></div>

                <div id="aboutDialog" dojotype="dijit.Dialog" href="about.html" title="About" style="width:500px;height:500px;"></div>

<!--
                <xf:output ref="instance('i-vars')/selectedTasks">
                    <xf:label>all selected tasks</xf:label>
                </xf:output>
-->

                <div id="report"></div>

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
