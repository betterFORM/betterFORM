xquery version "1.0";

declare namespace request="http://exist-db.org/xquery/request";
declare namespace functx = "http://www.functx.com";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

import module namespace fileListing="http://betterform.de/xquery/fileListing" at "xmldb:exist:///db/betterform/utils/FileListing.xqm";

(: Starting point similar to doGet() in FormsServlet :)
let $ajaxFunction := request:get-parameter('ajax' , "")
let $fragmentParameter := request:get-parameter('fragment' , "")
let $path := request:get-parameter('path', 'betterform')
let $enableCommands := request:get-parameter('enableCommands', true())
let $listView := request:get-parameter('listView', false())
let $uri := request:get-uri()
let $contextPath := request:get-context-path()
let $restRoot := fn:concat($contextPath, '/rest/db/', $path)


let $uri := request:get-uri()

return
	fileListing:getHTMLFilesListing($uri, $contextPath, $path, $ajaxFunction, $enableCommands, $listView)
