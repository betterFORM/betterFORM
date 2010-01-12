<?xml version="1.0" encoding="UTF-8"?>
<!-- Stylesheet that transforms a W3C Schema into an XForms document-->
<xsl:stylesheet version="1.0" xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events"
    xmlns:fo="http://www.w3.org/1999/XSL/Format"
    xmlns:exslt="http://exslt.org/common"
    extension-element-prefixes="exslt" >

    <xsl:output method="xml" encoding="UTF-8" indent="yes"/>
	<xsl:param name="targetElement"/>
	<!-- the root element of the instance associated with the form document -->
	<xsl:param name="targetAction"/>
	<!-- the form data will be posted to this URI -->
	<!--currently this transform only supports the 'post' method, though this could be entirely configurable-->
	<!--Building XPATH expressions to the locations of elements for use in naming the controls on the generated UI and for constructing the result instance from the UI might not work. Consider multiple cardinality for an Address Element. If there are two address elements, then the PostalCode element inside each will be //Address/PostalCode. Using the position() function to get control names like "//Address[1]/PostalCode at transform time won't work, because there's no way to tell at transform time how many addresses the user will input. However, if whatever mechanism that creates new input fields for "Address" runs its own transform, then perhaps it can keep track of positions and rename the input controls at run-time? This is getting complicated.
	Unless we decide not to run a generic transform?
