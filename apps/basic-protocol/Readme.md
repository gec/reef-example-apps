
Reef Basic Protocol Example
========================

Unlike many of the examples, which are vanilla Java entry points, the protocol example is designed to be deployed
directly into the Reef application as an OSGi bundle. Reef's application container is based on [Apache Karaf](http://karaf.apache.org/),
so creating a bundle is done in the standard OSGi way and deployment occurs either through the Karaf feature system or
by dropping the bundle into the `deploy/` directory inside the Reef distribution.

Steps to quickly deploy the basic protocol example:

1.  Have access to a running Reef server (see Reef documentation for guidance).
2.  Run `mvn install` to create the bundles/JARs for the `protocol-library` and `protocol-adapter` projects.
3.  Copy the `protocol-library` bundle into the Reef `deploy/` directory.
4.  Copy the `protocol-adapter` bundle into the Reef `deploy/` directory.
5.  Inside Karaf, the `list` command should show both bundles with the state "Active".

The example also depends on an endpoint being present in the configuration that uses the example protocol. This can be found in the
"config.xml" example configuration included with the examples.