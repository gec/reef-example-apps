package org.totalgrid.reef.examples.service.basic.client;

import org.totalgrid.reef.client.exception.ReefServiceException;
import org.totalgrid.reef.examples.service.basic.client.proto.Sample.SampleMessage;

public interface SampleService {

    SampleMessage sendRequest(SampleMessage request) throws ReefServiceException;
}
