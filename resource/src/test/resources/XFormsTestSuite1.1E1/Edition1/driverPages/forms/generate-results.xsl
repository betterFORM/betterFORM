<xsl:stylesheet version="2.0"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xf="http://www.w3.org/2002/xforms"
                xmlns:ev="http://www.w3.org/2001/xml-events"
                xmlns:ts="http://www.w3c.org/MarkUp/Forms/XForms/Test/11"
                exclude-result-prefixes="html ev xsl xf">

    <!-- Copyright 2008 Lars Windauer, Joern Turner -->
    <xsl:param name="rootDir" select="''"/>
    <!--<xsl:param name="chapter" select="'Container Form Controls'"/>-->
    <xsl:param name="chapter" select="'all'"/>
    <xsl:param name="useragent" select="'Mac-10.5-FF-3.0'"/>

    <!-- if set to 'true' the report will only contain results for the $useragent above. Otherwise a result
    column for every config in test-config.xml will be created. -->
    <xsl:param name="referencePlatformOnly" select="'false'"/>


    <xsl:param name="mode" select="'full'"/>

    <xsl:variable name="config">
        <xsl:for-each select="document(concat($rootDir,'/../test-config.xml'))">
            <xsl:copy-of select="."/>
        </xsl:for-each>
    </xsl:variable>

    <xsl:output method="xhtml" omit-xml-declaration="yes" indent="yes"/>

    <xsl:template match="/">
        <html>
            <head>
                <title>XForms TestSuite Results (Edition 1)</title>
                <style type="text/css">
                    body{
                        font-family:sans-serif;
                        margin:20px;
                        background:url(resources/images/bgOne.gif) repeat-x fixed;
                    }
                    table{
                        width:800px;
                        margin-left:auto;
                        margin-right:auto;
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
                    .Unknown{background:#ddd;}

                    .testConfig {
                        background-color:#F0F0F0;
                        border:1px solid black;
                        margin-bottom:10px;
                        padding:10px;
                        width:400px;
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
                </style>
            </head>
            <body>
                <div class="pageheader">XForms 1.1 TestSuite Edition 1 Results for Chiba</div>

                <xsl:variable name="all">
                    <xsl:for-each select="//file[contains(@name,$useragent)]">
                        <xsl:for-each select="document(concat($rootDir,'/',@name))">
                            <xsl:copy-of select="."/>
                        </xsl:for-each>
                    </xsl:for-each>
                </xsl:variable>
                <xsl:variable name="total"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseNormative='true'])"/>
                <xsl:variable name="passed"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Passed'])"/>
                <xsl:variable name="failed"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Failed'])"/>
                <xsl:variable name="unknown"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseNormative='true'][./ts:testCaseStatus='Unknown'])"/>

                <xsl:variable name="btotal"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseBasic='true'])"/>
                <xsl:variable name="bpassed"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Passed'])"/>
                <xsl:variable name="bfailed"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Failed'])"/>
                <xsl:variable name="bunknown"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseBasic='true'][./ts:testCaseStatus='Unknown'])"/>

                <xsl:variable name="nonTotal"
                              select="count($all//ts:specChapter//ts:testCase[./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
                <xsl:variable name="nonPassed"
                              select="count($all//ts:testCase[./ts:testCaseStatus='Passed'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
                <xsl:variable name="nonFailed"
                              select="count($all//ts:testCase[./ts:testCaseStatus='Failed'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>
                <xsl:variable name="nonUnknown"
                              select="count($all//ts:testCase[./ts:testCaseStatus='Unknown'][./ts:testCaseBasic='false'][./ts:testCaseNormative='false'])"/>

                <xsl:variable name="totalTotal" select="count($all//ts:specChapter//ts:testCase)"/>
                <xsl:variable name="totalPassed"
                              select="count($all//ts:testCase[./ts:testCaseStatus='Passed'])"/>
                <xsl:variable name="totalFailed"
                              select="count($all//ts:testCase[./ts:testCaseStatus='Failed'])"/>
                <xsl:variable name="totalUnknown"
                              select="count($all//ts:testCase[./ts:testCaseStatus='Unknown'])"/>

                <div class="testConfig">
                    <div class="header">Test Summary</div>
                    <div>Product:
                        <xsl:value-of select="$config//product-id"/>
                    </div>
                    <div>Useragent:
                        <xsl:value-of select="$config//configs/config[@path=$useragent]"/>
                    </div>
                    <div>Chapter(s):
                        <xsl:value-of select="$chapter"/>
                    </div>
                    <div>Normative tests:
                        <xsl:value-of select="$total"/>
                    </div>
                    <div>Passed:
                        <xsl:value-of select="$passed"/> (<xsl:value-of select="format-number( (($passed div $total)), '##%')"/>)
                    </div>
                    <div>Failed:
                        <xsl:value-of select="$failed"/> (<xsl:value-of select="format-number( (($failed div $total)), '##%')"/>)
                    </div>
                </div>

                <!--
                                    <selectedChapter result="" clear="">Empty</selectedChapter>
                                    <selectedConfig>Mac-10.5-FF-3.0</selectedConfig>
                                    <configs>
                                        <config path="Mac-10.5-FF-3.0">Mac OS X 10.5 Firefox 3.0</config>
                                        <config path="Win-XPSP3-IE-6.0">Windows XP SP3 Internet Explorer 6.0</config>
                                    </configs>
                -->


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
                        <td colspan="4" align="center">XForms Basic Normative</td>
                        <xsl:choose>
                            <xsl:when test="$mode = 'full'">
                                <td colspan="4" align="center">XForms Non Normative</td>
                            </xsl:when>
                        </xsl:choose>

                    </tr>
                    <tr>
                        <td>Total</td>
                        <td>Passed</td>
                        <td>Failed</td>
                        <td>Unkown</td>
                        <td>Total</td>
                        <td>Passed</td>
                        <td>Failed</td>
                        <td>Unknown</td>
                        <xsl:choose>
                            <xsl:when test="$mode = 'full'">
                                <td>Total</td>
                                <td>Passed</td>
                                <td>Failed</td>
                                <td>Unknown</td>
                            </xsl:when>
                        </xsl:choose>
                    </tr>


                    <xsl:for-each select="$all//ts:specChapter">
                        <tr>
                            <xsl:choose>
                                <xsl:when test="$chapter = 'all'">
                                    <xsl:apply-templates select="." mode="chapter"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:apply-templates select="$all//ts:specChapter[@chapterTitle=$chapter]"
                                                         mode="chapter"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </tr>
                    </xsl:for-each>

                    <!--
                                            <xsl:for-each select="//file">
                                                <xsl:variable name="path" select="concat($rootDir,'/',./@name)" />
                                                <tr>
                                                    <xsl:for-each select="document($path)">
                                                        <xsl:choose>
                                                            <xsl:when test="$chapter = 'all'">
                                                                <xsl:apply-templates select="." mode="chapter"/>
                                                            </xsl:when>
                                                            <xsl:otherwise>
                                                                <xsl:apply-templates select="//ts:specChapter[@chapterTitle=$chapter]" mode="chapter"/>
                                                            </xsl:otherwise>
                                                        </xsl:choose>
                                                    </xsl:for-each>
                                                </tr>
                                            </xsl:for-each>
                    -->


                    <xsl:choose>
                        <xsl:when test="$chapter = 'all'">

                            <tr class="totals">
                                <td>Totals</td>
                                <td>
                                    <xsl:value-of select="$total"/>
                                </td>
                                <td>
                                    <xsl:value-of select="$passed"/>
                                    (<xsl:value-of select="format-number( (($passed div $total)), '##%')"/>)
                                </td>
                                <td>
                                    <xsl:value-of select="$failed"/>
                                    (<xsl:value-of select="format-number( (($failed div $total)), '##%')"/>)
                                </td>
                                <td>
                                    <xsl:value-of select="$unknown"/>
                                    (<xsl:value-of select="format-number( (($unknown div $total)), '##%')"/>)
                                </td>
                                <td>
                                    <xsl:value-of select="$btotal"/>
                                </td>
                                <td>
                                    <xsl:value-of select="$bpassed"/>
                                    (<xsl:value-of select="format-number( (($bpassed div $btotal)), '##%')"/>)
                                </td>
                                <td>
                                    <xsl:value-of select="$bfailed"/>
                                    (<xsl:value-of select="format-number( (($bfailed div $btotal)), '##%')"/>)
                                </td>
                                <td>
                                    <xsl:value-of select="$bunknown"/>
                                    (<xsl:value-of select="format-number( (($bunknown div $btotal)), '##%')"/>)
                                </td>
                                <xsl:choose>
                                    <xsl:when test="$mode = 'full'">
                                        <td>
                                            <xsl:value-of select="$nonTotal"/>
                                        </td>
                                        <td>
                                            <xsl:value-of select="$nonPassed"/>
                                            (<xsl:value-of select="format-number( (($nonPassed div $nonTotal)), '##%')"/>)
                                        </td>
                                        <td>
                                            <xsl:value-of select="$nonFailed"/>
                                            (<xsl:value-of select="format-number( (($nonFailed div $nonTotal)), '##%')"/>)
                                        </td>
                                        <td>
                                            <xsl:value-of select="$nonUnknown"/>
                                            (<xsl:value-of select="format-number( (($nonUnknown div $nonTotal)), '##%')"/>)
                                        </td>
                                    </xsl:when>
                                </xsl:choose>

                            </tr>
