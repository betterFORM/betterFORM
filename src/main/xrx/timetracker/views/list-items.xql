xquery version "1.0";


import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

declare option exist:serialize "method=xhtml media-type=text/xml";

declare function local:main() as node() * {
for $task in collection('/db/betterform/apps/timetracker/data/task')//task
            let $task-date := $task/date
            let $dur := $task/duration
            order by $task-date
			return
				<tr>
					<td>{$task-date}</td>
					<td>{$task/project}</td>
					<td>{$task/who}</td>
					<td>{data($task/duration/@hours)}:{data($task/duration/@minutes)}</td>
					<td>{$task/what}</td>
					<td>{$task/note}</td>
					<td>{$task/billable}</td>
					<td>{$task/status}</td>
					<td><a href="../edit/edit-item.xql?timestamp={$task/created}&amp;mode=edit">edit</a></td>
					<td><a href="../edit/delete-confirm.xql?timestamp={$task/created}&amp;mode=edit">delete</a></td>
				</tr>
};


request:set-attribute("betterform.filter.parseResponseBody", "true"),
<html   xmlns="http://www.w3.org/1999/xhtml"
        xmlns:ev="http://www.w3.org/2001/xml-events">
   <head>
      <title>All Tasks</title>
      <link rel="stylesheet" type="text/css"
                href="/exist/rest/db/betterform/apps/timetracker/resources/timetracker.css"/>

    </head>
    <body>
    	<div id="dataTable">
		   <table id="taskTable">
			 <tr>
				<th>Date</th>
				<th>Project</th>
				<th>Who</th>
				<th>Duration</th>
				<th>What</th>
				<th>Note</th>
				<th>Billable</th>
				<th>Status</th>
				<th colspan="2"> </th>
			 </tr>
			 {local:main()}
		 </table>
	 </div>
    </body>
</html>
