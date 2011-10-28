package org.totalgrid.samples.stateoptimizer;


import org.totalgrid.reef.clientapi.exceptions.ReefServiceException;
import org.totalgrid.reef.clientapi.SubscriptionEvent;
import org.totalgrid.reef.clientapi.SubscriptionEventAcceptor;
import org.totalgrid.reef.clientapi.SubscriptionResult;
import org.totalgrid.reef.client.rpc.AllScadaService;
import org.totalgrid.reef.proto.Measurements.Measurement;
import org.totalgrid.reef.proto.Model.Point;

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
        List<Point> points = services.getAllPoints();
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
