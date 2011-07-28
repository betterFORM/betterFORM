xquery version "1.0";

let $name as xs:string := request:get-parameter('name', 'noname')
let $path := request:get-parameter('path', '')
let $collection := concat('/db/',$path)

(: make sure you use the right user permissions that has write access to this collection :)
let $login := xmldb:login($collection, 'betterFORM', 'Tha0xeiC8a', true())
let $create := xmldb:create-collection($collection, $name)

return
(: <results>
   <message>Collection {$name} has been created as subcollection of collection={$collection}.</message>
</results>
:)

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
    <head>
        <title>betterFORM Create Collection</title>
    </head>

    <body id="timetracker">
		<div id="content">
			<h1>Upload successful</h1>
   			<div style="font-size:large">Collection {$name} has been created as subcollection of collection={$collection}.</div>
        </div>
    </body>
</html>
