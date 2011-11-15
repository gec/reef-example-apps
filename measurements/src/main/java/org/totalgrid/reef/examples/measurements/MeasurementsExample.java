package org.totalgrid.reef.examples.measurements;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.MeasurementService;
import org.totalgrid.reef.client.rpc.PointService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Measurements.Measurement;
import org.totalgrid.reef.proto.Model.Point;

import java.util.List;

public class MeasurementsExample {


    public static void getMeasurementByPoint(Client client) throws ReefServiceException {

        PointService pointService = client.getRpcInterface(PointService.class);

        Point examplePoint = pointService.getAllPoints().get(0);

        MeasurementService measurementService = client.getRpcInterface(MeasurementService.class);

        Measurement measurement = measurementService.getMeasurementByPoint(examplePoint);

        System.out.println("Found Measurement by Point: \n" + measurement);
    }

    public static void getMeasurementByName(Client client) throws ReefServiceException {

        PointService pointService = client.getRpcInterface(PointService.class);

        String pointName = pointService.getAllPoints().get(0).getName();

        MeasurementService measurementService = client.getRpcInterface(MeasurementService.class);

        Measurement measurement = measurementService.getMeasurementByName(pointName);

        System.out.println("Found Measurement by name: \n" + measurement);
    }

    public static void getMultipleMeasurements(Client client) throws ReefServiceException {

        PointService pointService = client.getRpcInterface(PointService.class);

        List<Point> pointList = pointService.getAllPoints();

        MeasurementService measurementService = client.getRpcInterface(MeasurementService.class);

        List<Measurement> measurements = measurementService.getMeasurementsByPoints(pointList);

        System.out.println("Found measurements: " + measurements.size());
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

}
