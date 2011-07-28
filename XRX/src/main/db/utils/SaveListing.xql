xquery version "1.0";

declare namespace request="http://exist-db.org/xquery/request";
declare namespace exist = "http://exist.sourceforge.net/NS/exist";

declare option exist:serialize "method=xhtml media-type=application/xhtml+xml";

let $contextPath := request:get-context-path()
return
<html xmlns="http://www.w3.org/1999/xhtml"
xmlns:xf="http://www.w3.org/2002/xforms"
xmlns:ev="http://www.w3.org/2001/xml-events"
xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:bf="http://betterform.sourceforge.net/xforms">
		<head>
			<title>Save as ...</title>
			<script type="text/javascript">
				<![CDATA[
				
				function load(url) {
				dojo.xhrGet({
				url: url,
				load: function(newContent) {
				dojo.byId("mountpoint").innerHTML = newContent;
				},
				error: function() {}
				});
				}
				
				function viewContent(node, url) {
				setFilePath(url);
				load(url);
				}
				
				function viewRoot(node,url){
				viewContent(node,url);
				}
				
				function viewParent(node,url){
				viewContent(node,url);
				}
				
				function setFilePath(url) {
				var filePath = url.substring(url.indexOf('?path=')+6);
				filePath = filePath.substring(0,filePath.indexOf('fragment')-1);
				fluxProcessor.setControlValue('filePath', filePath);
				}
					]]>
				</script>
			</head>
			<body>
				<div id="xforms">
					<div style="display:none">
						<xf:model id="model-1">
							<xf:instance id="i-save-as">
								<data xmlns="">
									<filename/>
									<filePath>betterform</filePath>
								</data>
							</xf:instance>
							
							<xf:instance id="i-login" src="{$contextPath}/rest/db/betterform/utils/Login.xql" xmlns=""/>
							
							<xf:submission id="s-login"
							method="get"
							replace="instance"
							instance="i-login"
							serialization="none"
							validate="false"
								relevant="false">
									<xf:resource value="concat('{$contextPath}/rest/db/betterform/utils/Login.xql?username=', instance('i-login')/username, '&amp;password=', instance('i-login')/password)"/>
									
									<xf:action ev:event="xforms-submit-done" if="boolean-from-string(instance('i-login')/login)">
										<xf:message level="ephemeral">Logged in.</xf:message>
										<xf:toggle case="c-save"/>
									</xf:action>
									<xf:message ev:event="xforms-submit-done" level="modal" if="not(boolean-from-string(instance('i-login')/login))">Login failed.</xf:message>
									<xf:message ev:event="xforms-submit-error" level="modal">Login failed.</xf:message>
								</xf:submission>
								
								<xf:submission id="s-embedHTML"
								replace="embedHTML"
								targetid="embedHTML"
									method="get">
										<xf:resource value="concat(bf:appContext('contextroot'), '/rest/db/betterform/utils/FileListHTML.xql')"/>
									</xf:submission>
									
									<xf:action ev:event="xforms-ready">
										<xf:send submission="s-embedHTML"/>
										<xf:toggle case="c-login" if="not(boolean-from-string(instance('i-login')/login))"/>
										<xf:toggle case="c-save" if="boolean-from-string(instance('i-login')/login)"/>
									</xf:action>
								</xf:model>
								
								<xf:input id="filePath" ref="instance('i-save-as')/filePath"/>
							</div>
							<xf:switch>
								<xf:case id="c-default" selected="true"/>
								<xf:case id="c-login" selected="false">
									<xf:group appearance="bf:verticalTable">
										<div id="notice">
											In order to save the XForm Please login.
										</div>
										<xf:input ref="instance('i-login')/username">
											<xf:label>Username:</xf:label>
										</xf:input>
										<xf:secret ref="instance('i-login')/password">
											<xf:label>Password:</xf:label>
										</xf:secret>
										<xf:trigger>
											<xf:label>Login</xf:label>
											<xf:send submission="s-login"/>
										</xf:trigger>
									</xf:group>
								</xf:case>
								<xf:case id="c-save" selected="false">
									<div id="notice">
										Please select a collection and enter a filename to store the file.
									</div>
									<xf:group>
										<div id="embedHTML"/>
									</xf:group>
									<!--
									<xf:group apperance="full">
									    <xf:label>DEBUG:</xf:label>
           								<xf:output ref="instance('i-save-as')/filePath">
           									<xf:label>Selected collection : </xf:label>
           								</xf:output>
           								<xf:output ref="instance('i-login')/login">
           									<xf:label>login : </xf:label>
           								</xf:output>
           								<xf:output ref="instance('i-login')/username">
           									<xf:label>username : </xf:label>
           								</xf:output>
           								<xf:output ref="instance('i-login')/password">
           									<xf:label>username : </xf:label>
           								</xf:output>
       								</xf:group>
       								-->
									<xf:input ref="instance('i-save-as')/filename">
										<xf:label>Enter a filename : </xf:label>
									</xf:input>
									<xf:trigger>
										<xf:label>Save</xf:label>
										<xf:action ev:event="DOMActivate">
											<xf:setvariable name="username" select="instance('i-login')/username"/>
											<xf:setvariable name="password" select="instance('i-login')/password"/>
											<xf:setvariable name="filename" select="instance('i-save-as')/filename"/>
											<xf:setvariable name="filePath" select="concat(bf:appContext('contextroot'), '/rest/db/',instance('i-save-as')/filePath)"/>
											<xf:setvariable name="mode" select="'save-as'"/>
											<script type="text/javascript">
											    serializeTree();
											</script>
										</xf:action>
									</xf:trigger>
								</xf:case>
							</xf:switch>
						</div>
					</body>
				</html>
				