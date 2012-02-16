package org.totalgrid.reef.examples.service.event.client.entry;


import org.totalgrid.reef.client.*;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.examples.service.event.client.KeyValueService;
import org.totalgrid.reef.examples.service.event.client.KeyValueServiceList;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.util.List;

public class KeyValueClientEntry {

    private KeyValueClientEntry() {}

    /**
     * Demonstrates subscribing to all subscription events associated with KeyValue service objects.
     *
     * @param client Logged-in client
     * @return
     * @throws ReefServiceException
     */
    public static Subscription<KeyValue> subscribe(Client client) throws ReefServiceException {

        System.out.print("\n=== Subscribe ===\n\n");

        // Get KeyValueService interface from logged-in client
        KeyValueService keyValueService = client.getService(KeyValueService.class);

        // Make subscription, getting an interface used to start the subscription, along with all immediate results of the query
        SubscriptionResult<List<KeyValue>, KeyValue> subResult = keyValueService.subscribeToAllKeyValues();
        
        System.out.println("-- Immediate results of subscription: " + subResult.getResult());

        // Start the subscription, forwarding events to a simple println implementation of an event acceptor
        subResult.getSubscription().start(new PrintingEventAcceptor());

        return subResult.getSubscription();
    }

    /**
     * Demonstrates adding/modifying/removing a service object
     *
     * @param client Logged-in client
     * @throws ReefServiceException
     */
    public static void showLifecycle(Client client) throws ReefServiceException {

        System.out.print("\n=== Show Lifecycle ===\n\n");

        // Get KeyValueService interface from logged-in client
        KeyValueService keyValueService = client.getService(KeyValueService.class);

        // Put a key/value pair that doesn't currently exist
        KeyValue addResponse = keyValueService.putMessage("testKey", "testValue");

        System.out.println("-- Added rest message: " + addResponse);

        // Put a key/value pair that exists, modifying the current key/value in the map
        KeyValue modifyResponse = keyValueService.putMessage("testKey", "secondValue");

        System.out.println("-- Modify rest message: " + modifyResponse);

        System.out.println("-- Current message store, pre-delete: " + keyValueService.getAllMessages());

        // Clean-up the key/value pair (causing a "REMOVED" event)
        keyValueService.deleteMessage("testKey");

        System.out.println("-- Current message store, post-delete: " + keyValueService.getAllMessages());
    }

    /**
     * Demonstrates subscribing to a specific key/value pair. Events should only be
     * received for the key we subscribed to.
     *
     * @param client Logged-in client
     * @return
     * @throws ReefServiceException
     */
    public static Subscription<KeyValue> subscribeToSpecificKey(Client client) throws ReefServiceException {

        System.out.print("\n=== Subscribe To Specific Key ===\n\n");

        // Get KeyValueService interface from logged-in client
        KeyValueService keyValueService = client.getService(KeyValueService.class);

        // Subscribe to only "key01" objects, getting an interface used to start the subscription, along with all immediate results of the query
        SubscriptionResult<List<KeyValue>, KeyValue> subResult = keyValueService.subscribeToKeyValues("key01");

        // Start the subscription, forwarding events to a simple println implementation of an event acceptor
        subResult.getSubscription().start(new PrintingEventAcceptor());

        // Add a KeyValue with the key we're subscribed to
        KeyValue firstAdd = keyValueService.putMessage("key01", "value01");

        // Add a KeyValue with the key we're NOT subscribed to
        KeyValue secondAdd = keyValueService.putMessage("key02", "value02");

        // Clean up all messages, should receive and event for "key01"
        keyValueService.deleteAllMessages();

        return subResult.getSubscription();
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
            connection.addServicesList(new KeyValueServiceList());

            // Login with the user credentials
            Client client = connection.login(user);

            // Subscribe to all service events
            Subscription<KeyValue> firstSub = subscribe(client);

            // Add, modify and delete an object -- should cause subscription events
            showLifecycle(client);

            // Cancel first subscription
            firstSub.cancel();

            // Demonstrates a partial subscription
            Subscription<KeyValue> secondSub = subscribeToSpecificKey(client);

            // Cancels second subscription
            secondSub.cancel();
            
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

    /**
     * Simple SubscriptionEventAcceptor that prints received events to the screen
     */
    public static class PrintingEventAcceptor implements SubscriptionEventAcceptor<KeyValue> {

        /**
         * Allows clients to handle subscription events when they occur
         *
         * @param subscriptionEvent Subscription type (ADDED/MODIFIED/REMOVED) and message payload
         */
        @Override
        public void onEvent(SubscriptionEvent<KeyValue> subscriptionEvent) {
            System.out.println("-- Got subscription event: " + subscriptionEvent);
        }
    }
}
