package org.totalgrid.reef.examples.service.event.client.impl;

import org.totalgrid.reef.client.Client;
import org.totalgrid.reef.client.ServiceProviderFactory;

/**
 * Implements a service provider factory, provides the implementation for the
 * KeyValueService interface
 */
public class KeyValueServiceFactory implements ServiceProviderFactory {

    /**
     * The connection framework provides us with a client to implement our
     * KeyValueService implementation
     *
     * @param client Logged-in client
     * @return
     */
    @Override
    public Object createRpcProvider(Client client) {
        return new KeyValueServiceImpl(client);
    }
}
