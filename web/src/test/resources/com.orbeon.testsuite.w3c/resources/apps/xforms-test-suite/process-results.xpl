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
					<results>
						<xsl:apply-templates />
					</results>
				</xsl:template>
				
				<xsl:template match="parameter[starts-with(name, 'testTable.')]">
					<xsl:variable name="test-data" select="saxon:parse(replace(value, '&amp;nbsp;', '&amp;#160;'))"/>
					<xsl:variable name="test-header-tr" select="$test-data/div/table/tbody/tr[1]"/>
					<xsl:variable name="test-name" select="normalize-space($test-header-tr)"/>
					<test name="{$test-name}" pass="{if ($test-data/div/table/tbody/tr[last()][td[1] != 'fail' or  normalize-space(td[2]) != 'Test not implemented yet!']) then contains($test-header-tr/@class, 'status_passed') else 'unknown'}" chapter="{substring($test-name,1,1)}"/>
				</xsl:template>
				
				<xsl:template match="text()"/>
			</xsl:stylesheet>
			
		</p:input>
		
		<p:input name="data" href="#request"/>
		<p:output name="data" id="test-results" />
	</p:processor>
	
	<p:processor name="oxf:xslt">
		<p:input name="config">
			<config xsl:version="2.0">
				<url>oxf:/apps/xforms-test-suite/test-results/results-<xsl:value-of select="(adjust-dateTime-to-timezone(current-dateTime(), xs:dayTimeDuration('PT0H')) - xs:dateTime('1970-01-01T00:00:00')) div xs:dayTimeDuration('PT0.001S')"/>.xml</url>
			</config>
			
		</p:input>
		
		<p:input name="data" href="#test-results"/>
		<p:output name="data" id="test-results-url" />
	</p:processor>
	
	<p:processor name="oxf:url-serializer">
		<p:input name="config" href="#test-results-url"/>
		<p:input name="data" href="#test-results" />
	</p:processor>
	
	<p:processor name="oxf:identity">
		<p:input name="data" href="#test-results"/>
		<p:output name="data" ref="data" />
	</p:processor>

</p:config>
