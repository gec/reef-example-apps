package org.totalgrid.reef.examples.applications;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.ApplicationService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Application.ApplicationConfig;

import java.util.List;

public class ApplicationsExample {

    public static void getApplications(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Applications ===\n\n");

        ApplicationService applicationService = client.getRpcInterface(ApplicationService.class);

        List<ApplicationConfig> applicationConfigList =  applicationService.getApplications();

        ApplicationConfig applicationConfig = applicationConfigList.get(0);

        System.out.println("ApplicationConfig");
        System.out.println("-----------");
        System.out.println("Uuid: " + applicationConfig.getUuid().getUuid());
        System.out.println("Instance Name: " + applicationConfig.getInstanceName());
        System.out.println("Capabilities: " + applicationConfig.getCapabilitesList());
        System.out.println("Location: " + applicationConfig.getLocation());
        System.out.println("Network: " + applicationConfig.getNetwork());
        System.out.println("-----------\n");

        for (ApplicationConfig appConfig : applicationConfigList) {
            System.out.println("Application: " + appConfig.getInstanceName());
        }
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

            getApplications(client);


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
