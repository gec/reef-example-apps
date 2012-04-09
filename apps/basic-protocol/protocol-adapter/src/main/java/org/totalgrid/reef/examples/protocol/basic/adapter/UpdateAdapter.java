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
package org.totalgrid.reef.examples.protocol.basic.adapter;

import org.totalgrid.reef.client.AddressableDestination;
import org.totalgrid.reef.client.AnyNodeDestination;
import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.MeasurementService;
import org.totalgrid.reef.client.service.proto.Measurements;
import org.totalgrid.reef.client.service.proto.Measurements.Measurement;
import org.totalgrid.reef.client.service.proto.Measurements.MeasurementBatch;
import org.totalgrid.reef.examples.protocol.basic.library.ExternalUpdateAcceptor;
import org.totalgrid.reef.protocol.api.ProtocolResources;
import org.totalgrid.reef.protocol.api.Publisher;

import java.util.ArrayList;
import java.util.List;

/**
 * Bridge between external (fake) protocol API and the Reef
 * protocol API. Forwards a measurement update notification from the
 * external protocol to the Reef client API.
 */
public class UpdateAdapter implements ExternalUpdateAcceptor {

    ProtocolResources resources;

    /**
     * @param resources Protocol resources helper to use to publish measurements
     */
    public UpdateAdapter(ProtocolResources resources) {
        this.resources = resources;
    }

    /**
     * Implementation of external protocol measurement callback, translates
     * update to Reef measurement and uses client-based ProtocolResources
     * interface to publish it.
     *
     * @param name Measurement name
     * @param value Analog value
     * @param time Time in milliseconds
     */
    @Override
    public void handleUpdate(String name, double value, long time) {

        // Build measurement object to represent this update
        Measurement.Builder builder = Measurement.newBuilder();
        builder.setName(name);
        builder.setType(Measurement.Type.DOUBLE);
        builder.setDoubleVal(value);
        builder.setTime(time);
        builder.setQuality(Measurements.Quality.newBuilder().build());

        List<Measurement> list = new ArrayList<Measurement>();
        list.add(builder.build());

        // Publish measurements to the system
        try {
            resources.publishMeasurements(list);
        } catch (ReefServiceException ex) {
            System.out.println("Could not publish measurement batch! " + ex);
        }
    }
}
