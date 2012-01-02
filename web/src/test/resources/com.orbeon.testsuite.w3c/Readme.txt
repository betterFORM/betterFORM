0. PRE REQUISITES (other implementers, please skip to section 4)
# Orbeon installation

1. INSTALL
# Copy build.properties.xml to build.properties.local.xml and fill in your Orbeon installation information
# Execute target deploy-tests of build/build.xml using ant.


2. RUN
# open your browser and navigate to http://localhost:8080/orbeon/xforms-test-suite/


3. SOURCE OF TESTS 
You can find the src of the tests in src/tests (see next session for an example).

Currently the following chapters have tests:
* Chapter 2
* Chapter 3
* Chapter 4
* Chapter 5
* Chapter 6
* Chapter 7
* Chapter 10
* Chapter 11
* Appendix B

4. HOW IT WORKS
The tests are written in a 'neutral' xml grammar. These xml files could be transformed 
to selenium html tests (using a stylesheet, use target generate-selenium-tests for it in build.xml).
The tests can be found in src/tests (currently only Chapter 2 and 7 are completed)
 
These generated selenium tests use user extensions in selenium to do the tests 
(see src/selenium/orbeon/user-extensions.js for an implementation of the user extension which work in Orbeon).

Currently there is no stylesheet that transforms the xml test report, that is submitted back by 
the selenium web tests, to a w3c test suite result format. This I still something that needs to be written. 


The tests use a simple locator language for identifying the control to test or execute the operation on. 
The locator starts with the type of the control (e.g.: xf-output) followed by the occurrence number, 
in document order (e.g.: -1). When the control is in a repeat you can identify the iteration 
after a • (e.g.: •2). If you want to identify the 3th xforms output control you get xf-output-3. 
If that control is embedded in a repeat and you want to identify the control in the 2nd iteration 
the locator becomes xf-output-3•2 (for nested repeats just add the • separator and the iteration number).

There is a stylesheet build/orbeon-xforms.xsl which can be ran on the XForms W3C test suite to 
assign id’s to the xforms controls that match the locator semantics. This can be an easy way to 
support the locators.


Example test file:

<?xml version="1.0" encoding="UTF-8"?>
<test-case xmlns:xs="http://www.w3.org/2001/XMLSchema"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns="http://www.w3c.org/MarkUp/Forms/XForms/Test/Runner"
	xmlns:xf="http://www.w3.org/2002/xforms"
	xsi:schemaLocation="http://www.w3c.org/MarkUp/Forms/XForms/Test/Runner ../test-case.xsd">
	
	<!-- Open test form -->
	<open href="Chapt02/2.1.a.xhtml" />
	
	<!-- Check if we opened the correct form, by looking at the title -->
	<assert-title title="2.1.a Introductory Example No. 1" />
	
	<!-- Test if the selection and imput controls are present and available -->
	<assert-selection-options locator="xf-select1-1" options="Cash,Credit" />
	<assert-control-present locator="xf-input-1" type="input" />
	<assert-control-present locator="xf-input-2" type="input" />
	
	<!-- Type input in the xforms input fields -->
	<type-input locator="xf-input-1" value="4012888888881881" />
	<type-input locator="xf-input-2" value="2001-08" />
	
	<!-- Click on the submission button -->
	<click locator="xf-submit-1" />
	
	<!-- Wait for the replace all page to load -->
	<wait-for-page-to-load />
	
	<!-- Check the title and some text on the page -->
	<assert-title title="Results from echo.sh" />
	<assert-text-present text="&lt;method&gt;cc&lt;/method&gt;" />
	<assert-text-present text="&lt;number&gt;4012888888881881&lt;/number&gt;" />
	<assert-text-present text="&lt;expiry&gt;2001-08&lt;/expiry&gt;" />

	<!-- Open the test again -->
	<open href="Chapt02/2.1.a.xhtml" />
	
	<!-- Check if we opened the correct form, by looking at the title -->
	<assert-title title="2.1.a Introductory Example No. 1" />
	
	<!-- Select the option cache -->
	<select-option locator="xf-select1-1" option="Cash"/>
	
	<!-- Click on the submission button -->
	<click locator="xf-submit-1" />
	
	<!-- Wait for the replace all page to load -->
	<wait-for-page-to-load />
	
	<!-- Check the title and some text on the page -->
	<assert-title title="Results from echo.sh" />
	<assert-text-present text="&lt;method&gt;cash&lt;/method&gt;" />
	<assert-text-present text="&lt;number/&gt;" />
	<assert-text-present text="&lt;expiry/&gt;" />
	
</test-case> 

