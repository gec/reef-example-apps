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
 * A state optimizer is a simple sort of algorithm that is able to look at a single snapshot of the system
 * and issue commands to move the system towards an optimal global state.
 */
public interface IStateOptimizer {

    /**
     * The StateOptimizerManager will call this function with a snapshot of the state in the field on some period.
     * The algorithm should evaluate the current state and then issue any necessary commands
     *
     * @param currentState measurement value at beginning of the evaluation, values will never change
     * @param issuer       interface to issue commands, gets global Select before every command
     * @throws ReefServiceException command issuing can fail, these exceptions can be caught and logged by manager
     */
    public void optimize(MeasurementState currentState, CommandIssuer issuer) throws ReefServiceException;
}
