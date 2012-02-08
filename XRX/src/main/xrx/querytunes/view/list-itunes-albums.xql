xquery version "1.0";

import module namespace request="http://exist-db.org/xquery/request";

declare namespace xf = "http://www.w3.org/2002/xforms";

declare option exist:serialize "method=xhtml media-type=application/xhtml+html";


(: creates the output for all tasks matching the query :)
declare function local:main() as node() * {

    let $start := xs:integer(request:get-parameter("start", "1"))
    let $quantity := xs:integer(request:get-parameter("quantity", "20"))
    let $search := request:get-parameter("search","")
    
    let $albums :=          
            if(string-length($search) gt 0) then 
            for $album in collection('/db/betterform/apps/querytunes/data')//album
                where contains(lower-case($album/name),lower-case($search)) or contains(lower-case($album/artist),lower-case($search))
                order by $album/artist/text() ascending  collation "?lang=de-DE"
                return $album
        else 
            for $album in collection('/db/betterform/apps/querytunes/data')//album
                order by $album/artist/text() ascending collation "?lang=de-DE"
                return $album

    for $album at $pos in $albums
        let $currentPosition := $pos + $start - 1
        let $position := if($currentPosition lt 10) then concat(0,$currentPosition) else $currentPosition        
        where $pos ge $start and $pos lt ($start + $quantity)                
            return            
                if(string-length($album/artist) gt 0) then 
                    <tr class="tableBody">
                        <td class="column1">{$position}</td>
                        <td class="column2">{data($album/artist)}</td>
                        <td class="column3"><a href="javascript:dojo.publish('/album/show',['{data($album/@id)}']);">{data($album/name)}</a></td>
                        <td class="column4"></td>
                    </tr>
                else ()

};

let $contextPath := request:get-context-path()
return
<html   xmlns="http://www.w3.org/1999/xhtml"
        xmlns:ev="http://www.w3.org/2001/xml-events">
   <head>
      <title>All Tasks</title>

    </head>
    <body>
    	<div id="dataTable">
<!--
    	   <div id="checkBoxSelectors">
    	        Select: <a href="javascript:selectAll();">All</a> | <a href="javascript:selectNone();">None</a>    	        
    	   </div>
-->
		   <table id="taskTable">
			 <tr class="tableHeader">
				<th class="column1">No.</th>
				<th class="column2">Artist</th>
				<th class="column3">Album</th>
				<th class="column4"></th>
			 </tr>
			 {local:main()}
		 </table>
	 </div>
    </body>
</html>
