package org.totalgrid.samples.stateoptimizer;

import org.totalgrid.reef.api.ISubscription;
import org.totalgrid.reef.api.ReefServiceException;
import org.totalgrid.reef.api.ServiceTypes;
import org.totalgrid.reef.api.javaclient.IEventAcceptor;
import org.totalgrid.reef.api.request.AllScadaService;
import org.totalgrid.reef.proto.Measurements;
import org.totalgrid.reef.proto.Model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * subscribes to all measurements in the system, keeps last value of each measurement
 */
public class MeasurementSubscriber implements IEventAcceptor<Measurements.Measurement> {

    private Map<String, Measurements.Measurement> measurementState = new HashMap<String, Measurements.Measurement>();

    public MeasurementSubscriber(AllScadaService services) throws ReefServiceException {

        ISubscription<Measurements.Measurement> sub = services.createMeasurementSubscription(this);

        // get list of all points in system
        List<Model.Point> points = services.getAllPoints();
        // TODO: fliter points to only ones we plan on looking at

        List<Measurements.Measurement> integrityPoll = services.getMeasurementsByPoints(points, sub);

        for (Measurements.Measurement m : integrityPoll) {
            updateMeasurement(m);
        }
    }

    @Override
    public void onEvent(ServiceTypes.Event<Measurements.Measurement> event) {
        // subscription updates come in from another thread here
        synchronized (this) {
            updateMeasurement(event.getResult());
        }
    }

    private void updateMeasurement(Measurements.Measurement updatedMeasurement) {
        // replace the current value with the most recently received measurement
        measurementState.put(updatedMeasurement.getName(), updatedMeasurement);
    }

    /**
     * @return copy of the measurement state
     */
    public Map<String, Measurements.Measurement> getCurrentState() {
        synchronized (this) {
            return new HashMap<String, Measurements.Measurement>(measurementState);
        }
    }
}
