xquery version "1.0";

import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

declare option exist:serialize "method=xhtml media-type=application/xhtml+html";


(: creates the output for all tasks matching the query :)
declare function local:main() as node() * {
    let $albums := local:getMatchingTasks()
    let $start := xs:integer(request:get-parameter("start", "1"))    
    for $album at $pos in $albums
        let $currentPosition := $pos + $start - 1
        let $position := if($currentPosition lt 10) then concat(0,$currentPosition) else $currentPosition
        return            
            
            <tr>
                <td>{$position}</td>
                <td class="selectorCol"><input type="checkbox" dojotype="dijit.form.CheckBox" value="false" /></td>                                
                <td>{data($album/artist)}</td>
                <td>{data($album/name)}</td>
                <td></td>
                
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
                <td></td>
            </tr>

};

declare function local:getMatchingTasks() as node() * {
    let $start := xs:integer(request:get-parameter("start", "1"))
    let $quantity := xs:integer(request:get-parameter("quantity", "20"))
    
    for $album in collection('/db/betterform/apps/querytunes/data')//album
        where $album/position() ge $start and $album/position() lt ($start + $quantity)        
        return $album

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
    	   <div id="checkBoxSelectors">
    	        Select: <a href="javascript:selectAll();">All</a> | <a href="javascript:selectNone();">None</a>    	        
    	   </div>
		   <table id="taskTable">
			 <tr>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th></th>
				<th colspan="2"> </th>
			 </tr>
			 {local:main()}
		 </table>
	 </div>
    </body>
</html>
