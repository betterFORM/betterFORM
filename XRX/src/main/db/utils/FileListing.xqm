xquery version "1.0";

module namespace fileListing="http://betterform.de/xquery/fileListing";

declare namespace request="http://exist-db.org/xquery/request";
declare namespace functx = "http://www.functx.com";
declare namespace xmldb="http://exist-db.org/xquery/xmldb";

(:
        todo: the hard-coded '/bfResources' should be replaced with a config param
:)
declare function functx:substring-before-last
  ( $arg as xs:string? ,
    $delim as xs:string )  as xs:string {

   if (fn:matches($arg, functx:escape-for-regex($delim)))
   then fn:replace($arg,
            fn:concat('^(.*)', functx:escape-for-regex($delim),'.*'),
            '$1')
   else ''
 };

 declare function functx:escape-for-regex
  ( $arg as xs:string? )  as xs:string {

   fn:replace($arg,
           '(\.|\[|\]|\\|\||\-|\^|\$|\?|\*|\+|\{|\}|\(|\))','\\$1')
 };
 
declare function fileListing:getRequestURI( $uri as xs:string, $path as xs:string) {
        fn:concat($uri, '?path=', $path)
};

declare function fileListing:generateCrumbs($uri as xs:string, $path as xs:string, $ajaxFunction as xs:string, $wrapperStart as xs:string, $wrapperEnd as xs:string, $steps) {
	for $step at $count in $steps
	let $currentPath := fn:string-join(fn:subsequence($steps, 0, $count+1), '/')
       	return
			if ( $count = fn:count($steps) )
			then (
				<div class="pathName" id="current">
					<a href="#" onclick="{fn:concat($wrapperStart, fileListing:getRequestURI($uri, $currentPath), '&amp;fragment=true&amp;ajax=', $ajaxFunction, $wrapperEnd)}">{$step}</a>
				</div>
			
			) else (
				<div class="pathName">
			<a href="#" onclick="{fn:concat($wrapperStart, fileListing:getRequestURI($uri, $currentPath), '&amp;fragment=true&amp;ajax=', $ajaxFunction, $wrapperEnd)}">{ if ($step eq '') then ('/') else ($step)}</a>
				</div>
			)
};
declare function fileListing:generateCrumbs($uri as xs:string, $path as xs:string, $ajaxFunction as xs:string) {
	let $wrapperStart := "viewParent(this, '"
	let $wrapperEnd := "');"

	let $steps :=  fn:tokenize($path, '/')
	return
	if (not(fn:empty($steps)))
	then (
		fileListing:generateCrumbs($uri, $path, $ajaxFunction, $wrapperStart, $wrapperEnd, $steps)
	) else (
		<div class="pathName" id="current">
			<a href="#" onclick="{fn:concat($wrapperStart, fileListing:getRequestURI($uri, $path), '&amp;fragment=true&amp;ajax=', $ajaxFunction, $wrapperEnd)}">/</a>
		</div>
	)


};

declare function fileListing:generateCollectionMarkup($uri as xs:string, $contextPath as xs:string, $path as xs:string, $altTextCreateCollection as xs:string) {
let $xqeuryPath := functx:substring-before-last($uri, 'FormsQuery.xql')
	return
	<div dojoType="dijit.form.DropDownButton" class="createCollectionDropDownButton">
		<span class="label">
			<img style="height:28px;width:28px;" src="{fn:concat($contextPath, '/bfResources/images/add-folder.png')}" title="{$altTextCreateCollection}"/>
		</span>
		<div dojoType="dijit.TooltipDialog" name="collectionTooltip" >
			<form type="dijit.form.Form"  name="createCollection" class="createCollection" method="post" enctype="multipart/form-data">
					<input id="bfUploadPath" type="path" name="path" style="display:none" value="{$path}"/>
           			<p>
						<b>Create Collection</b>
					</p>
					<p>Name:
						<input dojoType="dijit.form.TextBox" class="bfCollectionName" type="name" name="name" value=""/>
					</p>
               		<p>
						<button dojoType="dijit.form.Button" type="button">create
							<script type="dojo/method" event="onClick" args="evt">
								createCollection();
							</script>
						</button>
					</p>
	  		</form>
		</div>
	</div>
};

