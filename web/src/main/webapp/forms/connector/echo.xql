xquery version "3.0";

declare namespace output ="http://www.w3.org/2010/xslt-xquery-serialization";
declare option output:method "xml";
declare option output:media-type "text/xml";
declare option output:encoding "UTF-8";

declare variable $data external;

let $data := util:parse($data)
let $input := $data//input/text()

return
    <data xmlns="">
        <input>{$input}</input>
    </data>
