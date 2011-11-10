package org.totalgrid.reef.examples.points;

import org.totalgrid.reef.client.ReefConnectionFactory;
import org.totalgrid.reef.client.rpc.PointService;
import org.totalgrid.reef.clientapi.Client;
import org.totalgrid.reef.clientapi.Connection;
import org.totalgrid.reef.clientapi.ConnectionFactory;
import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.settings.AmqpSettings;
import org.totalgrid.reef.clientapi.settings.UserSettings;
import org.totalgrid.reef.proto.Model.ReefUUID;
import org.totalgrid.reef.proto.Model.Point;

import java.util.List;

public class PointsExample {

    public static void getAllPoints(Client client) throws ReefServiceException {

        PointService service = client.getRpcInterface(PointService.class);

        List<Point> pointList = service.getAllPoints();

        for (Point point : pointList) {
            System.out.println("Point: " + point.getName());
        }
    }

    public static void getPointByName(Client client) throws ReefServiceException {

        PointService service = client.getRpcInterface(PointService.class);

        Point examplePoint = service.getAllPoints().get(0);

        String name = examplePoint.getName();

        Point point = service.getPointByName(name);

        System.out.println("Found point by name: " + point.getName());
    }

    public static void getPointByUuid(Client client) throws ReefServiceException {

        PointService service = client.getRpcInterface(PointService.class);

        Point examplePoint = service.getAllPoints().get(0);

        ReefUUID uuid = examplePoint.getUuid();

        Point point = service.getPointByUid(uuid);

        System.out.println("Found point by UUID: " + point.getName() + ", " + point.getUuid());
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

            getAllPoints(client);

            getPointByName(client);

            getPointByUuid(client);

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

}