declare function fileListing:generateUploadMarkup($uri as xs:string, $contextPath as xs:string, $path as xs:string, $altTextFormUpload as xs:string) {
	let $xqeuryPath := functx:substring-before-last($uri, 'FormsQuery.xql')
	return
	<div dojoType="dijit.form.DropDownButton" class="uploadDropDownButton">
		<span class="label">
			<img style="height:28px;width:28px;" src="{fn:concat($contextPath, '/bfResources/images/add-file.png')}" title="{$altTextFormUpload}"/>
		</span>
		<div dojoType="dijit.TooltipDialog" name="uploadTooltip" >
			<form type="dijit.form.Form" name="upload" class="upload" method="post"  enctype="multipart/form-data" action="{$xqeuryPath}upload-document.xql" >
				<fieldset>
					<input id="bfUploadPath" type="path" name="path" style="display:none" value="{$path}"> </input>
					<p><b>Upload file into current collection</b></p>
                	<input id="bfUploadControl" type="file" name="file" class="bfFileUpload" onchange="sendFile();this.blur();dojo.attr(dojo.query('.bfFileUpload')[0],'value','');"/>
				</fieldset>
        	</form>
		</div>
	</div>
};

declare function fileListing:generateLoginMarkup($uri as xs:string, $contextPath as xs:string, $path as xs:string) {
	let $xqeuryPath := functx:substring-before-last($uri, 'FormsQuery.xql')
    let $guest := xmldb:get-current-user() eq 'guest'
    let $label := if ($guest) then ('Not logged in' ) else ('Logged in')
    let $markup := if (xmldb:get-current-user() eq 'guest')
    then (
        <div dojoType="dijit.form.DropDownButton" class="loginDropDownButton">
		<span class="label">
                <img style="height:28px;width:28px;" src="{fn:concat($contextPath, '/bfResources/images/gear-blue-trans.png')}" title="{$label}"/>
		</span>
            <div dojoType="dijit.TooltipDialog" name="loginTooltip">
                <form type="dijit.form.Form" name="login" class="login" method="post"  enctype="multipart/form-data" action="{$xqeuryPath}login.xql" >
				<fieldset>
                        <p>Name:
                            <input dojoType="dijit.form.TextBox" type="name" name="user" value=""/>
                        </p>
                        <p>Password:
                            <input dojoType="dijit.form.TextBox" type="password" name="password" value=""/>
                        </p>
                        <p>
                            <button dojoType="dijit.form.Button" type="button">login
                                <script type="dojo/method" event="onClick" args="evt">
                                    login();
                                </script>
                            </button>
                        </p>
				</fieldset>
        	</form>
		</div>
	</div>
    ) else (
     <div dojoType="dijit.form.DropDownButton" class="loginDropDownButton">
            <span class="label">
                <img style="height:28px;width:28px;" src="{fn:concat($contextPath, '/bfResources/images/gear-blue-trans.png')}" title="{$label}"/>
            </span>
            <div dojoType="dijit.TooltipDialog" name="loginTooltip">
                <form type="dijit.form.Form" name="login" class="login" method="post"  enctype="multipart/form-data" action="{$xqeuryPath}login.xql" >
                    <fieldset>
                        <input dojoType="dijit.form.TextBox" type="name" name="user" value="guest" style="display:none"/>
                        <input dojoType="dijit.form.TextBox" type="password" name="password" value="guest" style="display:none"/>
                        <p>
                            <button dojoType="dijit.form.Button" type="button">logout
                                <script type="dojo/method" event="onClick" args="evt">
                                    login();
                                </script>
                            </button>
                        </p>
                    </fieldset>
                </form>
            </div>
        </div>
    )
	return
	    <span>
            {$markup}
        </span>
};

declare function fileListing:generateExistAdminClientMarkup($uri as xs:string, $contextPath as xs:string, $path as xs:string) {
<div class="eXistAdminClient" >
	<span class="label">
		<a href="{fn:concat($contextPath,'/webstart/exist.jnlp')}">
			<img style="height:28px;width:28px;" src="{fn:concat($contextPath, '/bfResources/images/eXist-admin.png')}" title="download eXist Admin Client"/>
		</a>
	</span>
</div>
};

declare function fileListing:handleDirectory($uri as xs:string, $contextPath as xs:string, $path as xs:string, $ajaxFunction as xs:string, $childCollection as xs:string) {
	let $wrapperStart := "viewParent(this, '"
	let $wrapperEnd := "');"
	let $ignores := 'utils incubator images resources css'
	return
if (fn:not(fn:contains($ignores, $childCollection)))
		then (		
	if ($ajaxFunction = "")
	then (
	<div class="directory">
		<a class="bfIconDirectory" href="{fn:concat(fileListing:getRequestURI($uri, $path), '/', $childCollection)}">
			<img src="{fn:concat($contextPath, '/bfResources/images/arrow-down.png')}" border="0" />
		</a>
		<a class="textLink" href="{fn:concat(fileListing:getRequestURI($uri, $path), '/', $childCollection)}"> {$childCollection} </a>
	</div>
	) else (
	<div class="directory">

		<a class="bfIconDirectory" href="#" onclick="{fn:concat($wrapperStart, fileListing:getRequestURI($uri, $path), '/', $childCollection , '&amp;fragment=true&amp;ajax=', $ajaxFunction, $wrapperEnd)}">
			<img src="{fn:concat($contextPath, '/bfResources/images/arrow-down.png')}" border="0" />
		</a>
		<a class="textLink" title="{$childCollection}" href="#" onclick="{fn:concat($wrapperStart, fileListing:getRequestURI($uri, $path), '/', $childCollection , '&amp;fragment=true&amp;ajax=', $ajaxFunction, $wrapperEnd)}"> {$childCollection} </a>
	</div>
	)
	) else()
};

