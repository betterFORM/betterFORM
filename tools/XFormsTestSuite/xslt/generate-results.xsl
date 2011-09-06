<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:ts="http://www.w3c.org/MarkUp/Forms/XForms/Test/11"
                exclude-result-prefixes="html ev xsl xf">

    <!-- Copyright 2008 Lars Windauer, Joern Turner -->
    <xsl:param name="basePath" select="''"/>
    <xsl:param name="chapter" select="'all'"/>
    <xsl:param name="useragent" select="'Mac-10.5-FF-3.0'"/>

    <!-- if set to 'true' the report will only contain results for the $useragent above. Otherwise a result
    column for every config in test-config.xml will be created. -->
    <xsl:param name="referencePlatformOnly" select="'false'"/>


    <xsl:param name="mode" select="'full'"/>

    <xsl:variable name="config">
        <xsl:for-each select="document(concat($basePath,'test-config.xml'))">
            <xsl:copy-of select="."/>
        </xsl:for-each>
    </xsl:variable>

    <xsl:output method="xhtml" omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>XForms TestSuite Results (Edition 1)</title>
                <style type="text/css">
                    @import "http://ajax.googleapis.com/ajax/libs/dojo/1.2/dijit/themes/tundra/tundra.css";
                    @import "http://ajax.googleapis.com/ajax/libs/dojo/1.2/dojo/resources/dojo.css"
                </style>

                <style type="text/css">
                    body{
                        font-family:sans-serif;
                        margin:50px;
                        background:url(resources/images/bgOne.gif) repeat-x fixed;
                    }
                    table{
                        width:800px;
                    }

                    td{
                        background-color:lightSteelBlue;
                    }
                    .totals td{
                        text-align:center;
                        font-weight:bold;
                        font-size:12pt;
                        padding:10px;
                    }
                    .true{font-weight:bold;}
                    .numNormPass, .numBasicPass, .Passed {background:chartreuse;}
                    .numNormFail, .numBasicFail, .Failed {background:orangered;}
                    .numNormUnknown, .numBasicUnknown, .Unknown{background:#ddd;}
                    .Passed {background:chartreuse;}
                    .Failed {background:orangered;}
                    .Manual {background:darkgreen;}
                    .Unknown{background:#ddd;}

                    .testConfig {
                        background-color:LemonChiffon;
                        border:1px solid black;
                        margin-bottom:10px;
                        padding:10px;
                        width:775px;
                    }
                    .testConfig .header {
                        border-bottom:1px solid;
                        display:block;
                        font-size:large;
                        font-variant:small-caps;
                        margin-bottom:5px;
                    }
                    .testConfig div {
                        display:block;
                        font-size:10pt;
                        font-weigth:bolder;
                    }
                    .testConfig table{
                        width:100%;
                    }
                    .testConfig td{
                        background:LemonChiffon;
                    }
                    .pageheader {
                        display:block;
                        font-size:x-large;
                        font-weight:bold;
                        margin-bottom:20px;
                    }
                    #summary{
                        font-size:12pt;
                        background-color:SteelBlue;
                        padding:10px;
                    }
                    .chapterHeader{
                        background:SteelBlue;
                        font-size:12pt;
                        padding:10px;
                    }
                    .
                    .Note{
                        text-align:center;
                        font-size:8pt;
                        display:none;
                    }
                    .dijitTooltipContainer{
                        padding:3px;
                        background:lightyellow;
                        border:thin solid lightsteelblue;
                        font-size:11pt;
                    }
                </style>
                <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.2/dojo/dojo.xd.js"
                    djConfig="parseOnLoad: true"></script>
                    <script type="text/javascript">
                       dojo.require("dojo.parser");
                       dojo.require("dijit.TitlePane");
                       dojo.require("dojo.fx");
                       dojo.require("dijit.Tooltip");
                 </script>

            </head>
            <body>
                <div class="pageheader">XForms 1.1 TestSuite Edition 1 Results (normative only)</div>

                <xsl:variable name="total"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseNormative='true'])"/>
                <xsl:variable name="passed"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Passed'])"/>
                <xsl:variable name="failed"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Failed'])"/>
                <xsl:variable name="unknown"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Unknown'])"/>

