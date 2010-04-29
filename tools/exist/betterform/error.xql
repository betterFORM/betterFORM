xquery version "1.0";

import module namespace session="http://exist-db.org/xquery/session";

let exceptionMessage := session:get-attribute("betterform.exception")


<html>
<head>
	<title>betterFORM Error Page</title>
	<style>
	body{
        font-family:Tahoma;
        font-size:14pt;
        background:url('<%=request.getContextPath()%>/resources/images/bgOne.gif') repeat-x scroll;
    }
	pre { font-size:8pt; }
    .errorContent{
        margin-top:50px;
        width:600px;
        border:thin solid steelblue;
        margin-left:auto;
        margin-right:auto;
        padding:20px;
    }
    .message1{
        display:block;
        color:steelblue;
        font-weight:bold;
    }
    .message2{
        display:block;
        color:darkred;
        font-size:12pt;
        padding-top:30px;
        font-weight:bold;
    }
    .message3{
        display:block;
        font-size:10pt;
        color:steelblue;
        margin-top:10px;
    }
    input{
        margin-top:20px;
        margin-left:0;
        margin-bottom:0;
    }
	</style>
</head>
<body>
<div class="errorContent">
    <img src="<%=request.getContextPath()%>/resources/images/error.png" width="24" height="24" alt="Error" style="float:left;padding-right:5px;"/>
    <div class="message1">
        Oops, an error occured...<br/>

    </div>

    <div class="message2">{$exceptionMessage}/div>
    <form>
        <input type="button" value="Back" onClick="history.back()">
    </form>
</div>

</body>
</html>



