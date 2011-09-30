<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2011. betterForm Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:html="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="xsl html">


    <xsl:output method="xhtml" version="1.0" encoding="UTF-8" media-type="text/xml"
            doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"
            doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN" />

    <!--<xsl:param name="rootDir" select="'../..'"/>-->
    <xsl:param name="currentFile" select="''"/>

    <xsl:variable name="calcRoot" select="if ($currentFile='dashboard.xhtml')
                                            then '.'
                                            else '..'"/>
    <xsl:template match="/">
        <html xml:lang="en" lang="en">
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
                <meta name="description"
                      content=" betterFORM allows easy creation of highly dynamic Web 2.0 user interfaces with attractive controls. Add validations, calculations, actions and events to build webapplications."/>
                <meta name="keywords"
                      content="betterFORM, xforms, forms, form, xhtml, xpath, xslt, xslt2.0, dojo, dojotoolkit, java, javascript, xml, schema, web, web2.0, web2, web3.0, web3, semantic, semantic web, joern turner, turner, lars windauer, ajax, xforms processor, processor"/>
                <meta name="copyright" content="betterForm Project"/>
                <meta name="content-language" content="en"/>
                <meta name="robots" content="all"/>
                <meta http-equiv="expires" content="Wed, 9 Feb 2011 12:21:57 GMT"/>
                <meta name="revisit-after" content="2 days"/>
                <meta name="google-site-verification" content="L5gmOYcZv-vwF2q0e-qSIXx8ecpKUN8A7lrW4ia9YR0"/>


                <link rev="made" href="mailto:info@betterform.de"/>
                <link rel="SHORTCUT ICON" href="{$calcRoot}/images/betterform.ico"/>
                <link rel="ICON" href="{$calcRoot}/images/betterform.ico" type="image/x-icon"/>
                <link rel="stylesheet" type="text/css" href="{$calcRoot}/styles/website.css"/>
                <link href='http://fonts.googleapis.com/css?family=Istok+Web:400,700&amp;subset=latin,latin-ext' rel='stylesheet' type='text/css'/>
                <xsl:copy-of select="//html:head/html:style"/><xsl:text>
</xsl:text>

                <xsl:copy-of select="//html:head/html:script"/>
                <xsl:if test="//html:body/@id='index'">
                    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.min.js" type="text/javascript"></script><xsl:text>
</xsl:text>

                     <!-- Anything Slider optional plugins -->
                     <script src="../js/jquery.easing.1.2.js" type="text/javascript"></script><xsl:text>
</xsl:text>

                     <!-- Anything Slider -->
                     <link href="../styles/anythingslider.css" type="text/css" rel="stylesheet" /><xsl:text>
</xsl:text>

                     <script src="../js/jquery.anythingslider.min.js" type="text/javascript"></script><xsl:text>
</xsl:text>

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
                                    <a href="demo.html">demo</a><span class="menuDevider"> | </span>
                                    <a href="download.html">download</a><span class="menuDevider"> | </span>
                                    <a href="product.html">solutions</a><span class="menuDevider"> | </span>
                                    <a href="support.html">support</a><span class="menuDevider"> | </span>
                                    <a href="whoweare.html">contact</a>
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
                        <!--<img id="shadowTop" src="{$calcRoot}/images/shad_top.jpg" alt=""/>-->
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
                                        <li>Dashboard</li>
                                        <li>XForms Feature Explorer</li>
                                        <li>Registration</li>
                                        <li>OrderList</li>
                                        <li>Contacts</li>
                                    </ul>
                                </div>
                                <div class="linkList">
                                    <div class="listTitle">Solutions</div>
                                    <ul>
                                        <li>Rich Internet Applications</li>
                                        <li>eForms / XForms</li>
                                        <li>XML Applications</li>
                                        <li>Data Mangement</li>
                                        <li>EAI / SOA</li>
                                        <li>References</li>
                                    </ul>
                                </div>
                                <div class="linkList">
                                    <div class="listTitle"><a href="services.html">Services</a></div>
                                    <ul>
                                        <li><a href="services.html#consulting">Consulting</a></li>
                                        <li><a href="services.html#development">Development</a></li>
                                        <li><a href="services.html#training">Training</a></li>
                                        <li><a href="services.html#support">Support</a></li>
                                    </ul>
                                </div>
                                <div class="linkList">
                                    <div class="listTitle">Links</div>
                                    <ul>
                                        <li><a href="http://betterform.wordpress.com" target="_blank">Blog</a></li>
                                        <li><a href="http://betterform.wordpress.com/documentation/" target="_blank">Documenation</a></li>
                                        <li><a href="https://github.com/betterFORM/betterFORM" target="_blank">Sourcecode</a></li>
                                        <li><a href="download.html">Download</a></li>
                                        <li><a href="http://en.wikibooks.org/wiki/XForms" target="_blank">XForms Wikibook</a></li>
                                        <li><a href="http://en.wikibooks.org/wiki/XRX" target="_blank">XRX Wikibook</a></li>
                                    </ul>
                                </div>
                            </div>


                            <div id="bottomMenu">
                                &#169; 2011 betterFORM&#160;&#160; | &#160;&#160;
                                <a href="contact.html">impressum</a>
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

                <script type="text/javascript">
                    var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
                    document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
                </script>
                <script type="text/javascript">
                    try {
                        var pageTracker = _gat._getTracker("UA-15044944-1");
                        pageTracker._trackPageview();
                    } catch(err) {
                    }
                </script>

            </body>
        </html>
    </xsl:template>


</xsl:stylesheet>
