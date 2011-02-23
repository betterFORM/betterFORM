#!/bin/sh

# will be set by the installer

if [ -z "$JAVA_HOME" ]; then
    JAVA_HOME="%{JDKPath}"
fi

if [ ! -d "$JAVA_HOME" ]; then
    JAVA_HOME="%{JAVA_HOME}"
fi

"${JAVA_HOME}"/bin/java -jar "./betterFORM.jar" -Xms256m -Xmx1024m
