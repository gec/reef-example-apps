
Sample Reef Application
========================

This is a shell of an application to manage a simple state optimizer using the Reef 
platform. It aims to be simple and understandable and provide a base to jump start
a developer in using Reef. It is not "production ready" code to be deployed against
a real power system, more error and connection handling code is necessary.

The project is built using maven 3.0.1 and can be edited most Java IDEs; IntelliJ or Eclipse are recommended.

It must be run against a Reef system that has an appropriate equipment model for 
the algorithm in question. Defaults to point to Reef system running on local system 
are set.  This can be overriden to connect to a remote reef system.

Also note that the code has been factored to make this example clear, as an application
grows it may become clear that the abstractions are not at the right level, developers 
should feel free to edit or replace any of the classes in this sample project. 
(Specifically CommandIssuer and MeasurementState may be too simplified)

Implementing a State Optimizing Algorithm
-----------------------------------------

There is an example algorithm to switch a capacitor bank depending on a lines voltage.
Developers wanting to jump straight into implementing their algorithm should start by
looking at this class.

Running the application:
-----------------------

Using Maven

    mvn exec:java -Dexec.mainClass="org.totalgrid.samples.stateoptimizer.EntryPoint"

Or using IntelliJ:

    Right-Click on EntryPoint and select "Run EntryPoint.main"

Connecting to a remote reef node:

    mvn exec:java -Dexec.mainClass="org.totalgrid.samples.stateoptimizer.EntryPoint" \
    -Dorg.totalgrid.reef.amqp.host=192.168.100.80

Once the project is loaded you will need to point your app to the AMQP Message Broker. 
The default is local (127.0.0.1).  If you are using a remote broker you can set host 
by going to the IntelliJ RUN menu and selecting "Edit Configuration" and insert the 
following command into the VM parameters. 

    -Dorg.totalgrid.reef.amqp.host=x.x.x.x

The default connection parameters are located in EntryPoint.java.


Current limitations, possible solutions:
------------------------

* All points and commands are selected and consumed; filtering to just those of interest
* All Commands are selected for each execution; could select once for all executions
* No unit tests; adding unit tests is a good way to understand all the parts of the system
* Reef user/passwords are hardcoded; make configurable in EntryPoint
* Doesn't handle disconnection from message broker after initial connection; use IConnectionListener to re-setup after failure
* Measurements subscription could start one measurement behind; API makes writing this correctly difficult, use new subscription api

