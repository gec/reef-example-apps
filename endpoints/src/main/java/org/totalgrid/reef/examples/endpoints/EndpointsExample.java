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

import java.util.List;

/**
 * Example: Endpoints
 *
 * Endpoints manage the protocol connections to external devices and
 * systems.
 *
 * Endpoint configurations are the system-wide representations of communications to remote
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
        EndpointManagementService endpointService = client.getRpcInterface(EndpointManagementService.class);

        // Retrieve list of all endpoint configurations
        List<CommEndpointConfig> endpointConfigList = endpointService.getAllEndpoints();

        // Inspect a single endpoint configuration
        CommEndpointConfig endpointConfig = endpointConfigList.get(0);

        // Display properties of endpoint configuration
        System.out.println("Endpoint Config");
        System.out.println("-----------");
        System.out.println("Name: " + endpointConfig.getName());
        System.out.println("Protocol: " + endpointConfig.getProtocol());
        System.out.println("Channel: " + endpointConfig.getChannel().getUuid().getUuid());

        // ConfigFiles are explicitly associated with endpoint configurations in order to provide
        // protocol configurations
        for (ConfigFile configFile : endpointConfig.getConfigFilesList()) {
            System.out.println("Config File: " + configFile.getUuid().getUuid());
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
        EndpointManagementService endpointService = client.getRpcInterface(EndpointManagementService.class);

        // Retrieve a list of all endpoint connections
        List<CommEndpointConnection> endpointConnections = endpointService.getAllEndpointConnections();

        // Display list of endpoint connections, showing enabled/disabled status and COMMS status
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

        // Get service interface for endpoints
        EndpointManagementService endpointService = client.getRpcInterface(EndpointManagementService.class);

        // Select a single endpoint connection
        CommEndpointConnection endpoint = endpointService.getAllEndpointConnections().get(0);

        // Display origin state (should be enabled, COMMS_UP)
        System.out.println("Original: " + endpoint.getEndpoint().getName() + ", " + endpoint.getEnabled() + ", " + endpoint.getState());

        // Get UUID of endpoint connection
        ReefUUID endpointUuid = endpoint.getEndpoint().getUuid();

        // Disable the endpoint connection
        CommEndpointConnection disabled = endpointService.disableEndpointConnection(endpointUuid);

        // Display state immediately after disabled. Because the communication channel is managed asynchronously, state may still be COMMS_UP
        System.out.println("Disabled: " + disabled.getEndpoint().getName() + ", " + disabled.getEnabled() + ", " + disabled.getState());

        // Wait one second
        Thread.sleep(1000);

        // Get state after one second
        CommEndpointConnection disabledDelay = endpointService.getEndpointConnection(endpointUuid);

        // Display one second after disabled. Should now be COMMS_DOWN
        System.out.println("Disabled +1s: " + disabledDelay.getEndpoint().getName() + ", " + disabledDelay.getEnabled() + ", " + disabledDelay.getState());

        // Re-enable endpoint connection
        CommEndpointConnection enabled = endpointService.enableEndpointConnection(endpointUuid);

        // Display state immediately after enabled. Because the communication channel is managed asynchronously, state may still be COMMS_DOWN
        System.out.println("Enabled: " + enabled.getEndpoint().getName() + ", " + enabled.getEnabled() + ", " + enabled.getState());

        // Wait one second
        Thread.sleep(1000);

        // Get state after one second
        CommEndpointConnection enabledDelay = endpointService.getEndpointConnection(endpointUuid);

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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp);

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
