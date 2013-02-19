<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:html="http://www.w3.org/1999/xhtml"
        xmlns:xf="http://www.w3.org/2002/xforms"
         xmlns:ev="http://www.w3.org/2001/xml-events"
        version="2.0" exclude-result-prefixes="html">

    <xsl:import href="bfEditor.xsl" />

    <!-- eXist version -->
    <!--
        xml:base="/betterform/rest/db/betterform/apps/editor/">
        <xsl:variable name="EDITOR_HOME" select="'/rest/db/betterform/apps/editor/'"/>
    -->

    <xsl:template name="main-ui">
        <xf:switch>
        <!-- intially loaded case to prevent showing not initialized markup to the user -->
            <xf:case id="c-empty" selected="true"/>
        <!-- login view if user is not logged in yet-->
            <xf:case id="c-login">
                 <div style="font-style: italic;color: orangered;text-align: center;width: 100%; font-size: 18px; border: #ff4500 solid thick;">
                    Caution: This Editor is only a preview.
                </div>
                <xf:group id="g-login" appearance="full" style="padding:20px;">
                    <xf:label>betterFORM XForms Editor</xf:label>
                    <div id="notice">In order to use the XForms Editor please login.</div>
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
        <!-- betterFORM XForms Editor -->
            <xf:case id="c-editor">
                <xsl:call-template name="editor-panel"/>
            </xf:case>
        </xf:switch>
    </xsl:template>

    <xsl:template name="editor-model">
        <div style="display:none">
            <xf:model id="model-1">
                <!--todo: we need to use the baseURI of the editor here instead of the one we load -->
                <!--  i-default hold the editor DOM transformed to XForms -->
                <xf:instance xmlns="" id="i-default" src="{$EDITOR_HOME}cdata-instance.xml"/>

                <!-- Submission transforms the editor DOM to XForms-->
                <xf:submission id="s-dom2xforms" method="get"
                               resource="xslt:{$APP_CONTEXT}{$EDITOR_HOME}dom2xf.xsl?parseString=true" replace="instance">
                    <xf:action ev:event="xforms-submit-done">
                        <!--<xf:message level="ephemeral">Data transformed to xforms</xf:message>-->
                        <xf:send submission="s-replaceContent"/>
                    </xf:action>
                    <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                </xf:submission>

                  <!-- merge original XForms host document with Editor XForms markup -->
                <xf:submission id="s-replaceContent" method="get"
                               action="xslt:{$APP_CONTEXT}{$EDITOR_HOME}updateOriginal.xsl?originDoc={$APP_CONTEXT}/rest{$filename}"
                               replace="instance" validate="false">
                    <!-- MODE: SAVE AS -->
                    <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'save-as'">
                        <!--<xf:message level="ephemeral">Data mounted back to original document</xf:message>-->
                        <script type="text/javascript">
                            dijit.byId("saveDialog").hide();
                        </script>
                        <xf:send submission="s-save-as"/>
                    </xf:action>
                    <!-- MODE: SAVE -->
                    <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'save'">
                        <!--<xf:message level="ephemeral">Data mounted back to original document</xf:message>-->
                        <xf:send submission="s-save"/>
                    </xf:action>
                    <!-- MODE: PREVIEW -->
                    <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'preview'">
                        <xf:message level="ephemeral">Data ready to be saved at tmp directory and to be loaded</xf:message>
                        <xf:send submission="s-preview"/>
                    </xf:action>

                    <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                </xf:submission>

                <!-- Overwrites the current form loaded within the editor  -->
                <xf:submission id="s-save" method="put" replace="none">
                    <xf:resource value="IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) )"/>
                    <xf:header>
                        <xf:name>username</xf:name>
                        <xf:value value="instance('i-login')/username"/>
                    </xf:header>
                    <xf:header>
                        <xf:name>password</xf:name>
                        <xf:value value="instance('i-login')/password"/>
                    </xf:header>
                    <xf:action ev:event="xforms-submit-done">
                        <xf:setvalue ref="instance('i-controller')/save-msg" value="concat('Data stored to ',IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) ) )"/>
                        <xf:message level="ephemeral" ref="instance('i-controller')/save-msg"/>
                    </xf:action>
                    <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                </xf:submission>

                <!-- Saves the form to a location specified by the user with  a custom name (given by the user too) -->
                <xf:submission id="s-save-as" method="put" replace="none">
                    <xf:resource value="concat(bf:appContext('sl-filePath') ,'/', bf:appContext('sl-filename'))"/>
                    <!-- Set username and password for submission -->
                    <xf:header>
                        <xf:name>username</xf:name>
                        <xf:value value="instance('i-login')/username"/>
                    </xf:header>
                    <xf:header>
                        <xf:name>password</xf:name>
                        <xf:value value="instance('i-login')/password"/>
                    </xf:header>
                    <xf:action ev:event="xforms-submit-done">
                        <xf:setvalue ref="instance('i-controller')/save-msg" value="concat('Data stored to ', bf:appContext('sl-filePath') ,'/', bf:appContext('sl-filename'))"/>
                        <xf:message level="ephemeral" ref="instance('i-controller')/save-msg"/>
                    </xf:action>
                    <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                </xf:submission>

                <!-- saves form to preview and opens it -->
                <xf:submission id="s-preview" method="put" replace="none">
                    <xf:resource value="concat(substring-before( IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) ),'.xhtml'),'-prev.xhtml')"/>
                    <xf:header>
                        <xf:name>username</xf:name>
                        <xf:value value="instance('i-login')/username"/>
                    </xf:header>
                    <xf:header>
                        <xf:name>password</xf:name>
                        <xf:value value="instance('i-login')/password"/>
                    </xf:header>
                    <xf:load show="new" ev:event="xforms-submit-done">
                        <xf:resource value="concat(substring-before( IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) ),'.xhtml'),'-prev.xhtml')"/>
                    </xf:load>
                    <xf:message ev:event="xforms-submit-error">Preview failed</xf:message>
                </xf:submission>

                <!-- internal controller instance -->
                <xf:instance id="i-controller">
                    <data xmlns="">
                        <originalDoc/>
                        <save-msg/>
                        <preview-msp>Error previewing file</preview-msp>
                        <preview-path/>
                        <mode/>
                        <filename><xsl:value-of select="$filename"/></filename>
                    </data>
                </xf:instance>

                <!-- Instance holding the login data of the user -->
                <xf:instance id="i-login" xmlns="" >
                    <data/>
                </xf:instance>

                <xf:submission  id="s-get-login-credentials"
                                method="get"
                                replace="instance"
                                instance="i-login">
                    <xf:resource value=" concat(bf:appContext('contextroot'), '/rest/db/betterform/utils/Login.xql')"/>

                    <xf:action ev:event="xforms-submit-done">
                        <xf:toggle case="c-login" if="not(boolean-from-string(instance('i-login')/login))"/>
                        <xf:toggle case="c-editor" if="boolean-from-string(instance('i-login')/login)"/>
                    </xf:action>
                </xf:submission>

                <xf:submission id="s-login"
                                method="get"
                                replace="instance"
                                instance="i-login"
                                serialization="none"
                                validate="false"
                                relevant="false">

                    <xf:resource value="concat(bf:appContext('contextroot'), '/rest/db/betterform/utils/Login.xql?username=', instance('i-login')/username, '&amp;password=', instance('i-login')/password)"/>

                    <xf:action ev:event="xforms-submit-done" if="boolean-from-string(instance('i-login')/login)">
                        <xf:toggle case="c-editor"/>
                    </xf:action>

                    <xf:message ev:event="xforms-submit-done" level="modal" if="not(boolean-from-string(instance('i-login')/login))">Login failed.</xf:message>
                    <xf:message ev:event="xforms-submit-error" level="modal">Login failed.</xf:message>
                </xf:submission>

                <xf:action ev:event="xforms-ready">
                    <xf:send submission="s-get-login-credentials"/>
                </xf:action>
            </xf:model>

        </div>
    </xsl:template>

     <!--
    THE MENU BAR OF THE XFORMS EDITOR APPLICATION ALONG WITH THE XFORMS TRIGGERS TO SUPPORT THE MENU ACTIONS.
    -->
    <xsl:template name="menu-bar" priority="100">
        <!-- HIDDEN CONTROLS EXECUTING MENU CHOICES -->
        <div style="display:none;">
            <xf:input id="save" ref="instance()">
                <xf:label>this is a hidden control set from JS when saving is executed</xf:label>
            </xf:input>
            <xf:trigger id="transform2xf">
                <xf:label>this is hidden</xf:label>
                <xf:send submission="s-dom2xforms"/>
            </xf:trigger>
            <xf:trigger id="t-save">
                <xf:label>this is hidden</xf:label>
                <xf:action>
                    <xf:setvalue ref="instance('i-controller')/mode" value="'save'"/>
                    <script type="text/javascript">
                        serializeTree();
                    </script>
                </xf:action>
            </xf:trigger>
            <xf:trigger id="t-preview">
                <xf:label>this is hidden</xf:label>
                <xf:action>
                    <xf:message level="ephemeral">Preview Start</xf:message>
                    <xf:setvalue ref="instance('i-controller')/mode" value="'preview'"/>
                    <script type="text/javascript">
                        serializeTree();
                    </script>
                </xf:action>
            </xf:trigger>
            <xf:trigger id="t-save-as">
                <xf:label>saveas</xf:label>
                <xf:action>
                    <xf:setvalue ref="instance('i-controller')/mode" value="'save-as'"/>
                    <xf:load show="embed" targetid="embedDialog">
                        <xf:resource value="concat(bf:appContext('contextroot'), '/rest/db/betterform/utils/SaveListing.xql#xforms')"/>
                        <xf:extension includeCSS="true" includeScript="true"/>
                    </xf:load>
                </xf:action>
            </xf:trigger>
        </div>

        <div dojoType="dijit.MenuBar" id="mainMenu">
            <div dojoType="dijit.PopupMenuBarItem" label="File">
                <div dojoType="dijit.Menu" id="File">
                    <!--
                                                    <div dojoType="dijit.MenuItem"
                                                         onClick="this.window.href='bfEditor/forms/incubator/editor/standardTemplate.xhtml');">
                                                        New
                                                    </div>
                    -->
                    <div dojoType="dijit.MenuItem" onClick="dijit.byId('fluxProcessor').dispatchEvent('t-preview');">
                        Preview Strg+p
                    </div>
                    <div dojoType="dijit.MenuItem" onClick="dijit.byId('fluxProcessor').dispatchEvent('t-save');">
                        Save
                    </div>
                    <div dojoType="dijit.MenuItem" onClick="showSaveDialog();">
                        Save as...
                    </div>
                    <!--
                    <div dojotype="dijit.PopupMenuItem" label="input">
                        <div dojotype="dijit.Menu">
                            <div dojoType="dijit.MenuItem"
                                 onClick="fluxProcessor.dispatchEvent('inputTextTrigger');hideStatic();"
                                 label="Text"/>
                        </div>
                    </div>
                    -->
                </div>
            </div>
            <div dojoType="dijit.PopupMenuBarItem" label="Edit">
                <div dojoType="dijit.Menu" id="Edit">
                    <div dojoType="dijit.MenuItem" onClick="alert('cut');">
                        Cut
                    </div>
                    <div dojoType="dijit.MenuItem" onClick="alert('copy');">
                        Copy
                    </div>
                    <div dojoType="dijit.MenuItem" onClick="alert('paste');">
                        Paste
                    </div>
                </div>
            </div>
            <div dojoType="dijit.MenuBarItem" onClick="dijit.byId('bfEditorHelp').show();">
                Help
            </div>

            <!--
                <div dojoType="dijit.PopupMenuBarItem" label="Add" id="addMenu">
                </div>
            -->
        </div>

    </xsl:template>
</xsl:stylesheet>
