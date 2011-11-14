package org.totalgrid.reef.examples.subscriptions;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.MeasurementService;
import org.totalgrid.reef.client.rpc.PointService;
import org.totalgrid.reef.clientapi.*;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.proto.Envelope;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Measurements;
import org.totalgrid.reef.proto.Measurements.Measurement;
import org.totalgrid.reef.proto.Model.Point;
import org.totalgrid.reef.proto.Model.ReefUUID;

import java.util.List;


public class SubscriptionsExample {

    public static class MeasurementSubscriber implements SubscriptionEventAcceptor<Measurement> {

        @Override
        public void onEvent(SubscriptionEvent<Measurement> measurementSubscriptionEvent) {

            Envelope.Event eventType = measurementSubscriptionEvent.getEventType();

            Measurement measurement = measurementSubscriptionEvent.getValue();

            System.out.println("Event Type: " + eventType);
            System.out.println("Measurement: " + measurement.getName() + ", " + buildValueString(measurement));
        }
    }

    public static void subscribeToMeasurements(Client client) throws ReefServiceException, InterruptedException  {

        System.out.print("\n=== Measurement Subscription ===\n\n");

        PointService pointService = client.getRpcInterface(PointService.class);

        List<Point> pointList = pointService.getAllPoints();

        MeasurementService measurementService = client.getRpcInterface(MeasurementService.class);

        SubscriptionResult<List<Measurement>, Measurement> result = measurementService.subscribeToMeasurementsByPoints(pointList);

        List<Measurement> currentMeasurements = result.getResult();

        for (Measurement measurement : currentMeasurements) {
            System.out.println("Current Measurement: " + measurement.getName() + ", " + buildValueString(measurement));
        }

        System.out.println("\nMeasurment Events:\n");

        MeasurementSubscriber subscriber = new MeasurementSubscriber();

        result.getSubscription().start(subscriber);

        Thread.sleep(15 * 1000);
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

            subscribeToMeasurements(client);


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
