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
import org.totalgrid.reef.client.service.AllScadaService;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Takes a service interface, constructs the classes necessary to see measurements and issue commands and then runs
 * algorithm every periodMs milliseconds
 */
public class StateOptimizerManager {
    private final MeasurementSubscriber measurementSubscriber;
    private final CommandIssuer commandIssuer;
    private final IStateOptimizer algorithm;
    private final long periodMs;

    /**
     * Sets up the plumbing necessary to see current measurement state and issue commands for the switching algorithm
     *
     * @param services  a pre authorized interface to the services
     * @param algorithm implementation of a switching algorithm
     * @param periodMs  milliseconds delay between runs of the algorithm
     * @throws ReefServiceException if any of the initial setup calls to Reef fail
     */
    public StateOptimizerManager(AllScadaService services, IStateOptimizer algorithm, long periodMs) throws ReefServiceException {
        measurementSubscriber = new MeasurementSubscriber(services);
        commandIssuer = new CommandIssuer(services);
        this.algorithm = algorithm;
        this.periodMs = periodMs;
    }

    /**
     * runs the optimize routine every periodMs
     */
    public void run() {
        Timer timer = new Timer();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                try {
                    algorithm.optimize(new MeasurementState(measurementSubscriber.getCurrentState()), commandIssuer);
                } catch (Exception e) {
                    System.out.println("Error running optimize: " + e.getMessage());
                    e.printStackTrace();
                }

            }
        }, 0, periodMs);

        try {
            System.out.println("Press Enter to quit...");
            System.in.read();
        } catch (IOException e) {
            // ignore IOException from placeholder in.read()
        }
        timer.cancel();
    }
}
