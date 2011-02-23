@echo off

if not "%JAVA_HOME%" == "" goto JavaAvailable

rem will be set by the installer
set JAVA_HOME=$JDKPath

if not "%JAVA_HOME%" == "" goto JavaAvailable

echo Java environment could not be found.
echo Please add an environment variable pointing to your Java SDK installation
goto :eof

:JavaAvailable
set JAVA_OPTS="-Xms128m -Xmx512m"

"%JAVA_HOME%\bin\java" "%JAVA_OPTS%" -jar ".\betterFORM-Launcher-1.3.jar"
:eof

