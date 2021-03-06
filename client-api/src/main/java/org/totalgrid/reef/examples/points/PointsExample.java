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
package org.totalgrid.reef.examples.points;

import org.totalgrid.reef.client.factory.ReefConnectionFactory;
import org.totalgrid.reef.client.service.list.ReefServices;
import org.totalgrid.reef.client.service.PointService;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.Connection;
import org.totalgrid.reef.client.ConnectionFactory;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.settings.AmqpSettings;
import org.totalgrid.reef.client.settings.UserSettings;
import org.totalgrid.reef.client.service.proto.Model.ReefUUID;
import org.totalgrid.reef.client.service.proto.Model.Point;

import java.util.List;

/**
 * Example: Points
 *
 */
public class PointsExample {

    /**
     * Get All Points
     *
     * Get all points configured in the system.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getPoints(Client client) throws ReefServiceException {

        System.out.print("\n=== Get All Points ===\n\n");

        // Get service interface for points
        PointService service = client.getService(PointService.class);

        // Retrieve list of all points
        List<Point> pointList = service.getPoints();

        System.out.println("Found points: " + pointList.size());

        // Inspect a single point
        Point point = pointList.get(0);

        // Display properties of the point
        System.out.println("Point");
        System.out.println("-----------");
        System.out.println("Uuid: " + point.getUuid().getValue());
        System.out.println("Name: " + point.getName());
        System.out.println("Type: " + point.getType());
        System.out.println("Unit: " + point.getUnit());
        System.out.println("Abnormal: " + point.getAbnormal());
        System.out.println("Endpoint: " + point.getEndpoint().getName());
        System.out.println("-----------");
    }

    /**
     * Get Point by Name
     *
     * Get a particular point by providing the point name.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getPointByName(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Point By Name ===\n\n");

        // Get service interface for points
        PointService service = client.getService(PointService.class);

        // Select a single example point
        Point examplePoint = service.getPoints().get(0);

        // Get the name of the example point
        String name = examplePoint.getName();

        // Find the point again using the point name
        Point point = service.getPointByName(name);

        System.out.println("Found point by name: " + point.getName());
    }

    /**
     * Get Point by UUID
     *
     * Get a particular point by providing the point UUID.
     *
     * @param client Logged-in Client object
     * @throws ReefServiceException
     */
    public static void getPointByUuid(Client client) throws ReefServiceException {

        System.out.print("\n=== Get Point By UUID ===\n\n");

        // Get service interface for points
        PointService service = client.getService(PointService.class);

        // Select a single example point
        Point examplePoint = service.getPoints().get(0);

        // Get the UUID of the example point
        ReefUUID uuid = examplePoint.getUuid();

        // Find the point again using the point UUID
        Point point = service.getPointByUuid(uuid);

        System.out.println("Found point by UUID: " + point.getName() + ", " + point.getUuid());
    }
}
