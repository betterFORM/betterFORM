<?xml version="1.0" encoding="UTF-8"?>
<!-- $Id$ -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="html" encoding="UTF-8" omit-xml-declaration="yes"/>
    <xsl:param name="now"/>
    <xsl:param name="demoUrl" select="''"/>
    <xsl:param name="server" select="'sourceforge'"/>

    <xsl:param name="onlineVersion" select="'false'" />

    <xsl:template match="page">
        <html>
            <head>
                <title>
                    <xsl:value-of select="@title"/>
                </title>
                <!-- Google Webmaster Toolkit -->
                <xsl:choose>
                    <xsl:when test="$server = 'sourceforge'">
                        <meta name="verify-v1" content="bj1zGpSozojt7Vp/Sr5DOAkm/MYVA0sYoSYqsR+OpDk=" />
                    </xsl:when>
                    <xsl:when test="$server = 'betterFormJetty'">
                        <meta name="verify-v1" content="B3mrB2hDtjj34soDgSMppdcIXDbKK4N6Ebh9btMxZR8=" />
                    </xsl:when>
                </xsl:choose>
                <link rel="stylesheet" type="text/css" href="resources/styles/betterform-styles.css"/>
                <style type="text/css">
                    @import "http://ajax.googleapis.com/ajax/libs/dojo/1.2/dijit/themes/tundra/tundra.css";
                    @import "http://ajax.googleapis.com/ajax/libs/dojo/1.2/dojo/resources/dojo.css"
                </style>
                <style type="text/css">
                    #web3{
                        border:thin solid;
                        -moz-border-radius:5px;
                        padding:30px 30px 10px;
                        margin:5px 0px 10px 0px;
                    }

                    #screen1, #screen2{
                        padding:10px;
                    }
                </style>

                <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/dojo/1.2/dojo/dojo.xd.js"
                    djConfig="parseOnLoad: true"></script>
                    <script type="text/javascript">
                       dojo.require("dojo.parser");
                       dojo.require("dijit.TitlePane");
                       dojo.require("dojo.fx");
                 </script>
            </head>
            <body bgcolor="#aabbdd" class="tundra" id="indexPage">
                <table id="nav" cellpadding="0" cellspacing="0" border="0" width="100%">
                    <tbody>
                        <tr>
                            <td valign="middle">

                            </td>
                            <td valign="Middle">


                                <a href="http://betterform.sourceforge.net">
                                    <img style="margin-left:45px;padding-bottom:5px;" src="resources/images/betterform50t.gif" border="0" vspace="0" alt="betterForm Logo" width="113" height="39"/>
                                </a>
                                <br/>

                            </td>
                        </tr>
                    </tbody>
                </table>

                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                    <tr>
                        <td valign="top" class="leftPane">
                            <font face="sans-serif">
                                <div id="navLinks">
                                    <span>&lt;<a href="index.html">Home</a> /&gt;</span><br/>
                                    <span>&lt;<a href="http://sourceforge.net/project/showfiles.php?group_id=20274">Download</a> /&gt;</span><br/>
                                    <xsl:if test="$onlineVersion='false'">
                                    <span>&lt;<a href="jsp/forms.jsp" target="_blank">Run betterForm</a> /&gt;</span><br/>
                                    </xsl:if>
                                    <xsl:if test="$onlineVersion='true'">
                                    <span>&lt;<a href="demo.html">  Live Demo</a> /&gt;</span><br/>
                                    </xsl:if>

                                    <span>&lt;<a href="https://www.betterform.de/trac">Trac</a> /&gt;</span><br/>
                                    <span>&lt;<a href="reports/ConformanceReport1.1.html" target="_blank">Conformance Report</a> /&gt;</span><br/>
                                    <span>&lt;<a href="BetterFormUserGuide.pdf">UserGuide</a> /&gt;</span><br/>
                                    <span>&lt;<a href="BetterFormDeveloperGuide.pdf">DeveloperGuide</a> /&gt;</span><br/>
                                    <span>&lt;<a href="CSSReference.pdf">CSS Reference</a> /&gt;</span><br/>
                                    <span>&lt;<a href="http://sourceforge.net/projects/betterform">@Sourceforge</a> /&gt;</span>
