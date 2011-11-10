package org.totalgrid.reef.examples.measurements.history;

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

import java.util.Date;
import java.util.List;

public class MeasurementHistoryExample {


    public static void getMeasurementHistory(Client client, Point point) throws ReefServiceException {

        System.out.print("\n=== Measurement History ===\n\n");

        MeasurementService measurementService = client.getRpcInterface(MeasurementService.class);

        int limit = 5;

        List<Measurement> measurementList = measurementService.getMeasurementHistory(point, limit);

        for (Measurement measurement : measurementList) {

            Date time = new Date(measurement.getTime());

            System.out.println("Measurement: " + measurement.getName() + ", Value: " + buildValueString(measurement) + ", Time: " + time);
        }

    }

    public static void getMeasurementHistorySince(Client client, Point point) throws ReefServiceException {

        System.out.print("\n=== Measurement History (Last 5 Minutes) ===\n\n");

        MeasurementService measurementService = client.getRpcInterface(MeasurementService.class);

        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        int limit = 5;

        List<Measurement> measurementList = measurementService.getMeasurementHistory(point, fiveMinutesAgo, limit);

        for (Measurement measurement : measurementList) {

            Date time = new Date(measurement.getTime());

            System.out.println("Measurement: " + measurement.getName() + ", Value: " + buildValueString(measurement) + ", Time: " + time);
        }

    }

    public static void getMeasurementHistoryInterval(Client client, Point point) throws ReefServiceException {

        System.out.print("\n=== Measurement History (Interval: 20 Minutes Ago to 5 Minutes Ago) ===\n\n");

        MeasurementService measurementService = client.getRpcInterface(MeasurementService.class);

        long twentyMinutesAgo = System.currentTimeMillis() - (20 * 60 * 1000);

        long fiveMinutesAgo = System.currentTimeMillis() - (5 * 60 * 1000);

        boolean returnNewest = true;

        int limit = 10;

        List<Measurement> measurementList = measurementService.getMeasurementHistory(point, twentyMinutesAgo, fiveMinutesAgo, returnNewest, limit);

        for (Measurement measurement : measurementList) {

            Date time = new Date(measurement.getTime());

            System.out.println("Measurement: " + measurement.getName() + ", Value: " + buildValueString(measurement) + ", Time: " + time);
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

            PointService pointService = client.getRpcInterface(PointService.class);

            Point point = pointService.getAllPoints().get(0);

            getMeasurementHistory(client, point);

            getMeasurementHistorySince(client, point);

            getMeasurementHistoryInterval(client, point);

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
