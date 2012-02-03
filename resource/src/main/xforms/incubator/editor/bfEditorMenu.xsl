<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
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
                        <xf:resource value="concat('{$EDITOR_HOME}', 'SaveAsDialog.xhtml#xforms')"/>
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

    <xsl:template name="contextMenuBar">
        <div id="contextBar" dojoType="dijit.Toolbar">
<!--
            <div dojoType="dijit.form.Button" id="contextBar.edit" iconClass="dijitEditorIcon dijitEditorIconCut" showLabel="true">
                edit
                <script type="dojo/method" event="onClick" args="evt">
                    // Do something:
                    console.debug("Thank you!: ",evt);
                    // embed the property form
                    fluxProcessor.dispatchEvent("t-loadProperties");
                </script>
            </div>
-->
            <div dojoType="dijit.form.Button" id="contextmenu.addsibling" class="bfaddSibling" iconClass="dijitEditorIcon"
            showLabel="false">
               <script type="dojo/method" event="onClick" args="evt">
                    /* <![CDATA[ */
                    console.debug("add sibling:",evt);
                    evt.stopPropagation();
                    evt.cancelBubble=true;

                    // remember to stop the event as it would otherwise trigger the handlers on the surrounding element too
                    dojo.style("componentTree","display", "inline-block");
                    dojo.style("componentTree","left", evt.pageX + "px");
                    dojo.style("componentTree","top", (evt.pageY + 15) + "px");
                    xformsEditor.updateComponentTree('sibling');
                    return false;
                    /* ]]> */
                </script>
                add sibling
            </div>

            <div dojoType="dijit.form.Button" id="contextmenu.addchild" class="bfaddChild" iconClass="dijitEditorIcon"
            showLabel="false">
                <script type="dojo/method" event="onClick" args="evt">
                    /* <![CDATA[ */
                    console.debug("add child:",evt);
                    evt.stopPropagation();
                    evt.cancelBubble=true;

                    // remember to stop the event as it would otherwise trigger the handlers on the surrounding element too
                    dojo.style("componentTree","display", "inline-block");
                    dojo.style("componentTree","left", evt.pageX + "px");
                    dojo.style("componentTree","top", (evt.pageY + 15) + "px");
                    xformsEditor.updateComponentTree('child');
                    return false;
                    /* ]]> */
                </script>
                add child
            </div>
            <div dojoType="dijit.form.DropDownButton" id="contextmenu.delete" class="bfDelete" iconClass="dijitEditorIcon dijitEditorIconDelete"
            showLabel="false">
                <script type="dojo/method" event="onClick" args="evt">
                    /* <![CDATA[ */
                    console.debug("delete clicked:",evt);
                    // remember to stop the event as it would otherwise trigger the handlers on the surrounding element too
                    evt.stopPropagation();
                    evt.cancelBubble=true;

                    return false;
                    /* ]]> */
                </script>

                <span>delete</span>

                <div dojotype="dijit.TooltipDialog" autofocus="true">
                    Really delete?
                    <div dojotype="dijit.form.Button" type="submit">
                        Ok
                        <script type="dojo/method" event="onClick" args="evt">
                            /* <![CDATA[ */

                            console.debug("OK evt:",evt);
                            console.debug("OK target:",evt.target);
                            console.debug("OK target id:",evt.target.id);



                            evt.stopPropagation();
                            evt.cancelBubble=true;
                            dojo.place(dojo.byId("contextBar"), dojo.byId("parkToolbar"), "last");
                            dojo.place(dojo.byId("nodeNameInput"), dojo.byId("parkNodeInput"), "last");
                            $("#xfDoc").jstree("remove", null);
                            return;
                            /* ]]> */
                        </script>

                    </div>
<!--
                    <div dojotype="dijit.form.Button" type="submit" class="linkButton">
                        Cancel
                        <script type="dojo/method" event="onClick" args="evt">
                            /* <![CDATA[ */

                            console.debug("Cancel evt:",evt);
                            // remember to stop the event as it would otherwise trigger the handlers on the surrounding element too
                            evt.stopPropagation();
                            evt.cancelBubble=true;
                            return false;
                            /* ]]> */
                        </script>

                    </div>
-->

                    <!--<a tabindex="0" href="javascript:alert('cancel');">cancel</a>-->
                </div>
            </div>
        </div>
</xsl:template>
</xsl:stylesheet>
