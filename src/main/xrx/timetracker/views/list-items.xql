xquery version "1.0";

declare option exist:serialize "method=xhtml media-type=text/html indent=yes";

<html>
   <head>
      <title>All Tasks</title>
    </head>
    <body>
       <h1>Tasks</h1>
       <div class="menu">
       		<a href="../edit/edit-item.xql?mode=new">new task</a>
       </div>
       <table border="1">
       	 <tr>
       	    <td>Date</td>
       	    <td>Project</td>
       	    <td>Who</td>
       	    <td>Duration</td>
       	    <td>What</td>
       	    <td>Note</td>
       	    <td>Billable</td>
       	    <td>Status</td>
       	    <td></td>
       	 </tr>
       	 {
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
       		</tr>
       }</table>
    </body>
</html>
