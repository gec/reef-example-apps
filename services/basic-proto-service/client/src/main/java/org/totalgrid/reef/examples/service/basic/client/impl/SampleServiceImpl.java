package org.totalgrid.reef.examples.service.basic.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.ClientOperations;
import org.totalgrid.reef.examples.service.basic.client.SampleService;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample;

public class SampleServiceImpl implements SampleService {
    
    Client client;
    
    public SampleServiceImpl(Client client) {
        this.client = client;
    }
    
    @Override
    public Sample.SampleMessage sendRequest(Sample.SampleMessage request) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        return operations.getOne(request);
    }
}
