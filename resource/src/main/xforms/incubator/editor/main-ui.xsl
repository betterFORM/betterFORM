<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:bf="http://betterform.sourceforge.net/xforms" xmlns:html="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms" version="2.0" exclude-result-prefixes="html" xml:base="/exist/rest/db/betterform/apps/editor/">

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

                <div id="topPane">
                    <!-- ################### MENU BAR ################### -->
                    <!-- ################### MENU BAR ################### -->
                    <!-- ################### MENU BAR ################### -->
                    <xsl:call-template name="menu-bar" />

                    <img src="{$APP_CONTEXT}/bfResources/images/betterform_icon16x16.png" alt=""/>
                </div>
                <div id="mainWindow" style="width:100%;">
                    <div id="docWrapper" tabindex="-1">
                        <!--
                                            <xf:output value="bf:appContext('pathInfo')">
                                                <xf:label>PathInfo: </xf:label>
                                            </xf:output>
                        -->
                        <div id="docPane" tabindex="-1">
                            <xsl:variable name="elements" select="//xf:model[not(ancestor::xf:*)]|//xf:group[not(ancestor::xf:*)]"/>
                            <!--<xsl:variable name="uiElements" select="//*[name()='xf:group']"/>-->
                            <div id="xfDoc" class="xfDoc" tabindex="-1">
                                <ul>
                                    <li id="root" data-xf-type="document" tabindex="0">
                                        <a href="#">Document</a>
                                        <ul>
                                            <!--<xsl:for-each select="$elements">-->
                                            <xsl:apply-templates select="$elements"/>
                                            <!--</xsl:for-each>-->
                                        </ul>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                    <div id="leftPane" tabindex="-1">
                        <div id="addLabel">
                            <div class="caption">Add ...</div>
                            <div id="addModeDiv">
                                <button id="btnChildMode" type="button" class="modeSelector selected" onclick="attrEditor.updateComponentTree(this);">Child
                                </button>
                                <button id="btnSiblingMode" type="button" class="modeSelector" onclick="attrEditor.updateComponentTree(this);">Sibling
                                </button>
                            </div>
                        </div>
                        <!--
                        the 'mode' attribute is used to switch between 'children' and 'siblings' mode which
                        determines the list of possible elements displayed in the component tree.
                        -->
                        <div id="componentTree" data-bf-addmode="child"/>
                    </div>
                    <div id="rightPane" tabindex="-1">
                        <div id="xfMount" dojotype="dijit.layout.ContentPane" href="{$APP_CONTEXT}{$EDITOR_HOME}document.html" preload="false">
                            <script type="dojo/connect" event="onDownloadEnd">
                                var xfId = dojo.attr(dojo.byId("xfMount"), "xfId");
                                if (xfId == undefined) {
                                    return;
                                }
                                // console.debug("xfid: ", xfId);
                                attrEditor.editProperties(xfId);
                            </script>
                        </div>
                    </div>
                </div>
            </xf:case>
        </xf:switch>
    </xsl:template>


</xsl:stylesheet>
