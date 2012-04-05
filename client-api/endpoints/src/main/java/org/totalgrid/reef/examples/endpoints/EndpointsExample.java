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
package org.totalgrid.reef.examples.endpoints;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.EndpointService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.FEP.EndpointConnection;
import org.totalgrid.reef.client.service.proto.FEP.Endpoint;
import org.totalgrid.reef.client.service.proto.Model.ReefUUID;
import org.totalgrid.reef.client.service.proto.Model.ConfigFile;

import java.util.List;

/**
 * Example: Endpoints
 *
 * Endpoints manage the protocol connections to external devices and
 * systems. There are two central service objects: endpoints and endpoint connections.
 *
 * Endpoints themselves are the system-wide representations of communications to remote
 * devices/systems. Communications are not established until responsibility for an endpoint is
 * assigned to a front end processor (FEP).
 *
 * Endpoint connections represent actual communications on specific front end processors (FEP).
 */
public class EndpointsExample {

    /**
     * Get Endpoint Configurations
     *
     * Retrieves the list of Endpoints in the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getEndpointConfigurations(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Endpoint Configurations ===\n\n");

        // Get service interface for endpoints
        EndpointService endpointService = client.getService(EndpointService.class);

        // Retrieve list of all endpoint configurations
        List<Endpoint> endpointConfigList = endpointService.getEndpoints();

        // Inspect a single endpoint configuration
        Endpoint endpointConfig = endpointConfigList.get(0);

        // Display properties of endpoint configuration
        System.out.println("Endpoint Config");
        System.out.println("-----------");
        System.out.println("Name: " + endpointConfig.getName());
        System.out.println("Protocol: " + endpointConfig.getProtocol());
        System.out.println("Channel: " + endpointConfig.getChannel().getUuid().getValue());

        // ConfigFiles are explicitly associated with endpoint configurations in order to provide
        // protocol configurations
        for (ConfigFile configFile : endpointConfig.getConfigFilesList()) {
            System.out.println("Config File: " + configFile.getUuid().getValue());
        }

        // Points (data inputs) are explicitly associated with endpoints
        for (String pointName : endpointConfig.getOwnerships().getPointsList()) {
            System.out.println("Point: " + pointName);
        }

        // Commands (data outputs) are explicitly associated with endpoints
        for (String commandName : endpointConfig.getOwnerships().getCommandsList()) {
            System.out.println("Command: " + commandName);
        }

        System.out.println("-----------");
    }

    /**
     * Get Endpoint Connections
     *
     * Retrieves the list of Endpoints in the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getEndpointConnections(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Endpoint Connections ===\n\n");

        // Get service interface for endpoints
        EndpointService endpointService = client.getService(EndpointService.class);

        // Retrieve a list of all endpoint connections
        List<EndpointConnection> endpointConnections = endpointService.getEndpointConnections();

        // Display list of endpoint connections, showing enabled/disabled status and COMMS status
        for (EndpointConnection endpointConnection : endpointConnections) {
            System.out.print("Endpoint: " + endpointConnection.getEndpoint().getName());
            System.out.print(", Enabled: " + endpointConnection.getEnabled());
            System.out.print(", State: " + endpointConnection.getState());
            System.out.print(", FEP: " + endpointConnection.getFrontEnd().getAppConfig().getInstanceName());
            System.out.print("\n");
        }

    }

    public static void enableDisableEndpoint(Client client) throws ReefServiceException, InterruptedException {

        System.out.print("\n=== Enable/Disable Endpoint ===\n\n");

        // Get service interface for endpoints
        EndpointService endpointService = client.getService(EndpointService.class);

        // Select a single endpoint connection
        EndpointConnection endpoint = endpointService.getEndpointConnectionByEndpointName("SimulatedEndpoint");

        // Display origin state (should be enabled, COMMS_UP)
        System.out.println("Original: " + endpoint.getEndpoint().getName() + ", " + endpoint.getEnabled() + ", " + endpoint.getState());

        // Get UUID of endpoint connection
        ReefUUID endpointUuid = endpoint.getEndpoint().getUuid();

        // Disable the endpoint connection
        EndpointConnection disabled = endpointService.disableEndpointConnection(endpointUuid);

        // Display state immediately after disabled. Because the communication channel is managed asynchronously, state may still be COMMS_UP
        System.out.println("Disabled: " + disabled.getEndpoint().getName() + ", " + disabled.getEnabled() + ", " + disabled.getState());

        // Wait one second
        Thread.sleep(1000);

        // Get state after one second
        EndpointConnection disabledDelay = endpointService.getEndpointConnectionByUuid(endpointUuid);

        // Display one second after disabled. Should now be COMMS_DOWN
        System.out.println("Disabled +1s: " + disabledDelay.getEndpoint().getName() + ", " + disabledDelay.getEnabled() + ", " + disabledDelay.getState());

        // Re-enable endpoint connection
        EndpointConnection enabled = endpointService.enableEndpointConnection(endpointUuid);

        // Display state immediately after enabled. Because the communication channel is managed asynchronously, state may still be COMMS_DOWN
        System.out.println("Enabled: " + enabled.getEndpoint().getName() + ", " + enabled.getEnabled() + ", " + enabled.getState());

        // Wait one second
        Thread.sleep(1000);

        // Get state after one second
        EndpointConnection enabledDelay = endpointService.getEndpointConnectionByUuid(endpointUuid);

        // Display one second after enabled. Should now be COMMS_UP
        System.out.println("Enabled +1s: " + enabledDelay.getEndpoint().getName() + ", " + enabledDelay.getEnabled() + ", " + enabledDelay.getState());

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

            getEndpointConfigurations(client);

            getEndpointConnections(client);

            enableDisableEndpoint(client);

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
