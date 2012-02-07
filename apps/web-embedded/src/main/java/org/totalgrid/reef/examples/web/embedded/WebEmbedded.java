package org.totalgrid.reef.examples.web.embedded;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.MeasurementService;
import org.totalgrid.reef.client.service.PointService;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.proto.Measurements;
import org.totalgrid.reef.client.service.proto.Model;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class WebEmbedded extends AbstractHandler {

    private final Client client;

    public WebEmbedded(Client client) {
        this.client = client;
    }
    
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        response.setContentType("text/html;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        baseRequest.setHandled(true);
        response.getWriter().println("<h1>Current Measurements:</h1>");

        try {
            // Get service interface for points
            PointService pointService = client.getService(PointService.class);

            // Select four points to get the measurements
            List<Model.Point> pointList = pointService.getPoints();

            // Get service interface for measurements
            MeasurementService measurementService = client.getService(MeasurementService.class);

            // Get the latest measurements for the list of points
            List<Measurements.Measurement> measurementList = measurementService.getMeasurementsByPoints(pointList);

            response.getWriter().println("<table><tr><th>Measurement</th><th>Value</th><th>Time</th></tr>");

            // Display latest measurements for the points
            for (Measurements.Measurement measurement : measurementList) {
                response.getWriter().println(
                        "<tr><td>" + measurement.getName() +
                                "</td><td>" + buildValueString(measurement) +
                                "</td><td>" + new Date(measurement.getTime()) + "</td></tr>");
            }

            response.getWriter().println("</table>");

        } catch (ReefServiceException ex) {
            throw new ServletException(ex);
        }
    }

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

            // Application code here...
            Server server = new Server(8080);
            server.setHandler(new WebEmbedded(client));

            System.out.println("Starting Jetty web server...");
            server.start();
            server.join();

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

    public static String buildValueString(Measurements.Measurement measurement) {
        if(measurement.getType() == Measurements.Measurement.Type.BOOL) {
            return Boolean.toString(measurement.getBoolVal());
        } else if(measurement.getType() == Measurements.Measurement.Type.INT) {
            return Long.toString(measurement.getIntVal());
        } else if(measurement.getType() == Measurements.Measurement.Type.DOUBLE) {
            return Double.toString(measurement.getDoubleVal());
        } else if(measurement.getType() == Measurements.Measurement.Type.STRING) {
            return measurement.getStringVal();
        } else {
            return "";
        }
    }
}
