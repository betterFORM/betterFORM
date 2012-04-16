<map version="1.0.0">
<!-- To view this file, download free mind mapping software FreeMind from http://freemind.sourceforge.net -->
<node CREATED="1334312641624" ID="ID_532793562" MODIFIED="1334332330052" TEXT="xhtml5">
<node CREATED="1334312729819" ID="ID_1850248356" MODIFIED="1334332361582" POSITION="left" TEXT="missing controls">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      List is incomplete, must be compared with existing dojo controls / reference form
    </p>
  </body>
</html>
</richcontent>
<node CREATED="1334312734193" ID="ID_1891416982" MODIFIED="1334312736849" TEXT="Groups"/>
<node CREATED="1334312737851" ID="ID_625967465" MODIFIED="1334312744094" TEXT="Range (Star)"/>
<node CREATED="1334312746737" ID="ID_1267714804" MODIFIED="1334312755481" TEXT="Uploads">
<node CREATED="1334312762791" ID="ID_823972280" MODIFIED="1334312764288" TEXT="anyURI"/>
<node CREATED="1334312765290" ID="ID_1886192199" MODIFIED="1334312768619" TEXT="base64"/>
</node>
<node CREATED="1334312782366" ID="ID_782540235" MODIFIED="1334312785101" TEXT="Textarea">
<node CREATED="1334312791581" ID="ID_1290528025" MODIFIED="1334312794315" TEXT="HTML Editor"/>
</node>
<node CREATED="1334312798004" ID="ID_1306089" MODIFIED="1334312799242" TEXT="Input">
<node CREATED="1334312802428" ID="ID_910667397" MODIFIED="1334312804366" TEXT="DateTime"/>
<node CREATED="1334312808230" ID="ID_692743157" MODIFIED="1334312808949" TEXT="Time"/>
</node>
<node CREATED="1334312812169" ID="ID_1301387691" MODIFIED="1334312822507" TEXT="Select(1)">
<node CREATED="1334312823037" ID="ID_459754586" MODIFIED="1334312829540" TEXT="Open Select(1)"/>
</node>
<node CREATED="1334312830618" ID="ID_319478051" MODIFIED="1334312832588" TEXT="Switch"/>
<node CREATED="1334312844816" ID="ID_293627423" MODIFIED="1334312847640" TEXT="Trigger">
<node CREATED="1334312848145" ID="ID_1862534914" MODIFIED="1334312850879" TEXT="Minimal"/>
<node CREATED="1334312859442" ID="ID_717792717" MODIFIED="1334312864583" TEXT="Image"/>
</node>
<node CREATED="1334312855954" ID="ID_1986927603" MODIFIED="1334312857102" TEXT="Submit"/>
</node>
<node CREATED="1334312899212" ID="ID_1882836682" MODIFIED="1334313529254" POSITION="right" TEXT="missing features / features to verify">
<node CREATED="1334312911023" ID="ID_1960487130" MODIFIED="1334312918639" TEXT="load embed">
<node CREATED="1334312920923" ID="ID_1738385913" MODIFIED="1334312929564" TEXT="behavior must be applied after subform is loaded"/>
</node>
<node CREATED="1334313080956" ID="ID_1742622436" MODIFIED="1334313082421" TEXT="create custom controls">
<node CREATED="1334313082641" ID="ID_140652518" MODIFIED="1334313113398" TEXT="form demonstrating how to easily implement custom controls"/>
</node>
<node CREATED="1334313119522" ID="ID_267880984" MODIFIED="1334313119522">
<richcontent TYPE="NODE"><html>
  <head>
    
  </head>
  <body>
    <p>
      EventOptimization
    </p>
  </body>
