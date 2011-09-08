xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";
import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace xmldb="http://exist-db.org/xquery/xmldb";

declare option exist:serialize "method=xml media-type=application/xml omit-xml-declaration=no indent=yes";

let $contextPath := request:get-context-path()
let $username := request:get-parameter("username", xmldb:get-current-user())
let $password := request:get-parameter("password", "")
let $login := xmldb:login("/db", $username, $password, true())
(: TODO: check if writeable or log into editor :)  
return
if ( $login or not(xmldb:get-current-user() = 'guest'))
then (
	<data xmlns="">
		<username>{$username}</username>
		<password>{$password}</password>
		<login>true</login>
	</data>
) else (
	<data xmlns="">
		<username>{$username}</username>
		<password>{$password}</password>
		<login>false</login>
	</data>
)
