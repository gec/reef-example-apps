package org.totalgrid.reef.examples.measurements.history;

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
import org.totalgrid.reef.proto.Measurements.Measurement;
import org.totalgrid.reef.proto.Model.Point;

import java.util.Date;
import java.util.List;

/**
 * Example: Measurement History
 *
 *
 */
public class MeasurementHistoryExample {

    /**
     * Get Measurement History
     *
     * Gets five most recent measurements for a point.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMeasurementHistory(Client client, Point point) throws ReefServiceException {

        System.out.print("\n=== Measurement History ===\n\n");

        // Get service interface for events
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Limit the results to five; there are a potentially large number of measurements in the history
        int limit = 5;

        // Retrieve a list of the last five measurements for the point
        List<Measurement> measurementList = measurementService.getMeasurementHistory(point, limit);

        // Display measurement history
        for (Measurement measurement : measurementList) {
            System.out.println("Measurement: " + measurement.getName() + ", Value: " + buildValueString(measurement) + ", Time: " + new Date(measurement.getTime()));
        }

    }

    /**
     * Get Measurement History Since
     *
     * Gets measurement history for the last five minutes (limited to five results).
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMeasurementHistorySince(Client client, Point point) throws ReefServiceException {

        System.out.print("\n=== Measurement History (Last 5 Minutes) ===\n\n");

        // Get service interface for events
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Specify the time as five minutes ago
        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        // Limit the results to five; there are a potentially large number of measurements in the history
        int limit = 5;

        // Retrieve a list of measurements in the last five minutes (limited to five)
        List<Measurement> measurementList = measurementService.getMeasurementHistory(point, fiveMinutesAgo, limit);

        // Display measurement history
        for (Measurement measurement : measurementList) {
            System.out.println("Measurement: " + measurement.getName() + ", Value: " + buildValueString(measurement) + ", Time: " + new Date(measurement.getTime()));
        }

    }

    /**
     * Get Measurement History Interval
     *
     * Gets measurement history for the time period of twenty minutes ago to five minutes ago (limited to ten results).
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMeasurementHistoryInterval(Client client, Point point) throws ReefServiceException {

        System.out.print("\n=== Measurement History (Interval: 20 Minutes Ago to 5 Minutes Ago) ===\n\n");

        // Get service interface for events
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Specify the start time as twenty minutes ago
        long twentyMinutesAgo = System.currentTimeMillis() - (20 * 60 * 1000);

        // Specify the end time as five minutes ago
        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        // Specify that the newest measurements in the interval should be returned
        boolean returnNewest = true;

        // Limit the results to ten; there are a potentially large number of measurements in the history
        int limit = 10;

        // Retrieve a list of measurements in the time interval (limited to ten)
        List<Measurement> measurementList = measurementService.getMeasurementHistory(point, twentyMinutesAgo, fiveMinutesAgo, returnNewest, limit);

        // Display measurement history
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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp, ReefServices.getInstance());

        // Prepare a Connection reference so it can be cleaned up in case of an error
        Connection connection = null;

        try {

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            Client client = connection.login(user);

            // Run examples...

            // Get service interface for points
            PointService pointService = client.getService(PointService.class);

            // Select a single point to use as an example
            Point point = pointService.getPoints().get(0);

            getMeasurementHistory(client, point);

            getMeasurementHistorySince(client, point);

            getMeasurementHistoryInterval(client, point);

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