<!--
                <xsl:variable name="btotal"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseBasic='true'])"/>
                <xsl:variable name="bpassed"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Passed'])"/>
                <xsl:variable name="bfailed"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Failed'])"/>
                <xsl:variable name="bunknown"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Unknown'])"/>
-->

                <xsl:variable name="nonTotal"
                              select="count(//ts:specChapter//ts:testCase[./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
                <xsl:variable name="nonPassed"
                              select="count(//ts:testCase[./ts:testCaseStatus='Passed'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
                <xsl:variable name="nonFailed"
                              select="count(//ts:testCase[./ts:testCaseStatus='Failed'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
                <xsl:variable name="nonUnknown"
                              select="count(//ts:testCase[./ts:testCaseStatus='Unknown'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>

                <xsl:variable name="totalTotal" select="count(//ts:specChapter//ts:testCase)"/>
                <xsl:variable name="totalPassed"
                              select="count(//ts:testCase[./ts:testCaseStatus='Passed'])"/>
                <xsl:variable name="totalFailed"
                              select="count(//ts:testCase[./ts:testCaseStatus='Failed'])"/>
                <xsl:variable name="totalUnknown"
                              select="count(//ts:testCase[./ts:testCaseStatus='Unknown'])"/>

                <div class="testConfig">
                    <div class="header">Test Summary</div>
                    <table>
                        <tr>
                            <td>Product:</td>
                            <td><xsl:value-of select="$config//product-id"/></td>
                            <td rowspan="6">
                                <div>
                                   <script type="text/javascript">
                                     dojo.require("dojox.charting.Chart2D");
                                     dojo.require("dojox.charting.plot2d.Pie");
                                     dojo.require("dojox.charting.action2d.Highlight");
                                     dojo.require("dojox.charting.action2d.MoveSlice");
                                     dojo.require("dojox.charting.action2d.Tooltip");
                                     dojo.require("dojox.charting.themes.MiamiNice");
                                     dojo.require("dojox.charting.widget.Legend");

                                     createChart = function(passed, failed, unknown) {
                                       var chart = new dojox.charting.Chart2D("conformanceChart"),
                                           sum   = passed + failed + unknown;

                                       chart.addPlot("default", {
                                           labels: true,
                                           ticks: true,
                                           type: "Pie",
                                           font: "normal normal 11pt Tahoma",
                                           fontColor: "black",
                                           labelOffset: -50,
                                           radius: 50,
                                           fill: "lemonchiffon"
                                       });
                                       var numbers = [
                                         {
                                           y: passed,
                                           stroke: {color: "black" },
                                           fill: "chartreuse",
                                           tooltip: passed + " tests passed",
                                           text: ((passed / sum) * 100).toFixed(0) + "% passed"
                                         },
                                         {
                                           y: failed,
                                           stroke: {color: "black" }, 
                                           fill: "orangered",
                                           tooltip: failed + " tests failed",
                                           text: ((failed / sum) * 100).toFixed(0) + "% failed"
                                         },
                                         {
                                           y: unknown,
                                           stroke: {color: "black"}, 
                                           fill: "#DDD",
                                           tooltip: unknown + " tests unknown",
                                           text: ((unknown / sum) * 100).toFixed(0) + "% unknown"
                                         }
                                       ];
                                       chart.addSeries("Results", numbers);
                                       var anim_a = new dojox.charting.action2d.MoveSlice(chart, "default");
                                       var anim_b = new dojox.charting.action2d.Highlight(chart, "default");
                                       var anim_c = new dojox.charting.action2d.Tooltip(chart, "default");
                                       chart.render();
                                       // Do not show the legend!
                                       // var legend = new dojox.charting.widget.Legend({
                                       //                                              chart: chart },"conformanceLegend");
                                       return chart;
                                     }
                                     dojo.addOnLoad(function(){
                                         createChart( $passed,
                                                      $failed,
                                                      $unknown);
                                     });
                                   </script>
                                   <div id="conformanceChart" style="width: 150px; height: 150px;"></div>
                                   <!-- do not show the legend -->
                                   <!-- <div id="conformanceLegend"></div> -->
                               </div>
                            </td>
                        </tr>
                        <tr>
                            <td>Useragent:</td>
                            <td><xsl:value-of select="$config//configs/config[@path=$useragent]"/></td>
                        </tr>
                        <tr>
                            <td>Normative tests:</td>
                            <td><xsl:value-of select="$total"/></td>
                        </tr>
                        <tr>
                            <td>Passed:</td>
                            <td><xsl:value-of select="$passed"/> (<xsl:value-of select="format-number( (($passed div $total)), '##%')"/>)</td>
                        </tr>
                        <tr>
                            <td>Failed:</td>
                            <td><xsl:value-of select="$failed"/> (<xsl:value-of select="format-number( (($failed div $total)), '##%')"/>)</td>
                        </tr>
                        <tr>
                            <td>Unknown:</td>
                            <td><xsl:value-of select="$unknown"/> (<xsl:value-of select="format-number( (($unknown div $total)), '##%')"/>)</td>
                        </tr>
                    </table>
                </div>

                <table border="0" cellspacing="1" cellpadding="3">
                    <tr>
                        <td colspan="13">
                            <div id="summary">Result Summary for Reference Platform:
                                <xsl:value-of select="$config//configs/config[@path=$useragent]"/>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td rowspan="2">Chapter</td>
                        <td colspan="4" align="center">XForms Full Normative</td>
                        <!--<td colspan="4" align="center">XForms Basic Normative</td>-->
                    </tr>
                    <tr>
                        <td>Total</td>
                        <td>Passed</td>
                        <td>Failed</td>
                        <td>Unkown</td>
                        <!--<td>Total</td>-->
                        <!--<td>Passed</td>-->
                        <!--<td>Failed</td>-->
                        <!--<td>Unknown</td>-->
                    </tr>


                    <xsl:for-each select="//ts:specChapter[.//ts:testCaseNormative='true']">
                        <tr>
                            <xsl:apply-templates select="." mode="chapterSummary"/>
                        </tr>
                    </xsl:for-each>
                </table>

                <xsl:for-each select=".//ts:testSuite">
                    <xsl:apply-templates select="."/>
                </xsl:for-each>
                <!--<xsl:apply-templates select="ts:testSuite"/>-->
                <p/>
                <p/>
            </body>
        </html>
            
    </xsl:template>

    <xsl:template match="ts:testSuite">
        <p/>
        <!-- details -->
        <table border="0" cellpadding="3" cellspacing="1">
            <xsl:for-each select="ts:specChapter[.//ts:testCaseNormative='true']">
                <xsl:variable name="chapterName" select="./@chapterName"/>

                <tr>
                    <xsl:variable name="headerCols" select="6 + count($config//config)"/>
                    <td colspan="{$headerCols}" class="chapterHeader">
                        <xsl:value-of select="@chapterTitle"/>
                    </td>
                </tr>
                <tr>
                    <td width="10%">Test Case</td>
                    <td>Description</td>
                    <td width="10%">Spec Link</td>
