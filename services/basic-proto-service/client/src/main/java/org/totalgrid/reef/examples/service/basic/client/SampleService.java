package org.totalgrid.reef.examples.service.basic.client;

import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample.SampleMessage;

/**
 * Provides an Java-idiom, RPC-like method for clients to make service calls.
 */
public interface SampleService {

    /**
     * Send a sample message request and receive a sample message response
     *
     * @param request
     * @return
     * @throws ReefServiceException
     */
    SampleMessage sendRequest(SampleMessage request) throws ReefServiceException;
}
