package org.totalgrid.reef.examples.service.event.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.SubscriptionResult;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.ClientOperations;
import org.totalgrid.reef.examples.service.event.client.KeyValueService;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.KeyValue;

import java.util.List;

public class KeyValueServiceImpl implements KeyValueService {

    private final Client client;

    public KeyValueServiceImpl(Client client) {
        this.client = client;
    }

    @Override
    public KeyValue getMessage(String key) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        KeyValue request = KeyValue.newBuilder().setKey(key).build();

        return operations.getOne(request);
    }

    @Override
    public List<KeyValue> getAllMessages() throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        KeyValue request = KeyValue.newBuilder().setKey("*").build();

        return operations.getMany(request);
    }

    @Override
    public KeyValue putMessage(String key, String value) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        KeyValue request = KeyValue.newBuilder().setKey(key).setValue(value).build();

        return operations.putOne(request);
    }

    @Override
    public void deleteMessage(String key) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        KeyValue request = KeyValue.newBuilder().setKey(key).build();

        operations.deleteOne(request);
    }

    @Override
    public void deleteAllMessages() throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        KeyValue request = KeyValue.newBuilder().setKey("*").build();

        operations.deleteMany(request);
    }

    @Override
    public SubscriptionResult<List<KeyValue>, KeyValue> subscribeToAllKeyValues() throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        KeyValue request = KeyValue.newBuilder().setKey("*").build();

        return operations.subscribeMany(request);
    }

    @Override
    public SubscriptionResult<List<KeyValue>, KeyValue> subscribeToKeyValues(String key) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        KeyValue request = KeyValue.newBuilder().setKey(key).build();

        return operations.subscribeMany(request);
    }
}
