package org.totalgrid.reef.examples.service.event.client;

import org.totalgrid.reef.client.SubscriptionResult;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.util.List;

/**
 * Provides an Java-idiom, RPC-like method for clients to make service calls.
 */
public interface KeyValueService {

    /**
     * Get a single value by name
     *
     * @param key Key of the key-value pair
     * @return The key-value pair
     * @throws ReefServiceException
     */
    KeyValue getValue(String key) throws ReefServiceException;

    /**
     * Get all key-value pairs
     *
     * @return The list of key-value pairs
     * @throws ReefServiceException
     */
    List<KeyValue> getAllValues() throws ReefServiceException;

    /**
     * Put a key-value pair, adding if one doesn't exist already, modifying otherwise
     *
     * @param key Key of the key-value pair
     * @param value Value of the key-value pair
     * @return The key-value pair that was put by the service
     * @throws ReefServiceException
     */
    KeyValue putValue(String key, String value) throws ReefServiceException;

    /**
     * Delete a specific key-value pair by specifying the key
     *
     * @param key Key of the key-value pair
     * @throws ReefServiceException
     */
    void deleteValue(String key) throws ReefServiceException;

    /**
     * Delete all entries in the system
     *
     * @throws ReefServiceException
     */
    void deleteAllValues() throws ReefServiceException;

    /**
     * Subscribe to all subscription events associated with all KeyValue service objects
     *
     * @return Contains immediate results to the query as well as subscription management object
     * @throws ReefServiceException
     */
    SubscriptionResult<List<KeyValue>, KeyValue> subscribeToAllKeyValues() throws ReefServiceException;

    /**
     * Subscribe to subscription events associated with a specific KeyValue service object (by key)
     *
     * @param key Key of the key-value pair
     * @return Contains immediate results to the query as well as subscription management object
     * @throws ReefServiceException
     */
    SubscriptionResult<List<KeyValue>, KeyValue> subscribeToKeyValues(String key) throws ReefServiceException;

}
