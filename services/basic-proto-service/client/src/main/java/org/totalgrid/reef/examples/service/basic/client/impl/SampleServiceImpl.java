package org.totalgrid.reef.examples.service.basic.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.client.service.ClientOperations;
import org.totalgrid.reef.examples.service.basic.client.SampleService;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample;

/**
 * Implementation of SampleService interface.
 *
 * Uses a Client interface to translate RPC calls into REST
 * service calls.
 *
 */
public class SampleServiceImpl implements SampleService {
    
    private final Client client;
    
    public SampleServiceImpl(Client client) {
        this.client = client;
    }

    /**
     * Send sample message request, implemented using "GET" verb
     *
     * @param request
     * @return
     * @throws ReefServiceException
     */
    @Override
    public Sample.SampleMessage sendRequest(Sample.SampleMessage request) throws ReefServiceException {

        ClientOperations operations = client.getService(ClientOperations.class);

        return operations.getOne(request);
    }
}
