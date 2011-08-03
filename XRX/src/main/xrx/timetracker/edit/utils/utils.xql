xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";

import module namespace request="http://exist-db.org/xquery/request";

declare option exist:serialize "method=xhtml media-type=text/xml";

declare function local:getProjectName($clientId as xs:string) as node() {
    for $project in collection('betterform/apps/timetracker/data/client/')/client[@id=$clientId]/projects/project
    return
        <project id="{$project/@id}">{$project/name/text()}</project>
};

declare function local:getIterationName($clientId as xs:string, $projectId as xs:string) {
    for $iteration in collection('betterform/apps/timetracker/data/client/')/client[@id=$clientId]/projects/project[@id=$projectId]/iterations/iteration
    return
        <iteration id="{$iteration/@id}">{$iteration/text()}</iteration>
};


let $mode := request:get-parameter("mode", "")
let $clientId := request:get-parameter("clientId", "")
let $projectId := request:get-parameter("projectId", "")
return
<data mode="{$mode}">
{
if ($mode eq "project")
then
    local:getProjectName($clientId)
else 
    local:getIterationName($clientId,$projectId)
}
</data>