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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	version="2.0" xmlns:ev="http://www.w3.org/2001/xml-events" xmlns:xf="http://www.w3.org/2002/xforms" xmlns:xfts="http://www.w3c.org/MarkUp/Forms/XForms/Test/11"
	xmlns:xs="http://www.w3.org/2001/XMLSchema" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	exclude-result-prefixes="xsl ev xfts">
	
	<xsl:output method="xml" indent="yes" name="xml"/>

	<xsl:template match="/xfts:testSuite/xfts:specChapter[1]">
		<suite name="concat(@chapterName, @chapterTitle)">
			<test-case href="{substring-before(substring-after(xfts:testCaseLink, '../../'), '.xhtml')}.xml"/>
		</suite>
		<xsl:apply-templates/>
	</xsl:template>
	
	<xsl:template match="xfts:testCase">
		<xsl:result-document href="{substring-before(substring-after(xfts:testCaseLink, '../../'), '.xhtml')}.xml" format="xml">
			<test-case xmlns="http://www.w3c.org/MarkUp/Forms/XForms/Test/Runner" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.w3c.org/MarkUp/Forms/XForms/Test/Runner {substring-after(replace(substring-after(xfts:testCaseLink, '../../'), '[^/]+', '..'), '../')}/test-case.xsd">
				<open href="{substring-after(xfts:testCaseLink, '../../')}" />
				<assert-title title="{lower-case(xfts:testCaseName)} {xfts:testCaseDescription}" />
				<fail msg="Test not implemented yet!" />
			</test-case>
		</xsl:result-document>
	</xsl:template>

	<xsl:template match="text()"/>

</xsl:stylesheet>
