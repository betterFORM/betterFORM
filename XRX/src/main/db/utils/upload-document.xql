xquery version "1.0";

let $filename := request:get-uploaded-file-name('file')
let $path := request:get-parameter('path', '')
let $collection := concat('/db/',$path)

(: make sure you use the right user permissions that has write access to this collection :)
let $login := xmldb:login($collection, 'betterFORM', 'Tha0xeiC8a', true())
let $store := xmldb:store($collection, $filename, request:get-uploaded-file-data('file'), 'application/octet-stream')

return
(: <results>
   <message>File {$filename} has been stored at collection={$collection}.</message>
</results>
:)

<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      xml:lang="en">
    <head>
        <title>none</title>
    </head>
    <body>
        <textarea>File {$filename} has been stored within collection={$collection}.</textarea>
    </body>
</html>