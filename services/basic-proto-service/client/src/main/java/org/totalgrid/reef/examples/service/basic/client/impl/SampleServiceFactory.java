package org.totalgrid.reef.examples.service.basic.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.ServiceProviderFactory;

/**
 * Implements a ServiceProviderFactory to build a SampleService impl given
 * a client.
 */
public class SampleServiceFactory implements ServiceProviderFactory {

    /**
     * The connection framework provides us with a client to implement our
     * SampleService implementation
     *
     * @param client
     * @return
     */
    @Override
    public Object createRpcProvider(Client client) {
        return new SampleServiceImpl(client);
    }
}
