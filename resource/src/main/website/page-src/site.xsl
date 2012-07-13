<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                exclude-result-prefixes="xsl html">


    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" media-type="text/xml"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />

    <!--<xsl:param name="rootDir" select="'../..'"/>-->
    <xsl:param name="currentFile" select="''"/>
    <xsl:param name="lang" select="'en'"/>

    <xsl:variable name="calcRoot" select="if ($currentFile='dashboard.xhtml')
                                            then '.'
                                            else '..'"/>

    <xsl:variable name="langFile" select="concat('lang/properties_',$lang,'.xml')"/>
    <xsl:variable name="language" select="document($langFile)"/>

    <xsl:variable name="date" select="current-date()"/>
    <xsl:variable name="year" select="year-from-date(current-date())"/>
    <xsl:variable name="nextYear" select="year-from-date(current-date())+1"/>
    <xsl:variable name="month" select="if(string-length(string(month-from-date(current-date()))) eq 2)
                                            then string(month-from-date(current-date()))
                                            else concat('0',month-from-date(current-date()))" />
    <xsl:variable name="day" select="if(string-length(string(day-from-date(current-date()))) eq 2)
                                            then string(day-from-date(current-date()))
                                            else concat('0',day-from-date(current-date()))"/>
    <xsl:variable name="currentDate" select="xs:date(concat($year,'-',$month,'-',$day))"/>
    <xsl:variable name="expireDate" select="xs:date(concat($nextYear,'-',$month,'-',$day))"/>


    <xsl:template match="/">
        <html xml:lang="{$lang}" lang="{$lang}">
            <head>
                <!--
                ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
                ~ Licensed under the terms of BSD License
                -->

                <title><xsl:value-of select="//html:title"/> - <xsl:value-of select="$currentFile"/></title>
                <meta http-equiv="Content-Type" content="text/html; charset=utf-8"/>
                <meta name="Author" content="Joern Turner"/>
                <meta name="Author" content="Lars Windauer"/>
                <meta name="publisher" content="betterFORM Project"/>
                <meta name="description" content=" betterFORM allows easy creation of highly dynamic Web 2.0 user interfaces with attractive controls. Add validations, calculations, actions and events to build webapplications."/>
                <meta name="abstract" content="The XForms Toolkit adds advanced eForms to your application.  The betterFORM XML Suite is an all-in-one solution for complex webapplications completely based upon XML.  betterFORM is free open source - no license cost, no restrictions - just open standards "/>
                <meta name="copyright" content="betterForm Project"/>
                <meta name="content-language" content="{$lang}"/>
                <meta name="robots" content="index,follow"/>
                <meta name="date" content="{$currentDate}"/>
                <meta http-equiv="expires" content="{$expireDate}"/>
                <meta name="revisit-after" content="10 days"/>
                <meta name="revisit" content="after 10 days"/>
                <meta name="DC.Title" content="betterFORM Project"/>
                <meta name="DC.Creator" content="Jörn Turner"/>
                <meta name="DC.Contributor" content="Lars Windauer"/>
                <meta name="DC.Rights" content="betterFORM Project"/>
                <meta name="DC.Publisher" content="betterFORM Project"/>
                <meta name="DC.Date" content="{$currentDate}"/>
                <meta name="DC.Subject" content="betterFORM XML Suite"/>
                <meta name="DC.Description" content="The XForms Toolkit adds advanced eForms to your application.  The betterFORM XML Suite is an all-in-one solution for complex webapplications completely based upon XML.  betterFORM is free open source - no license cost, no restrictions - just open standards "/>
                <meta name="DC.Coverage" content="Berlin / 2011"/>
                <meta name="DC.Language" content="{$lang}"/>
                <meta name="DC.Type" content="Software"/>
                <meta name="DC.Format" content="text/html"/>

                <meta name="google-site-verification" content="L5gmOYcZv-vwF2q0e-qSIXx8ecpKUN8A7lrW4ia9YR0"/>
                <xsl:copy-of select="//html:head/html:meta"/>

                <link rev="made" href="mailto:info@betterform.de"/>
                <link rel="SHORTCUT ICON" href="{$calcRoot}/images/betterform.ico"/>
                <link rel="ICON" href="{$calcRoot}/images/betterform.ico" type="image/x-icon"/>
                <link href="jquery.bubblepopup.v2.3.1.css" rel="stylesheet" type="text/css" />

                <link rel="stylesheet" type="text/css" href="{$calcRoot}/styles/website.css"/>
                <link href='http://fonts.googleapis.com/css?family=Istok+Web:400,700&amp;subset=latin,latin-ext' rel='stylesheet' type='text/css'/>



                <xsl:copy-of select="//html:head/html:script"/>
                <xsl:copy-of select="//html:head/html:link"/><xsl:text>
