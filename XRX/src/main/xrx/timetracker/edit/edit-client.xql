xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";

import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

declare option exist:serialize "method=xhtml media-type=application/xhtml+html";

declare function local:getClient() as xs:string{
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
                    <xf:instance id="i-client" xmlns="" src="{$contextPath}/rest/db/betterform/apps/timetracker/data/template/client.xml"/>
                    
                    <xf:bind nodeset="name" required="true()"/>                    
                    <xf:bind nodeset="@shortName" required="true()"/>
                    
                    <xf:instance id="i-temp" xmlns="">
                        <data>
                            <project id=""/>
                            <iteration id=""/>
                        </data>
                    </xf:instance>
                    
                                        
                    

                    <xf:submission id="s-add"
                                   method="put"
                                   replace="none">
                        <xf:resource value="concat('{$contextPath}/rest/db/betterform/apps/timetracker/data/client/',instance()/@shortName,'.xml')"/>
                        
                        <xf:header>
                            <xf:name>username</xf:name>
                            <xf:value>admin</xf:value>
                        </xf:header>
                        
                        <xf:header>
                            <xf:name>password</xf:name>
                            <xf:value></xf:value>
                        </xf:header>
                    
                        <xf:action ev:event="xforms-submit" if="'{local:mode()}' = 'new'">
                            <xf:message level="ephemeral">Creating id</xf:message>
                            <xf:setvalue ref="instance('i-client')/@id" value="'{util:uuid()}'" />
                        </xf:action>
                        
                        <xf:message level="ephemeral" ev:event="xforms-submit-done">Saved changes</xf:message>
        			    <xf:message level="modal" ev:event="xforms-submit-error">Failure saving changes</xf:message>
                    </xf:submission>
    
                    <xf:submission id="s-get-client"
                                   method="get"
        						   resource="{local:getClient()}"
        						   validate="false"
        						   replace="instance">
        		    </xf:submission>
                    
                    
          		    <xf:action ev:event="xforms-ready" >
          			   <xf:message if="'{local:mode()}' = 'edit'">EDIT</xf:message>
          			   <xf:send submission="s-get-client" if="'{local:mode()}' = 'edit'"/>
          		    </xf:action>
                
                </xf:model>
            </div>
            <xf:group id="client-table" appearance="bf:verticalTable" >
                <xf:input ref="name">
                    <xf:label>Client name:</xf:label>
                </xf:input>
                <xf:input ref="instance()/@shortName">
                    <xf:label>Short name:</xf:label>
                </xf:input>
        
                <xf:select1 id="projects" ref="instance('i-temp')/project/@id" appearance="minimal">
                    <xf:label>Project</xf:label>
					<xf:alert>a client must be selected</xf:alert>
                    <xf:hint>select the client</xf:hint>
                     <xf:itemset nodeset="projects/project">
                        <xf:label ref="name"/>
                        <xf:value ref="./@id"/>
                    </xf:itemset>
                </xf:select1>
                
                <xf:trigger>
                    <xf:label>Add a project</xf:label>
                    <xf:insert context="instance()/projects/project" nodeset="." origin="instance()/projects/project" position="last"/>
                </xf:trigger>
                
                <xf:input ref="instance()/projects/project[position()=last()]/name">
                    <xf:label>Name:</xf:label>
                </xf:input>
                
                <xf:select1 id="iterations" ref="instance('i-temp')/iteration/@id" appearance="minimal">
                    <xf:label>Project</xf:label>
					<xf:alert>a client must be selected</xf:alert>
                    <xf:hint>select the client</xf:hint>
                     <xf:itemset nodeset="projects/project/iterations/iteration">
                        <xf:label ref="."/>
                        <xf:value ref="./@id"/>
                    </xf:itemset>
                </xf:select1>
                
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