<!--
                    <xsl:choose>
                        <xsl:when test="$mode='full'">
                            <td>Normative Basic</td>
                            <td>Normative Full</td>
                        </xsl:when>
                    </xsl:choose>
-->


                    <td width="10%">last run</td>
                </tr>

                <xsl:for-each select="ts:testCase[.//ts:testCaseNormative='true']">
                    <tr>
                        <td>
                            <a href="../forms/XFormsTestSuite1.1E1/Edition1/{substring(ts:testCaseLink,7,string-length(ts:testCaseLink))}"><xsl:value-of select="ts:testCaseName"/></a>
                        </td>
                        <td>
                            <xsl:value-of select="ts:testCaseDescription"/>
                        </td>
                        <td>
                            <a href="{ts:testCaseSpecLink}" target="_blank"><xsl:value-of select="ts:testCaseSpecLinkName"/></a>
                        </td>
<!--
                        <td class="{ts:testCaseBasic}">
                            <xsl:value-of select="ts:testCaseBasic"/>
                        </td>
-->
<!--
                        <td class="{ts:testCaseNormative}">
                            <xsl:value-of select="ts:testCaseNormative"/>
                        </td>
-->
                        <xsl:variable name="connectId" select="generate-id()"/>
                        <td class="{ts:testCaseStatus}" style="white-space:nowrap;">
                            <div class="Note" dojoType="dijit.Tooltip" connectId="{$connectId}">
                                <xsl:value-of select="ts:testCaseNote"/>
                            </div>
                            <div class="lastUpdate" id="{$connectId}">
                                <xsl:value-of select="ts:testCaseDate"/>
                            </div>
                        </td>
