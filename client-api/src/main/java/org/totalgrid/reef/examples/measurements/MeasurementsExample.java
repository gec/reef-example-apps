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
package org.totalgrid.reef.examples.measurements;

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
 * Example: Measurements
 *
 */
public class MeasurementsExample {

    /**
     * Get Measurement by Point
     *
     * Finds latest measurement value for a specific point.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMeasurementByPoint(Client client) throws ReefServiceException {

        System.out.print("\n=== Measurement By Point ===\n\n");

        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Select a specific point
        Point examplePoint = pointService.getPoints().get(0);

        // Get service interface for measurements
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Get latest measurement for the point
        Measurement measurement = measurementService.getMeasurementByPoint(examplePoint);

        // Display measurement properties
        System.out.println("Found Measurement by Point: \n" + measurement);
    }

    /**
     * Get Measurement by Name
     *
     * Finds latest measurement value for a specific point, specified by name.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMeasurementByName(Client client) throws ReefServiceException {

        System.out.print("\n=== Measurement By Name ===\n\n");

        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Select a specific point, get its point name
        String pointName = pointService.getPoints().get(0).getName();

        // Get service interface for measurements
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Get latest measurement for the point by name
        Measurement measurement = measurementService.getMeasurementByName(pointName);

        // Display measurement properties
        System.out.println("Found Measurement by name: \n" + measurement);
    }

    /**
     * Get Multiple Measurements
     *
     * Finds latest measurement value for multiple points.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getMultipleMeasurements(Client client) throws ReefServiceException {

        System.out.print("\n=== Multiple Measurements ===\n\n");

        // Get service interface for points
        PointService pointService = client.getService(PointService.class);

        // Select four points to get the measurements
        List<Point> pointList = pointService.getPoints().subList(0, 4);

        // Get service interface for measurements
        MeasurementService measurementService = client.getService(MeasurementService.class);

        // Get the latest measurements for the list of points
        List<Measurement> measurementList = measurementService.getMeasurementsByPoints(pointList);

        // Display latest measurements for the points
        for (Measurement measurement : measurementList) {
            System.out.println("Measurement: " + measurement.getName() + ", Value: " + buildValueString(measurement) + ", Time: " + new Date(measurement.getTime()));
        }
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
