xquery version "1.0";
(: $Id: timetracker.xql  2009-10-02 11:52:08Z lars $ :)
(: 
 This is used to query time tracking data in an exist db.
 Its based on a query from Lars Windauer and was modified
 by Fabian Otto
 
 It allows to filter for projects, workers and a billabel flag.
 It also summarises the amount of time for a query.

 -- Fabian Otto 2010-03-16
:)

declare option exist:serialize "method=xhtml media-type=text/html";
import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

(:
  <data xmlns:xf="http://www.w3.org/2002/xforms"
        xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" 
        xmlns:xsd="http://www.w3.org/2001/XMLSchema" 
        xmlns:ev="http://www.w3.org/2001/xml-events" 
        xmlns:bf="http://betterform.sourceforge.net/xforms">
      <task>
          <date>2010-03-11</date>
          <project>b.telligent</project>
          <start/>
          <end/>
          <duration hours="8" minutes="0"/>
          <who>Fabian</who>
          <what>bugfix</what>
          <note>Fixing: #45</note>
          <billable>true</billable>
          <billed date=""/>
          <status>inprogress</status>
          <created>2010-03-12T15:45:21.515+01:00</created>
      </task>
  </data>
:)


declare function local:equal-or-true($key, $value) as xs:boolean
{
   if (empty($value))
   then true()
   else $value = $key
};

declare function local:tasks() as node()*
{
    session:create(),
    let $projects    := tokenize(xs:string(request:get-parameter("projects", "")), '\s')    
    let $billable-p  := xs:string(request:get-parameter("billable", ()))
    let $worker-p    := xs:string(request:get-parameter("worker", ""))
    let $worker-s    := tokenize($worker-p, '\s')

    for $task in collection("/db/timetracking/tasks/task")//task
    for $w in tokenize($task/who, "\s")
    where local:equal-or-true($task/project, $projects) and 
          local:equal-or-true($w, $worker-s) and
          local:equal-or-true($task/billable, $billable-p)
    return $task
};


declare function local:hours-in-minutes($tasks as node()*) as xs:integer
{
  let $sum := sum($tasks//duration/@hours)
  return  $sum * 60
};

declare function local:billing ($hours as xs:integer, $minutes as xs:integer) 
as element()
{
   local:billing($hours, $minutes, 850)
};


declare function local:billing($hours as xs:integer, $minutes as xs:integer, $dailyRate as xs:integer)
as element()
{
    let $hourlyRate   := $dailyRate div 8
    let $nettoHours   := $hours * $hourlyRate
    let $nettoMinutes := ($minutes div 60) * $hourlyRate
    return 
	<billing>
          <dailyRate>{$dailyRate}</dailyRate>
          <hourlyRate>{$hourlyRate}</hourlyRate>
          <nettoHours>{$nettoHours}</nettoHours>
          <nettoMinutes>{$nettoMinutes}</nettoMinutes>
          <bill>€ {$nettoHours + $nettoMinutes}</bill>
	</billing>
};


declare function local:main()
as element()?
{
  let $selectedTasks     := local:tasks()
  let $hoursInMinutes    := local:hours-in-minutes($selectedTasks)
  let $minutes           := sum($selectedTasks//duration/@minutes)
  let $totalMinutes      := $hoursInMinutes + $minutes
  let $totalHours        := $totalMinutes idiv 60
  let $remainingMinutes  := $totalMinutes mod 60
  let $totalTime         := concat($totalHours,':',$remainingMinutes)
  let $days              := $totalHours idiv 8
  let $billing           := local:billing($totalHours, $remainingMinutes)

  return
  <project days="{$days}"
           totalTime="{$totalTime}"
           totalMinutes="{$totalMinutes}"
           remainingMinutes="{$remainingMinutes}">
  { for $z in $selectedTasks
    let $date := $z/date
    order by $date
    return $z
  }
  { $billing }
  </project>
};

<data>
 { local:main() }
</data>
