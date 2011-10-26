<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:ev="http://www.w3.org/2001/xml-events"
        xmlns:xf="http://www.w3.org/2002/xforms" version="2.0"
        >
    <!--
    THE MENU BAR OF THE XFORMS EDITOR APPLICATION ALONG WITH THE XFORMS TRIGGERS TO SUPPORT THE MENU ACTIONS.
    -->
    <xsl:template name="menu-bar">
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