declare function fileListing:getIconForExtension($fileName as xs:string) as xs:string {
	let $extension := fn:upper-case(tokenize($fileName,"\.")[last()])
	return 
	if ($fileName eq 'FeatureExplorer.xhtml') then ( 'gear-blue.png' )
	else if ($fileName eq 'TimeTracker.xhtml') then ( 'apps.png' )
	else if ($fileName eq 'Status.xhtml') then ( 'settings_blue.png' )
	else if ($fileName eq 'Demo.xhtml') then ( 'atomium_blue.png' )
	else if ($extension eq 'XHTML') then ( 'type-bf.png' )
	else if ($extension eq 'TXT') then ( 'type-txt.png' )
	else if ($extension eq 'XML') then ( 'type-xml.png' )
	else if ($extension eq 'XSD') then ( 'type-xsd.png' )
	else if ($extension eq 'XHTML') then ( 'type-xhtml.png' )
	else if ($extension eq 'XSL') then ( 'type-xsl.png' )
	else if ($extension eq 'GIF') then ( 'type-gif.png' )
	else if ($extension eq 'PNG') then ( 'type-png.png' )
	else if ($extension eq 'JPG') then ( 'type-jpg.png' )
	else if ($extension eq 'CSS') then ( 'type-css.png' )
	else if ($extension eq 'JS') then ( 'type-js.png' )
	else ( 'standardIcon.png' )					
};
(: To enable the editor link in the XRX Dashboard add the following line :)
(: <a class="editorLink" title="editor" href="{fn:concat($filePath, 'db/betterform/utils/EditorWrapper.xql?filename=', '/db/', $path, '/', $fileLink)}" target="_blank">editor</a> :)

declare function fileListing:handleFile($uri as xs:string, $contextPath as xs:string, $path as xs:string, $ajaxFunction as xs:string, $fileName as xs:string, $shorten as xs:string, $enableCommands as xs:boolean) {
	let $icon := fileListing:getIconForExtension($fileName)
	let $timetrackerLink := if ($fileName eq 'TimeTracker.xhtml') then ( 'apps/timetracker/index.xql') else ($fileName)
	let $referenceLink := if ($timetrackerLink eq 'FeatureExplorer.xhtml' and not(contains($path, 'forms/reference'))) then ( fn:concat('forms/reference/', $fileName)) else ($timetrackerLink)
	let $fileLink := if ($referenceLink eq 'Demo.xhtml' and not(contains($path, 'forms/demo'))) then ( fn:concat('forms/demo/', $referenceLink)) else ($referenceLink)
	let $fileName := if (fn:contains($fileName, '.xhtml')) then( functx:substring-before-last($fileName, '.xhtml') ) else ( $fileName )
	let $shortendFileName := if (fn:string-length($fileName) gt 15 and $shorten eq 'true') then (fn:concat(fn:substring($fileName,0,10), '...', fn:substring($fileName, fn:string-length($fileName) -5))) else ($fileName)  
	let $filePath := functx:substring-before-last($uri, 'db')
	return
	if ($enableCommands)
		then (
        <div class="file">
		    <a class="bfIconFile" href="{fn:concat($filePath, 'db/', $path, '/', $fileLink)}" target="_blank">
			    <img src="{fn:concat($contextPath, '/bfResources/images/', $icon)}" border="0" />
		    </a>
		    <a class="textLink" title="{$fileName}" href="{fn:concat($filePath, 'db/', $path, '/', $fileLink)}" target="_blank">{$shortendFileName}</a>
		    <a class="sourceLink" title="view" href="{fn:concat($filePath, 'db/', $path, '/', $fileLink, '?source=true')}" target="_blank">source</a>
        </div>
		) else (
		<div class="file">
		    <a class="bfIconFile" href="{fn:concat($filePath, 'db/', $path, '/', $fileLink)}" target="_blank">
			    <img src="{fn:concat($contextPath, '/bfResources/images/', $icon)}" border="0" />
		    </a>
		    {$shortendFileName}
		</div>
		)
};