<!--
                        <xsl:variable name="chapterNumber">
                            <xsl:choose>
                                <xsl:when test="string(number($chapterName)) = 'NaN'">
                                    <xsl:value-of select="concat('Appendix',$chapterName)"/>
                                </xsl:when>
                                <xsl:when
                                        test="number($chapterName) &gt; 0 and number($chapterName) &lt; 10">
                                    <xsl:value-of select="concat('0',$chapterName)"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="$chapterName"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:variable>
-->

                    </tr>
                </xsl:for-each>

            </xsl:for-each>
        </table>
    </xsl:template>

    <!-- copy template -->
    <xsl:template match="*|@*|text()">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="ts:specChapter" mode="chapterSummary">
        <td>
            <xsl:value-of select="concat(@chapterName,' ' ,@chapterTitle)"/>
        </td>

        <xsl:variable name="total" select="count(.//ts:testCase[./ts:testCaseNormative='true'])"/>
        <xsl:variable name="passed"
                      select="count(.//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Passed'])"/>
        <xsl:variable name="failed"
                      select="count(.//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Failed'])"/>
        <xsl:variable name="unknown"
                      select="count(.//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Unknown'])"/>
        <xsl:variable name="btotal" select="count(.//ts:testCase[./ts:testCaseBasic='true'])"/>
        <xsl:variable name="bpassed"
                      select="count(.//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Passed'])"/>
        <xsl:variable name="bfailed"
                      select="count(.//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Failed'])"/>
        <xsl:variable name="bunknown"
                      select="count(.//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Unknown'])"/>
        <xsl:variable name="chapterTotal"
                      select="count(.//ts:testCase[./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
        <xsl:variable name="chapterPassed"
                      select="count(.//ts:testCase[./ts:testCaseStatus='Passed'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
        <xsl:variable name="chapterFailed"
                      select="count(.//ts:testCase[./ts:testCaseStatus='Failed'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
        <xsl:variable name="chapterUnknown"
                      select="count(.//ts:testCase[./ts:testCaseStatus='Unknown'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>

        <td align="center" class="Total">
            <xsl:value-of select="$total"/>
        </td>
        <td align="center" class="Passed">
            <xsl:value-of select="$passed"/>
            <xsl:if test="$total != 0">
                (<xsl:value-of select="format-number( (($passed div $total)), '##%')"/>)
            </xsl:if>
        </td>
        <td align="center" class="Failed">
            <xsl:value-of select="$failed"/>
            <xsl:if test="$total != 0">
                (<xsl:value-of select="format-number( (($failed div $total)), '##%')"/>)
            </xsl:if>
        </td>
        <td align="center" class="Unknown">
            <xsl:value-of select="$unknown"/>
            <xsl:if test="$total != 0">
                (<xsl:value-of select="format-number( (($unknown div $total)), '##%')"/>)
            </xsl:if>
        </td>
<!--
        <td align="center" class="Total">
            <xsl:value-of select="$btotal"/>
        </td>
        <td align="center" class="Passed">
            <xsl:value-of select="$bpassed"/>
            <br/>
            <xsl:if test="$btotal != 0">
                (<xsl:value-of select="format-number( (($bpassed div $btotal)), '##%')"/>)
            </xsl:if>
        </td>
        <td align="center" class="Failed">
            <xsl:value-of select="$bfailed"/>
            <br/>
            <xsl:if test="$btotal != 0">
                (<xsl:value-of select="format-number( (($bfailed div $btotal)), '##%')"/>)
            </xsl:if>
        </td>
        <td align="center" class="Unknown">
            <xsl:value-of select="$bunknown"/>
            <br/>
            <xsl:if test="$btotal != 0">
                (<xsl:value-of select="format-number( (($bunknown div $btotal)), '##%')"/>)
            </xsl:if>
        </td>
-->
    </xsl:template>


</xsl:stylesheet>

