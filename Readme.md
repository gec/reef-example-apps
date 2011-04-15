
Sample Reef Applications
========================

This repository has a collection of sample applications and build environments showing 
best practices for writing reef applications.


Projects
------------------------

- _State Optimizer_ : Reads measurements and issues commands on a period 
  to do global optmization of the system.


Dependencies & Environment
------------------------
There are a number of basic steps to build an environment for supporting the 
development of client applications. 

First you will need a running distribution see: http://reef.totalgrid.org/documentation/getting-started for details.

1. Install Java6
2. You will need access to the QPID message broker to support messaging and test 
   your client. The installation of QPID can be on a node (machine) other than the 
   one you are developing on.  You will need IP of the node for you configuration.
3. You will need to install PostgreSQL for persistence.
4. You will need to install the reef-karaf deployment for your operating system.
5. Setup Development Environment -  The following is one method to create a build environment on a Windows System.
  - Install the community version of IntelliJ from http://www.jetbrains.com/idea/download/index.html
  - Unzip the package and place in your workspace folder ( a folder you create)
  - From IntelliJ on the FILE menu select New Project
  - Select "Import Project from External Model", choose Maven and point it at your directory
  - Run "Build > Make Project" to have IntelliJ collect the dependencies and build the project. 
    Note:  Reef apis are maintained in a maven artifactory repository and you will need network access to the internet.

You will also need an approriate "System Model" to run your application against.
There are a few examples provided with the Reef distribution (in the samples
directory) for generic applications but some example applications will include 
their own configuration file in the root directory.
