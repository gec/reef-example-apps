package org.totalgrid.reef.examples.stateoptimizer;

import org.totalgrid.reef.client.service.proto.Measurements;

import java.util.HashMap;
import java.util.Map;

/**
 * provides a wrapper around a snapshot of the current point values to extract the required data.
 * <p/>
 * Also provides a helpful toString overload that displays current state of the system
 */
public class MeasurementState {

    private final Map<String, Measurements.Measurement> currentState;

    public MeasurementState(Map<String, Measurements.Measurement> currentState) {

        this.currentState = currentState;
    }

    /**
     * Get the current analog (double) value for the Point
     *
     * @param pointName name of Point to get current value
     * @return the current value of the point
     * @throws IllegalArgumentException if measurement doesn't exist or has wrong data type
     */
    public double analog(String pointName) throws IllegalArgumentException {
        Measurements.Measurement m = currentState.get(pointName);

        if (m == null) throw new IllegalArgumentException("No measurement for: " + pointName);
        if (m.getType() != Measurements.Measurement.Type.DOUBLE)
            throw new IllegalArgumentException("Measurement for: " + pointName + " didn't have double type: " + m.getType());

        return m.getDoubleVal();
    }

    /**
     * Get the current integer value for the Point
     *
     * @param pointName name of Point to get current value
     * @return the current value of the point
     * @throws IllegalArgumentException if measurement doesn't exist or has wrong data type
     */
    public long counter(String pointName) throws IllegalArgumentException {
        Measurements.Measurement m = currentState.get(pointName);

        if (m == null) throw new IllegalArgumentException("No measurement for: " + pointName);
        if (m.getType() != Measurements.Measurement.Type.INT)
            throw new IllegalArgumentException("Measurement for: " + pointName + " didn't have integer type: " + m.getType());

        return m.getIntVal();
    }

    /**
     * Get the current boolean value of a status point.
     *
     * @param pointName name of Point to get current value
     * @return the current value of the point
     * @throws IllegalArgumentException if measurement doesn't exist or has wrong data type
     */
    public boolean status(String pointName) throws IllegalArgumentException {
        Measurements.Measurement m = currentState.get(pointName);

        if (m == null) throw new IllegalArgumentException("No measurement for: " + pointName);
        if (m.getType() != Measurements.Measurement.Type.BOOL)
            throw new IllegalArgumentException("Measurement for: " + pointName + " didn't have boolean type: " + m.getType());

        return m.getBoolVal();
    }

    /**
     * Status points may use a string to represent the state (rather than using a boolean)
     * For example a circuit breaker may use the values "CLOSED" and "OPENED" rather than true and
     * false to make it clear what state it is in.
     *
     * @param pointName     name of Point to get current value
     * @param expectedValue the state we want to treat as true, anything else is false
     * @return whether the enum value matched the expectedValue
     * @throws IllegalArgumentException if measurement doesn't exist or has wrong data type
     */
    public boolean status(String pointName, String expectedValue) throws IllegalArgumentException {
        Measurements.Measurement m = currentState.get(pointName);

        if (m == null) throw new IllegalArgumentException("No measurement for: " + pointName);
        if (m.getType() != Measurements.Measurement.Type.STRING)
            throw new IllegalArgumentException("Measurement for: " + pointName + " didn't have String type: " + m.getType());

        return expectedValue.compareTo(m.getStringVal()) == 0;
    }

    /**
     * @return a copy of the measurement map for more advanced measurement manipulations
     */
    public Map<String, Measurements.Measurement> getMeasurements() {
        return new HashMap<String, Measurements.Measurement>(currentState);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Measurements.Measurement m : currentState.values()) {
            sb.append(m.getName()).append(" : ");
            if (m.getType() == Measurements.Measurement.Type.DOUBLE) {
                sb.append(m.getDoubleVal());
            } else if (m.getType() == Measurements.Measurement.Type.BOOL) {
                sb.append(m.getBoolVal());
            } else if (m.getType() == Measurements.Measurement.Type.INT) {
                sb.append(m.getIntVal());
            } else if (m.getType() == Measurements.Measurement.Type.STRING) {
                sb.append(m.getStringVal());
            } else if (m.getType() == Measurements.Measurement.Type.NONE) {
                sb.append("--");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
