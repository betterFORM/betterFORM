xquery version "1.0";

declare option exist:serialize "method=xhtml media-type=text/xml";
import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

request:set-attribute("betterform.filter.parseResponseBody", "true"),
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:xf="http://www.w3.org/2002/xforms">
    <head>
        <title>Status</title>
    </head>
    <body>
        <div style="display:none;">
            <xf:model>
                <xf:instance xmlns="">
                    <data/>
                </xf:instance>
            </xf:model>
        </div>


        <xf:group>
            <xf:label>XForms Engine Configuration</xf:label>
            <xf:output value='bar'>
            	<xf:label>Foo</xf:label>
           	</xf:output>
        </xf:group>


    </body>
</html>


	   
