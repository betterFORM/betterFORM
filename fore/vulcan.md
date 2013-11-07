

# Grunt Tasks

## Vulcanize
Turns polymers into more durable materials or in a more technical speak: concatenate a set of Web Components into one file


### Configuration
* command: 'node bin/vulcanize -csp -i app/index.html -o dist/index.html',
* -csp: The Content Security Policy externalizes all JavaScript of the Polymer components, the generated js file is saved unter the same name as the input file but with the ending js instead of html
* -i: specifies the input file
* -o: specifies the out file (optional)

### Execute
Vulcanize is triggered by the wcbuild task (configured in Gruntfile.js). The wcbuild task is executed by default is 'grunt build' is executed



