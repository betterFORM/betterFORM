<?xml version="1.0" encoding="UTF-8"?>
<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:oxf="http://www.orbeon.com/oxf/processors" xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:xxforms="http://orbeon.org/oxf/xml/xforms" xmlns:saxon="http://saxon.sf.net/">

	<p:param type="output" name="data" />

	<p:processor name="oxf:request">
		<p:input name="config">
			<config stream-type="xs:anyURI">
				<include>/request</include>
			</config>
		</p:input>
		<p:output name="data" id="request" />
	</p:processor>
	
	<p:processor name="oxf:xslt">
		<p:input name="config">
			<xsl:stylesheet version="2.0">
				<xsl:output method="xml" name="xml"/>
	
				<xsl:template match="/request">
				
					<html xmlns='http://www.w3.org/1999/xhtml'>
						<head>
							<title>Results from echo.sh</title>
						</head>
						<body>
							<h1>Form posted data</h1>
							<pre>
								<xsl:for-each select="body[boolean(text())]">
									<xsl:value-of select="saxon:serialize(document(.), 'xml')"></xsl:value-of>
								</xsl:for-each>
							</pre>
							<h1>Environment variables</h1>
							<pre>
								<xsl:for-each select="headers/header">
									<xsl:text>HTTP_</xsl:text>
									<xsl:value-of select="upper-case(name)"/>
									<xsl:text>=</xsl:text>
									<xsl:value-of select="value"/>
									<xsl:text>
</xsl:text>
								</xsl:for-each>
								<xsl:text>QUERY_STRING=</xsl:text>
								<xsl:value-of select="query-string"/>
								<xsl:text>
</xsl:text>
								<xsl:text>REMOTE_ADDR=</xsl:text>
								<xsl:value-of select="remote-addr"/>
								<xsl:text>
</xsl:text>
								<xsl:text>REQUEST_METHOD=</xsl:text>
								<xsl:value-of select="method"/>
								<xsl:text>
</xsl:text>
								<xsl:text>SCRIPT_URI=http://xformstest.org/cgi-bin/echo.sh
SCRIPT_URL=/cgi-bin/echo.sh
</xsl:text>
								<xsl:text>CONTENT_TYPE=</xsl:text>
								<xsl:value-of select="content-type"/>
								<xsl:text>
</xsl:text>
							</pre>
							<pre>
								<xsl:copy-of select="/."></xsl:copy-of>
							</pre>
						</body>
					</html>	
				</xsl:template>			
			</xsl:stylesheet>
			
		</p:input>
		<p:input name="data" href="#request"/>
		<p:output name="data" ref="data" />
	</p:processor>
</p:config>
