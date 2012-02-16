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

/**
 * sample switching algorithm
 */
public class CapacitorSwitchingAlgorithm implements IStateOptimizer {

    public void optimize(MeasurementState currentState, CommandIssuer issuer) throws ReefServiceException {

        // debugging only
        System.out.println("System State:");
        System.out.println(currentState);
        System.out.println("Available Commands:");
        System.out.println(issuer);

        /*
        // example of what algorithm might look like
        double vi = currentState.analog("Line1.Voltage");
        boolean capacitorOn = currentState.status("Line1.Capacitor", "ON");
        if (vi <= 0.95) {
            if (!capacitorOn) {
                issuer.issueCommand("Line1.Capacitor.ON");
            } else {
                issuer.issueCommand("Line2.Capacitor.ON");
            }
        } else if (vi >= 1.05) {
            if (capacitorOn) {
                issuer.issueCommand("Line1.Capacitor.OFF");
            } else {
                issuer.issueCommand("Line2.Capacitor.OFF");
            }
        } else {
            // everything is nominal, no work needed
        }*/
    }


}
