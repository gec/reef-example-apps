package org.totalgrid.samples.stateoptimizer;


import org.totalgrid.reef.api.japi.ReefServiceException;

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