</xsl:text>
                <xsl:copy-of select="//html:head/html:style"/><xsl:text>
</xsl:text>

                <xsl:if test="//html:body/@id='solutions'">
                    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js" type="text/javascript"> </script><xsl:text>
</xsl:text>
                     <!-- Anything Slider optional plugins -->
                     <script src="../js/jquery.easing.1.2.js" type="text/javascript"> </script><xsl:text>
</xsl:text>

                     <!-- Anything Slider -->
                     <link href="../styles/anythingslider.css" type="text/css" rel="stylesheet" /><xsl:text>
</xsl:text>

                     <script src="../js/jquery.anythingslider.min.js" type="text/javascript"> </script><xsl:text>
</xsl:text>
                     <!-- Anything Slider optional FX extension -->
                     <!--<script src="js/jquery.anythingslider.fx.min.js" type="text/javascript"></script>-->
                    <script type="text/javascript">
                        $(document).ready(function(){
                             $('#referenceSlide')
                              .anythingSlider({
                               width               : 580,
                               height              : 300,
                               resizeContents      : false,
                               animationTime       : 300,
                               delay               : 4000,
                               buildNavigation     : false,
                               pauseOnHover        : true,
                               buildArrows         : true,
                               navigationFormatter : null,
                               startStopped        : false
                              });
                            });
                    </script>
                </xsl:if>


            </head>
            <body id="{//html:body/@id}">
                <xsl:copy-of select="//html:body/@style"/>
                <div class="page">

                    <div id="header">
                        <div class="pageMarginBox">
                            <div id="logoBar">
                                <div class="languages">
                                    <a href="{$calcRoot}/en/{//html:meta[@name='name']/@content}.html">
                                    <img src="{$calcRoot}/images/en.png" class="langSelector" alt="english version"
                                         title="english version"/>
                                    </a>

                                    <a href="{$calcRoot}/de/{//html:meta[@name='name']/@content}.html">
                                        <img src="{$calcRoot}/images/de.png" class="langSelector" alt="deutsch"
                                             title="deutsche Version"/>
                                    </a>
                                </div>

                                <a href="index.html" class="link" id="linkLogo">
                                    <img id="logo" src="{$calcRoot}/images/logo.png" alt="betterFORM project"/>
                                </a>

                                <!--<div id="mission">the XForms way to build the web</div>-->

                                <xsl:variable name="links">
                                    <a href="index.html">home</a><span class="menuDevider"> | </span>
                                    <!--<a href="demo.xhtml">demo</a><span class="menuDevider"> | </span>-->
                                    <a href="../dashboard.html" target="_blank">demo</a><span class="menuDevider"> | </span>
                                    <a href="download.html">download</a><span class="menuDevider"> | </span>
                                    <a href="software.html">software</a><span class="menuDevider"> | </span>
                                    <a href="services.html">services</a><span class="menuDevider"> | </span>
                                    <a href="solutions.html"><xsl:value-of select="$language/data/solutions"/></a><span class="menuDevider"> | </span>
                                    <a href="support.html">support</a><span class="menuDevider"> | </span>
                                    <a href="whoweare.html"><xsl:value-of select="$language/data/contact"/></a>
                                </xsl:variable>

                                <div id="topnav">
                                        <xsl:for-each select="$links/*">
                                            <xsl:choose>
                                                <xsl:when test="substring-before(./@href,'.') = substring-before($currentFile,'.')">
                                                    <xsl:copy>
                                                        <xsl:attribute name="style">color:#488aa6;</xsl:attribute>                                                                                 <xsl:attribute name="href" select="@href"/>
                                                        <xsl:copy-of select="text()"/>
                                                    </xsl:copy>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:copy-of select="."/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:for-each>
                                </div>
                            </div>
                        </div>
                    </div>

                    <div id="content-area">
                        <xsl:if test="not(exists(//html:body/@id[.='index']))">
                            <img id="shadowTop" src="{$calcRoot}/images/shad_top.jpg" alt=""/>
                        </xsl:if>
                        <noscript>
                            <div style="border: thin solid ; width: 100%; color: darkred;position:absolute;top:120px;z-index:999;padding:5px;background:orange">Sorry - this site was optimized for use with JavaScript. You won't be able to access all content until you activate JavaScript</div>
                        </noscript>
                        <div id="box" class="box">
                            <div id="altContent">
                            <div id="sub-header">
                                <div class="pageMarginBox">
                                    <div class="contentBody">
                                        <xsl:copy-of select="//html:div[@id='sub-header']/*"/>
                                    </div>
                                </div>
                            </div>
                            <div id="main-content">
                                    <div class="pageMarginBox">
                                        <xsl:copy-of select="//html:div[@id='main-content']/*"/>
                                    </div>
                            </div>

                            </div>
                        </div>
                    </div>
                    <!--<img id="shadowBottom" src="{$calcRoot}/images/shad_bottom.jpg" alt=""/>-->
                    <div id="footer">
                        <div class="pageMarginBox">

                            <div id="bottomContainer">
                                <div class="linkList">
                                    <div class="listTitle">Demo</div>
                                    <ul>
                                        <li><a href="../dashboard.html" target="_blank">Dashboard</a></li>
                                        <li><a href="../forms/reference/FeatureExplorer.xhtml" target="_blank">XForms Feature Explorer</a></li>
                                        <li><a href="../forms/demo/registration.xhtml" target="_blank">Registration</a></li>
                                        <li><a href="../forms/demo/orderlist.xhtml" target="_blank">OrderList</a></li>
                                        <li><a href="../forms/demo/contacts.xhtml" target="_blank">Contacts</a></li>
                                    </ul>
                                </div>
                                <div class="linkList">
                                    <div class="listTitle"><xsl:value-of select="$language/data/Solutions"/></div>
                                    <ul>
                                        <li><a href="solutions.html#ria">Rich Internet Applications</a></li>
                                        <li><a href="solutions.html#eforms">eForms / XForms</a></li>
                                        <li><a href="solutions.html#xml-applications"><xsl:value-of select="$language/data/xmlApplications"/></a></li>
                                        <li><a href="solutions.html#data-management"><xsl:value-of select="$language/data/dataManagement"/></a></li>
                                        <li><a href="solutions.html#eai">EAI / SOA</a></li>
                                        <li><a href="solutions.html#references"><xsl:value-of select="$language/data/references"/></a></li>
                                    </ul>
                                </div>
                                <div class="linkList">
                                    <div class="listTitle">Services</div>
                                    <ul>
                                        <li><a href="services.html#consulting">Consulting</a></li>
                                        <li><a href="services.html#development"><xsl:value-of select="$language/data/development"/></a></li>
                                        <li><a href="services.html#training"><xsl:value-of select="$language/data/training"/></a></li>
                                        <li><a href="services.html#support">Support</a></li>
                                    </ul>
                                </div>
                                <div class="linkList">
                                    <div class="listTitle">Software</div>
                                    <ul>
                                        <li><a href="xforms-toolkit.html">XForms Toolkit</a></li>
                                        <li><a href="xml-suite.html">betterFORM XML Suite</a></li>
                                        <li><a href="services.html#development"><xsl:value-of select="$language/data/customSolutions"/></a></li>
                                    </ul>
                                </div>
                                <div class="linkList">
                                    <div class="listTitle">Links</div>
                                    <ul>
                                        <li><a href="http://betterform.wordpress.com" target="_blank">Blog</a></li>
                                        <li><a href="http://betterform.wordpress.com/documentation/" target="_blank"><xsl:value-of select="$language/data/documentation"/></a></li>
                                        <li><a href="https://github.com/betterFORM/betterFORM" target="_blank">Sourcecode</a></li>
                                        <li><a href="download.html">Download</a></li>
                                        <li><a href="http://en.wikibooks.org/wiki/XForms" target="_blank">XForms Wikibook</a></li>
                                        <li><a href="http://en.wikibooks.org/wiki/XRX" target="_blank">XRX Wikibook</a></li>
                                    </ul>
                                </div>
                            </div>


                            <div id="bottomMenu">
                                &#169; 2012 betterFORM&#160;&#160; | &#160;&#160;
                                <xsl:choose>
                                    <xsl:when test="$lang='en'">
                                        <a href="contact.html">imprint</a> | &#160;&#160;
                                        <a href="privacy.html">privacy</a>
                                    </xsl:when>
                                    <xsl:otherwise>
                                        <a href="contact.html">Impressum</a> | &#160;&#160;
                                        <a href="privacy.html">Datenschutzerklärung</a>
                                    </xsl:otherwise>
                                </xsl:choose>
                            </div>
                        </div>
                    </div>