</html></richcontent>
<node CREATED="1334313130454" ID="ID_679663838" MODIFIED="1334313139128" TEXT="ClientSide: if not absolut needed, don&apos;t send events to the server for every change but in packages"/>
<node CREATED="1334313147336" ID="ID_1252702002" MODIFIED="1334313147336" TEXT="if DOMFocus (in/out) is not used, don&apos;t send the events"/>
</node>
</node>
<node CREATED="1334312937269" ID="ID_658451491" MODIFIED="1334319295924" POSITION="left" TEXT="known bugs / issues">
<node CREATED="1334312684148" ID="ID_552097956" MODIFIED="1334312950860" TEXT="behaviors &amp; xsdTypes">
<node CREATED="1334312706034" ID="ID_1685225063" MODIFIED="1334312722403" TEXT="we need to have the xsd basetype for unknown xsd types"/>
</node>
<node CREATED="1334313161965" ID="ID_1775336959" MODIFIED="1334313168472" TEXT="Repeat">
<node CREATED="1334313164226" ID="ID_1049424695" MODIFIED="1334313165260" TEXT="Repeat: the RepeatIndex does not get changed correctly if the index(&apos;repeatId&apos;) function is used for insert / delete actions"/>
<node CREATED="1334313228123" ID="ID_1787087565" MODIFIED="1334332255510" TEXT="Switching a RepeatFull to readonly does not work correctly, handleStateChanged events are only present for repeat items but not the repeat itself."/>
</node>
<node CREATED="1334313177759" ID="ID_1196653002" MODIFIED="1334313194545" TEXT="DropDownDate">
<node CREATED="1334313195418" ID="ID_331816711" MODIFIED="1334313195418" TEXT="validation of DropDownDate (if bf:dropdowndata=xyz is used there must be a corresponding constraint on the bind)"/>
</node>
<node CREATED="1334313201936" ID="ID_143705202" MODIFIED="1334313204266" TEXT="Select(1)">
<node CREATED="1334313204801" ID="ID_196888749" MODIFIED="1334313204801" TEXT="xforms-select events within itemsets do not work"/>
</node>
<node CREATED="1334313240637" ID="ID_1908766117" MODIFIED="1334313422459" TEXT="Outputs as Labels">
<node CREATED="1334313244807" ID="ID_1971874188" MODIFIED="1334332307837" TEXT="Using Outputs within Labels (e.g. of a trigger) does not work failsave yet">
<richcontent TYPE="NOTE"><html>
  <head>
    
  </head>
  <body>
    <p>
      This has different reasons,
    </p>
    <ul>
      <li>
        one issue are the CSS class used on the inner Output. These classes should be changed to something like xfInnerValue...
      </li>
      <li>
        outputs within trigger labels do not have markup at all, the Java processor should send the correct parentId here in the handleStateChanged event
      </li>
      <li>
        Implementing 'value' attribute for labels would make the usage of outputs within labels useless in many situations
      </li>
    </ul>
  </body>
</html>
</richcontent>
</node>
</node>
<node CREATED="1334319297739" ID="ID_903626495" MODIFIED="1334319300018" TEXT="Group">
<node CREATED="1334319300019" ID="ID_1046566464" MODIFIED="1334319334870" TEXT="Group: bf:horizontalTable does not render correctly"/>
<node CREATED="1334319336715" ID="ID_1431910970" MODIFIED="1334319342036" TEXT="Test Groups within Repeats"/>
<node CREATED="1334319415445" ID="ID_244483394" MODIFIED="1334319440522" TEXT="Relevance does not work for bf:horizontalTable and bf:verticalTable"/>
</node>
</node>
<node CREATED="1334332332449" ID="ID_476499108" MODIFIED="1334332339019" POSITION="right" TEXT="build (Dojo Release)"/>
<node CREATED="1334332341302" ID="ID_1942386648" MODIFIED="1334332351368" POSITION="right" TEXT="Mobile"/>
<node CREATED="1334332352766" ID="ID_1492704790" MODIFIED="1334332361572" POSITION="right" TEXT="Performance"/>
</node>
</map>
