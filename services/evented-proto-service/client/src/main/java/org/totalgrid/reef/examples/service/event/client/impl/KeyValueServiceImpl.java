package org.totalgrid.reef.examples.service.event.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.SubscriptionResult;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.ClientOperations;
import org.totalgrid.reef.examples.service.event.client.KeyValueService;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.util.List;

/**
 * Implementation of the KeyValueService interface.
 *
 * Uses a Client interface to translate RPC calls into REST
 * service calls.
 *
 */
public class KeyValueServiceImpl implements KeyValueService {

    private final Client client;

    public KeyValueServiceImpl(Client client) {
        this.client = client;
    }

    /**
     * Get a particular key-value, implemented with the "GET" verb
     *
     * @param key Key of the key-value pair
     * @return
     * @throws ReefServiceException
     */
    @Override
    public KeyValue getMessage(String key) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        // Request is a KeyValue with the key filled in
        KeyValue request = KeyValue.newBuilder().setKey(key).build();

        return operations.getOne(request);
    }

    /**
     * Get all key-value pairs, implemented with the "GET" verb
     *
     * @return
     * @throws ReefServiceException
     */
    @Override
    public List<KeyValue> getAllMessages() throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        // Request is a KeyValue with the key filled in as the special "*" character
        KeyValue request = KeyValue.newBuilder().setKey("*").build();

        return operations.getMany(request);
    }

    /**
     * Add/modify a key/value pair, implemented with the "PUT" verb
     *
     * @param key Key of the key-value pair
     * @param value Value of the key-value pair
     * @return
     * @throws ReefServiceException
     */
    @Override
    public KeyValue putMessage(String key, String value) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        // Request is a KeyValue with both fields filled in
        KeyValue request = KeyValue.newBuilder().setKey(key).setValue(value).build();

        return operations.putOne(request);
    }

    /**
     * Delete a key/value pair, implemeted with "DELETE" verb
     *
     * @param key Key of the key-value pair
     * @throws ReefServiceException
     */
    @Override
    public void deleteMessage(String key) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        // Request is a KeyValue with the key filled in
        KeyValue request = KeyValue.newBuilder().setKey(key).build();

        operations.deleteOne(request);
    }

    /**
     * Delete all key/value pairs, implemeted with "DELETE" verb
     *
     * @throws ReefServiceException
     */
    @Override
    public void deleteAllMessages() throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        // Request is a KeyValue with the key filled in as the special "*" character
        KeyValue request = KeyValue.newBuilder().setKey("*").build();

        operations.deleteMany(request);
    }

    /**
     * Subscribe to all key/value pair updates
     *
     * @return
     * @throws ReefServiceException
     */
    @Override
    public SubscriptionResult<List<KeyValue>, KeyValue> subscribeToAllKeyValues() throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        // Request is a KeyValue with the key filled in as the special "*" character
        KeyValue request = KeyValue.newBuilder().setKey("*").build();

        return operations.subscribeMany(request);
    }

    /**
     * Subscribe to key/value pair updates with a particular key
     *
     * @param key Key of the key-value pair
     * @return
     * @throws ReefServiceException
     */
    @Override
    public SubscriptionResult<List<KeyValue>, KeyValue> subscribeToKeyValues(String key) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        // Request is a KeyValue with the key filled in
        KeyValue request = KeyValue.newBuilder().setKey(key).build();

        return operations.subscribeMany(request);
    }
}
