<?xml version="1.0" encoding="UTF-8"?>
<!--
  ~ Copyright (c) 2012. betterFORM Project - http://www.betterform.de
  ~ Licensed under the terms of BSD License
  -->

<!-- $Id: sort-instance.xsl,v 1.4 2006/03/21 19:24:57 uli Exp $ -->
<xsl:stylesheet version="2.0"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:param name="appContext" select="'/betterform'"/>
    <xsl:output method="xhtml" encoding="UTF-8" indent="yes"/>

    
    
    <xsl:template match="/error">
        <html>
            <head>
                <title>Error Page</title>
                <link rel="stylesheet" type="text/css" href="{context}/bfResources/styles/error.css"/>
            </head>
            <body>
                <div class="errorContent">
                    <img src="{context}/bfResources/images/error.png" width="24" height="24" alt="Error"
                         style="float:left;padding-right:5px;"/>
                    <div class="message1">Oops, an error occured...
                        <br/>
                    </div>
                    <div class="message2" id="msg"><xsl:value-of select="message"/></div>
                    <div class="message3">
                        <strong>URL:</strong>
                        <br/><xsl:value-of select="url"/>
                    </div>
                    <div class="message3">
                        <strong>Element causing Exception:</strong>
                        <br/><xsl:value-of select="xpath"/>
                    </div>
                    <form>
                        <input type="button" value="Back" onClick="history.back()"/>
                    </form>
<!--
                    <div class="message3">
                        <a href="mailto:foo@bar.de%3Fsubject%3D%27XForms+Problem+at+http%3A%2F%2Flocalhost%3A8080%2Fbetterform%2Fforms%2Ftest%2Fbrokenform.xhtml%27%26Body%3D%27Message%3A%0Axforms-binding-exception%3A+bind+Element+with+id%3A+%27bar%27+not+found+in+model%0A%0AElement+causing+Exception%3A%0A%2Fhtml%5B1%5D%2Fbody%5B1%5D%2Fxforms%3Aoutput%5B1%5D%0A%0ACaused+by%3A%0A+%27">
                            Report this problem...
                        </a>
                    </div>
-->
                </div>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>
