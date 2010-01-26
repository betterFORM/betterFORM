Preparations for Installer Run
--------------------------------------
This is still a semi-automated task. The installer script assumes all pack (izPack speak) resouces to be present in subdir
'target'. For eXist the war must be build manually and cannot be downloaded any more amd must be added to the target-dir
manually while all other files might be gathered by executing target 'prepare'.

The installer script assumes some resources to be present in the target dir under tools/installer:
- a betterform.war
- the BSD-license.txt
- an up-to-date exist.war
- a directory named 'jetty/betterform' which contains an expanded jetty tree
- other resources like the test suite and sample forms are already fetched from the source-tree directly

