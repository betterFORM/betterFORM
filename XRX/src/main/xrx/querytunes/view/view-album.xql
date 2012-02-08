xquery version "1.0";

declare namespace exist = "http://exist.sourceforge.net/NS/exist";

import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

declare option exist:serialize "method=xhtml media-type=text/xml";

declare function local:millisecondsToDuration($duration as xs:double)  {
    if($duration gt 0) then 
        let $hours   := floor($duration div (1000 * 60 * 60))
        let $minutes := floor(($duration div (1000 * 60)) mod 60)
        let $formatedMinutes := if($minutes < 10) then concat(0,$minutes)
                                else $minutes       
        let $seconds := floor(($duration  div 1000) mod 60)
        let $formatedSeconds := if($seconds < 10) then concat(0,$seconds)
                                else $seconds
        return 
            if($hours > 0) then 
                concat($hours, ":", $formatedMinutes, ":", $formatedSeconds)
            else 
                concat($formatedMinutes, ":", $formatedSeconds)
    else()            
};

(: creates the output for all tasks matching the query :)
declare function local:viewAlbum() as node() * {     
     let $albumId := request:get-parameter("album", "")
     for $track in collection('/db/betterform/apps/querytunes/data')//album[@id=$albumId]//track
        order by number($track/@trackNumber)
        return
            <tr class="tableBody">
                <td class="column1">{data($track/@trackNumber)}</td>                
                <td class="column2">{data($track)}</td>
                <td class="column3">{local:millisecondsToDuration(xs:double($track/@time))}</td>
                <td></td>
            
            </tr>
};

declare function local:getAlbumMetadata() as node() {
    let $albumId := request:get-parameter("album", "")
    let $album := collection('/db/betterform/apps/querytunes/data')//album[@id=$albumId]
    return 
        <h1 id="albumHeader">{$album/artist/text()} : {$album/name/text()} Duration: {local:millisecondsToDuration(sum($album//track/@time))}</h1>
};
declare function local:getArtist() as xs:string {
    let $albumId := request:get-parameter("album", "")
    let $album := collection('/db/betterform/apps/querytunes/data')//album[@id=$albumId]
    return 
        $album/artist
};


let $contextPath := request:get-context-path()
return
<html   xmlns="http://www.w3.org/1999/xhtml"
        xmlns:xf="http://www.w3.org/2002/xforms"
        xmlns:exist="http://exist.sourceforge.net/NS/exist"
        xmlns:ev="http://www.w3.org/2001/xml-events">
   <head>
      <title>View Album </title>
       <link rel="stylesheet" type="text/css" href="{$contextPath}/rest/db/betterform/forms/demo/styles/demo.css"/>
    </head>
    <body class="tundra InlineRoundBordersAlert">
    	<div id="xforms">
            <div style="display:none">
                <xf:model>
                    <xf:instance id="i-task" xmlns="">
                        <data>
                            <test>test</test>
                        </data>
                     </xf:instance>
                </xf:model>
            </div>
            <xf:group>
                {local:getAlbumMetadata()} 
                <table id="albumTable">
                    <tr class="tableHeader">
                        <th class="column1">No.</th>
                        <th class="column2">Titel</th>
                        <th class="column3">Duration</th>
                        <th class="column4"></th>
                     </tr>
                     {local:viewAlbum()}
                 </table>
                 <h3>View {local:getArtist()} at: </h3>
                 <a href="http://de.wikipedia.org/wiki/{local:getArtist()}" target="_blank">Wikipedia</a> 
                 -
                 <a href="http://www.youtube.com/results?search_query={local:getArtist()}" target="_blank">Youtube</a>
                 -
                 <a href="http://www.last.fm/search?q={local:getArtist()}" target="_blank">last.fm</a>
                 -
                 <a href="http://grooveshark.com/#!/search?q={local:getArtist()}" target="_blank">Grooveshark</a>
                 -
                 <a href="http://twitter.com/#!/search/{local:getArtist()}" target="_blank">Twitter</a>
                                  
                  
            </xf:group>
        </div>
    </body>
</html>
