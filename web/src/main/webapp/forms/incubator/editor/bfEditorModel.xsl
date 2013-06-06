<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:ev="http://www.w3.org/2001/xml-events"
        xmlns:xf="http://www.w3.org/2002/xforms" version="2.0"
        >

    <xsl:template name="editor-model">
        <div style="display:none">
            <xf:model id="m-editor">
                <!--todo: we need to use the baseURI of the editor here instead of the one we load -->
                <!--  i-default hold the editor DOM transformed to XForms -->
                <xf:instance xmlns="" id="i-default" src="{$EDITOR_HOME}cdata-instance.xml"/>

                <!-- Submission transforms the editor DOM to XForms-->
                <xf:submission id="s-dom2xforms" method="get"
                               resource="xslt:{$EDITOR_HOME}dom2xf.xsl?parseString=true" replace="instance">
                    <xf:action ev:event="xforms-submit-done">
                        <xf:message level="ephemeral">Data transformed to xforms</xf:message>
                        <xf:send submission="s-replaceContent"/>
                    </xf:action>
                    <xf:message ev:event="xforms-submit-error">?!?!Storing failed</xf:message>
                </xf:submission>

                  <!-- merge original XForms host document with Editor XForms markup -->
                <xf:submission id="s-replaceContent" method="get"
                               action="xslt:{$EDITOR_HOME}updateOriginal.xsl?originDoc={$filename}"
                               replace="instance" validate="false">
                    <!-- MODE: SAVE AS -->
                    <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'save-as'">
                        <xf:message level="ephemeral">Data mounted back to original document</xf:message>
                        <script type="text/javascript">
                            dijit.byId("saveDialog").hide();
                        </script>
                        <xf:send submission="s-save-as"/>
                    </xf:action>
                    <!-- MODE: SAVE -->
                    <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'save'">
                        <xf:message level="ephemeral">Data mounted back to original document</xf:message>
                        <xf:send submission="s-save"/>
                    </xf:action>
                    <!-- MODE: PREVIEW -->
                    <xf:action ev:event="xforms-submit-done" if="instance('i-controller')/mode eq 'preview'">
                        <xf:message level="ephemeral">Data ready to be saved.</xf:message>
                        <xf:send submission="s-preview"/>
                    </xf:action>

                    <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                </xf:submission>

                <!-- Overwrites the current form loaded within the editor  -->
                <xf:submission id="s-save" method="put" replace="none" action="{$filename}">
                    <xf:action ev:event="xforms-submit-done">
                        <xf:setvalue ref="instance('i-controller')/save-msg" value="concat('Data stored to ',IF(substring(bf:appContext('pathInfo'),2) eq '', concat(bf:appContext('contextroot'), '/rest', instance('i-controller')/filename) , concat(bf:appContext('webapp.realpath'),substring(bf:appContext('pathInfo'),2)) ) )"/>
                        <xf:message level="ephemeral" ref="instance('i-controller')/save-msg"/>
                    </xf:action>
                    <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                </xf:submission>

                <!-- Saves the form to a location specified by the user with  a custom name (given by the user too) -->
                <xf:submission id="s-save-as" method="put" replace="none">
                    <xf:resource value="concat('file://', bf:appContext('sl-filePath') ,'/', bf:appContext('sl-filename'))"/>
                    <!-- Set username and password for submission -->
                    <xf:action ev:event="xforms-submit-done">
                        <xf:setvalue ref="instance('i-controller')/save-msg" value="concat('Data stored to ', bf:appContext('sl-filePath') ,'/', bf:appContext('sl-filename'))"/>
                        <xf:message level="ephemeral" ref="instance('i-controller')/save-msg"/>
                    </xf:action>
                    <xf:message ev:event="xforms-submit-error">Storing failed</xf:message>
                </xf:submission>

                <!-- saves form to preview and opens it -->
                <xf:submission id="s-preview" method="put" replace="none">
                    <xf:resource value="concat(substring-before('{$filename}', '.xhtml'),'-prev.xhtml')"/>
                    <xf:action ev:event="xforms-submit-done">
                        <xf:load show="new" >
                            <xf:resource value="concat(replace(bf:appContext('contextPath'), 'bfEditor/', ''), substring-before(bf:appContext('fileName'), '.xhtml'),'-prev.xhtml')"/>
                        </xf:load>
                    </xf:action>
                    <xf:message ev:event="xforms-submit-error">Preview failed</xf:message>
                </xf:submission>

                <!-- internal controller instance -->
                <xf:instance id="i-controller">
                    <data xmlns="">
                        <currentXfType description="name of the currently selected xforms element"/>
                        <originalDoc/>
                        <save-msg/>
                        <preview-msp>Error previewing file</preview-msp>
                        <preview-path/>
                        <mode/>
                        <filename><xsl:value-of select="$filename"/></filename>
                        <instanceNodeName></instanceNodeName>
                    </data>
                </xf:instance>
            </xf:model>

            <xf:input ref="instance('i-controller')/currentXfType" id="currentType">
                <xf:label>hidden</xf:label>
            </xf:input>

            <xf:input ref="instance('i-controller')/instanceNodeName" id="instanceNodeName">
                <xf:label>hidden</xf:label>
            </xf:input>

            <xf:trigger id="t-loadProperties">
                <xf:label>hidden load trigger</xf:label>
                <xf:hint>this will load the properties encoded on the data-xf-props into the respective subform.</xf:hint>
                <xf:action>

                        <xf:load show="embed" targetid="xfMount">
                            <xf:resource value="concat('/betterform/forms/incubator/editor/',instance('i-controller')/currentXfType,'.xhtml#xforms')"/>
                            <xf:extension includeCSS="true" includeScript="true"/>
                        </xf:load>
                </xf:action>
            </xf:trigger>

        </div>
    </xsl:template>

</xsl:stylesheet>
