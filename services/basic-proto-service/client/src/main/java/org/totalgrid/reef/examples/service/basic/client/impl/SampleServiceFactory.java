package org.totalgrid.reef.examples.service.basic.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.ServiceProviderFactory;

public class SampleServiceFactory implements ServiceProviderFactory {

    @Override
    public Object createRpcProvider(Client client) {
        return new SampleServiceImpl(client);
    }
}
