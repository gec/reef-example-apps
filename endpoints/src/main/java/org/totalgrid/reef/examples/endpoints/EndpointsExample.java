package org.totalgrid.reef.examples.endpoints;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.EndpointManagementService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.FEP.CommEndpointConnection;
import org.totalgrid.reef.proto.FEP.CommEndpointConfig;
import org.totalgrid.reef.proto.Model.ReefUUID;
import org.totalgrid.reef.proto.Model.ConfigFile;

import java.util.Date;
import java.util.List;

public class EndpointsExample {


    public static void getEndpoints(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Endpoints ===\n\n");

        EndpointManagementService endpointService = client.getRpcInterface(EndpointManagementService.class);

        List<CommEndpointConfig> endpointConfigList = endpointService.getAllEndpoints();

        CommEndpointConfig endpointConfig = endpointConfigList.get(0);

        System.out.println("Endpoint Config");
        System.out.println("-----------");
        System.out.println("Name: " + endpointConfig.getName());
        System.out.println("Protocol: " + endpointConfig.getProtocol());
        System.out.println("Channel: " + endpointConfig.getChannel().getUuid().getUuid());

        for (ConfigFile configFile : endpointConfig.getConfigFilesList()) {
            System.out.println("Config File: " + configFile.getUuid().getUuid());
        }

        for (String pointName : endpointConfig.getOwnerships().getPointsList()) {
            System.out.println("Point: " + pointName);
        }

        for (String commandName : endpointConfig.getOwnerships().getCommandsList()) {
            System.out.println("Command: " + commandName);
        }

        System.out.println("-----------");
    }

    public static void getEndpointConnections(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Endpoints ===\n\n");

        EndpointManagementService endpointService = client.getRpcInterface(EndpointManagementService.class);

        List<CommEndpointConnection> endpointConnections = endpointService.getAllEndpointConnections();

        for (CommEndpointConnection endpointConnection : endpointConnections) {
            System.out.print("Endpoint: " + endpointConnection.getEndpoint().getName());
            System.out.print(", Enabled: " + endpointConnection.getEnabled());
            System.out.print(", State: " + endpointConnection.getState());
            System.out.print(", FEP: " + endpointConnection.getFrontEnd().getAppConfig().getInstanceName());
            System.out.print("\n");
        }

    }

    public static void enableDisableEndpoint(Client client) throws ReefServiceException, InterruptedException {

        System.out.print("\n=== Enable/Disable Endpoint ===\n\n");

        EndpointManagementService endpointService = client.getRpcInterface(EndpointManagementService.class);

        CommEndpointConnection endpoint = endpointService.getAllEndpointConnections().get(1);

        System.out.println("Original: " + endpoint.getEndpoint().getName() + ", " + endpoint.getEnabled());

        ReefUUID endpointUuid = endpoint.getEndpoint().getUuid();

        CommEndpointConnection disabled = endpointService.disableEndpointConnection(endpointUuid);

        System.out.println("Disabled: " + disabled.getEndpoint().getName() + ", " + disabled.getEnabled() + ", " + disabled.getState());

        Thread.sleep(1000);

        CommEndpointConnection disabledDelay = endpointService.getEndpointConnection(endpointUuid);

        System.out.println("Disabled +1s: " + disabledDelay.getEndpoint().getName() + ", " + disabledDelay.getEnabled() + ", " + disabledDelay.getState());

        CommEndpointConnection enabled = endpointService.enableEndpointConnection(endpointUuid);

        System.out.println("Enabled: " + enabled.getEndpoint().getName() + ", " + enabled.getEnabled() + ", " + enabled.getState());

        Thread.sleep(1000);

        CommEndpointConnection enabledDelay = endpointService.getEndpointConnection(endpointUuid);

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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp);

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            getEndpoints(client);

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
