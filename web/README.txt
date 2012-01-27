+==========================================================================+
+===============                                      =====================+
+===============    betterForm Web Quickstart Guide   =====================+
+===============                                      =====================+
+==========================================================================+

+-------------------------------------------------------------------------+
for Minimal Requirements  see README.txt within the project root directory
+-------------------------------------------------------------------------+

+------------------------------------------------------------------------+
Apache Ant Targets
+------------------------------------------------------------------------+

clean
    cleans up web target directory

compile
    compiles all betterForm Web Java sources to web/target/class

exploded-webapp
    creates exploded betterForm Web webapp dir at web/target/betterform
        - no forms are included
        - unzips Dojo to web/target/betterform/resources/scripts to be use in developer mode

deploy-resources
    deploys forms ($PROJECT_HOME/src/main/xforms) to web/target/betterform/forms

deploy-test-resources
    deploys conformance tests ($PROJECT_HOME/src/test) to web/target/betterform/forms

deploy-to-servlet-container
    copies web/target/betterform to the servlet contanier webbapp (webappPath) configured
    within $PROJECT_HOME/build.properties.xml


create-war
    creates betterForm Web war file
        - replaces developer dojo branch by fresh compiled dojo release
        - see web/target/betterform/resources/scripts/util/buildscripts/profiles/betterform.profile
          to see which betterForm resources are included within the release and to add your own
          dijits to it

doc
    generates Java API to web/target/doc
