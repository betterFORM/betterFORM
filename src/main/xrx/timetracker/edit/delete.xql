xquery version "1.0";

declare option exist:serialize "method=xhtml media-type=text/html indent=yes";



let $data-collection := '/db/betterform/apps/timetracker/data/task/'

(: get the id parameter from the URL :)
let $timestamp := request:get-parameter('timestamp', '')

(: log into the collection :)
let $login := xmldb:login($data-collection, 'admin', 'admin')

(: construct the filename from the id :)
let $file := concat($timestamp, '.xml')

(: delete the file :)
let $store := xmldb:remove($data-collection, $file)

return
<html>
    <head>
        <title>Delete Term</title>
        <style>
            <![CDATA[
               .warn  {background-color: silver; color: black; font-size: 16pt; line-height: 24pt; padding: 5pt; border:solid 2px black;}
            ]]>
        </style>
    </head>
    <body>
        <a href="../views/list-items.xql">Task Overview</a><br/>
        <h1>Term {$timestamp} has been removed.</h1>
    </body>
</html>
