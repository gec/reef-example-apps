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


    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Usage: <broker settings> <user settings>");
            System.exit(-1);
        }

        int result = 0;
        AmqpSettings amqp = new AmqpSettings(args[0]);
        UserSettings user = new UserSettings(args[1]);

        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp);
        Connection connection = null;

        try {

            connection = connectionFactory.connect();
            Client client = connection.login(user);

            getEndpoints(client);

            getEndpointConnections(client);

            enableDisableEndpoint(client);

        } catch(ReefServiceException rse) {

            System.out.println("Reef service error: " + rse.getMessage() + ". check that Reef server is running.");
            rse.printStackTrace();
            result = -1;

        } finally {
            if(connection != null) {
                connection.disconnect();
            }
            connectionFactory.terminate();
        }

        System.exit(result);
    }

}
