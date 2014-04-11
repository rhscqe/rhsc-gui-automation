RHSC GUI Automation Suite
=========================

Prerequisite
===========
Java 7
Ant 
Sahi opensource


Quick Start
===========

source code
-----------
clone this repository and git://github.com/weissjeffm/webui-framework.git into the same directory

environment and configration
---------------------------
Right now the test suite makes use of 1 RHSC instance, 2 RHS nodes, and 2 RHS client machines


cp /resources/te/jenkins-blr.xml.sample /resources/te/jenkins-blr.xml
and update the xml with the hostnames or ip addresses of your RHSC, RHS nodes, RHS Client machines

sahi
----
install sahi opensource from http://sourceforge.net/projects/sahi/
http://sourceforge.net/projects/sahi/

put the following into your shell startup script (if bash, .bashrc)
export SAHI_BASE=<where sahi is installed>


Running Tests
--------------
from the project directory, issue the command `ant run`


Additional Configuration Information
====================================

resources/te/te.properties
--------------------------
points to a test environment xml defined by JAXB annotated class,
com.redhat.qe.storageconsole.te.TestEnvironment
This file can be overridden by using the environment variable TEST_ENVIRONMENT_FILE.

resources/ReportEngineClient/ReportEngineClient-GUI-RHSC.properties
------------------------------------------------------------------
if defined and suite is using com.redhat.qe.storageconsole.listeners.RhscSuiteListener,
suite will report test results to a Report Engine instance (https://github.com/jkandasa/report-engine)
This file can be overridden by the environment variable REPORT_ENGINE_PROPERTY_FILE.

build.xml
--------
ant file that specifies how to compile and build. 
this file also defines what test-ng suites are run (see the run target)


Development Environment
=======================
* Eclipse for Java Developers
* TestNG eclipse plugin

import both this project and webui-framework into the same workspace



