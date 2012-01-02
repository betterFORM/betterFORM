<?xml version="1.0" encoding="UTF-8"?>
<p:config xmlns:p="http://www.orbeon.com/oxf/pipeline"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:oxf="http://www.orbeon.com/oxf/processors" xmlns:xf="http://www.w3.org/2002/xforms"
	xmlns:xxforms="http://orbeon.org/oxf/xml/xforms" xmlns:saxon="http://saxon.sf.net/">

	<p:param type="output" name="data" />

	<!--
		<p:processor name="oxf:request"> <p:input name="config"> <config
		stream-type="xs:anyURI"> <include>/request</include> </config>
		</p:input> <p:output name="data" id="request" /> </p:processor>
	-->

	<p:processor name="oxf:directory-scanner">
		<p:input name="config">
			<config>
				<base-directory>oxf:/apps/xforms-test-suite/test-results
				</base-directory>
				<include>*.xml</include>
			</config>
		</p:input>
		<p:output name="data" id="directory-listing" />
	</p:processor>

	<p:processor name="oxf:xslt">
		<p:input name="config">
			<xsl:stylesheet version="2.0">
				<xsl:output method="xml" name="xml" />
				<xsl:template match="/directory">
					<xsl:copy-of select="doc(concat('file:', replace(@path, '\\', '/'), '/', file[@last-modified-ms = max(../file/@last-modified-ms)][1]/@path))"></xsl:copy-of>
				</xsl:template>
			</xsl:stylesheet>

		</p:input>
		<p:input name="data" href="#directory-listing" />
		<p:output name="data" id="test-results" />
	</p:processor>
	
	<p:processor name="oxf:xslt">
		<p:input name="config" href="generate-report.xsl"/>
		<p:input name="data" href="#test-results" />
		<p:output name="data" ref="data" />
	</p:processor>


	<!--
		<p:processor name="oxf:xslt"> <p:input name="config"> <config
		xsl:version="2.0">
		<url>oxf:/xforms-test-suite/test-results/results-<xsl:value-of
		select="(adjust-dateTime-to-timezone(current-dateTime(),
		xs:dayTimeDuration('PT0H')) - xs:dateTime('1970-01-01T00:00:00')) div
		xs:dayTimeDuration('PT0.001S')"/>.xml</url> </config> </p:input>

		<p:input name="data" href="#test-results"/> <p:output name="data"
		id="test-results-url" /> </p:processor> <p:processor
		name="oxf:url-serializer"> <p:input name="config"
		href="#test-results-url"/> <p:input name="data" href="#test-results"
		/> </p:processor> <p:processor name="oxf:identity"> <p:input
		name="data" href="#test-results"/> <p:output name="data" ref="data" />
		</p:processor>
	-->

</p:config>
