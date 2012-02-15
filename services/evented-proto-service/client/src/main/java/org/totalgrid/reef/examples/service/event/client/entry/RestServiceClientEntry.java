package org.totalgrid.reef.examples.service.event.client.entry;


import org.totalgrid.reef.client.*;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.examples.service.event.client.RestService;
import org.totalgrid.reef.examples.service.event.client.RestServiceList;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.RestMessage;

import java.util.List;

public class RestServiceClientEntry {

    private RestServiceClientEntry() {}


    
    public static void subscribe(Client client) throws ReefServiceException {

        RestService restService = client.getService(RestService.class);

        SubscriptionResult<List<RestMessage>, RestMessage> subResult = restService.subscribeToAllRestMessages();
        
        System.out.println("-- Immediate results of subscription: " + subResult.getResult());

        subResult.getSubscription().start(new MessageEventAcceptor());
    }

    public static void showLifecycle(Client client) throws ReefServiceException {

        RestService restService = client.getService(RestService.class);

        RestMessage addResponse = restService.putMessage("testKey", "testValue");

        System.out.println("-- Added rest message: " + addResponse);

        RestMessage modifyResponse = restService.putMessage("testKey", "secondValue");

        System.out.println("-- Modify rest message: " + modifyResponse);

        System.out.println("-- Current message store, pre-delete: " + restService.getAllMessages());

        restService.deleteMessage("testKey");

        System.out.println("-- Current message store, post-delete: " + restService.getAllMessages());
    }

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

            // Add Sample service to connection list
            connection.addServicesList(new RestServiceList());

            // Login with the user credentials
            Client client = connection.login(user);

            subscribe(client);

            showLifecycle(client);
            
            System.in.read();

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


    public static class MessageEventAcceptor implements SubscriptionEventAcceptor<RestMessage> {

        @Override
        public void onEvent(SubscriptionEvent<RestMessage> subscriptionEvent) {
            System.out.println("-- Got subscription event: " + subscriptionEvent);
        }
    }
}