<!--
                            <tr class="totals">
                                <td>Totals (Overall)</td>
                                <td>
                                    <xsl:value-of select="$totalTotal"/>
                                </td>
                                <td>
                                    <xsl:value-of select="$totalPassed"/>
                                    (<xsl:value-of select="format-number( (($totalPassed div $totalTotal)), '##%')"/>)
                                </td>
                                <td>
                                    <xsl:value-of select="$totalFailed"/>
                                    (<xsl:value-of select="format-number( (($totalFailed div $totalTotal)), '##%')"/>)
                                </td>
                                <td>
                                    <xsl:value-of select="$totalUnknown"/>
                                    (<xsl:value-of select="format-number( (($totalUnknown div $totalTotal)), '##%')"/>)
                                </td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                                <td></td>
                            </tr>
-->

                        </xsl:when>

                    </xsl:choose>


                </table>
                <p/>

                <!-- details -->
                <table border="0" cellpadding="3" cellspacing="1">
                    <xsl:choose>
                        <xsl:when test="$chapter = 'all'">
                            <xsl:for-each select="$all//ts:specChapter">
                                <xsl:variable name="chapterName" select="./@chapterName"/>

                                <xsl:if test="$mode='all' or .//ts:testCaseNormative='true'">


                                <tr>
                                    <xsl:variable name="headerCols" select="6 + count($config//config)"/>
                                    <td colspan="{$headerCols}" class="chapterHeader">
                                        <xsl:value-of select="@chapterTitle"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Test Case2</td>
                                    <td>Description</td>
                                    <td>Spec Link</td>
                                    <xsl:choose>
                                        <xsl:when test="$mode='full'">
                                            <td>Normative Basic</td>
                                            <td>Normative Full</td>
                                        </xsl:when>
                                    </xsl:choose>

                                    <xsl:choose>
                                        <xsl:when test="$referencePlatformOnly='true'">
                                            <td>last run</td>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:for-each select="$config//config">
                                                <td>
                                                    <xsl:value-of select="@path"/>
                                                </td>
                                            </xsl:for-each>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </tr>

                                <xsl:for-each select="ts:testCase">
                                    <xsl:variable name="currentTestCaseName" select="ts:testCaseName"/>

                                    <xsl:if test="$mode='all' or ts:testCaseNormative='true'">
                                        <tr>
                                            <td>
                                                <xsl:value-of select="$currentTestCaseName"/>
                                            </td>
                                            <td>
                                                <xsl:value-of select="ts:testCaseDescription"/>
                                            </td>
                                            <td>
                                                <a href="{ts:testCaseSpecLink}" target="_blank"><xsl:value-of select="ts:testCaseSpecLinkName"/></a>
                                            </td>
                                            <xsl:choose>
                                                <xsl:when test="$mode='full'">
                                                    <td class="{ts:testCaseBasic}">
                                                        <xsl:value-of select="ts:testCaseBasic"/>
                                                    </td>
                                                    <td class="{ts:testCaseNormative}">
                                                        <xsl:value-of select="ts:testCaseNormative"/>
                                                    </td>
                                                </xsl:when>
                                            </xsl:choose>

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

                                            <xsl:choose>
                                                <xsl:when test="$referencePlatformOnly='true'">
                                                    <xsl:variable name="resultsFile"
                                                                  select="document(concat($rootDir,'/XF11_', substring-before($chapterNumber,'.') ,'_Results-',$useragent,'.xml'))"/>
                                                    <xsl:variable name="useragentResult"
                                                                  select="$resultsFile//ts:testCase[ts:testCaseName = $currentTestCaseName]/ts:testCaseStatus"/>
                                                    <!--<td class="{$useragentResult}">&#160;</td>-->
                                                    <td class="{$useragentResult}" align="center" valign="middle"><xsl:value-of select="$resultsFile//ts:testCase[ts:testCaseName = $currentTestCaseName]/ts:testCaseDate"/></td>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:for-each select="$config//config">
                                                        <xsl:variable name="resultsFile"
                                                                      select="document(concat($rootDir,'/XF11_', substring-before($chapterNumber,'.') ,'_Results-',@path,'.xml'))"/>
                                                        <xsl:variable name="useragentResult"
                                                                      select="$resultsFile//ts:testCase[ts:testCaseName = $currentTestCaseName]/ts:testCaseStatus"/>
                                                        <td class="{$useragentResult}" align="center" valign="middle"><xsl:value-of select="$resultsFile//ts:testCase[ts:testCaseName = $currentTestCaseName]/ts:testCaseDate"/></td>
                                                    </xsl:for-each>
                                                </xsl:otherwise>
                                            </xsl:choose>


                                        </tr>
                                    </xsl:if>
                                </xsl:for-each>
                                </xsl:if>
                                
                            </xsl:for-each>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:for-each select="$all//ts:specChapter[@chapterTitle=$chapter]">
                                <tr>
                                    <td colspan="6" style="background:#ccc;">
                                        <xsl:value-of select="@chapterTitle"/>
                                    </td>
                                </tr>
                                <tr>
                                    <td>Test Case</td>
                                    <td>Description</td>
                                    <td>Spec Link</td>
                                    <td>Normative Basic</td>
                                    <td>Normative Full</td>
                                    <td>Status</td>
                                </tr>

                                <xsl:for-each select="ts:testCase">
                                    <tr>
                                        <td>
                                            <xsl:value-of select="ts:testCaseName"/>
                                        </td>
                                        <td>
                                            <xsl:value-of select="ts:testCaseDescription"/>
                                        </td>
                                        <td>
                                            <a href="{ts:testCaseLink}">
                                                <xsl:value-of select="ts:testCaseName"/>
                                            </a>
                                        </td>
                                        <td class="{ts:testCaseBasic}">
                                            <xsl:value-of select="ts:testCaseBasic"/>
                                        </td>
                                        <td class="{ts:testCaseNormative}">
                                            <xsl:value-of select="ts:testCaseNormative"/>
                                        </td>
                                        <td class="{ts:testCaseStatus}">
                                            <xsl:value-of select="ts:testCaseStatus"/>
                                        </td>
                                    </tr>
                                </xsl:for-each>
                            </xsl:for-each>

                        </xsl:otherwise>
                    </xsl:choose>
                </table>
                <p/>
                <p/>
            </body>
        </html>
    </xsl:template>

    <!-- copy template -->
    <xsl:template match="*|@*|text()">
        <xsl:copy>
            <xsl:apply-templates select="*|@*|text()"/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="dir">
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="file">
        <div>
            <xsl:value-of select="@name"/>
        </div>
    </xsl:template>

    <xsl:template match="ts:specChapter" mode="chapter">
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
            <br/>
            <xsl:if test="$total != 0">
                (<xsl:value-of select="format-number( (($passed div $total)), '##%')"/>)
            </xsl:if>
        </td>
        <td align="center" class="Failed">
            <xsl:value-of select="$failed"/>
            <br/>
            <xsl:if test="$total != 0">
                (<xsl:value-of select="format-number( (($failed div $total)), '##%')"/>)
            </xsl:if>
        </td>
        <td align="center" class="Unknown">
            <xsl:value-of select="$unknown"/>
            <br/>
            <xsl:if test="$total != 0">
                (<xsl:value-of select="format-number( (($unknown div $total)), '##%')"/>)
            </xsl:if>
        </td>
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
        <xsl:choose>
            <xsl:when test="$mode='full'">
                <td align="center" class="Total">
                    <xsl:value-of select="$chapterTotal"/>
                </td>
                <td align="center" class="Passed">
                    <xsl:value-of select="$chapterPassed"/>
                    <br/>
                    <xsl:if test="$chapterTotal != 0">
                        (<xsl:value-of select="format-number( (($chapterPassed div $chapterTotal)), '##%')"/>)
                    </xsl:if>
                </td>
                <td align="center" class="Failed">
                    <xsl:value-of select="$chapterFailed"/>
                    <br/>
                    <xsl:if test="$chapterTotal != 0">
                        (<xsl:value-of select="format-number( (($chapterFailed div $chapterTotal)), '##%')"/>)
                    </xsl:if>
                </td>
                <td align="center" class="Unknown">
                    <xsl:value-of select="$chapterUnknown"/>
                    <br/>
                    <xsl:if test="$chapterTotal != 0">
                        (<xsl:value-of select="format-number( (($chapterUnknown div $chapterTotal)), '##%')"/>)
                    </xsl:if>
                </td>                
            </xsl:when>
        </xsl:choose>
    </xsl:template>


    <!--
         <xsl:template match="ts:numNormPass | ts:numNormFail" mode="chapter">
             <xsl:value-of select="."/>
             <br/>
             <xsl:if test="../ts:numNormTotal != 0">
                 (<xsl:value-of select="format-number( ((. div ../ts:numNormTotal)), '##%')"/>)
             </xsl:if>
         </xsl:template>
    -->

    <!--
         <xsl:template match="ts:numBasicPass | ts:numBasicFail" mode="chapter">
             <xsl:value-of select="."/>
             <br/>
             <xsl:if test="../ts:numBasicTotal != 0">
                 (<xsl:value-of select="format-number( ((. div ../ts:numBasicTotal)), '##%')"/>)
             </xsl:if>
         </xsl:template>
    -->


    <!--
        <xsl:template match="ts:numNormTotal">
            <div><xsl:value-of select="."/></div>
        </xsl:template>
    -->
