/*###############################################*/
/*#####                                     #####*/
/*#####    betterFORM TimeTracker Readme    #####*/
/*#####                                     #####*/
/*###############################################*/


= Prerequisite =
 * betterFORM & eXist xmldb are installed and running

= Set Up betterFORM TimeTracker = 
 * deploy the ./resources directory into your eXist xmldb (any path you like)  	 
 * edit timeTracker.xhtml and let the dbPath property within the i-controller instance
 	point to the just installed resources directory: 
 		<dbPath>localhost:8080/exist/rest/db/timetracking</dbPath>

= Note =
As all forms within the incubator directory this form is under heavy development
