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

            getApplications(client);


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
