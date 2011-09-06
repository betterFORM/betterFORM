xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";

import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

declare option exist:serialize "method=xhtml media-type=text/xml";


declare function local:getClients() {
    for $clients in collection('betterform/apps/timetracker/data/client/')/client
    order by fn:upper-case($clients)
    return
        <client id="'{$clients/@id}'" shortName="{$clients/@shortName}">
            {$clients/name/text()}
        </client>
};

declare function local:getInitialClient() as xs:string{
      let $id := request:get-parameter("id", "")
      let $contextPath := request:get-context-path()
      let $path2resource := concat('{$contextPath}/rest/db/betterform/apps/timetracker/data/client?_query=/client',encode-for-uri('['), '@id=' ,$id ,encode-for-uri(']'))
      return $path2resource
};

(: TODO: create util-module :)
declare function local:mode() as xs:string{
    let $mode := request:get-parameter("mode", "new")
    return $mode
};

let $contextPath := request:get-context-path()
return
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
   <head>
      <title>Edit Client</title>
    </head>
    <body>
    	<div id="xforms" style="height:360px;">
            <div style="display:none">
                <xf:model>
                    <xf:instance id="i-client-template" xmlns="" src="{$contextPath}/rest/db/betterform/apps/timetracker/data/template/client.xml"/>

                    <xf:instance id="i-client" xmlns="" src="{$contextPath}/rest/db/betterform/apps/timetracker/data/template/client.xml"/>

                    <xf:bind nodeset="name" required="true()"/>
                    <xf:bind nodeset="@shortName" required="true()"/>

                    <xf:instance id="i-temp" xmlns="">
                        <data>
                            <client id=""/>
                            <project id=""/>
                            <iteration id=""/>
                            <trigger>
                                <saveClientNew/>
                                <saveProjectNew/>
                                <saveProjectSelected/>
                            </trigger>
                        </data>
                    </xf:instance>
                    <xf:bind nodeset="instance('i-temp')/trigger">
                        <xf:bind nodeset="saveClientNew"      readonly="string-length(instance('i-client-template')/name) = 0 or string-length(instance('i-client-template')/@shortName) = 0"/>
                        <xf:bind nodeset="saveProjectNew"      readonly="string-length(instance('i-client-template')/projects/project/name) = 0"/>
                        <xf:bind nodeset="saveProjectSelected" readonly="string-length(instance('i-temp')/project/@id) = 0 or string-length(instance('i-client-template')/projects/project[1]/iterations/iteration[1]) = 0"/>
                    </xf:bind>

                   <xf:instance id="i-clients">
                        <data xmlns="">
                            {local:getClients()}
                        </data>
                   </xf:instance>


                    <xf:submission id="s-add"
                                   ref="instance('i-client-template')"
                                   validate="true"
                                   method="put"
                                   replace="none">
                        <xf:resource value="concat('{$contextPath}/rest/db/betterform/apps/timetracker/data/client/',instance()/@shortName,'.xml')"/>

                        <xf:header>
                            <xf:name>username</xf:name>
                            <xf:value>betterFORM</xf:value>
                        </xf:header>

                        <xf:header>
                            <xf:name>password</xf:name>
                            <xf:value>Tha0xeiC8a</xf:value>
                        </xf:header>

                        <xf:action ev:event="xforms-submit" >
                            <!-- <xf:message level="ephemeral">Creating id</xf:message> -->
                            <xf:setvalue ref="instance('i-client-template')/@id" value="'{util:uuid()}'" />
                            <xf:setvalue ref="instance('i-client-template')/projects/project[position()=last()]/@id" value="'{util:uuid()}'" />
                            <xf:setvalue ref="instance('i-client-template')/projects/project[position()=last()]/iterations/iteration[position()=last()]/@id" value="'{util:uuid()}'" />
                        </xf:action>

        			    <xf:message level="modal" ev:event="xforms-submit-error">Failure saving changes. Please check required fields (project name and shortname)</xf:message>

                       <xf:action ev:event="xforms-submit-done">
                            <xf:send submission="s-clean"/>
                       </xf:action>

                    </xf:submission>


                    <xf:submission id="s-add-existing-client"
                                   ref="instance('i-client')/client"
                                   validate="true"
                                   method="put"
                                   replace="none">
                        <xf:resource value="concat('{$contextPath}/rest/db/betterform/apps/timetracker/data/client/',instance('i-client')/client/@shortName,'.xml')"/>

                        <xf:header>
                            <xf:name>username</xf:name>
                            <xf:value>betterFORM</xf:value>
                        </xf:header>

                        <xf:header>
                            <xf:name>password</xf:name>
                            <xf:value>Tha0xeiC8a</xf:value>
                        </xf:header>
                        <xf:action ev:event="xforms-submit" if="string-length(instance('i-client')/client/projects/project[position()=last()]/@id) = 0">
                            <xf:setvalue ref="instance('i-client')/client/projects/project[position()=last()]/@id" value="'{util:uuid()}'"/>
                            <xf:setvalue ref="instance('i-client')/client/projects/project[position()=last()]/iterations/iteration/@id" value="'{util:uuid()}'" />
                        </xf:action>

                        <xf:action ev:event="xforms-submit" if="string-length(instance('i-client')/client/projects/project[position()=last()]/@id) != 0">
                            <xf:setvalue ref="instance('i-client')/client/projects/project[@id = instance('i-temp')/project/@id]/iterations/iteration[position() = last()]/@id" value="'{util:uuid()}'" />
                        </xf:action>

        			    <xf:message level="modal" ev:event="xforms-submit-error">Failure saving changes. Please check required fields (project name and shortname)</xf:message>

                       <xf:action ev:event="xforms-submit-done">
                            <xf:send submission="s-clean"/>
                       </xf:action>


                    </xf:submission>



                    <xf:submission id="s-clean"
                                   method="get"
                                   replace="instance"
                                   instance="i-client"
                                   resource="echo:test"
                                   validate="false">

                                   <xf:action ev:event="xforms-submit-done">
                                        <xf:message level="ephemeral">Data stored</xf:message>
                                        <script type="text/javascript">
                                            dijit.byId("taskDialog").hide();
                                            dojo.publish("/task/refresh");
                                        </script>
                                    </xf:action>
                    </xf:submission>
                    <xf:submission id="s-get-initial-client"
                                   method="get"
        						   resource="{local:getInitialClient()}"
        						   validate="false"
        						   replace="instance"
        						   instance="i-client">
        		    </xf:submission>

                    <xf:submission id="s-get-selected-client"
                                   ref="instance('i-client')"
                                   method="get"
                                   replace="instance"
                                   instance="i-client"
                                   validate="false">
                                   <xf:resource value="concat('{$contextPath}/rest/db/betterform/apps/timetracker/data/client?_query=/client',encode-for-uri('['), '@id=' ,instance('i-temp')/client/@id, encode-for-uri(']'))"/>
                                   <xf:toggle ev:event="xforms-submit-done" case="select-or-create-project"/>
                                   <xf:message ev:event="xforms-submit-error">Error loading client data</xf:message>
                    </xf:submission>



                    <xf:action ev:event="xforms-value-changed" ev:observer="clientSelect" ev:target="clients">
                        <xf:send submission="s-get-selected-client" if="string-length(normalize-space(instance('i-temp')/client/@id)) &gt; 0"/>
                        <xf:toggle if="string-length(normalize-space(instance('i-temp')/client/@id)) = 0" case="project-empty"/>
                    </xf:action>

                </xf:model>
            </div>
            <xf:group id="createClient" appearance="full" >
                <xf:label/>
                <xf:switch>
                    <xf:case id="client-select-or-new" selected="true">
                        <xf:trigger>
                            <xf:label>Select Client</xf:label>
                            <xf:toggle case="client-select"/>
                        </xf:trigger>
                        <div> or create </div>
                        <xf:trigger>
                            <xf:label>New Client</xf:label>
                            <xf:toggle case="client-new"/>
                            <xf:toggle case="project-empty"/>
                        </xf:trigger>
                    </xf:case>

                    <xf:case id="client-new" selected="true">
                        <xf:trigger>
                            <xf:label>Back</xf:label>
                            <xf:toggle case="client-select-or-new"/>
                        </xf:trigger>

                        <xf:input ref="name">
                            <xf:label>Client:</xf:label>
                        </xf:input>
                        <xf:input ref="instance()/@shortName">
                            <xf:label>Client short name:</xf:label>
                        </xf:input>
                        <xf:input ref="instance()/projects/project[1]/name">
                            <xf:label>Project:</xf:label>
                        </xf:input>
                        <xf:input ref="instance()/projects/project[1]/iterations/iteration[1]">
                            <xf:label>Iteration:</xf:label>
                        </xf:input>                        

                        <xf:trigger style="padding-right:0;" ref="instance('i-temp')/trigger/saveClientNew">
                            <xf:label>Save</xf:label>
                            <xf:message if="exists(instance('i-clients')/client[.=instance('i-client-template')/name])">Name already exists</xf:message>
                            <xf:message if="exists(instance('i-clients')/client[@shortName=instance('i-client-template')/@shortName])">Short name already exists</xf:message>
                            <xf:send submission="s-add" if="not(exists(instance('i-clients')/client[.=instance('i-client-template')/name])) and not(exists(instance('i-clients')/client[@shortName=instance('i-client-template')/@shortName]))" />
                        </xf:trigger>

                    </xf:case>
                    
                    <xf:case id="client-select">
                        <xf:trigger>
                            <xf:label>Back</xf:label>
                            <xf:toggle case="client-select-or-new"/>
                        </xf:trigger>
                        <xf:group id="clientSelect" appearance="full">
                            <xf:select1 id="clients" ref="instance('i-temp')/client/@id" appearance="minimal">
                                <xf:label>Select Client</xf:label>
                                <xf:alert>a client must be selected</xf:alert>
                                <xf:hint>select the client</xf:hint>
                                 <xf:itemset nodeset="instance('i-clients')/client">
                                    <xf:label ref="."/>
                                    <xf:value ref="./@id"/>
                                </xf:itemset>
                            </xf:select1>
                        </xf:group>
                        <xf:switch>
                            <xf:case id="project-empty">
                            </xf:case>
                            <xf:case id="select-or-create-project">
                                <xf:trigger>
                                    <xf:label>Select Project</xf:label>
                                    <xf:toggle case="project-select"/>
                                </xf:trigger>
                                <div> or create </div>
                                <xf:trigger>
                                    <xf:label>New Project</xf:label>
                                    <xf:toggle case="project-new"/>
                                </xf:trigger>

                            </xf:case>
                            <xf:case id="project-new">
                                    <xf:trigger>
                                        <xf:label>Back</xf:label>
                                        <xf:toggle case="select-or-create-project"/>
                                    </xf:trigger>

                                    <xf:input ref="instance()/projects/project[1]/name">
                                        <xf:label>Project:</xf:label>
                                    </xf:input>
                                    <xf:input ref="instance()/projects/project[1]/iterations/iteration[1]">
                                        <xf:label>Iteration:</xf:label>
                                    </xf:input>

                                <xf:trigger style="padding-right:0;" ref="instance('i-temp')/trigger/saveProjectNew">
                                    <xf:label>Save</xf:label>
                                    <xf:insert  nodeset="instance('i-client')/client/projects/project"
                                                origin="instance('i-client-template')/projects/project"
                                                at="last()"
                                                position="after"/>

                                    <xf:send submission="s-add-existing-client"/>
                                </xf:trigger>
                            </xf:case>
                            <xf:case id="project-select">
                                <xf:group id="projectSelect" appearance="full">
                                    <xf:trigger>
                                        <xf:label>Back</xf:label>
                                        <xf:toggle case="select-or-create-project"/>
                                    </xf:trigger>
                                    <xf:select1 id="projects" ref="instance('i-temp')/project/@id" appearance="minimal">
                                        <xf:label>Select Project:</xf:label>
                                        <xf:alert>a client must be selected</xf:alert>
                                        <xf:hint>select the client</xf:hint>
                                         <xf:itemset nodeset="instance('i-client')/client/projects/project">
                                            <xf:label ref="name"/>
                                            <xf:value ref="@id"/>
                                        </xf:itemset>
                                    </xf:select1>
                                    <xf:input ref="instance()/projects/project[1]/iterations/iteration[1]">
                                        <xf:label>Iteration Name:</xf:label>
                                    </xf:input>

                                   <xf:trigger style="padding-right:0;" ref="instance('i-temp')/trigger/saveProjectSelected">
                                        <xf:label>Save</xf:label>
                                        <xf:insert  nodeset="instance('i-client')/client/projects/project[@id = instance('i-temp')/project/@id]/iterations/iteration"
                                                    origin="instance('i-client-template')/projects/project/iterations/iteration"
                                                    at="last()"
                                                    position="after"/>
                                        <xf:send submission="s-add-existing-client"/>
                                    </xf:trigger>

                                </xf:group>
                            </xf:case>
                        </xf:switch>

                    </xf:case>
                </xf:switch>
            </xf:group>
<!--
            <xf:group id="debug" appearance="full">
                <xf:output value="string-length(instance('i-client-template')/projects/project/name) = 0">
                    <xf:label>saveProjectNew:</xf:label>
                </xf:output>

                <xf:output value="string-length(instance('i-temp')/project/@id) = 0 or string-length(instance('i-client-template')/projects/project[1]/iterations/iteration[1]) = 0">
                    <xf:label>saveProjectSelected:</xf:label>
                </xf:output>
            </xf:group>
-->
        	<xf:switch>
        		<xf:case id="default" selected="true"/>
        		<xf:case id="doIt" selected="false">
        			<div style="font-weight:bold;">Really add?</div>
        			<xf:trigger>
        				<xf:label>Yes</xf:label>
        				<xf:send submission="s-add"/>
        			</xf:trigger>
        			<xf:trigger>
        				<xf:label>Cancel</xf:label>
        				<xf:toggle case="default"/>
        			</xf:trigger>
        		</xf:case>
        	</xf:switch>
        </div>
    </body>
</html>
