/**
 * Copyright 2011 Green Energy Corp.
 *
 * Licensed to Green Energy Corp (www.greenenergycorp.com) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. Green Energy
 * Corp licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.totalgrid.reef.examples;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.settings.util.PropertyReader;
import org.totalgrid.reef.examples.alarms.AlarmsExample;
import org.totalgrid.reef.examples.applications.ApplicationsExample;
import org.totalgrid.reef.examples.commands.CommandsExample;
import org.totalgrid.reef.examples.configfile.ConfigFileExample;
import org.totalgrid.reef.examples.endpoints.EndpointsExample;
import org.totalgrid.reef.examples.entities.EntitiesExample;
import org.totalgrid.reef.examples.events.EventsExample;
import org.totalgrid.reef.examples.events.publishing.EventPublishingExample;
import org.totalgrid.reef.examples.measurements.MeasurementsExample;
import org.totalgrid.reef.examples.measurements.history.MeasurementHistoryExample;
import org.totalgrid.reef.examples.measurements.publish.MeasurementPublishingExample;
import org.totalgrid.reef.examples.points.PointsExample;
import org.totalgrid.reef.examples.subscriptions.SubscriptionsExample;

import java.util.Arrays;
import java.util.Properties;

public class Examples {
    /**
     * Java entry-point for running examples.
     *
     * Starts a client connection to Reef, logs in, and executes example code.
     * This is a "single shot" connection, if an application plans on running for extended periods it should use a
     * ConnectedApplicationManagers to be informed of the connection to the server is acquired or lost.
     *
     * @param args Command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        if(args.length == 0){
            args = new String[]{"org.totalgrid.reef.amqp.cfg", "org.totalgrid.reef.user.cfg", "org.totalgrid.reef.node.cfg"};
        }

        // the properties we need to configure our connection to a reef server may be spread across many files
        // or combined into a single file. We read them all into a single large properties object and then construct
        // the settings objects from set of all properties. If there are duplicate keys the last value read in wins.
        Properties properties = PropertyReader.readFromFiles(Arrays.asList(args));

        // Load broker settings from config file
        AmqpSettings amqp = new AmqpSettings(properties);

        // Load user settings (login credentials) from config file
        UserSettings user = new UserSettings(properties);

        // Create a ConnectionFactory by passing the broker settings. The ConnectionFactory is
        // used to create a Connection to the Reef server
        ConnectionFactory connectionFactory = ReefConnectionFactory.buildFactory(amqp, new ReefServices());

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        int result = 0;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Application code here...

            runAllExamples(client);

        } catch(ReefServiceException rse) {

            // Handle ReefServiceException, potentially caused by connection, login, or service request errors
            System.out.println("Reef service error: " + rse.getMessage() + ". check that Reef server is running.");
            rse.printStackTrace();
            result = -1;

        } finally {

            if(connection != null) {

                // Disconnect the Connection object, removes clients and subscriptions
                connection.disconnect();
            }

            // Terminate the ConnectionFactory to close threading objects
            connectionFactory.terminate();
        }

        System.exit(result);
    }

    public static void runAllExamples(Client client) throws Exception{

        AlarmsExample.getActiveAlarms(client);
        AlarmsExample.alarmLifecycle(client);

        ApplicationsExample.getApplications(client);

        CommandsExample.getCommands(client);
        CommandsExample.executionLock(client);
        CommandsExample.multipleExecutionLock(client);
        CommandsExample.commandBlocking(client);
        CommandsExample.executeControl(client);
        CommandsExample.executeSetpoint(client);

        ConfigFileExample.getConfigFile(client);
        ConfigFileExample.createUpdateRemove(client);
        ConfigFileExample.entityAssociation(client);

        EndpointsExample.getEndpointConfigurations(client);
        EndpointsExample.getEndpointConnections(client);
        EndpointsExample.enableDisableEndpoint(client);

        EntitiesExample.getEntities(client);
        EntitiesExample.getByType(client);
        EntitiesExample.getImmediateChildren(client);
        EntitiesExample.getChildren(client);
        EntitiesExample.entityTree(client);

        EventsExample.getRecentEvents(client);
        EventsExample.getRecentEventsByType(client);
        EventsExample.searchForEventsBySeverity(client);
        EventsExample.searchForEventsByInterval(client);

        EventPublishingExample.publishEvent(client);
        EventPublishingExample.publishEventWithArguments(client);

        MeasurementsExample.getMeasurementByPoint(client);
        MeasurementsExample.getMeasurementByName(client);
        MeasurementsExample.getMultipleMeasurements(client);

        MeasurementHistoryExample.getMeasurementHistory(client);
        MeasurementHistoryExample.getMeasurementHistorySince(client);
        MeasurementHistoryExample.getMeasurementHistoryInterval(client);

        MeasurementPublishingExample.publishMeasurement(client);

        PointsExample.getPoints(client);
        PointsExample.getPointByName(client);
        PointsExample.getPointByUuid(client);

        SubscriptionsExample.subscribeToMeasurements(client);
    }
}
