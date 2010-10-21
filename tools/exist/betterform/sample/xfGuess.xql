xquery version "1.0";
(: $Id: guess.xql 10401 2009-11-08 19:01:26Z wolfgang_m $ :)

import module namespace request="http://exist-db.org/xquery/request";
import module namespace session="http://exist-db.org/xquery/session";
import module namespace util="http://exist-db.org/xquery/util";

declare function local:random($max as xs:integer)
as empty()
{
    let $r := ceiling(util:random() * $max) cast as xs:integer
    return (
        session:set-attribute("random", $r),
        session:set-attribute("guesses", 0)
    )
};

declare function local:guess($guess as xs:integer,
$rand as xs:integer) as element()
{
    let $count := session:get-attribute("guesses") + 1
    return (
        session:set-attribute("guesses", $count),
        if ($guess lt $rand) then
            <p>Your number is too small!</p>
        else if ($guess gt $rand) then
            <p>Your number is too large!</p>
        else
            let $newRandom := local:random(100)
            return
                <p>Congratulations! You guessed the right number with
                {$count} tries. Try again!</p>
    )
};

declare function local:main() as node()?
{
    session:create(),
    let $rand := session:get-attribute("random"),
        $guess := xs:integer(request:get-parameter("guess", ()))
    return
		if ($rand) then
			if ($guess) then
				local:guess($guess, $rand)
			else
				<p>No input!</p>
		else
		    local:random(100)
};

request:set-attribute("betterform.filter.parseResponseBody", "true"),
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:xf="http://www.w3.org/2002/xforms"
      xmlns:ev="http://www.w3.org/2001/xml-events"
      >
    <head><title>Number Guessing</title></head>
    <body>
    	<div style="display:none;">
    		<xf:model>
    		  	<xf:instance xmlns="">
    		  		<data>
    		  			<input/>
    		  		</data>
    		  	</xf:instance>
                <xf:bind nodeset="input" type="integer" constraint=". &gt;= 0 and . &lt;= 100"/>
    		  	<xf:action ev:event="xforms-ready">
    		  		<xf:setvalue ref="input" value="bf:appContext('guess')"/>
    		  	</xf:action>
		  	</xf:model>
    	</div>
        <form action="">
        	<xf:group appearance="full">
        		<xf:label>Guess a number</xf:label>
        		<xf:input ref="input" name="guess" size="3" incremental="true">
        			<xf:label>Number:</xf:label>
        			<xf:alert>Value must be a number between 0 and 100</xf:alert>
				</xf:input>
				<xf:trigger>
					<xf:label>Submit</xf:label>
					  <xf:load show="replace" if="number(input) &gt; 0">
						  <xf:resource value="concat('http://localhost:8080/exist/xquery/xfGuess.xql?guess=',instance()/input)"/>
					  </xf:load>
					  <xf:message if="not(number(input))">The input value must be a positive number</xf:message>
				</xf:trigger>
        	</xf:group>
        </form>
        { local:main()}
        <p id="view-source"><a href="xfGuess.xql/source">View source</a></p>
    </body>
</html>
