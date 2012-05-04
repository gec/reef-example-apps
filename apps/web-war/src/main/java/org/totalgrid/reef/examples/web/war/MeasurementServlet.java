/**
 * Copyright 2011 Green Energy Corp.
 *
 * Licensed to Green Energy Corp (www.greenenergycorp.com) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. Green Energy
 * Corp licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.totalgrid.reef.examples.web.war;

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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class MeasurementServlet extends HttpServlet {
    ConnectionFactory connectionFactory = null;
    Connection connection = null;
    Client client = null;

    @Override
    public void init() throws ServletException {
        try {
            // Load broker settings from config file
            AmqpSettings amqp = new AmqpSettings("org.totalgrid.reef.amqp.cfg");

            // Load user settings (login credentials) from config file
            UserSettings user = new UserSettings("org.totalgrid.reef.user.cfg");

            // Create a ConnectionFactory by passing the broker settings. The ConnectionFactory is
            // used to create a Connection to the Reef server
            this.connectionFactory = ReefConnectionFactory.buildFactory(amqp, new ReefServices());

            // Connect to the Reef server, may fail if can't connect
            connection = connectionFactory.connect();

            // Login with the user credentials
            this.client = connection.login(user);

        } catch(ReefServiceException rse) {

            throw new ServletException(rse);

        } catch(IOException ioe) {

            throw new ServletException(ioe);

        }
    }

    @Override
    public void destroy() {
        // Disconnect the Connection object, removes clients and subscriptions
        if(connection != null) {
            connection.disconnect();
        }
        // Terminate the ConnectionFactory to close threading objects
        if(connectionFactory != null) {
            connectionFactory.terminate();
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);

        if ( client == null) {
            resp.getWriter().println("<h1>Not connected</h1>");
            return;
        }

        try {
            // Get service interface for points
            PointService pointService = client.getService(PointService.class);

            // Select four points to get the measurements
            List<Model.Point> pointList = pointService.getPoints();

            // Get service interface for measurements
            MeasurementService measurementService = client.getService(MeasurementService.class);

            // Get the latest measurements for the list of points
            List<Measurements.Measurement> measurementList = measurementService.getMeasurementsByPoints(pointList);

            resp.getWriter().println("<h1>Current Measurements:</h1>");

            resp.getWriter().println("<table><tr><th>Measurement</th><th>Value</th><th>Time</th></tr>");

            // Display latest measurements for the points
            for (Measurements.Measurement measurement : measurementList) {
                resp.getWriter().println(
                        "<tr><td>" + measurement.getName() +
                                "</td><td>" + buildValueString(measurement) +
                                "</td><td>" + new Date(measurement.getTime()) + "</td></tr>");
            }

            resp.getWriter().println("</table>");

        } catch (ReefServiceException ex) {
            throw new ServletException(ex);
        }

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
