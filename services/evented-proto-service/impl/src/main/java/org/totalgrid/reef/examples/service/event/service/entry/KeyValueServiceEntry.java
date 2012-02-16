package org.totalgrid.reef.examples.service.event.service.entry;

import org.totalgrid.reef.client.AnyNodeDestination;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.registration.EventPublisher;
import org.totalgrid.reef.client.registration.ServiceRegistration;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.examples.service.event.client.KeyValueDescriptor;
import org.totalgrid.reef.examples.service.event.client.KeyValueServiceList;
import org.totalgrid.reef.examples.service.event.service.KeyValueService;

public class KeyValueServiceEntry {
    private KeyValueServiceEntry() {}


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

            // Add the KeyValue service list, which contains the type description of the KeyValue service message
            connection.addServicesList(new KeyValueServiceList());

            // Obtain the service registration interface to perform service provider duties
            ServiceRegistration registration = connection.getServiceRegistration();

            // Obtain the event publisher interface for the service to provide subscriptions/publish events
            EventPublisher eventPublisher = registration.getEventPublisher();

            // Bind KeyValue service to handle KeyValue requests.
            // Uses AnyNodeDestination and competing consumers patterns -- meaning all service instances are interchangeable.
            // (This isn't actually true since the "back-end" is in-memory, but we are assuming a single node for this example).
            registration.bindService(new KeyValueService(eventPublisher), new KeyValueDescriptor(), new AnyNodeDestination(), true);

            System.out.println("Service registered. Press any key to exit...");

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

}
