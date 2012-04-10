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
package org.totalgrid.reef.examples.measurements.publish;

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
import org.totalgrid.reef.client.service.proto.Measurements;
import org.totalgrid.reef.client.service.proto.Measurements.Measurement;
import org.totalgrid.reef.client.service.proto.Model.Point;

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
        Point point = pointService.getPoints().get(0);

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
