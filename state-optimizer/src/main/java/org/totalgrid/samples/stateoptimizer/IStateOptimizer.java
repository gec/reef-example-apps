package org.totalgrid.samples.stateoptimizer;

import org.totalgrid.reef.japi.ReefServiceException;

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
