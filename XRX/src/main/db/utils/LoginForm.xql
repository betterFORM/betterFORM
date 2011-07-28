xquery version "1.0";

declare namespace request="http://exist-db.org/xquery/request";
declare namespace exist = "http://exist.sourceforge.net/NS/exist";

declare option exist:serialize "method=xhtml media-type=application/xhtml+xml";

let $contextPath := request:get-context-path()
let $filename    := request:get-parameter("filename", '')
return
<html xmlns="http://www.w3.org/1999/xhtml"
	xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:ev="http://www.w3.org/2001/xml-events"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:xsd="http://www.w3.org/2001/XMLSchema"
	xmlns:bf="http://betterform.sourceforge.net/xforms">
	<head>
	<script type="text/javascript" src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.js"/>
    <script type="text/javascript" src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.cookie.js"/>
    <script type="text/javascript" src="../../../bfResources/scripts/jstree_pre1.0_stable/_lib/jquery.hotkeys.js"/>
    <script type="text/javascript" src="../../../bfResources/scripts/jstree_pre1.0_stable/jquery.jstree.js"/>
    <script type="text/javascript" src="../../../bfResources/scripts/betterform/xfEditorUtil.js"/>
    <script type="text/javascript" src="../../../bfResources/scripts/betterform/betterform-XFormsEditor.js"/>
    <script type="text/javascript" src="../../../bfResources/scripts/betterform/editor/addMenu.js"/>
    <script type="text/javascript">
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.Menu");
        dojo.require("dijit.MenuBar");
        dojo.require("dijit.PopupMenuBarItem");
        dojo.require("dijit.MenuItem");
        dojo.require("dijit.PopupMenuItem");
        dojo.require("dijit.layout.TabContainer");
        dojo.require("dijit.form.TextBox");
        dojo.require("dijit.form.Select");
        dojo.require("dijit.form.FilteringSelect");
        dojo.require("dojo.data.ItemFileReadStore");
        dojo.require("dojox.layout.FloatingPane");
        dojo.require("dijit.TitlePane");
        
        var initLoad = dojo.subscribe("/xf/ready", function() {{
            dojo.unsubscribe(initLoad);
            fluxProcessor.dispatchEvent('t-initial');
        }});
    </script>
	</head>
	<body>
		<div id="xforms">
			<div style="display:none">
				<xf:model id="login-1">
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
							<xf:message>Session-Id: <xf:output value="instance('i-login')/session-id"/></xf:message>
							<xf:setvariable name="session-id" select="instance('i-login')/session-id"/>
							<xf:setvariable name="username" select="instance('i-login')/username"/>
							<xf:setvariable name="password" select="instance('i-login')/password"/>
							<xf:dispatch name="load-editor" targetid="loginGroup"/>
						</xf:action>

						<xf:message ev:event="xforms-submit-done" level="modal" if="not(boolean-from-string(instance('i-login')/login))">Login failed.</xf:message>
						<xf:message ev:event="xforms-submit-error" level="modal">Login failed.</xf:message>
					</xf:submission>
					
				</xf:model>
				<xf:trigger id="t-initial">
				    <xf:label>initial</xf:label>
				    <xf:dispatch name="load-editor" targetid="loginGroup" if="boolean-from-string(instance('i-login')/login)"/>
				    <xf:toggle case="c-login" if="not(boolean-from-string(instance('i-login')/login))"/>
			    </xf:trigger>
			    
			    
			</div>

			<xf:group appearance="bf:verticalTable" id="loginGroup">
			    <xf:load ev:event="load-editor" show="replace">
			        <xf:resource value="concat('{$filename}?_xsl=/betterform/apps/editor/xf2jsTree.xsl&amp;_session=', instance('i-login')/session-id)"/>
			    </xf:load>
				<xf:switch>
					<xf:case id="c-default" selected="true"/>
					<xf:case id="c-login" selected="false">
        				<div id="notice">
        						In order to use the XForms Editor please login.
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
        			</xf:case>
        		</xf:switch>
			</xf:group>
		</div>
	</body>
</html>