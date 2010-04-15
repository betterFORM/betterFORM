xquery version "1.0";

declare option exist:serialize "method=xhtml media-type=text/html indent=yes";

let $timestamp := request:get-parameter("timestamp", "")

let $data-collection := '/db/betterform/apps/timetracker/data/task/'
let $doc := concat($data-collection, $timestamp, '.xml')

return
<html>
    <head>
        <title>Delete Confirmation</title>
        <style>
        <![CDATA[
        .warn {background-color: silver; color: black; font-size: 16pt; line-height: 24pt; padding: 5pt; border: solid 2px black;}
        ]]>
        </style>
    </head>
    <body>
        <a href="../views/list-items.xql">List Items</a>

        <h1>Are you sure you want to delete this task?</h1>
        <strong>Name: </strong>{doc($doc)/*/task/created/text()}<br/>
        <strong>Path: </strong> {$doc}<br/>
        <br/>
        <a class="warn" href="delete.xql?timestamp={$timestamp}">Yes - Delete This Term</a>
        <br/>
        <br/>
        <a  class="warn" href="../views/list-items.xql">Cancel (Back to View Term)</a>
    </body>
</html>