</xsl:stylesheet>

        <!--
            <specChapter chapterName="6. " chapterTitle="Model Item Properties" productId="">
                <statusSummary>
                    <numNormTotal>11</numNormTotal>
                    <numNormPass>9</numNormPass>
                    <numNormFail>2</numNormFail>
                    <numNormUnknown>0</numNormUnknown>
                    <numBasicTotal>9</numBasicTotal>
                    <numBasicPass>8</numBasicPass>
                    <numBasicFail>1</numBasicFail>
                    <numBasicUnknown>0</numBasicUnknown>
                </statusSummary>
                <profile>
                    <specification>XForms 1.1</specification>
                    <normativeOnly>false</normativeOnly>
                    <basicOnly>false</basicOnly>
                </profile>
                <testCase>
                    <testCaseSection>6.1.1</testCaseSection>
                    <testCaseName>6.1.1.a</testCaseName>
                    <testCaseLink>../../Chapt06/6.1/6.1.1/6.1.1.a.xhtml</testCaseLink>
                    <testCaseDescription>type property</testCaseDescription>
                    <testCaseSpecLinkName>6.1.1</testCaseSpecLinkName>
                    <testCaseSpecLink>http://www.w3.org/TR/xforms11/#model-prop-type</testCaseSpecLink>
                    <testCaseBasic>true</testCaseBasic>
                    <testCaseNormative>true</testCaseNormative>
                    <testCaseStatus>Failed</testCaseStatus>
                    <testCaseDate>2009-09-12</testCaseDate>
                    <testCaseRequired>true</testCaseRequired>
                    <testCaseNote/>
                </testCase>
            </specChapter>
        -->
