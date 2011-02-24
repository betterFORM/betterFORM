xquery version "1.0";
declare namespace request="http://exist-db.org/xquery/request";
declare namespace functx = "http://www.functx.com";

declare function functx:substring-before-last
  ( $arg as xs:string? ,
    $delim as xs:string )  as xs:string {

   if (matches($arg, functx:escape-for-regex($delim)))
   then replace($arg,
            concat('^(.*)', functx:escape-for-regex($delim),'.*'),
            '$1')
   else ''
 } ;

 declare function functx:escape-for-regex
  ( $arg as xs:string? )  as xs:string {

   replace($arg,
           '(\.|\[|\]|\\|\||\-|\^|\$|\?|\*|\+|\{|\}|\(|\))','\\$1')
 } ;

declare function local:list($ajaxFunction as xs:string, $fragmentParameter as xs:string, $path as xs:string, $uri as xs:string, $restroot as xs:string) as node()* {
    <table>
        <tr class="formBrowserHead">
            <td class="formBrowserHeader" colspan="3">
                <span id="path">{$path}</span>
            </td>f
        </tr>
        <tr>
        {local:generate-directory($ajaxFunction, $fragmentParameter, $path, $uri, $restroot, '..')}
        </tr>
        {local:list-collection($ajaxFunction, $fragmentParameter, $path, $uri, $restroot)}
</table>
};

declare function local:generate-directory($ajaxFunction as xs:string, $fragmentParameter as xs:string, $path as xs:string, $uri as xs:string, $restroot as xs:string, $text as xs:string) as node()* {
<div class="directory">
{
if ($ajaxFunction = "")
then (
<a>
                    {attribute {'href'} {fn:concat($uri, '?path=', $path)}}
                    <img>
                        {attribute {'src'} { fn:concat($restroot, '/db/betterform/resources/images/bf_logo_square_effect_gray_dark.png') }}
                        {attribute {'border'} {'0'}}
                    </img>
                </a>,
                <a>
                    {attribute {'class'} {'textLink'}} {attribute {'href'} {fn:concat($uri, '?path=', $path)}}{$text}
                </a> )

else (
 <a href="#">
                    {attribute {'onclick'} { fn:concat($ajaxFunction, '(', $uri, '?path=', $path, '&amp;fragment=true&amp;ajax=', $ajaxFunction, ')') }}
                    <img>
                        {attribute {'src'} { fn:concat($restroot, '/db/betterform/resources/images/bf_logo_square_effect_gray_dark.png') }}
                        {attribute {'border'} {'0'}}
                    </img>
                </a>,
<a>
                    {attribute {'class'} {'textLink'}}
                    {attribute {'title'} {$text}}
                    {attribute {'href'} { fn:concat($restroot, $path) }}
                    {attribute {'onclick'} { fn:concat($ajaxFunction, '(', $uri, '?path=', $path, '&amp;fragment=true&amp;ajax=', $ajaxFunction, ')') }}
                    {$text}
                </a>
)
}
</div>
};

declare function local:list-collection($ajaxFunction as xs:string, $fragmentParameter as xs:string, $path as xs:string, $uri as xs:string, $restroot as xs:string) as node()* {
if (exists(xmldb:get-child-collections($path)))
    then (
        for $childCollection in xmldb:get-child-collections($path)
        order by $childCollection
        return
        <div>
            {local:generate-directory($ajaxFunction, $fragmentParameter, fn:concat($path, '/', $childCollection), $uri, $restroot, $childCollection)}
        </div>
       )
    else (),
        for $resource in xmldb:get-child-resources($path)
  return
        <div>
            {local:generate-files($path, $restroot, $resource)}
        </div>
};

declare function local:generate-files($path as xs:string, $restroot as xs:string, $file as xs:string) as node()* {
        <div class="file">

                <a class="bfIconFile" target="_blank">
                    {attribute {'href'} { fn:concat($restroot, $path, '/', $file) }}{$file}
                    <img border="0">
                    {attribute {'src'} { fn:concat($restroot, '/db/betterform/resources/images/bf_logo_square_no_effect_gray.png') }}
                    </img>
                </a>
                 <a>
                    {attribute {'class'} {'textLink'}} {attribute {'href'} { fn:concat($restroot, $path, '/', $file) }}{$file}
                </a>
            <!--
                <a target="_blank">
                    {attribute {'href'} { fn:concat($restroot, $path, '/', $file, '?source=true')}}source
                </a>

            <div>{xmldb:last-modified($path, $file)}</div>
            -->
        </div>
};

let $ajaxFunction := request:get-parameter('ajax' , "")
let $fragmentParameter := request:get-parameter('fragment' , "")
let $path := request:get-parameter('path', '/db/betterform')
let $restroot := '/betterform/rest'
let $uri := request:get-uri()
let $up := functx:substring-before-last($path, '/')
return
if (not ($fragmentParameter = '') )
then (
<div id="bfFormBrowser">
        <div class="formBrowserHead">
            <div class="formBrowserHeader">
                <span id="path">{$path}</span>
            </div>
        </div>
        {local:generate-directory($ajaxFunction, $fragmentParameter, $path, $uri, $restroot, '..')}
        {local:list-collection($ajaxFunction, $fragmentParameter, $path, $uri, $restroot)}
</div>
)
else (
<html>
<head>
</head>
<body>
<div id="bfFormBrowser">
        <div class="formBrowserHead">
            <div class="formBrowserHeader">
                <span id="path">{$path}</span>
            </div>
        </div>
        {local:generate-directory($ajaxFunction, $fragmentParameter, $up, $uri, $restroot, '..')}
        {local:list-collection($ajaxFunction, $fragmentParameter, $path, $uri, $restroot)}
</div>
</body>
</html>
)
