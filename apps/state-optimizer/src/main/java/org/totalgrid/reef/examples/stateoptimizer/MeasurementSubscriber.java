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
package org.totalgrid.reef.examples.stateoptimizer;


import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.SubscriptionEvent;
import org.totalgrid.reef.client.SubscriptionEventAcceptor;
import org.totalgrid.reef.client.SubscriptionResult;
import org.totalgrid.reef.client.service.AllScadaService;
import org.totalgrid.reef.client.service.proto.Measurements.Measurement;
import org.totalgrid.reef.client.service.proto.Model.Point;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * subscribes to all measurements in the system, keeps last value of each measurement
 */
public class MeasurementSubscriber implements SubscriptionEventAcceptor<Measurement> {

    private Map<String, Measurement> measurementState = new HashMap<String, Measurement>();

    public MeasurementSubscriber(AllScadaService services) throws ReefServiceException {

        // get list of all points in system
        List<Point> points = services.getPoints();
        // TODO: filter points to only ones we plan on looking at

        SubscriptionResult<List<Measurement>, Measurement> result = services.subscribeToMeasurementsByPoints(points);

        for (Measurement m : result.getResult()) {
            updateMeasurement(m);
        }
        result.getSubscription().start(this);
    }

    @Override
    public void onEvent(SubscriptionEvent<Measurement> event) {
        // subscription updates come in from another thread here
        synchronized (this) {
            updateMeasurement(event.getValue());
        }
    }

    private void updateMeasurement(Measurement updatedMeasurement) {
        // replace the current value with the most recently received measurement
        measurementState.put(updatedMeasurement.getName(), updatedMeasurement);
    }

    /**
     * @return copy of the measurement state
     */
    public Map<String, Measurement> getCurrentState() {
        synchronized (this) {
            return new HashMap<String, Measurement>(measurementState);
        }
    }
}
