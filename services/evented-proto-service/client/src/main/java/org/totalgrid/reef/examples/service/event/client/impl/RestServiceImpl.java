package org.totalgrid.reef.examples.service.event.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.ClientOperations;
import org.totalgrid.reef.examples.service.event.client.RestService;
import org.totalgrid.reef.examples.service.event.client.proto.RestEvented.RestMessage;

import java.util.List;

public class RestServiceImpl implements RestService {

    private final Client client;

    public RestServiceImpl(Client client) {
        this.client = client;
    }

    @Override
    public RestMessage getMessage(String key) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        RestMessage request = RestMessage.newBuilder().setKey(key).build();

        return operations.getOne(request);
    }

    @Override
    public List<RestMessage> getAllMessages() throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        RestMessage request = RestMessage.newBuilder().setKey("*").build();

        return operations.getMany(request);
    }

    @Override
    public RestMessage putMessage(String key, String value) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        RestMessage request = RestMessage.newBuilder().setKey(key).setValue(value).build();

        return operations.putOne(request);
    }

    @Override
    public void deleteMessage(String key) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        RestMessage request = RestMessage.newBuilder().setKey(key).build();

        operations.deleteOne(request);
    }

    @Override
    public void deleteAllMessages() throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        RestMessage request = RestMessage.newBuilder().setKey("*").build();

        operations.deleteMany(request);
    }
}
