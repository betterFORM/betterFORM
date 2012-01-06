xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";

import module namespace request="http://exist-db.org/xquery/request";

declare option exist:serialize "method=xhtml media-type=application/xhtml+html";

let $contextPath := request:get-context-path()
return
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
    <head>
        <title>betterFORM Demo XForms: Address, Registration, FeatureExplorer</title>
        <link rel="stylesheet" type="text/css" href="{$contextPath}/rest/db/betterform/forms/demo/styles/demo.css"/>
    </head>
    <body id="timetracker" class="tundra InlineRoundBordersAlert">
        <div class="page">
            <div style="display:none;">
                <xf:model id="model-1">
                    <xf:instance>
                        <data xmlns="">
                            <start>20</start>
                            <quantity>20</quantity>
                        </data>
                    </xf:instance>

                    <xf:submission id="s-query-albums"
                                    resource="{$contextPath}/rest/db/betterform/apps/querytunes/view/list-itunes-albums.xql"
                                    method="get"
                                    replace="embedHTML"
                                    targetid="embedInline"
                                    ref="instance()"
                                    validate="false">
                        <xf:action ev:event="xforms-submit-error">
                            <xf:message>Submission 'Query Albums' failed</xf:message>
                        </xf:action>
                    </xf:submission>
                </xf:model>
            </div>

            <!-- ######################### Content ################################## -->
            <div id="content">
                <div id="header">
                    <a href="http://www.betterform.de"><img src="{$contextPath}/rest/db/betterform/apps/timetracker/resources/images/bf_logo_201x81.png" alt="betterFORM"/></a>
                    <div id="appName">queryTunes</div>
                </div>
                <div id="toolbar" dojoType="dijit.Toolbar">
                    <div id="overviewBtn" dojoType="dijit.form.DropDownButton" showLabel="true"
                         onclick="fluxProcessor.dispatchEvent('overviewTrigger');">
                        <span>Filter</span>
                        <div id="filterPopup" dojoType="dijit.TooltipDialog">
                            <table id="searchBar">
                                <tr>
                                    <td>
                                        <xf:input ref="start" incremental="true">
                                            <xf:label>Start</xf:label>
                                            <xf:action ev:event="xforms-value-changed" if="instance()/start &gt; 0">
                                                <xf:dispatch name="DOMActivate" targetid="overviewTrigger"/>
                                            </xf:action>
                                        </xf:input>
                                    </td>
                                    <td>
                                        <xf:select1 ref="quantity" appearance="minimal" incremental="true">
                                            <xf:label>Quantity</xf:label>
                                            <xf:action ev:event="xforms-value-changed">
                                                <xf:dispatch  name="DOMActivate" targetid="overviewTrigger"/>
                                            </xf:action>
                                            <xf:item>
                                                <xf:label>20</xf:label>
                                                <xf:value>20</xf:value>
                                            </xf:item>
                                            <xf:item>
                                                <xf:label>50</xf:label>
                                                <xf:value>50</xf:value>
                                            </xf:item>
                                            <xf:item>
                                                <xf:label>100</xf:label>
                                                <xf:value>100</xf:value>
                                            </xf:item>
                                        </xf:select1>
                                    </td>
                                    <td>
                                        <xf:trigger id="closeFilter">
                                            <xf:label/>
                                            <script type="text/javascript">
                                                dijit.byId("filterPopup").onCancel();
                                                fluxProcessor.dispatchEvent("overviewTrigger");
                                            </script>
                                        </xf:trigger>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                    <div id="searchBtn" dojoType="dijit.form.Button" showLabel="true" onclick="alert('todo');">
                        <span>Search</span>
                    </div>
                </div>

                <img id="shadowTop" src="{$contextPath}/rest/db/betterform/apps/timetracker/resources/images/shad_top.jpg" alt=""/>
                <xf:trigger id="overviewTrigger">
                    <xf:label>iTunes</xf:label>
                    <xf:send submission="s-query-albums"/>
                </xf:trigger>

                <!-- inlined content -->
                <div id="embedInline"></div>
            </div>
        </div>
        <script defer="defer" type="text/javascript" src="{$contextPath}/bfResources/scripts/betterform/betterform-TimeTracker.js"> </script>

    </body>
</html>
