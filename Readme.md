
Sample Reef Applications
========================

This repository has a collection of sample applications and build environments showing 
best practices for writing reef applications.


Projects
------------------------

- Client API
    - **Alarms** : Demonstrates "Alarm" service client operations.
    - **Applications** : Demonstrates "ApplicationConfig" service client operations.
    - **Commands** : Demonstrates service client operations around command modeling, locking, and execution.
    - **ConfigFile** : Demonstrates "ConfigFile" service client operations.
    - **Endpoints** : Demonstrates service client operations for inspecting and enabling/disabling endpoints.
    - **Entities** : Demonstrates entity model queries.
    - **Event Publishing** : Demonstrates the event publishing service client operations.
    - **Events** : Demonstrates querying for events.
    - **Login** : Basis for all other client API examples, demonstrates creating a connection the server and using credentials to log-in to the services.
    - **Measurement History** : Demonstrates queries for past measurements.
    - **Measurement Publishing** : Demonstrates publishing new measurements to points.
    - **Measurements** : Demonstrates queries for current measurements (measurement snapshots).
    - **Points** : Demonstrates service client operations for point model objects.
    - **Subscriptions** : Demonstrates subscribing to subscription service events for services (in this case measurements). 

- Applications
    - **State Optimizer** : Reads measurements and issues commands on a period to do global optmization of the system.
    - **Web Embedded** : An embedded [Jetty](http://www.eclipse.org/jetty/) application that serves a page which lists the current values of measurements in the system. 
    - **Web WAR** : A traditional Java web application (WAR) that hosts a servlet that displays the current values of measurements in the system.

- Services
    - **Basic Proto Service** : A skeleton implementation of a Reef service that demonstrates the various parts of hosting a service and providing a client library. 
    - **Evented Proto Service** : A more complete service implementation, demonstrating using the RESTful verbs and issuing service subscription events when service objects are added/modified/removed. The "back-end" for the service is a simple concurrent map instead of a database connection.

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

You will also need an appropriate "System Model" to run your application against.
There are a few examples provided with the Reef distribution (in the samples
directory) for generic applications but some example applications will include 
their own configuration file in the root directory.
