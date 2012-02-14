<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
                xmlns:webxml="http://java.sun.com/xml/ns/j2ee"
                xmlns="http://java.sun.com/xml/ns/j2ee"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                exclude-result-prefixes="webxml">
    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/webxml:web-app/webxml:description">
        <description>betterFORM Open Source XForms Framework</description>
        <display-name>betterFORM Open Source XForms Framework</display-name>
        <context-param>
            <param-name>betterform.configfile</param-name>
            <param-value>WEB-INF/betterform-config.xml</param-value>
        </context-param>
    </xsl:template>

    <xsl:template match="/webxml:web-app/webxml:display-name"/>

    <xsl:template match="/webxml:web-app/webxml:servlet[last()]">
        <xsl:copy-of select="."/>
        <servlet>
            <servlet-name>error</servlet-name>
            <servlet-class>de.betterform.agent.web.servlet.ErrorServlet</servlet-class>
        </servlet>
    </xsl:template>

    <xsl:template match="/webxml:web-app/webxml:servlet-mapping[webxml:servlet-name/text()='ResourceServlet']">
        <xsl:copy-of select="."/>
        <servlet-mapping>
            <servlet-name>error</servlet-name>
            <url-pattern>/error/*</url-pattern>
        </servlet-mapping>
    </xsl:template>

    <xsl:template match="node()|@*">
        <xsl:copy>
            <xsl:apply-templates select="node()|@*"/>
        </xsl:copy>
    </xsl:template>

</xsl:stylesheet>
