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
package org.totalgrid.reef.examples.subscriptions;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.MeasurementService;
import org.totalgrid.reef.client.service.PointService;
import org.totalgrid.reef.client.*;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.proto.Envelope;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Measurements.Measurement;
import org.totalgrid.reef.client.service.proto.Model.Point;

import java.util.List;

/**
 * Example: Subscriptions
 *
 */
public class SubscriptionsExample {

    /**
     * Implements the SubscriptionEventAcceptor, which provides a callback to be notified
     * of new subscription events, in this case for measurements.
     *
     */
    public static class MeasurementSubscriber implements SubscriptionEventAcceptor<Measurement> {

        /**
         * Receives notifications when subscription events (measurements) happen
         * in the system.
         *
         * @param measurementSubscriptionEvent
         */
        @Override
        public void onEvent(SubscriptionEvent<Measurement> measurementSubscriptionEvent) {

            // Type of the Event (ADDED, MODIFIED, REMOVED)
            Envelope.SubscriptionEventType eventType = measurementSubscriptionEvent.getEventType();

            // Measurement associated with the event
            Measurement measurement = measurementSubscriptionEvent.getValue();

            System.out.println("Event Type: " + eventType);
            System.out.println("Measurement: " + measurement.getName() + ", " + buildValueString(measurement));
        }
    }

    /**
     * Subscribe to Measurements
     *
     * Subscribes to measurement updates for all points.
     *
     * @param client
     * @throws ReefServiceException
     * @throws InterruptedException
     */
    public static void subscribeToMeasurements(Client client) throws ReefServiceException, InterruptedException  {

        System.out.print("\n=== Measurement Subscription ===\n\n");

        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Retrieve list of all points in the system
        List<Point> pointList = pointService.getPoints();

        // Get service interface for measurements
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Subscribe to measurements using the list of points, obtaining a subscription result.
        // The subscription result contains both the immediate result of the request (the current measurement values)
        // and a Subscription object, which can be provided with a callback that will be notified when new
        // measurements arrive.
        SubscriptionResult<List<Measurement>, Measurement> result = measurementService.subscribeToMeasurementsByPoints(pointList);

        // Get the list of current measurements
        List<Measurement> currentMeasurements = result.getResult();

        // Display the current measurements
        for (Measurement measurement : currentMeasurements) {
            System.out.println("Current Measurement: " + measurement.getName() + ", " + buildValueString(measurement));
        }

        System.out.println("\nMeasurement Events:\n");

        // Build a MeasurementSubscriber callback to accept subscription events
        MeasurementSubscriber subscriber = new MeasurementSubscriber();

        // Get the Subscription object from the SubscriptionResult
        Subscription<Measurement> subscription = result.getSubscription();

        // Start the subscription, providing the MeasurementSubscriber as a callback
        subscription.start(subscriber);

        // Receive new measurements for fifteen seconds
        Thread.sleep(15 * 1000);

        // Cancel subscription to clean up resources in broker
        subscription.cancel();
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

            subscribeToMeasurements(client);


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

    public static String buildValueString(Measurement measurement) {
        if(measurement.getType() == Measurement.Type.BOOL) {
            return Boolean.toString(measurement.getBoolVal());
        } else if(measurement.getType() == Measurement.Type.INT) {
            return Long.toString(measurement.getIntVal());
        } else if(measurement.getType() == Measurement.Type.DOUBLE) {
            return Double.toString(measurement.getDoubleVal());
        } else if(measurement.getType() == Measurement.Type.STRING) {
            return measurement.getStringVal();
        } else {
            return "";
        }
    }
}
