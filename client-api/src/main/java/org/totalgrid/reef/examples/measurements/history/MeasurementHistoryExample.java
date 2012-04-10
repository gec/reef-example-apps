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
import org.totalgrid.reef.client.service.proto.Measurements.Measurement;
import org.totalgrid.reef.client.service.proto.Model.Point;

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
    public static void getMeasurementHistory(Client client) throws ReefServiceException {

        Point point = getPoint(client);

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
    public static void getMeasurementHistorySince(Client client) throws ReefServiceException {

        Point point = getPoint(client);

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
    public static void getMeasurementHistoryInterval(Client client) throws ReefServiceException {

        Point point = getPoint(client);

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

    private static Point getPoint(Client client ) throws ReefServiceException{
        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Select a single point to use as an example
        Point point = pointService.getPoints().get(0);
        return point;
    }

    private static String buildValueString(Measurement measurement) {
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