<!--
                    <div style="display:none">
                        <img src="{$calcRoot}/images/b_tryit_hover.png" alt=""/>
                        <img src="{$calcRoot}/images/b_getit_hover.png" alt=""/>
                        <img src="{$calcRoot}/images/b_exploreit_hover.png" alt=""/>
                    </div>
-->
                </div>

                <xsl:copy-of select="//*[@id='import']"/>


                <xsl:if test="//html:body/@id='index'">
                    <!--
                                        <link href="../js/jquery.bubblepopup.v2.3.1.css" rel="stylesheet" type="text/css" /><xsl:text>
                    </xsl:text>
                    -->
                    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js" type="text/javascript"></script><xsl:text>
</xsl:text>
                    <!-- JQuery Twitter resources -->
                    <script type="text/javascript" src="../js/jquery.juitter.js"></script><xsl:text>
</xsl:text>
                    <!--
                                        <script type="text/javascript" src="../js/juitter/js/system.js"></script><xsl:text>
                    </xsl:text>
                    -->
                    <!--
                                        <link type="text/css" href="../js/juitter/css/main.css"/><xsl:text>
                    </xsl:text>
                    -->
                    <!--
                                        <script src="../js/jquery.bubblepopup.v2.3.1.min.js" type="text/javascript"></script><xsl:text>
                    </xsl:text>
                    -->

                    <!-- Anything Slider optional plugins -->
                    <script src="../js/jquery.easing.1.2.js" type="text/javascript"></script><xsl:text>
