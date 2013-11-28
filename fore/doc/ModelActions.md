# Proposal bfModel Actions

## The Clue

The whole idea is to have on the one hand 'Model Action' (xforms actions which are noted within any xforms model
listening to a custom event) and on the other hand a JavaScript / DWR connection to be able to dispatch customs events
from the client.

### Model Action
<pre><code>&lt;xf:action id="does-nothing" ev:event="event-dispatched-from-javascript"/&gt;</code></pre>

### Dispatch Custom Event From JavaScript
fluxProcessor.dispatchCustomEvent(fluxProcessor.sessionId, "event-dispatched-from-javascript");

## The Sugar

fluxProcessor.dispatchCustomEvent calls a DWR function which dispatches the event to the model. The latter should work
out of the box for 'static' actions. But the real fun starts if we are able to map any parameters given by the JS
dispatchEvent function to our xforms actions. Sample 2,3 and 4 show how the syntax might look like to archive this.

## The XForms Model Part


<pre>
<code>
&lt;xf:model&gt;
    &lt;xf:instance xmlns="" id="i-default"&gt;
        &lt;user&gt;
            &lt;name&gt;Lars&lt;/name&gt;
            &lt;surname&gt;Windauer&lt;/surname&gt;
        &lt;/user&gt;
    &lt;/xf:instance&gt;
    &lt;!-- SAMPLE 1 - static xforms setvalue action --&gt;
    &lt;xf:setvalue ev:event="a-setvalue-static" ref="name" value="'Lasse'" /&gt;
    &lt;!-- SAMPLE 2 - dynamic xforms setvalue action --&gt;
    &lt;xf:setvalue ev:event="a-setvalue-dynamic" ref="name" value="bf:appContext('name')" /&gt;
    &lt;!-- SAMPLE 3 - dynamic complex setvalue action --&gt;
    &lt;xf:action ev:event="a-multiple-setvalue"&gt;
        &lt;xf:setvalue ref="name" value="bf:appContext('name')" /&gt;
        &lt;xf:setvalue ref="surname" value="bf:appContext('surname')" /&gt;
    &lt;/xf:action&gt;
    &lt;!-- SAMPLE 4 - complex insert action --&gt;
    &lt;xf:insert ev:event="a-insert" nodeset="instance()" origin="bf:jsToXML('user')" /&gt;
&lt;xf:model&gt;</code></pre>


## The JavaScript Part
    /* Sample 1 */
    fluxProcessor.dispatchEventTypeWithContext(fluxProcessor.sessionId, "a-setvalue-static");
    /* Sample 2 */
    fluxProcessor.dispatchEventTypeWithContext(fluxProcessor.sessionId, "a-setvalue-dynamic", {'name:Lasse'})
    /* Sample 3 */
    fluxProcessor.dispatchEventTypeWithContext(fluxProcessor.sessionId, "a-multiple-setvalue", { name:'Joern', surname:'Turner'})
    /* Sample 4 */
    var {
            name:"Tobias",
            surname:"Krebs"
        }

    fluxProcessor.dispatchEventTypeWithContext(fluxProcessor.sessionId, "a-insert", jsToXML(user:data))