declare function fileListing:generateUp($uri as xs:string, $contextPath as xs:string, $path as xs:string, $ajaxFunction as xs:string) {
	let $wrapperStart := if ($path eq 'betterform') then ( 'viewRoot(this,"' ) else ('viewParent(this,"')
	let $wrapperEnd := '");'
	let $up := functx:substring-before-last($path, '/')
	return    
	if ($ajaxFunction = "")
	then (
             <div class="directory parent" >
					<a href="{fileListing:getRequestURI($uri, $up)}">
						<img id="go-up" title="up one level" src="{fn:concat($contextPath, '/bfResources/images/arrow-up.png')}" border="0"/>
					</a>
					<a class="textLink" href="{fileListing:getRequestURI($uri,$up)}">..</a>
			</div>
	) else (
		<div class="directory parent">
			<a href="#" onclick="{fn:concat($wrapperStart, fileListing:getRequestURI($uri ,$up), '&amp;fragment=true&amp;ajax=', $ajaxFunction, $wrapperEnd)}">
				<img id="go-up" title="up one level" src="{fn:concat($contextPath, '/bfResources/images/arrow-up.png')}" border="0"/>
			</a>
			<a class="textLink" href="#" onclick="{fn:concat($wrapperStart, fileListing:getRequestURI($uri ,$up), '&amp;fragment=true&amp;ajax=', $ajaxFunction, $wrapperEnd)}">..</a>
		</div>
	)	
};

declare function fileListing:handleFileListing($uri as xs:string, $contextPath as xs:string, $path as xs:string, $ajaxFunction as xs:string, $enableCommands as xs:boolean, $listView as xs:boolean) {
	if ( $listView or fn:not('betterform' eq $path) )
		then(
			<div id="bfListView">
				{fileListing:handleUp($uri, $contextPath, $path, $ajaxFunction)}
				{fileListing:generateFileListing($uri, $contextPath, $path, $ajaxFunction, 'false', $enableCommands)}
			</div>
	) else (
		fileListing:generateFileListing($uri, $contextPath, $path, $ajaxFunction, 'true', $enableCommands)
	)
}; 
declare function fileListing:generateFileListing($uri as xs:string, $contextPath as xs:string, $path as xs:string, $ajaxFunction as xs:string, $shorten as xs:string, $enableCommands as xs:boolean) {
	if (fn:exists(xmldb:get-child-collections($path)))
	then (
		for $childCollection in xmldb:get-child-collections($path)
		order by fn:upper-case($childCollection)
		return
			fileListing:handleDirectory($uri, $contextPath, $path, $ajaxFunction, $childCollection)
		
	)
	else (),
	for $fileName in xmldb:get-child-resources($path)
	order by fn:upper-case($fileName)
	return
	    <div>
			{fileListing:handleFile($uri, $contextPath, $path, $ajaxFunction, $fileName, $shorten, $enableCommands)}
		</div>
};

declare function fileListing:handleUp($uri as xs:string, $contextPath as xs:string, $path as xs:string, $ajaxFunction as xs:string) {
	if ( fn:not('betterform' eq $path) )
	then (
		fileListing:generateUp($uri, $contextPath, $path, $ajaxFunction)
	) else ()
};
declare function fileListing:getHTMLFilesListing($uri as xs:string, $contextPath as xs:string, $path as xs:string, $ajaxFunction as xs:string) {
    fileListing:getHTMLFilesListing($uri,$contextPath,$path,$ajaxFunction,true(), false())
};

declare function fileListing:getHTMLFilesListing($uri as xs:string, $contextPath as xs:string, $path as xs:string, $ajaxFunction as xs:string, $enableCommands as xs:boolean, $listView as xs:boolean) {
	let $altTextCreateCollection := 'Create a new collection'
	let $altTextFormUpload := 'Upload your form into this collection'	
	(: {fileListing:generateLoginMarkup($uri, $contextPath, $path)} :)
	return
	<div class="bfFormBrowser" style="width:800px">
		<div class="formBrowserHead">
			<div class="formBrowserHeader">
			 	  {fileListing:generateCrumbs($uri, $path, $ajaxFunction)}
            </div>
            {
                if ($enableCommands)
                then (
			        <div id="commands">
                        {fileListing:generateExistAdminClientMarkup($uri, $contextPath, $path)}
                        {fileListing:generateCollectionMarkup($uri, $contextPath, $path, $altTextCreateCollection)}
			            {fileListing:generateUploadMarkup($uri, $contextPath, $path, $altTextFormUpload)}
  			        </div>
  			    ) else (
  			        <div id="commands"/>
  			    )
  			}
		</div>
			
			{fileListing:handleFileListing($uri, $contextPath, $path, $ajaxFunction, $enableCommands, $listView)}
		<iframe name="targetFrame" style="display:none">
		</iframe>
	</div>
	
};
