xquery version "1.0";

import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";
declare option exist:serialize "method=xhtml media-type=text/xml";


declare function local:timestamp() as xs:string{
      let $timestamp := request:get-parameter("timestamp", "")
      let $path2resource := concat("/exist/rest/db/betterform/apps/timetracker/data/task?_query=/*/task",encode-for-uri('['), "created='" ,$timestamp,"'",encode-for-uri(']'))
      return $path2resource
};

declare function local:mode() as xs:string{
    let $mode := request:get-parameter("mode", "")

    return $mode
};



(: request:set-attribute("betterform.filter.parseResponseBody", "true"), :)
request:set-attribute("betterform.filter.parseResponseBody", "true"),
<html   xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xf="http://www.w3.org/2002/xforms"
        xmlns:exist="http://exist.sourceforge.net/NS/exist"
        xmlns:ev="http://www.w3.org/2001/xml-events">
   <head>
      <title>Edit Task</title>
       <link rel="stylesheet" type="text/css" href="/exist/resources/styles/bf.css"/>
       <link rel="stylesheet" type="text/css" href="/exist/resources/styles/demo.css"/>
       <link rel="stylesheet" type="text/css"
             href="/exist/rest/db/betterform/apps/timetracker/resources/timetracker.css"/>
       <link rel="stylesheet" type="text/css"
             href="/exist/rest/db/betterform/apps/timetracker/resources/InlineRoundBordersAlert.css"/>
       
    </head>
    <body class="tundra InlineRoundBordersAlert">
    	<div id="xforms" style="height:400px;">
        <div style="display:none">
            <xf:model>
                <xf:instance id="i-task" src="/exist/rest/db/betterform/apps/timetracker/data/task.xml"/>

              <xf:bind nodeset="task">
                  <xf:bind nodeset="date" type="xf:date" required="true()" />
                  <xf:bind nodeset="project" required="true()" />
                  <xf:bind nodeset="duration/@hours" type="integer" constraint=". != 0 or ../@minutes != 0"/>
                  <xf:bind nodeset="duration/@minutes" type="integer" constraint=". != 0 or ../@hours != 0"/>
                  <xf:bind nodeset="who" required="true()"/>
                  <xf:bind nodeset="what" required="true()"/>
                  <xf:bind nodeset="billable" type="xf:boolean"/>
                  <xf:bind nodeset="created" required="true()"/>
              </xf:bind>
			 

              <xf:submission id="s-get-task"
                             method="get"
                             resource="{local:timestamp()}"
                             replace="instance"
                             serialization="none">
                             <!--
                             	 <xf:resource value="concat('/exist/rest/db/betterform/apps/timetracker/data/task?_query=/data/task', encode-for-uri('['),'created=', '{local:timestamp()}',encode-for-uri(']') )"/>
							 -->
             </xf:submission>

             <xf:instance id="i-project"     src="/exist/rest/db/betterform/apps/timetracker/data/project.xml"/>
             <xf:instance id="i-worker"  	 src="/exist/rest/db/betterform/apps/timetracker/data/worker.xml"/>
             <xf:instance id="i-controller"  src="/exist/rest/db/betterform/apps/timetracker/data/controller.xml"/>


        <xf:submission id="s-add"
                       method="put"
                       replace="none"
                       validate="false">
		    <xf:resource value="concat('/exist/rest/db/betterform/apps/timetracker/data/task/', instance('i-task')/task/created, '.xml')"/>

            <xf:header>
                <xf:name>username</xf:name>
                <xf:value>admin</xf:value>
            </xf:header>
            <xf:header>
                <xf:name>password</xf:name>
                <xf:value>Atai42!</xf:value>
            </xf:header>
            <xf:header>
                <xf:name>realm</xf:name>
                <xf:value>exist</xf:value>
            </xf:header>

            <xf:action ev:event="xforms-submit" if="'{local:mode()}' = 'new'">
                <xf:message level="ephemeral">Creating timestamp</xf:message>
                <xf:setvalue ref="instance('i-task')/task/created" value="now()" />
                <xf:recalculate/>
				<xf:setvalue ref="instance('i-task')/task/created" value="concat(
					year-from-dateTime(.),
					substring(.,6,2),
					substring(.,9,2),
					'-',
					substring(.,12,2),
					substring(.,15,2),
					substring(.,18,2)
					)" />
            </xf:action>

            <xf:action ev:event="xforms-submit-done">
                <xf:message level="ephemeral">Data stored</xf:message>
				<xf:send submission="s-clean" if="'{local:mode()}' = 'new'"/>
                <script type="text/javascript">
                    dijit.byId("taskDialog").hide();
                </script>
			</xf:action>

            <xf:action ev:event="xforms-submit-error" if="instance('i-controller')/error/@hasError='true'">
                <xf:setvalue ref="instance('i-controller')/error/@hasError" value="'true'"/>
                <xf:setvalue ref="instance('i-controller')/error" value="event('response-reason-phrase')"/>
            </xf:action>

            <xf:action ev:event="xforms-submit-error" if="instance('i-controller')/error/@hasError='false'">
                <xf:message>The form has not been filled in correctly</xf:message>
            </xf:action>
        </xf:submission>

        <xf:submission id="s-clean"
                       ref="instance('i-task')"
                       resource="/exist/rest/db/betterform/apps/timetracker/data/task.xml" 
                       method="get"
                       replace="instance"
                       instance="i-task">
			   <xf:toggle ev:event="xforms-submit-done" case="default"/>
		</xf:submission>



		<xf:action ev:event="xforms-ready" >
			<xf:send submission="s-get-task" if="'{local:mode()}' = 'edit'"/>
		</xf:action>

        </xf:model>
    </div>

        <xf:group ref="task">

            <xf:group id="task-table" appearance="bf:verticalTable">
                <xf:input id="date" ref="date">
                    <xf:label>Date</xf:label>
                    <xf:alert>a valid Date is required</xf:alert>
                    <xf:hint>pick the date to report</xf:hint>
                </xf:input>

                <xf:select1 id="project" ref="project" appearance="minimal">
                    <xf:label>Project</xf:label>
					<xf:alert>a project must be selected</xf:alert>
                    <xf:hint>select the project</xf:hint>
                    <xf:itemset nodeset="instance('i-project')/project">
                        <xf:label ref="."/>
                        <xf:value ref="."/>
                    </xf:itemset>
                </xf:select1>

                <xf:group appearance="bf:horizontalColumn">
                    <xf:label>Hours/Minutes</xf:label>
                    <xf:select1 ref="duration/@hours">
                        <xf:label/>
                        <xf:item>
                            <xf:label>0</xf:label>
                            <xf:value>0</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>1</xf:label>
                            <xf:value>1</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>2</xf:label>
                            <xf:value>2</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>3</xf:label>
                            <xf:value>3</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>4</xf:label>
                            <xf:value>4</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>5</xf:label>
                            <xf:value>5</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>6</xf:label>
                            <xf:value>6</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>7</xf:label>
                            <xf:value>7</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>8</xf:label>
                            <xf:value>8</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>9</xf:label>
                            <xf:value>9</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>10</xf:label>
                            <xf:value>10</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>11</xf:label>
                            <xf:value>11</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>12</xf:label>
                            <xf:value>12</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>13</xf:label>
                            <xf:value>13</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>14</xf:label>
                            <xf:value>14</xf:value>
                        </xf:item>
                    </xf:select1>

                    <xf:select1 ref="duration/@minutes" appearance="mininmal">
                        <xf:label/>
                        <xf:alert>minutes are missing</xf:alert>
                        <xf:item>
                            <xf:label>0</xf:label>
                            <xf:value>0</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>15</xf:label>
                            <xf:value>15</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>30</xf:label>
                            <xf:value>30</xf:value>
                        </xf:item>
                        <xf:item>
                            <xf:label>45</xf:label>
                            <xf:value>45</xf:value>
                        </xf:item>
                    </xf:select1>
                </xf:group>

                <xf:select ref="who" appearance="minimal">
                    <xf:label>Who</xf:label>
                    <xf:alert>Who value is missing</xf:alert>
                    <xf:hint>who has worked on the task?</xf:hint>
                    <xf:itemset nodeset="instance('i-worker')/worker">
                        <xf:label ref="."/>
                        <xf:value ref="."/>
                    </xf:itemset>
                </xf:select>

                <xf:select ref="what" appearance="minimal">
                    <xf:label>What</xf:label>
                    <xf:alert>What value is missing</xf:alert>
                    <xf:hint>what has been done?</xf:hint>
                    <xf:item>
                        <xf:label>Bug Fix</xf:label>
                        <xf:value>bugfix</xf:value>
                    </xf:item>
                    <xf:item>
                        <xf:label>Feature</xf:label>
                        <xf:value>feature</xf:value>
                    </xf:item>
                    <xf:item>
                        <xf:label>Support</xf:label>
                        <xf:value>support</xf:value>
                    </xf:item>
                </xf:select>

                <xf:select1 ref="status">
                    <xf:label>Status</xf:label>
                    <xf:hint>please select the status for this task</xf:hint>
                    <xf:alert>Status value is missing</xf:alert>
                    <xf:item>
                        <xf:label/>
                        <xf:value/>
                    </xf:item>
                    <xf:item>
                        <xf:label>in progress</xf:label>
                        <xf:value>inprogress</xf:value>
                    </xf:item>
                    <xf:item>
                        <xf:label>completed</xf:label>
                        <xf:value>completed</xf:value>
                    </xf:item>
                </xf:select1>

                <xf:textarea ref="note" mediatype="dojo">
                    <xf:label>Note</xf:label>
                    <xf:alert>Note value is invalid</xf:alert>
                    <xf:hint>more details about the task</xf:hint>
                </xf:textarea>

                <xf:input ref="billable">
                    <xf:label>Billable</xf:label>
                    <xf:alert>Billable value is missing</xf:alert>
                    <xf:hint>can this billed to Customer?</xf:hint>
                </xf:input>

			<xf:trigger style="padding-right:0;">
				<xf:label>Save</xf:label>
                <xf:action>
                    <xf:send submission="s-add"/>
                </xf:action>
			</xf:trigger>
            </xf:group>
			<xf:output mediatype="text/html" ref="instance('i-controller')/error" id="errorReport"/>

        </xf:group>
        </div>
    </body>
</html>
