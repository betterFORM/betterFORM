xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace transform="http://exist-db.org/xquery/transform";

let $filename := request:get-parameter("filename", '')
let $contextPath := request:get-context-path()
let $doc := doc($filename)
return transform:transform($doc, doc("/db/betterform/apps/editor/bfExistEditor.xsl"),
<parameters><param name="bfContext" value="{$contextPath}"/><param name="filename" value="{$filename}"/></parameters>)