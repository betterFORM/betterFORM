- betterFORM UI browser test readme -
 Where to find the "source"-xml files for the test?
     You can find them inside the "web/test/resources/testsuite" and
     "web/test/resources/uitest" directories.

 - Wie?

 - Was?


 FAQ:

 Why does Webdriver not start FireFox on my PC/Mac ?

 Normally the Firefox binary is assumed to be in the default location for your particular operating system:
    Linux 	firefox (found using "which")
    Mac 	/Applications/Firefox.app/Contents/MacOS/firefox
    Windows %PROGRAMFILES%\Mozilla Firefox\firefox.exe

 If you have installed Firefox to another Location you will have to provide the Loaction via the "webdriver.firefox.bin" system property.

 e.g. :
   -Dwebdriver.firefox.bin="/Applications/Firefox5.app/Contents/MacOS/firefox"



