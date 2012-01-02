<?xml version="1.0" encoding="UTF-8"?>
<!-- 
Copyright (c) 2010, Nick Van den Bleeken
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, 
are permitted provided that the following conditions are met:

    * Redistributions of source code must retain the above copyright notice, this
      list of conditions and the following disclaimer.
    * Redistributions in binary form must reproduce the above copyright notice, 
      this list of conditions and the following disclaimer in the documentation 
      and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY 
EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES 
OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, 
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 -->
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xforms="http://www.w3.org/2002/xforms"
	xmlns:xhtml="http://www.w3.org/1999/xhtml" xmlns:ext="http://www.myextensions.com/ext"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" exclude-result-prefixes="ext xs">

	<xsl:template match="xhtml:link[@rel = 'stylesheet']">
		<xhtml:link rel="stylesheet" href="/xforms-test-suite/TestSuite11.css"
			type="text/css" />
	</xsl:template>

	<xsl:template match="xforms:*[not(@id) and 
		exists(index-of(('input', 'secret', 'textarea', 'output', 'upload', 'range', 'trigger', 'submit', 'select', 'select1'), local-name(.)))]">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:attribute name="id"><xsl:value-of select="concat('xf-', local-name(.) ,'-', count(preceding::xforms:*[local-name(.) = local-name(current())]) + 1)"/></xsl:attribute>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="xforms:group[not(@id)]">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:attribute name="id">xf-group-<xsl:value-of select="count(preceding::xforms:group) + count(ancestor::xforms:group) + 1"/></xsl:attribute>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	
	<!-- Rewrite uri's (to prevent cross domain scripting problem) -->
	<xsl:template match="xforms:load[@resource and not(xforms:resource)]">
		<xsl:copy>
			<xsl:apply-templates select="@* except @resource" />
			<xsl:attribute name="resource"><xsl:value-of select="ext:map-load-uri(@resource)"/></xsl:attribute>
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>

	<xsl:template match="xforms:load[xforms:resource]">
		<xsl:copy>
			<xsl:apply-templates select="@* except @resource" />
			<xsl:if test="@resource">
				<xsl:attribute name="resource"><xsl:value-of select="ext:map-load-uri(@resource)"/></xsl:attribute>
			</xsl:if>
			<xsl:apply-templates select="child::node() except xforms:resource"/>
			<xforms:resource>
				<xsl:choose>
					<xsl:when test="starts-with(xforms:resource/@value, &quot;'&quot;) and ends-with(xforms:resource/@value, &quot;'&quot;)">
						<xsl:attribute name="value">'<xsl:value-of select="ext:map-load-uri(substring(xforms:resource/@value, 2, string-length(xforms:resource/@value) - 2))"></xsl:value-of>'</xsl:attribute>
					</xsl:when>
					<xsl:when test="xforms:resource/@value"><xsl:attribute name="value"><xsl:copy-of select="xforms:resource/@value"/></xsl:attribute></xsl:when>
				</xsl:choose>
				<xsl:value-of select="ext:map-load-uri(xforms:resource)"></xsl:value-of>
			</xforms:resource>
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="uri">
		<xsl:copy>
			<xsl:value-of select="ext:map-load-uri(text())"></xsl:value-of>		
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="binduri[@uri]">
		<xsl:copy>
			<xsl:apply-templates select="@* except @uri" />
			<xsl:attribute name="uri"><xsl:value-of select="ext:map-load-uri(@uri)"/></xsl:attribute>		
		</xsl:copy>
	</xsl:template>
	
	<xsl:template match="@*|node()">
		<xsl:copy>
			<xsl:apply-templates select="@*" />
			<xsl:apply-templates />
		</xsl:copy>
	</xsl:template>
	
	<xsl:function name="ext:map-load-uri" as="xs:string">
	  <xsl:param name="uri" as="xs:string"/>
	  <xsl:sequence  
	     select="if ($uri = 'http://www.w3.org/TR/xforms11/')
	             then '/xforms-test-suite/xforms11.html'
	             else  if ($uri = 'http://www.w3.org')
	             		then '/xforms-test-suite/www.w3.org.html'
	             		else $uri"/>
	</xsl:function>
		
</xsl:stylesheet>