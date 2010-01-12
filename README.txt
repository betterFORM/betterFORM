+==========================================================================================+
+==========================================================================================+
+=======================                                      =============================+
+=======================    betterForm Project Quickstart Guide ===========================+
+=======================                                      =============================+
+==========================================================================================+
+==========================================================================================+


+--------------------------------------------------------------------------------------+
Minimal Requirements
+--------------------------------------------------------------------------------------+
   - JDK 1.5
   - Apache Ant 1.7.0 or newer
   - [Optional: Apache Maven 2.0.5 or newer] 


+--------------------------------------------------------------------------------------+
Apache Ant Targets
+--------------------------------------------------------------------------------------+
To execute a target simply call ant <target-name> in the betterform project root directory e.g. ant clean

- clean
    cleans up all target directories

- package
    creates betterform-core-[version].jar in core/target and betterform-web-[version].war in web/target

- update-maven-poms
    if any libraries in build.properites.xml were added / deleted or changed update-maven-poms is needed to sync the
    Ant build.xml and Maven pom.xml file

- install-maven-dependencies [depends on installed Maven2]
    installs all needed Maven2 dependencies into your local Maven repository




+--------------------------------------------------------------------------------------+
Apache Maven Goals
+--------------------------------------------------------------------------------------+
To execute a goal simply call mvn <goal-name> in the betterform project root directory e.g. mvn idea:idea

- idea:idea
    Create IntelliJ project files

- eclipse:eclipse
    Create Eclipse project files 




+--------------------------------------------------------------------------------------+
Further Information
+--------------------------------------------------------------------------------------+
For more detailed informations see
    - README.txt in core, web and convex for detailed information how to build each module
    - User Guide (doc/betterFormUserGuide)
    - Cookbook (doc/betterFormCookBook)
    - Developer Guide (doc/betterFormDeveloperGuide)
    - Wiki (https://r15z.de/trac)
