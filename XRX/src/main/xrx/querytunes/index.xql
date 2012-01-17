xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace xf = "http://www.w3.org/2002/xforms";

import module namespace request="http://exist-db.org/xquery/request";

declare option exist:serialize "method=xhtml media-type=application/xhtml+html";



declare function local:numberOfAlbums() as xs:integer {
    count(collection('/db/betterform/apps/querytunes/data')//album)
};

let $contextPath := request:get-context-path()
return
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
    <head>
        <title>betterFORM Demo XForms: Address, Registration, FeatureExplorer</title>
        <link rel="stylesheet" type="text/css" href="{$contextPath}/rest/db/betterform/forms/demo/styles/demo.css"/>
        <link rel="stylesheet" type="text/css" href="./resources/css/querytunes.css"/>
    </head>
    <body id="timetracker" class="tundra InlineRoundBordersAlert">
        <div class="page">
            <div style="display:none;">
                <xf:model id="model-1">
                    <xf:instance>
                        <data xmlns="">
                            <start>1</start>
                            <quantity>10</quantity>
                            <search/>
                        </data>
                    </xf:instance>
                    <xf:bind nodeset="instance()">
                        <xf:bind nodeset="start" type="integer"/>
                        <xf:bind nodeset="quantity" type="integer"/>
                    </xf:bind>

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

                    <xf:action ev:event="xforms-ready">
                        <xf:send submission="s-query-albums"/>
                    </xf:action>

                   <xf:instance id="i-controller" xmlns="">
                        <data>
                            <currentAlbum/> 
                            <next/>
                            <previous/>
                            <labelNext/>
                            <labelPrevious/>
                            <overall>{local:numberOfAlbums()}</overall>
                        </data>
                    </xf:instance>
                    
                    <xf:bind nodeset="instance('i-controller')">
                        <xf:bind nodeset="previous" readonly="instance()/start &lt;= 1"/>
                        <xf:bind nodeset="next" readonly="instance()/start + instance()/quantity &gt; instance('i-controller')/overall"/>                        
                        <xf:bind nodeset="labelPrevious" calculate="concat('previous ', instance()/quantity)"/>
                        <xf:bind nodeset="labelNext" calculate="concat('next ', instance()/quantity)"/>
                    </xf:bind>
                    
                </xf:model>
                <xf:input id="currentAlbum" ref="instance('i-controller')/currentAlbum">
                    <xf:label>This is just a dummy used by JS</xf:label>
                </xf:input>
                <xf:trigger id="showAlbum">
                    <xf:label>Show Album</xf:label>
                    <xf:action>
                        <xf:load show="embed" targetid="embedDialog">
                            <xf:resource value="concat('{$contextPath}/rest/db/betterform/apps/querytunes/view/view-album.xql#xforms?album=',instance('i-controller')/currentAlbum)"/>
                        </xf:load>
                    </xf:action>
                </xf:trigger>
                <xf:trigger id="overviewTrigger">
                    <xf:label>Search</xf:label>
                    <xf:send submission="s-query-albums"/>
                </xf:trigger>


            </div>

            <!-- ######################### Content ################################## -->
            <div id="content">
                <div id="header">
                    <a id="linklogo" href="http://www.betterform.de"><img src="{$contextPath}/rest/db/betterform/apps/timetracker/resources/images/bf_logo_201x81.png" alt="betterFORM"/></a>
                    <div id="appName">queryTunes</div>
    
                </div>
                <div id="toolbar" dojoType="dijit.Toolbar" style="clear:both;">
                    <div id="overviewBtn" dojoType="dijit.form.DropDownButton" showLabel="true"
                         onclick="fluxProcessor.dispatchEvent('overviewTrigger');">
                        <span>Filter</span>
                        <div id="filterPopup" dojoType="dijit.TooltipDialog">
                            <table id="searchBar">
                                <tr>
                                    <td>
                                        <xf:input id="inputStart" ref="start" incremental="true" >
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
                                                <xf:label>10</xf:label>
                                                <xf:value>10</xf:value>
                                            </xf:item>
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
                                        <xf:input ref="search" incremental="true">
                                            <xf:label>Search</xf:label>
                                            <xf:action ev:event="xforms-value-changed">
                                                <xf:setvalue ref="instance()/start" value="1"/>
                                                <xf:recalculate/>
                                                <xf:send submission="s-query-albums" />                                                
                                            </xf:action>
                                            
                                            
                                        </xf:input>
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
                </div>

                <img id="shadowTop" src="{$contextPath}/rest/db/betterform/apps/timetracker/resources/images/shad_top.jpg" alt=""/>


                <div id="albumDialog" dojotype="dijit.Dialog" style="left:200px;top:200px;position:absolute;width:auto;height:auto;" title="Album" autofocus="false">
                    <div id="embedDialog"></div>
                </div>
                
                <div class="paging" style="float:right;">
                    <xf:trigger appearance="minimal" ref="instance('i-controller')/previous">
                        <xf:label ref="instance('i-controller')/labelPrevious"/>
                        <xf:setvalue ref="instance()/start" value="number(instance()/start) - number(instance()/quantity)" />
                        <xf:recalculate/>
                        <xf:dispatch name="DOMActivate" targetid="overviewTrigger"/>
                    </xf:trigger>
                    <span> / </span>
                    <xf:trigger appearance="minimal" ref="instance('i-controller')/next">
                        <xf:label ref="instance('i-controller')/labelNext"/>
                        <xf:setvalue ref="instance()/start" value="number(instance()/start) + number(instance()/quantity)" />
                        <xf:recalculate/>
                        <xf:dispatch name="DOMActivate" targetid="overviewTrigger"/>
                    </xf:trigger>
                </div>
                
                <!-- inlined content -->
                <!-- inlined content -->
                <div id="embedInline"></div>
            </div>
        </div>
        <script defer="defer" type="text/javascript" src="{$contextPath}/bfResources/scripts/betterform/betterform-TimeTracker.js"> </script>
        <script type="text/javascript" defer="defer">
            <!--
            var xfReadySubscribers;

            function embed(targetTrigger,targetMount){
                console.debug("embed",targetTrigger,targetMount);
                if(dojo.byId('embedDialog') != undefined) {
                    dojo.byId('embedDialog').innerHTML = "";
                }
                
                if(targetMount == "embedDialog"){
                    dijit.byId("albumDialog").show();                    
                    dojo.style(dojo.byId('albumDialog'), {
                        top:'200px',
                        left:'200px'
                    });
                }

                var targetMount =  dojo.byId(targetMount);

                fluxProcessor.dispatchEvent(targetTrigger);
                console.debug("dispatched Event to ", targetTrigger);
            }

            var editSubcriber = dojo.subscribe("/album/show", function(data){
                console.debug("album show: ", data);
                fluxProcessor.setControlValue("currentAlbum",data);
                embed('showAlbum','embedDialog');

            });


            var refreshSubcriber = dojo.subscribe("/task/refresh", function(){
                fluxProcessor.dispatchEvent("overviewTrigger");
            });

            // -->
        </script>
    </body>
</html>
