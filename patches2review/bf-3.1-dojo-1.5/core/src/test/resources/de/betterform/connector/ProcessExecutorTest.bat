@echo off
rem Copyright 2010 betterForm
rem $Id: ProcessExecutorTest.bat 161 2005-11-30 14:41:17Z uli $

echo stdout
rem well, this looks crazy, but indeed its a way to avoid additional
rem space to occur: when writing "echo stderr 1>&2", "stderr " would
rem be written to stderr effectively.
echo 1>&2stderr
