package org.totalgrid.reef.examples.measurements;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.MeasurementService;
import org.totalgrid.reef.client.service.PointService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Measurements.Measurement;
import org.totalgrid.reef.client.service.proto.Model.Point;

import java.util.Date;
import java.util.List;

/**
 * Example: Measurements
 *
 */
public class MeasurementsExample {

    /**
     * Get Measurement by Point
     *
     * Finds latest measurement value for a specific point.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMeasurementByPoint(Client client) throws ReefServiceException {

        System.out.print("\n=== Measurement By Point ===\n\n");

        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Select a specific point
        Point examplePoint = pointService.getPoints().get(0);

        // Get service interface for measurements
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Get latest measurement for the point
        Measurement measurement = measurementService.getMeasurementByPoint(examplePoint);

        // Display measurement properties
        System.out.println("Found Measurement by Point: \n" + measurement);
    }

    /**
     * Get Measurement by Name
     *
     * Finds latest measurement value for a specific point, specified by name.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMeasurementByName(Client client) throws ReefServiceException {

        System.out.print("\n=== Measurement By Name ===\n\n");

        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Select a specific point, get its point name
        String pointName = pointService.getPoints().get(0).getName();

        // Get service interface for measurements
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Get latest measurement for the point by name
        Measurement measurement = measurementService.getMeasurementByName(pointName);

        // Display measurement properties
        System.out.println("Found Measurement by name: \n" + measurement);
    }

    /**
     * Get Multiple Measurements
     *
     * Finds latest measurement value for multiple points.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMultipleMeasurements(Client client) throws ReefServiceException {

        System.out.print("\n=== Multiple Measurements ===\n\n");

        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Select four points to get the measurements
        List<Point> pointList = pointService.getPoints().subList(0, 4);

        // Get service interface for measurements
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Get the latest measurements for the list of points
        List<Measurement> measurementList = measurementService.getMeasurementsByPoints(pointList);

        // Display latest measurements for the points
        for (Measurement measurement : measurementList) {
            System.out.println("Measurement: " + measurement.getName() + ", Value: " + buildValueString(measurement) + ", Time: " + new Date(measurement.getTime()));
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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp, new ReefServices());

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            getMeasurementByPoint(client);

            getMeasurementByName(client);

            getMultipleMeasurements(client);

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

    public static String buildValueString(Measurement measurement) {
        if(measurement.getType() == Measurement.Type.BOOL) {
            return Boolean.toString(measurement.getBoolVal());
        } else if(measurement.getType() == Measurement.Type.INT) {
            return Long.toString(measurement.getIntVal());
        } else if(measurement.getType() == Measurement.Type.DOUBLE) {
            return Double.toString(measurement.getDoubleVal());
        } else if(measurement.getType() == Measurement.Type.STRING) {
            return measurement.getStringVal();
        } else {
            return "";
        }
    }

}
