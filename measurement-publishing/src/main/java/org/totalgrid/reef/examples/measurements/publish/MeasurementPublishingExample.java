package org.totalgrid.reef.examples.measurements.publish;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.MeasurementService;
import org.totalgrid.reef.client.rpc.PointService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Measurements;
import org.totalgrid.reef.proto.Measurements.Measurement;
import org.totalgrid.reef.proto.Model.Point;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MeasurementPublishingExample {

    public static void publishMeasurement(Client client) throws ReefServiceException {

        System.out.print("\n=== Publish Measurement ===\n\n");

        PointService pointService = client.getRpcInterface(PointService.class);

        MeasurementService measurementService = client.getRpcInterface(MeasurementService.class);

        Point point = pointService.getAllPoints().get(0);

        System.out.println("Publishing to point: " + point.getName());

        Measurement.Builder builder = Measurement.newBuilder();

        builder.setName(point.getName());
        builder.setUnit("raw");
        builder.setType(Measurement.Type.INT);
        builder.setIntVal(3462);
        builder.setQuality(Measurements.Quality.newBuilder());

        Measurement measurement = builder.build();

        List<Measurement> batch = new ArrayList<Measurement>();
        batch.add(measurement);

        boolean wasPublished = measurementService.publishMeasurements(batch);

        System.out.println("Was published: " + wasPublished);

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
        ConnectionFactory connectionFactory = new ReefConnectionFactory(amqp);

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