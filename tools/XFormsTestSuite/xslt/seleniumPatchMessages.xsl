<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<xsl:stylesheet version="2.0"
    xmlns:xhtml="http://www.w3.org/1999/xhtml"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xforms="http://www.w3.org/2002/xforms"
    xmlns:ev="http://www.w3.org/2001/xml-events"
    exclude-result-prefixes="xsl">

    <!-- Copyright 2009 - Joern Turner, Lars Windauer, Tobias Krebs, Fabian Otto
         Licensed under the terms of BSD and Apache 2 Licenses -->

    <xsl:param name="filename"></xsl:param>
    <xsl:param name="filedir">.</xsl:param>

    <!--
    Simple Stylesheet to modify W3C Conformance TestSuite XForms documents.
    <xf:messages> are replaced with JavaScript so they can be processed with automatic Selenium Test.

    -->
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="/">
        <xsl:message>
         Current file is <xsl:value-of select="$filename"/> in directory <xsl:value-of select="$filedir"/>.
        </xsl:message>
        <xsl:copy>
            <xsl:apply-templates/>
        </xsl:copy>
    </xsl:template>

    <xsl:template match="@*|node()">
        <xsl:copy>
            <xsl:apply-templates select="@*"/>
            <xsl:apply-templates />
        </xsl:copy>
    </xsl:template>
    
    <xsl:template match="xhtml:head">
        <xsl:copy>

            <xhtml:script type="text/javascript">
            function addMessage(message) {
                <!-- Debug -->
                <!-- alert("addMessage called!"); -->
                <!-- get <div> which functions as log for messages -->
                var log = document.getElementById('messageLog');

                <!-- if lod does not exist, create it -->
                if (!log) {
                    log = document.createElement('div');
                    log.id = 'messageLog';
                    document.body.appendChild(log);
                }
                <!-- create new div-Element for message -->
                var messageDiv = document.createElement('message');
                messageDiv.id = 'message' + ( countMessages(log) + 1);

                <!-- set messageText -->
                var messageText = document.createTextNode(message);
                <!-- append messageText to messageDiv -->
                messageDiv.appendChild(messageText);
                <!-- append message to log -->
                log.appendChild(messageDiv);
            }

            <!--count messages in log -->
            function countMessages(log) {
                var logMessagesCount = log.getElementsByTagName('message').length;
                return logMessagesCount;
            }
        </xhtml:script>
        <xsl:apply-templates/>

        </xsl:copy>
    </xsl:template>

    <xsl:template match="xforms:message" priority="50">
        <xforms:action>
            <xsl:apply-templates mode="messages" select="@*"/>
            <script type="text/javascript">
                addMessage('<xsl:value-of select="."/>');
            </script>
        </xforms:action>
    </xsl:template>


     <xsl:template match="xforms:action/xforms:message" priority="50">
         <xsl:message>Found nested xforms:message</xsl:message>
              <script type="text/javascript">
                addMessage('<xsl:value-of select="."/>');
            </script>
     </xsl:template>

    <xsl:template match="@ev:observer" mode="messages" priority="50">
            <xsl:message>Found Attribute: @ev:observer</xsl:message>
            <xsl:attribute name="ev:observer" select="."/>
    </xsl:template>

     <xsl:template match="@ev:event" mode="messages" priority="50">
            <xsl:message>Found Attribute: @ev:event</xsl:message>
            <xsl:attribute name="ev:event" select="."/>
    </xsl:template>

    <xsl:template match="@level" mode="messages" priority="50">
            <xsl:message>Found Attribute: @level</xsl:message>
    </xsl:template>

     <xsl:template match="*" mode="messages" priority="50">
           <xsl:message>Found new Attribute: <xsl:value-of select="local-name(.)"/></xsl:message>
    </xsl:template>

    <!-- 1. auf Head-matchen Script includen -->
    <!-- 2. auf xf:message matchen -> mit script aufruf  ersetzten -->

</xsl:stylesheet>
