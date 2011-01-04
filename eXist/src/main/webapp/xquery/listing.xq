xquery version "1.0";
declare namespace request="http://exist-db.org/xquery/request";

declare function local:list($collection as xs:string, $uri as xs:string, $restroot as xs:string) as node()* {
    <table>
        <tr class="formBrowserHead">
            <td class="formBrowserHeader" colspan="3">
                <span id="path">{$collection}</span>
            </td>
        </tr>
        <tr>
        {local:generate-directory($collection, $uri, $restroot, '..')}
        </tr>
        {local:list-collection($collection, $uri, $restroot)}
    </table>
};

declare function local:generate-directory($collection as xs:string, $uri as xs:string, $restroot as xs:string, $text as xs:string) as node()* {
       
            <td class="directory" colspan="3">
                <a>
                    {attribute {'href'} {fn:concat($uri, '?collection=', $collection)}}
                    <img>
                        {attribute {'src'} { fn:concat($restroot, '/db/betterform/resources/images/folder.gif') }}
                        {attribute {'border'} {'0'}}
                    </img>
                </a>
                <a>
                    {attribute {'class'} {'textLink'}} {attribute {'href'} { fn:concat($restroot, $collection) }}{$text}
                </a>
            </td>
};

declare function local:list-collection($collection as xs:string, $uri as xs:string, $restroot as xs:string) as node()* {
if (exists(xmldb:get-child-collections($collection)))
    then (
        for $childCollection in xmldb:get-child-collections($collection)
        order by $childCollection
        return
        <tr>
            {local:generate-directory( fn:concat($collection, '/', $childCollection), $uri, $restroot, $childCollection)}
        </tr>
       )
    else (),
        for $resource in xmldb:get-child-resources($collection)
  return
        <tr>
            {local:generate-files($collection, $restroot, $resource)}
        </tr>
};

declare function local:generate-files($collection as xs:string, $restroot as xs:string, $file as xs:string) as node()* {
        <tr class="file">
            <td width="25%">
                <a target="_blank">
                    {attribute {'href'} { fn:concat($restroot, $collection, '/', $file) }}{$file}
                </a>
            </td>
            <td width="25%">
                <a target="_blank">
                    {attribute {'href'} { fn:concat($restroot, $collection, '/', $file, '?source=true')}}source
                </a>
            </td>
            <td>{xmldb:last-modified($collection, $file)}</td>
        </tr>
};

let $collection := request:get-parameter('collection', '/db/betterform/apps/forms/demo')
let $restroot := '/betterform/rest'
let $uri := request:get-uri()

return
<div id="bfFormBrowser">
    {local:list($collection, $uri, $restroot)}
</div>
