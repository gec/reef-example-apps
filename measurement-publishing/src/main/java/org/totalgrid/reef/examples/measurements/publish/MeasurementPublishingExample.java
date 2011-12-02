package org.totalgrid.reef.examples.measurements.publish;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.MeasurementService;
import org.totalgrid.reef.client.service.PointService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.proto.Measurements;
import org.totalgrid.reef.proto.Measurements.Measurement;
import org.totalgrid.reef.proto.Model.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Examples: Measurement Publishing
 *
 */
public class MeasurementPublishingExample {

    /**
     * Publish Measurement
     *
     * Publishes a new measurement to a random point.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void publishMeasurement(Client client) throws ReefServiceException {

        System.out.print("\n=== Publish Measurement ===\n\n");

        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Get service interface for measurements
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Select a single point to publish measurements to
        Point point = pointService.getAllPoints().get(0);

        System.out.println("Publishing to point: " + point.getName());

        // Create new measurement
        Measurement.Builder builder = Measurement.newBuilder();

        // Set name of measusrement (must be the same name as the point)
        builder.setName(point.getName());

        // Set unit of the measurement
        builder.setUnit("raw");

        // Set the value type to INT (integer)
        builder.setType(Measurement.Type.INT);

        // Set the integer value
        builder.setIntVal(3462);

        // Set quality to normal (default)
        builder.setQuality(Measurements.Quality.newBuilder());

        Measurement measurement = builder.build();

        // Create a list of measurements (the measurement "batch") contain the new measurement
        List<Measurement> batch = new ArrayList<Measurement>();
        batch.add(measurement);

        // Publish measurement, returning success/failure
        boolean wasPublished = measurementService.publishMeasurements(batch);

        System.out.println("Was published: " + wasPublished);

        // Verify that the latest measurement value is what was published
        Measurement latest = measurementService.getMeasurementByPoint(point);

        System.out.println("Measurement: " + latest.getName() + ", Value: " + buildValueString(latest) + ", Time: " + new Date(latest.getTime()));
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

            publishMeasurement(client);


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
