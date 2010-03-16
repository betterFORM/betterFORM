<?xml version="1.0"?>

<!-- A sample XML transformation style sheet for displaying the Subversion
  directory listing that is generated by mod_dav_svn when the "SVNIndexXSLT"
  directive is used. -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0">

	<xsl:output method="html"/>

	<xsl:template match="*"/>

	<xsl:template match="svn">
		<html>
			<head>
				<link REV="made" HREF="mailto:info@betterform.de"/>
				<link rel="SHORTCUT ICON" href="http://betterform.de/images/betterform.ico"/>
				
				<link rel="ICON" href="http://betterform.de/images/betterform.ico" type="image/x-icon"/>
				<link rel="stylesheet" type="text/css" href="http://betterform.de/styles/bf.css"/>
				
				
				<link rel="stylesheet" type="text/css" href="http://betterform.de/resources/styles/betterform-styles.css"/>
				<title>
					<xsl:if test="string-length(index/@name) != 0">
						<xsl:value-of select="index/@name"/>
						<xsl:text>: </xsl:text>
					</xsl:if>
					<xsl:value-of select="index/@path"/>
				</title>
				<link rel="stylesheet" type="text/css" href="/svnindex.css"/>
				<script type="text/javascript">
					function onLoadHandler(){
						for(var i = 0; i &lt; document.links.length; i++){
							if(document.links[i].target == 'tsvn'){
								document.links[i].href = 'tsvn:' + document.links[i].href;
							}
						}
					}
				</script>
				<style type="text/css">
					#content .linkbox{
						background:#488aa6;
						color:white;
						display:block;
						padding:10px;
						-moz-border-radius:10px;
						-webkit-border-radius:10px;
						margin:0 0 25px;
					}
					#content .linkbox a {
					text-decoration:underline;
					}
					#shadowBottom{
						width:760px;
						padding:0;
						margin:0;
						display:block;
						padding:0 0 0 20%;
					}					
				</style>
			</head>
			<body id="svnbrowser" onload="onLoadHandler()">
				<div class="page">					
					<div id="header">
						<div class="pageMarginBox">
							<div class="languages"> </div>
							<div id="logoBar">
								<a href="http://betterform.de/index.html" id="linkLogo" class="link"><img id="logo" src="http://betterform.de/images/logo.png" alt="betterFORM project"/></a>
								
								<div id="topnav">
									<a href="http://betterform.de/index.html">home</a><span class="menuDevider"> | </span>
									<a href="http://betterform.de/demo.xhtml">demo</a><span class="menuDevider"> | </span>
									<a href="http://betterform.de/download.html">download</a><span class="menuDevider"> | </span>
									<a href="http://betterform.de/product.html">product</a><span class="menuDevider"> | </span>
									<a href="http://betterform.de/support.html">support</a><span class="menuDevider"> | </span>
									<a href="http://betterform.de/whoweare.html">who we are</a>
								</div>
							</div>
						</div>
					</div>
					
					<div id="content" class="contact">
						<img id="shadowTop" src="http://betterform.de/images/shad_top.jpg" alt=""/>
						
						<!-- ######################### Content here ################################## -->
						<!-- ######################### Content here ################################## -->
						<!-- ######################### Content here ################################## -->
						<!-- ######################### Content here ################################## -->
						<!-- ######################### Content here ################################## -->
						<div class="pageMarginBox">
							<div class="contentBody">
								<h1>betterFORM Subversion Repository Browser</h1>
								<div class="svn">
									<xsl:apply-templates/>
								</div>
                                <div class="linkbox svncheckout">
                                	<p>
                                		install <a href="http://subversion.apache.org/packages.html" target="_blank">Subversion</a> and execute:
                                		<span><i><b>svn checkout https://betterform.de/svn/betterform<xsl:value-of select="/svn/index/@path"/></b></i></span> 
                                			in your terminal or use your prefered <a href="http://en.wikipedia.org/wiki/Comparison_of_Subversion_clients" target="_blank">
                                			Subversion client</a> to download this revision.
                                	</p>
                                </div>
							</div>
						</div>	
					</div>
					<div id="footer">
						<img id="shadowBottom" src="http://betterform.de/images/shad_bottom.jpg" alt=""/>
						
						<div class="pageMarginBox">
							<span id="bottomMenu">
								&#169; 2010 betterFORM<span class="menuDevider"> | </span>
								<a href="http://betterform.de/index.html">home</a><span class="menuDevider"> | </span>
								<a href="http://betterform.de/demo.xhtml">demo</a><span class="menuDevider"> | </span>
								<a href="http://betterform.de/download.html">download</a><span class="menuDevider"> | </span>
								<a href="http://betterform.de/product.html">product</a><span class="menuDevider"> | </span>
								<a href="http://betterform.de/support.html">support</a><span class="menuDevider"> | </span>
								<a href="http://betterform.de/whoweare.html">who we are</a><span class="menuDevider"> | </span>
								<a href="http://betterform.de/contact.html">contact / impressum</a>
							</span>
						</div>
					</div>
					
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="index">
		<div class="rev">
			<xsl:if test="string-length(@name) != 0">
				<xsl:value-of select="@name"/>
				<xsl:if test="string-length(@rev) != 0">
					<xsl:text> &#8212; </xsl:text>
				</xsl:if>
			</xsl:if>
			<xsl:if test="string-length(@rev) != 0">
				<xsl:text>Revision </xsl:text>
				<xsl:value-of select="@rev"/>
			</xsl:if>
		</div>
		<div class="path">
			<xsl:value-of select="@path"/>
		</div>
		<xsl:apply-templates select="updir"/>
		<xsl:apply-templates select="dir"/>
		<xsl:apply-templates select="file"/>
	</xsl:template>

	<xsl:template match="updir">
		<div class="updir">
			<xsl:text>[</xsl:text>
			<xsl:element name="a">
				<xsl:attribute name="href">..</xsl:attribute>
				<xsl:text>Parent Directory</xsl:text>
			</xsl:element>
			<xsl:text>]</xsl:text>
		</div>
    <!-- xsl:apply-templates/ -->
	</xsl:template>

	<xsl:template match="dir">
		<div class="dir">
<!--
			<xsl:element name="a">
				<xsl:attribute name="title">Checkout: <xsl:value-of select="@href"/></xsl:attribute>
				<xsl:attribute name="style">float:right;clear:both;</xsl:attribute>
				<xsl:attribute name="target">tsvn</xsl:attribute>
				<xsl:attribute name="href">
					<xsl:value-of select="@href"/>
				</xsl:attribute>
				<img src="/menucheckout.ico"/>
			</xsl:element>
-->
			<xsl:element name="a">
				<!--<xsl:attribute name="style">margin-right: 22px</xsl:attribute>-->
				<xsl:attribute name="href">
					<xsl:value-of select="@href"/>
				</xsl:attribute>
				<xsl:value-of select="@name"/>
				<xsl:text>/</xsl:text>
			</xsl:element>
		</div>
    <!-- <xsl:apply-templates/ -->
	</xsl:template>

	<xsl:template match="file">
		<div class="file">
			<xsl:element name="a">
				<xsl:attribute name="href">
					<xsl:value-of select="@href"/>
				</xsl:attribute>
				<xsl:value-of select="@name"/>
			</xsl:element>
		</div>
    <!-- xsl:apply-templates/ -->
	</xsl:template>

</xsl:stylesheet>
