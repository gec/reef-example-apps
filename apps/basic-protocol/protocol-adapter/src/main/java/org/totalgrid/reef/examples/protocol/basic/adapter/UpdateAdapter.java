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
import org.totalgrid.reef.protocol.api.Publisher;

import java.util.ArrayList;
import java.util.List;

public class UpdateAdapter implements ExternalUpdateAcceptor {

    private final String routingKey;
    private final MeasurementService service;

    public UpdateAdapter(MeasurementService service, String routingKey) {
        this.routingKey = routingKey;
        this.service = service;
    }

    @Override
    public void handleUpdate(String name, double value, long time) {
        Measurement.Builder builder = Measurement.newBuilder();
        builder.setName(name);
        builder.setType(Measurement.Type.DOUBLE);
        builder.setDoubleVal(value);
        builder.setTime(time);
        builder.setQuality(Measurements.Quality.newBuilder().build());

        List<Measurement> list = new ArrayList<Measurement>();
        list.add(builder.build());

        try {
            service.publishMeasurements(list, new AddressableDestination(routingKey));
        } catch (ReefServiceException ex) {
            System.out.println("Could not publish measurement batch! " + ex);
        }
    }
}