-->
	<xsl:variable name="imports">
		<xsl:apply-templates select="/xs:schema/xs:import"/>
	</xsl:variable>

	<!-- get info about the schema being transformed -->
	<xsl:variable name="namespace" select="/xs:schema/@targetNamespace"/>

    <!-- Ok, here seems to be an assumption ... -->
	<xsl:variable name="prefix">
		<xsl:value-of select="/xs:schema/xs:annotation/xs:appinfo/Prefix"/>
	</xsl:variable>

	<xsl:template match="xs:import">
		<xsl:element name="schema">
			<!-- the prefix used for the imported schema must be in an xs:annotation/xs:appinfo/Prefix element
            inside the xs:import element-->
			<xsl:attribute name="prefix"><xsl:value-of select="xs:annotation/xs:appinfo/Prefix"/></xsl:attribute>
			<xsl:attribute name="urn"><xsl:value-of select="@namespace"/></xsl:attribute>
			<xsl:copy-of select="document(@schemaLocation)"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="xs:schema">
		<!--we know that all our elements are global, so there's no need to dig into the tree to find our target
        element we wish to transform. If the elements were nested, a la 'venetian blind', we'd have to write some
        logic like that in the xml.com article for finding our target.-->

		<!--Or just allow searching for global 'document' elements in the venetian blind schema. Why would you
        need to build a form from a nested local element?-->

		<!--Build the XFORMS model element first -->
		<xsl:element name="html" namespace="http://www.w3.org/1999/xhtml">
			<!--<html xmlns="http://www.w3.org/1999/xhtml">-->
			<xsl:element name="head" namespace="http://www.w3.org/1999/xhtml">
				<xsl:element name="xforms:model">
					<xsl:attribute name="id"><xsl:value-of select="concat($targetElement,'Model')"/></xsl:attribute>
					<xsl:element name="xforms:submission">
						<xsl:attribute name="id">formSubmit</xsl:attribute>
						<xsl:attribute name="method">post</xsl:attribute>
						<xsl:attribute name="action"><xsl:value-of select="$targetAction"/></xsl:attribute>
					</xsl:element>
					<!-- should we put an xforms:schema element here? would need documentation inside the schema giving its name-->
					<!-- how about xforms:bind elements? -->
					<!-- How about a link to an instance instead of embedding the instance here?-->
                    <!-- todo: add namespace decl -->
					<xsl:element name="xforms:instance">
						<xsl:attribute name="id"><xsl:value-of select="concat($targetElement,'Instance')"/></xsl:attribute>
						<xsl:apply-templates select="xs:element[@name=$targetElement]" mode="buildModel"/>
					</xsl:element>
				</xsl:element>
			</xsl:element>
			<xsl:element name="body" namespace="http://www.w3.org/1999/xhtml">
				<xsl:call-template name="HackNamespaces"/>
				<xsl:apply-templates select="xs:element[@name=$targetElement]" mode="buildUI">
					<xsl:with-param name="currXPath" select="@name"/>
				</xsl:apply-templates>
				<xsl:element name="xforms:submit" namespace="http://www.w3.org/2002/xforms">
					<!--<xsl:attribute name="name">Submit</xsl:attribute>-->
					<xsl:attribute name="submission">formSubmit</xsl:attribute>
					<xsl:element name="xforms:label">Submit</xsl:element>
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<!--</html>-->
	</xsl:template>

    <xsl:template match="*"/>

    <!--****************************************-->
	<!-- build the XForms Model templates-->
	<!--****************************************-->
	<!--<xsl:template match="xs:complexType[@abstract='true']" priority="1000" mode="buildModel">
		<xsl:value-of select="concat($prefix,':',@name)"/>
		<xsl:apply-templates select="/xs:schema/xs:complexType[descendant::xs:extension/@base = concat($prefix,':',@name)]" mode="buildModel"/>
	</xsl:template>-->
	<xsl:template match="xs:complexType|xs:complexContent|xs:sequence|xs:all|xs:group[@name]|xs:simpleContent" mode="buildModel">
		<xsl:apply-templates select="*" mode="buildModel"/>
	</xsl:template>

    <xsl:template match="xs:extension" mode="buildModel">
		<xsl:variable name="typeNS" select="substring-before(@base,':')"/>
		<xsl:variable name="typeName" select="substring-after(@base,':')"/>
		<xsl:call-template name="Attributes">
			<xsl:with-param name="typeName" select="$typeName"/>
			<xsl:with-param name="typeNS" select="$typeNS"/>
		</xsl:call-template>
		<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:complexType[@name=$typeName]" mode="buildModel"/>
		<xsl:apply-templates select="/xs:schema/xs:complexType[@name=$typeName]" mode="buildModel"/>
		<xsl:apply-templates select="*" mode="buildModel"/>
	</xsl:template>

    <xsl:template match="xs:element[@ref]" mode="buildModel">
		<xsl:variable name="elementNSPrefix" select="substring-before(@ref, ':')"/>
		<xsl:variable name="elementName" select="substring-after(@ref,':')"/>
		<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$elementNSPrefix]/xs:schema/xs:element[@name = $elementName]" mode="buildModel"/>
		<xsl:apply-templates select="/xs:schema/xs:element[@name=$elementName]" mode="buildModel"/>
	</xsl:template>

    <!--need to do something special for the root element. need namespace declarations for all the namespaces imported by the schema.-->
	<!-- So we should check for matching on the element whose name = $targetName -->
	<xsl:template match="xs:element[@name]" mode="buildModel">
		<xsl:variable name="typeNS" select="substring-before(@type,':')"/>
		<xsl:variable name="typeName" select="substring-after(@type,':')"/>
		<xsl:element name="{concat(ancestor::xs:schema/xs:annotation/xs:appinfo/Prefix,':',@name)}" namespace="{ancestor::xs:schema/@targetNamespace}">
			<!-- xsl:attribute must be declared immediately after an xsl:element, so i have to call the template to get the attributes here. i don't really like doing this here - ideally attributes would be found when matching on the complexType, but the processor complains.-->
			<xsl:call-template name="Attributes">
				<xsl:with-param name="typeName" select="$typeName"/>
				<xsl:with-param name="typeNS" select="$typeNS"/>
			</xsl:call-template>
			<!--<xsl:choose>-->
			<!--<xsl:when test="$typeNS = 'cct'">-->
			<!--do nothing-->
			<!--</xsl:when>-->
			<!--<xsl:when test="contains($typeNS, 'xs')">-->
			<!--  do nothing for xs types. cant go any deeper -->
			<!--</xsl:when>-->
			<!--<xsl:otherwise>-->
			<!--first check if the type is in an imported schema, then check if its local -->
			<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:complexType[@name=$typeName]" mode="buildModel"/>
			<xsl:apply-templates select="/xs:schema/xs:complexType[@name = $typeName]" mode="buildModel"/>
			<!--</xsl:otherwise>-->
			<!--</xsl:choose>-->
		</xsl:element>
	</xsl:template>
	<xsl:template match="*" mode="buildModel"/>
	<!--****************************************-->
	<!--END XForms Model templates-->
	<!--****************************************-->

    <!--****************************************-->
	<!-- build the XForms UI templates-->
	<!--****************************************-->
	<xsl:template match="xs:complexType" mode="buildUI">
		<xsl:param name="currXPath"/>
		<xsl:param name="beingExtended"/>
		<xsl:param name="refMaxOccurs"/>
		<xsl:param name="maxOccurs"/>
		<xsl:param name="label"/>
		<!-- build a group with a ref to the currXPath -->
		<!--<p>
			<xsl:value-of select="@name"/>
		</p>-->
		<!-- if this complex type is being matched on as a result of being extended, we don't want to make another group. To accomplish this, this template will be called with the parameter 'beingExtended'. if this maps to 'true', then we just call templates without forming a group.-->
		<!-- will need to add logic to handle repeating elements. in the case of a repeating element, the stylesheet should create an xforms:repeat and not an xforms:group -->
		<xsl:choose>
			<xsl:when test="$beingExtended='true'">
				<xsl:apply-templates select="*" mode="buildUI">
					<xsl:with-param name="currXPath" select="$currXPath"/>
					<xsl:with-param name="label" select="$label"/>
				</xsl:apply-templates>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="number($maxOccurs)>1">
						<xsl:call-template name="BuildRepeatControl">
							<xsl:with-param name="currNode" select="."/>
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="number($refMaxOccurs)>1">
						<xsl:call-template name="BuildRepeatControl">
							<xsl:with-param name="currNode" select="."/>
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$maxOccurs='unbounded'">
						<xsl:call-template name="BuildRepeatControl">
							<xsl:with-param name="currNode" select="."/>
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$refMaxOccurs='unbounded'">
						<xsl:call-template name="BuildRepeatControl">
							<xsl:with-param name="currNode" select="."/>
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:element name="xforms:group">
							<xsl:attribute name="ref"><xsl:value-of select="$currXPath"/></xsl:attribute>
							<xsl:element name="xforms:label">
								<xsl:value-of select="$label"/>
							</xsl:element>
							<xsl:apply-templates select="*" mode="buildUI">
								<xsl:with-param name="currXPath" select="$currXPath"/>
								<xsl:with-param name="label" select="$label"/>
							</xsl:apply-templates>
						</xsl:element>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

    <xsl:template match="xs:extension" mode="buildUI">
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
		<xsl:variable name="typeNS" select="substring-before(@base,':')"/>
		<xsl:variable name="typeName" select="substring-after(@base,':')"/>
		<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:complexType[@name=$typeName]" mode="buildUI">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="beingExtended">true</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="/xs:schema/xs:complexType[@name=$typeName]" mode="buildUl">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="beingExtended">true</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:simpleType[@name=$typeName]" mode="buildUI">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="beingExtended">true</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="/xs:schema/xs:simpleType[@name=$typeName]" mode="buildUl">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="beingExtended">true</xsl:with-param>
		</xsl:apply-templates>
		<xsl:apply-templates select="*" mode="buildUI">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="label" select="$label"/>
		</xsl:apply-templates>
	</xsl:template>

    <xsl:template match="xs:simpleType" mode="buildUI">
		<!-- this also needs to check whether the element of this type is repeating. in the case of a repeating element, the stylesheet should create a xforms:repeat or an xforms:select-->
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
		<xsl:param name="beingExtended"/>
		<xsl:param name="elementName"/>
		<xsl:param name="refMaxOccurs"/>
		<xsl:param name="maxOccurs"/>
		<!--<xsl:param name="repeating"/>-->
		<xsl:choose>
			<xsl:when test="xs:restriction/xs:enumeration">
				<!-- here it needs to differentiate select vs select1 based on the refMaxOccurs and maxOccurs parameters-->
				<!--
				<xsl:element name="xforms:select1">
					<xsl:attribute name="ref"><xsl:value-of select="$currXPath"/></xsl:attribute>
					<xsl:element name="xforms:label">
						<xsl:value-of select="$label"/>
					</xsl:element>
					<xsl:for-each select="xs:restriction/xs:enumeration">
						<xsl:element name="xforms:item">
							<xsl:element name="xforms:caption">
								<xsl:value-of select="xs:annotation/xs:appinfo"/>
							</xsl:element>
							<xsl:element name="xforms:value">
								<xsl:value-of select="@value"/>
							</xsl:element>
						</xsl:element>
					</xsl:for-each>
				</xsl:element>-->
				<xsl:choose>
					<xsl:when test="number($refMaxOccurs)>1">
						<xsl:call-template name="BuildSelectControl">
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
							<xsl:with-param name="currentNode" select="."/>
							<xsl:with-param name="select">select</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="number($maxOccurs)>1">
						<xsl:call-template name="BuildSelectControl">
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
							<xsl:with-param name="currentNode" select="."/>
							<xsl:with-param name="select">select</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$refMaxOccurs = 'unbounded'">
						<xsl:call-template name="BuildSelectControl">
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
							<xsl:with-param name="currentNode" select="."/>
							<xsl:with-param name="select">select</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$maxOccurs='unbounded'">
						<xsl:call-template name="BuildSelectControl">
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
							<xsl:with-param name="currentNode" select="."/>
							<xsl:with-param name="select">select</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="BuildSelectControl">
							<xsl:with-param name="currXPath" select="$currXPath"/>
							<xsl:with-param name="label" select="$label"/>
							<xsl:with-param name="currentNode" select="."/>
							<xsl:with-param name="select">select1</xsl:with-param>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="BuildSingleInputControl">
					<xsl:with-param name="currXPath" select="$currXPath"/>
					<xsl:with-param name="label" select="$label"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

    <xsl:template match="xs:complexContent|xs:simpleContent|xs:sequence|xs:all|xs:group[@name]" mode="buildUI">
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
		<xsl:apply-templates select="*" mode="buildUI">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="label" select="$label"/>
		</xsl:apply-templates>
	</xsl:template>

    <xsl:template match="xs:attribute" mode="buildUI">
		<xsl:param name="currXPath"/>
		<xsl:variable name="xpath" select="concat($currXPath,'/@',@name)"/>
		<xsl:variable name="label" select="xs:annotation/xs:appinfo/Label"/>
		<xsl:variable name="attributeName" select="@name"/>
		<xsl:variable name="typeNS" select="substring-before(@type,':')"/>
		<xsl:variable name="typeName" select="substring-after(@type,':')"/>
		<xsl:choose>
			<xsl:when test="@fixed">
				<!-- when an attribute has a fixed value, don't create an input control-->
			</xsl:when>
			<!-- add processing for creating select1 control for attributes of type xs:boolean -->
			<xsl:when test="contains(@type, 'boolean')">
				<xsl:call-template name="BuildBooleanSelect">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="label" select="$label"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="contains(@type,'xs')">
				<xsl:call-template name="BuildSingleInputControl">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="label" select="$label"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<!-- process the attribute's simple type here. -->
				<xsl:apply-templates select="/xs:schema/xs:simpleType[@name=$typeName]" mode="buildUI">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="attributeName" select="$attributeName"/>
					<xsl:with-param name="label" select="$label"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:simpleType[@name=$typeName]" mode="buildUI">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="attributeName" select="$attributeName"/>
					<xsl:with-param name="label" select="$label"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

    <xsl:template match="xs:element[@ref]" mode="buildUI">
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
		<!--<xsl:variable name="refRepeating" select="@maxOccurs"/>-->
		<xsl:variable name="elementNSPrefix" select="substring-before(@ref, ':')"/>
		<xsl:variable name="elementName" select="substring-after(@ref,':')"/>
		<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$elementNSPrefix]/xs:schema/xs:element[@name = $elementName]" mode="buildUI">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="refMaxOccurs" select="@maxOccurs"/>
		</xsl:apply-templates>
		<xsl:apply-templates select="/xs:schema/xs:element[@name=$elementName]" mode="buildUI">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="label" select="$label"/>
			<xsl:with-param name="refMaxOccurs" select="@maxOccurs"/>
		</xsl:apply-templates>
	</xsl:template>

    <xsl:template match="xs:element[@name]" mode="buildUI">
		<!-- get the type
			if 'xs', do control
		-->
		<xsl:param name="currXPath"/>
		<!--<xsl:param name="refRepeating"/>-->
		<xsl:param name="refMaxOccurs"/>
		<xsl:param name="refMinOccurs"/>
		<!--<xsl:variable name="repeating"/>-->
		<xsl:variable name="maxOccurs" select="@maxOccurs"/>
		<xsl:variable name="typeNS" select="substring-before(@type,':')"/>
		<xsl:variable name="typeName" select="substring-after(@type,':')"/>
		<xsl:variable name="elementName" select="@name"/>
		<xsl:variable name="label" select="xs:annotation/xs:appinfo/Label"/>
		<xsl:variable name="xpath" select="concat($currXPath,'/',ancestor::xs:schema/xs:annotation/xs:appinfo/Prefix,':',@name)"/>
		<!-- if the element is repeating, we need to create an xforms:repeat instead of an xforms:group when processing the type -->
		<xsl:choose>
			<xsl:when test="contains($typeNS, 'xs')">
				<xsl:choose>
					<!--need to construct repeat containers for repeating elements with xs types-->
					<xsl:when test="number($refMaxOccurs) > 1">
						<xsl:call-template name="BuildSimpleRepeat">
							<xsl:with-param name="currXPath" select="$xpath"/>
							<xsl:with-param name="elementName" select="$elementName"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="number($maxOccurs) > 1">
						<xsl:call-template name="BuildSimpleRepeat">
							<xsl:with-param name="currXPath" select="$xpath"/>
							<xsl:with-param name="elementName" select="$elementName"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$refMaxOccurs='unbounded'">
						<xsl:call-template name="BuildSimpleRepeat">
							<xsl:with-param name="currXPath" select="$xpath"/>
							<xsl:with-param name="elementName" select="$elementName"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:when test="$maxOccurs='unbounded'">
						<xsl:call-template name="BuildSimpleRepeat">
							<xsl:with-param name="currXPath" select="$xpath"/>
							<xsl:with-param name="elementName" select="$elementName"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="BuildSingleInputControl">
							<xsl:with-param name="currXPath" select="$xpath"/>
							<xsl:with-param name="label" select="$label"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:when>
			<!--<xsl:when test="$typeNS = 'cct'">
				<xsl:call-template name="BuildInputControl">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="caption" select="xs:annotation/xs:appinfo/Caption"/>
				</xsl:call-template>
			</xsl:when>-->
			<xsl:otherwise>
				<!-- process the type
					- complexTypes
					- simpleType
				-->
				<!--<p><xsl:value-of select="@name"/><xsl:text>: </xsl:text><xsl:value-of select="$xpath"/></p>-->
				<!--<xsl:call-template name="AttributesUI" mode="buildUI">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="typeNS" select="$typeNS"/>
					<xsl:with-param name="typeName" select="$typeName"/>
				</xsl:call-template>-->
				<xsl:apply-templates select="/xs:schema/xs:complexType[@name=$typeName]" mode="buildUI">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="label" select="$label"/>
					<xsl:with-param name="refMaxOccurs" select="$refMaxOccurs"/>
					<xsl:with-param name="maxOccurs" select="$maxOccurs"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:complexType[@name=$typeName]" mode="buildUI">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="label" select="$label"/>
					<xsl:with-param name="refMaxOccurs" select="$refMaxOccurs"/>
					<xsl:with-param name="maxOccurs" select="$maxOccurs"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="/xs:schema/xs:simpleType[@name=$typeName]" mode="buildUI">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="elementName" select="$elementName"/>
					<xsl:with-param name="label" select="$label"/>
					<xsl:with-param name="refMaxOccurs" select="$refMaxOccurs"/>
					<xsl:with-param name="maxOccurs" select="$maxOccurs"/>
				</xsl:apply-templates>
				<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:simpleType[@name=$typeName]" mode="buildUI">
					<xsl:with-param name="currXPath" select="$xpath"/>
					<xsl:with-param name="elementName" select="$elementName"/>
					<xsl:with-param name="label" select="$label"/>
					<xsl:with-param name="refMaxOccurs" select="$refMaxOccurs"/>
					<xsl:with-param name="maxOccurs" select="$maxOccurs"/>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

    <xsl:template match="xs:restriction" mode="buildUI">
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
		<xsl:variable name="typeNS" select="substring-before(@base,':')"/>
		<xsl:variable name="typeName" select="substring-after(@base,':')"/>
		<xsl:choose>
			<xsl:when test="*">
				<!--do nothing now, only looking for empty restrictions-->
			</xsl:when>
			<xsl:otherwise>
				<!-- this template will only be applied as a result of an xs:restriction inside a complexType. A complexType cannot restrict a simpleType.
						The program will only look for complexTypes to process. -->
				<xsl:apply-templates select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:complexType[@name=$typeName]" mode="buildUI">
					<xsl:with-param name="currXPath" select="$currXPath"/>
					<xsl:with-param name="label" select="$label"/>
					<xsl:with-param name="beingExtended">true</xsl:with-param>
				</xsl:apply-templates>
				<xsl:apply-templates select="/xs:schema/xs:complexType[@name=$typeName]" mode="buildUl">
					<xsl:with-param name="currXPath" select="$currXPath"/>
					<xsl:with-param name="label" select="$label"/>
					<xsl:with-param name="beingExtended">true</xsl:with-param>
				</xsl:apply-templates>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="*" mode="buildUI"/>

	<!-- named templates-->
	<xsl:template name="Attributes">
		<xsl:param name="typeNS"/>
		<xsl:param name="typeName"/>
		<xsl:for-each select="/xs:schema/xs:complexType[@name=$typeName]//xs:attribute">
			<xsl:if test="not(@fixed)">
				<!-- if an attribute is fixed, there's no need to put it into the model-->
				<xsl:attribute name="{@name}"/>
			</xsl:if>
		</xsl:for-each>
		<xsl:for-each select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:complexType[@name=$typeName]//xs:attribute">
			<xsl:if test="not(@fixed)">
				<!-- if an attribute is fixed, there's no need to put it into the model-->
				<xsl:attribute name="{@name}"/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>
	<!--
	<xsl:template name="AttributesUI" mode="buildUI">
		<xsl:param name="currXPath"/>
		<xsl:param name="typeNS"/>
		<xsl:param name="typeName"/>
		<xsl:for-each select="/xs:schema/xs:complexType[@name=$typeName]//xs:attribute">
			<xsl:element name="xforms:input">
				<xsl:attribute name="ref"><xsl:value-of select="concat($currXPath,'/@',@name)"/></xsl:attribute>
				<xsl:element name="xforms:caption">
					<xsl:value-of select="@name"/>
				</xsl:element>
			</xsl:element>
		</xsl:for-each>
		<xsl:for-each select="exslt:node-set($imports)/schema[@prefix=$typeNS]/xs:schema/xs:complexType[@name=$typeName]//xs:attribute">
			<xsl:attribute name="{@name}"/>
		</xsl:for-each>
	</xsl:template>-->

	<xsl:template name="BuildSingleInputControl">
		<!--build an xforms:input control that maps (with ref or with nodeset?) to the currXPath, with the caption (and bound to the type?)-->
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
		<xsl:param name="type"/>
		<xsl:element name="xforms:input">
			<xsl:attribute name="ref"><xsl:value-of select="$currXPath"/></xsl:attribute>
			<xsl:element name="xforms:label">
				<xsl:value-of select="$label"/>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildSimpleRepeat">
		<!--build a xforms:repeat container for simple xs type content-->
		<xsl:param name="currXPath"/>
		<xsl:param name="elementName"/>
		<xsl:param name="label"/>
		<xsl:variable name="repeatID" select="concat($currXPath,'Repeat')"/>
		<xsl:element name="xforms:repeat">
			<xsl:attribute name="nodeset"><xsl:value-of select="$currXPath"/></xsl:attribute>
			<xsl:attribute name="id"><xsl:value-of select="$repeatID"/></xsl:attribute>
			<xsl:call-template name="BuildSingleInputControl">
				<xsl:with-param name="currXPath" select="$currXPath"/>
				<xsl:with-param name="label" select="$label"/>
			</xsl:call-template>
		</xsl:element>
		<xsl:call-template name="BuildRepeatButtons">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="repeatID" select="$repeatID"/>
			<xsl:with-param name="label" select="$label"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="HackNamespaces">
		<!-- we have to get the namespaces used by the schemas into the body element. This is an ugly way to do it,
        following a suggestion made by Michael Kay on a newsgroup. You create a dummy attribute you don't want to
        use on the body, making sure that it's prefix is the one used by the schema. You also assign the dummy
        attribute to the correct namespace URI. This will force the transformer to put an xmlns: declaration in
        the body element for all your imported elements. In XSL 2.0, it is claimed, there will be an xsl:namespace
        tag, which will be a better solution to this problem. You can find Michael Kay's post here:
        http://sources.redhat.com/ml/xsl-list/2001-09/msg01204.html -->
        <xsl:variable name="prefix" select="'po'"/>
		<xsl:attribute name="{concat($prefix,':sims')}" namespace="{$namespace}"/>
		<xsl:for-each select="exslt:node-set($imports)/schema">
			<xsl:attribute name="{concat(@prefix,':sims')}" namespace="{@urn}"/>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="BuildSelectControl">
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
		<xsl:param name="currentNode"/>
		<xsl:param name="select"/>
		<xsl:element name="{concat('xforms:',$select)}">
			<xsl:attribute name="ref"><xsl:value-of select="$currXPath"/></xsl:attribute>
			<xsl:element name="xforms:label">
				<xsl:value-of select="$label"/>
			</xsl:element>
			<xsl:for-each select="$currentNode/xs:restriction/xs:enumeration">
				<xsl:element name="xforms:item">
					<xsl:element name="xforms:caption">
						<xsl:value-of select="xs:annotation/xs:appinfo"/>
					</xsl:element>
					<xsl:element name="xforms:value">
						<xsl:value-of select="@value"/>
					</xsl:element>
				</xsl:element>
			</xsl:for-each>
		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildRepeatControl">
		<xsl:param name="currNode"/>
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
		<xsl:variable name="repeatID" select="concat($currXPath,'Repeat')"/>
		<xsl:element name="xforms:repeat">
			<xsl:attribute name="nodeset"><xsl:value-of select="$currXPath"/></xsl:attribute>
			<xsl:attribute name="id"><xsl:value-of select="$repeatID"/></xsl:attribute>
			<xsl:element name="xforms:label">
				<xsl:value-of select="$label"/>
			</xsl:element>
			<xsl:apply-templates select="$currNode/*" mode="buildUI">
				<xsl:with-param name="currXPath" select="$currXPath"/>
				<xsl:with-param name="label" select="$label"/>
			</xsl:apply-templates>
		</xsl:element>
		<xsl:call-template name="BuildRepeatButtons">
			<xsl:with-param name="currXPath" select="$currXPath"/>
			<xsl:with-param name="repeatID" select="$repeatID"/>
			<xsl:with-param name="label" select="$label"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="BuildRepeatButtons">
		<xsl:param name="currXPath"/>
		<xsl:param name="repeatID"/>
		<xsl:param name="label"/>
		<xsl:element name="xforms:trigger" namespace="http://www.w3.org/2002/xforms">
			<xsl:element name="xforms:label">
				<xsl:value-of select="concat('Insert New ',$label)"/>
			</xsl:element>
			<xsl:element name="xforms:action">
				<xsl:attribute name="ev:event"><xsl:text>xforms-activate</xsl:text></xsl:attribute>
				<xsl:element name="xforms:insert">
					<xsl:attribute name="nodeset"><xsl:value-of select="$currXPath"/></xsl:attribute>
					<xsl:attribute name="at"><xsl:text>xforms:index('</xsl:text><xsl:value-of select="$repeatID"/>')</xsl:attribute>
					<xsl:attribute name="position"><xsl:text>after</xsl:text></xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
		<xsl:element name="xforms:trigger" namespace="http://www.w3.org/2002/xforms">
			<xsl:element name="xforms:label">
				<xsl:value-of select="concat('Delete ',$label)"/>
			</xsl:element>
			<xsl:element name="xforms:action">
				<xsl:attribute name="ev:event"><xsl:text>xforms-activate</xsl:text></xsl:attribute>
				<xsl:element name="xforms:delete">
					<xsl:attribute name="nodeset"><xsl:value-of select="$currXPath"/></xsl:attribute>
					<xsl:attribute name="at"><xsl:text>xforms:index('</xsl:text><xsl:value-of select="$repeatID"/><xsl:text>')</xsl:text></xsl:attribute>
					<xsl:attribute name="position"><xsl:text>after</xsl:text></xsl:attribute>
				</xsl:element>
			</xsl:element>
		</xsl:element>
	</xsl:template>

	<xsl:template name="BuildBooleanSelect">
		<xsl:param name="currXPath"/>
		<xsl:param name="label"/>
			<xsl:element name="xforms:select1">
				<xsl:attribute name="ref"><xsl:value-of select="$currXPath"/></xsl:attribute>
				<xsl:element name="xforms:label"><xsl:value-of select="$label"/></xsl:element>
				<xsl:element name="xforms:choices">
					<xsl:element name="xforms:item">
						<xsl:element name="xforms:caption">Yes</xsl:element>
						<xsl:element name="xforms:value">true</xsl:element>
					</xsl:element>
					<xsl:element name="xforms:item">
						<xsl:element name="xforms:caption">No</xsl:element>
						<xsl:element name="xforms:value">false</xsl:element>
					</xsl:element>
				</xsl:element>
			</xsl:element>
	</xsl:template>

</xsl:stylesheet>
