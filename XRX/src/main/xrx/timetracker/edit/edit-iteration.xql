xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";

import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

declare option exist:serialize "method=xhtml media-type=application/xhtml+html";

declare function local:getClients() {
    for $clients in collection('betterform/apps/timetracker/data/client/')/client
    order by fn:upper-case($clients)
    return
        <client id="{$clients/@id}">
            {$clients/name/text()}
        </client>
};

declare function local:getIteration() as xs:string{
      let $id := request:get-parameter("id", "")
      let $contextPath := request:get-context-path()
      let $path2resource := concat("{$contextPath}/rest/db/betterform/apps/timetracker/data?_query=/*/iteration",encode-for-uri('['), "@id='" ,$id,"'",encode-for-uri(']'))
      return $path2resource
};

declare function local:mode() as xs:string{
    let $mode := request:get-parameter("mode", "")
    return $mode
};


let $contextPath := request:get-context-path()
return
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
   <head>
      <title>Edit Iteration</title>
    </head>
    <body>
    	<div id="xforms" style="height:360px;">
        <div style="display:none">
            <xf:model>
            <xf:instance id="i-iteration" src="{$contextPath}/rest/db/betterform/apps/timetracker/data/template/client.xml"/>
            
            <xf:instance id="i-temp">
                <data xmlns="">
                    <value/>
                </data>
            </xf:instance>
            
            <xf:instance id="i-client">
                <data xmlns="">
                    {local:getClients()}
                </data>
            </xf:instance>
            
            <xf:submission id="s-add"
                       method="put"
                       replace="none">
		    <xf:resource value="'{$contextPath}/rest/db/betterform/apps/timetracker/data/iteration.xml'"/>

            <xf:header>
                <xf:name>username</xf:name>
                <xf:value>admin</xf:value>
            </xf:header>
            <xf:header>
                <xf:name>password</xf:name>
                <xf:value></xf:value>
            </xf:header>
            <xf:header>
                <xf:name>realm</xf:name>
                <xf:value>exist</xf:value>
            </xf:header>

			<xf:message level="ephemeral" ev:event="xforms-submit-done">Saved changes</xf:message>
			<xf:message level="modal" ev:event="xforms-submit-error">Failure saving changes</xf:message>
        </xf:submission>


		  <xf:submission id="s-get-project"
						 method="get"
						 resource="{local:getIteration()}"
						 validate="false"
						 replace="instance">
		 </xf:submission>

  		<xf:action ev:event="xforms-ready" >
  			<xf:send submission="s-get-iteration" if="'{local:mode()}' = 'edit'"/>
  		</xf:action>
        
        </xf:model>
    </div>

        <xf:group id="project-table" appearance="bf:verticalTable" >
                <xf:select1 id="customer" ref="instance('i-temp')/value" appearance="minimal">
                    <xf:label>Customer</xf:label>
					<xf:alert>a customer must be selected</xf:alert>
                    <xf:hint>select the customer</xf:hint>
                    <xf:itemset nodeset="instance('i-client')/client">
                        <xf:label ref="."/>
                        <xf:value ref="./@id"/>
                    </xf:itemset>
                </xf:select1>
                
                <xf:select1 id="project" ref="instance()/iteration/@project" appearance="minimal">
                    <xf:label>Project</xf:label>
					<xf:alert>a project must be selected</xf:alert>
                    <xf:hint>select the project</xf:hint>
                    <xf:itemset nodeset="instance('i-project')/project">
                        <xf:label ref="."/>
                        <xf:value ref="./@id"/>
                    </xf:itemset>
                </xf:select1>
<!--                
                <xf:select1 id="project" ref="project" appearance="minimal">
                    <xf:label>Project</xf:label>
					<xf:alert>a project must be selected</xf:alert>
                    <xf:hint>select the project</xf:hint>
                    <xf:itemset nodeset="instance('i-project')/project">
                        <xf:label ref="."/>
                        <xf:value ref="."/>
                    </xf:itemset>
                </xf:select1>
                
                <xf:input id="iteration" ref="project">
                    <xf:label>Name: </xf:label>
                    <xf:alert>Username is not valid</xf:alert>
                </xf:input>
-->
				<xf:trigger style="padding-right:0;">
					<xf:label>Save</xf:label>
					<xf:toggle case="doIt"/>
				</xf:trigger>
		</xf:group>

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