<!--
                                     &lt;
                                    <a href="features.html">Status</a>/&gt;
-->
                                    <br/>
                                    <br/>
                                </div>
                            </font>


                            </td>
                        <xsl:choose>
                            <xsl:when test="/page/@id='index'">
                                <td class="content-area" valign="top">
<!--
                                    <xsl:for-each select="teaser | title | subtitle | para | list | cite">
                                        <xsl:apply-templates/>
                                    </xsl:for-each>
-->
                                    <xsl:apply-templates select="teaser"/>
                                    <xsl:apply-templates select="title | subtitle | para | list | cite "/>
                                </td>
                                <td width="150" id="news" valign="top">
<!--
                                    <span class="subtitle">
                                    News
                                    </span>
                                    <xsl:apply-templates select="//headline"/>
-->
                                </td>
                            </xsl:when>
                            <xsl:when test="/page/@id='demo'">
                                <xsl:variable name="url">
                                    <xsl:choose>
                                        <xsl:when test="$demoUrl = ''">jsp/forms.jsp</xsl:when>
                                        <xsl:otherwise><xsl:value-of select="concat($demoUrl,'jsp/forms.jsp')"/></xsl:otherwise>
                                    </xsl:choose>
                                </xsl:variable>

                                <td class="content-area demo" valign="top">
                                    <xsl:apply-templates/>
                                </td>
                                <td width="150"></td>
                            </xsl:when>
                            <xsl:otherwise>
                                <td class="content-area" valign="top">
                                    <xsl:apply-templates/>
                                </td>
                            </xsl:otherwise>
                        </xsl:choose>
                    </tr>
                </table>
                <div id="footer">
                    <table width="100%">
                        <tr>
                            <td width="150px;"></td>
                            <td>
                                <span id="osi">
                                    <a href="http://www.opensource.org/docs/certification_mark.php">
                                        <img src="resources/images/osi-certified-72x60.png" width="72" height="60" border="0" alt="Open Source Initiative"/>
                                    </a>
                                </span>
                            </td>
                            <td>
                                <font face="sans-serif" size="-1">
                                    <center>
                                Hosted by
                                        <a href="http://sourceforge.net">
                                            <img alt="SourceForge Logo" border="0" height="31" src="http://sourceforge.net/sflogo.php?group_id=23211&amp;type=1" width="88"/>
                                        </a>
                                    </center>
                                </font>
                            </td>
                            <td>
                                <div align="right"><xsl:value-of disable-output-escaping="yes" select="'&amp;copy;'"/> 2001-2005 betterForm Project</div>
                                <div align="right">last update: <xsl:value-of select="$now"/></div>
                                <div align="right">author: <a href="mailto:joernt@users.sourceforge.net">joernt</a>/<a href="mailto:windauer@users.sourceforge.net">lars</a></div>
                            </td>
                            <td width="150px;"></td>
                        </tr>
                    </table>

                </div>

                <!-- Google Analytics -->

                <script type="text/javascript">
                    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
                    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
                </script>
                <script type="text/javascript">
                    try {
                    <xsl:choose>
                        <xsl:when test="$server = 'sourceforge'">
                            var pageTracker = _gat._getTracker("UA-6761283-1");
                        </xsl:when>
                        <!-- todo: fix page tracking -->
                        <xsl:when test="$server = 'betterFormJetty'">
                            var pageTracker = _gat._getTracker("UA-6761316-1");
                        </xsl:when>
                    </xsl:choose>
                        pageTracker._trackPageview();
                    }
                    catch(err) {}
                </script>
        </body>
        </html>
    </xsl:template>

    <xsl:template match="headline">
        <div class="headline">
            <span class="date">
                <xsl:value-of select="@date"/>
            </span>
            <span style="text-size:-1;">
                <xsl:apply-templates/>
            </span>
        </div>
    </xsl:template>

    <xsl:template match="headline[@id]">
        <div class="headline" id="{@id}">
            <span class="date">
                <xsl:value-of select="@date"/>
            </span>
            <span style="text-size:-1;">
                <xsl:apply-templates/>
            </span>
        </div>
    </xsl:template>

    <xsl:template match="teaser">
        <div id="{@name}">
            <xsl:for-each select="*">
                <xsl:apply-templates select="."/>
            </xsl:for-each>
        </div>
    </xsl:template>

     <xsl:template match="title">
        <div class="title">
            <xsl:value-of select="."/>
        </div>
    </xsl:template>

    <xsl:template match="subtitle">
        <div class="subtitle">
            <xsl:if test="@id"><xsl:attribute name="id"><xsl:value-of select="@id"/></xsl:attribute></xsl:if> 
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="list[@id='features']">
        <ul style="text-decoration:none;margin:0;padding:0;">
            <xsl:for-each select="item">
                <li style="margin:3px;">
                    <div dojotype="dijit.TitlePane" title="{@title}" open="false">
                        <xsl:apply-templates/>
                    </div>
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="list" name="simpleList">
        <ul>
            <xsl:for-each select="item">
                <li style="margin:3px;">
                    <xsl:apply-templates/>
                </li>
            </xsl:for-each>
        </ul>
    </xsl:template>

    <xsl:template match="b">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="para">
        <div class="para" id="{@id}">
            <xsl:copy-of select="@style"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <xsl:template match="code">
        <span style="font-family:courier;"><xsl:apply-templates /></span>
    </xsl:template>

    <xsl:template match="cite">
        <blockquote>
            <i>
                <xsl:value-of disable-output-escaping="yes" select="'&amp;#132;'"/>
                <xsl:apply-templates/>
                <xsl:value-of disable-output-escaping="yes" select="'&amp;#147;'"/>
            </i>
            <xsl:value-of select="@from"/>
        </blockquote>
    </xsl:template>

    <xsl:template match="table">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="hr">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="a">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="img">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="tt">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="form">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="input">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="select">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="option">
        <xsl:copy-of select="."/>
    </xsl:template>

    <xsl:template match="center">
        <xsl:copy>
            <xsl:value-of select="."/>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="features">
        <table border="0" cellpadding="5" cellspacing="2">
            <tr>
                <td colspan="3">Legend</td>
            </tr>
            <tr>
                <td class="open" bgcolor="red">open -
                    <xsl:value-of select="count(/page/features/section/feature[@status='open'])"/>
                </td>
                <td class="partly" bgcolor="yellow">partly -
                    <xsl:value-of select="count(/page/features/section/feature[@status='partly'])"/>
                </td>
                <td class="ready" bgcolor="green">ready -
                    <xsl:value-of select="count(/page/features/section/feature[@status='ready'])"/>
                </td>
                <td class="na">not applicable -
                    <xsl:value-of select="count(/page/features/section/feature[@status='na'])"/>
                </td>
            </tr>
        </table>
        <br/>
        <table border="1" cellpadding="5" cellspacing="2" id="features">
            <tr align="left" valign="top" id="featureheader">
                <td>Section</td>
                <td>Comments</td>
                <!--<td>Status</td>-->
            </tr>
            <xsl:apply-templates/>
        </table>
    </xsl:template>

    <xsl:template match="section">
		<xsl:variable name="classes">
			<xsl:choose>
				<xsl:when test="@class">section <xsl:value-of select="@class"/></xsl:when>
				<xsl:otherwise>section</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<tr align="left" valign="top">
            <td colspan="3" class="{$classes}">
                <xsl:value-of select="@name"/>
            </td>
        </tr>
        <xsl:apply-templates/>
    </xsl:template>



    <xsl:template match="feature">
        <xsl:call-template name="row">
            <!--<xsl:with-param name="color" select="'silver'"/>-->
			<xsl:with-param name="status" select="@status"/>
        </xsl:call-template>
    </xsl:template>


    <xsl:template name="row">
        <xsl:param name="status"/>

		<tr align="left" valign="top" class="{$status}">
            <td>
                <xsl:value-of select="name"/>
				<xsl:value-of disable-output-escaping="yes" select="'&amp;nbsp;'"/>
            </td>
            <td>
                <xsl:apply-templates select="comment"/>
                <xsl:value-of disable-output-escaping="yes" select="'&amp;nbsp;'"/>
            </td>
         </tr>
    </xsl:template>

    <xsl:template match="comment">
        <xsl:copy-of select="."/>
    </xsl:template>
</xsl:stylesheet>
