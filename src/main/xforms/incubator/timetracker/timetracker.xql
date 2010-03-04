xquery version "1.0";
(: $Id: timetracker.xql  2009-10-02 11:52:08Z lars $ :)

import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

declare function local:main() as node()?
{
	session:create(),
	let $project := xs:string(request:get-parameter("project", ()))
	let $tasks := collection("/db/timetracking/tasks/task")//task
	let $selectedTasks := $tasks[./project = $project]
	let $durationHours := if(sum($selectedTasks//duration/@hours) > 0) then sum($selectedTasks//duration/@hours) else 0
	let $sumMinutesInHours := xs:double(sum($selectedTasks//duration/@minutes) div 60)
	let $minutesRest := sum($selectedTasks//duration/@minutes) mod 60
	let $durationMinutes := if(sum($selectedTasks//duration/@minutes) > 0) then concat(substring-before(xs:string(sum($selectedTasks//duration/@minutes) div 60),'.'),':', (sum($selectedTasks//duration/@minutes)) mod 60) else '0:0'
	let $duration := if($minutesRest > 0) then concat($durationHours + xs:double(substring-before($durationMinutes,':')),':',substring-after($durationMinutes,':')) else concat($durationHours+$sumMinutesInHours,':0')

	return
		<project>
			<name>{$project}</name>
			<!-- <duration minutes="{$durationMinutes}" hours="{$durationHours}" minutesInHours="{$sumMinutesInHours}"   minutesRest="{$minutesRest}" >{$duration}</duration> -->
			<duration>{$duration}</duration>
			{
			for $z in $selectedTasks
				let $date := $z/date
				order by $date descending
				return $z
			}
		</project>
};

<data>
	{ local:main() }
</data>
