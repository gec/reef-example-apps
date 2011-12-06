package org.totalgrid.reef.examples.points;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.PointService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Model.ReefUUID;
import org.totalgrid.reef.client.service.proto.Model.Point;

import java.util.List;

/**
 * Example: Points
 *
 */
public class PointsExample {

    /**
     * Get All Points
     *
     * Get all points configured in the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getPoints(Client client) throws ReefServiceException {

        System.out.print("\n=== Get All Points ===\n\n");

        // Get service interface for points
        PointService service = client.getService(PointService.class);

        // Retrieve list of all points
        List<Point> pointList = service.getPoints();

        System.out.println("Found points: " + pointList.size());

        // Inspect a single point
        Point point = pointList.get(0);

        // Display properties of the point
        System.out.println("Point");
        System.out.println("-----------");
        System.out.println("Uuid: " + point.getUuid().getValue());
        System.out.println("Name: " + point.getName());
        System.out.println("Type: " + point.getType());
        System.out.println("Unit: " + point.getUnit());
        System.out.println("Abnormal: " + point.getAbnormal());
        System.out.println("Endpoint: " + point.getEndpoint().getName());
        System.out.println("-----------");
    }

    /**
     * Get Point by Name
     *
     * Get a particular point by providing the point name.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getPointByName(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Point By Name ===\n\n");

        // Get service interface for points
        PointService service = client.getService(PointService.class);

        // Select a single example point
        Point examplePoint = service.getPoints().get(0);

        // Get the name of the example point
        String name = examplePoint.getName();

        // Find the point again using the point name
        Point point = service.getPointByName(name);

        System.out.println("Found point by name: " + point.getName());
    }

    /**
     * Get Point by UUID
     *
     * Get a particular point by providing the point UUID.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getPointByUuid(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Point By UUID ===\n\n");

        // Get service interface for points
        PointService service = client.getService(PointService.class);

        // Select a single example point
        Point examplePoint = service.getPoints().get(0);

        // Get the UUID of the example point
        ReefUUID uuid = examplePoint.getUuid();

        // Find the point again using the point UUID
        Point point = service.getPointByUuid(uuid);

        System.out.println("Found point by UUID: " + point.getName() + ", " + point.getUuid());
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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp, ReefServices.getInstance());

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            getPoints(client);

            getPointByName(client);

            getPointByUuid(client);

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