</xsl:text>

                    <!-- Anything Slider -->
                    <link href="../styles/anythingslider.css" type="text/css" rel="stylesheet" /><xsl:text>
</xsl:text>

                    <script src="../js/jquery.anythingslider.min.js" type="text/javascript"></script><xsl:text>
</xsl:text>
                    <!--
                                        <script src="jquery.bubblepopup.v2.3.1.min.js" type="text/javascript"></script><xsl:text>
                    </xsl:text>
                    -->

                    <!-- Anything Slider optional FX extension -->
                    <!--<script src="js/jquery.anythingslider.fx.min.js" type="text/javascript"></script>-->
                    <script type="text/javascript">
                        $(document).ready(function(){
                        $('#slider2')
                        .anythingSlider({
                        width               : 900,
                        height              : 500,
                        resizeContents      : true ,
                        animationTime       : 500,
                        pauseOnHover        : false,
                        delay               : 5000,
                        buildArrows         : true,
                        buildNavigation     : false,
                        navigationFormatter : null,
                        startStopped        : false
                        });

                        $.Juitter.start({
                        searchType:"fromUser",
                        searchObject:"betterFORM2010",
                        placeHolder:"juitterContainer",
                        loadMSG: "Loading messages...",
                        imgName: "loader.gif", // Loading image, to enable it, go to the loadMSG var above and change it to "image/gif"
                        total: 2, // number of tweets to be show - max 100
                        readMore: "blablb",
                        nameUser:"none",
                        openExternalLinks:"newWindow"
                        });

                        //create a bubble popup for each DOM element with class attribute as "text", "button" or "link" and LI, P, IMG elements.
                        /*
                        $('#xformsToolkit').CreateBubblePopup({

                        position : 'top',
                        align	 : 'center',

                        innerHtml: 'Take a look to the HTML source of this page <br /> \
                        to learn how the plugin works!',

                        innerHtmlStyle: {
                        'text-align':'center'
                        },

                        });
                        */
                        });

                    </script>

                </xsl:if>

                <script type="text/javascript">
                    var _gaq = _gaq || []; _gaq.push(['_setAccount', 'UA-15044944-1']); _gaq.push (['_gat._anonymizeIp']); _gaq.push(['_trackPageview']);
                    (function() {
                        var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
                        ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
                        var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
                    })();
                </script>

            </body>
        </html>
    </xsl:template>


</xsl:stylesheet>
