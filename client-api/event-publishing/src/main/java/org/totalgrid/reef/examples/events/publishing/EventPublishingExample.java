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
package org.totalgrid.reef.examples.events.publishing;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.EventPublishingService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Events.Event;
import org.totalgrid.reef.client.service.proto.Utils.Attribute;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Examples: Event Publishing
 *
 */
public class EventPublishingExample {

    /**
     * Publish Event
     *
     * Publish a user login event.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void publishEvent(Client client) throws ReefServiceException {

        System.out.print("\n=== Publish Event ===\n\n");

        // Get service interface for publishing events
        EventPublishingService eventCreationService = client.getService(EventPublishingService.class);

        // Set event type to user login
        String eventType = "System.UserLogin";

        // Set subsystem to generic "system"
        String subsystem = "system";

        // Publish event by specifying type and subsystem
        Event published = eventCreationService.publishEvent(eventType, subsystem);

        // Display properties of published Event
        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + published.getId());
        System.out.println("User: " + published.getUserId());
        System.out.println("Type: " + published.getEventType());
        System.out.println("Severity: " + published.getSeverity());
        System.out.println("Subsystem: " + published.getSubsystem());
        System.out.println("Message: " + published.getRendered());
        System.out.println("Is Alarm: " + published.getAlarm());
        System.out.println("Time: " + new Date(published.getTime()));
        System.out.println("-----------\n");

    }

    /**
     * Publish Event
     *
     * Publish a user login event with arguments. Arguments are used to provide event-specific
     * details. Their structure is determined by the event type.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void publishEventWithArguments(Client client) throws ReefServiceException {

        System.out.print("\n=== Publish Event With Arguments ===\n\n");

        // Get service interface for publishing events
        EventPublishingService eventCreationService = client.getService(EventPublishingService.class);

        // Set event type to user login
        String eventType = "System.UserLogin";

        // Set subsystem to generic "system"
        String subsystem = "system";

        // The "System.UserLogin" type has two arguments: "status" and "reason"

        // Create attribute for "status"
        Attribute status = Attribute.newBuilder().setName("status").setValueString("StatusArg").setVtype(Attribute.Type.STRING).build();

        // Create attribute for "reason"
        Attribute reason = Attribute.newBuilder().setName("reason").setValueString("ReasonArg").setVtype(Attribute.Type.STRING).build();

        // Create list of attributes
        List<Attribute> attributeList = new ArrayList<Attribute>();

        attributeList.add(status);

        attributeList.add(reason);

        // Publish event, including attributes
        Event published = eventCreationService.publishEvent(eventType, subsystem, attributeList);

        // Display properties of published Event, message will include status/reason
        System.out.println("Event");
        System.out.println("-----------");
        System.out.println("Uid: " + published.getId());
        System.out.println("User: " + published.getUserId());
        System.out.println("Type: " + published.getEventType());
        System.out.println("Severity: " + published.getSeverity());
        System.out.println("Subsystem: " + published.getSubsystem());
        System.out.println("Message: " + published.getRendered());
        System.out.println("Is Alarm: " + published.getAlarm());
        System.out.println("Time: " + new Date(published.getTime()));
        System.out.println("-----------\n");

    }

    /**
     * Java entry-point for running examples.
     *
     * Starts a client connection to Reef, logs in, and executes example code.
     *
     * @param args Command line arguments
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        // Parse command line arguments
        if (args.length < 2) {
            System.out.println("Usage: <broker settings> <user settings>");
            System.exit(-1);
        }

        int result = 0;

        // Load broker settings from config file
        AmqpSettings amqp = new AmqpSettings(args[0]);

        // Load user settings (login credentials) from config file
        UserSettings user = new UserSettings(args[1]);

        // Create a ConnectionFactory by passing the broker settings. The ConnectionFactory is
        // used to create a Connection to the Reef server
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp, new ReefServices());

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            publishEvent(client);

            publishEventWithArguments(client);

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

}
