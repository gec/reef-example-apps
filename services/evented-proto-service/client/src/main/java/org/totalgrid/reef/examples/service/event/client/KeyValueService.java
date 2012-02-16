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
     *
     *
     * @param key
     * @return
     * @throws ReefServiceException
     */
    KeyValue getMessage(String key) throws ReefServiceException;

    List<KeyValue> getAllMessages() throws ReefServiceException;

    KeyValue putMessage(String key, String value) throws ReefServiceException;

    void deleteMessage(String key) throws ReefServiceException;

    void deleteAllMessages() throws ReefServiceException;
    
    SubscriptionResult<List<KeyValue>, KeyValue> subscribeToAllKeyValues() throws ReefServiceException;
    
    SubscriptionResult<List<KeyValue>, KeyValue> subscribeToKeyValues(String key) throws ReefServiceException;

}
